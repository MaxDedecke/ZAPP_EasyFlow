package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class GroupSettingsViewModel extends ViewModel {


    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseRecyclerOptions;
    private boolean mIsCurrentUserGroupAdmin;

    public GroupSettingsViewModel() {
        super();
        FirebaseHelper helper = FirebaseHelper.getInstance();
        mIsCurrentUserGroupAdmin=helper.isCurrentUserGroupAdmin();
        mFireBaseRecyclerOptions=helper.getFirebaseRecyclerOptionsMembersGroup();
    }

    public boolean isCurrentUserGroupAdmin() {
        return mIsCurrentUserGroupAdmin;
    }


    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptions() {
        return mFireBaseRecyclerOptions;
    }
}
