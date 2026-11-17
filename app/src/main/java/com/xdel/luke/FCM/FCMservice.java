package com.xdel.luke.FCM;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.AddressListNewActivity;
import com.xdel.luke.AddressListRoutingActivity;
import com.xdel.luke.BCnSVC.ActiveService;
import com.xdel.luke.BCnSVC.MpdRefreshReceiver;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.BattLvl_Receiver;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.GrabLogFileOnDemand_BroadcastReceiver;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.MPD_CheckOutActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.MapsAllActivity;
import com.xdel.luke.Messaging;
import com.xdel.luke.Messaginglist;
import com.xdel.luke.PieceByPiecePU.PipPcs;
import com.xdel.luke.R;
import com.xdel.luke.Rooting.RootGuard;
import com.xdel.luke.UpdateCOD_List;
import com.xdel.luke.WebService;
import com.xdel.luke.changeOfJobInfoDetails_BroadcastReceiver;
import com.xdel.luke.messagingClasses.ChatMessage;
import com.xdel.luke.messagingClasses.ChatMsgSenders;
import com.xdel.luke.messagingClasses.GetAllMsg_IntentService;
import com.xdel.luke.messagingClasses.Status;
import com.xdel.luke.messagingClasses.UserType;
import com.xdel.luke.messagingClasses.objChatMessage;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.objClasses.objUpdateJobInfoStatus;
import com.xdel.luke.sqlite.DAO_Jobs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class FCMservice extends FirebaseMessagingService {
    Context context;
    public static Activity currentActivity;
    public static final Object CURRENTACTIVIYLOCK = new Object();
    public static int myNOTIFICATION_ID = 1;
    private static String regid;
    public static NotificationManager mNotificationManager;
    public static String msg = "";
    public static Bundle notificationExtras;
    public static String newPIPhash = "";
    public static String newPUhash = "";
    public static String newDIPhash = "";
    public static String newDELhash = "";
    public static String newFPhash = "";
    public static String newFDhash = "";
    public static String newPDhash = "";
    public static String newTThash = "";
    public static String sysPIPhash = "";
    public static String sysPUhash = "";
    public static String sysDIPhash = "";
    public static String sysDELhash = "";
    public static String sysFPhash = "";
    public static String sysFDhash = "";
    public static String sysPDhash = "";
    public static String sysTThash = "";
    private static final Semaphore mutex = new Semaphore(1);
    private SharedPreferences prefs, UpdateJobInfoPreferences;
    private String username = "";
    private String useridx = "";
    private String[] UsernamePwd = new String[4];
    public static boolean notiNotRead = false;
    public static AlarmManager manager2;
    private static PendingIntent pendingIntent2;
    private static List<objUpdateJobInfoStatus> JobInfoListInQueForStatusUpdate;
    public static boolean enablePush = true;
    private Uri msgTone = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.text_message);

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        objGlobalMethods.storeRegistrationId(myApp.getAppContext(), s, objGlobalMethods.getGcmPreferences());
        //getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }

    public static String getToken() {
        String s = objGlobalMethods.getRegistrationId(null, objGlobalMethods.getGcmPreferences());
        return s;
        //return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //R6: ignore every push while RootGuard blocks this process, so no server-driven sync, settings change or log pull runs
        if (RootGuard.blockOperation("FCMservice.onMessageReceived", null)) {
            return;
        }
        synchronized (CURRENTACTIVIYLOCK) {
            try {
                //if this is called, there is working internet connection, check to send data back
                int queCount = JobsHandler.returnNumberOfQueToSend(objGlobalMethods.getUpdateJobInfoPreferences());
                if (queCount > 0) {
                    sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));
                }
                if (remoteMessage.getData().size() > 0) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(remoteMessage.getSentTime());
                    Date dt = new Date();
                    dt = c.getTime();

                    Map<String, String> params = remoteMessage.getData();
                    JSONObject extras = new JSONObject(params);
                    JSONObject extras2 = null;
                    String plb64 = "";
                    String pl = "";
                    if (extras != null) {
                        context = myApp.getAppContext();
                        ArrayList JobHashesAL = new ArrayList();
                        JSONArray JobHashesJsonArr;
                        ArrayList sysJobHashesAL;
                        prefs = objGlobalMethods.getGcmPreferences();
                        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                        regid = objGlobalMethods.getRegistrationIdOnly();
                        String disablePush = objGlobalMethods.getGCMPrefs(prefs, "disablePush");
                        if (!disablePush.toString().equals("")) {
                            return;
                        }

                        try {
                            /*String data = extras.has("Data") && !extras.getString("Data").equals(JSONObject.NULL) ? extras.getString("Data") : "";
                            String dataEx = extras.has("Data_Ex") && !extras.getString("Data_Ex").equals(JSONObject.NULL) ? extras.getString("Data_Ex") : "";
                            objGlobalMethods.writeToFile("Push received during logged out.\n\n" + data + "\n\n" + dataEx, "", "", "", "");*/

                            if (extras.has("Payload") && !objGlobalMethods.IsEmptyString(extras.getString("Payload"))) {
                                plb64 = extras.getString("Payload");
                                pl = objGlobalMethods.b64ToString(plb64);
                                extras2 = new JSONObject(pl);
                            }

                            if (extras2 == null) {
                                objGlobalMethods.writeToFile("FCMservice extras2 is null.\nPayload: " + plb64 + "\nPayloadValue: " + pl, "", "", "", "");
                                return;
                            }

                            if (enablePush) {
                                //objGlobalMethods.writeToFile("FCMservice enablePush true.", "", "", "", "");
                                if (extras2.has("JobHashes") && !extras2.getString("JobHashes").equals(JSONObject.NULL)) {
                                    try {
                                        sysPIPhash = prefs.getString("PIPhashName", "");
                                        sysPUhash = prefs.getString("PUhashName", "");
                                        sysDIPhash = prefs.getString("DIPhashName", "");
                                        sysDELhash = prefs.getString("DELhashName", "");
                                        sysFPhash = prefs.getString("FPhashName", "");
                                        sysFDhash = prefs.getString("FDhashName", "");
                                        sysPDhash = prefs.getString("PDhashName", "");
                                        sysTThash = prefs.getString("TThashName", "");
                                        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(prefs);
                                        username = UsernamePwd[0];
                                        useridx = UsernamePwd[2];
                                        Gson gson = new Gson();
                                        JobHashesJsonArr = new JSONArray(extras2.getString("JobHashes").toString());
                                        JobHashesAL = gson.fromJson(JobHashesJsonArr.toString(), ArrayList.class);

                                        if (JobHashesAL != null && JobHashesAL.size() > 0) {
                                            JobInfoListInQueForStatusUpdate = getJobInfoListInQueForStatusUpdate();
                                            //PIP------------------------------------------------------------
                                            newPIPhash = (String) JobHashesAL.get(0);
                                            //temporary loggingcode - remove once not required
                                            if (extras2.has("rtNew") && !extras2.getString("rtNew").equals(JSONObject.NULL)) {
                                                objGlobalMethods.writeToFile("rtNew JobsIDXs", extras2.getString("rtNew"), "", username, useridx);
                                                if (!newPIPhash.equals("")) {
                                                    if (!newPIPhash.equals(sysPIPhash)) {
                                                        //update
                                                        if (extras2.getString("rtNew") != null) {
                                                            try {
                                                                mutex.acquire();
                                                                //objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at FCMservice when newPIPhash is not empty and not equal sysPIPhash", extras2.getString("rtNew"), "", username, useridx);
                                                                DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                                RegetSpecificJobCategoryCountSQLite(0, context, true, newPIPhash, prefs, 0,
                                                                        extras2.getString("rtNew"), objGlobalVariables.rtNewJobsIDXarrName,
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
                                                    objGlobalMethods.writeToFile("Clearing all unwanted PIP jobs from DB.", "", "", "", "");
                                                    DAO_Jobs.deleteJobsByCatTypeID(0);
                                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                    if (sysPIPhash != "") {
                                                        updateDrawerPanel(0, objGlobalVariables.PIPhashName, "", true, null);
                                                    } else if (sysPIPhash == "") {
                                                        updateDrawerPanel(0, objGlobalVariables.PIPhashName, "", false, null);
                                                    }
                                                }
                                            } else if (newPIPhash.equals("") && sysPIPhash != "") {
                                                objGlobalMethods.writeToFile("Clearing all unwanted FP jobs from DB when newPIPhash is empty and sysPIPhash is not empty..", "", "", "", "");
                                                DAO_Jobs.deleteJobsByCatTypeID(0);
                                                JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                if (sysPIPhash != "") {
                                                    updateDrawerPanel(0, objGlobalVariables.PIPhashName, "", true, null);
                                                } else if (sysPIPhash == "") {
                                                    updateDrawerPanel(0, objGlobalVariables.PIPhashName, "", false, null);
                                                }
                                            }

                                            //PU------------------------------------------------------------
                                            newPUhash = (String) JobHashesAL.get(1);

                                            //temporary loggingcode - remove once not required
                                            if (extras2.has("rtPicked") && !extras2.getString("rtPicked").equals(JSONObject.NULL)) {
                                                objGlobalMethods.writeToFile("rtPicked JobsIDXs", extras2.getString("rtPicked"), "", username, useridx);
                                                if (!newPUhash.equals("")) {
                                                    if (!newPUhash.equals(sysPUhash)) {
                                                        //update
                                                        if (extras2.getString("rtPicked") != null) {
                                                            try {
                                                                mutex.acquire();
                                                                //objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at FCMservice when newPUhash is not empty and not equal sysPUhash", extras2.getString("rtPicked"), "", username, useridx);
                                                                DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                                RegetSpecificJobCategoryCountSQLite(1, context, false, newPUhash, prefs, 1,
                                                                        extras2.getString("rtPicked"), objGlobalVariables.rtPickedJobsIDXarrName,
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
                                                    objGlobalMethods.writeToFile("Clearing all unwanted PU jobs from DB.", "", "", "", "");
                                                    DAO_Jobs.deleteJobsByCatTypeID(1);
                                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                    if (sysPUhash != "") {
                                                        updateDrawerPanel(1, objGlobalVariables.PUhashName, "", true, null);
                                                    } else if (sysPUhash == "") {
                                                        updateDrawerPanel(1, objGlobalVariables.PUhashName, "", false, null);
                                                    }
                                                }
                                                JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                                            } else if (newPUhash.equals("") && sysPUhash != "") {
                                                objGlobalMethods.writeToFile("Clearing all unwanted FP jobs from DB when newPUhash is empty and sysPUhash is not empty..", "", "", "", "");
                                                DAO_Jobs.deleteJobsByCatTypeID(1);
                                                JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                if (sysPUhash != "") {
                                                    updateDrawerPanel(1, objGlobalVariables.PUhashName, "", true, null);
                                                } else if (sysPUhash == "") {
                                                    updateDrawerPanel(1, objGlobalVariables.PUhashName, "", false, null);
                                                }
                                                JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                                            }

                                            //DIP------------------------------------------------------------
                                            newDIPhash = (String) JobHashesAL.get(2);

                                            //temporary loggingcode - remove once not required
                                            if (extras2.has("rtDelivery") && !extras2.getString("rtDelivery").equals(JSONObject.NULL)) {
                                                objGlobalMethods.writeToFile("rtDelivery JobsIDXs", extras2.getString("rtDelivery"), "", username, useridx);
                                                if (!newDIPhash.equals("")) {
                                                    if (!newDIPhash.equals(sysDIPhash)) {
                                                        //update
                                                        if (extras2.getString("rtDelivery") != null) {
                                                            try {
                                                                mutex.acquire();
                                                                //objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at FCMservice when newDIPhash is not empty and not equal sysDIPhash", extras2.getString("rtDelivery"), "", username, useridx);
                                                                DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                                RegetSpecificJobCategoryCountSQLite(2, context, false, newDIPhash, prefs, 2,
                                                                        extras2.getString("rtDelivery"), objGlobalVariables.rtDeliveryJobsIDXarrName,
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
                                                    objGlobalMethods.writeToFile("Clearing all unwanted DIP jobs from DB.", "", "", "", "");
                                                    DAO_Jobs.deleteJobsByCatTypeID(2);
                                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                    if (sysDIPhash != "") {
                                                        updateDrawerPanel(2, objGlobalVariables.DIPhashName, "", true, null);
                                                    } else if (sysDIPhash == "") {
                                                        updateDrawerPanel(2, objGlobalVariables.DIPhashName, "", false, null);
                                                    }
                                                }
                                            } else if (newDIPhash.equals("") && sysDIPhash != "") {
                                                objGlobalMethods.writeToFile("Clearing all unwanted FP jobs from DB when newDIPhash is empty and sysDIPhash is not empty..", "", "", "", "");
                                                DAO_Jobs.deleteJobsByCatTypeID(2);
                                                JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                if (sysDIPhash != "") {
                                                    updateDrawerPanel(2, objGlobalVariables.DIPhashName, "", true, null);
                                                } else if (sysDIPhash == "") {
                                                    updateDrawerPanel(2, objGlobalVariables.DIPhashName, "", false, null);
                                                }
                                            }

                                            //DEL------------------------------------------------------------
                                            newDELhash = (String) JobHashesAL.get(3);

                                            //temporary loggingcode - remove once not required
                                            if (extras2.has("rtDelivered") && !extras2.getString("rtDelivered").equals(JSONObject.NULL)) {
                                                objGlobalMethods.writeToFile("rtDelivered JobsIDXs", extras2.getString("rtDelivered"), "", username, useridx);
                                                if (!newDELhash.equals("")) {
                                                    if (!newDELhash.equals(sysDELhash)) {
                                                        //update
                                                        if (extras2.getString("rtDelivered") != null) {
                                                            try {
                                                                mutex.acquire();
                                                                //objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at FCMservice when newDELhash is not empty and not equal sysDELhash", extras2.getString("rtDelivered"), "", username, useridx);
                                                                DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                                RegetSpecificJobCategoryCountSQLite(3, context, false, newDELhash, prefs, 3,
                                                                        extras2.getString("rtDelivered"), objGlobalVariables.rtDeliveredJobsIDXarrName,
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
                                                    objGlobalMethods.writeToFile("Clearing all unwanted OKP jobs from DB.", "", "", "", "");
                                                    DAO_Jobs.deleteJobsByCatTypeID(3);
                                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                    if (sysDELhash != "") {
                                                        updateDrawerPanel(3, objGlobalVariables.DELhashName, "", true, null);
                                                    } else if (sysDELhash == "") {
                                                        updateDrawerPanel(3, objGlobalVariables.DELhashName, "", false, null);
                                                    }
                                                }
                                                JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                                            } else if (newDELhash.equals("") && sysDELhash != "") {
                                                objGlobalMethods.writeToFile("Clearing all unwanted FP jobs from DB when newDELhash is empty and sysDELhash is not empty..", "", "", "", "");
                                                DAO_Jobs.deleteJobsByCatTypeID(3);
                                                JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                if (sysDELhash != "") {
                                                    updateDrawerPanel(3, objGlobalVariables.DELhashName, "", true, null);
                                                } else if (sysDELhash == "") {
                                                    updateDrawerPanel(3, objGlobalVariables.DELhashName, "", false, null);
                                                }
                                                JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                                            }

                                            //FP------------------------------------------------------------
                                            newFPhash = (String) JobHashesAL.get(4);

                                            //temporary loggingcode - remove once not required
                                            if (extras2.has("rtFailedPickUp") && !extras2.getString("rtFailedPickUp").equals(JSONObject.NULL)) {
                                                objGlobalMethods.writeToFile("rtFailedPickUp JobsIDXs", extras2.getString("rtFailedPickUp"), "", username, useridx);
                                                if (!newFPhash.equals("")) {
                                                    if (!newFPhash.equals(sysFPhash)) {
                                                        //update
                                                        if (extras2.getString("rtFailedPickUp") != null) {
                                                            try {
                                                                mutex.acquire();
                                                                //objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at FCMservice when newFPhash is not empty and not equal sysFPhash", extras2.getString("rtFailedPickUp"), "", username, useridx);
                                                                DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                                RegetSpecificJobCategoryCountSQLite(4, context, false, newFPhash, prefs, 4,
                                                                        extras2.getString("rtFailedPickUp"), objGlobalVariables.rtFailedPickUpJobsIDXarrName,
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
                                                    objGlobalMethods.writeToFile("Clearing all unwanted FP jobs from DB.", "", "", "", "");
                                                    DAO_Jobs.deleteJobsByCatTypeID(4);
                                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                    if (sysFPhash != "") {
                                                        updateDrawerPanel(4, objGlobalVariables.FPhashName, "", true, null);
                                                    } else if (sysFPhash == "") {
                                                        updateDrawerPanel(4, objGlobalVariables.FPhashName, "", false, null);
                                                    }
                                                }
                                                JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                                            } else if (newFPhash.equals("") && sysFPhash != "") {
                                                objGlobalMethods.writeToFile("Clearing all unwanted FP jobs from DB when newFPhash is empty and sysFPhash is not empty..", "", "", "", "");
                                                DAO_Jobs.deleteJobsByCatTypeID(4);
                                                JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                if (sysFPhash != "") {
                                                    updateDrawerPanel(4, objGlobalVariables.FPhashName, "", true, null);
                                                } else if (sysFPhash == "") {
                                                    updateDrawerPanel(4, objGlobalVariables.FPhashName, "", false, null);
                                                }
                                                JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                                            }

                                            //FD------------------------------------------------------------
                                            newFDhash = (String) JobHashesAL.get(5);

                                            //temporary loggingcode - remove once not required
                                            if (extras2.has("rtFailedDelivery") && !extras2.getString("rtFailedDelivery").equals(JSONObject.NULL)) {
                                                objGlobalMethods.writeToFile("rtFailedDelivery JobsIDXs", extras2.getString("rtFailedDelivery"), "", username, useridx);
                                                if (!newFDhash.equals("")) {
                                                    if (!newFDhash.equals(sysFDhash)) {
                                                        //update
                                                        if (extras2.getString("rtFailedDelivery") != null) {
                                                            try {
                                                                mutex.acquire();
                                                                //objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at FCMservice when newFDhash is not empty and not equal sysFDhash", extras2.getString("rtFailedDelivery"), "", username, useridx);
                                                                DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                                RegetSpecificJobCategoryCountSQLite(5, context, false, newFDhash, prefs, 5,
                                                                        extras2.getString("rtFailedDelivery"), objGlobalVariables.rtFailedDeliveryJobsIDXarrName,
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
                                                    objGlobalMethods.writeToFile("Clearing all unwanted FD jobs from DB.", "", "", "", "");
                                                    DAO_Jobs.deleteJobsByCatTypeID(5);
                                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                    if (sysFDhash != "") {
                                                        updateDrawerPanel(5, objGlobalVariables.FDhashName, "", true, null);
                                                    } else if (sysFDhash == "") {
                                                        updateDrawerPanel(5, objGlobalVariables.FDhashName, "", false, null);
                                                    }
                                                }
                                                JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                                            } else if (newFDhash.equals("") && sysFDhash != "") {
                                                objGlobalMethods.writeToFile("Clearing all unwanted FD jobs from DB when newFDhash is empty and sysFDhash is not empty.", "", "", "", "");
                                                DAO_Jobs.deleteJobsByCatTypeID(5);
                                                JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                if (sysFDhash != "") {
                                                    updateDrawerPanel(5, objGlobalVariables.FDhashName, "", true, null);
                                                } else if (sysFDhash == "") {
                                                    updateDrawerPanel(5, objGlobalVariables.FDhashName, "", false, null);
                                                }
                                                JobsHandler2.UpdateUnreadMsgAftJobUpdate();
                                            }

                                            //PD------------------------------------------------------------
                                            newPDhash = (String) JobHashesAL.get(6);

                                            //temporary loggingcode - remove once not required
                                            if (extras2.has("rtParkedDelivery") && !extras2.getString("rtParkedDelivery").equals(JSONObject.NULL)) {
                                                objGlobalMethods.writeToFile("rtParkedDelivery JobsIDXs", extras2.getString("rtParkedDelivery"), "", username, useridx);
                                                if (!newPDhash.equals("")) {
                                                    if (!newPDhash.equals(sysPDhash)) {
                                                        //update
                                                        if (extras2.getString("rtParkedDelivery") != null) {
                                                            try {
                                                                mutex.acquire();
                                                                //objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at FCMservice when newPDhash is not empty and not equal sysPDhash", extras2.getString("rtParkedDelivery"), "", username, useridx);
                                                                DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                                RegetSpecificJobCategoryCountSQLite(6, context, false, newPDhash, prefs, 6,
                                                                        extras2.getString("rtParkedDelivery"), objGlobalVariables.rtParkedDeliveryJobsIDXarrName,
                                                                        objGlobalVariables.PDhashName, objGlobalVariables.JobInfoPDALjsonPrefName,
                                                                        objGlobalVariables.PDjobsCountName);
                                                            } catch (InterruptedException e) {
                                                                objGlobalMethods.writeToFile("Calling BackendRun. InterruptedException when newPDhash is not empty string, " + e.getMessage(),
                                                                        "", "", username, useridx);
                                                                JobsHandler.sendErrorEmail("Calling BackendRun. InterruptedException when newPDhash is not empty string for Driver: " + username + ", " + e.getMessage());
                                                                Context ctx = myApp.getAppContext();
                                                                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                                            } finally {
                                                                mutex.release();
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    objGlobalMethods.writeToFile("Clearing all unwanted PD jobs from DB.", "", "", "", "");
                                                    DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                    DAO_Jobs.deleteJobsByCatTypeID(6);
                                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                    if (sysPDhash != "") {
                                                        updateDrawerPanel(6, objGlobalVariables.PDhashName, "", true, null);
                                                    } else if (sysFDhash == "") {
                                                        updateDrawerPanel(6, objGlobalVariables.PDhashName, "", false, null);
                                                    }
                                                }
                                            } else if (newPDhash.equals("") && sysPDhash != "") {
                                                objGlobalMethods.writeToFile("Clearing all unwanted PD jobs from DB when newPDhash is empty and sysPDhash is not empty.", "", "", "", "");
                                                DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                DAO_Jobs.deleteJobsByCatTypeID(6);
                                                JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                if (sysPDhash != "") {
                                                    updateDrawerPanel(6, objGlobalVariables.PDhashName, "", true, null);
                                                } else if (sysPDhash == "") {
                                                    updateDrawerPanel(6, objGlobalVariables.PDhashName, "", false, null);
                                                }
                                            }

                                            //TT------------------------------------------------------------
                                            newTThash = (String) JobHashesAL.get(7);

                                            //temporary loggingcode - remove once not required
                                            if (extras2.has("rtInTransit") && !extras2.getString("rtInTransit").equals(JSONObject.NULL)) {
                                                objGlobalMethods.writeToFile("rtInTransit JobsIDXs", extras2.getString("rtInTransit"), "", username, useridx);
                                                if (!newTThash.equals("")) {
                                                    if (!newTThash.equals(sysTThash)) {
                                                        //update
                                                        if (extras2.getString("rtInTransit") != null) {
                                                            try {
                                                                mutex.acquire();
                                                                //objGlobalMethods.writeToFile("Calling RegetSpecificJobCategoryCountSQLite at FCMservice when newTThash is not empty and not equal sysTThash", extras2.getString("rtInTransit"), "", username, useridx);
                                                                ////DAO_Jobs.updateParkedDIPJobsToParkedCat();
                                                                RegetSpecificJobCategoryCountSQLite(7, context, false, newTThash, prefs, 7,
                                                                        extras2.getString("rtInTransit"), objGlobalVariables.rtInTransitJobsIDXarrName,
                                                                        objGlobalVariables.TThashName, objGlobalVariables.JobInfoTTALjsonPrefName,
                                                                        objGlobalVariables.TTjobsCountName);
                                                            } catch (InterruptedException e) {
                                                                objGlobalMethods.writeToFile("Calling BackendRun. InterruptedException when newTThash is not empty string, " + e.getMessage().toString(),
                                                                        "", "", username, useridx);
                                                                JobsHandler.sendErrorEmail("Calling BackendRun. InterruptedException when newTThash is not empty string for Driver: " + username + ", " + e.getMessage().toString());
                                                                Context ctx = myApp.getAppContext();
                                                                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                                                            } finally {
                                                                mutex.release();
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    objGlobalMethods.writeToFile("Clearing all unwanted TT jobs from DB.", "", "", "", "");
                                                    DAO_Jobs.deleteJobsByCatTypeID(7);
                                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                    if (sysTThash != "") {
                                                        updateDrawerPanel(7, objGlobalVariables.TThashName, "", true, null);
                                                    } else if (sysTThash == "") {
                                                        updateDrawerPanel(7, objGlobalVariables.TThashName, "", false, null);
                                                    }
                                                }
                                            } else if (newTThash.equals("") && sysTThash != "") {
                                                objGlobalMethods.writeToFile("Clearing all unwanted TT jobs from DB when newTThash is empty and sysTThash is not empty.", "", "", "", "");
                                                DAO_Jobs.deleteJobsByCatTypeID(7);
                                                JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                                if (sysTThash != "") {
                                                    updateDrawerPanel(7, objGlobalVariables.TThashName, "", true, null);
                                                } else if (sysTThash == "") {
                                                    updateDrawerPanel(7, objGlobalVariables.TThashName, "", false, null);
                                                }
                                            }

                                        } else {
                                            sendNotification2New(context, myApp.getAppContext().getString(R.string.alert_error));
                                        }
                                    } catch (Exception e) {
                                        objGlobalMethods.writeToFile("Calling BackendRun. Exception at FCMservice onMessageReceived, " + e.getMessage().toString(),
                                                "", "", username, useridx);
                                        JobsHandler.sendErrorEmail("Calling BackendRun. Exception at FCMservice onMessageReceived for Driver: " + username + ", " + e.getMessage().toString());
                                        Context ctx = myApp.getAppContext();
                                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                                    }
                                }
                            } else {
                                objGlobalMethods.writeToFile("EnablePush is false at FCMservice onMessageReceived. Jobhashes not resync.", "", "", "", "");
                            }
                        } catch (Exception e) {
                            String x = e.getMessage().toString();
                            objGlobalMethods.writeToFile("Exception ta FCMservice onMessageReceived in Try Catch outside EnablePush check. Exception: " + x, "", "", "", "");
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
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("1")) {
                            objGlobalMethods.writeToFile("OpID 1 received to set gps grabbing interval.", "", "", "", "");
                            //Opid #1: gpsGrabbingInterval
                            int interval = 0; //0
                            int count = 0;
                            String data = "";
                            String dataEx = "";
                            try {
                                data = extras2.has("Data") && !extras2.getString("Data").equals(JSONObject.NULL) ? extras2.getString("Data") : "";
                                dataEx = extras2.has("Data_Ex") && !extras2.getString("Data_Ex").equals(JSONObject.NULL) ? extras2.getString("Data_Ex") : "";

                                try {
                                    interval = data.equals("") ? 0 : Integer.parseInt(data);
                                } catch (Exception e) {
                                    objGlobalMethods.writeToFile("Exception caught at FCMservice onMessageReceived for setting gpsGrabbingInterval. Exception: , " + e.getMessage().toString(),
                                            "", "", "", "");
                                    JobsHandler.sendErrorEmail("Exception caught at FCMservice onMessageReceived for setting gpsGrabbingInterval for Driver: " + username + ", " + e.getMessage().toString());
                                    interval = 0;
                                }

                                try {
                                    count = dataEx.equals("") ? 0 : Integer.parseInt(dataEx);
                                } catch (Exception e) {
                                    String stack = JobsHandler.getStackTraceString(e);
                                    objGlobalMethods.writeToFile("Exception caught at FCMservice onMessageReceived for setting GPSsendingIntervalCount. Exception: , "
                                            + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                                    JobsHandler.sendErrorEmail("Exception caught at FCMservice onMessageReceived for setting GPSsendingIntervalCount for Driver: " + username + ", " + e.getMessage().toString());
                                    count = 0;
                                }

                            } catch (Exception e) {
                                String stack = JobsHandler.getStackTraceString(e);
                                objGlobalMethods.writeToFile("Exception caught at FCMservice onMessageReceived for setting gpsGrabbingInterval or GPSsendingIntervalCount. Exception: , "
                                        + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                                JobsHandler.sendErrorEmail("Exception caught at FCMservice onMessageReceived for setting gpsGrabbingInterval or GPSsendingIntervalCount for Driver: " + username + ", " + e.getMessage().toString());
                                interval = 0;
                                count = 0;
                            } finally {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("GPSalarmInterval", interval == 0 || count == 0 ? "" : String.valueOf(interval));
                                editor.putString("GPSsendingIntervalCount", interval == 0 || count == 0 ? "" : String.valueOf(count));
                                editor.apply();

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
                        } else if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("2")) {
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
                                data = extras2.has("Data") && !extras2.getString("Data").equals(JSONObject.NULL) ? extras2.getString("Data") : "";
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
                                                objGlobalMethods.writeToFile("Exception caught at FCMservice onMessageReceived for converting String JobsIDX "
                                                        + parts[v] + " to Long JobsIDX. Exception: , " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                                                JobsHandler.sendErrorEmail("Exception caught at FCMservice onMessageReceived for converting String JobsIDX " + parts[v] + " to Long JobsIDX for Driver: " + username + ", " + e.getMessage().toString());
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

                                        objGlobalMethods.writeToFile("FCM OpID 2", "", "", "", "");
                                        DAO_Jobs.cleanPIPPCS();

                                        updateDrawerPanel(0, objGlobalVariables.PIPhashName, newHash, true, null);
                                    } else {
                                        objGlobalMethods.writeToFile("JobsIDXlist is empty at FCMservice onMessageReceived OpID = 2", "", "", "", "");
                                    }
                                }
                            } catch (Exception e) {
                                String stack = JobsHandler.getStackTraceString(e);
                                objGlobalMethods.writeToFile("Exception caught at FCMservice onMessageReceived for setting gpsGrabbingInterval or GPSsendingIntervalCount. Exception: , "
                                        + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                                JobsHandler.sendErrorEmail("Exception caught at FCMservice onMessageReceived for setting gpsGrabbingInterval or GPSsendingIntervalCount for Driver: " + username + ", " + e.getMessage().toString());
                            }
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("3")) {
                            //force logout
                            //long st = Long.parseLong("1740619151000");
                            String forceLogOutTime = objGlobalMethods.getGCMPrefs(prefs, "forceLogOutTime");
                            long comparedTimeLong = !objGlobalMethods.IsEmptyString(forceLogOutTime) ? Long.parseLong(forceLogOutTime) : 0;

                            //boolean moreThanXHours = JobsHandler.moreThanXhours(st, 5);
                            boolean moreThanXHours = JobsHandler.moreThanXhours(remoteMessage.getSentTime(), 5);
                            boolean moreThanXMin = JobsHandler.moreThanXminutesVersion2(comparedTimeLong, remoteMessage.getSentTime(), 10);

                            if (moreThanXHours) {
                                //this is due to backend multiple sent during wee hours n device received e push, so we counter check with NOW
                                objGlobalMethods.writeToFile("OpID 3 received to force logout skipped. OpID sent out more than 5 hours.", "", "", "", "");
                            } else if (!moreThanXMin) {
                                //this to check if push came within 10mins after last force log out
                                String[] prefName = new String[]{"forceLogOutTime"};
                                String[] prefvalue = new String[]{String.valueOf(new Date().getTime())};
                                objGlobalMethods.setGCMPrefsMass(prefs, prefName, prefvalue);
                                objGlobalMethods.writeToFile("OpID 3 received to force logout skipped. OpID sent out within 10 minutes.", "", "", "", "");
                            } else {
                                objGlobalMethods.writeToFile("OpID 3 received to force logout. getSentTime " + String.valueOf(remoteMessage.getSentTime()), "", "", "", "");

                                String[] prefName = new String[]{"forceLogOutBooleanString", "forceLogOutTime", "gpsLocked"};
                                String[] prefvalue = new String[]{"true", String.valueOf(new Date().getTime()), ""};
                                objGlobalMethods.setGCMPrefsMass(prefs, prefName, prefvalue);
                                JobsHandler2.checkForBatchIDsDataToSendBack();
                                sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                            }

                            /*if (!moreThanXHours) {
                                objGlobalMethods.writeToFile("OpID 3 received to force logout. getSentTime " + String.valueOf(remoteMessage.getSentTime()), "", "", "", "");
                                *//*objGlobalMethods.setGCMPrefs(prefs, "forceLogOutBooleanString", "true");
                                objGlobalMethods.setGCMPrefs(prefs, "gpsLocked", "");*//*

                                forceLogOutTime = String.valueOf(new Date().getTime());
                                String[] prefName = new String[]{"forceLogOutBooleanString", "forceLogOutTime", "gpsLocked"};
                                String[] prefvalue = new String[]{"true", forceLogOutTime, ""};
                                objGlobalMethods.setGCMPrefsMass(prefs, prefName, prefvalue);
                                JobsHandler2.checkForBatchIDsDataToSendBack();
                                sendBroadcast(new Intent(context, BackendRun_BroadcastReceiver.class));
                            } else {
                                objGlobalMethods.writeToFile("OpID 3 received to force logout skipped. OpID sent out more than 5 hours.", "", "", "", "");
                            }*/
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("4")) {
                            if (JobsHandler.moreThanXminutesZerofied(remoteMessage.getSentTime(), 5)) {
                                objGlobalMethods.writeToFile("OpID 4 received to force refresh @ dashboard skipped. OpID sent out more than 5 minutes ago.", "", "", "", "");
                            } else {
                                objGlobalMethods.writeToFile("OpID 4 received to force refresh @ dashboard.", "", "", "", "");
                                //force refresh when @ dashboard
                                objGlobalMethods.setGCMPrefs(prefs, "refreshQueBooleanString", "true");
                                Context ctx = myApp.getAppContext();
                                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                            }

                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("5")) {
                            objGlobalMethods.writeToFile("OpID 5 received to grab log file.", "", "", "", "");
                            //grab log file
                            Context ctx = myApp.getAppContext();
                            sendBroadcast(new Intent(ctx, GrabLogFileOnDemand_BroadcastReceiver.class));
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("6")) {
                            objGlobalMethods.writeToFile("OpID 6 received to change of JobInfo details.", "", "", "", "");
                            //detect change of JobInfo details
                            String editedJobsIDXstr = "";
                            editedJobsIDXstr = extras2.has("Data") && !extras2.getString("Data").equals(JSONObject.NULL) ? extras2.getString("Data") : "";
                            if (!editedJobsIDXstr.equals("")) {
                                processEditedJobInfo(prefs, editedJobsIDXstr);
                            }
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("7")) {
                            objGlobalMethods.writeToFile("OpID 7 received to activate COD not collected button.", "", "", "", "");
                            //activate MainCODrow Not Collected button
                            String InvoiceIDXstr = extras2.has("Data_Ex") && !extras2.getString("Data_Ex").equals(JSONObject.NULL) && !extras2.getString("Data_Ex").equals("") ? extras2.getString("Data_Ex") : "";
                            String JobsIDXstr = extras2.has("Data") && !extras2.getString("Data").equals(JSONObject.NULL) && !extras2.getString("Data").equals("") ? extras2.getString("Data") : "";
                            objGlobalMethods.writeToFile("OpID #7 received for JobsIDX, " + JobsIDXstr + ", InvoiceIDX, " + InvoiceIDXstr + ".", "", "", "", "");
                            handleMainCODrowNotCollectedButtonActivation(prefs, JobsIDXstr, InvoiceIDXstr);
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("8")) {
                            objGlobalMethods.writeToFile("OpID 8 received to grab batt level.", "", "", "", "");
                            //Opid #8: grab Batt level Integer
                            float battlvl = 0;
                            Integer battlvlint = 0;
                            battlvl = getBatteryLevel();
                            battlvlint = Math.round(battlvl);
                            objGlobalMethods.setGCMPrefs(prefs, "battlvl", String.valueOf(battlvlint));
                            sendBroadcast(new Intent(context, BattLvl_Receiver.class));
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("9")) {
                            objGlobalMethods.writeToFile("OpID 9 received to grab new routing solution.", "", "", "", "");
                            //grab new routing solution
                            JobsHandler.HandleRouteList(regid, prefs);
                            //JobsHandler.getJobQue2(regid, true, true, -1);
                            ArrayList<Integer> ListItemIndex = new ArrayList<>();
                            ListItemIndex.add(-1);
                            String JobsPostalsToBeUNPARKEDJsonStr = objGlobalMethods.getGCMPrefs(prefs, "JobsPostalsToBeUNPARKEDJsonStr");
                            ArrayList<String> JobsPostalsToBeUNPARKED = null;
                            if (!objGlobalMethods.IsEmptyString(JobsPostalsToBeUNPARKEDJsonStr)) {
                                JobsPostalsToBeUNPARKED = new Gson().fromJson(JobsPostalsToBeUNPARKEDJsonStr, new TypeToken<ArrayList<String>>() {
                                }.getType());
                            }
                            //DrawerMain2Activity.calculateAvailCredits();
                            JobsHandler.getJobQue2(regid, true, true, ListItemIndex, JobsPostalsToBeUNPARKED);
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("10")) {
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
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("11")) {
                            //settings related
                            objGlobalMethods.grabSettingsFromServer();
                            objGlobalMethods.writeToFile("OpID 11 received to grab settings from Luke Cabinet.", "", "", "", "");
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("12")) {
                            //refresh MPD Check-In/Out related
                            objGlobalMethods.writeToFile("OpID 12 received to refresh MPDlist.", "", "", "", "");

                            if (cdTimer != null) {
                                Log.i("FCMservice", "Restart Timer forceRestart = true");
                                forceRestart = true;
                                cdTimer.cancel();
                                forceRestart = false;
                                handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        startTimer();
                                    }
                                });
                            } else {
                                Log.i("FCMservice", "Fresh Start Timer");
                                forceRestart = false;
                                handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        startTimer();
                                    }
                                });
                            }

                            /*ArrayList<objMPD_Info> mpds = objGlobalMethods.grabMPDFromServer();
                            objGlobalMethods.setMPDsPrefStr(mpds, prefs);
                            refreshMPD_CheckOutList();*/
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("13")) {
                            //activate deactivate Spic
                            //dataEx = "0" deactivate
                            //dataEx = "1" activate
                            String dataEx = "";
                            try {
                                dataEx = extras2.has("Data_Ex") && !extras2.getString("Data_Ex").equals(JSONObject.NULL) ? extras2.getString("Data_Ex") : "";
                                if (!dataEx.isEmpty() && !dataEx.equals("") && dataEx != "") {
                                    objGlobalMethods.setGCMPrefs(prefs, "ActDeactSpic", dataEx);
                                    objGlobalMethods.writeToFile("OpID 12 received to activate deactivate Spic with code " + dataEx + ".", "", "", "", "");
                                }
                            } catch (Exception e) {
                                String s = e.getMessage().toString();
                            }
                        }
                        if (extras2.has("OpID") && !extras2.getString("OpID").equals(JSONObject.NULL) && !extras2.getString("OpID").equals("") && extras2.getString("OpID").equals("14")) {
                            //GetInventoryBatch
                            objGlobalMethods.writeToFile("OpID 14 received for GetInventoryBatch.", "", "", "", "");
                            String BatchID = extras2.has("Data") && !extras2.getString("Data").equals(JSONObject.NULL) && !extras2.getString("Data").equals("") ? extras2.getString("Data") : "";
                            JobsHandler2.GetInventoryBatch(BatchID);
                        }
                        //boolean llite = JobsHandler2.isLuke_Reduced(prefs);
                        if (extras2.has("Message") && !extras2.getString("Message").equals(JSONObject.NULL) && !extras2.getString("Message").equals("") &&
                                extras2.has("IDX") && !extras2.getString("IDX").equals(JSONObject.NULL) && !extras2.getString("IDX").equals("") &&
                                extras2.has("UserIDX") && !extras2.getString("UserIDX").equals(JSONObject.NULL) && !extras2.getString("UserIDX").equals("") &&
                                extras2.has("UserName") && !extras2.getString("UserName").equals(JSONObject.NULL) && !extras2.getString("UserName").equals("")) {
                            handleIncomingMessage2(
                                    extras2.getString("TimeStamp"),
                                    extras2.has("IDX") && !extras2.getString("IDX").equals(JSONObject.NULL) ? extras2.getString("IDX") : "0",
                                    extras2.has("UserIDX") && !extras2.getString("UserIDX").equals(JSONObject.NULL) ? extras2.getString("UserIDX") : "0",
                                    extras2.has("UserName") && !extras2.getString("UserName").equals(JSONObject.NULL) ? extras2.getString("UserName") : "",
                                    extras2.has("JobsIDX") && !extras2.getString("JobsIDX").equals(JSONObject.NULL) ? extras2.getString("JobsIDX") : "0",
                                    extras2.has("Message") && !extras2.getString("Message").equals(JSONObject.NULL) ? extras2.getString("Message") : "",
                                    extras2.has("Reference") && !extras2.getString("Reference").equals(JSONObject.NULL) && !extras2.getString("Reference").equals("") ? extras2.getString("Reference") : "");
                        }

                    }
                } else {
                    objGlobalMethods.writeToFile("FCM onMessageReceived remoteMessage.getData().size() == 0.", "", "", "", "");
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at FCMservice onMessageReceived. Exception: " + s, "", "", "", "");
            }
        }
    }

    Handler handler;
    static CountDownTimer cdTimer = null;
    boolean forceRestart = false;

    private void startTimer() {
        try {
            cdTimer = new CountDownTimer(6 * 1000, 1 * 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    if (cdTimer != null) {
                        cdTimer.cancel();
                        cdTimer = null;
                    }

                    Log.i("FCMservice", "startTimer onFinish forceRestart " + String.valueOf(forceRestart));
                    if (!forceRestart) {
                        Log.i("FCMservice", "sendBroadcast(MpdRefreshReceiver_)");
                        Intent MpdRefreshReceiver_ = new Intent(context, MpdRefreshReceiver.class);
                        sendBroadcast(MpdRefreshReceiver_);
                    }
                }
            }.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at FCMservice startTimer. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
        String RegID = objGlobalMethods.getRegistrationIdOnly();
        ArrayList<Long> StatuscodeExclusionChecks = new ArrayList<>();
        StatuscodeExclusionChecks.add(Long.parseLong("368"));
        StatuscodeExclusionChecks.add(Long.parseLong("369"));
        StatuscodeExclusionChecks.add(Long.parseLong("388"));
        String insertedJobsIDX = "";
        ArrayList<String> NewPIPpostals = null;
        String pippostal = "";
        ArrayList<String> JobsPostalsToBeUNPARKED = new ArrayList<>();

        try {
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();

            if (!categoryJobsIDXjsonStr.equals("")) {
                JSONArray newJobsIDXjarr = new JSONArray(categoryJobsIDXjsonStr);
                Long[] newJobsIDXlongArr = gson.fromJson(newJobsIDXjarr.toString(), Long[].class);
                List<Long> newJobsIDXlongArrList = new ArrayList<Long>(Arrays.asList(newJobsIDXlongArr));

                if (JobInfoListInQueForStatusUpdate != null && JobInfoListInQueForStatusUpdate.size() > 0) {
                    //there are jobs status update pending
                    for (int i = 0; i <= JobInfoListInQueForStatusUpdate.size() - 1; i++) {
                        if (!StatuscodeExclusionChecks.contains(JobInfoListInQueForStatusUpdate.get(i).StatusCodeIDX)) {
                            if (newJobsIDXlongArrList.contains(JobInfoListInQueForStatusUpdate.get(i).JobsIDX)) {
                                newJobsIDXlongArrList.remove(JobInfoListInQueForStatusUpdate.get(i).JobsIDX);
                            }
                        }
                        /*if (newJobsIDXlongArrList.contains(JobInfoListInQueForStatusUpdate.get(i).JobsIDX)) {
                            newJobsIDXlongArrList.remove(JobInfoListInQueForStatusUpdate.get(i).JobsIDX);
                        }*/
                    }
                }

                if (newJobsIDXlongArrList.size() > 0) {
                    Long[] remainingJobsIDXarr = new Long[newJobsIDXlongArrList.size()];
                    for (int i = 0; i <= newJobsIDXlongArrList.size() - 1; i++)
                        remainingJobsIDXarr[i] = newJobsIDXlongArrList.get(i);
                    newExtraJobInfoAL = WebService.GetDriverJobsInfoAL(RegID, remainingJobsIDXarr, "L2_GetDriverJobInfo");

                    if (newExtraJobInfoAL != null && newExtraJobInfoAL.length > 0) {
                        newExtraJobInfoList = new ArrayList<objJobInfo>(Arrays.asList(newExtraJobInfoAL));
                        List<Long> dbJobsIDXsList = DAO_Jobs.getAlldbObjJobsIDXsPerCategory(CatTypeID);

                        int HASLUNCHTIME = 0;

                        //create list of jobs to be inserted to db for this category
                        for (int x = 0; x <= newExtraJobInfoList.size() - 1; x++) {
                            HASLUNCHTIME = 0;
                            objJobInfo ji = newExtraJobInfoList.get(x);
                            if (CatTypeID == 0) {
                                if (ji.PULunch_Avoid != null && ji.PULunch_Avoid &&
                                        ji.PULunch_From != null && !ji.PULunch_From.equals("") &&
                                        ji.PULunch_To != null && !ji.PULunch_To.equals("")) {
                                    HASLUNCHTIME = 1;
                                }
                            } else if (CatTypeID == 2) {
                                if (ji.DLLunch_Avoid != null && ji.DLLunch_Avoid &&
                                        ji.DLLunch_From != null && !ji.DLLunch_From.equals("") &&
                                        ji.DLLunch_To != null && !ji.DLLunch_To.equals("")) {
                                    HASLUNCHTIME = 1;
                                }
                            }

                            ji.setHASLUNCHTIME(HASLUNCHTIME);

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

                        ArrayList<PipPcs> pippcslist = null;
                        List<Long> JobsIDXList = null;
                        Long[] JobsIDXsArr = null;
                        String jobsidxs = "";
                        if (JobInfoToBeInsertedList != null && JobInfoToBeInsertedList.size() > 0) {
                            pippcslist = CatTypeID == 0 ? JobsHandler2.rtnPipPcs(null, JobInfoToBeInsertedList) : null;
                            //insert all these record into db base on this cattypeid
                            for (int i = 0; i <= JobInfoToBeInsertedList.size() - 1; i++) {
                                objJobInfo ji = JobInfoToBeInsertedList.get(i);
                                JobInfoJsonStr = gson.toJson(ji);
                                if (i < JobInfoToBeInsertedList.size() - 1) {
                                    insertedJobsIDX += ji.JobsIDX + ",";
                                } else if (i == JobInfoToBeInsertedList.size() - 1) {
                                    insertedJobsIDX += ji.JobsIDX;
                                }

                                if (ListItemIndex == 0 || ListItemIndex == 1 || ListItemIndex == 4) {
                                    DAO_Jobs.updateParkedJobsToDIPCat(
                                            ji.PURedirectedLocation != null ? ji.PURedirectedLocation.POSTALCODE : ji.PULocation.POSTALCODE
                                    );
                                    if (ListItemIndex == 0) {
                                        String JobsPostalsToBeUNPARKEDJsonStr = objGlobalMethods.getGCMPrefs(prefs, "JobsPostalsToBeUNPARKEDJsonStr");
                                        if (!objGlobalMethods.IsEmptyString(JobsPostalsToBeUNPARKEDJsonStr)) {
                                            JobsPostalsToBeUNPARKED = new Gson().fromJson(JobsPostalsToBeUNPARKEDJsonStr, new TypeToken<ArrayList<String>>() {
                                            }.getType());
                                        }
                                        if (!JobsPostalsToBeUNPARKED.contains(ji.PURedirectedLocation != null ? ji.PURedirectedLocation.POSTALCODE : ji.PULocation.POSTALCODE)) {
                                            JobsPostalsToBeUNPARKED.add(ji.PURedirectedLocation != null ? ji.PURedirectedLocation.POSTALCODE : ji.PULocation.POSTALCODE);
                                        }
                                    } else {
                                        JobsPostalsToBeUNPARKED.add(ji.PURedirectedLocation != null ? ji.PURedirectedLocation.POSTALCODE : ji.PULocation.POSTALCODE);
                                    }
                                } else if (ListItemIndex == 2 || ListItemIndex == 3 || ListItemIndex == 5) {
                                    DAO_Jobs.updateParkedJobsToDIPCat(
                                            ji.DLRedirectedLocation != null ? ji.DLRedirectedLocation.POSTALCODE : ji.DLLocation.POSTALCODE
                                    );
                                    JobsPostalsToBeUNPARKED.add(ji.DLRedirectedLocation != null ? ji.DLRedirectedLocation.POSTALCODE : ji.DLLocation.POSTALCODE);
                                }

                                /*if (ListItemIndex == 0 || ListItemIndex == 1 || ListItemIndex == 4) {
                                    JobsPostalsToBeUNPARKED.add(ji.PURedirectedLocation != null ? ji.PURedirectedLocation.POSTALCODE : ji.PULocation.POSTALCODE);
                                } else if (ListItemIndex == 2 || ListItemIndex == 3 || ListItemIndex == 5) {
                                    JobsPostalsToBeUNPARKED.add(ji.DLRedirectedLocation != null ? ji.DLRedirectedLocation.POSTALCODE : ji.DLLocation.POSTALCODE);
                                }*/

                                DAO_Jobs.insertJobs(ji, CatTypeID, JobInfoJsonStr);
                                if (isNewPIP && hasNewPIPjobs) {
                                    if (NewPIPpostals == null)
                                        NewPIPpostals = new ArrayList<>();
                                    pippostal = ji.PULocation.POSTALCODE;
                                    NewPIPpostals.add(pippostal);
                                }

                                if (isNewPIP && ji.StatusCodeIDX == 0 || ji.StatusCodeIDX == 235 || ji.StatusCodeIDX == 165) {
                                    /*Date dt = new Date();
                                    Long l = dt.getTime();
                                    String timestampstr = "/Date(" + l + ")/";

                                    objGlobalMethods.writeToFile("Update job status from " + String.valueOf(ji.StatusCodeIDX) + " to 103", String.valueOf(ji.JobsIDX), "103", username, useridx);

                                    Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                                    int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, ji.JobsIDX, 103, timestampstr, "", "",
                                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");*/

                                    if (JobsIDXList == null)
                                        JobsIDXList = new ArrayList<>();
                                    if (ji.JobsIDX > 0 && !JobsIDXList.contains(ji.JobsIDX))
                                        JobsIDXList.add(ji.JobsIDX);


                                    requireUpdate = true;
                                } else {
                                    JobsHandler.getGCPerformancePercentage(UpdateJobInfoPreferences, prefs, false);
                                }
                            }

                            if (JobsIDXList != null && JobsIDXList.size() > 0) {
                                jobsidxs = Arrays.toString(JobsIDXList.toArray());
                                objGlobalMethods.writeToFile("From FCMservice, Update job status to 103", "JobsIDX: " +
                                        jobsidxs, "103", "", "");

                                JobsIDXsArr = JobsIDXList.toArray(new Long[JobsIDXList.size()]);

                                Date dt = new Date();
                                Long l = dt.getTime();
                                String timestampstr = "/Date(" + l + ")/";

                                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

                                JobsHandler.addUpdateJobInfoStatusWithGPSMassQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDXsArr, 103,
                                        timestampstr, "", "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(),
                                        Accuracy, "");
                            }

                            if (pippcslist != null && pippcslist.size() > 0) {
                                objGlobalMethods.writeToFile("FCM bulkInsertPIPPCS pippcslist size " + String.valueOf(pippcslist.size()), "", "", "", "");
                                DAO_Jobs.bulkInsertPIPPCS(pippcslist);
                            }
                            if (CatTypeID == 0 || CatTypeID == 1 || CatTypeID == 2) {
                                objGlobalMethods.writeToFile("FCM cleanPIPPCS", "", "", "", "");
                                DAO_Jobs.cleanPIPPCS();
                            }


                            String NewPIPpostalsJsonStr = "";
                            if (NewPIPpostals != null && NewPIPpostals.size() > 0) {
                                NewPIPpostalsJsonStr = gson.toJson(NewPIPpostals);
                            }

                            String JobsPostalsToBeUNPARKEDJsonStr = "";
                            if (JobsPostalsToBeUNPARKED != null && JobsPostalsToBeUNPARKED.size() > 0) {
                                JobsPostalsToBeUNPARKEDJsonStr = gson.toJson(JobsPostalsToBeUNPARKED);
                            }

                            String[] prefName = new String[]{"NewPIPpostalsJsonStr", "JobsPostalsToBeUNPARKEDJsonStr"};
                            String[] prefvalue = new String[]{NewPIPpostalsJsonStr, JobsPostalsToBeUNPARKEDJsonStr};
                            objGlobalMethods.setGCMPrefsMass(prefs, prefName, prefvalue);

                            if (!objGlobalMethods.IsEmptyString(insertedJobsIDX)) {
                                objGlobalMethods.writeToFile("insertJobs at FCMservice RegetSpecificJobCategoryCountSQLite when CatTypeID = " + String.valueOf(CatTypeID) + ", JobsIDXs: " + insertedJobsIDX, "", "", "", "");
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

                        updateDrawerPanel(ListItemIndex, objGlobalVariablesCategoryHashName, newHash, true, JobsPostalsToBeUNPARKED);
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
                objGlobalMethods.writeToFile("Job hash from server is empty at FCMservice RegetSpecificJobCategoryCountSQLite when CatTypeID is " + String.valueOf(CatTypeID), "", "", "", "");
            }
        } catch (Exception e) {
            prefs = objGlobalMethods.getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(prefs);
            username = UsernamePwd[0];
            useridx = UsernamePwd[2];
            String stacktrace = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at FCMservice, RegetSpecificJobCategoryCountSQLite." + "\n" + e.getMessage().toString() + "\n" + "Stack: " + stacktrace,
                    "", "", username, useridx);

            JobsHandler.sendErrorEmail("Exception caught at FCMservice for " + username + ", RegetSpecificJobCategoryCountSQLite."
                    + "\n" + e.getMessage().toString() + "\n" + "Stack: " + stacktrace);

            Context ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
        }
    }

    private void sendNotification2New(Context ctx, String msg) {
        try {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(gcmp, "showpippop", "t");
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
            notiNotRead = true;
            mNotificationManager.notify(2, b.build());

            objGlobalMethods.showNewPIPdialogUponReceived(gcmp);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at FCMservice sendNotification2New. Exception: , " + e.getMessage().toString() + "\nStack: " + stack,
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
            //Uri alarmSound = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.raw.text_message);

            NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, myApp.CHANNEL_ID_MSG);

            b.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_action_info_outline)
                    .setLargeIcon(bm)
                    .setContentTitle(UserName)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setWhen(System.currentTimeMillis())
                    .setGroup(group_id)
                    .setSound(msgTone)
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
            objGlobalMethods.writeToFile("Exception caught at FCMservice sendMultipleNoficationNew. Exception: , " + e.getMessage().toString() + "\nStack: " + stack,
                    "", "", "", "");
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
            if (context == null) {
                context = myApp.getAppContext();
            }
            sendBroadcast(new Intent(context, changeOfJobInfoDetails_BroadcastReceiver.class));

        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at FCMservice processEditedJobInfo. Force refresh data activated. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
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

    private void updateDrawerPanel(final int ListItemIndex, final String objGlobalVariablesCategoryHashName, final String cHashstr, final Boolean changeCountColor, ArrayList<String> JobsPostalsToBeUNPARKED) {
        final SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        JobsHandler.setCategoryHashes(GcmPreferences, objGlobalVariablesCategoryHashName, cHashstr);
        String isrefreshing = objGlobalMethods.getGCMPrefs(GcmPreferences, "isrefreshing");
        try {
            if (!isrefreshing.equals("t")) {
                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {


                    ArrayList<Integer> lii = new ArrayList<>();
                    if (ListItemIndex == 0 || ListItemIndex == 1 || ListItemIndex == 4) {
                        lii.add(0);
                        lii.add(1);
                        lii.add(2);
                        lii.add(6);
                    } else if (ListItemIndex == 2 || ListItemIndex == 3 || ListItemIndex == 5 || ListItemIndex == 6 || ListItemIndex == 7) {
                        lii.add(2);
                        lii.add(3);
                        lii.add(6);
                        lii.add(7);
                    }
                    /*if(ListItemIndex == 0 || ListItemIndex == 2 || ListItemIndex == 6)
                        DrawerMain2Activity.calculateAvailCredits();*/
                    JobsHandler.getJobQue2(regid, true, false, lii, JobsPostalsToBeUNPARKED);

                    /*final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            // Notifiy activity
                            act.updateListRowPerCategory(ListItemIndex, changeCountColor);
                        }
                    });*/
                } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                    /*final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            act.refreshrv();
                        }
                    });*/

                    //JobsHandler.getJobQue2(regid, true, true, -1);
                    ArrayList<Integer> lii = new ArrayList<>();
                    //lii.add(-1);
                    if(ListItemIndex == 0 || ListItemIndex == 2 || ListItemIndex == 6)
                        lii.add(ListItemIndex);
                    JobsHandler.getJobQue2(regid, true, true, lii, JobsPostalsToBeUNPARKED);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at FCMservice updateDrawerPanel. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void refreshMPD_CheckOutList() {
        try {
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MPD_CheckOutActivity.class) {
                final MPD_CheckOutActivity act = (MPD_CheckOutActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.refreshlist();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == MapsAllActivity.class) {
                final MapsAllActivity act = (MapsAllActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListRoutingActivity.class) {
                final AddressListRoutingActivity act = (AddressListRoutingActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == AddressListNewActivity.class) {
                final AddressListNewActivity act = (AddressListNewActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobsListGroupActivity.class) {
                final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.promptReqMPD();
                    }
                });
            } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DashboardSubActivity.class) {
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
            objGlobalMethods.writeToFile("Exception at FCMservice refreshMPD_CheckOutList. Exception: " + s, "", "", "", "");
        }
    }

    public static void stopAlarmForCommon() {
        try {
            if (manager2 != null) {
                manager2.cancel(pendingIntent2);
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at FCMservice, stopAlarmForCommon. Exception: " + e.getMessage().toString() + "\nStack: " + stack,
                    "", "", "", "");
        }

    }

    private List<objUpdateJobInfoStatus> getJobInfoListInQueForStatusUpdate() {
        List<objUpdateJobInfoStatus> ListUpdateJobInfoStatusALAL = null;
        Gson gson = new Gson();
        objUpdateJobInfoStatus[] objUpdateJobInfoStatusALQue;
        objUpdateJobInfoStatus objUpdateJobInfoStatusObj;

        try {
            mutex.acquire();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
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

    private void handleIncomingMessage2(String aTimeStamp, String aIDX, String aUserIDX, String aUserName, String aJobsIDX, String msg, String aRef) {
        Context ctx = myApp.getAppContext();
        SharedPreferences gcmMsgPrefs;
        ChatMsgSenders cms = null;
        final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        ArrayList<ChatMessage> cmal;
        List<ChatMsgSenders> chatmsgsendersForAllUsersAL = null;
        List<ChatMsgSenders> chatmsgsendersAL = null;
        String address = "";

        try {
            Date d = sdf.parse(aTimeStamp);
            aTimeStamp = "/Date(" + d.getTime() + "+0800)/";
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
                /*address = DAO_Jobs.getAddressWithPostalNoUnitByJobsIDX(String.valueOf(JobsIDX));
                aUserName += !address.isEmpty() && !address.equals("") ? "\n" + address : "";*/
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

                objChatMessage cm = new objChatMessage(String.valueOf(objGlobalMethods.DateToLong(aTimeStamp)), Long.parseLong(aIDX), Long.parseLong(aUserIDX), aUserName, Long.parseLong(aJobsIDX), msg, 200,
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
                    if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                        final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                act.setMsgCountNew(null);
                            }
                        });
                    } else if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMainActivity.class) {
                        final DrawerMainActivity act = (DrawerMainActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                act.setMsgCountNew(null);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at FCMservice handleIncomingMessage2. " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void handleMainCODrowNotCollectedButtonActivation(SharedPreferences gcmPrefs, final String aJobsIDXstr, final String aInvoiceIDXstr) {

        long[] JobsIDXInvoiceIDXpairArr = null;
        long[] existingJobsIDXInvoiceIDXpairArr = null;
        ArrayList<long[]> CODncActivationJobsIDXInvoiceIDXstrAL = null;
        String CODncActivationJobsIDXInvoiceIDXstrALJsonStr = "";
        Gson gson = new Gson();

        try {
            objGlobalMethods.writeToFile("FCMservice handleMainCODrowNotCollectedButtonActivation started for JobsIDX, " + aJobsIDXstr + ", InvoiceIDX, " + aInvoiceIDXstr, "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception caught at FCMservice handleMainCODrowNotCollectedButtonActivation. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
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
