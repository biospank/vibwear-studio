package it.vibwear.app.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.adapters.Notification;
import it.vibwear.app.adapters.NotificationListAdapter;
import it.vibwear.app.utils.NotificationPreference;
import it.vibwear.app.utils.VersionPreference;

/**
 * Created by biospank on 02/09/15.
 */
public class NotificationListFragment extends Fragment {

    protected Context context;
    protected NotificationPreference notificationPreference;
    protected VersionPreference versionPref;
    protected ListView lvNotification;
    protected NotificationListAdapter adapter;

    protected String[] blockedPackages = {
            "com.android.vending",
            "com.android.settings",
            "com.android.systemui"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();

        View layout = inflater.inflate(R.layout.fragment_notification_list, container, false);

        lvNotification = (ListView)layout.findViewById(R.id.lv_chat_notifications);

        notificationPreference = new NotificationPreference(context);

        versionPref = new VersionPreference(context);

        initBlacklist();

        List<Notification> notifications = notificationPreference.getBlackList();

        adapter = new NotificationListAdapter(context, notifications);

        lvNotification.setAdapter(adapter);

        setListViewHeightBasedOnChildren(lvNotification);

        unsetParentScrollFor(lvNotification);

        return layout;
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null)
//            return;
//
//        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
//        int totalHeight = 0;
//        View view = null;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            view = listAdapter.getView(i, view, listView);
//            if (i == 0)
//                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
//            totalHeight += view.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//        listView.requestLayout();

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = 450;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void unsetParentScrollFor(ListView listView) {
        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private void initBlacklist() {
        if(versionPref.isFirstRun()) {
            for (String name : blockedPackages) {
                notificationPreference.addToBlackList(new Notification(name));
            }
        }
    }

}
