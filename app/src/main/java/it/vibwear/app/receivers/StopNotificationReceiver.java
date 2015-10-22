package it.vibwear.app.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mbientlab.metawear.api.MetaWearBleService;

import it.vibwear.app.VibWearActivity;
import it.vibwear.app.adapters.Notification;
import it.vibwear.app.utils.NotificationPreference;

/**
 * Created by biospank on 31/08/15.
 */
public class StopNotificationReceiver extends BroadcastReceiver {
    public static final String STOP_ACTION = "STOP_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationPreference preference = new NotificationPreference(context);
        Notification notification = new Notification(intent.getStringExtra("sourcePackageName"));
        preference.addToBlackList(notification);
        dismissNotification(context);
    }

    private void dismissNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(VibWearActivity.VIBWEAR_TEMPORARY_NOTIFICATION_ID);
    }
}
