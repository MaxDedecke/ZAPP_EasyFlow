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
import com.example.easyflow.models.NotificationSettings;
import com.example.easyflow.models.UserNotificationSettings;
import com.example.easyflow.utils.FirebaseHelper;
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

        NotificationSettings notificationSettings = getNotificationSettingsFromDataSnapshot(model);
        int messageImageId;
        int euroImageId;

        messageImageId = R.drawable.ic_email_black;
        euroImageId = R.drawable.ic_euro_symbol_black_24dp;

        holder.mTvEmail.setText(notificationSettings.getUserNotificationSettings().getEmail());
        holder.mTvAmount.setText(notificationSettings.getUserNotificationSettings().getAmount());
        holder.mImageEuro.setImageResource(euroImageId);
        holder.mImageMessage.setImageResource(messageImageId);

    }
    //finished

    private NotificationSettings getNotificationSettingsFromDataSnapshot(@NonNull DataSnapshot model) {
        return new NotificationSettings(model.getKey(), model.getValue(UserNotificationSettings.class));
    }
    //finished

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.notifications_recycler_item_view, viewGroup, false);
        return new NotificationAdapter.ViewHolder(itemView);
        }
    //finished

    private void enableCheckbox(CheckBox checkBox, int position) {

        checkBox.setBackgroundColor(mContext.getResources().getColor(R.color.buttonGreen));
        checkBox.setTag(position);
        checkBox.setOnClickListener(v -> {

           NotificationSettings snapshot = getNotificationSettingsFromDataSnapshot(getSnapshots().getSnapshot(position));

            FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
            firebaseHelper.sendNotificationBackToUser(snapshot);

        });

        checkBox.setEnabled(true);
    }
    //finished

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
    //finished
}