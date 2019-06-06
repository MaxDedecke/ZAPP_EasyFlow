package com.example.easyflow.models;

import java.util.HashMap;
import java.util.Map;

public class GroupSettings {
    private Map<String,StateGroupMembership> mMembers;

    public GroupSettings(){
        mMembers=new HashMap<>();
    }

    public Map<String, StateGroupMembership> getMembers() {
        return mMembers;
    }

    public void setMembers(Map<String, StateGroupMembership> mMembers) {
        this.mMembers = mMembers;
    }

    public void addUser(String userkey,StateGroupMembership stateGroupMembership){
        mMembers.put(userkey,stateGroupMembership);
    }
}
