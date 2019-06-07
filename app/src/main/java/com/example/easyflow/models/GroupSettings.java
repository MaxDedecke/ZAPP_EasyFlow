package com.example.easyflow.models;

import java.util.HashMap;
import java.util.Map;

public class GroupSettings {

    private String mKey;
    private UserGroupSettings mUserGroupSettings;

    public GroupSettings(){}

    public GroupSettings(String userId, String email, StateGroupMembership sgm) {
        setKey(userId);
        setUserGroupSettings(new UserGroupSettings(email,sgm));
    }

    public GroupSettings(String key, UserGroupSettings userGroupSettings) {
        setKey(key);
        setUserGroupSettings(userGroupSettings);
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public UserGroupSettings getUserGroupSettings() {
        return mUserGroupSettings;
    }

    public void setUserGroupSettings(UserGroupSettings userGroupSettings) {
        this.mUserGroupSettings = userGroupSettings;
    }

    public HashMap<String, UserGroupSettings> toMap() {
        HashMap<String,UserGroupSettings> map =new HashMap<>();
        map.put(getKey(),getUserGroupSettings());
        return map;
    }
}
