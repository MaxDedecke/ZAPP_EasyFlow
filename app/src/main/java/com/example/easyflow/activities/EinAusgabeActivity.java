package com.example.easyflow;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.easyflow.models.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class EinAusgabeActivity extends AppCompatActivity implements View.OnClickListener{

    // Rechner
    private State state = State.clean;
    private String op1;
    private Operation op = Operation.none;
    private String op2;

    // Display
    public EditTextWithClear mDisplayValueEditText;
    private EditText mDisplayDateEditText;
    private EditText mNoteEditText;
    private CalcFragment mCalcFragment;
    private Spinner mSpinnerFrequence;
    private boolean mNewOperandExpected = true;

    private final Calendar myCalendar = Calendar.getInstance();
    private Date mDateOfCosts;
    private boolean mShowEingabeCategories;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ein_ausgabe);

        // Show Categories for Einnahme or Ausgabe
        Intent intent= getIntent();
        mShowEingabeCategories= intent.getBooleanExtra(getString(R.string.key_show_ein_or_ausgabe),true);

        if(mShowEingabeCategories){
            this.setTitle(getString(R.string.new_income));
        }else {
            this.setTitle(getString(R.string.new_cost));
        }

        mSpinnerFrequence=findViewById(R.id.spinnerFrequence);
        ArrayAdapter<CharSequence> frequenceAdapter =ArrayAdapter.createFromResource(
                this,R.array.wiederkehrend_array,R.layout.spinner_choose_frequence_item);
        mSpinnerFrequence.setAdapter(frequenceAdapter);

        mCalcFragment=CalcFragment.newInstance();

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.placeholer_ein_ausgabe, mCalcFragment);
        // Complete the changes added above
        ft.commit();


        this.mDisplayValueEditText = findViewById(R.id.etDisplayValue);
        this.mDisplayValueEditText.setInputType(InputType.TYPE_NULL);
        this.mDisplayDateEditText=findViewById(R.id.editTextDate);
        this.mDisplayDateEditText.setInputType(InputType.TYPE_NULL);
        this.mNoteEditText=findViewById(R.id.editTextNote);


        mDisplayDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EinAusgabeActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabelDate();
                    }
                }, myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        updateLabelDate();


    }

    private void updateLabelDate() {
        mDateOfCosts = myCalendar.getTime();
        String s=android.text.format.DateFormat.format(MainActivity.dateTimeFormat,mDateOfCosts).toString();
        mDisplayDateEditText.setText(s);
    }

    public void setButtonOnClickListener() {
        // Set onClickListener for Operation Buttons.
        Button btnMinus = findViewById(R.id.btnOp_Minus);
        Button btnPlus = findViewById(R.id.btnOp_Plus);
        Button btnDiv = findViewById(R.id.btnOp_Div);
        Button btnMul = findViewById(R.id.btnOp_Mul);
        Button btnEquals = findViewById(R.id.btnOp_Equals);

        Button[] btnOpArray = new Button[]{btnMinus, btnPlus, btnDiv, btnMul, btnEquals};

        for (Button b : btnOpArray) {
            b.setOnClickListener(this);
        }

        // Set onClickListener for Num Buttons.
        Button btnNumPeriod = findViewById(R.id.btnNum_Period);
        Button btn0 = findViewById(R.id.btnNum_0);
        Button btn1 = findViewById(R.id.btnNum_1);
        Button btn2 = findViewById(R.id.btnNum_2);
        Button btn3 = findViewById(R.id.btnNum_3);
        Button btn4 = findViewById(R.id.btnNum_4);
        Button btn5 = findViewById(R.id.btnNum_5);
        Button btn6 = findViewById(R.id.btnNum_6);
        Button btn7 = findViewById(R.id.btnNum_7);
        Button btn8 = findViewById(R.id.btnNum_8);
        Button btn9 = findViewById(R.id.btnNum_9);

        Button[] btnNumArray = new Button[]{btnNumPeriod, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9};

        for (Button b : btnNumArray) {
            b.setOnClickListener(this);
        }

        // Set onClickListener for Category.
        Button btnCategory = findViewById(R.id.btnCategory);
        btnCategory.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNum_Period:
            case R.id.btnNum_0:
            case R.id.btnNum_1:
            case R.id.btnNum_2:
            case R.id.btnNum_3:
            case R.id.btnNum_4:
            case R.id.btnNum_5:
            case R.id.btnNum_6:
            case R.id.btnNum_7:
            case R.id.btnNum_8:
            case R.id.btnNum_9:
                onClickBtnNum(v);
                break;
            case R.id.btnOp_Plus:
            case R.id.btnOp_Minus:
            case R.id.btnOp_Div:
            case R.id.btnOp_Mul:
            case R.id.btnOp_Equals:
                onClickBtnOp(v);
                break;
            case R.id.btnCategory:
                onClickBtnCategorie();
                break;
        }
    }

    private void onClickBtnCategorie() {
        double val =Double.parseDouble(Objects.requireNonNull(mDisplayValueEditText.getText()).toString());

        if(val==0){
            mDisplayValueEditText.valueIsZero();
            return;
        }


        CategoriesFragment mCategoriesFragment = CategoriesFragment.newInstance(mShowEingabeCategories);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.detach(mCalcFragment);
        ft.add(R.id.placeholer_ein_ausgabe, mCategoriesFragment);
        ft.addToBackStack("fragmentCategories");
        // Complete the changes added above
        ft.commit();
    }


    public void onClickBtnNum(View v) {

        String num = "";

        String displayText = "";

        if (this.mNewOperandExpected) {
            this.writeDisplay("0");
            this.mNewOperandExpected = false;
        } else {
            displayText = this.readDisplay();


            if(displayText.substring(0,1).equals("0"))
                displayText="";

        }

        // Auswerten des betaetigten Schalters
        switch (v.getId()) {

            case R.id.btnNum_0:
                num = "0";
                break;
            case R.id.btnNum_1:
                num = "1";
                break;
            case R.id.btnNum_2:
                num = "2";
                break;
            case R.id.btnNum_3:
                num = "3";
                break;
            case R.id.btnNum_4:
                num = "4";
                break;
            case R.id.btnNum_5:
                num = "5";
                break;
            case R.id.btnNum_6:
                num = "6";
                break;
            case R.id.btnNum_7:
                num = "7";
                break;
            case R.id.btnNum_8:
                num = "8";
                break;
            case R.id.btnNum_9:
                num = "9";
                break;
            case R.id.btnNum_Period:
                if (!this.hasDecimalPoint(displayText)) {
                    num = ".";
                }
                break;
        }



        this.writeDisplay(displayText + num);
    }

    public void onClickBtnOp(View v) {

        Operation op = Operation.none;
        String result;

        this.mNewOperandExpected = true;

        switch (v.getId()) {

            case R.id.btnOp_Plus:
                op = Operation.add;
                break;
            case R.id.btnOp_Minus:
                op = Operation.sub;
                break;
            case R.id.btnOp_Mul:
                op = Operation.mul;
                break;
            case R.id.btnOp_Div:
                op = Operation.div;
                break;
            case R.id.btnOp_Equals:
                equalsOp();
                return;


        }

        this.handleOperand(op);

        if (this.state == State.hasOp2) {
            result = this.calculate(this.op);
            this.writeDisplay(result);
            this.handleOperand(op);
        }

    }

    void writeDisplay(String value) {
        this.mDisplayValueEditText.setText(value);
    }

    String readDisplay() {
        return this.mDisplayValueEditText.getText().toString();
    }

    boolean hasDecimalPoint(String value) {
        return value.contains(".");
    }

    void handleOperand(Operation op) {

        switch (this.state) {
            case clean :
                this.setOp1(this.readDisplay());
                this.op = op;
                break;
            case hasOp1 :
                this.setOp2(this.readDisplay());
                break;
            default:
                break;
        }
    }

    private void setOp1(String value) {
        this.op1 = value;
        this.state = State.hasOp1;
    }

    private void setOp2(String value) {
        this.op2 = value;
        this.state = State.hasOp2;
    }

    private String calculate(Operation op) {

        double op1 = Double.parseDouble(this.op1);
        double op2 = Double.parseDouble(this.op2);
        double res = 0;

        switch (op) {
            case add : res = op1 + op2; this.state = State.clean; break;
            case sub : res = op1 - op2; this.state = State.clean; break;
            case mul : res = op1 * op2; this.state = State.clean; break;
            case div : res = op1 / op2; this.state = State.clean; break;
            default:
                break;

        }

        return Double.toString(res);
    }

    void equalsOp() {

        this.setOp2(this.readDisplay());

        String result = this.calculate(this.op);

        this.writeDisplay(result);
        this.state = State.clean;

    }

    void finishEinAusgabeActivity(Category c){
        //mDateOfCosts
        double valueOfCosts =Double.parseDouble(mDisplayValueEditText.getText().toString());
        String note=mNoteEditText.getText().toString();
        int frequenceId=mSpinnerFrequence.getSelectedItemPosition();

        //todo safe cost

        Cost cost = new Cost(valueOfCosts,mDateOfCosts,c,Frequency.fromId(frequenceId),note);

        MainActivity.mDbRef.setValue(cost);
        this.finish();
    }


}
