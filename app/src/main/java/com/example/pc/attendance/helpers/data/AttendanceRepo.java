package com.example.pc.attendance.helpers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pc.attendance.models.AttendanceDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by azaudio on 7/21/2018.
 */

public class AttendanceRepo {
    DBHelper dbHelper;

    public AttendanceRepo(Context context){
        dbHelper=new DBHelper(context);
    }

    public void insert(AttendanceDetail data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(new Date());
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(AttendanceDetail.KEY_DATE,strDate);
        values.put(AttendanceDetail.KEY_STUDENTID,data.getStudentId());
        values.put(AttendanceDetail.KEY_CLASSID,data.getClassId());
        values.put(AttendanceDetail.KEY_ISABSENT,data.isAbsent());
        values.put(AttendanceDetail.KEY_ISUPDATE,data.isUpdate());
        db.insert(AttendanceDetail.TABLE,null,values);
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from "+AttendanceDetail.TABLE);
        db.close();

    }

    public void updateServer(String date,String classID,String studentID){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();;
        cv.put(AttendanceDetail.KEY_ISUPDATE,true);
        int result=0;
        String selectQuery="UPDATE "+AttendanceDetail.TABLE +" SET "
                +AttendanceDetail.KEY_ISUPDATE+"=1"+" WHERE "
                +"("+AttendanceDetail.KEY_CLASSID+"='"+classID+"' AND "
                +AttendanceDetail.KEY_DATE+"='"+date+"' AND "
                +AttendanceDetail.KEY_STUDENTID+"='"+studentID+"')";
        db.execSQL(selectQuery);
        //db.update(AttendanceDetail.TABLE,cv,"("+AttendanceDetail.KEY_DATE+"="+date+" ,"+AttendanceDetail.KEY_CLASSID+"="+classID+")",null);
        db.close();

    }

    public ArrayList<String> getAllDate(String classID){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String strDate;
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+AttendanceDetail.KEY_DATE +" FROM "
                +AttendanceDetail.TABLE+" WHERE "
                +AttendanceDetail.KEY_CLASSID+" =?"
                +" GROUP BY "+ AttendanceDetail.KEY_DATE
                +" ORDER BY "+AttendanceDetail.KEY_DATE+" ASC";
        ArrayList<String> result=new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{classID});
        if(cursor.moveToFirst()){
            do{
                strDate=cursor.getString(cursor.getColumnIndex(AttendanceDetail.KEY_DATE));


                result.add(strDate);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<AttendanceDetail> getAllClass(String classID){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate;
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT * FROM "
                +AttendanceDetail.TABLE+" WHERE "
                +AttendanceDetail.KEY_CLASSID+"=?";
        ArrayList<AttendanceDetail> result=new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{classID});
        if(cursor.moveToFirst()){
            do{
                AttendanceDetail ad=new AttendanceDetail();
                ad.setClassId(cursor.getString(cursor.getColumnIndex(AttendanceDetail.KEY_CLASSID)));;
                ad.setStudentId(cursor.getString(cursor.getColumnIndex(AttendanceDetail.KEY_STUDENTID)));
                ad.setUpdate(cursor.getInt(cursor.getColumnIndex(AttendanceDetail.KEY_ISUPDATE)));
                ad.setAbsent(cursor.getInt(cursor.getColumnIndex(AttendanceDetail.KEY_ISABSENT)));
                strDate=cursor.getString(cursor.getColumnIndex(AttendanceDetail.KEY_DATE));

                try {
                    Date strday = sdf.parse(strDate);
                    ad.setDate(strday);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                result.add(ad);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }
}
