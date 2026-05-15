package com.xdel.luke.ACI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objGCLog;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.xwsBase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DigitalAgreementActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    String[] UsernamePwd = new String[4];
    String username;
    String password;
    String useridx;
    String ishomesuser;
    ConnectionDetector cd;
    WebView DAWV;
    LinearLayout LLerror, LLContent;
    TextView txtTerms;
    private boolean DeAuthDriverResult;
    private ProgressDialog pd;
    Button btnDAContinue, btnDAReject, btnDAAccept;
    String url = "https://www.xdel.com/0190492854056041KDKCX/doku.php";

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(DigitalAgreementActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
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
        setContentView(R.layout.activity_digital_agreement);

        DAWV = findViewById(R.id.DAWV);
        LLerror = findViewById(R.id.LLerror);
        LLContent = findViewById(R.id.LLContent);
        btnDAContinue = findViewById(R.id.btnDAContinue);
        btnDAReject = findViewById(R.id.btnDAReject);
        btnDAAccept = findViewById(R.id.btnDAAccept);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_wait_loading));

        txtTerms = findViewById(R.id.txtTerms);


        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
        Date createViewTime = new Date();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "DigiAgreementcreateViewTime", String.valueOf(createViewTime.getTime()));
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            Context ctx = myApp.getAppContext();
            setTitle(ctx.getString(R.string.title_activity_digitalagreement));

            JobsHandler.checkForceLogOut();

            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];

            if (username.equals("") && password.equals("")) {
                BackToLoginPage2();
            }

            //cd = new ConnectionDetector(ctx);
            cd = new ConnectionDetector(null);
            changeActiveNotiLang(username);
            if (!cd.isConnectingToInternet()) {
                //// Internet Connection is not present
                Toast.makeText(ctx, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                btnDAContinue.setVisibility(View.VISIBLE);
                btnDAReject.setVisibility(View.GONE);
                btnDAAccept.setVisibility(View.GONE);
            } else {
                init();
                /*processURL pu = new processURL();
                pu.execute();*/
            }
        }
        super.onResume();
    }

    private void changeActiveNotiLang(String name) {
        String langChangeStr = "false";
        Boolean langChange = false;
        try {
            langChangeStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "langChangeStr");
            try {
                langChange = Boolean.parseBoolean(langChangeStr);
            } catch (Exception e) {
                langChange = false;
            }

            if (langChange) {
                langChange = false;
                langChangeStr = String.valueOf(langChange);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
                //FCMservice.mNotificationManager.cancel(-1);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //objGlobalMethods.stopActiveForegroundSvc();
                    objGlobalMethods.startActiveForegroundSvc("");
                } else {
                    if (FCMservice.mNotificationManager != null) {
                        FCMservice.mNotificationManager.cancel(-1);
                        MainActivity.showActiveNotification(name);
                    }
                }
            } else {
                //do nothing
            }
        } catch (Exception e) {
            langChange = false;
            langChangeStr = String.valueOf(langChange);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
        }
    }

    private class processURL extends AsyncTask<Void,Void,String> {

        public processURL(){

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String terms = "";
            try {
                String ARegID = objGlobalMethods.getRegistrationIdOnly();

                //ARegID = "cT6HMNcoFQY:APA91bE7PNbC-R14OaTojFIr3kWtsadI9TlwfqJeWvmrK6dVlFJwecp29Kd7TMQxj_2_6-eNgrEM-rkK6URPJ9MGOx5sM0rQel_OT7w66sEM3w2m8broGeVhzCYDh39v1UvzvkHTIJcW";
                xwsBase xb = WebService.L2_GetPaymentTerms(ARegID);

                if (xb != null && !objGlobalMethods.IsEmptyString(xb.Message)) {
                    terms = xb.Message;
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity processURL doInBackground. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                return terms;
            }
        }

        @Override
        protected void onPostExecute(final String terms) {
            try {
                if (!objGlobalMethods.IsEmptyString(terms))
                    txtTerms.setText(terms);
                init();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity processURL onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        }



    }

    private void init() {
        try {
            if (!pd.isShowing()) {
                pd.show();
            }
            DAWV.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(
                        WebView view, WebResourceRequest request) {
                    DAWV.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageStarted(
                        WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    boolean succ = DAWV.getProgress() == 100;
                    LLerror.setVisibility(succ ? View.GONE : View.VISIBLE);
                    DAWV.setVisibility(succ ? View.VISIBLE : View.GONE);
                    btnDAContinue.setVisibility(succ ? View.GONE : View.VISIBLE);
                    btnDAReject.setVisibility(succ ? View.VISIBLE : View.GONE);
                    btnDAAccept.setVisibility(succ ? View.VISIBLE : View.GONE);
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
            DAWV.loadUrl(url);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity init. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);

            if (pd != null) {
                pd.dismiss();
                pd = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_digitalagreement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            //init();
            processURL pu = new processURL();
            pu.execute();
        } else if (item.getItemId() == R.id.digiALanguage) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
            if (langType.equals("en") || langType.equals("")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "zh");
            } else if (langType.equals("zh")) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "en");
            }

            Boolean langChange = true;
            String langChangeStr = String.valueOf(langChange);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
            this.recreate();
        }

        /*switch (item.getItemId()) {
            case R.id.refresh:
                //init();
                processURL pu = new processURL();
                pu.execute();
                break;
            case R.id.digiALanguage:
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
                if (langType.equals("en") || langType.equals("")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "zh");
                } else if (langType.equals("zh")) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "language", "en");
                }

                Boolean langChange = true;
                String langChangeStr = String.valueOf(langChange);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "langChangeStr", langChangeStr);
                this.recreate();
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //do nothing
        super.onBackPressed();
    }

    private objGCLog getobjGCLog(SharedPreferences gcmp, long l, int flag) {
        objGCLog o = null;
        try {
            String DigiAgreementcreateViewTime = objGlobalMethods.getGCMPrefs(gcmp, "DigiAgreementcreateViewTime");
            String durationStr = !objGlobalMethods.IsEmptyString(DigiAgreementcreateViewTime) &&
                    !DigiAgreementcreateViewTime.equalsIgnoreCase("0") ? objGlobalMethods.getDuration(Long.parseLong(DigiAgreementcreateViewTime)) : "Unknown";
            String durationStr2 = "View Duration: " + durationStr;
            String AUTC = "/Date(" + l + ")/";
            String ARemarks = (flag == 32 ? "Accepted DA" : "Rejected DA") + "\n" + durationStr2;
            o = new objGCLog(ARemarks, flag, AUTC, durationStr2);
        } catch (Exception e) {
            o = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity getobjGCLog. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return o;
        }
    }

    public void btnDAAccept_Click(View v) {
        try {
            DAAccept daa = new DAAccept();
            daa.execute();
            /*objGlobalMethods.writeToFile("Accepted DA.", "", "", "", "");
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            SharedPreferences updatep = objGlobalMethods.getUpdateJobInfoPreferences();
            Date now = new Date();
            long l = now.getTime();
            String lastDigiAgreementTSJsonStr = String.valueOf(l);
            objGlobalMethods.setGCMPrefs(gcmp, "lastDigiAgreementTSJsonStr", lastDigiAgreementTSJsonStr);

            objGCLog gclog = getobjGCLog(gcmp, l, 32);
            if (gclog == null) {
                objAuthDriverResult dr = objGlobalMethods.getGCdetails(gcmp);
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                SimpleDateFormat datetimeformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String nowddMMyyyy = datetimeformat.format(now);
                String gcName = (dr != null && !objGlobalMethods.IsEmptyString(dr.FullName) ? dr.FullName : !objGlobalMethods.IsEmptyString(username) ? username : "NA");
                String emailbody = nowddMMyyyy + "\n" + gcName + " accepted DA prompted on LUKE.";
                String[] emailRecipients = {"it@xdel.com", "aci@xdel.com"};
                //String[] emailRecipients = {"kean.ong@xdel.com"};
                objGlobalMethods.prepareSendEmail(emailRecipients, "Unable to save log for " + gcName, emailbody, "", false, "Unable to save log for " + gcName);
            } else {
                JobsHandler.addGCLogQue(updatep, gclog);
            }
            Context ctx = myApp.getAppContext();
            objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at DigitalAgreementActivity btnDAAccept_Click.", "", "", "", "");
            Intent SendQueuedEmailReceiver = new Intent(ctx, SendQueuedEmail_Receiver.class);
            sendBroadcast(SendQueuedEmailReceiver);
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

            final Intent intObj = new Intent(DigitalAgreementActivity.this, (objGlobalMethods.IsDefaultVRPpage() ? DrawerMain2Activity.class : DrawerMainActivity.class));
            startActivity(intObj);
            this.finish();*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity btnDAAccept_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class DAAccept extends AsyncTask<Void, Void, Boolean> {
        DAAccept(){

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pd == null) {
                pd = new ProgressDialog(DigitalAgreementActivity.this);
            }
            if (pd != null && !pd.isShowing()) {
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage(myApp.getAppContext().getString(R.string.pd_wait_loading));
                pd.show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            Context ctx = myApp.getAppContext();
            try {
                objGlobalMethods.writeToFile("Accepted DA.", "", "", "", "");
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                SharedPreferences updatep = objGlobalMethods.getUpdateJobInfoPreferences();
                Date now = new Date();
                long l = now.getTime();
                String lastDigiAgreementTSJsonStr = String.valueOf(l);
                objGlobalMethods.setGCMPrefs(gcmp, "lastDigiAgreementTSJsonStr", lastDigiAgreementTSJsonStr);

                objGCLog gclog = getobjGCLog(gcmp, l, 32);
                if (gclog == null) {
                    objAuthDriverResult dr = objGlobalMethods.getGCdetails(gcmp);
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    SimpleDateFormat datetimeformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String nowddMMyyyy = datetimeformat.format(now);
                    String gcName = (dr != null && !objGlobalMethods.IsEmptyString(dr.FullName) ? dr.FullName : !objGlobalMethods.IsEmptyString(username) ? username : "NA");
                    String emailbody = nowddMMyyyy + "\n" + gcName + " accepted DA prompted on LUKE.";
                    String[] emailRecipients = {"it@xdel.com", "aci@xdel.com"};
                    //String[] emailRecipients = {"kean.ong@xdel.com"};
                    objGlobalMethods.prepareSendEmail(emailRecipients, "Unable to save log for " + gcName, emailbody, "", false, "Unable to save log for " + gcName);
                    sendBroadcast(new Intent(ctx, SendQueuedEmail_Receiver.class));
                } else {
                    JobsHandler.addGCLogQue(updatep, gclog);
                }
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                asd = true;
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity DAAccept doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                if (pd != null) {
                    pd.dismiss();
                    pd = null;
                }
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                final Intent intObj = new Intent(DigitalAgreementActivity.this, (objGlobalMethods.IsDefaultVRPpage() && !IsMaintenanceHours ? DrawerMain2Activity.class : DrawerMainActivity.class));
                startActivity(intObj);
                DigitalAgreementActivity.this.finish();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity DAAccept onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    private class DAReject extends AsyncTask<Void, Void, Boolean> {
        DAReject() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pd == null) {
                pd = new ProgressDialog(DigitalAgreementActivity.this);
            }
            if (pd != null && !pd.isShowing()) {
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage(myApp.getAppContext().getString(R.string.pd_loggingout));
                pd.show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            try {
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                SharedPreferences updatep = objGlobalMethods.getUpdateJobInfoPreferences();
                objGlobalMethods.writeToFile("Rejected DA.", "", "", "", "");
                objAuthDriverResult dr = objGlobalMethods.getGCdetails(gcmp);
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                SimpleDateFormat datetimeformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date now = new Date();
                long l = now.getTime();
                String nowddMMyyyy = datetimeformat.format(now);
                String gcName = (dr != null && !objGlobalMethods.IsEmptyString(dr.FullName) ? dr.FullName : !objGlobalMethods.IsEmptyString(username) ? username : "NA");
                objGCLog gclog = getobjGCLog(gcmp, l, 64);
                if (gclog != null) {
                    String emailbody = nowddMMyyyy + "\n" + gcName + "\n" + gclog.ARemarks;
                    String[] emailRecipients = {"it@xdel.com", "aci@xdel.com"};
                    //String[] emailRecipients = {"kean.ong@xdel.com"};
                    objGlobalMethods.prepareSendEmail(emailRecipients, "DA Rejection!", emailbody, "", false, "DA Rejection!");
                    objGlobalMethods.writeToFile("GC rejected DA. Set Force Logout true, send email to ACI and log GC out of LUKE.", "", "", "", "");
                    objGlobalMethods.setGCMPrefs(gcmp, "forceLogOutBooleanString", "true");
                    JobsHandler.addGCLogQue(updatep, gclog);
                } else {
                    String DigiAgreementcreateViewTime = objGlobalMethods.getGCMPrefs(gcmp, "DigiAgreementcreateViewTime");
                    String durationStr = !objGlobalMethods.IsEmptyString(DigiAgreementcreateViewTime) &&
                            !DigiAgreementcreateViewTime.equalsIgnoreCase("0") ? objGlobalMethods.getDuration(Long.parseLong(DigiAgreementcreateViewTime)) : "Unknown";
                    String durationStr2 = "View Duration: " + durationStr;
                    String emailbody = nowddMMyyyy + "\n" + gcName + " rejected the DA prompted on LUKE.\n" + durationStr2;
                    String[] emailRecipients = {"it@xdel.com", "aci@xdel.com"};
                    //String[] emailRecipients = {"kean.ong@xdel.com"};
                    objGlobalMethods.prepareSendEmail(emailRecipients, "Unable to save log for " + gcName, emailbody, "", false, "Unable to save log for " + gcName);
                }

                //Context ctx = myApp.getAppContext();
                //objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at DigitalAgreementActivity btnDAReject_Click.", "", "", "", "");
                //sendBroadcast(new Intent(ctx, SendQueuedEmail_Receiver.class));
                //sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                //objGlobalMethods.setGCMPrefs(gcmp, "agreementrejected", "t");
                objGlobalMethods.setGCMPrefs(gcmp, "lastDigiAgreementTSJsonStr", "");
                JobsHandler.backendRun(gcmp, updatep);
                asd = true;
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity DAReject doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            /*try {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pd != null) {
                            try {
                                final Handler han = new Handler(Looper.getMainLooper());
                                han.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        //SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                                        //objGlobalMethods.setGCMPrefs(gcmp, "agreementrejected", "t");
                                        Intent intent = new Intent(DigitalAgreementActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        DigitalAgreementActivity.this.finish();
                                    }
                                }, 1000);
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity DAReject onPostExecute dismissing pd. Exception: " + e.getMessage(), "", "", "", "");
                                pd = null;
                            }
                        }

                    }
                }, 4000);
            } catch (Exception e) {
                if (pd != null) {
                    pd.dismiss();
                }
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity DAReject onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                DigitalAgreementActivity.this.finish();
            }*/
        }
    }

    public void btnDAReject_Click(View v) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            String msg = myApp.getAppContext().getString(R.string.rejectagreement);
            builder.setMessage(msg);
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DAReject t = new DAReject();
                    t.execute();
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
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity btnDAReject_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnDAContinue_Click(View v) {
        try {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.writeToFile("btnDAContinue_Click clicked at DigitalAgreementActivity.", "", "", "", "");
            Intent intent = new Intent(DigitalAgreementActivity.this, DrawerMainActivity.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(intent);
            this.finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DigitalAgreementActivity btnDAContinue_Click. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

}