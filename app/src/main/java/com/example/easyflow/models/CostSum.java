package com.example.easyflow.models;

import com.example.easyflow.interfaces.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CostSum {


    private double mValue;
    private Date mDateLastUpdated;

    public CostSum() {
    }


    public double getValue() {
        return mValue;
    }

    public void setValue(double value) {
        this.mValue = value;
    }

    public Date getDateLastUpdated(){return mDateLastUpdated;}

    public void setDateLastUpdated(String mDateString) {
        try {
            this.mDateLastUpdated=new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE).parse(mDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("value", getValue());
        result.put("dateLastUpdated", getDateString());

        return result;
    }

    public String getDateString() {
        return new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE).format(getDateLastUpdated());
    }


}
