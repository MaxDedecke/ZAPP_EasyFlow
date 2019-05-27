package com.example.easyflow.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.easyflow.R;
import com.example.easyflow.models.User;
import com.google.gson.Gson;

public class SplashActivity extends AppCompatActivity {

    public static final String ACTIVITY_EXECUTED = "activity_executed";
    public static final String USER_DATABASE = "user_database";
    public static User mCurrenUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean(ACTIVITY_EXECUTED, false)) {
            Intent intent = new Intent(this, MainActivity.class);

            Gson gson = new Gson();
            String json = pref.getString(SplashActivity.USER_DATABASE, "");

            mCurrenUser=gson.fromJson(json,User.class);

            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
