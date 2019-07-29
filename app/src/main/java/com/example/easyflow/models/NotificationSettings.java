package com.example.easyflow.models;

import java.util.HashMap;

public class NotificationSettings {

    private String key;
    private UserNotificationSettings mUserNotificationSettings;

    public NotificationSettings(){}

    public NotificationSettings(String notificationId, String email, String a) {
        setKey(notificationId);
        setUserNotificationSettings(new UserNotificationSettings(email, a));
    }

    public NotificationSettings(String key, UserNotificationSettings userNotificationSettings) {
        setKey(key);
        setUserNotificationSettings(userNotificationSettings);
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String notificationId) {
        this.key = notificationId;
    }

    public void setUserNotificationSettings(UserNotificationSettings userNotificationSettings) {
        this.mUserNotificationSettings = userNotificationSettings;
    }

    public UserNotificationSettings getUserNotificationSettings() {
        return this.mUserNotificationSettings;
    }

    public HashMap<String, UserNotificationSettings> tomap() {
        HashMap<String, UserNotificationSettings> map = new HashMap<>();
        map.put(getKey(), getUserNotificationSettings());
        return map;
    }
}
