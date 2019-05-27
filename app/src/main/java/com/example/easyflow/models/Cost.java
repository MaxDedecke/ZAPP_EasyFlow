package com.example.easyflow.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Cost {

    private double mValue;
    private Date mDate;
    private Category mCategory;
    private Frequency mFrequency;
    private String mNote;

    public Cost() {
    }

    public Cost(double value, Date date, Category category, Frequency frequency, String note) {
        mValue = value;
        mDate = date;
        mCategory = category;
        mFrequency = frequency;
        mNote = note;
    }

    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        this.mValue = value;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
    }

    public Frequency getFrequency() {
        return mFrequency;
    }

    public void setFrequency(Frequency frequency) {
        this.mFrequency = frequency;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        this.mNote = note;
    }


    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("value", getValue());
        result.put("date", getDate());
        result.put("category", getCategory());
        result.put("frequency", getFrequency());
        result.put("note", getNote());

        return result;
    }
}
