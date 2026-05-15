package com.xdel.luke.HRM;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.xdel.luke.BottomSheet.BottomSheetQRDialogFragment;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAuthDriverResult;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.temploggerclass;

public class StaffPassActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    TextView lblName, lblID;
    ImageView imgProfilePix, imgIDBarcode, imgVisible;
    Button btnQR;
    String[] UsernamePwd = new String[4];
    String username;
    String password;
    ScreenShotContentObserver screenShotContentObserver;
    ConnectionDetector cd;
    private ProgressDialog pd;

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(StaffPassActivity.this, MainActivity.class);
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

        setContentView(R.layout.activity_staff_pass);

        lblName = (TextView) findViewById(R.id.lblName);
        lblID = (TextView) findViewById(R.id.lblID);
        imgProfilePix = (ImageView) findViewById(R.id.imgProfilePix);
        imgIDBarcode = (ImageView) findViewById(R.id.imgIDBarcode);
        imgVisible = (ImageView) findViewById(R.id.imgVisible);
        btnQR = (Button) findViewById(R.id.btnQR);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                Context ctx = myApp.getAppContext();
                setTitle(ctx.getString(R.string.title_activity_staff_pass));

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("StaffPassActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                JobsHandler.checkForceLogOut();
                imgVisible.setImageResource(R.mipmap.ic_action_eye_off);


                objGlobalMethods.setGCMPrefs(GcmPreferences, "showID", "");
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
                } else {
                    lblName.setText(GcName);
                    lblID.setText(objGlobalMethods.IsEmptyString(MaskedGcID) ? GcID : MaskedGcID);
                    if (GcImg != null) {
                        imgProfilePix.setImageBitmap(BitmapFactory.decodeByteArray(GcImg, 0, GcImg.length));
                    }

                    //cd = new ConnectionDetector(ctx);
                    cd = new ConnectionDetector(null);
                    if (!cd.isConnectingToInternet()) {
                        //// Internet Connection is not present
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.toast_workingconnection)).show();
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnQR);
                    } else {
                        String StaffPassLoaded = objGlobalMethods.getGCMPrefs(GcmPreferences, "StaffPassLoaded");
                        if (objGlobalMethods.IsEmptyString(StaffPassLoaded)) {
                            getPassQR_IDbarcode t = new getPassQR_IDbarcode(GcID);
                            t.execute();
                        }
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at StaffPassActivity onResume. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at StaffPassActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at StaffPassActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
            }
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent parentIntent = new Intent(this, ProfileActivity.class);
        MainActivity.uponLogin = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private class getPassQR_IDbarcode extends AsyncTask<Void, Void, objPassQR_IDbarcode> {
        private final String IDval;

        public getPassQR_IDbarcode(String aIDval) {
            this.IDval = aIDval;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
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
                String regid = objGlobalMethods.getRegistrationId(null, GcmPreferences);
                res = WebService.L2_GetGCPassQRCode(regid);
                if (res != null) {
                    finalmsg = JobsHandler.convertByteArrToBase64String(res);
                }
                obj.setQRimgBase64(finalmsg);

                com.google.zxing.Writer writer = new MultiFormatWriter();
                String finaldata = Uri.encode(IDval, "utf-8");
                int height = imgIDBarcode.getHeight();
                int width = imgIDBarcode.getWidth();
                BitMatrix bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, height);
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for (int i = 0; i < width; i++) {//width
                    for (int j = 0; j < height; j++) {//height
                        bmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
                obj.setIDbarcode(bmp);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at StaffPassActivity getPassQR_IDbarcode doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return obj;
            }
        }

        @Override
        protected void onPostExecute(final objPassQR_IDbarcode res) {
            boolean err = false;
            boolean QRerr = false;
            boolean IDerr = false;
            String QRimgBase64 = "";
            Bitmap IDbarcode = null;

            Context ctx = myApp.getAppContext();
            try {
                QRimgBase64 = res.getQRimgBase64();
                final String finalQRimgBase64 = QRimgBase64;

                btnQR.setOnClickListener(null);
                if (!QRimgBase64.isEmpty() && !QRimgBase64.equals("")) {
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnQR);
                    btnQR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetQRDialogFragment();
                                Bundle args = new Bundle();
                                args.putString("qrbase64", finalQRimgBase64);
                                bottomSheetDialogFragment.setArguments(args);
                                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception at StaffPassActivity getPassQR_IDbarcode onPostExecute btnQR.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                            }
                        }
                    });
                } else {
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnQR);
                    QRerr = true;
                }

                IDbarcode = res.getIDbarcode();
                IDerr = IDbarcode == null ? true : false;

                if (IDbarcode != null) {
                    imgIDBarcode.setImageBitmap(IDbarcode);
                }
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                objGlobalMethods.setGCMPrefs(GcmPreferences, "StaffPassLoaded", IDbarcode != null ? "t" : "");

                pd.dismiss();
            } catch (Exception e) {
                err = true;
                objGlobalMethods.writeToFile("Exception at StaffPassActivity getPassQR_IDbarcode onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                if (pd != null)
                    pd.dismiss();
                String finalmsg = "";
                if (err || (QRerr && IDerr)) {
                    finalmsg = ctx.getString(R.string.alert_error_staffpassqr_idbarcode);
                } else if (QRerr) {
                    finalmsg = ctx.getString(R.string.alert_errorretrivedata_id_qr);
                } else if (IDerr) {
                    finalmsg = ctx.getString(R.string.alert_errorretrivedata_id_barcode);
                }
                if (!finalmsg.isEmpty() && !finalmsg.equals("")) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), finalmsg).show();
                }
            }
        }
    }

    private class getPassQR extends AsyncTask<Void, Void, String> {
        getPassQR() {
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            byte[] res = null;
            String finalmsg = "";
            try {
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                String regid = objGlobalMethods.getRegistrationId(null, GcmPreferences);
                res = WebService.L2_GetGCPassQRCode(regid);
                if (res != null) {
                    finalmsg = JobsHandler.convertByteArrToBase64String(res);
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at StaffPassActivity getPassQR doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return finalmsg;
            }
        }

        @Override
        protected void onPostExecute(final String finalmsg) {
            boolean err = false;
            Context ctx = myApp.getAppContext();
            try {
                pd.dismiss();
                btnQR.setOnClickListener(null);
                if (!finalmsg.isEmpty() && !finalmsg.equals("")) {
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnQR);
                    btnQR.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetQRDialogFragment();
                                Bundle args = new Bundle();
                                args.putString("qrbase64", finalmsg);
                                bottomSheetDialogFragment.setArguments(args);
                                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception at StaffPassActivity getPassQR onPostExecute btnQR.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                            }
                        }
                    });
                } else {
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnQR);
                    err = true;
                }
            } catch (Exception e) {
                err = true;
                objGlobalMethods.writeToFile("Exception at StaffPassActivity getPassQR onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                if (pd != null)
                    pd.dismiss();
                if (err)
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorretrivedata_id_qr)).show();
            }
        }

    }

    private class genIDbarcode extends AsyncTask<Void, Void, Bitmap> {
        final private String val;

        genIDbarcode(String aval) {
            this.val = aval;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bmp = null;
            byte[] res = null;
            String finalmsg = "";
            try {
                com.google.zxing.Writer writer = new MultiFormatWriter();
                String finaldata = Uri.encode(val, "utf-8");
                int height = imgIDBarcode.getHeight();
                int width = imgIDBarcode.getWidth();
                BitMatrix bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, height);
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for (int i = 0; i < width; i++) {//width
                    for (int j = 0; j < height; j++) {//height
                        bmp.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }
                /*String AImageData = JobsHandler.convertBitmapToBase64(bmp);
                String filepath = objGlobalMethods.getSavedBitmapFileToQueueFolder(AImageData, "5315173");*/
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at StaffPassActivity genIDbarcode doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return bmp;
            }
        }

        @Override
        protected void onPostExecute(final Bitmap barcode) {
            boolean err = false;
            Context ctx = myApp.getAppContext();
            try {
                //pd.dismiss();
                if (barcode != null) {
                    imgIDBarcode.setImageBitmap(barcode);
                }

            } catch (Exception e) {
                err = true;
                objGlobalMethods.writeToFile("Exception at StaffPassActivity genIDbarcode onPostExecute. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                if (pd != null)
                    pd.dismiss();
                if (err)
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorretrivedata_id_qr)).show();
            }
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
            objGlobalMethods.writeToFile("Exception at StaffPassActivity toggleIDView. Exception: " + e.getMessage(),"","","","");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at StaffPassActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StaffPassActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
