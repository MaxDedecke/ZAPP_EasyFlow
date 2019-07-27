package com.example.easyflow.models;

public class AccountData {
    private StateAccount stateAccount;
    private String stateAccountString;
    private Integer imageId;

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
