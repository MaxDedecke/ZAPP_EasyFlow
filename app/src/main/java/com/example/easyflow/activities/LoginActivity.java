package com.example.easyflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;
import com.example.easyflow.models.User;

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
        Toast.makeText(this, this.getString(R.string.login_successfull), Toast.LENGTH_SHORT).show();

        // Set activity_executed inside login() method.
        // Set userid_database inside login() method.
        GlobalApplication.saveStartUpInSharedPreferences(user,true);


        // Show Main Activity.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}

