package it.vibwear.app.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by biospank on 22/09/15.
 */
public class AppManager {
    private Context context;
    private String packageName;

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
