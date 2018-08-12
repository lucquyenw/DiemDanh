package com.example.pc.attendance.viewModels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;

import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.models.Student;
import com.example.pc.attendance.views.AddPersonActivity;

import java.io.File;

/**
 * Created by azaudio on 5/25/2018.
 */

public class StudentVM extends BaseObservable {
    private Context context;
    private Student student;

    public StudentVM(Context context, Student student){
        this.context = context;
        this.student = student;

    }

    @Bindable
    public Student getStudent(){

        return this.student;
    }

    public String checkExistImage(){
        if(isNameAlreadyUsed(new FileHelper().getTrainingList(),student.getId())){
            return "Đã có hình";
        }
        return "Chụp hình";
    }

    private boolean isNameAlreadyUsed(File[] list, String name){
        boolean used=false;
        if(list!=null && list.length>0){
            for(File person:list){
                String[] tokens=person.getAbsolutePath().split("/");
                final String foldername=tokens[tokens.length-1];
                if(foldername.equals(name)){
                    used=true;
                    break;
                }
            }
        }
        return used;
    }

    public void onAccessClick(){
        Intent intent=new Intent(context,AddPersonActivity.class);
        Bundle b=new Bundle();
        intent.putExtra("id",student.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("Folder","Training");
        context.startActivity(intent);
    }
}
