package com.example.easyflow.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.FirebaseHelper;
import com.example.easyflow.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class SplashActivity extends AppCompatActivity {

    public static final String ACTIVITY_EXECUTED = "activity_executed";
    public static final String USER_DATABASE = "user_database";
    public static User mCurrenUser;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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

            FirebaseHelper helper = FirebaseHelper.getInstance();
            helper.checkUserDataChanged();

            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
