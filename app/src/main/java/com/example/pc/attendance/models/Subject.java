package com.example.pc.attendance.models;

/**
 * Created by PC on 14/02/2018.
 */

public class Subject {
    public static final String TABLE="Subject";
    public static final String KEY_ID="id";
    public static final String KEY_NAME="name";
    public static final String KEY_CREDIT="credit";

    private String id;
    private String name;
    private String credit;

    public Subject(){

    }

    public Subject(String id, String name, String credit) {
        this.id = id;
        this.name = name;
        this.credit = credit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

}
