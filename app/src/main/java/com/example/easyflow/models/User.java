package com.example.easyflow.models;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.easyflow.interfaces.Constants;
import com.google.gson.Gson;

public class User {

    private String mUserId;
    private String mEmail;
    private String mPassword;

    private String mCashId;
    private String mBankAccountId;
    private String mGroupId;

    public User() { }


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


    public String getCashId() {
        return mCashId;
    }

    public void setCashId(String mCash) {
        this.mCashId = mCash;
    }

    public String getBankAccountId() {
        return mBankAccountId;
    }

    public void setBankAccountId(String mBankAccount) {
        this.mBankAccountId = mBankAccount;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String mGroup) {
        this.mGroupId = mGroup;
    }
}
