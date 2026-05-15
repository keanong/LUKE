package com.xdel.luke.PieceByPiecePU;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.BottomSheet.ScannListDialog;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objListItemPosChecked;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjJobs;
import com.xdel.luke.templogger.temploggerclass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StartQueryActivity extends AppCompatActivity {

    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    private Uri verifiedToneURI2 = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.hit);
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri errTone2URI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.dingdong);

    private SharedPreferences g, UpdateJobInfoPreferences;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    LinearLayout LLMPDMainShown;
    ExpandableListView expListView;
    TextView lblIsDefaultVRPpage, lblListHeader, lblListHeaderCount;
    ImageView img_i_have_arrive;
    EditText txtBarcodeMANUALENTRY;
    Button btnBarcodeSubmitENTER, btnPU;
    private ProgressDialog pd, pdp;
    static String cameraScannedCode;
    private ArrayList<String> headerAl;
    private List<String> listDataHeader;
    private ArrayList<SQHeaderJobsPcsCount> headerjcpcAl;
    private List<SQHeaderJobsPcsCount> listDataHeaderJcPc;
    private ArrayList itemAL;
    private List<SQItem> subGroupItemSqliteAL;
    private StartQueryAdapter expandableSortedSQLiteListAdapter;
    private HashMap<String, List<SQItem>> listDataChildSortedSQLite2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_query);

        LLMPDMainShown = findViewById(R.id.LLMPDMainShown);
        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        expListView = findViewById(R.id.expListView);
        txtBarcodeMANUALENTRY = findViewById(R.id.txtBarcodeMANUALENTRY);
        btnBarcodeSubmitENTER = findViewById(R.id.btnBarcodeSubmitENTER);
        btnPU = findViewById(R.id.btnPU);

        Context c = myApp.getAppContext();

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        //pd.setMessage("Please wait. Retrieving Info...");
        pd.setMessage(c.getString(R.string.pd_waitRetriveInfo));


        pdp = new ProgressDialog(this);
        pdp.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdp.setCanceledOnTouchOutside(false);
        //pd.setMessage("Please wait. Retrieving Info...");
        pdp.setMessage(c.getString(R.string.pd_waitprocessing));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        g = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        if (!objGlobalMethods.UserCanScreenShot(g)) {
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        }
        myApp.currentActivity = this;
        context = myApp.getAppContext();
        int appFlag = myApp.getDeviceSettingsFlag();
        if (myApp.isAirplaneModeOnFlag(appFlag)) {
            showChangeAirplaneModeSettingsDialog();
            return;
        }
        if (myApp.isGpsOnFlag(appFlag)) {
            showChangeGPSModeSettingsDialog();
            return;
        }

        if (myApp.isAllPermissionGrantedFlag(appFlag)) {
            showChangePermissionDialog();
            return;
        }

        if (myApp.GPSisHighAccFlag(appFlag)) {
            showChangeGPStoHighAccModeSettingsDialog();
            return;
        }

        if (!temploggerclass.isBTEnabled()) {
            objGlobalMethods.writeToFile("JobsheetActivity !BTEnabled", "", "", "", "");
            temploggerclass.promptToOnBT();
            return;
        }

        lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

        DashboardSubActivity.frompage = null;

        //cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(null);

        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
        }

        objGlobalMethods.setGCMPrefs(g, "CannotTakePhoto", "");

        //initPage();
        loadlist ll = new loadlist();
        ll.execute();

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sq, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.refresh) {
            try {
                loadlist ll = new loadlist();
                ll.execute();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at StartQueryActivity onOptionsItemSelected. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        } else if (item.getItemId() == R.id.list) {
            showScanned();
        }

        /*switch (item.getItemId()) {
            case R.id.refresh:
                try {
                    loadlist ll = new loadlist();
                    ll.execute();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at StartQueryActivity onOptionsItemSelected. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
                }
                break;
            case R.id.list:
                showScanned();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at StartQueryActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at StartQueryActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAirplaneMode);
                    } catch (ActivityNotFoundException e) {
                        objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }
                } else {
                    Intent intent1 = new Intent("android.settings.WIRELESS_SETTINGS");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPSModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmodehighacc));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangePermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpspermission));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    String stack = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at JobsheetActivity showChangePermissionDialog. ActivityNotFoundException: "
                            + e.toString() + "\nStack: " + stack, "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
        g = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        objGlobalMethods.setGCMPrefs(g, "QRScanRequestIntent", "bpu2");
        objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");
        //parentIntent = new Intent(Dashboard2Activity.this, QRReaderActivity.class);
        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*String scanned = "";
        SharedPreferences m = objGlobalMethods.getMedsColSP();
        scanned = objGlobalMethods.getMedsColPrefs(m, "scanned");
        if (!objGlobalMethods.IsEmptyString(scanned)) {


        } else {
            back();
        }*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.backlosedata));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", update job status.
                dialog.dismiss();
                back();
            }
        });

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void back() {
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    public void btnBarcodeSubmitENTER_Click(View v) {
        try {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            String t = txtBarcodeMANUALENTRY.getText().toString();
            if (objGlobalMethods.IsEmptyString(t)) {
                playTone();
                Context c = myApp.getAppContext();
                objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.pls_enter_job_ref)).show();
            } else {
                cameraScannedCode = "";
                selectJobSQLite();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StartQueryActivity btnBarcodeSubmitENTER_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnCX_Click(View v) {
        /*String scanned = "";
        SharedPreferences m = objGlobalMethods.getMedsColSP();
        scanned = objGlobalMethods.getMedsColPrefs(m, "scanned");
        if (!objGlobalMethods.IsEmptyString(scanned)) {

        } else {
            back();
        }*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.backlosedata));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", update job status.
                dialog.dismiss();
                back();
            }
        });

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void btnPU_Click(View v) {
        boolean canproceed = false;
        Long[] JobsIDXsArr = null;
        String JobsIDXstr;
        final Context c = myApp.getAppContext();
        try {
            //final List<Long> JobsIDXList = DAO_Jobs.getPIPPCS_to_Update();
            final List<Long> PIPPCS_selectedJobsIDXList = DAO_Jobs.getPIPPCS_selected();
            final List<Long> ToBeTalliedJobsIDXList = DAO_Jobs.getPIPPCS_to_Update();
            //final int pcs = PIPPCS_selectedJobsIDXList != null && PIPPCS_selectedJobsIDXList.size() > 0 ? PIPPCS_selectedJobsIDXList.size() : 0;
            final int pcs = DAO_Jobs.getPCS_SCANNEDNOW();
            final int jc = ToBeTalliedJobsIDXList != null && ToBeTalliedJobsIDXList.size() > 0 ? ToBeTalliedJobsIDXList.size() : 0;
            canproceed = PIPPCS_selectedJobsIDXList != null && PIPPCS_selectedJobsIDXList.size() > 0;

            if (canproceed) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                //builder.setMessage(c.getString(R.string.cfmupdateprogress) + "\n\n" + c.getString(R.string.pcscollected) + " " + pcs);
                builder.setMessage(c.getString(R.string.cfmupdateprogress) + "\n\n" + c.getString(R.string.jobstallied) + " " + jc + "\n" + c.getString(R.string.pcscollected) + " " + pcs);

                builder.setPositiveButton(c.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status.
                        dialog.dismiss();
                        String jobsidxs = "";
                        jobsidxs = Arrays.toString(PIPPCS_selectedJobsIDXList.toArray());
                        objGlobalMethods.writeToFile("btnPU_Click clicked to mass update selected Med JobsIDX: " + jobsidxs,
                                "", "", "", "");

                        //List<Long> JobsIDXLists = DAO_Jobs.getPIPPCS_to_Update();
                        if (ToBeTalliedJobsIDXList != null && ToBeTalliedJobsIDXList.size() > 0) {
                            jobsidxs = Arrays.toString(ToBeTalliedJobsIDXList.toArray());
                            objGlobalMethods.writeToFile("Med JobsIDX to PU: " + jobsidxs, "", "", "", "");
                            submit sm = new submit(ToBeTalliedJobsIDXList, jobsidxs, pcs);
                            sm.execute();
                        } else {
                            final String jidxs = jobsidxs;
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(myApp.currentActivity);
                            builder2.setCancelable(false);
                            //builder2.setMessage(c.getString(R.string.processcompleted) + "\n\n" + c.getString(R.string.pcscollected) + " " + pcs);
                            builder2.setMessage(c.getString(R.string.processcompleted));

                            builder2.setPositiveButton(c.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    objGlobalMethods.writeToFile("Pieces collected updated in sqlite. Selected Med JobsIDX: " + jidxs,
                                            "", "", "", "");
                                    SharedPreferences m = objGlobalMethods.getMedsColSP();
                                    String[] medpn = new String[]{"mChildCheckStates2PrefStr", "SQGroupScrollSavedIndex", "SQGroupScrollSavedPosition", "scanned"};
                                    String[] medpv = new String[]{"", "", "", ""};
                                    objGlobalMethods.setMedsColPrefsMass(m, medpn, medpv);
                                    DAO_Jobs.updatePIPPCS_ZeroScannedNow();
                                    //btnPU.setText(c.getString(R.string.buttontext_PU) );
                                    loadlist ll = new loadlist();
                                    ll.execute();
                                }
                            });
                            AlertDialog alert2 = builder2.create();
                            alert2.show();
                        }
                    }
                });

                builder.setNegativeButton(c.getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.noitemselected)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StartQueryActivity btnPU_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class submit extends AsyncTask<Void, Void, Boolean> {
        private final List<Long> JobsIDXList;
        private final String jobsidxs;
        private final int pcs;
        private int jc = 0;

        public submit(List<Long> aJobsIDXList, String ajobsidxs, int apcs) {
            this.JobsIDXList = aJobsIDXList;
            this.jobsidxs = ajobsidxs;
            this.pcs = apcs;
            this.jc = aJobsIDXList.size();
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdp.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            SharedPreferences gcmPrefs = objGlobalMethods.getGcmPreferences();
            SharedPreferences m = objGlobalMethods.getMedsColSP();
            Date dt = new Date();
            Long l = dt.getTime();
            final String timestampstr = "/Date(" + l + ")/";
            Long[] JobsIDXsArr = null;
            try {
                boolean manual = !objGlobalMethods.IsEmptyString(objGlobalMethods.getMedsColPrefs(m, "manual"));
                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                String RegID = objGlobalMethods.getRegistrationIdOnly();

                for (int i = JobsIDXList.size() - 1; i >= 0; i--) {
                    ////set from pip to pu
                    ////remove from pip pref
                    DAO_Jobs.updateJobsCatTypeID(1, JobsIDXList.get(i));
                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 0);
                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(gcmPrefs, 1);

                    if (i == 0) {
                        DAO_Jobs.updatePIPPCS_ZeroScannedNow();
                        objGlobalMethods.writeToFile("Mass update Med job status.", "JobsIDX: " +
                                jobsidxs, "Submitted StatusCodeIDX: 49", "", "");
                        JobsIDXsArr = JobsIDXList.toArray(new Long[JobsIDXList.size()]);
                        JobsHandler.addUpdateJobInfoStatusWithGPSMassQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDXsArr, 49,
                                timestampstr, manual ? "Manually selected, not by scanning." : "", "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(),
                                Accuracy, "");

                        objGlobalMethods.writeToFile("StartQueryActivity submit cleanPIPPCS", "", "", "", "");
                        DAO_Jobs.cleanPIPPCS();
                        Context ctx = myApp.getAppContext();
                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                        objGlobalMethods.writeToFile("StartQueryActivity submit ended without error.", "", "", "", "");

                        objGlobalMethods.setLastUpdatedJobLatLng(gcmPrefs, true);
                        asd = true;
                    }
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at StartQueryActivity onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Context c = myApp.getAppContext();
            try {
                if (pdp != null && pdp.isShowing()) {
                    pdp.dismiss();
                }
                if (success) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(myApp.currentActivity);
                    builder2.setCancelable(false);
                    //builder2.setMessage(c.getString(R.string.processcompleted) + "\n\n" + c.getString(R.string.jobstallied) + " " + jc + "\n" + c.getString(R.string.pcscollected) + " " + pcs);
                    builder2.setMessage(c.getString(R.string.processcompleted));

                    builder2.setPositiveButton(c.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SharedPreferences m = objGlobalMethods.getMedsColSP();
                            String[] medpn = new String[]{"mChildCheckStates2PrefStr", "SQGroupScrollSavedIndex", "SQGroupScrollSavedPosition", "scanned"};
                            String[] medpv = new String[]{"", "", "", ""};
                            objGlobalMethods.setMedsColPrefsMass(m, medpn, medpv);
                            //btnPU.setText(c.getString(R.string.buttontext_PU) );
                            loadlist ll = new loadlist();
                            ll.execute();
                        }
                    });
                    AlertDialog alert2 = builder2.create();
                    alert2.show();

                } else {
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_errorperformoperation)).show();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at StartQueryActivity onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    private class loadlist extends AsyncTask<Void, Void,List<dbMedsJobs>> {
        private final String ContactIDX = "3650876";
        private final String ClientIDX = "28657";

        public loadlist(){}

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pdp.show();
        }

        @Override
        protected List<dbMedsJobs> doInBackground(Void... params) {
            List<dbMedsJobs> dbObjJobsAL = null;
            try {
                DAO_Jobs.cleanPIPPCS();
                ArrayList<PipPcs> pippcslist = DAO_Jobs.getJobsIDXNotInPIPPCS(ContactIDX, ClientIDX);
                if (pippcslist != null && pippcslist.size() > 0) {
                    objGlobalMethods.writeToFile("StartQueryActivity bulkInsertPIPPCS pippcslist size " + pippcslist.size(), "", "", "", "");
                    DAO_Jobs.bulkInsertPIPPCS(pippcslist);
                }
                dbObjJobsAL = DAO_Jobs.getAllMedsColdbObjJobsList();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exeption at StartQueryActivity loadlist onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                return dbObjJobsAL;
            }
        }

        @Override
        protected void onPostExecute(List<dbMedsJobs> dbObjJobsAL) {
            try {
                if (pdp != null && pdp.isShowing())
                    pdp.dismiss();
                initPage(dbObjJobsAL);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exeption at StartQueryActivity loadlist onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                if (pdp != null && pdp.isShowing())
                    pdp.dismiss();
            }
        }
    }

    private void showErrorAlert(String errMsg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                back();
                /*boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent i = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();*/
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initPage(List<dbMedsJobs> dbObjJobsAL) {
        try {
            //List<dbMedsJobs> dbObjJobsAL = DAO_Jobs.getAllMedsColdbObjJobsList();
            if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                loadListSQLite(dbObjJobsAL);

                cameraScannedCode = objGlobalMethods.getGCMPrefs(g, "cameraScannedCode");

                if (!cameraScannedCode.equals("")) {
                    selectJobSQLite();
                    objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");
                } else {
                    // restore index and position
                    int index = 0;
                    int pos = 0;
                    try {
                        index = Integer.parseInt(objGlobalMethods.getGCMPrefs(g, "SQGroupScrollSavedIndex"));
                    } catch (Exception e) {
                        index = 0;
                    }

                    try {
                        pos = Integer.parseInt(objGlobalMethods.getGCMPrefs(g, "SQGroupScrollSavedPosition"));
                    } catch (Exception e) {
                        pos = 0;
                    }

                    if (index == 0 && pos == 0) {

                    } else {
                        final Handler handler = new Handler();
                        final int finalpos = pos;
                        final int finalindex = index;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                    /*final int skippedCount = getSkippedCount(expandableSortedSQLiteListAdapter._listDataChild.entrySet().iterator(), finalindex, expandableSortedSQLiteListAdapter._listDataHeader);
                                    expListView.setSelection(skippedCount + finalpos);*/
                                expListView.setSelection(finalpos);
                                // restore index and position
                            }
                        }, 100);
                    }
                }

                txtBarcodeMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        cameraScannedCode = null;
                        //if (catType == 0 || catType == 1 || catType == 2) {
                        if (!txtBarcodeMANUALENTRY.getText().toString().equals("")) {
                            if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                                return false;
                            if (actionId == 0) {
                                // input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                                selectJobSQLite();
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }

                            } else if (actionId == EditorInfo.IME_ACTION_GO) {
                                selectJobSQLite();
                                if (getCurrentFocus() != null) {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                                }
                            }

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    expListView.clearFocus();
                                    txtBarcodeMANUALENTRY.clearFocus();
                                    txtBarcodeMANUALENTRY.setText("");
                                    txtBarcodeMANUALENTRY.requestFocus();
                                }
                            }, 100);
                        }

                        return true;
                    }
                });
            } else {
                //showErrorAlert(myApp.getAppContext().getString(R.string.alert_nojobfound));
                setEmptyList();
                Context c = myApp.getAppContext();
                //objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.nomedjoborallpu)).show();
                showErrorAlert(c.getString(R.string.nomedjoborallpu));
            }


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
    }

    private void loadListSQLite(List<dbMedsJobs> dbObjJobsAL) {
        g = objGlobalMethods.getGcmPreferences();
        SharedPreferences m = objGlobalMethods.getMedsColSP();

        try {
            Boolean isPartOne = false;
            objJobInfo ji;
            String JobInfoJsonStr = "";
            headerAl = new ArrayList<String>();
            headerjcpcAl = new ArrayList<>();
            itemAL = new ArrayList();
            listDataHeader = new ArrayList<String>();
            listDataHeaderJcPc = new ArrayList<>();
            listDataChildSortedSQLite2 = new HashMap<String, List<SQItem>>();
            Gson gson = new Gson();
            SQItem postdata;
            int headerALselectedIDX = 0;
            int fpcs = 0;
            int puc = 0;
            SQHeaderJobsPcsCount jcpc;
            boolean manual = !objGlobalMethods.IsEmptyString(objGlobalMethods.getMedsColPrefs(m, "manual"));

            if (dbObjJobsAL != null) {
                for (int i = 0; i <= dbObjJobsAL.size() - 1; i++) {
                    dbMedsJobs obj = dbObjJobsAL.get(i);
                    fpcs = obj.FPCS;
                    puc = obj.PUC;

                    ji = null;
                    JobInfoJsonStr = obj.JobInfoJsonStr;
                    if (JobInfoJsonStr != "") {
                        ji = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                    }

                    if (!headerAl.contains(obj.AddressWithoutPostal)) {
                        headerAl.add(obj.AddressWithoutPostal);
                        //jcpc = new SQHeaderJobsPcsCount(1, obj.PCS != null ? obj.PCS : 0);
                        jcpc = new SQHeaderJobsPcsCount(1, fpcs, puc);
                        headerjcpcAl.add(jcpc);
                        subGroupItemSqliteAL = new ArrayList<SQItem>();

                        if (manual) {
                            if (obj.PCS != null && obj.PCS > 1) {
                                for (int p = 0; p <= obj.PCS - 1; p++) {
                                    postdata = new SQItem(obj.ExpTypeDesc, obj.DelAddFull, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo,
                                            String.valueOf(obj.JobsIDX), false, obj.FLAG, obj.TimingTxt, obj.PCS, obj.PCS_SCANNED,
                                            subGroupItemSqliteAL.size(), obj.FLAG2, ji, true);

                                    postdata.setPosition(subGroupItemSqliteAL.size());
                                    subGroupItemSqliteAL.add(postdata);
                                }
                            } else {
                                postdata = new SQItem(obj.ExpTypeDesc, obj.DelAddFull, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo,
                                        String.valueOf(obj.JobsIDX), false, obj.FLAG, obj.TimingTxt, obj.PCS, obj.PCS_SCANNED,
                                        subGroupItemSqliteAL.size(), obj.FLAG2, ji, true);

                                postdata.setPosition(subGroupItemSqliteAL.size());
                                subGroupItemSqliteAL.add(postdata);
                            }
                        } else {
                            postdata = new SQItem(obj.ExpTypeDesc, obj.DelAddFull, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo,
                                    String.valueOf(obj.JobsIDX), false, obj.FLAG, obj.TimingTxt, obj.PCS, obj.PCS_SCANNED,
                                    subGroupItemSqliteAL.size(), obj.FLAG2, ji, false);

                            postdata.setPosition(subGroupItemSqliteAL.size());
                            subGroupItemSqliteAL.add(postdata);
                        }

                        itemAL.add(subGroupItemSqliteAL);
                    } else {
                        headerALselectedIDX = headerAl.indexOf(obj.AddressWithoutPostal);
                        headerjcpcAl.get(headerALselectedIDX).setJobsCount(1);
                        //headerjcpcAl.get(headerALselectedIDX).setPcsCount(obj.PCS != null ? obj.PCS : 0);
                        headerjcpcAl.get(headerALselectedIDX).setPcsCount(fpcs);
                        subGroupItemSqliteAL = (List<SQItem>) itemAL.get(headerALselectedIDX);

                        if (manual) {
                            if (obj.PCS != null && obj.PCS > 1) {
                                for (int p = 0; p <= obj.PCS - 1; p++) {
                                    postdata = new SQItem(obj.ExpTypeDesc, obj.DelAddFull, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo,
                                            String.valueOf(obj.JobsIDX), false, obj.FLAG, obj.TimingTxt, obj.PCS, obj.PCS_SCANNED,
                                            subGroupItemSqliteAL.size(), obj.FLAG2, ji, true);

                                    postdata.setPosition(subGroupItemSqliteAL.size());
                                    subGroupItemSqliteAL.add(postdata);
                                }
                            } else {
                                postdata = new SQItem(obj.ExpTypeDesc, obj.DelAddFull, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo,
                                        String.valueOf(obj.JobsIDX), false, obj.FLAG, obj.TimingTxt, obj.PCS, obj.PCS_SCANNED,
                                        subGroupItemSqliteAL.size(), obj.FLAG2, ji, true);

                                postdata.setPosition(subGroupItemSqliteAL.size());
                                subGroupItemSqliteAL.add(postdata);
                            }
                        } else {
                            postdata = new SQItem(obj.ExpTypeDesc, obj.DelAddFull, obj.JobInfoJsonStr, (obj.CNNo == null || obj.CNNo.isEmpty()) ? obj.JobNo : obj.CNNo,
                                    String.valueOf(obj.JobsIDX), false, obj.FLAG, obj.TimingTxt, obj.PCS, obj.PCS_SCANNED,
                                    subGroupItemSqliteAL.size(), obj.FLAG2, ji, false);

                            postdata.setPosition(subGroupItemSqliteAL.size());
                            subGroupItemSqliteAL.add(postdata);
                        }

                        itemAL.set(headerALselectedIDX, subGroupItemSqliteAL);
                    }
                }

                HashMap<String, List<SQItem>> rePositionedGroupItems = JobsHandler.getRePositionedSQItemGroupItems(m);

                String headervalue = "";
                String key = "";
                List<SQItem> rePosItems = new ArrayList<SQItem>();
                List<SQItem> oriItems = new ArrayList<SQItem>();
                SQItem rePosItem = null;
                String oriJobsIDX = "";
                boolean exist = false;
                int countUp = 0;
                int countdown = 0;
                if (rePositionedGroupItems != null && rePositionedGroupItems.size() > 0) {
                    Iterator<HashMap.Entry<String, List<SQItem>>> itr = null;
                    for (int i = 0; i <= headerAl.size() - 1; i++) {
                        headervalue = headerAl.get(i);
                        itr = rePositionedGroupItems.entrySet().iterator();
                        outer:
                        while (itr.hasNext()) {
                            HashMap.Entry<String, List<SQItem>> entry = itr.next();
                            key = entry.getKey();
                            if (key.equalsIgnoreCase(headervalue)) {
                                rePosItems = entry.getValue();
                                oriItems = (List<SQItem>) itemAL.get(i);
                                for (int x = 0; x <= oriItems.size() - 1; x++) {
                                    oriJobsIDX = oriItems.get(x).JobsIDX;
                                    for (int v = 0; v <= rePosItems.size() - 1; v++) {
                                        rePosItem = rePosItems.get(v);
                                        if (objGlobalMethods.IsEqualString(rePosItem.JobsIDX, oriJobsIDX)) {
                                            exist = true;
                                            break;
                                        }
                                    }

                                    if (exist) {
                                        oriItems.get(x).setSavedValued(rePosItem.position, rePosItem.oriPos, rePosItem.IsSelected);
                                    }
                                }
                                //rearrange items
                                Collections.sort((List<SQItem>) itemAL.get(i));
                            }
                        }
                    }
                }

                for (int i = 0; i <= headerAl.size() - 1; i++) {
                    listDataHeader.add(headerAl.get(i));
                    listDataHeaderJcPc.add(headerjcpcAl.get(i));
                    listDataChildSortedSQLite2.put(headerAl.get(i), (List<SQItem>) itemAL.get(i));
                }

                expandableSortedSQLiteListAdapter = new StartQueryAdapter(myApp.getAppContext(), listDataHeader, listDataHeaderJcPc, listDataChildSortedSQLite2);
                expandableSortedSQLiteListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

                String mChildCheckStates2PrefStr = objGlobalMethods.getGCMPrefs(m, "mChildCheckStates2PrefStr");
                if (!objGlobalMethods.IsEmptyString(mChildCheckStates2PrefStr) && !mChildCheckStates2PrefStr.equalsIgnoreCase("[]")) {
                    expandableSortedSQLiteListAdapter.mChildCheckStates2 = gson.fromJson(mChildCheckStates2PrefStr, new TypeToken<HashMap<Integer, objListItemPosChecked[]>>() {
                    }.getType());
                }

                // setting list adapter
                expListView.setAdapter(expandableSortedSQLiteListAdapter);
                setGroupIndicatorToRight();
                //expandSortedSQLiteList(expandableSortedSQLiteListAdapter);

            } else {
                //showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + "JobInfoAL is Null.");
                setEmptyList();
                Context c = myApp.getAppContext();
                //objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.nomedjoborallpu)).show();
                showErrorAlert(c.getString(R.string.nomedjoborallpu));
            }
            //new code with sqlite

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        } finally {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    expListView.clearFocus();
                    txtBarcodeMANUALENTRY.clearFocus();
                    txtBarcodeMANUALENTRY.setText("");
                    txtBarcodeMANUALENTRY.requestFocus();
                    objGlobalMethods.showNewPIPdialog(objGlobalMethods.getGcmPreferences());
                }
            }, 100);
        }
    }

    private void setEmptyList() {
        try {
            listDataHeader = new ArrayList<String>();
            listDataHeaderJcPc = new ArrayList<>();
            listDataChildSortedSQLite2 = new HashMap<String, List<SQItem>>();
            expandableSortedSQLiteListAdapter = new StartQueryAdapter(myApp.getAppContext(), listDataHeader, listDataHeaderJcPc, listDataChildSortedSQLite2);
            expandableSortedSQLiteListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
            expListView.setAdapter(expandableSortedSQLiteListAdapter);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StartQueryActivity setEmptyList. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void selectJobSQLite() {
        g = objGlobalMethods.getGcmPreferences();
        SharedPreferences m = objGlobalMethods.getMedsColSP();
        String c = "";
        Context ctx = myApp.getAppContext();
        boolean jobFound = false;
        List<dbObjJobs> dbObjJobsAL = null;
        dbObjJobs obj = null;
        int PCS = 0;
        int PCS_SCANNED = 0;
        String scanned = "";
        ArrayList<String> slist = null;

        try {
            if (cameraScannedCode != null) {
                if (!cameraScannedCode.equals("")) {
                    c = cameraScannedCode.toUpperCase();
                } else {
                    c = txtBarcodeMANUALENTRY.getText().toString().toUpperCase();
                }
            } else {
                c = txtBarcodeMANUALENTRY.getText().toString().toUpperCase();
            }
            objGlobalMethods.setGCMPrefs(g, "cameraScannedCode", "");

            if (c.indexOf(objGlobalVariables.EIDTPOD) != 0) {
                dbObjJobsAL = DAO_Jobs.searchJobsWithoutPostal(0, c);
                if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                    obj = dbObjJobsAL.get(0);
                    expandableSortedSQLiteListAdapter = (StartQueryAdapter) expListView.getExpandableListAdapter();
                    List<String> DataHeader;
                    List<SQHeaderJobsPcsCount> _listDataHeaderJcPc;
                    HashMap<String, List<SQItem>> DataChild2;
                    DataHeader = expandableSortedSQLiteListAdapter._listDataHeader;
                    _listDataHeaderJcPc = expandableSortedSQLiteListAdapter._listDataHeaderJcPc;
                    DataChild2 = expandableSortedSQLiteListAdapter._listDataChild;
                    List<SQItem> jpdlist = null;
                    SQItem jpd = null;
                    Iterator<HashMap.Entry<String, List<SQItem>>> itr = DataChild2.entrySet().iterator();
                    String groupName = "";
                    int groupIDX = 0;
                    int index = -1;
                    String key = "";

                    try {
                        outer:
                        while (itr.hasNext()) {
                            jpd = null;
                            HashMap.Entry<String, List<SQItem>> entry = itr.next();
                            key = entry.getKey();
                            jpdlist = entry.getValue();
                            for (int i = 0; i <= jpdlist.size() - 1; i++) {
                                jpd = jpdlist.get(i);
                                if (jpd.JobsIDX.equals(String.valueOf(obj.JobsIDX))) {
                                    for (int j = 0; j <= DataHeader.size() - 1; j++) {
                                        groupIDX = j;
                                        groupName = DataHeader.get(j);
                                        if (key.equalsIgnoreCase(groupName)) {
                                            expandableSortedSQLiteListAdapter.mChildCheckStates2 = expandableSortedSQLiteListAdapter.getmChildCheckStates2(groupIDX);
                                            objListItemPosChecked getChecked2[] = expandableSortedSQLiteListAdapter.mChildCheckStates2.get(groupIDX);
                                            getChecked2[i].setChecked(true);
                                            expandableSortedSQLiteListAdapter.mChildCheckStates2.put(groupIDX, getChecked2);

                                            index = i;
                                            jobFound = true;
                                            break outer;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception caught at StartQueryActivity selectJobSQLite().", "", "", "", "");
                        jpd = null;
                        jobFound = false;
                    }

                    if (jobFound) {
                        if (jpd.PCS_SCANNED < jpd.PCS) {
                            objGlobalMethods.writeToFile(c + " detected.","","","","");
                            jpd.setPCS_SCANNED(1);
                            PCS = jpd.PCS;
                            PCS_SCANNED = jpd.PCS_SCANNED;
                            _listDataHeaderJcPc.get(groupIDX).setPcsCountMinus();

                            scanned = objGlobalMethods.getMedsColPrefs(m, "scanned");
                            if (!objGlobalMethods.IsEmptyString(scanned)) {
                                slist = new Gson().fromJson(scanned, new TypeToken<ArrayList<String>>() {}.getType());
                            }
                            if (slist == null)
                                slist = new ArrayList<>();

                            slist.add(jpd.CNNO);
                            scanned = (slist != null && slist.size() > 0) ? new Gson().toJson(slist) : "";

                            /*if ((PCS == 1) || (PCS > 1 && PCS_SCANNED == 1)) {
                                jpd.setBlink(true);
                                jpd.setBlinkmode(PCS_SCANNED < PCS ? 1 : 0);
                                playSuccessVerifiedTone();
                            } else if (PCS > 1 && PCS_SCANNED > 1) {
                                jpd.setBlink(true);
                                jpd.setBlinkmode(PCS_SCANNED < PCS ? 1 : 0);
                                playSuccessVerifiedTone2();
                            }*/
                            if ((PCS == 1)) {
                                jpd.setBlink(true);
                                jpd.setBlinkmode(PCS_SCANNED < PCS ? 1 : 0);
                                playSuccessVerifiedTone();
                            } else if (PCS > 1) {
                                jpd.setBlink(true);
                                jpd.setBlinkmode(PCS_SCANNED < PCS ? 1 : 0);
                                playSuccessVerifiedTone2();
                            }
                            txtBarcodeMANUALENTRY.setText("");
                            txtBarcodeMANUALENTRY.requestFocus();

                            if (expandableSortedSQLiteListAdapter.mChildCheckStates2 != null && expandableSortedSQLiteListAdapter.mChildCheckStates2.size() > 0) {
                                Gson gson = new Gson();
                                String mChildCheckStates2PrefStr = gson.toJson(expandableSortedSQLiteListAdapter.mChildCheckStates2);
                                objGlobalMethods.setMedsColPrefs(m, "mChildCheckStates2PrefStr", mChildCheckStates2PrefStr);
                                String[] nn = new String[]{"mChildCheckStates2PrefStr", "scanned"};
                                String[] vv = new String[]{mChildCheckStates2PrefStr, scanned};
                                objGlobalMethods.setMedsColPrefsMass(m, nn, vv);
                            } else {
                                objGlobalMethods.setMedsColPrefs(m, "scanned", scanned);
                            }
                            //expandableSortedSQLiteListAdapter.notifyDataSetChanged();

                            //btnPU.setText(ctx.getString(R.string.buttontext_PU) + " (" + (slist != null ? slist.size() : "0") + ")");

                            final int finalindex = index;
                            if (index > 0) {
                                int finalIDX = 0;
                                if (groupIDX == 0) {
                                    finalIDX = 1 + finalindex;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(1 + finalindex);
                                        }
                                    }, 100);
                                } else if (groupIDX > 0) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    finalIDX = skippedCount + finalindex;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount + finalindex);
                                        }
                                    }, 100);
                                }

                                expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                String[] prefName = new String[]{"SQGroupScrollSavedIndex", "SQGroupScrollSavedPosition"};
                                String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                objGlobalMethods.setMedsColPrefsMass(m, prefName, prefvalue);

                                expandableSortedSQLiteListAdapter.notifyDataSetChanged();

                            } else {
                                int finalIDX = 0;
                                if (groupIDX == 0) {
                                    finalIDX = 1 + finalindex;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(1 + finalindex);
                                        }
                                    }, 100);
                                } else if (groupIDX > 0) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    finalIDX = skippedCount + finalindex;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount + finalindex);
                                        }
                                    }, 100);
                                }

                                expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                                String[] prefName = new String[]{"SQGroupScrollSavedIndex", "SQGroupScrollSavedPosition"};
                                String[] prefvalue = new String[]{String.valueOf(groupIDX), String.valueOf(finalIDX)};
                                objGlobalMethods.setMedsColPrefsMass(m, prefName, prefvalue);

                                expandableSortedSQLiteListAdapter.notifyDataSetChanged();

                            }

                            final long JobsIDX = Long.parseLong(jpd.JobsIDX);
                            final Thread t = new Thread() {
                                @Override
                                public void run() {
                                    DAO_Jobs.updatePIPPCS_1by1(JobsIDX, 1);
                                }
                            };
                            t.start();
                        } else if (jpd.PCS_SCANNED == jpd.PCS) {
                            playTone();
                            txtBarcodeMANUALENTRY.setText("");
                            txtBarcodeMANUALENTRY.requestFocus();
                            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.morethanrequired) + "\n\n" + obj.CNNo + "\n" + obj.DelAddFull);
                            objGlobalMethods.writeToFile(c + " detected. More than indicated pcs.","","","","");
                            final int finalindex = index;
                            if (index > 0) {
                                int finalIDX = 0;
                                if (groupIDX == 0) {
                                    finalIDX = 1 + finalindex;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(1 + finalindex);
                                        }
                                    }, 100);
                                } else if (groupIDX > 0) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    finalIDX = skippedCount + finalindex;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount + finalindex);
                                        }
                                    }, 100);
                                }

                                expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                            } else {
                                int finalIDX = 0;
                                if (groupIDX == 0) {
                                    finalIDX = 1 + finalindex;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(1 + finalindex);
                                        }
                                    }, 100);
                                } else if (groupIDX > 0) {
                                    final int skippedCount = getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
                                    finalIDX = skippedCount + finalindex;
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do something after 5s = 5000ms
                                            expListView.setSelection(skippedCount + finalindex);
                                        }
                                    }, 100);
                                }

                                expandSortedSQLiteList(expandableSortedSQLiteListAdapter);
                            }
                        }
                    } else {
                        playTone();
                        txtBarcodeMANUALENTRY.setText("");
                        txtBarcodeMANUALENTRY.requestFocus();
                        showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.alert_jobnotfound));
                        objGlobalMethods.writeToFile(c + " detected. Not found.","","","","");
                    }
                } else {
                    dbObjJobsAL = DAO_Jobs.searchJobsWithoutPostal(1, c);
                    if (dbObjJobsAL != null && dbObjJobsAL.size() > 0) {
                        playTone2();
                        txtBarcodeMANUALENTRY.setText("");
                        txtBarcodeMANUALENTRY.requestFocus();
                        showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.itempu));
                        objGlobalMethods.writeToFile(c + " detected. Item under PU list.","","","","");
                    } else {
                        playTone();
                        txtBarcodeMANUALENTRY.setText("");
                        txtBarcodeMANUALENTRY.requestFocus();
                        showDialogNfocusTxtbox(c + "\n\n" + myApp.getAppContext().getString(R.string.alert_jobnotfound));
                        objGlobalMethods.writeToFile(c + " detected. Not found.","","","","");
                    }
                }
            } else {
                playTone();
                txtBarcodeMANUALENTRY.setText("");
                txtBarcodeMANUALENTRY.requestFocus();
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_jobnotfound));
                objGlobalMethods.writeToFile(c + " detected. Not found.","","","","");
            }
            //EnableDisableFailOKPbuttons();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at StartQueryActivity selectJobSQLite. Exception: " + e.getMessage().toString(), "", "", "", "");
            playTone();
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata));
        }
    }

    private Dialog showDialogNfocusTxtbox(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                txtBarcodeMANUALENTRY.setText("");
                txtBarcodeMANUALENTRY.requestFocus();
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    private void expandSortedSQLiteList(StartQueryAdapter adapter) {
        for (int i = 0; i <= adapter.getGroupCount() - 1; i++) {
            expListView.expandGroup(i);
        }
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void scrollToTopOfGroup(int groupIDX, List<String> DataHeader, HashMap<String, List<SQItem>> DataChild2) {
        try {
            int skippedCount = (groupIDX == 0) ? 1 : getSkippedCount(DataChild2.entrySet().iterator(), groupIDX, DataHeader);
            final int askippedCount = skippedCount > 0 ? skippedCount - 1 : skippedCount;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    expListView.setSelection(askippedCount);
                }
            }, 100);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity scrollToTop. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public static int getSkippedCount(Iterator<HashMap.Entry<String, List<SQItem>>> itr, int selectGroupIDX, List<String> DataHeader) {
        int count = 0;
        String key = "";
        List<SQItem> jpdlist = null;
        String groupName = "";
        List<String> keyList = new ArrayList<>();
        List<List<SQItem>> jpdlistList = new ArrayList<>();
        try {
            while (itr.hasNext()) {
                HashMap.Entry<String, List<SQItem>> entry = itr.next();
                key = entry.getKey();
                jpdlist = entry.getValue();
                keyList.add(key);
                jpdlistList.add(jpdlist);
            }

            asd:
            for (int j = 0; j <= DataHeader.size() - 1; j++) {
                if (j < selectGroupIDX) {
                    groupName = DataHeader.get(j);
                    outer:
                    for (int i = 0; i <= keyList.size() - 1; i++) {
                        if (keyList.get(i).equals(groupName) && jpdlistList.get(i) != null) {
                            count += jpdlistList.get(i).size() + 1;
                            break outer;
                        }
                    }
                } else if (j == selectGroupIDX) {
                    count += 1;
                    break asd;
                }
            }

        } catch (Exception e) {
            count = 0;
        } finally {
            return count;
        }
    }

    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone2() {
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, errTone2URI);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity playTone2. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playSuccessVerifiedTone2() {
        try {
            stopPlayingTone();

            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, verifiedToneURI2);
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at JobsListGroupActivity playSuccessVerifiedTone2. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void showScanned() {
        String scanned = "";
        /*ArrayList<String> slist = null;*/
        SharedPreferences m = objGlobalMethods.getMedsColSP();
        try {

            /*scanned = objGlobalMethods.getMedsColPrefs(m, "scanned");
            if (!objGlobalMethods.IsEmptyString(scanned)) {
                slist = new Gson().fromJson(scanned, new TypeToken<ArrayList<String>>() {}.getType());
            }

            if (slist != null && slist.size() > 0)
                objGlobalMethods.showToNoteMsgPromptScrollable(myApp.getAppContext().getString(R.string.progresscount), slist);*/
            scanned = objGlobalMethods.getMedsColPrefs(m, "scanned");
            if (!objGlobalMethods.IsEmptyString(scanned)) {
                ScannListDialog sld = new ScannListDialog();
                sld.show(getSupportFragmentManager(), "ScannListDialog");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StartQueryActivity showScanned. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}