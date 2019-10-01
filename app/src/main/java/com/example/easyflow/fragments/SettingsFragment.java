package com.example.easyflow.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.example.easyflow.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreferenceCompat mSwitchPreference;
    private SwitchPreference switchPreference;
    private ListPreference listPreference;
    private PreferenceScreen mPreferenceScreen;
    private String mTitle;
    private String mSummary;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setTitleAndSummary();
    }

    public void setTitleAndSummary() {


        Context context = getPreferenceManager().getContext();
        mPreferenceScreen = getPreferenceManager().createPreferenceScreen(context);

        for(String key : getPreferenceScreen().getSharedPreferences().getAll().keySet()) {
            String keyListPreference = "list_preference_1";

            if(!key.equals(keyListPreference)) {

                keyListPreference = "keep_logged_in";

                if(keyListPreference.equals(key)) {break;}

                switchPreference = (SwitchPreference) findPreference(key);

                switchPreference.setIconSpaceReserved(false);
                //switchPreference.setWidgetLayoutResource(R.layout.preference_layout);
                mPreferenceScreen.addPreference(switchPreference);
            }
            else {
                listPreference = (ListPreference) findPreference(key);
                listPreference.setIconSpaceReserved(false);
                mPreferenceScreen.addPreference(listPreference);
            }


        }
        setPreferenceScreen(mPreferenceScreen);
    }

}
