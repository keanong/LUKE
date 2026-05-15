package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Date;


public class LocationProvider implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public abstract interface LocationCallback {
        public void handleNewLocation(Location location);
    }


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationCallback mLocationCallback;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Location mCurrentLocation;

    public LocationProvider(Context context, LocationCallback callback) {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationCallback = callback;

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)         // 10 seconds, in milliseconds
                    .setFastestInterval(5000)  // 5 second, in milliseconds
                    .setSmallestDisplacement(10); //10meters, minimum distance in meters from your last-location in order to trigger onLocationChanged

            mContext = context;
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at LocationProvider LocationProvider. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            objGlobalMethods.writeToFile("Exception caught at LocationProvider LocationProvider. Exception: " + e.getMessage().toString(),"","","","");
        }
    }

    public void connect() {
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at LocationProvider connect. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            objGlobalMethods.writeToFile("Exception caught at LocationProvider connect. Exception: " + e.getMessage().toString(),"","","","");
        }
    }


    public void disconnect() {
        try {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at LocationProvider disconnect. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            objGlobalMethods.writeToFile("Exception caught at LocationProvider disconnect. Exception: " + e.getMessage().toString(),"","","","");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        /*try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (Exception e){
            e.printStackTrace();
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at LocationProvider onConnected LocationServices.FusedLocationApi 1. " + e.getMessage().toString(),"","","","");
        }*/

        //Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        /*Location location = null;

        if (location == null) {
            try{
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            catch (Exception e){
                e.printStackTrace();
                Date dt = new Date();
                objGlobalMethods.writeToFile( "Exception caught at LocationProvider onConnected LocationServices.FusedLocationApi 2. " + e.getMessage().toString(), "", "", "", "");
            }
        }
        else {
            mLocationCallback.handleNewLocation(location);
        }*/

        //====================================================================================================================================================================================
        //using in ver 1.5.4
        try{
            //Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Location location = null;

            if (location == null) {
                try{
                    if (ContextCompat.checkSelfPermission(myApp.getAppContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Date dt = new Date();
                    objGlobalMethods.writeToFile( "Exception caught at LocationProvider onConnected LocationServices.FusedLocationApi 2. " + e.getMessage().toString(), "", "", "", "");
                }
            }
            else {
                mLocationCallback.handleNewLocation(location);
            }
        }
        catch (Exception e){
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at LocationProvider onConnected. Exception: " + e.toString(),"","","","");
        }
        //using in ver 1.5.4
        //====================================================================================================================================================================================
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution() && mContext instanceof Activity) {
            try {
                Activity activity = (Activity)mContext;
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocationCallback.handleNewLocation(location);
    }

}
