package com.example.pc.attendance.Recognition;

import android.content.Context;

import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.helpers.OneToOneMap;
import com.example.pc.attendance.helpers.PreferencesHelper;

import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by azaudio on 7/12/2018.
 */

public class SupportVectorMachine implements Recognition {
    PreferencesHelper preferencesHelper;
    private FileHelper fh;
    private File trainingFile;
    private File predictionFile;
    private File testFile;
    private List<String> trainingList;
    private List<String> testList;
    private OneToOneMap<String,Integer> labelMap;
    private OneToOneMap<String,Integer> labelMapTest;
    private int method;
    private boolean offline=false;
    private String classID;

    public SupportVectorMachine(Context context, int method,boolean offline) {
        preferencesHelper = new PreferencesHelper(context);
        fh = new FileHelper();
        trainingFile = fh.createOSvmTrainingFile();
        predictionFile = fh.createOSvmPredictionFile();
        testFile = fh.createOSvmTestFile();
        trainingList = new ArrayList<>();
        testList = new ArrayList<>();
        labelMap = new OneToOneMap<String, Integer>();
        labelMapTest = new OneToOneMap<String, Integer>();
        this.method = method;
        this.offline=true;
        if(method == RECOGNITION){
            loadFromFile();
        }
    }

    public SupportVectorMachine(Context context,String classID ,int method) {
        preferencesHelper = new PreferencesHelper(context);
        fh = new FileHelper();
        trainingFile = fh.createSvmTrainingFile(classID);
        predictionFile = fh.createSvmPredictionFile(classID);
        testFile = fh.createSvmTestFile();
        trainingList = new ArrayList<>();
        testList = new ArrayList<>();
        labelMap = new OneToOneMap<String, Integer>();
        labelMapTest = new OneToOneMap<String, Integer>();
        this.method = method;
        this.classID=classID;
        if(method == RECOGNITION){
            loadFromFile();
        }
    }

    public SupportVectorMachine(File trainingFile, File predictionFile){
        fh = new FileHelper();
        this.trainingFile = trainingFile;
        this.predictionFile = predictionFile;
        trainingList = new ArrayList<>();
    }

    // link jni library
    static {
        System.loadLibrary("jnilibsvm");
    }

    // connect the native functions
    private native void jniSvmTrain(String cmd);
    private native void jniSvmPredict(String cmd);

    @Override
    public boolean train() {

        fh.saveStringList(trainingList, trainingFile);

        // linear kernel -t 0
        String svmTrainOptions = preferencesHelper.getSvmTrainOptions();
        String training = trainingFile.getAbsolutePath();
        String model = trainingFile.getAbsolutePath() + "_model";

        jniSvmTrain(svmTrainOptions + " " + training + " " + model);

        saveToFile();
        return true;
    }

    public boolean trainProbability(String svmTrainOptions) {
        fh.saveStringList(trainingList, trainingFile);

        String training = trainingFile.getAbsolutePath();
        String model = trainingFile.getAbsolutePath() + "_model";
        jniSvmTrain(svmTrainOptions + " -b 1" + " " + training + " " + model);

        return true;
    }

    @Override
    public String recognize(Mat img, String expectedLabel) {
        try {
            FileWriter fw = new FileWriter(predictionFile, false);
            String line = imageToSvmString(img, expectedLabel);
            testList.add(line);
            fw.append(line);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String prediction = predictionFile.getAbsolutePath();
        String model = trainingFile.getAbsolutePath() + "_model";
        String output = predictionFile.getAbsolutePath() + "_output";
        jniSvmPredict(prediction + " " + model + " " + output);

        try {
            BufferedReader buf = new BufferedReader(new FileReader(output));
            int iLabel = Integer.valueOf(buf.readLine());
            buf.close();
            return labelMap.getKey(iLabel);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String recognizeProbability(String svmString){
        try {
            FileWriter fw = new FileWriter(predictionFile, false);
            fw.append(String.valueOf(1) + svmString);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String prediction = predictionFile.getAbsolutePath();
        String model = trainingFile.getAbsolutePath() + "_model";
        String output = predictionFile.getAbsolutePath() + "_output";
        jniSvmPredict("-b 1 " + prediction + " " + model + " " + output);

        try {
            BufferedReader buf = new BufferedReader(new FileReader(output));
            // read header line
            String probability = buf.readLine() + "\n";
            // read content line
            probability = probability + buf.readLine();
            buf.close();
            return probability;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveToFile() {
        if(offline==false){
            if(method == TRAINING){
                String path=fh.SVM_PATH+"/"+classID+"/";
                fh.saveLabelMapToFile(path, labelMap, "train");
            } else {
                String path=fh.SVM_PATH+"/"+classID+"/";
                fh.saveLabelMapToFile(path, labelMapTest, "test");
            }
        }
        else{
            if(method == TRAINING){
                String path=fh.SVM_OFFLINE_PATH+"/"+classID+"/";
                fh.saveLabelMapToFile(path, labelMap, "train");
            } else {
                String path=fh.SVM_OFFLINE_PATH+"/"+classID+"/";
                fh.saveLabelMapToFile(path, labelMapTest, "test");
            }
        }

    }

    @Override
    public void saveTestData(){
        fh.saveStringList(testList, testFile);
    }

    @Override
    public void loadFromFile() {
        if(offline==true){
            String path=fh.SVM_OFFLINE_PATH+"/"+classID+"/";
            labelMap = fh.getLabelMapFromFile(path);
        }else{
            String path=fh.SVM_PATH+"/"+classID+"/";
            labelMap = fh.getLabelMapFromFile(path);
        }

    }

    @Override
    public void addImage(Mat img, String label, boolean featuresAlreadyExtracted) {
        // Ignore featuresAlreadyExtracted because either SVM get the features from TensorFlow or Caffe, or it takes the image reshaping method (image itself)
        if(method == TRAINING){
            trainingList.add(imageToSvmString(img, label));
        } else {
            testList.add(imageToSvmString(img, label));
        }
    }

    public void addImage(String svmString, String label) {
        trainingList.add(label + " " + svmString);
    }

    public Mat getFeatureVector(Mat img){
        return img.reshape(1,1);
    }

    private String imageToSvmString(Mat img, String label){
        int iLabel = 0;
        if(method == TRAINING){
            if (labelMap.containsKey(label)) {
                iLabel = labelMap.getValue(label);
            } else {
                iLabel = labelMap.size() + 1;
                labelMap.put(label, iLabel);
            }
        } else {
            if (labelMapTest.containsKey(label)){
                iLabel = labelMapTest.getValue(label);
            } else {
                iLabel = labelMapTest.size() + 1;
                labelMapTest.put(label, iLabel);
            }
        }
        String result = String.valueOf(iLabel);
        return result + getSvmString(img);
    }

    public String getSvmString(Mat img){
        img = getFeatureVector(img);
        String result = "";
        for (int i=0; i<img.cols(); i++){
            result = result + " " + i + ":" + img.get(0,i)[0];
        }
        return result;
    }
}
