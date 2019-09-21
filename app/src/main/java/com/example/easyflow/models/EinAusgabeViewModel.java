package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.Date;

public class EinAusgabeViewModel extends ViewModel {



    private final Calendar myCalendar = Calendar.getInstance();
    private Date mDateOfCosts;
    private boolean mShowEingabeCategories;
    private double mFaktor;


    public Calendar getMyCalendar() {
        return myCalendar;
    }

    public Date getDateOfCosts() {
        return mDateOfCosts;
    }

    public void setDateOfCosts(Date mDateOfCosts) {
        this.mDateOfCosts = mDateOfCosts;
    }

    public boolean isShowEingabeCategories() {
        return mShowEingabeCategories;
    }

    public void setShowEingabeCategories(boolean mShowEingabeCategories) {
        this.mShowEingabeCategories = mShowEingabeCategories;
    }

    public double getFaktor() {
        return mFaktor;
    }

    public void setFaktor(double mFaktor) {
        this.mFaktor = mFaktor;
    }
}
