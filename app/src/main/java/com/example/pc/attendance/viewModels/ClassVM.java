package com.example.pc.attendance.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.pc.attendance.models.MyClass;

/**
 * Created by PC on 19/03/2018.
 */

public class ClassVM extends BaseObservable {
    private Context context;
    private MyClass mClass;

    public ClassVM(Context context, MyClass mClass){
        this.context = context;
        this.mClass = mClass;

    }

    @Bindable
    public MyClass getmClass(){
        return this.mClass;
    }

}
