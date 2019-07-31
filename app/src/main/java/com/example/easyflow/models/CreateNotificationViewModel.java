package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

public class CreateNotificationViewModel extends ViewModel {

    private boolean showNotificationCategories;
    double Faktor;

    public void setShowNotificationCategories(boolean showNotificationCategories) {
        this.showNotificationCategories = showNotificationCategories;
    }
    //finished

    public boolean isShowNotificationCategories() {
        return showNotificationCategories;
    }
    //finished

    public double getFaktor() {
        return this.Faktor;
    }
    //finished

    public void setFaktor(double faktor) {
        this.Faktor = faktor;
    }
    //finished

}
