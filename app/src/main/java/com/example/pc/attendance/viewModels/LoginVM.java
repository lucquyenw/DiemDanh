package com.example.pc.attendance.viewModels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.pc.attendance.models.User;
import com.example.pc.attendance.views.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by PC on 24/02/2018.
 */

public class LoginVM extends BaseObservable {

    private static final String TAG = "LoginVM";


    private static final String API_TOKEN = "/token";
    // Key for preferences
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER_ME = "is_remember";

    // Strings login error
    private static final String ERROR_INCORRECT_EMAIL = "This email address is incorrect";
    private static final String ERROR_INCORRECT_PASSWORD = "This password is incorrect";
    private static final String ERROR_FIELD_REQUIRED = "Email or password is invalid";

    private String url;
    private Context context;
    private String port;
    private SharedPreferences currentAPI;
    private SharedPreferences loginStorage;
    private SharedPreferences.Editor editor;

    private User user;
    private boolean isRememberMe;


    public LoginVM(Context context){
        this.context = context;

        loginStorage = PreferenceManager.getDefaultSharedPreferences(context);
        editor = loginStorage.edit();
        url=loginStorage.getString(ConnectionVM.API_KEY,"");
        port=loginStorage.getString(ConnectionVM.PORT_KEY,"");
        this.user = User.getInstance();

        isRememberMe = loginStorage.getBoolean(KEY_REMEMBER_ME, false);
        String teacherId=loginStorage.getString("UID","");
        if(teacherId!=null){
            this.user.setUsername(loginStorage.getString("UID", ""));
            //onLoadUser(this.user.getEmail());
            onLogin();
        }
    }

    public void onLogin() {
        LoginAsyntask asyntask=new LoginAsyntask(user.getUsername(),user.getPassword());
        asyntask.execute();
    }

    public String getServerUrl(){
        if(url.equals("")){
            return "";
        }
        Log.d(TAG, ConnectionVM.PROTOCOL + url + ":" + port+API_TOKEN);
        return ConnectionVM.PROTOCOL + url + ":" + port + API_TOKEN;
    }
    @Bindable
    public User getUser(){
        return this.user;
    }

    public boolean isRememberMe() {
        return isRememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        isRememberMe = rememberMe;
    }

    private class LoginAsyntask extends AsyncTask<String,String,String> {
        private String username;
        private String password;

        public LoginAsyntask(String username,String password){
            this.username=username;
            this.password=password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String token=loginStorage.getString("auth-token","");
            if(token!=null){
                Toast.makeText(context,
                        "Login successfully", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, MainActivity.class));
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(null == s){
                return;
            }

            try {
                Log.d(TAG, s);

                JSONObject result = new JSONObject(s);
                String token=result.getString("access_token");
                editor=loginStorage.edit();
                editor.putString("auth-token",token);
                editor.putString("UID",username);
                editor.commit();

                if(token!=null){
                    Toast.makeText(context,
                            "Login successfully", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, MainActivity.class));
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
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("Accept","application/json");
                    urlConnection.setUseCaches(true);
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);

                    String urlParamters="username="+username+"&password="+password+"&grant_type=password";
                    DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(urlParamters);
                    Log.d(TAG,urlParamters);
                    os.flush();
                    os.close();
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
