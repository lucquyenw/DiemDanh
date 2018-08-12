package com.example.pc.attendance.viewModels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.pc.attendance.BR;
import com.example.pc.attendance.viewModels.Interfaces.IConnectionVMContract;
import com.example.pc.attendance.views.LoginActivity;
import com.example.pc.attendance.views.OfflineModeActivity;
import com.example.pc.attendance.views.dialogs.ConnectServerDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by PC on 15/02/2018.
 */

public class ConnectionVM extends BaseObservable implements IConnectionVMContract.ViewModel{

    public static final int CONNECT_TIMEOUT = 15000;
    public static final String CONNECT_SUCCESS = "Connect success";
    public static final String CONNECT_FAIL = "Connect fail";
    public static final String CONNECTING = "Connecting...";

    private static final String TAG = "Connection VM";
    public static final String PROTOCOL = "http://";
    private static final String API_CONNECT_PATH = "/api/values";
    public static final String API_KEY = "api_key";
    public static final String PORT_KEY = "port_key";

    private Context context;
    private String status;
    public String url;
    public String port;
    private SharedPreferences currentAPI;
    private SharedPreferences.Editor apiEditor;
    private ConnectServerDialog dialog;
    private ConnectAsync connect;

    public ConnectionVM(Context context, String layout) {
        this.context = context;

        currentAPI = PreferenceManager.getDefaultSharedPreferences(context);
        apiEditor = currentAPI.edit();

        resetUrl();
        status = CONNECTING;

        if(layout.equals("ConnectionActivity")){
            connect = new ConnectAsync();
            connect.execute();
            dialog = new ConnectServerDialog(context);
        }

        //dialog = new ConnectServerDialog(context);


    }


    private void showDialog() {
        dialog.show();
    }

    public void dismissDialog(){
        if(dialog != null) dialog.dismiss();
    }

    @Bindable
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        notifyPropertyChanged(BR._all);
    }

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        notifyPropertyChanged(BR.url);
    }

    public void openOfflineMode(){
        Intent intent=new Intent(this.context, OfflineModeActivity.class);
        context.startActivity(intent);
    }

    @Bindable
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
        notifyPropertyChanged(BR.port);
    }

    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, PROTOCOL + url + ":" + port);
        return PROTOCOL + url + ":" + port + API_CONNECT_PATH;
    }

    @Override
    public void destroy() {

    }

    public void resetUrl(){
        url = currentAPI.getString(API_KEY, "");
        port = currentAPI.getString(PORT_KEY, "");
    }

    public void onSaveClick(View view){
        apiEditor.putString(API_KEY, getUrl());
        apiEditor.putString(PORT_KEY, getPort());
        Log.d(TAG,API_KEY);
        apiEditor.commit();
        this.status = CONNECTING;
        connect = new ConnectAsync();
        connect.execute();
    }

    public void changeUrl(View v){
        dialog.show();
    }

    public void onReloadUrl(View v){
        Log.d("Connect", "Reload");
        if(connect != null){
            connect.cancel(false);
            connect = null;
        }

        this.status = CONNECTING;
        resetUrl();
        connect = new ConnectAsync();
        connect.execute();

    }

    private class ConnectAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            if(null == s){
                setStatus(ConnectionVM.CONNECT_FAIL);

                    showDialog();
                return;
            }

            try {
                Log.d("POST", s);

                JSONArray jsonArray = new JSONArray(s);
                String result = jsonArray.getString(0);
                if(result.equals("success")){
                    setStatus(ConnectionVM.CONNECT_SUCCESS);
                    Toast.makeText(context,
                            result, Toast.LENGTH_LONG).show();

                    context.startActivity(new Intent(context, LoginActivity.class));
                    Log.d(TAG,"Success");
                }else{
                    setStatus(ConnectionVM.CONNECT_FAIL);
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
                Log.d(TAG, "doInBackGround");

                try{
                    url = new URL(getServerUrl());
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
                    setStatus(ConnectionVM.CONNECT_FAIL);
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
