package com.example.pc.attendance.helpers;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by azaudio on 6/26/2018.
 */

public class ListPreference extends android.preference.ListPreference {
    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return this.getValue();
    }
}
