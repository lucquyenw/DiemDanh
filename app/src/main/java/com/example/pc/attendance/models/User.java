package com.example.pc.attendance.models;

import java.io.Serializable;

/**
 * Created by PC on 14/02/2018.
 */

public class User implements Serializable{

    private String id;
    private String username;
    private String password;
    private String fullname;
    private String email;

    private static User instance;
    public static User getInstance()
    {
        if(instance == null)
            instance = new User();
        return instance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
