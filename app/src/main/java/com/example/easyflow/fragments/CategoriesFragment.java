package com.example.easyflow.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.app.Application;
import android.content.Context;

import com.example.easyflow.activities.CreateNotificationActivity;
import com.example.easyflow.activities.EinAusgabeActivity;
import com.example.easyflow.R;
import com.example.easyflow.utils.GlobalApplication;
import com.example.easyflow.models.Category;

import java.util.List;

public class CategoriesFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM = "param1";
    private static final int BTN_PER_ROW = 3;
    private boolean mEingabe;

    private EinAusgabeActivity mParentActivity;
    private CreateNotificationActivity mParentActivity1;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    public static CategoriesFragment newInstance(boolean showEingabeCategories) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM, showEingabeCategories);
        fragment.setArguments(args);


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEingabe = getArguments().getBoolean(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_categories, container, false);

            if (mEingabe) {
                loadCategories(layout, GlobalApplication.getCategoriesIncome());
            } else {
                loadCategories(layout, GlobalApplication.getCategoriesCost());
            }

        return layout;
    }


    private void loadCategories(View v, List<Category> categories) {

        Context context = this.getContext();
        int countBtnPerLinearLayout = 0;

        LinearLayout categoriesRowLinearLayout=v.findViewById(R.id.categoriesFirstRow);
        categoriesRowLinearLayout.removeAllViews();

        for (Category c : categories) {
            Button btn = new Button(context);
            btn.setText(c.getName());
            btn.setTextSize(10);
            btn.setTag(c);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            params.setMargins(5, 0, 5, 0);

            btn.setLayoutParams(params);
            btn.setOnClickListener(this);
            btn.setBackgroundResource(R.drawable.border_green);
            btn.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(context, c.getIconId()), null, null);

            if (countBtnPerLinearLayout == BTN_PER_ROW) {
                categoriesRowLinearLayout = v.findViewById(R.id.categoriesSecondRow);
                categoriesRowLinearLayout.removeAllViews();
            }
            else if (countBtnPerLinearLayout == BTN_PER_ROW * 2) {
                categoriesRowLinearLayout = v.findViewById(R.id.categoriesThirdRow);
                categoriesRowLinearLayout.removeAllViews();
            }
            else if (countBtnPerLinearLayout == BTN_PER_ROW * 3) {
                categoriesRowLinearLayout = v.findViewById(R.id.categoriesFourthRow);
                categoriesRowLinearLayout.removeAllViews();
            }
            else if (countBtnPerLinearLayout == BTN_PER_ROW * 4) {
                categoriesRowLinearLayout = v.findViewById(R.id.categoriesFifthRow);
                categoriesRowLinearLayout.removeAllViews();
            }

            countBtnPerLinearLayout++;
            categoriesRowLinearLayout.addView(btn);
        }
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        String classname = context.getClass().getSimpleName();

        switch(classname) {

            case "EditRecurringCostsActivity": {
                if (mParentActivity == null) {
                    mParentActivity = (EinAusgabeActivity) this.getActivity();
                    mParentActivity.mDisplayValueEditText.disableInput();}
                break;
            }

            case "CreateNotificationActivity": {
                if (mParentActivity1 == null) {
                    mParentActivity1 = (CreateNotificationActivity) this.getActivity();
                    mParentActivity1.mDisplayValueEditText.disableInput();
                }
                break;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Context context = getContext();
        String classname = context.getClass().getSimpleName();

        switch(classname) {
            case "EditRecurringCostsActivity":  mParentActivity.mDisplayValueEditText.enableInput();
            case "CreateNotificationActivity":  mParentActivity1.mDisplayValueEditText.enableInput();
        }

    }

    @Override
    public void onClick(View v) {

        Category c = (Category) v.getTag();
        Context context = v.getContext();
        String classname =context.getClass().getSimpleName();

        switch(classname) {
            case "EinAusgabeActivity": {
                EinAusgabeActivity activity1 = (EinAusgabeActivity) getActivity();
                if (activity1 != null) {
                    activity1.finishEinAusgabeActivity(c); }
                break;
            }
            case"CreateNotificationActivity": {
                CreateNotificationActivity activity2 = (CreateNotificationActivity) getActivity();
                if(activity2 != null) {
                    activity2.finishCreateNotificationActivity(c); }
            }
        }
    }
}
