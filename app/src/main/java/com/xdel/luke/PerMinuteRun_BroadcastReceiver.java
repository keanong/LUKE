package com.xdel.luke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class PerMinuteRun_BroadcastReceiver extends BroadcastReceiver {
    private Context acontext;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    ConnectionDetector cd;
    public static int count;
    public static int COD_five_min_Count;
    private static int Battlvl_five_min_count;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        acontext = context;
        int queCount = 0;

        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        username = UsernamePwd[0];
        password = UsernamePwd[1];
        useridx = UsernamePwd[2];
        ishomesuser = UsernamePwd[3];
        COD_five_min_Count += 1;
        Battlvl_five_min_count += 1;

        //cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(null);
        if (cd.isConnectingToInternet()) {
            try {
                queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);
                if (queCount > 0) {
                    if (!username.equals("") && !password.equals("") && count >= 1) {
                        objGlobalMethods.writeToFile("Calling BackendRun on per minute run check. Driver: " + username + ", DriverIDX: " + useridx,
                                "", "", username, useridx);
                        count = 0;

                        objGlobalMethods.writeToFile("PerMinuteRun counter reset. Starting BackendRun_BroadcastReceiver.",
                                "", "", "", "");

                        JobsHandler.backEndIsRunning = false;
                        BackendRun_IntentService.isRunning = false;
                        FCMservice.enablePush = false;
                        acontext.sendBroadcast(new Intent(acontext, BackendRun_BroadcastReceiver.class));

                    }
                } else {
                    count = 0;
                }

                Integer battlvlInt = objGlobalMethods.getBattLvl(acontext);
                boolean isCharging = objGlobalMethods.getBattChargingState(acontext);
                Boolean isOnNonWorkingScreenBool = myApp.isOnNonWorkingScreen();
                Boolean isScreenOn = objGlobalMethods.isScreenOn(acontext);
                if ((battlvlInt == 30 || battlvlInt == 20 || battlvlInt == 15 || battlvlInt == 10 || battlvlInt == 5) && !isCharging) {
                    if (isScreenOn && isOnNonWorkingScreenBool) {
                        Boolean showBattChargeNoti = objGlobalMethods.checkshowBattChargeNoti();
                        if (!showBattChargeNoti) {
                            objGlobalMethods.promptNotiForBattCharging(GcmPreferences);
                        }
                    } else {
                        Boolean asd = true;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "showBattChargeNotiPrefStr", asd.toString());
                    }
                } /*else if (battlvlInt <= 65 && !isCharging) {
                    Gson gson = new Gson();
                    String HendersonLocationPrefStr = "";
                    HendersonLocationPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "HendersonLocationPrefStr");
                    if (!HendersonLocationPrefStr.isEmpty()) {

                        Location HendersonLocation = gson.fromJson(HendersonLocationPrefStr, Location.class);
                        Location lastKnownLocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                        double calcdist = LocationService.calcDistance(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), HendersonLocation.getLatitude(), HendersonLocation.getLongitude());
                        if (calcdist < 0.1) { //less than 100m from office
                            if (isScreenOn && isOnNonWorkingScreenBool) {
                                Boolean showBattChargeNoti = objGlobalMethods.checkshowBattChargeNoti();
                                if (!showBattChargeNoti) {
                                    objGlobalMethods.promptNotiForBattCharging(GcmPreferences);
                                }
                            } else {
                                Boolean asd = true;
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "showBattChargeNotiPrefStr", asd.toString());
                            }
                        }
                    } else {
                        MainActivity.setHendersonLocation(GcmPreferences);
                    }
                }*/ else {
                    //do nothing
                    Boolean asd = false;
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "showBattChargeNotiPrefStr", asd.toString());
                }

                //send battery timer
                if (Battlvl_five_min_count == 3) {
                    Battlvl_five_min_count = 0;
                    //objGlobalMethods.sendBattLvl(acontext,GcmPreferences);
                    acontext.sendBroadcast(new Intent(acontext, BattLvl_Receiver.class));
                }
                //send battery timer
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at PerMinuteRun_BroadcastReceiver onReceive. " + e.getMessage().toString(), "", "", "", "");
                count = 0;
                COD_five_min_Count = 0;
            }
        }
    }
}
