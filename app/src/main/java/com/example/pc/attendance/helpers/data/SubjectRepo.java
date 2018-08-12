package com.example.pc.attendance.helpers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pc.attendance.models.Subject;

import java.util.ArrayList;

/**
 * Created by azaudio on 7/2/2018.
 */

public class SubjectRepo {
    private DBHelper dbHelper;

    public SubjectRepo(Context context){
        dbHelper=new DBHelper(context);
    }

    public void insert(Subject subject){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Subject.KEY_ID,subject.getId());
        values.put(Subject.KEY_NAME,subject.getName());
        values.put(Subject.KEY_CREDIT,subject.getCredit());

        db.insert(Subject.TABLE,null,values);
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from "+Subject.TABLE);
        db.close();;
    }


    public Subject getSubject(String ID){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+ Subject.KEY_ID+","
                +Subject.KEY_NAME+","+Subject.KEY_CREDIT+" FROM "
                +Subject.TABLE+" WHERE "
                +Subject.KEY_ID+"=?";

        ArrayList<Subject> subjects=new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{ID});
        Subject subject=new Subject();
        if(cursor.moveToFirst()){
            do{
                subject.setId(cursor.getString(cursor.getColumnIndex(subject.KEY_ID)));
                subject.setName(cursor.getString(cursor.getColumnIndex(subject.KEY_NAME)));
                subject.setCredit(cursor.getString(cursor.getColumnIndex(subject.KEY_CREDIT)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return subject;
    }


    public ArrayList<Subject> getAllListSubject(){
        SQLiteDatabase db= dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+ Subject.KEY_ID+","
                +Subject.KEY_NAME+","+Subject.KEY_CREDIT+" FROM "
                +Subject.TABLE;

        ArrayList<Subject> subjects=new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                Subject subject=new Subject();
                subject.setId(cursor.getString(cursor.getColumnIndex(subject.KEY_ID)));
                subject.setName(cursor.getString(cursor.getColumnIndex(subject.KEY_NAME)));
                subject.setCredit(cursor.getString(cursor.getColumnIndex(subject.KEY_CREDIT)));
                subjects.add(subject);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return subjects;
    }
}
