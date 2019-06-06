package com.example.easyflow.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.easyflow.EditTextWithClear;
import com.example.easyflow.R;
import com.example.easyflow.fragments.CalcFragment;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.interfaces.FirebaseHelper;
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.interfaces.SpinnerAccountAdapter;
import com.example.easyflow.models.AccountData;
import com.example.easyflow.models.Cost;
import com.example.easyflow.models.Frequency;
import com.example.easyflow.models.StateAccount;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class BookCostActivity extends AppCompatActivity implements NotifyEventHandler {

    private Spinner mSpinnerFrom;
    private Spinner mSpinnerTo;
    public EditTextWithClear mEditText;
    private EditText mDisplayDate;

    private final Calendar myCalendar = Calendar.getInstance();
    private Date mDateOfCosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_cost);


        mEditText=findViewById(R.id.etDisplayValue);
        mEditText.setInputType(InputType.TYPE_NULL);
        this.mEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
                return;

            hideKeyboard(this);
            v.clearFocus();
        });


        ArrayList<AccountData> list=new ArrayList<>();
        list.add(new AccountData(StateAccount.Cash,"Bargeld",R.drawable.ic_cash_new_black));
        list.add(new AccountData(StateAccount.BankAccount,"Bank",R.drawable.ic_bank_account_new_black));
        list.add(new AccountData(StateAccount.Group,"WG",R.drawable.ic_group_black_32dp));

        SpinnerAccountAdapter adapter= new SpinnerAccountAdapter(this,R.layout.spinner_choose_account_item,R.id.textSpinner,list);


        mSpinnerFrom=findViewById(R.id.spinnerFrom);
        mSpinnerFrom.setAdapter(adapter);
        mSpinnerFrom.setSelection(1);

        mSpinnerTo=findViewById(R.id.spinnerTo);
        mSpinnerTo.setAdapter(adapter);


        CalcFragment mCalcFragment = CalcFragment.newInstance();
        mCalcFragment.setOnFragCalcFinishEventListener(this);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.placeholder_activity_book_cost, mCalcFragment);
        // Complete the changes added above
        ft.commit();



        mDisplayDate=findViewById(R.id.editTextDate);
        mDisplayDate.setInputType(InputType.TYPE_NULL);

        mDisplayDate.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
                return;

            new DatePickerDialog(BookCostActivity.this, R.style.DialogTheme, (view, year, monthOfYear, dayOfMonth) -> {
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
        String s=android.text.format.DateFormat.format(Constants.DATE_FORMAT_WEEKDAY,mDateOfCosts).toString();
        mDisplayDate.setText(s);
    }


    @Override
    public void Notify() {
        if(mSpinnerFrom.getSelectedItemPosition()==mSpinnerTo.getSelectedItemPosition()){
            Toast.makeText(this,getString(R.string.konten_muessen_verschieden_sein),Toast.LENGTH_LONG).show();
            return;
        }

        StateAccount fromStateAccount=((AccountData) mSpinnerFrom.getSelectedItem()).getStateAccountObject();
        StateAccount toStateAccount=((AccountData) mSpinnerTo.getSelectedItem()).getStateAccountObject();

        if((fromStateAccount==StateAccount.Group || toStateAccount==StateAccount.Group)&&FirebaseHelper.mCurrentUser.getGroupId()==null){
            Toast.makeText(this,"Sie befinden sich derzeit in keiner Gruppe",Toast.LENGTH_SHORT).show();
            return;
        }

        Cost fromCost =new Cost(-Double.parseDouble(Objects.requireNonNull(mEditText.getText()).toString()),mDateOfCosts, MainActivity.categoryTransferFrom, Frequency.Einmalig,null);
        Cost toCost =new Cost(Double.parseDouble(Objects.requireNonNull(mEditText.getText()).toString()),mDateOfCosts, MainActivity.categoryTransferTo, Frequency.Einmalig,null);


        FirebaseHelper helper= FirebaseHelper.getInstance();
        helper.addCost(fromCost,fromStateAccount);
        helper.addCost(toCost,toStateAccount);

        this.finish();
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
