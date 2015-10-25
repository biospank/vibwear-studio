package it.vibwear.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by biospank on 21/09/15.
 */
public class VersionPreference {
    public static final String VERSION_PREFS_NAME = "VERSION_DETAIL";
    public static final String PREF_VERSION_CODE_KEY = "version_code";
    public final int DOESNT_EXIST = -1;

    protected Context context;

    public VersionPreference(Context context) {
        super();
        this.context = context;
    }

    public boolean isFirstRun() {

        // Get current version code
        int currentVersionCode = 0;
        boolean firstRun = false;

        try {
            currentVersionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            //e.printStackTrace();
        }

        // Get saved version code
        SharedPreferences prefs = context.getSharedPreferences(VERSION_PREFS_NAME, Context.MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)
            firstRun = true;

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade

        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();

        return firstRun;

    }

}
