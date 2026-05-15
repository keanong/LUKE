package com.xdel.luke;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdel.luke.BottomSheet.tempLoggerEntryDialog;
import com.xdel.luke.HRM_Leave.LeaveApplicationActivity;
import com.xdel.luke.Locker.AuthActivity;
import com.xdel.luke.objClasses.Mail;
import com.xdel.luke.objClasses.checklist_PostData;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import android.support.v7.app.ActionBarActivity;


public class Camera5Activity extends Activity {
    //private static final String AUTHORITY = "com.xdel.luke";
    private final String AUTHORITY = "com.xdel.luke";
    ImageView preview;
    Button btnCancel, btnOK;
    TextView lblphototakentext;
    public static String mCurrentPhotoPath;
    private int catType;
    private SharedPreferences GcmPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];

    /*private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_PHOTO_S = 2;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";*/
    private final int ACTION_TAKE_PHOTO_B = 1;
    private final int ACTION_TAKE_PHOTO_S = 2;
    private final String JPEG_FILE_PREFIX = "IMG_";
    private final String JPEG_FILE_SUFFIX = ".jpg";
    private final String BITMAP_STORAGE_KEY = "viewbitmap";
    //private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";

    private Bitmap mImageBitmap = null;
    private Bitmap bitmap = null;
    File f = null;
    public static Uri fileuri = null;
    private ProgressDialog pd;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    String langType = "en";
    Activity thisAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        GcmPreferences = objGlobalMethods.getGcmPreferences();
        langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

        /*String languageToLoad = langType; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());*/

        setContentView(R.layout.activity_camera);

        lblphototakentext = (TextView) findViewById(R.id.lblphototakentext);
        preview = (ImageView) findViewById(R.id.preview);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOK = (Button) findViewById(R.id.btnOK);
        mCurrentPhotoPath = null;
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));

        //dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
        dispatchTakePictureIntent(1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        thisAct = this;
    }

    private void back() {
        Intent parentIntent = null;
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        mCurrentPhotoPath = null;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        try {
            if (catType == 0) {
                if (CheckListActivity.openCamera != null) {
                    CheckListActivity.openCamera = null;
                    CheckListActivity.itemIndex = null;
                    parentIntent = new Intent(thisAct, CheckListActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    parentIntent = new Intent(thisAct, JobInfoActivity.class);
                } else {
                    parentIntent = new Intent(thisAct, setFailStatusActivity.class);
                }
                //parentIntent = new Intent(this, setFailStatusActivity.class);
            } else if (catType == 2) {

                if (CheckListActivity.openCamera != null) {
                    CheckListActivity.openCamera = null;
                    CheckListActivity.itemIndex = null;
                    parentIntent = new Intent(thisAct, CheckListActivity.class);
                } else if (setDeliveredStatusActivity.isFromSetOKP) {
                    setDeliveredStatusActivity.isFromSetOKP = false;
                    parentIntent = new Intent(thisAct, setDeliveredStatusActivity.class);
                } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                    setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                    parentIntent = new Intent(thisAct, setDeliveredMassStatusActivity.class);
                } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                    setDunningDeliveredStatusActivity.isFromSetDunningOKP = false;
                    parentIntent = new Intent(thisAct, setDunningDeliveredStatusActivity.class);
                } else if (!objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "skiplocker"))) {
                    parentIntent = new Intent(thisAct, AuthActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    parentIntent = new Intent(thisAct, JobInfoActivity.class);
                } else {
                    parentIntent = new Intent(thisAct, setFailStatusActivity.class);
                }
            } else if (catType == -1) {
                LeaveApplicationActivity.isFromLeaveApplication = false;
                parentIntent = new Intent(thisAct, LeaveApplicationActivity.class);
            }

            objGlobalMethods.writeToFile("Camera5Activity back() catType = " + catType + ", rtnChecklistVal = " + rtnChecklistVal() + ", PageMode = " + rtnPageMode(), "", "", "", "");

            MainActivity.uponLogin = 0;
            if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                JobInfoActivity.accessFromDirection = 1;
                tempLoggerEntryDialog.setLaunchCamFromTempLoggerEntry(false);
                startActivity(parentIntent);
                this.finish();
            } else {
                startActivity(parentIntent);
                this.finish();
            }
            /*startActivity(parentIntent);
            this.finish();*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity back(). Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            if (myApp.currentActivity != null) {
                parentIntent = new Intent(thisAct, myApp.currentActivity.getClass());
            } else {
                parentIntent = new Intent(thisAct, JobInfoActivity.class);
            }

            objGlobalMethods.writeToFile("Camera5Activity back() Exception catType = " + catType + ", rtnChecklistVal = " + rtnChecklistVal() + ", PageMode = " + rtnPageMode(), "", "", "", "");
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            this.finish();
        }
    }

    public void btnCancelClick(View view) {
        back();
    }

    private int rtnChecklistVal() {
        int v = 0; //everything ok at checklist photo taking
        try {
            if (CheckListActivity.openCamera == null) {
                v = 1;
            } else if (CheckListActivity.al == null) {
                v = 2;
            } else if (CheckListActivity.al.size() == 0) {
                v = 3;
            } else if (CheckListActivity.itemIndex == null) {
                v = 4;
            } else if (CheckListActivity.itemIndex < 0) {
                v = 5;
            } else if (CheckListActivity.al.size() - 1 < CheckListActivity.itemIndex) {
                v = 6;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity rtnChecklistVal. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public int rtnPageMode() {
        int v = 0;
        try {
            if (catType == 0) {
                if (MassUpdateOKPwP2.isFailFromMassUpdateOKPwP2) {
                    v = 1;
                } else {
                    v = 2;
                }
            } else if (catType == 2) {
                if (setDeliveredStatusActivity.isFromSetOKP) {
                    v = 3;
                } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                    v = 4;
                } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                    v = 5;
                } else {
                    v = 6; //fail
                }
            } else if (catType == -1) {
                v = 7;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity rtnPageMode. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return v;
        }
    }

    public void btnOKClick(View view) {
        //Intent parentIntent = new Intent(this, setFailStatusActivity.class);
        Intent parentIntent = null;
        try {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
            String isItemProof = objGlobalMethods.getGCMPrefs(GcmPreferences, "isItemProof");
            String skiplocker = objGlobalMethods.getGCMPrefs(GcmPreferences, "skiplocker");
            //String AuthActivity = objGlobalMethods.getGCMPrefs(GcmPreferences, "AuthActivity");
            if (catType == 0) {
                if (MassUpdateOKPwP2.isFailFromMassUpdateOKPwP2) {
                    parentIntent = new Intent(thisAct, setFailStatusActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    parentIntent = new Intent(thisAct, JobInfoActivity.class);
                } else {
                    parentIntent = new Intent(thisAct, setFailStatusActivity.class);
                }
            } else if (catType == 2) {
                if (setDeliveredStatusActivity.isFromSetOKP) {
                    parentIntent = new Intent(thisAct, setDeliveredStatusActivity.class);
                } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                    parentIntent = new Intent(thisAct, setDeliveredMassStatusActivity.class);
                } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                    parentIntent = new Intent(thisAct, setDunningDeliveredStatusActivity.class);
                } else if (!objGlobalMethods.IsEmptyString(skiplocker)) {
                    parentIntent = new Intent(thisAct, JobInfoActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    parentIntent = new Intent(thisAct, JobInfoActivity.class);
                } else {
                    parentIntent = new Intent(thisAct, setFailStatusActivity.class);
                }
            } else if (catType == -1) {
                parentIntent = new Intent(thisAct, LeaveApplicationActivity.class);
            }

            //objGlobalMethods.writeToFile("Camera5Activity btnOKClick catType = " + catType + ", rtnChecklistVal = " + rtnChecklistVal() + ", PageMode = " + rtnPageMode(), "", "", "", "");

            if (CheckListActivity.openCamera != null
                    && CheckListActivity.al != null && CheckListActivity.al.size() > 0
                    && CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1
                    && CheckListActivity.al.size() - 1 >= CheckListActivity.itemIndex) {

                int index = CheckListActivity.itemIndex;
                checklist_PostData ci = CheckListActivity.al.get(index);
                CustomGallery item = new CustomGallery();
                item.sdcardPath = mCurrentPhotoPath;
                item.setCallLog(isCallLog.equals("t") ? true : false);
                item.setItemProof(isItemProof.equals("t") ? true : false);
                /*item.setPhotodesc(isItemProof.equals("t") && !objGlobalMethods.IsEmptyString(CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText)
                        ? CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText : !objGlobalMethods.IsEmptyString(CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText)
                        ? CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText : "");
                item.setChecklistitemidx(isItemProof.equals("t") ? CheckListActivity.itemIndex : -1);
                CheckListActivity.al.get(CheckListActivity.itemIndex).addDataT(item);
                CheckListActivity.al.get(CheckListActivity.itemIndex).setAltColor(false);*/
                item.setPhotodesc(!objGlobalMethods.IsEmptyString(ci.DisplayText) ? ci.DisplayText : "");
                item.setChecklistitemidx(isItemProof.equals("t") ? index : -1);
                CheckListActivity.al.get(index).addDataT(item);
                CheckListActivity.al.get(index).setAltColor(false);
                if (isItemProof.equals("t")) {
                    JobsHandler.startRulingTimer(JobsHandler.getFileCreationDateTime(mCurrentPhotoPath), null);
                }
                CheckListActivity.al.get(index).OptionValue = new ArrayList<String>(){{add("YES");}};
                CheckListActivity.openCamera = null;
                CheckListActivity.itemIndex = null;
                mCurrentPhotoPath = null;
                parentIntent = new Intent(thisAct, CheckListActivity.class);
            } else if (setFailStatusActivity.isFromSetFailSatus) {
                if (setFailStatusActivity.dataT == null && setFailStatusActivity.imagePaths == null) {
                    setFailStatusActivity.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    setFailStatusActivity.imagePaths = new ArrayList<String>();
                    setFailStatusActivity.imagePaths.add(mCurrentPhotoPath);
                    setFailStatusActivity.dataT.add(item);
                } else {
                    setFailStatusActivity.imagePaths.add(mCurrentPhotoPath);
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    setFailStatusActivity.dataT.add(item);
                }
            } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                if (setDeliveredMassStatusActivity.dataT == null && setDeliveredMassStatusActivity.imagePaths == null) {
                    setDeliveredMassStatusActivity.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    setDeliveredMassStatusActivity.imagePaths = new ArrayList<String>();
                    setDeliveredMassStatusActivity.imagePaths.add(mCurrentPhotoPath);
                    setDeliveredMassStatusActivity.dataT.add(item);
                } else {
                    setDeliveredMassStatusActivity.imagePaths.add(mCurrentPhotoPath);
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    setDeliveredMassStatusActivity.dataT.add(item);
                }
            } else if (setDeliveredStatusActivity.isFromSetOKP) {
                if (setDeliveredStatusActivity.dataT == null && setDeliveredStatusActivity.imagePaths == null) {
                    setDeliveredStatusActivity.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    setDeliveredStatusActivity.imagePaths = new ArrayList<String>();
                    setDeliveredStatusActivity.imagePaths.add(mCurrentPhotoPath);
                    setDeliveredStatusActivity.dataT.add(item);
                } else {
                    setDeliveredStatusActivity.imagePaths.add(mCurrentPhotoPath);
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    setDeliveredStatusActivity.dataT.add(item);
                }
            } /*else if (!objGlobalMethods.IsEmptyString(skiplocker) && !objGlobalMethods.IsEmptyString(AuthActivity)) {
                setDeliveredStatusActivity.dataT = null;
                setDeliveredStatusActivity.imagePaths = null;
                setDeliveredStatusActivity.mCurrentPhotoPath = null;

                setDeliveredStatusActivity.dataT = new ArrayList<CustomGallery>();
                CustomGallery item = new CustomGallery();
                item.sdcardPath = mCurrentPhotoPath;
                item.setCallLog(isCallLog.equals("t") ? true : false);
                item.setItemProof(isItemProof.equals("t") ? true : false);
                setDeliveredStatusActivity.imagePaths = new ArrayList<String>();
                setDeliveredStatusActivity.imagePaths.add(mCurrentPhotoPath);
                setDeliveredStatusActivity.dataT.add(item);
            }*/ else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                if (setDunningDeliveredStatusActivity.dataT == null && setDunningDeliveredStatusActivity.imagePaths == null) {
                    setDunningDeliveredStatusActivity.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    setDunningDeliveredStatusActivity.imagePaths = new ArrayList<String>();
                    setDunningDeliveredStatusActivity.imagePaths.add(mCurrentPhotoPath);
                    setDunningDeliveredStatusActivity.dataT.add(item);
                } else {
                    setDunningDeliveredStatusActivity.imagePaths.add(mCurrentPhotoPath);
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    setDunningDeliveredStatusActivity.dataT.add(item);
                }
            } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                if (LeaveApplicationActivity.dataT == null && LeaveApplicationActivity.imagePaths == null) {
                    LeaveApplicationActivity.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    LeaveApplicationActivity.imagePaths = new ArrayList<String>();
                    LeaveApplicationActivity.imagePaths.add(mCurrentPhotoPath);
                    LeaveApplicationActivity.dataT.add(item);
                } else {
                    LeaveApplicationActivity.imagePaths.add(mCurrentPhotoPath);
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    LeaveApplicationActivity.dataT.add(item);
                }
            } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                tempLoggerEntryDialog.dataT = new ArrayList<CustomGallery>();
                tempLoggerEntryDialog.imagePaths = new ArrayList<String>();

                CustomGallery item = new CustomGallery();
                item.sdcardPath = mCurrentPhotoPath;
                item.setCallLog(false);
                item.setItemProof(false);
                item.setTempLogger(true);
                tempLoggerEntryDialog.imagePaths.add(mCurrentPhotoPath);
                tempLoggerEntryDialog.dataT.add(item);

                /*if (tempLoggerEntryDialog.dataT == null && tempLoggerEntryDialog.imagePaths == null) {
                    tempLoggerEntryDialog.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(false);
                    item.setItemProof(false);
                    item.setTempLogger(true);
                    tempLoggerEntryDialog.imagePaths = new ArrayList<String>();
                    tempLoggerEntryDialog.imagePaths.add(mCurrentPhotoPath);
                    tempLoggerEntryDialog.dataT.add(item);
                } else {
                    tempLoggerEntryDialog.imagePaths.add(mCurrentPhotoPath);
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(false);
                    item.setItemProof(false);
                    item.setTempLogger(true);
                    tempLoggerEntryDialog.dataT.add(item);
                }*/
            } else {
                if (CustomGalleryActivity.dataT == null && CustomGalleryActivity.imagePaths == null) {
                    CustomGalleryActivity.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    CustomGalleryActivity.imagePaths = new ArrayList<String>();
                    CustomGalleryActivity.imagePaths.add(mCurrentPhotoPath);
                    CustomGalleryActivity.dataT.add(item);
                } else {
                    CustomGalleryActivity.imagePaths.add(mCurrentPhotoPath);
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = mCurrentPhotoPath;
                    item.setCallLog(isCallLog.equals("t") ? true : false);
                    item.setItemProof(isItemProof.equals("t") ? true : false);
                    CustomGalleryActivity.dataT.add(item);
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            String stack = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity btnOKClick. Exception: " + s, "", "", "", "");
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
            useridx = UsernamePwd[2];
            Mail m = new Mail("luke@xdel.com", "XDel2015");
            objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "", "Exception at Camera5Activity btnOKClick. Exception: " + s + "\n\nStack: " + stack
                    , "Exception at Camera5Activity btnOKClick." + " DriverIDX: " + useridx + ".", null);
            sendmail.execute();
            JobsHandler.disposeRulingTimer();
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            parentIntent = new Intent(thisAct, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        } finally {
            MassUpdateOKPwP2.isFailFromMassUpdateOKPwP2 = false;
            setDeliveredMassStatusActivity.isFromMassSetOKP = false;
            setDeliveredStatusActivity.isFromSetOKP = false;
            setDunningDeliveredStatusActivity.isFromSetDunningOKP = false;
            LeaveApplicationActivity.isFromLeaveApplication = false;
            MainActivity.uponLogin = 0;
            if (parentIntent == null) {
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                parentIntent = new Intent(thisAct, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(parentIntent);
                this.finish();
            } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                JobInfoActivity.accessFromDirection = 1;
                tempLoggerEntryDialog.setLaunchCamFromTempLoggerEntry(false);
                startActivity(parentIntent);
                this.finish();
            } else {
                startActivity(parentIntent);
                this.finish();
            }
            /*startActivity(parentIntent);
            this.finish();*/
        }
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.appspecificfilefolder_image);
    }

    private File getAlbumDir() {
        File storageDir = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
                ////below 2 lines of codes for app specific folder, but have issues on some devices suspected unable to read/write from app specific folder
                //String dirPath = getFilesDir().getAbsolutePath() + File.separator + getAlbumName(); //app specific - same location per preferenes & sqlite, cannot find via normal file system
                //storageDir = new File(dirPath);

                if (storageDir != null) {
                    if (!storageDir.mkdirs()) {
                        if (!storageDir.exists()) {
                            //Log.d("CameraSample", "failed to create directory");
                            objGlobalMethods.writeToFile("Camera5Activity getAlbumDir failed to create directory.", "", "", "", "");
                            return null;
                        }
                    }
                }

            } else {
                //Toast.makeText(myApp.getAppContext(),"External storage is not mounted READ/WRITE.", Toast.LENGTH_SHORT);
                Toast.makeText(myApp.getAppContext(), "External storage is not mounted READ/WRITE.", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity getAlbumDir. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return storageDir;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File imageF = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
            File albumF = getAlbumDir();
            //File af = getAppSpecificAlbumStorageDir(myApp.getAppContext(), getAlbumName());
            imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
            //File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, af);
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String fullFilename = imageF != null ? imageF.getName() : "";
            objGlobalMethods.setGCMPrefs(GcmPreferences, "photofilename", fullFilename);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity createImageFile. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            imageF = null;
        } finally {
            return imageF;
        }
    }

    private File setUpPhotoFile() throws IOException {

        File fi = createImageFile();
        mCurrentPhotoPath = fi.getAbsolutePath();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        objGlobalMethods.setGCMPrefs(GcmPreferences, "mCurrentPhotoPath", mCurrentPhotoPath);
        return fi;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight, boolean areqLesserImgCompression) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        try {
            if (!areqLesserImgCompression) {
                reqWidth = 267;
                reqHeight = 200;
                if (height > reqHeight || width > reqWidth) {

                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                    // height and width larger than the requested height and width.
                    while ((halfHeight / inSampleSize) > reqHeight
                            && (halfWidth / inSampleSize) > reqWidth) {
                        inSampleSize *= 2;
                    }
                }
            } else {

                /*reqWidth = 801;
                reqHeight = 600;
                if (height > reqHeight || width > reqWidth) {

                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                    // height and width larger than the requested height and width.
                    while ((halfHeight / inSampleSize) > reqHeight
                            && (halfWidth / inSampleSize) > reqWidth) {
                        inSampleSize *= 2;
                    }
                }*/

                inSampleSize = 2;
            }

        } catch (Exception e) {

        }
        return inSampleSize;
    }

    private void galleryAddPic() {
        try {
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at Camera5Activity galleryAddPic. " + e.getMessage().toString(), "", "", "", "");
            mCurrentPhotoPath = null;
        }

    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            switch (actionCode) {
                //case ACTION_TAKE_PHOTO_B:
                case 1:
                    try {
                        /*f = setUpPhotoFile();
                        fileuri = FileProvider.getUriForFile(myApp.getAppContext(), AUTHORITY, f);*/

                        //ensure fileuri is not null b4 activating camera
                        while (fileuri == null) {
                            f = setUpPhotoFile();
                            fileuri = FileProvider.getUriForFile(myApp.getAppContext(), AUTHORITY, f); //fileuri = FileProvider.getUriForFile(this, AUTHORITY, f);
                        }
                        if (f != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);
                            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivityForResult(takePictureIntent, actionCode);
                        } else {
                            objGlobalMethods.writeToFile("dispatchTakePictureIntent f == null.", "", "", "", "");
                            lblphototakentext.setVisibility(View.VISIBLE);
                            lblphototakentext.setText(myApp.getAppContext().getString(R.string.cannottakeimg));
                            preview.setVisibility(View.GONE);
                        }
                    } catch (IOException e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at Camera5Activity dispatchTakePictureIntent. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                        f = null;
                        mCurrentPhotoPath = null;
                        lblphototakentext.setVisibility(View.VISIBLE);
                        lblphototakentext.setText(myApp.getAppContext().getString(R.string.cannottakeimg));
                        preview.setVisibility(View.GONE);
                    }
                    break;

                default:
                    objGlobalMethods.writeToFile("dispatchTakePictureIntent default.", "", "", "", "");
                    lblphototakentext.setVisibility(View.VISIBLE);
                    lblphototakentext.setText(myApp.getAppContext().getString(R.string.cannottakeimg));
                    preview.setVisibility(View.GONE);
                    break;
            } // switch
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity dispatchTakePictureIntent. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            lblphototakentext.setVisibility(View.VISIBLE);
            lblphototakentext.setText(myApp.getAppContext().getString(R.string.cannottakeimg));
            preview.setVisibility(View.GONE);
        }
        //startActivityForResult(takePictureIntent, actionCode);
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage, String filename) throws IOException {
        try {
            ////ExifInterface ei = new ExifInterface(selectedImage.getPath());
            //ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
            //////filename = "/data/user/0/com.xdel.luke/files/Images/IMG_20210317_130525_1033579303201685758.jpg";
            ExifInterface ei = new ExifInterface(filename);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity rotateImageIfRequired., Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity rotateImage., Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            return img;
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Bitmap returnTimeStampedBM2(Bitmap bm, boolean areqLesserImgCompression) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system
            Canvas cs = new Canvas(bm);
            Paint tPaint = new Paint();
            tPaint.setTextSize(15f);
            tPaint.setColor(Color.WHITE);
            tPaint.setStyle(Paint.Style.FILL);
            cs.drawBitmap(bm, 0f, 0f, null);
            cs.drawText(dateTime + " XDel", 25f, bm.getHeight() - 25, tPaint);
            try {
                OutputStream fOut = null;
                /*if (f.exists()){
                    f.delete();
                    f.createNewFile();
                }*/
                fOut = new FileOutputStream(f);
                bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();

                ////add to gallery
                /*Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);*/
                ////add to gallery
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("FileNotFoundException at Camera5Activity returnTimeStampedBM2. FileNotFoundException: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity returnTimeStampedBM2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return bm;
        }
    }

    private void replaceImageFile(Bitmap bm) {
        try {
            File existingFile = new File(mCurrentPhotoPath);
            if (existingFile.exists()) {
                existingFile.delete();
            }
            FileOutputStream out = new FileOutputStream(existingFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    private class processImg extends AsyncTask<Void, Void, Boolean> {

        private String doInbackgroundExceptionMsg = "";
        private final String mCurrentPhotoPathfilename;
        private boolean areqLesserImgCompression;

        public processImg(String amCurrentPhotoPathfilename, boolean reqLesserImgCompression) {
            this.mCurrentPhotoPathfilename = amCurrentPhotoPathfilename;
            this.areqLesserImgCompression = reqLesserImgCompression;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean asd = false;
            try {
                Uri picUri = fileuri;
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                if (!areqLesserImgCompression) {
                    bmOptions.inMutable = true;
                    //resizing
                    //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
                    //      you try the use the bitmap here, you will get null.
                    bmOptions.inJustDecodeBounds = true;
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPathfilename, bmOptions);
                    int actualHeight = bmOptions.outHeight;
                    int actualWidth = bmOptions.outWidth;

                    /*//      max Height and width values of the compressed image is taken as 816x612
                    float maxHeight = 816.0f;
                    float maxWidth = 612.0f;
                    float imgRatio = (float) actualWidth / (float) actualHeight;
                    float maxRatio = maxWidth / maxHeight;

                    //      width and height values are set maintaining the aspect ratio of the image
                    if (actualHeight > maxHeight || actualWidth > maxWidth) {
                        if (imgRatio < maxRatio) {
                            imgRatio = maxHeight / actualHeight;
                            actualWidth = (int) (imgRatio * actualWidth);
                            actualHeight = (int) maxHeight;
                        } else if (imgRatio > maxRatio) {
                            imgRatio = maxWidth / actualWidth;
                            actualHeight = (int) (imgRatio * actualHeight);
                            actualWidth = (int) maxWidth;
                        } else {
                            actualHeight = (int) maxHeight;
                            actualWidth = (int) maxWidth;

                        }
                    }

                    ////      setting inSampleSize value allows to load a scaled down version of the original image
                    bmOptions.inSampleSize = calculateInSampleSize(bmOptions, actualWidth, actualHeight, areqLesserImgCompression);
                    ////      inJustDecodeBounds set to false to load the actual bitmap
                    bmOptions.inJustDecodeBounds = false;*/


                    int maxwidth = 1280;
                    if (actualWidth < maxwidth) {
                        //do nothing
                        ////      setting inSampleSize value allows to load a scaled down version of the original image
                        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, actualWidth, actualHeight, areqLesserImgCompression);
                        ////      inJustDecodeBounds set to false to load the actual bitmap
                        bmOptions.inJustDecodeBounds = false;

                        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPathfilename, bmOptions);
                    } else {
                        float aspectRatio = (float) actualHeight / actualWidth;
                        int newWidth = maxwidth;
                        int newHeight = Math.round(maxwidth * aspectRatio);
                        bmOptions.inJustDecodeBounds = false;
                        Bitmap originalBitmap = BitmapFactory.decodeFile(mCurrentPhotoPathfilename, bmOptions);
                        // Step 4: Create scaled bitmap
                        bitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

                    }

                } else {
                    bmOptions.inMutable = true;
                    //resizing
                    //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
                    //      you try the use the bitmap here, you will get null.
                    bmOptions.inJustDecodeBounds = true;
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPathfilename, bmOptions);
                    int actualHeight = bmOptions.outHeight;
                    int actualWidth = bmOptions.outWidth;

                    ////      setting inSampleSize value allows to load a scaled down version of the original image
                    bmOptions.inSampleSize = calculateInSampleSize(bmOptions, actualWidth, actualHeight, areqLesserImgCompression);
                    ////      inJustDecodeBounds set to false to load the actual bitmap
                    bmOptions.inJustDecodeBounds = false;

                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPathfilename, bmOptions);
                }

                //bitmap = BitmapFactory.decodeFile(mCurrentPhotoPathfilename, bmOptions);

                ////resizing
                if (bitmap != null) {
                    bitmap = rotateImageIfRequired(bitmap, picUri, mCurrentPhotoPathfilename);
                }

                if (bitmap != null) {
                    bitmap = returnTimeStampedBM2(bitmap, areqLesserImgCompression);
                }

                asd = true;
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at Camera5Activity processImg doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                doInbackgroundExceptionMsg = "Exception at Camera5Activity processImg doInBackground. Exception: " + e.getMessage() + "\nStack: " + s;
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                    pd = null;
                }

                if (success) {
                    preview = null;
                    if (preview == null)
                        preview = (ImageView) findViewById(R.id.preview);
                    if (lblphototakentext == null)
                        lblphototakentext = (TextView) findViewById(R.id.lblphototakentext);
                    if (bitmap != null) {
                        lblphototakentext.setVisibility(View.GONE);
                        preview.setVisibility(View.VISIBLE);
                        preview.setImageBitmap(bitmap);
                    } else {
                        lblphototakentext.setVisibility(View.VISIBLE);
                        preview.setVisibility(View.GONE);
                    }

                    //btnOK.setEnabled(true);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnOK);
                    //btnOKClick(null);
                } else {
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    bitmap = null;
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnOK);
                    if (GcmPreferences == null) {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                    }
                    UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                    username = UsernamePwd[0];
                    useridx = UsernamePwd[2];
                    Mail m = new Mail("luke@xdel.com", "XDel2015");
                    objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "",
                            "There was a problem grabbing the photo: Camera5Activity processImg returned false." + (!objGlobalMethods.IsEmptyString(doInbackgroundExceptionMsg) ? "\n\ndoInbackgroundExceptionMsg: " + doInbackgroundExceptionMsg : "")
                            , "There was a problem grabbing the photo by " + username + ", useridx: " + useridx + ".", null);
                    sendmail.execute();
                    objGlobalMethods.writeToFile("There was a problem grabbing the photo. Driver: " + username, "", "", username, useridx);
                }
            } catch (Exception e) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                bitmap = null;
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
                username = UsernamePwd[0];
                useridx = UsernamePwd[2];
                String stack = JobsHandler.getStackTraceString(e);
                //Mail m = new Mail("xdelsingapore66@gmail.com", "12321qwE");
                Mail m = new Mail("luke@xdel.com", "XDel2015");
                objGlobalMethods.sendEmail sendmail = new objGlobalMethods.sendEmail(m, "",
                        "There was a problem grabbing the photo : " + e.getMessage() + "\n\nStack: " + stack + (!objGlobalMethods.IsEmptyString(doInbackgroundExceptionMsg) ? "\n\ndoInbackgroundExceptionMsg: " + doInbackgroundExceptionMsg : "")
                        , "There was a problem grabbing the photo by " + username + ", useridx: " + useridx + ".", null);
                sendmail.execute();

                objGlobalMethods.writeToFile("There was a problem grabbing the photo. Driver: " + username,
                        "", "", username, useridx);

                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnOK);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            //if (resultCode == RESULT_OK && requestCode == ACTION_TAKE_PHOTO_B) {
            if (resultCode == RESULT_OK && requestCode == 1) {
                switch (requestCode) {
                    //case ACTION_TAKE_PHOTO_B: {
                    case 1: {
                        if (resultCode == RESULT_OK) {
                            if (mCurrentPhotoPath == null) {
                                GcmPreferences = objGlobalMethods.getGcmPreferences();
                                mCurrentPhotoPath = objGlobalMethods.getGCMPrefs(GcmPreferences, "mCurrentPhotoPath");
                            }
                            boolean reqLesserImgCompression = objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "reqLesserImgCompression")) ? false : true;
                            processImg pi = new processImg(mCurrentPhotoPath, reqLesserImgCompression);
                            pi.execute();
                            //setPic5(data);
                            ////handleBigCameraPhoto();
                        }
                        break;
                    } // ACTION_TAKE_PHOTO_B
                } // switch
            } else if (resultCode == RESULT_CANCELED) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                if (mCurrentPhotoPath == null || objGlobalMethods.IsEmptyString(mCurrentPhotoPath)) {
                    mCurrentPhotoPath = objGlobalMethods.getGCMPrefs(GcmPreferences, "mCurrentPhotoPath ");
                }
                objGlobalMethods.deleteFile(mCurrentPhotoPath);
                mCurrentPhotoPath = null;
                Intent parentIntent = null;
                catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
                if (catType == 0) {
                    if (CheckListActivity.openCamera != null) {
                        CheckListActivity.openCamera = null;
                        CheckListActivity.itemIndex = null;
                        parentIntent = new Intent(thisAct, CheckListActivity.class);
                    } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                        parentIntent = new Intent(thisAct, JobInfoActivity.class);
                    } else {
                        parentIntent = new Intent(thisAct, setFailStatusActivity.class);
                    }
                    //parentIntent = new Intent(this, setFailStatusActivity.class);
                } else if (catType == 2) {

                    if (CheckListActivity.openCamera != null) {
                        CheckListActivity.openCamera = null;
                        CheckListActivity.itemIndex = null;
                        parentIntent = new Intent(thisAct, CheckListActivity.class);
                    } else if (setDeliveredStatusActivity.isFromSetOKP) {
                        setDeliveredStatusActivity.isFromSetOKP = false;
                        parentIntent = new Intent(thisAct, setDeliveredStatusActivity.class);
                    } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                        setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                        parentIntent = new Intent(thisAct, setDeliveredMassStatusActivity.class);
                    } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                        setDunningDeliveredStatusActivity.isFromSetDunningOKP = false;
                        parentIntent = new Intent(thisAct, setDunningDeliveredStatusActivity.class);
                    } else if (!objGlobalMethods.IsEmptyString(objGlobalMethods.getGCMPrefs(GcmPreferences, "skiplocker"))) {
                        parentIntent = new Intent(thisAct, AuthActivity.class);
                    } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                        parentIntent = new Intent(thisAct, JobInfoActivity.class);
                    } else {
                        parentIntent = new Intent(thisAct, setFailStatusActivity.class);
                    }
                } else if (catType == -1) {
                    LeaveApplicationActivity.isFromLeaveApplication = false;
                    parentIntent = new Intent(thisAct, LeaveApplicationActivity.class);
                }

                objGlobalMethods.writeToFile("Camera5Activity resultCode == RESULT_CANCELED catType = " + catType + ", rtnChecklistVal = " + rtnChecklistVal() + ", PageMode = " + rtnPageMode(), "", "", "", "");

                MainActivity.uponLogin = 0;
                if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    JobInfoActivity.accessFromDirection = 1;
                    tempLoggerEntryDialog.setLaunchCamFromTempLoggerEntry(false);
                    startActivity(parentIntent);
                    this.finish();
                } else {
                    startActivity(parentIntent);
                    this.finish();
                }
                /*startActivity(parentIntent);
                this.finish();*/
            } else {
                objGlobalMethods.writeToFile("Camera5Activity resultCode == " + resultCode + " requestcode == " + requestCode + " catType = " + catType + ", rtnChecklistVal = " + rtnChecklistVal() + ", PageMode = " + rtnPageMode(), "", "", "", "");
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
            outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity onSaveInstanceState. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
            preview.setImageBitmap(mImageBitmap);
            preview.setVisibility(
                    savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                            ImageView.VISIBLE : ImageView.INVISIBLE
            );
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity onRestoreInstanceState. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera5, menu);
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            fileuri = null;
            if (mImageBitmap != null && !mImageBitmap.isRecycled()) {
                mImageBitmap.recycle();
                mImageBitmap = null;
            }
            mAlbumStorageDirFactory = null;

            if (preview != null) {
                preview.setImageBitmap(null);
                preview.setImageDrawable(null);
                preview = null;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Camera5Activity onDestroy. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }
}

