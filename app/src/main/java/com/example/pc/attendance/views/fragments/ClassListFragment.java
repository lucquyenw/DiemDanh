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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.pc.attendance.R;
import com.example.pc.attendance.helpers.NetworkUtils;
import com.example.pc.attendance.helpers.data.ClassRepo;
import com.example.pc.attendance.helpers.data.SubjectRepo;
import com.example.pc.attendance.models.MyClass;
import com.example.pc.attendance.viewModels.ConnectionVM;
import com.example.pc.attendance.viewModels.Converters.ClassRecyclerViewAdapter;
import com.example.pc.attendance.views.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClassListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClassListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassListFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG="Classfm";

    private static final String API_SUBJECT = "/Classes/byYear";

    private Context context;
    private String url;
    private String port;
    private SharedPreferences currentAPI;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SubjectRepo subjectRepo;
    private ClassRepo classRepo;
    private Handler handler;


    private OnFragmentInteractionListener mListener;
    private String subjectId;
    private Spinner spinTerm, spinYear;
    private ArrayList<MyClass> classes=new ArrayList<>();
    private ClassRecyclerViewAdapter classRecyclerViewAdapter;
    private RecyclerView recyclerView;

    private ArrayList<String> getTerms(){
        ArrayList<String> terms = new ArrayList<>();
        terms.add("Term");
        terms.add("HK-1");
        terms.add("HK-2");
        terms.add("HK-3");
        return terms;
    }

    private ArrayList<String> getYears(){
        ArrayList<String> years = new ArrayList<>();
        years.add("Year");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int i = currentYear - 5; i <= currentYear; i++){
            years.add(String.valueOf(i));
        }

        return years;
    }

    public ClassListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassListFragment newInstance(String param1, String param2) {
        ClassListFragment fragment = new ClassListFragment();
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
            subjectId = getArguments().getString("subjectId");
        }
        subjectRepo=new SubjectRepo(getActivity());
        classRepo=new ClassRepo(getActivity());
        handler=new Handler();
        Timer timer=new Timer();
        timer.schedule(new CheckConnection(getActivity()),0, MainActivity.MILLISECONDS);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_class_list, container, false);
        spinTerm = (Spinner) view.findViewById(R.id.spin_term);
        spinYear = (Spinner) view.findViewById(R.id.spin_year);
        spinTerm.setAdapter(new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, getTerms()));
        spinYear.setAdapter(new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, getYears()));

        spinTerm.setSelection(0);
        spinYear.setSelection(getYears().size()-1);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_class);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        classRecyclerViewAdapter=new ClassRecyclerViewAdapter(view.getContext(), classes);
        recyclerView.setAdapter(classRecyclerViewAdapter);
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

    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, ConnectionVM.PROTOCOL + url + ":" + port);
        return ConnectionVM.PROTOCOL + url + ":" + port + API_SUBJECT;
    }

    public String exchangeDate(String date){
        String day = "";
        switch (date) {
            case "sunday":
                day = "chủ nhật";
                break;
            case "monday":
                day = "thứ hai";
                break;
            case "tuesday":
                day = "thứ ba";
                break;
            case "wednesday":
                day = "thứ tư";
                break;
            case "thursday":
                day = "thứ năm";
                break;
            case "friday":
                day = "thứ 6";
                break;
            case "saturday":
                day = "thứ 7";
                break;
        }
        return day;
    }

    class CheckConnection extends TimerTask {
        private Context context;

        public CheckConnection(Context context) {
            this.context = context;
        }

        public void run() {

            if (NetworkUtils.isNetworkAvailable(context)) {
                classes.clear();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        AsystaskClass asystaskClass = new AsystaskClass(subjectId, "2018");
                        asystaskClass.execute();
                    }
                });

            } else {
                //DISCONNECTED
                classes.clear();
                ArrayList<MyClass> temp = classRepo.getAllCLassBySubject(subjectId);
                for (MyClass myClass : temp) {

                    myClass.setSubjectName(subjectRepo.getSubject(subjectId).getName());
                    myClass.setDayOfWeek(exchangeDate(myClass.getDayOfWeek()));
                    classes.add(myClass);
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
                        classRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    private class AsystaskClass extends AsyncTask<String,String,String> {
        String subjectId;
        String year;

        public AsystaskClass(String subjectId, String year) {
            this.subjectId = subjectId;
            this.year = year;
        }

        @Override
        protected void onPostExecute(String s) {

            if (null == s) {
                return;
            }

            try {

                JSONArray subjectsJSON = new JSONArray(s);
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                for (int i = 0; i < subjectsJSON.length(); i++) {
                    JSONObject c = subjectsJSON.getJSONObject(i);
                    String id = c.getString("classId");
                    String room = c.getString("room");
                    String timeStart = c.getString("timeStart");
                    String timeEnd = c.getString("timeEnd");
                    String dayOfWeek = exchangeDate(c.getString("dayOfWeek"));
                    String year = c.getString("year");
                    String subjectId = c.getString("subjectId");
                    String group = c.getString("groupId");
                    MyClass mclass = new MyClass(id, subjectId, room, timeStart, timeEnd, dayOfWeek, year);
                    mclass.setSubjectName(subjectRepo.getSubject(subjectId).getName());
                    mclass.setGroup(group);
                    classes.add(mclass);

                }
                classRecyclerViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                Log.d(TAG, "doInBackGround");

                try {
                    url = new URL(getServerUrl() + "?subjectId=" + subjectId + "&year=" + year);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    urlConnection.setRequestProperty("Accept", "application/json");

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);

                    int data = reader.read();

                    while (data != -1) {
                        current += (char) data;
                        data = reader.read();
                    }

                    return current;

                } catch (ConnectException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return current;
        }
    }
}

