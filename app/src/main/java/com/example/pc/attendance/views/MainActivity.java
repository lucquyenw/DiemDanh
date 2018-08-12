package com.example.pc.attendance.views;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.example.pc.attendance.R;
import com.example.pc.attendance.databinding.ActivityMainBinding;
import com.example.pc.attendance.helpers.NetworkUtils;
import com.example.pc.attendance.helpers.data.ClassRepo;
import com.example.pc.attendance.helpers.data.StudentRepo;
import com.example.pc.attendance.helpers.data.SubjectRepo;
import com.example.pc.attendance.models.MyClass;
import com.example.pc.attendance.models.Student;
import com.example.pc.attendance.models.Subject;
import com.example.pc.attendance.viewModels.ConnectionVM;
import com.example.pc.attendance.viewModels.MainVM;
import com.example.pc.attendance.views.fragments.AttendanceFragment;
import com.example.pc.attendance.views.fragments.AttendanceListFragment;
import com.example.pc.attendance.views.fragments.ClassListFragment;
import com.example.pc.attendance.views.fragments.StudentListFragment;
import com.example.pc.attendance.views.fragments.StudentTabFragment;
import com.example.pc.attendance.views.fragments.SubjectListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SubjectListFragment.OnFragmentInteractionListener, StudentListFragment.OnFragmentInteractionListener,
        AttendanceFragment.OnFragmentInteractionListener, ClassListFragment.OnFragmentInteractionListener,
        StudentTabFragment.OnFragmentInteractionListener,AttendanceListFragment.OnFragmentInteractionListener{

    public static final int MILLISECONDS = 10000; //10 seconds

    private FragmentManager fragmentManager;
    private MainVM mainVM;
    private ActivityMainBinding mainBinding;
    private AutoCompleteTextView autoComplSearch;
    private NavigationView navigationView;
    private int fragmentToAddStack;

    private static final String API_SUBJECT = "/Classes/byTeacher";

    private String teacherId;
    int currentDate;

    private SharedPreferences currentAPI;
    private String url;
    private String port;

    private ArrayList<MyClass> classes=new ArrayList<>();
    private ArrayList<Student> listStudent=new ArrayList<>();
    private Subject subject;
    private ClassRepo classRepo;
    private StudentRepo studentRepo;
    private SubjectRepo subjectRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mainVM = new MainVM(this);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setMainVm(mainVM);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        studentRepo=new StudentRepo(this);
        subjectRepo=new SubjectRepo(this);
        classRepo=new ClassRepo(this);


        currentAPI = PreferenceManager.getDefaultSharedPreferences(this);
        url=currentAPI.getString(ConnectionVM.API_KEY,"");
        port=currentAPI.getString(ConnectionVM.PORT_KEY,"");
        teacherId=currentAPI.getString("UID","");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkUtils.isNetworkAvailable(view.getContext())==true){
                    UpdateAysnctask task=new UpdateAysnctask(teacherId);
                    task.execute();
                    Snackbar.make(view, "Làm mới dữ liệu", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Hãy kết nối internet ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_attendent_list);

        fragmentManager = getSupportFragmentManager();

        // Show attendance fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.content_main, new AttendanceFragment());
        transaction.commit();

        fragmentToAddStack = R.id.nav_attendent_list;



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

            int checkedItem = mainVM.popFragmentBackStacks();
            navigationView.setCheckedItem(checkedItem);
            if(checkedItem == R.id.nav_attendent_list){
                fragmentManager.popBackStack();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        mainVM.addFragmentBackStacks(fragmentToAddStack);
        fragmentToAddStack = id;
        switch (id){
            case R.id.nav_attendent_list:
                transaction.replace(R.id.content_main, new AttendanceFragment());
                break;
            case R.id.nav_subject_list:
                transaction.replace(R.id.content_main, new SubjectListFragment());
                transaction.addToBackStack(null);
                break;
        }

        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        return ConnectionVM.PROTOCOL + url + ":" + port + API_SUBJECT;
    }

    private class UpdateAysnctask extends AsyncTask<String,String,String> {
        String teacherId;
        public UpdateAysnctask(String teacherId){
            this.teacherId=teacherId;
        }

        @Override
        protected void onPostExecute(String s) {

            if(s.equals("")){
                return;
            }

            try {



                JSONArray subjectsJSON = new JSONArray(s);
                if(subjectsJSON.length()!=0){

                    studentRepo.deleteAll();
                    classRepo.deleteAll();
                    subjectRepo.deleteAll();
                }
                for(int i=0;i<subjectsJSON.length();i++){
                    JSONObject c=subjectsJSON.getJSONObject(i);
                    JSONArray students=c.getJSONArray("learnings");

                    JSONObject jsubject=c.getJSONObject("subject");
                    String subjectID=jsubject.getString("subjectId");
                    String subjectName=jsubject.getString("name");
                    String credit=jsubject.getString("credit");
                    subject=new Subject();
                    subject.setId(subjectID);
                    subject.setName(subjectName);
                    subject.setCredit(credit);

                    subjectRepo.insert(subject);


                    String id=c.getString("classId");
                    String room=c.getString("room");
                    String timeStart=c.getString("timeStart");
                    String timeEnd=c.getString("timeEnd");
                    String dayOfWeek=c.getString("dayOfWeek");
                    String year=c.getString("year");
                    String subjectId=c.getString("subjectId");
                    String group=c.getString("groupId");
                    MyClass mclass=new MyClass(id,subjectId,room,timeStart,timeEnd,dayOfWeek,year);

                    mclass.setGroup(group);
                    classRepo.insert(mclass);
                    for(int j=0;j<students.length();j++){
                        JSONObject student=students.getJSONObject(j);
                        String sid=student.getString("studentId");
                        String classId=student.getString("classId");
                        Student student1=new Student();
                        student1.setId(sid);
                        student1.setClassID(classId);
                        studentRepo.insert(student1);
                    }
                }


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

                try{
                    url = new URL(getServerUrl()+"?teacherId="+teacherId);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    urlConnection.setRequestProperty("Accept","application/json");

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
