package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.MPD_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objMPD_Info;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.List;

public class MPD_CheckOutActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    TextView txtdevicetodraw;
    TextView txtdevicedrawn;
    EditText txtbarcode;
    Button btnSubmit;
    ListView lvDevicesDrawn;
    private ProgressDialog pd;
    String username, password, useridx, ishomesuser, cameraScannedCode;
    String[] UsernamePwd = new String[4];
    ConnectionDetector cd;
    MPD_PostItemAdapter adapter;
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
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

        setContentView(R.layout.activity_mpd__check_out);

        txtdevicedrawn = (TextView) findViewById(R.id.txtdevicedrawn);
        txtdevicetodraw = (TextView) findViewById(R.id.txtdevicetodraw);
        txtbarcode = (EditText) findViewById(R.id.txtbarcode);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        lvDevicesDrawn = (ListView) findViewById(R.id.lvDevicesDrawn);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                Context ctx = myApp.getAppContext();
                setTitle(ctx.getString(R.string.title_activity_mpd));

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];

                if (username.equals("") && password.equals("")) {
                    BackToLoginPage2();
                }

                lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);
                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    Toast.makeText(ctx, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                //String mpdsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mpdsPrefStr");
                String mpdsPrefStr = objGlobalMethods.getMPDRelatedSP_KeyValue("mpdsPrefStr");
                ArrayList<objMPD_Info> mpds = null;
                if (!mpdsPrefStr.isEmpty() && !mpdsPrefStr.equals("") && mpdsPrefStr != "" && !mpdsPrefStr.equals("[]") && mpdsPrefStr != "[]") {
                    Gson gson = new Gson();
                    mpds = gson.fromJson(mpdsPrefStr, new TypeToken<List<objMPD_Info>>() {
                    }.getType());
                }
                if (mpds == null) {
                    refresh task = new refresh();
                    task.execute();
                } else {
                    loadlist(mpds);
                }

                cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
                if (!cameraScannedCode.equals("")) {
                    txtbarcode.setText(cameraScannedCode);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                    if (!objGlobalMethods.verifyBarcode(cameraScannedCode)) {
                        showDialog(myApp.getAppContext().getString(R.string.alert_InvalidBarcode)).show();
                    }
                }

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("MPD_CheckOutActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at MPD_CheckOutActivity onResume. " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
            }
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    @Override
    public void onDestroy() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = null;
        if (GcmPreferences == null) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
        }
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
        JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mpd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        } else if (item.getItemId() == R.id.refresh) {
            refresh task = new refresh();
            task.execute();
        }

        /*switch (item.getItemId()) {
            case R.id.qrscan:
                launchQRScan();
                break;
            case R.id.refresh:
                refresh task = new refresh();
                task.execute();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(MPD_CheckOutActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
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
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "mpd");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        parentIntent = new Intent(MPD_CheckOutActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    public void btnScanClick(View view) {
        launchQRScan();
    }

    public void btnSubmitClick(View view) {
        try {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            Context ctx = myApp.getAppContext();
            //cd = new ConnectionDetector(ctx);
            cd = new ConnectionDetector(null);
            if (!cd.isConnectingToInternet()) {
                showDialog(ctx.getString(R.string.toast_workingconnection));
            } else {
                final String val = txtbarcode.getText().toString().toUpperCase();
                if (!val.isEmpty() && !val.equals("") && val != "") {
                    if (objGlobalMethods.verifyBarcode(val) && !machineExist(val)) {
                        showSubmissionPOP(val);
                    } else if (objGlobalMethods.verifyBarcode(val) && machineExist(val)) {
                        showDialog(myApp.getAppContext().getString(R.string.mpdalreadytaken)).show();
                    } else {
                        showDialog(myApp.getAppContext().getString(R.string.alert_InvalidBarcode)).show();
                    }
                } else {
                    showDialog(ctx.getString(R.string.alert_mpdplssubmit));
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity btnSubmitClick. Exception: " + s, "", "", "", "");
        }
    }

    private boolean machineExist(String val) {
        boolean asd = false;
        try {
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            ArrayList<objMPD_Info> mpds = null;
            mpds = objGlobalMethods.getMPDsALfromPrefStr();
            if (mpds != null && mpds.size() > 0) {
                for (int i = 0; i <= mpds.size() - 1; i++) {
                    if (mpds.get(i).Serial_Number.equals(val)) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity machineExist. Exception: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    private void showSubmissionPOP(final String val) {
        try {
            final Context ctx = myApp.getAppContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity != null ? myApp.currentActivity : this);
            builder.setCancelable(false);
            builder.setTitle("");
            builder.setMessage(ctx.getString(R.string.alert_mpdsubmit));

            //builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_jobsheetsubmitno), new DialogInterface.OnClickListener() {
            builder.setNegativeButton(ctx.getString(R.string.buttontext_jobsheetsubmitcancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });

            builder.setPositiveButton(ctx.getString(R.string.buttontext_jobsheetsubmityes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status , no printing
                    try {
                        dialog.dismiss();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                txtbarcode.setText("");
                                MPDCheckOut task = new MPDCheckOut(val);
                                task.execute();
                            }
                        }, 500);

                    } catch (Exception e) {
                        showDialog("Error submitting due following error." + "\n" + e.getMessage()).show();
                    }
                }
            });

            /*builder.setNeutralButton(myApp.getAppContext().getString(R.string.buttontext_jobsheetsubmitcancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "Cancel", update job status
                    dialog.cancel();

                }
            });*/

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at JobsheetSubmitActivity showDriverSubmissionPOP. Exception: " + e.toString(), "", "", "", "");
        }
    }

    //Start showDialog
    public Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity != null ? myApp.currentActivity : this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    private class MPDCheckOut extends AsyncTask<Void, Void, Integer> {
        final String ASerialNumber;

        MPDCheckOut(String aASerialNumber) {
            this.ASerialNumber = aASerialNumber;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int val = 0;
            String ARegID = "";
            try {
                /*if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }*/
                objGlobalMethods.writeToFile("Checking out MPD " + ASerialNumber + ".", "", "", "", "");
                ARegID = objGlobalMethods.getRegistrationIdOnly();
                val = WebService.MPD_Check_Out(ARegID, ASerialNumber);
            } catch (Exception e) {
                val = -1;
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity MPDCheckOut doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return val;
            }
        }

        @Override
        protected void onPostExecute(final Integer MPD_Check_OutResult) {
            try {
                pd.dismiss();
                final Context ctx = myApp.getAppContext();
                if (MPD_Check_OutResult <= 0) {
                    //invalid/unknown
                    objGlobalMethods.writeToFile("Unknown MPD " + ASerialNumber + " checked out.", "", "", "", "");
                    showDialog(ctx.getString(R.string.mpdunknown));
                } else {
                    //1 == visa 2 == nets
                    objMPD_Info mpd = new objMPD_Info(MPD_Check_OutResult, ASerialNumber);
                    /*if (GcmPreferences == null) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                    }
                    String mpdsPrefStr;
                    Gson gson = new Gson();
                    mpdsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mpdsPrefStr");
                    ArrayList<objMPD_Info> mpds = null;
                    if (!mpdsPrefStr.isEmpty() && !mpdsPrefStr.equals("") && mpdsPrefStr != "") {
                        mpds = gson.fromJson(mpdsPrefStr, new TypeToken<List<objMPD_Info>>() {
                        }.getType());
                    }*/

                    String mpdsPrefStr;
                    Gson gson = new Gson();
                    ArrayList<objMPD_Info> mpds = objGlobalMethods.getMPDsALfromPrefStr();

                    if (mpds != null) {
                        boolean exist = false;
                        for (int i = 0; i <= mpds.size() - 1; i++) {
                            if (mpds.get(i).Serial_Number.equals(ASerialNumber)) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            mpds.add(mpd);
                        }
                    } else {
                        mpds = new ArrayList<objMPD_Info>();
                        mpds.add(mpd);
                    }
                    mpdsPrefStr = gson.toJson(mpds);
                    /*objGlobalMethods.setGCMPrefs(GcmPreferences, "mpdsPrefStr",
                            !mpdsPrefStr.isEmpty() && !mpdsPrefStr.equals("") && mpdsPrefStr != "" && !mpdsPrefStr.equals("[]") && mpdsPrefStr != "[]" ? mpdsPrefStr : "");*/
                    objGlobalMethods.setMPDRelatedSP_KeyValue("mpdsPrefStr",
                            !mpdsPrefStr.isEmpty() && !mpdsPrefStr.equals("") && mpdsPrefStr != "" && !mpdsPrefStr.equals("[]") && mpdsPrefStr != "[]" ? mpdsPrefStr : "");
                    objGlobalMethods.writeToFile("MPD " + ASerialNumber + " checked out.", "", "", "", "");
                    loadlist(mpds);
                    showDialog(ctx.getString(R.string.equipped) + " " +
                            ctx.getString(
                                    MPD_Check_OutResult == 1 ? R.string.mpdcc :
                                            MPD_Check_OutResult == 2 ? R.string.mpdnets :
                                                    MPD_Check_OutResult == 3 ? R.string.mpdcba :
                                                            MPD_Check_OutResult == 4 ? R.string.mpdcbb :
                                                                    MPD_Check_OutResult == 5 ? R.string.mpdcbc :
                                                                            MPD_Check_OutResult == 6 ? R.string.mpdcbd :
                                                                                    MPD_Check_OutResult == 13 ? R.string.mpdpcmbs :
                                                                                            MPD_Check_OutResult == 14 ? R.string.mpdpcmbl :
                                                                                                    MPD_Check_OutResult == 15 ? R.string.mpdpcmg :
                                                                                                            MPD_Check_OutResult == 16 ? R.string.mpdpcmw :
                                                                                                                    MPD_Check_OutResult == 21 ? R.string.mpdtl :
                                                                                                                            R.string.mpdunknown));
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity MPDCheckOut doInBackground. Exception: " + s, "", "", "", "");
            }
        }
    }

    private class refresh extends AsyncTask<Void, Void, ArrayList<objMPD_Info>> {
        refresh() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected ArrayList<objMPD_Info> doInBackground(Void... params) {
            ArrayList<objMPD_Info> mpds = null;
            try {
                mpds = objGlobalMethods.grabMPDFromServer();
            } catch (Exception e) {
                mpds = null;
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity refresh doInBackground. Exception: " + s, "", "", "", "");
            } finally {
                return mpds;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<objMPD_Info> mpds) {
            try {
                pd.dismiss();
                if (mpds != null) {
                    /*if (GcmPreferences == null) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                    }
                    objGlobalMethods.setMPDsPrefStr(mpds, GcmPreferences);*/
                    objGlobalMethods.setMPDsPrefStrNew(mpds);
                    loadlist(mpds);
                } else {
                    Context ctx = myApp.getAppContext();
                    showDialog(ctx.getString(R.string.alert_errorretrivedata2));
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity refresh onPostExecute. Exception: " + s, "", "", "", "");
            }
        }
    }

    private void loadlist(ArrayList<objMPD_Info> mpds) {
        String mpdsPrefStr;
        Gson gson = new Gson();
        ArrayList<String> mpdal = null;
        try {
            if (mpds == null) {
                /*if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                mpdsPrefStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "mpdsPrefStr");
                if (!mpdsPrefStr.isEmpty() && !mpdsPrefStr.equals("") && mpdsPrefStr != "" && !mpdsPrefStr.equals("[]") && mpdsPrefStr != "[]") {
                    mpds = gson.fromJson(mpdsPrefStr, new TypeToken<List<objMPD_Info>>() {
                    }.getType());
                }*/
                mpds = objGlobalMethods.getMPDsALfromPrefStr();
            }
            if (mpds == null) {
                if (adapter != null) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                }
            } else {
                if (adapter != null) {
                    adapter.clear();
                    adapter.addAll(mpds);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new MPD_PostItemAdapter(this, mpds);
                    lvDevicesDrawn.setAdapter(adapter);
                }
            }
            Context ctx = myApp.getAppContext();
            mpdal = objGlobalMethods.rtnReqMPDnames();
            if (mpdal != null && mpdal.size() > 0) {
                //String msg = ctx.getString(R.string.alert_mpdnotequipped);
                String msg = "";
                for (int i = 0; i <= mpdal.size() - 1; i++) {
                    msg += i == 0 ? mpdal.get(i) + " " + ctx.getString(R.string.nottaken) : "\n" + mpdal.get(i) + " " + ctx.getString(R.string.nottaken);
                }
                txtdevicetodraw.setText(msg);
                txtdevicedrawn.setVisibility(View.VISIBLE);
            } else {
                txtdevicetodraw.setText(mpds != null && mpds.size() > 0 ? ctx.getString(R.string.alldevicedrawn) : ctx.getString(R.string.nothingtotake));
                txtdevicedrawn.setVisibility(mpds != null && mpds.size() > 0 ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity loadlist. Exception: " + s, "", "", "", "");
        }
    }

    public void refreshlist() {
        try {
            loadlist(null);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity refreshlist. Exception: " + s, "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at MPD_CheckOutActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPD_CheckOutActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
