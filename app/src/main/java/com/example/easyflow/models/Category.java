package com.example.easyflow.models;

public class Category {
    private static int mIdCounter=1;



    private int mId;
    private String mName;
    private int mIconId;

    public Category(String name, int iconId){
        setId(mIdCounter++);
        setName(name);
        setIconId(iconId);
    }

    public Category(){}

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int mIconId) {
        this.mIconId = mIconId;
    }
}
