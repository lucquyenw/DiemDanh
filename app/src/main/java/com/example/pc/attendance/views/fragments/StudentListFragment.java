package com.example.pc.attendance.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pc.attendance.R;
import com.example.pc.attendance.helpers.NetworkUtils;
import com.example.pc.attendance.helpers.data.StudentRepo;
import com.example.pc.attendance.models.Student;
import com.example.pc.attendance.viewModels.ConnectionVM;
import com.example.pc.attendance.viewModels.Converters.StudentRecylerViewAdapter;
import com.example.pc.attendance.views.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final String TAG="Studentfm";

    private static final String API_SUBJECT = "/student/byClass";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private StudentRepo studentRepo;

    private Handler handler;

    private String url;
    private String port;
    private SharedPreferences currentAPI;

    private String ClassId;
    private ArrayList<Student> students=new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private Button btnAttendance;
    private  StudentRecylerViewAdapter studentRecylerViewAdapter;
    public StudentListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentListFragment newInstance(String param1, String param2) {
        StudentListFragment fragment = new StudentListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


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

        studentRepo=new StudentRepo(getActivity());
        handler=new Handler();
        Timer timer=new Timer();
        timer.schedule(new CheckConnection(getActivity()),0, MainActivity.MILLISECONDS);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_list, container, false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_student);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        studentRecylerViewAdapter=new StudentRecylerViewAdapter(view.getContext(), students);
        recyclerView.setAdapter(studentRecylerViewAdapter);
        return view;
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


    class CheckConnection extends TimerTask {
        private Context context;
        public CheckConnection(Context context){
            this.context = context;
        }
        public void run() {

            if(NetworkUtils.isNetworkAvailable(context)){
                students.clear();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        AsystaskStudent asystaskStudent= new AsystaskStudent(ClassId);
                        asystaskStudent.execute();
                    }
                });

            }else {
                //DISCONNECTED
                students.clear();
                ArrayList<Student> temp= studentRepo.getAllStudentByClass(ClassId);
                for (Student student:temp) {
                    student.setFullname("kết nối mạng để xem tên");
                    students.add(student);
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(getActivity() == null)
                    return;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        studentRecylerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, ConnectionVM.PROTOCOL + url + ":" + port);
        return ConnectionVM.PROTOCOL + url + ":" + port + API_SUBJECT;
    }
    private class AsystaskStudent extends AsyncTask<String,String,String> {
        String classId;
        public AsystaskStudent(String classId){
            this.classId=classId;
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
            try{
                URL url;
                HttpURLConnection urlConnection = null;
                Log.d(TAG, "doInBackGround");

                try{
                    url = new URL(getServerUrl()+"?classId="+classId);
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
