package com.example.easyflow.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.widget.TextView;

import com.example.easyflow.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat mSwitchPreference;
    private ListPreference listPreference;
    private TextView mTitle;
    private String mSummary;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //setPreferencesFromResource(R.xml.preferences, rootKey);
        addPreferencesFromResource(R.xml.preferences);
        setTitleAndSummary();
    }

    public void setTitleAndSummary() {

        mSwitchPreference = (SwitchPreferenceCompat) findPreference("switch_preference_1");
        mSwitchPreference.setIconSpaceReserved(false);

        mSwitchPreference = (SwitchPreferenceCompat) findPreference("switch_preference_2");
        mSwitchPreference.setIconSpaceReserved(false);

        mSwitchPreference = (SwitchPreferenceCompat) findPreference("switch_preference_3");
        mSwitchPreference.setIconSpaceReserved(false);

        listPreference = (ListPreference) findPreference("list_preference_1");
        listPreference.setIconSpaceReserved(false);
    }

}
