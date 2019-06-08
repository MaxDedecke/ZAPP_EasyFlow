package com.example.easyflow.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.easyflow.EditTextWithClear;
import com.example.easyflow.activities.BookCostActivity;
import com.example.easyflow.activities.EinAusgabeActivity;
import com.example.easyflow.R;
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.models.Operation;
import com.example.easyflow.models.StateCalculator;

import java.util.Objects;

public class CalcFragment extends Fragment implements View.OnClickListener {

    private EditTextWithClear mEditText;
    private View mView;
    NotifyEventHandler mListener;


    // Rechner
    private StateCalculator stateCalculator = StateCalculator.clean;
    private String op1;
    private Operation op = Operation.none;
    private String op2;
    private boolean mNewOperandExpected = true;


    public CalcFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity parentActivity = getActivity();

        if (getActivity().getClass() == EinAusgabeActivity.class) {
            mEditText = ((EinAusgabeActivity) parentActivity).mDisplayValueEditText;
        } else if (getActivity().getClass() == BookCostActivity.class) {
            mEditText = ((BookCostActivity) parentActivity).mEditText;
            ((Button)mView.findViewById(R.id.btnCategory)).setText(getString(R.string.ueberweisung_hinzufügen));
        }
        setButtonOnClickListener();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalcFragment.
     */
    public static CalcFragment newInstance() {
        return new CalcFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_calc, container, false);
        return mView;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void setButtonOnClickListener() {
        // Set onClickListener for Operation Buttons.
        Button btnMinus = mView.findViewById(R.id.btnOp_Minus);
        Button btnPlus = mView.findViewById(R.id.btnOp_Plus);
        Button btnDiv = mView.findViewById(R.id.btnOp_Div);
        Button btnMul = mView.findViewById(R.id.btnOp_Mul);
        Button btnEquals = mView.findViewById(R.id.btnOp_Equals);

        Button[] btnOpArray = new Button[]{btnMinus, btnPlus, btnDiv, btnMul, btnEquals};

        for (Button b : btnOpArray) {
            b.setOnClickListener(this);
        }

        // Set onClickListener for Num Buttons.
        Button btnNumPeriod = mView.findViewById(R.id.btnNum_Period);
        Button btn0 = mView.findViewById(R.id.btnNum_0);
        Button btn1 = mView.findViewById(R.id.btnNum_1);
        Button btn2 = mView.findViewById(R.id.btnNum_2);
        Button btn3 = mView.findViewById(R.id.btnNum_3);
        Button btn4 = mView.findViewById(R.id.btnNum_4);
        Button btn5 = mView.findViewById(R.id.btnNum_5);
        Button btn6 = mView.findViewById(R.id.btnNum_6);
        Button btn7 = mView.findViewById(R.id.btnNum_7);
        Button btn8 = mView.findViewById(R.id.btnNum_8);
        Button btn9 = mView.findViewById(R.id.btnNum_9);

        Button[] btnNumArray = new Button[]{btnNumPeriod, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9};

        for (Button b : btnNumArray) {
            b.setOnClickListener(this);
        }

        // Set onClickListener for Category.
        Button btnCategory = mView.findViewById(R.id.btnCategory);
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
                if (mListener != null)
                    onClickBtnCategorie();
                break;
        }
    }


    public void onClickBtnNum(View v) {

        String num = "";

        String displayText = "";

        if (this.mNewOperandExpected) {
            this.writeDisplay("0");
            this.mNewOperandExpected = false;
        } else {
            displayText = this.readDisplay();


            if (displayText.substring(0, 1).equals("0"))
                displayText = "";

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

        if (this.stateCalculator == StateCalculator.hasOp2) {
            result = this.calculate(this.op);
            this.writeDisplay(result);
            this.handleOperand(op);
        }

    }

    void writeDisplay(String value) {
        this.mEditText.setText(value);
    }

    String readDisplay() {
        return this.mEditText.getText().toString();
    }

    boolean hasDecimalPoint(String value) {
        return value.contains(".");
    }

    void handleOperand(Operation op) {

        switch (this.stateCalculator) {
            case clean:
                this.setOp1(this.readDisplay());
                this.op = op;
                break;
            case hasOp1:
                this.setOp2(this.readDisplay());
                break;
            default:
                break;
        }
    }

    private void setOp1(String value) {
        this.op1 = value;
        this.stateCalculator = StateCalculator.hasOp1;
    }

    private void setOp2(String value) {
        this.op2 = value;
        this.stateCalculator = StateCalculator.hasOp2;
    }

    private String calculate(Operation op) {

        double op1 = Double.parseDouble(this.op1);
        double op2 = Double.parseDouble(this.op2);
        double res = 0;

        switch (op) {
            case add:
                res = op1 + op2;
                this.stateCalculator = StateCalculator.clean;
                break;
            case sub:
                res = op1 - op2;
                this.stateCalculator = StateCalculator.clean;
                break;
            case mul:
                res = op1 * op2;
                this.stateCalculator = StateCalculator.clean;
                break;
            case div:
                res = op1 / op2;
                this.stateCalculator = StateCalculator.clean;
                break;
            default:
                break;

        }

        return Double.toString(res);
    }

    void equalsOp() {

        this.setOp2(this.readDisplay());

        String result = this.calculate(this.op);

        this.writeDisplay(result);
        this.stateCalculator = StateCalculator.clean;

    }

    private void onClickBtnCategorie() {
        double val = Double.parseDouble(Objects.requireNonNull(mEditText.getText()).toString());

        if (val == 0) {
            mEditText.valueIsZero();
            return;
        }

        mListener.Notify();
    }

    public void setOnFragCalcFinishEventListener(NotifyEventHandler eventListener) {
        mListener = eventListener;
    }
}
