package com.example.easyflow.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.CostAdapter;
import com.example.easyflow.interfaces.FirebaseHelper;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.Cost;
import com.example.easyflow.models.StateAccount;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static List<Category> categoriesIncome = new ArrayList<>();
    public static List<Category> categoriesCost = new ArrayList<>();
    public static Category categoryTransferFrom;
    public static Category categoryTransferTo;
    public static StateAccount stateAccount=StateAccount.Cash;

    private CostAdapter mCostAdapter;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);




        loadCategories();
        // Initialize and show LiveData for the Main Content
        setUpRecyclerView();
    }

    private void loadCategories() {
        // Kategorien für Ausgaben.
        categoriesCost.add(new Category(getString(R.string.categoriy_reisen), R.drawable.ic_airplane_brown_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_auto), R.drawable.ic_car_darkblue_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_kommunikation), R.drawable.ic_communication_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_eating), R.drawable.ic_eating_green_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_lebensmittel), R.drawable.ic_food_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_gesundheit), R.drawable.ic_health_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_haus), R.drawable.ic_home_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_taxi), R.drawable.ic_local_taxi_black_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_geschenke), R.drawable.ic_present_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_sport), R.drawable.ic_sport_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_verkehrsmittel), R.drawable.ic_traffic_24dp));
        categoriesCost.add(new Category(getString(R.string.category_bildung), R.drawable.ic_school_black_24dp));


        // Kategorien für Einkommen.
        categoriesIncome.add(new Category(getString(R.string.categoriy_einzahlungen), R.drawable.ic_einzahlungen_24dp));
        categoriesIncome.add(new Category(getString(R.string.categoriy_ersparnisse), R.drawable.ic_trending_up_black_24dp));
        categoriesIncome.add(new Category(getString(R.string.categoriy_gehalt), R.drawable.ic_gehalt_24dp));

        // Kategorien für Transfer
        categoryTransferFrom=new Category("Überweisung",R.drawable.ic_swap_horiz_red_32dp);
        categoryTransferTo=new Category("Überweisung",R.drawable.ic_swap_horiz_green_32dp);
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
        }
        else if(id==R.id.action_booking){
            Intent intent = new Intent(this, BookCostActivity.class);
            startActivity(intent);
            return true;
        }


        if (id == R.id.action_account_second||id == R.id.action_account_third) {
            MenuItem head=mToolbar.getMenu().getItem(1);
            String itemTitle=item.getTitle().toString();
            String headTitle=head.getTitle().toString();


            // Set head title and icon
            if(itemTitle.equals(getString(R.string.actionbar_item_bank))){
                setActionBarItem(head,R.string.actionbar_item_bank,R.drawable.ic_gehalt_white_32dp);
                databaseSelectedAccountHasChanged(StateAccount.BankAccount);

            }else if(itemTitle.equals(getString(R.string.actionbar_item_bargeld))){
                setActionBarItem(head,R.string.actionbar_item_bargeld,R.drawable.ic_einzahlungen_white_32_dp);
                databaseSelectedAccountHasChanged(StateAccount.Cash);

            }else if (itemTitle.equals(getString(R.string.actionbar_item_wg))){
                setActionBarItem(head,R.string.actionbar_item_wg,R.drawable.ic_group_white_32dp);

                if(FirebaseHelper.mCurrentUser.getGroup()!=null) {
                    databaseSelectedAccountHasChanged(StateAccount.Group);
                }
                else{
                    // todo form öffnen und fragen, ob man eine wg erstellen möchteS
                }
            }


            // Set item title and icon
            if(headTitle.equals(getString(R.string.actionbar_item_bank))){
                setActionBarItem(item,R.string.actionbar_item_bank,R.drawable.ic_gehalt_black_32dp);

            }else if(headTitle.equals(getString(R.string.actionbar_item_bargeld))){
                setActionBarItem(item,R.string.actionbar_item_bargeld,R.drawable.ic_einzahlungen_black_24dp);

            }else if (headTitle.equals(getString(R.string.actionbar_item_wg))){
                setActionBarItem(item,R.string.actionbar_item_wg,R.drawable.ic_group_black_32dp);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void databaseSelectedAccountHasChanged(StateAccount newSelectedAccount) {
        stateAccount=newSelectedAccount;
        updateAndSetCostAdapter();
    }

    private void setActionBarItem(MenuItem head, int stringId, int drawableId) {
        head.setTitle(getString(stringId));
        head.setIcon(ResourcesCompat.getDrawable(getResources(),drawableId,null));
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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


        updateAndSetCostAdapter();

    }

    private void updateAndSetCostAdapter(){

        Query query = FirebaseHelper.getInstance().getQuery(stateAccount);

        FirebaseRecyclerOptions<Cost> options = new FirebaseRecyclerOptions.Builder<Cost>()
                .setQuery(query, snapshot -> snapshot.getValue(Cost.class))
                .build();

        if(mCostAdapter!=null)
            mCostAdapter.stopListening();

        mCostAdapter=new CostAdapter(MainActivity.this,options);
        mRecyclerView.setAdapter(mCostAdapter);

        mCostAdapter.startListening();
    }


}
