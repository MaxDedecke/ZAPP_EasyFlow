package com.example.easyflow.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.adapters.CostAdapter;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.interfaces.NotifyEventHandlerCostSum;
import com.example.easyflow.interfaces.NotifyEventHandlerStringMap;
import com.example.easyflow.models.CostSum;
import com.example.easyflow.models.MainViewModel;
import com.example.easyflow.models.StateAccount;
import com.example.easyflow.utils.FirebaseHelper;
import com.example.easyflow.utils.GlobalApplication;
import com.google.gson.Gson;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NotifyEventHandlerCostSum, NotifyEventHandlerStringMap {
    private MainViewModel mViewModel;

    private CostAdapter mCostAdapter;
    private RecyclerView mRecyclerView;
    private TextView mSumTextView;
    private MenuItem mMenuItemGroup;
    private MenuItem mMenuItemSelectAccount;
    private TextView mEmptyTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.list);
        mSumTextView = findViewById(R.id.tvSummary);
        mEmptyTextView = findViewById(R.id.tvEmpty);

        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (!TextUtils.isEmpty(FirebaseHelper.mCurrentUser.getGroupId()))
            navigationView.getMenu().getItem(3).getSubMenu().getItem(0).setTitle(R.string.menu_settings_group);


        FirebaseHelper helper = new FirebaseHelper();
        helper.setListener(this, this);

        // Initialize and show LiveData for the Main Content
        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mMenuItemSelectAccount != null)
            setActionBarHeadItem();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mCostAdapter.stopListening();
    }

    private String getColoredSpanned(String text, String color) {
        return "<font color=#" + color + ">" + text + "</font>";
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenuItemGroup = menu.getItem(2);
        mMenuItemSelectAccount = menu.getItem(1);
        setActionBarHeadItem();



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_account_current) {
            return true;
        } else if (id == R.id.action_booking) {
            Intent intent = new Intent(this, BookCostActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_menu_group) {
            Intent intent = new Intent(this, GroupSettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_account_bank) {
            mViewModel.setStateAccount(StateAccount.BankAccount);
            setActionBarHeadItem();
            return true;
        } else if (id == R.id.action_account_cash) {
            mViewModel.setStateAccount(StateAccount.Cash);
            setActionBarHeadItem();
            return true;
        } else if (id == R.id.action_account_group) {
            if (FirebaseHelper.mCurrentUser.getGroupId() != null) {

                mViewModel.setStateAccount(StateAccount.Group);
                setActionBarHeadItem();
                return true;
            }

            showAlertDialogCreateGroup();
        }

        return super.onOptionsItemSelected(item);
    }


    private void showAlertDialogCreateGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alertdialog_keine_gruppe_message));
        builder.setTitle(getString(R.string.alertdialog_keine_gruppe_title));
        builder.setNegativeButton(getString(R.string.alertdialog_keine_gruppe_negative_button), (dialog, which) -> {
            switch (mViewModel.getStateAccount()){
                case Cash:
                    ((NavigationView)findViewById(R.id.nav_view)).getMenu().getItem(0).setChecked(true);
                    break;
                case BankAccount:
                    ((NavigationView)findViewById(R.id.nav_view)).getMenu().getItem(1).setChecked(true);
                    break;
            }

        });
        builder.setPositiveButton(getString(R.string.alertdialog_keine_gruppe_positive_button), (dialog, which) -> {
            FirebaseHelper helper = FirebaseHelper.getInstance();
            helper.createGroup();

            GlobalApplication.saveUserInSharedPreferences(FirebaseHelper.mCurrentUser);


            ((MenuItem) findViewById(R.id.nav_settings_group)).setTitle(R.string.menu_settings_group);

            mViewModel.setStateAccount(StateAccount.Group);
            setActionBarHeadItem();

        });
        builder.show();
    }

    private void setActionBarHeadItem() {
        switch (mViewModel.getStateAccount()) {
            case Cash:
                mMenuItemSelectAccount.setTitle(getString(R.string.actionbar_item_bargeld));
                mMenuItemSelectAccount.setIcon(R.drawable.ic_cash_new_white);
                mMenuItemGroup.setVisible(false);
                break;
            case BankAccount:
                mMenuItemSelectAccount.setTitle(getString(R.string.actionbar_item_bank));
                mMenuItemSelectAccount.setIcon(R.drawable.ic_bank_account_new_white);
                mMenuItemGroup.setVisible(false);
                break;
            case Group:
                mMenuItemSelectAccount.setTitle(getString(R.string.actionbar_item_wg));
                mMenuItemSelectAccount.setIcon(R.drawable.ic_group_white_32dp);
                mMenuItemGroup.setVisible(true);
                break;
        }

        updateAndSetCostAdapter();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_show_cash) {
            mViewModel.setStateAccount(StateAccount.Cash);
            setActionBarHeadItem();
        } else if (id == R.id.nav_show_bank_account) {
            mViewModel.setStateAccount(StateAccount.BankAccount);
            setActionBarHeadItem();
        } else if (id == R.id.nav_show_group) {
            if (FirebaseHelper.mCurrentUser.getGroupId() == null) {
                showAlertDialogCreateGroup();
            }
            else {
                mViewModel.setStateAccount(StateAccount.Group);
                setActionBarHeadItem();
            }

        } else if (id == R.id.nav_settings_group) {
            if (FirebaseHelper.mCurrentUser.getGroupId() == null) {
                showAlertDialogCreateGroup();
            } else {
                Intent intent = new Intent(this, GroupSettingsActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_settings) {
            //todo show settings


            // todo sammeln von ideen für drawer
        /*
            Gruppe gründen /Gruppen einstellungen
            dark mode
            monats anfang
            einträge exportieren
            einträge löschen

         */

            Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    public void showEinAusgabeActivity(View view) {
        int viewId = view.getId();

        Intent newIntent = new Intent(MainActivity.this, EinAusgabeActivity.class);

        if (viewId == R.id.btnEinnahme) {
            newIntent.putExtra(getString(R.string.key_show_ein_or_ausgabe), true);
        } else if (viewId == R.id.btnAusgabe) {
            newIntent.putExtra(getString(R.string.key_show_ein_or_ausgabe), false);
        }

        MainActivity.this.startActivity(newIntent);
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
                mCostAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(mRecyclerView);

    }

    private void updateAndSetCostAdapter() {

        if (mCostAdapter != null)
            mCostAdapter.stopListening();


        mCostAdapter = new CostAdapter(MainActivity.this, mViewModel.getFirebaseRecyclerOptions());
        mCostAdapter.setEmptyView(mEmptyTextView);
        mRecyclerView.setAdapter(mCostAdapter);

        mCostAdapter.startListening();
    }


    @Override
    public void Notify(String key, String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sie haben eine Einladung von '" + email + "' erhalten.\n Wollen sie der Gruppe beitreten?");
        builder.setTitle(getString(R.string.invitation));
        builder.setNegativeButton(getString(R.string.nein), (dialog, which) -> {
            FirebaseHelper helper = FirebaseHelper.getInstance();
            helper.declineGroupInvitation(key);

        });
        builder.setPositiveButton(getString(R.string.ja), (dialog, which) -> {
            FirebaseHelper helper = FirebaseHelper.getInstance();
            helper.followGroupInvitation(key);

            SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();

            Gson gson = new Gson();
            String json = gson.toJson(FirebaseHelper.mCurrentUser);
            edt.remove(Constants.SHARED_PREF_KEY_USER_DATABASE);
            edt.putString(Constants.SHARED_PREF_KEY_USER_DATABASE, json);
            edt.apply();


        });
        builder.show();
    }

    @Override
    public void Notify(CostSum costSum) {

        if (costSum == null){
            costSum=new CostSum();
            costSum.setCurrentValue(0);
            costSum.setFutureValue(0);
        }


        mViewModel.setCostSum(costSum);

        initSumTextView(costSum);
    }

    private void initSumTextView(CostSum costSum) {
        String color;
        if (costSum.getCurrentValue() >= 0) {
            color = Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary) & 0x00ffffff);
        } else {
            color = Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.buttonRed) & 0x00ffffff);
        }

        String actSum = getColoredSpanned(String.format(Locale.getDefault(), Constants.DOUBLE_FORMAT_TWO_DECIMAL, costSum.getCurrentValue()), color);
        String futureSum = getColoredSpanned(String.format(Locale.getDefault(), Constants.DOUBLE_FORMAT_TWO_DECIMAL, costSum.getCurrentValue() + costSum.getFutureValue()), Integer.toHexString(ContextCompat.getColor(getApplicationContext(), R.color.lightGrey) & 0x00ffffff));

        mSumTextView.setText(Html.fromHtml(actSum + " / " + futureSum));
    }
}
