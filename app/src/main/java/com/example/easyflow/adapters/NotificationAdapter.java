package com.example.easyflow.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.activities.NotificationsActivity;
import com.example.easyflow.models.NotificationSettings;
import com.example.easyflow.models.User;
import com.example.easyflow.models.UserNotification;
import com.example.easyflow.models.UserNotificationConverter;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;

public class NotificationAdapter extends FirebaseRecyclerAdapter<DataSnapshot, NotificationAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private TextView mEmptyView;
    private FirebaseRecyclerOptions<DataSnapshot> mDataSnapshots;
    private ArrayList<UserNotificationConverter> data;
    private ViewHolder mViewHolder;


    public NotificationAdapter(Context context, @NonNull FirebaseRecyclerOptions<DataSnapshot> options) {

        super(options);
        mDataSnapshots = options;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }
    //finished

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DataSnapshot model) {

        int messageImageId;
        int euroImageId;

        if(mViewHolder == null) {
            mViewHolder = holder;
        }

        double helpValue = data.get(position).getValue();
        String Email = data.get(position).getEmailSending();
        String Note = data.get(position).getNote();
        String DisplayFrom = "Von:";
        enableButtonConfirm(holder.mConfirmButton, position);

        messageImageId = R.drawable.ic_email_black;
        euroImageId = R.drawable.ic_euro_symbol_black_24dp;

        holder.mTvEmail.setText(Email);
        holder.mTvAmount.setText(Double.toString(helpValue));
        holder.mTvNote.setText(Note);
        holder.mTvDisplayFrom.setText(DisplayFrom);
        holder.mImageEuro.setImageResource(euroImageId);
        holder.mImageMessage.setImageResource(messageImageId);

    }
    //finished

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        initEmptyView();
    }

    private NotificationSettings getNotificationSettingsFromDataSnapshot(@NonNull DataSnapshot model) {
        return new NotificationSettings(model.getKey(), model.getValue(UserNotificationConverter.class));
    }
    //finished

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(data == null) {
            prepareVector();
        }

        View itemView = mInflater.inflate(R.layout.notifications_recycler_item_view, viewGroup, false);
        return new ViewHolder(itemView);
        }
    //finished

    private void enableButtonConfirm(Button button, int position) {

        button.setTag(position);
        button.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(R.string.alert_dialog_confirm_notification);
            builder.setTitle(R.string.alert_diaolg_confirm_notification_title);

            builder.setNegativeButton(R.string.nein, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                }
            });

            builder.setPositiveButton(R.string.ja, (dialog, which) -> {

                NotificationSettings snapshot = getNotificationSettingsFromDataSnapshot(getSnapshots().getSnapshot(position));
                FirebaseHelper helper = FirebaseHelper.getInstance();
                helper.sendConfirmationBackToUser(snapshot);

                DataSnapshot mSnapShot = getSnapshots().getSnapshot(position);
                helper.deleteNotification(mSnapShot);

            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        button.setEnabled(true);

    }
    //finished

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvEmail;
        private TextView mTvAmount;
        private TextView mTvNote;
        private TextView mTvDisplayFrom;
        private ImageView mImageMessage;
        private ImageView mImageEuro;
        private Button mConfirmButton;

        ViewHolder(View view) {

            super(view);

            mTvEmail = view.findViewById(R.id.list_email2);
            mTvAmount = view.findViewById(R.id.list_number);
            mTvNote = view.findViewById(R.id.list_note);
            mTvDisplayFrom = view.findViewById(R.id.display_from);
            mImageMessage = view.findViewById(R.id.list_notification);
            mImageEuro = view.findViewById(R.id.list_euro);
            mConfirmButton = view.findViewById(R.id.button_msg_confirm);

        }
    }
    //finished

    public void setEmptyView(TextView view) {
        this.mEmptyView = view;
        initEmptyView();
    }

    private void initEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void prepareVector() {

        Vector<UserNotificationConverter> helpVector = new Vector<>();
        Iterable<DataSnapshot> iterable = mDataSnapshots.getSnapshots();

        for(DataSnapshot snapshot : iterable) {
            UserNotificationConverter converter = snapshot.getValue(UserNotificationConverter.class);
            helpVector.addElement(converter);
        }
        data = new ArrayList<>(helpVector);
    }

}