package it.vibwear.app.utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

/**
 * Created by biospank on 04/03/16.
 */
public class GpsServiceManager {
    private Context context;

    public GpsServiceManager(Context context) {
        this.context = context;
    }

    public boolean isEnabled() {
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);;

        return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }


    public void requestActivation() {

        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(gpsOptionsIntent);

    }



}
