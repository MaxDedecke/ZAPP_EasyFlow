package com.example.easyflow.models;


public class User {

    private String mUserId;
    private String mEmail;
    private String mPassword;

    private String mCash;
    private String mBankAccount;
    private String mGroup;

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


    public String getCash() {
        return mCash;
    }

    public void setCash(String mCash) {
        this.mCash = mCash;
    }

    public String getBankAccount() {
        return mBankAccount;
    }

    public void setBankAccount(String mBankAccount) {
        this.mBankAccount = mBankAccount;
    }

    public String getGroup() {
        return mGroup;
    }

    public void setGroup(String mGroup) {
        this.mGroup = mGroup;
    }
}
