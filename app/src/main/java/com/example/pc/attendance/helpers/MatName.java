package com.example.pc.attendance.helpers;

import org.opencv.core.Mat;

/**
 * Created by azaudio on 4/27/2018.
 */

public class MatName {
    private String name;
    private Mat mat;

    public MatName(String name, Mat mat){
        this.name = name;
        this.mat = mat;
    }

    public String getName() {
        return name;
    }

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }
}
