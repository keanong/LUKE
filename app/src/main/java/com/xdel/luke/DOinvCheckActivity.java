package com.xdel.luke;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.DOinvCheck_PostData;
import com.xdel.luke.objClasses.DOinvCheck_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objDOObject;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DOinvCheckActivity extends AppCompatActivity {

    public static String fromPage = "";
    public static Intent nextActivtyIntent;

    ConnectionDetector cd;
    Context context;
    private ExpandableListView expListView;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    private DOinvCheck_PostItemAdapter expandableSortedListAdapter;
    String langType = "en";
    String username, password, useridx, ishomesuser, regid;
    String[] UsernamePwd = new String[4];
    private List<String> listDataHeader;
    private HashMap<String, List<DOinvCheck_PostData>> listDataChildSorted2;
    private ArrayList<String> headerAl;
    private ArrayList itemAL;
    private List<DOinvCheck_PostData> subGroupItemAL;
    ScreenShotContentObserver screenShotContentObserver;

    private void showChangeAirplaneModeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DOinvCheckActivity.this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_airplanemode));

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (android.os.Build.VERSION.SDK_INT < 17) {
                    try {
                        Intent intentAirplaneMode = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                        intentAirplaneMode.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentAirplaneMode);
                    } catch (ActivityNotFoundException e) {
                        //String stack = JobsHandler.getStackTraceString(e);
                        //objGlobalMethods.writeToFile("ActivityNotFoundException at DOinvCheckActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                        objGlobalMethods.writeToFile("ActivityNotFoundException at DOinvCheckActivity showChangeAirplaneModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
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
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(DOinvCheckActivity.this);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_gpsmode));

            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                    } catch (ActivityNotFoundException e) {
                        //String stack = JobsHandler.getStackTraceString(e);
                        //objGlobalMethods.writeToFile("ActivityNotFoundException at DOinvCheckActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString() + "\nStack: " + stack, "", "", "", "");
                        objGlobalMethods.writeToFile("ActivityNotFoundException at DOinvCheckActivity showChangeGPSModeSettingsDialog. ActivityNotFoundException: " + e.toString(), "", "", "", "");
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at DOinvCheckActivity showChangeGPSModeSettingsDialog. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
            objGlobalMethods.writeToFile("Exception caught at DOinvCheckActivity showChangeGPSModeSettingsDialog. Exception: " + e.getMessage().toString(), "", "", "", "");
        }

    }

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private SharedPreferences getUpdateJobInfoPreferences() {
        return getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        GcmPreferences = getGcmPreferences();
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

        setContentView(R.layout.activity_doinv_check);

        expListView = (ExpandableListView) findViewById(R.id.expListView);

        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume(){
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = getGcmPreferences();
            UpdateJobInfoPreferences = getUpdateJobInfoPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            context = myApp.getAppContext();

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("DOinvCheckActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            Gson gson = new Gson();
            objGlobalMethods.setGCMPrefs(GcmPreferences, "PreDeterminedStatusCodeIDX", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "mChildCheckStates2PrefStr", "");
            JobsHandler.resetrePositionedGroupItems(GcmPreferences);

            /*if (myApp.isAirplaneModeOn()) {
                showChangeAirplaneModeSettingsDialog();
                return;
            }

            if (!myApp.isGpsOn()) {
                showChangeGPSModeSettingsDialog();
                return;
            }*/

            if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("CheckListActivity") || fromPage.equals("OtpActivity"))){
                objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                objJobInfo[] jiAL = new objJobInfo[1];
                jiAL[0] = currJobInfo;
                loadList(jiAL);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("JobsListGroupActivity")){
                String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName);
                if (!objCheckedItemsJsonStr.equals("") || !objCheckedItemsJsonStr.equals("[]")) {
                    objCheckedItems[] objCheckedItemsAL = null;
                    objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                    objJobInfo[] jiAL = new objJobInfo[objCheckedItemsAL.length];
                    for (int i =0; i<=objCheckedItemsAL.length-1;i++) {
                        jiAL[i] = objCheckedItemsAL[i].jobinfo;
                    }

                    if (jiAL.length > 0) {
                        loadList(jiAL);
                    } else if (jiAL.length == 0) {
                        showErrorAlert("");
                    }
                } else if (objCheckedItemsJsonStr.equals("") || objCheckedItemsJsonStr.equals("[]")) {
                    showErrorAlert("");
                }
            } else if (fromPage.equals("")) {
                showErrorAlert("");
            }

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DOinvCheckActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
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
            objGlobalMethods.writeToFile("Exception at DOinvCheckActivity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
    }

    private void showErrorAlert(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                GcmPreferences = getGcmPreferences();
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                Intent i;
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(DOinvCheckActivity.this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doinv_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        if (fromPage == null) {
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            intent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        } else if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("OtpActivity"))) {
            intent = new Intent(this, JobInfoActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("CheckListActivity")) {
            intent = new Intent(this, CheckListActivity.class);
        } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("JobsListGroupActivity")) {
            intent = new Intent(this, JobsListGroupActivity.class);
        } else {
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            intent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        }
        fromPage = "";
        nextActivtyIntent = null;
        MainActivity.uponLogin = 0;
        startActivity(intent);
    }

    public void CancelsetDeliveredStatus(View view) {
        onBackPressed();
    }

    public void btnNextClick(View view) {
        try {
            startActivity(nextActivtyIntent);
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at DOinvCheckActivity btnNextClick. Exception: " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            objGlobalMethods.writeToFile("Exception caught at DOinvCheckActivity btnNextClick. Exception: " + e.getMessage().toString(),"","","","");
        }
    }

    private void loadList(objJobInfo[] JobInfoAL){
        try {
            objJobInfo ji;
            objDOObject DOobj;
            GcmPreferences = getGcmPreferences();
            headerAl = new ArrayList<String>();
            itemAL = new ArrayList();

            listDataHeader = new ArrayList<String>();
            listDataChildSorted2 = new HashMap<String, List<DOinvCheck_PostData>>();
            Gson gson = new Gson();

            DOinvCheck_PostData postdata;
            int headerALselectedIDX = 0;
            List<objJobInfo> jiList;
            HashMap<Integer, boolean[]> existing_mChildCheckStates = null;
            List<String> existing_listDataHeader = null;
            HashMap<String, List<DOinvCheck_PostData>> existing_listDataChild = null;

            if (expandableSortedListAdapter != null) {
                existing_mChildCheckStates = expandableSortedListAdapter.mChildCheckStates;
                existing_listDataHeader = expandableSortedListAdapter._listDataHeader;
                existing_listDataChild = expandableSortedListAdapter._listDataChild;
            }

            jiList = new ArrayList<objJobInfo>(Arrays.asList(JobInfoAL));
            for (int i = 0; i <= JobInfoAL.length - 1; i++) {
                ji = JobInfoAL[i];
                String JobNoOrCNNO = ji.ConsignmentNote != null && !ji.ConsignmentNote.equals("") ? ji.ConsignmentNote : ji.JobNo;

                if (ji.BarcodeTags != null || ji.BarcodeTags.length > 0) {
                    Integer total = JobsHandler.getNoOfValidTags2(ji);
                    for (int x = 0; x<=ji.BarcodeTags.length-1;x++){
                        if (!headerAl.contains(JobNoOrCNNO)) {
                            headerAl.add(JobNoOrCNNO);
                            subGroupItemAL = new ArrayList<DOinvCheck_PostData>();
                            //postdata = new DOinvCheck_PostData(ji.JobsIDX, ji.DOList[x].DOIDX, ji.DOList[x].Invoice, true, JobNoOrCNNO, ji);
                            if (!objGlobalMethods.verifyBarcode(ji.BarcodeTags[x].toUpperCase())) {
                                postdata = new DOinvCheck_PostData(ji.JobsIDX, ji.BarcodeTags[x], true, JobNoOrCNNO, ji, total);
                                subGroupItemAL.add(postdata);
                                itemAL.add(subGroupItemAL);
                            }

                        } else {
                            headerALselectedIDX = headerAl.indexOf(JobNoOrCNNO);
                            subGroupItemAL = (List<DOinvCheck_PostData>) itemAL.get(headerALselectedIDX);
                            //postdata = new DOinvCheck_PostData(ji.JobsIDX, ji.DOList[x].DOIDX, ji.DOList[x].Invoice, true, JobNoOrCNNO, ji);
                            if (!objGlobalMethods.verifyBarcode(ji.BarcodeTags[x].toUpperCase())) {
                                postdata = new DOinvCheck_PostData(ji.JobsIDX, ji.BarcodeTags[x], true, JobNoOrCNNO, ji, total);
                                subGroupItemAL.add(postdata);
                            }
                        }
                    }
                }

                /*if (ji.DOList != null || ji.DOList.length > 0) {
                    for (int x = 0; x<=ji.DOList.length-1;x++){
                        if (!headerAl.contains(JobNoOrCNNO)) {
                            headerAl.add(JobNoOrCNNO);
                            subGroupItemAL = new ArrayList<DOinvCheck_PostData>();
                            postdata = new DOinvCheck_PostData(ji.JobsIDX, ji.DOList[x].DOIDX, ji.DOList[x].Invoice, true, JobNoOrCNNO, ji);
                            subGroupItemAL.add(postdata);
                            itemAL.add(subGroupItemAL);
                        } else {
                            headerALselectedIDX = headerAl.indexOf(JobNoOrCNNO);
                            subGroupItemAL = (List<DOinvCheck_PostData>) itemAL.get(headerALselectedIDX);
                            postdata = new DOinvCheck_PostData(ji.JobsIDX, ji.DOList[x].DOIDX, ji.DOList[x].Invoice, true, JobNoOrCNNO, ji);
                            subGroupItemAL.add(postdata);
                        }
                    }
                }*/
            }

            for (int i = 0; i <= headerAl.size() - 1; i++) {
                listDataHeader.add(headerAl.get(i));
                listDataChildSorted2.put(headerAl.get(i), (List<DOinvCheck_PostData>) itemAL.get(i));
            }

            expandableSortedListAdapter = new DOinvCheck_PostItemAdapter(this, listDataHeader, listDataChildSorted2);
            if (existing_mChildCheckStates != null && expandableSortedListAdapter.mChildCheckStates != null) {
                expandableSortedListAdapter.mChildCheckStates = existing_mChildCheckStates;
            }
            expandableSortedListAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);

            // setting list adapter
            expListView.setAdapter(expandableSortedListAdapter);




            setGroupIndicatorToRight();
            expandSortedList(expandableSortedListAdapter);

        } catch (Exception e) {
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_errorretrivedata) + "\n" + e.getMessage().toString());
        } finally {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    expListView.clearFocus();
                }
            }, 100);
        }

    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    private void expandSortedList(DOinvCheck_PostItemAdapter adapter) {
        for (int i = 0; i <= adapter.getGroupCount() - 1; i++) {
            expListView.expandGroup(i);
        }
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at DOinvCheckActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DOinvCheckActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


}
