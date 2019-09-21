package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class MainViewModel extends ViewModel {

    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseRecyclerOptions;
    private FirebaseHelper mFirebaseHelper;
    public static StateAccount mStateAccount =StateAccount.Cash;


    public MainViewModel() {
        super();
        mFirebaseHelper=FirebaseHelper.getInstance();
        setStateAccount(mStateAccount);
    }




    public StateAccount getStateAccount() {
        return mStateAccount;
    }

    public void setStateAccount(StateAccount stateAccount) {
        mFirebaseHelper.setKeyAccount(stateAccount);
        mFireBaseRecyclerOptions=mFirebaseHelper.getFirebaseRecyclerOptionsCosts();

        mStateAccount =stateAccount;
    }


    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptions() {
        return mFireBaseRecyclerOptions;
    }

}
