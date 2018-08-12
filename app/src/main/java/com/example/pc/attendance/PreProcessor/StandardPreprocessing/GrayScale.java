package com.example.pc.attendance.PreProcessor.StandardPreprocessing;

import com.example.pc.attendance.PreProcessor.Command;
import com.example.pc.attendance.PreProcessor.PreProcessor;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azaudio on 4/27/2018.
 */

public class GrayScale  implements Command{
    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            if(img.channels()>1) {
                Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2GRAY);
            }
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
