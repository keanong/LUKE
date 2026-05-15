package com.xdel.luke.HandTakeOver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.MainActivity;
import com.xdel.luke.QR.Contents;
import com.xdel.luke.QR.QRCodeEncoder;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TakeoverActivity extends AppCompatActivity {
    private static ListView listView;
    private static String TOScannedIDnUsername = "";
    public static ArrayList<ho_PostData> scannedItems = null;
    private static EditText txtTOMANUALENTRY;
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    LinearLayout LLTOQR, LLTOItems;
    TextView lblTOID, lblTOIDval, lblTOCOUNT, lblTOCOUNTscan, lblHODriverIDX, lblTOtitle;
    ImageView imgQR;
    Button btnTOENTER, btnTOCANCEL, btnTOSCAN, btnTOPREPARE, btnTOCANCEL2, btnTOCANCELsubmit, btnTOSUBMIT;
    String cameraScannedCode;
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    SharedPreferences gcmp;

    to_PostItemAdapter adapter = null;
    NfcAdapter mNfcAdapter;
    private Uri verifiedToneURI = Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.doorbell);
    private ProgressDialog pd;
    TextView lblIsDefaultVRPpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gcmp = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(gcmp, "language");

        /*String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/

        setContentView(R.layout.activity_takeover);


        LLTOQR = findViewById(R.id.LLTOQR);
        LLTOItems = findViewById(R.id.LLTOItems);

        lblHODriverIDX = findViewById(R.id.lblHODriverIDX);

        imgQR = findViewById(R.id.imgQR);
        btnTOCANCEL = findViewById(R.id.btnTOCANCEL);

        txtTOMANUALENTRY = findViewById(R.id.txtTOMANUALENTRY);
        btnTOENTER = findViewById(R.id.btnTOENTER);
        lblTOID = findViewById(R.id.lblTOID);
        lblTOIDval = findViewById(R.id.lblTOIDval);
        lblTOCOUNT = findViewById(R.id.lblTOCOUNT);
        lblTOCOUNTscan = findViewById(R.id.lblTOCOUNTscan);
        listView = findViewById(R.id.listView);
        btnTOCANCEL2 = findViewById(R.id.btnTOCANCEL2);
        btnTOSCAN = findViewById(R.id.btnTOSCAN);
        btnTOPREPARE = findViewById(R.id.btnTOPREPARE);
        btnTOCANCELsubmit = findViewById(R.id.btnTOCANCELsubmit);
        btnTOSUBMIT = findViewById(R.id.btnTOSUBMIT);
        lblTOtitle = findViewById(R.id.lblTOtitle);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                gcmp = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(gcmp)) {
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                context = myApp.getAppContext();
                myApp.currentActivity = this;
                setTitle(myApp.getAppContext().getString(R.string.takeover));

                try {
                    if (mNfcAdapter != null && myApp.isNfcEnabled(mNfcAdapter, context)) {
                        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
                        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};
                        PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                        mNfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, nfcIntentFilter, null);
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                }

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("TakeoverActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                JobsHandler.checkForceLogOut();
                String[] prefName = new String[]{"dailypoints", "NFCLocationtagPrefVal", "CannotTakePhoto"};
                String[] prefvalue = new String[]{"", "", ""};
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
                DashboardSubActivity.frompage = null;
                lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

                cd = new ConnectionDetector(null);
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    Toast.makeText(context, context.getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
                }
                initPage();
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at TakeoverActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;
        try {
            if (mNfcAdapter != null)
                mNfcAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at TakeoverActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        listView = null;
        TOScannedIDnUsername = null;
        scannedItems = null;
        txtTOMANUALENTRY = null;
        imgQR = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at TakeoverActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        super.onDestroy();
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
                        launchQRScan(false);
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        //TODO
                        launchQRScan(false);
                    }
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void launchQRScan(boolean scanqr) {
        Intent parentIntent;
        gcmp = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"QRScanRequestIntent", "cameraScannedCode"};
        String[] prefvalue = new String[]{"takeover", ""};
        objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_takeover, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.clear) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_cleardata));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        clear();
                    } catch (Exception e) {
                        lblTOCOUNT.setText("0");
                        LLTOItems.setVisibility(View.GONE);
                        LLTOQR.setVisibility(View.VISIBLE);
                        imgQR.setOnClickListener(null);
                        imgQR.setOnClickListener(imgQROnClickListener());
                        lblTOtitle.setText(myApp.getAppContext().getString(R.string.totext_1));
                        focusTextbox();
                    }
                }
            });

            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        /*switch (item.getItemId()) {
            case R.id.clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_cleardata));
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            clear();
                        } catch (Exception e) {
                            lblTOCOUNT.setText("0");
                            LLTOItems.setVisibility(View.GONE);
                            LLTOQR.setVisibility(View.VISIBLE);
                            imgQR.setOnClickListener(null);
                            imgQR.setOnClickListener(imgQROnClickListener());
                            lblTOtitle.setText(myApp.getAppContext().getString(R.string.totext_1));
                            focusTextbox();
                        }
                    }
                });

                builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initBack();
    }

    private void initBack() {
        gcmp = objGlobalMethods.getGcmPreferences();
        String TOQRimgB64Str = objGlobalMethods.getGCMPrefs(gcmp, "TOQRimgB64Str");
        if ((scannedItems != null && scannedItems.size() > 0 && adapter != null) || !objGlobalMethods.IsEmptyString(TOQRimgB64Str)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_moveawayclearall));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    back();
                }
            });

            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            back();
        }
    }

    private void back() {
        gcmp = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"TOScannedIDnUsername", "cameraScannedCode", "TOQRimgB64Str", "to_json"};
        String[] prefvalue = new String[]{"", "", "", ""};
        objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
        scannedItems = null;
        Intent parentIntent;
        //parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        parentIntent = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    public View.OnClickListener imgQROnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScan(true);
            }
        };
    }

    private boolean isQRscan(String qrstr) {
        boolean asd = false;
        try {
            try {
                Gson gson = new Gson();
                hotoqr hqr = gson.fromJson(qrstr, hotoqr.class);
                asd = hqr != null;
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Unable to convert scan to hotoqr. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                asd = false;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity isQRscan. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private void initPage() {
        try {
            btnTOCANCEL.setVisibility(View.VISIBLE);
            btnTOCANCELsubmit.setVisibility(View.GONE);
            btnTOSUBMIT.setVisibility(View.GONE);
            imgQR.setOnClickListener(null);
            TOScannedIDnUsername = objGlobalMethods.getGCMPrefs(gcmp, "TOScannedIDnUsername");
            lblTOIDval.setText(TOScannedIDnUsername);
            String TOQRimgB64Str = objGlobalMethods.getGCMPrefs(gcmp, "TOQRimgB64Str");

            cameraScannedCode = objGlobalMethods.getGCMPrefs(gcmp, "cameraScannedCode");

            boolean scannedQR = ((scannedItems == null) || (scannedItems != null && scannedItems.size() == 0) || isQRscan(cameraScannedCode)) && objGlobalMethods.IsEmptyString(TOQRimgB64Str);
            LLTOItems.setVisibility(!scannedQR ? View.VISIBLE : View.GONE);
            LLTOQR.setVisibility(!scannedQR ? View.GONE : View.VISIBLE);
            if (scannedQR)
                imgQR.setOnClickListener(imgQROnClickListener());

            if (scannedQR) {
                if (!objGlobalMethods.IsEmptyString(cameraScannedCode)) {
                    objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
                    processHOTOQR(cameraScannedCode);
                }
            } else {
                if (!objGlobalMethods.IsEmptyString(TOQRimgB64Str)) {
                    displayQR(TOQRimgB64Str);
                } else if (!objGlobalMethods.IsEmptyString(cameraScannedCode)) {
                    txtTOMANUALENTRY.setText(cameraScannedCode);
                    objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
                    submitManualEntry();
                } else if (scannedItems != null && scannedItems.size() > 0 && adapter == null) {
                    adapter = new to_PostItemAdapter(this, scannedItems);
                    listView.setAdapter(adapter);
                    lblTOCOUNT.setText(scannedItems != null ? String.valueOf(scannedItems.size()) : "0");
                    //ctrlSettings.enableDisableSubmitButtonWithStyles(scannedItems != null ? true : false, btnTOPREPARE);
                    verify();
                    setScannedCount();
                }

                txtTOMANUALENTRY.setOnEditorActionListener(null);
                txtTOMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        gcmp = objGlobalMethods.getGcmPreferences();
                        objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
                        if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                            return false;
                        if (actionId == 0) {
                            //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                            submitManualEntry();
                            if (getCurrentFocus() != null) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                        } else if (actionId == EditorInfo.IME_ACTION_GO) {
                            submitManualEntry();
                            if (getCurrentFocus() != null) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                        } else if (actionId == 6) {
                            return false;
                        }
                        return true;
                    }
                });

                focusTextbox();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity initPage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void submitManualEntry() {
        try {
            if (!txtTOMANUALENTRY.getText().toString().equals("")) {
                int foundidx = -1;
                boolean jobFound = false;
                String scanned = txtTOMANUALENTRY.getText().toString().toUpperCase();
                objGlobalMethods.writeToFile("TO: " + scanned, "", "", "", "");

                if (scannedItems != null && scannedItems.size() > 0) {
                    for (int i = 0; i <= scannedItems.size() - 1; i++) {
                        if (scanned.equals(scannedItems.get(i).ref)) {
                            scannedItems.get(i).setSelected(true);
                            scannedItems.get(i).setBlink(true);
                            jobFound = true;
                            foundidx = i;
                            break;
                        }
                    }
                }

                if (jobFound) {
                    final int foundPos = foundidx;
                    listView.post(new Runnable() {
                        public void run() {
                            try {
                                //scroll the listview to the last item
                                listView.setSelection(foundPos);
                                //focus certain view or any other calls after listview completed updating
                            } catch (Exception e) {
                                objGlobalMethods.writeToFile("Exception at TakeoverActivity listView.setSelection(foundPos);. Exception: " + e.getMessage(), "", "", "", "");
                            }
                        }
                    });
                    adapter.notifyDataSetChanged();
                    playSuccessVerifiedTone();
                } else {
                    playTone();
                }
            } else {
                playTone();
                showDialog(myApp.getAppContext().getString(R.string.alert_enteratleastonejob));
            }
            verify();
            setScannedCount();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity submitManualEntry. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void setScannedCount() {
        int c = 0;
        try {
            if (scannedItems != null && scannedItems.size() > 0) {
                for (int i = 0; i <= scannedItems.size() - 1; i++) {
                    if (scannedItems.get(i).selected) {
                        c++;
                    }
                }
                lblTOCOUNTscan.setText(String.valueOf(c));
            } else {
                lblTOCOUNTscan.setText("0");
            }
        } catch (Exception e) {
            String s =  JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity setScannedCount. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public void verify() {
        boolean asd = true;
        try {
            if (scannedItems != null && scannedItems.size() > 0) {
                for (int i = 0; i <= scannedItems.size() - 1; i++) {
                    if (!scannedItems.get(i).selected) {
                        asd = false;
                        break;
                    }
                }
            } else {
                asd = false;
            }
            ctrlSettings.enableDisableSubmitButtonWithStyles(asd, btnTOPREPARE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity verify. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void processHOTOQR(String qrstr) {
        hotoqr hqr = null;
        ho_PostData data = null;
        String HODriverIDX = "";
        try {
            Gson gson = new Gson();
            try {
                hqr = gson.fromJson(qrstr, hotoqr.class);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Wrong QR code scanned at TakeoverActivity. Value scanned: " + qrstr, "", "", "", "");
                hqr = null;
            }

            if (hqr != null && hqr.scannedItems != null && hqr.scannedItems.size() > 0) {
                boolean found = false;
                for (int i = 0; i <= hqr.scannedItems.size() - 1; i++) {
                    found = false;
                    if (scannedItems == null) {
                        scannedItems = new ArrayList<ho_PostData>();
                    }
                    if (scannedItems != null && scannedItems.size() > 0) {
                        for (int f = 0; f <= scannedItems.size() - 1; f++) {
                            if (objGlobalMethods.IsEqualString(scannedItems.get(f).ref, hqr.scannedItems.get(i))) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        data = new ho_PostData(hqr.scannedItems.get(i), false);
                        scannedItems.add(data);
                    }
                }

                HODriverIDX = hqr.HODriverIDX;
                if (scannedItems != null && scannedItems.size() > 0) {
                    loadlist(HODriverIDX);
                } else {
                    scannedItems = null;
                    LLTOItems.setVisibility(View.GONE);
                    LLTOQR.setVisibility(View.VISIBLE);
                    imgQR.setOnClickListener(imgQROnClickListener());
                    showDialog(myApp.getAppContext().getString(R.string.unableprocess)).show();
                }
            } else {
                scannedItems = null;
                LLTOItems.setVisibility(View.GONE);
                LLTOQR.setVisibility(View.VISIBLE);
                imgQR.setOnClickListener(imgQROnClickListener());
                showDialog(myApp.getAppContext().getString(R.string.unableprocess)).show();
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity processHOTOQR. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void processTOQR() {
        to_json tj = null;
        String refTOhash = "";
        String TOQRimgB64Str = "";
        String jsonstr = "";
        String TODriverIDX = "";
        gcmp = objGlobalMethods.getGcmPreferences();
        try {
            TODriverIDX = lblTOIDval.getText().toString();
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            Gson gson = new Gson();
            refTOhash = getTORefHash(TODriverIDX, lblHODriverIDX.getText().toString(), true);
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();

            tj = new to_json(TODriverIDX, refTOhash, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude());
            jsonstr = gson.toJson(tj);
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(jsonstr,
                    null,
                    Contents.Type.TEXT,
                    BarcodeFormat.QR_CODE.toString(),
                    smallerDimension);
            try {
                Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                TOQRimgB64Str = JobsHandler.convertBitmapToBase64(bitmap);
                bitmap.recycle();
                bitmap = null;
            } catch (WriterException e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at TakeoverActivity generateQRstr generate qr bitmap. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }

            objGlobalMethods.setGCMPrefs(gcmp, "TOQRimgB64Str", TOQRimgB64Str);

            if (!objGlobalMethods.IsEmptyString(TOQRimgB64Str)) {
                tj.setScannedItems(scannedItems);
                jsonstr = gson.toJson(tj);
                objGlobalMethods.setGCMPrefs(gcmp, "to_json", jsonstr);
                displayQR(TOQRimgB64Str);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity processTOQR. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void displayQR(String TOQRimgB64Str) {
        try {
            LLTOItems.setVisibility(View.GONE);
            LLTOQR.setVisibility(View.VISIBLE);
            btnTOCANCEL.setVisibility(View.GONE);
            btnTOCANCELsubmit.setVisibility(View.VISIBLE);
            btnTOSUBMIT.setVisibility(View.VISIBLE);
            if (scannedItems != null) {
                scannedItems = null;
            }
            byte[] GcImg = Base64.decode(TOQRimgB64Str, Base64.DEFAULT);
            imgQR.setImageBitmap(BitmapFactory.decodeByteArray(GcImg, 0, GcImg.length));
            imgQR.setOnClickListener(null);
            lblTOtitle.setText(myApp.getAppContext().getString(R.string.totext_2));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity displayQR. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loadlist(String HODriverIDX) {
        try {
            adapter = new to_PostItemAdapter(myApp.getAppContext(), scannedItems);
            listView.setAdapter(adapter);
            LLTOItems.setVisibility(View.VISIBLE);
            LLTOQR.setVisibility(View.GONE);
            imgQR.setOnClickListener(null);
            lblTOCOUNT.setText(String.valueOf(scannedItems.size()));
            lblHODriverIDX.setText(HODriverIDX);
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnTOPREPARE);
            focusTextbox();
            verify();
        } catch (Exception e) {
            LLTOItems.setVisibility(View.GONE);
            LLTOQR.setVisibility(View.VISIBLE);
            imgQR.setOnClickListener(imgQROnClickListener());
            lblTOtitle.setText(myApp.getAppContext().getString(R.string.totext_1));
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity loadlist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void clear() {
        gcmp = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"TOQRimgB64Str", "cameraScannedCode"};
        String[] prefvalue = new String[]{"", ""};
        objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
        if (scannedItems != null) {
            scannedItems.clear();
            scannedItems = null;
        }
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            adapter = null;
        }
        lblTOCOUNT.setText("0");
        LLTOItems.setVisibility(View.GONE);
        LLTOQR.setVisibility(View.VISIBLE);
        imgQR.setOnClickListener(null);
        imgQR.setOnClickListener(imgQROnClickListener());
        imgQR.setImageResource(R.drawable.xdel_brandsig_rgb);
        lblTOtitle.setText(myApp.getAppContext().getString(R.string.totext_1));
        focusTextbox();
    }

    public String getTORefHash(String TODriverIDX, String HODriverIDX, boolean addsalt) {
        String l = "";
        String hash = "";
        String[] arr = null;
        try {
            arr = new String[scannedItems.size()];
            for (int i = 0; i <= scannedItems.size() - 1; i++) {
                arr[i] = scannedItems.get(i).ref;
            }

            Arrays.sort(arr);
            for (int i = 0; i <= arr.length - 1; i++) {
                l += "[" + arr[i] + "]";
            }
            l = HODriverIDX + l;
            hash = JobsHandler2.SHA1(l, addsalt);
            hash = TODriverIDX + hash;
            hash = JobsHandler2.SHA1(hash, addsalt);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity getTORefHash. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return hash;
        }
    }

    private class processHOTO extends AsyncTask<Void, Void, Boolean> {

        public processHOTO() {
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            String GUID = "";
            Gson gson = new Gson();
            gcmp = objGlobalMethods.getGcmPreferences();
            ArrayList<HOTOItem> hil = new ArrayList<>();
            HOTOItem hi = null;
            HOTO t = null;
            String to_jsonStr = "";
            to_json tj = null;

            try {
                to_jsonStr = objGlobalMethods.getGCMPrefs(gcmp, "to_json");
                tj = gson.fromJson(to_jsonStr, to_json.class);

                for (int i = 0; i <= tj.scannedItems.size() - 1; i++) {
                    hi = new HOTOItem(tj.scannedItems.get(i).ref);
                    hil.add(hi);
                }

                //GUID = UUID.randomUUID().toString();
                GUID = tj.refTOHash;
                t = new HOTO();
                t.setGUID(GUID);
                t.setTO_DriverIDX(Long.parseLong(tj.TODriverIDX));
                t.setTO_Timestamp(tj.TO_Timestamp);
                t.setTO_LAT(tj.TO_LAT);
                t.setTO_LNG(tj.TO_LNG);
                t.setItems(hil);
                t.setFlag(512);
                t.setHO_DriverIDX(Long.parseLong(lblHODriverIDX.getText().toString()));

                String ARegID = objGlobalMethods.getRegistrationIdOnly();

                asd = WebService.L2_SubmitHOTO(ARegID, t);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at TakeoverActivity processHOTO doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean r) {
            Context ctx = myApp.getAppContext();
            try {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                String[] prefName;
                String[] prefvalue;
                prefName = new String[]{"cameraScannedCode"};
                prefvalue = new String[]{""};
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);

                if (r) {
                    objGlobalMethods.writeToFile("HOTO successful.", "", "", "", "");
                    showSuccessAlert();
                } else {
                    objGlobalMethods.writeToFile("HOTO unsuccessful.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                            ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            } catch (Exception e) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                pd = null;
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at TakeoverActivity processHOTO onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                        ctx.getString(R.string.alert_errorperformoperation)).show();
            }
        }
    }

    public void btnTOENTERClick(View v) {
        try {
            submitManualEntry();
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity btnTOENTERClick. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnbtnTOSCANClick(View v) {
        launchQRScan(true);
    }

    public void btnTOCANCELClick(View v) {
        initBack();
    }

    public void btnTOCANCEL2Click(View v) {
        initBack();
    }

    public void btnTOPREPAREClick(View v) {
        try {
            if (scannedItems != null && scannedItems.size() > 0) {
                processTOQR();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at TakeoverActivity btnTOPREPAREClick. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    public void btnTOSUBMITClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_ccSubmit));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                processHOTO p = new processHOTO();
                p.execute();
            }
        });

        builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void stopPlayingTone() {
        try {
            if (player != null) {
                player.reset();
                //player.prepare();
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at TakeoverActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(myApp.currentActivity, Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error));
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at TakeoverActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at TakeoverActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void showSuccessAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_data_submittedsuccess));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                back();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void focusTextbox() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                listView.clearFocus();
                txtTOMANUALENTRY.setText("");
                txtTOMANUALENTRY.requestFocus();
            }
        }, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at TakeoverActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at TakeoverActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}