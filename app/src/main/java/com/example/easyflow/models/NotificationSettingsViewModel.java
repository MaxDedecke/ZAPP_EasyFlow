package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class NotificationSettingsViewModel extends ViewModel {

    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseRecyclerOptions;
    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseSentNotificationsRecyclerOptions;
    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseReceivedNotificationsRecyclerOptions;
    private  FirebaseRecyclerOptions<DataSnapshot> mFireBaseConfirmationsRecyclerOptions;

    public static int displaymode = 0;      //1 = sent; 2 = received; 3 = confirmations

    public NotificationSettingsViewModel() {
        super();
        FirebaseHelper helper = FirebaseHelper.getInstance();
        mFireBaseRecyclerOptions = helper.getFirebaseRecyclerOptionNotifications();
        mFireBaseSentNotificationsRecyclerOptions = helper.getFirebaseSentNotifications();
        mFireBaseReceivedNotificationsRecyclerOptions = helper.getFirebaseReceivedNotifications();
        mFireBaseConfirmationsRecyclerOptions = helper.getFirebaseConfirmations();
        setDisplaymode(0);
    }

    public FirebaseRecyclerOptions<DataSnapshot> getmFireBaseRecyclerOptions() {
        return mFireBaseRecyclerOptions;
    }

    public FirebaseRecyclerOptions<DataSnapshot> getmFireBaseSentNotificationsRecyclerOptions() {
        return mFireBaseSentNotificationsRecyclerOptions;
    }

    public FirebaseRecyclerOptions<DataSnapshot> getmFireBaseReceivedNotificationsRecyclerOptions() {
        return mFireBaseReceivedNotificationsRecyclerOptions;
    }

    public FirebaseRecyclerOptions<DataSnapshot> getmFireBaseConfirmationsRecyclerOptions() {
        return mFireBaseConfirmationsRecyclerOptions;
    }

    public void setDisplaymode(int mode) { displaymode = mode;}

    public int getDisplaymode() { return displaymode;}
}
