package com.example.robin.gps_moudle;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by robin on 31/1/18.
 */

public class GPS_Service extends Service {
    LocationListener listener;
    LocationManager manager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
             Double latitude = location.getLatitude();
             Double longitude = location.getLongitude();
             Intent i = new Intent("location_update");
             Bundle bundle = new Bundle();
             bundle.putDouble("lat",latitude);
             bundle.putDouble("lon",longitude);
             i.putExtras(bundle);
             sendBroadcast(i);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
             Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(i);
            }
        };
           manager =(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
           manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(manager !=null){
            manager.removeUpdates(listener);
        }
    }
}
