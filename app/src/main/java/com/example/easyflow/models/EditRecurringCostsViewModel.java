package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class EditRecurringCostsViewModel extends ViewModel {

    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseRecyclerOptions;
    private FirebaseHelper mFirebaseHelper;
    private StateAccount mStateAccount=StateAccount.Cash;

    public EditRecurringCostsViewModel() {
        super();
        mFirebaseHelper=FirebaseHelper.getInstance();
    }


    public FirebaseRecyclerOptions<DataSnapshot> getFireBaseRecyclerOptions() {
        return mFireBaseRecyclerOptions;
    }

    public StateAccount getStateAccount() {
        return mStateAccount;
    }

    public void setStateAccount(StateAccount stateAccount) {
        this.mStateAccount = stateAccount;
        mFirebaseHelper.setKeyAccount(stateAccount);
        mFireBaseRecyclerOptions=mFirebaseHelper.getFirebaseRecyclerOptionsRecurringCosts(getStateAccount());
    }
}
