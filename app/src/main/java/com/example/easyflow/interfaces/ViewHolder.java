package com.example.easyflow.interfaces;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.easyflow.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    public TextView txtTitle;
    public TextView txtDesc;

    // todo make code and view specific for costs

    public ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        txtTitle = itemView.findViewById(R.id.list_title);
        txtDesc = itemView.findViewById(R.id.list_desc);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setTxtTitle(String string) {
        txtTitle.setText(string);
    }


    public void setTxtDesc(String string) {
        txtDesc.setText(string);
    }
}
