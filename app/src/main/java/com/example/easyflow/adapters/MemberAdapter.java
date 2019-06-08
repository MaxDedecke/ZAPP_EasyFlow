package com.example.easyflow.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.models.GroupSettings;
import com.example.easyflow.models.StateGroupMembership;
import com.example.easyflow.utils.FirebaseHelper;

import java.util.List;


public class MemberAdapter extends ArrayAdapter<GroupSettings> implements View.OnClickListener {
    private List<GroupSettings> mListItemProperties;
    private Context mContext;
    private boolean mIsAdmin;

    public MemberAdapter(Context context, int resource, List<GroupSettings> listGroupSettings, boolean isAdmin) {
        super(context, resource, listGroupSettings);
        mContext = context;
        mListItemProperties = listGroupSettings;

        mIsAdmin = isAdmin;
    }

    //called when rendering the list
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.group_list_item_view, null);

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
        tvEmail.setText(groupSettings.getUserGroupSettings().getEmail());
        imageRole.setImageResource(imageId);

        return view;
    }

    private void enableDeleteButton(Button deleteButton,int position) {
        if(!mIsAdmin)
            return;

        deleteButton.setBackgroundColor(mContext.getResources().getColor(R.color.buttonRed));
        deleteButton.setTag(position);
        deleteButton.setOnClickListener(this);
        deleteButton.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        int position= (int) v.getTag();

        FirebaseHelper firebaseHelper=FirebaseHelper.getInstance();
        firebaseHelper.removeUserFromGroup(mListItemProperties.get(position));

        mListItemProperties.remove(position);
        notifyDataSetChanged();

        Toast.makeText(mContext,mContext.getResources().getString(R.string.user_removed_from_group), Toast.LENGTH_SHORT);
    }
}
