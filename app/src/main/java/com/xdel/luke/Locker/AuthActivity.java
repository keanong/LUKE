package com.xdel.luke.Locker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.xdel.luke.CheckListActivity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.HRM.objPassQR_IDbarcode;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objAddressStructure;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.setDeliveredStatusActivity;
import com.xdel.luke.templogger.temploggerclass;

public class AuthActivity extends AppCompatActivity {

    private SharedPreferences gcmp, UpdateJobInfoPreferences;
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;

    Button btnLockerAuthCANCEL, btnLockerAuthNext, btnLockerResidential;
    TextView lblPrivateKey, lblerrGenUserKeyBarcode;
    ImageView imgPrivateKeyBarcode;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        gcmp = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(gcmp, "language");
        /*String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/
        setContentView(R.layout.activity_auth);

        btnLockerAuthCANCEL = (Button) findViewById(R.id.btnLockerAuthCANCEL);
        btnLockerAuthNext = (Button) findViewById(R.id.btnLockerAuthNext);
        btnLockerResidential = findViewById(R.id.btnLockerResidential);
        lblPrivateKey = (TextView) findViewById(R.id.lblPrivateKey);
        lblerrGenUserKeyBarcode = (TextView) findViewById(R.id.lblerrGenUserKeyBarcode);
        imgPrivateKeyBarcode = (ImageView) findViewById(R.id.imgPrivateKeyBarcode);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    @Override
    protected void onResume() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (!objGlobalMethods.UserCanScreenShot(gcmp)){
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;
                setTitle("");

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("AuthActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                JobsHandler.checkForceLogOut();
                init();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at AuthActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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

            btnLockerAuthCANCEL = null;
            btnLockerAuthNext =  null;
            lblPrivateKey =  null;
            lblerrGenUserKeyBarcode =  null;
            imgPrivateKeyBarcode =  null;
            dismissNullifyPD(pd);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AuthActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at AuthActivity dismissNullifyPD. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }
    @Override
    protected void onPause() {
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at AuthActivity onPause unregisterContentObserver. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    public void btnLockerAuthCANCEL_Click(View v) {
        back();
    }

    public void btnLockerAuthNext_Click(View v) {
        next();
    }

    private void back() {
        Intent parentIntent = new Intent(this, JobInfoActivity.class);
        MainActivity.uponLogin = 0;
        gcmp = objGlobalMethods.getGcmPreferences();
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private void next() {
        Intent parentIntent = new Intent(this, ShipmentActivity.class);
        MainActivity.uponLogin = 0;
        gcmp = objGlobalMethods.getGcmPreferences();
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private void loadResidentialAddressButtonText() {
        try {
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            String hfJobInfoJsonStr = gcmp.getString("hfJobInfoJsonStr", "");
            if (!objGlobalMethods.IsEmptyString(hfJobInfoJsonStr)) {
                String address = "";
                Gson gson = new Gson();
                objJobInfo currJobInfo = gson.fromJson(gcmp.getString("hfJobInfoJsonStr", ""), objJobInfo.class);

                if (currJobInfo != null && currJobInfo.DLLocation != null) {
                    objAddressStructure DLLocation = currJobInfo.DLLocation;
                    String DELcompany = (DLLocation.COMPANY != null && !DLLocation.COMPANY.equals("")) ? DLLocation.COMPANY : "";
                    String DELaddress = objGlobalMethods.returnFullAddress(DLLocation);
                    if (!objGlobalMethods.IsEmptyString(DELaddress)) {
                        address = !objGlobalMethods.IsEmptyString(DELcompany) ? DELcompany + ", " + DELaddress : DELaddress;
                        btnLockerResidential.setText(address);
                        btnLockerResidential.setVisibility(View.VISIBLE);
                    } else {
                        btnLockerResidential.setVisibility(View.GONE);
                    }
                } else {
                    btnLockerResidential.setVisibility(View.GONE);
                }            } else {
                btnLockerResidential.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AuthActivity loadResidentialAddressButtonText. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void init() {
        try {
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            objGlobalMethods.setGCMPrefs(gcmp, "skiplocker", "");
            int catType = objGlobalMethods.getDashboard2ActivityCategoryType(gcmp);
            String isAlps = objGlobalMethods.getGCMPrefs(gcmp, "isAlps");
            Boolean isAlpsB = !objGlobalMethods.IsEmptyString(isAlps);
            btnLockerResidential.setVisibility((catType == 2 && within200m()) && !isAlpsB ? View.VISIBLE : View.GONE);

            String L2_LA_GetUserResultJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "GetUserResultJsonStr");
            String LockerUserKeyBarcode64str = objGlobalMethods.getGCMPrefs(gcmp, "LockerUserKeyBarcode64str");
            if (!objGlobalMethods.IsEmptyString(L2_LA_GetUserResultJsonStr)) {
                Gson gson = new Gson();
                final L2_LA_GetUserResult res = gson.fromJson(L2_LA_GetUserResultJsonStr, L2_LA_GetUserResult.class);
                if (res != null) {
                    lblPrivateKey.setText(res.User_Key);
                    if (!objGlobalMethods.IsEmptyString(LockerUserKeyBarcode64str)) {
                        genNormBarcodeFromBase64str t = new genNormBarcodeFromBase64str(LockerUserKeyBarcode64str);
                        t.execute();
                    } else {
                        new CountDownTimer(500, 500) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (pd != null && !pd.isShowing()) {
                                    pd.show();
                                }
                            }

                            @Override
                            public void onFinish() {
                                GenNormBarcodeAysnc t = new GenNormBarcodeAysnc(res.User_Key);
                                t.execute();
                            }
                        }.start();
                    }
                    //loadResidentialAddressButtonText();
                } else {
                    showError_n_Back();
                }
            } else {
                objGlobalMethods.writeToFile("L2_LA_GetUserResultJsonStr is empty string at AuthActivity init.", "", "", "", "");
                showError_n_Back();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AuthActivity init. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class genNormBarcodeFromBase64str extends AsyncTask<Void, Void, byte[]> {
        private final String base64str;
        public genNormBarcodeFromBase64str(String abase64str) {
            this.base64str = abase64str;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            if (pd != null && !pd.isShowing()) {
                pd.show();
            }
        }

        @Override
        protected byte[] doInBackground(Void... params) {
            byte[] b = null;
            try {
                b = Base64.decode(base64str, Base64.DEFAULT);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at AuthActivity genNormBarcodeFromBase64str doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return b;
            }
        }

        @Override
        protected void onPostExecute(final byte[] res) {
            boolean err = false;
            boolean IDerr = false;
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            try {
                if (res != null) {
                    imgPrivateKeyBarcode.setImageBitmap(BitmapFactory.decodeByteArray(res, 0, res.length));
                    imgPrivateKeyBarcode.setVisibility(View.VISIBLE);
                    lblerrGenUserKeyBarcode.setVisibility(View.GONE);
                } else {
                    IDerr = true;
                }

                pd.dismiss();
            } catch (Exception e) {
                err = true;
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at AuthActivity genNormBarcodeFromBase64str onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                if (pd != null)
                    pd.dismiss();

                if (err || IDerr) {
                    lblerrGenUserKeyBarcode.setVisibility(View.VISIBLE);
                    imgPrivateKeyBarcode.setVisibility(View.GONE);
                }
            }
        }
    }

    private class GenNormBarcodeAysnc extends AsyncTask<Void, Void, objPassQR_IDbarcode> {
        private final String IDval;
        private SharedPreferences gcmp;

        public GenNormBarcodeAysnc(String aIDval) {
            this.IDval = aIDval;
            this.gcmp = objGlobalMethods.getGcmPreferences();
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
            Bitmap bmp = null;
            try {
                if (gcmp == null) {
                    gcmp = objGlobalMethods.getGcmPreferences();
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
                objGlobalMethods.writeToFile("Exception at AuthActivity GenNormBarcodeAysnc doInBackground. Exception: " + e.getMessage(), "", "", "", "");
            } finally {
                return obj;
            }
        }

        @Override
        protected void onPostExecute(final objPassQR_IDbarcode res) {
            boolean err = false;
            boolean IDerr = false;
            Bitmap IDbarcode = null;
            String LockerUserKeyBarcode64str = "";
            if (gcmp == null) {
                gcmp = objGlobalMethods.getGcmPreferences();
            }
            try {
                IDbarcode = res.getIDbarcode();
                IDerr = IDbarcode == null ? true : false;

                if (IDbarcode != null) {
                    imgPrivateKeyBarcode.setImageBitmap(IDbarcode);
                    imgPrivateKeyBarcode.setVisibility(View.VISIBLE);
                    lblerrGenUserKeyBarcode.setVisibility(View.GONE);
                    LockerUserKeyBarcode64str = JobsHandler.convertBitmapToBase64(IDbarcode);
                } else {
                    IDerr = true;
                }

                objGlobalMethods.setGCMPrefs(gcmp, "LockerUserKeyBarcode64str", LockerUserKeyBarcode64str);

                pd.dismiss();
            } catch (Exception e) {
                err = true;
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at AuthActivity GenNormBarcodeAysnc onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                if (pd != null)
                    pd.dismiss();

                if (err || IDerr) {
                    lblerrGenUserKeyBarcode.setVisibility(View.VISIBLE);
                    imgPrivateKeyBarcode.setVisibility(View.GONE);
                }
            }
        }
    }

    private void showError_n_Back() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.emptylockerauth));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    back();
                } catch (ActivityNotFoundException e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AuthActivity showError_n_Back. ActivityNotFoundException: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void btnLockerResidential_Click(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.checkdeliverresidential));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    gcmp = objGlobalMethods.getGcmPreferences();
                    resetToOriginalAddressForMap(gcmp);
                    objGlobalMethods.setGCMPrefs(gcmp, "skiplocker", "t");
                    setDeliveredStatusActivity.dataT = null;
                    setDeliveredStatusActivity.imagePaths = null;
                    setDeliveredStatusActivity.mCurrentPhotoPath = null;
                    CheckListActivity.PhotoImagePathList = null;
                    CheckListActivity.dataTlist = null;
                    Intent intent = new Intent(myApp.currentActivity, JobInfoActivity.class);
                    startActivity(intent);
                    myApp.currentActivity.finish();
                } catch (ActivityNotFoundException e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("ActivityNotFoundException at AuthActivity btnLockerResidential_Click. ActivityNotFoundException: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void resetToOriginalAddressForMap(SharedPreferences gcmp) {
        try {
            String hfJobInfoJsonStr = gcmp.getString("hfJobInfoJsonStr", "");
            if (!objGlobalMethods.IsEmptyString(hfJobInfoJsonStr)) {
                Gson gson = new Gson();
                objJobInfo currJobInfo = gson.fromJson(gcmp.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                objAddressStructure oda = currJobInfo.DLLocation == null ? null : currJobInfo.DLLocation;
                Double LAT, LNG;
                String postal, AddressWithPostalNoUnit, addressWithUnit;
                LAT = oda.LAT;
                LNG = oda.LNG;
                postal = oda.POSTALCODE;
                AddressWithPostalNoUnit = objGlobalMethods.returnAddressWithPostalNoUnit(oda);
                addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(oda);

                String[] prefName = new String[]{"lrLAT", "lrLNG", "lrdesPostal", "lrdesAddress", "lraddressWithUnit"};
                String[] prefvalue = new String[]{String.valueOf(LAT), String.valueOf(LNG), postal, AddressWithPostalNoUnit, addressWithUnit};
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
            } else {
                objGlobalMethods.writeToFile("Unable to reset to Original Delivery Address latlng postal address. hfJobInfoJsonStr empty.", "", "", "", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AuthActivity resetToOriginalAddressForMap. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private boolean within200m() {
        boolean asd = false;
        try {
            String hfJobInfoJsonStr = gcmp.getString("hfJobInfoJsonStr", "");
            if (!objGlobalMethods.IsEmptyString(hfJobInfoJsonStr)) {
                Gson gson = new Gson();
                objJobInfo currJobInfo = gson.fromJson(gcmp.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                objAddressStructure oda = currJobInfo.DLLocation == null ? null : currJobInfo.DLLocation;
                objAddressStructure rda = currJobInfo.DLRedirectedLocation == null ? null : currJobInfo.DLRedirectedLocation;

                if (oda != null && rda != null) {
                    Location ol = new Location("ol");
                    Location rl = new Location("rl");
                    Double oLAT, oLNG, rLAT, rLNG;
                    oLAT = oda.LAT;
                    oLNG = oda.LNG;
                    rLAT = rda.LAT;
                    rLNG = rda.LNG;
                    ol.setLatitude(oLAT);
                    ol.setLongitude(oLNG);
                    rl.setLatitude(rLAT);
                    rl.setLongitude(rLNG);

                    float dist = ol.distanceTo(rl);
                    asd = dist <= 200;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AuthActivity within200m. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
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
                    objGlobalMethods.writeToFile("Bluetooth is on at AuthActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AuthActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}