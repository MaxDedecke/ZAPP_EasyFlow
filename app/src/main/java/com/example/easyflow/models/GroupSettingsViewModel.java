package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;

import java.util.List;

public class GroupSettingsViewModel extends ViewModel {


    private boolean mIsCurrentUserGroupAdmin;
    private List<GroupSettings> mMembers;

    public GroupSettingsViewModel() {
        super();
        FirebaseHelper helper = FirebaseHelper.getInstance();
        mMembers = helper.getMembersOfGroup();
        mIsCurrentUserGroupAdmin=helper.isCurrentUserGroupAdmin();

        mIsCurrentUserGroupAdmin =helper.isCurrentUserGroupAdmin();
    }


    public List<GroupSettings> getMembers() {
        return mMembers;
    }

    public boolean isCurrentUserGroupAdmin() {
        return mIsCurrentUserGroupAdmin;
    }

}
