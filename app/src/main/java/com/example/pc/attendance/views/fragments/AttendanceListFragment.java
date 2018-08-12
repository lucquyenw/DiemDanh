package com.example.pc.attendance.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.example.pc.attendance.R;
import com.example.pc.attendance.helpers.NetworkUtils;
import com.example.pc.attendance.helpers.data.AttendanceRepo;
import com.example.pc.attendance.models.AttendanceDetail;
import com.example.pc.attendance.models.Student;
import com.example.pc.attendance.viewModels.ConnectionVM;
import com.example.pc.attendance.viewModels.Converters.DateAdapter;
import com.example.pc.attendance.viewModels.Converters.StudentRecylerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AttendanceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AttendanceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendanceListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final String TAG="Studentfm";

    private static final String API_SUBJECT = "/student/absentTrue";
    private static final String API_DATES = "/api/attendance/getADDate";
    private static final String API_UPLOAD="/attendance/postAttendance";
    private String url;
    private String port;
    private SharedPreferences currentAPI;

    private ArrayList<AttendanceDetail> attendanceDetails;
    private AttendanceRepo attendanceRepo;

    private String date;
    private Spinner snDay;
    private Button btnUpload;
    private ArrayList<String> dates=new ArrayList<>();
    private DateAdapter dateAdapter;
    private String ClassId;
    private ArrayList<Student> students=new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private StudentRecylerViewAdapter studentRecylerViewAdapter;
    private String time;
    String formattedDate;

    public AttendanceListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendanceListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendanceListFragment newInstance(String param1, String param2) {
        AttendanceListFragment fragment = new AttendanceListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentAPI = PreferenceManager.getDefaultSharedPreferences(getActivity());
        url=currentAPI.getString(ConnectionVM.API_KEY,"");
        port=currentAPI.getString(ConnectionVM.PORT_KEY,"");

        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
            ClassId = getArguments().getString("classId");
        }
        Date c= Calendar.getInstance().getTime();
        Log.d(TAG,c.toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c);
        Log.d(TAG,formattedDate);
        dates.add(formattedDate);




        attendanceRepo=new AttendanceRepo(getActivity());
        attendanceDetails=new ArrayList<>();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_attendance_list, container, false);

        snDay=(Spinner) view.findViewById(R.id.spinnerDay);
        btnUpload=(Button) view.findViewById(R.id.btnUpload);

        loadDateList();
        this.dateAdapter=new DateAdapter(view.getContext(),R.layout.spinner_dropdown_item,dates);

        snDay.setAdapter(dateAdapter);
        snDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                students.clear();
                loadAttendanceList((String)parent.getAdapter().getItem(position));
                date=(String)parent.getAdapter().getItem(position);
                studentRecylerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_attendance);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        studentRecylerViewAdapter=new StudentRecylerViewAdapter(view.getContext(), students);
        recyclerView.setAdapter(studentRecylerViewAdapter);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtils.isNetworkAvailable(v.getContext())){
                    if(students.size()!=0) {
                        for (Student student : students) {
                            attendanceRepo.updateServer(date, ClassId, student.getId());
                            UploadAsynctask uploadTask = new UploadAsynctask(student.getId(), ClassId);
                            uploadTask.execute();

                        }
                        students.clear();
                        loadAttendanceList(date);
                        studentRecylerViewAdapter.notifyDataSetChanged();
                    }
                }else{
                    Snackbar.make(view, "Hãy kết nối internet ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });
        return view;
    }

    public void loadDateList(){
        dates=attendanceRepo.getAllDate(ClassId);


    }
    public void loadAttendanceList(String date){
        attendanceDetails=attendanceRepo.getAllClass(ClassId);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        for (AttendanceDetail ad: attendanceDetails){
            Student student=new Student();
            student.setId(ad.getStudentId());
            if(ad.isUpdate()==false){
                student.setIsUpdate("Đã cập nhật");
            }else{
                student.setIsUpdate("Chưa cập nhật");
            }

            if(date.equals(sdf.format(ad.getDate()))){
                students.add(student);
            }

        }

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public String getServerUrl(String api){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, ConnectionVM.PROTOCOL + url + ":" + port);
        return ConnectionVM.PROTOCOL + url + ":" + port + api;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class UploadAsynctask extends AsyncTask<String,String,Boolean>{
        String classID;
        String studentID;
        Boolean isSuccess;

        public UploadAsynctask(String studentID,String classID){
            this.studentID=studentID;
            this.classID=classID;
        }

        private boolean isSuccess(Boolean rs){
            return rs;
        }



        @Override
        protected void onPostExecute(Boolean s) {
            if(false == s){
                return ;
            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean current = false;
            try{
                URL url;
                HttpURLConnection urlConnection = null;
                Log.d(TAG, "doInBackGround");

                try{
                    url = new URL(getServerUrl(API_UPLOAD)+"?date="+date+"&studentID="+studentID+"&classID="+classID);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    urlConnection.setRequestProperty("Accept","application/json");
                    Log.d(TAG,url.toString());
                    if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                        current= true;
                        //Log.d("ttttt","tttt ");
                        attendanceRepo.updateServer(date, ClassId, studentID);
                    }
                    current= false;





                } catch (ConnectException e){
                    e.printStackTrace();
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return current;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            AttendanceListFragment atfragment=new AttendanceListFragment();

            ft.detach(atfragment).attach(atfragment).commit();
        }
    }

    private class DateAsystask extends AsyncTask<String,String,String>{
        String classID;

        public DateAsystask(String classid){
            this.classID=classid;
        }

        @Override
        protected void onPostExecute(String s) {
            if(null == s){
                return;
            }

            try {
                JSONArray subjectsJSON = new JSONArray(s);
                for(int i=0;i<subjectsJSON.length();i++){
                    String d= (String)subjectsJSON.get(i);
                    if(d.compareTo(formattedDate)!=0){
                        dates.add(d);
                    }

                }
                dateAdapter.notifyDataSetChanged();
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String current = "";
            try{
                URL url;
                HttpURLConnection urlConnection = null;
                Log.d(TAG, "doInBackGround");

                try{
                    url = new URL(getServerUrl(API_DATES)+"?classID="+classID);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    urlConnection.setRequestProperty("Accept","application/json");
                    Log.d(TAG,url.toString());
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);

                    int data = reader.read();

                    while(data != -1){
                        current += (char) data;
                        data = reader.read();
                    }
                    return current;

                } catch (ConnectException e){
                    e.printStackTrace();
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return current;
        }
    }

    private class AsystaskStudent extends AsyncTask<String,String,String> {
        String classId;
        String date;
        public AsystaskStudent(String classId,String date){
            this.classId=classId;
            this.date=date;

        }
        @Override
        protected void onPostExecute(String s) {

            if(null == s){
                return;
            }

            try {
                JSONArray subjectsJSON = new JSONArray(s);

                for(int i=0;i<subjectsJSON.length();i++){
                    JSONObject c=subjectsJSON.getJSONObject(i);
                    String id=c.getString("studentId");
                    String fristName=c.getString("firstName");
                    String lastName=c.getString("lastName");
                    String name=fristName.concat(lastName);
                    String gender=c.getString("gender");
                    String dOB=c.getString("dateOfBirth");
                    Student student=new Student(id,name,gender,dOB);
                    students.add(student);

                }
                studentRecylerViewAdapter.notifyDataSetChanged();
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... strings) {
            String current = "";
            students.clear();
            try{
                URL url;
                HttpURLConnection urlConnection = null;
                Log.d(TAG, "doInBackGround");

                try{
                    url = new URL(getServerUrl(API_SUBJECT)+"?classId="+classId+"&date="+date);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    urlConnection.setRequestProperty("Accept","application/json");
                    Log.d(TAG,url.toString());
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);

                    int data = reader.read();

                    while(data != -1){
                        current += (char) data;
                        data = reader.read();
                    }
                    return current;

                } catch (ConnectException e){
                    e.printStackTrace();
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return current;
        }
    }
}
