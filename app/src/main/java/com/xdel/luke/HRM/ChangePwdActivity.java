package com.xdel.luke.HRM;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objChangePasswordResult;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.templogger.temploggerclass;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangePwdActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences, LocationPrefs;
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    String[] UsernamePwd = new String[4];
    String username;
    String password;
    String useridx;
    String ishomesuser;
    private File attachment;
    EditText txtOldPwd, txtNewPwd, txtcfmnewPwd;
    TextView lblChangePwdErr;
    private ProgressDialog pd, pdCheck;
    ConnectionDetector cd;
    private LinearLayout linearLayout1;
    Button btnChangePwdSubmit, btnChangePwdReset;
    private boolean DeAuthDriverResult;

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(ChangePwdActivity.this, MainActivity.class);
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

        setContentView(R.layout.activity_change_pwd);

        txtOldPwd = (EditText) findViewById(R.id.txtOldPwd);
        txtNewPwd = (EditText) findViewById(R.id.txtNewPwd);
        txtcfmnewPwd = (EditText) findViewById(R.id.txtcfmnewPwd);
        btnChangePwdSubmit = (Button) findViewById(R.id.btnChangePwdSubmit);

        lblChangePwdErr = (TextView) findViewById(R.id.lblChangePwdErr);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        linearLayout1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        pdCheck = new ProgressDialog(this);
        pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCheck.setCanceledOnTouchOutside(false);
        pdCheck.setMessage(myApp.getAppContext().getString(R.string.pd_waitCheck));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
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
                setTitle(ctx.getString(R.string.title_activity_change_pwd));

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("ChangePwdActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                JobsHandler.checkForceLogOut();

                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];

                if (username.equals("") && password.equals("")) {
                    BackToLoginPage2();
                }

                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);
                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    Toast.makeText(ctx, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                init();
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at ChangePwdActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
        super.onResume();
    }

    private void init() {
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        String changePwd_oldpwd, changePwd_newpwd, changePwd_cfmnewpwd, pwdchangeflag;
        Context ctx = myApp.getAppContext();
        try {
            changePwd_oldpwd = gcmp.getString("changePwd_oldpwd", "");
            changePwd_newpwd = gcmp.getString("changePwd_newpwd", "");
            changePwd_cfmnewpwd = gcmp.getString("changePwd_cfmnewpwd", "");
            pwdchangeflag = gcmp.getString("pwdchangeflag", "");

            txtOldPwd.setText(changePwd_oldpwd);
            txtNewPwd.setText(changePwd_newpwd);
            txtcfmnewPwd.setText(changePwd_cfmnewpwd);

            if (pwdchangeflag.equals("2")) {
                lblChangePwdErr.setText(ctx.getString(R.string.changepwd_compulsory));
                lblChangePwdErr.setVisibility(View.VISIBLE);
            } else {
                lblChangePwdErr.setText("");
                lblChangePwdErr.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ChangePwdActivity init. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ProfileActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
            try {
                try {
                    if (screenShotContentObserver != null)
                        getContentResolver().unregisterContentObserver(screenShotContentObserver);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at LeaveApplicationActivity onPause. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String pwdchangeflag = GcmPreferences.getString("pwdchangeflag", "");

        if (pwdchangeflag.equals("2")) {
            LogOutNew();
        } else {
            String[] prefName = new String[]{"changePwd_oldpwd", "changePwd_newpwd", "changePwd_cfmnewpwd"};
            String[] prefvalue = new String[]{"", "", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            Intent parentIntent = new Intent(this, ProfileActivity.class);
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        }
    }

    private void LogOutNew() {
        Context ctx = myApp.getAppContext();
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_cannotLogoutNoInternet)).show();
            // stop executing code by return
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_cfmlogout));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pdCheck.show();
                dialog.dismiss();

                String[] prefName = new String[]{"gpsLocked", "changePwd_oldpwd", "changePwd_newpwd", "changePwd_cfmnewpwd"};
                String[] prefvalue = new String[]{"", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];
                useridx = UsernamePwd[2];
                ishomesuser = UsernamePwd[3];

                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                int queCount = 0;
                queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);

                if (queCount > 0) {
                    //unable to logout due there are still ques
                    pdCheck.hide();
                    objGlobalMethods.writeToFile("Unable to log out. There are still data waiting to be sent out. Please try again later.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_cannotlogoutsendingdata)).show();
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    return;
                } else if (queCount == 0) {
                    //no que, send back all plotted locations
                    //then logout
                    if (Boolean.parseBoolean(ishomesuser)) {
                        objGlobalMethods.writeToFile("Logging out HOMES USER.", "", "", "", "");
                        //LogOutHomesWSCall();
                        checkForAttachmentToEmailOut task = new checkForAttachmentToEmailOut();
                        task.execute();
                    } else {
                        objGlobalMethods.writeToFile("Logging out.", "", "", "", "");
                        checkForAttachmentToEmailOut task = new checkForAttachmentToEmailOut();
                        task.execute();
                    }
                }
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

    private class checkForAttachmentToEmailOut extends AsyncTask<Void, Void, File> {
        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
        }

        @Override
        protected File doInBackground(Void... params) {

            try {
                Date dt = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
                String Filenamedtstr = "";
                Filenamedtstr = formatter.format(dt);
                File externalStorageDir = Environment.getExternalStorageDirectory();
                attachment = new File(externalStorageDir, Filenamedtstr + "_log.txt");
            } catch (Exception e) {
                attachment = null;
            }

            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final File success) {
            String attachmentfilename = "";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            username = UsernamePwd[0];
            password = UsernamePwd[1];
            ishomesuser = UsernamePwd[3];
            try {
                if (attachment.exists()) {
                    attachmentfilename = attachment.getAbsolutePath().toString();
                    if (!attachmentfilename.equals("")) {
                        Mail m = new Mail("luke@xdel.com", "XDel2015");
                        String versionname = objGlobalMethods.getAppVersionName(myApp.getAppContext());
                        objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, attachmentfilename, "", "Luke report from " + username
                                + ", Version: " + versionname, attachment);
                        sendmail.execute();
                    }
                }
            } catch (Exception e) {

            }

            if (Boolean.parseBoolean(ishomesuser)) {
                LogOutHomesWSCall();
            } else {
                LogOutWSCall task = new LogOutWSCall(username, password);
                task.execute();
            }
        }
    }

    private class LogOutWSCall extends AsyncTask<Void, Void, Boolean> {
        private final String LoginName;
        private final String Password;

        LogOutWSCall(String aLoginName, String aPassword) {
            LoginName = aLoginName;
            Password = aPassword;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String ARegID;
            ARegID = objGlobalMethods.getRegistrationIdOnly();
            DeAuthDriverResult = WebService.UnAuthDriver(ARegID, timestampstr, 0, 0);
            return DeAuthDriverResult;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final Boolean success) {
            try {
                pdCheck.hide();
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                useridx = UsernamePwd[2];
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.PROPERTY_REG_ID, "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "prevLoginUserIDX", useridx);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "prevLoginUserName", username);
                JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
                Intent parentIntent = new Intent(ChangePwdActivity.this, MainActivity.class);
                startActivity(parentIntent);
                JobsHandler.actFinish();
            } catch (Exception e) {
                String stack = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception caught at ChangePwdActivity LogOutWSCall onPostExecute. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
            }
        }
    }

    private void LogOutHomesWSCall() {
        Intent parentIntent;
        parentIntent = new Intent(ChangePwdActivity.this, MainActivity.class);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        LocationPrefs = objGlobalMethods.getLocationSP();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "wifistate", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "leftofc", "");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "GetAllMsgCompleted", "");
        JobsHandler.storeLogin(GcmPreferences, "", "", "", "false");
        JobsHandler.clearGPSQue(LocationPrefs);
        JobsHandler.resetAllJobsInfoRelatedPref(GcmPreferences, UpdateJobInfoPreferences);
        JobsHandler.resetAllAddressListActivityArrayList(GcmPreferences);
        objGlobalMethods.stopGpsAlarm();
        objGlobalMethods.stopPerMinRunAlarm();
        //objGlobalMethods.stopAlarmForCommon();
        if (FCMservice.mNotificationManager != null) {
            FCMservice.mNotificationManager.cancel(2);
        }
        FCMservice.notiNotRead = false;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            objGlobalMethods.stopActiveForegroundSvc();
        } else {
            if (FCMservice.mNotificationManager != null) {
                FCMservice.mNotificationManager.cancel(-1);
            }
        }
        startActivity(parentIntent);
        finish();
    }

    public void btnChangePwdCancelClicked(View v) {
        onBackPressed();
    }

    public void btnChangePwdResetClicked(View v) {
        try {
            txtOldPwd.setText("");
            txtNewPwd.setText("");
            txtcfmnewPwd.setText("");
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ChangePwdActivity btnChangePwdResetClicked. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public boolean allfieldsentered(String oldpwd, String newpwd, String cfmnewpwd) {
        boolean asd = false;
        try {
            asd = !oldpwd.isEmpty() && !newpwd.isEmpty() && !cfmnewpwd.isEmpty();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ChangePwdActivity allfieldsentered. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public boolean verifyNewPwd(String newpwd, String cfmnewpwd) {
        boolean asd = false;
        try {
            asd = newpwd.equals(cfmnewpwd);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ChangePwdActivity verifyNewPwd. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public boolean newPwdLength8Char(String cfmnewpwd) {
        boolean asd = false;
        try {
            asd = cfmnewpwd.length() >= 8;
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ChangePwdActivity newPwdLength8Char. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    public void btnChangePwdSubmitClicked(View v) {
        try {
            final String oldp = txtOldPwd.getText().toString();
            String newp = txtNewPwd.getText().toString();
            final String cnewp = txtcfmnewPwd.getText().toString();
            boolean allfieldsentered_ = allfieldsentered(oldp, newp, cnewp);
            boolean verifyNewPwd_ = verifyNewPwd(newp, cnewp);
            boolean newPwdLength8Char_ = newPwdLength8Char(cnewp);
            Context ctx = myApp.getAppContext();

            if (allfieldsentered_ && verifyNewPwd_ && newPwdLength8Char_) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(ctx.getString(R.string.changepwd_cfm_update));

                builder.setPositiveButton(ctx.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChangePassword task = new ChangePassword(oldp, cnewp);
                        task.execute();
                    }
                });

                builder.setNegativeButton(ctx.getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else if (!allfieldsentered_) {
                objGlobalMethods.writeToFile("Please enter all pwd fields.", "", "", "", "");
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.changepwd_fillall)).show();
            } else if (!verifyNewPwd_) {
                objGlobalMethods.writeToFile("Both new pwds do not match. New Pwd1: " + newp + ", New Pwd2: " + cnewp, "", "", "", "");
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.changepwd_notverified)).show();
            } else {
                objGlobalMethods.writeToFile("New pwd must be 8 or more characters. Entered: " + cnewp, "", "", "", "");
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.changepwd_length)).show();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ChangePwdActivity btnChangePwdSubmitClicked. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public class ChangePassword extends AsyncTask<Void, Void, objChangePasswordResult> {

        private final String AOldPassword;
        private final String ANewPassword;

        ChangePassword(String aAOldPassword, String aANewPassword) {
            this.AOldPassword = aAOldPassword;
            this.ANewPassword = aANewPassword;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected objChangePasswordResult doInBackground(Void... params) {
            objChangePasswordResult res = null;
            try {
                String ARegID;
                ARegID = objGlobalMethods.getRegistrationIdOnly();
                res = WebService.L2ChangePassword(ARegID, AOldPassword, ANewPassword);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at ChangePwdActivity ChangePassword doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return res;
            }
        }

        @Override
        protected void onPostExecute(final objChangePasswordResult res) {
            try {
                pd.dismiss();
                Context ctx = myApp.getAppContext();
                if (res == null) {
                    objGlobalMethods.writeToFile("ChangePwdActivity ChangePassword failed. Result is null.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.changepwd_err)).show();
                } else if (res != null && res.Status == 0) {
                    objGlobalMethods.writeToFile("ChangePwdActivity ChangePassword successful.", "", "", "", "");
                    showChangeSuccessAlert(ctx.getString(R.string.changepwd_succ), ANewPassword);
                } else if (res != null && res.Status != 0) {
                    String lang = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
                    String fullmsg = res.Message;
                    String msg_en = "";
                    String msg_zh = "";
                    String[] splitmsg = objGlobalMethods.rtnSplitStringArr(fullmsg, "\\|");
                    if (splitmsg != null && splitmsg.length > 0) {
                        msg_en = splitmsg[0] != null && !splitmsg[0].isEmpty() && !splitmsg[0].trim().equals("") ? splitmsg[0] : ctx.getString(R.string.changepwd_err);
                        if (splitmsg.length >= 2) {
                            msg_zh = splitmsg[1] != null && !splitmsg[1].isEmpty() && !splitmsg[1].trim().equals("") ? splitmsg[1] : ctx.getString(R.string.changepwd_err);
                        }
                    }
                    if (msg_en.isEmpty()) {
                        objGlobalMethods.writeToFile("ChangePwdActivity ChangePassword failed. msg_en is empty.", "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.changepwd_err)).show();
                    } else {
                        objGlobalMethods.writeToFile("ChangePwdActivity ChangePassword failed. " + msg_en, "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), lang.equals("zh") ? msg_zh : msg_en).show();
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at ChangePwdActivity ChangePassword onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            }
        }

    }

    private void showChangeSuccessAlert(String msg, final String newpwd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                try {
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(gcmp, "password", newpwd);
                    objGlobalMethods.setGCMPrefs(gcmp, "pwdchangeflag", "");
                    onBackPressed();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at ChangePwdActivity showChangeSuccessAlert. Exception: " + e.getMessage(), "", "", "", "");
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at ChangePwdActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ChangePwdActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
