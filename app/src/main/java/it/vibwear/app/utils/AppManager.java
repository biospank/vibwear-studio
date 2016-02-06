package it.vibwear.app.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by biospank on 22/09/15.
 */
public class AppManager {
    private static final String[] BLACK_LIST_PERMISSIONS = {
        "android.permission.KILL_BACKGROUND_PROCESSES",
        "android.permission.GET_TASKS"
//        "android.permission.CLEAR_APP_CACHE",
//        "android.permission.SYSTEM_ALERT_WINDOW",
//        "android.permission.RESTART_PACKAGES",
//        "android.permission.WAKE_LOCK",
//        "android.permission.WRITE_SETTINGS"
    };
    private Context context;
    private String packageName;

    public static ArrayList<String> findKillerApp(Context context) {
        ArrayList<String> killerAppPackageList = new ArrayList<String>();
        List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
        Iterator<PackageInfo> iterator = packageInfoList.iterator();

        while (iterator.hasNext()) {
            PackageInfo packageInfo = iterator.next();

            if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                if (packageInfo.requestedPermissions != null) {
                    for (String requestedPermission : packageInfo.requestedPermissions) {
                        for (String blackListPermission : BLACK_LIST_PERMISSIONS) {
                            if (requestedPermission.equalsIgnoreCase(blackListPermission))
                                killerAppPackageList.add(packageInfo.packageName);
                        }
                    }
                }
            }
        }

        return killerAppPackageList;

    }

    public AppManager(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    public Drawable getIconApp() {
        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            //e.printStackTrace();
        }

        return icon;

    }

    public String getAppName() {
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
