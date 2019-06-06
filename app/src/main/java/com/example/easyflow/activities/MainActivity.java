package com.example.easyflow.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.interfaces.CostAdapter;
import com.example.easyflow.interfaces.FirebaseHelper;
import com.example.easyflow.interfaces.GlobalApplication;
import com.example.easyflow.interfaces.NotifyEventHandlerDouble;
import com.example.easyflow.interfaces.NotifyEventHandlerStrinMap;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.StateAccount;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NotifyEventHandlerDouble, NotifyEventHandlerStrinMap {

    public static List<Category> categoriesIncome = new ArrayList<>();
    public static List<Category> categoriesCost = new ArrayList<>();
    public static Category categoryTransferFrom;
    public static Category categoryTransferTo;
    public static StateAccount stateAccount = StateAccount.Cash;

    private CostAdapter mCostAdapter;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TextView mSumTextView;
    private MenuItem mMenuItemGroup;
    private MenuItem mMenuItemSelectAccount;

    static {
        loadCategories();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        /*
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
*/

        mSumTextView = findViewById(R.id.tvSummary);

        FirebaseHelper.setKeyAccount(stateAccount);
        FirebaseHelper helper=new FirebaseHelper();
        helper.setListener(this,this);


        // Initialize and show LiveData for the Main Content
        setUpRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCostAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mCostAdapter.stopListening();
    }

    @Override
    public void Notify(double sumActualAccount) {
        if (sumActualAccount >= 0) {
            mSumTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mSumTextView.setTextColor(getResources().getColor(R.color.buttonRed));
        }
        mSumTextView.setText(String.format(Constants.DOUBLE_FORMAT_TWO_DECIMAL, sumActualAccount));

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
        mMenuItemGroup=menu.getItem(2);
        mMenuItemSelectAccount=menu.getItem(1);
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
            setActionBarHeadItem(StateAccount.BankAccount);
            return true;
        } else if (id == R.id.action_account_cash) {
            setActionBarHeadItem(StateAccount.Cash);
            return true;
        } else if (id == R.id.action_account_group) {
            if (FirebaseHelper.mCurrentUser.getGroupId() != null) {
                setActionBarHeadItem(StateAccount.Group);
                return true;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.alertdialog_keine_gruppe_message));
            builder.setTitle(getString(R.string.alertdialog_keine_gruppe_title));
            builder.setNegativeButton(getString(R.string.alertdialog_keine_gruppe_negative_button), null);
            builder.setPositiveButton(getString(R.string.alertdialog_keine_gruppe_positive_button), (dialog, which) -> {
                FirebaseHelper helper = FirebaseHelper.getInstance();
                helper.createGroup();

                SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = pref.edit();

                Gson gson = new Gson();
                String json = gson.toJson(FirebaseHelper.mCurrentUser);
                edt.remove(Constants.SHARED_PREF_KEY_USER_DATABASE);
                edt.putString(Constants.SHARED_PREF_KEY_USER_DATABASE, json);
                edt.commit();


                setActionBarHeadItem(StateAccount.Group);

            });
            builder.show();

        }

        return super.onOptionsItemSelected(item);
    }

    private static void loadCategories() {
        Context context = GlobalApplication.getAppContext();

        // Kategorien für Ausgaben.
        categoriesCost.add(new Category(context.getString(R.string.categoriy_reisen), R.drawable.ic_airplane_brown_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_auto), R.drawable.ic_car_darkblue_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_kommunikation), R.drawable.ic_communication_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_eating), R.drawable.ic_eating_green_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_lebensmittel), R.drawable.ic_food_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_gesundheit), R.drawable.ic_health_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_haus), R.drawable.ic_home_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_taxi), R.drawable.ic_local_taxi_black_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_geschenke), R.drawable.ic_present_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_sport), R.drawable.ic_sport_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoriy_verkehrsmittel), R.drawable.ic_traffic_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_bildung), R.drawable.ic_school_black_24dp));

        // Kategorien für Einkommen.
        categoriesIncome.add(new Category(context.getString(R.string.categoriy_einzahlungen), R.drawable.ic_einzahlungen_24dp));
        categoriesIncome.add(new Category(context.getString(R.string.categoriy_ersparnisse), R.drawable.ic_trending_up_black_24dp));
        categoriesIncome.add(new Category(context.getString(R.string.categoriy_gehalt), R.drawable.ic_gehalt_24dp));

        // Kategorien für Transfer
        categoryTransferFrom = new Category(context.getString(R.string.ueberweisung), R.drawable.ic_swap_horiz_red_32dp);
        categoryTransferTo = new Category(context.getString(R.string.ueberweisung), R.drawable.ic_swap_horiz_green_32dp);

    }

    private void setActionBarHeadItem(StateAccount newSelectedAccount) {
        stateAccount = newSelectedAccount;

        switch (newSelectedAccount) {
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

        FirebaseHelper.setKeyAccount(newSelectedAccount);
        updateAndSetCostAdapter();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
        mRecyclerView = findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

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


        updateAndSetCostAdapter();

    }

    private void updateAndSetCostAdapter() {
        FirebaseHelper helper = FirebaseHelper.getInstance();
        helper.getActualAccountSum();

        Query query = helper.getQuery();

        FirebaseRecyclerOptions<DataSnapshot> options = new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();

        if (mCostAdapter != null)
            mCostAdapter.stopListening();

        mCostAdapter = new CostAdapter(MainActivity.this, options);
        mRecyclerView.setAdapter(mCostAdapter);

        mCostAdapter.startListening();
    }


    @Override
    public void Notify(String key,String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sie haben eine Einladung von '"+email+"' erhalten.\n Wollen sie der Gruppe beitreten?");
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
            edt.commit();


        });
        builder.show();
    }
}
