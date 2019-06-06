package com.example.easyflow.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.FirebaseHelper;

public class GroupSettingsActivity extends AppCompatActivity {
    private EditText mNewMemberEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // todo oberfläche und funktionalität hinzufügen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        mNewMemberEmail=findViewById(R.id.email_address_new_member_edittext);
    }

    public void addMember(View view) {
        if(!isValidEmail(mNewMemberEmail.getText()))
            return; // todo toast or whatever

        FirebaseHelper helper=FirebaseHelper.getInstance();
        helper.addUserToGroup(mNewMemberEmail.getText().toString());
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
