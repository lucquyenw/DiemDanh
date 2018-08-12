package com.example.pc.attendance.helpers.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pc.attendance.models.AttendanceDetail;
import com.example.pc.attendance.models.MyClass;
import com.example.pc.attendance.models.Student;
import com.example.pc.attendance.models.Subject;

/**
 * Created by azaudio on 7/2/2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME="attendance.db";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_SUBJECT= "CREATE TABLE " + Subject.TABLE  + "("
                + Subject.KEY_ID  + " TEXT PRIMARY KEY,"
                + Subject.KEY_NAME + " TEXT,"
                + Subject.KEY_CREDIT +" TEXT)";
        String CREATE_TABLE_CLASS= "CREATE TABLE " + MyClass.TABLE  + "("
                + MyClass.KEY_ID  + " TEXT PRIMARY KEY,"
                + MyClass.KEY_GROUP + " TEXT, "
                + MyClass.KEY_SUBJECTID + " TEXT, "
                + MyClass.KEY_ROOM + " TEXT, "
                + MyClass.KEY_TIMESTART+ " TEXT, "
                + MyClass.KEY_TIMEEND+" TEXT,"
                + MyClass.KEY_DAYOFWEEK+" TEXT,"
                + MyClass.KEY_YEAR+" TEXT,"
                +MyClass.KEY_ISTRAINED+" INTEGER DEFAULT 0,"
                +"FOREIGN KEY ("+MyClass.KEY_SUBJECTID +") REFERENCES "
                +Subject.TABLE +"("+ Subject.KEY_ID+")"
                +")";

        String CREATE_TABLE_STUDENT= "CREATE TABLE " + Student.TABLE  + "("
                + Student.KEY_ID  + " TEXT,"
                +Student.KEY_CLASSID+ " TEXT,"+
                Student.KEY_gender+" TEXT,PRIMARY KEY("
                +Student.KEY_ID+","+Student.KEY_CLASSID+")"
                +"FOREIGN KEY ("+Student.KEY_CLASSID +") REFERENCES "
                +MyClass.TABLE +"("+ MyClass.KEY_ID+")"
                +")";

        String CREATE_TABLE_ATTENDANCE= "CREATE TABLE " + AttendanceDetail.TABLE  + "("
                + AttendanceDetail.KEY_DATE  + " TEXT,"
                + AttendanceDetail.KEY_CLASSID + " TEXT, "
                + AttendanceDetail.KEY_STUDENTID + " TEXT, "
                + AttendanceDetail.KEY_ISABSENT + " INTEGER DEFAULT 0, "
                + AttendanceDetail.KEY_ISUPDATE+ " INTEGER DEFAULT 0, "
                + "PRIMARY KEY("+AttendanceDetail.KEY_DATE+","
                +AttendanceDetail.KEY_STUDENTID+","
                +AttendanceDetail.KEY_CLASSID+"),"
                +"FOREIGN KEY ("+AttendanceDetail.KEY_CLASSID +") REFERENCES "
                +MyClass.TABLE +"("+ MyClass.KEY_ID+"),"
                +"FOREIGN KEY ("+AttendanceDetail.KEY_STUDENTID +") REFERENCES "
                +AttendanceDetail.TABLE +"("+ AttendanceDetail.KEY_STUDENTID+")"
                +")";

        db.execSQL(CREATE_TABLE_SUBJECT);
        db.execSQL(CREATE_TABLE_CLASS);
        db.execSQL(CREATE_TABLE_STUDENT);
        db.execSQL(CREATE_TABLE_ATTENDANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AttendanceDetail.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MyClass.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Subject.TABLE);


        // Create tables again
        onCreate(db);
    }


}
