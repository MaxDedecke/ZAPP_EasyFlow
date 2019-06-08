package com.example.easyflow.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.interfaces.NotifyEventHandlerDouble;
import com.example.easyflow.interfaces.NotifyEventHandlerStrinMap;
import com.example.easyflow.models.Cost;
import com.example.easyflow.models.CostSum;
import com.example.easyflow.models.GroupSettings;
import com.example.easyflow.models.StateAccount;
import com.example.easyflow.models.StateGroupMembership;
import com.example.easyflow.models.User;
import com.example.easyflow.models.UserGroupSettings;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    private static DatabaseReference mDbRefCost;
    private static DatabaseReference mDbRefCostSum;
    private static DatabaseReference mDbRefGroupSettings;
    private static DatabaseReference mDbRefGroupInvitations;
    private static  DatabaseReference mDbRefUser;
    private static Date mStartDate;
    private static Date mEndDate;
    private static SimpleDateFormat mSimpleDateFormat;
    private static String mKeyAccount;
    public static User mCurrentUser;
    private static boolean mCurrentUserGroupAdmin = false;

    private static NotifyEventHandlerDouble mListenerDouble;
    private static NotifyEventHandlerStrinMap mListenerStringMap;
    private static List<GroupSettings> mCurrentGroupSettings;


    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        instance = new FirebaseHelper();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDbRefUser= database.getReference("users/");
        mDbRefCost = database.getReference("costs/");
        mDbRefCostSum = database.getReference("costs-sum/");
        mDbRefGroupSettings = database.getReference("group/settings/");
        mDbRefGroupInvitations = database.getReference("group/invitations/");

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

        // Read from the database
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User cur = dataSnapshot.getValue(User.class);
                if (cur != null) {
                    mCurrentUser.setEmail(cur.getEmail());
                    mCurrentUser.setPassword(cur.getPassword());
                    mCurrentUser.setCashId(cur.getCashId());
                    mCurrentUser.setBankAccountId(cur.getBankAccountId());
                    mCurrentUser.setGroupId(cur.getGroupId());

                    checkInvitationsForGroup();

                    GlobalApplication.saveUserInSharedPreferences(mCurrentUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDbRefUser.child(mCurrentUser.getUserId()).addListenerForSingleValueEvent(valueEventListener);

        if (TextUtils.isEmpty(mCurrentUser.getGroupId()))
            return;


        mDbRefGroupSettings.child(mCurrentUser.getGroupId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                mCurrentGroupSettings = new ArrayList<>();

                while (iterator.hasNext()) {
                    DataSnapshot snapshot = iterator.next();
                    GroupSettings groupSettings = new GroupSettings(snapshot.getKey(), snapshot.getValue(UserGroupSettings.class));


                    if (groupSettings.getKey().equals(mCurrentUser.getUserId()))
                        mCurrentUserGroupAdmin = true;

                    mCurrentGroupSettings.add(groupSettings);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private static void checkInvitationsForGroup() {

        // Check new invitations
        if (mCurrentUser == null || mCurrentUser.getGroupId() != null || mListenerStringMap == null)
            return;

        ValueEventListener valueEventListenerGroupInvitaions = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> userInvitationMap = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userInvitationMap = new HashMap<>();
                    userInvitationMap.put(snapshot.getKey(), snapshot.getValue().toString());
                }


                //HashMap<String, String> userInvitationMap = (HashMap<String, String>) dataSnapshot.getValue();
                if (userInvitationMap == null)
                    return;

                Set<Map.Entry<String, String>> set = userInvitationMap.entrySet();

                if (set.size() != 1)
                    return;

                for (Map.Entry<String, String> entry : set) {
                    mListenerStringMap.Notify(entry.getKey(), entry.getValue());
                }
                mDbRefGroupInvitations.child(mCurrentUser.getUserId()).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDbRefGroupInvitations.child(mCurrentUser.getUserId()).addListenerForSingleValueEvent(valueEventListenerGroupInvitaions);
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

    public boolean isCurrentUserGroupAdmin() {
        return mCurrentUserGroupAdmin;
    }

    public void addCost(Cost cost) {
        addCost(cost, mKeyAccount);
    }

    public void addCost(Cost cost, StateAccount account) {
        addCost(cost, getKeyAccountString(account));
    }

    private void addCost(Cost cost, String keyAccount) {

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

                if (mListenerDouble != null && mKeyAccount.equals(keyAccount))
                    mListenerDouble.Notify(newValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDbRefCostSum.child(keyAccount).addListenerForSingleValueEvent(valueEventListener);


    }

    public void deleteCost(DataSnapshot snapshot) {
        Cost cost = snapshot.getValue(Cost.class);
        snapshot.getRef().removeValue();

        if (mListenerDouble == null)
            return;

        // Update Sum over all Costs for this month
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double newValue = cost.getValue();

                CostSum cur = dataSnapshot.getValue(CostSum.class);
                if (cur != null) {
                    newValue = cur.getValue() - newValue;

                    dataSnapshot.getRef().child("value").setValue(newValue);
                } else {
                    CostSum newCostSum = new CostSum();
                    newCostSum.setDateLastUpdated(cost.getDateString());
                    newCostSum.setValue(newValue);

                    Map<String, Object> costSumValues = newCostSum.toMap();
                    dataSnapshot.getRef().updateChildren(costSumValues);
                }

                mListenerDouble.Notify(newValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDbRefCostSum.child(mKeyAccount).addListenerForSingleValueEvent(valueEventListener);

    }

    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptions() {
        getActualAccountSum();

        Query query = mDbRefCost.child(mKeyAccount).orderByChild("date").startAt(mSimpleDateFormat.format(mStartDate)).endAt(mSimpleDateFormat.format(mEndDate));;

        return new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();
    }

    public void createGroup() {
        String key = mDbRefCost.push().getKey();
        mCurrentUser.setGroupId(key);

        mDbRefUser.child(mCurrentUser.getUserId()).child(Constants.DATABASE_KEY_GROUP_ID).setValue(key)
                .addOnSuccessListener(aVoid -> Log.d(Constants.TAG, "GroupId saved successfully"))
                .addOnFailureListener(e -> Log.d(Constants.TAG, "Failed saving GroupId"));

        // Add Settings for Group
        UserGroupSettings groupSettings = new UserGroupSettings(mCurrentUser.getEmail(), StateGroupMembership.Admin);

        mDbRefGroupSettings.child(key).child(mCurrentUser.getUserId()).setValue(groupSettings)
                .addOnSuccessListener(aVoid -> Log.d(Constants.TAG, "GroupSettings saved successfully"))
                .addOnFailureListener(e -> Log.d(Constants.TAG, "Failed saving GroupSettings"));
    }

    public void declineGroupInvitation(String key) {
        mDbRefGroupInvitations.child(mCurrentUser.getUserId()).removeValue();
        mDbRefGroupSettings.child(key).child(mCurrentUser.getUserId()).child(Constants.DATABASE_KEY_STATE_GROUP_MEMBERSHIP).setValue(StateGroupMembership.Refused);
    }

    public void followGroupInvitation(String key) {
        mDbRefGroupInvitations.child(mCurrentUser.getUserId()).removeValue();
        mCurrentUser.setGroupId(key);
        mDbRefUser.child(mCurrentUser.getUserId()).child(Constants.DATABASE_KEY_GROUP_ID).setValue(key);
        mDbRefGroupSettings.child(key).child(mCurrentUser.getUserId()).child(Constants.DATABASE_KEY_STATE_GROUP_MEMBERSHIP).setValue(StateGroupMembership.Member);
    }

    public List<GroupSettings> getMembersOfGroup() {
        if (mCurrentGroupSettings == null)
            mCurrentGroupSettings = new ArrayList<>();
        return mCurrentGroupSettings;
    }

    public void removeGroup() {
        for (GroupSettings groupSettings:mCurrentGroupSettings) {
            removeUserFromGroup(groupSettings);
        }
    }

    public void removeUserFromGroup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            GroupSettings groupSettings = (GroupSettings) mCurrentGroupSettings.stream().filter(o -> o.getKey().equals(mCurrentUser.getUserId()));
            removeUserFromGroup(groupSettings);
            return;
        }

        for (GroupSettings groupSettings : mCurrentGroupSettings) {
            if (!groupSettings.getKey().equals(mCurrentUser.getUserId()))
                continue;
            removeUserFromGroup(groupSettings);
            break;
        }

    }

    public void removeUserFromGroup(GroupSettings groupSettings) {
        switch (groupSettings.getUserGroupSettings().getStateGroupMemberShip()) {
            case Member:
                // Remove node from group settings and set groupId null by the user.
                mDbRefGroupSettings.child(mCurrentUser.getGroupId()).child(groupSettings.getKey()).removeValue();
                mDbRefUser.child(groupSettings.getKey()).child(Constants.DATABASE_KEY_GROUP_ID).removeValue();
                break;
            case Pending:
                // Remove node from group settings and invitations.
                mDbRefGroupSettings.child(mCurrentUser.getGroupId()).child(groupSettings.getKey()).removeValue();
                mDbRefGroupInvitations.child(groupSettings.getKey()).removeValue();
                break;
            case Refused:
                // Remove node for user from group settings.
                mDbRefGroupSettings.child(mCurrentUser.getGroupId()).child(groupSettings.getKey()).removeValue();
                break;
        }
    }

    public void addUserToGroup(String newUserEmail) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();

                User cur = null;
                for (DataSnapshot snapshot : iterable) {
                    User temp = snapshot.getValue(User.class);
                    if (temp == null)
                        return;


                    if (newUserEmail.equals(temp.getEmail())) {
                        cur = temp;
                        break;
                    }
                }

                if (cur == null)
                    return;

                if (cur.getGroupId() != null) {
                    Context context = GlobalApplication.getAppContext();
                    Toast.makeText(context, context.getString(R.string.user_already_in_group), Toast.LENGTH_SHORT).show();
                } else {
                    GroupSettings groupSettings = new GroupSettings(cur.getUserId(), cur.getEmail(), StateGroupMembership.Pending);


                    //todo changes made because of adapter for listview

                    mCurrentGroupSettings.add(groupSettings);

                    HashMap<String, UserGroupSettings> map = groupSettings.toMap();

                    mDbRefGroupSettings.child(mCurrentUser.getGroupId()).setValue(map);

                    mDbRefGroupInvitations.child(cur.getUserId()).child(mCurrentUser.getGroupId()).setValue(mCurrentUser.getEmail());

                }
                mDbRefUser.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDbRefUser.orderByChild("email").addListenerForSingleValueEvent(valueEventListener);

    }

    public User addUser(String email, String password) {
        String keyUser = mDbRefUser.push().getKey();

        String keyBankAccount = mDbRefCost.push().getKey();
        String keyCash = mDbRefCost.push().getKey();

        User user = new User();
        user.setUserId(keyUser);
        user.setEmail(email);
        user.setPassword(password);
        user.setBankAccountId(keyBankAccount);
        user.setCashId(keyCash);


        mDbRefUser.child(keyUser).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d(Constants.TAG, "User saved successfully"))
                .addOnFailureListener(e -> Log.d(Constants.TAG, "Failed saving User"));

        mCurrentUser = user;
        return user;
    }

    private void getActualAccountSum() {
        if (mListenerDouble == null)
            return;

        // Get Sum over all Costs for this month
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CostSum cur = dataSnapshot.getValue(CostSum.class);
                if (cur != null) {
                    mListenerDouble.Notify(cur.getValue());
                } else {
                    mListenerDouble.Notify(0.0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDbRefCostSum.child(mKeyAccount).addListenerForSingleValueEvent(valueEventListener);

    }

    public void setListener(NotifyEventHandlerDouble listenerDouble, NotifyEventHandlerStrinMap listenerStringMap) {
        mListenerDouble = listenerDouble;
        mListenerStringMap = listenerStringMap;
    }

}
