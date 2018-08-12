package com.example.pc.attendance.helpers;

import org.opencv.core.MatOfFloat;

/**
 * Created by azaudio on 4/27/2018.
 */

public class Eyes {
    double dist;
    MatOfFloat rightCenter;
    MatOfFloat leftCenter;
    double angle;

    public Eyes(double dist, MatOfFloat rightCenter, MatOfFloat leftCenter, double angle) {
        this.dist = dist;
        this.rightCenter = rightCenter;
        this.leftCenter = leftCenter;
        this.angle = angle;
    }

    public double getDist() {
        return dist;
    }

    public MatOfFloat getRightCenter() {
        return rightCenter;
    }

    public MatOfFloat getLeftCenter() {
        return leftCenter;
    }

    public double getAngle() {
        return angle;
    }
}
