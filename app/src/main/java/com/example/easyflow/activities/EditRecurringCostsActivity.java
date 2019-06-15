package com.example.easyflow.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.adapters.CostAdapter;
import com.example.easyflow.adapters.SpinnerAccountAdapter;
import com.example.easyflow.models.EditRecurringCostsViewModel;
import com.example.easyflow.utils.GlobalApplication;

public class EditRecurringCostsActivity extends AppCompatActivity {
    private Spinner mSpinnerAccount;
    private RecyclerView mRecyclerView;
    private CostAdapter mCostAdapter;
    private EditRecurringCostsViewModel mViewModel;
    private TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recurring_costs);

        mViewModel = ViewModelProviders.of(this).get(EditRecurringCostsViewModel.class);

        mEmptyTextView=findViewById(R.id.tv_empty_recurring_costs);
        mRecyclerView=findViewById(R.id.recycler_view_recurring_costs);

        SpinnerAccountAdapter adapter= new SpinnerAccountAdapter(this,R.layout.spinner_choose_account_item,R.id.spinner_recurring_costs, GlobalApplication.getListAccounts());

        mSpinnerAccount=findViewById(R.id.spinner_recurring_costs);
        mSpinnerAccount.setAdapter(adapter);

        setUpRecyclerView();

        mSpinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mViewModel.setStateAccount(GlobalApplication.getListAccounts().get(position).getStateAccountObject());
                updateAndSetCostAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                mCostAdapter.onItemRemove(viewHolder, mRecyclerView);
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void updateAndSetCostAdapter() {

        if (mCostAdapter != null)
            mCostAdapter.stopListening();


        mCostAdapter = new CostAdapter(this, mViewModel.getFireBaseRecyclerOptions(),true);
        mCostAdapter.setEmptyView(mEmptyTextView);
        mCostAdapter.startListening();
        mRecyclerView.setAdapter(mCostAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCostAdapter != null)
            mCostAdapter.stopListening();
    }

}
