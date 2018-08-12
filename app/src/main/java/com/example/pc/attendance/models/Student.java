package com.example.pc.attendance.models;

/**
 * Created by PC on 14/02/2018.
 */

public class Student {
    public static final  String TABLE="Student";

    public static final String KEY_ID="id";
    public static final String KEY_CLASSID="classID";
    public static final String KEY_gender="gender";
    public static final String KEY_dateofbirth="dateofbirth";

    private String id;
    private String classID;
    private String fullname;
    private String gender;
    private String dateOfBirth;
    private String imagePath;
    private String isUpdate;

    public String getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(String isUpdate) {
        this.isUpdate = isUpdate;
    }

    public Student(){
    };
    public Student(String id, String fullname, String gender, String dateOfBirth) {
        this.id = id;
        this.fullname = fullname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.isUpdate="Chưa cập nhật";
    }

    public Student(String id, String fullname, String gender, String dateOfBirth, String imagePath) {
        this.id = id;
        this.fullname = fullname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.imagePath = imagePath;
        this.isUpdate="Chưa cập nhật";
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
