package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class NotificationSettingsViewModel extends ViewModel {

    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseRecyclerOptions;

    public NotificationSettingsViewModel() {
        super();
        FirebaseHelper helper = FirebaseHelper.getInstance();
        mFireBaseRecyclerOptions = helper.getFirebaseRecyclerOptionNotifications();
    }

    public FirebaseRecyclerOptions<DataSnapshot> getmFireBaseRecyclerOptions() {
        return mFireBaseRecyclerOptions;
    }
}
