package com.example.easyflow.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.adapters.MemberAdapter;
import com.example.easyflow.models.GroupSettingsViewModel;
import com.example.easyflow.models.MainViewModel;
import com.example.easyflow.models.StateAccount;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;

public class GroupSettingsActivity extends AppCompatActivity {
    private EditText mNewMemberEmail;
    private GroupSettingsViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private MemberAdapter mMemberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        mViewModel = ViewModelProviders.of(this).get(GroupSettingsViewModel.class);

        mNewMemberEmail = findViewById(R.id.email_address_new_member_edittext);
        mRecyclerView = findViewById(R.id.recyclerview_members);
        Button btnDeleteOrLeaveGroup = findViewById(R.id.delete_leave_group_button);

        if(!mViewModel.isCurrentUserGroupAdmin()){
            btnDeleteOrLeaveGroup.setText(R.string.leave_group_button);
        }

        setUpRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMemberAdapter.stopListening();
    }

    public void addMember(View view) {
        String email=mNewMemberEmail.getText().toString();
        mNewMemberEmail.setText(null);
        if (!GlobalApplication.isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.error_no_valid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if(mMemberAdapter.contains(email)){
            Toast.makeText(this, getString(R.string.error_member_already_contains_email), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseHelper helper = FirebaseHelper.getInstance();
        helper.addUserToGroup(email);
    }

    /*
    private boolean groupAlreadyContainsMember(String email) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return mViewModel.getMembers().stream().anyMatch(o-> Objects.equals(o.getUserGroupSettings().getEmail(), email));
        }

        for(GroupSettings groupSettings:mViewModel.getMembers()){
            if(Objects.equals(groupSettings.getUserGroupSettings().getEmail(), email))
                return true;
        }

        return false;

        for(mRecyclerView.getchild)
    }
    */

    @Override
    protected void onStart() {
        super.onStart();
        updateAndSetCostAdapter();
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

                Toast.makeText(this,getString(R.string.message_group_removed_successfully),Toast.LENGTH_SHORT).show();

                MainViewModel.mStateAccount= StateAccount.Cash;
                finish();
            });
        }
        else{
            builder.setMessage(R.string.alert_leave_group_message);
            builder.setTitle(R.string.alert_leave_group_title);
            builder.setPositiveButton(getString(R.string.ja), (dialog, which) -> {
                FirebaseHelper firebaseHelper =FirebaseHelper.getInstance();
                firebaseHelper.removeUserFromGroup();

                MainViewModel.mStateAccount= StateAccount.Cash;
                finish();
            });


            //finishActivity(MainActivity.DELETE_GROUP_REQUEST);
        }


        builder.show();
    }

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    private void updateAndSetCostAdapter() {

        if (mMemberAdapter != null)
            mMemberAdapter.stopListening();

        mMemberAdapter = new MemberAdapter(GlobalApplication.getAppContext(),mViewModel.getFirebaseRecyclerOptions(),mViewModel.isCurrentUserGroupAdmin());
        mRecyclerView.setAdapter(mMemberAdapter);

        mMemberAdapter.startListening();
    }
}
