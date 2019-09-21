package com.example.easyflow.models;

import com.example.easyflow.interfaces.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public Date getDate(){return mDate;}

    public void setDate(String mDateString) {

        try {
            this.mDate=new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE).parse(mDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        result.put("date", getDateString());
        result.put("category", getCategory());
        result.put("frequency", getFrequency());
        result.put("note", getNote());

        return result;
    }

    public String getDateString() {
        return new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE).format(getDate());
    }

    public void addDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        cal.add(Calendar.DAY_OF_YEAR, +1);
        this.mDate=cal.getTime();
    }
    public void addWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        cal.add(Calendar.DAY_OF_YEAR, +7);
        this.mDate=cal.getTime();
    }
    public void addMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        cal.add(Calendar.MONTH, +1);
        this.mDate=cal.getTime();
    }
    public void addYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        cal.add(Calendar.YEAR, +1);
        this.mDate=cal.getTime();
    }
}
