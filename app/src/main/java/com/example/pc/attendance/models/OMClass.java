package com.example.pc.attendance.models;

/**
 * Created by azaudio on 7/6/2018.
 */

public class OMClass {
    private String classID;
    private String temp;

    public OMClass(String classID, String temp) {
        this.classID = classID;
        this.temp = temp;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
