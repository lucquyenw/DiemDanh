package com.example.pc.attendance.models;

/**
 * Created by PC on 19/03/2018.
 */

public class Registration {
    private String subjectId;
    private String studentId;
    private String term;
    private int year;
    private String classId;

    public Registration(String subjectId, String studentId, String term, int year, String classId) {
        this.subjectId = subjectId;
        this.studentId = studentId;
        this.term = term;
        this.year = year;
        this.classId = classId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
