package com.example.easyflow.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.NotifyEventHandler;

public class NotificationsActivity extends AppCompatActivity implements NotifyEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
    }

    @Override
    public void Notify() {

    }
}
