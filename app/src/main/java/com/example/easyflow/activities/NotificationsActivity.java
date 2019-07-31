package com.example.easyflow.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.example.easyflow.R;
import com.example.easyflow.adapters.NotificationAdapter;
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.models.NotificationSettings;
import com.example.easyflow.models.NotificationSettingsViewModel;
import com.example.easyflow.utils.GlobalApplication;

public class NotificationsActivity extends AppCompatActivity implements NotifyEventHandler {

    private RecyclerView mRecyclerView;
    private NotificationAdapter mNotificationAdapter;
    private NotificationSettingsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mViewModel = ViewModelProviders.of(this).get(NotificationSettingsViewModel.class);

        mRecyclerView = findViewById(R.id.recyclerview_notifications);
        setUpRecyclerview();
    }
    //finished

    @Override
    protected void onStop() {
        super.onStop();
        mNotificationAdapter.stopListening();
    }
    //finished

    public void addNotification() {

    }
    //in progress

    protected void onStart() {
        super.onStart();
        updateAndSetNotificationsAdapter();
    }
    //finished

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }*/
    //in progress

    @Override
    public void Notify() {

    }
    //in progress

    private void setUpRecyclerview() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }
    //finished

    private void updateAndSetNotificationsAdapter() {

        if(mNotificationAdapter != null) {
            mNotificationAdapter.stopListening();
        }

        mNotificationAdapter = new NotificationAdapter(GlobalApplication.getAppContext(),mViewModel.getmFireBaseRecyclerOptions());
        mRecyclerView.setAdapter(mNotificationAdapter);

        mNotificationAdapter.startListening();
    }
    //finished

    public void showCreateNotificationActivity(View view) {
        int viewid = view.getId();

        Intent newIntent = new Intent(NotificationsActivity.this, CreateNotificationActivity.class);

        newIntent.putExtra(getString(R.string.key_create_notification), true);

        NotificationsActivity.this.startActivity(newIntent);

    }
}