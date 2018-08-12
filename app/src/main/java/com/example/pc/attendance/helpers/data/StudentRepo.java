package com.example.pc.attendance.helpers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.pc.attendance.models.Student;

import java.util.ArrayList;

/**
 * Created by azaudio on 7/2/2018.
 */

public class StudentRepo {
    private DBHelper dbHelper;

    public StudentRepo(Context context){
        dbHelper=new DBHelper(context);
    }


    public String insert(Student student){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Student.KEY_ID,student.getId());
        values.put(Student.KEY_CLASSID,student.getClassID());
        db.insert(Student.TABLE,null,values);
        db.close();
        return student.getId();
    }

    public void deleteAll(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.execSQL("delete from "+Student.TABLE);
        db.close();

    }

    public ArrayList<Student> getAllStudentByClass(String ClassId){
        SQLiteDatabase db= dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+Student.KEY_ID+","
                +Student.KEY_CLASSID+" FROM "
                +Student.TABLE+" WHERE "
                +Student.KEY_CLASSID+"=?";
        int iCount=0;

        ArrayList<Student> students=new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{ClassId});

        if(cursor.moveToFirst()){
            do{
                Student student=new Student();
                student.setId(cursor.getString(cursor.getColumnIndex(Student.KEY_ID)));
                student.setClassID(cursor.getString(cursor.getColumnIndex(Student.KEY_CLASSID)));
                students.add(student);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return students;
    }
}
