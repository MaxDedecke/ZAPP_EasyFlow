package com.example.easyflow.models;

public class UserGroupSettings {

    private String mEmail;
    private StateGroupMembership mStateGroupMemberShip;

    public UserGroupSettings(){

    }

    public UserGroupSettings(String email, StateGroupMembership stateGroupMembership) {
        setEmail(email);
        setStateGroupMemberShip(stateGroupMembership);
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public StateGroupMembership getStateGroupMemberShip() {
        return mStateGroupMemberShip;
    }

    public void setStateGroupMemberShip(StateGroupMembership stateGroupMemberShip) {
        this.mStateGroupMemberShip = stateGroupMemberShip;
    }
}
