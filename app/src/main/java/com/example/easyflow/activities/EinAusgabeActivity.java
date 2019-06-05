package com.example.easyflow.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.easyflow.EditTextWithClear;
import com.example.easyflow.R;
import com.example.easyflow.fragments.CalcFragment;
import com.example.easyflow.fragments.CategoriesFragment;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.interfaces.FirebaseHelper;
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.models.*;

import java.util.Calendar;
import java.util.Date;


public class EinAusgabeActivity extends AppCompatActivity implements NotifyEventHandler {


    // Display
    public EditTextWithClear mDisplayValueEditText;
    private EditText mDisplayDateEditText;
    private EditText mNoteEditText;
    private CalcFragment mCalcFragment;
    private CategoriesFragment mCategoriesFragment;
    private Spinner mSpinnerFrequence;

    private final Calendar myCalendar = Calendar.getInstance();
    private Date mDateOfCosts;
    private boolean mShowEingabeCategories;
    private double mFaktor;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ein_ausgabe);

        // Show Categories for Einnahme or Ausgabe
        Intent intent = getIntent();
        mShowEingabeCategories = intent.getBooleanExtra(getString(R.string.key_show_ein_or_ausgabe), true);

        if (mShowEingabeCategories) {
            this.setTitle(getString(R.string.new_income));
            mFaktor = 1;
        } else {
            this.setTitle(getString(R.string.new_cost));
            mFaktor = -1;
        }

        mSpinnerFrequence = findViewById(R.id.spinnerFrequence);
        mSpinnerFrequence.setAdapter(new ArrayAdapter<Frequency>(this, R.layout.spinner_choose_frequence_item, Frequency.values()));

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

        mDisplayDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
                return;

            new DatePickerDialog(EinAusgabeActivity.this, R.style.DialogTheme, (view, year, monthOfYear, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelDate();
            }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            v.clearFocus();
        });

        updateLabelDate();


    }

    private void updateLabelDate() {
        mDateOfCosts = myCalendar.getTime();
        String s = android.text.format.DateFormat.format(Constants.DATE_FORMAT_WEEKDAY, mDateOfCosts).toString();
        mDisplayDateEditText.setText(s);
    }


    public void finishEinAusgabeActivity(Category c) {
        //mDateOfCosts
        double valueOfCosts = Double.parseDouble(mDisplayValueEditText.getText().toString()) * mFaktor;
        String note = mNoteEditText.getText().toString();
        int frequenceId = mSpinnerFrequence.getSelectedItemPosition();

        Cost cost = new Cost(valueOfCosts, mDateOfCosts, c, Frequency.fromId(frequenceId), note);

        FirebaseHelper helper = FirebaseHelper.getInstance();
        helper.addCost(cost);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeholer_ein_ausgabe);
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        this.finish();
    }


    @Override
    public void Notify() {
        mCategoriesFragment = CategoriesFragment.newInstance(mShowEingabeCategories);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.detach(mCalcFragment);
        ft.add(R.id.placeholer_ein_ausgabe, mCategoriesFragment);
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
