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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;

//import android.support.v7.app.ActionBarActivity;


//public class CheckStatusActivity extends ActionBarActivity {
public class CheckStatusActivity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    static boolean errored = false;
    private ProgressDialog pd;
    private ProgressDialog pdRefresh;
    private ListView listView;
    private Button btnSubmit;
    private EditText txtInput;
    private TextView lblResult;
    private String lblResultTxt;
    private String inputTxt;
    public String status = "";
    private static ArrayList<SimpleStringList_PostData> scannedItems = null;
    SimpleStringList_PostItemAdapter adapter;
    static String cameraScannedCode;
    private static SharedPreferences GcmPreferences;
    ScreenShotContentObserver screenShotContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_check_status);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView) findViewById(R.id.listView);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        txtInput = (EditText) findViewById(R.id.txtInput);
        //lblResult = (TextView) findViewById(R.id.lblResult);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        //pd.setMessage("Please wait. Retrieving Info...");
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitRetriveInfo));
        pdRefresh = new ProgressDialog(this);
        pdRefresh.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdRefresh.setCanceledOnTouchOutside(false);
        //pdRefresh.setMessage("Please wait. Refreshing...");
        pdRefresh.setMessage(myApp.getAppContext().getString(R.string.pd_refreshing));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        }
        //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        context = myApp.getAppContext();
        //cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(null);

        myApp.currentActivity = this;

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputTxt = txtInput.getText().toString();
                if (inputTxt.length() != 0 && !inputTxt.equals("")) {
                    checkJobStatus task = new checkJobStatus(txtInput.getText().toString());
                    task.execute();
                }
            }
        });

        txtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (actionId == 0) {
                    //// input by bluetooth scanner, each EditorInfo.IME_ACTION_DONE/GO/SEND has their own idx, so after debug, actionId is 0, so hardcode 0
                    checkJobStatus task = new checkJobStatus(txtInput.getText().toString());
                    task.execute();
                    if (getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                } else if (actionId == EditorInfo.IME_ACTION_GO) {
                    checkJobStatus task = new checkJobStatus(txtInput.getText().toString());
                    task.execute();
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

        if (scannedItems != null && scannedItems.size() > 0 && adapter == null) {
            adapter = new SimpleStringList_PostItemAdapter(this, scannedItems);
            listView.setAdapter(adapter);
        }

        cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
        if (!cameraScannedCode.equals("")) {
            txtInput.setText(cameraScannedCode);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            checkJobStatus task = new checkJobStatus(txtInput.getText().toString());
            task.execute();
        }

        focusTextbox();

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
            objGlobalMethods.writeToFile("Exception at CheckStatusActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CheckStatusActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
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
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "checkstatus");
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        parentIntent = new Intent(CheckStatusActivity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    public class checkJobStatus extends AsyncTask<Void, Void, Boolean> {
        private final String ASearchCriteria;

        checkJobStatus(String aASearchCriteria) {
            this.ASearchCriteria = aASearchCriteria;
        }

        @Override
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //android.os.Debug.waitForDebugger();
            //Call Web Method
            status = WebService.checkStatus(ASearchCriteria, "CheckStatus");
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            pd.hide();
            if (!errored) {
                appendToListview(status);
                //lblResult.append(status + "\n");
            } else {
                //appendToListview("Error retrieving status.");
                appendToListview(myApp.getAppContext().getString(R.string.alert_retreiveErr));
                //lblResult.append("Error retrieving status." + "\n");
            }
            //focusTextbox();
        }
    }

    private void appendToListview(String val) {
        try {
            SimpleStringList_PostData data = new SimpleStringList_PostData(val, 0);
            if (scannedItems != null) {
                scannedItems.add(data);
                if (adapter == null) {
                    adapter = new SimpleStringList_PostItemAdapter(this, scannedItems);
                    listView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            } else {
                scannedItems = new ArrayList<SimpleStringList_PostData>();
                scannedItems.add(data);
                adapter = new SimpleStringList_PostItemAdapter(this, scannedItems);
                listView.setAdapter(adapter);
            }

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


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckStatusActivity appendToListview. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.refresh) {
            pdRefresh.show();
            scannedItems = null;
            adapter.clear();
            adapter.notifyDataSetChanged();
            pdRefresh.hide();
            txtInput.setText("");
        } else if (item.getItemId() == R.id.qrscan) {
            launchQRScan();
        }

        /*Intent parentIntent;
        switch (item.getItemId()) {
            case R.id.refresh:
                pdRefresh.show();
                scannedItems = null;
                adapter.clear();
                adapter.notifyDataSetChanged();
                pdRefresh.hide();
                txtInput.setText("");
                break;
            case R.id.qrscan:
                *//*GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "checkstatus");
                //parentIntent = new Intent(CheckStatusActivity.this, QRReaderActivity.class);
                parentIntent = new Intent(CheckStatusActivity.this, QRBarcodeReader.class);
                startActivity(parentIntent);*//*
                launchQRScan();
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scannedItems = null;
        MainActivity.uponLogin = 0;
        objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
        //Dashboard2Activity.DashboardOpenFromPush = false;
        DrawerMainActivity.DashboardOpenFromPush = false;
        //Intent parentIntent = new Intent(this, Dashboard2Activity.class);
        Intent parentIntent = new Intent(this, DrawerMainActivity.class);
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
                txtInput.setText("");
                txtInput.requestFocus();
            }
        }, 100);
    }

}
