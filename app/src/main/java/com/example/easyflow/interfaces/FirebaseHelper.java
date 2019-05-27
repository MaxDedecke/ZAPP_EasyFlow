package com.example.easyflow.interfaces;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.easyflow.activities.SplashActivity;
import com.example.easyflow.models.Cost;
import com.example.easyflow.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    private static DatabaseReference mDbRefUser;
    private static DatabaseReference mDbRefCost;
    private static FirebaseDatabase mDatabase;

    private static final String TAG = "EasyFlow_Debug_Tag";

    static {
        instance=new FirebaseHelper();
    }

    public static FirebaseHelper getInstance(){


        mDatabase=FirebaseDatabase.getInstance();

        // Reference to users
        mDbRefUser=mDatabase.getReference("users");

        // Read from the database
        mDbRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                if(user!=null)
                    Log.d(TAG, "Value is: " + user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




        // Reference to costs

        // Reference to users
        mDbRefCost=mDatabase.getReference("costs");

        // Read from the database
        mDbRefCost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Cost cost = dataSnapshot.getValue(Cost.class);
                if(cost!=null) {
                    Log.d(TAG, "Value is: " + cost.getNote());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return instance;

    }

    public User addUser(String email, String password) {
        String key = mDbRefUser.push().getKey();

        User user = new User();
        user.setUserId(key);
        user.setEmail(email);
        user.setPassword(password);


        mDbRefUser.child(key).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User saved successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed saving User");
                    }
                });

        return user;
    }

    public void addCost(Cost cost) {
        User user = SplashActivity.mCurrenUser;

        String key = mDbRefCost.push().getKey();
        Map<String, Object> costValues = cost.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(user.getUserId()+"/"+ key, costValues);

        mDbRefCost.updateChildren(childUpdates);
    }

}
