package com.xdel.luke;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;

import java.util.Date;

public class BattLvl_IntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    private String regid;
    SharedPreferences gcmPrefs;

    public BattLvl_IntentService() {
        super("BattLvl_IntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Integer battlvlInt = 0;
        String battlvlintStr = "";
        Context context = myApp.getAppContext();
        Boolean success = false;
        try{
            gcmPrefs = objGlobalMethods.getGcmPreferences();
            battlvlInt = objGlobalMethods.getBattLvl(context);
            //regid = getRegistrationId(context);
            regid = objGlobalMethods.getRegistrationId(null, gcmPrefs);
            String username;
            String password;
            String[] UsernamePwd = new String[4];

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmPrefs);
            username = UsernamePwd[0];
            password = UsernamePwd[1];

            if (!username.equals("") && !password.equals("")) {
                success = WebService.UpdateBattLvl(regid,battlvlInt);

                if (success == null){
                    objGlobalMethods.writeToFile("UpdateBattLvl return null.","","","","");
                } else if (success){
                    //objGlobalMethods.writeToFile("UpdateBattLvl successful.","","","","");
                } else if (!success){
                    objGlobalMethods.writeToFile("UpdateBattLvl returns false.","","","","");
                }
            }
        }
        catch (Exception e){
            objGlobalMethods.writeToFile("Exception caught at BattLvl_IntentService. Exception: " + e.toString(),"","","","");
        }
    }
}
