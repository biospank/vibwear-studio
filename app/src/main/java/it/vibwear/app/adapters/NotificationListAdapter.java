package it.vibwear.app.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.NotificationPreference;

/**
 * Created by biospank on 02/09/15.
 */
public class NotificationListAdapter extends ArrayAdapter<Notification> {


    private Context context;
    List<Notification> notifications;
    NotificationPreference notificationPreference;

    public NotificationListAdapter(Context context, List<Notification> notifications) {
        super(context, R.layout.notification_list_item, notifications);
        this.context = context;
        this.notifications = notifications;
        notificationPreference = new NotificationPreference(context);
    }

    private class ViewHolder {
        ImageView notificationAppImg;
        TextView notificationNameTxt;
        TextView notificationDateTxt;
        ImageView deleteImg;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Notification getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notification_list_item, null);
            holder = new ViewHolder();
            holder.notificationAppImg = (ImageView) convertView
                    .findViewById(R.id.img_notification_app);
            holder.notificationNameTxt = (TextView) convertView
                    .findViewById(R.id.txt_notification_name);
            holder.deleteImg = (ImageView) convertView
                    .findViewById(R.id.imgbtn_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Notification notification = (Notification) getItem(position);

        Drawable icon = getIconAppFor(notification.getPackageName());

        if(icon != null)
            holder.notificationAppImg.setImageDrawable(icon);

        holder.notificationNameTxt.setText(getAppNameFor(notification.getPackageName()));

        holder.deleteImg.setImageResource(R.drawable.ic_delete);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.reenable_notification_msg, getAppNameFor(notification.getPackageName())))
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(R.string.reenable_notification_title)
                        .setPositiveButton(R.string.reenable_notification_confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                remove(notification);
                            }
                        })
                        .setNegativeButton(R.string.reenable_notification_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        }).show();
            }
        });

        return convertView;
    }

    /*Checks whether a particular contact exists in SharedPreferences*/
    public boolean hasNotification(Notification other) {
        boolean check = false;
        List<Notification> notifications = notificationPreference.getBlackList();
        if (notifications != null) {
            for (Notification notification : notifications) {
                if (notification.equals(other)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    @Override
    public void remove(Notification notification) {
        //super.remove(notification);
        notifications.remove(notification);
        notificationPreference.removeFromBlackList(notification);
        notifyDataSetChanged();
    }


    private Drawable getIconAppFor(String packageName) {
        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }

        return icon;

    }

    private String getAppNameFor(String packageName) {
        ApplicationInfo applicationInfo = null;

        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }

        if(applicationInfo != null)
            return ((String) context.getPackageManager().getApplicationLabel(applicationInfo));
        else
            return packageName;

    }
}
