package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.Tagging_PostData;
import com.xdel.luke.objClasses.Tagging_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.objTagging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Label_Tagging_Activity extends AppCompatActivity {
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    private static String regid;
    private static Switch taggingSwitch;
    private static ListView tagginglistView;
    private static TextView lbltaggingJobNoValue;
    private static TextView lbltaggingCNNoValue;
    private static TextView lbltaggingJobIDX;
    private static TextView lbltaggingCOUNT;
    private static EditText txttaggingMANUALENTRY;
    private static Button btntaggingENTER;
    private static Button btntaggingCLEARALL;
    private static Button btntaggingCANCEL;
    private static Button btntaggingSUBMIT;
    private ProgressDialog pd;

    static boolean errored = false;
    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];

    private static ArrayList<Tagging_PostData> scannedItems = null;
    Tagging_PostItemAdapter adapter = null;
    static String cameraScannedCode;
    ScreenShotContentObserver screenShotContentObserver;

    private static Boolean switchStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_label__tagging);


        taggingSwitch = (Switch) findViewById(R.id.taggingSwitch);
        tagginglistView = (ListView) findViewById(R.id.tagginglistView);
        lbltaggingJobNoValue = (TextView) findViewById(R.id.lbltaggingJobNoValue);
        lbltaggingCNNoValue = (TextView) findViewById(R.id.lbltaggingCNNoValue);
        lbltaggingJobIDX = (TextView) findViewById(R.id.lbltaggingJobIDX);
        lbltaggingCOUNT = (TextView) findViewById(R.id.lbltaggingCOUNT);
        txttaggingMANUALENTRY = (EditText) findViewById(R.id.txttaggingMANUALENTRY);
        btntaggingENTER = (Button) findViewById(R.id.btntaggingENTER);
        btntaggingCLEARALL = (Button) findViewById(R.id.btntaggingCLEARALL);
        btntaggingCANCEL = (Button) findViewById(R.id.btntaggingCANCEL);
        btntaggingSUBMIT = (Button) findViewById(R.id.btntaggingSUBMIT);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        //pd.setMessage("Please wait. Submitting Info...");
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume() {
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        UpdateJobInfoPreferences = getSharedPreferences("UpdateJobInfoPrefs", Context.MODE_PRIVATE);
        if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
            getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        }
        //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
        context = myApp.getAppContext();

        //cd = new ConnectionDetector(context);
        cd = new ConnectionDetector(null);
        regid = getRegistrationId(context);

        JobsHandler.checkForceLogOut();

        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            //showDialog("Please connect to working Internet connection");
            //Toast.makeText(context, "Please connect to working Internet connection.", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, myApp.getAppContext().getString(R.string.toast_workingconnection), Toast.LENGTH_SHORT).show();
        }
        initPage();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Label_Tagging_Activity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at Label_Tagging_Activity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
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
        objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "tagging");
        parentIntent = new Intent(Label_Tagging_Activity.this, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(objGlobalVariables.PROPERTY_REG_ID, "");
        //String registrationId = "";
        if (registrationId.isEmpty()) {

            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(objGlobalVariables.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_label__tagging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.qrscan)
            launchQRScan();

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
            //builder.setMessage("Moving away from thus screen will CLEAR all scanned information. Confirm to clear ALL pending data?");
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_moveawayclearall));
            //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    back();
                }
            });
            //builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        scannedItems = null;
        switchStatus = null;
        Intent parentIntent;
        parentIntent = new Intent(this, JobInfoActivity.class);
        JobInfoActivity.accessFromDirection = 0;
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

    private void showSubmissionOKAlert(int JSPcount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //builder.setMessage(JSPcount + " Bulk Pick Up data submitted.");
        builder.setMessage(JSPcount + " " + myApp.getAppContext().getString(R.string.alert_Taggingdatasubmitted));
        //builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                scannedItems = null;
                Intent parentIntent = new Intent(Label_Tagging_Activity.this, JobInfoActivity.class);
                JobInfoActivity.accessFromDirection = 0;
                MainActivity.uponLogin = 0;
                startActivity(parentIntent);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initPage() {
        Date dt = new Date();

        try {
            txttaggingMANUALENTRY.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

            if (scannedItems != null) {
                lbltaggingCOUNT.setText(String.valueOf(scannedItems.size()));
                btntaggingSUBMIT.setEnabled(true);
            } else {
                lbltaggingCOUNT.setText("0");
                btntaggingSUBMIT.setEnabled(false);
            }

            GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            String JobNo = "";
            String CNNO = "";

            JobNo = objGlobalMethods.getGCMPrefs(GcmPreferences, "currJobInfoJobNo");
            CNNO = objGlobalMethods.getGCMPrefs(GcmPreferences, "currJobInfoCN");

            lbltaggingJobNoValue.setText(JobNo);
            lbltaggingCNNoValue.setText(CNNO);

            if (switchStatus == null || switchStatus) {
                switchStatus = true;
                taggingSwitch.setChecked(true);
                switchStatus = true;
                taggingSwitch.setText(myApp.getAppContext().getString(R.string.taggingAdd));
            } else if (!switchStatus) {
                taggingSwitch.setChecked(false);
                switchStatus = false;
                taggingSwitch.setText(myApp.getAppContext().getString(R.string.taggingRemove));
            }

            //attach a listener to check for changes in state
            taggingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if (isChecked) {
                        switchStatus = true;
                        taggingSwitch.setText(myApp.getAppContext().getString(R.string.taggingAdd));
                    } else {
                        switchStatus = false;
                        taggingSwitch.setText(myApp.getAppContext().getString(R.string.taggingRemove));
                    }

                }
            });

            txttaggingMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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


            cameraScannedCode = objGlobalMethods.getGCMPrefs(GcmPreferences, "cameraScannedCode");
            if (!cameraScannedCode.equals("")) {
                txttaggingMANUALENTRY.setText(cameraScannedCode);
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                submitManualEntry();
            }

            if (scannedItems != null && scannedItems.size() > 0 && adapter == null) {
                adapter = new Tagging_PostItemAdapter(this, scannedItems);
                tagginglistView.setAdapter(adapter);
            }

            tagginglistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View v = tagginglistView.getChildAt(position - tagginglistView.getFirstVisiblePosition());
                    final int deleteRow = position;
                    if (v == null)
                        return;

                    TextView lblBulkPickUpItemTitle = (TextView) v.findViewById(R.id.lblBulkPickUpItemTitle);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Label_Tagging_Activity.this);
                    builder.setCancelable(false);
                    //builder.setMessage("Confirm to delete " + lblBulkPickUpItemTitle.getText().toString() + " ?");
                    builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetcfmDelete) + lblBulkPickUpItemTitle.getText().toString() + " ?");
                    //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user pressed "yes", update job status
                            scannedItems.remove(deleteRow);
                            if (scannedItems.size() > 0) {
                                btntaggingSUBMIT.setEnabled(true);
                            } else if (scannedItems.size() == 0) {
                                btntaggingSUBMIT.setEnabled(false);
                            }
                            lbltaggingCOUNT.setText(String.valueOf(scannedItems.size()));
                            adapter.notifyDataSetChanged();
                            focusTextbox();
                        }
                    });
                    //builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
            focusTextbox();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Label_Tagging_Activity initPage. Exception: " + e.toString(), "", "", "", "");
        }
    }

    private void submitManualEntry() {
        if (!txttaggingMANUALENTRY.getText().toString().equals("")) {
            if (txttaggingMANUALENTRY.getText().toString().length() == 10) {
                String scanned = txttaggingMANUALENTRY.getText().toString().toUpperCase();
                if (verifyBarcode(scanned)) {
                    boolean jobFound = false;
                    View v;
                    TextView lblCnno;
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Tagging_PostData data = null;
                    String Cnno = "";

                    if (tagginglistView.getCount() > 0) {
                        for (int i = 0; i <= tagginglistView.getCount() - 1; i++) {
                            v = tagginglistView.getAdapter().getView(i, null, null);
                            lblCnno = (TextView) v.findViewById(R.id.lblBulkPickUpItemTitle);
                            Cnno = lblCnno.getText().toString();

                            if (scanned.equals(Cnno.toUpperCase())) {
                                jobFound = true;
                                break;
                            }
                        }
                    }

                    if (!jobFound) {

                        data = new Tagging_PostData(scanned.toUpperCase());

                        if (scannedItems != null) {
                            scannedItems.add(data);
                            if (adapter == null) {
                                adapter = new Tagging_PostItemAdapter(this, scannedItems);
                                tagginglistView.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            scannedItems = new ArrayList<Tagging_PostData>();
                            scannedItems.add(data);
                            adapter = new Tagging_PostItemAdapter(this, scannedItems);
                            tagginglistView.setAdapter(adapter);
                        }

                        //runs after listview is done updating itself
                        tagginglistView.post(new Runnable() {
                            public void run() {
                                //scroll the listview to the last item
                                tagginglistView.setSelection(tagginglistView.getCount() - 1);
                                //focus certain view or any other calls after listview completed updating

                            }
                        });

                        lbltaggingCOUNT.setText(String.valueOf(scannedItems.size()));
                        btntaggingSUBMIT.setEnabled(true);
                        focusTextbox();

                    } else {
                        playTone();
                        //showDialogNfocusTxtbox("Duplicate scan!");
                        showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_duplicatescan));
                    }

                } else {
                    playTone();
                    //showDialogNfocusTxtbox("Duplicate scan!");
                    showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.taggingInvalidBarcode));
                }
            } else {
                playTone();
                //showDialog("Please enter something.");
                showDialog(myApp.getAppContext().getString(R.string.taggingAtLeastOneBarcode));
            }
        } else {
            playTone();
            //showDialog("Please enter something.");
            showDialog(myApp.getAppContext().getString(R.string.taggingAtLeastOneBarcode));
        }
    }

    private void submitManualEntry2() {
        if (!txttaggingMANUALENTRY.getText().toString().equals("")) {
            String scanned = txttaggingMANUALENTRY.getText().toString().toUpperCase();
            if (verifyBarcode(scanned)) {
                int bulkycount = 0;
                boolean jobFound = false;
                View v;
                TextView lblCnno;
                String currentDateTime = "";
                Date dt = new Date();
                //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                currentDateTime = formatter.format(dt);
                String currentScannedItem = "";
                Tagging_PostData data = null;
                String Cnno = "";

                if (tagginglistView.getCount() > 0) {
                    for (int i = 0; i <= tagginglistView.getCount() - 1; i++) {
                        v = tagginglistView.getAdapter().getView(i, null, null);
                        lblCnno = (TextView) v.findViewById(R.id.lblBulkPickUpItemTitle);
                        Cnno = lblCnno.getText().toString();

                        if (scanned.equals(Cnno.toUpperCase())) {
                            jobFound = true;
                            break;
                        }
                    }
                }

                if (!jobFound) {

                    data = new Tagging_PostData(scanned.toUpperCase());

                    if (scannedItems != null) {
                        scannedItems.add(data);
                        if (adapter == null) {
                            adapter = new Tagging_PostItemAdapter(this, scannedItems);
                            tagginglistView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        scannedItems = new ArrayList<Tagging_PostData>();
                        scannedItems.add(data);
                        adapter = new Tagging_PostItemAdapter(this, scannedItems);
                        tagginglistView.setAdapter(adapter);
                    }

                    //runs after listview is done updating itself
                    tagginglistView.post(new Runnable() {
                        public void run() {
                            //scroll the listview to the last item
                            tagginglistView.setSelection(tagginglistView.getCount() - 1);
                            //focus certain view or any other calls after listview completed updating

                        }
                    });

                    lbltaggingCOUNT.setText(String.valueOf(scannedItems.size()));
                    btntaggingSUBMIT.setEnabled(true);
                    focusTextbox();

                } else {
                    playTone();
                    //showDialogNfocusTxtbox("Duplicate scan!");
                    showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_duplicatescan));
                }

            } else {
                playTone();
                //showDialogNfocusTxtbox("Duplicate scan!");
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.taggingInvalidBarcode));
            }
        } else {
            playTone();
            //showDialog("Please enter something.");
            showDialog(myApp.getAppContext().getString(R.string.alert_enteratleastonejob));
        }
    }

    public void btnTaggingENTERClick(View view) {
        submitManualEntry();
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void btnTaggingCANCELClick(View view) {
        initBack();
    }

    public void btnTaggingCLEARALLClick(View view) {
        if (scannedItems != null && scannedItems.size() > 0 && adapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            //builder.setMessage("Confirm to clear ALL pending data?");
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_cleardata));
            //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", update job status
                    try {
                        clear();
                    } catch (Exception e) {
                        lbltaggingCOUNT.setText("0");
                        btntaggingSUBMIT.setEnabled(false);
                    }
                }
            });
            //builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    private void clear() {
        scannedItems.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        btntaggingSUBMIT.setEnabled(false);
        lbltaggingCOUNT.setText(String.valueOf(scannedItems.size()));
        focusTextbox();
    }

    public void btnTaggingSUBMITClick(View view) {
        final Date dt = new Date();
        try {
            if (scannedItems != null && scannedItems.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                //builder.setMessage("Confirm to submit Bulk Pick Up data?");
                builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitTagging));
                //builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", update job status
                        try {
                            GcmPreferences = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                            UpdateJobInfoPreferences = getSharedPreferences("UpdateJobInfoPrefs", Context.MODE_PRIVATE);
                            regid = getRegistrationId(myApp.getAppContext());
                            /*submitTask task = new submitTask(scannedItems,regid,GcmPreferences,UpdateJobInfoPreferences);
                            task.execute();*/

                            submit(scannedItems, regid, GcmPreferences, UpdateJobInfoPreferences);

                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception caught at Label_Tagging_Activity btnTaggingSUBMITClick. Exception: " + e.toString(), "", "", "", "");
                            showDialog(myApp.getAppContext().getString(R.string.alert_errSubmitJobsheet));
                        }
                    }
                });
                //builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                //showDialog("Error submitting Bulk Pick Up data." + "\n" + "0 Scanned data saved.");
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_enteratleastonejob));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Label_Tagging_Activity btnTaggingSUBMITClick. Exception: " + e.toString(), "", "", "", "");
        }
    }

    private void submit(ArrayList<Tagging_PostData> scannedItemsAL, String aregid, SharedPreferences gcmp, SharedPreferences updatep) {
        Date dt = new Date();
        Boolean asd = true;
        Long l = dt.getTime();
        String timestampstr = "/Date(" + l + ")/";
        objTagging objTaggingobj = null;
        String[] JSParr;
        int JSPcount = 0;
        String JobsIDXstr = "";
        long JobsIDX;
        objJobInfo currJobInfo = null;
        Gson gson = new Gson();

        JobsIDXstr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
        try {
            JobsIDX = Long.parseLong(JobsIDXstr);
        } catch (Exception e) {
            JobsIDX = 0;
            objGlobalMethods.writeToFile("Exception caught converting JobsIDXstr to long value at Label_Tagging_Activity submit.", "", "", "", "");
        }

        try {
            objGlobalMethods.writeToFile("Label_Tagging_Activity submit started.", "", "", "", "");
            JSParr = new String[scannedItemsAL.size()];
            try {
                currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                int catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);

                for (int i = 0; i <= scannedItemsAL.size() - 1; i++) {
                    JSParr[i] = scannedItemsAL.get(i).tagBarcode;
                }

                Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                objTaggingobj = new objTagging(aregid, JobsIDX, JSParr, timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude());
                if (switchStatus) {
                    JobsHandler.addUploadTaggingQueALPrefs(updatep, objTaggingobj);
                    if (currJobInfo != null) {
                        String[] bct = null;
                        bct = currJobInfo.BarcodeTags;
                        if (bct == null) {
                            bct = new String[JSParr.length];
                            for (int i = 0; i <= JSParr.length - 1; i++) {
                                bct[i] = JSParr[i];
                            }
                            currJobInfo.setBarcodeTags(bct);
                        } else {
                            List<String> bctList;
                            bctList = new ArrayList<String>(Arrays.asList(bct));
                            for (int i = 0; i <= JSParr.length - 1; i++) {
                                bctList.add(JSParr[i]);
                            }
                            bct = bctList.toArray(new String[bctList.size()]);
                            currJobInfo.setBarcodeTags(bct);
                        }
                        JobsHandler.updateSystemJobBarcodeTag(myApp.getAppContext(), GcmPreferences, JobsIDX, currJobInfo, catType, bct);
                    }
                } else {
                    JobsHandler.addRemoveTaggingQueALPrefs(updatep, objTaggingobj);
                    if (currJobInfo != null) {
                        String[] bct = null;
                        bct = currJobInfo.BarcodeTags;
                        if (bct != null) {
                            List<String> bctList;
                            bctList = new ArrayList<String>(Arrays.asList(bct));
                            for (int i = bctList.size() - 1; i >= 0; i--) {
                                for (int c = 0; c <= JSParr.length - 1; c++) {
                                    if (JSParr[c].equals(bctList.get(i))) {
                                        bctList.remove(i);
                                        break;
                                    }
                                }
                            }
                            bct = bctList.toArray(new String[bctList.size()]);
                            currJobInfo.setBarcodeTags(bct);
                        }
                        JobsHandler.updateSystemJobBarcodeTag(myApp.getAppContext(), GcmPreferences, JobsIDX, currJobInfo, catType, bct);
                        String hfJobInfoJsonStr = gson.toJson(currJobInfo);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", hfJobInfoJsonStr);
                    }
                }

                Context ctx = myApp.getAppContext();
                sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                JSPcount = JSParr.length;
                scannedItems = null;
                asd = true;
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at Label_Tagging_Activity submit inner TryCatch. Exception: " + e.toString(), "", "", "", "");
                asd = false;
            }

            switchStatus = true;

            if (asd) {
                objGlobalMethods.writeToFile("Label_Tagging_Activity submit ended.", "", "", "", "");
                JSPcount = JSParr.length;
                scannedItems = null;
                showSubmissionOKAlert(JSPcount);
            } else {
                objGlobalMethods.writeToFile("Label_Tagging_Activity submit ended.", "", "", "", "");
                showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_submittedfail));
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Label_Tagging_Activity submit. Exception: " + e.toString(), "", "", "", "");
            showDialogNfocusTxtbox(myApp.getAppContext().getString(R.string.alert_submittedfail));
        }

    }


    public static void focusTextbox() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                tagginglistView.clearFocus();
                btntaggingSUBMIT.clearFocus();
                btntaggingCANCEL.clearFocus();
                btntaggingCLEARALL.clearFocus();
                btntaggingENTER.clearFocus();
                lbltaggingCOUNT.clearFocus();
                lbltaggingJobIDX.clearFocus();
                txttaggingMANUALENTRY.setText("");
                txttaggingMANUALENTRY.requestFocus();
            }
        }, 100);
    }

    private void playTone() {
        player = MediaPlayer.create(Label_Tagging_Activity.this, R.raw.error);
        player.start();
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
                focusTextbox();
                dialog.dismiss();

            }
        });

        return builder.show();
    }

    public Boolean verifyBarcode(String barcode) {
        Date dt = new Date();
        Boolean asd = true;
        try {
            if (barcode.length() != 10) {
                asd = false;
            } else {
                String Val = barcode.substring(0, 8);
                String CheckSum = barcode.substring(8, 10).toUpperCase();
                byte[] asc = Val.getBytes("US-ASCII");

                int agg = (asc[0] * 41) +
                        (asc[1] * 59) +
                        (asc[2] * 37) +
                        (asc[3] * 73) +
                        (asc[4] * 31) +
                        (asc[5] * 27) +
                        (asc[6] * 23) +
                        (asc[7] * 89);
            /*Byte b = Convert.ToByte(agg % 256);
            String Verify = String.Format("{0:x2}", b).ToUpper();*/
                String Verify = Integer.toHexString(agg % 256).toUpperCase();
                if (Verify.length() == 1) {
                    Verify = "0" + Verify;
                }
                //Integer VerifyInt = Integer.parseInt(Verify);
                asd = CheckSum.equals(Verify);
                if (asd) {
                    objGlobalMethods.writeToFile("Label_Tagging_Activity verifyBarcode returns TRUE.", "", "", "", "");
                } else {
                    objGlobalMethods.writeToFile("Label_Tagging_Activity verifyBarcode returns FALSE.", "", "", "", "");
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Label_Tagging_Activity verifyBarcode. Returning FALSE. Exception: " + e.toString(), "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }
}
