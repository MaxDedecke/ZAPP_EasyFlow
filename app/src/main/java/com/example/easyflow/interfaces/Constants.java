package com.example.easyflow.interfaces;

import com.example.easyflow.models.StateAccount;

public interface Constants {
    String DATE_FORMAT_DATABASE = "yyyy-MM-dd-HH-mm-ss";
    String DATE_FORMAT_WEEKDAY = "EEEE, d.MM.yyyy";

    String DOUBLE_FORMAT_TWO_DECIMAL = "%.2f€";


    String TAG = "EasyFlow_Debug_Tag";


    String SHARED_PREF_KEY_ACTIVITY_EXECUTED = "activity_executed";
    String SHARED_PREF_KEY_USER_DATABASE = "user_database";
    String SHARED_PREF_KEY = "ActivityPREF";

    public interface GroupDatabase{
        String GROUP="group";
        String GROUP_ID="groupId";
        String INVITATIONS="invitations";
        String EMAIL="email";
        String STATEGROUPMEMBERSHIP="stateGroupMemberShip";
        String USERS="users";
    }


}
