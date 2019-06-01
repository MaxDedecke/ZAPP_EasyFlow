package com.example.easyflow.interfaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.models.Cost;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;

public class CostAdapter extends FirebaseRecyclerAdapter<Cost, ViewHolder> {
    private Context mContext;


    public CostAdapter(Context context,@NonNull FirebaseRecyclerOptions options) {
        super(options);
        mContext=context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Cost cost) {
        holder.setTxtTitle(cost.getCategory().getName());
        holder.setImageViews(cost.getCategory());
        holder.setTxtDesc(new SimpleDateFormat(Constants.DATE_FORMAT_WEEKDAY).format(cost.getDate()) + " - " + cost.getNote());
        holder.setTxtValue(cost.getValue());

        holder.root.setOnClickListener(view -> {
            //todo
            Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.sample_list_item_view, viewGroup, false);

        return new ViewHolder(view);
    }

    public void deleteItem(int position) {

        //Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }
}
