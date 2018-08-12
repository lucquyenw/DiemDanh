package com.example.pc.attendance.PreProcessor.Coutours;

/**
 * Created by azaudio on 6/26/2018.
 */

import com.example.pc.attendance.PreProcessor.Command;
import com.example.pc.attendance.PreProcessor.PreProcessor;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class Masking implements Command {

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images) {
            preProcessor.normalize0255(img);

            /***************************************************************************************
             *    Title: Automatic calculation of low and high thresholds for the Canny operation in opencv
             *    Author: VP
             *    Date: 16.04.2013
             *    Code version: -
             *    Availability: http://stackoverflow.com
             *
             ***************************************************************************************/

            double otsu_thresh_val = Imgproc.threshold(img, img, 0, 255, Imgproc.THRESH_OTSU);
            Imgproc.Canny(img, img, otsu_thresh_val * 0.5, otsu_thresh_val);
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
