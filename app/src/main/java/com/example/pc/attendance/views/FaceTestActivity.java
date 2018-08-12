package com.example.pc.attendance.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.attendance.PreProcessor.PreProcessorFactory;
import com.example.pc.attendance.R;
import com.example.pc.attendance.helpers.CustomCameraView;
import com.example.pc.attendance.helpers.MatOperation;
import com.example.pc.attendance.models.UserImage;

import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FaceTestActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private AlertDialog resultDialog;
    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;
    private CustomCameraView mDetectionView;
    private PreProcessorFactory ppF;

    private String url;
    private String port;
    private String classID;
    String imgFolderPath;


    public static final String PROTOCOL = "http://";
    private static final String API_Train_Image = "/user/portImageForTest";
    public static final String HOST_ADDRESS = "api_key";
    public static final String PORT_KEY = "port_key";
    private static String TAG="Test Face Activity";
    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"OpenCV not loaded");
        }else{
            Log.d(TAG,"OpenCV loaded");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_test);
        mDetectionView = (CustomCameraView) findViewById(R.id.testCameraView);
        // Use camera which is selected in settings
        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this);
        url=sharedPref.getString(HOST_ADDRESS,"");
        port=sharedPref.getString(PORT_KEY,"");
        classID=getIntent().getExtras().getString("classid");
        front_camera = true;

        if (front_camera){
            mDetectionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        } else {
            mDetectionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        mDetectionView.setVisibility(SurfaceView.VISIBLE);
        mDetectionView.setCvCameraViewListener(this);

        int maxCameraViewWidth =320;
        int maxCameraViewHeight =240;
        mDetectionView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);

        createImageDirectory();

    }
    public void showDialog(String txt) {
        resultDialog = new AlertDialog.Builder(FaceTestActivity.this)
                .setTitle("Kết quả của việc nhận dạng: ")
                .setView(R.layout.dialog_regconition_train).create();
        resultDialog.show();
        TextView tvResult=(TextView)resultDialog.findViewById(R.id.tvResult);
        Button btnExit=(Button) resultDialog.findViewById(R.id.btnExit);
        Button btnAgain=(Button) resultDialog.findViewById(R.id.btnAgain);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultDialog.dismiss();
            }
        });
        tvResult.setText(txt);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(FaceTestActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ppF = new PreProcessorFactory(getApplicationContext());
        mDetectionView.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();

        Mat img = new Mat();
        imgRgba.copyTo(img);
        List<Mat> images = ppF.getCroppedImage(img);
        Rect[] faces = ppF.getFacesForRecognition();
        // Selfie / Mirror mode
        if(front_camera){
            Core.flip(imgRgba,imgRgba,1);
        }

        if(images == null || images.size() == 0 || faces == null || faces.length == 0 || ! (images.size() == faces.length)){
            // skip
            return imgRgba;
        } else {
            faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
            for(int i = 0; i<faces.length; i++){
                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], "", front_camera);

            }

            TrainAsysTask trainAsysTask=new TrainAsysTask();
            trainAsysTask.execute();

            return imgRgba;
        }

    }

    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, PROTOCOL + url + ":" + port);
        return PROTOCOL + url + ":" + port + API_Train_Image;
    }

    private void createImageDirectory(){

        File directory=new File(Environment.getExternalStorageDirectory(),"Attandance");
        if(!directory.exists()||!directory.isDirectory()){
            directory.mkdir();
            Log.d("CreateFolder","folder created");
            imgFolderPath=directory.getPath();

        }
    }

    private String loadImageFromDirectory(){
        File directory=new File(Environment.getExternalStorageDirectory(),"Attandance");
        File image=new File(directory,"test.jpg");

        Bitmap bitmap1 = BitmapFactory.decodeFile(image.getPath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] trainImagge=stream.toByteArray();
        String encodeImage= Base64.encodeToString(trainImagge, Base64.DEFAULT);
        Log.d(TAG,encodeImage);
        return  encodeImage;
    }


    class TrainAsysTask extends AsyncTask<String,String,String> {


        @Override
        protected void onPostExecute(String s) {

            if(null == s){
                return;
            }

                Log.d("POST", s);



                showDialog(s);

        }
        @Override
        protected String doInBackground(String... strings) {
            String current = "";
            mDetectionView.takePicture("test.jpg");
            String base64Img= loadImageFromDirectory();

            try{
                URL url;
                HttpURLConnection urlConnection = null;
                Log.d(TAG, "doInBackGround");

                try{
                    url = new URL(getServerUrl());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    urlConnection.setRequestProperty("Accept","application/json");
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);


                    JSONObject jsonParam = new JSONObject();
                    UserImage userImage=new UserImage("test",classID,base64Img);
                    jsonParam.put("img",base64Img);
                    jsonParam.put("id","test");
                    jsonParam.put("classId",classID);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);

                    int data = reader.read();

                    while(data != -1){
                        current += (char) data;
                        data = reader.read();
                    }

                    return current;

                } catch (ConnectException e){
                    e.printStackTrace();
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }



            return current;
        }
    }
}
