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
    private VibWearActivity activity;

    public StopNotificationHandler(VibWearActivity activity) {
        this.activity = activity;
    }

    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case 0:
                activity.dismissNotification();
                break;

            default:
                break;
        }
    }
}
