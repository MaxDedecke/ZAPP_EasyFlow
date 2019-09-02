package com.example.easyflow.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.easyflow.R;
import com.example.easyflow.interfaces.Constants;
import com.example.easyflow.interfaces.NotifyEventHandlerBoolean;
import com.example.easyflow.interfaces.NotifyEventHandlerCostSum;
import com.example.easyflow.interfaces.NotifyEventHandlerStringMap;
import com.example.easyflow.models.Cost;
import com.example.easyflow.models.CostSum;
import com.example.easyflow.models.Frequency;
import com.example.easyflow.models.UserNotification;
import com.example.easyflow.models.GroupSettings;
import com.example.easyflow.models.NotificationSettings;
import com.example.easyflow.models.StateAccount;
import com.example.easyflow.models.StateGroupMembership;
import com.example.easyflow.models.User;
import com.example.easyflow.models.UserGroupSettings;
import com.example.easyflow.models.UserNotificationSettings;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static com.example.easyflow.interfaces.Constants.TAG;

public class FirebaseHelper {

    private static FirebaseHelper instance;

    private static DatabaseReference mDbRefCost;
    private static DatabaseReference mDbRefCostSum;
    private static DatabaseReference mDbRefCostFuture;
    private static DatabaseReference mDbRefGroupSettings;
    private static DatabaseReference mDbRefNotificationSettings;
    private static DatabaseReference mDbRefNotifications;
    private static DatabaseReference mDbRefGroupInvitations;
    private static DatabaseReference mDbRefUser;
    private static ValueEventListener mValueEventListenerCostSum;
    private static Date mStartDate;
    private static Date mEndDate;
    private static Date mEndOfTodayDate;
    private static SimpleDateFormat mSimpleDateFormat;
    private static String mKeyAccount;
    public static User mCurrentUser;
    private static boolean mCurrentUserGroupAdmin = false;

    private static NotifyEventHandlerCostSum mListenerCostSum;
    private static NotifyEventHandlerStringMap mListenerStringMap;
    private static NotifyEventHandlerBoolean mListenerBoolean;
    private static List<GroupSettings> mCurrentGroupSettings;
    private static List<NotificationSettings> mCurrentNotificationSettings;
    private static List<UserNotification> mCurrentNotifications;


    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        instance = new FirebaseHelper();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDbRefUser = database.getReference("users/");
        mDbRefCost = database.getReference("costs/");
        mDbRefCostSum = database.getReference("costs-sum/");
        mDbRefCostFuture = database.getReference("costs-future/");
        mDbRefGroupSettings = database.getReference("group/settings/");
        mDbRefGroupInvitations = database.getReference("group/invitations/");
        mDbRefNotificationSettings = database.getReference("notificationlist/settings/");
        mDbRefNotifications = database.getReference("notificationslist/settings/notifications/");

        // Init ValueEventListener for Cost Sum.
        mValueEventListenerCostSum = initValueEventListenerCostSum();

