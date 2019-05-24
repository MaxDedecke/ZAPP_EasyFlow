package com.example.easyflow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class EditTextWithClear extends AppCompatEditText {

    Drawable mClearButtonImage;
    private boolean mInputEnabled;

    public EditTextWithClear(Context context) {
        super(context);
        init();
    }

    public EditTextWithClear(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextWithClear(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        mClearButtonImage = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_backspace_white_24dp, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        }

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((getCompoundDrawablesRelative()[2] == null) || !mInputEnabled) {
                    return false;
                }

                float clearButtonStart; // Used for LTR languages
                float clearButtonEnd;  // Used for RTL languages
                boolean isClearButtonClicked = false;
                // Detect the touch in RTL or LTR layout direction.
                if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                    // If RTL, get the end of the button on the left side.
                    clearButtonEnd = mClearButtonImage.getIntrinsicWidth() + getPaddingStart();
                    // If the touch occured before the end of the button,
                    // set isClearButtonClicked to true.
                    if (event.getX() < clearButtonEnd) {
                        isClearButtonClicked = true;
                    }

                } else {
                    // Layout is LTR.
                    // Get the start of the button on the right side.
                    clearButtonStart = (getWidth()
                            - getPaddingStart() - mClearButtonImage.getIntrinsicHeight());
                    // If the touch occured after the start of the button,
                    // set isClearButtonClicked to true.
                    if (event.getX() > clearButtonStart) {
                        isClearButtonClicked = true;
                    }
                }

                // Check for actions if the button is tapped.
                if (isClearButtonClicked) {
                    // Check for ACTION_DOWN (always occurs before ACTION_UP):
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Switch to the black version of the clear button.
                        mClearButtonImage = ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_backspace_opaque_24dp, null);
                    }
                    // Check for ACTION_UP.
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Switch to the opaque version of the clear button.
                        mClearButtonImage = ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_backspace_white_24dp, null);
                        // Clear the text.
                        if(getText().length()>1) {
                            setText(getText().subSequence(0, getText().length() - 1));

                        }
                        else{
                            setText("0");
                        }

                        return true;
                    }
                }
                return false;
            }
        });


        setText("0");

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                setSelection(getText().length());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showClearButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mInputEnabled=true;
        showClearButton();
    }

    private void showClearButton() {
        setCompoundDrawablesRelativeWithIntrinsicBounds
                (null,                      // Start of text.
                        null,               // Above text.
                        mClearButtonImage,  // End of text.
                        null);              // Below text.
    }

    public void valueIsZero() {
        Toast.makeText(this.getContext(),"Betrag darf nicht 0 sein.",Toast.LENGTH_SHORT).show();
    }

    public void enableInput() {
        mClearButtonImage = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_backspace_white_24dp, null);
        showClearButton();
        mInputEnabled=true;
    }

    public void disableInput() {
        mClearButtonImage = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_backspace_opaque_24dp, null);
        showClearButton();
        mInputEnabled=false;
    }
}
