package com.example.easyflow.interfaces;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.easyflow.R;
import com.example.easyflow.activities.MainActivity;
import com.example.easyflow.activities.SplashActivity;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.Cost;
import com.example.easyflow.models.StateAccount;
import com.example.easyflow.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    private static DatabaseReference mDbRefCost;
    private static FirebaseDatabase mDatabase;
    private static String mStartDate;
    private static String mEndDate;
    public static User mCurrentUser;


    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        instance = new FirebaseHelper();
        mDatabase = FirebaseDatabase.getInstance();


        // Set Start and Enddate for Database Queries
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE);

        Calendar calendarStart = GregorianCalendar.getInstance();
        calendarStart.setTime(new Date());
        calendarStart.set(Calendar.DAY_OF_MONTH, 1);

        mStartDate = sdf.format(calendarStart.getTime());
        calendarStart.set(Calendar.DAY_OF_MONTH, calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH));
        mEndDate = sdf.format(calendarStart.getTime());
    }

    public static FirebaseHelper getInstance() {
        return instance;
    }

    public static void checkUser(User currentUser) {
        mCurrentUser = currentUser;
        checkUserDataChanged();

    }

    public User addUser(String email, String password) {


        // Reference to users
        DatabaseReference refUsers = mDatabase.getReference("users");

        String keyUser = refUsers.push().getKey();

        // Reference to Cost
        if (mDbRefCost == null) {
            initDbRefCost(keyUser);
        }

        String keyBankAccount = mDbRefCost.push().getKey();
        String keyCash = mDbRefCost.push().getKey();

        User user = new User();
        user.setUserId(keyUser);
        user.setEmail(email);
        user.setPassword(password);
        user.setBankAccount(keyBankAccount);
        user.setCash(keyCash);


        refUsers.child(keyUser).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d(Constants.TAG, "User saved successfully"))
                .addOnFailureListener(e -> Log.d(Constants.TAG, "Failed saving User"));

        mCurrentUser = user;
        return user;
    }

    public void addCost(Cost cost) {
        if (mDbRefCost == null) {
            initDbRefCost(null);
        }

        String keyAccount;

        switch (MainActivity.stateAccount) {
            case cash:
                keyAccount = mCurrentUser.getCash();
                break;
            case group:
                keyAccount = mCurrentUser.getGroup();
                break;
            case bankAccount:
                keyAccount = mCurrentUser.getBankAccount();
                break;
            default:
                keyAccount = mCurrentUser.getCash();
        }


        String key = mDbRefCost.child(keyAccount).push().getKey();
        Map<String, Object> costValues = cost.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, costValues);

        mDbRefCost.child(keyAccount).updateChildren(childUpdates);
    }

    private static void checkUserDataChanged() {

        // Get Database Reference
        DatabaseReference refUser = mDatabase.getReference("users/" + mCurrentUser.getUserId());

        // Read from the database
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User cur = dataSnapshot.getValue(User.class);
                if (cur != null) {
                    String email = mCurrentUser.getEmail();
                    String password = mCurrentUser.getPassword();

                    if (!cur.getEmail().equals(email)) {
                        mCurrentUser.setEmail(email);
                    }
                    if (!cur.getPassword().equals(password)) {
                        mCurrentUser.setPassword(password);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        refUser.addListenerForSingleValueEvent(valueEventListener);

    }

    public void setLiveDataListener() {
        if (mDbRefCost == null) {
            initDbRefCost(null);
        }

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE);

        Calendar calendarStart = GregorianCalendar.getInstance();
        calendarStart.setTime(new Date());
        calendarStart.set(Calendar.DAY_OF_MONTH, 1);

        String startDate = sdf.format(calendarStart.getTime());
        calendarStart.set(Calendar.DAY_OF_MONTH, calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = sdf.format(calendarStart.getTime());

//todo finanzmonat definieren ermöglichen

        Query currentCostsQuery = mDbRefCost.orderByChild("date").startAt(startDate).endAt(endDate);
        currentCostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                GenericTypeIndicator<HashMap<String, Cost>> t = new GenericTypeIndicator<HashMap<String, Cost>>() {
                };

                List<Cost> muteModelList;
                HashMap<String, Cost> hashMap = dataSnapshot.getValue(t);

                if (hashMap == null) {
                    return;
                }

                muteModelList = new ArrayList<Cost>(hashMap.values()) {
                };

                for (Cost muteModel : muteModelList) {
                    Category c = muteModel.getCategory();
                    Log.d(Constants.TAG, muteModel.getCategory().getName() + " " + muteModel.getValue());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initDbRefCost(String userKey) {
        if (userKey != null) {
            mDbRefCost = mDatabase.getReference(
                    "costs/" + userKey);
        } else {
            mDbRefCost = mDatabase.getReference(
                    "costs/" + mCurrentUser.getUserId());
        }
    }

    public Query getQuery(StateAccount stateAccount) {
        if (mDbRefCost == null) {
            initDbRefCost(null);
        }


        String childKey;

        switch (stateAccount) {
            case bankAccount:
                childKey = mCurrentUser.getBankAccount();
                break;
            case group:
                childKey = mCurrentUser.getGroup();
                break;
            case cash:
            default:
                childKey = mCurrentUser.getCash();
        }

        return  mDbRefCost.child(childKey).orderByChild("date").startAt(mStartDate).endAt(mEndDate);
    }
}
