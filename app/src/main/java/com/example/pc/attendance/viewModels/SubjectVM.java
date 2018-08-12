package com.example.pc.attendance.viewModels;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.pc.attendance.R;
import com.example.pc.attendance.models.Subject;
import com.example.pc.attendance.views.fragments.ClassListFragment;

/**
 * Created by PC on 19/03/2018.
 */

public class SubjectVM extends BaseObservable {

    private Context context;
    private Subject object;



    public SubjectVM(Context context, Subject object) {
        this.context = context;
        this.object = object;

    }


    @Bindable
    public Subject getObject() {
        return object;
    }


    public void onAccessClick(){
        Bundle args = new Bundle();
        args.putString("subjectId", object.getId());
        Log.d("SubjectVM","onAccessClick");
        ClassListFragment fragment = new ClassListFragment();
        fragment.setArguments(args);

        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main, fragment)
                .addToBackStack(null)
                .commit();
    }



}
