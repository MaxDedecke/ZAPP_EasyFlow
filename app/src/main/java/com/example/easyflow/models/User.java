package com.example.easyflow.models;


public class User {


    private String mUserId;
    private String mEmail;
    private String mPassword;




    public User() { }


    public User(String userId, String email, String password) {
        mUserId=userId;
        mEmail=email;
        mPassword=password;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }



}
