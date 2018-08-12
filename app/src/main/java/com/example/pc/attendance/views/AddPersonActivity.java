package com.example.pc.attendance.views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import com.example.pc.attendance.PreProcessor.PreProcessorFactory;
import com.example.pc.attendance.R;
import com.example.pc.attendance.helpers.CustomCameraView;
import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.helpers.MatName;
import com.example.pc.attendance.helpers.MatOperation;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.Date;
import java.util.List;

public class AddPersonActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    public static final int TIME=0;

    private CustomCameraView mAddPersonView;
    private ImageButton btnChangeCamera;

    private long timeDiff;
    private long lastTime;
    private PreProcessorFactory ppF;
    private FileHelper fh;
    private String folder;
    private String id;
    private int total;
    private int numberOfPictures;
    private int method;
    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;


    private String url;
    private String port;
    String imgFolderPath;
    private String studentId;

    public static final String PROTOCOL = "http://";
    private static final String API_Train_Image = "/user/PostImage";
    public static final String HOST_ADDRESS = "api_key";
    public static final String PORT_KEY = "port_key";
    private static String TAG="Main Activity";

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);



        Intent intent=getIntent();
        folder=intent.getStringExtra("Folder");

        id=intent.getStringExtra("id");
        method=TIME;

        fh=new FileHelper();
        total=0;
        lastTime=new Date().getTime();

        SharedPreferences sharedPrefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        timeDiff=Integer.valueOf(sharedPrefs.getString("key_timerDiff","500"));

        mAddPersonView=(CustomCameraView) findViewById(R.id.AddPersonPreview);
        btnChangeCamera=(ImageButton) findViewById(R.id.btnChangeCamera);

        front_camera = sharedPrefs.getBoolean("key_front_camera", false);
        numberOfPictures=Integer.valueOf(sharedPrefs.getString("key_numberOfPictures","20"));

        night_portrait=false;
        exposure_compensation=50;

        if(front_camera==true){
            mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

        }else{
            mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }

        btnChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(front_camera==true){
                    front_camera=false;
                    mAddPersonView.disableView();
                    mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
                    mAddPersonView.enableView();
                }else{
                    front_camera=true;
                    mAddPersonView.disableView();
                    mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
                    mAddPersonView.enableView();

                }
            }
        });
        mAddPersonView.setVisibility(SurfaceView.VISIBLE);
        mAddPersonView.setCvCameraViewListener(this);

        int maxCameraViewWidth = Integer.parseInt(sharedPrefs.getString("key_maximum_camera_view_width", "640"));
        int maxCameraViewHeight = Integer.parseInt(sharedPrefs.getString("key_maximum_camera_view_height", "480"));
        mAddPersonView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        if (night_portrait) {
            mAddPersonView.setNightPortrait();
        }

        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
            mAddPersonView.setExposure(exposure_compensation);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba=inputFrame.rgba();
        Mat imgCopy=new Mat();
        imgRgba.copyTo(imgCopy);

        if(front_camera){
            Core.flip(imgRgba,imgRgba,1);
        }

        long time=new Date().getTime();
        if((method==TIME)&&(lastTime+timeDiff<time)){
            lastTime=time;

            List<Mat> images=ppF.getCroppedImage(imgCopy);
            if(images!=null && images.size()==1){
                Mat img=images.get(0);
                if(img!=null){
                    Rect[] faces=ppF.getFacesForRecognition();

                    if(faces!=null&&faces.length==1){
                        faces= MatOperation.rotateFaces(imgRgba,faces,ppF.getAngleForRecognition());
                        if(method==TIME){
                            MatName m=new MatName(id+"_"+total,img);
                            if(folder.equals("Test")){

                            }else{
                                String wholeFolderPath=fh.TRAINING_PATH+id;
                                new File(wholeFolderPath).mkdir();
                                String path =fh.saveMatToImage(m,wholeFolderPath+"/");
                                TrainAsysTask trainAsysTask=new TrainAsysTask(id,path);
                                trainAsysTask.execute();
                            }

                            for(int i=0;i<faces.length;i++){
                                MatOperation.drawRectangleAndLabelOnPreview(imgRgba,faces[i],String.valueOf(total),front_camera);

                            }
                            total++;

                            if(total>=numberOfPictures){
                               finish();
                            }
                        }
                        else{
                            for(int i=0;i<faces.length;i++){
                                MatOperation.drawRectangleOnPreview(imgRgba,faces[i],front_camera);
                            }
                        }
                    }
                }

            }
        }

        return imgRgba;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ppF=new PreProcessorFactory(this);
        mAddPersonView.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAddPersonView!=null){
            mAddPersonView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAddPersonView!=null){
            mAddPersonView.disableView();
        }
    }


    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, PROTOCOL + url + ":" + port);
        return PROTOCOL + url + ":" + port + API_Train_Image;
    }

    private String loadImageFromDirectory(String path){
        File image=new File(path);

        Bitmap bitmap1 = BitmapFactory.decodeFile(image.getPath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] trainImagge=stream.toByteArray();
        String encodeImage= Base64.encodeToString(trainImagge, Base64.DEFAULT);
        Log.d(TAG,encodeImage);
        return  encodeImage;
    }

    class TrainAsysTask extends AsyncTask<String,String,String> {
        private String studentId;
        private String path;

        public TrainAsysTask(String studentId,String path){
            this.path=path;
            this.studentId=studentId;
        }

        @Override
        protected void onPostExecute(String s) {

            if(null == s){
                return;
            }

            try {
                Log.d("POST", s);
                JSONArray jsonArray = new JSONArray(s);
                String result = jsonArray.getString(0);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            String current = "";

            String base64Img= loadImageFromDirectory(this.path);

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
                    jsonParam.put("id", studentId);
                    jsonParam.put("img", base64Img);

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
