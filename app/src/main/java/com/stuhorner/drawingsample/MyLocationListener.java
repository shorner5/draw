package com.stuhorner.drawingsample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageButton;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;

/**
 * Created by Stu on 5/5/2016.
 */
public class MyLocationListener implements LocationListener {
    LocationManager locationManager;
    Activity activity;
    GeoFire geoFire = new GeoFire(MainActivity.rootRef);
    public static boolean hasLocation = false;
    ImageButton filter_near_me, filter_public;

    // call "new MyLocationListener(this) from an activity to instantiate.
    public MyLocationListener(Activity activity, @Nullable ImageButton filter_near_me, @Nullable ImageButton filter_public) {
        this.activity = activity;
        this.filter_near_me = filter_near_me;
        this.filter_public = filter_public;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.d("SecurityException", e.toString());
        }
    }

    // Gets the user's location once. To keep getting the current location continuously, comment out "close()"
    @Override
    public void onLocationChanged(Location location) {
        filter_near_me.setColorFilter(activity.getResources().getColor(R.color.colorPrimary));
        filter_public.setColorFilter(activity.getResources().getColor(R.color.lightGray));
        MainActivity.near_me = true;
        hasLocation = true;
        Log.d("Lat", Double.toString(location.getLatitude()));
        Log.d("Long", Double.toString(location.getLongitude()));
        geoFire.setLocation(MyUser.getInstance().getUID(), new GeoLocation(location.getLatitude(), location.getLongitude()));
        close();
    }

    // This is called if location is turned off. It should probably make something pop up and tell
    // the user to turn location on but we can do that later
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("MyLocationListener", "DISABLED");
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(activity.getResources().getString(R.string.gps_disabled));
        dialog.setPositiveButton(activity.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivityForResult(myIntent, 0);
            }
        });
        dialog.show();
    }

    //This is called when location is turned on
    @Override
    public void onProviderEnabled(String provider) {
        hasLocation = true;
        filter_near_me.setColorFilter(activity.getResources().getColor(R.color.colorPrimary));
        filter_public.setColorFilter(activity.getResources().getColor(R.color.lightGray));
        MainActivity.near_me = true;
        Log.d("location", "ENABLED");
    }

    // This doesn't matter
    @Override
    public void onStatusChanged(String provider, int status, Bundle b) {
        Log.d("location", "status");
    }

    // Stops listening for location
    public void close() {
        Log.d("MyLocationListener", "closing");
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.d("securityException", e.toString());
        }
    }
}
