package com.example.pc.attendance.helpers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pc.attendance.models.MyClass;

import java.util.ArrayList;

/**
 * Created by azaudio on 7/2/2018.
 */

public class ClassRepo {
    DBHelper dbHelper;

    public ClassRepo(Context context){
        dbHelper=new DBHelper(context);
    }

    public void insert(MyClass myClass){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(MyClass.KEY_ID,myClass.getClassId());
        values.put(MyClass.KEY_GROUP,myClass.getGroup());
        values.put(MyClass.KEY_ROOM,myClass.getRoom());
        values.put(MyClass.KEY_SUBJECTID,myClass.getSubjectId());
        values.put(MyClass.KEY_DAYOFWEEK,myClass.getDayOfWeek());
        values.put(MyClass.KEY_TIMEEND,myClass.getTimeEnd());
        values.put(MyClass.KEY_TIMESTART,myClass.getTimeStart());
        values.put(MyClass.KEY_YEAR,myClass.getYear());
        db.insert(myClass.TABLE,null,values);
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from "+MyClass.TABLE);
        db.close();

    }


    public ArrayList<MyClass> getAllClass(String UID){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT * FROM "
                +MyClass.TABLE;
        ArrayList<MyClass> result=new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do{
                MyClass myClass=new MyClass();
                myClass.setClassId(cursor.getString(cursor.getColumnIndex(MyClass.KEY_ID)));
                myClass.setGroup(cursor.getString(cursor.getColumnIndex(MyClass.KEY_GROUP)));
                myClass.setRoom(cursor.getString(cursor.getColumnIndex(MyClass.KEY_ROOM)));
                myClass.setYear(cursor.getString(cursor.getColumnIndex(MyClass.KEY_YEAR)));
                myClass.setDayOfWeek(cursor.getString(cursor.getColumnIndex(MyClass.KEY_DAYOFWEEK)));
                myClass.setSubjectId(cursor.getString(cursor.getColumnIndex(MyClass.KEY_SUBJECTID)));
                result.add(myClass);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<MyClass> getAllCLassBySubject(String SubjectID){
        SQLiteDatabase db= dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+MyClass.KEY_ID+","
                +MyClass.KEY_SUBJECTID+","
                +MyClass.KEY_ISTRAINED+","
                +MyClass.KEY_DAYOFWEEK+","
                +MyClass.KEY_GROUP+" ,"
                +MyClass.KEY_ROOM +" ,"
                +MyClass.KEY_YEAR+ " FROM "
                +MyClass.TABLE+" WHERE "
                + MyClass.KEY_SUBJECTID+"=?";
        int iCount=0;

        ArrayList<MyClass> myClasses=new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{SubjectID});

        if(cursor.moveToFirst()){
            do{
                MyClass myClass=new MyClass();
                myClass.setClassId(cursor.getString(cursor.getColumnIndex(MyClass.KEY_ID)));
                if(cursor.getInt(cursor.getColumnIndex(MyClass.KEY_ISTRAINED))==0){
                    myClass.setTrained(false);
                }else{
                    myClass.setTrained(true);
                }
                myClass.setSubjectId(cursor.getString(cursor.getColumnIndex(MyClass.KEY_SUBJECTID)));
                myClass.setDayOfWeek(cursor.getString(cursor.getColumnIndex(MyClass.KEY_DAYOFWEEK)));
                myClass.setGroup(cursor.getString(cursor.getColumnIndex(MyClass.KEY_GROUP)));
                myClass.setRoom(cursor.getString(cursor.getColumnIndex(MyClass.KEY_ROOM)));
                myClass.setYear(cursor.getString(cursor.getColumnIndex(MyClass.KEY_YEAR)));
                myClasses.add(myClass);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return myClasses;
    }


}
