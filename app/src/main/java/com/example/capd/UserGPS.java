package com.example.capd;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;


public class UserGPS{

    Context context;
    Location location;
    double longitude;
    double latitude;
    String provider;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000;
    protected LocationManager locationManager;

    public UserGPS(Context context){
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean gpsEnabled =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gpsEnabled && !networkEnabled) {

            } else {
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                } else
                    return null;


                if (gpsEnabled){
                    if (location == null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null){
                            location =
                                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null){
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                            }
                        }
                    }
                }

                if (networkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);

                    if (locationManager != null) {
                        location =
                                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                }

            }
        } catch (Exception e) {
            Log.d("UserGPS", "" + e.toString());
        }
        return location;
    }


    final LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            provider = location.getProvider();
        }


        @Override
        public void onProviderEnabled(String provider) {
            Log.d("GPS", "onProviderEnabled, provider:" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("GPS", "onProviderDisabled, provider:" + provider);
        }

    };

    public double getLongitude(){
        if (location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }


    public void stopGPS(){
        if (locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }


}

