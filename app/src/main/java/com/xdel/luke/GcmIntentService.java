package com.xdel.luke;

/**
 * Created by kean.ong on 04/05/2015.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BCnSVC.ActiveService;
import com.xdel.luke.messagingClasses.ChatMessage;
import com.xdel.luke.messagingClasses.ChatMsgSenders;
import com.xdel.luke.messagingClasses.GetAllMsg_IntentService;
import com.xdel.luke.messagingClasses.Status;
import com.xdel.luke.messagingClasses.UserType;
import com.xdel.luke.messagingClasses.objChatMessage;
import com.xdel.luke.objClasses.LocationProvider;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.objClasses.objUpdateJobInfoStatus;
import com.xdel.luke.sqlite.DAO_Jobs;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class GcmIntentService extends IntentService {
    public static Activity currentActivity;
    public static final Object CURRENTACTIVIYLOCK = new Object();
    public static final int NOTIFICATION_ID = 1;
    public static int myNOTIFICATION_ID = 1;
    private static String regid;
    public static NotificationManager mNotificationManager;
    public static String msg = "";
    public static Bundle notificationExtras;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    Context context;
    public static final String TAG = "Luke";
    public static String newPIPhash = "";
    public static String newPUhash = "";
    public static String newDIPhash = "";
    public static String newDELhash = "";
    public static String newFPhash = "";
    public static String newFDhash = "";
    public static String sysPIPhash = "";
    public static String sysPUhash = "";
    public static String sysDIPhash = "";
    public static String sysDELhash = "";
    public static String sysFPhash = "";
    public static String sysFDhash = "";
    private static final Semaphore mutex = new Semaphore(1);
    private boolean hasNewJobs = false;
    private LocationProvider mLocationProvider;
    private double Lat, Lng;
    private SharedPreferences UpdateJobInfoPreferences;
    private String username = "";
    private String password = "";
    private String useridx = "";
    private String ishomesuser = "";
    private String[] UsernamePwd = new String[4];

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private SharedPreferences getUpdateJobInfoPreferences() {
        return getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);
    }

    public static boolean notiNotRead = false;
    public static AlarmManager manager2;
    private static PendingIntent pendingIntent2;
    private static List<objUpdateJobInfoStatus> JobInfoListInQueForStatusUpdate;
    private static int UpdateJobInfoStatusQueCount;
    public static boolean enablePush = true;


    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (CURRENTACTIVIYLOCK) {

            try {
                ArrayList JobHashesAL = new ArrayList();
                JSONArray JobHashesJsonArr;
                ArrayList sysJobHashesAL;

                //context = getApplicationContext();
                context = myApp.getAppContext();
                final SharedPreferences prefs = getGcmPreferences();
                UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
                regid = prefs.getString(objGlobalVariables.PROPERTY_REG_ID, "");
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
                // The getMessageType() intent parameter must be the intent you received
                // in your BroadcastReceiver.
                String messageType = gcm.getMessageType(intent);

                Bundle extras = intent.getExtras();
                notificationExtras = extras;

                String disablePush = objGlobalMethods.getGCMPrefs(prefs, "disablePush");
                if (!disablePush.toString().equals("")) {
                    return;
                }

                if (!extras.isEmpty()) {
                    /*
                     * Filter messages based on message type. Since it is likely that GCM will be
                     * extended in the future with new message types, just ignore any message types you're
                     * not interested in, or that you don't recognize.
                     */
                    if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                        sendNotification(this, "Send error: " + extras.toString());
                    } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                        sendNotification(this, "Deleted messages on server: " + extras.toString());
                        // If it's a regular GCM message, do some work.
                    } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                        sysPIPhash = prefs.getString("PIPhashName", "");
                        sysPUhash = prefs.getString("PUhashName", "");
                        sysDIPhash = prefs.getString("DIPhashName", "");
                        sysDELhash = prefs.getString("DELhashName", "");
                        sysFPhash = prefs.getString("FPhashName", "");
                        sysFDhash = prefs.getString("FDhashName", "");

                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(prefs);
                        username = UsernamePwd[0];
                        password = UsernamePwd[1];
                        useridx = UsernamePwd[2];
                        ishomesuser = UsernamePwd[3];

                        /*if (DAO_Jobs.databaseIsOpen()) {
                            DAO_Jobs daojobs = new DAO_Jobs(this);
                            try {
                                daojobs.open();
                            } catch (SQLException sqlex) {
                                String stack = JobsHandler.getStackTraceString(sqlex);
                                objGlobalMethods.writeToFile("Exception caught at GcmIntentService onHandleIntent. Exception: " + sqlex.getMessage().toString() + "\n" + "Stack: " + stack, "", "", "", "");
                                JobsHandler.sendErrorEmail("Exception caught at GCMIntentService onHandleIntent for " + username + ". Exception: "
                                        + "\n" + sqlex.getMessage().toString() + "\n" + "Stack: " + stack);
                            }
                        }*/

                        if (enablePush) {
                            if (extras.getString("JobHashes") != null) {
                                try {
                                    Gson gson = new Gson();
                                    JobHashesJsonArr = new JSONArray(extras.getString("JobHashes").toString());
                                    JobHashesAL = gson.fromJson(JobHashesJsonArr.toString(), ArrayList.class);
                                    Date dt = new Date();

                                    if (JobHashesAL != null && JobHashesAL.size() > 0) {
                                        JobInfoListInQueForStatusUpdate = getJobInfoListInQueForStatusUpdate();
                                        //PIP------------------------------------------------------------
                                        newPIPhash = (String) JobHashesAL.get(0);

                                        //temporary loggingcode - remove once not required
                                        if (extras.getString("rtNew") != null) {
                                            objGlobalMethods.writeToFile("rtNew JobsIDXs", extras.getString("rtNew"), "null", username, useridx);
                                        }

                                        if (!newPIPhash.equals("")) {
                                            if (!newPIPhash.equals(sysPIPhash)) {
                                                //update
                                                if (extras.getString("rtNew") != null) {
                                                    try {
                                                        mutex.acquire();
                                                        objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at GcmIntentService when newPIPhash is not empty and not equal sysPIPhash", extras.getString("rtNew"), "null", username, useridx);
                                                        RegetSpecificJobCategoryCountSQLite(0, context, true, newPIPhash, prefs, 0,
                                                                extras.getString("rtNew"), objGlobalVariables.rtNewJobsIDXarrName,
                                                                objGlobalVariables.PIPhashName, objGlobalVariables.JobInfoPIPALjsonPrefName,
                                                                objGlobalVariables.PIPjobsCountName);
                                                    } catch (InterruptedException e) {
                                                        objGlobalMethods.writeToFile("Calling BackendRun. InterruptedException when newPIPhash is not empty string, " + e.getMessage().toString(),
                                                                "", "", username, useridx);
                                                        JobsHandler.sendErrorEmail("Calling BackendRun. InterruptedException when newPIPhash is not empty string for Driver: " + username + ", " + e.getMessage().toString());
                                                        Context ctx = myApp.getAppContext();
                                                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                                                    } finally {
                                                        mutex.release();
                                                    }
                                                }
                                            }
                                        } else {
                                            DAO_Jobs.deleteJobsByCatTypeID(0);
                                            if (sysPIPhash != "") {
                                                updateDrawerPanel(0, objGlobalVariables.PIPhashName, "", true);
                                            } else if (sysPIPhash == "") {
                                                updateDrawerPanel(0, objGlobalVariables.PIPhashName, "", false);
                                            }
                                        }

                                        //PU------------------------------------------------------------
                                        newPUhash = (String) JobHashesAL.get(1);

                                        //temporary loggingcode - remove once not required
                                        if (extras.getString("rtPicked") != null) {
                                            objGlobalMethods.writeToFile("rtPicked JobsIDXs", extras.getString("rtPicked"), "null", username, useridx);
                                        }

                                        if (!newPUhash.equals("")) {
                                            if (!newPUhash.equals(sysPUhash)) {
                                                //update
                                                if (extras.getString("rtPicked") != null) {
                                                    try {
                                                        mutex.acquire();
                                                        objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at GcmIntentService when newPUhash is not empty and not equal sysPUhash", extras.getString("rtPicked"), "null", username, useridx);
                                                        RegetSpecificJobCategoryCountSQLite(1, context, false, newPUhash, prefs, 1,
                                                                extras.getString("rtPicked"), objGlobalVariables.rtPickedJobsIDXarrName,
                                                                objGlobalVariables.PUhashName, objGlobalVariables.JobInfoPUALjsonPrefName,
                                                                objGlobalVariables.PUjobsCountName);
                                                    } catch (InterruptedException e) {
                                                        objGlobalMethods.writeToFile("Calling BackendRun. InterruptedException when newPUhash is not empty string, " + e.getMessage().toString(),
                                                                "", "", username, useridx);
                                                        JobsHandler.sendErrorEmail("Calling BackendRun. InterruptedException when newPUhash is not empty string for Driver: " + username + ", " + e.getMessage().toString());
                                                        Context ctx = myApp.getAppContext();
                                                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                                    } finally {
                                                        mutex.release();
                                                    }
                                                }
                                            }
                                        } else {
                                            DAO_Jobs.deleteJobsByCatTypeID(1);
                                            if (sysPUhash != "") {
                                                updateDrawerPanel(1, objGlobalVariables.PUhashName, "", true);
                                            } else if (sysPUhash == "") {
                                                updateDrawerPanel(1, objGlobalVariables.PUhashName, "", false);
                                            }
                                        }

                                        //DIP------------------------------------------------------------
                                        newDIPhash = (String) JobHashesAL.get(2);

                                        //temporary loggingcode - remove once not required
                                        if (extras.getString("rtDelivery") != null) {
                                            objGlobalMethods.writeToFile("rtDelivery JobsIDXs", extras.getString("rtDelivery"), "null", username, useridx);
                                        }

                                        if (!newDIPhash.equals("")) {
                                            if (!newDIPhash.equals(sysDIPhash)) {
                                                //update
                                                if (extras.getString("rtDelivery") != null) {
                                                    try {
                                                        mutex.acquire();
                                                        objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at GcmIntentService when newDIPhash is not empty and not equal sysDIPhash", extras.getString("rtDelivery"), "null", username, useridx);
                                                        RegetSpecificJobCategoryCountSQLite(2, context, false, newDIPhash, prefs, 2,
                                                                extras.getString("rtDelivery"), objGlobalVariables.rtDeliveryJobsIDXarrName,
                                                                objGlobalVariables.DIPhashName, objGlobalVariables.JobInfoDIPALjsonPrefName,
                                                                objGlobalVariables.DIPjobsCountName);
                                                    } catch (InterruptedException e) {
                                                        objGlobalMethods.writeToFile("Calling BackendRun. InterruptedException when newDIPhash is not empty string, " + e.getMessage().toString(),
                                                                "", "", username, useridx);
                                                        JobsHandler.sendErrorEmail("Calling BackendRun. InterruptedException when newDIPhash is not empty string for Driver: " + username + ", " + e.getMessage().toString());
                                                        Context ctx = myApp.getAppContext();
                                                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                                    } finally {
                                                        mutex.release();
                                                    }
                                                }
                                            }
                                        } else {
                                            DAO_Jobs.deleteJobsByCatTypeID(2);
                                            if (sysDIPhash != "") {
                                                updateDrawerPanel(2, objGlobalVariables.DIPhashName, "", true);
                                            } else if (sysDIPhash == "") {
                                                updateDrawerPanel(2, objGlobalVariables.DIPhashName, "", false);
                                            }
                                        }

                                        //DEL------------------------------------------------------------
                                        newDELhash = (String) JobHashesAL.get(3);

                                        //temporary loggingcode - remove once not required
                                        if (extras.getString("rtDelivered") != null) {
                                            objGlobalMethods.writeToFile("rtDelivered JobsIDXs", extras.getString("rtDelivered"), "null", username, useridx);
                                        }

                                        if (!newDELhash.equals("")) {
                                            if (!newDELhash.equals(sysDELhash)) {
                                                //update
                                                if (extras.getString("rtDelivered") != null) {
                                                    try {
                                                        mutex.acquire();
                                                        objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at GcmIntentService when newDELhash is not empty and not equal sysDELhash", extras.getString("rtDelivered"), "null", username, useridx);
                                                        RegetSpecificJobCategoryCountSQLite(3, context, false, newDELhash, prefs, 3,
                                                                extras.getString("rtDelivered"), objGlobalVariables.rtDeliveredJobsIDXarrName,
                                                                objGlobalVariables.DELhashName, objGlobalVariables.JobInfoDELALjsonPrefName,
                                                                objGlobalVariables.DELjobsCountName);
                                                    } catch (InterruptedException e) {
                                                        objGlobalMethods.writeToFile("Calling BackendRun. InterruptedException when newDELhash is not empty and not equal sysDELhash, " + e.getMessage().toString(),
                                                                "", "", username, useridx);
                                                        JobsHandler.sendErrorEmail("Calling BackendRun. InterruptedException when newDELhash is not empty string for Driver: " + username + ", " + e.getMessage().toString());
                                                        Context ctx = myApp.getAppContext();
                                                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                                    } finally {
                                                        mutex.release();
                                                    }
                                                }
                                            }
                                        } else {
                                            DAO_Jobs.deleteJobsByCatTypeID(3);
                                            if (sysDELhash != "") {
                                                updateDrawerPanel(3, objGlobalVariables.DELhashName, "", true);
                                            } else if (sysDELhash == "") {
                                                updateDrawerPanel(3, objGlobalVariables.DELhashName, "", false);
                                            }
                                        }

                                        //FP------------------------------------------------------------
                                        newFPhash = (String) JobHashesAL.get(4);
                                        if (!newFPhash.equals("")) {
                                            if (!newFPhash.equals(sysFPhash)) {
                                                //update
                                                if (extras.getString("rtFailedPickUp") != null) {
                                                    try {
                                                        mutex.acquire();
                                                        objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at GcmIntentService when newFPhash is not empty and not equal sysFPhash", extras.getString("rtFailedPickUp"), "null", username, useridx);
                                                        RegetSpecificJobCategoryCountSQLite(4, context, false, newFPhash, prefs, 4,
                                                                extras.getString("rtFailedPickUp"), objGlobalVariables.rtFailedPickUpJobsIDXarrName,
                                                                objGlobalVariables.FPhashName, objGlobalVariables.JobInfoFPALjsonPrefName,
                                                                objGlobalVariables.FPjobsCountName);
                                                    } catch (InterruptedException e) {
                                                        objGlobalMethods.writeToFile("Calling BackendRun. InterruptedException when newFPhash is not empty string , " + e.getMessage().toString(),
                                                                "", "", username, useridx);
                                                        JobsHandler.sendErrorEmail("Calling BackendRun. InterruptedException when newFPhash is not empty string for Driver: " + username + ", " + e.getMessage().toString());
                                                        Context ctx = myApp.getAppContext();
                                                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                                    } finally {
                                                        mutex.release();
                                                    }
                                                }
                                            }
                                        } else {
                                            DAO_Jobs.deleteJobsByCatTypeID(4);
                                            if (sysFPhash != "") {
                                                updateDrawerPanel(4, objGlobalVariables.FPhashName, "", true);
                                            } else if (sysFPhash == "") {
                                                updateDrawerPanel(4, objGlobalVariables.FPhashName, "", false);
                                            }
                                        }

                                        //FD------------------------------------------------------------
                                        newFDhash = (String) JobHashesAL.get(5);
                                        if (!newFDhash.equals("")) {
                                            if (!newFDhash.equals(sysFDhash)) {
                                                //update
                                                if (extras.getString("rtFailedDelivery") != null) {
                                                    try {
                                                        mutex.acquire();
                                                        objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at GcmIntentService when newFDhash is not empty and not equal sysFDhash", extras.getString("rtFailedDelivery"), "null", username, useridx);
                                                        RegetSpecificJobCategoryCountSQLite(5, context, false, newFDhash, prefs, 5,
                                                                extras.getString("rtFailedDelivery"), objGlobalVariables.rtFailedDeliveryJobsIDXarrName,
                                                                objGlobalVariables.FDhashName, objGlobalVariables.JobInfoFDALjsonPrefName,
                                                                objGlobalVariables.FDjobsCountName);
                                                    } catch (InterruptedException e) {
                                                        objGlobalMethods.writeToFile("Calling BackendRun. InterruptedException when newFDhash is not empty string, " + e.getMessage().toString(),
                                                                "", "", username, useridx);
                                                        JobsHandler.sendErrorEmail("Calling BackendRun. InterruptedException when newFDhash is not empty string for Driver: " + username + ", " + e.getMessage().toString());
                                                        Context ctx = myApp.getAppContext();
                                                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                                    } finally {
                                                        mutex.release();
                                                    }
                                                }
                                            }
                                        } else {
                                            DAO_Jobs.deleteJobsByCatTypeID(5);
                                            if (sysFDhash != "") {
                                                updateDrawerPanel(5, objGlobalVariables.FDhashName, "", true);
                                            } else if (sysFDhash == "") {
                                                updateDrawerPanel(5, objGlobalVariables.FDhashName, "", false);
                                            }
                                        }
                                    } else {
                                        sendNotification2New(context, myApp.getAppContext().getString(R.string.alert_error));
                                    }
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Calling BackendRun. Exception at GCMIntentService onHandleIntent, " + e.getMessage().toString(),
                                            "", "", username, useridx);
                                    JobsHandler.sendErrorEmail("Calling BackendRun. Exception at GCMIntentService onHandleIntent for Driver: " + username + ", " + e.getMessage().toString());
                                    Context ctx = myApp.getAppContext();
                                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                                }
                            }
                        } else {
                            objGlobalMethods.writeToFile("EnablePush is false at GcmIntentService onHandleIntent. Jobhashes not resync.", "", "", "", "");
                        }

                        //Opid #1: gpsGrabbingInterval
                        //Opid #2: cancel PIP, remove job(s) inside data, from PIP prefs
                        //Opid #3: force logout
                        //Opid #4: force refresh when @ dashboard
                        //Opid #5: grab log file
                        //Opid #6: detect change of JobInfo details
                        //Opid #7: MainCODrow Not Collected status button activation
                        //Opid #8: grab Batt level Integer
                        //Opid #9: grab new routing solution\
                        //Opid #10: refresh trade in list, statuscodes, recipient types
                        //Opid #11: activate deactivate Spic
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("1")) {
                            objGlobalMethods.writeToFile("OpID 1 received to set gps grabbing interval.", "", "", "", "");
                            //Opid #1: gpsGrabbingInterval
                            int interval = 0; //0
                            int count = 0;
                            String data = "";
                            String dataEx = "";
                            try {
                                data = extras.getString("Data");
                                dataEx = extras.getString("Data_Ex");

                                try {
                                    interval = data.equals("") ? 0 : Integer.parseInt(data);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception caught at GCMIntentService onHandleIntent for setting gpsGrabbingInterval. Exception: , " + e.getMessage().toString(),
                                            "", "", "", "");
                                    JobsHandler.sendErrorEmail("Exception caught at GCMIntentService onHandleIntent for setting gpsGrabbingInterval for Driver: " + username + ", " + e.getMessage().toString());
                                    interval = 0;
                                }

                                try {
                                    count = dataEx.equals("") ? 0 : Integer.parseInt(dataEx);
                                } catch (Exception e) {
                                    String stack = JobsHandler.getStackTraceString(e);
                                    objGlobalMethods.writeToFile("Exception caught at GCMIntentService onHandleIntent for setting GPSsendingIntervalCount. Exception: , "
                                            + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                                    JobsHandler.sendErrorEmail("Exception caught at GCMIntentService onHandleIntent for setting GPSsendingIntervalCount for Driver: " + username + ", " + e.getMessage().toString());
                                    count = 0;
                                }

                            } catch (Exception e) {
                                String stack = JobsHandler.getStackTraceString(e);
                                objGlobalMethods.writeToFile("Exception caught at GCMIntentService onHandleIntent for setting gpsGrabbingInterval or GPSsendingIntervalCount. Exception: , "
                                        + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                                JobsHandler.sendErrorEmail("Exception caught at GCMIntentService onHandleIntent for setting gpsGrabbingInterval or GPSsendingIntervalCount for Driver: " + username + ", " + e.getMessage().toString());
                                interval = 0;
                                count = 0;
                            } finally {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("GPSalarmInterval", interval == 0 || count == 0 ? "" : String.valueOf(interval));
                                editor.putString("GPSsendingIntervalCount", interval == 0 || count == 0 ? "" : String.valueOf(count));
                                editor.commit();

                                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (!objGlobalMethods.isActiveServiceRunning()) {
                                        objGlobalMethods.startActiveForegroundSvc("");
                                    } else {
                                        Context ctx = myApp.getAppContext();
                                        Intent i = new Intent(ctx, ActiveService.class);
                                        i.setAction(ActiveService.ACTION_STARTGPSALARMSERVICE);
                                        ctx.startService(i);
                                    }
                                }
                            }
                        } else if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("2")) {
                            //cancel PIP, remove job(s) inside data, from PIP prefs
                            objGlobalMethods.writeToFile("OpID 2 received for cx pip.", "", "", "", "");
                            ArrayList JobsIDXsAL = new ArrayList();
                            long JobsIDX = 0;
                            String data = "";
                            String[] parts = null;
                            Boolean jobFound = false;
                            objJobInfo objJI = null;
                            List<Long> JobsIDXlist;
                            try {
                                data = extras.getString("Data");
                                parts = data.split(", ");
                                if (parts != null && parts.length > 0) {
                                    JobsIDXlist = new ArrayList<Long>();
                                    for (int v = 0; v <= parts.length - 1; v++) {
                                        JobsIDX = 0;
                                        if (!parts[v].equals("")) {
                                            try {
                                                JobsIDX = Long.parseLong(parts[v]);
                                            } catch (Exception e) {
                                                String stack = JobsHandler.getStackTraceString(e);
                                                objGlobalMethods.writeToFile("Exception caught at GCMIntentService onHandleIntent for converting String JobsIDX "
                                                        + parts[v] + " to Long JobsIDX. Exception: , " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                                                JobsHandler.sendErrorEmail("Exception caught at GCMIntentService onHandleIntent for converting String JobsIDX " + parts[v] + " to Long JobsIDX for Driver: " + username + ", " + e.getMessage().toString());
                                                JobsIDX = 0;
                                            }

                                        }
                                        JobsIDXlist.add(JobsIDX);
                                    }

                                    if (JobsIDXlist != null && JobsIDXlist.size() > 0) {
                                        //deleteJobsFromSystemPrefs(prefs, JobsIDXlist);
                                        String joinedString = "";
                                        for (int i = 0; i <= JobsIDXlist.size() - 1; i++) {
                                            if (i == JobsIDXlist.size() - 1) {
                                                joinedString += String.valueOf(JobsIDXlist.get(i));
                                            } else {
                                                joinedString += String.valueOf(JobsIDXlist.get(i)) + ",";
                                            }
                                        }
                                        DAO_Jobs.deleteJobByIDXrange(joinedString);

                                        String newHash = "";
                                        List<Long> JobsIDXsList = DAO_Jobs.getAlldbObjJobsIDXsPerCategory(0);
                                        if (JobsIDXsList != null && JobsIDXsList.size() > 0) {
                                            long[] JobsIDXarr = new long[JobsIDXsList.size()];
                                            for (int i = 0; i < JobsIDXsList.size(); i++)
                                                JobsIDXarr[i] = JobsIDXsList.get(i);
                                            Arrays.sort(JobsIDXarr);
                                            String s = "";
                                            for (int v = 0; v <= JobsIDXarr.length - 1; v++) {
                                                s += "[" + String.valueOf(JobsIDXarr[v]) + "]";
                                            }
                                            if (!s.trim().equals("")) {
                                                try {
                                                    newHash = SHA1(s);
                                                } catch (Exception e) {
                                                    s = "";
                                                    newHash = "";
                                                }
                                            } else {
                                                newHash = "";
                                            }

                                        }
                                        updateDrawerPanel(0, objGlobalVariables.PIPhashName, newHash, true);
                                    } else {
                                        objGlobalMethods.writeToFile("JobsIDXlist is empty at GCMIntentService onHandleIntent OpID = 2", "", "", "", "");
                                    }
                                }
                            } catch (Exception e) {
                                String stack = JobsHandler.getStackTraceString(e);
                                objGlobalMethods.writeToFile("Exception caught at GCMIntentService onHandleIntent for setting gpsGrabbingInterval or GPSsendingIntervalCount. Exception: , "
                                        + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                                JobsHandler.sendErrorEmail("Exception caught at GCMIntentService onHandleIntent for setting gpsGrabbingInterval or GPSsendingIntervalCount for Driver: " + username + ", " + e.getMessage().toString());
                            }
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("3")) {
                            //force logout
                            objGlobalMethods.writeToFile("OpID 3 received to force logout.", "", "", "", "");
                            objGlobalMethods.setGCMPrefs(prefs, "forceLogOutBooleanString", "true");
                            sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("4")) {
                            objGlobalMethods.writeToFile("OpID 4 received to force refresh @ dashboard.", "", "", "", "");
                            //force refresh when @ dashboard
                            objGlobalMethods.setGCMPrefs(prefs, "refreshQueBooleanString", "true");
                            Context ctx = myApp.getAppContext();
                            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("5")) {
                            objGlobalMethods.writeToFile("OpID 5 received to grab log file.", "", "", "", "");
                            //grab log file
                            Context ctx = myApp.getAppContext();
                            sendBroadcast(new Intent(ctx, GrabLogFileOnDemand_BroadcastReceiver.class));
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("6")) {
                            objGlobalMethods.writeToFile("OpID 6 received to change of JobInfo details.", "", "", "", "");
                            //detect change of JobInfo details
                            String editedJobsIDXstr = "";
                            editedJobsIDXstr = extras.getString("Data");
                            if (!editedJobsIDXstr.equals("")) {
                                processEditedJobInfo(prefs, editedJobsIDXstr);
                            }
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("7")) {
                            objGlobalMethods.writeToFile("OpID 7 received to activate COD not collected button.", "", "", "", "");
                            //activate MainCODrow Not Collected button
                            String InvoiceIDXstr = "";
                            String JobsIDXstr = "";
                            if (extras.getString("Data") != null && !extras.getString("Data").equals("")) {
                                JobsIDXstr = extras.getString("Data");
                            }
                            if (extras.getString("Data_Ex") != null && !extras.getString("Data_Ex").equals("")) {
                                InvoiceIDXstr = extras.getString("Data_Ex");
                            }
                            objGlobalMethods.writeToFile("OpID #7 received for JobsIDX, " + JobsIDXstr + ", InvoiceIDX, " + InvoiceIDXstr + ".", "", "", "", "");
                            handleMainCODrowNotCollectedButtonActivation(prefs, JobsIDXstr, InvoiceIDXstr);
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("8")) {
                            objGlobalMethods.writeToFile("OpID 8 received to grab batt level.", "", "", "", "");
                            //Opid #8: grab Batt level Integer
                            float battlvl = 0;
                            Integer battlvlint = 0;
                            battlvl = getBatteryLevel();
                            battlvlint = Math.round(battlvl);
                            objGlobalMethods.setGCMPrefs(prefs, "battlvl", String.valueOf(battlvlint));
                            sendBroadcast(new Intent(context, BattLvl_Receiver.class));
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("9")) {
                            objGlobalMethods.writeToFile("OpID 9 received to grab new routing solution.", "", "", "", "");
                            //grab new routing solution
                            JobsHandler.HandleRouteList(regid, prefs);
                            //JobsHandler.getJobQue2(regid, true, true, -1);
                            ArrayList<Integer> ListItemIndex = new ArrayList<>();
                            ListItemIndex.add(-1);
                            JobsHandler.getJobQue2(regid, true, true, ListItemIndex, null);
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("10")) {
                            //refresh trade in list, statuscodes, recipient list etc etc
                            objGlobalMethods.writeToFile("OpID 10 received to refresh trade in list, statuscodes, recipient.", "", "", "", "");
                            //refresh trade in list
                            JobsHandler.refreshTradeInList();

                            //refresh NFCTags list
                            JobsHandler.refreshNFCTags();

                            //refresh statuscodes
                            JobsHandler.downloadStatusCodesLibWSstandAlone();

                            //refresh recipient list
                            JobsHandler.downloadRecipientTypesWSstandAlone();
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("11")) {
                            //settings related
                            objGlobalMethods.grabSettingsFromServer();
                            objGlobalMethods.writeToFile("OpID 11 received to grab settings from Luke Cabinet.", "", "", "", "");
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("12")) {
                            //refresh MPD Check-In/Out related
                            objGlobalMethods.writeToFile("OpID 12 received to refresh MPDlist.", "", "", "", "");
                            ArrayList<objMPD_Info> mpds = objGlobalMethods.grabMPDFromServer();
                            //objGlobalMethods.setMPDsPrefStr(mpds, prefs);
                            objGlobalMethods.setMPDsPrefStrNew(mpds);
                            refreshMPD_CheckOutList();
                        }
                        if (extras.getString("OpID") != null && !extras.getString("OpID").equals("") && extras.getString("OpID").equals("13")) {
                            //activate deactivate Spic
                            //dataEx = "0" deactivate
                            //dataEx = "1" activate
                            String dataEx = "";
                            try {
                                dataEx = extras.getString("Data_Ex");
                                if (!dataEx.isEmpty() && !dataEx.equals("") && dataEx != "") {
                                    objGlobalMethods.setGCMPrefs(prefs, "ActDeactSpic", dataEx);
                                    objGlobalMethods.writeToFile("OpID 12 received to activate deactivate Spic with code " + dataEx + ".", "", "", "", "");
                                }
                            } catch (Exception e) {
                                String s = e.getMessage().toString();
                            }
                        }
                        if (extras.getString("Message") != null && !extras.getString("Message").equals("") &&
                                extras.getString("IDX") != null && !extras.getString("IDX").equals("") &&
                                extras.getString("UserIDX") != null && !extras.getString("UserIDX").equals("") &&
                                extras.getString("UserName") != null && !extras.getString("UserName").equals("")) {
                            handleIncomingMessage2(extras.getString("TimeStamp"), extras.getString("IDX"), extras.getString("UserIDX"), extras.getString("UserName"),
                                    extras.getString("JobsIDX"), extras.getString("Message"),
                                    extras.getString("Reference") != null && !extras.getString("Reference").equals("") ? extras.getString("Reference") : "");
                        }
                    }
                } else {
                    objGlobalMethods.writeToFile("Extras is empty @ GcmIntentService onHandleIntent.", "", "", "", "");
                }
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at GcmIntentService onHandleIntent. Excpetion: " + e.getMessage().toString()
                        + "\nStacktrace: " + stack, "", "", "", "");
            } finally {
                // Release the wake lock provided by the WakefulBroadcastReceiver.
                try {
                    GcmBroadcastReceiver.completeWakefulIntent(intent);
                } catch (Throwable th) {

                }
            }
        }
    }

    private void RegetSpecificJobCategoryCountSQLite(final int ListItemIndex, final Context context, boolean isNewPIP, final String newHash, SharedPreferences prefs, final int CatTypeID,
                                                     String categoryJobsIDXjsonStr, final String objGlobalVariablesrtCategoryJobsIDXarrName,
                                                     final String objGlobalVariablesCategoryHashName, final String objGlobalVariablesJobInfoCategoryALjsonPrefName,
                                                     final String objGlobalVariablesCategoryjobsCountName) {

        Gson gson = new Gson();
        objJobInfo[] newExtraJobInfoAL;
        List<objJobInfo> newExtraJobInfoList;
        String JobInfoJsonStr = "";
        List<Long> JobsIDXsToBeDeletedList = null;
        List<objJobInfo> JobInfoToBeInsertedList = null;
        Boolean hasNewPIPjobs = false;
        Boolean requireUpdate = false;

        try {
            UpdateJobInfoPreferences = getUpdateJobInfoPreferences();

            if (!categoryJobsIDXjsonStr.equals("")) {
                JSONArray newJobsIDXjarr = new JSONArray(categoryJobsIDXjsonStr);
                Long[] newJobsIDXlongArr = gson.fromJson(newJobsIDXjarr.toString(), Long[].class);
                List<Long> newJobsIDXlongArrList = new ArrayList<Long>(Arrays.asList(newJobsIDXlongArr));

                if (JobInfoListInQueForStatusUpdate != null && JobInfoListInQueForStatusUpdate.size() > 0) {
                    //there are jobs status update pending
                    for (int i = 0; i <= JobInfoListInQueForStatusUpdate.size() - 1; i++) {
                        if (newJobsIDXlongArrList.contains(JobInfoListInQueForStatusUpdate.get(i).JobsIDX)) {
                            newJobsIDXlongArrList.remove(JobInfoListInQueForStatusUpdate.get(i).JobsIDX);
                        }
                    }
                }

                if (newJobsIDXlongArrList.size() > 0) {
                    Long[] remainingJobsIDXarr = new Long[newJobsIDXlongArrList.size()];
                    for (int i = 0; i <= newJobsIDXlongArrList.size() - 1; i++)
                        remainingJobsIDXarr[i] = newJobsIDXlongArrList.get(i);
                    newExtraJobInfoAL = WebService.GetDriverJobsInfoAL(regid, remainingJobsIDXarr, "L2_GetDriverJobInfo");

                    if (newExtraJobInfoAL != null && newExtraJobInfoAL.length > 0) {
                        newExtraJobInfoList = new ArrayList<objJobInfo>(Arrays.asList(newExtraJobInfoAL));
                        List<Long> dbJobsIDXsList = DAO_Jobs.getAlldbObjJobsIDXsPerCategory(CatTypeID);

                        //create list of jobs to be inserted to db for this category
                        for (int x = 0; x <= newExtraJobInfoList.size() - 1; x++) {
                            objJobInfo ji = newExtraJobInfoList.get(x);

                            if (!dbJobsIDXsList.contains(ji.JobsIDX) && JobInfoToBeInsertedList != null) {
                                //add
                                JobInfoToBeInsertedList.add(ji);
                                if (isNewPIP) {
                                    hasNewPIPjobs = true;
                                }
                            } else if (!dbJobsIDXsList.contains(ji.JobsIDX) && JobInfoToBeInsertedList == null) {
                                //add
                                JobInfoToBeInsertedList = new ArrayList<>();
                                JobInfoToBeInsertedList.add(ji);
                                if (isNewPIP) {
                                    hasNewPIPjobs = true;
                                }
                            }
                        }

                        //create list of JobsIDXs to be deleted from db for this category
                        for (int x = 0; x <= dbJobsIDXsList.size() - 1; x++) {
                            if (JobsIDXsToBeDeletedList != null && !newJobsIDXlongArrList.contains(dbJobsIDXsList.get(x))) {
                                //remove
                                JobsIDXsToBeDeletedList.add(dbJobsIDXsList.get(x));
                            } else if (JobsIDXsToBeDeletedList == null && !newJobsIDXlongArrList.contains(dbJobsIDXsList.get(x))) {
                                JobsIDXsToBeDeletedList = new ArrayList<>();
                                JobsIDXsToBeDeletedList.add(dbJobsIDXsList.get(x));
                            }
                        }

                        if (JobsIDXsToBeDeletedList != null && JobsIDXsToBeDeletedList.size() > 0) {
                            //delete all these record from db based on cattypeid n jobsidx
                            String[] JobsIDXsToBeDeletedStrArr = new String[JobsIDXsToBeDeletedList.size()];
                            for (int i = 0; i < JobsIDXsToBeDeletedList.size(); i++)
                                JobsIDXsToBeDeletedStrArr[i] = String.valueOf(JobsIDXsToBeDeletedList.get(i));

                            String joinedString = "";
                            for (int i = 0; i <= JobsIDXsToBeDeletedList.size() - 1; i++) {
                                if (i == JobsIDXsToBeDeletedList.size() - 1) {
                                    joinedString += String.valueOf(JobsIDXsToBeDeletedList.get(i));
                                } else {
                                    joinedString += String.valueOf(JobsIDXsToBeDeletedList.get(i)) + ",";
                                }
                            }
                            DAO_Jobs.deleteJobByIDXrange(joinedString);
                        }

                        if (JobInfoToBeInsertedList != null && JobInfoToBeInsertedList.size() > 0) {
                            //insert all these record into db base on this cattypeid
                            for (int i = 0; i <= JobInfoToBeInsertedList.size() - 1; i++) {
                                objJobInfo ji = JobInfoToBeInsertedList.get(i);
                                JobInfoJsonStr = gson.toJson(ji);
                                DAO_Jobs.insertJobs(ji, CatTypeID, JobInfoJsonStr);
                                if (isNewPIP && ji.StatusCodeIDX == 0 || ji.StatusCodeIDX == 235 || ji.StatusCodeIDX == 165) {
                                    Date dt = new Date();
                                    Long l = dt.getTime();
                                    String timestampstr = "/Date(" + l + ")/";

                                    objGlobalMethods.writeToFile("Update job status from " + String.valueOf(ji.StatusCodeIDX) + " to 103", String.valueOf(ji.JobsIDX), "103", username, useridx);

                                    Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                                    int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, regid, ji.JobsIDX, 103, timestampstr, "", "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                                    requireUpdate = true;
                                }
                            }
                        }

                        JobsHandler.setSystemCategoryJobsIDXhashSQLite(prefs, CatTypeID);

                        if (requireUpdate) {
                            Context ctx = myApp.getAppContext();
                            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                        }

                        if (isNewPIP && hasNewPIPjobs) {
                            sendNotification2New(myApp.getAppContext(), myApp.getAppContext().getString(R.string.alert_newpip));
                            //showDrawerMain2ActivityBottomSheet();
                        }

                        updateDrawerPanel(ListItemIndex, objGlobalVariablesCategoryHashName, newHash, true);
                    } else {
                        Date dt = new Date();
                        Long l = dt.getTime();
                        String timestampstr = "/Date(" + l + ")/";

                        objGlobalMethods.writeToFile("From Push, L2_GetDriverJobInfo web svc return null. Run backend repeatedly until there is signal to resync.", "", "", username, useridx);

                        Context ctx = myApp.getAppContext();
                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    }
                }

            } else {
                //new job hash is empty
            }
        } catch (Exception e) {
            getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(getGcmPreferences());
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            useridx = UsernamePwd[2];
            ishomesuser = UsernamePwd[3];
            Date dt = new Date();
            String stacktrace = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCMIntentService, RegetSpecificJobCategoryCountSQLite." + "\n" + e.getMessage().toString() + "\n" + "Stack: " + stacktrace,
                    "", "", username, useridx);

            JobsHandler.sendErrorEmail("Exception caught at GCMIntentService for " + username + ", RegetSpecificJobCategoryCountSQLite."
                    + "\n" + e.getMessage().toString() + "\n" + "Stack: " + stacktrace);

            Context ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
        }
    }

    private void processEditedJobInfo(SharedPreferences GcmPreferences, String editedJobsIDXstr) {
        Date dt = new Date();
        String storededitedJobsIDXJsonStr = "";
        String newdeditedJobsIDXJsonStr = "";
        long[] storededitedJobsIDXAL;
        long[] newdeditedJobsIDXAL = null;
        ArrayList<Long> newdeditedJobsIDXList = new ArrayList<>();

        long editedJobsIDX;
        Gson gson = new Gson();

        try {
            storededitedJobsIDXJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "storededitedJobsIDXJsonStr");
            if (!storededitedJobsIDXJsonStr.equals("")) {
                storededitedJobsIDXAL = gson.fromJson(storededitedJobsIDXJsonStr.toString(), long[].class);
                if (storededitedJobsIDXAL != null && storededitedJobsIDXAL.length > 0) {

                    for (int i = 0; i <= storededitedJobsIDXAL.length - 1; i++) {
                        newdeditedJobsIDXList.add(storededitedJobsIDXAL[i]);
                    }

                    if (newdeditedJobsIDXList != null && newdeditedJobsIDXList.size() > 0) {
                        if (!newdeditedJobsIDXList.contains(Long.parseLong(editedJobsIDXstr))) {
                            newdeditedJobsIDXList.add(Long.parseLong(editedJobsIDXstr));
                        }
                    } else if (newdeditedJobsIDXList == null || newdeditedJobsIDXList.size() == 0) {
                        newdeditedJobsIDXList.add(Long.parseLong(editedJobsIDXstr));
                    }
                }
            } else {
                newdeditedJobsIDXList.add(Long.parseLong(editedJobsIDXstr));
            }

            if (newdeditedJobsIDXList != null && newdeditedJobsIDXList.size() > 0) {
                newdeditedJobsIDXAL = new long[newdeditedJobsIDXList.size()];
                for (int i = 0; i <= newdeditedJobsIDXList.size() - 1; i++) {
                    newdeditedJobsIDXAL[i] = newdeditedJobsIDXList.get(i);
                }
            }

            if (newdeditedJobsIDXAL != null && newdeditedJobsIDXAL.length > 0) {
                newdeditedJobsIDXJsonStr = gson.toJson(newdeditedJobsIDXAL);
                if (newdeditedJobsIDXJsonStr.equals("[]")) {
                    newdeditedJobsIDXJsonStr = "";
                }
            } else {
                newdeditedJobsIDXJsonStr = "";
            }
            objGlobalMethods.setGCMPrefs(GcmPreferences, "storededitedJobsIDXJsonStr", newdeditedJobsIDXJsonStr);
            sendBroadcast(new Intent(context, changeOfJobInfoDetails_BroadcastReceiver.class));

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GcmIntentService processEditedJobInfo. Force refresh data activated. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "refreshQueBooleanString", "true");
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString().toUpperCase();
    }

    private static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private void updateDrawerPanel(final int ListItemIndex, final String objGlobalVariablesCategoryHashName, final String cHashstr, final Boolean changeCountColor) {
        final SharedPreferences GcmPreferences = getGcmPreferences();
        JobsHandler.setCategoryHashes(GcmPreferences, objGlobalVariablesCategoryHashName, cHashstr);
        if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
            final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;


            act.runOnUiThread(new Runnable() {
                public void run() {
                    // Notifiy activity
                    act.updateListRowPerCategory(ListItemIndex, changeCountColor);
                }
            });
        } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
            final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    act.refreshrv(false);
                }
            });
        }
    }

    private void refreshMPD_CheckOutList(){
        try {
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPD_CheckOutActivity.class) {
                final MPD_CheckOutActivity act = (MPD_CheckOutActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.refreshlist();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class)  {
                final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class)  {
                final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class)  {
                final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class)  {
                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class)  {
                final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class)  {
                final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class)  {
                final DashboardSubActivity act = (DashboardSubActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            }/* else {
                ArrayList<objMPD_Info> mpds = objGlobalMethods.grabMPDFromServer();
                SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setMPDsPrefStr(mpds, GcmPreferences);
            }*/
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at GcmIntentService refreshMPD_CheckOutList. Exception: " + s,"","","","");
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Context context, String msg) {
        int icon = R.mipmap.ic_stat_gcm;
        NotificationCompat.Builder nBuilder;

        Uri alarmSound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        nBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher2)
                .setContentTitle(myApp.getAppContext().getString(R.string.alert_LukeNotification))
                .setLights(Color.BLUE, 500, 500)
                .setContentText(msg)
                .setAutoCancel(true).setTicker("Notification from LUKE")
                .setVibrate(new long[]{100, 250, 100, 250, 100, 250})
                .setSound(alarmSound)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg));

        //Intent resultIntent = new Intent(this, ViewNotificationActivity.class);
        //Intent resultIntent = new Intent(this, Dashboard2Activity.class);
        Intent resultIntent = new Intent(this, MainActivity.class);
        MainActivity.uponLogin = 1;
        //resultIntent.putExtra("notificationExtras",notificationExtras);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);

        /*PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                myNOTIFICATION_ID, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        nBuilder.setContentIntent(resultPendingIntent);
        NotificationManager nNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nNotifyMgr.notify(myNOTIFICATION_ID, nBuilder.build());

    }

    private void sendNotification2New(Context ctx, String msg) {
        try {
            stopAlarmForCommon();
            DrawerMainActivity.DashboardOpenFromPush = true;
            String group_id = "LUKEMPIP";
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class), 0);
            MainActivity.uponLogin = 1;
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_action_info_outline);
            Uri alarmSound = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.pip3);

            NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, myApp.CHANNEL_ID_PIP);

            b.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_xdel_noti2)
                    .setLargeIcon(bm)
                    .setContentTitle(myApp.getAppContext().getString(R.string.alert_LukeNotification))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setWhen(System.currentTimeMillis())
                    .setGroup(group_id)
                    .setSound(alarmSound)
                    .setContentText(msg)
                    .setAutoCancel(true);
            b.setContentIntent(contentIntent);
            //startAlarmForCommon();
            notiNotRead = true;
            //objGlobalMethods.startNewPerMinAlarm();
            mNotificationManager.notify(2, b.build());
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCMIntentService sendNotification2New. Exception: , " + e.getMessage().toString() + "\nStack: " + stack,
                    "", "", "", "");
        }
    }

    private void showDrawerMain2ActivityBottomSheet(){
        /*try {
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                act.showbottomsheet();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }*/
    }

    private void sendMultipleNotification(Context ctx, String msg, String UserName, String UserIDX, int notiID) {
        try {
            ActiveService.playTone(ctx, R.raw.text_message);
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                    new Intent(ctx, Messaging.class), 0);
            MainActivity.uponLogin = 0;
            Uri alarmSound = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.text_message);
            Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher2);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(ctx, myApp.CHANNEL_ID)
                            //.setSmallIcon(R.mipmap.ic_xdel_noti)
                            .setSmallIcon(R.mipmap.ic_action_info_outline)
                            .setLargeIcon(bm)
                            .setContentTitle(UserName)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                            .setSound(alarmSound)
                            .setWhen((new Date()).getTime())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentText(msg)
                            /*.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})*/
                            //.setNumber(2)
                            .setAutoCancel(true);

            mBuilder.setContentIntent(contentIntent);
            SharedPreferences GcmPreferences = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "readingMsgOfSelectedUserIDXPrefStr", UserIDX);

            mNotificationManager.notify(notiID, mBuilder.build());
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCMIntentService sendNotification. Exception: , " + e.getMessage().toString() + "\nStack: " + stack,
                    "", "", "", "");
        }

    }

    private void sendMultipleNoficationNew(Context ctx, String msg, String UserName, String UserIDX, int notiID, String JobsIDX) {
        try {
            String group_id = "LUKEMSG";
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                    new Intent(ctx, Messaging.class), 0);
            MainActivity.uponLogin = 0;
            Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher2);
            Uri alarmSound = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.text_message);

            NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, myApp.CHANNEL_ID_MSG);

            b.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_action_info_outline)
                    .setLargeIcon(bm)
                    .setContentTitle(UserName)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setWhen(System.currentTimeMillis())
                    .setGroup(group_id)
                    .setSound(alarmSound)
                    .setContentText(msg)
                    .setAutoCancel(true);
            b.setContentIntent(contentIntent);
            SharedPreferences GcmPreferences = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            /*objGlobalMethods.setGCMPrefs(GcmPreferences, "readingMsgOfSelectedUserIDX_id_JobsIDXPrefStr", JobsIDX);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "readingMsgOfSelectedUserIDX_idPrefStr", _id);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "readingMsgOfSelectedUserIDXPrefStr", UserIDX);*/
            Messaging.JobsIDX = JobsIDX;
            Messaging.msgUserIDX = UserIDX;
            mNotificationManager.notify(notiID, b.build());
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCMIntentService sendMultipleNoficationNew. Exception: , " + e.getMessage().toString() + "\nStack: " + stack,
                    "", "", "", "");
        }
    }

    public void startAlarmForCommon() {
        try {
            notiNotRead = true;
            Intent alarmIntent2 = new Intent(context, Common_BroadcastReceiver.class);
            pendingIntent2 = PendingIntent.getBroadcast(context, 0, alarmIntent2, 0);
            manager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            //int interval = 30000;
            int interval = 120000; //2min
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(System.currentTimeMillis());
            Common_BroadcastReceiver.count = 0;
            manager2.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), interval, pendingIntent2);
            objGlobalMethods.writeToFile("AlarmForCommon started.", "", "", "", "");
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCMIntentService, startAlarmForCommon. Exception: " + e.getMessage().toString() + "\nStack: " + stack,
                    "", "", "", "");
        }
    }

    public static void stopAlarmForCommon() {
        try {
            if (manager2 != null) {
                manager2.cancel(pendingIntent2);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCMIntentService, stopAlarmForCommon. Exception: " + e.getMessage().toString() + "\nStack: " + stack,
                    "", "", "", "");
        }

    }
    //end get json text data

    private List<objUpdateJobInfoStatus> getJobInfoListInQueForStatusUpdate() {
        List<objUpdateJobInfoStatus> ListUpdateJobInfoStatusALAL = null;
        Gson gson = new Gson();
        objUpdateJobInfoStatus[] objUpdateJobInfoStatusALQue;
        objUpdateJobInfoStatus objUpdateJobInfoStatusObj;

        try {
            mutex.acquire();
            UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
            String UpdateJobInfoStatusALPrefStr = UpdateJobInfoPreferences.getString(objGlobalVariables.UpdateJobInfoStatusALPrefStr, "");
            mutex.release();
            if (!UpdateJobInfoStatusALPrefStr.isEmpty()) {
                objUpdateJobInfoStatusALQue = gson.fromJson(UpdateJobInfoStatusALPrefStr, objUpdateJobInfoStatus[].class);
                if (objUpdateJobInfoStatusALQue != null) {
                    ListUpdateJobInfoStatusALAL = new ArrayList<objUpdateJobInfoStatus>(Arrays.asList(objUpdateJobInfoStatusALQue));
                }
            }
        } catch (Exception e) {
            ListUpdateJobInfoStatusALAL = null;
        } finally {
            mutex.release();
        }
        return ListUpdateJobInfoStatusALAL;
    }

    private ArrayList<ChatMessage> addDateToChatMessage(ArrayList<ChatMessage> achatMessages) {
        Date dt = new Date();
        ArrayList<ChatMessage> aachatMessages = achatMessages;
        ArrayList<ChatMessage> aachatMessages2 = new ArrayList<>();
        //Date prevMsgDate = null;
        Long prevMsgDate = null;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateText = "";
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        try {
            for (int i = 0; i <= aachatMessages.size() - 1; i++) {
                if (i == 0) {
                    if (aachatMessages.get(i).messageDate == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -1);
                        aachatMessages.get(i).messageDate = cal.getTimeInMillis();
                        aachatMessages.get(i).setMessageText(dateFormat.format(cal.getTime()));
                    }

                    if (dateFormat.format(aachatMessages.get(i).messageDate).equals(dateFormat.format(dt)) && aachatMessages.get(i).getMessageText().equals("Today")) {
                        prevMsgDate = aachatMessages.get(i).messageDate;
                    } else {
                        if (dateFormat.format(aachatMessages.get(i).messageDate).equals(dateFormat.format(dt))) {
                            dateText = "Today";
                        } else {
                            dateText = dateFormat.format(aachatMessages.get(i).messageDate);
                        }
                        final ChatMessage message = new ChatMessage();

                        aachatMessages.get(i).setMessageText(dateText);
                        message.setMessageTime(new Date().getTime());
                        prevMsgDate = aachatMessages.get(i).messageDate;
                    }
                    aachatMessages2.add(aachatMessages.get(i));
                } else if (i > 0) {
                    if (aachatMessages.get(i).messageDate != null
                            && !dateFormat.format(aachatMessages.get(i).messageDate).equals(dateFormat.format(prevMsgDate))
                            && !aachatMessages.get(i).getMessageText().equals("Today")) {
                        if (dateFormat.format(aachatMessages.get(i).messageDate).equals(dateFormat.format(dt))) {
                            dateText = "Today";
                        } else {
                            dateText = dateFormat.format(aachatMessages.get(i).messageDate);
                        }

                        if (!aachatMessages.get(i).getMessageText().equals(dateText)) {
                            final ChatMessage message = new ChatMessage();

                            message.setID(m);
                            message.setMessageStatus(Status.SENT);
                            message.setMessageText(dateText);
                            message.setUserType(UserType.DATE);
                            message.setMessageTime(new Date().getTime());
                            message.messageDate = aachatMessages.get(i).messageDate;
                            aachatMessages2.add(message);
                        }
                    }
                    prevMsgDate = aachatMessages.get(i).messageDate;
                    aachatMessages2.add(aachatMessages.get(i));
                }
            }

            if (aachatMessages2.size() > 0) {
                int x = aachatMessages2.size() - 1;
                if (!dateFormat.format(aachatMessages2.get(x).messageDate).equals(dateFormat.format(dt))
                        && !aachatMessages2.get(x).getMessageText().equals("Today")) {
                    final ChatMessage xmessage = new ChatMessage();
                    m = random.nextInt(9999 - 1000) + 1000;
                    xmessage.setID(m);
                    xmessage.setMessageStatus(Status.SENT);
                    xmessage.setMessageText("Today");
                    xmessage.setUserType(UserType.DATE);
                    xmessage.setMessageTime(new Date().getTime());
                    xmessage.messageDate = dt.getTime();
                    aachatMessages2.add(xmessage);
                }
            }

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at Messaging addDateToChatMessage. " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            aachatMessages2 = achatMessages;
        } finally {
            return aachatMessages2;
        }
    }

    private void handleIncomingMessage2(String aTimeStamp, String aIDX, String aUserIDX, String aUserName, String aJobsIDX, String msg, String aRef) {
        Context ctx = myApp.getAppContext();
        SharedPreferences gcmMsgPrefs;
        ChatMsgSenders cms = null;
        final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        ArrayList<ChatMessage> cmal;
        List<ChatMsgSenders> chatmsgsendersForAllUsersAL = null;
        List<ChatMsgSenders> chatmsgsendersAL = null;

        try {
            gcmMsgPrefs = objGlobalMethods.getGcmPreferences();
            String GetAllMsgCompleted = objGlobalMethods.getGCMPrefs(gcmMsgPrefs, "GetAllMsgCompleted");
            if (!GetAllMsgCompleted.equals("")) {
                //objGlobalMethods.writeToFile("Handling message receive from XDel: " + msg, "", "", "", "");
                //insert into db
                chatmsgsendersForAllUsersAL = DAO_Jobs.getChatMsgSendersListForAllUsers();
                chatmsgsendersAL = DAO_Jobs.getChatMsgSendersList();
                if (chatmsgsendersForAllUsersAL.size() > chatmsgsendersAL.size()) {
                    for (int i = 0; i <= chatmsgsendersForAllUsersAL.size() - 1; i++) {
                        cms = chatmsgsendersForAllUsersAL.get(i);
                        DAO_Jobs.insertChatMsgSenders2(cms);
                    }
                }

                Long JobsIDX = !aJobsIDX.equals("") && !aJobsIDX.equals("0") ? Long.parseLong(aJobsIDX) : 0;
                Long UserIDX = !aUserIDX.equals("") && !aUserIDX.equals("0") ? Long.parseLong(aUserIDX) : 0;
                //aUserName = !aJobsIDX.equals("") && !aJobsIDX.equals("0") ? aUserName + " (" + aJobsIDX +")" : aUserName;
                aUserName = !aRef.equals("") ? aUserName + " (" + aRef + ")" : aJobsIDX.equals("") && !aJobsIDX.equals("0") ? aUserName + " (" + aJobsIDX + ")" : aUserName;
                cms = DAO_Jobs.getChatMsgSendersWithJobsIDXnChatMsgSendersIDX(JobsIDX, UserIDX);
                if (cms == null) {
                    cms = new ChatMsgSenders(aUserIDX, aUserName, null,
                            SIMPLE_DATE_FORMAT.format(objGlobalMethods.DateToSecondsLong(aTimeStamp)), false, m, objGlobalMethods.DateToLong(aTimeStamp), aJobsIDX.equals("") ? "0" : aJobsIDX);
                    DAO_Jobs.insertChatMsgSenders2(cms);
                } else {
                    cms.Read = false;
                    cms.messageTime = SIMPLE_DATE_FORMAT.format(objGlobalMethods.DateToSecondsLong(aTimeStamp));
                    //cms.msgDateLong = objGlobalMethods.DateToSecondsLong(aTimeStamp);
                    cms.msgDateLong = objGlobalMethods.DateToLong(aTimeStamp);
                    m = cms.notiID;
                    DAO_Jobs.updateChatMsgSenders(cms);
                }

                objChatMessage cm = new objChatMessage(String.valueOf(objGlobalMethods.DateToLong(aTimeStamp)), Long.parseLong(aIDX), Long.parseLong(aUserIDX), aUserName, Long.parseLong(aJobsIDX), msg, 2,
                        Status.SENT.ordinal(), UserType.OTHER.ordinal(), Long.parseLong(aIDX));
                DAO_Jobs.insertChatMessage(cm);
                //cms = DAO_Jobs.getChatMsgSenders(Long.parseLong(aUserIDX));

                cmal = DAO_Jobs.getchatMessages2(Long.parseLong(cms.JobsIDX), Long.parseLong(cms.UserIDX));
                if (cmal != null && cmal.size() > 0) {
                    Collections.sort(cmal, new ChatMessage.OrderbymessageDate());
                    Collections.sort(cmal, Collections.reverseOrder());
                    GetAllMsg_IntentService.addDateToChatMessage(cmal, Long.parseLong(cms.UserIDX), cms.UserName,
                            cms.JobsIDX == null || cms.JobsIDX.equals("") || cms.JobsIDX.equals("0") ? "0" : cms.JobsIDX, false);
                }

                objGlobalMethods.writeToFile("Message receive from XDel: " + msg, "", "", "", "");
                JobsHandler.addAckMessagesToBeSent(gcmMsgPrefs, new Long[]{Long.parseLong(aIDX)}, true);

                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == Messaging.class) {
                    if (JobsIDX > 0
                            && (Messaging.JobsIDX != null && !Messaging.JobsIDX.equals("") && Messaging.JobsIDX.equals(aJobsIDX))
                            && (Messaging.msgUserIDX != null && !Messaging.msgUserIDX.equals("") && Messaging.msgUserIDX.equals(aUserIDX))) {
                        final Messaging act = (Messaging) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                act.updateListView2();
                            }
                        });
                    } else if (JobsIDX == 0
                            && (Messaging.JobsIDX == null || Messaging.JobsIDX.equals("") || Messaging.JobsIDX.equals("0"))
                            && (Messaging.msgUserIDX != null && !Messaging.msgUserIDX.equals("") && Messaging.msgUserIDX.equals(aUserIDX))) {
                        final Messaging act = (Messaging) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                act.updateListView2();
                            }
                        });
                    } else {
                        objGlobalMethods.setGCMPrefs(gcmMsgPrefs, "unreadMsgExist", "true");
                        sendMultipleNoficationNew(ctx, msg, aUserName, aUserIDX, m, aJobsIDX);
                    }
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == Messaginglist.class) {
                    objGlobalMethods.setGCMPrefs(gcmMsgPrefs, "unreadMsgExist", "true");
                    sendMultipleNoficationNew(ctx, msg, aUserName, aUserIDX, m, aJobsIDX);

                    final Messaginglist act = (Messaginglist) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            act.updateList2();
                        }
                    });
                    //updateList
                } else {
                    objGlobalMethods.setGCMPrefs(gcmMsgPrefs, "unreadMsgExist", "true");
                    sendMultipleNoficationNew(ctx, msg, aUserName, aUserIDX, m, aJobsIDX);
                }


            /*if (myApp.currentActivity != null && myApp.currentActivity.getClass() == Messaging.class) {
                if (Messaging.msgUserIDX != null && !Messaging.msgUserIDX.equals("") && Messaging.msgUserIDX.equals(aUserIDX)) {
                    final Messaging act = (Messaging) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            act.updateListView2();
                        }
                    });
                } else {
                    objGlobalMethods.setGCMPrefs(gcmMsgPrefs, "unreadMsgExist", "true");
                    sendMultipleNoficationNew(ctx, msg, aUserName, aUserIDX, m, cms._id, aJobsIDX);
                }
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == Messaginglist.class) {
                objGlobalMethods.setGCMPrefs(gcmMsgPrefs, "unreadMsgExist", "true");
                sendMultipleNoficationNew(ctx, msg, aUserName, aUserIDX, m, cms._id, aJobsIDX);

                final Messaginglist act = (Messaginglist) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.updateList2();
                    }
                });
                //updateList
            } else {
                objGlobalMethods.setGCMPrefs(gcmMsgPrefs, "unreadMsgExist", "true");
                sendMultipleNoficationNew(ctx, msg, aUserName, aUserIDX, m, cms._id, aJobsIDX);
            }*/
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GCMIntentService handleIncomingMessage2. " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void handleMainCODrowNotCollectedButtonActivation(SharedPreferences gcmPrefs, final String aJobsIDXstr, final String aInvoiceIDXstr) {

        long[] JobsIDXInvoiceIDXpairArr = null;
        long[] existingJobsIDXInvoiceIDXpairArr = null;
        ArrayList<long[]> CODncActivationJobsIDXInvoiceIDXstrAL = null;
        String CODncActivationJobsIDXInvoiceIDXstrALJsonStr = "";
        Gson gson = new Gson();

        try {
            objGlobalMethods.writeToFile("GcmIntentService handleMainCODrowNotCollectedButtonActivation started for JobsIDX, " + aJobsIDXstr + ", InvoiceIDX, " + aInvoiceIDXstr, "", "", "", "");
            if (!aJobsIDXstr.equals("") && !aInvoiceIDXstr.equals("")) {

                //save in settings
                JobsIDXInvoiceIDXpairArr = new long[]{Long.parseLong(aJobsIDXstr), Long.parseLong(aInvoiceIDXstr)};

                CODncActivationJobsIDXInvoiceIDXstrALJsonStr = objGlobalMethods.getGCMPrefs(gcmPrefs, "CODncActivationJobsIDXInvoiceIDXstrALJsonStr");
                if (!CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("") && !CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("[]")) {
                    CODncActivationJobsIDXInvoiceIDXstrAL = gson.fromJson(CODncActivationJobsIDXInvoiceIDXstrALJsonStr, new TypeToken<List<long[]>>() {
                    }.getType());
                    Boolean exist = false;
                    for (int i = 0; i <= CODncActivationJobsIDXInvoiceIDXstrAL.size() - 1; i++) {
                        existingJobsIDXInvoiceIDXpairArr = CODncActivationJobsIDXInvoiceIDXstrAL.get(i);
                        if (existingJobsIDXInvoiceIDXpairArr[0] == JobsIDXInvoiceIDXpairArr[0] && existingJobsIDXInvoiceIDXpairArr[1] == JobsIDXInvoiceIDXpairArr[1]) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        CODncActivationJobsIDXInvoiceIDXstrAL.add(JobsIDXInvoiceIDXpairArr);
                    }
                } else {
                    CODncActivationJobsIDXInvoiceIDXstrAL = new ArrayList<>();
                    CODncActivationJobsIDXInvoiceIDXstrAL.add(JobsIDXInvoiceIDXpairArr);
                }

                if (CODncActivationJobsIDXInvoiceIDXstrAL != null && CODncActivationJobsIDXInvoiceIDXstrAL.size() > 0) {
                    CODncActivationJobsIDXInvoiceIDXstrALJsonStr = gson.toJson(CODncActivationJobsIDXInvoiceIDXstrAL);
                    objGlobalMethods.setGCMPrefs(gcmPrefs, "CODncActivationJobsIDXInvoiceIDXstrALJsonStr", CODncActivationJobsIDXInvoiceIDXstrALJsonStr);
                } else if (CODncActivationJobsIDXInvoiceIDXstrAL == null || CODncActivationJobsIDXInvoiceIDXstrAL.size() == 0) {
                    objGlobalMethods.setGCMPrefs(gcmPrefs, "CODncActivationJobsIDXInvoiceIDXstrALJsonStr", "");
                }

                if (myApp.currentActivity != null) {
                    if (myApp.currentActivity.getClass() == UpdateCOD_List.class) {

                        final UpdateCOD_List act = (UpdateCOD_List) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                // Notifiy activity
                                act.activateNCbutton(Long.parseLong(aJobsIDXstr), Long.parseLong(aInvoiceIDXstr));
                            }
                        });
                    }
                } else {

                }
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at GcmIntentService handleMainCODrowNotCollectedButtonActivation. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    public float getBatteryLevel() {
        try {
            Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // Error checking that probably isn't needed but I added just in case.
            if (level == -1 || scale == -1) {
                return 50.0f;
            }

            return ((float) level / (float) scale) * 100.0f;
        } catch (Exception e) {
            return 0;
        }

    }

}
