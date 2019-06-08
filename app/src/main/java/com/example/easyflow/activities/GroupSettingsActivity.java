package com.example.easyflow.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.adapters.MemberAdapter;
import com.example.easyflow.models.GroupSettings;
import com.example.easyflow.models.GroupSettingsViewModel;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;

import java.util.List;
import java.util.Objects;

public class GroupSettingsActivity extends AppCompatActivity {
    private EditText mNewMemberEmail;
    private GroupSettingsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        mViewModel = ViewModelProviders.of(this).get(GroupSettingsViewModel.class);

        mNewMemberEmail = findViewById(R.id.email_address_new_member_edittext);
        ListView listView = findViewById(R.id.listview_members);
        Button btnDeleteOrLeaveGroup = findViewById(R.id.delete_leave_group_button);

        List<GroupSettings> members = mViewModel.getMembers();
        MemberAdapter arrayAdapter = new MemberAdapter(GlobalApplication.getAppContext(), R.layout.group_list_item_view, members, mViewModel.isCurrentUserGroupAdmin());

        listView.setAdapter(arrayAdapter);


        if(!mViewModel.isCurrentUserGroupAdmin()){
            btnDeleteOrLeaveGroup.setText(R.string.leave_group_button);
        }

    }

    public void addMember(View view) {
        String email=mNewMemberEmail.getText().toString();
        if (!GlobalApplication.isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.error_no_valid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if(groupAlreadyContainsMember(email)){
            Toast.makeText(this, getString(R.string.error_member_already_contains_email), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseHelper helper = FirebaseHelper.getInstance();
        helper.addUserToGroup(email);
    }

    private boolean groupAlreadyContainsMember(String email) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return mViewModel.getMembers().stream().anyMatch(o-> Objects.equals(o.getUserGroupSettings().getEmail(), email));
        }

        for(GroupSettings groupSettings:mViewModel.getMembers()){
            if(Objects.equals(groupSettings.getUserGroupSettings().getEmail(), email))
                return true;
        }

        return false;
    }


    public void deleteGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.nein), null);

        if(mViewModel.isCurrentUserGroupAdmin()){
            builder.setMessage(R.string.alert_delete_group_message);
            builder.setTitle(R.string.alert_delete_group_title);
            builder.setPositiveButton(getString(R.string.ja), (dialog, which) -> {
                FirebaseHelper firebaseHelper =FirebaseHelper.getInstance();
                firebaseHelper.removeGroup();
            });
        }
        else{
            builder.setMessage(R.string.alert_leave_group_message);
            builder.setTitle(R.string.alert_leave_group_title);
            builder.setPositiveButton(getString(R.string.ja), (dialog, which) -> {
                FirebaseHelper firebaseHelper =FirebaseHelper.getInstance();
                firebaseHelper.removeUserFromGroup();
            });
        }


        builder.show();
    }
}
