package com.example.easyflow.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.Cost;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class CostAdapter extends FirebaseRecyclerAdapter<DataSnapshot, CostAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private Date mEndDate;
    private HashMap<Cost, Integer> mCostHashMap = new HashMap<>();
    private TextView mEmptyView;
    private boolean mShowRecurringCosts;
    //private SparseIntArray mSparseIntArray =new SparseIntArray();


    public CostAdapter(Context context, @NonNull FirebaseRecyclerOptions<DataSnapshot> options, boolean showRecurringCosts) {
        super(options);
        mInflater = LayoutInflater.from(context);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        mEndDate = calendar.getTime();
        this.mShowRecurringCosts = showRecurringCosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.main_recycler_item_view, viewGroup, false);
        return new ViewHolder(mItemView);
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DataSnapshot dataSnapshot) {
        Cost cost = dataSnapshot.getValue(Cost.class);

        // Set Title text.
        holder.mTxtTitle.setText(cost.getCategory().getName().trim());
        // Set Description text.
        String description = new SimpleDateFormat(Constants.DATE_FORMAT_WEEKDAY).format(cost.getDate());
        if (cost.getNote() != null && !cost.getNote().isEmpty())
            description += " - " + cost.getNote();
        if (mShowRecurringCosts)
            description += " - " + cost.getFrequency().name()
                    .replace("oe", "ö")
                    .replace("ae", "ä");
        holder.mTxtDesc.setText(description);
        // Set Value text.
        holder.mTxtValue.setText(String.format(Constants.DOUBLE_FORMAT_TWO_DECIMAL, Math.abs(cost.getValue())).trim());
        // Set Imageviews.
        setImageViews(holder, cost.getCategory().getId());
        // Set grey overlay, if cost date is in future


        if (mShowRecurringCosts)
            return;


        if (!mCostHashMap.containsKey(cost)) {
            if (cost.getDate().after(mEndDate))
                mCostHashMap.put(cost, View.VISIBLE);
            else
                mCostHashMap.put(cost, View.INVISIBLE);
        }

        holder.mConstraintLayoutGrey.setVisibility(mCostHashMap.get(cost));
    }


    public void onItemRemove(RecyclerView.ViewHolder viewHolder, RecyclerView recyclerView) {
        Resources resources = mInflater.getContext().getResources();
        int position = viewHolder.getAdapterPosition();


        DataSnapshot snapshot = getSnapshots().getSnapshot(position);
        Cost cost = snapshot.getValue(Cost.class);

        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance();
        firebaseHelper.deleteCost(snapshot, mShowRecurringCosts);


        Snackbar snackbar = Snackbar
                .make(recyclerView, resources.getString(R.string.snackbar_message_cost_deleted), Snackbar.LENGTH_LONG)
                .setAction("UNDO", view -> {
                    if (mShowRecurringCosts)
                        firebaseHelper.addFutureCost(cost);
                    else
                        firebaseHelper.addCost(cost);
                    recyclerView.scrollToPosition(position);
                })
                .setActionTextColor(resources.getColor(R.color.colorPrimary));
        snackbar.show();


    }

    private void setImageViews(ViewHolder holder, int categoryId) {
        Resources resources = mInflater.getContext().getResources();

        int iconId = R.drawable.ic_error_outline_red_32dp;
        int arrowId = R.drawable.ic_error_outline_red_32dp;

        Category matching;


        // Set specified data and return, if matching category was found.
        if ((matching = listContainsCategory(GlobalApplication.getCategoriesCost(), categoryId)) != null) {
            iconId = matching.getIconId();
            arrowId = R.drawable.ic_arrow_downward_red_32dp;
        }
        // Set specified data and return, if matching category was found.
        else if ((matching = listContainsCategory(GlobalApplication.getCategoriesIncome(), categoryId)) != null) {
            iconId = matching.getIconId();
            arrowId = R.drawable.ic_arrow_upward_green_24dp;
        }
        // If matching Category was not in categories Income or Cost, look for it in transfer categories.
        else if (categoryId == GlobalApplication.getCategoryTransferTo().getId()) {
            iconId = GlobalApplication.getCategoryTransferTo().getIconId();
            arrowId = R.drawable.ic_arrow_upward_green_24dp;
        } else if (categoryId == GlobalApplication.getCategoryTransferFrom().getId()) {
            iconId = GlobalApplication.getCategoryTransferFrom().getIconId();
            arrowId = R.drawable.ic_arrow_downward_red_32dp;
        }

        holder.mIVCategory.setImageDrawable(resources.getDrawable(iconId));
        holder.mIVArrow.setImageDrawable(resources.getDrawable(arrowId));
    }


    // Search for matching Category in categoriesCost.
    private Category listContainsCategory(List<Category> categoryList, int categoryId) {
        Category matching = null;
        Iterator<Category> iteratorCost = categoryList.iterator();
        while (iteratorCost.hasNext() && matching == null) {
            Category c = iteratorCost.next();
            if (c.getId() == categoryId)
                matching = c;
        }
        return matching;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        initEmptyView();
    }

    public void setEmptyView(TextView view) {
        this.mEmptyView = view;
        initEmptyView();
    }

    private void initEmptyView() {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxtTitle;
        private TextView mTxtDesc;
        private TextView mTxtValue;
        private ImageView mIVCategory;
        private ImageView mIVArrow;
        private ConstraintLayout mConstraintLayoutGrey;


        ViewHolder(View itemView) {
            super(itemView);

            mTxtTitle = itemView.findViewById(R.id.list_title);
            mTxtDesc = itemView.findViewById(R.id.list_desc);
            mTxtValue = itemView.findViewById(R.id.list_value);
            mIVArrow = itemView.findViewById(R.id.list_arrow);
            mIVCategory = itemView.findViewById(R.id.list_category);
            mConstraintLayoutGrey = itemView.findViewById(R.id.transparent_constraint_layout);

        }
    }

}
