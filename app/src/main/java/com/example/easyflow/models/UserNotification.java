package com.example.easyflow.models;

import java.util.HashMap;
import java.util.Map;

public class UserNotification {

    private double mValue;
    //private Category mCategory;
    private String mEmailSending;
    private String mEmailReceiving;
    private String mNote;
    private boolean seen;



    public UserNotification() {

    }

    public UserNotification(String note, String emailReceiving, String emailSending, boolean seen, double value) {
        mValue = value;
        mNote = note;
        mEmailSending = emailSending;
        mEmailReceiving = emailReceiving;
        this.seen = seen;
    }

    public double getValue() { return this.mValue;}
    public void setValue(double a) {mValue = a;}

    //public Category getCategory() { return this.mCategory;}
    //public void setCategory(Category category) {this.mCategory = category;}

    public String getEmailSending() {return mEmailSending;}
    public void setmEmailSending(String emailSending) {this.mEmailSending = emailSending;}

    public String getEmailReceiving() {return this.mEmailReceiving;}
    public void setmEmailReceiving(String emailReceiving) {this.mEmailReceiving = emailReceiving;}

    public boolean getSeen() {return this.seen;}
    public void setSeen(boolean status) {this.seen = status;}

    public String getNote() {return this.mNote;}
    public void setNote(String note) { this.mNote = note;}

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("value", getValue());
        result.put("note", getNote());
        result.put("emailSending", getEmailSending());
        result.put("emailReceiving", getEmailReceiving());
        result.put("checked", getSeen());

        return result;

    }
}
