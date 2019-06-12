package com.example.easyflow.models;

import com.example.easyflow.interfaces.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CostSum {


    private double mCurrentValue= (double) 0;
    private double mFutureValue=(double) 0;
    private Date mDateLastUpdated;

    public CostSum() {
    }


    public double getCurrentValue() {
        return mCurrentValue;
    }

    public void setCurrentValue(double value) {
        this.mCurrentValue = value;
    }

    public double getFutureValue() {
        return mFutureValue;
    }

    public void setFutureValue(double value) {
        this.mFutureValue = value;
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
        result.put("currentValue", getCurrentValue());
        result.put("futureValue", getFutureValue());
        result.put("dateLastUpdated", getStringDateLastUpdated());

        return result;
    }

    public String getStringDateLastUpdated() {
        return new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE).format(getDateLastUpdated());
    }


}
