package com.example.pc.attendance.views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pc.attendance.PreProcessor.PreProcessorFactory;
import com.example.pc.attendance.R;
import com.example.pc.attendance.Recognition.Recognition;
import com.example.pc.attendance.Recognition.RecognitionFactory;
import com.example.pc.attendance.helpers.CustomCameraView;
import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.helpers.MatOperation;
import com.example.pc.attendance.helpers.data.AttendanceRepo;
import com.example.pc.attendance.models.AttendanceDetail;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecognitionActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private ImageView imgStudent;
    private CustomCameraView mRecognitionView;
    private TextView tvRecognition;
    private LinearLayout layoutPredict;
    private ImageButton btnClear;
    private ImageButton btnDone;
    private ImageButton ibtnChangeCamera;
    private Button btnSave;

    private static final String TAG = "Recognition";
    private FileHelper fh;
    private Recognition rec;
    private PreProcessorFactory ppF;

    private boolean front_camera;
    private boolean night_portrait;
    private long lastTime;
    private long timeDiff;
    private int fristTime;
    private int exposure_compensation;
    private String result;
    private String classID;
    private String tempResult="no face";
    private boolean offline=true;

    AttendanceRepo attendanceRepo;
    ArrayList<AttendanceDetail> attendanceDetails;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_recognition);

        imgStudent=(ImageView)findViewById(R.id.imgStudent);
        layoutPredict=(LinearLayout) findViewById(R.id.layoutPredict);
        ibtnChangeCamera=(ImageButton) findViewById(R.id.ibtnChangeCamera);
        btnClear=(ImageButton) findViewById(R.id.btnClear);
        btnSave=(Button) findViewById(R.id.btnSave);


        Intent data=getIntent();
        offline=data.getBooleanExtra("offline",true);
        classID=data.getStringExtra("classid");
        attendanceRepo=new AttendanceRepo(this);
        attendanceDetails=new ArrayList<>();

        fristTime=0;
        lastTime=new Date().getTime();
        timeDiff=5000;

        tvRecognition=(TextView) findViewById(R.id.tvRecognition);
        fh = new FileHelper();
        File folder = new File(fh.getFolderPath());
        if(folder.mkdir() || folder.isDirectory()){
            Log.i(TAG,"New directory for photos created");
        } else {
            Log.i(TAG,"Photos directory already existing");
        }
        mRecognitionView = (CustomCameraView) findViewById(R.id.RecognitionView);
        // Use camera which is selected in settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        front_camera = sharedPref.getBoolean("key_front_camera", false);
        night_portrait = false;
        exposure_compensation = 50;


        if(front_camera==true){
            mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

        }else{
            mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        ibtnChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(front_camera==true){
                    front_camera=false;
                    mRecognitionView.disableView();
                    mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
                    mRecognitionView.enableView();
                }else{
                    front_camera=true;
                    mRecognitionView.disableView();
                    mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
                    mRecognitionView.enableView();

                }
            }
        });


        mRecognitionView.setVisibility(SurfaceView.VISIBLE);
        mRecognitionView.setCvCameraViewListener(this);


        mRecognitionView.setMaxFrameSize(640, 480);
    }




    @Override
    public void onCameraViewStarted(int width, int height) {
        if (night_portrait) {
            mRecognitionView.setNightPortrait();
        }

        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
            mRecognitionView.setExposure(exposure_compensation);


    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();
        Mat img = new Mat();
        imgRgba.copyTo(img);
        List<Mat> images = ppF.getProcessedImage(img, PreProcessorFactory.PreprocessingMode.RECOGNITION);
        Rect[] faces = ppF.getFacesForRecognition();

        if(front_camera){
            Core.flip(imgRgba,imgRgba,1);
        }
        long time=new Date().getTime();
        if(lastTime+timeDiff>time) {
            lastTime = time;

            if (images == null || images.size() == 0 || faces == null || faces.length == 0 || !(images.size() == faces.length)) {
                // skip
                return imgRgba;
            } else {
                faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
                for (int i = 0; i < faces.length; i++) {
                    MatOperation.drawRectangleOnPreview(imgRgba, faces[i], front_camera);
                    result = rec.recognize(images.get(i), "");

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutPredict.setVisibility(View.VISIBLE);

                        if (fh.loadBitmapFromPath(result) != null) {
                            imgStudent.setImageBitmap(fh.loadBitmapFromPath(result));
                        }

                        if(fristTime==0){
                            if(result==""){

                            }else{
                                tempResult=result;
                                fristTime=1;
                            }

                            Log.d("tempResult",tempResult);
                        }


                        final AttendanceDetail recentAD = new AttendanceDetail(result, classID, false, false);
                        if (checkAD(recentAD) == false) {
                            tvRecognition.setText(result + "- đã có sẵn");
                        } else {

                            tvRecognition.setText(result);
                        }
                        Log.d("tempResult",tempResult);
                        Log.d("result",result);
                        if(tempResult.equals("no face")==false) {
                            final AttendanceDetail AD = new AttendanceDetail(tempResult, classID, false, false);
                            if(checkAD(AD)==true){
                                if(result.equals(tempResult)==false){
                                    tempResult=result;
                                    attendanceDetails.add(AD);
                                    attendanceRepo.insert(AD);
                                }
                            }
                        }else{
                            tempResult=result;
                        }

                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (checkAD(recentAD) == true) {
                                    attendanceRepo.insert(recentAD);
                                }
                                setResult(Activity.RESULT_OK);
                                finish();
                            }
                        });



                        btnClear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                tempResult="no face";
                                layoutPredict.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }
        return imgRgba;
    }


    private boolean checkAD(AttendanceDetail ad){
        for(AttendanceDetail temp:attendanceDetails){
            if(temp.getStudentId().equals(ad.getStudentId())==true){
                return false;
            }
        }

        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        ppF = new PreProcessorFactory(getApplicationContext());

        final android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        Thread t = new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                rec = RecognitionFactory.getRecognitionAlorithm(getApplicationContext(), Recognition.RECOGNITION, classID,offline);
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

        t.start();

        // Wait until Eigenfaces loading thread has finished
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mRecognitionView.enableView();
    }

}
