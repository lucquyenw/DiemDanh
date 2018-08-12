package com.example.pc.attendance.PreProcessor.ContrastAdjustment;

import com.example.pc.attendance.PreProcessor.Command;
import com.example.pc.attendance.PreProcessor.PreProcessor;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azaudio on 6/26/2018.
 */

public class HistogrammEqualization implements Command {


    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            img.convertTo(img, CvType.CV_8U);
            Imgproc.equalizeHist(img, img);
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
