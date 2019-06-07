package com.example.easyflow.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.models.GroupSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class MemberAdapter extends ArrayAdapter<GroupSettings> implements View.OnClickListener {
    private List<GroupSettings> mListItemProperties;
    private Context mContext;

    public MemberAdapter(Context context, int resource, List<GroupSettings> listGroupSettings) {
        super(context, resource, listGroupSettings);
        mContext = context;
        mListItemProperties=listGroupSettings;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        GroupSettings groupSettings = mListItemProperties.get(position);
        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.group_list_item_view,null);

        TextView tvEmail = view.findViewById(R.id.list_email);
        TextView tvRole = view.findViewById(R.id.list_role);
        ImageView image = view.findViewById(R.id.list_group_role);
        Button button=view.findViewById(R.id.list_button_delete);
        button.setOnClickListener(this);


        int imageId;
        int roleTextId;

        switch (groupSettings.getUserGroupSettings().getStateGroupMemberShip()) {
            case Admin:
                imageId = R.drawable.ic_admin_role;
                roleTextId = R.string.admin_role_text;
                break;
            case Member:
                imageId = R.drawable.ic_accepted_role;
                roleTextId = R.string.member_role_text;
                break;
            case Pending:
                imageId = R.drawable.ic_pending_role;
                roleTextId = R.string.pending_role_text;
                break;
            case Refused:
            default:
                imageId = R.drawable.ic_declined_role;
                roleTextId = R.string.declined_role_text;
                break;
        }


        tvRole.setText(mContext.getString(roleTextId));
        tvEmail.setText(groupSettings.getUserGroupSettings().getEmail());
        image.setImageResource(imageId);


        return view;
    }

    @Override
    public void onClick(View v) {

        // todo delete from database and list

    }
}
