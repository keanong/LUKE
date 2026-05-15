package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.SimpleStringList_PostData;
import com.xdel.luke.objClasses.SimpleStringList_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;

public class ItemGroupCheck extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    private static EditText txtIGCMANUALENTRY;
    private static Button btnIGCENTER;
    private static TextView lblIGCenrty1val;
    private static TextView lblJobsheetSubmitMISC;
    private static Button btnIGCRESET;
    private static ListView listView;
    private static Button btnIGCCANCEL;
    private ProgressDialog pd, pdCheck;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];
    private String inputTxt;
    private static String[] enrty1n2vals = null;
    private static ArrayList<SimpleStringList_PostData> scannedResults = null;
    private SimpleStringList_PostItemAdapter adapter;
    private static String cameraScannedCode;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;

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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showMobileNetworkSettingDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                        objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
                    objGlobalMethods.writeToFile("ActivityNotFoundException at MainActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        SharedPreferences GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
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

        setContentView(R.layout.activity_item_group_check);

        txtIGCMANUALENTRY = (EditText) findViewById(R.id.txtIGCMANUALENTRY);
        btnIGCENTER = (Button) findViewById(R.id.btnIGCENTER);

        lblIGCenrty1val = (TextView) findViewById(R.id.lblIGCenrty1val);
        lblJobsheetSubmitMISC = (TextView) findViewById(R.id.lblJobsheetSubmitMISC);

        btnIGCRESET = (Button) findViewById(R.id.btnIGCRESET);
        btnIGCCANCEL = (Button) findViewById(R.id.btnIGCCANCEL);


        listView = (ListView) findViewById(R.id.listView);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume() {
        SharedPreferences GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        }
        //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        context = myApp.getAppContext();
        //cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(null);

        myApp.currentActivity = this;
        objGlobalMethods.setGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX", "");

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
        }

        btnIGCENTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTxt = txtIGCMANUALENTRY.getText().toString();
                if (inputTxt.length() != 0 && inputTxt != "") {
                    checkFor2Strings();
                } else {
                    playErrorTone();
                    showDialog(myApp.getAppContext().getString(R.string.alert_entertagvalue));
                }
            }
        });

        txtIGCMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    if (txtIGCMANUALENTRY.getText().toString() != "") {
                        checkFor2Strings();
                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    } else {
                        playErrorTone();
                        showDialog(myApp.getAppContext().getString(R.string.alert_entertagvalue));
                    }


                } else if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (txtIGCMANUALENTRY.getText().toString() != "") {
                        checkFor2Strings();
                        if (getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    } else {
                        playErrorTone();
                        showDialog(myApp.getAppContext().getString(R.string.alert_entertagvalue));
                    }

                } else if (actionId == 6) {
                    return false;
                }
                return true;
            }
        });

        lblJobsheetSubmitMISC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enrty1n2vals != null) {
                    lblIGCenrty1val.setText("");
                    enrty1n2vals = null;
                    focusTextbox();
                }
            }
        });

        if (scannedResults != null && scannedResults.size() > 0 && adapter == null) {
            adapter = new SimpleStringList_PostItemAdapter(this, scannedResults);
            listView.setAdapter(adapter);
        }

        cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
        if (!cameraScannedCode.equals("")) {
            txtIGCMANUALENTRY.setText(cameraScannedCode);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            checkFor2Strings();
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ItemGroupCheck onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        DAO_Jobs.nullifystaticitems();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myApp.currentActivity = null;

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ItemGroupCheck onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_group_check, menu);
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

    //Start showDialog
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

    @Override
    public void onBackPressed() {
        scannedResults = null;
        enrty1n2vals = null;
        MainActivity.uponLogin = 0;
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        DrawerMainActivity.DashboardOpenFromPush = false;
        Intent parentIntent = new Intent(this, JobsheetActivity.class);
        startActivity(parentIntent);
        this.finish();

    }

    private void focusTextbox() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                listView.clearFocus();
                txtIGCMANUALENTRY.setText("");
                txtIGCMANUALENTRY.requestFocus();
            }
        }, 100);
    }

    public void btnIGCCANCELClick(View view) {
        onBackPressed();
    }

    public void btnIGCRESETClick(View view) {
        scannedResults = null;
        enrty1n2vals = null;
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        lblIGCenrty1val.setText("");
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    public void btnIGCENTERClick(View view) {
        if (txtIGCMANUALENTRY.getText().toString() != "") {
            checkFor2Strings();
        } else {
            playErrorTone();
            showDialog(myApp.getAppContext().getString(R.string.alert_entertagvalue));
        }

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
                        launchQRScan();
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        //TODO
                        launchQRScan();
                    }
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void launchQRScan() {
        Intent parentIntent;
        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "itemgroupcheck");
        parentIntent = new Intent(this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    private void checkFor2Strings() {
        try {
            String val = txtIGCMANUALENTRY.getText().toString();
            if (enrty1n2vals == null) {
                enrty1n2vals = new String[2];
                enrty1n2vals[0] = val;
                lblIGCenrty1val.setText(val.toUpperCase());
                focusTextbox();
            } else if (enrty1n2vals != null) {
                enrty1n2vals[1] = val;
                CheckTaggingGroup task = new CheckTaggingGroup(enrty1n2vals[0], enrty1n2vals[1]);
                task.execute();
            }
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at ItemGroupClick checkFor2Strings. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            String val1 = "";
            String val2 = "";
            if (enrty1n2vals != null) {
                if (enrty1n2vals[0] == null || enrty1n2vals[0] == "") {
                    val1 = "empty";
                } else {
                    val1 = enrty1n2vals[0];
                }
                if (enrty1n2vals[1] == null || enrty1n2vals[1] == "") {
                    val2 = "empty";
                } else {
                    val2 = enrty1n2vals[1];
                }
            }
            JobsHandler.sendErrorEmail("Exception caught at ItemGroupClick checkFor2Strings with value1 = " + val1 + ", value2 = " + val2 + ". Exception: " + e.getMessage().toString());
            showDialog(myApp.getAppContext().getString(R.string.alert_errorretrivedata2));
        }
    }

    public class CheckTaggingGroup extends AsyncTask<Void, Void, String> {
        private final String val1;
        private final String val2;

        CheckTaggingGroup(String aval1, String aval2) {
            this.val1 = aval1;
            this.val2 = aval2;
        }

        @Override
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            //android.os.Debug.waitForDebugger();
            //Call Web Method
            String result = WebService.CheckTaggingGroup(val1, val2);
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            pd.hide();
            Integer changeColor = 0;
            if (result.indexOf("[OK]") == 0) {
                //play success sound
                playSuccessTone();
                changeColor = 0;
            } else {
                //play error sound
                playErrorTone();
                changeColor = 2;
            }
            //display result
            appendToListview(result, changeColor);
        }
    }

    private void appendToListview(String val, Integer changeColor) {
        SimpleStringList_PostData data = new SimpleStringList_PostData(val, changeColor);
        if (scannedResults != null) {
            scannedResults.add(data);
            if (adapter == null) {
                adapter = new SimpleStringList_PostItemAdapter(this, scannedResults);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        } else {
            scannedResults = new ArrayList<SimpleStringList_PostData>();
            scannedResults.add(data);
            adapter = new SimpleStringList_PostItemAdapter(this, scannedResults);
            listView.setAdapter(adapter);
        }

        txtIGCMANUALENTRY.setText("");
        lblIGCenrty1val.setText("");
        enrty1n2vals = null;

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        //runs after listview is done updating itself
        listView.post(new Runnable() {
            public void run() {
                //scroll the listview to the last item
                listView.setSelection(listView.getCount() - 1);
                //focus certain view or any other calls after listview completed updating
                focusTextbox();
            }
        });

    }

    private void playErrorTone() {
        player = MediaPlayer.create(this, R.raw.error);
        player.start();
    }

    private void playSuccessTone() {
        player = MediaPlayer.create(this, R.raw.success);
        player.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at ItemGroupCheck", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ItemGroupCheck onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }


}
