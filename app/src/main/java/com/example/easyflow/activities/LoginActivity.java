package com.example.easyflow.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.FirebaseHelper;
import com.example.easyflow.models.User;

public class LoginActivity extends AppCompatActivity {
    private EditText mUserName;
    private EditText mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserName=findViewById(R.id.email_address_edittext);
        mPassword=findViewById(R.id.password_edittext);

        setContentView(R.layout.activity_login);
    }

    public void insert() {

        User user = new User (
                mUserName.getText().toString(),
                mPassword.getText().toString());

        FirebaseHelper firebaseHelper= FirebaseHelper.getInstance();
        firebaseHelper.addUser(user);
        Toast.makeText(getBaseContext(), "Hallo", Toast.LENGTH_SHORT).show();

//set activity_executed inside insert() method.
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("activity_executed", true);
        edt.commit();

    }
}
