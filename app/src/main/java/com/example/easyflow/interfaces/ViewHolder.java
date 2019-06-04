package com.example.easyflow.interfaces;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.activities.MainActivity;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.Cost;

import java.text.SimpleDateFormat;
import java.util.Iterator;

class ViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout root;
    private TextView mTxtTitle;
    private TextView mTxtDesc;
    private TextView mTxtValue;
    private ImageView mIVCategory;
    private ImageView mIVArrow;
    private Cost mCost;
    private double mValue=0;



    ViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
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

    void setTxtTitle() {
        mTxtTitle.setText(mCost.getCategory().getName());
    }


    void setTxtDesc() {
        String description = new SimpleDateFormat(Constants.DATE_FORMAT_WEEKDAY).format(mCost.getDate());
        if (mCost.getNote()!=null && !mCost.getNote().isEmpty())
            description += " - " + mCost.getNote();
        mTxtDesc.setText(description);
    }

    void setTxtValue() {
        mValue=mCost.getValue();
        mTxtValue.setText(String.format(Constants.DOUBLE_FORMAT_TWO_DECIMAL, mValue));
    }

    void setImageViews() {
        Context context = root.getContext();
        Integer categoryId = mCost.getCategory().getId();


        Category matching = null;

        // Search for matching Category in categoriesCost.
        Iterator<Category> iteratorCost = MainActivity.categoriesCost.iterator();
        while (iteratorCost.hasNext() && matching == null) {
            Category c = iteratorCost.next();
            if (c.getId() == categoryId)
                matching = c;
        }
        // Set specified data and return, if matching category was found.
        if (matching != null) {
            mIVArrow.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_arrow_downward_red_32dp, null));
            mIVCategory.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), matching.getIconId(), null));
            return;
        }

        // Search for matching Category in categoriesIncome.
        Iterator<Category> iteratorIncome = MainActivity.categoriesIncome.iterator();
        while (iteratorIncome.hasNext() && matching == null) {
            Category c = iteratorIncome.next();
            if (c.getId() == categoryId)
                matching = c;
        }
        // Set specified data and return, if matching category was found.
        if (matching != null) {
            mIVArrow.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_arrow_upward_green_24dp, null));
            mIVCategory.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), matching.getIconId(), null));
            return;
        }

        // If matching Category was not in categories Income or Cost, look for it in transfer categories.
        if (categoryId == MainActivity.categoryTransferTo.getId()) {
            mIVArrow.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_arrow_upward_green_24dp, null));
            mIVCategory.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), MainActivity.categoryTransferTo.getIconId(), null));
        } else if (categoryId == MainActivity.categoryTransferFrom.getId()) {
            mIVArrow.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_arrow_downward_red_32dp, null));
            mIVCategory.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), MainActivity.categoryTransferFrom.getIconId(), null));
        }
    }

    public void setData(Cost cost) {
        mCost = cost;
        setTxtTitle();
        setImageViews();
        setTxtDesc();
        setTxtValue();
    }

    public double getValue() {
        return mValue;
    }
}
