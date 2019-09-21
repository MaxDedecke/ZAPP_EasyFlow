package com.example.easyflow.activities;

import android.app.Activity;
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
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.CreateNotificationViewModel;
import com.example.easyflow.models.Frequency;
import com.example.easyflow.models.UserNotification;
import com.example.easyflow.utils.FirebaseHelper;

public class CreateNotificationActivity extends AppCompatActivity implements NotifyEventHandler {

    public EditTextWithClear mDisplayValueEditText;
    private EditText mDisplayMemberEditText;
    private EditText mNoteEditText;
    private CalcFragment mCalcFragment;
    private Spinner mSpinnerMembers;
    private CreateNotificationViewModel mViewModel;

    @Override
    protected void onStart() {
        super.onStart();
    }
    //finished

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);

        mViewModel = ViewModelProviders.of(this).get(CreateNotificationViewModel.class);

        //show Categories
        Intent intent = getIntent();
        mViewModel.setShowNotificationCategories(intent.getBooleanExtra(getString(R.string.key_create_notification), true));

        if(mViewModel.isShowNotificationCategories()) {
            this.setTitle(getString(R.string.new_notification));
            mViewModel.setFaktor(1);
        }

        mSpinnerMembers = findViewById(R.id.spinnerMember);
        mSpinnerMembers.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_choose_member_item, Frequency.values()));

        mCalcFragment = CalcFragment.newInstance();
        mCalcFragment.setOnFragCalcFinishEventListener(this);

        //Begin Transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.placeholder_activity_create_notification, mCalcFragment);
        ft.commit();

        this.mDisplayValueEditText = findViewById(R.id.etDisplayValue);
        this.mDisplayValueEditText.setInputType(InputType.TYPE_NULL);
        this.mDisplayValueEditText.setOnFocusChangeListener((v, hasFocus) -> {

            if(!hasFocus)
                return;

                hideKeyBoard(this);
                v.clearFocus();
        });

        this.mDisplayMemberEditText = findViewById(R.id.editTextMember);
        this.mNoteEditText = findViewById(R.id.editTextNote);

    }
    //finished

    public void finishCreateNotificationActivity(Category c) {

        double amountOfNotification = Double.parseDouble(mDisplayValueEditText.getText().toString());
        String note = mNoteEditText.getText().toString();
        int frequenceId = mSpinnerMembers.getSelectedItemPosition();
        String emailSending = FirebaseHelper.mCurrentUser.getEmail();
        String emailReceiving = mDisplayMemberEditText.getText().toString();


        UserNotification notification = new UserNotification(note, emailReceiving, emailSending, false, amountOfNotification);

        FirebaseHelper helper = FirebaseHelper.getInstance();
        helper.addNotification(notification);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.placeholder_activity_create_notification);
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        this.finish();
    }
    //in progress

    @Override
    public void Notify() {

        CategoriesFragment categoriesFragment = CategoriesFragment.newInstance(mViewModel.isShowNotificationCategories());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.detach(mCalcFragment);
        ft.add(R.id.placeholder_activity_create_notification, categoriesFragment);
        ft.addToBackStack("fragmentCategories");
        ft.commit();
    }
    //finished

    public static void hideKeyBoard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    //finished
}
