package com.example.pc.attendance.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.JavaCameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by azaudio on 4/27/2018.
 */

public class CustomCameraView extends JavaCameraView implements Camera.PictureCallback{
    private Camera.Parameters params;

    private String mPictureFileName;
    private Context context;
    public CustomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }


    protected void setDisplayOrientation(Camera camera, int angle){
        Method downPolymorphic;
        try
        {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[] { angle });
        }
        catch (Exception e1)
        {
        }
    }
    public void setExposure(int exposure) {
        params =  mCamera.getParameters();
        float minEx = params.getMinExposureCompensation();
        float maxEx = params.getMaxExposureCompensation();

        exposure = Math.round((maxEx - minEx) / 100 * exposure + minEx);

        params.setExposureCompensation(exposure);
        Log.d("JavaCameraViewSettings", "Exposure Compensation " + String.valueOf(exposure));
        mCamera.setParameters(params);

    }

    public void setNightPortrait() {
        params =  mCamera.getParameters();

        List<String> sceneModes = params.getSupportedSceneModes();
        if (sceneModes != null){
            if (sceneModes.contains(Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT)) {
                Log.d("JavaCameraViewSettings", "Night portrait mode supported");
                params.setSceneMode(Camera.Parameters.SCENE_MODE_NIGHT_PORTRAIT);
            } else {
                Log.d("JavaCameraViewSettings", "Night portrait mode supported");
            }

            mCamera.setParameters(params);
        } else {
            Toast.makeText(getContext(), "The selected camera doesn't support Night Portrait Mode", Toast.LENGTH_SHORT).show();
        }

    }
    public void takePicture(final String fileName) {

        this.mPictureFileName = fileName;

        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
        Log.d("onPictureTaken","PictureTaken");
        // Write the image in a file (in jpeg format)
        try {
            File file=new File(Environment.getExternalStorageDirectory()+"/Attandance/"+mPictureFileName);
            Log.d("EnvironmenDirectory",file.getAbsolutePath());
            Bitmap originalBitmap= BitmapFactory.decodeByteArray(data,0,data.length);
            Bitmap resizeBitmap=Bitmap.createScaledBitmap(originalBitmap,300,300,false);
            FileOutputStream fos=new FileOutputStream(file);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] byteArray = stream.toByteArray();
            fos.write(byteArray);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }
}
