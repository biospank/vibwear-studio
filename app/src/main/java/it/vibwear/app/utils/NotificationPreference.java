package it.vibwear.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import it.vibwear.app.adapters.Notification;

/**
 * Created by biospank on 31/08/15.
 */
public class NotificationPreference {

    public static final String NOTIFICATION_PREFS_NAME = "NOTIFICATION_DETAILS";
    public static final String NOTIFICATIONS_LIST = "notifications_list";

    protected Context context;

    public NotificationPreference(Context context) {
        super();
        this.context = context;
    }

    public void saveBlackList(List<Notification> notifications) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(NOTIFICATION_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonNotifications = gson.toJson(notifications);

        editor.putString(NOTIFICATIONS_LIST, jsonNotifications);

        editor.commit();
    }

    public void addToBlackList(Notification contact) {
        List<Notification> notifications = getBlackList();
        if (notifications == null)
            notifications = new ArrayList<Notification>();
        notifications.add(contact);
        saveBlackList(notifications);
    }

    public void removeFromBlackList(Notification other) {
        ArrayList<Notification> notifications = getBlackList();
        if (notifications != null) {
            for (Iterator<Notification> it = notifications.iterator(); it.hasNext(); ) {
                Notification notification = it.next();
                if (notification.getPackageName().equals(other.getPackageName())) {
                    it.remove();
                    break;
                }
            }
            saveBlackList(notifications);
        }
    }

    public ArrayList<Notification> getBlackList() {
        SharedPreferences settings;
        List<Notification> notifications;

        settings = context.getSharedPreferences(NOTIFICATION_PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(NOTIFICATIONS_LIST)) {
            String jsonFavorites = settings.getString(NOTIFICATIONS_LIST, null);
            Gson gson = new Gson();
            Notification[] favoriteItems = gson.fromJson(jsonFavorites,
                    Notification[].class);

            notifications = Arrays.asList(favoriteItems);
            notifications = new ArrayList<Notification>(notifications);
        } else
            return new ArrayList<Notification>();

        return (ArrayList<Notification>) notifications;
    }

}
