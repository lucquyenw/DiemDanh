package com.example.pc.attendance.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.pc.attendance.R;
import com.example.pc.attendance.models.MyClass;
import com.example.pc.attendance.views.fragments.StudentTabFragment;

/**
 * Created by azaudio on 5/25/2018.
 */

public class MyClassVM extends BaseObservable {
    private Context context;
    private MyClass ccClass;

    public MyClassVM(Context context, MyClass mClass){
        this.context = context;
        this.ccClass = mClass;

    }

    @Bindable
    public MyClass getCcClass(){
        return this.ccClass;
    }

    public void onAccessClick(){
        Bundle args = new Bundle();
        args.putString("classId", ccClass.getClassId());
        Log.d("MyClassVN","onAccessClick");
        StudentTabFragment fragment = new StudentTabFragment();
        fragment.setArguments(args);

        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment)
                .addToBackStack(null)
                .commit();
    }
}
