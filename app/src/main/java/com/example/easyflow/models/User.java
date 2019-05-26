package com.example.easyflow.models;

import android.provider.ContactsContract;

public class User {

    private String mEmail;
    private String mPassword;

    public User(String email, String password){
        this.setmEmail(email);
        this.setmPassword(password);
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
