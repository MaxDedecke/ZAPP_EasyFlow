package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;

import java.util.List;

public class GroupSettingsViewModel extends ViewModel {

    private List<GroupSettings> mMembers;

    public GroupSettingsViewModel() {
        super();
        FirebaseHelper helper = FirebaseHelper.getInstance();
        mMembers = helper.getMembersOfGroup();
    }


    public List<GroupSettings> getMembers() {
        return mMembers;
    }

}
