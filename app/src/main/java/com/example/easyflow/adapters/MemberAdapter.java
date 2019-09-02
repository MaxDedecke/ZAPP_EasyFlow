package com.example.easyflow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.models.GroupSettings;
import com.example.easyflow.models.StateGroupMembership;
import com.example.easyflow.models.UserGroupSettings;
import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;


public class MemberAdapter extends FirebaseRecyclerAdapter<DataSnapshot, MemberAdapter.ViewHolder> {
    private Context mContext;
    private boolean mIsAdmin;
    private LayoutInflater mInflater;

    public MemberAdapter(Context context, @NonNull FirebaseRecyclerOptions<DataSnapshot> options, boolean isAdmin) {
        super(Objects.requireNonNull(options));
        mInflater = LayoutInflater.from(context);
        mContext = context;

        mIsAdmin = isAdmin;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DataSnapshot model) {
        GroupSettings groupSettings = getGroupSettingsFromDataSnapshot(model);

        StateGroupMembership stateGroupMembership = groupSettings.getUserGroupSettings().getStateGroupMemberShip();

        int imageId;
        int roleTextId;

        switch (stateGroupMembership) {
            case Admin:
                imageId = R.drawable.ic_admin_role;
                roleTextId = R.string.admin_role_text;
                break;
            case Member:
                enableDeleteButton(holder.mBtnDelete, position);
                imageId = R.drawable.ic_accepted_role;
                roleTextId = R.string.member_role_text;
                break;
            case Pending:
                enableDeleteButton(holder.mBtnDelete, position);
                imageId = R.drawable.ic_pending_role;
                roleTextId = R.string.pending_role_text;
                break;
            case Refused:
            default:
                enableDeleteButton(holder.mBtnDelete, position);
                imageId = R.drawable.ic_declined_role;
                roleTextId = R.string.declined_role_text;
                break;
        }

        holder.mTvRole.setText(mContext.getString(roleTextId));
        holder.mTvEmail.setText(groupSettings.getUserGroupSettings().getEmail());
        holder.mImageRole.setImageResource(imageId);
    }

    private GroupSettings getGroupSettingsFromDataSnapshot(@NonNull DataSnapshot model) {
        return new GroupSettings(model.getKey(),model.getValue(UserGroupSettings.class));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.group_recycler_item_view, viewGroup, false);
        return new MemberAdapter.ViewHolder(itemView);
    }

    public boolean contains(String email){
        for(DataSnapshot snapshot:getSnapshots ()){
            UserGroupSettings userGroupSettings=snapshot.getValue(UserGroupSettings.class);
            if(email.equals(userGroupSettings.getEmail()))
                return true;
        }

        return false;
    }

    private void enableDeleteButton(Button deleteButton, int position) {
        if (!mIsAdmin)
            return;

        deleteButton.setBackgroundColor(mContext.getResources().getColor(R.color.buttonRed));
        deleteButton.setTag(position);
        deleteButton.setOnClickListener(v -> {
            GroupSettings snapshot = getGroupSettingsFromDataSnapshot(getSnapshots().getSnapshot(position));

            FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
            firebaseHelper.removeUserFromGroup(snapshot);
        });
        deleteButton.setEnabled(true);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvEmail;
        private TextView mTvRole;
        private ImageView mImageRole;
        private Button mBtnDelete;


        ViewHolder(View view) {
            super(view);


            mTvEmail = view.findViewById(R.id.list_email);
            mTvRole = view.findViewById(R.id.list_role);
            mImageRole = view.findViewById(R.id.list_group_role);
            mBtnDelete = view.findViewById(R.id.list_button_delete);
        }
    }
}


/*
    //called when rendering the list
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.group_recycler_item_view, null);

        TextView tvEmail = view.findViewById(R.id.list_email);
        TextView tvRole = view.findViewById(R.id.list_role);
        ImageView imageRole = view.findViewById(R.id.list_group_role);
        Button btnDelete = view.findViewById(R.id.list_button_delete);


        GroupSettings groupSettings = mListItemProperties.get(position);
        StateGroupMembership stateGroupMembership = groupSettings.getUserGroupSettings().getStateGroupMemberShip();

        int imageId;
        int roleTextId;

        switch (stateGroupMembership) {
            case Admin:
                imageId = R.drawable.ic_admin_role;
                roleTextId = R.string.admin_role_text;
                break;
            case Member:
                enableDeleteButton(btnDelete,position);
                imageId = R.drawable.ic_accepted_role;
                roleTextId = R.string.member_role_text;
                break;
            case Pending:
                enableDeleteButton(btnDelete,position);
                imageId = R.drawable.ic_pending_role;
                roleTextId = R.string.pending_role_text;
                break;
            case Refused:
            default:
                enableDeleteButton(btnDelete,position);
                imageId = R.drawable.ic_declined_role;
                roleTextId = R.string.declined_role_text;
                break;
        }

        tvRole.setText(mContext.getString(roleTextId));
        tvEmail.setText(groupSettings.getUserGroupSettings().getEmailSending());
        imageRole.setImageResource(imageId);

        return view;
    }
*/
