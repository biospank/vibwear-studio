package it.vibwear.app.handlers;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Handler;

import it.vibwear.app.VibWearActivity;

/**
 * Created by biospank on 29/08/15.
 */
public class StopNotificationHandler extends Handler {
    public static final int DISMISS_NOTIFICATION_MSG = 0;
    public static final int DISMISS_NOTIFICATION_TIMEOUT = 10000;
    private VibWearActivity activity;

    public StopNotificationHandler(VibWearActivity activity) {
        this.activity = activity;
    }

    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case DISMISS_NOTIFICATION_MSG:
                activity.dismissNotification();
                break;

            default:
                break;
        }
    }
}
