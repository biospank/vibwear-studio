package it.vibwear.app.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by biospank on 04/03/16.
 */
public class GACServiceManager implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static GACServiceManager manager;

    private Context context;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    protected GACServiceManager(Context context) {
        this.context = context;
    }

    public static GACServiceManager getInstance(Context context) {
        if(manager == null) {
            manager = new GACServiceManager(context);
            manager.buildGoogleApiClient();
        }

        return manager;

    }

    public void connect() {
        // Connect the client.
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
    }

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public String getLocationUrlMap() {
        //             return servicesFrag.getLocationUrlMap();
        String query = "";
        String url = "";
        try {
            if(mLastLocation != null) {
                Log.i("Location", "latitude: " + mLastLocation.getLatitude());
                Log.i("Location", "longitude: " + mLastLocation.getLongitude());
                query = URLEncoder.encode(mLastLocation.getLatitude() + "N," + mLastLocation.getLongitude() + "W", "utf-8");
                url = "http://maps.google.com/maps?q=" + query;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            //                     e.printStackTrace();
        }

        return url;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("Vibwear", "GoogleApiClient connection established");
//             mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//        }

//             if (mRequestingLocationUpdates) {
//                     mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        createLocationRequest();
//        startLocationUpdates();
//         }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Vibwear", "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, "connection failded", Toast.LENGTH_LONG).show();
        Log.i("Vibwear", "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.i("Vibwear", "Location received: " + location.toString());
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

}
