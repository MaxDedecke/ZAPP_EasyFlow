package com.example.easyflow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyflow.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

public class NotificationAdapter extends FirebaseRecyclerAdapter<DataSnapshot, NotificationAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;

    public NotificationAdapter(Context context, @NonNull FirebaseRecyclerOptions<DataSnapshot> options) {
        super(Objects.requireNonNull(options));
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }
    //finished

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DataSnapshot model) {

        int imageId1;
        int imageId2;

        int emailId;
        int numberId;

        //Erst MessageSettings implementieren
        //holder.mTvEmail.setText(mContext.getString());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.notifications_recycler_item_view, viewGroup, false);
        return new NotificationAdapter.ViewHolder(itemView);
        }

        private void enableCheckbox(CheckBox checkBox, int position) {

        checkBox.setBackgroundColor(mContext.getResources().getColor(R.color.buttonGreen));
        checkBox.setTag(position);
        checkBox.setOnClickListener(v -> {

            //Declare Action when Checkbox checked here !
            //
            //

        });

        checkBox.setEnabled(true);
        }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvEmail;
        private TextView mTvAmount;
        private ImageView mImageMessage;
        private ImageView mImageEuro;
        private CheckBox mCheckbox;

        ViewHolder(View view) {
            super(view);

            mTvEmail = view.findViewById(R.id.list_email2);
            mTvAmount = view.findViewById(R.id.list_number);
            mImageMessage = view.findViewById(R.id.list_notification);
            mImageEuro = view.findViewById(R.id.list_euro);
            mCheckbox = view.findViewById(R.id.checkBoxMessage);

        }
    }
}