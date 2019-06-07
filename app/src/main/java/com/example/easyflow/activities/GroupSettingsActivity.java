package com.example.easyflow.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;
import com.example.easyflow.adapters.MemberAdapter;
import com.example.easyflow.models.GroupSettings;
import com.example.easyflow.models.GroupSettingsViewModel;

import java.util.List;

public class GroupSettingsActivity extends AppCompatActivity {
    private EditText mNewMemberEmail;
    private ListView mListView;
    private GroupSettingsViewModel mViewModel;
    private MemberAdapter mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // todo oberfläche und funktionalität hinzufügen

        mViewModel = ViewModelProviders.of(this).get(GroupSettingsViewModel.class);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);
        mNewMemberEmail = findViewById(R.id.email_address_new_member_edittext);
        mListView = findViewById(R.id.listview_members);


        List<GroupSettings> members = mViewModel.getMembers();
        mArrayAdapter = new MemberAdapter(GlobalApplication.getAppContext(), R.layout.group_list_item_view, members );

        mListView.setAdapter(mArrayAdapter);


    }

    public void addMember(View view) {
        String email=mNewMemberEmail.getText().toString();
        if (!GlobalApplication.isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.error_no_valid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if(memberAlreadyContains(email)){
            Toast.makeText(this, getString(R.string.error_member_already_contains_email), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseHelper helper = FirebaseHelper.getInstance();
        helper.addUserToGroup(email);
    }

    private boolean memberAlreadyContains(String email) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return mViewModel.getMembers().stream().anyMatch(o->o.getUserGroupSettings().getEmail()==email);
        }
        else{
            for(GroupSettings groupSettings:mViewModel.getMembers()){
                if(groupSettings.getUserGroupSettings().getEmail()==email)
                    return true;
            }
            return false;
        }
    }


    public void deleteGroup(View view) {
        // todo nachfragen und anschließend Gruppe löschen
    }
}
