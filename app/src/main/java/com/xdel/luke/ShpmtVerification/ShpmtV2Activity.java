package com.xdel.luke.ShpmtVerification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.CODupdate;
import com.xdel.luke.CheckListActivity;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DOCOD.CODupdate2;
import com.xdel.luke.DOinvCheckActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobInfo;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.JobsHandler3;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.Locker.ShipmentActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.NoPasteEditText;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.jobinfo.JobInfoLite;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.jobSelectionInfo;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.setDeliveredMassStatusActivity;
import com.xdel.luke.setDeliveredStatusActivity;

import java.util.ArrayList;

public class ShpmtV2Activity extends AppCompatActivity {

    private SharedPreferences g;
    private SharedPreferences glsp;
    private SharedPreferences svsp;
    ConnectionDetector cd;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    private String cameraScannedCode;
    ListView lv;
    NoPasteEditText txtShpmtVValue;
    private LinearLayout linearLayout1;
    private Button btnShpmtVScan, btnShpmtVValue, btnShpmtVNext;
    private TextView lblisAlps, lblverifyInstruction, lblRef, lblactual;
    public static ArrayList<ShpmtV2data> verifyItemsV2 = null;
    private String verifyItemsV2JsonStr = "";
    public ShpmtV2Adapter adapter;
    public String objCheckedItemsJsonStr;
    public objCheckedItems[] objCheckedItemsAL = null;
    //public static Intent nextActivtyIntent;
    public static String fromPage = "";
    private MediaPlayer player;
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);

    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                //player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playSuccessVerifiedTone() {
        try {
            stopPlayingTone();

            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, verifiedToneURI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            g = objGlobalMethods.getGcmPreferences();
            langType = objGlobalMethods.getGCMPrefs(g, "language");
            setContentView(R.layout.activity_shpmt_v2);

            lblisAlps = findViewById(R.id.lblisAlps);
            lblverifyInstruction = findViewById(R.id.lblverifyInstruction);
            lblRef = findViewById(R.id.lblRef);
            lblactual = findViewById(R.id.lblactual);
            btnShpmtVScan = (Button) findViewById(R.id.btnShpmtVScan);
            btnShpmtVValue = (Button) findViewById(R.id.btnShpmtVValue);
            btnShpmtVNext = (Button) findViewById(R.id.btnShpmtVNext);
            txtShpmtVValue = findViewById(R.id.txtShpmtVValue);
            lv = findViewById(R.id.lv);
            linearLayout1 = findViewById(R.id.linearLayout1);

            linearLayout1.setOnClickListener(null);
            linearLayout1.setOnClickListener(hidekeyboard_clickListner());

            lblverifyInstruction.setOnClickListener(null);
            lblverifyInstruction.setOnClickListener(hidekeyboard_clickListner());

            lblRef.setOnClickListener(null);
            lblRef.setOnClickListener(hidekeyboard_clickListner());

            lblactual.setOnClickListener(null);
            lblactual.setOnClickListener(hidekeyboard_clickListner());


            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity onCreate. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

    }

    public View.OnClickListener hidekeyboard_clickListner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    hidekeyboard();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at ShpmtV2Activity hidekeyboard_clickListner. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    public void hidekeyboard() {
        try {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity hidekeyboard. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                g = objGlobalMethods.getGcmPreferences();
                glsp = objGlobalMethods.getGrouplistSP();
                svsp = objGlobalMethods.getShpmtVSP();
                if (!objGlobalMethods.UserCanScreenShot(g)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;

                if (fromPage != null)
                    objGlobalMethods.setShpmtVSP_KeyValue(svsp, "fromPage", fromPage);

                loadlist();

                if (verifyItemsV2 != null) {
                    cameraScannedCode = objGlobalMethods.getGCMPrefs(g, "cameraScannedCode").toUpperCase();
                    if (!objGlobalMethods.IsEmptyString(cameraScannedCode)) {
                        String val = cameraScannedCode;
                        cameraScannedCode = null;
                        objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");
                        verification_(val, 0);
                    }
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShpmtV2Activity onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            savelist();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity onPause. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        try {
            Intent intent = null;
            svsp = objGlobalMethods.getShpmtVSP();
            objGlobalMethods.setShpmtVSPMass(svsp, new String[]{"verifyItemsV2JsonStr"}, new String[]{""});
            verifyItemsV2 = null;
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();

            if (fromPage == null)
                fromPage = objGlobalMethods.getShpmtVSP_KeyValue(svsp, "fromPage");

            if (fromPage == null) {
                intent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            } else if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobsListGroupActivity"))) {
                String[] prefName = new String[]{objGlobalVariables.checkedItemsName};
                String[] prefvalue = new String[]{""};
                objGlobalMethods.setGCMPrefsMass(objGlobalMethods.getGcmPreferences(), prefName, prefvalue);
                intent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("OtpActivity"))) {
                intent = new Intent(myApp.currentActivity, JobInfoActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("DOinvCheckActivity")) {
                intent = new Intent(myApp.currentActivity, DOinvCheckActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("ShipmentActivity")) {
                intent = new Intent(myApp.currentActivity, ShipmentActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CODupdate")) {
                intent = new Intent(myApp.currentActivity, CODupdate.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CODupdate2")) {
                intent = new Intent(myApp.currentActivity, CODupdate2.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CheckListActivity")) {
                intent = new Intent(myApp.currentActivity, CheckListActivity.class);
            }

            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(intent);
            finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity back. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean hasAutoFocus = false;
        PackageManager pm = getPackageManager();

        hasAutoFocus = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);

        if (hasAutoFocus) {
            int action = event.getAction();
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        //TODO
                        launchQRScan();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        //TODO
                        launchQRScan();
                    }
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void launchQRScan() {
        Intent parentIntent;
        g = objGlobalMethods.getGcmPreferences();

        String[] prefName = new String[]{"JobInfoScanFrom", "QRScanRequestIntent", "cameraScannedCode"};
        String[] prefvalue = new String[]{"scanVerify", "ShpmtV2", ""};
        objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        JobInfoActivity.accessFromDirection = 0;
        startActivity(parentIntent);
    }

    private void verification_(String val, int mode) {
        try {
            Context ctx = myApp.getAppContext();
            String instr = "";
            boolean hasColdItem;
            boolean isAlps = !objGlobalMethods.IsEmptyString(lblisAlps.getText().toString());
            ShpmtV2data d = null;
            JobInfoLite jil = null;
            int expectedc = 0;
            int actualc = 0;
            boolean found = false;
            boolean verified = false;
            boolean isJC;
            boolean isJX;
            int idx = -1;

            if (verifyItemsV2 != null && verifyItemsV2.size() > 0) {
                for (int i = 0; i <= verifyItemsV2.size() - 1; i++) {
                    idx = i;
                    d = verifyItemsV2.get(i);
                    found = JobsHandler3.JobFound(d.ji, val, false, mode);
                    if (found)
                        break;
                }
                if (found) {
                    jil = d.ji;
                    hasColdItem = jil.hasColdItem;
                    expectedc = d.expectedc;
                    actualc = d.actualc;
                    isJC = JobsHandler2.isJC(val);
                    isJX = JobsHandler.isJobsIDXcode(val);

                    if (expectedc == 1) {
                        if (isAlps && hasColdItem && !isJC && !isJX) {
                            txtShpmtVValue.setText("");
                            instr = ctx.getString(R.string.scan_pink_label);
                            //considered not verified
                        } else if (isAlps && hasColdItem && isJC) {
                            d.setJccount(1);
                            d.setActualc(1);
                            d.setBlink(true);
                            verifyItemsV2.set(idx, d);
                            verified = true;
                            instr = "";
                        } else if (isAlps && hasColdItem && !isJC) {
                            //scan cold pink
                            instr = ctx.getString(R.string.scan_pink_label);
                            //considered not verified
                        } else if (isAlps && !hasColdItem && isJC) {
                            //scan cold pink
                            //but job is marked as non-cold job
                            instr = ctx.getString(R.string.scan_white_label);
                            //considered not verified
                        } else if (isAlps && !hasColdItem) {
                            txtShpmtVValue.setText("");
                            d.setActualc(1);
                            d.setJxcount(1);
                            d.setBlink(true);
                            verifyItemsV2.set(idx, d);
                            verified = true;
                            instr = "";
                        } else {
                            txtShpmtVValue.setText("");
                            d.setActualc(1);
                            d.setBlink(true);
                            verifyItemsV2.set(idx, d);
                            verified = true;
                            instr = "";
                        }
                    } else {
                        if (isAlps) {
                            if (!hasColdItem && isJC) {
                                //scan cold pink
                                //but job is marked as non-cold job
                                instr = ctx.getString(R.string.scan_white_label);
                                //considered not verified
                            } else if ((actualc < expectedc) && ((actualc + 1) == expectedc)) {
                                if (hasColdItem && d.jccount == 0 && !isJC) {
                                    //scan cold pink
                                    //non cold pink already scanned at least once
                                    instr = ctx.getString(R.string.scan_pink_label_c);
                                    //considered not verified
                                } else if (hasColdItem && d.jccount > 0 && isJC) {
                                    //scan non-cold pink
                                    //cold pink already scanned
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    //considered not verified
                                } else {
                                    if (isJC)
                                        d.setJccount(1);
                                    if (isJX)
                                        d.setJxcount(1);
                                    if (!isJC && !isJX)
                                        d.setValcount(1);
                                    d.setActualc(1);
                                    d.setBlink(true);
                                    verifyItemsV2.set(idx, d);
                                    verified = true;
                                    //verifyItems.add(isJC ? "jc" : isJX ? "jx" : val);
                                    instr = "";
                                }
                            } else {
                                if (d.jccount == 0 && d.jxcount == 0) {
                                    if (isJC)
                                        d.setJccount(1);
                                    if (isJX)
                                        d.setJxcount(1);
                                    d.setActualc(1);
                                    d.setBlink(true);
                                    verifyItemsV2.set(idx, d);
                                    verified = true;
                                    instr = ctx.getString(
                                            hasColdItem && isJC ? R.string.scan_pink_label_nc :
                                                    ((hasColdItem && isJX) || (hasColdItem && !isJX && !isJC)) ? R.string.scan_pink_label_c :
                                                            R.string.scan_white_label);
                                    //considered verified
                                } else if (hasColdItem && d.jccount == 0 && !isJC) {
                                    txtShpmtVValue.setText("");
                                    if (isJC)
                                        d.setJccount(1);
                                    if (isJX)
                                        d.setJxcount(1);
                                    d.setActualc(1);
                                    d.setBlink(true);
                                    verifyItemsV2.set(idx, d);
                                    verified = true;
                                    instr = ctx.getString(R.string.scan_pink_label_c);
                                    //considered verified
                                } else if (hasColdItem && d.jccount == 0 && isJC) {
                                    txtShpmtVValue.setText("");
                                    if (isJC)
                                        d.setJccount(1);
                                    if (isJX)
                                        d.setJxcount(1);
                                    d.setActualc(1);
                                    d.setBlink(true);
                                    verifyItemsV2.set(idx, d);
                                    verified = true;
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    //considered verified
                                } else if (hasColdItem && d.jccount > 0 && isJC) {
                                    txtShpmtVValue.setText("");
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    //considered not verified
                                } else if (hasColdItem && d.jccount > 0 && isJX) {
                                    txtShpmtVValue.setText("");
                                    if (isJC)
                                        d.setJccount(1);
                                    if (isJX)
                                        d.setJxcount(1);
                                    d.setActualc(1);
                                    d.setBlink(true);
                                    verifyItemsV2.set(idx, d);
                                    verified = true;
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    //considered verified
                                } else if (hasColdItem && d.jccount > 0 && !isJX && !isJC) {
                                    txtShpmtVValue.setText("");
                                    d.setValcount(1);
                                    d.setActualc(1);
                                    d.setBlink(true);
                                    verifyItemsV2.set(idx, d);
                                    //verifyItems.add(isJC ? "jc" : val);
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    //considered verified
                                } else if (!hasColdItem && d.jxcount > 0 && !isJC) {
                                    txtShpmtVValue.setText("");
                                    if (isJC)
                                        d.setJccount(1);
                                    if (isJX)
                                        d.setJxcount(1);
                                    d.setActualc(1);
                                    d.setBlink(true);
                                    verifyItemsV2.set(idx, d);
                                    verified = true;
                                    instr = ctx.getString(R.string.scan_white_label);
                                    //considered verified
                                }
                            }
                        } else {
                            txtShpmtVValue.setText("");
                            if (isJC)
                                d.setJccount(1);
                            if (isJX)
                                d.setJxcount(1);
                            if (!isJC && !isJX)
                                d.setValcount(1);
                            d.setActualc(1);
                            d.setBlink(true);
                            verifyItemsV2.set(idx, d);
                            verified = true;
                            instr = "";
                            //verifyItems.add(isJC ? "jc" : isJX ? "jx" : val);
                            //considered verified
                        }
                    }
                } else {
                    instr = ctx.getString(R.string.verificationtxtno);
                }

                txtShpmtVValue.setText("");

                if (verified) {
                    adapter.notifyDataSetChanged();
                    ctrlSettings.enableDisableSubmitButtonWithStyles(canProceed(), btnShpmtVNext);
                } else if (!objGlobalMethods.IsEmptyString(instr))
                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                            instr, "", false).show();
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity verification_. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private ArrayList<ShpmtV2data> getList() {
        Gson gson = new Gson();
        ShpmtV2data d = null;
        objJobInfo ji = null;
        JobInfoLite jil = null;
        boolean hasColdItem;
        try {
            if (verifyItemsV2 == null) {
                verifyItemsV2JsonStr = objGlobalMethods.getShpmtVSP_KeyValue(svsp, "verifyItemsV2JsonStr");
                if (!objGlobalMethods.IsEmptyString(verifyItemsV2JsonStr)) {
                    verifyItemsV2 = gson.fromJson(verifyItemsV2JsonStr, new TypeToken<ArrayList<ShpmtV2data>>() {
                    }.getType());
                } else {
                    String jobSelectionInfoJsonStr = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "jobSelectionInfoJsonStr");
                    jobSelectionInfo j1 = !objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr) ? gson.fromJson(jobSelectionInfoJsonStr, jobSelectionInfo.class) : null;
                    if (j1 == null) {
                        objGlobalMethods.writeToFile("Unable to verify shipment at ShpmtV2Activity getList. j1 is null.", "", "", "", "");
                        return null;
                    }

                    lblisAlps.setText(j1.IS_ALPS ? "t" : "");

                    objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(g, objGlobalVariables.checkedItemsName);
                    if (objGlobalMethods.IsEmptyString(objCheckedItemsJsonStr)) {
                        objGlobalMethods.writeToFile("Unable to verify shipment at ShpmtV2Activity getList. objCheckedItemsJsonStr is empty.", "", "", "", "");
                        return null;
                    }

                    objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                    if (objCheckedItemsAL == null || (objCheckedItemsAL != null && objCheckedItemsAL.length == 0)) {
                        objGlobalMethods.writeToFile("Unable to verify shipment at ShpmtV2Activity getList. objCheckedItemsAL is empty.", "", "", "", "");
                        return null;
                    }

                    for (int i = 0; i <= objCheckedItemsAL.length - 1; i++) {
                        ji = objCheckedItemsAL[i].jobinfo;
                        hasColdItem = JobsHandler2.hasColdItem(ji);
                        jil = new JobInfoLite(
                                ji.JobsIDX, ji.JobNo, ji.DOList, ji.BarcodeTags, ji.BarcodeTagsExt, ji.ConsignmentNote, hasColdItem,
                                j1.IS_ALPS, j1.IS_LOCKER, false, ji.Pieces
                        );
                        d = new ShpmtV2data(jil, 0);
                        if (verifyItemsV2 == null)
                            verifyItemsV2 = new ArrayList<>();
                        verifyItemsV2.add(d);
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity getList. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return verifyItemsV2;
        }
    }

    private void loadlist() {
        Context ctx = myApp.getAppContext();
        try {
            verifyItemsV2 = getList();
            if (verifyItemsV2 != null) {
                adapter = new ShpmtV2Adapter(myApp.getAppContext(), verifyItemsV2);
                lv.setAdapter(adapter);
                ctrlSettings.enableDisableSubmitButtonWithStyles(canProceed(), btnShpmtVNext);
            } else {
                objGlobalMethods.writeToFile("Unable to verify shipment at ShpmtV2Activity loadlist. verifyItems is empty.", "", "", "", "");
                showErrorAlert(ctx.getString(R.string.unable_to_v_shpmt));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity loadlist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void savelist() {
        Gson gson = new Gson();
        try {
            svsp = objGlobalMethods.getShpmtVSP();
            verifyItemsV2JsonStr = (verifyItemsV2 != null && verifyItemsV2.size() > 0) ? gson.toJson(verifyItemsV2) : "";
            objGlobalMethods.setShpmtVSP_KeyValue(svsp, "verifyItemsV2JsonStr", verifyItemsV2JsonStr);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity savelist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private boolean canProceed() {
        ShpmtV2data d = null;
        int c = 0;
        int vc = 0;
        try {
            if (verifyItemsV2 != null && verifyItemsV2.size() > 0) {
                c = verifyItemsV2.size();
                for (int i = 0; i <= verifyItemsV2.size() - 1; i++) {
                    if (verifyItemsV2.get(i).verified)
                        vc++;
                }
                return (vc == c);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity verification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
        return false;
    }

    public void canProceedCheck() {
        try {
            if (btnShpmtVNext == null)
                btnShpmtVNext = findViewById(R.id.btnShpmtVNext);
            ctrlSettings.enableDisableSubmitButtonWithStyles(canProceed(), btnShpmtVNext);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity canProceedCheck. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

    }

    public void btnShpmtVScan_Click(View v) {
        try {
            launchQRScan();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity btnShpmtVScan_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnShpmtVValue_Click(View v) {
        Context ctx = myApp.getAppContext();
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                }
            }, 500);

            String val = txtShpmtVValue.getText().toString();
            if (!objGlobalMethods.IsEmptyString(val)) {
                g = objGlobalMethods.getGcmPreferences();
                String JobInfoLiteJson = objGlobalMethods.getGCMPrefs(g, "JobInfoLite");
                JobInfoLite jil = !objGlobalMethods.IsEmptyString(JobInfoLiteJson) ? new Gson().fromJson(JobInfoLiteJson, JobInfoLite.class) : null;
                if (jil != null) {
                    verification_(val, 0);
                } else {
                    objGlobalMethods.writeToFile("Unable to shipment verify at ShpmtVActivity btnShpmtVValue_Click due jil is null.", "", "", "", "");
                    showErrorAlert("");
                }
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.verificationtxtenterval)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnShpmtVValue_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnShpmtVCANCEL_Click(View v) {
        try {
            back();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity btnShpmtVCANCEL_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnShpmtVNext_Click(View v) {
        try {
            if (canProceed()) {
                processNext pn = new processNext(verifyItemsV2);
                pn.execute();
            } else {
                Context ctx = myApp.getAppContext();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.cfm_no_pcs_txt)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Activity btnShpmtVNext_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class processNext extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        private final ArrayList<ShpmtV2data> shpmtV2datas;

        public processNext(ArrayList<ShpmtV2data> list) {
            this.pd = new ProgressDialog(myApp.currentActivity);
            this.pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.pd.setCanceledOnTouchOutside(false);
            this.pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
            this.shpmtV2datas = list;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            String objCheckedItemsJsonStr;
            objCheckedItems[] aobjCheckedItemsAL = null;
            objCheckedItems obj = null;
            g = objGlobalMethods.getGcmPreferences();
            Gson gson = new Gson();
            JobInfoLite ji = null;
            ShpmtV2data d = null;
            try {
                objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(g, objGlobalVariables.checkedItemsName);
                if (!objGlobalMethods.IsEmptyString(objCheckedItemsJsonStr))
                    aobjCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);

                if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0 && this.shpmtV2datas != null && this.shpmtV2datas.size() > 0) {
                    for (int i = 0; i <= this.shpmtV2datas.size() - 1; i++) {
                        d = this.shpmtV2datas.get(i);
                        ji = d.ji;
                        for (int m = 0; m <= aobjCheckedItemsAL.length -1; m++) {
                            obj = aobjCheckedItemsAL[m];
                            if (obj.jobinfo != null && obj.jobinfo.JobsIDX == ji.JobsIDX) {
                                aobjCheckedItemsAL[m].setPcs(d.actualc);
                                break;
                            }
                        }
                    }

                    objCheckedItemsJsonStr = gson.toJson(aobjCheckedItemsAL);
                    objGlobalMethods.setGCMPrefs(g, objGlobalVariables.checkedItemsName, objCheckedItemsJsonStr);
                    asd = true;
                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShpmtV2Activity processNext doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                if (pd != null) {
                    pd.dismiss();
                }
                if (success) {
                    Intent nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                    startActivity(nextActivtyIntent);
                    finish();
                } else {
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.unableprocess)).show();
                }
            } catch (Exception e) {
                if (pd != null) {
                    pd.dismiss();
                }
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShpmtV2Activity processNext onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

    }

    private void showErrorAlert(String errMsg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(objGlobalMethods.IsEmptyString(errMsg) ? myApp.getAppContext().getString(R.string.alert_errorperformoperation) : errMsg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                g = objGlobalMethods.getGcmPreferences();
                Intent i = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}