package com.example.pc.attendance.Recognition;

import android.content.Context;
import android.util.Log;

import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.helpers.MatName;
import com.example.pc.attendance.helpers.OneToOneMap;
import com.example.pc.attendance.helpers.PreferencesHelper;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azaudio on 6/20/2018.
 */

public class Eigenfaces implements Recognition {
    private Context context;
    private Mat Gamma=new Mat();
    private Mat Psi=new Mat();
    private Mat Phi=new Mat();
    private Mat eigVectors=new Mat();
    private Mat Omega=new Mat();
    private Mat testList=new Mat();
    private List<Integer> labelList;
    private List<Integer> labelListTest;
    private OneToOneMap<String,Integer> labelMap;
    private OneToOneMap<String,Integer> labelMapTest;
    private String filename="eigenfaces.xml";
    private int method;


    public Eigenfaces(Context context,int method){
        this.context=context;
        this.labelList=new ArrayList<>();
        this.labelListTest=new ArrayList<>();
        this.labelMap=new OneToOneMap<String,Integer>();
        this.labelMapTest=new OneToOneMap<String,Integer>();
        this.method=method;
        if(method==RECOGNITION){
            loadFromFile();
        }
    }

    @Override
    public boolean train() {
        if(Gamma.empty()){
            return false;
        }
        computePsi();
        computePhi();
        computeEigVectors();
        Omega=getFeatureVector(Phi);
        saveToFile();
        return true;
    }

    @Override
    public String recognize(Mat img, String expectedLabel) throws RuntimeException{
        img=img.reshape(1,1);

        img.convertTo(img, CvType.CV_32F);
        Core.subtract(img,Psi,img);

        Mat projected=getFeatureVector(img);

        img.convertTo(img,CvType.CV_8U);
        addImage(projected,expectedLabel,true);

        Mat distance=new Mat(Omega.rows(),1,CvType.CV_64FC1);
        for(int i=0;i<Omega.rows();i++){
            double dist=Core.norm(projected.row(0),Omega.row(i),Core.NORM_L2);
            distance.put(i,0,dist);
        }

        Mat sortedDist=new Mat(Omega.rows(),1,CvType.CV_8UC4);
        Core.sortIdx(distance,sortedDist,Core.SORT_EVERY_COLUMN+Core.SORT_ASCENDING);

        int index=(int)(sortedDist.get(0,0)[0]);
        return labelMap.getKey(labelList.get(index));
    }

    private void computePsi(){
        Core.reduce(Gamma, Psi, 0, Core.REDUCE_AVG);
    }

    private void computePhi(){
        Mat Psi_repeated = new Mat();
        Core.repeat(Psi, Gamma.rows(), 1, Psi_repeated);
        Core.subtract(Gamma, Psi_repeated, Phi);
    }

    private void computeEigVectors(){
        PreferencesHelper preferencesHelper = new PreferencesHelper(context);
        float pca_threshold = preferencesHelper.getPCAThreshold();
        Log.d("PCAThreshold",String.valueOf(pca_threshold));
        Core.PCACompute(Phi, Psi, eigVectors, pca_threshold);
    }

    @Override
    public Mat getFeatureVector(Mat original) {
        Mat projected = new Mat();
        Core.PCAProject(original, Psi, eigVectors, projected);
        return projected;
    }

    @Override
    public void saveTestData() {
        FileHelper fh=new FileHelper();
        fh.saveIntegerList(labelListTest,fh.createLabelFile(fh.EIGENFACES_PATH,"test"));
        fh.saveLabelMapToFile(fh.EIGENFACES_PATH,labelMapTest,"test");
        MatName mTestList=new MatName("TestList",testList);
        List<MatName> listMat=new ArrayList<>();
        listMat.add(mTestList);
        fh.saveMatListToXml(listMat,fh.EIGENFACES_PATH,"testlist.xml");
    }

    @Override
    public void saveToFile() {
        FileHelper fh=new FileHelper();
        fh.saveIntegerList(labelList,fh.createLabelFile(fh.EIGENFACES_PATH,"train"));
        fh.saveLabelMapToFile(fh.EIGENFACES_PATH,labelMap,"train");
        MatName mOmega=new MatName("Omega",Omega);
        MatName mPsi=new MatName("Psi",Psi);
        MatName mEigVectors=new MatName("eigVectors",eigVectors);

        MatName mPhi=new MatName("Phi",Phi);
        List<MatName> listMat=new ArrayList<MatName>();
        listMat.add(mOmega);
        listMat.add(mPsi);
        listMat.add(mEigVectors);
        listMat.add(mPhi);
        fh.saveMatListToXml(listMat,fh.EIGENFACES_PATH,filename);
    }

    @Override
    public void loadFromFile() {
        FileHelper fh=new FileHelper();
        MatName mOmega=new MatName("Omega",Omega);
        MatName mPsi=new MatName("Psi",Psi);
        MatName mEigVectors=new MatName("eigVectors",eigVectors);
        List<MatName> listMat=new ArrayList<MatName>();
        listMat.add(mOmega);
        listMat.add(mPsi);
        listMat.add(mEigVectors);
        listMat=fh.getMatListFromXml(listMat,fh.EIGENFACES_PATH,filename);
        for(MatName mat:listMat){
            switch (mat.getName()){
                case "Omega":
                    Omega=mat.getMat();
                    break;
                case "Psi":
                    Psi=mat.getMat();
                    break;
                case "eigVectors":
                    eigVectors=mat.getMat();
                    break;
            }
        }
        labelList = fh.loadIntegerList(fh.createLabelFile(fh.EIGENFACES_PATH,"train"));
        labelMap  = fh.getLabelMapFromFile(fh.EIGENFACES_PATH);
    }



    @Override
    public void addImage(Mat img, String label, boolean featuresAlreadyExtrated) {
        int iLabel=0;
        if(method==TRAINING){
            //Mat m2;
           // img=img.ones(1,1,CvType.CV_8UC1);
            Gamma.push_back(img.reshape(1,1));
            if(labelMap.containsKey(label)){
                iLabel=labelMap.getValue(label);
            }else{
                iLabel=labelMap.size()+1;
                labelMap.put(label,iLabel);
            }
            labelList.add(iLabel);
        }else{
            testList.push_back(img);
            if(labelMapTest.containsKey(label)){
                iLabel=labelMapTest.getValue(label);
            }else{
                iLabel=labelMapTest.size()+1;
                labelMapTest.put(label,iLabel);
            }
            labelListTest.add(iLabel);
        }
    }


}
