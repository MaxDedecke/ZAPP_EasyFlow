package com.example.easyflow.models;

public class Category {
    private static int mIdCounter=1;

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getIcon() {
        return mIconId;
    }

    private int mId;
    private String mName;
    private int mIconId;

    public Category(String name, int iconId){
        mId=mIdCounter++;
        mName=name;
        mIconId=iconId;
    }
}
