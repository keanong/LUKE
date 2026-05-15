package com.xdel.luke.BCnSVC;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.NFCTag;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCabinet;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjTradeInList;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Download_Data_IntentService extends IntentService {
    ConnectionDetector cd;

    public Download_Data_IntentService() {
        super("Download_Data_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Context ctx = myApp.getAppContext();
            objCabinet Cabinet = null;
            dbObjTradeInList[] TradeInList = null;
            NFCTag[] NFCTagList = null;
            SharedPreferences gcmPref;
            try {
                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);
                if (cd.isConnectingToInternet()) {
                    //objGlobalMethods.writeToFile("Download_Data_IntentService onHandleIntent started.","","","","");
                    String aregid = objGlobalMethods.getRegistrationIdOnly();

                    JobsHandler.processCODJobs();
                    TradeInList = WebService.GetTradeInList(aregid);
                    if (TradeInList != null && TradeInList.length > 0) {
                        DAO_Jobs.insertTradeInList(TradeInList);
                    }

                    NFCTagList = WebService.GetNFCTagsList(aregid);
                    if (NFCTagList != null && NFCTagList.length > 0) {
                        DAO_Jobs.insertNFCTAGS(NFCTagList);
                    }

                    //not required, already done in DrawerMainActivity & DrawerMainActivity2 GetDriverJobsCountSQLite
                    /*gcmPref = objGlobalMethods.getGcmPreferences();
                    ArrayList<objMPD_Info> mpds = objGlobalMethods.grabMPDFromServer();
                    objGlobalMethods.setMPDsPrefStr(mpds, gcmPref);

                    objGlobalMethods.grabSettingsFromServer();*/

                    //objGlobalMethods.writeToFile("Download_Data_IntentService onHandleIntent ended.","","","","");
                } else {
                    objGlobalMethods.writeToFile("Download_Data_IntentService onHandleIntent cannot run due no network.","","","","");
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at Download_Data_IntentService onHandleIntent. Exception: " + e.getMessage(),"","","","");
            }
        } else {
            objGlobalMethods.writeToFile("Download_Data_IntentService onHandleIntent intent == null.","","","","");
        }
    }
}
