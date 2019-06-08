package com.example.easyflow.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.models.AccountData;

import java.util.ArrayList;

public class SpinnerAccountAdapter extends ArrayAdapter<AccountData> {
    int mGroupId;
    ArrayList<AccountData> mList;
    LayoutInflater mInflater;

    public SpinnerAccountAdapter(Activity context, int groupid, int id, ArrayList<AccountData> list){
        super(context,id,list);
        this.mList =list;
        mInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mGroupId =groupid;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent ){
        View itemView= mInflater.inflate(mGroupId,parent,false);

        TextView textView= itemView.findViewById(R.id.textSpinner);
        ImageView imageView= itemView.findViewById(R.id.ivIconSpinner);

        textView.setText(mList.get(position).getStateAccount());
        imageView.setImageResource(mList.get(position).getImageId());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, @NonNull ViewGroup
            parent){
        return getView(position,convertView,parent);

    }
}
