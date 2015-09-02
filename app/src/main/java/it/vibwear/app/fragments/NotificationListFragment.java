package it.vibwear.app.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.adapters.Notification;
import it.vibwear.app.adapters.NotificationListAdapter;
import it.vibwear.app.utils.NotificationPreference;

/**
 * Created by biospank on 02/09/15.
 */
public class NotificationListFragment extends Fragment {

    protected Context context;
    protected NotificationPreference notificationPreference;
    protected ListView lvNotification;
    protected NotificationListAdapter adapter;

    public NotificationListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();

        View layout = inflater.inflate(R.layout.notification_list_item, container, false);

        lvNotification = (ListView)layout.findViewById(R.id.lv_chat_notifications);

        notificationPreference = new NotificationPreference(context);

        List<Notification> notifications = notificationPreference.getBlackList();

        adapter = new NotificationListAdapter(context, notifications);

        lvNotification.setAdapter(adapter);

        return layout;
    }

}
