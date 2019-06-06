package com.example.easyflow.interfaces;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.easyflow.models.Cost;
import com.example.easyflow.models.CostSum;
import com.example.easyflow.models.StateAccount;
import com.example.easyflow.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    private static DatabaseReference mDbRefCost;
    private static DatabaseReference mDbRefCostSum;
    private static FirebaseDatabase mDatabase;
    private static Date mStartDate;
    private static Date mEndDate;
    private static SimpleDateFormat mSimpleDateFormat;
    private static String mKeyAccount;
    public static User mCurrentUser;
    private static NotifyEventHandlerDouble mListener;


    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        instance = new FirebaseHelper();
        mDatabase = FirebaseDatabase.getInstance();
        mDbRefCost = mDatabase.getReference("costs/");
        mDbRefCostSum = mDatabase.getReference("costs-sum/");


        // Set Start and Enddate for Database Queries
        mSimpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        mStartDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mEndDate = calendar.getTime();
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

        String keyBankAccount = mDbRefCost.push().getKey();
        String keyCash = mDbRefCost.push().getKey();

        User user = new User();
        user.setUserId(keyUser);
        user.setEmail(email);
        user.setPassword(password);
        user.setBankAccountId(keyBankAccount);
        user.setCashId(keyCash);


        refUsers.child(keyUser).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d(Constants.TAG, "User saved successfully"))
                .addOnFailureListener(e -> Log.d(Constants.TAG, "Failed saving User"));

        mCurrentUser = user;
        return user;
    }

    public void addCost(Cost cost) {
        addCost(cost, mKeyAccount);
    }

    public void addCost(Cost cost, StateAccount account) {
        addCost(cost, getKeyAccountString(account));
    }

    public void addCost(Cost cost, String keyAccount) {

        String key = mDbRefCost.child(keyAccount).push().getKey();

        Map<String, Object> costValues = cost.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, costValues);

        mDbRefCost.child(keyAccount).updateChildren(childUpdates);

        if (cost.getDate().before(mStartDate))
            return;

        // Update Sum over all Costs for this month
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CostSum cur = dataSnapshot.getValue(CostSum.class);
                double newValue = cost.getValue();
                String newDateString = cost.getDateString();

                if (cur != null) {
                    if (!cur.getDateLastUpdated().before(mStartDate)) {
                        newValue += cur.getValue();
                    }
                    dataSnapshot.getRef().child("value").setValue(newValue);
                    dataSnapshot.getRef().child("dateLastUpdated").setValue(newDateString);
                } else {
                    CostSum newCostSum = new CostSum();
                    newCostSum.setDateLastUpdated(newDateString);
                    newCostSum.setValue(newValue);

                    Map<String, Object> costSumValues = newCostSum.toMap();
                    dataSnapshot.getRef().updateChildren(costSumValues);
                }

                if (mListener != null&& mKeyAccount.equals(keyAccount))
                    mListener.Notify(newValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDbRefCostSum.child(keyAccount).addListenerForSingleValueEvent(valueEventListener);


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


    public Query getQuery() {
        return mDbRefCost.child(mKeyAccount).orderByChild("date").startAt(mSimpleDateFormat.format(mStartDate)).endAt(mSimpleDateFormat.format(mEndDate));
    }


    public void createGroup() {
        String key = mDbRefCost.push().getKey();
        mCurrentUser.setGroupId(key);


        // Reference to users
        DatabaseReference refUser = mDatabase.getReference("users/" + mCurrentUser.getUserId());

        refUser.child("groupId").setValue(key)
                .addOnSuccessListener(aVoid -> Log.d(Constants.TAG, "GroupId saved successfully"))
                .addOnFailureListener(e -> Log.d(Constants.TAG, "Failed saving GroupId"));

    }

    void deleteCost(DataSnapshot snapshot) {
        Cost cost = snapshot.getValue(Cost.class);
        snapshot.getRef().removeValue();

        if(mListener==null)
            return;

        // Update Sum over all Costs for this month
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double newValue=cost.getValue();

                CostSum cur = dataSnapshot.getValue(CostSum.class);
                if (cur != null) {
                    newValue = cur.getValue() -newValue;

                    dataSnapshot.getRef().child("value").setValue(newValue);
                } else {
                    CostSum newCostSum = new CostSum();
                    newCostSum.setDateLastUpdated(cost.getDateString());
                    newCostSum.setValue(newValue);

                    Map<String, Object> costSumValues = newCostSum.toMap();
                    dataSnapshot.getRef().updateChildren(costSumValues);
                }

                    mListener.Notify(newValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDbRefCostSum.child(mKeyAccount).addListenerForSingleValueEvent(valueEventListener);

    }

    public void getActualAccountSum() {
        if (mListener == null)
            return;


        // Get Sum over all Costs for this month
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CostSum cur = dataSnapshot.getValue(CostSum.class);
                if (cur != null) {
                    mListener.Notify(cur.getValue());
                } else {
                    mListener.Notify(0.0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDbRefCostSum.child(mKeyAccount).addListenerForSingleValueEvent(valueEventListener);

    }

    public void setListener(NotifyEventHandlerDouble listener) {
        mListener = listener;
    }

    public static void setKeyAccount(StateAccount stateAccount) {
        mKeyAccount = getKeyAccountString(stateAccount);
    }

    private static String getKeyAccountString(StateAccount stateAccount) {
        switch (stateAccount) {
            case Group:
                return mCurrentUser.getGroupId();
            case BankAccount:
                return mCurrentUser.getBankAccountId();
            case Cash:
            default:
                return mCurrentUser.getCashId();
        }
    }
}
