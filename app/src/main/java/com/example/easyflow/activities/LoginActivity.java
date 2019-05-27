package com.example.easyflow.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.FirebaseHelper;
import com.example.easyflow.models.User;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {
    private EditText mUserName;
    private EditText mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mUserName = findViewById(R.id.email_address_edittext);
        mPassword = findViewById(R.id.password_edittext);
    }

    public void login(View view) {

        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
        User user =firebaseHelper.addUser(mUserName.getText().toString(),mPassword.getText().toString());
        Toast.makeText(getBaseContext(), "Sie wurden erfolgreich eingeloggt.", Toast.LENGTH_SHORT).show();

        // Set activity_executed inside login() method.
        // Set userid_database inside login() method.
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean(SplashActivity.ACTIVITY_EXECUTED, true);


        SplashActivity.mCurrenUser=user;

        Gson gson = new Gson();
        String json = gson.toJson(user);
        edt.putString(SplashActivity.USER_DATABASE, json);

        edt.commit();

        // Show Main Activity.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}

