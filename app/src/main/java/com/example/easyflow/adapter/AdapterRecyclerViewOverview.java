package com.example.easyflow.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.easyflow.R;

import java.util.List;
import java.util.Locale;

public class AdapterRecyclerViewOverview extends RecyclerView.Adapter<AdapterRecyclerViewOverview.CustomViewHolder> {
    private LayoutInflater mInflater;
    private List<String> mItems;
    private SparseBooleanArray mIsCheckedLookUp;

    AdapterRecyclerViewOverview(Context context, List<String> items) {
        this.mItems = items;
        mInflater = LayoutInflater.from(context);
        mIsCheckedLookUp = new SparseBooleanArray();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
// Done: add name of the row layout file
        return new CustomViewHolder(
                mInflater.inflate(R.layout.fragment_overview_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
// Done: Set text depending on the current position
        holder.mCheckBox.setText(mItems.get(position));
        holder.mCheckBox.setChecked(mIsCheckedLookUp.get(position));
    }

    @Override
    public int getItemCount() {
// Done: return size of our data set
        return mItems.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox mCheckBox;

        CustomViewHolder(View view) {
            super(view);

            //todo mCheckBox = view.findViewById(R.id.checkBox);
            mCheckBox.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            boolean isChecked = mCheckBox.isChecked();
            int position = getLayoutPosition();
            mIsCheckedLookUp.append(position, isChecked);

            Toast.makeText(v.getContext(), String.format(Locale.GERMAN, "Position: %d is checked %s",
                    position, isChecked),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }


}

