package com.xdel.luke.JobScheduler;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BackendRun_IntentService;
import com.xdel.luke.BattLvl_Receiver;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.objClasses.objUpdateCOD_PostData;
import com.xdel.luke.templogger.MacDevicePair;
import com.xdel.luke.templogger.bttemploggerStandaloneclass;
import com.xdel.luke.templogger.bttemploggerclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class btLoggerService extends Service {

    public  final String TAG = "btLoggerService";
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context ctx = myApp.getApplicationCtx();
        try {
            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            String bttemploggerclassStarted = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "bttemploggerclassStarted");
            String bttemploggerStandaloneclassStarted = objGlobalMethods.getBTLoggerRelatedPrefs(btp, "bttemploggerStandaloneclassStarted");
            if (!objGlobalMethods.IsEmptyString(bttemploggerclassStarted)) {
                Log.i(TAG, "bttemploggerclass not started due bttemploggerclassStarted still running.");
            } else if (!objGlobalMethods.IsEmptyString(bttemploggerStandaloneclassStarted)) {
                Log.i(TAG, "bttemploggerclass not started due bttemploggerStandaloneclassStarted still running.");
            }else {
                bttemploggerclass btc = new bttemploggerclass();
                btc.StopScan();
                if (intent != null && intent.hasExtra("mdpsJsonStr")) {
                    String mdpsJsonStr = intent.getStringExtra("mdpsJsonStr");
                    String ARegID = intent.getStringExtra("ARegID");

                    if (!objGlobalMethods.IsEmptyString(mdpsJsonStr)) {
                        Gson gson = new Gson();
                        List<MacDevicePair> mdps = gson.fromJson(mdpsJsonStr, new TypeToken<List<MacDevicePair>>() {
                        }.getType());
                        btc.setMacDevicePairs(mdps);
                        btc.setARegID(ARegID);
                        btc.scan();
                    } else {
                        //do nothing
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at btLoggerService onStartCommand. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return START_STICKY;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
