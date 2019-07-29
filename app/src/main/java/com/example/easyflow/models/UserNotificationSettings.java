package com.example.easyflow.models;

public class UserNotificationSettings {

    private String mEmail;
    private int mAmount;
    private String mNote;

    public UserNotificationSettings() {}

    public UserNotificationSettings(String email, int a, String note) {

        setEmail(email);
        setAmount(a);
        setNote(note);

    }


    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getEmail() {
        return this.mEmail;
    }

    public void setAmount(int a) {
        this.mAmount = a;
    }

    public int getAmount() {
        return this.mAmount;
    }

    public void setNote(String note) {
        this.mNote = note;
    }

    public String getNote() {
        return this.mNote;
    }

}
