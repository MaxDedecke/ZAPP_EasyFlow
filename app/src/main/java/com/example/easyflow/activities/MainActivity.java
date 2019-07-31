package com.example.easyflow.activities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
    private Menu mNavigationViewMenu;

    // Constants for the notification actions buttons.
    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.easyflow.easyflow.ACTION_UPDATE_NOTIFICATION";
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;


    private NotificationManager mNotifyManager;

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
        createNotificationChannel();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mNavigationViewMenu = navigationView.getMenu();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        FirebaseHelper helper = new FirebaseHelper();
        helper.setListener(this, this);

        if (!TextUtils.isEmpty(FirebaseHelper.mCurrentUser.getGroupId()))
            mNavigationViewMenu.findItem(R.id.nav_settings_group).setTitle(R.string.menu_settings_group);
        else
            helper.initInvitationsForGroup();


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

    private void showAlertDialogNoNotifications() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alertdialog_keine_benachrichtigungen_message));
        builder.setTitle(getString(R.string.alertdialog_keine_benachrichtigungen_title));
        builder.setNegativeButton(getString(R.string.alertdialog_keine_benachrichtigungen_negative_button), (dialog, which) -> {
            switch(mViewModel.getStateAccount()) {
                case Cash:
                    ((NavigationView) findViewById(R.id.nav_view)).getMenu().getItem(0).setChecked(true);
                    break;
                case BankAccount:
                    ((NavigationView) findViewById(R.id.nav_view)).getMenu().getItem(1).setChecked(true);
                    break;
                case Group:
                    ((NavigationView) findViewById(R.id.nav_view)).getMenu().getItem(2).setChecked(true);
            }
        });

        builder.setPositiveButton(getString(R.string.alertdialog_keine_benachrichtigungen_positive_button), (dialog, which) -> {
            FirebaseHelper helper = FirebaseHelper.getInstance();
            helper.createNotification();

            GlobalApplication.saveUserInSharedPreferences(FirebaseHelper.mCurrentUser);

        });
        builder.show();
    }

    private void showAlertDialogCreateGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alertdialog_keine_gruppe_message));
        builder.setTitle(getString(R.string.alertdialog_keine_gruppe_title));
        builder.setNegativeButton(getString(R.string.alertdialog_keine_gruppe_negative_button), (dialog, which) -> {
            switch (mViewModel.getStateAccount()) {
                case Cash:
                    ((NavigationView) findViewById(R.id.nav_view)).getMenu().getItem(0).setChecked(true);
                    break;
                case BankAccount:
                    ((NavigationView) findViewById(R.id.nav_view)).getMenu().getItem(1).setChecked(true);
                    break;
            }

        });
        builder.setPositiveButton(getString(R.string.alertdialog_keine_gruppe_positive_button), (dialog, which) -> {
            FirebaseHelper helper = FirebaseHelper.getInstance();
            helper.createGroup();

            GlobalApplication.saveUserInSharedPreferences(FirebaseHelper.mCurrentUser);


            mNavigationViewMenu.findItem(R.id.nav_settings_group).setTitle(R.string.menu_settings_group);

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
            } else {
                mViewModel.setStateAccount(StateAccount.Group);
                setActionBarHeadItem();
            }

        } else if(id == R.id.nav_show_notifications){
            if(FirebaseHelper.mCurrentUser.getNotificationId() == null) {
                showAlertDialogNoNotifications();
            } else {
                Intent intent = new Intent(this, NotificationsActivity.class);
                startActivity(intent);
            }

        }else if (id == R.id.nav_settings_group) {
            if (FirebaseHelper.mCurrentUser.getGroupId() == null) {
                showAlertDialogCreateGroup();
            } else {
                Intent intent = new Intent(this, GroupSettingsActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_edit_recurring_costs) {
            Intent intent = new Intent(this, EditRecurringCostsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            //todo show settings
            // sammeln von ideen für drawer
        /*
            Gruppe gründen /Gruppen einstellungen
            dark mode
            monats anfang
            einträge exportieren
            einträge löschen

         */

            Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();

        }
        else if(id == R.id.nav_show_notifications) {

            //Activity verknüpfen hier (per Intent)

            Toast.makeText(this, "nicht implementiert", Toast.LENGTH_SHORT).show();
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
                mCostAdapter.onItemRemove(viewHolder, mRecyclerView);
            }
        }).attachToRecyclerView(mRecyclerView);

    }

    private void updateAndSetCostAdapter() {

        if (mCostAdapter != null)
            mCostAdapter.stopListening();


        mCostAdapter = new CostAdapter(MainActivity.this, mViewModel.getFirebaseRecyclerOptions(), false);
        mCostAdapter.setEmptyView(mEmptyTextView);
        mCostAdapter.startListening();
        mRecyclerView.setAdapter(mCostAdapter);

    }

    @Override
    public void Notify(String key, String email) {
        if (GlobalApplication.isIsInForeGround()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Du hast eine Einladung von '" + email + "' erhalten.\n Wollen sie der Gruppe beitreten?");
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
        } else {

            // Build the notification with all of the parameters using helper
            // method.
            NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
            notifyBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("Du hast eine Einladung von '" + email + "' erhalten."));

            // Deliver the notification.
            mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());



        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {

        // Set up the pending intent that is delivered when the notification
        // is clicked.
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);



        // Sets up the pending intent to update the notification.
        // Corresponds to a press of the Update Me! button.
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this,
                NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);

        // Build the notification with all of the parameters.
        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Einladung")
                .setSmallIcon(R.drawable.ic_start_logo)
                .setContentIntent(updatePendingIntent)
                .setContentText("Du hast eine Gruppeneinladung erhalten.")
                .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);


        return notifyBuilder;
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.colorPrimaryDark);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    (getString(R.string.notification_channel_description));

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void Notify(CostSum costSum) {

        if (costSum == null) {
            costSum = new CostSum();
            costSum.setCurrentValue(0);
            costSum.setFutureValue(0);
        }

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
