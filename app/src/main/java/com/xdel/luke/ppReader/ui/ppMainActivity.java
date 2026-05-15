package com.xdel.luke.ppReader.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.xdel.luke.BottomSheet.ProgressBarDialog;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.ProfileActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.LocaleHelper;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.xwsBase;
import com.xdel.luke.ppReader.util.DateUtil;
import com.xdel.luke.ppReader.model.DocType;
import com.xdel.luke.ppReader.model.PersonDetails;

import org.jmrtd.lds.icao.MRZInfo;

import static com.xdel.luke.ppReader.ui.CaptureActivity.DOC_TYPE;
import static com.xdel.luke.ppReader.ui.CaptureActivity.MRZ_RESULT;

import java.util.Date;
import java.util.Locale;


/*
codes taken from sample project EPassportNFCReader
com.alimert.passportreader
*/


public class ppMainActivity extends AppCompatActivity {
    private SharedPreferences g, pps;
    ConnectionDetector cd;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;

    //private static final String TAG = ppMainActivity.class.getSimpleName();

    private static final int APP_CAMERA_ACTIVITY_REQUEST_CODE = 150;
    //private static final int APP_SETTINGS_ACTIVITY_REQUEST_CODE = 550;

    private TextView tvResult, lblUserIDX, lblmode, lblIsHomesUser;

    PersonDetails pd;
    private String mrzInfoStr;
    private String passportNumber, expirationDate , birthDate, name, documentCode, issuingState, primaryIdentifier, secondaryIdentifier, nationality, documentNumber, gender, optionalData1, optionalData2;
    private DocType docType;
    //private ProgressDialog pdp;
    private ProgressBarDialog pdp;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private Button btnppSafekeep, btnppReturn;

