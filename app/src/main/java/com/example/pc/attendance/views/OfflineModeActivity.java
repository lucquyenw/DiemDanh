package com.example.pc.attendance.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.attendance.R;
import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.helpers.data.ClassRepo;
import com.example.pc.attendance.helpers.data.StudentRepo;
import com.example.pc.attendance.helpers.data.SubjectRepo;
import com.example.pc.attendance.models.MyClass;
import com.example.pc.attendance.models.OMClass;
import com.example.pc.attendance.models.Student;
import com.example.pc.attendance.models.Subject;
import com.example.pc.attendance.viewModels.Converters.OMClassAdapter;
import com.example.pc.attendance.viewModels.Converters.OStudentRecylerViewAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OfflineModeActivity extends Activity {

    Spinner spinSubjectName;
    TextView tvClassGroup;
    Button btnAttendance;
    Button btnTrain;
    RecyclerView rcStudentList;
    ProgressDialog mProgressDialog;

    String classID;

    ArrayList<OMClass> snDataSubject=new ArrayList<>();
    ArrayList<Student> students=new ArrayList<>();

    OStudentRecylerViewAdapter studentArrayAdapter;

    ClassRepo classRepo;
    SubjectRepo subjectRepo;
    StudentRepo studentRepo;

    String currentClassID;
    private String url;
    private String port;
    String imgFolderPath;
    private String studentId;

    public static final String PROTOCOL = "http://";
    private static final String API_Train_Image = "/api/class/downloadfilexml";
    public static final String HOST_ADDRESS = "api_key";
    public static final String PORT_KEY = "port_key";
    private static String TAG="OfflineModeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offline_mode);

        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this);
        url=sharedPref.getString(HOST_ADDRESS,"");
        port=sharedPref.getString(PORT_KEY,"");

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);
        spinSubjectName=(Spinner) findViewById(R.id.spin_offMode_subjectName);
        tvClassGroup=(TextView) findViewById(R.id.spin_offMode_ClassGroup);
        btnAttendance=(Button) findViewById(R.id.btn_OffMode_Attendance);
        btnTrain=(Button) findViewById(R.id.btn_OffMode_train);




        classRepo=new ClassRepo(this);
        subjectRepo=new SubjectRepo(this);
        studentRepo=new StudentRepo(this);


        setDataSubject();
        OMClassAdapter snSubjectArrayAdapter=new OMClassAdapter(this,R.layout.omclass_item,snDataSubject);


        spinSubjectName.setAdapter(snSubjectArrayAdapter);
        spinSubjectName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvClassGroup.setText(snDataSubject.get(position).getClassID());
                students.clear();
                students.addAll(studentRepo.getAllStudentByClass(snDataSubject.get(position).getClassID()));
                classID=snDataSubject.get(position).getClassID();
                studentArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), RecognitionActivity.class);
                intent.putExtra("offline",true);
                startActivity(intent);
            }
        });

        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),TrainingActivity.class);
                intent.putExtra("offline",true);
                intent.putExtra("classid",classID);
                startActivity(intent);
            }
        });
        studentArrayAdapter=new OStudentRecylerViewAdapter(this,students);

        rcStudentList=(RecyclerView) findViewById(R.id.rc_offMode_studentList);
        rcStudentList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        rcStudentList.setAdapter(studentArrayAdapter);

    }

    private void setDataSubject(){
        ArrayList<Subject> subjectArrayList=subjectRepo.getAllListSubject();
       // Log.d("offlinemod",String.valueOf(subjectArrayList.size()));
        for(int i=0;i<subjectArrayList.size();i++){
            Subject subject=subjectArrayList.get(i);
            String temp=subject.getName();
            //Log.d("offlinemode",temp);
            ArrayList<MyClass> myClassArrayList=classRepo.getAllCLassBySubject(subject.getId());
           // Log.d("offlinemod",String.valueOf(myClassArrayList.size()));
            for(int j=0;j<myClassArrayList.size();j++){
                MyClass myClass=myClassArrayList.get(j);
                String name=temp+"-"+myClass.getGroup()+"-"+myClass.getYear();
                OMClass omClass=new OMClass(myClass.getClassId(),name);
                snDataSubject.add(omClass);
               // Log.d("offlinemode",temp);
                temp="";
            }
        }
    }



    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, PROTOCOL + url + ":" + port);
        return PROTOCOL + url + ":" + port + API_Train_Image;
    }
    private class DownloaXMLTask extends AsyncTask<Void,Integer,ArrayList<String>> {
        private String classID;
        private String[] fileNames;
        private ArrayList<String>  item=new ArrayList<>();
        private Context context;

        public DownloaXMLTask(String classid, String[] fileName, Context context) {
            this.classID = classid;
            this.fileNames = fileName;
            this.context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.show();
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
            if(item!=null){
                mProgressDialog.setMessage("Đang tải "+(item.size())+"/"+fileNames.length);
            }else{
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            item=new ArrayList<>();
            for(String filename:fileNames){
                if(downloadXML(classID,filename).equals("Download Failed")==false){
                    item.add(downloadXML(classID,filename));
                }

            }

            return item;
        }

        private String downloadXML(String classID, String fileName) {
            URL url;
            HttpURLConnection conn = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                url = new URL(getServerUrl() + "?classID=" + classID + "&fileName=" + fileName);
                Log.d("url", getServerUrl() + "?classID=" + classID + "&fileName=" + fileName);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    conn.disconnect();
                    return "Download Failed";
                   /* return "Server returned HTTP " + conn.getResponseCode()
                            + " " + conn.getResponseMessage();*/
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = conn.getContentLength();

                // download the file
                input = conn.getInputStream();
                output = new FileOutputStream(FileHelper.SVM_OFFLINE_PATH + "/" + fileName);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total+=count;
                    publishProgress((int)((total*100)/count));
                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (conn != null)
                    conn.disconnect();
            }
            return "Tải xuống hoàn tất";
        }

        @Override
        protected void onPostExecute(ArrayList<String>  strings) {
            super.onPostExecute(strings);
            mProgressDialog.dismiss();
            if(strings.isEmpty()){
                Toast.makeText(OfflineModeActivity.this, "Hiện nhóm học này không có dữ liệu để nhận dạng", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
