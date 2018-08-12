package com.example.pc.attendance.Recognition;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by azaudio on 6/20/2018.
 */

public class RecognitionFactory {
    public static  Recognition getRecognitionAlorithm(Context context,int method,String classID,Boolean offline){
        Resources resources=context.getResources();
        if(offline==true){
            return new TensorFlow(context,method,false);

        }else{
            return new TensorFlow(context,classID,method);
        }



    }
}
