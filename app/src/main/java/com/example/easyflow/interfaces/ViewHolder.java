package com.example.easyflow.interfaces;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout root;
    public TextView mTxtTitle;
    public TextView mTxtDesc;
    public TextView mTxtValue;
    public ImageView mIVCategory;
    public ImageView mIVArrow;


    public ViewHolder(View itemView) {
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
                //todo wenn benötigt

            }
        });
        */
    }

    public void setTxtTitle(String string) {
        mTxtTitle.setText(string);
    }


    public void setTxtDesc(String string) {
        mTxtDesc.setText(string);
    }

    public void setTxtValue(Double value) {
        mTxtValue.setText(String.format("%.2f",value));
    }

    public void setImageViews(final Category category) {
        Context context = root.getContext();
        Category matching = null;

        Iterator<Category> iteratorCost = MainActivity.categoriesCost.iterator();
        while (iteratorCost.hasNext() && matching == null) {
            Category c = iteratorCost.next();
            if (c.getId() == category.getId())
                matching = c;
        }


        if (matching != null) {
            mIVArrow.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_arrow_downward_red_32dp,null));
            mIVCategory.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),matching.getIconId(),null));
            return;
        }


        Iterator<Category> iteratorIncome = MainActivity.categoriesIncome.iterator();
        while (iteratorIncome.hasNext() && matching == null) {
            Category c = iteratorIncome.next();
            if (c.getId() == category.getId())
                matching = c;
        }

        if (matching != null) {
            mIVArrow.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_arrow_upward_green_24dp,null));
            mIVCategory.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),matching.getIconId(),null));
        }

    }
}
