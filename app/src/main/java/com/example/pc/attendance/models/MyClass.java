package com.example.pc.attendance.models;

/**
 * Created by PC on 19/03/2018.
 */

public class MyClass {

    public static final String TABLE="MyClass";
    public static final String KEY_ID="id";
    public static final String KEY_SUBJECTID="subjectid";
    public static final String KEY_GROUP="classgroup";
    public static final String KEY_ROOM="room";
    public static final String KEY_TIMESTART="timestart";
    public static final String KEY_TIMEEND="timeend";
    public static final String KEY_DAYOFWEEK="dayofweek";
    public static final String KEY_YEAR="year";
    public static final  String KEY_ISTRAINED="istrained";

    private String classId;
    private String subjectId;
    private String room;
    private String group;
    private String timeStart;
    private String timeEnd;
    private String dayOfWeek;
    private String year;
    private String SubjectName;
    private boolean isTrained;


    public MyClass(){

    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public MyClass(String classId, String subjectId, String room, String timeStart, String timeEnd, String dayOfWeek, String year) {
        this.classId = classId;
        this.subjectId = subjectId;
        this.room = room;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.dayOfWeek = dayOfWeek;
        this.year=year;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public boolean isTrained() {
        return isTrained;
    }

    public void setTrained(boolean trained) {
        isTrained = trained;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
