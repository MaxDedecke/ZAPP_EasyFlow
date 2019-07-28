package com.example.easyflow.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

import com.example.easyflow.R;
import com.example.easyflow.adapters.NotificationAdapter;
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.utils.GlobalApplication;

public class NotificationsActivity extends AppCompatActivity implements NotifyEventHandler {
    private RecyclerView mRecyclerView;
    private NotificationAdapter mNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mRecyclerView = findViewById(R.id.recyclerview_notifications);

        setUpRecyclerview();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public void Notify() {

    }


    private void setUpRecyclerview() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    private void updateAndSetNotificationsAdapter() {

        if(mNotificationAdapter != null) {
            mNotificationAdapter.stopListening();
        }

        //mNotificationAdapter = new NotificationAdapter(GlobalApplication.getAppContext(),)
    }
}