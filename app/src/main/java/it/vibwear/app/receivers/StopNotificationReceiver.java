package it.vibwear.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import it.vibwear.app.VibWearActivity;
import it.vibwear.app.adapters.Notification;
import it.vibwear.app.utils.NotificationPreference;

/**
 * Created by biospank on 31/08/15.
 */
public class StopNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationPreference preference = new NotificationPreference(context);
        Notification notification = new Notification(intent.getStringExtra("sourcePackageName"));
        preference.addToBlackList(notification);
    }
}
