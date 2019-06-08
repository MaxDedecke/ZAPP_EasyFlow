package com.example.easyflow.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class GlobalApplication extends Application {

    public static List<Category> categoriesIncome = new ArrayList<>();
    public static List<Category> categoriesCost = new ArrayList<>();
    public static Category categoryTransferFrom;
    public static Category categoryTransferTo;
    private static Context appContext;


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        loadCategories();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void saveUserInSharedPreferences(User user){
        SharedPreferences pref = appContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);
        edt.remove(Constants.SHARED_PREF_KEY_USER_DATABASE);
        edt.putString(Constants.SHARED_PREF_KEY_USER_DATABASE, json);
        edt.commit();
    }

    public static void saveStartUpInSharedPreferences(User user,boolean started) {

        SharedPreferences pref = appContext.getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean(Constants.SHARED_PREF_KEY_ACTIVITY_EXECUTED, started);

        Gson gson = new Gson();
        String json = gson.toJson(user);
        edt.putString(Constants.SHARED_PREF_KEY_USER_DATABASE, json);

        edt.commit();
    }



    private static void loadCategories() {
        Context context = GlobalApplication.getAppContext();

        // Kategorien für Ausgaben.
        categoriesCost.add(new Category(context.getString(R.string.category_reisen), R.drawable.ic_airplane_brown_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_auto), R.drawable.ic_car_darkblue_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_kommunikation), R.drawable.ic_communication_24dp));
        categoriesCost.add(new Category(context.getString(R.string.categoiy_eating), R.drawable.ic_eating_green_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_lebensmittel), R.drawable.ic_food_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_gesundheit), R.drawable.ic_health_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_haus), R.drawable.ic_home_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_taxi), R.drawable.ic_local_taxi_black_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_geschenke), R.drawable.ic_present_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_sport), R.drawable.ic_sport_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_verkehrsmittel), R.drawable.ic_traffic_24dp));
        categoriesCost.add(new Category(context.getString(R.string.category_bildung), R.drawable.ic_school_black_24dp));

        // Kategorien für Einkommen.
        categoriesIncome.add(new Category(context.getString(R.string.category_einzahlungen), R.drawable.ic_einzahlungen_24dp));
        categoriesIncome.add(new Category(context.getString(R.string.category_ersparnisse), R.drawable.ic_trending_up_black_24dp));
        categoriesIncome.add(new Category(context.getString(R.string.category_gehalt), R.drawable.ic_gehalt_24dp));

        // Kategorien für Transfer
        categoryTransferFrom = new Category(context.getString(R.string.ueberweisung), R.drawable.ic_swap_horiz_red_32dp);
        categoryTransferTo = new Category(context.getString(R.string.ueberweisung), R.drawable.ic_swap_horiz_green_32dp);

    }

}
