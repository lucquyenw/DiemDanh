package com.example.pc.attendance.views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.example.pc.attendance.PreProcessor.PreProcessorFactory;
import com.example.pc.attendance.R;
import com.example.pc.attendance.Recognition.Recognition;
import com.example.pc.attendance.Recognition.RecognitionFactory;
import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.helpers.MatName;
import com.example.pc.attendance.helpers.PreferencesHelper;
import com.example.pc.attendance.helpers.data.MultipartUtility;
import com.example.pc.attendance.helpers.data.StudentRepo;
import com.example.pc.attendance.models.Student;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class TrainingActivity extends Activity {
    private static final String TAG="TrainingActivity";


    public static final String PROTOCOL = "http://";
    private static final String postXML = "/api/class/postXMl";
    public static final String HOST_ADDRESS = "api_key";
    public static final String PORT_KEY = "port_key";

    TextView progress;
    Thread thread;

    private String url;
    private String port;
    private String classID;
    private boolean offline=true;

    private StudentRepo studentRepo;
    ArrayList<Student> students=new ArrayList<>();
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Intent data=getIntent();
        offline=data.getBooleanExtra("offline",true);

        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this);
        url=sharedPref.getString(HOST_ADDRESS,"");
        port=sharedPref.getString(PORT_KEY,"");
        progress=(TextView) findViewById(R.id.progressText);
        progress.setMovementMethod(new ScrollingMovementMethod());

        Intent intent=getIntent();
        classID=intent.getStringExtra("classid");
        studentRepo=new StudentRepo(this);
        students= studentRepo.getAllStudentByClass(classID);

    }

    private boolean checkStudent(String id){
        for(Student student:students){
            if(student.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, PROTOCOL + url + ":" + port);
        return PROTOCOL + url + ":" + port + postXML;
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Handler handler=new Handler(Looper.getMainLooper());
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                if(!Thread.currentThread().isInterrupted()){
                    PreProcessorFactory ppF=new PreProcessorFactory(getApplicationContext());
                    PreferencesHelper preferencesHelper=new PreferencesHelper(getApplicationContext());

                    FileHelper fileHelper=new FileHelper();
                    fileHelper.createDataFolderIfNotExsiting();
                    final File[] persons=fileHelper.getTrainingList();
                    if(persons.length>0){
                        Recognition recognition=  RecognitionFactory.getRecognitionAlorithm(getApplicationContext(),Recognition.TRAINING,classID,offline);
                        for(File person:persons) {
                            Log.d("filename",person.getName());
                            if(checkStudent(person.getName())){
                                if(person.isDirectory()){
                                    File[] files=person.listFiles();
                                    int counter=1;
                                    for(File file:files){
                                        if(FileHelper.isFileAnImage(file)){
                                            Mat imgRgb= Imgcodecs.imread(file.getAbsolutePath());
                                            Imgproc.cvtColor(imgRgb,imgRgb,Imgproc.COLOR_BGRA2RGBA);
                                            Mat processedImage=new Mat();
                                            imgRgb.copyTo(processedImage);
                                            List<Mat> images=ppF.getProcessedImage(processedImage,PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                            if(images==null||images.size()>1){
                                                continue;
                                            }else{
                                                processedImage=images.get(0);
                                            }
                                            if(processedImage.empty()){
                                                continue;
                                            }
                                            Log.d(TAG,file.getParent());
                                            String[] tokens=file.getParent().split("/");
                                            final String name =tokens[tokens.length-1];

                                            MatName m=new MatName("processedImage",processedImage);
                                            fileHelper.saveMatToImage(m,FileHelper.DATA_PATH);

                                            recognition.addImage(processedImage,name,false);

                                            final int counterPost=counter;
                                            final int filesLength=files.length;
                                            progress.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progress.append("Hình "+counterPost+" của "+filesLength+" từ "+name+"thêm vào.\n");
                                                }
                                            });

                                            counter++;
                                        }
                                    }
                                }
                            }

                        }


                        if(recognition.train()){
                            if(offline==false) {
                                AsytaskUploadTrainingXML task = new AsytaskUploadTrainingXML(classID);
                                task.execute();
                            }
                            finish();
                            }else{
                            finish();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }else{
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        thread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thread.interrupt();
    }
    private class AsytaskUploadTrainingXML extends AsyncTask<String,String,String>{
        private String classID;
        private String path1;
        private String path2;
        private String path3;
        public AsytaskUploadTrainingXML(String ClassID){
            this.classID=ClassID;
            this.path1=FileHelper.SVM_OFFLINE_PATH+"/labelMap_train";
            this.path2= FileHelper.SVM_OFFLINE_PATH+"/svm_train";
            this.path3=FileHelper.SVM_OFFLINE_PATH+"/svm_train_model";
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                MultipartUtility multipartUtility=new MultipartUtility(getServerUrl()+"?ClassId="+this.classID);
                File file=new File(this.path1);
                multipartUtility.addFilePart("1",file);
                File file2=new File(this.path2);
                multipartUtility.addFilePart("2",file2);
                File file3=new File(this.path3);
                multipartUtility.addFilePart("3",file3);
                multipartUtility.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
