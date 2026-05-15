package com.xdel.luke.JobInventoryVerify;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objStatusCodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobInventoryVerifyActivity extends AppCompatActivity {

    ConnectionDetector cd;
    private ProgressDialog pd, pdp;
    private SharedPreferences g, jiv;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    private String cameraScannedCode;
    private LinearLayout LLJIVList;
    private TextView lblJIVJobsIDX, lblJIVJobNo, lblJIVJobStatus, lblJIVErrMsgSub;
    private EditText txtJIVRef;
    private ListView lv;
    private Button btnJIVScan, btnJIVValue, btnJIVNext;
    private MediaPlayer player;
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);

    String jobsidx, jobno, jobstatus, JobInventoryDetailJson;
    List<String> verifiedRef;
    JobInventoryVAdapter ad;
    public static ArrayList<InventoryEntry> ScannedAItems = null;

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
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, errToneURI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            g = objGlobalMethods.getGcmPreferences();
            langType = objGlobalMethods.getGCMPrefs(g, "language");
            setContentView(R.layout.activity_job_inventory_verify);

            lblJIVJobsIDX = (TextView) findViewById(R.id.lblJIVJobsIDX);
            lblJIVJobNo = (TextView) findViewById(R.id.lblJIVJobNo);
            lblJIVJobStatus = (TextView) findViewById(R.id.lblJIVJobStatus);
            lblJIVErrMsgSub = (TextView) findViewById(R.id.lblJIVErrMsgSub);
            btnJIVScan = (Button) findViewById(R.id.btnJIVScan);
            btnJIVValue = (Button) findViewById(R.id.btnJIVValue);
            btnJIVNext = (Button) findViewById(R.id.btnJIVNext);
            txtJIVRef = (EditText) findViewById(R.id.txtJIVRef);
            lv = (ListView) findViewById(R.id.lv);
            LLJIVList = (LinearLayout) findViewById(R.id.LLJIVList);

            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

            pdp = new ProgressDialog(this);
            pdp.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pdp.setCanceledOnTouchOutside(false);
            pdp.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity onCreate. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (!objGlobalMethods.UserCanScreenShot(g)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                Context ctx = myApp.getAppContext();
                setTitle(ctx.getString(R.string.item_jobinventoryverification));

                cd = new ConnectionDetector(null);
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ctx, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }
                lblJIVErrMsgSub.setText("");
                /*loaddetail();
                focustb();*/

                g = objGlobalMethods.getGcmPreferences();
                jiv = objGlobalMethods.getJobInventoryVSP();
                cameraScannedCode = objGlobalMethods.getGCMPrefs(g, "cameraScannedCode");

                JobInventoryDetail obj = null;
                JobInventoryDetailJson = objGlobalMethods.getJobInventoryVSP_KeyValue(jiv, "JobInventoryDetailJson");
                if (!objGlobalMethods.IsEmptyString(JobInventoryDetailJson)) {
                    Gson gson = new Gson();
                    obj = gson.fromJson(JobInventoryDetailJson, JobInventoryDetail.class);
                }
                if (obj != null) {
                    jobsidx = String.valueOf(obj.JobsIDX);
                    jobno = obj.JobNo;
                    jobstatus = obj.Status;
                    lblJIVJobsIDX.setText(jobsidx);
                    lblJIVJobNo.setText(jobno);
                    lblJIVJobStatus.setText(jobstatus);
                    //loadlist(obj.ies);
                } else {
                    //loadlist(null);
                    ScannedAItems = null;
                    if (ad != null) {
                        ad.clear();
                        ad = null;
                    }
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJIVNext);
                }
                focustb();

                if (!cameraScannedCode.isEmpty() && !cameraScannedCode.equals("") && cameraScannedCode != "") {
                    objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");
                    inputManualEntry2(cameraScannedCode.toUpperCase());
                } else if (ScannedAItems != null && ScannedAItems.size() > 0 && ad == null) {
                    ad = new JobInventoryVAdapter(this, ScannedAItems);
                    lv.setAdapter(ad);
                }

                txtJIVRef.setOnEditorActionListener(null);
                txtJIVRef.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                            return false;
                        if (actionId == 0) {
                            //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                            inputManualEntry2(txtJIVRef.getText().toString().toUpperCase());
                            if (getCurrentFocus() != null) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                        } else if (actionId == EditorInfo.IME_ACTION_GO) {
                            inputManualEntry2(txtJIVRef.getText().toString().toUpperCase());
                            if (getCurrentFocus() != null) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                        } else if (actionId == 6) {
                            return false;
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_inventory_verfiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuReset) {
            g = objGlobalMethods.getGcmPreferences();
            jiv = objGlobalMethods.getJobInventoryVSP();

            String[] prefName = new String[]{"JobInfoScanFrom", "QRScanRequestIntent", "cameraScannedCode"};
            String[] prefvalue = new String[]{"", "", ""};
            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

            objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", "");

            lblJIVErrMsgSub.setText("");
            lblJIVJobsIDX.setText("");
            lblJIVJobNo.setText("");
            lblJIVJobStatus.setText("");
            if (ad != null) {
                ad.clear();
                ad.notifyDataSetChanged();
            }
            ad = null;
            if (ScannedAItems != null)
                ScannedAItems.clear();
            ScannedAItems = null;
            focustb();
        }


        /*switch (item.getItemId()) {
            case R.id.menuReset:
                g = objGlobalMethods.getGcmPreferences();
                jiv = objGlobalMethods.getJobInventoryVSP();

                String[] prefName = new String[]{"JobInfoScanFrom", "QRScanRequestIntent", "cameraScannedCode"};
                String[] prefvalue = new String[]{"", "", ""};
                objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

                objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", "");

                lblJIVErrMsgSub.setText("");
                lblJIVJobsIDX.setText("");
                lblJIVJobNo.setText("");
                lblJIVJobStatus.setText("");
                if (ad != null) {
                    ad.clear();
                    ad.notifyDataSetChanged();
                }
                ad = null;
                if (ScannedAItems != null)
                    ScannedAItems.clear();
                ScannedAItems = null;
                focustb();
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        try {
            Intent intent = new Intent(myApp.currentActivity, DrawerMainActivity.class);
            g = objGlobalMethods.getGcmPreferences();
            jiv = objGlobalMethods.getJobInventoryVSP();

            String[] prefName = new String[]{"JobInfoScanFrom", "QRScanRequestIntent", "cameraScannedCode"};
            String[] prefvalue = new String[]{"", "", ""};
            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

            objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", "");
            ScannedAItems = null;
            if (ad != null)
                ad.clear();
            ad = null;
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(intent);
            finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity back. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
        jobsidx = lblJIVJobsIDX.getText().toString();

        String[] prefName = new String[]{"JobInfoScanFrom", "QRScanRequestIntent", "cameraScannedCode"};
        String[] prefvalue = new String[]{objGlobalMethods.IsEmptyString(jobsidx) ? "jobref" : "ref", "JobInventoryV", ""};
        objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);

        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        JobInfoActivity.accessFromDirection = 0;
        startActivity(parentIntent);
    }

    private void loaddetail() {
        if (jiv == null)
            jiv = objGlobalMethods.getJobInventoryVSP();
        JobInventoryDetail obj = null;
        try {
            JobInventoryDetailJson = objGlobalMethods.getJobInventoryVSP_KeyValue(jiv, "JobInventoryDetailJson");
            if (!objGlobalMethods.IsEmptyString(JobInventoryDetailJson)) {
                Gson gson = new Gson();
                obj = gson.fromJson(JobInventoryDetailJson, JobInventoryDetail.class);
            }

            if (obj != null) {
                jobsidx = String.valueOf(obj.JobsIDX);
                jobno = obj.JobNo;
                jobstatus = obj.Status;
                lblJIVJobsIDX.setText(jobsidx);
                lblJIVJobNo.setText(jobno);
                lblJIVJobStatus.setText(jobstatus);
                //loadlist(obj.ies);
                loadlist2();
            } else {
                //loadlist(null);
                ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJIVNext);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity loaddetail. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loadlist2() {
        try {
            if (ScannedAItems != null && ScannedAItems.size() > 0) {
                if (ad == null)
                    ad = new JobInventoryVAdapter(this, ScannedAItems);
                lv.setAdapter(ad);
                lblJIVErrMsgSub.setText("");
            } else {
                lblJIVErrMsgSub.setText(myApp.getAppContext().getString(R.string.inventorydatanotfound));
            }
            ctrlSettings.enableDisableSubmitButtonWithStyles(canSubmit(), btnJIVNext);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity loadlist2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loadlist(ArrayList<InventoryEntry> d) {
        try {
            if ((ScannedAItems == null || (ScannedAItems != null && ScannedAItems.size() == 0)) && d != null && d.size() > 0)
                ScannedAItems = d;

            if (ScannedAItems != null && ScannedAItems.size() > 0) {
                if (ad == null) {
                    ad = new JobInventoryVAdapter(this, ScannedAItems);
                    lv.setAdapter(ad);
                    lblJIVErrMsgSub.setText("");
                }/* else {
                    ad.clear();
                    ad.addAll(ScannedAItems);
                }*/
            } else {
                lblJIVErrMsgSub.setText(myApp.getAppContext().getString(R.string.inventorydatanotfound));
            }

            /*if (ScannedAItems != null && ScannedAItems.size() > 0 && ad == null) {
                ad = new JobInventoryVAdapter(this, ScannedAItems);
                lv.setAdapter(ad);
                lblJIVErrMsgSub.setText("");
            } else if (ScannedAItems == null || (ScannedAItems != null && ScannedAItems.size() == 0)) {
                lblJIVErrMsgSub.setText(myApp.getAppContext().getString(R.string.inventorydatanotfound));
            }*/

            ctrlSettings.enableDisableSubmitButtonWithStyles(canSubmit(), btnJIVNext);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity loadlist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void focustb() {
        String idx = "";
        Context ctx = myApp.getAppContext();
        try {
            idx = lblJIVJobsIDX.getText().toString();
            txtJIVRef.setText("");
            txtJIVRef.setHint(ctx.getString(!objGlobalMethods.IsEmptyString(idx) ? R.string.stockref : R.string.lbljobref));
            txtJIVRef.requestFocus();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity focustb. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void focustb2() {
        String idx = "";
        Context ctx = myApp.getAppContext();
        try {
            idx = "";
            lblJIVJobsIDX.setText("");
            txtJIVRef.setText("");
            txtJIVRef.setHint(ctx.getString(!objGlobalMethods.IsEmptyString(idx) ? R.string.stockref : R.string.lbljobref));
            txtJIVRef.requestFocus();
            ScannedAItems = null;
            ad.clear();
            ad.notifyDataSetChanged();
            ad = null;
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnJIVNext);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity focustb. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class JobInventoryDetail {
        public long JobsIDX;
        public String JobNo;
        public String Status;
        public ArrayList<InventoryEntry> ies;

        public JobInventoryDetail(long aJobsIDX, String aJobNo, String aStatus, ArrayList<InventoryEntry> aies) {
            this.JobsIDX = aJobsIDX;
            this.JobNo = aJobNo;
            this.Status = aStatus;
            this.ies = aies;
        }
    }

    private class validateobj {
        public String ValidateJobInventoryResult;
        public String JobInfoStatus;

        public validateobj(String aValidateJobInventoryResult, String aJobInfoStatus) {
            this.ValidateJobInventoryResult = aValidateJobInventoryResult;
            this.JobInfoStatus = aJobInfoStatus;
        }

        public void setValidateJobInventoryResult(String validateJobInventoryResult) {
            ValidateJobInventoryResult = validateJobInventoryResult;
        }

        public void setJobInfoStatus(String jobInfoStatus) {
            JobInfoStatus = jobInfoStatus;
        }
    }

    private String getStoredStatusCodesEnZH(String type) {
        /*final SharedPreferences prefs = objGlobalMethods.getGcmPreferences();
        String fullStatusCodesJsonStr = "";
        if (type.equals("en") || type.equals("")) {
            fullStatusCodesJsonStr = prefs.getString(objGlobalVariables.storedStatusCodesPrefNameEN, "");
        } else if (type.equals("zh")) {
            fullStatusCodesJsonStr = prefs.getString(objGlobalVariables.storedStatusCodesPrefNameZH, "");
        }*/

        if (g == null)
            g = objGlobalMethods.getGcmPreferences();
        String fullStatusCodesJsonStr = "";
        if (type.equals("en") || type.equals("")) {
            fullStatusCodesJsonStr = g.getString(objGlobalVariables.storedStatusCodesPrefNameEN, "");
        } else if (type.equals("zh")) {
            fullStatusCodesJsonStr = g.getString(objGlobalVariables.storedStatusCodesPrefNameZH, "");
        }

        return fullStatusCodesJsonStr;
    }

    public class getJobInfo extends AsyncTask<Void, Void, JobInventoryDetail> {
        private final String JobsIDXstr;

        getJobInfo(String aJobsIDXstr) {
            this.JobsIDXstr = aJobsIDXstr;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected JobInventoryDetail doInBackground(Void... params) {
            JobInventoryDetail obj = null;
            ArrayList<InventoryEntry> d = null;
            objJobInfo ji = null;
            String StatusCodesFullJsonStr = "";
            objStatusCodes[] fullStatusCodesAL = null;
            long statusidx = -1;
            String status = "No Status";
            boolean isJx;
            String cJobsIDXstr = JobsIDXstr;
            try {
                isJx = JobsHandler.isJobsIDXcode(cJobsIDXstr);
                if (isJx)
                    cJobsIDXstr = cJobsIDXstr.substring(2, cJobsIDXstr.length());

                ji = WebService.GetJob(Long.parseLong(cJobsIDXstr));
                if (ji != null && ji.InventoryItems != null && ji.InventoryItems.length > 0) {
                    statusidx = ji.StatusCodeIDX;
                    d = new ArrayList<>(Arrays.asList(ji.InventoryItems));
                    if (d != null && d.size() > 0) {
                        Gson gson = new Gson();
                        if (jiv == null)
                            jiv = objGlobalMethods.getJobInventoryVSP();

                        if (statusidx == 49)
                            status = "Pick Up";


                        obj = new JobInventoryDetail(ji.JobsIDX, ji.JobNo, status, d);
                        String JobInventoryDetailJson = gson.toJson(obj);
                        ScannedAItems = d;
                        objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", JobInventoryDetailJson);
                    }
                } else if (ji != null && (ji.InventoryItems == null || (ji.InventoryItems != null && ji.InventoryItems.length == 0))) {
                    obj = new JobInventoryDetail(0, ji.JobNo, status, new ArrayList<InventoryEntry>());
                    objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", "");
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity getJobInfo doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return obj;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final JobInventoryDetail res) {
            Context ctx = myApp.getAppContext();
            try {
                if (pd != null)
                    pd.dismiss();

                if (res != null && res.JobsIDX > 0) {
                    playSuccessVerifiedTone();
                    lblJIVErrMsgSub.setText("");
                    loaddetail();
                    focustb();
                } else if (res != null && res.JobsIDX == 0) {
                    playTone();
                    lblJIVErrMsgSub.setText(ctx.getString(R.string.inventorydatanotfound));
                    lblJIVJobNo.setText("");
                    lblJIVJobStatus.setText("");
                    focustb();
                } else {
                    playTone();
                    lblJIVErrMsgSub.setText(ctx.getString(R.string.alert_nodatafound));
                    focustb();
                }
            } catch (Exception e) {
                playTone();
                lblJIVErrMsgSub.setText(ctx.getString(R.string.unableprocess));
                if (pd != null)
                    pd.dismiss();
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity getJobInfo onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public class ValidateJobInventory_2 extends AsyncTask<Void, Void, validateobj> {
        private final String input;
        public ValidateJobInventory_2(String ainput) {
            this.input = ainput;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pdp.show();
        }

        @Override
        protected validateobj doInBackground(Void... params) {
            validateobj obj = new validateobj("", "");
            String ValidateJobInventoryResult = "";
            objStatusCodes[] fullStatusCodesAL = null;
            long AUserIDX = 0;
            long AJobsIDX = 0;
            long JobStatus = 0;
            String status = "No Status";
            String StatusCodesFullJsonStr = "";
            String ARemarks = "";
            try {
                jobsidx = lblJIVJobsIDX.getText().toString();
                if (!objGlobalMethods.IsEmptyString(jobsidx))
                    AJobsIDX = Long.parseLong(jobsidx);

                String useridxStr = objGlobalMethods.getGCMPrefs(objGlobalMethods.getGcmPreferences(), "IDX");
                if (!objGlobalMethods.IsEmptyString(useridxStr))
                    AUserIDX = Long.parseLong(useridxStr);

                ARemarks = input;

                if (AUserIDX > 0 && AJobsIDX > 0) {
                    ValidateJobInventoryResult = WebService.ValidateJobInventory(AUserIDX,AJobsIDX, ARemarks);
                    if (ValidateJobInventoryResult.equalsIgnoreCase("ok")) {
                        JobStatus = WebService.CheckJobStatus(AJobsIDX);
                        if (JobStatus > 0) {
                            if (JobStatus == 49)
                                status = "Pick Up";

                            obj.setValidateJobInventoryResult(ValidateJobInventoryResult);
                            obj.setJobInfoStatus(status);
                            if (jiv == null)
                                jiv = objGlobalMethods.getJobInventoryVSP();
                            objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", "");
                        } else {
                            obj.setValidateJobInventoryResult(ValidateJobInventoryResult);
                        }

                        objJobInfo ji = null;
                        ArrayList<InventoryEntry> d = null;
                        ji = WebService.GetJob(AJobsIDX);
                        if (ji != null && ji.InventoryItems != null && ji.InventoryItems.length > 0) {
                            d = new ArrayList<>(Arrays.asList(ji.InventoryItems));
                            if (d != null && d.size() > 0) {
                                Gson gson = new Gson();
                                if (jiv == null)
                                    jiv = objGlobalMethods.getJobInventoryVSP();

                                JobInventoryDetail jid = null;
                                jid = new JobInventoryDetail(ji.JobsIDX, ji.JobNo, status, d);
                                String JobInventoryDetailJson = gson.toJson(jid);
                                objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", JobInventoryDetailJson);

                                if (ad == null && lv != null && lv.getAdapter() != null)
                                    ad = (JobInventoryVAdapter)lv.getAdapter();

                                if (ad != null && ScannedAItems != null) {
                                    InventoryEntry ie = null;
                                    InventoryEntry ie2 = null;
                                    InventoryEntry ie3 = null;
                                    int idx = -1;
                                    for (int i = 0; i <= d.size() -1; i++) {
                                        ie = d.get(i);
                                        for (int s = 0; s <= ScannedAItems.size() -1; s++) {
                                            ie2 = ScannedAItems.get(s);
                                            if (ie.IDX == ie2.IDX) {
                                                ScannedAItems.get(s).setVerified(ie.Verified);
                                                ScannedAItems.get(s).setFlag(ie.Flag);
                                                break;
                                            }
                                        }

                                        for (int a = 0; a <= ad.getCount() -1; a++) {
                                            ie3 = ad.getItem(a);
                                            if (ie.IDX == ie3.IDX) {
                                                ie3.setVerified(ie.Verified);
                                                ie3.setFlag(ie.Flag);
                                                break;
                                            }
                                        }
                                    }

                                    /*for (int i = 0; i <= d.size() -1; i++) {
                                        ie2 = null;
                                        ie3 = null;
                                        idx = -1;
                                        ie = d.get(i);
                                        for (int v = 0; v <= ScannedAItems.size() -1; v++) {
                                            ie2 = ScannedAItems.get(v);
                                            if (ie.IDX == ie2.IDX) {
                                                idx = i;
                                                break;
                                            }
                                        }
                                        if (i >= 0) {
                                            ScannedAItems.get(idx).setVerified(ie.Verified);
                                            ScannedAItems.get(idx).setFlag(ie.Flag);

                                            if (ad != null) {
                                                ie3 = ad.getItem(idx);
                                                if (ie3 != null) {
                                                    ie3.setVerified(ie.Verified);
                                                    ie3.setFlag(ie.Flag);
                                                }
                                            }
                                        }

                                    }*/

                                }
                            }
                        }
                    } else {
                        obj.setValidateJobInventoryResult(ValidateJobInventoryResult);
                    }
                }
            } catch (Exception e) {
                obj = null;
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity ValidateJobInventory_2 doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return obj;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final validateobj res) {
            Context ctx = myApp.getAppContext();
            try {
                if (pdp != null)
                    pdp.dismiss();

                if (res != null && res.ValidateJobInventoryResult.equalsIgnoreCase("ok")) {
                    playSuccessVerifiedTone();
                    lblJIVErrMsgSub.setText("");
                    lblJIVJobStatus.setText(res.JobInfoStatus);

                    if (ad == null && lv != null && lv.getAdapter() != null)
                        ad = (JobInventoryVAdapter)lv.getAdapter();

                    if (ad != null)
                        ad.notifyDataSetChanged();

                    if (res.JobInfoStatus.equalsIgnoreCase("Pick Up")) {
                        focustb2();
                    } else {
                        focustb();
                    }
                    //focustb2();
                } else if (res != null && !res.ValidateJobInventoryResult.equalsIgnoreCase("ok")) {
                    playTone();
                    lblJIVErrMsgSub.setText(ctx.getString(R.string.unableprocess) + "\n" + "Result: " + res.ValidateJobInventoryResult);
                    focustb();
                } else {
                    playTone();
                    lblJIVErrMsgSub.setText(ctx.getString(R.string.unableprocess));
                    focustb();
                }
            } catch (Exception e) {
                playTone();
                lblJIVErrMsgSub.setText(ctx.getString(R.string.unableprocess));
                focustb();
                if (pdp != null)
                    pdp.dismiss();
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity ValidateJobInventory_ onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    public class ValidateJobInventory_ extends AsyncTask<Void, Void, validateobj> {
         public ValidateJobInventory_() {

         }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pdp.show();
        }

        @Override
        protected validateobj doInBackground(Void... params) {
            validateobj obj = new validateobj("", "");
             String ValidateJobInventoryResult = "";
            objStatusCodes[] fullStatusCodesAL = null;
            long AUserIDX = 0;
            long AJobsIDX = 0;
            long JobStatus = 0;
            String status = "No Status";
            String StatusCodesFullJsonStr = "";
            String ARemarks = "";
            try {
                jobsidx = lblJIVJobsIDX.getText().toString();
                if (!objGlobalMethods.IsEmptyString(jobsidx))
                    AJobsIDX = Long.parseLong(jobsidx);

                String useridxStr = objGlobalMethods.getGCMPrefs(objGlobalMethods.getGcmPreferences(), "IDX");
                if (!objGlobalMethods.IsEmptyString(useridxStr))
                    AUserIDX = Long.parseLong(useridxStr);

                ARemarks = getRemarks();

                if (AUserIDX > 0 && AJobsIDX > 0) {
                    ValidateJobInventoryResult = WebService.ValidateJobInventory(AUserIDX,AJobsIDX, ARemarks);
                    if (ValidateJobInventoryResult.equalsIgnoreCase("ok")) {
                        JobStatus = WebService.CheckJobStatus(AJobsIDX);
                        if (JobStatus > 0) {
                            /*StatusCodesFullJsonStr = (langType.equals("en") || langType.equals("")) ? getStoredStatusCodesEnZH("en") : getStoredStatusCodesEnZH("zh");
                            Gson gson = new Gson();
                            if (!objGlobalMethods.IsEmptyString(StatusCodesFullJsonStr))
                                fullStatusCodesAL = gson.fromJson(StatusCodesFullJsonStr, objStatusCodes[].class);

                            if (fullStatusCodesAL != null) {
                                for (int i = 0; 9 <= fullStatusCodesAL.length - 1; i++) {
                                    if (JobStatus == fullStatusCodesAL[i].IDX)
                                        status = fullStatusCodesAL[i].StatusCode;
                                }
                            }*/
                            if (JobStatus == 49)
                                status = "Pick Up";

                            obj.setValidateJobInventoryResult(ValidateJobInventoryResult);
                            obj.setJobInfoStatus(status);
                            if (jiv == null)
                                jiv = objGlobalMethods.getJobInventoryVSP();
                            objGlobalMethods.setJobInventoryVSP(jiv, "JobInventoryDetailJson", "");
                        }
                    } else {
                        obj.setValidateJobInventoryResult(ValidateJobInventoryResult);
                    }
                }
            } catch (Exception e) {
                obj = null;
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity ValidateJobInventory_ doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return obj;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final validateobj res) {
            Context ctx = myApp.getAppContext();
            try {
                if (pdp != null)
                    pdp.dismiss();

                if (res != null && res.ValidateJobInventoryResult.equalsIgnoreCase("ok")) {
                    playSuccessVerifiedTone();
                    lblJIVErrMsgSub.setText("");
                    lblJIVJobStatus.setText(res.JobInfoStatus);
                    focustb2();
                } else if (res != null && !res.ValidateJobInventoryResult.equalsIgnoreCase("ok")) {
                    playTone();
                    lblJIVErrMsgSub.setText(ctx.getString(R.string.unableprocess) + "\n" + "Result: " + res.ValidateJobInventoryResult);
                } else {
                    playTone();
                    lblJIVErrMsgSub.setText(ctx.getString(R.string.unableprocess));
                }
            } catch (Exception e) {
                playTone();
                lblJIVErrMsgSub.setText(ctx.getString(R.string.unableprocess));
                if (pdp != null)
                    pdp.dismiss();
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity ValidateJobInventory_ onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    private void inputManualEntry2(String input) {
        try {
            jobsidx = lblJIVJobsIDX.getText().toString();
            if (objGlobalMethods.IsEmptyString(jobsidx)) {
                getJobInfo gji = new getJobInfo(input);
                gji.execute();
            } else {
                //verifyInventoryItem(input);
                ValidateJobInventory_2 vji2 = new ValidateJobInventory_2(input);
                vji2.execute();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity inputManualEntry2. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void verifyInventoryItem(String input) {
        ArrayList<InventoryEntry> d = null;
        Context ctx = myApp.getAppContext();
        ArrayList<String> IsDupScanALval = null;
        InventoryEntry ie = null;
        int idx = -1;
        boolean dup;
        try {
            if (ScannedAItems != null && ScannedAItems.size() > 0) {
                for (int i = 0; i <= ScannedAItems.size() -1; i++) {
                    ie = ScannedAItems.get(i);
                    if (ie.Reference.equalsIgnoreCase(input)) {
                        idx = i;
                        break;
                    }
                }
                if (idx >= 0) {
                    final int idxx = idx;
                    ScannedAItems.get(idxx).setVerified(true);
                    ScannedAItems.get(idxx).setBlink(true);
                    if (ad != null) {
                        ie = ad.getItem(idxx);
                        if (ie != null) {
                            ie.setBlink(true);
                            ie.setVerified(true);
                            ad.notifyDataSetChanged();
                            playSuccessVerifiedTone();
                        }
                    }
                } else {
                    playTone();
                    lblJIVErrMsgSub.setText(input + "\n" + ctx.getString(R.string.doesnotbelongtojob));
                }
            } else {
                playTone();
                objGlobalMethods.writeToFile("Unable to verify " + input + ". List data not found.", "", "", "", "");
                lblJIVErrMsgSub.setText(ctx.getString(R.string.unabletoverify2) + "\n" + input + "\n" + ctx.getString(R.string.inventorydatanotfound));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity verifyInventoryItem. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            focustb();
            ctrlSettings.enableDisableSubmitButtonWithStyles(canSubmit(), btnJIVNext);
        }
    }

    public boolean canSubmit(){
        boolean asd = false;
        ArrayList<InventoryEntry> d = null;
        InventoryEntry ie = null;
        int c = 0;
        try {
            if (ScannedAItems != null && ScannedAItems. size() > 0) {
                for (int i = 0; i <= ScannedAItems.size() -1; i++) {
                    ie = ScannedAItems.get(i);
                    if (ie.Verified)
                        c++;
                }
                asd = (c == ScannedAItems.size());
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity canSubmit. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public String getRemarks() {
        String r = "";
        ArrayList<InventoryEntry> d = null;
        InventoryEntry ie = null;
        try {
            if (ScannedAItems != null && ScannedAItems. size() > 0) {
                for (int i = 0; i <= ScannedAItems.size() -1; i++) {
                    ie = ScannedAItems.get(i);
                    r += (ie.Verified && objGlobalMethods.IsEmptyString(r)) ? ie.Reference : ", " + ie.Reference;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVerifyActivity getRemarks. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return r;
        }
    }

    public void btnJIVScan_Click(View v) {
        try {
            launchQRScan();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnJIVScan_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnJIVValue_Click(View v) {
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

            jobsidx = lblJIVJobsIDX.getText().toString();
            String val = txtJIVRef.getText().toString();
            if (!objGlobalMethods.IsEmptyString(val) && !objGlobalMethods.IsEmptyString(jobsidx)) {
                inputManualEntry2(val);
            } else if (!objGlobalMethods.IsEmptyString(val)) {
                getJobInfo gji = new getJobInfo(val);
                gji.execute();
            } else {
                focustb();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_entertagvalue)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnJIVValue_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnJIVCANCEL_Click(View v) {
        try {
            back();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnJIVCANCEL_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnJIVNext_Click(View v) {
        try {
            ValidateJobInventory_ vji = new ValidateJobInventory_();
            vji.execute();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtVActivity btnShpmtVNext_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

}