package com.example.easyflow.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.adapters.ConfirmationAdapter;
import com.example.easyflow.adapters.NotificationAdapter;
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.models.NotificationSettings;
import com.example.easyflow.models.NotificationSettingsViewModel;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;

public class NotificationsActivity extends AppCompatActivity implements NotifyEventHandler {

    private RecyclerView mRecyclerView;
    private NotificationAdapter mNotificationAdapter;
    private NotificationSettingsViewModel mViewModel;
    private ConfirmationAdapter mConfirmationAdapter;
    private TextView emptyRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mViewModel = ViewModelProviders.of(this).get(NotificationSettingsViewModel.class);

        mRecyclerView = findViewById(R.id.recyclerview_notifications);
        emptyRecyclerView = findViewById(R.id.empty_view);

        setUpRecyclerview();
    }
    //finished

    @Override
    protected void onStop() {
        super.onStop();

        if(mNotificationAdapter != null) {
        mNotificationAdapter.stopListening(); }

        if(mConfirmationAdapter != null) {
            mConfirmationAdapter.stopListening();
        }
    }
    //finished

    protected void onStart() {
        super.onStart();
        updateAndSetNotificationsAdapter();
    }
    //finished

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_notification_menu, menu);

        return true;
    }
    //in progress

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if(id == R.id.sent) {

            mNotificationAdapter = null;
            mConfirmationAdapter = null;
            mNotificationAdapter = new NotificationAdapter(this, mViewModel.getmFireBaseSentNotificationsRecyclerOptions());
            mNotificationAdapter.startListening();
            mRecyclerView.setAdapter(mNotificationAdapter);
            this.setTitle(getString(R.string.sent_notification));
            return true;
        }

        else if(id == R.id.received) {

            mNotificationAdapter = null;
            mConfirmationAdapter = null;
            mNotificationAdapter = new NotificationAdapter(this, mViewModel.getmFireBaseReceivedNotificationsRecyclerOptions());
            mNotificationAdapter.startListening();
            mRecyclerView.setAdapter(mNotificationAdapter);
            this.setTitle(getString(R.string.received_notifications));
            return true;
        }

        else if(id == R.id.confirmations) {

            mNotificationAdapter = null;
            updateAndSetConfirmationsAdapter();
            this.setTitle(getString(R.string.title_confirmation));
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

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

        mNotificationAdapter = new NotificationAdapter(this ,mViewModel.getmFireBaseSentNotificationsRecyclerOptions());
        mNotificationAdapter.setEmptyView(emptyRecyclerView);

        mNotificationAdapter.startListening();

        mRecyclerView.setAdapter(mNotificationAdapter);

    }
    //finished

    private void updateAndSetConfirmationsAdapter() {

        if(mConfirmationAdapter != null) {
            mConfirmationAdapter.stopListening();
        }

        mConfirmationAdapter = new ConfirmationAdapter(this, mViewModel.getmFireBaseConfirmationsRecyclerOptions());
        mConfirmationAdapter.setEmptyView(emptyRecyclerView);

        mConfirmationAdapter.startListening();

        mRecyclerView.setAdapter(mConfirmationAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if (mConfirmationAdapter != null) {
                    mConfirmationAdapter.onItemRemove(viewHolder, mRecyclerView);
                }
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    public void showCreateNotificationActivity(View view) {
        int viewid = view.getId();

        Intent newIntent = new Intent(NotificationsActivity.this, CreateNotificationActivity.class);

        newIntent.putExtra(getString(R.string.key_create_notification), true);

        NotificationsActivity.this.startActivity(newIntent);

    }
}