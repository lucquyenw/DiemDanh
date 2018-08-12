package com.example.pc.attendance.models;

import java.util.Date;

/**
 * Created by PC on 14/02/2018.
 */

public class AttendanceDetail {
    public static final String TABLE="attendancedetail";
    public static final String KEY_DATE="date";
    public static final String KEY_STUDENTID="studentid";
    public static final String KEY_CLASSID="classid";
    public static final String KEY_ISABSENT="isAbsent";
    public static final String KEY_ISUPDATE="isUpdate";


    private String studentId;
    private String classId;
    private boolean isAbsent;
    private Date date;
    private boolean isUpdate;


    public AttendanceDetail() {
    }

    public AttendanceDetail(String studentId, String classId, boolean isAbsent, boolean isUpdate) {
        this.studentId = studentId;
        this.classId = classId;
        this.isAbsent = isAbsent;
        this.isUpdate=isUpdate;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(int update) {
        if(update==0){
            isUpdate=true;
        }else{
            isUpdate=false;
        }
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public boolean isAbsent() {
        return isAbsent;
    }

    public void setAbsent(int absent){
        if(absent==0){
            isAbsent=true;
        }else{
            isAbsent=false;
        }
    }
    public void setAbsent(boolean absent) {
        isAbsent = absent;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
