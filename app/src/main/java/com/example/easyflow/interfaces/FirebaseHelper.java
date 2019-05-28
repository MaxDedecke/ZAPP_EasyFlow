package com.example.easyflow.interfaces;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.easyflow.activities.SplashActivity;
import com.example.easyflow.models.Category;
import com.example.easyflow.models.Cost;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    private static DatabaseReference mDbRefCost;
    private static FirebaseDatabase mDatabase;

    private static final String TAG = "EasyFlow_Debug_Tag";

    static {
        instance=new FirebaseHelper();
    }

    public static FirebaseHelper getInstance(){

        //todo by load mainactivity user mit userid online abgleichen und costs valueevent setzen

        mDatabase=FirebaseDatabase.getInstance();

        mDbRefCost=FirebaseDatabase.getInstance().getReference("costs");

        return instance;

    }

    public User addUser(String email, String password) {

        // Reference to users
        DatabaseReference refUsers=mDatabase.getReference("users");

        String key = refUsers.push().getKey();

        User user = new User();
        user.setUserId(key);
        user.setEmail(email);
        user.setPassword(password);


        refUsers.child(key).setValue(user)
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

    public void checkUserDataChanged(){

        // Get Database Reference
        DatabaseReference refUser=mDatabase.getReference("users/"+SplashActivity.mCurrenUser.getUserId());

        // Read from the database
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User cur=dataSnapshot.getValue(User.class);
                if(cur!=null){
                    String email=SplashActivity.mCurrenUser.getEmail();
                    String password=SplashActivity.mCurrenUser.getPassword();

                    if(!cur.getEmail().equals(email)){
                        SplashActivity.mCurrenUser.setEmail(email);
                    }
                    if(!cur.getPassword().equals(password)) {
                        SplashActivity.mCurrenUser.setPassword(password);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        refUser.addListenerForSingleValueEvent(valueEventListener);

    }

    public void setLiveDataListener(){

        if(SplashActivity.mCurrenUser!=null) {

            String myUserId = SplashActivity.mCurrenUser.getUserId();
            Query myTopPostsQuery = mDbRefCost.child(myUserId);
            myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    GenericTypeIndicator<HashMap<String, Cost>> t = new GenericTypeIndicator<HashMap<String, Cost>>() {
                    };

                    List<Cost> muteModelList;
                    HashMap<String, Cost> hashMap = dataSnapshot.getValue(t);

                    if (hashMap == null) {
                        return;
                    }

                    muteModelList = new ArrayList<Cost>(hashMap.values()) {};

                    for (Cost muteModel : muteModelList) {
                        Category c = muteModel.getCategory();
                        Log.d(TAG, muteModel.getCategory().getName() + " " + muteModel.getValue());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
