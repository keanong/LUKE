package com.xdel.luke.JobScheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.xdel.luke.BackendRun_IntentService;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objAuthRequest;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.Date;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;

public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private ConnectivityReceiver mConnectivityReceiver;
    private Intent xintent;

    @Override
    public void onCreate() {
        super.onCreate();

        mConnectivityReceiver = new ConnectivityReceiver(this);
    }

    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        xintent = intent;
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //objGlobalMethods.writeToFile("mConnectivityReceiver registered","","","","");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        try {
            unregisterReceiver(mConnectivityReceiver);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at NetworkSchedulerService onStopJob: " + s,"","","","");
        } finally {
            return true;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        String message = isConnected ? "Regain Internet Connection @ NetworkSchedulerService." : "Lost Internet Connection @ NetworkSchedulerService.";
        if (isConnected) {
            //objGlobalMethods.writeToFile(message,"","","","");
            String username = "";
            String password = "";
            String ishomesuser = "";
            String[] UsernamePwd = new String[4];

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            ishomesuser = UsernamePwd[3];
            if (objGlobalMethods.getGCMPrefs(GcmPreferences,"connectionState").equals("false")) {
                if (!username.equals("") && !password.equals("")) {
                    Context context = myApp.getApplicationCtx();
                    login(username, password, ishomesuser, GcmPreferences, context, xintent);

                    JobsHandler.backEndIsRunning = false;
                    BackendRun_IntentService.isRunning =false;
                    FCMservice.enablePush = false;
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "connectionState", "true");

                    objGlobalMethods.writeToFile("starting BackendRun_IntentService at NetworkSchedulerService.","","","","");
                    Intent comp = new Intent(context, BackendRun_IntentService.class);
                    context.startService(comp);
                } else {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "connectionState", "false");
                    objGlobalMethods.writeToFile(message + " when is connected and username n pwd r empty. set connectionState to false.","","","","");
                }
            }
        } else {
            objGlobalMethods.setGCMPrefs(GcmPreferences,"connectionState","false");
            objGlobalMethods.writeToFile(message,"","","","");
        }
    }

    public void login(String username, String pwd, String ISHOMESUSER, SharedPreferences gcmp, Context ctx, Intent intent){
        String regid = FCMservice.getToken();
        try {
            if (!regid.isEmpty()){
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
            objGlobalMethods.writeToFile("Exception caught at NetworkChanged_BroadcastReceiver login. Exception: " + e.getMessage().toString(),"","","","");
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
                objGlobalMethods.writeToFile("Exception caught at NetworkSchedulerService GCLogin doInBackground. " + e.getMessage().toString(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final objAuthDriverResult res) {
            Context ctx = myApp.getAppContext();
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            try {
                if (res != null) {
                    if (res.Status >= 0) {
                        //login succ
                        JobsHandler.backEndIsRunning = false;
                        BackendRun_IntentService.isRunning =false;
                        FCMservice.enablePush = false;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "connectionState", "true");

                        objGlobalMethods.writeToFile("starting BackendRun_IntentService at NetworkSchedulerService.","","","","");
                        Intent comp = new Intent(ctx, BackendRun_IntentService.class);
                        ctx.startService(comp);
                    } else if (res.Status < 0) {
                        //err
                        JobsHandler.backEndIsRunning = false;
                        FCMservice.enablePush = false;
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
                        Intent parentIntent;
                        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
                        //ctx.startActivity(parentIntent);
                        myApp.currentActivity.startActivity(parentIntent);
                        myApp.currentActivity.finish();
                    }
                } else {
                    objGlobalMethods.writeToFile("GCLogin return null @ NetworkSchedulerService GCLogin. Do nothing.","","","","");
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
                objGlobalMethods.writeToFile("Exception caught at NetworkSchedulerService GCLogin onPostExecute. " + e.getMessage().toString(), "", "", "", "");
            }
        }

    }

    public class AsyncCallHomesWS extends AsyncTask<Void, Void, Long> {
        private final String LoginName;
        private final String LoginPassword;

        AsyncCallHomesWS(String aLoginName, String aLoginPassword){
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
