package com.example.easyflow.models;

import android.arch.lifecycle.ViewModel;

import com.example.easyflow.utils.FirebaseHelper;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

public class MainViewModel extends ViewModel {

    private FirebaseRecyclerOptions<DataSnapshot> mFireBaseRecyclerOptions;
    private CostSum mCostSum;
    public static StateAccount mStateAccount =StateAccount.Cash;

    public MainViewModel() {
        super();
        setStateAccount(mStateAccount);
    }




    public StateAccount getStateAccount() {
        return mStateAccount;
    }

    public void setStateAccount(StateAccount stateAccount) {
        FirebaseHelper firebaseHelper=FirebaseHelper.getInstance();
        firebaseHelper.setKeyAccount(stateAccount);

        this.mStateAccount =stateAccount;
        this.mFireBaseRecyclerOptions=firebaseHelper.getFirebaseRecyclerOptionsCosts();
    }


    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptions() {
        return mFireBaseRecyclerOptions;
    }

    public void setCostSum(CostSum costSum) {
        mCostSum=costSum;
    }

    public CostSum getCostSum(){return mCostSum;}
}
