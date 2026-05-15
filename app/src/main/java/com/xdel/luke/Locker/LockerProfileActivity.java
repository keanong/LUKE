package com.xdel.luke.Locker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.HRM.objPassQR_IDbarcode;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LockerProfileActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    ConnectionDetector cd;
    TextView lblStatus, lblMobile, lblPinExpiry, lblPrivateKey, lblchangePin, lblchangeMobile;
    ImageView imgPrivateKeyBarcode;
    private ProgressDialog pd, pdprocessing;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mma");
    LinearLayout LLChangeMobile;
    EditText txtmobile;
    Button btnUpdateMobile;

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(LockerProfileActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToDashboard() {
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
            JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity BackToDashboard. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            startActivity(parentIntent);
            this.finish();
        }
    }

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

        setContentView(R.layout.activity_locker_profile);

        lblPrivateKey = (TextView) findViewById(R.id.lblPrivateKey);
        lblStatus = (TextView) findViewById(R.id.lblStatus);
        lblMobile = (TextView) findViewById(R.id.lblMobile);
        lblPinExpiry = (TextView) findViewById(R.id.lblPinExpiry);
        lblchangeMobile = (TextView) findViewById(R.id.lblchangeMobile);
        lblchangePin = (TextView) findViewById(R.id.lblchangePin);
        imgPrivateKeyBarcode = (ImageView) findViewById(R.id.imgPrivateKeyBarcode);
        LLChangeMobile = (LinearLayout) findViewById(R.id.LLChangeMobile);
        txtmobile = (EditText) findViewById(R.id.txtmobile);
        btnUpdateMobile = (Button) findViewById(R.id.btnUpdateMobile);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

        pdprocessing = new ProgressDialog(this);
        pdprocessing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdprocessing.setCanceledOnTouchOutside(false);
        pdprocessing.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                Context ctx = myApp.getAppContext();
                setTitle(ctx.getString(R.string.title_activity_lockerprofile));

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("LockerProfileActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                JobsHandler.checkForceLogOut();
                LLChangeMobile.setVisibility(View.GONE);
                txtmobile.setText("");
                init();

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at LockerProfileActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);

            LLChangeMobile = null;
            dismissNullifyPD(pd);
            dismissNullifyPD(pdprocessing);

            lblPrivateKey = null;
            lblStatus = null;
            lblMobile = null;
            lblPinExpiry = null;
            lblchangeMobile = null;
            lblchangePin = null;
            imgPrivateKeyBarcode = null;
            LLChangeMobile = null;
            txtmobile = null;
            btnUpdateMobile = null;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void dismissNullifyPD(ProgressDialog apd) {
        try {
            if (apd != null && apd.isShowing()) {
                apd.dismiss();
                apd = null;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity dismissNullifyPD. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LockerProfileActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_locker_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.refresh) {
            ConnectionDetector cd = new ConnectionDetector(null);
            if (!cd.isConnectingToInternet()) {
                cd = null;
                Context ctx = myApp.getAppContext();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_cannotRefreshNoInternet)).show();
            } else {
                cd = null;
                refreshData r = new refreshData();
                r.execute();
            }
        }

        /*Intent parentIntent;
        switch (item.getItemId()) {
            case R.id.refresh:
                ConnectionDetector cd = new ConnectionDetector(null);
                if (!cd.isConnectingToInternet()) {
                    cd = null;
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_cannotRefreshNoInternet)).show();
                } else {
                    cd = null;
                    refreshData r = new refreshData();
                    r.execute();
                }
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent parentIntent = new Intent(this, ProfileActivity.class);
        MainActivity.uponLogin = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private void init() {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            Date expiry;
            String L2_LA_GetUserResultJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "GetUserResultJsonStr");
            if (!objGlobalMethods.IsEmptyString(L2_LA_GetUserResultJsonStr)) {
                Gson gson = new Gson();
                final L2_LA_GetUserResult res = gson.fromJson(L2_LA_GetUserResultJsonStr, L2_LA_GetUserResult.class);
                if (res != null) {
                    setInfoText(res, null);

                    new CountDownTimer(1500, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            pd.show();
                        }

                        @Override
                        public void onFinish() {
                            getPassQR_IDbarcode t = new getPassQR_IDbarcode(res.User_Key);
                            t.execute();
                        }
                    }.start();
                } else {
                    objGlobalMethods.writeToFile("res is null at LockerProfileActivity init.", "", "", "", "");
                    Context ctx = myApp.getAppContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setTitle("");
                    builder.setMessage(ctx.getString(R.string.alert_noRetrieveDataFailDueNoInternet));
                    builder.setCancelable(false);
                    builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user pressed "yes", then he is allowed to exit from application
                            BackToDashboard();
                        }
                    });
                    builder.show();
                }
            } else {
                objGlobalMethods.writeToFile("L2_LA_GetUserResultJsonStr is empty at LockerProfileActivity init.", "", "", "", "");
                Context ctx = myApp.getAppContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setTitle("");
                builder.setMessage(ctx.getString(R.string.alert_noRetrieveDataFailDueNoInternet));
                builder.setCancelable(false);
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        BackToDashboard();
                    }
                });
                builder.show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity init. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void setInfoText(L2_LA_GetUserResult ur, L2_LA_UpdateMobileResult mr) {
        Date expiry;
        try {
            if (ur != null) {
                lblPrivateKey.setText(ur.User_Key);
                lblStatus.setText(ur.Status);
                lblMobile.setText(ur.User_Mobile);
                expiry = objGlobalMethods.JsonDateToDate(ur.PIN_Expiry);
                lblPinExpiry.setText(formatter.format(expiry));
            } else if (mr != null) {
                lblPrivateKey.setText(mr.User_Key);
                lblStatus.setText(mr.Status);
                lblMobile.setText(mr.User_Mobile);
                expiry = objGlobalMethods.JsonDateToDate(mr.PIN_Expiry);
                lblPinExpiry.setText(formatter.format(expiry));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity setInfoText. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void lblchangeMobile_OnClick(View v) {
        try {
            if (LLChangeMobile == null) {
                LLChangeMobile = findViewById(R.id.LLChangeMobile);
            }

            LLChangeMobile.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity lblchangeMobile_OnClick. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private String getChangePinURL() {
        String url = "";
        try {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            String GetUserResultJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "GetUserResultJsonStr");
            Gson gson = new Gson();
            L2_LA_GetUserResult GetUserResult = gson.fromJson(GetUserResultJsonStr, L2_LA_GetUserResult.class);
            url = GetUserResult.PIN_Portal_URL;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity lblchangePin_OnClick. Exception: " + e.getMessage() + "\nStack: " + s,"", "", "", "");
        } finally {
            return url;
        }
    }

    public void lblchangePin_OnClick(View v) {
        try {
            String url = getChangePinURL();
            if (!objGlobalMethods.IsEmptyString(url)) {
                openChangePinBrowser(url);
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.unableresetpin)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity lblchangePin_OnClick. Exception: " + e.getMessage() + "\nStack: " + s,"", "", "", "");
        }
    }

    public void btnUpdateMobile_Click(View v) {
        Context ctx = myApp.getAppContext();
        String newmobile = txtmobile.getText().toString();
        try {
            if (objGlobalMethods.IsMobile(newmobile) || objGlobalMethods.IsContactNumber(newmobile)) {
                Update_Mobile x = new Update_Mobile(newmobile);
                x.execute();
            } else if (objGlobalMethods.IsEmptyString(newmobile) || ((!objGlobalMethods.IsMobile(newmobile) && !objGlobalMethods.IsContactNumber(newmobile)))) {
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_entermobile)).show();
            } else {
                objGlobalMethods.writeToFile("Unable to call btnUpdateMobile_Click. New mobile entered: " + newmobile, "", "", "", "");
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity btnUpdateMobile_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
        }
    }

    private void openChangePinBrowser(final String url) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("key", lblPrivateKey.getText().toString());
            clipboardManager.setPrimaryClip(clipData);

            Context ctx = myApp.getAppContext();
            final AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setTitle("");
            builder.setMessage(ctx.getString(R.string.accountnumbercopied));
            builder.setCancelable(false);
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application

                    /*String url = "www.lockeralliance.net/pin";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    dialog.dismiss();*/

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    dialog.dismiss();
                }
            });
            builder.show();


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity openChangePinBrowser. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class getPassQR_IDbarcode extends AsyncTask<Void, Void, objPassQR_IDbarcode> {
        private final String IDval;

        public getPassQR_IDbarcode(String aIDval) {
            this.IDval = aIDval;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pd != null && !pd.isShowing()) {
                pd.show();
            }
        }

        @Override
        protected objPassQR_IDbarcode doInBackground(Void... params) {
            objPassQR_IDbarcode obj = new objPassQR_IDbarcode();
            byte[] res = null;
            String finalmsg = "";
            Bitmap bmp = null;
            try {
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                com.google.zxing.Writer writer = new MultiFormatWriter();
                String finaldata = Uri.encode(IDval, "utf-8");
                int height = imgPrivateKeyBarcode.getHeight();
                int width = imgPrivateKeyBarcode.getWidth();
                BitMatrix bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, height);
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for (int i = 0; i < width; i++) {//width
                    for (int j = 0; j < height; j++) {//height
                        bmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
                obj.setIDbarcode(bmp);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LockerProfileActivity getPassQR_IDbarcode doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return obj;
            }
        }

        @Override
        protected void onPostExecute(final objPassQR_IDbarcode res) {
            boolean err = false;
            boolean IDerr = false;
            Bitmap IDbarcode = null;

            Context ctx = myApp.getAppContext();
            try {
                IDbarcode = res.getIDbarcode();
                IDerr = IDbarcode == null ? true : false;

                if (IDbarcode != null) {
                    imgPrivateKeyBarcode.setImageBitmap(IDbarcode);
                }
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                dismissNullifyPD(pd);
            } catch (Exception e) {
                err = true;
                objGlobalMethods.writeToFile("Exception at LockerProfileActivity getPassQR_IDbarcode onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                dismissNullifyPD(pd);
                String finalmsg = "";
                if (err || IDerr) {
                    finalmsg = ctx.getString(R.string.alert_errorretrivedata_locker_barcode);
                }
                if (!finalmsg.isEmpty() && !finalmsg.equals("")) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), finalmsg).show();
                }
            }
        }
    }

    private class Update_Mobile extends AsyncTask<Void, Void, L2_LA_UpdateMobileResult> {

        private final String mobile;

        public Update_Mobile(String amobile) {
            this.mobile = amobile;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pdprocessing != null && !pdprocessing.isShowing()) {
                pdprocessing.show();
            }
        }

        @Override
        protected L2_LA_UpdateMobileResult doInBackground(Void... params) {
            L2_LA_UpdateMobileResult res = null;
            try {
                String regid = objGlobalMethods.getRegistrationIdOnly();
                res = WebService.L2_LA_UpdateMobile(regid, mobile);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LockerProfileActivity Update_Mobile doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final L2_LA_UpdateMobileResult res) {
            boolean proceed = false;
            try {
                dismissNullifyPD(pdprocessing);
                Context ctx = myApp.getAppContext();
                String oldmobile = lblMobile.getText().toString();
                if (res == null) {
                    objGlobalMethods.writeToFile("Unable to update new mobile due res is null.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                } else if (objGlobalMethods.IsEmptyString(res.User_Mobile)) {
                    objGlobalMethods.writeToFile("Unable to update new mobile due res.User_Mobile is null or empty.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                } else if (objGlobalMethods.IsEqualString(res.User_Mobile, oldmobile)) {
                    objGlobalMethods.writeToFile("Unable to update new mobile due submitted same mobile or submitted mobile already in used.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_mobile_not_updated)).show();
                } else if (!objGlobalMethods.IsEqualString(res.User_Mobile, oldmobile)) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    Gson gson = new Gson();
                    String GetUserResultJsonStr = gson.toJson(res);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "GetUserResultJsonStr", GetUserResultJsonStr);
                    setInfoText(null, res);
                    proceed = true;
                    if (LLChangeMobile == null) {
                        LLChangeMobile = findViewById(R.id.LLChangeMobile);
                    }
                    LLChangeMobile.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LockerProfileActivity Update_Mobile onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                dismissNullifyPD(pdprocessing);
                if (proceed) {
                    openChangePinBrowser("www.lockeralliance.net/pin");
                    //not using the following cuz change mobile does not reget L2_LA_GetUserResult, so the change pin url is hard coded here
                    /*String url = getChangePinURL();
                    if (!objGlobalMethods.IsEmptyString(url)) {
                        openChangePinBrowser(url);
                    } else {
                        objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.unableresetpin)).show();
                    }*/
                }
            }
        }

    }

    private class refreshData extends AsyncTask<Void, Void, L2_LA_GetUserResult> {
        public refreshData() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pd != null && !pd.isShowing()) {
                pd.show();
            }
        }

        @Override
        protected L2_LA_GetUserResult doInBackground(Void... params) {
            L2_LA_GetUserResult asd = null;
            try {
                String aregid = objGlobalMethods.getRegistrationIdOnly();
                asd = WebService.L2_LA_GetUser(aregid);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at LockerProfileActivity refreshData doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final L2_LA_GetUserResult res) {
            try {
                if (res != null) {
                    Gson gson = new Gson();
                    String GetUserResultJsonStr = gson.toJson(res);
                    String[] prefName = new String[]{"GetUserResultJsonStr", "LockerUserKeyBarcode64str"};
                    String[] prefvalue = new String[]{
                            GetUserResultJsonStr,
                            ""
                    };
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                    dismissNullifyPD(pd);
                    init();
                } else {
                    dismissNullifyPD(pd);
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorretrivedata2)).show();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at LockerProfileActivity refreshData onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
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
                    objGlobalMethods.writeToFile("Bluetooth is on at LockerProfileActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LockerProfileActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }




}