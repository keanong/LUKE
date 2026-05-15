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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.List;

public class HandoverActivity extends AppCompatActivity {
    private static ListView listView;
    public static ArrayList<String> hotoqrlist = null;
    private static String HOScannedIDnUsername = "";
    public static ArrayList<ho_PostData> scannedItems = null;
    private static EditText txtHOMANUALENTRY;
    Context context;
    ConnectionDetector cd;
    private MediaPlayer player;
    LinearLayout LLHOQR, LLHOItems, LLprevQR, LLnextQR;
    TextView lblHOID, lblHOIDval, lblHOCOUNT, lblhotoqrlistSize, lblimgNumber, lblimgNumberHidden;
    ImageView imgQR;
    Button btnHOENTER, btnHOCANCEL, btnHOSCAN, btnHOPREPARE, btnHOCANCEL2, btnHORESET, btnHOVERIFY, btnPrevQR, btnNextQR;
    String cameraScannedCode;
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    SharedPreferences gcmp;
    ho_PostItemAdapter adapter = null;
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

        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_handover);

        LLHOQR = findViewById(R.id.LLHOQR);
        LLHOItems = findViewById(R.id.LLHOItems);
        LLprevQR = findViewById(R.id.LLprevQR);
        LLnextQR = findViewById(R.id.LLnextQR);
        lblhotoqrlistSize = findViewById(R.id.lblhotoqrlistSize);
        lblimgNumber = findViewById(R.id.lblimgNumber);
        lblimgNumberHidden = findViewById(R.id.lblimgNumberHidden);

        imgQR = findViewById(R.id.imgQR);
        btnPrevQR = findViewById(R.id.btnPrevQR);
        btnNextQR = findViewById(R.id.btnNextQR);

        btnHOCANCEL2 = findViewById(R.id.btnHOCANCEL2);
        btnHORESET = findViewById(R.id.btnHORESET);
        btnHOVERIFY = findViewById(R.id.btnHOVERIFY);

        txtHOMANUALENTRY = findViewById(R.id.txtHOMANUALENTRY);
        lblHOID = findViewById(R.id.lblHOID);
        lblHOIDval = findViewById(R.id.lblHOIDval);
        lblHOCOUNT = findViewById(R.id.lblHOCOUNT);
        listView = findViewById(R.id.listView);
        btnHOENTER = findViewById(R.id.btnHOENTER);
        btnHOCANCEL = findViewById(R.id.btnHOCANCEL);
        btnHOSCAN = findViewById(R.id.btnHOSCAN);
        btnHOPREPARE = findViewById(R.id.btnHOPREPARE);

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessingupdates));

        lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(myApp.getAppContext());
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
                setTitle(myApp.getAppContext().getString(R.string.handover));

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
                    objGlobalMethods.writeToFile("HandoverActivity !BTEnabled", "", "", "", "");
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
                objGlobalMethods.writeToFile("Exception at HandoverActivity onResume. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at HandoverActivity onPause  mNfcAdapter.disableForegroundDispatch. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }

        gcmp = objGlobalMethods.getGcmPreferences();
        String HOQRimgB64ListJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "HOQRimgB64ListJsonStr");
        if (!objGlobalMethods.IsEmptyString(HOQRimgB64ListJsonStr)) {
            int imgNumber = objGlobalMethods.IsEmptyString(lblimgNumberHidden.getText().toString()) ? 1 : Integer.parseInt(lblimgNumberHidden.getText().toString());
            imgNumber += -1;
            lblimgNumberHidden.setText(imgNumber <= 0 ? "0" : String.valueOf(imgNumber));
        }

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at HandoverActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onDestroy() {
        listView = null;
        hotoqrlist = null;
        txtHOMANUALENTRY = null;
        HOScannedIDnUsername = null;
        scannedItems = null;
        txtHOMANUALENTRY = null;
        imgQR = null;

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = null;

        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at HandoverActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        super.onDestroy();
    }

    private void launchQRScan(boolean verifyTOQR) {
        Intent parentIntent;
        gcmp = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"QRScanRequestIntent", "cameraScannedCode", "verifyTOQR"};
        String[] prefvalue = new String[]{"handover", "", verifyTOQR ? "t" : ""};
        /*if (verifyTOQR) {
            int imgNumber = objGlobalMethods.IsEmptyString(lblimgNumberHidden.getText().toString()) ? 1 : Integer.parseInt(lblimgNumberHidden.getText().toString());
            imgNumber += -1;
            lblimgNumberHidden.setText(imgNumber <= 0 ? "0" : String.valueOf(imgNumber));
        }*/
        objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
        parentIntent = new Intent(myApp.currentActivity, QRBarcodeReader.class);
        startActivity(parentIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handover, menu);
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
                        lblHOCOUNT.setText("0");
                        LLHOItems.setVisibility(View.VISIBLE);
                        LLHOQR.setVisibility(View.GONE);
                        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnHOPREPARE);
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


        /*Intent parentIntent;
        switch (item.getItemId()) {
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
                            lblHOCOUNT.setText("0");
                            LLHOItems.setVisibility(View.VISIBLE);
                            LLHOQR.setVisibility(View.GONE);
                            ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnHOPREPARE);
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
        if (scannedItems != null && scannedItems.size() > 0 && adapter != null) {
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
        String[] prefName = new String[]{"HOScannedIDnUsername", "cameraScannedCode", "verifyTOQR"};
        String[] prefvalue = new String[]{"", "", ""};
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

    private void initPage() {
        try {
            LLprevQR.setOnClickListener(null);
            LLnextQR.setOnClickListener(null);
            HOScannedIDnUsername = objGlobalMethods.getGCMPrefs(gcmp, "HOScannedIDnUsername");
            String HOQRimgB64ListJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "HOQRimgB64ListJsonStr");
            String verifyTOQR = objGlobalMethods.getGCMPrefs(gcmp, "verifyTOQR");
            String ho_jsonStr = objGlobalMethods.getGCMPrefs(gcmp, "ho_jsonStr");
            cameraScannedCode = objGlobalMethods.getGCMPrefs(gcmp, "cameraScannedCode");
            boolean showQR = false;
            if (hotoqrlist != null && hotoqrlist.size() > 0) {
                showQR = true;
            }
            if (!showQR) {
                showQR = !objGlobalMethods.IsEmptyString(HOQRimgB64ListJsonStr);
                if (showQR) {
                    Gson gson = new Gson();
                    hotoqrlist = gson.fromJson(HOQRimgB64ListJsonStr, new TypeToken<List<String>>() {
                    }.getType());
                }
            }
            LLHOItems.setVisibility(!showQR ? View.VISIBLE : View.GONE);
            LLHOQR.setVisibility(!showQR ? View.GONE : View.VISIBLE);

            if (showQR) {
                displayMultiQR(1);
                Context ctx = myApp.getAppContext();
                if (!objGlobalMethods.IsEmptyString(ho_jsonStr) && !objGlobalMethods.IsEmptyString(cameraScannedCode) && !objGlobalMethods.IsEmptyString(verifyTOQR)) {
                    Gson gson = new Gson();
                    to_json tj = gson.fromJson(cameraScannedCode, to_json.class);
                    boolean TOQRVerified = verifyTOQR(tj);
                    ho_json hj = gson.fromJson(ho_jsonStr, ho_json.class);
                    if (TOQRVerified && hj != null) {
                        processHOTO p = new processHOTO(hj, tj);
                        p.execute();
                    } else {
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                                ctx.getString(R.string.verificationfail)).show();
                        String[] prefName = new String[]{"cameraScannedCode", "verifyTOQR"};
                        String[] prefvalue = new String[]{"", ""};
                        objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
                    }
                } else if (objGlobalMethods.IsEmptyString(ho_jsonStr) && !objGlobalMethods.IsEmptyString(cameraScannedCode) && !objGlobalMethods.IsEmptyString(verifyTOQR)) {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                            ctx.getString(R.string.rescanho)).show();
                    String[] prefName = new String[]{"cameraScannedCode", "verifyTOQR"};
                    String[] prefvalue = new String[]{"", ""};
                    objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);
                }
            } else {
                if (HOScannedIDnUsername.isEmpty()) {
                    showErrorAlert("");
                    return;
                }

                lblHOIDval.setText(HOScannedIDnUsername);

                if (!objGlobalMethods.IsEmptyString(cameraScannedCode)) {
                    txtHOMANUALENTRY.setText(cameraScannedCode);
                    objGlobalMethods.setGCMPrefs(gcmp, "cameraScannedCode", "");
                    submitManualEntry();
                } else if (scannedItems != null && scannedItems.size() > 0 && adapter == null) {
                    adapter = new ho_PostItemAdapter(this, scannedItems);
                    listView.setAdapter(adapter);
                    lblHOCOUNT.setText(scannedItems != null ? String.valueOf(scannedItems.size()) : "0");
                }
                ctrlSettings.enableDisableSubmitButtonWithStyles(scannedItems != null ? true : false, btnHOPREPARE);

                txtHOMANUALENTRY.setOnEditorActionListener(null);
                txtHOMANUALENTRY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        View v = listView.getChildAt(position - listView.getFirstVisiblePosition());
                        final int deleteRow = position;
                        if (v == null)
                            return;

                        TextView lblBulkPickUpItemTitle = (TextView) v.findViewById(R.id.lblBulkPickUpItemTitle);

                        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                        builder.setCancelable(false);
                        builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetcfmDelete) + " " + lblBulkPickUpItemTitle.getText().toString() + " ?");
                        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scannedItems.remove(deleteRow);
                                ctrlSettings.enableDisableSubmitButtonWithStyles(scannedItems.size() > 0 ? true : false, btnHOPREPARE);
                                lblHOCOUNT.setText(String.valueOf(scannedItems.size()));
                                adapter.notifyDataSetChanged();
                                focusTextbox();
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
                });

                focusTextbox();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at HandoverActivity initPage. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void submitManualEntry() {
        if (!txtHOMANUALENTRY.getText().toString().equals("")) {
            int foundidx = -1;
            boolean jobFound = false;
            View v;
            TextView lblCnno;
            Date dt = new Date();
            ho_PostData data = null;
            String Cnno = "";
            String scanned = txtHOMANUALENTRY.getText().toString().toUpperCase();
            objGlobalMethods.writeToFile("HO: " + scanned, "", "", "", "");

            if (listView.getCount() > 0) {
                for (int i = 0; i <= listView.getCount() - 1; i++) {
                    v = listView.getAdapter().getView(i, null, null);
                    lblCnno = (TextView) v.findViewById(R.id.lblBulkPickUpItemTitle);
                    Cnno = lblCnno.getText().toString();

                    if (scanned.equals(Cnno.toUpperCase())) {
                        jobFound = true;
                        foundidx = i;
                        break;
                    }
                }
            }

            if (!jobFound) {
                data = new ho_PostData(scanned.toUpperCase(), false);
                if (scannedItems != null) {
                    scannedItems.add(data);
                    if (adapter == null) {
                        adapter = new ho_PostItemAdapter(myApp.getAppContext(), scannedItems);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    scannedItems = new ArrayList<ho_PostData>();
                    scannedItems.add(data);
                    adapter = new ho_PostItemAdapter(myApp.getAppContext(), scannedItems);
                    listView.setAdapter(adapter);
                }

                listView.post(new Runnable() {
                    public void run() {
                        //scroll the listview to the last item
                        listView.setSelection(listView.getCount() - 1);
                    }
                });

                playSuccessVerifiedTone();
                lblHOCOUNT.setText(String.valueOf(scannedItems.size()));
                ctrlSettings.enableDisableSubmitButtonWithStyles(true, btnHOPREPARE);
            } else {
                ////do nothing
                final int foundPos = foundidx;
                listView.post(new Runnable() {
                    public void run() {
                        try {
                            //scroll the listview to the last item
                            listView.setSelection(foundPos);
                            //focus certain view or any other calls after listview completed updating
                        } catch (Exception e) {
                            objGlobalMethods.writeToFile("Exception at HandoverActivity listView.setSelection(foundPos);. Exception: " + e.getMessage(), "", "", "", "");
                        }
                    }
                });

                playSuccessVerifiedTone();
                scannedItems.get(foundPos).setBlink(true);
                adapter.notifyDataSetChanged();
            }
            playSuccessVerifiedTone();
            focusTextbox();
        } else {
            playTone();
            showDialog(myApp.getAppContext().getString(R.string.alert_enteratleastonejob));
        }
    }

    private void clear() {
        gcmp = objGlobalMethods.getGcmPreferences();
        String[] prefName = new String[]{"cameraScannedCode", "verifyTOQR", "HOQRimgB64ListJsonStr", "refHOHash", "ho_jsonStr"};
        String[] prefvalue = new String[]{"", "", "", "", ""};
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

        LLHOItems.setVisibility(View.VISIBLE);
        LLHOQR.setVisibility(View.GONE);
        ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnHOPREPARE);
        lblHOCOUNT.setText("0");
        focusTextbox();
    }

    public String getRefHash(String HODriverIDX, boolean addsalt) {
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
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at HandoverActivity getRefHash. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return hash;
        }
    }

    private String getComparedrefTOHash(String TODriverIDX, String refHOHash) {
        String l = "";
        String hash = "";
        String[] arr = null;
        try {
            l = TODriverIDX + refHOHash;
            hash = JobsHandler2.SHA1(l, true);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at HandoverActivity getComparedrefTOHash. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return hash;
        }
    }

    private void processMultiHOQR() {
        hotoqr hqr = null;
        ArrayList<String> reflist = null;
        ho_json hj = null;
        String refHOHash = "";
        String HOQRimgB64Str = "";
        String HOQRimgB64ListJsonStr = "";
        String jsonstr = "";
        String hotoqrJsonStr = "";
        String HODriverIDX = "";
        gcmp = objGlobalMethods.getGcmPreferences();
        try {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;
            HODriverIDX = lblHOIDval.getText().toString();

            Gson gson = new Gson();
            refHOHash = getRefHash(HODriverIDX, true);
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();

            int c = 0;
            for (int i = 0; i <= scannedItems.size() - 1; i++) {
                if (c < 10) {
                    if (reflist == null) {
                        reflist = new ArrayList<>();
                    }
                    reflist.add(scannedItems.get(i).ref);
                    c++;
                }

                if (((c == 10) || (i == scannedItems.size() - 1)) && reflist != null) {
                    hqr = new hotoqr(HODriverIDX, reflist, refHOHash);
                    hotoqrJsonStr = gson.toJson(hqr);

                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(hotoqrJsonStr,
                            null,
                            Contents.Type.TEXT,
                            BarcodeFormat.QR_CODE.toString(),
                            smallerDimension);
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                        HOQRimgB64Str = JobsHandler.convertBitmapToBase64(bitmap);
                        bitmap.recycle();
                        bitmap = null;
                    } catch (WriterException e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at HandoverActivity generateQRstr generate qr bitmap. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    }

                    if (hotoqrlist == null) {
                        hotoqrlist = new ArrayList<>();
                    }
                    hotoqrlist.add(HOQRimgB64Str);

                    c = 0;
                    reflist = null;
                    hotoqrJsonStr = "";
                    hqr = null;
                }
            }

            if (hotoqrlist != null && hotoqrlist.size() > 0) {
                HOQRimgB64ListJsonStr = gson.toJson(hotoqrlist);
            }

            hj = new ho_json(scannedItems, refHOHash, HODriverIDX);
            hj.setHO_Timestamp(timestampstr);
            hj.setHO_LAT(lastknownlocation.getLatitude());
            hj.setHO_LNG(lastknownlocation.getLongitude());
            jsonstr = gson.toJson(hj);


            String[] prefName = new String[]{"HOQRimgB64ListJsonStr", "refHOHash", "ho_jsonStr"};
            String[] prefvalue = new String[]{HOQRimgB64ListJsonStr, refHOHash, jsonstr};
            objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);

            if (!objGlobalMethods.IsEmptyString(HOQRimgB64ListJsonStr)) {
                displayMultiQR(0);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at HandoverActivity processHOQR. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void displayMultiQR(int dir) {
        byte[] QRImg = null;
        int imgNumber = objGlobalMethods.IsEmptyString(lblimgNumberHidden.getText().toString()) ? 1 : Integer.parseInt(lblimgNumberHidden.getText().toString());
        int viewidx = -1;

        try {
            viewidx = (imgNumber - 1 + dir) < 0 ? 0 : (imgNumber - 1 + dir);
            lblimgNumber.setText(String.valueOf(viewidx + 1));
            lblimgNumberHidden.setText(String.valueOf(viewidx + 1));

            LLHOItems.setVisibility(View.GONE);
            LLHOQR.setVisibility(View.VISIBLE);
            if (scannedItems != null) {
                scannedItems.clear();
                scannedItems = null;
            }
            if (adapter != null) {
                adapter.clear();
                adapter.notifyDataSetChanged();
                adapter = null;
            }

            if (hotoqrlist != null && hotoqrlist.size() > 0) {
                String HOQRimgB64ListJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "HOQRimgB64ListJsonStr");
                if (!objGlobalMethods.IsEmptyString(HOQRimgB64ListJsonStr)) {
                    Gson gson = new Gson();
                    hotoqrlist = gson.fromJson(HOQRimgB64ListJsonStr, new TypeToken<List<String>>() {
                    }.getType());
                }
            }

            if (hotoqrlist != null && hotoqrlist.size() > 0 && (viewidx >= 0 && viewidx <= hotoqrlist.size() - 1)) {
                QRImg = Base64.decode(hotoqrlist.get(viewidx), Base64.DEFAULT);
            }
            if (QRImg != null) {
                imgQR.setImageBitmap(BitmapFactory.decodeByteArray(QRImg, 0, QRImg.length));
                lblhotoqrlistSize.setText(hotoqrlist != null && hotoqrlist.size() > 0 ? String.valueOf(hotoqrlist.size()) : "0");
                LLprevQR.setOnClickListener(null);
                LLnextQR.setOnClickListener(null);
                if (hotoqrlist.size() == 1) {
                    LLprevQR.setOnClickListener(null);
                    LLnextQR.setOnClickListener(null);
                    LLprevQR.setEnabled(false);
                    LLnextQR.setEnabled(false);
                    btnPrevQR.setBackgroundResource(R.mipmap.ic_action_chevron_left_gray);
                    btnNextQR.setBackgroundResource(R.mipmap.ic_action_chevron_right_gray);
                } else if (hotoqrlist.size() > 1) {
                    if (viewidx == 0) {
                        LLprevQR.setOnClickListener(null);
                        LLnextQR.setOnClickListener(LLQROnClickListener(1));
                        LLprevQR.setEnabled(false);
                        LLnextQR.setEnabled(true);
                        btnPrevQR.setBackgroundResource(R.mipmap.ic_action_chevron_left_gray);
                        btnNextQR.setBackgroundResource(R.mipmap.ic_action_chevron_right);
                    } else if (viewidx == hotoqrlist.size() - 1) {
                        LLprevQR.setOnClickListener(LLQROnClickListener(-1));
                        LLnextQR.setOnClickListener(null);
                        LLprevQR.setEnabled(true);
                        LLnextQR.setEnabled(false);
                        btnPrevQR.setBackgroundResource(R.mipmap.ic_action_chevron_left);
                        btnNextQR.setBackgroundResource(R.mipmap.ic_action_chevron_right_gray);
                    } else {
                        LLprevQR.setOnClickListener(LLQROnClickListener(-1));
                        LLnextQR.setOnClickListener(LLQROnClickListener(1));
                        LLprevQR.setEnabled(true);
                        LLnextQR.setEnabled(true);
                        btnPrevQR.setBackgroundResource(R.mipmap.ic_action_chevron_left);
                        btnNextQR.setBackgroundResource(R.mipmap.ic_action_chevron_right);
                    }
                } else {
                    LLprevQR.setOnClickListener(null);
                    LLnextQR.setOnClickListener(null);
                    LLprevQR.setEnabled(false);
                    LLnextQR.setEnabled(false);
                    btnPrevQR.setBackgroundResource(R.mipmap.ic_action_chevron_left_gray);
                    btnNextQR.setBackgroundResource(R.mipmap.ic_action_chevron_right_gray);
                }
            } else {
                lblimgNumber.setText("");
                lblimgNumberHidden.setText("");
                LLprevQR.setOnClickListener(null);
                LLnextQR.setOnClickListener(null);
                LLprevQR.setEnabled(false);
                LLnextQR.setEnabled(false);
                btnPrevQR.setBackgroundResource(R.mipmap.ic_action_chevron_left_gray);
                btnNextQR.setBackgroundResource(R.mipmap.ic_action_chevron_right_gray);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at HandoverActivity displayMultiQR. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private View.OnClickListener LLQROnClickListener(final int dir) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMultiQR(dir);
            }
        };
    }

    private boolean verifyTOQR(to_json tj) {
        boolean asd = false;
        String hohash = "";
        String tohash = "";
        String ComparedrefTOHash = "";
        String TODriverIDX = "";
        try {
            if (tj != null) {
                gcmp = objGlobalMethods.getGcmPreferences();
                hohash = objGlobalMethods.getGCMPrefs(gcmp, "refHOHash");
                tohash = tj.refTOHash;
                TODriverIDX = tj.TODriverIDX;
                ComparedrefTOHash = getComparedrefTOHash(TODriverIDX, hohash);
                asd = (ComparedrefTOHash.equals(tohash));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at HandoverActivity verifyTOQR. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private class processHOTO extends AsyncTask<Void, Void, Boolean> {
        private final ho_json hj;
        private final to_json tj;

        public processHOTO(ho_json ahj, to_json atj) {
            this.hj = ahj;
            this.tj = atj;
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
            HOTO h = null;
            HOTO t = null;

            try {
                for (int i = 0; i <= hj.scannedItems.size() - 1; i++) {
                    hi = new HOTOItem(hj.scannedItems.get(i).ref);
                    hil.add(hi);
                }

                //GUID = UUID.randomUUID().toString();
                GUID = tj.refTOHash;
                h = new HOTO();
                h.setGUID(GUID);
                h.setHO_DriverIDX(Long.parseLong(hj.HODriverIDX));
                h.setHO_Timestamp(hj.HO_Timestamp);
                h.setHO_LAT(hj.HO_LAT);
                h.setHO_LNG(hj.HO_LNG);
                h.setItems(hil);
                h.setFlag(256);
                h.setTO_DriverIDX(Long.parseLong(tj.TODriverIDX));
                String ARegID = objGlobalMethods.getRegistrationIdOnly();

                asd = WebService.L2_SubmitHOTO(ARegID, h);
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at HandoverActivity processHOTO doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                if (r) {
                    prefName = new String[]{"cameraScannedCode", "verifyTOQR", "HOQRimgB64ListJsonStr", "ho_jsonStr", "refHOHash"};
                    prefvalue = new String[]{"", "", "", "", ""};
                } else {
                    prefName = new String[]{"cameraScannedCode", "verifyTOQR"};
                    prefvalue = new String[]{"", ""};
                }
                objGlobalMethods.setGCMPrefsMass(gcmp, prefName, prefvalue);

                if (r) {
                    objGlobalMethods.writeToFile("HOTO successful.", "", "", "", "");
                    showSuccessAlert();
                } else {
                    objGlobalMethods.writeToFile("HOTO unsuccessful.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                            ctx.getString(R.string.verificationfail)).show();
                }
            } catch (Exception e) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                pd = null;
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at HandoverActivity processHOTO onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok),
                        ctx.getString(R.string.verificationfail)).show();
            }
        }
    }

    public void btnHOENTERClick(View v) {
        submitManualEntry();
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void btnHOCANCELClick(View v) {
        initBack();
    }

    public void btnbtnHOSCANClick(View v) {
        launchQRScan(false);
    }

    public void btnHOPREPAREClick(View v) {
        if (scannedItems != null && scannedItems.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.cfm_ho_qr));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    processMultiHOQR();
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
    }

    public void btnHOCANCEL2Click(View v) {
        back();
    }

    public void btnHORESETClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_cleardata));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    gcmp = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(gcmp, "HOQRimgB64ListJsonStr", "");
                    recreate();
                } catch (Exception e) {
                    lblHOCOUNT.setText("0");
                    ctrlSettings.enableDisableSubmitButtonWithStyles(false, btnHOPREPARE);
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

    public void btnHOVERIFYClick(View v) {
        try {
            launchQRScan(true);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at HandoverActivity btnHOVERIFYClick. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
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
            objGlobalMethods.writeToFile("Exception at HandoverActivity stopPlayingTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at HandoverActivity playSuccessVerifiedTone. Exception: " + e.getMessage(), "", "", "", "");
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
            objGlobalMethods.writeToFile("Exception at HandoverActivity playTone. Exception: " + e.getMessage(), "", "", "", "");
        }
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

    private void showErrorAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (msg.isEmpty()) {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_submitjobsheetnoid));
        } else {
            builder.setMessage(msg);
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "HOScannedIDnUsername", "");
                scannedItems = null;

                //Intent i = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent i = new Intent(myApp.currentActivity, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
                txtHOMANUALENTRY.setText("");
                txtHOMANUALENTRY.requestFocus();
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
                    objGlobalMethods.writeToFile("Bluetooth is on at HandoverActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at HandoverActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}