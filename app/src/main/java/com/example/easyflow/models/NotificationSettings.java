package com.example.easyflow.models;

import java.util.HashMap;

public class NotificationSettings {

    private String key;
    private UserNotificationSettings mUserNotificationSettings;
    private UserNotification mUserNotification;
    private UserNotificationConverter mUserNotificationConverter;

    public NotificationSettings(){}

    public NotificationSettings(String notificationId, String email, String a) {
        setKey(notificationId);
        //setUserNotificationSettings(new UserNotificationSettings(email, a));
    }

    public NotificationSettings(String key, UserNotification userNotification) {
        setKey(key);
        setUserNotification(userNotification);
    }

    public NotificationSettings(String key, UserNotificationConverter userNotificationConverter) {
        setKey(key);
        setUserNotificationConverter(userNotificationConverter);
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

    public void setUserNotification(UserNotification userNotification) {
        this.mUserNotification = userNotification;
    }

    public void setUserNotificationConverter(UserNotificationConverter userNotificationConverter) {
        this.mUserNotificationConverter = userNotificationConverter;
    }

    public void setUserNotificationSettings(UserNotificationSettings userNotificationSettings) {
        this.mUserNotificationSettings = userNotificationSettings;
    }

    public UserNotificationConverter getUserNotification() {
        return this.mUserNotificationConverter;
    }

    public HashMap<String, UserNotificationConverter> tomap() {
        HashMap<String, UserNotificationConverter> map = new HashMap<>();
        map.put(getKey(), getUserNotification());
        return map;
    }
}
