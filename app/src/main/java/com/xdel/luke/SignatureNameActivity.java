package com.xdel.luke;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.camera2SecretPictureTaker.APictureCapturingService;
import com.xdel.luke.camera2SecretPictureTaker.PictureCapturingListener;
import com.xdel.luke.camera2SecretPictureTaker.PictureCapturingServiceImpl;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class SignatureNameActivity extends AppCompatActivity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {

    /*
    private static String SignatureImgBitmapStr;
    private static Bitmap mBitmap;
    private static String mCurrentPhotoPath;
    private static final String GIF_FILE_PREFIX = "IMG_";
    private static final String GIF_FILE_SUFFIX = ".gif";
    */

    public static String SignatureImgBitmapStr;
    public static Bitmap mBitmap;
    private static Boolean hasSignature = false;
    public static Boolean requireSignature = false;
    public static String mCurrentPhotoPath;
    public static Long CanGetReviewLinkJobsIDX = null;
    private String GIF_FILE_PREFIX = "IMG_";
    private String GIF_FILE_SUFFIX = ".gif";
    public static String rtnPageName;
    public static String ReceiverName;
    public static String StaffID;
    private ProgressDialog pd;

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    private LinearLayout LLSignPad, LLSignPadView, LLreceiverNamelbl, LLreceiverNametxt, LLhidden, LLStaffIDtxt;
    private TextView lblname, lblStaffIDTxt, lblsignbelow;
    private EditText txtReceiverName, txtStaffID;
    private Button btnCancel, btnSubmit;
    private String state = "";

    private APictureCapturingService pictureService;
    private PictureCapturingListener pcl;
    private boolean Spictaken = false;
    private static String FinalSPhotoURLStr = "";
    private String ActDeactSpic;

    /*=====================================================================SIGNATURE=====================================================================*/
    signature mSignature;
    View mView;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    /*=====================================================================SIGNATURE=====================================================================*/

    ScreenShotContentObserver screenShotContentObserver;

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

        setContentView(R.layout.activity_signature_name);

        checkPermissions();

        txtReceiverName = (EditText) findViewById(R.id.txtReceiverName);
        lblname = (TextView) findViewById(R.id.lblname);
        lblsignbelow = findViewById(R.id.lblsignbelow);
        LLSignPad = (LinearLayout) findViewById(R.id.LLSignPad);
        LLSignPadView = (LinearLayout) findViewById(R.id.LLSignPadView);
        LLreceiverNamelbl = (LinearLayout) findViewById(R.id.LLreceiverNamelbl);
        LLreceiverNametxt = (LinearLayout) findViewById(R.id.LLreceiverNametxt);
        LLStaffIDtxt = (LinearLayout) findViewById(R.id.LLStaffIDtxt);

        LLhidden = (LinearLayout) findViewById(R.id.LLhidden);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        LLSignPad.addView(mSignature, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        mView = LLSignPad;

        lblStaffIDTxt = (TextView) findViewById(R.id.lblStaffIDTxt);
        txtStaffID = (EditText) findViewById(R.id.txtStaffID);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        pictureService = PictureCapturingServiceImpl.getInstance(this);
        pcl = this;

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            myApp.currentActivity = this;
        }
        try {
            Context c = myApp.getAppContext();
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);

            Boolean reqStaffID = JobsHandler.reqStaffID(GcmPreferences);
            //reqStaffID = true;
            ActDeactSpic = objGlobalMethods.getGCMPrefs(GcmPreferences, "ActDeactSpic");
            lblStaffIDTxt.setVisibility(reqStaffID ? View.VISIBLE : View.GONE);
            txtStaffID.setVisibility(reqStaffID ? View.VISIBLE : View.GONE);
            //LLStaffIDtxt.setBackgroundColor(reqStaffID ? Color.parseColor("#ffffff") : Color.parseColor("#ffe4f4ff"));

            if (rtnPageName.equals("setDunningDeliveredStatusActivity")) {
                SignatureImgBitmapStr = setDunningDeliveredStatusActivity.signatureString;
                hideshowNameCtrls(false);
                lblsignbelow.setText(c.getString(R.string.pls_sign));
            } else if (rtnPageName.equals("setDeliveredStatusActivity")) {
                txtReceiverName.setText(ReceiverName == null ? "" : ReceiverName);
                SignatureImgBitmapStr = setDeliveredStatusActivity.signatureString;
                hideshowNameCtrls(true);
                lblsignbelow.setText(c.getString(R.string.ack_rec_inorder));
            } else if (rtnPageName.equals("setDeliveredMassStatusActivity")) {
                txtReceiverName.setText(ReceiverName);
                SignatureImgBitmapStr = setDeliveredMassStatusActivity.signatureString;
                hideshowNameCtrls(true);
                lblsignbelow.setText(c.getString(R.string.ack_rec_inorder));
            } else if (rtnPageName.equals("CheckListActivity")) {
                if (CheckListActivity.al != null
                        && CheckListActivity.al.size() > 0
                        && CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1
                        && CheckListActivity.al.size() - 1 >= CheckListActivity.itemIndex
                        && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT != null
                        && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.size() > 0) {

                    SignatureImgBitmapStr = CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.get(0).ImgBitmapStr;
                }

                hideshowNameCtrls(false);
                lblsignbelow.setText(c.getString(R.string.pls_sign));
            }

            if (SignatureImgBitmapStr != null && !SignatureImgBitmapStr.equals("")) {
                LLSignPad.setVisibility(View.GONE);
                LLSignPadView.setVisibility(View.VISIBLE);
                loadSavedSignature();
            } else {
                LLSignPad.setVisibility(View.VISIBLE);
                LLSignPadView.setVisibility(View.GONE);
            }

            //txtReceiverName.setText(ReceiverName == null || ReceiverName.equals("") ? "" : ReceiverName);
            if (ReceiverName != null && !ReceiverName.equals("") && ReceiverName.length() > 0) {
                txtReceiverName.setSelection(ReceiverName.length());
            }
            txtStaffID.setText(StaffID == null || StaffID.equals("") ? "" : StaffID);

            state = "";

            txtReceiverName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                        LLreceiverNamelbl.requestFocus();
                    }
                    return true;
                }
            });
            txtStaffID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {

                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                        LLreceiverNamelbl.requestFocus();
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at SignatureNameActivity onResume. Exception: " + e.getMessage().toString() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);

            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
            mBitmap = null;

            if (pcl != null) {
                pcl = null;
            }
            if (pictureService != null) {
                pictureService = null;
            }
            if (pd != null) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                pd = null;
            }
            if (mSignature != null) {
                mSignature = null;
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SignatureNameActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (state.equals("")) {
            ReceiverName = txtReceiverName.getText().toString();
            StaffID = txtStaffID.getText().toString();

        } else if (state.equals("cx")) {
            //do nothing
        } else if (state.equals("ok")) {
            if (rtnPageName != null) {
                if (rtnPageName.equals("setDunningDeliveredStatusActivity")) {

                } else if (rtnPageName.equals("setDeliveredStatusActivity")) {
                    ReceiverName = txtReceiverName.getText().toString();
                    StaffID = txtStaffID.getText().toString();
                } else if (rtnPageName.equals("setDeliveredMassStatusActivity")) {
                    ReceiverName = txtReceiverName.getText().toString();
                }
            }
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SignatureNameActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void hideshowNameCtrls(Boolean showName) {
        if (showName) {
            LLreceiverNamelbl.setVisibility(View.VISIBLE);
            LLreceiverNametxt.setVisibility(View.VISIBLE);
            LLhidden.setVisibility(View.GONE);
        } else {
            LLreceiverNamelbl.setVisibility(View.GONE);
            LLreceiverNametxt.setVisibility(View.GONE);
            LLhidden.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        state = "cx";
        SignatureImgBitmapStr = "";
        FinalSPhotoURLStr = null;
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        mBitmap = null;
        hasSignature = false;
        mCurrentPhotoPath = "";
        rtnToPrevPage2(false);
    }

    private Dialog showDialog(String msg) {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
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

    private void loadSavedSignature() {
        try {
            //convert the string to byte array
            byte[] imageAsBytes = Base64.decode(SignatureImgBitmapStr.getBytes(), Base64.DEFAULT);
            //get reference to the image view where you want to display the image
            ImageView image = (ImageView) this.findViewById(R.id.viewSignature);
            //set the image by decoding the byte array to bitmap
            image.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
            );

        } catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at SignatureNameActivity loadSavedSignature. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signature_name, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clearSignature(View view) {
        hasSignature = false;
        mSignature.clear();
        SignatureImgBitmapStr = "";
        LLSignPad.setVisibility(View.VISIBLE);
        LLSignPadView.setVisibility(View.GONE);
    }

    public void CancelsetSignatureName(View view) {
        onBackPressed();
    }

    private void rtnToPrevPage2(Boolean btnOkClicked) {
        //Intent intent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent intent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        hasSignature = false;

        try {
            if (rtnPageName.equals("setDunningDeliveredStatusActivity")) {
                if (btnOkClicked) {
                    setDunningDeliveredStatusActivity.signatureString = SignatureImgBitmapStr;
                }
                //intent = new Intent(this, setDunningDeliveredStatusActivity.class);
                intent = new Intent(myApp.currentActivity, setDunningDeliveredStatusActivity.class);
            } else if (rtnPageName.equals("setDeliveredMassStatusActivity")) {
                if (btnOkClicked) {
                    setDeliveredMassStatusActivity.signatureString = SignatureImgBitmapStr;
                    ReceiverName = txtReceiverName.getText().toString();
                }

                //intent = new Intent(this, setDeliveredMassStatusActivity.class);
                intent = new Intent(myApp.currentActivity, setDeliveredMassStatusActivity.class);
                if (setDeliveredMassStatusActivity.SimagePaths != null && (FinalSPhotoURLStr != null && !FinalSPhotoURLStr.equals(""))) {
                    setDeliveredMassStatusActivity.SimagePaths.add(FinalSPhotoURLStr);
                } else if (FinalSPhotoURLStr != null && !FinalSPhotoURLStr.equals("")) {
                    setDeliveredMassStatusActivity.SimagePaths = new ArrayList<String>();
                    setDeliveredMassStatusActivity.SimagePaths.add(FinalSPhotoURLStr);
                }
            } else if (rtnPageName.equals("setDeliveredStatusActivity")) {
                if (btnOkClicked) {
                    setDeliveredStatusActivity.signatureString = SignatureImgBitmapStr;
                    ReceiverName = txtReceiverName.getText().toString();
                }

                StaffID = txtStaffID.getText().toString();
                //intent = new Intent(this, setDeliveredStatusActivity.class);
                intent = new Intent(myApp.currentActivity, setDeliveredStatusActivity.class);
                if (setDeliveredStatusActivity.SimagePaths != null && (FinalSPhotoURLStr != null && !FinalSPhotoURLStr.equals(""))) {
                    setDeliveredStatusActivity.SimagePaths.add(FinalSPhotoURLStr);
                } else if (FinalSPhotoURLStr != null && !FinalSPhotoURLStr.equals("")) {
                    setDeliveredStatusActivity.SimagePaths = new ArrayList<String>();
                    setDeliveredStatusActivity.SimagePaths.add(FinalSPhotoURLStr);
                }
            } else if (rtnPageName.equals("CheckListActivity")) {
                SignatureImgBitmapStr = null;
                //intent = new Intent(this, CheckListActivity.class);
                intent = new Intent(myApp.currentActivity, CheckListActivity.class);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at SignatureNameActivity rtnToPrevPage2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            FinalSPhotoURLStr = null;
            startActivity(intent);
            this.finish();
        }
    }

    private void rtnToPrevPage(Boolean btnOkClicked) {
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent intent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        hasSignature = false;
        if (rtnPageName.equals("setDunningDeliveredStatusActivity")) {
            if (btnOkClicked) {
                setDunningDeliveredStatusActivity.signatureString = SignatureImgBitmapStr;
            }
            intent = new Intent(this, setDunningDeliveredStatusActivity.class);
        } else if (rtnPageName.equals("setDeliveredStatusActivity")) {
            if (btnOkClicked) {
                ReceiverName = txtReceiverName.getText().toString();
                //setDeliveredStatusActivity.SecuritySealNumber = txtSecuritySealNumber.getText().toString();
                setDeliveredStatusActivity.signatureString = SignatureImgBitmapStr;
                intent = new Intent(this, setDeliveredStatusActivity.class);
            } else {
                intent = new Intent(this, setDeliveredStatusActivity.class);
            }
        } else if (rtnPageName.equals("setDeliveredMassStatusActivity")) {
            if (btnOkClicked) {
                ReceiverName = txtReceiverName.getText().toString();
                setDeliveredMassStatusActivity.signatureString = SignatureImgBitmapStr;
            }
            intent = new Intent(this, setDeliveredMassStatusActivity.class);
        } else if (rtnPageName.equals("Jobs_Retrieval_Activity")) {
            if (btnOkClicked) {

            }
            intent = new Intent(this, Jobs_Retrieval_Activity.class);
        }

        startActivity(intent);
        this.finish();
    }

    public void btnOKClick2(View view) {
        Boolean reqStaffID = JobsHandler.reqStaffID(GcmPreferences);
        //boolean reqSign = objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "reqSign")) ? false : true;
        boolean reqName = objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "reqName")) ? false : true;
        boolean isResidential = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "isResidential")) ? true : false;
        String PreDeterminedStatusCodeIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX");

        if (LLSignPadView.getVisibility() == View.VISIBLE) {
            if (reqStaffID && txtStaffID.getText().toString().equals("")) {
                txtStaffID.setFocusable(true);
                String s = objGlobalMethods.getOKPrequirementMsg(requireSignature, false, reqStaffID);
                showDialog(s);
            } else {
                if (reqName && objGlobalMethods.IsEmptyString(txtReceiverName.getText().toString())) {
                    String s = objGlobalMethods.getOKPrequirementMsgNew(false, reqName, false, false, PreDeterminedStatusCodeIDX);
                    showDialog(s);
                } else {
                    state = "ok";
                    rtnToPrevPage2(false);
                }
                /*state = "ok";
                rtnToPrevPage2(false);*/
            }
        } else {
            if (reqStaffID && txtStaffID.getText().toString().equals("")) {
                txtStaffID.setFocusable(true);
                String s = objGlobalMethods.getOKPrequirementMsg(requireSignature, false, reqStaffID);
                showDialog(s);
            } else {
                SignatureImgBitmapStr = (rtnPageName.equals("CheckListActivity")) ? RTNSignatureStrForChecklist() : RTNSignatureStr();
                //SignatureImgBitmapStr = RTNSignatureStr();
                if (rtnPageName.equals("CheckListActivity") && SignatureImgBitmapStr.equals("")) {
                    hasSignature = false;
                    mSignature.clear();
                    showDialog(myApp.getAppContext().getString(R.string.impropersignature0) + "\n" + myApp.getAppContext().getString(R.string.impropersignature1));
                } else
                    //if (requireSignature && SignatureImgBitmapStr.equals("")) {
                    if ((requireSignature || isResidential) && SignatureImgBitmapStr.equals("")) {
                        hasSignature = false;
                        mSignature.clear();
                        showDialog(myApp.getAppContext().getString(R.string.impropersignature0) + "\n" + myApp.getAppContext().getString(R.string.impropersignature1));
                    } else if (!requireSignature && SignatureImgBitmapStr.equals("") && hasSignature) {
                        hasSignature = false;
                        mSignature.clear();
                        showDialog(myApp.getAppContext().getString(R.string.impropersignature2) + "\n" + myApp.getAppContext().getString(R.string.impropersignature3));
                    } else {
                        if (reqName && objGlobalMethods.IsEmptyString(txtReceiverName.getText().toString())) {
                            String s = objGlobalMethods.getOKPrequirementMsgNew(false, reqName, false, false, PreDeterminedStatusCodeIDX);
                            showDialog(s);
                        } else {
                            state = "ok";
                            rtnToPrevPage2(true);
                        }
                    /*state = "ok";
                    rtnToPrevPage2(true);*/
                    }
            }
        }
    }

    private class sendReviewLink extends AsyncTask<Void, Void, Boolean> {
        public sendReviewLink() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = true;

            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                String RegID = objGlobalMethods.getRegistrationId(null, GcmPreferences);
                UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                objGlobalMethods.writeToFile("Review Link requested for JobsIDX: " + String.valueOf(CanGetReviewLinkJobsIDX), "", "", "", "");
                JobsHandler.addSendReviewLinkQueALPrefs(UpdateJobInfoPreferences, RegID, CanGetReviewLinkJobsIDX);
                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at SignatureNameActivity sendReviewLink doInBackground. Excpetion: " + e.getMessage().toString(), "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (pd != null) {
                pd.dismiss();
            }
            try {
                if (success == null || !success) {
                    showDialog(myApp.getAppContext().getString(R.string.errgetreviewlink));
                } else if (success) {
                    showDialog(myApp.getAppContext().getString(R.string.lblgetreviewlink3));
                }

            } catch (Exception e) {
                if (pd != null) {
                    pd.dismiss();
                }
                objGlobalMethods.writeToFile("Exception caught at SignatureNameActivity sendReviewLink onPostExecute. Excpetion: " + e.toString(), "", "", "", "");
                showDialog(myApp.getAppContext().getString(R.string.errgetreviewlink));
            }
        }
    }

    /*=====================================================================Job Status Updates=====================================================================*/

    private void updateOKPviaJobsRetrievalActivity() {
        try {
            Bundle extra = this.getIntent().getExtras();
            String aJobsIDX = extra.getString("JobsIDX");
            Long JobsIDX = aJobsIDX == null || aJobsIDX.equals("") ? 0 : Long.parseLong(aJobsIDX);
            if (JobsIDX > 0) {
                String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), GcmPreferences);
                Date dt = new Date();
                Long l = dt.getTime();
                String timestampstr = "/Date(" + l + ")/";
                android.location.Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

                JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, regid, JobsIDX,
                        135, timestampstr, "Recipient", "",
                        lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                objGlobalMethods.writeToFile("Job OKP at SignaturenameActivity from Jobs_Retrieval_Activity.",
                        "JobsIDX: " + String.valueOf(JobsIDX), "", "", "");

                DAO_Jobs.updateJobsCatTypeID(3, JobsIDX);
                JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 3);
                sendBroadcast(new Intent(this, BackendRun_BroadcastReceiver.class));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
    }

    /*=====================================================================Job Status Updates=====================================================================*/

    /*=====================================================================Spic=====================================================================*/

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(myApp.getAppContext(), text, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                }
            }
        });
    }

    /**
     * We've finished taking pictures from all phone's cameras
     */
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        try {
            if (picturesTaken != null && !picturesTaken.isEmpty()) {
                //showToast("Done capturing all photos!");
                return;
            }
            //showToast("No camera detected!");
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    /**
     * Displaying the pictures taken.
     */
    @Override
    public void onCaptureDone(final String pictureUrl, final byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                        final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                        final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                        FinalSPhotoURLStr = pictureUrl;
                        /*if (pictureUrl.contains("0_pic.jpg")) {
                            //uploadBackPhoto.setImageBitmap(scaled);
                            FinalSPhotoURLStr = pictureUrl;
                        } else if (pictureUrl.contains("1_pic.jpg")) {
                            //uploadFrontPhoto.setImageBitmap(scaled);
                            FinalSPhotoURLStr = pictureUrl;
                        }*/

                        //galleryAddPic(pictureUrl);
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                    }
                }
            });
        }
    }

    /*=====================================================================Spic=====================================================================*/

    /*=====================================================================SIGNATURE=====================================================================*/

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public String saveNreturnSignatureImgBitmapStr(View v) {
            Date dt = new Date();
            /*Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());*/
            String str = "";

            mCurrentPhotoPath = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }

            File f = null;

            try {
                f = setUpPhotoFile();
                mCurrentPhotoPath = f.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                f = null;
                mCurrentPhotoPath = null;
            }

            if (mBitmap != null && !mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
            mBitmap = null;
            //if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(LLSignPad.getWidth(), LLSignPad.getHeight(), Bitmap.Config.RGB_565);
            //}

            Canvas canvas = new Canvas(mBitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mCurrentPhotoPath);

                v.draw(canvas);

                //mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, mFileOutStream);

                int whitecount = 0;
                if (requireSignature) {
                    whitecount = getWhitePercentage(mBitmap);
                    if (whitecount < 30) {
                        str = "";
                    } else {
                        str = convertBitmapToBase64(mBitmap);
                    }
                } else {
                    whitecount = getWhitePercentage(mBitmap);
                    if (whitecount < 30) {
                        str = "";
                    } else {
                        str = convertBitmapToBase64(mBitmap);
                    }
                }

                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
                mBitmap = null;

                /*mFileOutStream.flush();
                mFileOutStream.close();
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);*/

                //In case you want to delete the file
                //boolean deleted = mypath.delete();
                //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                //If you want to convert the image to string use base64 converter

                /*str = convertBitmapToBase64(mBitmap);
                mBitmap.recycle();
                mBitmap = null;*/

            } catch (Exception e) {
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
                mBitmap = null;
                objGlobalMethods.writeToFile("Exception caught at SignatureNameActivity signature class save function. Exception: " + e.toString(), "", "", "", "");
                str = "";
            } finally {
                return str;
            }
        }

        public void save(View v) {
            Date dt = new Date();
            /*Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());*/

            mCurrentPhotoPath = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }

            File f = null;

            try {
                f = setUpPhotoFile();
                mCurrentPhotoPath = f.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
                f = null;
                mCurrentPhotoPath = null;
            }


            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(LLSignPad.getWidth(), LLSignPad.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(mBitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mCurrentPhotoPath);

                v.draw(canvas);
                //mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);

                //In case you want to delete the file
                //boolean deleted = mypath.delete();
                //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                //If you want to convert the image to string use base64 converter

                //galleryAddPic();


            } catch (Exception e) {
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
                mBitmap = null;
                objGlobalMethods.writeToFile("Exception caught at SignatureNameActivity signature class save function. Exception: " + e.toString(), "", "", "", "");
            }
        }

        public void clear() {
            try {
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
                mBitmap = null;
                path.reset();
                invalidate();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at SignatureNameActivity clear. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            //mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    hasSignature = true;
                    if (!Spictaken && !ActDeactSpic.isEmpty() && !ActDeactSpic.equals("") && ActDeactSpic.equals("1")) {
                        Spictaken = true;
                        pictureService.startCapturing(pcl);
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    hasSignature = true;
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }

        private int getWhitePercentage(Bitmap img) {
            int pc = 0;
            Bitmap scaledImg = Bitmap.createScaledBitmap(img, 100, 100, false);

            int w = scaledImg.getWidth();
            int h = scaledImg.getHeight();
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int pixel = scaledImg.getPixel(i, j);

                    if (pixel == Color.BLACK) {
                        pc += 1;
                    }
                }
            }
            scaledImg.recycle();
            scaledImg = null;
            return pc;
        }
    }

    //****************Saving Signature************************************************************************************************************************

    private String RTNSignatureStrForChecklist() {
        String SignatureStr = "";
        try {
            if (hasSignature) {
                if (CheckListActivity.al != null && CheckListActivity.al.size() > 0
                        && CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1
                        && CheckListActivity.al.size() - 1 >= CheckListActivity.itemIndex) {
                    SignatureStr = mSignature.saveNreturnSignatureImgBitmapStr(mView);
                    if (!SignatureStr.isEmpty()) {
                        if (CheckListActivity.al.get(CheckListActivity.itemIndex).dataT != null) {
                            CheckListActivity.al.get(CheckListActivity.itemIndex).dataT = null;
                        }
                        CustomGallery item = new CustomGallery();
                        item.sdcardPath = mCurrentPhotoPath;
                        item.setImgBitmapStr(SignatureStr);
                        item.setCallLog(false);
                        item.setItemProof(false);
                        item.setPhotodesc(!objGlobalMethods.IsEmptyString(CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText)
                                ? CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText : "");
                        item.setChecklistitemidx(-1);
                        CheckListActivity.al.get(CheckListActivity.itemIndex).addDataT(item);
                        CheckListActivity.al.get(CheckListActivity.itemIndex).setAltColor(false);
                        CheckListActivity.openCamera = null;
                        CheckListActivity.itemIndex = null;
                        mCurrentPhotoPath = null;
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at SignatureNameActivity RTNSignatureStr. Exception: " + e.toString(), "", "", "", "");
            SignatureStr = "";
        }
        return SignatureStr;
    }

    private String RTNSignatureStr() {
        String SignatureStr = "";
        try {
            if (hasSignature) {
                SignatureStr = mSignature.saveNreturnSignatureImgBitmapStr(mView);
                if (!SignatureStr.isEmpty()) {
                    File f = new File(mCurrentPhotoPath);
                    if (f.exists()) {
                        Boolean deleted = f.delete();
                        if (deleted) {

                            //below has memory leak, advised to use intent to scan
                            /*MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                             *//*
                             *   (non-Javadoc)
                             * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                             *//*
                                public void onScanCompleted(String path, Uri uri) {
                                    Date dt = new Date();
                                    objGlobalMethods.writeToFile( "Signature image deleted.", "", "", "", "");
                                }
                            });*/

                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            this.sendBroadcast(mediaScanIntent);

                        }
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at SignatureNameActivity RTNSignatureStr. Exception: " + e.toString(), "", "", "", "");
            SignatureStr = "";
        }
        return SignatureStr;
    }

    private String getAlbumName() {
        //return getString(R.string.album_name);
        return getString(R.string.appspecificfilefolder_image);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        objGlobalMethods.writeToFile("Camera5Activity getAlbumDir failed to create directory.", "", "", "", "");
                        return null;
                    }
                }
            }

        } else {
            Toast.makeText(myApp.getAppContext(), "External storage is not mounted READ/WRITE.", Toast.LENGTH_SHORT);
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = GIF_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, GIF_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void galleryAddPic(String path) {
        try {
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            File f = new File(path);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Camera5Activity galleryAddPic. " + e.getMessage().toString(), "", "", "", "");
            mCurrentPhotoPath = null;
        }
    }

    private String convertBitmapToBase64(Bitmap bm) {
        String encoded;
        byte[] array;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

            array = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(array, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return encoded;
    }

    //****************Saving Signature************************************************************************************************************************

    /*=====================================================================SIGNATURE=====================================================================*/

    private static final String[] requiredPermissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    /**
     * checking  permissions at Runtime.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
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
                    objGlobalMethods.writeToFile("Bluetooth is on at SignatureNameActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at SignatureNameActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