    private MediaPlayer player;
    private Uri errToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error);
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    boolean oncreated = false;
    boolean resulted = false;

    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ppMainActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at ppMainActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at ppMainActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        langType = objGlobalMethods.getGCMPrefs(g, "language");
        myApp.getInstance().initAppLanguage(getBaseContext());
        myApp.getInstance().initAppLanguage(myApp.getAppContext());
        setContentView(R.layout.activity_pp_main);

        lblIsHomesUser = findViewById(R.id.lblIsHomesUser);
        lblmode = findViewById(R.id.lblmode);
        lblUserIDX = findViewById(R.id.lblUserIDX);
        tvResult = findViewById(R.id.text_result);
        btnppSafekeep = findViewById(R.id.btnppSafekeep);
        btnppReturn = findViewById(R.id.btnppReturn);

        //oncreated = true;
        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            Context ctx = myApp.getAppContext();
            try {
                Locale current = getResources().getConfiguration().getLocales().get(0);

                if (!resulted) {
                    g = objGlobalMethods.getGcmPreferences();
                    pps = objGlobalMethods.getPassportSP();
                    if (!objGlobalMethods.UserCanScreenShot(g)) {
                        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                    }
                    JobsHandler.currentActivity = this;
                    myApp.currentActivity = this;

                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(g);
                    username = UsernamePwd[0];
                    password = UsernamePwd[1];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];
                    lblUserIDX.setText(useridx);
                    boolean isHomesUserMode = objGlobalMethods.isHomesUserMode(ishomesuser);
                    //setTitle(ctx.getString(R.string.item_dashboard2ppsk));
                    setTitle("");

                    cd = new ConnectionDetector(null);

                    if (!cd.isConnectingToInternet()) {
                        // Internet Connection is not present
                        Toast.makeText(ctx, ctx.getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                    }

                    initPage(isHomesUserMode);

                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ppMainActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }
    }

    private void initPage(boolean isHomesUser) {
        try {
            if (pps == null)
                pps = objGlobalMethods.getPassportSP();
            if (isHomesUser) {
                btnppSafekeep.setVisibility(View.VISIBLE);
                btnppReturn.setVisibility(View.VISIBLE);
                String PPMainMode = objGlobalMethods.getPassportSP_KeyValue(pps, "PPMainMode");
                toggle(objGlobalMethods.IsEmptyString(PPMainMode) || objGlobalMethods.IsEqualString(PPMainMode, "0") ? 0 : 1);
            } else {
                tvResult.setText("");
                lblmode.setText("-1");
                objGlobalMethods.setPassportSP_KeyValue(pps, "PPMainMode" ,"-1");
                btnppSafekeep.setVisibility(View.GONE);
                btnppReturn.setVisibility(View.GONE);
                ctrlSettings.enableDisableButtonWithStyles(false, btnppSafekeep);
                ctrlSettings.enableDisableButtonWithStyles(false, btnppReturn);
            }

            String MRZInfo_PD_objGson = objGlobalMethods.getPassportSP_KeyValue(pps, "MRZInfo_PD_objGson");
            if (!objGlobalMethods.IsEmptyString(MRZInfo_PD_objGson)) {
                objGlobalMethods.setPassportSP_KeyValue(pps, "MRZInfo_PD_objGson", "");
                process(MRZInfo_PD_objGson);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity initPage. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void toggle(int PPMainMode) {
        //PPMainMode empty or 0 check in
        //PPMainMode 1 check out
        try {
            tvResult.setText("");
            lblmode.setText(String.valueOf(PPMainMode));
            objGlobalMethods.setPassportSP_KeyValue(pps, "PPMainMode", String.valueOf(PPMainMode));
            ctrlSettings.tottleButtonsWithStyles(PPMainMode == 0, btnppSafekeep);
            ctrlSettings.tottleButtonsWithStyles(PPMainMode == 1, btnppReturn);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity toggle. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ppMainActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            g = null;
            stopPlayingTone();
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ppMainActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        /*boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);*/

        String PPMainMode = "";
        if (lblmode != null)
            PPMainMode = lblmode.getText().toString();

        if (objGlobalMethods.IsEmptyString(PPMainMode)) {
            PPMainMode = objGlobalMethods.getPassportSP_KeyValue(pps, "PPMainMode");
        }

        Intent parentIntent = new Intent(this, (PPMainMode.equalsIgnoreCase("-1")) ? ProfileActivity.class : DrawerMainActivity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    private void setMrzData(MRZInfo m) {
        try {
            pd = new PersonDetails();
            pd.setName(m.getSecondaryIdentifier().replace("<", " ").trim());
            pd.setSurname(m.getPrimaryIdentifier().replace("<", " ").trim());
            pd.setPersonalNumber(m.getPersonalNumber());
            pd.setGender(m.getGender().toString());
            pd.setBirthDate(DateUtil.convertFromMrzDate(m.getDateOfBirth()));
            pd.setExpiryDate(DateUtil.convertFromMrzDate(m.getDateOfExpiry()));
            pd.setSerialNumber(m.getDocumentNumber());
            pd.setNationality(m.getNationality());
            pd.setIssuerAuthority(m.getIssuingState());
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity setMrzData. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void setResultFromScan() {
        String result = "";
        try {
            if (pd != null) {
                result = "mrzInfo: \n" + mrzInfoStr + "\n\n";
                result += "Name: " + pd.getName() + "\n";
                result += "Surname: " + pd.getSurname() + "\n";
                result += "PersonalNumber: " + pd.getPersonalNumber() + "\n";
                result += "Gender: " + pd.getGender().toString() + "\n";
                result += "BirthDate: " + pd.getBirthDate() + "\n";
                result += "ExpiryDate: " + pd.getExpiryDate() + "\n";
                result += "SerialNumber: " + pd.getSerialNumber() + "\n";
                result += "Nationality: " + pd.getNationality() + "\n";
                result += "IssuingState: " + pd.getIssuerAuthority() + "\n";
            }

            tvResult.setText(!objGlobalMethods.IsEmptyString(result) ? result : "");

            /*mainLayout.setVisibility(View.VISIBLE);
            imageLayout.setVisibility(View.GONE);*/

        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at ppMainActivity setResultFromScan. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public class MRZInfo_PD_obj {
        public MRZInfo mrzInfo;
        public PersonDetails pd;
        public String useridx;
        public boolean validuidx;
        public int mode;
        //-1  Confirm_Collected_Passport
        //0 CheckIn_Passport
        //1 CheckOut_Passport_By_Ops

        public MRZInfo_PD_obj(MRZInfo amrzInfo, PersonDetails apd, int amode, String auseridx, boolean avaliduidx) {
            this.mrzInfo = amrzInfo;
            this.pd = apd;
            this.mode = amode;
            this.useridx = auseridx;
            this.validuidx = avaliduidx;
        }
    }

    private void process(String MRZInfo_PD_objGson) {
        try {
            if (!objGlobalMethods.IsEmptyString(MRZInfo_PD_objGson)) {
                Gson gson = new Gson();
                MRZInfo_PD_obj mpo = gson.fromJson(MRZInfo_PD_objGson, MRZInfo_PD_obj.class);
                if (mpo != null) {
                    MRZInfo mrzInfo = mpo.mrzInfo;
                    pd = mpo.pd;
                    boolean validuidx;
                    validuidx = mpo.validuidx;
                    useridx = mpo.useridx;
                    int mode = mpo.mode;

                    if (pd != null) {
                        String pp = pd.getSerialNumber();
                        Date exp = DateUtil.convertFromMrzDate2(mrzInfo.getDateOfExpiry());

                        Long l = exp.getTime();
                        String timestampstr = "/Date(" + l + ")/";
                        Date currDay = new Date();
                        Long currDayl = currDay.getTime();
                        boolean expired = l < currDayl;

                        if (validuidx) {
                            if (mode == -1) {
                                Confirm_Collected_Passport cp = new Confirm_Collected_Passport(pp, timestampstr);
                                cp.execute();
                            } else if (mode == 0) {
                                if (!expired) {
                                    CheckIn_Passport cp = new CheckIn_Passport(useridx, pp, timestampstr);
                                    cp.execute();
                                } else if (expired) {
                                    playTone();
                                    Context c = myApp.getAppContext();
                                    tvResult.setText(c.getString(R.string.txt_invalid));
                                }
                            } else {
                                CheckOut_Passport_By_Ops cp = new CheckOut_Passport_By_Ops(useridx, pp, timestampstr);
                                cp.execute();
                            }
                        } else {
                            String msg = "Unable to detect UserIDX.\nPlease reload this page again.";
                            promptBack(msg);
                        }
                    } else {
                        playTone();
                        Context c = myApp.getAppContext();
                        tvResult.setText(c.getString(R.string.alert_error));
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity process. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            resulted = true;
            Locale current = getResources().getConfiguration().getLocales().get(0);

            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case APP_CAMERA_ACTIVITY_REQUEST_CODE:
                        MRZInfo mrzInfo = (MRZInfo) data.getSerializableExtra(MRZ_RESULT);
                        if(mrzInfo != null) {
                            mrzInfoStr = mrzInfo.toString();
                            setMrzData(mrzInfo);
                            //setResultFromScan();

                            if (pd != null) {
                                /*mrzInfo = new MRZInfo(mrzData); //scanned info replaced by test info in this line
                                setMrzData(mrzInfo);*/
                                useridx = lblUserIDX.getText().toString();
                                boolean validuidx = !objGlobalMethods.IsEmptyString(useridx);
                                String PPMainMode = "";
                                if (lblmode != null)
                                    PPMainMode = lblmode.getText().toString();

                                if (objGlobalMethods.IsEmptyString(PPMainMode)) {
                                    PPMainMode = objGlobalMethods.getPassportSP_KeyValue(pps, "PPMainMode");
                                }

                                int mode = PPMainMode.equalsIgnoreCase("-1") ? -1 : PPMainMode.equalsIgnoreCase("0") ? 0 : 1;

                                MRZInfo_PD_obj mpo = new MRZInfo_PD_obj(mrzInfo, pd, mode, useridx, validuidx);
                                Gson gson = new Gson();
                                String MRZInfo_PD_objGson = gson.toJson(mpo);
                                pps = objGlobalMethods.getPassportSP();
                                objGlobalMethods.setPassportSP_KeyValue(pps, "MRZInfo_PD_objGson", MRZInfo_PD_objGson);
                                this.recreate();
                            } else {
                                objGlobalMethods.setPassportSP_KeyValue(pps, "MRZInfo_PD_objGson", "");
                                this.recreate();
                            }

                            /*if (pd != null) {
                                mrzInfo = new MRZInfo(mrzData); //scanned info replaced by test info in this line
                                setMrzData(mrzInfo);

                                String pp = pd.getSerialNumber();
                                Date exp = DateUtil.convertFromMrzDate2(mrzInfo.getDateOfExpiry());

                                Long l = exp.getTime();
                                String timestampstr = "/Date(" + l + ")/";
                                Date currDay = new Date();
                                Long currDayl = currDay.getTime();
                                boolean expired = l < currDayl;
                                useridx = lblUserIDX.getText().toString();
                                boolean validuidx = !objGlobalMethods.IsEmptyString(useridx);
                                String PPMainMode = "";
                                if (lblmode != null)
                                    PPMainMode = lblmode.getText().toString();

                                if (objGlobalMethods.IsEmptyString(PPMainMode)) {
                                    PPMainMode = objGlobalMethods.getPassportSP_KeyValue(pps, "PPMainMode");
                                }

                                int mode = PPMainMode.equalsIgnoreCase("-1") ? -1 : PPMainMode.equalsIgnoreCase("0") ? 0 : 1;

                                MRZInfo_PD_obj mpo = new MRZInfo_PD_obj(mrzInfo, pd, mode, useridx, validuidx);
                                Gson gson = new Gson();
                                String MRZInfo_PD_objGson = gson.toJson(mpo);
                                pps = objGlobalMethods.getPassportSP();
                                objGlobalMethods.setPassportSP_KeyValue(pps, "MRZInfo_PD_objGson", MRZInfo_PD_objGson);


                                if (validuidx) {
                                    if (PPMainMode.equalsIgnoreCase("-1")) {
                                        Confirm_Collected_Passport cp = new Confirm_Collected_Passport(pp, timestampstr);
                                        cp.execute();
                                    } else if (PPMainMode.equalsIgnoreCase("0")) {
                                        if (!expired) {
                                            CheckIn_Passport cp = new CheckIn_Passport(pp, timestampstr);
                                            cp.execute();
                                        } else if (expired) {
                                            playTone();
                                            Context c = myApp.getAppContext();
                                            tvResult.setText(c.getString(R.string.txt_invalid));
                                        }
                                    } else {
                                        CheckOut_Passport_By_Ops cp = new CheckOut_Passport_By_Ops(useridx, pp, timestampstr);
                                        cp.execute();
                                    }
                                } else {
                                    String msg = "Unable to detect UserIDX.\nPlease reload this page again.";
                                    promptBack(msg);
                                }
                            } else {
                                playTone();
                                Context c = myApp.getAppContext();
                                tvResult.setText(c.getString(R.string.alert_error));
                            }*/
                        }
                        break;
                    default:
                        break;
                }
            } else {
                this.recreate();
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at ppMainActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void requestPermissionForCamera() {

        /*String[] permissions = { Manifest.permission.CAMERA };
        boolean isPermissionGranted = PermissionUtil.hasPermissions(this, permissions);

        if (!isPermissionGranted) {
            AppUtil.showAlertDialog(this, getString(R.string.permission_title), getString(R.string.permission_description), getString(R.string.button_ok), false, (dialogInterface, i) -> ActivityCompat.requestPermissions(this, permissions, PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS));
        } else {
            openCameraActivity();
        }*/

    }

    private void openCameraActivity() {
        try {
            tvResult.setText("");
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra(DOC_TYPE, DocType.PASSPORT);
            startActivityForResult(intent, APP_CAMERA_ACTIVITY_REQUEST_CODE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity openCameraActivity. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnppScan_Click(View v) {
        try {
            openCameraActivity();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity btnppScan_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnpptestVRead_Click(View v) {
        try {
            readCard();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity btnppScan_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnpptestVReadOut_Click(View v) {
        try {
            readCardOut();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity btnppScan_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnpptestVRead2_Click(View v) {
        try {
            readCard2();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity btnppScan_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    String mrzData = "PASGPONG<<KHAI<KHIN<<<<<<<<<<<<<<<<<<<<<<<<<" +
            "F235435043MYS0201014M2611309S8501550H<<<<<50";

    private void readCard() {

        /*String mrzData = "P<GBPANGELA<ZOE<<SMITH<<<<<<<<<<<<<<<<<<<<<<" +
                        "9990727768GBR7308196F2807041<<<<<<<<<<<<<<02";*/

        /*String mrzData = "P<IRLOSULLIVAN<<LAUREN<<<<<<<<<<<<<<<<<<<<<<" +
                "XN50042162IRL8805049F2309154<<<<<<<<<<<<<<<6";*/

        /*String mrzData = "PASGPONG<<KHAI<KHIN<<<<<<<<<<<<<<<<<<<<<<<<<" +
                "K4487830N5SGP8501134M3310193S8501550H<<<<<50";*/

        MRZInfo m = new MRZInfo(mrzData);
        setMrzData(m);
        //setResultFromScan();
        tvResult.setText("");
        if (pd != null) {
            String pp = pd.getSerialNumber();
            Date exp = DateUtil.convertFromMrzDate2(m.getDateOfExpiry());
            Long l = exp.getTime();
            String timestampstr = "/Date(" + l + ")/";
            Date currDay = new Date();
            Long currDayl = currDay.getTime();
            boolean expired = l < currDayl;

            useridx = lblUserIDX.getText().toString();
            if (!expired) {
                CheckIn_Passport cp = new CheckIn_Passport(useridx, pp, timestampstr);
                cp.execute();
            } else {
                playTone();
                Context c = myApp.getAppContext();
                tvResult.setText(c.getString(R.string.pp_expired));
            }
        }
    }

    private void readCardOut() {
        MRZInfo mrzInfo = new MRZInfo(mrzData);
        setMrzData(mrzInfo);
        //setResultFromScan();
        tvResult.setText("");
        if (pd != null) {
            String pp = pd.getSerialNumber();
            Date exp = DateUtil.convertFromMrzDate2(mrzInfo.getDateOfExpiry());
            Long l = exp.getTime();
            String timestampstr = "/Date(" + l + ")/";

            useridx = lblUserIDX.getText().toString();
            boolean validuidx = !objGlobalMethods.IsEmptyString(useridx);
            if (validuidx) {
                CheckOut_Passport_By_Ops cp = new CheckOut_Passport_By_Ops(useridx, pp, timestampstr);
                cp.execute();
            } else {
                String msg = "Unable to detect UserIDX.\nPlease reload this page again.";
                promptBack(msg);
            }
        }
    }

    private void readCard2() {

        MRZInfo m = new MRZInfo(mrzData);
        setMrzData(m);
        //setResultFromScan();
        tvResult.setText("");
        if (pd != null) {
            String pp = pd.getSerialNumber();
            Date exp = DateUtil.convertFromMrzDate2(m.getDateOfExpiry());
            Long l = exp.getTime();
            String timestampstr = "/Date(" + l + ")/";
            Date currDay = new Date();
            Long currDayl = currDay.getTime();
            boolean expired = l < currDayl;

            Confirm_Collected_Passport cp = new Confirm_Collected_Passport(pp, timestampstr);
            cp.execute();

            /*if (!expired) {
                Confirm_Collected_Passport cp = new Confirm_Collected_Passport(pp, timestampstr);
                cp.execute();
            } else {
                playTone();
                Context c = myApp.getAppContext();
                tvResult.setText(c.getString(R.string.pp_expired));
            }*/
        }
    }

    public void btnppCANCEL_Click(View v) {
        try {
            back();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity btnppCANCEL_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnppSafekeep_Click(View v) {
        try {
            if (pps == null)
                pps = objGlobalMethods.getPassportSP();
            toggle(0);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity btnppSafekeep_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void btnppReturn_Click(View v) {
        try {
            if (pps == null)
                pps = objGlobalMethods.getPassportSP();
            objGlobalMethods.setPassportSP_KeyValue(pps, "PPMainMode", "1");
            toggle(1);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ppMainActivity btnppReturn_Click. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private Dialog promptBack(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                dialog.dismiss();
                back();
            }
        });

        return builder.show();
    }

    private class CheckIn_Passport extends AsyncTask<Void, Void, xwsBase> {
        private final String APassportNumber;
        private final String AExpiryDate;
        private final String AUserIDX;

        public CheckIn_Passport(String aAUserIDX, String aAPassportNumber, String aAExpiryDate) {
            this.AUserIDX = aAUserIDX;
            this.APassportNumber = aAPassportNumber;
            this.AExpiryDate = aAExpiryDate;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            showPD();
        }

        @Override
        protected xwsBase doInBackground(Void... params) {
            xwsBase x = null;
            try {
                x = WebService.CheckIn_Passport(Long.parseLong(AUserIDX), APassportNumber, AExpiryDate);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ppMainActivity CheckIn_Passport doInBackground. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                return x;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final xwsBase x) {
            Context c = myApp.getAppContext();
            try {
                dismissPD();
                if (x != null && x.Status == 0) {
                    playSuccessVerifiedTone();
                    tvResult.setText(c.getString(R.string.ok));
                } else {
                    playTone();
                    Locale current = getResources().getConfiguration().getLocales().get(0);
                    //tvResult.setText(c.getString(R.string.txt_invalid));
                    String msg = x.Message;
                    if (!objGlobalMethods.IsEmptyString(msg) && msg.toLowerCase().contains("passport")) {
                        msg = msg.replace("Passport", "Item");
                        msg = msg.replace("passport", "item");
                        msg = msg.replace("PASSPORT", "ITEM");
                    }

                    tvResult.setText(msg);
                }
            } catch (Exception e) {
                playTone();
                tvResult.setText(c.getString(R.string.alert_error) + "\n\n" + e.getMessage());
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ppMainActivity CheckIn_Passport onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }
    }

    private class CheckOut_Passport_By_Ops extends AsyncTask<Void, Void, xwsBase> {
        private final String APassportNumber;
        private final String AExpiryDate;
        private final String AUserIDX;

        public CheckOut_Passport_By_Ops(String aAUserIDX, String aAPassportNumber, String aAExpiryDate) {
            this.AUserIDX = aAUserIDX;
            this.APassportNumber = aAPassportNumber;
            this.AExpiryDate = aAExpiryDate;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            showPD();
        }

        @Override
        protected xwsBase doInBackground(Void... params) {
            xwsBase x = null;
            try {
                x = WebService.CheckOut_Passport_By_Ops(Long.parseLong(AUserIDX), APassportNumber, AExpiryDate);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ppMainActivity CheckOut_Passport_By_Ops doInBackground. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                return x;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final xwsBase x) {
            Context c = myApp.getAppContext();
            try {
                dismissPD();
                if (x != null && x.Status == 0) {
                    playSuccessVerifiedTone();
                    tvResult.setText(c.getString(R.string.ok));
                } else {
                    playTone();
                    //tvResult.setText(c.getString(R.string.txt_invalid));

                    String msg = x.Message;
                    if (!objGlobalMethods.IsEmptyString(msg) && msg.toLowerCase().contains("passport")) {
                        msg = msg.replace("Passport", "Item");
                        msg = msg.replace("passport", "item");
                        msg = msg.replace("PASSPORT", "ITEM");
                    }

                    tvResult.setText(msg);
                }
            } catch (Exception e) {
                playTone();
                tvResult.setText(c.getString(R.string.alert_error) + "\n\n" + e.getMessage());
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ppMainActivity CheckOut_Passport_By_Ops onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }
    }

    private class Confirm_Collected_Passport extends AsyncTask<Void, Void, xwsBase> {
        private final String APassportNumber;
        private final String AExpiryDate;

        public Confirm_Collected_Passport(String aAPassportNumber, String aAExpiryDate) {
            this.APassportNumber = aAPassportNumber;
            this.AExpiryDate = aAExpiryDate;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            showPD();
        }

        @Override
        protected xwsBase doInBackground(Void... params) {
            xwsBase x = null;
            try {
                String regid = objGlobalMethods.getRegistrationIdOnly();
                //regid = "fdQlLSx7gns:APA91bGIqy4MWOOaHCF4py5lvLVdwBD1YJQ-fMoH_CkvxKzRbDT8M58NDpbjo8BhXepwr3HtjRhy4lb5xsP4Y0mzoBlqyVXdkF3RgoBHAKUXd1JdvPgDfuf3WLTHQ4q-uWGGPGktrn8T";
                x = WebService.Confirm_Collected_Passport(regid, APassportNumber, AExpiryDate);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ppMainActivity Confirm_Collected_Passport doInBackground. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            } finally {
                return x;
            }
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(final xwsBase x) {
            Context c = myApp.getAppContext();
            try {
                dismissPD();
                if (x != null && x.Status == 0) {
                    playSuccessVerifiedTone();
                    tvResult.setText(c.getString(R.string.ok));
                } else {
                    playTone();
                    //tvResult.setText(c.getString(R.string.txt_invalid));

                    String msg = x.Message;
                    if (!objGlobalMethods.IsEmptyString(msg) && msg.toLowerCase().contains("passport")) {
                        msg = msg.replace("Passport", "Item");
                        msg = msg.replace("passport", "item");
                        msg = msg.replace("PASSPORT", "ITEM");
                    }

                    tvResult.setText(msg);
                }
            } catch (Exception e) {
                playTone();
                tvResult.setText(c.getString(R.string.alert_error) + "\n\n" + e.getMessage());
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at ppMainActivity Confirm_Collected_Passport onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
            }
        }
    }

    public void showPD() {
        try {
            pdp = new ProgressBarDialog(myApp.currentActivity);
            pdp.setCanceledOnTouchOutside(false);
            pdp.setCancelable(false);
            pdp.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
            pdp.show();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ppMainActivity showPD. Exception: " + s, "", "", "", "");
        }
    }

    public void dismissPD() {
        try {
            if (pdp != null)
                pdp.dismiss();
            pdp = null;
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ppMainActivity dismissPD. Exception: " + s, "", "", "", "");
        }
    }

}
