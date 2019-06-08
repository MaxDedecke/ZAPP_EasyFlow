package com.example.easyflow.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.easyflow.EditTextWithClear;
import com.example.easyflow.R;
import com.example.easyflow.fragments.CalcFragment;
import com.example.easyflow.fragments.CategoriesFragment;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.models.*;

import java.util.Calendar;


public class EinAusgabeActivity extends AppCompatActivity implements NotifyEventHandler {


    // Display
    public EditTextWithClear mDisplayValueEditText;
    private EditText mDisplayDateEditText;
    private EditText mNoteEditText;
    private CalcFragment mCalcFragment;
    private Spinner mSpinnerFrequence;
    private EinAusgabeViewModel mViewModel;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ein_ausgabe);

        mViewModel= ViewModelProviders.of(this).get(EinAusgabeViewModel.class);

        // Show Categories for Einnahme or Ausgabe
        Intent intent = getIntent();
        mViewModel.setShowEingabeCategories(intent.getBooleanExtra(getString(R.string.key_show_ein_or_ausgabe), true));


        if (mViewModel.isShowEingabeCategories()) {
            this.setTitle(getString(R.string.new_income));
            mViewModel.setFaktor(1);
        } else {
            this.setTitle(getString(R.string.new_cost));
            mViewModel.setFaktor(-1);
        }

        mSpinnerFrequence = findViewById(R.id.spinnerFrequence);
        mSpinnerFrequence.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_choose_frequence_item, Frequency.values()));

        mCalcFragment = CalcFragment.newInstance();
        mCalcFragment.setOnFragCalcFinishEventListener(this);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.placeholer_ein_ausgabe, mCalcFragment);
        // Complete the changes added above
        ft.commit();


        this.mDisplayValueEditText = findViewById(R.id.etDisplayValue);
        this.mDisplayValueEditText.setInputType(InputType.TYPE_NULL);
        this.mDisplayValueEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
                return;

            hideKeyboard(this);
            v.clearFocus();
        });

        this.mDisplayDateEditText = findViewById(R.id.editTextDate);
        this.mDisplayDateEditText.setInputType(InputType.TYPE_NULL);
        this.mNoteEditText = findViewById(R.id.editTextNote);

        Calendar calendar = mViewModel.getMyCalendar();

        mDisplayDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
                return;

            new DatePickerDialog(EinAusgabeActivity.this, R.style.DialogTheme, (view, year, monthOfYear, dayOfMonth) -> {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelDate();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            v.clearFocus();
        });

        updateLabelDate();


    }

    private void updateLabelDate() {
        mViewModel.setDateOfCosts(mViewModel.getMyCalendar().getTime());
        String s = android.text.format.DateFormat.format(Constants.DATE_FORMAT_WEEKDAY,mViewModel.getDateOfCosts()).toString();
        mDisplayDateEditText.setText(s);
    }


    public void finishEinAusgabeActivity(Category c) {
        //mDateOfCosts
        double valueOfCosts = Double.parseDouble(mDisplayValueEditText.getText().toString()) * mViewModel.getFaktor();
        String note = mNoteEditText.getText().toString();
        int frequenceId = mSpinnerFrequence.getSelectedItemPosition();

        Cost cost = new Cost(valueOfCosts, mViewModel.getDateOfCosts(), c, Frequency.fromId(frequenceId), note);

        FirebaseHelper helper = FirebaseHelper.getInstance();
        helper.addCost(cost);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeholer_ein_ausgabe);
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        this.finish();
    }


    @Override
    public void Notify() {
        CategoriesFragment categoriesFragment = CategoriesFragment.newInstance(mViewModel.isShowEingabeCategories());

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.detach(mCalcFragment);
        ft.add(R.id.placeholer_ein_ausgabe, categoriesFragment);
        ft.addToBackStack("fragmentCategories");
        // Complete the changes added above
        ft.commit();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
