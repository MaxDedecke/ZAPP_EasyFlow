package com.example.easyflow.interfaces;

import android.util.Log;

import com.example.easyflow.models.Cost;
import com.example.easyflow.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    private static DatabaseReference mDbRef;
    private static FirebaseDatabase mDatabase;

    private static final String TAG = "EasyFlow_Debug_Tag";

    static {
        instance=new FirebaseHelper();
    }

    public static FirebaseHelper getInstance(){


        mDatabase=FirebaseDatabase.getInstance();
        mDbRef=mDatabase.getReference("costs");

        // Read from the database
        mDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                //todo firebasehelpber
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                //todo firebasehelper
            }
        });
        return instance;
    }

    public void addUser(User user) {
        //todo add User

    }

    public void addCost(Cost cost) {
        //todo add user
    }
}
