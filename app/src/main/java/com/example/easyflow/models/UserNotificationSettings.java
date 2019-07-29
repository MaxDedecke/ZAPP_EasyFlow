package com.example.easyflow.models;

public class UserNotificationSettings {

    private String mEmail;
    private String mAmount;

    public UserNotificationSettings() {}

    public UserNotificationSettings(String email, String amount) {

        setEmail(email);
        setAmount(amount);

    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public String getEmail() {
        return this.mEmail;
    }

    public void setAmount(String a) {
        this.mAmount = a;
    }

    public String getAmount() {
        return this.mAmount;
    }

}
