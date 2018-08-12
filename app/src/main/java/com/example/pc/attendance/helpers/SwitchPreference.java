package com.example.pc.attendance.helpers;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by azaudio on 6/26/2018.
 */
// link jni library

public class SwitchPreference extends android.preference.SwitchPreference  {
    public SwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public CharSequence getSummary() {
        if(this.isChecked()) {
            return this.getSwitchTextOn();
        } else {
            return this.getSwitchTextOff();
        }
    }
}
