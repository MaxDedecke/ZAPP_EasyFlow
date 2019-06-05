package com.example.easyflow.activities;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.example.easyflow.interfaces.NotifyEventHandler;
import com.example.easyflow.interfaces.NotifyEventHandlerDouble;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.StateAccount;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NotifyEventHandlerDouble {

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

        // Initialize and show LiveData for the Main Content
        setUpRecyclerView();
    }

    private static void loadCategories() {
        Context context=SplashActivity.getContext();

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
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
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
        }


        if (id != R.id.action_account_second && id != R.id.action_account_third) {
            return super.onOptionsItemSelected(item);
        }

        if (mMenuItemSelectAccount == null)
            mMenuItemSelectAccount = mToolbar.getMenu().getItem(1);
        if (mMenuItemGroup == null)
            mMenuItemGroup = mToolbar.getMenu().getItem(2);
        mMenuItemGroup.setVisible(false);


        String itemTitle = item.getTitle().toString();
        String headTitle = mMenuItemSelectAccount.getTitle().toString();


        // Set head title and icon
        if (itemTitle.equals(getString(R.string.actionbar_item_bank))) {
            setActionBarHeadItem(mMenuItemSelectAccount, R.string.actionbar_item_bank, R.drawable.ic_gehalt_white_32dp);
            setActionBarItem(item, headTitle);
            databaseSelectedAccountHasChanged(StateAccount.BankAccount);

        } else if (itemTitle.equals(getString(R.string.actionbar_item_bargeld))) {
            setActionBarHeadItem(mMenuItemSelectAccount, R.string.actionbar_item_bargeld, R.drawable.ic_einzahlungen_white_32_dp);
            setActionBarItem(item, headTitle);
            databaseSelectedAccountHasChanged(StateAccount.Cash);

        } else if (itemTitle.equals(getString(R.string.actionbar_item_wg))) {

            if (FirebaseHelper.mCurrentUser.getGroupId() != null) {
                setActionBarHeadItem(mMenuItemSelectAccount, R.string.actionbar_item_wg, R.drawable.ic_group_white_32dp);
                setActionBarItem(item, headTitle);
                mMenuItemGroup.setVisible(true);
                databaseSelectedAccountHasChanged(StateAccount.Group);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.alertdialog_keine_gruppe_message));
                builder.setTitle(getString(R.string.alertdialog_keine_gruppe_title));
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

                    setActionBarHeadItem(mMenuItemSelectAccount, R.string.actionbar_item_wg, R.drawable.ic_group_white_32dp);
                    setActionBarItem(item, headTitle);
                    mMenuItemGroup.setVisible(true);
                    databaseSelectedAccountHasChanged(StateAccount.Group);

                });
                builder.setNegativeButton(getString(R.string.alertdialog_keine_gruppe_negative_button), null);
                builder.show();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private void setActionBarItem(MenuItem item, String headTitle) {
        // Set item title and icon
        if (headTitle.equals(getString(R.string.actionbar_item_bank))) {
            setActionBarHeadItem(item, R.string.actionbar_item_bank, R.drawable.ic_gehalt_black_32dp);

        } else if (headTitle.equals(getString(R.string.actionbar_item_bargeld))) {
            setActionBarHeadItem(item, R.string.actionbar_item_bargeld, R.drawable.ic_einzahlungen_black_24dp);

        } else if (headTitle.equals(getString(R.string.actionbar_item_wg))) {
            setActionBarHeadItem(item, R.string.actionbar_item_wg, R.drawable.ic_group_black_32dp);
        }
    }


    private void setActionBarHeadItem(MenuItem head, int stringId, int drawableId) {
        head.setTitle(getString(stringId));
        head.setIcon(ResourcesCompat.getDrawable(getResources(), drawableId, null));
    }

    private void databaseSelectedAccountHasChanged(StateAccount newSelectedAccount) {
        stateAccount = newSelectedAccount;
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

    public void showMoveActivity(View view) {
        int viewId = view.getId();

        Intent newIntent = new Intent(MainActivity.this, EinAusgabeActivity.class);

        if (viewId == R.id.btnEinnahme) {
            newIntent.putExtra(getString(R.string.key_show_ein_or_ausgabe), true);
        } else if (viewId == R.id.btnAusgabe) {
            newIntent.putExtra(getString(R.string.key_show_ein_or_ausgabe), false);
        }

        MainActivity.this.startActivity(newIntent);

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
        helper.setListener(this);
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
    public void Notify(double sumActualAccount) {

        if (sumActualAccount >= 0) {
            mSumTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            mSumTextView.setTextColor(getResources().getColor(R.color.buttonRed));
        }
        mSumTextView.setText(String.format(Constants.DOUBLE_FORMAT_TWO_DECIMAL, sumActualAccount));

    }
}
