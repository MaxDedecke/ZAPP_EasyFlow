package com.example.easyflow.models;

public class UserNotificationSettings {

    private String Creator;

    public UserNotificationSettings() {}

    public UserNotificationSettings(String emailAdmin) {

        setEmailHost(emailAdmin);
    }

    public String getEmailHost() {return this.Creator;}

    public void setEmailHost(String email) {
        this.Creator = email;
    }

}
