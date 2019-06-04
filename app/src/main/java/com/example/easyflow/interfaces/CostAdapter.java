package com.example.easyflow.interfaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.easyflow.R;
import com.example.easyflow.activities.MainActivity;
import com.example.easyflow.models.Cost;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;


public class CostAdapter extends FirebaseRecyclerAdapter<DataSnapshot, ViewHolder> {
    private Context mContext;
    public NotifyEventHandler mListener;



    public CostAdapter(Context context,@NonNull FirebaseRecyclerOptions options) {
        super(options);
        mContext=context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DataSnapshot dataSnapshot) {
        Cost cost = dataSnapshot.getValue(Cost.class);
        String key = dataSnapshot.getKey();
        holder.setData(cost);

        if(!MainActivity.mSumHashMap.containsKey(key))
            MainActivity.mSumHashMap.put(key,holder.getValue());
        else {
            MainActivity.mSumHashMap.remove(key);
            MainActivity.mSumHashMap.put(key,holder.getValue());
        }
        if(mListener!=null)
            mListener.Notify();
        /*
        holder.root.setOnClickListener(view -> {
            Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
        });
        */
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sample_list_item_view, viewGroup, false);

        return new ViewHolder(view);
    }

    public void deleteItem(int position) {
        DataSnapshot snapshot=getSnapshots().getSnapshot(position);

        Cost cost = snapshot.getValue(Cost.class);

        if(MainActivity.mSumHashMap.containsKey(snapshot.getKey()))
            MainActivity.mSumHashMap.remove(snapshot.getKey());
        if(mListener!=null)
            mListener.Notify();

        snapshot.getRef().removeValue();
    }

    public void setNotifyEventListener(NotifyEventHandler eventListener) {
        mListener=eventListener;
    }

}
