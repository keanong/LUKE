package com.xdel.luke;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objAuthRequest;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.Date;

public class NetworkChanged_BroadcastReceiver extends WakefulBroadcastReceiver {

    private Context acontext;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username = "";
    String password = "";
    String useridx = "";
    String ishomesuser = "";
    String[] UsernamePwd = new String[4];
    ConnectionDetector cd;
    private boolean isreg;

    public NetworkChanged_BroadcastReceiver() {

    }

    public void register(final Context context) {
        try {
            if (!isreg) {
                final IntentFilter filters = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                //filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                context.registerReceiver(this, filters);
                isreg = true;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at NetworkChanged_BroadcastReceiver register. Exception: " + s, "", "", "", "");
        }
    }

    public void unregister(final Context context) {
        try {
            if (isreg) {
                context.unregisterReceiver(this);
                isreg = false;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at NetworkChanged_BroadcastReceiver unregister. Exception: " + s, "", "", "", "");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        this.acontext = context;
        try {
            GcmPreferences = acontext.getSharedPreferences(MainActivity.class.getSimpleName(),
                    Context.MODE_PRIVATE);
            UpdateJobInfoPreferences = acontext.getSharedPreferences("UpdateJobInfoPrefs",
                    Context.MODE_PRIVATE);
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);

            if (cd.isConnectingToInternet()) {
                if (objGlobalMethods.getGCMPrefs(GcmPreferences, "connectionState").equals("false")) {
                    if (!username.equals("") && !password.equals("")) {
                        objGlobalMethods.writeToFile("Regain Internet Connection. Calling BackendRun. Driver: " + username + ", DriverIDX: " + useridx,
                                "", "", username, useridx);

                        login(username, password, ishomesuser, GcmPreferences, context, intent);
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "connectionState", "true");
                    }
                } else {
                    //is already connected to internet, do nothing
                }
            } else {
                objGlobalMethods.writeToFile("Lost Internet Connection. Driver: " + username + ", DriverIDX: " + useridx,
                        "", "", username, useridx);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "connectionState", "false");

            }
        } catch (Exception e) {
            e.printStackTrace();
            objGlobalMethods.writeToFile("Exception at NetworkChanged_BroadcastReceiver onReceive. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public void login(String username, String pwd, String ISHOMESUSER, SharedPreferences gcmp, Context ctx, Intent intent) {
        Date dt = new Date();
        String regid = FCMservice.getToken();

        try {
            if (!regid.isEmpty()) {
                if (Boolean.parseBoolean(ISHOMESUSER)) {
                    AsyncCallHomesWS task = new AsyncCallHomesWS(username, pwd);
                    task.execute();
                } else {
                    boolean hasXDelCommunalDeviceFile_ = objGlobalMethods.hasXDelCommunalDeviceFile();
                    Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    GCLogin task = new GCLogin(username, pwd, regid, "", lastknownlocation, hasXDelCommunalDeviceFile_, intent);
                    task.execute();
                }
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at NetworkChanged_BroadcastReceiver login. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    public class GCLogin extends AsyncTask<Void, Void, objAuthDriverResult> {
        private final String aLoginName;
        private final String aPassword;
        private final String aRegID;
        private final String aQRCode;
        private final Location aalocation;
        private final boolean hasXDelCommunalDeviceFile_;
        private final Intent intent_;

        GCLogin(String aaLoginName, String aaPassword, String aaRegID, String aaQRCode, Location aaalocation, boolean ahasXDelCommunalDeviceFile_, Intent aintent_) {
            this.aLoginName = aaLoginName;
            this.aPassword = aaPassword;
            this.aRegID = aaRegID;
            this.aQRCode = aaQRCode;
            this.aalocation = aaalocation;
            this.hasXDelCommunalDeviceFile_ = ahasXDelCommunalDeviceFile_;
            this.intent_ = aintent_;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
        }

        @Override
        protected objAuthDriverResult doInBackground(Void... params) {
            objAuthDriverResult res = null;
            try {
                Date dt = new Date();
                Long l = dt.getTime();
                String timestampstr = "/Date(" + l + ")/";

                Location lastknownlocation = aalocation;
                int Accuracy = lastknownlocation == null ? 0 : Math.round(lastknownlocation.getAccuracy() * 100);
                objAuthRequest req = new objAuthRequest(aLoginName, aPassword, aRegID, timestampstr,
                        lastknownlocation == null ? 0 : lastknownlocation.getLatitude(), lastknownlocation == null ? 0 : lastknownlocation.getLongitude(), Accuracy, aQRCode);

                req.setCommunalDevice(hasXDelCommunalDeviceFile_);
                res = WebService.getGCLoginStatus(req);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at NetworkChanged_BroadcastReceiver GCLogin doInBackground. " + e.getMessage().toString(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final objAuthDriverResult res) {
            Context ctx = myApp.getAppContext();
            try {
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                if (res != null) {
                    if (res.Status >= 0) {
                        //login succ
                        JobsHandler.backEndIsRunning = false;
                        BackendRun_IntentService.isRunning = false;
                        FCMservice.enablePush = false;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "connectionState", "true");

                        objGlobalMethods.writeToFile("starting NetworkChanged_BroadcastReceiver at NetworkSchedulerService.","","","","");
                        //start backendrun intentservice
                        ComponentName comp = new ComponentName(ctx.getPackageName(), BackendRun_IntentService.class.getName());
                        startWakefulService(ctx, (intent_.setComponent(comp)));
                    } else if (res.Status < 0) {
                        //err
                        JobsHandler.backEndIsRunning = false;
                        FCMservice.enablePush = false;
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
                        Intent parentIntent;
                        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
                        //ctx.startActivity(parentIntent);
                        myApp.currentActivity.startActivity(parentIntent);
                        myApp.currentActivity.finish();
                    }
                } else {
                    /*JobsHandler.backEndIsRunning = false;
                    FCMservice.enablePush = false;
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                    JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
                    Intent parentIntent;
                    parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
                    ctx.startActivity(parentIntent);
                    myApp.currentActivity.finish();*/
                }

            } catch (Exception e) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                objGlobalMethods.writeToFile("Exception caught at NetworkChanged_BroadcastReceiver GCLogin onPostExecute. " + e.getMessage().toString(), "", "", "", "");
            }
        }

    }

    public class AsyncCallHomesWS extends AsyncTask<Void, Void, Long> {
        private final String LoginName;
        private final String LoginPassword;

        AsyncCallHomesWS(String aLoginName, String aLoginPassword) {
            this.LoginName = aLoginName;
            this.LoginPassword = aLoginPassword;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

        }

        @Override
        protected Long doInBackground(Void... params) {
            return WebService.getHomesLoginStatus(LoginName, LoginPassword, "AuthUser");
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Long LoginSuccess) {
            //Error status is false

            if (LoginSuccess > 0) {

            } else {

            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}
