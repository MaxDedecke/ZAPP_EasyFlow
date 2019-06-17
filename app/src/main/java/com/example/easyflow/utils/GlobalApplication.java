package com.example.easyflow.utils;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.models.AccountData;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.StateAccount;
import com.example.easyflow.models.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GlobalApplication extends Application implements LifecycleObserver {

    private static List<Category> categoriesIncome = new ArrayList<>();
    private static List<Category> categoriesCost = new ArrayList<>();
    private static Category categoryTransferFrom;
    private static Category categoryTransferTo;
    private static Context appContext;


    private static boolean isInForeGround=false;

    public static List<Category> getCategoriesIncome() {
        return categoriesIncome;
    }

    public static List<Category> getCategoriesCost() {
        return categoriesCost;
    }

    public static Category getCategoryTransferFrom() {
        return categoryTransferFrom;
    }

    public static Category getCategoryTransferTo() {
        return categoryTransferTo;
    }

    public static ArrayList<AccountData> getListAccounts() {

        ArrayList<AccountData> listAccounts = new ArrayList<>();
        listAccounts.add(new AccountData(StateAccount.Cash, "Bargeld", R.drawable.ic_cash_new_black));
        listAccounts.add(new AccountData(StateAccount.BankAccount, "Bank", R.drawable.ic_bank_account_new_black));
        if (FirebaseHelper.mCurrentUser != null && FirebaseHelper.mCurrentUser.getGroupId() != null)
            listAccounts.add(new AccountData(StateAccount.Group, "WG", R.drawable.ic_group_black_32dp));

        return listAccounts;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        loadCategories();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public static Context getAppContext() {
        return appContext;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches() && !TextUtils.isEmpty(target);
    }

    public static void saveUserInSharedPreferences(User user) {
        SharedPreferences pref = getAppContext().getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);
        edt.remove(Constants.SHARED_PREF_KEY_USER_DATABASE);
        edt.putString(Constants.SHARED_PREF_KEY_USER_DATABASE, json);
        edt.apply();
    }

    public static void saveStartUpInSharedPreferences(User user, boolean started) {

        SharedPreferences pref = getAppContext().getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean(Constants.SHARED_PREF_KEY_ACTIVITY_EXECUTED, started);

        Gson gson = new Gson();
        String json = gson.toJson(user);
        edt.putString(Constants.SHARED_PREF_KEY_USER_DATABASE, json);

        edt.apply();
    }

    private static void loadCategories() {
        Context context = GlobalApplication.getAppContext();

        // Kategorien für Ausgaben.
        getCategoriesCost().add(new Category(context.getString(R.string.category_auto), R.drawable.ic_car_darkblue_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_kommunikation), R.drawable.ic_communication_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_eating), R.drawable.ic_eating_green_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_lebensmittel), R.drawable.ic_food_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_gesundheit), R.drawable.ic_health_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_haus), R.drawable.ic_home_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_taxi), R.drawable.ic_local_taxi_black_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_geschenke), R.drawable.ic_present_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_sport), R.drawable.ic_sport_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_verkehrsmittel), R.drawable.ic_traffic_24dp));
        getCategoriesCost().add(new Category(context.getString(R.string.category_haustiere), R.drawable.ic_haustier));
        getCategoriesCost().add(new Category(context.getString(R.string.category_hygieneartikel), R.drawable.ic_hygieneartikel));
        getCategoriesCost().add(new Category(context.getString(R.string.category_kleidung), R.drawable.ic_kleidung));
        getCategoriesCost().add(new Category(context.getString(R.string.category_rechnungen), R.drawable.ic_rechnungen));
        getCategoriesCost().add(new Category(context.getString(R.string.category_unterhaltung), R.drawable.ic_unterhaltung));
//Haustiere,hygieneartikel,Kleidung, Rechnungen, unterhaltung


        // Kategorien für Einkommen.
        getCategoriesIncome().add(new Category(context.getString(R.string.category_einzahlungen), R.drawable.ic_einzahlungen_24dp));
        getCategoriesIncome().add(new Category(context.getString(R.string.category_ersparnisse), R.drawable.ic_trending_up_black_24dp));
        getCategoriesIncome().add(new Category(context.getString(R.string.category_gehalt), R.drawable.ic_gehalt_24dp));

        // Kategorien für Transfer
        categoryTransferFrom = new Category(context.getString(R.string.ueberweisung), R.drawable.ic_swap_horiz_red_32dp);
        categoryTransferTo = new Category(context.getString(R.string.ueberweisung), R.drawable.ic_swap_horiz_green_32dp);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        // app moved to foreground
        isInForeGround=true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        // app moved to background
        isInForeGround=false;
    }


    public static boolean isIsInForeGround() {
        return isInForeGround;
    }
}
