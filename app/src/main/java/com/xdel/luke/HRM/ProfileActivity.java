package com.xdel.luke.HRM;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    ListView lv;
    TextView lblName, lblID;
    ImageView imgProfilePix, imgVisible;
    String[] UsernamePwd = new String[4];
    String username;
    String password;
    String ishomesuser;
    ConnectionDetector cd;
    ProfileItemAdapter adapter;
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;
    private ProgressDialog pdCheck;

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
        myApp.getInstance().initAppLanguage(getBaseContext());
        myApp.getInstance().initAppLanguage(myApp.getAppContext());
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

        setContentView(R.layout.activity_profile);

        lv = (ListView) findViewById(R.id.lv);
        lblName = (TextView) findViewById(R.id.lblName);
        lblID = (TextView) findViewById(R.id.lblID);
        imgProfilePix = (ImageView) findViewById(R.id.imgProfilePix);
        imgVisible = (ImageView) findViewById(R.id.imgVisible);

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        pdCheck = new ProgressDialog(this);
        pdCheck.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdCheck.setCanceledOnTouchOutside(false);
        pdCheck.setMessage(myApp.getAppContext().getString(R.string.pd_waitCheck));


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
                setTitle("");

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("ProfileActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                JobsHandler.checkForceLogOut();
                imgVisible.setImageResource(R.mipmap.ic_action_eye_off);


                String[] pName = new String[]{"showID", "StaffPassLoaded"};
                String[] pvalue = new String[]{"", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                password = UsernamePwd[1];

                objAuthDriverResult res = objGlobalMethods.getGCdetails(GcmPreferences);
                String GcName = res.getFullName();
                byte[] GcImg = res.getImage();
                String GcID = res.getID();
                String MaskedGcID = res.getMaskedID();

                if (username.equals("") && password.equals("")) {
                    BackToLoginPage2();
                }

                JobsHandler.clearLeaveApplicationActivityPrefs(GcmPreferences);

                lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

                //cd = new ConnectionDetector(ctx);
                cd = new ConnectionDetector(null);
                // Check if Internet present
                if (!cd.isConnectingToInternet()) {
                    //// Internet Connection is not present
                    Toast.makeText(ctx, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }

                lblName.setText(GcName);
                lblID.setText(objGlobalMethods.IsEmptyString(MaskedGcID) ? GcID : MaskedGcID);
                if (GcImg != null) {
                    imgProfilePix.setImageBitmap(BitmapFactory.decodeByteArray(GcImg, 0, GcImg.length));
                }

                String[] ppn = new String[]{"PPMainMode", "MRZInfo_PD_objGson"};
                String[] ppv = new String[]{"", ""};
                objGlobalMethods.setPassportSPMass(null, ppn, ppv);

                loadList();

            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at ProfileActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
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
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ProfileActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at ProfileActivity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
            }
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
            JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;

            Context ctx = myApp.getAppContext();
            MainActivity.uponLogin = 0;

            String AdministrativeMode = objGlobalMethods.getGCMPrefs(GcmPreferences, "AdministrativeMode");
            if (!objGlobalMethods.IsEmptyString(AdministrativeMode)) {
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "AdministrativeMode", "");
                logout();
            } else {
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ProfileActivity onBackPressed. Exception: " + e.getMessage(), "", "", "", "");
        }/* finally {
            startActivity(parentIntent);
            this.finish();
        }*/
    }

    private void logout() {
        try {
            Context ctx = myApp.getAppContext();
            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                Context c = myApp.getAppContext();
                objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_cannotLogoutNoInternet)).show();
                // stop executing code by return
                return;
            }

            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            ishomesuser = UsernamePwd[3];

            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
            builder.setCancelable(false);
            String logoutmsg = !Boolean.parseBoolean(ishomesuser) ? ctx.getString(R.string.logoutrestartphone) + "\n\n" + ctx.getString(R.string.alert_cfmlogout) : ctx.getString(R.string.alert_cfmlogout);
            builder.setMessage(logoutmsg);

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // if user pressed "yes",
                    // 1. check that there is no more ques in the backend, if not, have to wait for all the ques to be cleared b4,
                    // 2. then send back all the plotted locations,
                    // 3. then he is allowed to exit from application
                    pdCheck.show();
                    dialog.dismiss();
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    ishomesuser = UsernamePwd[3];

                    Context ctx = myApp.getAppContext();
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                    int queCount = JobsHandler.returnNumberOfQueToSend(UpdateJobInfoPreferences);

                    if (queCount > 0) {
                        //unable to logout due there are still ques
                        pdCheck.dismiss();
                        objGlobalMethods.writeToFile("Unable to log out. There are still data waiting to be sent out. Please try again later.", "", "", "", "");
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_cannotlogoutsendingdata)).show();
                        sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                        return;
                    } else if (queCount == 0) {
                        //no que, send back all plotted locations
                        //then logout
                        if (Boolean.parseBoolean(ishomesuser)) {
                            objGlobalMethods.writeToFile("Logging out HOMES USER.", "", "", "", "");
                            //LogOutHomesWSCall();
                            JobsHandler.checkForAttachmentToEmailOut task = new JobsHandler.checkForAttachmentToEmailOut(username, password);
                            task.execute();
                        } else {
                            objGlobalMethods.writeToFile("Logging out.", "", "", "", "");
                            JobsHandler.checkForAttachmentToEmailOut task = new JobsHandler.checkForAttachmentToEmailOut(username, password);
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


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at LeaveActivity logout. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void loadList(){
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            boolean reqPPSafeKeep = JobsHandler2.reqPPSafeKeep(GcmPreferences);
            String GetUserResultJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "GetUserResultJsonStr");
            Context ctx = myApp.getAppContext();
            ArrayList<objHRM_Info> infolist = new ArrayList<>();
            objHRM_Info pointsinfo = new objHRM_Info(ctx.getString(R.string.title_activity_driver_point_summary), 0);
            objHRM_Info leaveinfo = new objHRM_Info(ctx.getString(R.string.title_activity_apply_leave), 1);
            objHRM_Info changepwdinfo = new objHRM_Info(ctx.getString(R.string.title_activity_change_pwd), 2);
            objHRM_Info staffpass = new objHRM_Info(ctx.getString(R.string.title_activity_staff_pass), 3);
            objHRM_Info lockerprofile = new objHRM_Info(ctx.getString(R.string.title_activity_lockerprofile), 4);
            objHRM_Info scheduleprofile = new objHRM_Info(ctx.getString(R.string.gcscheduletitle), 5);
            objHRM_Info passportprofile = new objHRM_Info(ctx.getString(R.string.item_dashboard2_safekeep_rtn), 6);


            infolist.add(staffpass);
            infolist.add(changepwdinfo);
            infolist.add(pointsinfo);
            infolist.add(leaveinfo);
            infolist.add(scheduleprofile);
            if (reqPPSafeKeep)
                infolist.add(passportprofile);

            if (!objGlobalMethods.IsEmptyString(GetUserResultJsonStr)) {
                infolist.add(lockerprofile);
            }

            adapter = new ProfileItemAdapter(this, infolist);
            lv.setAdapter(adapter);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ProfileActivity loadList. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void toggleIDView(View v) {
        String showID = "";
        String valueToDisplay = "";
        boolean show;
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        try {
            showID = objGlobalMethods.getGCMPrefs(gcmp, "showID");
            show = !objGlobalMethods.IsEmptyString(showID) ? false : true;
            valueToDisplay = objGlobalMethods.getToggledGCID(gcmp, show, lblID.getText().toString());
            lblID.setText(valueToDisplay);
            objGlobalMethods.setGCMPrefs(gcmp, "showID", show ? "t" : "");
            imgVisible.setImageResource(show ? R.mipmap.ic_action_eye : R.mipmap.ic_action_eye_off);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ProfileActivity toggleIDView. Exception: " + e.getMessage(),"","","","");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at ProfileActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ProfileActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
