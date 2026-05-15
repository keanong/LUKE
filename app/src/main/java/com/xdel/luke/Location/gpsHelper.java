package com.xdel.luke.Location;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.xdel.luke.MainActivity;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class gpsHelper implements LocationListener {

    private SharedPreferences GcmPreferences;
    private static LocationManager locationManager;
    private static String provider;
    private static Location location;

    public static void activateGPS(){
        try {

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at gpsHelper activateGPS. Exception: " + s,"","","","");
        }
    }

    private void requestLocation(){
        try {

        } catch (Exception e) {
            String s = e.getMessage();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            float lat = (float) (location.getLatitude());
            float lng = (float) (location.getLongitude());
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MainActivity.class) {
                String gpsLocked = objGlobalMethods.getGCMPrefs(GcmPreferences, "gpsLocked");
                if (lat > 0 && lng > 0 && (gpsLocked.isEmpty() || gpsLocked.equals("") || gpsLocked == "")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "gpsLocked", "true");
                    //isNearHendersonOfc = objGlobalMethods.isNearHendersonOfc(lat, lng);
                }
            } else {

            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}
