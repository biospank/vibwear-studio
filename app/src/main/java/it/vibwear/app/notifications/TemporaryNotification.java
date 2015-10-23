package it.vibwear.app.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.handlers.StopNotificationHandler;
import it.vibwear.app.receivers.StopNotificationReceiver;
import it.vibwear.app.utils.AppManager;

/**
 * Created by biospank on 23/10/15.
 */
public class TemporaryNotification {
    public static final int VIBWEAR_TEMPORARY_NOTIFICATION_ID = 9572;

    private Context context;
    private Intent intent;

    public TemporaryNotification(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    public void show() {
        Notification.Builder builder = buildNotification();

        NotificationManager notificationManager =
                (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(VIBWEAR_TEMPORARY_NOTIFICATION_ID, builder.build());

        StopNotificationHandler stopHandler = new StopNotificationHandler(this.context);
        stopHandler.sendEmptyMessageDelayed(
                StopNotificationHandler.DISMISS_NOTIFICATION_MSG,
                StopNotificationHandler.DISMISS_NOTIFICATION_TIMEOUT
        );

    }

    private Notification.Builder buildNotification() {
        Bundle extraInfo = this.intent.getExtras();

        String sourcePackageName = extraInfo.getString("sourcePackageName");

        Intent stopIntent = new Intent(this.context, StopNotificationReceiver.class);
        stopIntent.putExtra("sourcePackageName", sourcePackageName);
        stopIntent.setAction(StopNotificationReceiver.STOP_ACTION);

        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                this.context,
                VIBWEAR_TEMPORARY_NOTIFICATION_ID,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this.context)
                // Set Icon
                .setSmallIcon(R.drawable.ic_vibwear_notification)
                        // Dismiss Notification
                .setAutoCancel(true)
                        // Set PendingIntent into Notification
                .setContentIntent(stopPendingIntent);

        builder = builder.setContent(getNotificationView(sourcePackageName));

        return builder;
    }

    private RemoteViews getNotificationView(String sourcePackageName) {
        AppManager appManager = new AppManager(this.context, sourcePackageName);

        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(
                this.context.getPackageName(),
                R.layout.custom_notification
        );

        // Locate and set the Image into customnotificationtext.xml ImageViews
        notificationView.setImageViewResource(
                R.id.imagenotileft,
                R.drawable.ic_launcher);

        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.title, appManager.getAppName());
        notificationView.setTextViewText(R.id.text, this.context.getString(R.string.tap_to_block_notification));

        notificationView.setImageViewResource(
                R.id.imagenotiright,
                R.drawable.ic_lock);

        return notificationView;
    }
}
