package com.example.easyflow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.interfaces.FirebaseHelper;
import com.example.easyflow.interfaces.ViewHolder;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.Cost;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static List<Category> categoriesIncome=new ArrayList<>();
    public static List<Category> categoriesCost=new ArrayList<>();




    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //todo implement livedata
        FirebaseHelper helper= FirebaseHelper.getInstance();
        //helper.setLiveDataListener();
        //AdapterRecyclerViewOverview adapter = new AdapterRecyclerViewOverview(this, items);
        //mRecyclerView.setAdapter(adapter);

        recyclerView = findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();



        loadCategories();
    }

    private void loadCategories() {
        // Kategorien für Ausgaben.
        categoriesCost.add(new Category(getString(R.string.categoriy_reisen),R.drawable.ic_airplane_brown_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_auto),R.drawable.ic_car_darkblue_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_kommunikation),R.drawable.ic_communication_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_eating),R.drawable.ic_eating_green_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_lebensmittel),R.drawable.ic_food_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_gesundheit),R.drawable.ic_health_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_haus),R.drawable.ic_home_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_taxi),R.drawable.ic_local_taxi_black_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_geschenke),R.drawable.ic_present_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_sport),R.drawable.ic_sport_24dp));
        categoriesCost.add(new Category(getString(R.string.categoriy_verkehrsmittel),R.drawable.ic_traffic_24dp));
        categoriesCost.add(new Category(getString(R.string.category_bildung),R.drawable.ic_school_black_24dp));


        // Kategorien für Einkommen.
        categoriesIncome.add(new Category(getString(R.string.categoriy_einzahlungen),R.drawable.ic_einzahlungen_24dp));
        categoriesIncome.add(new Category(getString(R.string.categoriy_ersparnisse),R.drawable.ic_trending_up_black_24dp));
        categoriesIncome.add(new Category(getString(R.string.categoriy_gehalt),R.drawable.ic_gehalt_24dp));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
        int viewId=view.getId();

        Intent newIntent=new Intent(MainActivity.this, EinAusgabeActivity.class);

        if(viewId==R.id.btnEinnahme){
            newIntent.putExtra(getString(R.string.key_show_ein_or_ausgabe),true);
        }
        else if(viewId==R.id.btnAusgabe) {
            newIntent.putExtra(getString(R.string.key_show_ein_or_ausgabe), false);
        }

        MainActivity.this.startActivity(newIntent);

    }








    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    private void fetch() {

        Query query = FirebaseHelper.getInstance().getQuery();

        FirebaseRecyclerOptions<Cost> options =
                new FirebaseRecyclerOptions.Builder<Cost>()
                        .setQuery(query, new SnapshotParser<Cost>() {
                            @NonNull
                            @Override
                            public Cost parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return snapshot.getValue(Cost.class);
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Cost, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sample_list_item_view, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, Cost cost) {
                holder.setTxtTitle(cost.getCategory().getName());
                holder.setTxtDesc(cost.getValue()+" - "+new SimpleDateFormat(Constants.DATE_FORMAT_WEEKDAY).format(cost.getDate()));
                //todo set sum of costs holder.setTxtDesc(cost.getDate().toString());

                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }




}
