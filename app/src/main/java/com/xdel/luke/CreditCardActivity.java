package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.CreditCard_PostData;
import com.xdel.luke.objClasses.CreditCard_PostItemAdapter;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCCimportData;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objStringWTag;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;

public class CreditCardActivity extends AppCompatActivity {

    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;

    private static ListView listViewCreditCard;
    private static TextView lblCreditCardQUEVal;
    public static EditText txtCreditCardCNNO;
    private static EditText txtCreditCardPOSTAL;
    private static EditText txtCreditCardUnit;
    private static EditText txtCreditCardName;
    private static EditText txtCreditCardContactNo;
    private static EditText currET;
    private static CheckBox cbCreditCardHOUR;
    private static Button btnCreditCardENTER;
    private static Button btnCreditCardCLEARALL;
    private static Button btnJCreditCardCANCEL;
    private static Button btnCreditCardSUBMIT;
    private static Button btnJCreditCardCiti;
    private static Button btnJCreditCarddbs;
    private ProgressDialog pd, pdCheck;

    static boolean errored = false;
    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    public static ArrayList<CreditCard_PostData> scannedItems = null;
    CreditCard_PostItemAdapter adapter = null;
    static String cameraScannedCode;
    NfcAdapter mNfcAdapter;
    ScreenShotContentObserver screenShotContentObserver;
    TextView lblIsDefaultVRPpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_credit_card);

        listViewCreditCard = (ListView) findViewById(R.id.listViewCreditCard);
        lblCreditCardQUEVal = (TextView) findViewById(R.id.lblCreditCardQUEVal);
        txtCreditCardCNNO = (EditText) findViewById(R.id.txtCreditCardCNNO);
        txtCreditCardPOSTAL = (EditText) findViewById(R.id.txtCreditCardPOSTAL);
        txtCreditCardUnit = (EditText) findViewById(R.id.txtCreditCardUnit);
        txtCreditCardName = (EditText) findViewById(R.id.txtCreditCardName);
        txtCreditCardContactNo = (EditText) findViewById(R.id.txtCreditCardContactNo);
        cbCreditCardHOUR = (CheckBox) findViewById(R.id.cbCreditCardHOUR);
        btnCreditCardENTER = (Button) findViewById(R.id.btnCreditCardENTER);
        btnCreditCardCLEARALL = (Button) findViewById(R.id.btnCreditCardCLEARALL);
        btnJCreditCardCANCEL = (Button) findViewById(R.id.btnJCreditCardCANCEL);
        btnCreditCardSUBMIT = (Button) findViewById(R.id.btnCreditCardSUBMIT);
        btnJCreditCardCiti = (Button) findViewById(R.id.btnJCreditCardCiti);
        btnJCreditCarddbs = (Button) findViewById(R.id.btnJCreditCarddbs);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        //pd.setMessage("Please wait. Submitting Info...");
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
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            context = getApplicationContext();

            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            myApp.currentActivity = this;

            int appFlag = myApp.getDeviceSettingsFlag();
            if (myApp.isAirplaneModeOnFlag(appFlag)) {
                showChangeAirplaneModeSettingsDialog();
                return;
            }
            if (myApp.isGpsOnFlag(appFlag)) {
                showChangeGPSModeSettingsDialog();
                return;
            }

            if (myApp.isAllPermissionGrantedFlag(appFlag)) {
                showChangePermissionDialog();
                return;
            }

            if (myApp.GPSisHighAccFlag(appFlag)) {
                showChangeGPStoHighAccModeSettingsDialog();
                return;
            }

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("CreditCardActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

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
                String stack = JobsHandler.getStackTraceString(e);
                //objGlobalMethods.writeToFile("Exception caught at CreditCardActivity onResume mNfcAdapter portion. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            }

            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

            JobsHandler.checkForceLogOut();
            objGlobalMethods.clearOnResumeCreditCardActivityGcmPrefs(GcmPreferences);
            DashboardSubActivity.frompage = null;

            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);

            if (!cd.isConnectingToInternet()) {
                // Internet Connection is not present
                //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
            }
            initPage();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (txtCreditCardCNNO != null) {
                txtCreditCardCNNO.setOnEditorActionListener(null);
            }
            if (txtCreditCardPOSTAL != null) {
                txtCreditCardPOSTAL.setOnEditorActionListener(null);
                txtCreditCardPOSTAL.setOnFocusChangeListener(null);
            }
            if (txtCreditCardName != null) {
                txtCreditCardName.setOnEditorActionListener(null);
                txtCreditCardName.setOnFocusChangeListener(null);
            }
            if (txtCreditCardUnit != null) {
                txtCreditCardUnit.setOnEditorActionListener(null);
                txtCreditCardUnit.setOnFocusChangeListener(null);
            }
            if (txtCreditCardContactNo != null) {
                txtCreditCardContactNo.setOnEditorActionListener(null);
                txtCreditCardContactNo.setOnFocusChangeListener(null);
            }

            listViewCreditCard = null;
            lblCreditCardQUEVal = null;
            txtCreditCardCNNO = null;
            txtCreditCardPOSTAL = null;
            txtCreditCardUnit = null;
            txtCreditCardName = null;
            txtCreditCardContactNo = null;
            currET = null;
            cbCreditCardHOUR = null;
            btnCreditCardENTER = null;
            btnCreditCardCLEARALL = null;
            btnJCreditCardCANCEL = null;
            btnCreditCardSUBMIT = null;
            btnJCreditCardCiti = null;
            btnJCreditCarddbs = null;
            GcmPreferences = null;
            UpdateJobInfoPreferences = null;
            scannedItems = null;
            //objGlobalMethods.clearStaticActivities();

            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CreditCardActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        super.onDestroy();
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
            objGlobalMethods.writeToFile("Exception at CreditCardActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CreditCardActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void showMobileNetworkSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_mobiledatamode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    /*Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
                    startActivity(intent);*/

                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (ActivityNotFoundException e) {
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CreditCardActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAirplaneMode);
                    } catch (ActivityNotFoundException e) {
                        objGlobalMethods.writeToFile("ActivityNotFoundException at CreditCardActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }
                } else {
                    Intent intent1 = new Intent("android.settings.WIRELESS_SETTINGS");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPSModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CreditCardActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangeGPStoHighAccModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmodehighacc));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("ActivityNotFoundException at CreditCardActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CreditCardActivity showChangeGPStoHighAccModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showChangePermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpspermission));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            //builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("ActivityNotFoundException at CreditCardActivity showChangePermissionDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("ActivityNotFoundException at CreditCardActivity showChangePermissionDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
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
            default:
                return super.dispatchKeyEvent(event);
        }

    }

    private void launchQRScan() {
        Intent parentIntent;
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "creditcard");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        parentIntent = new Intent(CreditCardActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credit_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        }

        /*Intent parentIntent;
        switch (item.getItemId()) {
            case R.id.qrscan:
                launchQRScan();
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
        if (scannedItems != null && scannedItems.size() > 0 && adapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_moveawayclearall));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    back();
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
        } else {
            back();
        }
    }

    private void back() {
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        scannedItems = null;
        //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        this.finish();
    }

    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    private void showSubmissionOKAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.data_submitted));
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                scannedItems = null;
                Intent parentIntent;
                //parentIntent = new Intent(CreditCardActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                parentIntent = new Intent(CreditCardActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initPage() {

        if (scannedItems != null) {
            lblCreditCardQUEVal.setText(String.valueOf(scannedItems.size()));
            //ctrlSettings.enableDisableSubmitButton(true, btnCreditCardSUBMIT);
            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnCreditCardSUBMIT);
        } else {
            lblCreditCardQUEVal.setText("0");
            //ctrlSettings.enableDisableSubmitButton(false, btnCreditCardSUBMIT);
            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnCreditCardSUBMIT);
        }

        txtCreditCardCNNO.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    txtCreditCardPOSTAL.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtCreditCardPOSTAL, InputMethodManager.SHOW_IMPLICIT);

                    /*if (objGlobalMethods.IsEmptyString(txtCreditCardCNNO.getText().toString())) {
                        playTone();
                        //showDialog("Consignment Note is empty.");
                        txtCreditCardCNNO.requestFocus();
                        showDialog(myApp.getAppContext().getString(R.string.alert_plsenterCN));
                    } else {
                        txtCreditCardPOSTAL.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(txtCreditCardPOSTAL, InputMethodManager.SHOW_IMPLICIT);
                    }*/
                } else if (actionId == 6) {
                    return false;
                }
                return true;
            }
        });

        txtCreditCardPOSTAL.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    txtCreditCardName.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtCreditCardName, InputMethodManager.SHOW_IMPLICIT);

                    /*if (objGlobalMethods.IsEmptyString(txtCreditCardPOSTAL.getText().toString())) {
                        playTone();
                        txtCreditCardPOSTAL.requestFocus();
                        showDialog(myApp.getAppContext().getString(R.string.alert_plsenterPostal));
                    } else if (txtCreditCardPOSTAL.getText().toString().length() != 6) {
                        playTone();
                        txtCreditCardPOSTAL.requestFocus();
                        showDialog(myApp.getAppContext().getString(R.string.alert_postal6digit));
                    } else {
                        txtCreditCardName.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(txtCreditCardName, InputMethodManager.SHOW_IMPLICIT);
                    }*/
                } else if (actionId == 6) {
                    return false;
                }
                return true;
            }
        });

        txtCreditCardPOSTAL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.qrscan).setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtCreditCardPOSTAL, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    findViewById(R.id.qrscan).setVisibility(View.VISIBLE);
                }
            }
        });

        txtCreditCardName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.qrscan).setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtCreditCardName, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    findViewById(R.id.qrscan).setVisibility(View.VISIBLE);
                }
            }
        });

        txtCreditCardUnit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.qrscan).setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtCreditCardUnit, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    findViewById(R.id.qrscan).setVisibility(View.VISIBLE);
                }
            }
        });

        txtCreditCardContactNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    findViewById(R.id.qrscan).setVisibility(View.GONE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtCreditCardContactNo, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    findViewById(R.id.qrscan).setVisibility(View.VISIBLE);
                }
            }
        });

        txtCreditCardName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    txtCreditCardUnit.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtCreditCardUnit, InputMethodManager.SHOW_IMPLICIT);

                    /*if (objGlobalMethods.IsEmptyString(txtCreditCardName.getText().toString())) {
                        playTone();
                        txtCreditCardName.requestFocus();
                        showDialog(myApp.getAppContext().getString(R.string.alert_plsentername));
                    } else {
                        txtCreditCardUnit.requestFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(txtCreditCardUnit, InputMethodManager.SHOW_IMPLICIT);
                    }*/
                } else if (actionId == 6) {
                    return false;
                }
                return true;
            }
        });

        txtCreditCardUnit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    //unit no is optional
                    txtCreditCardContactNo.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtCreditCardContactNo, InputMethodManager.SHOW_IMPLICIT);
                } else if (actionId == 6) {
                    return false;
                }
                return true;
            }
        });

        txtCreditCardContactNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    //contact no is optional
                    txtCreditCardContactNo.requestFocus();
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

        /*txtCreditCardPOSTAL.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    if (!txtCreditCardPOSTAL.getText().toString().isEmpty()) {
                        submitManualEntry();
                        txtCreditCardCNNO.requestFocus();
                    } else {
                        playTone();
                        //showDialog("Postal Code is empty.");
                        showDialog(myApp.getAppContext().getString(R.string.alert_plsenterPostal));
                    }
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                } else if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (!txtCreditCardPOSTAL.getText().toString().isEmpty()) {
                        submitManualEntry();
                        txtCreditCardCNNO.requestFocus();
                    } else {
                        playTone();
                        //showDialog("Postal Code is empty.");
                        showDialog(myApp.getAppContext().getString(R.string.alert_plsenterPostal));
                    }
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }

                } else if (actionId == 6) {
                    return false;
                }
                return true;
            }
        });*/

        cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
        if (!objGlobalMethods.IsEmptyString(cameraScannedCode)) {
            txtCreditCardCNNO.setText(cameraScannedCode);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            txtCreditCardPOSTAL.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(txtCreditCardPOSTAL, InputMethodManager.SHOW_IMPLICIT);
        }

        if (scannedItems != null && scannedItems.size() > 0 && adapter == null) {
            adapter = new CreditCard_PostItemAdapter(this, scannedItems);
            listViewCreditCard.setAdapter(adapter);
        }

        listViewCreditCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View v = listViewCreditCard.getChildAt(position - listViewCreditCard.getFirstVisiblePosition());
                final int deleteRow = position;
                if (v == null)
                    return;

                TextView lblJobsheetItemTitle = (TextView) v.findViewById(R.id.lblCreditCardItemTitle);

                AlertDialog.Builder builder = new AlertDialog.Builder(CreditCardActivity.this);
                builder.setCancelable(false);
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetcfmDelete) + " " + lblJobsheetItemTitle.getText().toString() + " ?");
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        scannedItems.remove(deleteRow);
                        if (scannedItems.size() > 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnCreditCardSUBMIT);
                        } else if (scannedItems.size() == 0) {
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnCreditCardSUBMIT);
                        }
                        lblCreditCardQUEVal.setText(String.valueOf(scannedItems.size()));
                        adapter.notifyDataSetChanged();
                        focusTextbox();
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
        });

        String citidbshsbc = objGlobalMethods.getGCMPrefs(GcmPreferences, "citidbshsbc");
        toggleCitiDBSbtn(citidbshsbc);
    }

    private void submitManualEntry() {
        if (txtCreditCardCNNO.getText().toString().toUpperCase().indexOf(objGlobalVariables.EIDTPOD) != 0) {
            if (!objGlobalMethods.IsEmptyString(txtCreditCardCNNO.getText().toString()) &&
                    !objGlobalMethods.IsEmptyString(txtCreditCardPOSTAL.getText().toString()) &&
                    txtCreditCardPOSTAL.getText().toString().length() == 6 &&
                    !objGlobalMethods.IsEmptyString(txtCreditCardName.getText().toString())) {
                boolean jobFound = false;
                View v;
                TextView lblCnno;
                String currentScannedItem = "";
                CreditCard_PostData data = null;
                String Cnno = "";
                String scanned = txtCreditCardCNNO.getText().toString();
                String postal = txtCreditCardPOSTAL.getText().toString();
                String name = txtCreditCardName.getText().toString();
                String unit = objGlobalMethods.IsEmptyString(txtCreditCardUnit.getText().toString()) ? "" : txtCreditCardUnit.getText().toString();
                String mobile = objGlobalMethods.IsEmptyString(txtCreditCardContactNo.getText().toString()) ? "" : txtCreditCardContactNo.getText().toString();
                objStringWTag swt = null;

                for (int i = 0; i <= listViewCreditCard.getCount() - 1; i++) {
                    v = listViewCreditCard.getAdapter().getView(i, null, null);
                    lblCnno = (TextView) v.findViewById(R.id.lblCreditCardItemTitle);
                    Cnno = lblCnno.getText().toString();

                    if (scanned.equals(Cnno)) {
                        jobFound = true;
                        break;
                    }
                }

                if (!jobFound) {
                    /*swt = new objStringWTag(scanned.toUpperCase(), txtCreditCardPOSTAL.getText().toString().toUpperCase());
                    currentScannedItem = scanned.toUpperCase().toUpperCase() + ", " + txtCreditCardPOSTAL.getText().toString().toUpperCase();
                    data = new CreditCard_PostData(scanned.toUpperCase(), txtCreditCardPOSTAL.getText().toString().toUpperCase(), currentScannedItem, swt);*/
                    data = new CreditCard_PostData(postal.toUpperCase(), scanned.toUpperCase(), unit.toUpperCase(), name.toUpperCase(), mobile);

                    if (scannedItems != null) {
                        scannedItems.add(data);
                        if (adapter == null) {
                            adapter = new CreditCard_PostItemAdapter(this, scannedItems);
                            listViewCreditCard.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        scannedItems = new ArrayList<CreditCard_PostData>();
                        scannedItems.add(data);
                        adapter = new CreditCard_PostItemAdapter(this, scannedItems);
                        listViewCreditCard.setAdapter(adapter);
                    }

                    //runs after listview is done updating itself
                    listViewCreditCard.post(new Runnable() {
                        public void run() {
                            //scroll the listview to the last item
                            listViewCreditCard.setSelection(listViewCreditCard.getCount() - 1);
                            //focus certain view or any other calls after listview completed updating
                            CreditCardActivity.txtCreditCardCNNO.requestFocus();
                        }
                    });

                    lblCreditCardQUEVal.setText(String.valueOf(scannedItems.size()));
                    ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnCreditCardSUBMIT);
                    focusTextbox();
                } else {
                    playTone();
                    showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_duplicatescan) + "\n\n" + txtCreditCardCNNO.getText().toString());
                }
            } else {
                playTone();
                Context ctx = myApp.getAppContext();
                String errmsg = "";
                if (objGlobalMethods.IsEmptyString(txtCreditCardCNNO.getText().toString())) {
                    errmsg += "\n" + ctx.getString(R.string.alert_plsenterCN);
                }
                if (objGlobalMethods.IsEmptyString(txtCreditCardPOSTAL.getText().toString())) {
                    errmsg += "\n" + ctx.getString(R.string.alert_plsenterPostal);
                }
                if (txtCreditCardPOSTAL.getText().toString().length() != 6) {
                    errmsg += "\n" + ctx.getString(R.string.alert_postal6digit);
                }
                if (objGlobalMethods.IsEmptyString(txtCreditCardName.getText().toString())) {
                    errmsg += "\n" + ctx.getString(R.string.alert_plsentername);
                }
                showDialog(errmsg);
                //showDialog(myApp.getAppContext().getString(R.string.alert_plsenterCNPostal));
            }
        } else {
            focusTextbox();
        }
    }

    public void btnCreditCardENTERClick(View view) {
        submitManualEntry();
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void btnJCreditCardCANCELClick(View view) {
        initBack();
    }

    public void btnCreditCardCLEARALLClick(View view) {
        if (scannedItems != null && scannedItems.size() > 0 && adapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_cleardata));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    try {
                        clear();
                    } catch (Exception e) {
                        lblCreditCardQUEVal.setText("0");
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnCreditCardSUBMIT);
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
    }

    public void btnCreditCardCitiClick(View view) {
        toggleCitiDBSbtn("0");
    }

    public void btnCreditCardDbsClick(View view) {
        cbCreditCardHOUR.setChecked(false);
        toggleCitiDBSbtn("1");
    }

    private void toggleCitiDBSbtn(String citidbshsbc) {
        if (citidbshsbc.equals("0")) {
            //citi
            setTitle(myApp.getAppContext().getString(R.string.item_dashboard2CCciti));
            cbCreditCardHOUR.setVisibility(View.VISIBLE);
            ctrlSettings.tottleButtonsWithStyles(true, btnJCreditCardCiti);
            ctrlSettings.tottleButtonsWithStyles(false, btnJCreditCarddbs);
        } else if (citidbshsbc.equals("1")) {
            //dbs
            setTitle(myApp.getAppContext().getString(R.string.item_dashboard2CCdbs));
            cbCreditCardHOUR.setVisibility(View.INVISIBLE);
            ctrlSettings.tottleButtonsWithStyles(true, btnJCreditCarddbs);
            ctrlSettings.tottleButtonsWithStyles(false, btnJCreditCardCiti);
        }
        objGlobalMethods.setGCMPrefs(GcmPreferences, "citidbshsbc", citidbshsbc);
    }

    private void clear() {
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        scannedItems.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnCreditCardSUBMIT);
        lblCreditCardQUEVal.setText(String.valueOf(scannedItems.size()));
        focusTextbox();
    }

    public void btnCreditCardSUBMITClick(View view) {
        try {
            if (scannedItems != null && scannedItems.size() > 0) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                if (cbCreditCardHOUR.isChecked()) {
                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_ccSubmit3_5H));
                } else {
                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_ccSubmit));
                }

                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        try {
                            GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                            UpdateJobInfoPreferences = getSharedPreferences("UpdateJobInfoPrefs", Context.MODE_PRIVATE);

                            if (cbCreditCardHOUR.isChecked()) {
                                //submit(true, scannedItems, GcmPreferences, UpdateJobInfoPreferences);
                                submitnew(true, scannedItems);
                            } else {
                                //submit(false, scannedItems, GcmPreferences, UpdateJobInfoPreferences);
                                submitnew(false, scannedItems);
                            }
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception caught at CreditCardActivity btnCreditCardSUBMITClick. Exception: " + e.toString(), "", "", "", "");
                            showDialog(myApp.getAppContext().getString(R.string.alert_errSubmitJobsheet));
                        }
                    }
                });

                builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        focusTextbox();
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_enteratleastonejob));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at CreditCardActivity btnCreditCardSUBMITClick. Exception: " + e.toString(), "", "", "", "");
        }
    }

    private void submitnew(Boolean cbCreditCardHOURChecked, ArrayList<CreditCard_PostData> scannedItemsAL) {
        boolean asd = true;
        try {
            objGlobalMethods.writeToFile("CreditCardActivity submitnew started.", "", "", "", "");

            try {
                if (scannedItemsAL.size() > 0) {
                    CreditCard_PostData[] AJobs = new CreditCard_PostData[scannedItemsAL.size()];
                    AJobs = scannedItemsAL.toArray(AJobs);

                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];


                    String citidbshsbc = objGlobalMethods.getGCMPrefs(GcmPreferences, "citidbshsbc");

                    if (citidbshsbc.equals("") || citidbshsbc.equals("0")) {
                        //citi
                        objCCimportData CCimportData = new objCCimportData(cbCreditCardHOURChecked ? 2 : 1, AJobs);
                        JobsHandler.addUploadCreditCardQueALPrefs2(UpdateJobInfoPreferences, CCimportData);
                        /*if (cbCreditCardHOURChecked) {
                            objCCimportData CCimportData = new objCCimportData(2, AJobs);
                            JobsHandler.addUploadCreditCard3HQueALPrefs2(UpdateJobInfoPreferences, CCimportData);
                        } else {
                            objCCimportData CCimportData = new objCCimportData(1, AJobs);
                            JobsHandler.addUploadCreditCardQueALPrefs2(UpdateJobInfoPreferences, CCimportData);
                        }*/
                    } else if (citidbshsbc.equals("1")) {
                        //dbs
                        objCCimportData CCimportData = new objCCimportData(3, AJobs);
                        JobsHandler.addUploadCreditCardQueALPrefs2(UpdateJobInfoPreferences, CCimportData);
                    }

                    Context ctx = myApp.getAppContext();
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                    asd = true;
                } else {
                    asd = false;
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at CreditCardActivity submitnew inner TryCatch. Exception: " + e.toString(), "", "", "", "");
                asd = false;
            }

            objGlobalMethods.writeToFile("CreditCardActivity submitnew ended.", "", "", "", "");

            if (asd) {
                scannedItems = null;
                showSubmissionOKAlert();
            } else {
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_submittedfail));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at CreditCardActivity submitnew. Exception: " + e.toString(), "", "", "", "");
            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_submittedfail));
        }
    }

    private void submit(Boolean cbCreditCardHOURChecked, ArrayList<CreditCard_PostData> scannedItemsAL, SharedPreferences gcmp, SharedPreferences updatesp) {
        /*ArrayList SWTDriverIDXal = null;
        ArrayList<objStringWTag> SWTarr = null;
        Boolean asd = true;

        try {
            objGlobalMethods.writeToFile("CreditCardActivity submit started.", "", "", "", "");

            SWTarr = new ArrayList<objStringWTag>();
            try {
                for (int i = 0; i <= scannedItemsAL.size() - 1; i++) {
                    SWTarr.add(scannedItemsAL.get(i).swt);
                }

                if (SWTarr.size() > 0) {
                    SWTDriverIDXal = new ArrayList();
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    useridx = UsernamePwd[2];
                    ishomesuser = UsernamePwd[3];
                    SWTDriverIDXal.add(useridx);
                    SWTDriverIDXal.add(SWTarr);

                    String citidbshsbc = objGlobalMethods.getGCMPrefs(GcmPreferences, "citidbshsbc");
                    Integer citidbshsbcInt = 0;

                    if (citidbshsbc.equals("") || citidbshsbc.equals("0")) {
                        ////citi
                        if (cbCreditCardHOURChecked) {
                            citidbshsbcInt = 0;
                            objCCimportData CCimportData = new objCCimportData(citidbshsbcInt, SWTDriverIDXal);
                            JobsHandler.addUploadCreditCard3HQueALPrefs2(UpdateJobInfoPreferences, CCimportData);
                        } else {
                            citidbshsbcInt = 0;
                            objCCimportData CCimportData = new objCCimportData(citidbshsbcInt, SWTDriverIDXal);
                            JobsHandler.addUploadCreditCardQueALPrefs2(UpdateJobInfoPreferences, CCimportData);
                        }
                    } else if (citidbshsbc.equals("1")) {
                        ////dbs
                        citidbshsbcInt = 1;
                        objCCimportData CCimportData = new objCCimportData(citidbshsbcInt, SWTDriverIDXal);
                        JobsHandler.addUploadCreditCardQueALPrefs2(UpdateJobInfoPreferences, CCimportData);
                    }

                    Context ctx = myApp.getAppContext();
                    sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                    asd = true;
                } else if (SWTarr.size() == 0) {
                    asd = false;
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at CreditCardActivity submit inner TryCatch. Exception: " + e.toString(), "", "", "", "");
                asd = false;
            }

            objGlobalMethods.writeToFile("CreditCardActivity submit ended.", "", "", "", "");

            if (asd) {
                scannedItems = null;
                showSubmissionOKAlert();
            } else {
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_submittedfail));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at CreditCardActivity submit. Exception: " + e.toString(), "", "", "", "");
            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_submittedfail));
        }*/
    }

    public static void focusTextbox() {
        listViewCreditCard.clearFocus();
        listViewCreditCard.setFocusable(false);
        cbCreditCardHOUR.clearFocus();
        btnCreditCardENTER.clearFocus();
        btnCreditCardCLEARALL.clearFocus();
        btnJCreditCardCANCEL.clearFocus();
        btnCreditCardSUBMIT.clearFocus();
        lblCreditCardQUEVal.clearFocus();

        txtCreditCardPOSTAL.setText("");
        txtCreditCardPOSTAL.clearFocus();
        txtCreditCardPOSTAL.setFocusable(true);
        txtCreditCardPOSTAL.setFocusableInTouchMode(true);

        txtCreditCardCNNO.setFocusable(true);
        txtCreditCardCNNO.setFocusableInTouchMode(true);
        txtCreditCardCNNO.setText("");
        txtCreditCardCNNO.requestFocus();

        txtCreditCardUnit.setText("");
        txtCreditCardUnit.clearFocus();
        txtCreditCardUnit.setFocusable(true);
        txtCreditCardUnit.setFocusableInTouchMode(true);

        txtCreditCardName.setText("");
        txtCreditCardName.clearFocus();
        txtCreditCardName.setFocusable(true);
        txtCreditCardName.setFocusableInTouchMode(true);

        txtCreditCardContactNo.setText("");
        txtCreditCardContactNo.clearFocus();
        txtCreditCardContactNo.setFocusable(true);
        txtCreditCardContactNo.setFocusableInTouchMode(true);
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
            objGlobalMethods.writeToFile("Exception at CreditCardActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private void playTone() {
        /*player = MediaPlayer.create(CreditCardActivity.this, R.raw.error);
        player.start();*/
        try {
            stopPlayingTone();
            player = new MediaPlayer();
            player.setDataSource(CreditCardActivity.this, Uri.parse("android.resource://" + myApp.getAppContext().getPackageName() + "/" + R.raw.error));
            player.prepare();
            player.start();
        } catch (Exception e) {
            stopPlayingTone();
            objGlobalMethods.writeToFile("Exception at CreditCardActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    private Dialog showDialogNfocusTxtbox(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                try {
                    lblCreditCardQUEVal.setText(String.valueOf(scannedItems.size()));
                    focusTextbox();
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at CreditCardActivity showDialogNfocusTxtbox. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // TODO: handle Intent
        /*Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if(tag != null) {
            new NdefReaderTask().execute(tag);
        }*/

        try {
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            String action = intent.getAction();
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {


            } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {


            } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
                return;
            }

        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at CreditCardActivity handleIntent. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception at CreditCardActivity handleIntent. Exception: " + e.getMessage().toString(), "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
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
                    objGlobalMethods.writeToFile("Bluetooth is on at CreditCardActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CreditCardActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
