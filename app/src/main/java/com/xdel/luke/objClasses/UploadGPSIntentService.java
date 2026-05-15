package com.xdel.luke.objClasses;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.WebService;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.L2_GetJobTemperatureResult;
import com.xdel.luke.templogger.Temperature_Log;
import com.xdel.luke.templogger.temploggerclass;

import java.util.Date;

public class UploadGPSIntentService extends IntentService {

    private static Intent UploadGPSIntentSvc;
    public static Boolean isRunning = false;
    ConnectionDetector cd;

    public UploadGPSIntentService() {
        super("UploadGPSIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = myApp.getAppContext();
        Boolean success = false;
        Boolean updateBattSuccess = false;
        SharedPreferences gcmPrefs, UpdateJobInfoPrefs, LocationPrefs;
        gcmPrefs = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPrefs = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();

        UploadGPSIntentSvc = intent;

        String RegID = gcmPrefs.getString(objGlobalVariables.PROPERTY_REG_ID, "");

        objRtnGPS[] objRtnGPSpsALQue;
        Gson gson = new Gson();

        try {
            String username;
            String password;
            String[] UsernamePwd = new String[4];
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmPrefs);
            username = UsernamePwd[0];
            password = UsernamePwd[1];

            if (!username.equals("") && !password.equals("")) {
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    //String UploadRtnGPSALPrefStr = UpdateJobInfoPrefs.getString("GPSarrayQue", "");
                    String UploadRtnGPSALPrefStr = LocationPrefs.getString("GPSarrayQue", "");
                    objRtnGPSpsALQue = gson.fromJson(UploadRtnGPSALPrefStr, objRtnGPS[].class);

                    if (objRtnGPSpsALQue != null && objRtnGPSpsALQue.length > 0) {
                        //SharedPreferences.Editor editor = UpdateJobInfoPrefs.edit();
                        SharedPreferences.Editor editor = LocationPrefs.edit();
                        editor.putString("GPSarrayQue", "");
                        editor.apply();

                        //update gps n battlvl
                        Integer battlvlInt = objGlobalMethods.getBattLvl(myApp.getAppContext());
                        success = WebService.UploadGPS2(RegID, objRtnGPSpsALQue, battlvlInt, "L2_Upload_GPSBatt");
                        if (success != null) {
                            if (!success) {
                                //if result return false, do not add back to que, update battlvl
                                //objGlobalMethods.writeToFile( "UploadGPSIntentService L2_Upload_GPSBatt returns false, not add back to Que.", "", "", "", "");
                            } else {
                                ////if result return true, do nothing
                            }
                        } else if (success == null) {
                            JobsHandler.addUploadGPSarrQuePrefsNew(LocationPrefs, objRtnGPSpsALQue);
                            //JobsHandler.addUploadGPSarrQuePrefs(UpdateJobInfoPrefs, objRtnGPSpsALQue);
                            //objGlobalMethods.writeToFile("UploadGPSIntentService L2_Upload_GPSBatt returns null, add back to Que.", "", "", "", "");
                        }
                    } else if (objRtnGPSpsALQue != null && objRtnGPSpsALQue.length == 0) {
                        //SharedPreferences.Editor editor = UpdateJobInfoPrefs.edit();
                        SharedPreferences.Editor editor = LocationPrefs.edit();
                        editor.putString("GPSarrayQue", "");
                        editor.apply();
                        objGlobalMethods.sendBattLvl(context, gcmPrefs);
                    } else if (objRtnGPSpsALQue == null) {
                        //SharedPreferences.Editor editor = UpdateJobInfoPrefs.edit();
                        SharedPreferences.Editor editor = LocationPrefs.edit();
                        editor.putString("GPSarrayQue", "");
                        editor.apply();
                        objGlobalMethods.sendBattLvl(context, gcmPrefs);
                    }

                    /*//temp logger
                    temploggerclass.chkgrabtemps(gcmPrefs, RegID, false);
                    //temp logger*/
                }
            }
            UploadGPSReceiver.completeWakefulIntent(UploadGPSIntentSvc);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at UploadGPSIntentService. Exception: " + e.toString(), "", "", "", "");
            /*UpdateJobInfoPrefs = objGlobalMethods.getUpdateJobInfoPreferences();
            SharedPreferences.Editor editor = UpdateJobInfoPrefs.edit();*/
            LocationPrefs = objGlobalMethods.getLocationSP();
            SharedPreferences.Editor editor = LocationPrefs.edit();
            editor.putString("GPSarrayQue", "");
            editor.apply();
            UploadGPSReceiver.completeWakefulIntent(UploadGPSIntentSvc);
        }
    }
}
