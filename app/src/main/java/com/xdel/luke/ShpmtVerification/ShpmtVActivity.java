package com.xdel.luke.ShpmtVerification;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.xdel.luke.CODupdate;
import com.xdel.luke.CheckListActivity;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DOinvCheckActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobInfo;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.Locker.ShipmentActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.NoPasteEditText;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.jobinfo.CorrectJobSelectedRes;
import com.xdel.luke.jobinfo.JobInfoLite;
import com.xdel.luke.objClasses.checklist_PostItemAdapter;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.setDeliveredStatusActivity;
import com.xdel.luke.setFailStatusActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ShpmtVActivity extends AppCompatActivity {

    private TextView mTextView;
    private SharedPreferences g;
    Context context;
    ConnectionDetector cd;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    private String cameraScannedCode;
    public static ArrayList<String> verifyItems = null;
    private Button btnShpmtVScan, btnShpmtVValue, btnShpmtVNext;
    private TextView lblverifyInstruction, lblTotalNoOfPcs, lblShpmtVPcsValue, lblisAlps, lblCfmNoOfPcs;
    //EditText txtShpmtVValue, txtShpmtVPcsValue;
    EditText txtShpmtVPcsValue;
    NoPasteEditText txtShpmtVValue;
    private AlertDialog VerifyShipmentDialog = null;
    public static Intent nextActivtyIntent;
    public static String fromPage = "";
    private MediaPlayer player;
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);

    private void stopPlayingTone(){
        try {
            if (player != null) {
                player.reset();
                //player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity stopPlayingTone. Exception: " + e.getMessage(),"","","","");
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
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity playSuccessVerifiedTone. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            g = objGlobalMethods.getGcmPreferences();
            langType = objGlobalMethods.getGCMPrefs(g, "language");
            /*String languageToLoad = langType; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());*/

            //disable taking screen shot
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            //disable taking screen shot

            setContentView(R.layout.activity_shpmt_v);

            btnShpmtVScan = (Button) findViewById(R.id.btnShpmtVScan);
            btnShpmtVValue = (Button) findViewById(R.id.btnShpmtVValue);
            btnShpmtVNext = (Button) findViewById(R.id.btnShpmtVNext);
            lblverifyInstruction = (TextView) findViewById(R.id.lblverifyInstruction);
            lblTotalNoOfPcs = (TextView) findViewById(R.id.lblTotalNoOfPcs);
            //txtShpmtVValue = (EditText) findViewById(R.id.txtShpmtVValue);
            txtShpmtVValue = findViewById(R.id.txtShpmtVValue);
            txtShpmtVPcsValue = (EditText) findViewById(R.id.txtShpmtVPcsValue);
            lblShpmtVPcsValue = (TextView) findViewById(R.id.lblShpmtVPcsValue);
            lblCfmNoOfPcs = (TextView) findViewById(R.id.lblCfmNoOfPcs);
            lblisAlps = (TextView) findViewById(R.id.lblisAlps);
            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity onCreate. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                g = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(g)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                Context ctx = myApp.getAppContext();

                String JobInfoLiteJson = objGlobalMethods.getGCMPrefs(g, "JobInfoLite");
                String ShipmentVerified = objGlobalMethods.getGCMPrefs(g, "ShipmentVerified");
                String ShipmentVerifiedCold = objGlobalMethods.getGCMPrefs(g, "ShipmentVerifiedCold");
                JobInfoLite jil = !objGlobalMethods.IsEmptyString(JobInfoLiteJson) ? new Gson().fromJson(JobInfoLiteJson, JobInfoLite.class) : null;
                String instr = "";

                if (jil != null) {
                    boolean hasColdItem = jil.hasColdItem;
                    boolean isAlps = jil.isAlps;
                    int pcs = jil.pcs;
                    lblisAlps.setText(isAlps ? "t" : "");
                    ctrlSettings.enableDisableSubmitButtonWithStyles(
                            !(objGlobalMethods.IsEmptyString(ShipmentVerified) && objGlobalMethods.IsEmptyString(ShipmentVerifiedCold)), btnShpmtVNext
                    );

                    if (isAlps && hasColdItem && pcs >= 2 && (objGlobalMethods.IsEmptyString(ShipmentVerified) || objGlobalMethods.IsEmptyString(ShipmentVerifiedCold))) {
                        objGlobalMethods.writeToFile("Require Verify COLD and NON COLD Shipment for JobsIDX: " + jil.JobsIDX, "", "", "", "");
                        instr = ctx.getString(R.string.scan_pink_label_cnc);
                    } else if (isAlps && hasColdItem && pcs > 0 && pcs < 2 && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        objGlobalMethods.writeToFile("Require Verify COLD Shipment for JobsIDX: " + jil.JobsIDX, "", "", "", "");
                        instr = ctx.getString(R.string.scan_pink_label_c);
                    } else if (isAlps && objGlobalMethods.IsEmptyString(ShipmentVerified)) {
                        objGlobalMethods.writeToFile("Require Verify Shipment for JobsIDX: " + jil.JobsIDX, "", "", "", "");
                        instr = ctx.getString(R.string.scan_white_label);
                    } else {
                        instr = ctx.getString(R.string.verificationtxt);
                    }

                    lblverifyInstruction.setText(instr);
                    hideShowPcsLblTB(isAlps);
                    //lblTotalNoOfPcs.setText(isAlps ? "/ " + jil.pcs : "");
                    lblTotalNoOfPcs.setText(String.valueOf(jil.pcs));

                    cameraScannedCode = objGlobalMethods.getGCMPrefs(g, "cameraScannedCode").toUpperCase();
                    if (!objGlobalMethods.IsEmptyString(cameraScannedCode)) {
                        String val = cameraScannedCode;
                        cameraScannedCode = null;
                        objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");
                        verification(jil, val, 0);

                    /*String JobInfoScanFrom = "";
                    JobInfoScanFrom = objGlobalMethods.getGCMPrefs(g, "JobInfoScanFrom");
                    objGlobalMethods.setGCMPrefs(g, "JobInfoScanFrom", "");
                    if (!JobInfoScanFrom.equals("") && JobInfoScanFrom.equals("scanVerify")) {
                        objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");
                        verification(jil, val);
                    }*/
                    }
                } else {
                    objGlobalMethods.writeToFile("Unable to verify shipment. jil is null.","","","","");
                    showErrorAlert(ctx.getString(R.string.unable_to_v_shpmt));
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ShpmtVActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");

                showErrorAlert(myApp.getAppContext().getString(R.string.unable_to_v_shpmt));
            }
        }
    }

    private void hideShowPcsLblTB(boolean isAlps) {
        try {
            //lblTotalNoOfPcs.setVisibility(isAlps ? View.VISIBLE : View.GONE);
            txtShpmtVPcsValue.setVisibility(isAlps ? View.GONE : View.VISIBLE);
            lblShpmtVPcsValue.setVisibility(isAlps ? View.VISIBLE : View.GONE);
            lblCfmNoOfPcs.setVisibility(isAlps ? View.GONE : View.VISIBLE);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity hideShowPcsLblTB. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);


            if (VerifyShipmentDialog != null) {
                VerifyShipmentDialog.cancel();
                VerifyShipmentDialog = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        try {
            Intent intent = null;
            g = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold", "pcs"}, new String[]{"", "", ""});
            verifyItems = null;
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();

            if (fromPage == null) {
                intent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            } else if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobsListGroupActivity"))) {
                intent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("OtpActivity"))) {
                intent = new Intent(myApp.currentActivity, JobInfoActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("DOinvCheckActivity")) {
                intent = new Intent(myApp.currentActivity, DOinvCheckActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("ShipmentActivity")) {
                intent = new Intent(myApp.currentActivity, ShipmentActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CODupdate")) {
                intent = new Intent(myApp.currentActivity, CODupdate.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CheckListActivity")) {
                intent = new Intent(myApp.currentActivity, CheckListActivity.class);
            }

            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(intent);
            finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity back. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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
        String[] prefvalue = new String[]{"scanVerify", "ShpmtV", ""};
        objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        JobInfoActivity.accessFromDirection = 0;
        startActivity(parentIntent);
    }

    private void verification(JobInfoLite jil, String val, int mode) {
        try {
            Context ctx = myApp.getAppContext();
            String instr = "";
            if (jil != null) {
                boolean hasColdItem = jil.hasColdItem;
                boolean isAlps = jil.isAlps;
                int pcs = jil.pcs;

                boolean verified = JobsHandler2.isCorrectJobSelected(jil, val, false, mode);
                if (verifyItems == null)
                    verifyItems = new ArrayList<>();
                int jccount = JobsHandler2.jcCount(verifyItems);
                int jxcount = JobsHandler2.jxCount(verifyItems);
                int scannedpcs = verifyItems != null ? verifyItems.size() : 0;
                boolean isJC = JobsHandler2.isJC(val);
                boolean isJX = JobsHandler.isJobsIDXcode(val);
                if (verified) {
                    if (pcs == 1) {
                        if (isAlps && hasColdItem && !isJC && !isJX) {
                            txtShpmtVValue.setText("");
                            lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                            instr = ctx.getString(R.string.scan_pink_label);
                            lblverifyInstruction.setText(instr);

                            //considered not verified
                            objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                    "", instr, false).show();

                        } else if (isAlps && hasColdItem && isJC) {
                            txtShpmtVValue.setText("");
                            verifyItems.add(isJC ? "jc" : "jx");
                            objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                            btnShpmtVNext_Click(null);
                        } else if (isAlps && hasColdItem && !isJC) {
                            //scan cold pink
                            txtShpmtVValue.setText("");
                            lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                            instr = ctx.getString(R.string.scan_pink_label);
                            lblverifyInstruction.setText(instr);

                            //considered not verified
                            objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                    "", instr, false).show();

                        } else if (isAlps && !hasColdItem && isJC) {
                            //scan cold pink
                            //but job is marked as non-cold job
                            txtShpmtVValue.setText("");
                            lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                            instr = ctx.getString(R.string.scan_white_label);
                            lblverifyInstruction.setText(instr);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                            //considered not verified
                            objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                    "", instr, false).show();
                        } else if (isAlps && !hasColdItem) {
                            txtShpmtVValue.setText("");
                            verifyItems.add(isJC ? "jc" : isJX ? "jx" : val);
                            objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                            btnShpmtVNext_Click(null);
                        } else {
                            txtShpmtVValue.setText("");
                            verifyItems.add(val);
                            txtShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                            objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                            instr = ctx.getString(R.string.scan_enter_item_ref);
                            lblverifyInstruction.setText(instr);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnShpmtVNext);
                        }
                    } else {
                        if (isAlps) {
                            if (!hasColdItem && isJC) {
                                //scan cold pink
                                //but job is marked as non-cold job
                                txtShpmtVValue.setText("");
                                lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                instr = ctx.getString(R.string.scan_white_label);
                                lblverifyInstruction.setText(instr);
                                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                //considered not verified
                                objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                        "", instr, false).show();
                            } else if ((scannedpcs < pcs) && ((scannedpcs + 1) == pcs)) {
                                if (hasColdItem && jccount == 0 && !isJC) {
                                    //scan cold pink
                                    //non cold pink already scanned at least once
                                    txtShpmtVValue.setText("");
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(R.string.scan_pink_label_c);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered not verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            "", instr, false).show();

                                } else if (hasColdItem && jccount > 0 && isJC) {
                                    //scan non-cold pink
                                    //cold pink already scanned
                                    txtShpmtVValue.setText("");
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered not verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            "", instr, false).show();

                                } else {
                                    txtShpmtVValue.setText("");
                                    verifyItems.add(isJC ? "jc" : isJX ? "jx" : val);
                                    objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                                    btnShpmtVNext_Click(null);
                                }
                            } else {
                                if (jccount == 0 && jxcount == 0) {
                                    txtShpmtVValue.setText("");
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(
                                            hasColdItem && isJC ? R.string.scan_pink_label_nc :
                                                    ((hasColdItem && isJX) || (hasColdItem && !isJX && !isJC)) ? R.string.scan_pink_label_c :
                                                            R.string.scan_white_label);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            ctx.getString(R.string.otpbuttontext_verified), instr, true).show();

                                } else if (hasColdItem && jccount == 0 && !isJC) {
                                    txtShpmtVValue.setText("");
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(R.string.scan_pink_label_c);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            ctx.getString(R.string.otpbuttontext_verified), instr, true).show();

                                } else if (hasColdItem && jccount == 0 && isJC) {
                                    txtShpmtVValue.setText("");
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            ctx.getString(R.string.otpbuttontext_verified), instr, true).show();

                                } else if (hasColdItem && jccount > 0 && isJC) {
                                    txtShpmtVValue.setText("");
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered not verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            "", instr, false).show();

                                } else if (hasColdItem && jccount > 0 && isJX) {
                                    txtShpmtVValue.setText("");
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            ctx.getString(R.string.otpbuttontext_verified), instr, true).show();

                                } else if (hasColdItem && jccount > 0 && !isJX && !isJC) {
                                    txtShpmtVValue.setText("");
                                    verifyItems.add(isJC ? "jc" : val);
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(R.string.scan_pink_label_nc);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            ctx.getString(R.string.otpbuttontext_verified), instr, true).show();

                                } else if (!hasColdItem && jxcount > 0 && !isJC) {
                                    txtShpmtVValue.setText("");
                                    verifyItems.add(isJC ? "jc" : "jx");
                                    lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                                    instr = ctx.getString(R.string.scan_white_label);
                                    lblverifyInstruction.setText(instr);
                                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                                    //considered verified
                                    objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                            ctx.getString(R.string.otpbuttontext_verified), instr, true).show();

                                }
                            }
                        } else {
                            verifyItems.add(isJC ? "jc" : isJX ? "jx" : val);
                            txtShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                            txtShpmtVValue.setText("");
                            objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold"}, new String[]{"y", "y"});
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnShpmtVNext);

                            //considered verified
                            objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                    ctx.getString(R.string.otpbuttontext_verified), "", true).show();

                        }
                    }
                } else {
                    if (isAlps && hasColdItem) {
                        txtShpmtVValue.setText("");
                        lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                        //considered not verified
                        objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                ctx.getString(R.string.verificationtxtno), ctx.getString(R.string.scan_pink_label), false).show();

                    } else if (isAlps && !hasColdItem) {
                        txtShpmtVValue.setText("");
                        lblShpmtVPcsValue.setText(String.valueOf(verifyItems.size()));
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                        //considered not verified
                        objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                ctx.getString(R.string.verificationtxtno), ctx.getString(R.string.scan_white_label), false).show();


                    } else {
                        txtShpmtVValue.setText("");
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnShpmtVNext);

                        //considered not verified
                        objGlobalMethods.showCustomVerificationDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                ctx.getString(R.string.verificationtxtno), "", false).show();
                    }
                }
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity verification. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnShpmtVScan_Click(View v) {
        try {
            launchQRScan();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnShpmtVScan_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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
                    verification(jil, val, 1);
                } else {
                    objGlobalMethods.writeToFile("Unable to shipment verify at ShpmtVActivity btnShpmtVValue_Click due jil is null.","","","","");
                    showErrorAlert("");
                }
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.verificationtxtenterval)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnShpmtVValue_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnShpmtVCANCEL_Click(View v) {
        try {
            back();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnShpmtVCANCEL_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnShpmtVNext_Click(View v) {
        try {
            if (ShpmtVActivity.nextActivtyIntent == null)
                ShpmtVActivity.nextActivtyIntent = new Intent(myApp.currentActivity, setDeliveredStatusActivity.class);

            if (v == null) {
                int pcs = verifyItems == null ? 0 : verifyItems.size();
                g = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold", "pcs"}, new String[]{"", "", String.valueOf(pcs)});
                verifyItems = null;
                startActivity(ShpmtVActivity.nextActivtyIntent);
                finish();
            } else {
                boolean isAlpsJob = !objGlobalMethods.IsEmptyString(lblisAlps.getText().toString());
                String txtShpmtVPcsValueVal = txtShpmtVPcsValue.getText().toString();
                g = objGlobalMethods.getGcmPreferences();
                String ShipmentVerified = objGlobalMethods.getGCMPrefs(g, "ShipmentVerified");
                String ShipmentVerifiedCold = objGlobalMethods.getGCMPrefs(g, "ShipmentVerifiedCold");
                if (isAlpsJob && !objGlobalMethods.IsEmptyString(ShipmentVerified) && !objGlobalMethods.IsEmptyString(ShipmentVerifiedCold)) {
                    objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold", "pcs"}, new String[]{"", "", txtShpmtVPcsValueVal});
                    verifyItems = null;
                    startActivity(ShpmtVActivity.nextActivtyIntent);
                    finish();
                } else if ((!objGlobalMethods.IsEmptyString(ShipmentVerified) && !objGlobalMethods.IsEmptyString(ShipmentVerifiedCold)) &&
                        (objGlobalMethods.IsEmptyString(txtShpmtVPcsValueVal) || objGlobalMethods.IsEqualString(txtShpmtVPcsValueVal, "0"))){
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.cfm_no_pcs_txt)).show();
                }
                /*else if ((!objGlobalMethods.IsEmptyString(ShipmentVerified) && !objGlobalMethods.IsEmptyString(ShipmentVerifiedCold)) &&
                        (!objGlobalMethods.IsEqualString(txtShpmtVPcsValueVal, lblShpmtVPcsValue.getText().toString()))) {
                    showCfmPcsAlert();
                }*/
                else if ((!objGlobalMethods.IsEmptyString(ShipmentVerified) && !objGlobalMethods.IsEmptyString(ShipmentVerifiedCold)) ) {
                    objGlobalMethods.setGCMPrefsMass(g, new String[]{"ShipmentVerified", "ShipmentVerifiedCold", "pcs"}, new String[]{"", "", txtShpmtVPcsValueVal});
                    verifyItems = null;
                    startActivity(ShpmtVActivity.nextActivtyIntent);
                    finish();
                } else if ((objGlobalMethods.IsEmptyString(ShipmentVerified) && objGlobalMethods.IsEmptyString(ShipmentVerifiedCold))) {
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.verificationtxt)).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnShpmtVNext_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void showCfmPcsAlert() {
        Context ctx = myApp.getAppContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(ctx.getString(R.string.pcs_not_tally));

        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
                /*Intent i = new Intent(myApp.currentActivity, setDeliveredStatusActivity.class);
                startActivity(i);
                finish();*/
            }
        });

        /*builder.setNegativeButton(ctx.getString(R.string.buttontext_NO2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });*/

        AlertDialog alert = builder.create();
        alert.show();
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
                Intent i = new Intent(myApp.currentActivity, JobInfoActivity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}