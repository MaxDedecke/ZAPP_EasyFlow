package com.example.easyflow.interfaces;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

    // todo make code and view specific for costs

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setImageViews(final Category category) {
        //todo get right iconid from database object
        //todo find object with stream and string value

        Context context = root.getContext();
        Category matching = null;

        Iterator<Category> iteratorCost = MainActivity.categoriesCost.iterator();
        while (iteratorCost.hasNext() && matching == null) {
            Category c = iteratorCost.next();
            if (c.getId() == category.getId())
                matching = c;
        }


        if (matching != null) {
            mIVArrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_red_32dp));
            mIVCategory.setImageDrawable(context.getDrawable(matching.getIconId()));
            return;
        }


        Iterator<Category> iteratorIncome = MainActivity.categoriesIncome.iterator();
        while (iteratorIncome.hasNext() && matching == null) {
            Category c = iteratorIncome.next();
            if (c.getId() == category.getId())
                matching = c;
        }

        if (matching != null) {
            mIVArrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_upward_green_24dp));
            mIVCategory.setImageDrawable(context.getDrawable(matching.getIconId()));
        }


        //todo find iconid in list for ausgabe or einnahme

    }
}
