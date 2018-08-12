package com.example.pc.attendance.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.pc.attendance.R;
import com.example.pc.attendance.databinding.ActivityConnectionBinding;
import com.example.pc.attendance.viewModels.ConnectionVM;

public class ConnectionActivity extends AppCompatActivity{

    private ConnectionVM connectionVM;
    private ActivityConnectionBinding binding;
    private String UID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isNetWorkAvaiable()==true) {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_connection);
            connectionVM = new ConnectionVM(this, "ConnectionActivity");
            binding.setViewModel(connectionVM);
        }else{
            Intent intent=new Intent(this,MainActivity.class);
            SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
            UID=sharedPreferences.getString("UID","Admin");
            if(UID.equals("Admin")!=true) {
                startActivity(intent);
            }
        }
        /*connect = new ConnectAsync();
        dialog = new ConnectServerDialog(this);

        if(connectionVM.getServerUrl().equals("")){
            showDialog();
        }
        else{
            connect.execute();
        }*/
    }

    private boolean isNetWorkAvaiable(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null&&activeNetworkInfo.isConnected();
    }

    /*public void showDialog(){
        dialog.show();
    }

    public void dismissDialog(){
        if(dialog != null) dialog.dismiss();
    }

    public void changeUrl(View v){
        dialog.show();
    }

    public void onReloadUrl(View v){
        Log.d("Connect", "Reload");
        if(connect != null){
            connect.cancel(true);
            connect = null;
        }

        connect = new ConnectAsync();
        connect.execute();
    }

    private class ConnectAsync extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                dismissDialog();
                Log.d("POST", s);
                JSONArray jsonArray = new JSONArray(s);
                String result = jsonArray.getString(0);
                if(result.equals("success")){
                    connectionVM.setStatus(ConnectionVM.CONNECT_SUCCESS);
                }

            } catch (JSONException e){
                e.printStackTrace();
                connectionVM.setStatus(ConnectionVM.CONNECT_FAIL);
            }

        }

        @Override
        protected String doInBackground(String... strings) {
            String current = "";
            try{
                URL url;
                HttpURLConnection urlConnection = null;
                Log.d("Connection", "doInBackGround");

                try{
                    url = new URL(connectionVM.getServerUrl());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(ConnectionVM.CONNECT_TIMEOUT);

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
                    connectionVM.setStatus(ConnectionVM.CONNECT_FAIL);
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                connectionVM.setStatus(ConnectionVM.CONNECT_FAIL);
                return "Exception: " + e.getMessage();
            }
            return current;
        }
    }*/

}
