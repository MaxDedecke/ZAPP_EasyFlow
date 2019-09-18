package com.example.easyflow.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.models.Confirmation;
import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Vector;

public class ConfirmationAdapter extends FirebaseRecyclerAdapter<DataSnapshot, ConfirmationAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private TextView emptyView;
    private FirebaseRecyclerOptions<DataSnapshot> mDataSnapshot;
    private ArrayList<Confirmation> data;

    public ConfirmationAdapter(Context Context, @NonNull FirebaseRecyclerOptions<DataSnapshot> options) {

        super(options);
        mDataSnapshot = options;
        inflater = LayoutInflater.from(Context);
        this.context = Context;
    }

    @Override
    protected  void onBindViewHolder(@NonNull ConfirmationAdapter.ViewHolder holder, int position, @NonNull DataSnapshot model) {

        double amount = data.get(position).getValue();
        String emailReceiver = data.get(position).getEmailReceiving();
        String displayVon = "Von:";
        String displayTitle = "Bestätigung:";

        holder.mTvVon.setText(displayVon);
        holder.mTvTitle.setText(displayTitle);
        holder.mAmount.setText(Double.toString(amount));
        holder.mTvEmail.setText(emailReceiver);

    }

    public void onItemRemove(RecyclerView.ViewHolder viewHolder, RecyclerView recyclerView) {
        Resources resources = inflater.getContext().getResources();
        int position = viewHolder.getAdapterPosition();

        DataSnapshot snapshot = getSnapshots().getSnapshot(position);
        Confirmation confirmation = snapshot.getValue(Confirmation.class);

        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.deleteConfirmation(snapshot);

        /*Snackbar snackbar = Snackbar
                .make(recyclerView, "Bestätigung gelöscht", Snackbar.LENGTH_LONG)
                .setAction("UNDO", view -> {
                    firebaseHelper
                })*/
    }
    @Override
    public void onDataChanged() {
        super.onDataChanged();
        initEmptyView();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(data == null) {
            prepareVector();
        }

        View itemView = inflater.inflate(R.layout.confirmation_recycler_item_view, viewGroup, false);
        return new ViewHolder(itemView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvEmail;
        private TextView mTvTitle;
        private TextView mAmount;
        private TextView mTvVon;

        ViewHolder(View view) {
            super(view);
            mTvTitle = view.findViewById(R.id.text_confirmation_title);
            mTvEmail = view.findViewById(R.id.text_confirmation_email);
            mAmount = view.findViewById(R.id.text_amount_confirmation);
            mTvVon = view.findViewById(R.id.text_confirmation_von);
        }
    }

    private void prepareVector() {

    Vector<Confirmation> helpVector = new Vector<>();
    Iterable<DataSnapshot> iterable = mDataSnapshot.getSnapshots();

        for(DataSnapshot snapshot : iterable) {
        Confirmation confirmation = snapshot.getValue(Confirmation.class);
        helpVector.addElement(confirmation);
    }
    data = new ArrayList<>(helpVector);
}

    public void setEmptyView(TextView view) {
        this.emptyView = view;
        initEmptyView();
    }

    private void initEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

}
