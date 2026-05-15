package com.xdel.luke.objClasses;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.tzone.bluetooth.BleManager;
import com.xdel.luke.BCnSVC.ActiveService;
import com.xdel.luke.ExceptionHandler.DefaultExceptionHandler;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kean.ong on 14/06/2016.
 */
public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    public static GoogleApiClient googleApiClient;
    private SharedPreferences UpdateJobInfoPrefs, GCMprefs, LocationPrefs;
    private String username = "";
    private String password = "";
    private String useridx = "";
    private String ishomesuser = "";
    private String[] UsernamePwd = new String[4];
    public static Location mLastLocationReceived;
    private static final int ONE_MINUTES = 1000 * 60 * 1;
    private static Intent locationservice;
    private static PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        int rtnValue = START_STICKY;
        try {
            locationservice = intent;
            if (intent != null) {
                if (!currentlyProcessingLocation) {
                    currentlyProcessingLocation = true;
                    objGlobalMethods.GPSalarmStarted = true;
                    startTracking();
                }
                rtnValue = START_STICKY;
            } else {
                /*Context ctx = myApp.getAppContext();
                Activity activity = null;
                if (ctx instanceof Activity) {
                    activity = (Activity)ctx;
                }

                objGlobalMethods.writeToFile("LocationService Intent is null. Restart app.", "", "", "", "");
                DefaultExceptionHandler.getLogFiles();

                Intent intenta = new Intent(activity == null ? myApp.getAppContext() : activity, MainActivity.class);
                intenta.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        myApp.getInstance().getBaseContext(), 0, intenta, PendingIntent.FLAG_ONE_SHOT);

                //Restart your app after 2 seconds
                AlarmManager mgr = (AlarmManager) myApp.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
                //finishing the activity.
                if (activity != null){
                    activity.finish();
                }
                //Stopping application
                //System.exit(2);
                System.exit(1);*/



                String[] UsernamePwd = new String[4];
                String username;
                String password;
                String ishomesuser;
                SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                ishomesuser = UsernamePwd[3];
                if (objGlobalMethods.isNotHomesUser(username, password, ishomesuser)) {
                    if (!currentlyProcessingLocation) {
                        objGlobalMethods.writeToFile("Intent is null, user still logged in. Start Tracking.\nGPS Alarm started. Start Tracking.", "", "", "", "");
                        if (!objGlobalMethods.isActiveServiceRunning()) {
                            objGlobalMethods.startActiveForegroundSvc("");
                        } else {
                            Context ctx = myApp.getAppContext();
                            Intent i = new Intent(ctx, ActiveService.class);
                            i.setAction(ActiveService.ACTION_STARTGPSALARMSERVICE);
                            ctx.startService(i);
                        }
                    }
                    rtnValue = START_STICKY;
                } else {
                    objGlobalMethods.writeToFile("Intent is null, user not logged in. Stop Tracking.", "", "", "", "");
                    rtnValue = START_NOT_STICKY;
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at LocationService onStartCommand. Exception: " + e.getMessage().toString(), "", "", "", "");
            rtnValue = START_STICKY;
        } finally {
            return rtnValue;
        }
    }

    private void startTracking() {
        try {
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
                if (googleApiClient == null) {
                    googleApiClient = new GoogleApiClient.Builder(this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                }

                if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                    googleApiClient.connect();
                }
            } else {
                objGlobalMethods.writeToFile("unable to connect to google play services.", "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at LocationService startTracking. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Context acontext = myApp.getAppContext();
            GCMprefs = objGlobalMethods.getGcmPreferences();
            String stoplocationService = "";
            stoplocationService = objGlobalMethods.getGCMPrefs(GCMprefs, "stopLocationService");
            if (stoplocationService.equals("true")) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                objGlobalMethods.setGCMPrefs(GCMprefs, "stopLocationService", "");

                if (locationservice != null) {
                    LocationService_Receiver.completeWakefulIntent(locationservice);
                }

                return;
            } else if (stoplocationService.equals("") || stoplocationService.equals("false")) {
                //do nothing
            }

            if (location == null) {
                objGlobalMethods.writeToFile("null Location grabbed. not sending any location back.", "", "", "", "");
                return;
            }

            Boolean acceptedLocation = false;

            if (location.getLatitude() == 0 && location.getLongitude() == 0) {
                if (mLastLocationReceived != null) {
                    long diffInMillisec2 = location.getTime() - mLastLocationReceived.getTime();
                    long diffSeconds2 = diffInMillisec2 / 1000;
                    if (diffSeconds2 <= 300) {
                        //if lastknownlocation is less than 5min old, use it
                        location = mLastLocationReceived;
                        acceptedLocation = true;
                    } else if (diffSeconds2 > 300) {
                        //if lastknowlocation is more than 5min old, dispose it
                        //mLastLocationReceived = null; //purposely null it on 21/12/2017
                        acceptedLocation = false;
                        objGlobalMethods.writeToFile("Last known location not accepted due pass 5min.", "", "", "", "");
                    }
                } else {
                    objGlobalMethods.writeToFile("Last known location not accepted due is null.", "", "", "", "");
                }
            } else {
                acceptedLocation = LocationAccepted(location);
            }

            if (acceptedLocation) {
                int Accuracy = Math.round(location.getAccuracy() * 100);
                processLocations(location, Accuracy, acontext);
                //processLocations(location.getLatitude(), location.getLongitude(), Accuracy, acontext);
                ////processLocations(1.284711, 103.821135, 100, acontext);
            } else {
                //objGlobalMethods.writeToFile( "GPS not accepted.", "", "", "", "");
            }

            //objGlobalMethods.startGPSAlarmHandler();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile( "Exception caught at LocationService onLocationChanged. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at LocationService onLocationChanged. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private Boolean LocationAccepted(Location alocation) {
        Boolean accepted = false;
        try {

            //if (alocation.getAccuracy() <= 75.0f) {
            if (alocation.getLongitude() > 0 && alocation.getLatitude() > 0) {
                if (mLastLocationReceived == null) {
                    mLastLocationReceived = alocation;
                    return true;
                }

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                DecimalFormat f = new DecimalFormat("##.000000");
                double Lat = alocation.getLatitude();
                double Lng = alocation.getLongitude();
                float distMeter = 0;
                String distKMstr = "";
                float spdMeterPerSec = 0;
                float spdKMperHour = 0;
                float spdMeterPerHour = 0;
                distMeter = alocation.distanceTo(mLastLocationReceived);
                distKMstr = f.format(distMeter / 1000);
                double calcdist = calcDistance(mLastLocationReceived.getLatitude(), mLastLocationReceived.getLongitude(), alocation.getLatitude(), alocation.getLongitude());

                long diffInMillisec = alocation.getTime() - mLastLocationReceived.getTime();
                long diffSeconds = diffInMillisec / 1000;
                if (distMeter > 0) {
                    spdMeterPerSec = distMeter / diffSeconds; //meters per sec
                    spdKMperHour = (spdMeterPerSec * 3600) / 1000;
                    ////27.7777 equals to 27.7777m per sec equals to 100km/hr
                    //maxDist = 28 * diffSeconds; //max distance in meters travelled over the period between location n mLastLocationReceived

                    long timeA = mLastLocationReceived.getTime();
                    long timeB = alocation.getTime();

                    if (spdMeterPerSec <= 28) {
                        accepted = true;
                        /*String gpsLogMsg = "";
                        gpsLogMsg = "Location A Lat, Lng: " + mLastLocationReceived.getLatitude() + ", " + mLastLocationReceived.getLongitude() + ", at time: " + df.format(timeA) +
                                ", Location B Lat, Lng: " + Lat + ", " + Lng + ", at time: " + df.format(timeB) +
                                ", in " + diffSeconds + " seconds" +
                                ", Manual Calculated Dist travelled: " + distKMstr + "KM," +
                                ", Formulated calcDistance Dist travelled: " + calcdist + "KM," +
                                ", Spd: " + spdKMperHour + "KM/H.";

                        Context acontext = myApp.getAppContext();
                        GCMprefs = acontext.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                        objGlobalMethods.setGCMPrefs(GCMprefs, "gpsLogMsg", gpsLogMsg);*/
                        mLastLocationReceived = alocation;
                    }
                } else {
                    accepted = true;
                }
            }
            //}
        } catch (Exception e) {
            accepted = false;
        } finally {
            return accepted;
        }
    }

    public static double ToRadian(double val) {
        return val * (Math.PI / 180);
    }

    public static double DiffRadian(double val1, double val2) {
        return ToRadian(val2) - ToRadian(val1);
    }

    public static double calcDistance(double lat1, double lng1, double lat2, double lng2) {
        double calcdist = 0;
        double radius = 6371;
        try {
            calcdist = radius * 2 * Math.asin(Math.min(1, Math.sqrt((Math.pow(Math.sin((DiffRadian(lat1, lat2)) / 2.0), 2.0) + Math.cos(ToRadian(lat1)) * Math.cos(ToRadian(lat2)) * Math.pow(Math.sin((DiffRadian(lng1, lng2)) / 2.0), 2.0)))));
        } catch (Exception e) {
            calcdist = 0;
        } finally {
            return calcdist;
        }
    }

    public static void stopLocationUpdates() {
        try {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                googleApiClient.disconnect();
                if (locationservice != null) {
                    LocationService_Receiver.completeWakefulIntent(locationservice);
                } else {
                    //objGlobalMethods.writeToFile( "LocationService stopLocationUpdates locationservice is null, NO completeWakefulIntent", "", "", "", "");
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at LocationService stopLocationUpdates. Exception: " + e.getMessage().toString(), "", "", "", "");
        }

    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        try {
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000); // milliseconds
            locationRequest.setFastestInterval(5000); // the fastest rate in milliseconds at which your app can handle location updates
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ContextCompat.checkSelfPermission(myApp.getAppContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at LocationService onConnected. Exception: " + e.getMessage().toString() + "\nStack; " + stack,"","","","");
            objGlobalMethods.writeToFile("Exception caught at LocationService onConnected. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //objGlobalMethods.writeToFile("Location Service connection failed.","","","","");
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //objGlobalMethods.writeToFile("Location Service connection suspended.","","","","");
    }

    //private void processLocations(double aLat, double aLng, int Accuracy, Context acontext) {
    private void processLocations(Location loc, int Accuracy, Context acontext) {
        try {
            GCMprefs = acontext.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            //UpdateJobInfoPrefs = acontext.getSharedPreferences("UpdateJobInfoPrefs", Context.MODE_PRIVATE);
            LocationPrefs = objGlobalMethods.getLocationSP();

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GCMprefs);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];

            /*Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            JobsHandler.addUploadGPSQueALPrefs(UpdateJobInfoPrefs, timestampstr, aLat, aLng, Accuracy);*/
            Long l = loc.getTime();
            String timestampstr = "/Date(" + l + ")/";
            //JobsHandler.addUploadGPSQueALPrefs(UpdateJobInfoPrefs, timestampstr, loc.getLatitude(), loc.getLongitude(), Accuracy);
            JobsHandler.addUploadGPSQueALPrefs(LocationPrefs, timestampstr, loc.getLatitude(), loc.getLongitude(), Accuracy);

            UploadGPSorNot2(acontext);

            /*// Are we charging / charged?
            boolean isCharging = objGlobalMethods.getBattChargingState(acontext);
            //get batt lvl
            Integer battlvlInt = objGlobalMethods.getBattLvl(acontext);

            if (battlvlInt > 20 || (battlvlInt <= 20 && isCharging)) {
                UploadGPSorNot2(acontext);
            } else if (battlvlInt <= 20 && !isCharging) {
                ////do nothing dun send back anything, captured GPS is already in que, update battlvl
                objGlobalMethods.writeToFile("GPS not sent due battlvl less than 20% and device is not charging.","","","","");
            }*/
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at LocationService processLocations. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void UploadGPSorNot2(Context acontext) {
        objRtnGPS[] objRtnGPSpsALQue;
        int count;
        String countStr = "";
        Gson gson = new Gson();
        try {
            //String UploadRtnGPSALPrefStr = UpdateJobInfoPrefs.getString("GPSarrayQue", "");
            String UploadRtnGPSALPrefStr = LocationPrefs.getString("GPSarrayQue", "");
            countStr = GCMprefs.getString("GPSsendingIntervalCount", "");
            if (!countStr.equals("")) {
                count = Integer.parseInt(countStr);
            } else {
                count = 5;
            }

            objRtnGPSpsALQue = gson.fromJson(UploadRtnGPSALPrefStr, objRtnGPS[].class);
            if (objRtnGPSpsALQue != null && objRtnGPSpsALQue.length > 0 && objRtnGPSpsALQue.length >= count) {
                //objGlobalMethods.writeToFile("Attempt to send GPS.","","","","");
                sendBroadcast(new Intent(acontext, UploadGPSReceiver.class));
            } else if (objRtnGPSpsALQue != null && objRtnGPSpsALQue.length == 0) {
                /*SharedPreferences.Editor editor = UpdateJobInfoPrefs.edit();
                editor.putString("GPSarrayQue", "");
                editor.apply();*/
                objGlobalMethods.writeToFile("GPS not sent due less than que count.", "", "", "", "");
            } else if (objRtnGPSpsALQue == null) {
                //SharedPreferences.Editor editor = UpdateJobInfoPrefs.edit();
                SharedPreferences.Editor editor = LocationPrefs.edit();
                editor.putString("GPSarrayQue", "");
                editor.apply();
                objGlobalMethods.writeToFile("GPS not sent due que count is null.", "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at LocationService UploadGPSorNot2. Exception: , " + e.getMessage().toString(),
                    "", "", "", "");
            JobsHandler.sendErrorEmail("Exception caught at LocationService UploadGPSorNot2 for Driver: " + username + ", " + e.getMessage().toString());
        }
    }

    private Boolean isIdling() {
        Boolean asd = false;
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LocationService isIdling. Excpetion: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    private Boolean arrivedAtPUDIPlocation() {
        Boolean asd = false;
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LocationService arrivedAtPUDIPlocation. Excpetion: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

}
