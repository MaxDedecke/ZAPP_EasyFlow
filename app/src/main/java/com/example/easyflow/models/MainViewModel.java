package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class MainViewModel extends ViewModel {

    private StateAccount mStateAccount;
    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseRecyclerOptions;

    public MainViewModel() {
        super();

        setStateAccount(StateAccount.Cash);
    }




    public StateAccount getStateAccount() {
        return mStateAccount;
    }

    public void setStateAccount(StateAccount stateAccount) {
        FirebaseHelper.setKeyAccount(stateAccount);

        FirebaseHelper firebaseHelper=FirebaseHelper.getInstance();

        this.mStateAccount = stateAccount;
        this.mFireBaseRecyclerOptions=firebaseHelper.getFirebaseRecyclerOptions();
    }


    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptions() {
        return mFireBaseRecyclerOptions;
    }
}
