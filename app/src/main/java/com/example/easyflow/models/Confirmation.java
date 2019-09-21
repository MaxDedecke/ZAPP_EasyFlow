package com.example.easyflow.models;

public class Confirmation {

    private String mEmailReceiver;
    private double mValue;

    private Confirmation() {}

    public Confirmation(String emailReceiving, double value) {
        this.mEmailReceiver = emailReceiving;
        this.mValue = value;
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double mValue) {
        this.mValue = mValue;
    }

    public String getEmailReceiving() {
        return mEmailReceiver;
    }

    public void setEmailReceiving(String mEmailReceiving) {
        this.mEmailReceiver = mEmailReceiving;
    }
}
