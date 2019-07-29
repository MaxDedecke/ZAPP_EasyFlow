package com.example.easyflow.models;


public class User {

    private String mUserId;
    private String mEmail;
    private String mPassword;

    private String mCashId;
    private String mBankAccountId;
    private String mGroupId;

    private String mNotificationId;
    private String mAmountId;

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

    public String getNotificationId() {return mNotificationId;}

    public void setNotificationId(String mNotification) {this.mNotificationId = mNotification;}

    public String getAmountId() {return mAmountId;}

    public void setAmountId(String mAmountId) {this.mAmountId = mAmountId;}

}
