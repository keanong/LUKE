package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xdel.luke.BottomSheet.ProgressBarDialog;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.FixedChecklist.fixedChecklists;
import com.xdel.luke.Locker.ShipmentActivity;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.SlideDateTimePicker.SlideDateListener;
import com.xdel.luke.SlideDateTimePicker.SlideDatePicker;
import com.xdel.luke.SlideDateTimePicker.SlideDateTimeListener;
import com.xdel.luke.SlideDateTimePicker.SlideDateTimePicker;
import com.xdel.luke.checklist.ChecklistVariables;
import com.xdel.luke.objClasses.checklistObjForMassOKP;
import com.xdel.luke.objClasses.checklist_PostData;
import com.xdel.luke.objClasses.checklist_PostItemAdapter;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.jobSelectionInfo;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objConvertedBitmapToBase64;
import com.xdel.luke.objClasses.objDelBlockUnits;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.pdfviewer.pdfclass;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.templogger.objTemp;
import com.xdel.luke.templogger.temploggerclass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CheckListActivity extends AppCompatActivity {
    //public class CheckListActivity extends FragmentActivity {

    public static ListView listView;
    public static ArrayList<checklist_PostData> al;
    public static FragmentManager manager;
    public static String ActionCode;
    public static String fromPage = "";
    public static Intent nextActivtyIntent;
    public static Boolean openCamera;
    public static Integer itemIndex;
    public static ArrayList<String> PhotoImagePathList;
    public static ArrayList<CustomGallery> dataTlist;
    public static String signatureString;

    Context context;
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    private Button btnCheckListCANCEL, btnCheckListNext;
    private FragmentActivity activity;
    public checklist_PostItemAdapter adapter;
    SimpleDateFormat Dateformat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat DateTimeformat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
    String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;
    ProgressDialog pd;
    boolean hasOtp;
    boolean hasForceOTP;
    String TypeOfPinVerification = "";
    private ProgressBarDialog pbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        /*String[] ss = new String[]{};
        String x = ss[1];*/

        try {
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

            setContentView(R.layout.activity_check_list);

            listView = (ListView) findViewById(R.id.listView);
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub
                }

                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        String s = "";
                        if (adapter.hidden != null && adapter.hidden.length > 0)
                            s = Arrays.toString(adapter.hidden);
                        Log.i("scrolling", "scrolling stopped...");
                        Log.i("hidden", s);
                        Log.i("adapter.getCount()", (adapter != null && adapter.getCount() > 0) ? String.valueOf(adapter.getCount()) : "0");
                    }
                }
            });
            btnCheckListNext = (Button) findViewById(R.id.btnCheckListNext);
            activity = (FragmentActivity) this;
            manager = activity.getSupportFragmentManager();
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CheckListActivity onCreate. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("CheckListActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }

            context = myApp.getAppContext();
            //Gson gson = new Gson();

            try {
                String[] prefName = new String[]{"objMultiSelectInitialCriteriaJsonStr", "mChildCheckStates2PrefStr"};
                String[] prefvalue = new String[]{"", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                //JobsHandler.resetrePositionedGroupItems(GcmPreferences);
                if (al != null && al.size() == 0) {
                    onBackPressed();
                } else {
                    if (fromPage != null && fromPage.equalsIgnoreCase("JobsListGroupActivity")) {
                        String checklistObjForMassOKPJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "checklistObjForMassOKP");
                        if (!objGlobalMethods.IsEmptyString(checklistObjForMassOKPJsonStr)) {
                            //loadlistForMassOKP(checklistObjForMassOKPJsonStr);
                            loadlistGroup(checklistObjForMassOKPJsonStr);
                        } else {
                            objGlobalMethods.writeToFile("checklistObjForMassOKPJsonStr is empty at ChecklistActivity onResume.", "", "", "", "");
                            back();
                        }
                    } else {
                        loadlistNew00(); //shifted this to asynctask
                        /*prepToLoadListNew0 ll = new prepToLoadListNew0();
                        ll.execute();*/
                    }
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception caught at CheckListActivity onResume. Exception: " + e.getMessage().toString(), "", "", "", "");
                showErrorAlert();
            }
        }
    }

    @Override
    protected void onDestroy() {
        //al = null;
        manager = null;
        listView = null;
        checklist_PostItemAdapter.alertD = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CheckListActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        adapter = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CheckListActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            Intent intent = null;
            String checklistvalue = "";
            if (fromPage == null) {
                //intent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                intent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            } else if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobsListGroupActivity"))) {
                //intent = new Intent(this, JobsListGroupActivity.class);
                intent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && (fromPage.equals("JobInfoActivity") || fromPage.equals("OtpActivity"))) {
                //intent = new Intent(this, JobInfoActivity.class);
                intent = new Intent(myApp.currentActivity, JobInfoActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("DOinvCheckActivity")) {
                //intent = new Intent(this, DOinvCheckActivity.class);
                intent = new Intent(myApp.currentActivity, DOinvCheckActivity.class);
            } else if (!fromPage.toUpperCase().equals("") && fromPage.equals("ShipmentActivity")) {
                //intent = new Intent(this, ShipmentActivity.class);
                intent = new Intent(myApp.currentActivity, ShipmentActivity.class);
            } else if (!GcmPreferences.getString("hfJobInfoJsonStr", "").isEmpty()) {
                intent = new Intent(myApp.currentActivity, JobInfoActivity.class);
            }

            if (fromPage == null || (fromPage != null && !fromPage.toUpperCase().equals("") && (fromPage.equals("JobsListGroupActivity")))) {
                checklistvalue = "";
            } else {
                checklistvalue = getCheckListValueNew();
            }

            /*String[] prefName = new String[]{"checklistvalue", objGlobalVariables.checkedItemsName};
            String[] prefvalue = new String[]{checklistvalue, ""};*/
            String[] prefName = new String[]{objGlobalVariables.checkedItemsName};
            String[] prefvalue = new String[]{""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            objGlobalMethods.setChecklistRelatedSP_KeyValue("checklistvalue", checklistvalue);

            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(intent);
            finish();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity back(). Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void btnCheckListCANCEL_OnClick(View view) {
        onBackPressed();
    }

    public void btnNextClick_OnClick(View view) {
        if (al != null && al.size() > 0) {
            next2();
        } else {
            objGlobalMethods.writeToFile("Unable to next CheckListActivity. al is null or empty.", "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorupdatep1p2));
        }
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        //parentIntent = new Intent(CheckListActivity.this, MainActivity.class);
        parentIntent = new Intent(myApp.currentActivity, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_list, menu);
        /*objJobInfo ji;
        Gson gson = new Gson();
        if (GcmPreferences == null) {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
        }
        ji = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
        if (!JobsHandler2.canUnbrick(ji)) {
            menu.findItem(R.id.pdf).setVisible(false);
        }*/
        menu.findItem(R.id.pdf).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.pdf) {
            if (pdfclass.pdfExist()) {
                pdfclass.openpdf();
            } else {
                objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.btn_ok), myApp.getAppContext().getString(R.string.action_nopdf)).show();
            }
        }

       /* switch (item.getItemId()) {
            case R.id.pdf:
                if (pdfclass.pdfExist()) {
                    pdfclass.openpdf();
                } else {
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.btn_ok), myApp.getAppContext().getString(R.string.action_nopdf)).show();
                }
                break;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void showErrorAlert() {
        try {
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity == null ? this : myApp.currentActivity);
            builder.setCancelable(false);
            builder.setMessage(myApp.getAppContext().getString(R.string.alert_nodata));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                    JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                    Intent i;
                    //i = new Intent(CheckListActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                    boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                    i = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                    startActivity(i);
                    finish();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity showErrorAlert. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            Intent i = new Intent(myApp.currentActivity == null ? this : myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            startActivity(i);
            finish();
        }
        /*finally {
            String[] ss = new String[]{};
            String x = ss[1];
        }*/
    }

    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private Boolean IsParentOfNextDataNew(int ParentHierarchyCode, int nextDataIndex, String[] CheckList, Boolean show10to19, Boolean show20to29, Boolean ParentCurrentIsParentStatus, SharedPreferences gcmp) {
        Boolean asd = false;
        String codes = "";
        int index = 0;
        Boolean nextaddList = false;
        String[] nextcodesArr = null;
        String nextHierarchyCode_A = "0";
        int nextHierarchyCode = 0;
        String nextActionCode_A = "";
        String checklist = null;
        try {
            do {
                checklist = CheckList[nextDataIndex];
                if (checklist != null && !checklist.isEmpty()) {
                    index = checklist.indexOf("|");
                    if (index >= 0) {
                        codes = CheckList[nextDataIndex].substring(0, index);
                        if (codes != "") {
                            nextcodesArr = codes.split("\\.");
                            nextHierarchyCode_A = nextcodesArr[0];
                            if (!nextHierarchyCode_A.equals("")) {
                                try {
                                    nextHierarchyCode = Integer.parseInt(nextHierarchyCode_A);
                                } catch (Exception e) {
                                    nextHierarchyCode = 0;
                                    String currJobInfoIDX = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
                                    objGlobalMethods.writeToFile("Exception caught at CheckListActivity IsParentOfNextDataNew convert nextHierarchyCode_A to integer for JobsIDX: " + currJobInfoIDX + ". Exception: " + e.getMessage().toString(), "", "", "", "");
                                }
                            }

                            if ((nextHierarchyCode % 10) != 0) {
                                nextaddList = (nextHierarchyCode >= -10 && nextHierarchyCode <= -1)
                                        || (nextHierarchyCode >= 0 && nextHierarchyCode <= 9)
                                        || (nextHierarchyCode >= 10 && nextHierarchyCode <= 19 && show10to19)
                                        || (nextHierarchyCode >= 20 && nextHierarchyCode <= 29 && show20to29)
                                        || (nextHierarchyCode >= 50);

                                if (nextaddList) {
                                    nextActionCode_A = nextcodesArr[1];
                                    if (nextActionCode_A.equals(ActionCode)) {
                                    /*if (ParentHierarchyCode == nextHierarchyCode) {
                                        asd = false;
                                    } else if (ParentHierarchyCode + 1 == nextHierarchyCode) {
                                        asd = true;
                                    }*/
                                        if (ParentHierarchyCode == nextHierarchyCode) {
                                            asd = false;
                                        } else if ((ParentHierarchyCode + 1 == nextHierarchyCode) || ParentHierarchyCode - 1 == nextHierarchyCode) {
                                            asd = true;
                                        }
                                    }
                                }
                            } else if ((nextHierarchyCode % 10) == 0 && (ParentHierarchyCode % 10) == 0) {
                                asd = true;
                            } else {
                                asd = false;
                            }
                        }
                    }
                }
                if (!asd)
                    nextDataIndex++;
                //} while (!asd);
            } while (!asd && nextDataIndex <= CheckList.length - 1);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity IsParentOfNextDataNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    private Boolean IsParentOfNextData(int ParentHierarchyCode, int nextDataIndex, String[] CheckList, Boolean show10to19, Boolean show20to29, Boolean ParentCurrentIsParentStatus, SharedPreferences gcmp) {
        Boolean asd = false;
        String codes = "";
        int index = 0;
        Boolean nextaddList = false;
        String[] nextcodesArr = null;
        String nextHierarchyCode_A = "0";
        int nextHierarchyCode = 0;
        String nextActionCode_A = "";
        String checklist = null;
        try {
            checklist = CheckList[nextDataIndex];
            if (checklist != null && !checklist.isEmpty()) {
                index = checklist.indexOf("|");
                if (index >= 0) {
                    codes = CheckList[nextDataIndex].substring(0, index);
                    if (codes != "") {
                        nextcodesArr = codes.split("\\.");
                        nextHierarchyCode_A = nextcodesArr[0];
                        if (!nextHierarchyCode_A.equals("")) {
                            try {
                                nextHierarchyCode = Integer.parseInt(nextHierarchyCode_A);
                            } catch (Exception e) {
                                nextHierarchyCode = 0;
                                String currJobInfoIDX = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
                                objGlobalMethods.writeToFile("Exception caught at CheckListActivity IsParentOfNextData convert nextHierarchyCode_A to integer for JobsIDX: " + currJobInfoIDX + ". Exception: " + e.getMessage().toString(), "", "", "", "");
                            }
                        }

                        if ((nextHierarchyCode % 10) != 0) {
                            nextaddList = (nextHierarchyCode >= -10 && nextHierarchyCode <= -1)
                                    || (nextHierarchyCode >= 0 && nextHierarchyCode <= 9)
                                    || (nextHierarchyCode >= 10 && nextHierarchyCode <= 19 && show10to19)
                                    || (nextHierarchyCode >= 20 && nextHierarchyCode <= 29 && show20to29)
                                    || (nextHierarchyCode >= 50);

                            if (nextaddList) {
                                nextActionCode_A = nextcodesArr[1];
                                if (nextActionCode_A.equals(ActionCode)) {
                                    /*if (ParentHierarchyCode == nextHierarchyCode) {
                                        asd = false;
                                    } else if (ParentHierarchyCode + 1 == nextHierarchyCode) {
                                        asd = true;
                                    }*/
                                    if (ParentHierarchyCode == nextHierarchyCode) {
                                        asd = false;
                                    } else if ((ParentHierarchyCode + 1 == nextHierarchyCode) || ParentHierarchyCode - 1 == nextHierarchyCode) {
                                        asd = true;
                                    }
                                }
                            }
                        } else if ((nextHierarchyCode % 10) == 0 && (ParentHierarchyCode % 10) == 0) {
                            asd = true;
                        } else {
                            asd = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    private int getNegativeChildParentIDXviaALNew(int currentIndex, int currentHierarchyCode, SharedPreferences gcmp, String sss) {
        int ParentIDX = -99;
        int prevHierarchyCode = 0;
        int prevIndex = currentIndex - 1;
        int absV = 0;
        String xxx = sss;
        try {
            if (currentHierarchyCode < 0 && prevIndex > 0 && al != null && al.size() > 0) {
                for (int i = al.size() - 1; i >= 0; i--) {
                    prevHierarchyCode = al.get(i).HierarchyCode;
                    if (prevHierarchyCode % 10 == 0) {
                        //parent is 0
                        if (currentHierarchyCode < -10) {
                            absV = Math.abs(currentHierarchyCode);
                            if (currentHierarchyCode - 1 + absV + absV == prevHierarchyCode) {
                                ParentIDX = i;
                                break;
                            }
                        } else if (currentHierarchyCode + 1 == prevHierarchyCode) {
                            ParentIDX = i;
                            break;
                        }
                    } else if ((currentHierarchyCode + 1) == prevHierarchyCode) {
                        ParentIDX = i;
                        break;
                    } else if (prevHierarchyCode > 0) {
                        absV = Math.abs(currentHierarchyCode);
                        if ((currentHierarchyCode + absV + (absV - 1)) == prevHierarchyCode) {
                            ParentIDX = i;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            ;
            objGlobalMethods.writeToFile("Exception at CheckListActivity getNegativeChildParentIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return ParentIDX;
        }
    }

    private int getNegativeChildParentIDXviaAL(int currentIndex, int currentHierarchyCode, SharedPreferences gcmp, String sss) {
        int ParentIDX = -99;
        int prevHierarchyCode = 0;
        int prevIndex = currentIndex - 1;
        int absV = 0;
        String xxx = sss;
        try {
            if (currentHierarchyCode < 0 && prevIndex > 0 && al != null && al.size() > 0) {
                for (int i = al.size() - 1; i >= 0; i--) {
                    prevHierarchyCode = al.get(i).HierarchyCode;
                    if (prevHierarchyCode % 10 == 0) {
                        //parent is 0
                        if (currentHierarchyCode < -10) {
                            absV = Math.abs(currentHierarchyCode);
                            if (currentHierarchyCode - 1 + absV + absV == prevHierarchyCode) {
                                ParentIDX = i;
                                break;
                            }
                        } else if (currentHierarchyCode + 1 == prevHierarchyCode) {
                            ParentIDX = i;
                            break;
                        }
                    } else if (prevHierarchyCode > 0) {
                        absV = Math.abs(currentHierarchyCode);
                        if ((currentHierarchyCode + absV + (absV - 1)) == prevHierarchyCode) {
                            ParentIDX = i;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            ;
            objGlobalMethods.writeToFile("Exception at CheckListActivity getNegativeChildParentIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return ParentIDX;
        }
    }

    private int getNegativeChildParentIDX(int currentIndex, int currentHierarchyCode, String[] CheckList, SharedPreferences gcmp, String sss) {
        int ParentIDX = -99;
        boolean asd = false;
        String codes = "";
        int index = 0;
        Boolean nextaddList = false;
        String[] prevcodesArr = null;
        String prevHierarchyCode_A = "0";
        int prevHierarchyCode = 0;
        String prevActionCode_A = "";
        String checklist = null;
        int prevIndex = currentIndex - 1;
        int absV = 0;
        String xxx = sss;
        try {
            if (currentHierarchyCode < 0) {
                if (prevIndex > 0) {
                    for (int i = prevIndex; i >= 0; i--) {
                        checklist = CheckList[i];
                        if (checklist != null && !checklist.isEmpty()) {
                            index = checklist.indexOf("|");
                            if (index >= 0) {
                                codes = checklist.substring(0, index);
                                if (codes != "") {
                                    prevcodesArr = codes.split("\\.");
                                    prevHierarchyCode_A = prevcodesArr[0];
                                    if (!prevHierarchyCode_A.equals("")) {
                                        try {
                                            prevHierarchyCode = Integer.parseInt(prevHierarchyCode_A);
                                        } catch (Exception e) {
                                            prevHierarchyCode = 0;
                                            String currJobInfoIDX = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
                                            objGlobalMethods.writeToFile("Exception caught at CheckListActivity getNegativeChildParentIDX convert prevHierarchyCode to integer for JobsIDX: " + currJobInfoIDX + ". Exception: " + e.getMessage().toString(), "", "", "", "");
                                        }
                                    }

                                    if (prevHierarchyCode % 10 == 0) {
                                        //parent is 0
                                        if (currentHierarchyCode < -10) {
                                            absV = Math.abs(currentHierarchyCode);
                                            if (currentHierarchyCode - 1 + absV + absV == prevHierarchyCode) {
                                                ParentIDX = i;
                                                break;
                                            }
                                        } else if (currentHierarchyCode + 1 == prevHierarchyCode) {
                                            ParentIDX = i;
                                            break;
                                        }
                                    } else if (prevHierarchyCode > 0) {
                                        absV = Math.abs(currentHierarchyCode);
                                        if ((currentHierarchyCode + absV + (absV - 1)) == prevHierarchyCode) {
                                            ParentIDX = i;
                                            break;
                                        }
                                    }
                                    /*else if (prevHierarchyCode < 0) {
                                        //parent is -1/-2/-3 (more for YN ctrl)
                                        if ((currentHierarchyCode + 1) == prevHierarchyCode) {
                                            ParentIDX = i;
                                            break;
                                        }
                                    }*/
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            ;
            objGlobalMethods.writeToFile("Exception at CheckListActivity getNegativeChildParentIDX. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return ParentIDX;
        }
    }

    private void loadlistForMassOKP(String checklistObjForMassOKPJsonStr) {
        checklist_PostData CurrData;
        checklist_PostData NextData;
        String HierarchyCode_A = "0";
        int HierarchyCode = 0;
        int HierarchyCodeP = -1;
        String ActionCode_A = "";
        String ControlCode = "";
        String ManOptCode = "";
        String DisplayText = "";
        String DisplayTextFinal = "";
        String[] Option = null;
        String[] codesArr = null;
        String codes = "";
        int index = 0;
        int x = 0;
        int optionZHindex = 0;
        int NegativeChildParentIDX = -99;

        String displaytextNoptions = "";
        String options = "";
        String optionENtxt = "";
        String optionZHtxt = "";
        String[] optionsArr = null;
        String[] optionsArrEN = null;
        String[] optionsArrZH = null;
        ArrayList<String> asd = new ArrayList<>();
        Boolean hasVerifiedOTP = false;
        Boolean isBusiness = false;
        Boolean show10to19 = false;
        Boolean show20to29 = false;
        Boolean show40to49 = false;
        Boolean show50to59 = false;
        Boolean show60to69 = false;

        Boolean addList = false;
        Boolean isParent = false;
        Boolean is8192 = false;
        //boolean CustIDVerificationNotRequired = JobsHandler.CustIDVerificationNotRequired(ji);
        Gson gson = new Gson();
        checklistObjForMassOKP cl = gson.fromJson(checklistObjForMassOKPJsonStr, checklistObjForMassOKP.class);
        String[] CheckList = cl.getChecklist();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        try {
            langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");
            String jobflag2str = objGlobalMethods.getGCMPrefs(GcmPreferences, "jobflag2");
            Integer jobflag2 = 0;
            try {
                jobflag2 = Integer.parseInt(!objGlobalMethods.IsEmptyString(jobflag2str) ? jobflag2str : "0");
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at CheckListActivity loadlistForMassOKP convert jobflag2. Exception: " + e.getMessage().toString(), "", "", "", "");
                jobflag2 = 0;
            }

            boolean CustIDVerificationRequired = (jobflag2 & 128) == 128;
            boolean CustIDVerificationNotRequired = (jobflag2 & 32) == 32;
            if (al == null) {

                /*hasVerifiedOTP = (!CustIDVerificationRequired && CustIDVerificationNotRequired)
                        || (CustIDVerificationRequired && CustIDVerificationNotRequired) || (!CustIDVerificationRequired && !CustIDVerificationNotRequired);*/

                hasVerifiedOTP = !CustIDVerificationRequired;


                show10to19 = !hasVerifiedOTP ? true : false;
                show20to29 = hasVerifiedOTP ? true : false;
                show40to49 = hasVerifiedOTP ? false : true;
                //show50to59 = cl != null && (cl.FLAG & 8192) == 8192 ? true : false;
                show50to59 = false;
                show60to69 = !show50to59;
                /*List<String> CheckListList = null;
                if (CustIDVerificationNotRequired && ji.CheckList.length > 0) {
                    Context ctx = myApp.getAppContext();
                    CheckListList = new ArrayList<String>(Arrays.asList(ji.CheckList));
                    //CheckListList.add(0, "0.DS.LB.MD|Request for original NRIC, verify name and LAST 4 CHARACTERS of NRIC.[ZH]要求实际收件人身份证并验证名字和身份证号码最后4个字.");
                    String IDVerificationName = objGlobalMethods.getGCMPrefs(GcmPreferences, "IDVerificationName");
                    String name = !objGlobalMethods.IsEmptyString(IDVerificationName) ? " (" + IDVerificationName + ")" : "";
                    String fullchecklist = "0.DS.LB.MD|" + ctx.getString(R.string.idverifynotrequireden) + name + "[ZH]" + ctx.getString(R.string.idverifynotrequiredzh) + name;
                    CheckListList.add(0, fullchecklist);
                    ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);
                }*/

                for (int i = 0; i <= CheckList.length - 1; i++) {
                    addList = false;
                    CurrData = null;
                    if (al == null) {
                        al = new ArrayList<>();
                    }

                    index = CheckList[i].indexOf("|");
                    //next data portion
                    x = i + 1 <= CheckList.length - 1 ? i + 1 : i + 1 == CheckList.length ? i : -1;

                    //next data portion
                    if (index >= 0) {
                        codes = CheckList[i].substring(0, index);
                        if (codes != "") {
                            codesArr = codes.split("\\.");
                            HierarchyCode_A = codesArr[0];
                            if (!HierarchyCode_A.equals("")) {
                                try {
                                    HierarchyCode = Integer.parseInt(HierarchyCode_A);
                                } catch (Exception e) {
                                    HierarchyCode = 0;
                                    String currJobInfoIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                                    objGlobalMethods.writeToFile("Exception caught at CheckListActivity loadlist convert HierarchyCode_A to integer for JobsIDX: " + currJobInfoIDX + ". Exception: " + e.getMessage().toString(), "", "", "", "");
                                }
                            }

                            addList = (HierarchyCode < 0)
                                    || (HierarchyCode >= 0 && HierarchyCode <= 9)
                                    || (HierarchyCode >= 10 && HierarchyCode <= 19 && show10to19)
                                    || (HierarchyCode >= -19 && HierarchyCode <= -10 && show10to19)
                                    || (HierarchyCode >= 20 && HierarchyCode <= 29 && show20to29)
                                    || (HierarchyCode >= 40 && HierarchyCode <= 49 && show40to49)
                                    || (HierarchyCode >= 50 && HierarchyCode <= 59 && show50to59)
                                    || (HierarchyCode >= 60 && HierarchyCode <= 69 && show60to69)
                                    || (HierarchyCode >= 70);

                            //check negative values for series 10 (10-19 only show if no verified OTP)
                            if (HierarchyCode >= -19 && HierarchyCode <= -10) {
                                addList = show10to19;
                            }

                            if (addList) {
                                ActionCode_A = codesArr[1];
                                ControlCode = codesArr[2];
                                ManOptCode = codesArr[3];
                                if (ActionCode_A.equals(ActionCode)) {
                                    CurrData = new checklist_PostData(null, 0, null, null, null, null,
                                            null, null, false, null, null, null, cl.FLAG);
                                    CurrData.setFullCode(codes);
                                    CurrData.setActionCode(ActionCode_A);
                                    CurrData.setControlCode(ControlCode);
                                    CurrData.setManOptCode(ManOptCode);
                                    displaytextNoptions = CheckList[i].substring(index + 1, CheckList[i].length());
                                    if (displaytextNoptions != "") {
                                        index = displaytextNoptions.indexOf("|");
                                        if (index >= 0) {
                                            DisplayText = displaytextNoptions.substring(0, index);

                                            int newlineIndex = DisplayText.indexOf("[ZH]");
                                            if (newlineIndex >= 0) {
                                                DisplayTextFinal = DisplayText.substring(0, newlineIndex);
                                            } else {
                                                DisplayTextFinal = DisplayText;
                                            }

                                            //CurrData.setDisplayText(DisplayText);
                                            CurrData.setDisplayText(DisplayTextFinal);
                                            if (langType.equals("zh")) {
                                                if (newlineIndex >= 0) {
                                                    CurrData.setDisplayTextCN(DisplayText.substring(newlineIndex + 4, DisplayText.length()));
                                                } else {
                                                    CurrData.setDisplayTextCN(objGlobalMethods.ReplaceCheckListDisplayTextLang(DisplayTextFinal));
                                                }
                                            } else {
                                                CurrData.setDisplayTextCN(DisplayTextFinal);
                                            }

                                            options = displaytextNoptions.substring(index + 1, displaytextNoptions.length());
                                            if (options != "" && options.length() > 0) {
                                                //new code
                                                optionsArr = options.split("\\|");
                                                optionsArrEN = new String[optionsArr.length];
                                                optionsArrZH = new String[optionsArr.length];
                                                for (int l = 0; l <= optionsArr.length - 1; l++) {
                                                    optionZHindex = optionsArr[l].indexOf("[ZH]");
                                                    if (optionZHindex > 0) {
                                                        optionENtxt = optionsArr[l].substring(0, optionZHindex);
                                                        optionZHtxt = optionsArr[l].substring(optionZHindex + 4, optionsArr[l].length());
                                                    } else {
                                                        optionENtxt = optionsArr[l];
                                                        optionZHtxt = optionsArr[l];
                                                    }
                                                    optionsArrEN[l] = optionENtxt;
                                                    optionsArrZH[l] = langType.equals("zh") ? optionZHtxt : optionENtxt;
                                                }

                                                CurrData.setOption(optionsArrEN);
                                                CurrData.setOptionCN(optionsArrZH);
                                                //new code
                                            }

                                        } else {
                                            DisplayText = displaytextNoptions;

                                            int newlineIndex = DisplayText.indexOf("[ZH]");
                                            if (newlineIndex >= 0) {
                                                DisplayTextFinal = DisplayText.substring(0, newlineIndex);
                                            } else {
                                                DisplayTextFinal = DisplayText;
                                            }

                                            CurrData.setDisplayText(DisplayTextFinal);
                                            if (langType.equals("zh")) {
                                                if (newlineIndex >= 0) {
                                                    CurrData.setDisplayTextCN(DisplayText.substring(newlineIndex + 4, DisplayText.length()));
                                                } else {
                                                    CurrData.setDisplayTextCN(objGlobalMethods.ReplaceCheckListDisplayTextLang(DisplayTextFinal));
                                                }
                                            } else {
                                                CurrData.setDisplayTextCN(DisplayTextFinal);
                                            }
                                        }

                                        //these values are inserted into OptionValue List first as they are shown upon load
                                        if (CurrData.ManOptCode.equals("OP")) {
                                            //type 1 == fa (any 1 is yes) 2 == fy (all must be yes) 3 == fn (all must be no)
                                            if (ControlCode.equals("YN") || ControlCode.equals("AY") || ControlCode.equals("AN") || ControlCode.equals("FY") || ControlCode.equals("FN")) {
                                                CurrData.OptionValue = new ArrayList<>();
                                                CurrData.OptionValue.add("NO");
                                            } else if (ControlCode.equals("DD") || ControlCode.equals("DT")) {
                                            /*data.OptionValue = new ArrayList<>();
                                            data.OptionValue.add(Dateformat.format(new Date()));
                                            data.setSelectedDate(new Date());*/
                                            } else if (ControlCode.equals("CG") || ControlCode.equals("RG")) {
                                            /*if (data.Option != null && data.Option.length > 0) {
                                                data.OptionValue = new ArrayList<>();
                                                for (int q = 0; q <= data.Option.length - 1; q++) {
                                                    data.OptionValue.add("NO");
                                                }
                                            }*/
                                            }
                                            //CurrData.setIsChecked(true);
                                        } else if (CurrData.ManOptCode.equals("MD") && CurrData.ControlCode.equals("CB")) {
                                            //CurrData.IsChecked = true;
                                        }
                                        //these values are inserted into OptionValue List first as they are shown upon load

                                        if (ControlCode.equals("ID")) {
                                            CurrData.setDisplayText((cl.FLAG & 8192) == 8192 ? "" : objGlobalMethods.getGCMPrefs(GcmPreferences, "IDVerificationName"));
                                            CurrData.setDisplayTextCN((cl.FLAG & 8192) == 8192 ? "" : objGlobalMethods.getGCMPrefs(GcmPreferences, "IDVerificationName"));
                                        }

                                        if (ControlCode.equals("PR") && dataTlist != null && dataTlist.size() > 0) {
                                            boolean added = false;
                                            for (int t = 0; t <= dataTlist.size() - 1; t++) {
                                                if (dataTlist.get(t).checklistitemidx == i) {
                                                    CurrData.addDataT(dataTlist.get(t));
                                                    added = true;
                                                }
                                            }
                                            /*if (added) {
                                                CurrData.setIsChecked(true);
                                                if (CurrData.OptionValue != null) {
                                                    CurrData.OptionValue.clear();
                                                    CurrData.OptionValue.add("YES");
                                                } else {
                                                    CurrData.OptionValue = new ArrayList<String>();
                                                    CurrData.OptionValue.add("YES");
                                                }
                                            }*/
                                        }

                                        if (ControlCode.equals("PH") || ControlCode.equals("PR") || ControlCode.equals("SO")) {
                                            CurrData.setIsChecked(true);
                                            if (CurrData.OptionValue != null) {
                                                CurrData.OptionValue.clear();
                                            } else {
                                                CurrData.OptionValue = new ArrayList<String>();
                                            }
                                            CurrData.OptionValue.add("YES");
                                        }

                                        if (ControlCode.equals("ID") || ControlCode.equals("V1")) {
                                            CurrData.setIsChecked(true);
                                        }

                                        CurrData.FinalManOptCode = ((HierarchyCode % 10) != 0) && (HierarchyCode > 0) ? "OP" : ManOptCode;
                                        CurrData.setHierarchyCode(HierarchyCode);

                                        if (x >= 0) {
                                            CurrData.setIsParent(IsParentOfNextData(HierarchyCode, x, CheckList, show10to19, show20to29, CurrData.getIsParent(), GcmPreferences) ? true : false);
                                        }

                                        CurrData.setIsChild(HierarchyCode % 10 == 0 ? false : true);

                                        /*if (CurrData.getIsChild()) {
                                            HierarchyCodeP = HierarchyCode;
                                            //for (int p = i-1; p>=0; p--) {
                                            for (int p = al.size() - 1 == -1 ? 0 : al.size() - 1; p >= 0; p--) {
                                                try {
                                                    //HierarchyCodeP = Integer.parseInt(ji.CheckList[p].substring(0, ji.CheckList[p].indexOf(".")));
                                                    HierarchyCodeP = al.get(p).getHierarchyCode();
                                                } catch (Exception e) {
                                                    String stack = JobsHandler.getStackTraceString(e);
                                                }
                                                if (HierarchyCodeP < HierarchyCode) {
                                                    CurrData.setParentIDX(p);
                                                    break;
                                                }
                                            }
                                        }*/

                                        if (CurrData.getIsChild()) {
                                            if (HierarchyCode < 0) {
                                                NegativeChildParentIDX = getNegativeChildParentIDXviaAL(i, HierarchyCode, GcmPreferences, CurrData.DisplayText);
                                                if (NegativeChildParentIDX != -99) {
                                                    CurrData.setParentIDX(NegativeChildParentIDX);
                                                }
                                            } else {
                                                HierarchyCodeP = HierarchyCode;
                                                for (int p = al.size() - 1 == -1 ? 0 : al.size() - 1; p >= 0; p--) {
                                                    try {
                                                        //HierarchyCodeP = Integer.parseInt(ji.CheckList[p].substring(0, ji.CheckList[p].indexOf(".")));
                                                        HierarchyCodeP = al.get(p).getHierarchyCode();
                                                    } catch (Exception e) {
                                                        String stack = JobsHandler.getStackTraceString(e);
                                                    }
                                                /*if (HierarchyCodeP < HierarchyCode) {
                                                    CurrData.setParentIDX(p);
                                                    break;
                                                }*/
                                                    if (HierarchyCodeP >= 0 && HierarchyCodeP < HierarchyCode) {
                                                        CurrData.setParentIDX(p);
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        CurrData.setIndex(al.size());
                                        al.add(CurrData);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            } else {

                for (int i = 0; i <= al.size() - 1; i++) {
                    if (al.get(i).ControlCode.equals("SO")) {
                        al.get(i).setIsChecked(true);
                        if (al.get(i).OptionValue != null) {
                            al.get(i).OptionValue.clear();
                        } else {
                            al.get(i).OptionValue = new ArrayList<String>();
                        }
                        al.get(i).OptionValue.add("YES");
                    }
                }
            }

            CheckListActivity.PhotoImagePathList = null;
            CheckListActivity.dataTlist = null;

            if (al != null && al.size() > 0) {
                objGlobalMethods.writeToFile("Checklist viewed for JobsIDX: " + cl.getJobsIDXs(), "", "", "", "");
                //adapter = new checklist_PostItemAdapter(this, al);
                adapter = new checklist_PostItemAdapter(myApp.getAppContext(), al);
                listView.setAdapter(adapter);
                String pr_latemsg0_reqFuturePrompt = objGlobalMethods.getGCMPrefs(GcmPreferences, "pr_latemsg0_reqFuturePrompt");
                if (!objGlobalMethods.IsEmptyString(pr_latemsg0_reqFuturePrompt)) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "pr_latemsg0_reqFuturePrompt", "");
                    objGlobalMethods.showPR_lateprompt();
                }
            } else if (al != null && al.size() == 0 && hasVerifiedOTP) {
                objGlobalMethods.writeToFile("Checklist skipped due hasVerifiedOTP for JobsIDX: " + cl.getJobsIDXs(), "", "", "", "");
                startActivity(nextActivtyIntent);
                //finish(); //newly inserted
            } else {
                objGlobalMethods.writeToFile("al is null or empty at ChecklistActivity loadlistForMassOKP.", "", "", "", "");
                //showErrorAlert();
                back();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at CheckListActivity loadlistForMassOKP. Exception: " + e.getMessage().toString(), "", "", "", "");
            showErrorAlert();
        }
    }

    private int getIndexToAdd(String[] cl) {
        String cc = "";
        String item = "";
        String codes = "";
        int index = 0;
        String[] codesArr = null;
        int idx = -1;
        try {
            if (cl != null && cl.length > 0) {
                for (int i = 0; i <= cl.length - 1; i++) {
                    item = cl[i];
                    index = item.indexOf("|");
                    if (index >= 0) {
                        codes = item.substring(0, index);
                        codesArr = codes.split("\\.");
                        cc = codesArr[2];
                        if (cc.equalsIgnoreCase("PH") || cc.equalsIgnoreCase("PR") || cc.equalsIgnoreCase("TL")) {
                            idx = i;
                            idx++;
                        } else if (!cc.equalsIgnoreCase("PH") && !cc.equalsIgnoreCase("PR")) {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity getIndexToAdd. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return idx;
        }
    }

    private String getFirstItemControlCode(String[] cl) {
        String cc = "";
        String item = "";
        String codes = "";
        int index = 0;
        String[] codesArr = null;
        try {
            if (cl != null && cl.length > 0) {
                item = cl[0];
                index = item.indexOf("|");
                if (index >= 0) {
                    codes = item.substring(0, index);
                    codesArr = codes.split("\\.");
                    cc = codesArr[2];
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity getFirstItemControlCode. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return cc;
        }
    }

    private String[] hideAllItemsAfter8192(String[] arr, int idxOf8192) {
        try {
            if (arr != null && arr.length > 0) {
                List<String> list = Arrays.asList(arr);
                List<String> newList = new ArrayList<>(list);
                for (int i = newList.size() - 1; i > idxOf8192; i--) {
                    newList.remove(i);
                }
                if (newList != null && newList.size() > 0) {
                    arr = newList.toArray(new String[newList.size()]);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity hideAllItemsAfter8192. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return arr;
        }
    }

    private void loadlistGroup(String checklistObjForMassOKPJsonStr) {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        SharedPreferences glsp = objGlobalMethods.getGrouplistSP();
        final SharedPreferences gcmp = GcmPreferences;
        Gson g = new Gson();

        String jobSelectionInfoJsonStr = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "jobSelectionInfoJsonStr");
        jobSelectionInfo j1 = !objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr) ? g.fromJson(jobSelectionInfoJsonStr, jobSelectionInfo.class) : null;

        String DIPChecklistStr = "";
        String[] DIPChecklist = null;
        checklist_PostData CurrData;
        checklist_PostData NextData;
        String HierarchyCode_A = "0";
        int HierarchyCode = 0;
        int HierarchyCodeP = -1;
        String ActionCode_A = "";
        String ControlCode = "";
        String ManOptCode = "";
        String DisplayText = "";
        String DisplayTextFinal = "";
        String[] Option = null;
        String[] codesArr = null;
        String codes = "";
        int index = 0;
        int x = 0;
        int optionZHindex = 0;
        int NegativeChildParentIDX = -99;

        String displaytextNoptions = "";
        String options = "";
        String optionENtxt = "";
        String optionZHtxt = "";
        String[] optionsArr = null;
        String[] optionsArrEN = null;
        String[] optionsArrZH = null;
        Boolean show10to19 = false;
        Boolean show20to29 = false;
        Boolean show40to49 = false;
        Boolean show50to59 = false;
        Boolean show60to69 = false;

        Boolean addList = false;
        Boolean has8192 = false;

        Context ctx = myApp.getAppContext();
        String OriginalFirstControlCode = "";
        fixedChecklists fcl = new fixedChecklists();
        objTemp ot = null;
        //String hasIMEI_Verification = "";
        //String hasSerialNum_Verification = "";
        //String hasVoucher_Verification = "";
        boolean hasIMEIVerification = false;
        boolean hasSerialNumVerification = false;
        boolean hasVoucherVerification = false;

        Boolean hasOTP = j1 != null ? j1.HAS_OTP : false;
        String hasVerifiedOTPStr = objGlobalMethods.getGCMPrefs(gcmp, "hasVerifiedOTPStr");
        Boolean hasVerifiedOTP = !objGlobalMethods.IsEmptyString(hasVerifiedOTPStr);
        boolean CustIDVerificationNotRequired = j1 != null ? j1.HAS_CUSTID_VERIFICATION_NOT_REQ : false;
        boolean isLockerJob = j1 != null ? j1.IS_LOCKER : false;
        long StatusCodeIDX = j1 != null ? j1.StatusCodeIDX : 0;
        boolean skiplocker = false;
        Integer FLAG = j1 != null ? j1.FLAG : 0;
        String TypeOfPinVerificationStr = objGlobalMethods.getGCMPrefs(gcmp, "TypeOfPinVerificationStr");
        String hideshow10to19NewForced = objGlobalMethods.getGCMPrefs(gcmp, "hideshow10to19NewForced");
        Integer TypeOfPinVerification = !objGlobalMethods.IsEmptyString(TypeOfPinVerificationStr) ? Integer.parseInt(TypeOfPinVerificationStr) : 0;
        //String currJobInfoIDXStr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
        //long JobsIDX = !objGlobalMethods.IsEmptyString(currJobInfoIDXStr) ? Long.parseLong(currJobInfoIDXStr) : 0;
        long JobsIDX = 0;
        ArrayList<Long> JobsIDXs = j1 != null && j1.SelectedValidJobIDXList != null && j1.SelectedValidJobIDXList.size() > 0 ? j1.SelectedValidJobIDXList : null;

        has8192 = (FLAG & 8192) == 8192; //cannot skip OTP (latest as of 11/07/2025)

        boolean doHiddenAdjustment = false;


        try {
            langType = objGlobalMethods.getGCMPrefs(gcmp, "language");
            //listView = null;
            if (listView == null)
                listView = (ListView) findViewById(R.id.listView);

            checklistObjForMassOKP cl = g.fromJson(checklistObjForMassOKPJsonStr, checklistObjForMassOKP.class);
            DIPChecklist = cl.getChecklist();

            if (al != null && al.size() > 0) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    if (al.get(i).ControlCode.equals("OT")) {
                        al.get(i).setIsChecked(true);
                        if (al.get(i).OptionValue != null) {
                            al.get(i).OptionValue.clear();
                        } else {
                            al.get(i).OptionValue = new ArrayList<String>();
                        }
                        al.get(i).OptionValue.add(TypeOfPinVerification == 3 ? "3" : TypeOfPinVerification == 2 ? "2" : TypeOfPinVerification == 1 ? "1" : "0");
                        if (has8192 && hasOTP && TypeOfPinVerification == 2 && ((i + 1) > al.size() - 1))
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCheckListNext);
                        if (!has8192 && hasOTP && TypeOfPinVerification == 2 && ((i + 1) > al.size() - 1))
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCheckListNext);
                    }
                    if (al.get(i).ControlCode.equals("SO")) {
                        al.get(i).setIsChecked(true);
                        if (al.get(i).OptionValue != null) {
                            al.get(i).OptionValue.clear();
                        } else {
                            al.get(i).OptionValue = new ArrayList<String>();
                        }
                        al.get(i).OptionValue.add("YES");
                    }
                }
            } else if (DIPChecklist != null && DIPChecklist.length > 0) {
                doHiddenAdjustment = true;

                if (isLockerJob) {
                    skiplocker = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(gcmp, "skiplocker")) ? true : false;
                    DIPChecklist = StatusCodeIDX == 104 && !skiplocker ? fcl.getLockerOKPChecklist() :
                            StatusCodeIDX == 104 && skiplocker ? fcl.getLockerOKPreceiverChecklist() :
                                    fcl.getLockerPUChecklist();
                }

                if (hasOTP && isLockerJob && !skiplocker)
                    hasOTP = false;

                if (!hasVerifiedOTP && CustIDVerificationNotRequired)
                    hasVerifiedOTP = true;

                show10to19 = !hasVerifiedOTP ? true : false;
                show20to29 = hasVerifiedOTP ? true : false;
                show40to49 = hasVerifiedOTP ? false : true;
                //show50to59 = (FLAG & 8192) == 8192 ? true : false;
                show50to59 = false;
                show60to69 = !show50to59;
                List<String> CheckListList = null;
                if (CustIDVerificationNotRequired && DIPChecklist.length > 0 && StatusCodeIDX == 104) {
                    CheckListList = new ArrayList<String>(Arrays.asList(DIPChecklist));
                    //CheckListList.add(0, "0.DS.LB.MD|Request for original NRIC, verify name and LAST 4 CHARACTERS of NRIC.[ZH]要求实际收件人身份证并验证名字和身份证号码最后4个字.");
                    String IDVerificationName = objGlobalMethods.getGCMPrefs(gcmp, "IDVerificationName");
                    String name = !objGlobalMethods.IsEmptyString(IDVerificationName) ? " (" + IDVerificationName + ")" : "";
                    String fullchecklist = "0.DS.LB.MD|" + ctx.getString(R.string.idverifynotrequireden) + name + "[ZH]" + ctx.getString(R.string.idverifynotrequiredzh) + name;
                    CheckListList.add(0, fullchecklist);
                    DIPChecklist = CheckListList.toArray(new String[CheckListList.size()]);
                } else if (hasOTP && !CustIDVerificationNotRequired && StatusCodeIDX == 104) {
                    CheckListList = new ArrayList<String>(Arrays.asList(DIPChecklist));
                    String otpChecklistItem = "0.DS.OT.MD|" + ctx.getString(R.string.buttontext_otp_not_verifieden) + "[ZH]" + ctx.getString(R.string.buttontext_otp_not_verifiedzh);

                    int idxToAdd = getIndexToAdd(DIPChecklist);
                    CheckListList.add(idxToAdd >= 0 ? idxToAdd : 0, otpChecklistItem);
                    DIPChecklist = CheckListList.toArray(new String[CheckListList.size()]);

                    if (has8192 && hasOTP && !hasVerifiedOTP)
                        DIPChecklist = hideAllItemsAfter8192(DIPChecklist, idxToAdd);

                    if (has8192 && hasOTP && !hasVerifiedOTP && TypeOfPinVerification == 2)
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCheckListNext);

                            /*OriginalFirstControlCode = getFirstItemControlCode(ji.CheckList);
                            CheckListList.add((OriginalFirstControlCode.equalsIgnoreCase("PH") || OriginalFirstControlCode.equalsIgnoreCase("PR")) ? 1 : 0, otpChecklistItem);
                            ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);*/
                }
                         /*else if (JobsHandler.CustIDVerificationRequired(ji)) {
                            CheckListList = new ArrayList<String>(Arrays.asList(ji.CheckList));
                            String fullchecklist = "10.DS.ID.MD|@NRIC@";
                            CheckListList.add(0, fullchecklist);
                            ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);
                        }*/

                        /*//purposely insert new item to e bottom
                        CheckListList = new ArrayList<String>(Arrays.asList(ji.CheckList));
                        String otpChecklistItem = "0.DS.SO.MD|" + ctx.getString(R.string.rec_signature_en) + "[ZH]" + ctx.getString(R.string.rec_signature_zh);
                        //CheckListList.add(0, otpChecklistItem);
                        OriginalFirstControlCode = getFirstItemControlCode(ji.CheckList);
                        CheckListList.add(otpChecklistItem);
                        ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);
                        //purposely insert new item to e bottom*/


                //ot = temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), JobsIDX);

                if (JobsIDXs != null && JobsIDXs.size() > 0) {
                    for (int jx = 0; jx <= JobsIDXs.size() - 1; jx++) {
                        ot = StatusCodeIDX == 104 ? temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), JobsIDXs.get(jx)) : null;
                        if (ot != null) {
                            JobsIDX = JobsIDXs.get(jx);
                            break;
                        }
                    }
                }

                if (ot != null && !objGlobalMethods.IsEmptyString(ot.TempLastRecorded)) {
                    CheckListList = new ArrayList<String>(Arrays.asList(DIPChecklist));
                    CheckListList = fcl.rtnEditedChecklistIFAddedTempReader(CheckListList);
                    DIPChecklist = CheckListList.toArray(new String[CheckListList.size()]);

                            /*CheckListList = new ArrayList<String>(Arrays.asList(ji.CheckList));
                            String tlitem = ctx.getString(R.string.tl_checklist_item0);
                            CheckListList.add(0, tlitem);
                            String signtlitem = ctx.getString(R.string.tl_checklist_item1);
                            CheckListList.add( signtlitem);
                            ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);*/
                }
                CheckListList = new ArrayList<String>(Arrays.asList(DIPChecklist));
                CheckListList = fcl.rtnEditedChecklistForV2V3V4Existent(CheckListList, hasIMEIVerification, hasSerialNumVerification, hasVoucherVerification);
                DIPChecklist = CheckListList.toArray(new String[CheckListList.size()]);

                for (int i = 0; i <= DIPChecklist.length - 1; i++) {
                    addList = false;
                    CurrData = null;
                    if (al == null) {
                        al = new ArrayList<>();
                    }

                    index = DIPChecklist[i].indexOf("|");
                    //next data portion
                    x = i + 1 <= DIPChecklist.length - 1 ? i + 1 : i + 1 == DIPChecklist.length ? i : -1;

                    //next data portion
                    if (index >= 0) {
                        codes = DIPChecklist[i].substring(0, index);
                        if (codes != "") {
                            codesArr = codes.split("\\.");
                            HierarchyCode_A = codesArr[0];
                            if (!HierarchyCode_A.equals("")) {
                                try {
                                    HierarchyCode = Integer.parseInt(HierarchyCode_A);
                                } catch (Exception e) {
                                    HierarchyCode = 0;
                                    //String currJobInfoIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                                    /*objGlobalMethods.writeToFile("Exception caught at CheckListActivity loadlistNew00 convert HierarchyCode_A to integer for JobsIDX: " + currJobInfoIDXStr + ". Exception: " + e.getMessage().toString(), "", "", "", "");*/
                                    objGlobalMethods.writeToFile("Exception caught at CheckListActivity loadlistGroup convert HierarchyCode_A to integer"
                                            + ". Exception: " + e.getMessage().toString(), "", "", "", "");
                                }
                            }

                            addList = (HierarchyCode < 0)
                                    || (HierarchyCode >= 0 && HierarchyCode <= 9)
                                    || (HierarchyCode >= 10 && HierarchyCode <= 19 && show10to19)
                                    || (HierarchyCode >= -19 && HierarchyCode <= -10 && show10to19)
                                    || (HierarchyCode >= 20 && HierarchyCode <= 29 && show20to29)
                                    || (HierarchyCode >= 40 && HierarchyCode <= 49 && show40to49)
                                    || (HierarchyCode >= 50 && HierarchyCode <= 59 && show50to59)
                                    || (HierarchyCode >= 60 && HierarchyCode <= 69 && show60to69)
                                    || (HierarchyCode >= 70);

                            //check negative values for series 10 (10-19 only show if no verified OTP)
                            if (HierarchyCode >= -19 && HierarchyCode <= -10) {
                                addList = show10to19;
                            }

                            if (addList) {
                                ActionCode_A = codesArr[1];
                                ControlCode = codesArr[2];
                                ManOptCode = codesArr[3];
                                if (ActionCode_A.equals(ActionCode)) {
                                    CurrData = new checklist_PostData(null, 0, null, null, null, null,
                                            null, null, false, null, null, null, FLAG);
                                    CurrData.setFullCode(codes);
                                    CurrData.setActionCode(ActionCode_A);
                                    CurrData.setControlCode(ControlCode);
                                    CurrData.setManOptCode(ManOptCode);
                                    displaytextNoptions = DIPChecklist[i].substring(index + 1, DIPChecklist[i].length());
                                    if (displaytextNoptions != "") {
                                        index = displaytextNoptions.indexOf("|");
                                        if (index >= 0) {
                                            DisplayText = displaytextNoptions.substring(0, index);

                                            int newlineIndex = DisplayText.indexOf("[ZH]");
                                            if (newlineIndex >= 0) {
                                                DisplayTextFinal = DisplayText.substring(0, newlineIndex);
                                            } else {
                                                DisplayTextFinal = DisplayText;
                                            }

                                            //CurrData.setDisplayText(DisplayText);
                                            CurrData.setDisplayText(DisplayTextFinal);
                                            if (langType.equals("zh")) {
                                                if (newlineIndex >= 0) {
                                                    CurrData.setDisplayTextCN(DisplayText.substring(newlineIndex + 4, DisplayText.length()).replace("<rn>", "\n"));
                                                } else {
                                                    CurrData.setDisplayTextCN(objGlobalMethods.ReplaceCheckListDisplayTextLang(DisplayTextFinal).replace("<rn>", "\n"));
                                                }
                                            } else {
                                                CurrData.setDisplayTextCN(DisplayTextFinal.replace("<rn>", "\n"));
                                            }

                                            options = displaytextNoptions.substring(index + 1, displaytextNoptions.length());
                                            if (options != "" && options.length() > 0) {
                                                //new code
                                                optionsArr = options.split("\\|");
                                                optionsArrEN = new String[optionsArr.length];
                                                optionsArrZH = new String[optionsArr.length];
                                                for (int l = 0; l <= optionsArr.length - 1; l++) {
                                                    optionZHindex = optionsArr[l].indexOf("[ZH]");
                                                    if (optionZHindex > 0) {
                                                        optionENtxt = optionsArr[l].substring(0, optionZHindex);
                                                        optionZHtxt = optionsArr[l].substring(optionZHindex + 4, optionsArr[l].length());
                                                    } else {
                                                        optionENtxt = optionsArr[l];
                                                        optionZHtxt = optionsArr[l];
                                                    }
                                                    optionsArrEN[l] = optionENtxt;
                                                    optionsArrZH[l] = langType.equals("zh") ? optionZHtxt : optionENtxt;
                                                }

                                                CurrData.setOption(optionsArrEN);
                                                CurrData.setOptionCN(optionsArrZH);
                                                //new code
                                            }

                                        } else {
                                            DisplayText = displaytextNoptions;

                                            int newlineIndex = DisplayText.indexOf("[ZH]");
                                            if (newlineIndex >= 0) {
                                                DisplayTextFinal = DisplayText.substring(0, newlineIndex);
                                            } else {
                                                DisplayTextFinal = DisplayText;
                                            }

                                            CurrData.setDisplayText(DisplayTextFinal);
                                            if (langType.equals("zh")) {
                                                if (newlineIndex >= 0) {
                                                    CurrData.setDisplayTextCN(DisplayText.substring(newlineIndex + 4, DisplayText.length()).replace("<rn>", "\n"));
                                                } else {
                                                    CurrData.setDisplayTextCN(objGlobalMethods.ReplaceCheckListDisplayTextLang(DisplayTextFinal).replace("<rn>", "\n"));
                                                }
                                            } else {
                                                CurrData.setDisplayTextCN(DisplayTextFinal.replace("<rn>", "\n"));
                                            }
                                        }

                                        //these values are inserted into OptionValue List first as they are shown upon load
                                        if (CurrData.ManOptCode.equals("OP")) {
                                            //type 1 == fa (any 1 is yes) 2 == fy (all must be yes) 3 == fn (all must be no)
                                            if (ControlCode.equals("YN") || ControlCode.equals("AY") || ControlCode.equals("AN") || ControlCode.equals("FY") || ControlCode.equals("FN")) {
                                                CurrData.OptionValue = new ArrayList<>();
                                                CurrData.OptionValue.add("NO");
                                            } else if (ControlCode.equals("DD") || ControlCode.equals("DT")) {
                                            /*data.OptionValue = new ArrayList<>();
                                            data.OptionValue.add(Dateformat.format(new Date()));
                                            data.setSelectedDate(new Date());*/
                                            } else if (ControlCode.equals("CG") || ControlCode.equals("RG")) {
                                            /*if (data.Option != null && data.Option.length > 0) {
                                                data.OptionValue = new ArrayList<>();
                                                for (int q = 0; q <= data.Option.length - 1; q++) {
                                                    data.OptionValue.add("NO");
                                                }
                                            }*/
                                            }
                                            //CurrData.setIsChecked(true);
                                        } else if (CurrData.ManOptCode.equals("MD") && CurrData.ControlCode.equals("CB")) {
                                            //CurrData.IsChecked = true;
                                        }
                                        //these values are inserted into OptionValue List first as they are shown upon load

                                        if (ControlCode.equals("ID")) {
                                            CurrData.setDisplayText((FLAG & 8192) == 8192 ? "" : objGlobalMethods.getGCMPrefs(gcmp, "IDVerificationName"));
                                            CurrData.setDisplayTextCN((FLAG & 8192) == 8192 ? "" : objGlobalMethods.getGCMPrefs(gcmp, "IDVerificationName"));
                                        }

                                        if (ControlCode.equals("PR") && dataTlist != null && dataTlist.size() > 0) {
                                            boolean added = false;
                                            for (int t = 0; t <= dataTlist.size() - 1; t++) {
                                                if (dataTlist.get(t).checklistitemidx == i) {
                                                    CurrData.addDataT(dataTlist.get(t));
                                                    added = true;
                                                }
                                            }
                                            /*if (added) {
                                                CurrData.setIsChecked(true);
                                                if (CurrData.OptionValue != null) {
                                                    CurrData.OptionValue.clear();
                                                    CurrData.OptionValue.add("YES");
                                                } else {
                                                    CurrData.OptionValue = new ArrayList<String>();
                                                    CurrData.OptionValue.add("YES");
                                                }
                                            }*/
                                        }

                                        if (ControlCode.equals("PH") || ControlCode.equals("PR") || ControlCode.equals("SO") || ControlCode.equals("RD")) {
                                            CurrData.setIsChecked(true);
                                            if (CurrData.OptionValue != null) {
                                                CurrData.OptionValue.clear();
                                            } else {
                                                CurrData.OptionValue = new ArrayList<String>();
                                            }
                                            CurrData.OptionValue.add(ControlCode.equals("RD") ? "NO" : "YES");
                                        }

                                        if (ControlCode.equals("OT")) {
                                            CurrData.setIsChecked(true);
                                            if (CurrData.OptionValue != null) {
                                                CurrData.OptionValue.clear();
                                            } else {
                                                CurrData.OptionValue = new ArrayList<String>();
                                            }
                                            CurrData.OptionValue.add(TypeOfPinVerification == 3 ? "3" : TypeOfPinVerification == 2 ? "2" : TypeOfPinVerification == 1 ? "1" : "0");
                                        }

                                        if (ControlCode.equals("ID") || ControlCode.equals("V1") || ControlCode.equals("V2") || ControlCode.equals("V3") || ControlCode.equals("V4") || ControlCode.equals("V5") || ControlCode.equals("RO") || ControlCode.equals("TL")) {
                                            CurrData.setIsChecked(true);
                                        }

                                        if (ControlCode.equals("V5") && j1 != null) {
                                            CurrData.setDelBlock(!objGlobalMethods.IsEmptyString(j1.DelBlock) ? j1.DelBlock : "");
                                            CurrData.setAllDelUnits(j1.AllDelUnits != null ? j1.AllDelUnits : new ArrayList<>());
                                            CurrData.setAllDelUnitsCombined(j1.AllDelUnitsCombinedList != null ? j1.AllDelUnitsCombinedList : new ArrayList<>());
                                        }

                                        if (ControlCode.equals("TL")) {
                                            if (ot != null)
                                                ot = temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), JobsIDX);

                                            if (CurrData.OptionValue != null) {
                                                CurrData.OptionValue.clear();
                                            } else {
                                                CurrData.OptionValue = new ArrayList<String>();
                                            }
                                            //manual record: 0, auto record: 1
                                            //TempLastRecorded = "";
                                            //CurrData.OptionValue.add(objGlobalMethods.IsEmptyString(TempLastRecorded) ? "0" : "1");
                                            CurrData.OptionValue.add(ot.ManualAuto);

                                            if (CurrData.OptionValue2 != null) {
                                                CurrData.OptionValue2.clear();
                                            } else {
                                                CurrData.OptionValue2 = new ArrayList<String>();
                                            }

                                            CurrData.OptionValue2.add(ot.TempLastRecorded);
                                            CurrData.OptionValue2.add(ot.TempLastRecordedTimeStampTSstr);
                                            if (ot.item != null) {
                                                CurrData.addDataT(ot.item);
                                            }

                                        }

                                        CurrData.FinalManOptCode = ((HierarchyCode % 10) != 0) && (HierarchyCode > 0) ? "OP" : ManOptCode;
                                        CurrData.FinalManOptCode = ManOptCode;
                                        CurrData.setHierarchyCode(HierarchyCode);

                                        if (x >= 0) {
                                            CurrData.setIsParent(IsParentOfNextDataNew(HierarchyCode, x, DIPChecklist, show10to19, show20to29, CurrData.getIsParent(), gcmp) ? true : false);
                                        }

                                        CurrData.setIsChild(HierarchyCode % 10 == 0 ? false : true);

                                        if (CurrData.getIsChild()) {
                                            if (ControlCode.equals("YN")) {
                                                CurrData.setIsChecked(true);
                                                if (CurrData.OptionValue != null) {
                                                    CurrData.OptionValue.clear();
                                                } else {
                                                    CurrData.OptionValue = new ArrayList<String>();
                                                }
                                                CurrData.OptionValue.add("NO");
                                            } else if (ControlCode.equals("RG")) {
                                                CurrData.setIsChecked(true);
                                            }

                                            if (HierarchyCode < 0) {
                                                NegativeChildParentIDX = getNegativeChildParentIDXviaALNew(i, HierarchyCode, gcmp, CurrData.DisplayText);
                                                if (NegativeChildParentIDX != -99) {
                                                    CurrData.setParentIDX(NegativeChildParentIDX);
                                                }
                                            } else {
                                                HierarchyCodeP = HierarchyCode;
                                                for (int p = al.size() - 1 == -1 ? 0 : al.size() - 1; p >= 0; p--) {
                                                    try {
                                                        //HierarchyCodeP = Integer.parseInt(ji.CheckList[p].substring(0, ji.CheckList[p].indexOf(".")));
                                                        HierarchyCodeP = al.get(p).getHierarchyCode();
                                                    } catch (Exception e) {
                                                        String stack = JobsHandler.getStackTraceString(e);
                                                    }
                                                    /*//original code
                                                    if (HierarchyCodeP >= 0 && HierarchyCodeP < HierarchyCode) {
                                                        CurrData.setParentIDX(p);
                                                        break;
                                                    }
                                                    //original code*/

                                                    //new code
                                                    if ((HierarchyCodeP < 0) && ((HierarchyCode - HierarchyCode - HierarchyCode) == HierarchyCodeP)) {
                                                        CurrData.setParentIDX(p);
                                                        break;
                                                    } else if (HierarchyCodeP >= 0 && HierarchyCodeP < HierarchyCode) {
                                                        CurrData.setParentIDX(p);
                                                        break;
                                                    }
                                                    //new code
                                                }
                                            }
                                        }

                                        CurrData.setIndex(al.size());
                                        al.add(CurrData);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            } else {
                String msg1 = (al == null) ? "al is null" : (al != null && al.size() == 0) ? "al is empty" : "";
                String msg2 = (DIPChecklist == null || (DIPChecklist != null && DIPChecklist.length == 0)) ? "DIPChecklist is null or empty" : "";
                String msg3 = (!objGlobalMethods.IsEmptyString(msg1) ? msg1 + ", " : "") +
                        (!objGlobalMethods.IsEmptyString(msg2) ? msg2 + ", " : "") + " at ChecklistActivity loadlistGroup.";
                objGlobalMethods.writeToFile(msg3, "", "", "", "");
            }

            CheckListActivity.PhotoImagePathList = null;
            CheckListActivity.dataTlist = null;

            //al = null;
            if (al != null && al.size() > 0) {
                objGlobalMethods.writeToFile("Checklist viewed for JobsIDX: " + String.valueOf(JobsIDX), "", "", "", "");
                adapter = new checklist_PostItemAdapter(myApp.getAppContext(), al);

                if (doHiddenAdjustment)
                    adapter.adjustHiddenFirst();

                if (!objGlobalMethods.IsEmptyString(hideshow10to19NewForced) && hideshow10to19NewForced.equals("h")) {
                    adapter.hide10to19New();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "hideshow10to19NewForced", "");
                } else if (!objGlobalMethods.IsEmptyString(hideshow10to19NewForced) && hideshow10to19NewForced.equals("s")) {
                    adapter.show10to19New();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "hideshow10to19NewForced", "");
                }

                listView.setAdapter(adapter);
                String pr_latemsg0_reqFuturePrompt = objGlobalMethods.getGCMPrefs(gcmp, "pr_latemsg0_reqFuturePrompt");

                if ((isLockerJob && StatusCodeIDX == 103) || (StatusCodeIDX == 103)) {
                    btnCheckListNext.setText(ctx.getString(R.string.buttontext_PU));
                }

                if (!objGlobalMethods.IsEmptyString(pr_latemsg0_reqFuturePrompt)) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "pr_latemsg0_reqFuturePrompt", "");
                    objGlobalMethods.showPR_lateprompt();
                }
            } else if (al != null && al.size() == 0 && hasVerifiedOTP) {
                objGlobalMethods.writeToFile("Checklist skipped due hasVerifiedOTP for JobsIDX: " + String.valueOf(JobsIDX), "", "", "", "");
                startActivity(nextActivtyIntent);
            } else {
                objGlobalMethods.writeToFile("al is null or empty at ChecklistActivity loadlistGroup.", "", "", "", "");
                back();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at CheckListActivity loadlistGroup. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            showErrorAlert();
        }
    }

    private void loadlistNew00() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        final SharedPreferences gcmp = GcmPreferences;
        Gson g = new Gson();
        //String DIPChecklistStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "DIPChecklist");
        String DIPChecklistStr = objGlobalMethods.getChecklistFromChecklistSP();
        //String ChecklistVariablesJsonStr = objGlobalMethods.getChecklistRelatedSP_KeyValue("ChecklistVariablesJsonStr");
        //ChecklistVariables cv = !objGlobalMethods.IsEmptyString(ChecklistVariablesJsonStr) ? g.fromJson(ChecklistVariablesJsonStr, ChecklistVariables.class) : new ChecklistVariables();
        String[] DIPChecklist = null;
        checklist_PostData CurrData;
        checklist_PostData NextData;
        String HierarchyCode_A = "0";
        int HierarchyCode = 0;
        int HierarchyCodeP = -1;
        String ActionCode_A = "";
        String ControlCode = "";
        String ManOptCode = "";
        String DisplayText = "";
        String DisplayTextFinal = "";
        String[] Option = null;
        String[] codesArr = null;
        String codes = "";
        int index = 0;
        int x = 0;
        int optionZHindex = 0;
        int NegativeChildParentIDX = -99;

        String displaytextNoptions = "";
        String options = "";
        String optionENtxt = "";
        String optionZHtxt = "";
        String[] optionsArr = null;
        String[] optionsArrEN = null;
        String[] optionsArrZH = null;
        ArrayList<String> asd = new ArrayList<>();
        Boolean isBusiness = false;
        Boolean show10to19 = false;
        Boolean showNeg10to19 = false;
        Boolean show20to29 = false;
        Boolean showNeg20to29 = false;
        Boolean show40to49 = false;
        Boolean showNeg40to49 = false;
        Boolean show50to59 = false;
        Boolean showNeg50to59 = false;
        Boolean show60to69 = false;
        Boolean showNeg60to69 = false;

        Boolean addList = false;
        Boolean isParent = false;
        Boolean has8192 = false;

        Context ctx = myApp.getAppContext();
        String OriginalFirstControlCode = "";
        fixedChecklists fcl = new fixedChecklists();
        objTemp ot = null;
        String hasIMEI_Verification = "";
        String hasSerialNum_Verification = "";
        String hasVoucher_Verification = "";
        boolean hasIMEIVerification = false;
        boolean hasSerialNumVerification = false;
        boolean hasVoucherVerification = false;

        String hasOtpStr = objGlobalMethods.getGCMPrefs(gcmp, "hasOtpStr");
        Boolean hasOTP = !objGlobalMethods.IsEmptyString(hasOtpStr);
        String hasVerifiedOTPStr = objGlobalMethods.getGCMPrefs(gcmp, "hasVerifiedOTPStr");
        Boolean hasVerifiedOTP = !objGlobalMethods.IsEmptyString(hasVerifiedOTPStr);
        String CustIDVerificationNotRequiredStr = objGlobalMethods.getGCMPrefs(gcmp, "CustIDVerificationNotRequiredStr");
        boolean CustIDVerificationNotRequired = !objGlobalMethods.IsEmptyString(CustIDVerificationNotRequiredStr);
        String isLockerJobStr = objGlobalMethods.getGCMPrefs(gcmp, "isLockerJobStr");
        boolean isLockerJob = !objGlobalMethods.IsEmptyString(isLockerJobStr);
        String CurrJobInfoStatusCodeIDX = objGlobalMethods.getGCMPrefs(gcmp, "CurrJobInfoStatusCodeIDX");
        long StatusCodeIDX = !objGlobalMethods.IsEmptyString(CurrJobInfoStatusCodeIDX) ? Long.parseLong(CurrJobInfoStatusCodeIDX) : 0;
        boolean skiplocker = false;
        String currJobInfoFlagStr = objGlobalMethods.getGCMPrefs(gcmp, "currJobInfoFlag");
        Integer FLAG = !objGlobalMethods.IsEmptyString(currJobInfoFlagStr) ? Integer.parseInt(currJobInfoFlagStr) : 0;
        String TypeOfPinVerificationStr = objGlobalMethods.getGCMPrefs(gcmp, "TypeOfPinVerificationStr");
        String hideshow10to19NewForced = objGlobalMethods.getGCMPrefs(gcmp, "hideshow10to19NewForced");
        Integer TypeOfPinVerification = !objGlobalMethods.IsEmptyString(TypeOfPinVerificationStr) ? Integer.parseInt(TypeOfPinVerificationStr) : 0;
        String currJobInfoIDXStr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
        long JobsIDX = !objGlobalMethods.IsEmptyString(currJobInfoIDXStr) ? Long.parseLong(currJobInfoIDXStr) : 0;

        has8192 = (FLAG & 8192) == 8192; //cannot skip OTP (latest as of 11/07/2025)

        /*Boolean hasOTP = cv.getHasOTP();
        Boolean hasVerifiedOTP = cv.getHasVerifiedOTP();
        boolean CustIDVerificationNotRequired = cv.getCustIDVerificationNotRequired();
        boolean isLockerJob = cv.getisLockerJob();
        String CurrJobInfoStatusCodeIDX = cv.getCurrJobInfoStatusCodeIDX();
        long StatusCodeIDX = !objGlobalMethods.IsEmptyString(CurrJobInfoStatusCodeIDX) ? Long.parseLong(CurrJobInfoStatusCodeIDX) : 0;
        boolean skiplocker = false;
        Integer FLAG = cv.getFLAG();
        Integer TypeOfPinVerification = cv.getTypeOfPinVerification();
        long JobsIDX = cv.getJobsIDX();
        String hideshow10to19NewForced = objGlobalMethods.getGCMPrefs(GcmPreferences, "hideshow10to19NewForced");*/

        boolean doHiddenAdjustment = false;

        try {
            langType = objGlobalMethods.getGCMPrefs(gcmp, "language");
            //listView = null;
            if (listView == null)
                listView = (ListView) findViewById(R.id.listView);

            if (al != null && al.size() > 0) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    if (al.get(i).ControlCode.equals("OT")) {
                        al.get(i).setIsChecked(true);
                        if (al.get(i).OptionValue != null) {
                            al.get(i).OptionValue.clear();
                        } else {
                            al.get(i).OptionValue = new ArrayList<String>();
                        }
                        al.get(i).OptionValue.add(TypeOfPinVerification == 3 ? "3" : TypeOfPinVerification == 2 ? "2" : TypeOfPinVerification == 1 ? "1" : "0");
                        if (has8192 && hasOTP && TypeOfPinVerification == 2 && ((i + 1) > al.size() - 1))
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCheckListNext);
                        if (!has8192 && hasOTP && TypeOfPinVerification == 2 && ((i + 1) > al.size() - 1))
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCheckListNext);
                    }
                    if (al.get(i).ControlCode.equals("SO")) {
                        al.get(i).setIsChecked(true);
                        if (al.get(i).OptionValue != null) {
                            al.get(i).OptionValue.clear();
                        } else {
                            al.get(i).OptionValue = new ArrayList<String>();
                        }
                        al.get(i).OptionValue.add("YES");
                    }
                }
            } else if (!objGlobalMethods.IsEmptyString(DIPChecklistStr)) {

                DIPChecklist = g.fromJson(DIPChecklistStr, String[].class);

                if (DIPChecklist != null && DIPChecklist.length > 0) {
                    doHiddenAdjustment = true;
                    hasIMEI_Verification = objGlobalMethods.getGCMPrefs(gcmp, "hasIMEI_Verification");
                    hasSerialNum_Verification = objGlobalMethods.getGCMPrefs(gcmp, "hasSerialNum_Verification");
                    hasVoucher_Verification = objGlobalMethods.getGCMPrefs(gcmp, "hasVoucher_Verification");
                    hasIMEIVerification = !objGlobalMethods.IsEmptyString(hasIMEI_Verification);
                    hasSerialNumVerification = !objGlobalMethods.IsEmptyString(hasSerialNum_Verification);
                    hasVoucherVerification = !objGlobalMethods.IsEmptyString(hasVoucher_Verification);

                    /*hasIMEIVerification = cv.gethasIMEI_Verification();
                    hasSerialNumVerification = cv.gethasSerialNum_Verification();
                    hasVoucherVerification = cv.gethasVoucher_Verification();*/

                    if (isLockerJob) {
                        skiplocker = !objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(gcmp, "skiplocker")) ? true : false;
                        DIPChecklist = StatusCodeIDX == 104 && !skiplocker ? fcl.getLockerOKPChecklist() :
                                StatusCodeIDX == 104 && skiplocker ? fcl.getLockerOKPreceiverChecklist() :
                                        fcl.getLockerPUChecklist();
                    }

                        /*int catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                        boolean nophototaking = JobsHandler.CannotTakePhoto(ji, catType);
                        if (nophototaking) {
                            ji.CheckList = JobsHandler2.returnNoPhotoTakingCheckList(ji.CheckList);
                        }*/

                    if (hasOTP && isLockerJob && !skiplocker) {
                        hasOTP = false;
                    }

                    if (!hasVerifiedOTP && CustIDVerificationNotRequired) {
                        hasVerifiedOTP = true;
                    }
                    show10to19 = !hasVerifiedOTP ? true : false;
                    show20to29 = hasVerifiedOTP ? true : false;
                    show40to49 = hasVerifiedOTP ? false : true;
                    //show50to59 = (FLAG & 8192) == 8192 ? true : false;
                    show50to59 = false;
                    show60to69 = !show50to59;
                    List<String> CheckListList = null;
                    if (CustIDVerificationNotRequired && DIPChecklist.length > 0 && StatusCodeIDX == 104) {
                        CheckListList = new ArrayList<String>(Arrays.asList(DIPChecklist));
                        //CheckListList.add(0, "0.DS.LB.MD|Request for original NRIC, verify name and LAST 4 CHARACTERS of NRIC.[ZH]要求实际收件人身份证并验证名字和身份证号码最后4个字.");
                        String IDVerificationName = objGlobalMethods.getGCMPrefs(gcmp, "IDVerificationName");
                        String name = !objGlobalMethods.IsEmptyString(IDVerificationName) ? " (" + IDVerificationName + ")" : "";
                        String fullchecklist = "0.DS.LB.MD|" + ctx.getString(R.string.idverifynotrequireden) + name + "[ZH]" + ctx.getString(R.string.idverifynotrequiredzh) + name;
                        CheckListList.add(0, fullchecklist);
                        DIPChecklist = CheckListList.toArray(new String[CheckListList.size()]);
                    } else if (hasOTP && !CustIDVerificationNotRequired && StatusCodeIDX == 104) {
                        CheckListList = new ArrayList<String>(Arrays.asList(DIPChecklist));
                        String otpChecklistItem = "0.DS.OT.MD|" + ctx.getString(R.string.buttontext_otp_not_verifieden) + "[ZH]" + ctx.getString(R.string.buttontext_otp_not_verifiedzh);

                        int idxToAdd = getIndexToAdd(DIPChecklist);
                        CheckListList.add(idxToAdd >= 0 ? idxToAdd : 0, otpChecklistItem);
                        DIPChecklist = CheckListList.toArray(new String[CheckListList.size()]);

                        if (has8192 && hasOTP && !hasVerifiedOTP)
                            DIPChecklist = hideAllItemsAfter8192(DIPChecklist, idxToAdd);

                        if (has8192 && hasOTP && !hasVerifiedOTP && TypeOfPinVerification == 2)
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnCheckListNext);

                            /*OriginalFirstControlCode = getFirstItemControlCode(ji.CheckList);
                            CheckListList.add((OriginalFirstControlCode.equalsIgnoreCase("PH") || OriginalFirstControlCode.equalsIgnoreCase("PR")) ? 1 : 0, otpChecklistItem);
                            ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);*/
                    }
                         /*else if (JobsHandler.CustIDVerificationRequired(ji)) {
                            CheckListList = new ArrayList<String>(Arrays.asList(ji.CheckList));
                            String fullchecklist = "10.DS.ID.MD|@NRIC@";
                            CheckListList.add(0, fullchecklist);
                            ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);
                        }*/

                        /*//purposely insert new item to e bottom
                        CheckListList = new ArrayList<String>(Arrays.asList(ji.CheckList));
                        String otpChecklistItem = "0.DS.SO.MD|" + ctx.getString(R.string.rec_signature_en) + "[ZH]" + ctx.getString(R.string.rec_signature_zh);
                        //CheckListList.add(0, otpChecklistItem);
                        OriginalFirstControlCode = getFirstItemControlCode(ji.CheckList);
                        CheckListList.add(otpChecklistItem);
                        ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);
                        //purposely insert new item to e bottom*/


                    //ot = temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), JobsIDX);
                    ot = StatusCodeIDX == 104 ? temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), JobsIDX) : null;
                    if (ot != null && !objGlobalMethods.IsEmptyString(ot.TempLastRecorded)) {
                        CheckListList = new ArrayList<String>(Arrays.asList(DIPChecklist));
                        CheckListList = fcl.rtnEditedChecklistIFAddedTempReader(CheckListList);
                        DIPChecklist = CheckListList.toArray(new String[CheckListList.size()]);

                            /*CheckListList = new ArrayList<String>(Arrays.asList(ji.CheckList));
                            String tlitem = ctx.getString(R.string.tl_checklist_item0);
                            CheckListList.add(0, tlitem);
                            String signtlitem = ctx.getString(R.string.tl_checklist_item1);
                            CheckListList.add( signtlitem);
                            ji.CheckList = CheckListList.toArray(new String[CheckListList.size()]);*/
                    }
                    CheckListList = new ArrayList<String>(Arrays.asList(DIPChecklist));
                    CheckListList = fcl.rtnEditedChecklistForV2V3V4Existent(CheckListList, hasIMEIVerification, hasSerialNumVerification, hasVoucherVerification);
                    DIPChecklist = CheckListList.toArray(new String[CheckListList.size()]);

                    for (int i = 0; i <= DIPChecklist.length - 1; i++) {
                        addList = false;
                        CurrData = null;
                        if (al == null) {
                            al = new ArrayList<>();
                        }

                        index = DIPChecklist[i].indexOf("|");
                        //next data portion
                        x = i + 1 <= DIPChecklist.length - 1 ? i + 1 : i + 1 == DIPChecklist.length ? i : -1;

                        //next data portion
                        if (index >= 0) {
                            codes = DIPChecklist[i].substring(0, index);
                            if (codes != "") {
                                codesArr = codes.split("\\.");
                                HierarchyCode_A = codesArr[0];
                                if (!HierarchyCode_A.equals("")) {
                                    try {
                                        HierarchyCode = Integer.parseInt(HierarchyCode_A);
                                    } catch (Exception e) {
                                        HierarchyCode = 0;
                                        //String currJobInfoIDX = objGlobalMethods.getGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX);
                                        /*objGlobalMethods.writeToFile("Exception caught at CheckListActivity loadlistNew00 convert HierarchyCode_A to integer for JobsIDX: " + currJobInfoIDXStr + ". Exception: " + e.getMessage().toString(), "", "", "", "");*/
                                        objGlobalMethods.writeToFile("Exception caught at CheckListActivity loadlistNew00 convert HierarchyCode_A to integer for JobsIDX: " + String.valueOf(JobsIDX) + ". Exception: " + e.getMessage().toString(), "", "", "", "");
                                    }
                                }

                                addList = (HierarchyCode < 0)
                                        || (HierarchyCode >= 0 && HierarchyCode <= 9)
                                        || (HierarchyCode >= 10 && HierarchyCode <= 19 && show10to19)
                                        || (HierarchyCode >= -19 && HierarchyCode <= -10 && show10to19)
                                        || (HierarchyCode >= 20 && HierarchyCode <= 29 && show20to29)
                                        || (HierarchyCode >= 40 && HierarchyCode <= 49 && show40to49)
                                        || (HierarchyCode >= 50 && HierarchyCode <= 59 && show50to59)
                                        || (HierarchyCode >= 60 && HierarchyCode <= 69 && show60to69)
                                        || (HierarchyCode >= 70);

                                //check negative values for series 10 (10-19 only show if no verified OTP)
                                if (HierarchyCode >= -19 && HierarchyCode <= -10) {
                                    addList = show10to19;
                                }

                                if (addList) {
                                    ActionCode_A = codesArr[1];
                                    ControlCode = codesArr[2];
                                    ManOptCode = codesArr[3];
                                    if (ActionCode_A.equals(ActionCode)) {
                                        CurrData = new checklist_PostData(null, 0, null, null, null, null,
                                                null, null, false, null, null, null, FLAG);
                                        CurrData.setFullCode(codes);
                                        CurrData.setActionCode(ActionCode_A);
                                        CurrData.setControlCode(ControlCode);
                                        CurrData.setManOptCode(ManOptCode);
                                        displaytextNoptions = DIPChecklist[i].substring(index + 1, DIPChecklist[i].length());
                                        if (displaytextNoptions != "") {
                                            index = displaytextNoptions.indexOf("|");
                                            if (index >= 0) {
                                                DisplayText = displaytextNoptions.substring(0, index);

                                                int newlineIndex = DisplayText.indexOf("[ZH]");
                                                if (newlineIndex >= 0) {
                                                    DisplayTextFinal = DisplayText.substring(0, newlineIndex);
                                                } else {
                                                    DisplayTextFinal = DisplayText;
                                                }

                                                //CurrData.setDisplayText(DisplayText);
                                                CurrData.setDisplayText(DisplayTextFinal);
                                                if (langType.equals("zh")) {
                                                    if (newlineIndex >= 0) {
                                                        CurrData.setDisplayTextCN(DisplayText.substring(newlineIndex + 4, DisplayText.length()).replace("<rn>", "\n"));
                                                    } else {
                                                        CurrData.setDisplayTextCN(objGlobalMethods.ReplaceCheckListDisplayTextLang(DisplayTextFinal).replace("<rn>", "\n"));
                                                    }
                                                } else {
                                                    CurrData.setDisplayTextCN(DisplayTextFinal.replace("<rn>", "\n"));
                                                }

                                                options = displaytextNoptions.substring(index + 1, displaytextNoptions.length());
                                                if (options != "" && options.length() > 0) {
                                                    //new code
                                                    optionsArr = options.split("\\|");
                                                    optionsArrEN = new String[optionsArr.length];
                                                    optionsArrZH = new String[optionsArr.length];
                                                    for (int l = 0; l <= optionsArr.length - 1; l++) {
                                                        optionZHindex = optionsArr[l].indexOf("[ZH]");
                                                        if (optionZHindex > 0) {
                                                            optionENtxt = optionsArr[l].substring(0, optionZHindex);
                                                            optionZHtxt = optionsArr[l].substring(optionZHindex + 4, optionsArr[l].length());
                                                        } else {
                                                            optionENtxt = optionsArr[l];
                                                            optionZHtxt = optionsArr[l];
                                                        }
                                                        optionsArrEN[l] = optionENtxt;
                                                        optionsArrZH[l] = langType.equals("zh") ? optionZHtxt : optionENtxt;
                                                    }

                                                    CurrData.setOption(optionsArrEN);
                                                    CurrData.setOptionCN(optionsArrZH);
                                                    //new code
                                                }

                                            } else {
                                                DisplayText = displaytextNoptions;

                                                int newlineIndex = DisplayText.indexOf("[ZH]");
                                                if (newlineIndex >= 0) {
                                                    DisplayTextFinal = DisplayText.substring(0, newlineIndex);
                                                } else {
                                                    DisplayTextFinal = DisplayText;
                                                }

                                                CurrData.setDisplayText(DisplayTextFinal);
                                                if (langType.equals("zh")) {
                                                    if (newlineIndex >= 0) {
                                                        CurrData.setDisplayTextCN(DisplayText.substring(newlineIndex + 4, DisplayText.length()).replace("<rn>", "\n"));
                                                    } else {
                                                        CurrData.setDisplayTextCN(objGlobalMethods.ReplaceCheckListDisplayTextLang(DisplayTextFinal).replace("<rn>", "\n"));
                                                    }
                                                } else {
                                                    CurrData.setDisplayTextCN(DisplayTextFinal.replace("<rn>", "\n"));
                                                }
                                            }

                                            //these values are inserted into OptionValue List first as they are shown upon load
                                            if (CurrData.ManOptCode.equals("OP")) {
                                                //type 1 == fa (any 1 is yes) 2 == fy (all must be yes) 3 == fn (all must be no)
                                                if (ControlCode.equals("YN") || ControlCode.equals("AY") || ControlCode.equals("AN") || ControlCode.equals("FY") || ControlCode.equals("FN")) {
                                                    CurrData.OptionValue = new ArrayList<>();
                                                    CurrData.OptionValue.add("NO");
                                                } else if (ControlCode.equals("DD") || ControlCode.equals("DT")) {
                                            /*data.OptionValue = new ArrayList<>();
                                            data.OptionValue.add(Dateformat.format(new Date()));
                                            data.setSelectedDate(new Date());*/
                                                } else if (ControlCode.equals("CG") || ControlCode.equals("RG")) {
                                            /*if (data.Option != null && data.Option.length > 0) {
                                                data.OptionValue = new ArrayList<>();
                                                for (int q = 0; q <= data.Option.length - 1; q++) {
                                                    data.OptionValue.add("NO");
                                                }
                                            }*/
                                                }
                                                //CurrData.setIsChecked(true);
                                            } else if (CurrData.ManOptCode.equals("MD") && CurrData.ControlCode.equals("CB")) {
                                                //CurrData.IsChecked = true;
                                            }
                                            //these values are inserted into OptionValue List first as they are shown upon load

                                            if (ControlCode.equals("ID")) {
                                                CurrData.setDisplayText((FLAG & 8192) == 8192 ? "" : objGlobalMethods.getGCMPrefs(gcmp, "IDVerificationName"));
                                                CurrData.setDisplayTextCN((FLAG & 8192) == 8192 ? "" : objGlobalMethods.getGCMPrefs(gcmp, "IDVerificationName"));
                                            }

                                            if (ControlCode.equals("PR") && dataTlist != null && dataTlist.size() > 0) {
                                                boolean added = false;
                                                for (int t = 0; t <= dataTlist.size() - 1; t++) {
                                                    if (dataTlist.get(t).checklistitemidx == i) {
                                                        CurrData.addDataT(dataTlist.get(t));
                                                        added = true;
                                                    }
                                                }
                                            /*if (added) {
                                                CurrData.setIsChecked(true);
                                                if (CurrData.OptionValue != null) {
                                                    CurrData.OptionValue.clear();
                                                    CurrData.OptionValue.add("YES");
                                                } else {
                                                    CurrData.OptionValue = new ArrayList<String>();
                                                    CurrData.OptionValue.add("YES");
                                                }
                                            }*/
                                            }

                                            if (ControlCode.equals("PH") || ControlCode.equals("PR") || ControlCode.equals("SO") || ControlCode.equals("RD")) {
                                                CurrData.setIsChecked(true);
                                                if (CurrData.OptionValue != null) {
                                                    CurrData.OptionValue.clear();
                                                } else {
                                                    CurrData.OptionValue = new ArrayList<String>();
                                                }
                                                CurrData.OptionValue.add(ControlCode.equals("RD") ? "NO" : "YES");
                                            }

                                            if (ControlCode.equals("OT")) {
                                                CurrData.setIsChecked(true);
                                                if (CurrData.OptionValue != null) {
                                                    CurrData.OptionValue.clear();
                                                } else {
                                                    CurrData.OptionValue = new ArrayList<String>();
                                                }
                                                CurrData.OptionValue.add(TypeOfPinVerification == 3 ? "3" : TypeOfPinVerification == 2 ? "2" : TypeOfPinVerification == 1 ? "1" : "0");
                                            }

                                            if (ControlCode.equals("ID") || ControlCode.equals("V1") || ControlCode.equals("V2") || ControlCode.equals("V3") || ControlCode.equals("V4") || ControlCode.equals("V5") || ControlCode.equals("RO") || ControlCode.equals("TL")) {
                                                CurrData.setIsChecked(true);
                                            }

                                            if (ControlCode.equals("V5")) {
                                                SharedPreferences JobInfoSP = objGlobalMethods.getJobInfoSP();
                                                String DelBlockUnitJsonStr = objGlobalMethods.getJobInfoSP_KeyValue(JobInfoSP, "DelBlockUnit");
                                                objDelBlockUnits dbu = null;
                                                if (!objGlobalMethods.IsEmptyString(DelBlockUnitJsonStr))
                                                    dbu = g.fromJson(DelBlockUnitJsonStr, objDelBlockUnits.class);

                                                if (dbu != null) {
                                                    CurrData.setDelBlock(dbu.DelBlock);
                                                    CurrData.setAllDelUnits(dbu.DelUnits);
                                                    CurrData.setAllDelUnitsCombined(dbu.DelUnitsCombined);
                                                }
                                            }

                                            if (ControlCode.equals("TL")) {
                                                if (ot != null)
                                                    ot = temploggerclass.getLatestRecordedTempObjNew2(objGlobalMethods.getGcmPreferences(), JobsIDX);

                                                if (CurrData.OptionValue != null) {
                                                    CurrData.OptionValue.clear();
                                                } else {
                                                    CurrData.OptionValue = new ArrayList<String>();
                                                }
                                                //manual record: 0, auto record: 1
                                                //TempLastRecorded = "";
                                                //CurrData.OptionValue.add(objGlobalMethods.IsEmptyString(TempLastRecorded) ? "0" : "1");
                                                CurrData.OptionValue.add(ot.ManualAuto);

                                                if (CurrData.OptionValue2 != null) {
                                                    CurrData.OptionValue2.clear();
                                                } else {
                                                    CurrData.OptionValue2 = new ArrayList<String>();
                                                }

                                                CurrData.OptionValue2.add(ot.TempLastRecorded);
                                                CurrData.OptionValue2.add(ot.TempLastRecordedTimeStampTSstr);
                                                if (ot.item != null) {
                                                    CurrData.addDataT(ot.item);
                                                }

                                            }

                                            CurrData.FinalManOptCode = ((HierarchyCode % 10) != 0) && (HierarchyCode > 0) ? "OP" : ManOptCode;
                                            CurrData.FinalManOptCode = ManOptCode;
                                            CurrData.setHierarchyCode(HierarchyCode);

                                            if (x >= 0) {
                                                CurrData.setIsParent(IsParentOfNextDataNew(HierarchyCode, x, DIPChecklist, show10to19, show20to29, CurrData.getIsParent(), gcmp) ? true : false);
                                            }

                                            CurrData.setIsChild(HierarchyCode % 10 == 0 ? false : true);

                                            if (CurrData.getIsChild()) {
                                                if (ControlCode.equals("YN")) {
                                                    CurrData.setIsChecked(true);
                                                    if (CurrData.OptionValue != null) {
                                                        CurrData.OptionValue.clear();
                                                    } else {
                                                        CurrData.OptionValue = new ArrayList<String>();
                                                    }
                                                    CurrData.OptionValue.add("NO");
                                                } else if (ControlCode.equals("RG")) {
                                                    CurrData.setIsChecked(true);
                                                }

                                                if (HierarchyCode < 0) {
                                                    NegativeChildParentIDX = getNegativeChildParentIDXviaALNew(i, HierarchyCode, gcmp, CurrData.DisplayText);
                                                    if (NegativeChildParentIDX != -99) {
                                                        CurrData.setParentIDX(NegativeChildParentIDX);
                                                    }
                                                } else {
                                                    HierarchyCodeP = HierarchyCode;
                                                    for (int p = al.size() - 1 == -1 ? 0 : al.size() - 1; p >= 0; p--) {
                                                        try {
                                                            //HierarchyCodeP = Integer.parseInt(ji.CheckList[p].substring(0, ji.CheckList[p].indexOf(".")));
                                                            HierarchyCodeP = al.get(p).getHierarchyCode();
                                                        } catch (Exception e) {
                                                            String stack = JobsHandler.getStackTraceString(e);
                                                        }
                                                    /*//original code
                                                    if (HierarchyCodeP >= 0 && HierarchyCodeP < HierarchyCode) {
                                                        CurrData.setParentIDX(p);
                                                        break;
                                                    }
                                                    //original code*/

                                                        //new code
                                                        if ((HierarchyCodeP < 0) && ((HierarchyCode - HierarchyCode - HierarchyCode) == HierarchyCodeP)) {
                                                            CurrData.setParentIDX(p);
                                                            break;
                                                        } else if (HierarchyCodeP >= 0 && HierarchyCodeP < HierarchyCode) {
                                                            CurrData.setParentIDX(p);
                                                            break;
                                                        }
                                                        //new code
                                                    }
                                                }
                                            }

                                            CurrData.setIndex(al.size());
                                            al.add(CurrData);
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                }
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                } else {
                    objGlobalMethods.writeToFile("DIPChecklist is null or empty at ChecklistActivity loadlistNew00.", "", "", "", "");
                }
            } else {
                String msg1 = (al == null) ? "al is null" : (al != null && al.size() == 0) ? "al is empty" : "";
                String msg2 = (objGlobalMethods.IsEmptyString(DIPChecklistStr)) ? "empryDIPChecklistStr is null or empty" : "";
                String msg3 = (!objGlobalMethods.IsEmptyString(msg1) ? msg1 + ", " : "") +
                        (!objGlobalMethods.IsEmptyString(msg2) ? msg2 + ", " : "") + " at ChecklistActivity loadlistNew00.";
                objGlobalMethods.writeToFile(msg3, "", "", "", "");
            }

            CheckListActivity.PhotoImagePathList = null;
            CheckListActivity.dataTlist = null;

            //al = null;
            if (al != null && al.size() > 0) {
                objGlobalMethods.writeToFile("Checklist viewed for JobsIDX: " + String.valueOf(JobsIDX), "", "", "", "");
                //adapter = new checklist_PostItemAdapter(this, al);
                adapter = new checklist_PostItemAdapter(myApp.getAppContext(), al);

                if (doHiddenAdjustment)
                    adapter.adjustHiddenFirst();

                if (!objGlobalMethods.IsEmptyString(hideshow10to19NewForced) && hideshow10to19NewForced.equals("h")) {
                    adapter.hide10to19New();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "hideshow10to19NewForced", "");
                } else if (!objGlobalMethods.IsEmptyString(hideshow10to19NewForced) && hideshow10to19NewForced.equals("s")) {
                    adapter.show10to19New();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "hideshow10to19NewForced", "");
                }

                listView.setAdapter(adapter);
                String pr_latemsg0_reqFuturePrompt = objGlobalMethods.getGCMPrefs(gcmp, "pr_latemsg0_reqFuturePrompt");

                if ((isLockerJob && StatusCodeIDX == 103) || (StatusCodeIDX == 103)) {
                    btnCheckListNext.setText(ctx.getString(R.string.buttontext_PU));
                }

                if (!objGlobalMethods.IsEmptyString(pr_latemsg0_reqFuturePrompt)) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "pr_latemsg0_reqFuturePrompt", "");
                    objGlobalMethods.showPR_lateprompt();
                }
            } else if (al != null && al.size() == 0 && hasVerifiedOTP) {
                objGlobalMethods.writeToFile("Checklist skipped due hasVerifiedOTP for JobsIDX: " + String.valueOf(JobsIDX), "", "", "", "");
                startActivity(nextActivtyIntent);
                //finish(); //newly inserted
            } else {
                objGlobalMethods.writeToFile("al is null or empty at ChecklistActivity loadlistNew00.", "", "", "", "");
                //showErrorAlert();
                back();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at CheckListActivity loadlistNew00. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            showErrorAlert();
        }
    }

    private String getCheckListValueNew() {
        String chklsval = "";
        String txt = "";
        checklist_PostData d = null;
        try {
            if (al != null && al.size() > 0) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    d = al.get(i);
                    txt = d.DisplayText;
                    if (!d.ControlCode.equals("LB") && !d.ControlCode.equals("OT") && !d.getHidden()) {

                        if (d.ControlCode.equals("TL")) {
                            chklsval += d.getDisplayText().trim() + ": ";
                            chklsval += "\n" + d.OptionValue2.get(0) + "°C" + " ON " + objGlobalMethods.MillisecToDateStr2(Long.parseLong(d.OptionValue2.get(1)), "dd/MM/yyyy HH:mm") + "\n\n";

                            //manual record: 0, auto record: 1
                            if (d.OptionValue != null && d.OptionValue.size() > 0 && d.OptionValue.get(0).equals("0") && d.OptionValue2 != null && d.OptionValue2.size() > 0) {
                                //nid check for photo n value
                                ArrayList<CustomGallery> dataT = d.dataT;
                                CustomGallery cg = null;
                                if (dataT != null && dataT.size() > 0) {
                                    for (int t = 0; t <= dataT.size() - 1; t++) {
                                        cg = dataT.get(t);
                                        if (cg != null && !objGlobalMethods.IsEmptyString(cg.sdcardPath)) {
                                            cg.setPhotodesc("Temperature Logger Photo");
                                            if (dataTlist == null) {
                                                dataTlist = new ArrayList<>();
                                            }
                                            dataTlist.add(cg);
                                        }
                                    }
                                }
                            }
                        } else if (d.ControlCode.equals("CG")) {
                            if (d.OptionValue != null && d.OptionValue.size() > 0) {
                                chklsval += d.getDisplayText().trim() + ":\n";
                                for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                    if (d.OptionValue.get(x).equals("YES")) {
                                        //chklsval += d.Option[x].trim() + ": " + d.OptionValue.get(x).toUpperCase() + "\n";
                                        chklsval += " - " + d.Option[x].trim() + "\n";
                                    }
                                }
                                chklsval += "\n";
                            }
                        } else if (d.ControlCode.equals("RG")) {
                            if (d.OptionValue != null && d.OptionValue.size() > 0) {
                                chklsval += d.getDisplayText().trim() + ":\n";
                                for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                    if (d.OptionValue.get(x).equals("YES")) {
                                        chklsval += d.Option[x].trim() + "\n\n";
                                        break;
                                    }
                                }
                            }
                        } else if (d.ControlCode.equals("RO")) {
                            if (d.OptionValue != null && d.OptionValue.size() > 0) {
                                chklsval += d.getDisplayText().trim() + ": ";
                                for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                    if (d.OptionValue.get(x).equals("YES")) {
                                        chklsval += d.Option[x].trim() + "\n";
                                        if ((x == d.OptionValue.size() - 1 && d.OptionValue2 != null && d.OptionValue2.size() > 0)) {
                                            chklsval += d.OptionValue2.get(0).trim() + "\n\n";
                                        }
                                        break;
                                    }
                                }
                            }
                        } else if (d.ControlCode.equals("PH") || d.ControlCode.equals("PR") || d.ControlCode.equals("SO")) {
                            ArrayList<CustomGallery> dataT = d.dataT;
                            boolean hasimg = false;
                            if (dataT != null && dataT.size() > 0) {
                                for (int t = 0; t <= dataT.size() - 1; t++) {
                                    if (dataT.get(t) != null && !objGlobalMethods.IsEmptyString(dataT.get(t).sdcardPath)) {
                                        if (dataTlist == null) {
                                            dataTlist = new ArrayList<>();
                                        }
                                        dataTlist.add(dataT.get(t));
                                        hasimg = true;
                                    }
                                }
                            }
                            if (hasimg) {
                                chklsval += d.getDisplayText().trim() + (d.ControlCode.equals("SO") ? ": SIGNED" : ": YES") + "\n\n";
                            } else {
                                chklsval += d.getIsChecked() ? d.getDisplayText().trim() + ": NO" + "\n\n" : "";
                            }
                        } else if (d.ControlCode.equals("ID")) {
                            if (d.OptionValue != null && d.OptionValue.size() > 0) {
                                chklsval += "Verified ID:";
                                for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        chklsval += " " + d.OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    } else {
                                        chklsval += ", " + d.OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    }
                                }
                            }
                        } else if (d.ControlCode.equals("V1")) {
                            if (d.OptionValue != null && d.OptionValue.size() > 0) {
                                for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                    if (!objGlobalMethods.IsEmptyString(d.OptionValue.get(x).trim())) {
                                        chklsval += "Verified Contact Number:";
                                        chklsval += " " + d.OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    }
                                }
                            }
                        } else if (d.ControlCode.equals("V5")) {
                            if (d.OptionValue != null && d.OptionValue.size() > 0) {
                                for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                    if (!objGlobalMethods.IsEmptyString(d.OptionValue.get(x).trim())) {
                                        chklsval += "Verified Unit Number:";
                                        chklsval += " " + d.OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    }
                                }
                            }
                        } else if (d.ControlCode.equals("AY") || d.ControlCode.equals("AN") || d.ControlCode.equals("FY") || d.ControlCode.equals("FN")) {
                            //do nothing
                        } else if (isYNRD(d)) {
                            if (d.OptionValue != null && d.OptionValue.size() > 0) {
                                chklsval += d.getDisplayText().trim() + ":";
                                for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        chklsval += " " + d.OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    } else {
                                        chklsval += ", " + d.OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    }
                                }
                            } else {
                                chklsval += d.getDisplayText().trim() + ":";
                                chklsval += " NO" + "\n\n";
                            }
                        } else {
                            if (d.OptionValue != null && d.OptionValue.size() > 0) {
                                chklsval += d.getDisplayText().trim() + ":";
                                for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        chklsval += " " + d.OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    } else {
                                        chklsval += ", " + d.OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    }
                                }
                            }
                        }
                    } else {
                        if (d.ControlCode.equals("LB") && !d.ControlCode.equals("OT") && !d.getHidden() &&
                                objGlobalMethods.IsEqualString(d.DisplayText, myApp.getAppContext().getString(R.string.buttontext_otp_not_req_nomobile_en))) {
                            chklsval += d.getDisplayText().trim() + "\n\n";
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at CheckListActivity getCheckListValueNew. Exception: " + s, "", "", "", "");
            chklsval = "";
        } finally {
            return chklsval;
        }
    }

    private String getCheckListValue() {
        String chklsval = "";
        String txt = "";
        try {
            if (al != null && al.size() > 0) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    txt = al.get(i).DisplayText;
                    if (!al.get(i).ControlCode.equals("LB") && !al.get(i).ControlCode.equals("OT") && !al.get(i).getHidden()) {

                        if (al.get(i).ControlCode.equals("TL")) {
                            chklsval += al.get(i).getDisplayText().trim() + ": ";
                            //chklsval += "\n" + al.get(i).OptionValue2.get(0) + "°C" + " AT " + al.get(i).OptionValue2.get(1) + "\n\n";
                            chklsval += "\n" + al.get(i).OptionValue2.get(0) + "°C" + " ON " + objGlobalMethods.MillisecToDateStr2(Long.parseLong(al.get(i).OptionValue2.get(1)), "dd/MM/yyyy HH:mm") + "\n\n";

                            //manual record: 0, auto record: 1
                            if (al.get(i).OptionValue != null && al.get(i).OptionValue.size() > 0 && al.get(i).OptionValue.get(0).equals("0") && al.get(i).OptionValue2 != null && al.get(i).OptionValue2.size() > 0) {
                                //nid check for photo n value
                                ArrayList<CustomGallery> dataT = al.get(i).dataT;
                                CustomGallery cg = null;
                                if (dataT != null && dataT.size() > 0) {
                                    for (int t = 0; t <= dataT.size() - 1; t++) {
                                        cg = dataT.get(t);
                                        if (cg != null && !objGlobalMethods.IsEmptyString(cg.sdcardPath)) {
                                            cg.setPhotodesc("Temperature Logger Photo");
                                            if (dataTlist == null) {
                                                dataTlist = new ArrayList<>();
                                            }
                                            dataTlist.add(cg);
                                        }
                                    }
                                }
                            }
                        } else if (al.get(i).ControlCode.equals("CG")) {
                            if (al.get(i).OptionValue != null && al.get(i).OptionValue.size() > 0) {
                                chklsval += al.get(i).getDisplayText().trim() + ":\n";
                                for (int x = 0; x <= al.get(i).OptionValue.size() - 1; x++) {
                                    if (al.get(i).OptionValue.get(x).equals("YES")) {
                                        //chklsval += al.get(i).Option[x].trim() + ": " + al.get(i).OptionValue.get(x).toUpperCase() + "\n";
                                        chklsval += " - " + al.get(i).Option[x].trim() + "\n";
                                    }
                                }
                                chklsval += "\n";
                            }
                        } else if (al.get(i).ControlCode.equals("RG")) {
                            if (al.get(i).OptionValue != null && al.get(i).OptionValue.size() > 0) {
                                chklsval += al.get(i).getDisplayText().trim() + ":\n";
                                for (int x = 0; x <= al.get(i).OptionValue.size() - 1; x++) {
                                    if (al.get(i).OptionValue.get(x).equals("YES")) {
                                        chklsval += al.get(i).Option[x].trim() + "\n\n";
                                        break;
                                    }
                                }
                            }
                        } else if (al.get(i).ControlCode.equals("RO")) {
                            if (al.get(i).OptionValue != null && al.get(i).OptionValue.size() > 0) {
                                chklsval += al.get(i).getDisplayText().trim() + ": ";
                                for (int x = 0; x <= al.get(i).OptionValue.size() - 1; x++) {
                                    if (al.get(i).OptionValue.get(x).equals("YES")) {
                                        chklsval += al.get(i).Option[x].trim() + "\n";
                                        if ((x == al.get(i).OptionValue.size() - 1 && al.get(i).OptionValue2 != null && al.get(i).OptionValue2.size() > 0)) {
                                            chklsval += al.get(i).OptionValue2.get(0).trim() + "\n\n";
                                        }
                                        break;
                                    }
                                }
                            }
                        } else if (al.get(i).ControlCode.equals("PH") || al.get(i).ControlCode.equals("PR") || al.get(i).ControlCode.equals("SO")) {
                            ArrayList<CustomGallery> dataT = al.get(i).dataT;
                            boolean hasimg = false;
                            if (dataT != null && dataT.size() > 0) {
                                for (int t = 0; t <= dataT.size() - 1; t++) {
                                    if (dataT.get(t) != null && !objGlobalMethods.IsEmptyString(dataT.get(t).sdcardPath)) {
                                        if (dataTlist == null) {
                                            dataTlist = new ArrayList<>();
                                        }
                                        dataTlist.add(dataT.get(t));
                                        hasimg = true;
                                    }
                                }
                            }
                            if (hasimg) {
                                chklsval += al.get(i).getDisplayText().trim() + (al.get(i).ControlCode.equals("SO") ? ": SIGNED" : ": YES") + "\n\n";
                            } else {
                                chklsval += al.get(i).getIsChecked() ? al.get(i).getDisplayText().trim() + ": NO" + "\n\n" : "";
                            }
                        } else if (al.get(i).ControlCode.equals("ID")) {
                            if (al.get(i).OptionValue != null && al.get(i).OptionValue.size() > 0) {
                                chklsval += "Verified ID:";
                                for (int x = 0; x <= al.get(i).OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        chklsval += " " + al.get(i).OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    } else {
                                        chklsval += ", " + al.get(i).OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    }
                                }
                            }
                        } else if (al.get(i).ControlCode.equals("V1")) {
                            if (al.get(i).OptionValue != null && al.get(i).OptionValue.size() > 0) {
                                for (int x = 0; x <= al.get(i).OptionValue.size() - 1; x++) {
                                    if (!objGlobalMethods.IsEmptyString(al.get(i).OptionValue.get(x).trim())) {
                                        chklsval += "Verified Contact Number:";
                                        chklsval += " " + al.get(i).OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    }
                                }
                            }
                        } else if (al.get(i).ControlCode.equals("AY") || al.get(i).ControlCode.equals("AN") || al.get(i).ControlCode.equals("FY") || al.get(i).ControlCode.equals("FN")) {
                            //do nothing
                        } else {
                            if (al.get(i).OptionValue != null && al.get(i).OptionValue.size() > 0) {
                                chklsval += al.get(i).getDisplayText().trim() + ":";
                                for (int x = 0; x <= al.get(i).OptionValue.size() - 1; x++) {
                                    if (x == 0) {
                                        chklsval += " " + al.get(i).OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    } else {
                                        chklsval += ", " + al.get(i).OptionValue.get(x).trim().toUpperCase() + "\n\n";
                                    }
                                }
                            }
                        }
                    } else {

                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at CheckListActivity getCheckListValue. Exception: " + s, "", "", "", "");
            chklsval = "";
        } finally {
            return chklsval;
        }
    }

    private void next2() {
        String checklistvalue = "";
        try {
            hasOtp = false;
            Boolean canproceed = canProceedNew();
            if (canproceed) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                final SharedPreferences gcmp = GcmPreferences;
                checklistvalue = getCheckListValueNew();
                if (fromPage != null && fromPage.equalsIgnoreCase("JobsListGroupActivity")) {
                    objGlobalMethods.setChecklistRelatedSP_KeyValue("checklistvalue", checklistvalue);
                    Camera5Activity.mCurrentPhotoPath = null;
                    startActivity(nextActivtyIntent);
                    //finish(); //newly inserted
                } else {
                    String isLockerJobStr = objGlobalMethods.getGCMPrefs(gcmp, "isLockerJobStr");
                    boolean isLockerJob = !objGlobalMethods.IsEmptyString(isLockerJobStr);
                    String CurrJobInfoStatusCodeIDX = objGlobalMethods.getGCMPrefs(gcmp, "CurrJobInfoStatusCodeIDX");
                    long StatusCodeIDX = !objGlobalMethods.IsEmptyString(CurrJobInfoStatusCodeIDX) ? Long.parseLong(CurrJobInfoStatusCodeIDX) : 0;
                    String currJobInfoIDXStr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
                    long JobsIDX = !objGlobalMethods.IsEmptyString(currJobInfoIDXStr) ? Long.parseLong(currJobInfoIDXStr) : 0;
                    final String fchecklistvalue = checklistvalue;

                    if ((isLockerJob && StatusCodeIDX == 103) || (StatusCodeIDX == 103)) {
                        Context ctx = myApp.getAppContext();
                        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                        builder.setCancelable(false);
                        builder.setMessage(ctx.getString(R.string.alert_cfmupdatestatusPU));
                        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", update job status
                                UpdateJobStatusPU(JobsIDX, fchecklistvalue);
                            }
                        });
                        builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user select "No", just cancel this dialog and continue with app
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        //GcmPreferences = objGlobalMethods.getGcmPreferences();
                        //objGlobalMethods.setGCMPrefs(GcmPreferences, "checklistvalue", checklistvalue);
                        objGlobalMethods.setChecklistRelatedSP_KeyValue("checklistvalue", checklistvalue);

                        Camera5Activity.mCurrentPhotoPath = null;

                        String PreDeterminedStatusCodeIDX = objGlobalMethods.getGCMPrefs(gcmp, "PreDeterminedStatusCodeIDX");
                        boolean PreDeterminedStatusCodeIDXisNonOKP = !objGlobalMethods.IsEmptyString(PreDeterminedStatusCodeIDX) && PreDeterminedStatusCodeIDX.equals("129") ? true : false;

                        if (PreDeterminedStatusCodeIDXisNonOKP) {
                            Intent okpIntent = new Intent(myApp.currentActivity, setDeliveredStatusActivity.class);
                            startActivity(okpIntent);
                        } else {
                            startActivity(nextActivtyIntent);
                        }

                        /*Camera5Activity.mCurrentPhotoPath = null;
                        startActivity(nextActivtyIntent);*/
                    }
                }
            } else {
                showDialog(myApp.getAppContext().getString(hasOtp && TypeOfPinVerification.equals("0") ? R.string.alert_complete_otp : R.string.completechecklist));
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at CheckListActivity next2. Exception: " + s, "", "", "", "");
            showDialog(myApp.getAppContext().getString(R.string.alert_errorperformoperation));
        }
    }

    private boolean isYNRD(checklist_PostData d) {
        boolean asd = false;
        try {
            asd = d != null && (d.ControlCode.equals("YN") || d.ControlCode.equals("RD"));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity isYNRD. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private boolean isOV_YES(checklist_PostData d) {
        boolean asd = false;
        try {
            asd = d != null && (d.OptionValue != null && d.OptionValue.size() > 0 && d.OptionValue.get(0).equals("YES"));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity isOV_YES. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private Boolean canProceedNew() {
        Boolean asd = true;
        int breakIndex = 0;
        boolean hasItemProofImgFileMoreThan7MinOld = false;
        checklist_PostData d = null;

        try {
            outer:
            for (int i = 0; i <= al.size() - 1; i++) {
                d = al.get(i);
                al.get(breakIndex).setAltColor(false);
                if (!d.hidden) {
                    if (d.FinalManOptCode.equals("MD") && (d.ControlCode.equals("PH") || d.ControlCode.equals("PR") || d.ControlCode.equals("SO")) &&
                            (d.dataT == null || (d.dataT != null && d.dataT.size() == 0))) {
                        breakIndex = i;
                        if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                TypeOfPinVerification.equals("1")) {
                            //do nothing
                        } else {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                    } else if (d.FinalManOptCode.equals("MD") && (d.ControlCode.equals("TL"))) {
                        breakIndex = i;
                        //manual record: 0, auto record: 1
                        if (d.OptionValue != null && d.OptionValue.size() > 0 && d.OptionValue.get(0).equals("0")) {
                            //nid value n photo
                            if (d.OptionValue2 != null && d.OptionValue2.size() > 0 && objGlobalMethods.IsEmptyString(d.OptionValue2.get(0))) {
                                al.get(breakIndex).setAltColor(true);
                                asd = false;
                                break outer;
                            } else if (d.dataT == null || (d.dataT != null && d.dataT.size() == 0)) {
                                al.get(breakIndex).setAltColor(true);
                                asd = false;
                                break outer;
                            }

                        }
                    } else if (d.FinalManOptCode.equals("MD") && !d.getIsChecked() && (d.OptionValue == null || d.OptionValue.size() == 0)) {
                        breakIndex = i;
                        if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                TypeOfPinVerification.equals("1")) {
                            //do nothing
                        } else {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                    } else if (d.FinalManOptCode.equals("MD") && d.getIsChecked() && (d.OptionValue == null || d.OptionValue.size() == 0) && !isYNRD(d)) {
                        breakIndex = i;
                        if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                TypeOfPinVerification.equals("1")) {
                            //do nothing
                        } else {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                    } else if (d.FinalManOptCode.equals("MD") && d.getIsChecked() && (d.OptionValue != null && d.OptionValue.size() > 0) && d.ControlCode.equals("CG")) {
                        int c = 0;
                        for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                            if (d.OptionValue.get(x).equals("NO")) {
                                c++;
                            }
                        }
                        if (c == d.OptionValue.size()) {
                            breakIndex = i;
                            if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                    TypeOfPinVerification.equals("1")) {
                                //do nothing
                            } else {
                                al.get(breakIndex).setAltColor(true);
                                asd = false;
                                break outer;
                            }
                            //asd = false;
                        }
                    } else if (d.FinalManOptCode.equals("MD") && al.get(i).getIsChecked() && (d.OptionValue != null && d.OptionValue.size() > 0) && d.ControlCode.equals("RG")) {
                        int c = 0;
                        for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                            if (d.OptionValue.get(x).equals("NO")) {
                                c++;
                            }
                        }
                        if (c == d.OptionValue.size()) {
                            breakIndex = i;
                            if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                    TypeOfPinVerification.equals("1")) {
                                //do nothing
                            } else {
                                al.get(breakIndex).setAltColor(true);
                                asd = false;
                                break outer;
                            }
                        }
                    } else if (d.FinalManOptCode.equals("MD") && al.get(i).getIsChecked() && (d.OptionValue != null && d.OptionValue.size() > 0) && d.ControlCode.equals("RO")) {
                        int c = 0;
                        for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                            if (d.OptionValue.get(x).equals("NO")) {
                                c++;
                            } else if (d.OptionValue.get(x).equals("YES") && (x == d.OptionValue.size() - 1) &&
                                    (d.OptionValue2 == null ||
                                            (d.OptionValue2 != null && d.OptionValue2.size() == 0) ||
                                            (d.OptionValue2 != null && d.OptionValue2.size() > 0 && objGlobalMethods.IsEmptyString(d.OptionValue2.get(0).trim())))
                            ) {
                                c++;
                            }
                        }
                        if (c == d.OptionValue.size()) {
                            breakIndex = i;
                            if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                    TypeOfPinVerification.equals("1")) {
                                //do nothing
                            } else {
                                al.get(breakIndex).setAltColor(true);
                                asd = false;
                                break outer;
                            }
                        }
                    } else if (d.ControlCode.equals("OT")) {
                        breakIndex = i;
                        hasOtp = true;
                        if (d.OptionValue != null && d.OptionValue.size() > 0) {
                            TypeOfPinVerification = d.OptionValue.get(0);
                            if (TypeOfPinVerification.equals("0")) { //0 or within 3 tries
                                al.get(breakIndex).setAltColor(true);
                                asd = false;
                                break outer;
                            }
                        } else {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                    }

                    if (asd) {
                        breakIndex = i;
                    }
                }
            }

            if (!asd && listView != null) {
                for (int i = breakIndex + 1; i <= al.size() - 1; i++) {
                    al.get(i).setAltColor(false);
                }

                adapter = (checklist_PostItemAdapter) listView.getAdapter();
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity canProceedNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            asd = false;
        } finally {
            return asd;
        }
    }

    private Boolean canProceed() {
        Boolean asd = true;
        int breakIndex = 0;
        boolean hasItemProofImgFileMoreThan7MinOld = false;
        checklist_PostData d = null;

        try {
            outer:
            for (int i = 0; i <= al.size() - 1; i++) {
                //if (al.get(i).FinalManOptCode.equals("MD") && al.get(i).ControlCode.equals("PH") && al.get(i).PhotoImagePath.equals("")) {
                /*if (al.get(i).FinalManOptCode.equals("MD") && (al.get(i).ControlCode.equals("PH") || al.get(i).ControlCode.equals("PR")) &&
                        (al.get(i).dataT == null || (al.get(i).dataT != null && objGlobalMethods.IsEmptyString(al.get(i).dataT.sdcardPath)))) {*/

                d = al.get(i);
                al.get(breakIndex).setAltColor(false);
                if (d.FinalManOptCode.equals("MD") && (d.ControlCode.equals("PH") || d.ControlCode.equals("PR") || d.ControlCode.equals("SO")) &&
                        (d.dataT == null || (d.dataT != null && d.dataT.size() == 0)) && !d.hidden) {
                    //breakIndex = rtnBackwardParentIndex(i);
                    breakIndex = i;
                    if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                            TypeOfPinVerification.equals("1")) {
                        //do nothing
                    } else {
                        al.get(breakIndex).setAltColor(true);
                        asd = false;
                        break outer;
                    }
                }
                /*else if (al.get(i).FinalManOptCode.equals("MD") && al.get(i).ControlCode.equals("PR") &&
                        (al.get(i).dataT == null || (al.get(i).dataT != null && !objGlobalMethods.IsEmptyString(al.get(i).dataT.sdcardPath)))) {
                    hasItemProofImgFileMoreThan7MinOld = JobsHandler.hasImgFileMoreThan7MinOld(null, null, al.get(i).dataT);
                    if (hasItemProofImgFileMoreThan7MinOld) {
                        breakIndex = rtnBackwardParentIndex(i);
                        al.get(i).setDataT(null);
                        al.get(breakIndex).setAltColor(true);
                        asd = false;
                        break outer;
                    }
                }*/
                else if (d.FinalManOptCode.equals("MD")
                        && (d.ControlCode.equals("TL")) && !d.hidden) {
                    breakIndex = i;
                    //manual record: 0, auto record: 1
                    if (d.OptionValue != null && d.OptionValue.size() > 0 && d.OptionValue.get(0).equals("0")) {
                        //nid value n photo
                        if (d.OptionValue2 != null && d.OptionValue2.size() > 0 && objGlobalMethods.IsEmptyString(d.OptionValue2.get(0))) {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        } else if (d.dataT == null || (d.dataT != null && d.dataT.size() == 0)) {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }

                    }
                } else if (d.FinalManOptCode.equals("MD") && !d.getIsChecked() && (d.OptionValue == null || d.OptionValue.size() == 0) && !d.hidden) {
                    //breakIndex = rtnBackwardParentIndex(i);
                    breakIndex = i;
                    if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                            TypeOfPinVerification.equals("1")) {
                        //do nothing
                    } else {
                        al.get(breakIndex).setAltColor(true);
                        asd = false;
                        break outer;
                    }
                } else if (d.FinalManOptCode.equals("MD") && d.getIsChecked() && (d.OptionValue == null || d.OptionValue.size() == 0) && !d.hidden) {
                    //breakIndex = rtnBackwardParentIndex(i);
                    breakIndex = i;
                    if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                            TypeOfPinVerification.equals("1")) {
                        //do nothing
                    } else {
                        al.get(breakIndex).setAltColor(true);
                        asd = false;
                        break outer;
                    }
                } else if (d.FinalManOptCode.equals("MD") && d.getIsChecked() && (d.OptionValue != null && d.OptionValue.size() > 0)
                        && d.ControlCode.equals("CG") && !d.hidden) {
                    /*for (int x = 0; x <= al.get(i).OptionValue.size() - 1; x++) {
                        if (al.get(i).OptionValue.get(x).equals("NO")) {
                            breakIndex = rtnBackwardParentIndex(i);
                            //al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                    }*/
                    int c = 0;
                    for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                        if (d.OptionValue.get(x).equals("NO")) {
                            c++;
                        }
                    }
                    if (c == d.OptionValue.size()) {
                        //breakIndex = rtnBackwardParentIndex(i);
                        breakIndex = i;
                        if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                TypeOfPinVerification.equals("1")) {
                            //do nothing
                        } else {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                        //asd = false;
                    }
                } else if (d.FinalManOptCode.equals("MD") && al.get(i).getIsChecked() && (d.OptionValue != null && d.OptionValue.size() > 0)
                        && d.ControlCode.equals("RG") && !d.hidden) {
                    int c = 0;
                    for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                        if (d.OptionValue.get(x).equals("NO")) {
                            c++;
                        }
                    }
                    if (c == d.OptionValue.size()) {
                        //breakIndex = rtnBackwardParentIndex(i);
                        breakIndex = i;
                        if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                TypeOfPinVerification.equals("1")) {
                            //do nothing
                        } else {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                        //asd = false;
                    }
                } else if (d.FinalManOptCode.equals("MD") && al.get(i).getIsChecked() && (d.OptionValue != null && d.OptionValue.size() > 0)
                        && d.ControlCode.equals("RO") && !d.hidden) {
                    int c = 0;
                    for (int x = 0; x <= d.OptionValue.size() - 1; x++) {
                        if (d.OptionValue.get(x).equals("NO")) {
                            c++;
                        } else if (d.OptionValue.get(x).equals("YES") && (x == d.OptionValue.size() - 1) &&
                                (d.OptionValue2 == null ||
                                        (d.OptionValue2 != null && d.OptionValue2.size() == 0) ||
                                        (d.OptionValue2 != null && d.OptionValue2.size() > 0 && objGlobalMethods.IsEmptyString(d.OptionValue2.get(0).trim())))
                        ) {
                            c++;
                        }
                    }
                    if (c == d.OptionValue.size()) {
                        //breakIndex = rtnBackwardParentIndex(i);
                        breakIndex = i;
                        if (hasOtp && (d.HierarchyCode >= 10 && d.HierarchyCode <= 19) &&
                                TypeOfPinVerification.equals("1")) {
                            //do nothing
                        } else {
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                        //asd = false;
                    }
                } else if (d.ControlCode.equals("OT") && !d.hidden) {
                    breakIndex = i;
                    hasOtp = true;
                    if (d.OptionValue != null && d.OptionValue.size() > 0) {
                        TypeOfPinVerification = d.OptionValue.get(0);
                        if (TypeOfPinVerification.equals("0")) { //0 or within 3 tries
                            al.get(breakIndex).setAltColor(true);
                            asd = false;
                            break outer;
                        }
                    } else {
                        al.get(breakIndex).setAltColor(true);
                        asd = false;
                        break outer;
                    }
                }
                if (asd) {
                    //breakIndex = rtnBackwardParentIndex(i);
                    //al.get(breakIndex).setAltColor(false);
                    breakIndex = i;
                }
            }

            if (!asd && listView != null) {
                adapter = (checklist_PostItemAdapter) listView.getAdapter();
                adapter.notifyDataSetChanged();
                /*final int finalindex = breakIndex;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        listView.setSelection(finalindex);
                    }
                }, 100);*/
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }
    }

    public static View.OnClickListener rtnlblDateTimeTimeOnClickListener(final SlideDateTimeListener listener, final Date ddate) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Date dt = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                c.add(Calendar.DATE, -1);
                dt = c.getTime();

                new SlideDateTimePicker.Builder(manager)
                        .setListener(listener)
                        .setInitialDate(ddate != null ? ddate : dt)
                        //.setMinDate(minDate)
                        //.setMaxDate(maxDate)
                        //.setIs24HourTime(true)
                        .setTheme(SlideDateTimePicker.HOLO_DARK)
                        //.setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
            }
        };

    }

    public static View.OnClickListener rtnlblDateOnClickListener(final SlideDateListener listener, final Date ddate) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Date dt = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                c.add(Calendar.DATE, -1);
                dt = c.getTime();

                new SlideDatePicker.Builder(manager)
                        .setListener(listener)
                        .setInitialDate(ddate != null ? ddate : dt)
                        //.setMinDate(minDate)
                        //.setMaxDate(maxDate)
                        .setTheme(SlideDatePicker.HOLO_DARK)
                        //.setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
            }
        };
    }

    public static void clearItemPendingData(Integer idx) {
        try {
            checklist_PostData d = al.get(idx);
            d.setSelectedDatePendingDay(null);
            d.setSelectedDatePendingMth(null);
            d.setSelectedDatePendingYear(null);
            d.setSelectedDatePending(null);
            d.setSelectedDatePendingYearIndex(0);
            al.set(idx, d);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at CheckListActivity clearItemPendingData. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private void UpdateJobStatusPU(Long JobsIDX, String checklistvalue) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
            Date dt = new Date();
            Long l = dt.getTime();
            String timestampstr = "/Date(" + l + ")/";
            Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
            int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
            String RegID = objGlobalMethods.getRegistrationIdOnly();
            JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDX, 49, timestampstr, checklistvalue, "",
                    lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");

            ArrayList<objConvertedBitmapToBase64> CheckListconvertedBitmapToBase64List = null;
            CheckListconvertedBitmapToBase64List = RTNCheckListConvertedBitmapToBase64List();
            if (CheckListconvertedBitmapToBase64List != null && CheckListconvertedBitmapToBase64List.size() > 0) {
                for (int x = 0; x <= CheckListconvertedBitmapToBase64List.size() - 1; x++) {
                    JobsHandler.addUpdateJobInfoPhotoWithGPSQueALPrefs(UpdateJobInfoPreferences, RegID, JobsIDX,
                            !objGlobalMethods.IsEmptyString(CheckListconvertedBitmapToBase64List.get(x).photodesc) ? "[49]" + CheckListconvertedBitmapToBase64List.get(x).photodesc : "[49]",
                            CheckListconvertedBitmapToBase64List.get(x).convertedBitmapToBase64,
                            timestampstr, lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy,
                            CheckListconvertedBitmapToBase64List.get(x).FilePath);
                }
            }

            objGlobalMethods.writeToFile("Click to PU Locker job at CheckListActivity.", "JobsIDX: " + String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(49), "", "");
            submitSQLite(JobsIDX, 49, GcmPreferences, UpdateJobInfoPreferences);
        } catch (Exception e) {
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at CheckListActivity UpdateJobStatus. Exception: " + e.toString() + "\nStack: " + stack, "", "", "", "");
        }
    }

    private void submitSQLite(long JobsIDX, long aaAStatusCodeIDX, SharedPreferences agcmp, SharedPreferences aupdatep) {

        try {
            //objGlobalMethods.writeToFile("JobInfoActivity submit started.", "", "", "", "");
            pd.show();

            DAO_Jobs.updateJobsCatTypeID(1, JobsIDX);
            JobsHandler.setSystemCategoryJobsIDXhashSQLite(agcmp, 0);
            JobsHandler.setSystemCategoryJobsIDXhashSQLite(agcmp, 1);

            //objGlobalMethods.writeToFile("JobsHandler from JobInfoActivity submit started.", "", "", "", "");
            Context ctx = myApp.getAppContext();
            sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

            pd.dismiss();
            BackStepWithoutUpdateSQLite();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at ShipmentActivity submitSQLite. Exception: " + e.toString(), "", "", "", "");
            pd.dismiss();
            if (aaAStatusCodeIDX != 368 && aaAStatusCodeIDX != 369 && aaAStatusCodeIDX != 388) {
                BackStepWithoutUpdateSQLite();
            }
        }
    }

    private void BackStepWithoutUpdateSQLite() {
        Intent parentIntent = null;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        int CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        try {
            Camera5Activity.mCurrentPhotoPath = null;
            setFailStatusActivity.reasons = "";
            setFailStatusActivity.selectedStatusCodesIDX = "";
            setFailStatusActivity.ddlSelectedIndex = 0;
            setDeliveredStatusActivity.ddlSelectedIndex = 0;
            setDeliveredStatusActivity.ishuman = "0";
            objJobInfo[] JobInfoAL;
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsName, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.checkedItemsIDX, "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "ScannedCodeStrsPrefStr", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "massupdateokpwp2PostDataArr", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "objJobInfoBringtoUpdateCODListJsonStr", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "mutlipleCODjobs", "");
            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
            objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
            objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, "");
            String Postal = objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal");
            String SelectedVRPJobsIDXs = objGlobalMethods.getGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs");
            String fromSubMenu = objGlobalMethods.getGCMPrefs(GcmPreferences, "fromSubMenu");
            int TotalJobsCountPerCatTypePerPostal = !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal) :
                    !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalJobsCountPerCatTypePerPostal_And_JobsIDXsStr(CatTypeID, Postal, SelectedVRPJobsIDXs)
                            : DAO_Jobs.getTotalJobsCountPerCatTypePerPostal(CatTypeID, Postal);

            if (!GcmPreferences.getString("isFromMainCOD", "").isEmpty()) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMessaging", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                //parentIntent = new Intent(this, MainCODActivity.class);
                parentIntent = new Intent(myApp.currentActivity, MainCODActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!GcmPreferences.getString("isFromMessaging", "").isEmpty()) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMessaging", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                //parentIntent = new Intent(this, Messaging.class);
                parentIntent = new Intent(myApp.currentActivity, Messaging.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (!GcmPreferences.getString("isFromDashboardSub", "").isEmpty()) {
                if (TotalJobsCountPerCatTypePerPostal > 1) {
                    //parentIntent = new Intent(this, JobsListGroupActivity.class);
                    parentIntent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                } else if (TotalJobsCountPerCatTypePerPostal == 0 || TotalJobsCountPerCatTypePerPostal == 1) {
                    Integer TotalPIPDIPJobsCountPerPostal = !SelectedVRPJobsIDXs.equals("") ? DAO_Jobs.getTotalPIPDIPJobsCountPerPostalWithJobsIDXs(Postal, SelectedVRPJobsIDXs)
                            : DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(Postal);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "-1");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTitlePrefName, "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMessaging", "");
                    if (TotalPIPDIPJobsCountPerPostal > 1) {
                        //parentIntent = new Intent(this, DashboardSubActivity.class);
                        parentIntent = new Intent(myApp.currentActivity, DashboardSubActivity.class);
                    } else {
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");

                        if (DashboardSubActivity.frompage != null) {
                            //parentIntent = new Intent(this, MapsAllActivity.class);
                            parentIntent = new Intent(myApp.currentActivity, MapsAllActivity.class);
                        } else {
                            //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                            parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? AddressListRoutingActivity.class : DrawerMain2Activity.class);
                        }
                    }
                    MainActivity.uponLogin = 0;
                }
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (TotalJobsCountPerCatTypePerPostal == 0) {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMainCOD", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromMessaging", "");
                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "");
                int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
                if (TotalJobsCountPerCatType >= 1) {
                    //parentIntent = new Intent(this, AddressListNewActivity.class);
                    parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
                } else if (TotalJobsCountPerCatType < 1) {
                    //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                    parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                }
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            } else if (TotalJobsCountPerCatTypePerPostal >= 1) {
                //parentIntent = new Intent(this, JobsListGroupActivity.class);
                parentIntent = new Intent(myApp.currentActivity, JobsListGroupActivity.class);
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(parentIntent);
                this.finish();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at CheckListActivity BackStepWithoutUpdateSQLite. Exception: " + s, "", "", "", "");
            int TotalJobsCountPerCatType = DAO_Jobs.getTotalJobsCountPerCatType(CatTypeID);
            if (TotalJobsCountPerCatType >= 1) {
                //parentIntent = new Intent(this, AddressListNewActivity.class);
                parentIntent = new Intent(myApp.currentActivity, AddressListNewActivity.class);
            } else if (TotalJobsCountPerCatType < 1) {
                //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                parentIntent = new Intent(myApp.currentActivity, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
            }
            MainActivity.uponLogin = 0;
            DrawerMainActivity.DashboardOpenFromPush = false;
            startActivity(parentIntent);
            this.finish();
        }
    }

    private ArrayList<objConvertedBitmapToBase64> RTNCheckListConvertedBitmapToBase64List() {
        ArrayList<objConvertedBitmapToBase64> convertedBitmapToBase64List = null;
        String convertedBitmapToBase64 = "";
        objConvertedBitmapToBase64 obj = null;
        boolean isCallLogImg = false;
        String photodesc = "";
        try {
            if (dataTlist != null && dataTlist.size() > 0) {
                for (int i = 0; i <= dataTlist.size() - 1; i++) {
                    String imgmCurrentPhotoPath = dataTlist.get(i).sdcardPath;
                    photodesc = objGlobalMethods.IsEmptyString(dataTlist.get(i).photodesc) ? "" : dataTlist.get(i).photodesc;
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeFile(imgmCurrentPhotoPath, bmOptions);

                    if (bitmap != null) {
                        convertedBitmapToBase64 = JobsHandler.convertBitmapToBase64(bitmap);
                        bitmap.recycle();
                        bitmap = null;
                    } else {
                        convertedBitmapToBase64 = "";
                    }

                    if (!convertedBitmapToBase64.equals("")) {
                        obj = new objConvertedBitmapToBase64(isCallLogImg, convertedBitmapToBase64);
                        obj.setFilePath(imgmCurrentPhotoPath);
                        obj.setPhotodesc(photodesc);
                        if (convertedBitmapToBase64List == null) {
                            convertedBitmapToBase64List = new ArrayList<>();
                            convertedBitmapToBase64List.add(obj);
                        } else {
                            convertedBitmapToBase64List.add(obj);
                        }
                    }
                }
            }
        } catch (Exception e) {
            convertedBitmapToBase64List = null;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity RTNCheckListConvertedBitmapToBase64List. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertedBitmapToBase64List;
        }
    }

    public void startSignatureNameActivity(final int position, final ArrayList<CustomGallery> dataT) {
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            Context ctx = myApp.getAppContext();
            Intent i = new Intent(ctx, SignatureNameActivity.class);
            String[] prefName = new String[]{"reqSign", "reqName"};
            String[] prefvalue = new String[]{"y", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            CheckListActivity.itemIndex = position;
            CheckListActivity.signatureString = !(dataT == null || (dataT != null && dataT.size() == 0)) ? dataT.get(0).ImgBitmapStr : "";
            SignatureNameActivity.rtnPageName = "CheckListActivity";
            startActivity(i);
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at CheckListActivity startSignatureNameActivity. Exception: " + s, "", "", "", "");
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
                    objGlobalMethods.writeToFile("Bluetooth is on at CheckListActivity", "", "", "", "");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CheckListActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }


}
