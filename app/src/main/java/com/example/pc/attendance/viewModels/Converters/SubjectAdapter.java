package com.example.pc.attendance.viewModels.Converters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.example.pc.attendance.models.Subject;

/**
 * Created by azaudio on 7/3/2018.
 */

public class SubjectAdapter extends ArrayAdapter<Subject>{

    public SubjectAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
