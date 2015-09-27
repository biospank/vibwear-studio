package it.vibwear.app.handlers;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;

import it.vibwear.app.VibWearActivity;

/**
 * Created by biospank on 29/08/15.
 */
public class StopNotificationHandler extends Handler {
    public static final int DISMISS_NOTIFICATION_MSG = 0;
    public static final int DISMISS_NOTIFICATION_TIMEOUT = 15000;
    private Context context;

    public StopNotificationHandler(Context context) {
        this.context = context;
    }

    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case DISMISS_NOTIFICATION_MSG:
                dismissNotification();
                break;

            default:
                break;
        }
    }

    private void dismissNotification() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(VibWearActivity.VIBWEAR_NOTIFICATION_ID);

        ((VibWearActivity) context).showNotification(true);
    }

}