        // Set Start and Enddate for Database Queries
        mSimpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE);

        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getDefault());

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        mEndOfTodayDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mEndDate = calendar.getTime();


        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mStartDate = calendar.getTime();
    }

    public static FirebaseHelper getInstance() {
        return instance;
    }

    public static void checkUser(User currentUser) {
        mCurrentUser = currentUser;


        mDbRefUser.child(mCurrentUser.getUserId()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User cur = mutableData.getValue(User.class);

                if (cur == null) {
                    return Transaction.success(mutableData);
                }

                mCurrentUser.setEmail(cur.getEmail());
                mCurrentUser.setPassword(cur.getPassword());
                mCurrentUser.setCashId(cur.getCashId());
                mCurrentUser.setBankAccountId(cur.getBankAccountId());
                mCurrentUser.setGroupId(cur.getGroupId());
                mCurrentUser.setNotificationListId(cur.getNotificationListId());


                FirebaseHelper helper = FirebaseHelper.getInstance();

                helper.checkFutureCosts();
                helper.checkCostSums();
                helper.initializeCurrentGroupSettingsList();
                helper.initializeCurrentNotificationSettingsList();



                GlobalApplication.saveUserInSharedPreferences(mCurrentUser);


                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean success, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null || !success || dataSnapshot == null) {
                    System.out.println("Failed to get DataSnapshot");
                } else {
                    System.out.println("Successfully get DataSnapshot");
                    //handle data here
                }
            }
        });


    }

    private static ValueEventListener initValueEventListenerCostSum() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mListenerCostSum == null)
                    return;

                CostSum cur = dataSnapshot.getValue(CostSum.class);
                if (cur != null) {
                    mListenerCostSum.Notify(cur);
                } else {
                    mListenerCostSum.Notify(null);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
    }

    private void checkCostSums() {
        String dateTimeNow = mSimpleDateFormat.format(mEndOfTodayDate);

        checkCostSums(mCurrentUser.getCashId(), dateTimeNow);
        checkCostSums(mCurrentUser.getBankAccountId(), dateTimeNow);
        if (mCurrentUser.getGroupId() != null)
            checkCostSums(mCurrentUser.getGroupId(), dateTimeNow);
    }

    private void checkCostSums(String keyAccount, String dateTimeNow) {

        mDbRefCostSum.child(keyAccount).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CostSum costSum = dataSnapshot.getValue(CostSum.class);

                if (costSum == null)
                    return;


                mDbRefCost.child(keyAccount).orderByChild("date").startAt(costSum.getStringDateLastUpdated()).endAt(dateTimeNow).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        double valueCurrent=0;
                        double valueFuture=0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Cost cost = snapshot.getValue(Cost.class);

                            if (cost == null)
                                continue;

                            if (cost.getDate().after(mEndOfTodayDate))
                                valueFuture+=cost.getValue();
                            else
                                valueCurrent+=cost.getValue();
                        }

                        addValueToCostSum(valueCurrent,valueFuture,keyAccount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkFutureCosts() {
        checkFutureCosts(mCurrentUser.getCashId());
        checkFutureCosts(mCurrentUser.getBankAccountId());
        if (mCurrentUser.getGroupId() != null)
            checkFutureCosts(mCurrentUser.getGroupId());
    }

    private void checkFutureCosts(String keyAccount) {

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                double valueCurrent=0;
                double valueFuture=0;


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cost cost = snapshot.getValue(Cost.class);


                    if (cost == null)
                        continue;

                    while (!cost.getDate().after(mEndDate)) {
                        String key = mDbRefCost.child(keyAccount).push().getKey();

                        Map<String, Object> costValues = cost.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(key, costValues);

                        mDbRefCost.child(keyAccount).updateChildren(childUpdates);


                        if (cost.getDate().after(mEndOfTodayDate))
                            valueFuture+=cost.getValue();
                        else
                            valueCurrent+=cost.getValue();


                        switch (cost.getFrequency()) {
                            case Taeglich:
                                cost.addDay();
                                break;
                            case Woechentlich:
                                cost.addWeek();
                                break;
                            case Monatlich:
                                cost.addMonth();
                                break;
                            case Jaehrlich:
                                cost.addYear();
                                break;
                        }
                    }

                    addFutureCost(cost, keyAccount);
                    snapshot.getRef().removeValue();
                }



                // Update Sum over all Costs for this month
                addValueToCostSum(valueCurrent,valueFuture,keyAccount);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mDbRefCostFuture.child(keyAccount).orderByChild("date").endAt(mSimpleDateFormat.format(mEndDate)).addListenerForSingleValueEvent(valueEventListener);

    }

    private void initializeCurrentGroupSettingsList() {


        if (TextUtils.isEmpty(mCurrentUser.getGroupId()))
            return;


        mDbRefGroupSettings.child(mCurrentUser.getGroupId()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                Iterator<MutableData> iterator = mutableData.getChildren().iterator();
                mCurrentGroupSettings = new ArrayList<>();

                while (iterator.hasNext()) {
                    MutableData snapshot = iterator.next();
                    GroupSettings groupSettings = new GroupSettings(snapshot.getKey(), snapshot.getValue(UserGroupSettings.class));


                    if (groupSettings.getUserGroupSettings().getStateGroupMemberShip() == StateGroupMembership.Admin
                            && groupSettings.getKey().equals(mCurrentUser.getUserId()))
                        mCurrentUserGroupAdmin = true;

                    mCurrentGroupSettings.add(groupSettings);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean success, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null || !success || dataSnapshot == null) {
                    System.out.println("Failed to get DataSnapshot");
                } else {
                    System.out.println("Successfully get DataSnapshot");
                    //handle data here
                }
            }
        });
    }

    private void initializeCurrentNotificationSettingsList() {

        if (TextUtils.isEmpty(mCurrentUser.getNotificationListId()))
            return;

        mDbRefNotificationSettings.child(mCurrentUser.getNotificationListId()).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Iterator<MutableData> iterator = mutableData.getChildren().iterator();
                mCurrentNotificationSettings = new ArrayList<>();

                while(iterator.hasNext()) {
                    MutableData snapshot = iterator.next();
                    NotificationSettings notificationSettings = new NotificationSettings(snapshot.getKey(), snapshot.getValue(UserNotification.class));


                    mCurrentNotificationSettings.add(notificationSettings);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean success, @Nullable DataSnapshot dataSnapshot) {
                if(databaseError != null || !success || dataSnapshot == null) {
                    System.out.println("Failed to get Datasnapshot");
                } else {
                    System.out.println("Successfully get DataSnapshot");
                }
            }
        });
    }
    //finished

    public void initInvitationsForGroup() {

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


                //HashMap<String, String> userInvitationMap = (HashMap<String, String>) dataSnapshot.getCurrentValue();
                if (userInvitationMap == null)
                    return;

                Set<Map.Entry<String, String>> set = userInvitationMap.entrySet();

                if (set.size() != 1)
                    return;

                for (Map.Entry<String, String> entry : set) {
                    mListenerStringMap.Notify(entry.getKey(), entry.getValue());
                }
                //mDbRefGroupInvitations.child(mCurrentUser.getUserId()).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDbRefGroupInvitations.child(mCurrentUser.getUserId()).addValueEventListener(valueEventListenerGroupInvitaions);
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

    public void setKeyAccount(StateAccount stateAccount) {
        if (mKeyAccount != null)
            mDbRefCostSum.child(mKeyAccount).removeEventListener(mValueEventListenerCostSum);
        mKeyAccount = getKeyAccountString(stateAccount);
        mDbRefCostSum.child(mKeyAccount).addValueEventListener(mValueEventListenerCostSum);
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

        double valueCurrent=0;
        double valueFuture=0;


        do {
            String key = mDbRefCost.child(keyAccount).push().getKey();

            Map<String, Object> costValues = cost.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(key, costValues);

            mDbRefCost.child(keyAccount).updateChildren(childUpdates);

            if (cost.getDate().after(mEndOfTodayDate))
                valueFuture+=cost.getValue();
            else
                valueCurrent+=cost.getValue();


            switch (cost.getFrequency()) {
                case Taeglich:
                    cost.addDay();
                    break;
                case Woechentlich:
                    cost.addWeek();
                    break;
                case Monatlich:
                    cost.addMonth();
                    break;
                case Jaehrlich:
                    cost.addYear();
                    break;
            }

        } while (!cost.getDate().after(mEndDate) && cost.getFrequency() != Frequency.Einmalig);

        // Update Sum over all Costs for this month

        addValueToCostSum(valueCurrent,valueFuture,keyAccount);

        if (cost.getFrequency() != Frequency.Einmalig) {
            addFutureCost(cost, keyAccount);
        }


    }

    public void addFutureCost(Cost cost) {
        addFutureCost(cost, mKeyAccount);
    }

    private void addFutureCost(Cost cost, String keyAccount) {
        String key = mDbRefCostFuture.child(keyAccount).push().getKey();

        Map<String, Object> costValues = cost.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, costValues);

        mDbRefCostFuture.child(keyAccount).updateChildren(childUpdates);
    }

    private void addValueToCostSum(double valueCurrent, double valueFuture, String keyAccount) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CostSum costSum = dataSnapshot.getValue(CostSum.class);


                if (costSum == null)
                    costSum = new CostSum();

                costSum.setFutureValue(costSum.getFutureValue() + valueFuture);
                costSum.setCurrentValue(costSum.getCurrentValue() + valueCurrent);

                costSum.setDateLastUpdated(mSimpleDateFormat.format(new Date()));


                Map<String, Object> costSumValues = costSum.toMap();
                dataSnapshot.getRef().updateChildren(costSumValues);
