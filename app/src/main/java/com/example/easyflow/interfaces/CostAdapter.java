package com.example.easyflow.interfaces;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.activities.MainActivity;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.Cost;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;


public class CostAdapter extends FirebaseRecyclerAdapter<DataSnapshot, CostAdapter.ViewHolder> {
    private LayoutInflater mInflater;


    public CostAdapter(Context context, @NonNull FirebaseRecyclerOptions options) {
        super(options);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemVview = mInflater.inflate(R.layout.sample_list_item_view, viewGroup, false);
        return new ViewHolder(mItemVview);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull DataSnapshot dataSnapshot) {
        Cost cost = dataSnapshot.getValue(Cost.class);
        String key = dataSnapshot.getKey();

        // Set Title text.
        holder.mTxtTitle.setText(cost.getCategory().getName().trim());
        // Set Description text.
        String description = new SimpleDateFormat(Constants.DATE_FORMAT_WEEKDAY).format(cost.getDate());
        if (cost.getNote() != null && !cost.getNote().isEmpty())
            description += " - " + cost.getNote();
        holder.mTxtDesc.setText(description);
        // Set Value text.
        holder.mTxtValue.setText(String.format(Constants.DOUBLE_FORMAT_TWO_DECIMAL, Math.abs(cost.getValue())).trim());
        // Set Imageviews.
        setImageViews(holder,cost.getCategory().getId());

    }


    public void deleteItem(int position) {
        DataSnapshot snapshot = getSnapshots().getSnapshot(position);

        FirebaseHelper firebaseHelper=FirebaseHelper.getInstance();
        firebaseHelper.deleteCost(snapshot);


    }

    private void setImageViews(ViewHolder holder, int categoryId) {
        Resources resources = mInflater.getContext().getResources();

        int iconId = R.drawable.ic_error_outline_red_32dp;
        int arrowId = R.drawable.ic_error_outline_red_32dp;

        // todo probleme bei logo bei überweisung
        Category matching;


        // Set specified data and return, if matching category was found.
        if ((matching = listContainsCategory(MainActivity.categoriesCost, categoryId)) != null) {
            iconId = matching.getIconId();
            arrowId = R.drawable.ic_arrow_downward_red_32dp;
        }
        // Set specified data and return, if matching category was found.
        else if ((matching = listContainsCategory(MainActivity.categoriesIncome, categoryId)) != null) {
            iconId = matching.getIconId();
            arrowId = R.drawable.ic_arrow_upward_green_24dp;
        }
        // If matching Category was not in categories Income or Cost, look for it in transfer categories.
        else if (categoryId == MainActivity.categoryTransferTo.getId()) {
            iconId = MainActivity.categoryTransferTo.getIconId();
            arrowId = R.drawable.ic_arrow_upward_green_24dp;
        }
        else if (categoryId == MainActivity.categoryTransferFrom.getId()) {
            iconId = MainActivity.categoryTransferFrom.getIconId();
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


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxtTitle;
        private TextView mTxtDesc;
        private TextView mTxtValue;
        private ImageView mIVCategory;
        private ImageView mIVArrow;


        ViewHolder(View itemView) {
            super(itemView);

            mTxtTitle = itemView.findViewById(R.id.list_title);
            mTxtDesc = itemView.findViewById(R.id.list_desc);
            mTxtValue = itemView.findViewById(R.id.list_value);
            mIVArrow = itemView.findViewById(R.id.list_arrow);
            mIVCategory = itemView.findViewById(R.id.list_category);

            /*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            */
        }

    }

}
