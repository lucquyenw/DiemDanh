package com.example.pc.attendance.views;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import com.example.pc.attendance.R;

/**
 * Created by azaudio on 6/26/2018.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