/*
                if (mListenerCostSum != null && mKeyAccount.equals(keyAccount))
                    mListenerCostSum.Notify(costSum);
                    */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDbRefCostSum.child(keyAccount).addListenerForSingleValueEvent(valueEventListener);
    }

    public void deleteCost(DataSnapshot snapshot, boolean isFutureCost) {
        Cost cost = snapshot.getValue(Cost.class);
        snapshot.getRef().removeValue();

        if (mListenerCostSum == null || isFutureCost)
            return;

        // Update Sum over all Costs for this month
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                CostSum costSum = dataSnapshot.getValue(CostSum.class);

                if (costSum == null)
                    costSum = new CostSum();


                if (cost.getDate().after(mEndOfTodayDate))
                    costSum.setFutureValue(costSum.getFutureValue() - cost.getValue());
                else
                    costSum.setCurrentValue(costSum.getCurrentValue() - cost.getValue());


                costSum.setDateLastUpdated(new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE, Locale.getDefault()).format(new Date()));


                Map<String, Object> costSumValues = costSum.toMap();
                dataSnapshot.getRef().updateChildren(costSumValues);

                if (mListenerCostSum != null)
                    mListenerCostSum.Notify(costSum);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDbRefCostSum.child(mKeyAccount).addListenerForSingleValueEvent(valueEventListener);

    }

    public void createNotificationList() {
        String key = mDbRefCost.push().getKey();
        mCurrentUser.setNotificationListId(key);

        mDbRefUser.child(mCurrentUser.getUserId()).child(Constants.DATABASE_KEY_NOTIFICATION_ID).setValue(key)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "NotificationId saved successfully"))
                .addOnFailureListener(e -> Log.d(TAG, "Failed saving NotificationId"));

        //Add Settings for Notification
        UserNotificationSettings notificationSettings = new UserNotificationSettings(mCurrentUser.getEmail());

        //mDbRefNotificationSettings.child(key).child(mCurrentUser.getUserId()).setValue(notificationSettings)
        mDbRefNotificationSettings.child(key).child(mCurrentUser.getUserId()).setValue(notificationSettings.getEmailHost())
                .addOnSuccessListener(aVoid -> Log.d(TAG,"NotificationSettings saved successfully"))
                .addOnFailureListener(e -> Log.d(TAG, "Failed saving NotificationSettings"));

        mCurrentNotificationSettings = new ArrayList<>();
        mCurrentNotificationSettings.add(new NotificationSettings(mCurrentUser.getUserId(), notificationSettings));

    }
    //finished

    public void sendNotificationBackToUser() {
        for(NotificationSettings notificationSettings : mCurrentNotificationSettings) {
            if(!notificationSettings.getKey().equals(mCurrentUser.getUserId()))
                continue;
            sendNotificationBackToUser(notificationSettings);
            break;
        }
    }
    //finished

    public void sendNotificationBackToUser(NotificationSettings notificationSettings) {

        //Hier muss noch implementiert werden!
    }
    //in progress!

    public void addNotification(UserNotification notification) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();

                User cur = null;
                String emailReceiving = notification.getEmailReceiving();

                for(DataSnapshot snapshot : iterable) {
                    User temp = snapshot.getValue(User.class);
                    if (temp == null)
                        return;

                    if (emailReceiving.equals(temp.getEmail())) {
                        cur = temp;
                        break;
                    }
                }
                    if(cur == null) {
                        Toast.makeText(GlobalApplication.getAppContext(), "Nutzer nicht angemeldet", Toast.LENGTH_LONG).show();
                        return;
                    }

                    else {
                        UserNotification newUserNotification = notification;

                        Map<String, Object> notificationInfo = newUserNotification.toMap();

                        if(mCurrentNotifications == null) {
                            mCurrentNotifications = new ArrayList<>();
                        }
                            mCurrentNotifications.add(newUserNotification);
                        //mDbRefNotificationSettings.child(cur.getNotificationListId()).child(cur.getUserId()).setValue(notificationInfo);
                        mDbRefNotificationSettings.child(cur.getNotificationListId()).push().setValue(notificationInfo);
                            mDbRefUser.removeEventListener(this);
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDbRefUser.orderByChild("mEmailReceiving").addListenerForSingleValueEvent(valueEventListener);
    }

    public void createGroup() {
        String key = mDbRefCost.push().getKey();
        mCurrentUser.setGroupId(key);
        mCurrentUserGroupAdmin = true;

        mDbRefUser.child(mCurrentUser.getUserId()).child(Constants.DATABASE_KEY_GROUP_ID).setValue(key)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "GroupId saved successfully"))
                .addOnFailureListener(e -> Log.d(TAG, "Failed saving GroupId"));

        // Add Settings for Group
        UserGroupSettings groupSettings = new UserGroupSettings(mCurrentUser.getEmail(), StateGroupMembership.Admin);

        mDbRefGroupSettings.child(key).child(mCurrentUser.getUserId()).setValue(groupSettings)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "GroupSettings saved successfully"))
                .addOnFailureListener(e -> Log.d(TAG, "Failed saving GroupSettings"));

        mCurrentGroupSettings = new ArrayList<>();
        mCurrentGroupSettings.add(new GroupSettings(mCurrentUser.getUserId(), groupSettings));
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

    public void removeGroup() {
        mDbRefCost.child(mCurrentUser.getGroupId()).removeValue();
        for (GroupSettings groupSettings : mCurrentGroupSettings) {

            switch (groupSettings.getUserGroupSettings().getStateGroupMemberShip()) {
                case Admin:
                case Member:
                    // Remove node from group settings and set groupId null by the user.
                    mDbRefUser.child(groupSettings.getKey()).child(Constants.DATABASE_KEY_GROUP_ID).removeValue();
                    break;
                case Pending:
                    // Remove node from group settings and invitations.
                    mDbRefGroupInvitations.child(groupSettings.getKey()).removeValue();
                    break;
            }

            mDbRefGroupSettings.child(mCurrentUser.getGroupId()).child(groupSettings.getKey()).removeValue();

        }
        mCurrentUser.setGroupId(null);
        mCurrentGroupSettings = null;
    }

    public void removeUserFromGroup() {
        for (GroupSettings groupSettings : mCurrentGroupSettings) {
            if (!groupSettings.getKey().equals(mCurrentUser.getUserId()))
                continue;
            removeUserFromGroup(groupSettings);
            break;
        }

        mCurrentUser.setGroupId(null);
        mCurrentGroupSettings = null;

    }

    public void removeUserFromGroup(GroupSettings groupSettings) {
        switch (groupSettings.getUserGroupSettings().getStateGroupMemberShip()) {
            case Admin:
            case Member:
                // Remove node from group settings and set groupId null by the user.
                mDbRefUser.child(groupSettings.getKey()).child(Constants.DATABASE_KEY_GROUP_ID).removeValue();
                break;
            case Pending:
                // Remove node from group settings and invitations.
                mDbRefGroupInvitations.child(groupSettings.getKey()).removeValue();
                break;
        }

        mDbRefGroupSettings.child(mCurrentUser.getGroupId()).child(groupSettings.getKey()).removeValue();

        for (GroupSettings groupSettingsInList : mCurrentGroupSettings) {
            if (groupSettings.getKey().equals(groupSettingsInList.getKey())) {
                mCurrentGroupSettings.remove(groupSettingsInList);
                break;
            }
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

                if (cur == null) {
                    Toast.makeText(GlobalApplication.getAppContext(), GlobalApplication.getAppContext().getString(R.string.messag_user_not_registered), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cur.getGroupId() != null) {
                    Context context = GlobalApplication.getAppContext();
                    Toast.makeText(context, context.getString(R.string.user_already_in_group), Toast.LENGTH_SHORT).show();
                } else {
                    GroupSettings groupSettings = new GroupSettings(cur.getUserId(), cur.getEmail(), StateGroupMembership.Pending);


                    mCurrentGroupSettings.add(groupSettings);

                    mDbRefGroupSettings.child(mCurrentUser.getGroupId()).child(cur.getUserId()).setValue(groupSettings.getUserGroupSettings());

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

    public User registerUser(String email, String password) {
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
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User saved successfully"))
                .addOnFailureListener(e -> Log.d(TAG, "Failed saving User"));

        mCurrentUser = user;
        return user;
    }

    public void setListener(NotifyEventHandlerCostSum listenerCostSum, NotifyEventHandlerStringMap listenerStringMap) {
        mListenerCostSum = listenerCostSum;
        mListenerStringMap = listenerStringMap;
    }

    public void setListenerBoolean(NotifyEventHandlerBoolean listenerBoolean) {
        mListenerBoolean = listenerBoolean;
    }

    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptionNotifications() {
        Query query = mDbRefNotificationSettings.child(mCurrentUser.getNotificationListId());

        return new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();
    }
    //finished

    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptionsMembersGroup() {
        Query query = mDbRefGroupSettings.child(mCurrentUser.getGroupId());

        return new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();
    }

    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptionsCosts() {
        Query query = mDbRefCost.child(mKeyAccount).orderByChild("date").startAt(mSimpleDateFormat.format(mStartDate)).endAt(mSimpleDateFormat.format(mEndDate));

        return new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();
    }

    public FirebaseRecyclerOptions<DataSnapshot> getFirebaseRecyclerOptionsRecurringCosts(StateAccount stateAccount) {
        String stateAccountString = getKeyAccountString(stateAccount);
        Query query = mDbRefCostFuture.child(stateAccountString).orderByChild("date");

        return new FirebaseRecyclerOptions.Builder<DataSnapshot>()
                .setQuery(query, snapshot -> snapshot)
                .build();
    }

    public void validateUserCredentials(String email, String password) {

        if (mListenerBoolean == null)
            return;

        Query query = mDbRefUser.orderByChild("email").equalTo(email).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if (!iterator.hasNext()) {
                    mListenerBoolean.Notify(false, false);
                    query.removeEventListener(this);
                    return;
                }

                User user = iterator.next().getValue(User.class);

                if (user == null || !user.getEmail().equals(email)) {
                    mListenerBoolean.Notify(false, false);
                } else if (!user.getPassword().equals(password)) {
                    mListenerBoolean.Notify(true, false);
                } else {
                    mCurrentUser = user;
                    mListenerBoolean.Notify(true, true);
                }

                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
