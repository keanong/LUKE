package com.xdel.luke.Voip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.JobInfo;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCallStatus;
import com.xdel.luke.objClasses.objCallStatusHashmapList;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.setDeliveredStatusActivity;
import com.xdel.luke.setFailStatusActivity;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.VideoPreviewOpParam;
import org.pjsip.pjsua2.VideoWindowHandle;
import org.pjsip.pjsua2.pjmedia_orient;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;
import org.pjsip.pjsua2.pjsip_status_code;

import java.util.Date;
import java.util.HashMap;

class VideoPreviewHandler implements SurfaceHolder.Callback {
    public boolean videoPreviewActive = false;

    public void updateVideoPreview(SurfaceHolder holder) {
        if (JobInfoActivity.currentCall != null &&
                JobInfoActivity.currentCall.vidWin != null &&
                JobInfoActivity.currentCall.vidPrev != null) {
            if (videoPreviewActive) {
                VideoWindowHandle vidWH = new VideoWindowHandle();
                vidWH.getHandle().setWindow(holder.getSurface());
                VideoPreviewOpParam vidPrevParam = new VideoPreviewOpParam();
                vidPrevParam.setWindow(vidWH);
                try {
                    JobInfoActivity.currentCall.vidPrev.start(vidPrevParam);
                } catch (Exception e) {
                    System.out.println(e);
                }
            } else {
                try {
                    JobInfoActivity.currentCall.vidPrev.stop();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        updateVideoPreview(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            JobInfoActivity.currentCall.vidPrev.stop();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

public class CallActivity extends Activity
        implements Handler.Callback, SurfaceHolder.Callback, SensorEventListener {

    ImageView ibtnSpeaker;
    ImageView ibtnInfo;
    ImageView ibtnBT;
    //private static VideoPreviewHandler previewHandler = new VideoPreviewHandler();
    public static Handler handler_;
    public static CallInfo lastCallInfo;
    public static String log;
    //public static Boolean callloginserted;
    public static AudioManager audioManager;
    public static CountDownTimer cdTimer0, cdTimer;
    public static Long timeFirstInContactToThisPage;
    public static Long timeConnectingStarted;
    public static Long timeConnected;
    public static Long timeEnded;
    //public static Boolean disconnectionProcessingAtCallActivity;
    private boolean speakerOn = false;
    private int connectingCount;
    String CallRtn = "";
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private PowerManager.WakeLock proximityWakeLock;
    private TextView lbltel;
    public static Boolean callendedlogged = false;
    public static Boolean callHungUp = false;
    public static Boolean startRtnToJobInfoActivity2_called = false;
    private BroadcastReceiver bluetooth_Device_Detector_BC;
    ScreenShotContentObserver screenShotContentObserver;
    objCallStatusHashmapList CallStatusHashmapList;
    private WindowManager wm;
    private PowerManager pm;
    private boolean btReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call2);
        try {
            myApp.currentActivity = this;
            /*SurfaceView surfaceInVideo = (SurfaceView)
                    findViewById(R.id.surfaceIncomingVideo);
            SurfaceView surfacePreview = (SurfaceView)
                    findViewById(R.id.surfacePreviewCapture);
            Button buttonShowPreview = (Button)
                    findViewById(R.id.buttonShowPreview);*/
            lbltel = (TextView) findViewById(R.id.lbltel);

            ibtnSpeaker = (ImageView) findViewById(R.id.ibtnSpeaker);
            ibtnInfo = (ImageView) findViewById(R.id.ibtnInfo);
            ibtnBT = (ImageView) findViewById(R.id.ibtnBT);

            JobInfoActivity.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            JobInfoActivity.mProximity = JobInfoActivity.mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            /*if (JobInfoActivity.currentCall == null ||
                    JobInfoActivity.currentCall.vidWin == null) {
                surfaceInVideo.setVisibility(View.GONE);
                buttonShowPreview.setVisibility(View.GONE);
            }
            setupVideoPreview(surfacePreview, buttonShowPreview);
            surfaceInVideo.getHolder().addCallback(this);
            surfacePreview.getHolder().addCallback(previewHandler);*/

            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            CallRtn = objGlobalMethods.getGCMPrefs(GcmPreferences, "CallRtn");

            if (timeFirstInContactToThisPage == null) {
                timeFirstInContactToThisPage = new Date().getTime();
            }

            showBtnHangUp0();

            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            boolean bt_connected = objGlobalMethods.getGCMPrefs(gcmp, "bt_connected").equals("t") ? true : false;
            if (!bt_connected) {
                init_bluetooth_device_detection();
                toggleSpeaker2();
            } else {
                if (audioManager == null) {
                    Context ctx = myApp.getAppContext();
                    audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                }
                if (audioManager.isBluetoothScoOn()) {
                    audioManager.setBluetoothScoOn(true);
                    ibtnBT.setVisibility(View.VISIBLE);
                } else if (!audioManager.isSpeakerphoneOn()) {
                    initBluetoothAudioManager(false);
                }
                toggleSpeaker2();
            }

            String Calltel = objGlobalMethods.getGCMPrefs(GcmPreferences, "Calltel");
            lbltel.setText(JobInfoActivity.maskContactNum != null && JobInfoActivity.maskContactNum ? JobsHandler2.rtnMaskedContactNumber(Calltel) : Calltel);
            //lbltel.setText(Calltel);
            if (CallRtn.equals("") || CallRtn.equals("n")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "callloginserted", "f");
                //callloginserted = false;
                handler_ = new Handler();
            } else {
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "callloginserted", "t");
                ibtnInfo.setVisibility(View.VISIBLE);
                //callloginserted = true;
            }

            if (JobInfoActivity.currentCall != null) {
                try {
                    lastCallInfo = JobInfoActivity.currentCall.getInfo();
                    updateCallState(lastCallInfo);
                } catch (Exception e) {
                    //System.out.println(e);
                    String s = e.getMessage().toString();
                }
            } else {
                updateCallState(lastCallInfo);
            }
            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
        } catch (Exception e) {
            setAudioMode(0);
            objGlobalMethods.writeToFile("CalLActivity onCreated. Exception: " + e.getMessage().toString(), "", "", "", "");
            startRtnToJobInfoActivity2();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }

            JobInfoActivity.mSensorManager.registerListener(this, JobInfoActivity.mProximity, SensorManager.SENSOR_DELAY_NORMAL);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            JobInfoActivity.mSensorManager.unregisterListener(this);
            if (bluetooth_Device_Detector_BC != null) {
                unregisterReceiver(bluetooth_Device_Detector_BC);
            }
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CalLActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity onPause. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] < event.sensor.getMaximumRange()) {
                    pm = (PowerManager) myApp.getAppContext().getSystemService(Context.POWER_SERVICE);
                    JobInfoActivity.proximityWakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "CallActivityCallWakeLock");
                    JobInfoActivity.proximityWakeLock.acquire();
                } else {
                    if (JobInfoActivity.proximityWakeLock != null && JobInfoActivity.proximityWakeLock.isHeld()) {
                        JobInfoActivity.proximityWakeLock.release();
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CallActivity onSensorChanged. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Display display;
        int rotation;
        //pjmedia_orient orient;
        int orient;

        //wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        wm = (WindowManager) myApp.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        rotation = display.getRotation();
        //System.out.println("Device orientation changed: " + rotation);

        switch (rotation) {
            case Surface.ROTATION_0:   // Portrait
                orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_270DEG;
                break;
            case Surface.ROTATION_90:  // Landscape, home button on the right
                orient = pjmedia_orient.PJMEDIA_ORIENT_NATURAL;
                break;
            case Surface.ROTATION_180:
                orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_90DEG;
                break;
            case Surface.ROTATION_270: // Landscape, home button on the left
                orient = pjmedia_orient.PJMEDIA_ORIENT_ROTATE_180DEG;
                break;
            default:
                orient = pjmedia_orient.PJMEDIA_ORIENT_UNKNOWN;
        }

        if (VoipApp.ep != null && JobInfoActivity.account != null) {
            try {
                AccountConfig cfg = JobInfoActivity.account.cfg;
                int cap_dev = cfg.getVideoConfig().getDefaultCaptureDevice();
                VoipApp.ep.vidDevManager().setCaptureOrient(cap_dev, orient,
                        true);
            } catch (Exception e) {
                //System.out.println(e);
                String s = e.getMessage().toString();
            }
        }
    }

    @Override
    public void onBackPressed() {
        JobInfoActivity.ongoingcall = true;
        Intent intent = new Intent(myApp.getAppContext(),
                (myApp.currentActivity != null && myApp.currentActivity.getClass() == setFailStatusActivity.class) ? setFailStatusActivity.class :
                        (myApp.currentActivity != null && myApp.currentActivity.getClass() == setDeliveredStatusActivity.class) ? setDeliveredStatusActivity.class : JobInfoActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler_ = null;
        try {
            if (bluetooth_Device_Detector_BC != null) {
                unregisterReceiver(bluetooth_Device_Detector_BC);
            }
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CalLActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
            }

            if (JobInfoActivity.mSensorManager != null) {
                JobInfoActivity.mProximity = null;
                JobInfoActivity.mSensorManager.unregisterListener(this);
                //mSensorManager = null;
            }

            if (ibtnSpeaker != null) {
                ibtnSpeaker.setOnClickListener(null);
            }
            if (ibtnInfo != null) {
                ibtnInfo.setOnClickListener(null);
            }
            if (wm != null) {
                wm = null;
            }
            if (pm != null) {
                pm = null;
            }

            lbltel = null;
            ibtnSpeaker = null;
            ibtnInfo = null;
            CallRtn = null;
            ibtnBT = null;
            JobInfoActivity.proximityWakeLock = null;
        } catch (Exception e) {
            bluetooth_Device_Detector_BC = null;
            objGlobalMethods.writeToFile("Exception at CallActivity onDestroy. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (btReceiverRegistered && bluetooth_Device_Detector_BC != null) {
                unregisterReceiver(bluetooth_Device_Detector_BC);
                btReceiverRegistered = false;
            }
        } catch (Exception e) {
            bluetooth_Device_Detector_BC = null;
            objGlobalMethods.writeToFile("Exception at CallActivity onStop. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void updateVideoWindow(boolean show) {
        /*if (JobInfoActivity.currentCall != null &&
                JobInfoActivity.currentCall.vidWin != null &&
                JobInfoActivity.currentCall.vidPrev != null) {
            SurfaceView surfaceInVideo = (SurfaceView)
                    findViewById(R.id.surfaceIncomingVideo);

            VideoWindowHandle vidWH = new VideoWindowHandle();
            if (show) {
                vidWH.getHandle().setWindow(
                        surfaceInVideo.getHolder().getSurface());
            } else {
                vidWH.getHandle().setWindow(null);
            }
            try {
                JobInfoActivity.currentCall.vidWin.setWindow(vidWH);
            } catch (Exception e) {
                //System.out.println(e);
                String s = e.getMessage().toString();
            }
        }*/
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        updateVideoWindow(true);
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        updateVideoWindow(false);
    }

    public void acceptCall(View view) {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            JobInfoActivity.currentCall.answer(prm);
        } catch (Exception e) {
            //System.out.println(e);
            String s = e.getMessage().toString();
        }

        view.setVisibility(View.GONE);
    }

    public void hangupCall(View view) {
        try {
            setAudioMode(0);
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            final String callOfc = objGlobalMethods.getGCMPrefs(gcmp, "callOfc");
            log = objGlobalMethods.getGCMPrefs(gcmp, "CallLogFullMsg");
            log = objGlobalMethods.composeCallLog(log, "GC ended call.");
            objGlobalMethods.writeToFile("GC ended call.", "", "", "", "");
            timeEnded = CallActivity.init_timeEnded();
            if (callendedlogged != null && callendedlogged) {
                //do nothing
            } else {
                callendedlogged = true;
                if (callOfc.equals("")) {
                    log = objGlobalMethods.composeCallLog(log, "Call ended. Call Duration: " + String.valueOf(CallActivity.returnCallDuration()) + " seconds.");
                    objGlobalMethods.writeToFile("CalLActivity hangupCall started.", "", "", "", "");
                }
            }

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences gp = objGlobalMethods.getGcmPreferences();
                    String callloginserted = objGlobalMethods.getGCMPrefs(gp, "callloginserted");
                    boolean within10s = JobsHandler.within10s(gp);
                    if (!callloginserted.equals("t") && !within10s && callOfc.equals("")) {
                        objGlobalMethods.setGCMPrefs(gp, "callloginserted", "t");
                        Gson gson = new Gson();
                        String CallLogInsertedTime = gson.toJson(new Date());
                        objGlobalMethods.setGCMPrefs(gp, "CallLogInsertedTime", CallLogInsertedTime);
                        objGlobalMethods.writeToFile("Insert call log at CallActivity hangupCall.", "", "", "", "");
                        insertCallLog(false);
                    }
                }
            }, 500);

            callHungUp = true;
            startRtnToJobInfoActivity2();
        } catch (Exception e) {
            setAudioMode(0);
            objGlobalMethods.writeToFile("Exception at CallActivity hangupCall. Exception: " + e.getMessage(), "", "", "", "");
            callHungUp = true;
            startRtnToJobInfoActivity2();
        }
    }

    private static void insertCallLog(boolean connected) {
        try {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            String Calltel = objGlobalMethods.getGCMPrefs(GcmPreferences, "Calltel");
            int catType = objGlobalMethods.getDashboard2ActivityCategoryType(objGlobalMethods.getGcmPreferences());
            //callloginserted = true;
            //objGlobalMethods.setGCMPrefs(GcmPreferences, "callloginserted", "t");
            DAO_Jobs.insertCallLog(Calltel, new Date().getTime(), catType, false, connected);
            String AllValidTelNums = objGlobalMethods.getGCMPrefs(objGlobalMethods.getGcmPreferences(), "AllValidTelNums");
            if (!objGlobalMethods.IsEmptyString(AllValidTelNums)) {
                setFailStatusActivity.CheckReqCallLogImg2(AllValidTelNums);
            } else {
                setFailStatusActivity.CheckReqCallLogImg2(Calltel);
            }
            JobInfoActivity.maskContactNum = !JobsHandler2.canShowFullContactNumber(GcmPreferences);
            if (!(new ConnectionDetector(null).isConnectingToInternet()))
                JobInfoActivity.maskContactNum = false;
            JobInfoActivity.maskContactNum = false; // forcefully added here until further notice (by boss)
            /*if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class && !JobInfoActivity.maskContactNum) {
                final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                act.runOnUiThread(new Runnable() {
                    public void run() {
                        act.unmaskTel();
                    }
                });
            }*/
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at CalLActivity insertCallLog. Exception: " + s, "", "", "", "");
        }
    }

    private static void UpdateCallLogCallEnded() {
        SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        Long JobsIDX = null;

        try {
            String CallJobsIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "CallJobsIDX");
            String Calltel = objGlobalMethods.getGCMPrefs(GcmPreferences, "Calltel");
            String Callext = objGlobalMethods.getGCMPrefs(GcmPreferences, "Callext");

            try {
                JobsIDX = Long.parseLong(CallJobsIDX);
            } catch (Exception e) {
                JobsIDX = Long.parseLong("0");
            }

            log = objGlobalMethods.getGCMPrefs(GcmPreferences, "CallLogFullMsg");
            if (JobsIDX > 0 && log != null && !log.isEmpty() && !log.equals("") && log != "") {
                String timestampstr = objGlobalMethods.rtntimestampstr();
                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), GcmPreferences);
                JobsHandler.addUpdateJobInfoCallLogsQueALPrefs(UpdateJobInfoPreferences, regid, JobsIDX, 388, timestampstr,
                        log, "",
                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

                objGlobalMethods.writeToFile("Call ended with " + Calltel + ".", "JobsIDX: " + CallJobsIDX, "Submitted StatusCodeIDX: 388", "", "");
                Context ctx = myApp.getAppContext();
                ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at CalLActivity UpdateCallLogCallEnded. Exception: " + s, "", "", "", "");
        }
    }

    private static void startRtnToJobInfoActivity2() {
        try {
            if (startRtnToJobInfoActivity2_called != null && startRtnToJobInfoActivity2_called) {
                //do nothing
            } else {
                startRtnToJobInfoActivity2_called = true;
                JobInfoActivity.ongoingcall = null;
                UpdateCallLogCallEnded();
                log = null;
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "callloginserted", "f");
                objGlobalMethods.setGCMPrefs(gcmp, "disconnectionProcessingAtCallActivity", "f");
                handler_ = null;
                if (cdTimer0 != null) {
                    cdTimer0.cancel();
                    cdTimer0 = null;
                }

                if (cdTimer != null) {
                    cdTimer.cancel();
                    cdTimer = null;
                }

                if (JobInfoActivity.currentCall != null) {
                    CallOpParam prm = new CallOpParam();
                    prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
                    try {
                        JobInfoActivity.currentCall.hangup(prm);
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at CalLActivity startRtnToJobInfoActivity2 when JobInfoActivity.currentCall != null. Exception: " + e.getMessage(), "", "", "", "");
                        JobInfoActivity.currentCall = null;
                    }
                }

                if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CallActivity.class) {
                    Intent intent = new Intent(myApp.getAppContext(), JobInfoActivity.class);
                    myApp.currentActivity.startActivity(intent);
                    myApp.currentActivity.finish();
                }

            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CalLActivity startRtnToJobInfoActivity2. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public boolean handleMessage(Message m) {
        try {

        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
        if (m.what == JobInfoActivity.MSG_TYPE.CALL_STATE) {

            lastCallInfo = (CallInfo) m.obj;
            updateCallState(lastCallInfo);

        } else if (m.what == JobInfoActivity.MSG_TYPE.CALL_MEDIA_STATE) {

            /*if (JobInfoActivity.currentCall.vidWin != null) {
             *//* Set capture orientation according to current
             * device orientation.
             *//*
                onConfigurationChanged(getResources().getConfiguration());
                *//* If there's incoming video, display it. *//*
                setupVideoSurface();
            }*/

        } else {

            /* Message not handled */
            return false;

        }

        return true;
    }

    private void updateCallState(CallInfo ci) {
        try {
            Context ctx = myApp.getAppContext();
            LinearLayout base = (LinearLayout) findViewById(R.id.base);
            TextView tvPeer = (TextView) findViewById(R.id.textViewPeer);
            TextView tvState = (TextView) findViewById(R.id.textViewCallState);
            Button buttonHangup = (Button) findViewById(R.id.buttonHangup);
            Button buttonAccept = (Button) findViewById(R.id.buttonAccept);
            ImageView ibtnHangup = (ImageView) findViewById(R.id.ibtnHangup);
            String call_state = "";
            String state = ci.getStateText();

            if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAC) {
                buttonAccept.setVisibility(View.GONE);
            }

            if (state.equals("CONFIRMED") || state.equals("DISCONNCTD")) {
                objGlobalMethods.removedDialedNumberToHashmap(lbltel.getText().toString());
            } else {
                objGlobalMethods.addDialedNumberToHashmap(lbltel.getText().toString(), state);
            }

            //if (ci.getState().swigValue() < pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue()) {
            if (ci.getState() < pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAS) {
                    call_state = "Incoming call..";
                    /* Default button texts are already 'Accept' & 'Reject' */
                } else {
                    buttonHangup.setText("Cancel");
                    call_state = ci.getStateText();
                    if (call_state.toLowerCase().equals("early") || call_state.toLowerCase().equals("calling")) {
                        setAudioMode(2);
                        blink(true);
                        tvPeer.setText(ctx.getString(R.string.calling));
                        if (connectingCount == 0 && (JobInfoActivity.ongoingcall == null || !JobInfoActivity.ongoingcall)) {
                            JobInfoActivity.ongoingcall = true;
                            timeConnectingStarted = new Date().getTime();
                            try {
                                if (cdTimer0 != null) {
                                    cdTimer0.cancel();
                                    cdTimer0 = null;
                                }
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception at CallActivity updateCallState checking cdTimer0 state. Exception: " + e.getMessage().toString(), "", "", "", "");
                                cdTimer0 = null;
                            }
                            showBtnHangUp();
                            ibtnInfo.setVisibility(View.VISIBLE);
                            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                            log = objGlobalMethods.getGCMPrefs(gcmp, "CallLogFullMsg");
                            log = objGlobalMethods.composeCallLog(log, "Connecting/Ringing");
                            objGlobalMethods.writeToFile("Call connecting at CalLActivity", "", "", "", "");
                            connectingCount += 1;
                        } else if (connectingCount == 1) {
                            connectingCount += 1;
                            ibtnHangup.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            //else if (ci.getState().swigValue() >= pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue()) {
            else if (ci.getState() >= pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                buttonAccept.setVisibility(View.GONE);
                call_state = ci.getStateText();
                ibtnHangup.setVisibility(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED || ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED ? View.VISIBLE : View.GONE);
                blink(false);
                if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                    setAudioMode(3);
                    tvPeer.setText(ctx.getString(R.string.callcfm));
                    base.setBackground(ctx.getDrawable(R.drawable.gradient_bg_green));
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    String callloginserted = objGlobalMethods.getGCMPrefs(gcmp, "callloginserted");
                    if ((JobInfoActivity.ongoingcall == null || !JobInfoActivity.ongoingcall) && !callloginserted.equals("t")) {
                        timeConnected = init_timeConnected();

                        log = objGlobalMethods.getGCMPrefs(gcmp, "CallLogFullMsg");
                        log = objGlobalMethods.composeCallLog(log, "Customer Answered/Into Voicemail.");
                        objGlobalMethods.writeToFile("Call connected at CallActivity.", "", "", "", "");
                    }

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences gp = objGlobalMethods.getGcmPreferences();
                            String callloginserted = objGlobalMethods.getGCMPrefs(gp, "callloginserted");
                            boolean within10s = JobsHandler.within10s(gp);
                            String callOfc = objGlobalMethods.getGCMPrefs(gp, "callOfc");
                            if (!callloginserted.equals("t") && !within10s && callOfc.equals("")) {
                                objGlobalMethods.setGCMPrefs(gp, "callloginserted", "t");
                                Gson gson = new Gson();
                                String CallLogInsertedTime = gson.toJson(new Date());
                                objGlobalMethods.setGCMPrefs(gp, "CallLogInsertedTime", CallLogInsertedTime);
                                objGlobalMethods.writeToFile("Insert call log at CallActivity updateCallState", "", "", "", "");
                                insertCallLog(true);
                            }
                        }
                    }, 500);

                } else if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                    setAudioMode(0);
                    if (myApp.currentActivity != null && myApp.currentActivity.getClass() == JobInfoActivity.class && JobInfoActivity.currentCall == null) {
                        objGlobalMethods.writeToFile("Call disconnected at CallActivity when currentActivity is JobInfoActivity and JobInfoActivity.currentCall == null.", "", "", "", "");
                        final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                act.shideshowSubmitBtns(objGlobalMethods.getDashboard2ActivityCategoryType(objGlobalMethods.getGcmPreferences()));
                            }
                        });
                    } else {
                        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(gcmp, "disconnectionProcessingAtCallActivity", "t");
                        buttonHangup.setText("OK");
                        call_state = "Call disconnected";
                        objGlobalMethods.writeToFile("Call disconnected at CallActivity", "", "", "", "");
                        base.setBackground(ctx.getDrawable(R.drawable.gradient_bg_red));
                        tvPeer.setText(ctx.getString(R.string.callended));
                        timeEnded = init_timeEnded();
                        if (callendedlogged != null && callendedlogged) {
                            //do nothing
                        } else {
                            callendedlogged = true;
                            log = objGlobalMethods.getGCMPrefs(gcmp, "CallLogFullMsg");
                            log = objGlobalMethods.composeCallLog(log, "Call ended. Call Duration: " + String.valueOf(returnCallDuration()) + " seconds.");

                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences gp = objGlobalMethods.getGcmPreferences();
                                    String callloginserted = objGlobalMethods.getGCMPrefs(gp, "callloginserted");
                                    boolean within10s = JobsHandler.within10s(gp);
                                    String callOfc = objGlobalMethods.getGCMPrefs(gp, "callOfc");
                                    if (!callloginserted.equals("t") && !within10s && callOfc.equals("")) {
                                        objGlobalMethods.setGCMPrefs(gp, "callloginserted", "t");
                                        Gson gson = new Gson();
                                        String CallLogInsertedTime = gson.toJson(new Date());
                                        objGlobalMethods.setGCMPrefs(gp, "CallLogInsertedTime", CallLogInsertedTime);
                                        objGlobalMethods.writeToFile("Insert call log at CallActivity updateCallState", "", "", "", "");
                                        insertCallLog(false);
                                    }
                                }
                            }, 500);
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startRtnToJobInfoActivity2();
                            }
                        }, 2000);
                    }
                }
            }

            tvState.setText(call_state);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity updateCallState. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static Long init_timeConnected() {
        return new Date().getTime();
    }

    public static Long init_timeEnded() {
        return new Date().getTime();
    }

    public static long returnCallDuration() {
        if (timeConnected != null && timeEnded != null) {
            return (timeEnded - timeConnected) / 1000;
        } else {
            return 0;
        }
    }

    public static void forceHangUpLeavePage() {
        try {
            setAudioMode(0);
            if (callHungUp != null && callHungUp) {
                //do nothing
            } else {
                timeEnded = CallActivity.init_timeEnded();
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                log = objGlobalMethods.getGCMPrefs(gcmp, "CallLogFullMsg");
                if (log == null) {
                    String Callext = objGlobalMethods.getGCMPrefs(gcmp, "Callext");
                    String Calltel = objGlobalMethods.getGCMPrefs(gcmp, "Calltel");
                    log = objGlobalMethods.composeCallLog(log, "Attempt to call " + Calltel + " using ext " + Callext + ".");
                }
                if (lastCallInfo != null && lastCallInfo.getState() == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                    log = objGlobalMethods.composeCallLog(log, "Call rejected from receiver end. Auto end call.");
                } else if (lastCallInfo != null && lastCallInfo.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                    boolean CallLogContainsConnectingText = objGlobalMethods.CallLogContainsConnectingText(log);
                    if (!CallLogContainsConnectingText) {
                        log = objGlobalMethods.composeCallLog(log, "Call went into Voicemail. Auto end call.");
                    }
                }

                if (callendedlogged != null && callendedlogged) {
                    //do nothing
                } else {
                    callendedlogged = true;
                    log = objGlobalMethods.composeCallLog(log, "Call ended. Call Duration: " + String.valueOf(CallActivity.returnCallDuration()) + " seconds.");
                    /*if (callloginserted == null || !callloginserted) {
                        insertCallLog();
                    }*/
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences gp = objGlobalMethods.getGcmPreferences();
                            String callloginserted = objGlobalMethods.getGCMPrefs(gp, "callloginserted");
                            boolean within10s = JobsHandler.within10s(gp);
                            String callOfc = objGlobalMethods.getGCMPrefs(gp, "callOfc");
                            if (!callloginserted.equals("t") && !within10s && callOfc.equals("")) {
                                objGlobalMethods.setGCMPrefs(gp, "callloginserted", "t");
                                Gson gson = new Gson();
                                String CallLogInsertedTime = gson.toJson(new Date());
                                objGlobalMethods.setGCMPrefs(gp, "CallLogInsertedTime", CallLogInsertedTime);
                                objGlobalMethods.writeToFile("Insert call log at CallActivity forceHangUpLeavePage.", "", "", "", "");
                                insertCallLog(false);
                            }
                        }
                    }, 500);

                    objGlobalMethods.writeToFile("CalLActivity forceHangUpleavePage started.", "", "", "", "");
                }
                startRtnToJobInfoActivity2();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity forceHangUpLeavePage. Exception: " + e, "", "", "", "");
        }
    }

    public void forceShowBtnHangUp(boolean connected) {
        try {
            if (connected) {
                LinearLayout base = (LinearLayout) findViewById(R.id.base);
                TextView tvPeer = (TextView) findViewById(R.id.textViewPeer);
                tvPeer.setText(myApp.getAppContext().getString(R.string.callcfm));
                base.setBackground(myApp.getAppContext().getDrawable(R.drawable.gradient_bg_green));
                blink(false);
            }
            ImageView ibtnHangup = (ImageView) findViewById(R.id.ibtnHangup);
            ibtnHangup.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at CallActivity forceShowBtnHangUp. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void showBtnHangUp0() {
        long currTimeMS = new Date().getTime();
        long prevTimeMS = 0;
        long leftTimeMS = 0;
        try {
            if (timeFirstInContactToThisPage != null && timeFirstInContactToThisPage > 0) {
                prevTimeMS = timeFirstInContactToThisPage;
                leftTimeMS = 15000 - (currTimeMS - prevTimeMS); //15s before hang up button appears
                cdTimer0 = new CountDownTimer(leftTimeMS, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        ImageView ibtnHangup = (ImageView) findViewById(R.id.ibtnHangup);
                        ibtnHangup.setVisibility(View.VISIBLE);
                        //cdTimer.cancel();
                    }
                }.start();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    private void showBtnHangUp() {
        long currTimeMS = new Date().getTime();
        long prevTimeMS = 0;
        long leftTimeMS = 0;
        try {
            if (timeConnectingStarted != null && timeConnectingStarted > 0) {
                prevTimeMS = timeConnectingStarted;
                leftTimeMS = 10000 - (currTimeMS - prevTimeMS); //10s before hang up button appears
                cdTimer = new CountDownTimer(leftTimeMS, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        ImageView ibtnHangup = (ImageView) findViewById(R.id.ibtnHangup);
                        ibtnHangup.setVisibility(View.VISIBLE);
                        //cdTimer.cancel();
                    }
                }.start();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    private void blink(boolean blink) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the time of the blink with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        TextView tvPeer = (TextView) findViewById(R.id.textViewPeer);
        try {
            if (blink) {
                tvPeer.startAnimation(anim);
            } else {
                tvPeer.clearAnimation(); // cancel blink animation
                tvPeer.setAlpha(1.0f); // restore original alpha
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at CalLActivity blink. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    //==============================VIDEO

    public void setupVideoPreview(SurfaceView surfacePreview,
                                  Button buttonShowPreview) {
        /*surfacePreview.setVisibility(previewHandler.videoPreviewActive ?
                View.VISIBLE : View.GONE);

        buttonShowPreview.setText(previewHandler.videoPreviewActive ?
                getString(R.string.hide_preview) :
                getString(R.string.show_preview));*/
    }

    public void showPreview(View view) {
        /*SurfaceView surfacePreview = (SurfaceView)
                findViewById(R.id.surfacePreviewCapture);

        Button buttonShowPreview = (Button)
                findViewById(R.id.buttonShowPreview);


        previewHandler.videoPreviewActive = !previewHandler.videoPreviewActive;

        setupVideoPreview(surfacePreview, buttonShowPreview);

        previewHandler.updateVideoPreview(surfacePreview.getHolder());*/
    }

    private void setupVideoSurface() {
        /*SurfaceView surfaceInVideo = (SurfaceView)
                findViewById(R.id.surfaceIncomingVideo);
        SurfaceView surfacePreview = (SurfaceView)
                findViewById(R.id.surfacePreviewCapture);
        Button buttonShowPreview = (Button)
                findViewById(R.id.buttonShowPreview);
        surfaceInVideo.setVisibility(View.VISIBLE);
        buttonShowPreview.setVisibility(View.VISIBLE);
        surfacePreview.setVisibility(View.GONE);*/
    }

    public static void setAudioMode(int val) {
        //MODE_IN_CALL - In call audio mode. A telephony call is established. - Constant Value: 2
        //MODE_IN_COMMUNICATION - In communication audio mode. An audio/video chat or VoIP call is established. - Constant Value: 3
        //MODE_NORMAL - Normal audio mode: not ringing and no call established. - Constant Value: 0

        try {
            if (audioManager == null) {
                Context ctx = myApp.getAppContext();
                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            }

            if (val == 2) {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            } else if (val == 3) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity setAudioMode. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void speakermode(View view) {
        try {
            if (audioManager == null) {
                Context ctx = myApp.getAppContext();
                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            }

            if (!audioManager.isSpeakerphoneOn()) {
                /*if (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
                    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                } else {
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                }
                //audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);*/
                initSpeakerOnAudioManager();
                ibtnSpeaker.setImageResource(R.mipmap.icons8_speakeroff);
                ibtnBT.setVisibility(View.GONE);
            } else {
                speakerOn = false;
                if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {

                } else {

                }
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(false);
                ibtnSpeaker.setImageResource(R.mipmap.icons8_speaker);
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                boolean bt_connected = objGlobalMethods.getGCMPrefs(gcmp, "bt_connected").equals("t") ? true : false;
                if (bt_connected) {
                    if (audioManager == null) {
                        Context ctx = myApp.getAppContext();
                        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                    }
                    audioManager.setBluetoothScoOn(true);
                    ibtnBT.setVisibility(View.VISIBLE);
                }
                //init_bluetooth_device_detection();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at CalLActivity speakermode. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void toggleSpeaker2() {
        try {
            if (audioManager == null) {
                Context ctx = myApp.getAppContext();
                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            }

            if (JobInfoActivity.ongoingcall == null || !JobInfoActivity.ongoingcall) {
                ibtnSpeaker.setImageResource(R.mipmap.icons8_speaker);
                audioManager.setSpeakerphoneOn(false);
            } else {
                if (audioManager.isSpeakerphoneOn()) {
                    initSpeakerOnAudioManager();
                }
            }
            ibtnSpeaker.setImageResource(!audioManager.isSpeakerphoneOn() ? R.mipmap.icons8_speaker : R.mipmap.icons8_speakeroff);
            ibtnBT.setVisibility(!audioManager.isSpeakerphoneOn() && audioManager.isBluetoothScoOn() ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at CallActivity toggleSpeaker. Exception: " + s, "", "", "", "");
        }
    }

    public void viewInfo(View v) {
        onBackPressed();
    }

    private void init_bluetooth_device_detection() {
        try {
            init_bluetooth_Device_Detector_BC();
            if (audioManager == null) {
                Context ctx = myApp.getAppContext();
                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            }
            initBluetoothAudioManager(audioManager.isSpeakerphoneOn());
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
            registerReceiver(bluetooth_Device_Detector_BC, intentFilter);
            btReceiverRegistered = true;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity init_bluetooth_device_detection. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void init_bluetooth_Device_Detector_BC() {
        try {
            bluetooth_Device_Detector_BC = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
                        int scoAudioState = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                        if (scoAudioState == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                            // This method should be called only after SCO is connected!
                            if (audioManager == null) {
                                Context ctx = myApp.getAppContext();
                                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                            }

                            audioManager.setBluetoothScoOn(true);
                            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                            objGlobalMethods.setGCMPrefs(gcmp, "bt_connected", "t");
                            ibtnBT.setVisibility(View.VISIBLE);
                            //initBluetoothAudioManager(audioManager.isSpeakerphoneOn());
                        }
                    }
                }
            };
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivty init_bluetooth_Device_Detector_BC. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void initBluetoothAudioManager(boolean isSpeakerOn) {
        try {
            if (audioManager == null) {
                Context ctx = myApp.getAppContext();
                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            }
            /*if (!audioManager.isSpeakerphoneOn()) {
                resetSco();
                audioManager.startBluetoothSco();
            } else if (audioManager.isSpeakerphoneOn()) {
                resetSco();
                initSpeakerOnAudioManager();
            }*/
            resetSco(isSpeakerOn);
            audioManager.startBluetoothSco();
            if (audioManager.isSpeakerphoneOn()) {
                initSpeakerOnAudioManager();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity initBluetoothAudioManager. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void initSpeakerOnAudioManager() {
        try {
            if (audioManager == null) {
                Context ctx = myApp.getAppContext();
                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            }
            if (audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            }
            //audioManager.setMode(AudioManager.MODE_IN_CALL);
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity initSpeakerOnAudioManager. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public static void resetSco(boolean isSpeakerOn) {
        try {
            if (audioManager == null) {
                Context ctx = myApp.getAppContext();
                audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            }
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.stopBluetoothSco();
            audioManager.setBluetoothScoOn(false);
            //audioManager.setSpeakerphoneOn(false);
            audioManager.setSpeakerphoneOn(isSpeakerOn);
            audioManager.setWiredHeadsetOn(false);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CallActivity resetSco. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at CallActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CallActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }



}
