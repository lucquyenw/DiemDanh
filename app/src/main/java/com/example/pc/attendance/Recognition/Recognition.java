package com.example.pc.attendance.Recognition;

import org.opencv.core.Mat;

/**
 * Created by azaudio on 6/20/2018.
 */

public interface Recognition
{
    public static final int TRAINING=0;
    public static final int RECOGNITION=1;

    boolean train();
    String recognize(Mat img,String expectedLabel);
    void saveTestData();
    void saveToFile();
    void loadFromFile();
    void addImage(Mat img,String label,boolean featuresAlreadyExtrated);
    Mat getFeatureVector(Mat img);
}
