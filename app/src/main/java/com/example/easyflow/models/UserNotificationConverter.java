package com.example.easyflow.models;

public class UserNotificationConverter {

    private String Note;
    private String EmailReceiving;
    private String EmailSending;
    private boolean Checked;
    private double Value;

    private UserNotificationConverter() {}

    public UserNotificationConverter(String note, String emailReceiving, String emailSending, boolean checked, double value) {
        this.Note = note;
        this.EmailReceiving = emailReceiving;
        this.EmailSending = emailSending;
        this.Checked = checked;
        this.Value = value;
    }

    public String getNote() {return Note;}
    public void setNote(String note) {this.Note = note;}
    public String getEmailReceiving() {return EmailReceiving;}
    public void setEmailReceiving(String emailReceiving) {this.EmailReceiving = emailReceiving;}
    public String getEmailSending() {return EmailSending;}
    public void setEmailSending(String emailSending) {this.EmailSending = emailSending;}
    public boolean getChecked() {return Checked;}
    public void setChecked(boolean checked) {this.Checked = checked;}
    public double getValue() {return Value;}
    public void setValue(double value) {this.Value = value;}
}
