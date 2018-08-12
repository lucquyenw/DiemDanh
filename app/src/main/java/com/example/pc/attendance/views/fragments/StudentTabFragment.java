package com.example.pc.attendance.views.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.pc.attendance.R;
import com.example.pc.attendance.helpers.FileHelper;
import com.example.pc.attendance.viewModels.ConnectionVM;
import com.example.pc.attendance.views.RecognitionActivity;
import com.example.pc.attendance.views.TrainingActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudentTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String PROTOCOL = "http://";
    private static final String API_TRAIN_XML = "/api/class/downloadfilexml";
    public static final String HOST_ADDRESS = "api_key";
    public static final String PORT_KEY = "port_key";
    private static final String TAG="StudentTabfm";


    private String url;
    private String port;
    private SharedPreferences currentAPI;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String classId;
    private OnFragmentInteractionListener mListener;
    ProgressDialog mProgressDialog;


    private Toolbar toolbar;

    private ViewPager viewPager;
    private Button btnAttendance;
    private Button btnTrain;
    private TabLayout tabLayout;
    //Layout
    public static int[] resourceIds = {
            R.layout.fragment_student_list
    };
    public StudentTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentTabFragment newInstance(String param1, String param2) {
        StudentTabFragment fragment = new StudentTabFragment();
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

            classId = getArguments().getString("classId");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_student_tab,container,false);
        btnAttendance=(Button) view.findViewById(R.id.btnAttendance);

        if(isNameAlreadyUsed(new FileHelper().getSVMList(),classId)==false){
            btnAttendance.setEnabled(false);
        }

        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),RecognitionActivity.class);
                Bundle bClass=new Bundle();
                bClass.putString("classid",classId);
                bClass.putBoolean("offline",false);
                intent.putExtras(bClass);
                getActivity().startActivityForResult(intent,10001);
            }
        });


        btnTrain=(Button) view.findViewById(R.id.btnTrain);
        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),TrainingActivity.class);
                intent.putExtra("offline",false);
                intent.putExtra("classid",classId);
                intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
       // DownloaXMLTask downloaXMLTask=new DownloaXMLTask(classId,new String[]{"labelMap_train","svm_train","svm_train_model"},getContext());

        //downloaXMLTask.execute();

        viewPager = (ViewPager) view.findViewById(R.id.slViewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.studentTabs);
        tabLayout.setupWithViewPager(viewPager);

        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage("Đang tải...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        return view;
    }

    private boolean isNameAlreadyUsed(File[] list, String name){
        boolean used=false;
        if(list!=null && list.length>0){
            for(File person:list){
                String[] tokens=person.getAbsolutePath().split("/");
                final String foldername=tokens[tokens.length-1];
                if(foldername.equals(name)){
                    used=true;
                    break;
                }
            }
        }
        return used;
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter=new ViewPagerAdapter(getChildFragmentManager());
        Bundle args = new Bundle();
        args.putString("classId", classId);
        StudentListFragment fragment = new StudentListFragment();
        fragment.setArguments(args);
        adapter.addFragment(fragment,"Tất cả");

        AttendanceListFragment atfragment=new AttendanceListFragment();

        atfragment.setArguments(args);
        adapter.addFragment(atfragment,"Danh sách điểm danh");

        viewPager.setAdapter(adapter);
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
        Log.d(TAG, PROTOCOL + url + ":" + port);
        return PROTOCOL + url + ":" + port + API_TRAIN_XML;
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
                Toast.makeText(getContext(), "Hiện nhóm học này không có dữ liệu để nhận dạng", Toast.LENGTH_SHORT).show();
            }

        }
    }

}
