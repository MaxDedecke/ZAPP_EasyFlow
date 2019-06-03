package com.example.easyflow.models;

public class AccountData {
    StateAccount stateAccount;
    String stateAccountString;
    Integer imageId;

    public AccountData(StateAccount stateAccount,String stateAccountString, Integer imageId) {
        this.stateAccount=stateAccount;
        this.stateAccountString = stateAccountString;
        this.imageId = imageId;
    }

    public String getStateAccount() {
        return stateAccountString;
    }

    public Integer getImageId() {
        return imageId;
    }

    public StateAccount getStateAccountObject(){
        return stateAccount;
    }
}
