package com.xdel.luke;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xdel.luke.BottomSheet.tempLoggerEntryDialog;
import com.xdel.luke.HRM_Leave.LeaveApplicationActivity;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.temploggerclass;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class CustomGalleryActivity extends Activity {

    private int catType;
    private SharedPreferences GcmPreferences;
    Context context;
    private Uri imageUri;
    GridView gridGallery;
    Handler handler;
    CustomGalleryAdapter adapter;
    public static ArrayList<String> imagePaths;
    public static ArrayList<CustomGallery> dataT;
    ImageView imgSinglePick;
    Button btnGalleryPick;
    Button btnGalleryPickMul;
    ViewSwitcher viewSwitcher;
    ImageLoader imageLoader;

    String langType = "en";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        myApp.currentActivity = this;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_custom_gallery);


        initImageLoader();
        init();

        int CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
        ctrlSettings.enableDisableCommonButtonWithStyles(CannotTakePhoto > 0 ? false : true, btnGalleryPickMul);
        String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
        /*String isItemProof = objGlobalMethods.getGCMPrefs(GcmPreferences, "isItemProof");
        if (!objGlobalMethods.IsEmptyString(isCallLog) || !objGlobalMethods.IsEmptyString(isItemProof)) {
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnGalleryPickMul);
        }*/
        if (!objGlobalMethods.IsEmptyString(isCallLog)) {
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnGalleryPickMul);
        }

        if (setFailStatusActivity.isFromSetFailSatus) {
            //String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
            if (isCallLog.equals("t")) {
                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, 200);
            } else {
                if (setFailStatusActivity.dataT == null && setFailStatusActivity.imagePaths == null) {
                    if (Camera5Activity.mCurrentPhotoPath != null && !Camera5Activity.mCurrentPhotoPath.equals("")) {
                        setFailStatusActivity.dataT = new ArrayList<CustomGallery>();
                        CustomGallery item = new CustomGallery();
                        item.sdcardPath = Camera5Activity.mCurrentPhotoPath;
                        setFailStatusActivity.imagePaths = new ArrayList<String>();
                        setFailStatusActivity.imagePaths.add(Camera5Activity.mCurrentPhotoPath);
                        setFailStatusActivity.dataT.add(item);

                        viewSwitcher.setDisplayedChild(0);
                        adapter.addAll(setFailStatusActivity.dataT);
                    } else {
                        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                        startActivityForResult(i, 200);
                    }
                } else {
                    viewSwitcher.setDisplayedChild(0);
                    adapter.addAll(setFailStatusActivity.dataT);
                }
            }
        } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
            if (setDeliveredMassStatusActivity.dataT == null && setDeliveredMassStatusActivity.imagePaths == null) {
                if (Camera5Activity.mCurrentPhotoPath != null && !Camera5Activity.mCurrentPhotoPath.equals("")) {
                    setDeliveredMassStatusActivity.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = Camera5Activity.mCurrentPhotoPath;
                    setDeliveredMassStatusActivity.imagePaths = new ArrayList<String>();
                    setDeliveredMassStatusActivity.imagePaths.add(Camera5Activity.mCurrentPhotoPath);
                    setDeliveredMassStatusActivity.dataT.add(item);

                    viewSwitcher.setDisplayedChild(0);
                    adapter.addAll(setDeliveredMassStatusActivity.dataT);
                } else {
                    Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                    startActivityForResult(i, 200);
                }
            } else {
                viewSwitcher.setDisplayedChild(0);
                adapter.addAll(setDeliveredMassStatusActivity.dataT);
            }
        } else if (setDeliveredStatusActivity.isFromSetOKP) {
            //String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
            if (isCallLog.equals("t")) {
                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, 200);
            } else {
                if (setDeliveredStatusActivity.dataT == null && setDeliveredStatusActivity.imagePaths == null) {
                    if (Camera5Activity.mCurrentPhotoPath != null && !Camera5Activity.mCurrentPhotoPath.equals("")) {
                        setDeliveredStatusActivity.dataT = new ArrayList<CustomGallery>();
                        CustomGallery item = new CustomGallery();
                        item.sdcardPath = Camera5Activity.mCurrentPhotoPath;
                        setDeliveredStatusActivity.imagePaths = new ArrayList<String>();
                        setDeliveredStatusActivity.imagePaths.add(Camera5Activity.mCurrentPhotoPath);
                        setDeliveredStatusActivity.dataT.add(item);

                        viewSwitcher.setDisplayedChild(0);
                        adapter.addAll(setDeliveredStatusActivity.dataT);
                    } else {
                        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                        startActivityForResult(i, 200);
                    }
                } else {
                    viewSwitcher.setDisplayedChild(0);
                    adapter.addAll(setDeliveredStatusActivity.dataT);
                }
            }
        } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
            if (setDunningDeliveredStatusActivity.dataT == null && setDunningDeliveredStatusActivity.imagePaths == null) {
                if (Camera5Activity.mCurrentPhotoPath != null && !Camera5Activity.mCurrentPhotoPath.equals("")) {
                    setDunningDeliveredStatusActivity.dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = Camera5Activity.mCurrentPhotoPath;
                    setDunningDeliveredStatusActivity.imagePaths = new ArrayList<String>();
                    setDunningDeliveredStatusActivity.imagePaths.add(Camera5Activity.mCurrentPhotoPath);
                    setDunningDeliveredStatusActivity.dataT.add(item);

                    viewSwitcher.setDisplayedChild(0);
                    adapter.addAll(setDunningDeliveredStatusActivity.dataT);
                } else {
                    Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                    startActivityForResult(i, 200);
                }
            } else {
                viewSwitcher.setDisplayedChild(0);
                adapter.addAll(setDunningDeliveredStatusActivity.dataT);
            }
        } else if (LeaveApplicationActivity.isFromLeaveApplication) {
            if (LeaveApplicationActivity.dataT == null && LeaveApplicationActivity.imagePaths == null) {
                Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                startActivityForResult(i, 200);
            } else {
                viewSwitcher.setDisplayedChild(0);
                adapter.addAll(LeaveApplicationActivity.dataT);
            }
        } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
            viewSwitcher.setDisplayedChild(0);
            adapter.addAll(tempLoggerEntryDialog.dataT);
        } else {
            if (dataT == null && imagePaths == null) {
                if (Camera5Activity.mCurrentPhotoPath != null && !Camera5Activity.mCurrentPhotoPath.equals("")) {
                    dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = Camera5Activity.mCurrentPhotoPath;
                    imagePaths = new ArrayList<String>();
                    imagePaths.add(Camera5Activity.mCurrentPhotoPath);
                    dataT.add(item);

                    viewSwitcher.setDisplayedChild(0);
                    adapter.addAll(dataT);
                } /*else if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1 && CheckListActivity.al != null && CheckListActivity.al.size() > 0
                        && CheckListActivity.al.size() - 1 >= CheckListActivity.itemIndex && CheckListActivity.al.get(CheckListActivity.itemIndex).PhotoImagePath != null
                        && !CheckListActivity.al.get(CheckListActivity.itemIndex).PhotoImagePath.equals("")) {*/ else if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1 && CheckListActivity.al != null && CheckListActivity.al.size() > 0
                        && CheckListActivity.al.size() - 1 >= CheckListActivity.itemIndex && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT != null
                        && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.size() > 0) {
                    /*dataT = new ArrayList<CustomGallery>();
                    CustomGallery item = new CustomGallery();
                    item.sdcardPath = CheckListActivity.al.get(CheckListActivity.itemIndex).PhotoImagePath;
                    imagePaths = new ArrayList<String>();
                    imagePaths.add(CheckListActivity.al.get(CheckListActivity.itemIndex).PhotoImagePath);
                    dataT.add(item);*/

                    dataT = new ArrayList<CustomGallery>();
                    //dataT.add(CheckListActivity.al.get(CheckListActivity.itemIndex).dataT);
                    dataT = CheckListActivity.al.get(CheckListActivity.itemIndex).dataT;
                    imagePaths = new ArrayList<String>();
                    for (int t = 0; t <= CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.size() - 1; t++) {
                        if (!objGlobalMethods.IsEmptyString(CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.get(t).sdcardPath)) {
                            imagePaths.add(CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.get(t).sdcardPath);
                        }
                    }
                    viewSwitcher.setDisplayedChild(0);
                    adapter.addAll(dataT);
                    //ctrlSettings.enableDisableCommonButtonWithStyles(false, btnGalleryPickMul);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnGalleryPickMul);
                } else {
                    Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                    startActivityForResult(i, 200);
                }
            } else {
                viewSwitcher.setDisplayedChild(0);
                adapter.addAll(dataT);
            }
        }

        Camera5Activity.mCurrentPhotoPath = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GcmPreferences = getGcmPreferences();
        int CannotTakePhoto = objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto").equals("") ? 0 : Integer.parseInt(objGlobalMethods.getGCMPrefs(GcmPreferences, "CannotTakePhoto"));
        ctrlSettings.enableDisableCommonButtonWithStyles(CannotTakePhoto > 0 ? false : true, btnGalleryPickMul);
        String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");

        /*if (!objGlobalMethods.IsEmptyString(isCallLog) || !objGlobalMethods.IsEmptyString(isItemProof)
                || (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1 && CheckListActivity.al != null && CheckListActivity.al.size() > 0
                && CheckListActivity.al.size() - 1 >= CheckListActivity.itemIndex && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT != null
                && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.size() > 0)) {
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnGalleryPickMul);
        }*/
        /*String isItemProof = objGlobalMethods.getGCMPrefs(GcmPreferences, "isItemProof");
        if (!objGlobalMethods.IsEmptyString(isCallLog) || !objGlobalMethods.IsEmptyString(isItemProof)) {
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnGalleryPickMul);
        }*/
        if (!objGlobalMethods.IsEmptyString(isCallLog)) {
            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnGalleryPickMul);
        }
    }

    private Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    private void initImageLoader() {
        // for universal image loader
        /*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(false).cacheInMemory(false).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);*/

        //better alternatives from claude ai
        try {
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .defaultDisplayImageOptions(defaultOptions)
                    .discCacheSize(50 * 1024 * 1024)        // 50MB disk cache
                    .discCacheFileCount(100)                // Max 100 files
                    .memoryCacheSize(2 * 1024 * 1024)       // 2MB memory cache
                    .build();

            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CustomGalleryActivity.\nStack: " + s,"","","","");
        }
    }

    private void init() {
        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new CustomGalleryAdapter(getApplicationContext(), imageLoader);
        adapter.setMultiplePick(false);
        gridGallery.setAdapter(adapter);

        gridGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent ix = new Intent(CustomGalleryActivity.this, ViewImageActivity.class);
                if (setFailStatusActivity.isFromSetFailSatus) {
                    ix.putExtra("imgPath", setFailStatusActivity.imagePaths.get(i)); // String, Bundle value
                } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                    ix.putExtra("imgPath", setDeliveredMassStatusActivity.imagePaths.get(i)); // String, Bundle value
                } else if (setDeliveredStatusActivity.isFromSetOKP) {
                    ix.putExtra("imgPath", setDeliveredStatusActivity.imagePaths.get(i)); // String, Bundle value
                } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                    ix.putExtra("imgPath", setDunningDeliveredStatusActivity.imagePaths.get(i)); // String, Bundle value
                } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                    ix.putExtra("imgPath", LeaveApplicationActivity.imagePaths.get(i)); // String, Bundle value
                } else if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1 && CheckListActivity.al != null && CheckListActivity.al.size() > 0
                        && CheckListActivity.al.size() - 1 >= CheckListActivity.itemIndex && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT != null
                        && CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.size() > 0) {
                    if (i >= 0 && i <= CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.size() - 1) {
                        ix.putExtra("imgPath", CheckListActivity.al.get(CheckListActivity.itemIndex).dataT.get(i).sdcardPath); // String, Bundle value
                    }
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    ix.putExtra("imgPath", tempLoggerEntryDialog.imagePaths.get(i)); // String, Bundle value
                } else {
                    ix.putExtra("imgPath", imagePaths.get(i)); // String, Bundle value
                }
                ix.putExtra("imgPathIDX", String.valueOf(i));
                startActivity(ix);
            }
        });

        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        viewSwitcher.setDisplayedChild(1);

        imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);
        imgSinglePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagePaths != null)// if minimum image is choose
                    Toast.makeText(CustomGalleryActivity.this, "" + imagePaths.get(0), Toast.LENGTH_LONG).show();
            }
        });

        btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
        btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Manifest recognize our multiple request by this way
                if (setFailStatusActivity.isFromSetFailSatus) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
                    if (isCallLog.equals("t")) {
                        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                        startActivityForResult(i, 200);
                    } else {
                        int callLogImageCount = 0;
                        if (setFailStatusActivity.dataT != null && setFailStatusActivity.dataT.size() > 0) {
                            for (int i = 0; i <= setFailStatusActivity.dataT.size() - 1; i++) {
                                callLogImageCount += setFailStatusActivity.dataT.get(i).isCallLog ? 1 : 0;
                            }
                        }

                        if (adapter != null && adapter.getCount() - callLogImageCount == 6) {
                            showDialog(myApp.getAppContext().getString(R.string.alert_errorrselectmax6pic));
                        } else {
                            Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                            startActivityForResult(i, 200);
                        }
                    }
                } else if (setDeliveredStatusActivity.isFromSetOKP) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
                    if (isCallLog.equals("t")) {
                        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                        startActivityForResult(i, 200);
                    } else {
                        int callLogImageCount = 0;
                        if (setDeliveredStatusActivity.dataT != null && setDeliveredStatusActivity.dataT.size() > 0) {
                            for (int i = 0; i <= setDeliveredStatusActivity.dataT.size() - 1; i++) {
                                callLogImageCount += setDeliveredStatusActivity.dataT.get(i).isCallLog ? 1 : 0;
                            }
                        }

                        if (adapter != null && adapter.getCount() - callLogImageCount == 6) {
                            showDialog(myApp.getAppContext().getString(R.string.alert_errorrselectmax6pic));
                        } else {
                            Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                            startActivityForResult(i, 200);
                        }
                    }
                } else {
                    if (adapter != null && adapter.getCount() == 6) {
                        showDialog(myApp.getAppContext().getString(R.string.alert_errorrselectmax6pic));
                    } else {
                        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                        startActivityForResult(i, 200);
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                objGlobalMethods.writeToFile("Bluetooth is on at CustomGalleryActivity","","","","");
                Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                temploggerclass.disposeTimer();
                temploggerclass.startTimer(5000);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imagePaths = new ArrayList<String>();
            adapter.clear();

            viewSwitcher.setDisplayedChild(1);
            String single_path = data.getStringExtra("single_path");
            imagePaths.add(single_path);
            imageLoader.displayImage("file://" + single_path, imgSinglePick);

        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            String[] all_path = data.getStringArrayExtra("all_path");
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
            String isItemProof = objGlobalMethods.getGCMPrefs(GcmPreferences, "isItemProof");

            if (setFailStatusActivity.isFromSetFailSatus && setFailStatusActivity.dataT == null && setFailStatusActivity.imagePaths == null) {
                setFailStatusActivity.dataT = new ArrayList<CustomGallery>();
                setFailStatusActivity.imagePaths = new ArrayList<String>();
            } else if (setDeliveredMassStatusActivity.isFromMassSetOKP && setDeliveredMassStatusActivity.dataT == null && setDeliveredMassStatusActivity.imagePaths == null) {
                setDeliveredMassStatusActivity.dataT = new ArrayList<CustomGallery>();
                setDeliveredMassStatusActivity.imagePaths = new ArrayList<String>();
            } else if (setDeliveredStatusActivity.isFromSetOKP && setDeliveredStatusActivity.dataT == null && setDeliveredStatusActivity.imagePaths == null) {
                setDeliveredStatusActivity.dataT = new ArrayList<CustomGallery>();
                setDeliveredStatusActivity.imagePaths = new ArrayList<String>();
            } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP && setDunningDeliveredStatusActivity.dataT == null && setDunningDeliveredStatusActivity.imagePaths == null) {
                setDunningDeliveredStatusActivity.dataT = new ArrayList<CustomGallery>();
                setDunningDeliveredStatusActivity.imagePaths = new ArrayList<String>();
            } else if (LeaveApplicationActivity.isFromLeaveApplication && LeaveApplicationActivity.dataT == null && LeaveApplicationActivity.imagePaths == null) {
                LeaveApplicationActivity.dataT = new ArrayList<CustomGallery>();
                LeaveApplicationActivity.imagePaths = new ArrayList<String>();
            } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                tempLoggerEntryDialog.dataT = new ArrayList<>();
                tempLoggerEntryDialog.imagePaths = new ArrayList<>();
            } else {
                if (dataT == null && imagePaths == null) {
                    dataT = new ArrayList<CustomGallery>();
                    imagePaths = new ArrayList<String>();
                }
            }

            for (String string : all_path) {
                CustomGallery item = new CustomGallery();
                item.sdcardPath = string;
                item.setCallLog(isCallLog.equals("t") ? true : false);
                item.setItemProof(isItemProof.equals("t") ? true : false);
                if (setFailStatusActivity.isFromSetFailSatus) {
                    setFailStatusActivity.imagePaths.add(string);
                    setFailStatusActivity.dataT.add(item);
                } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                    setDeliveredMassStatusActivity.imagePaths.add(string);
                    setDeliveredMassStatusActivity.dataT.add(item);
                } else if (setDeliveredStatusActivity.isFromSetOKP) {
                    setDeliveredStatusActivity.imagePaths.add(string);
                    setDeliveredStatusActivity.dataT.add(item);
                } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                    setDunningDeliveredStatusActivity.imagePaths.add(string);
                    setDunningDeliveredStatusActivity.dataT.add(item);
                } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                    LeaveApplicationActivity.imagePaths.add(string);
                    LeaveApplicationActivity.dataT.add(item);
                } else if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1 && CheckListActivity.al != null && CheckListActivity.al.size() - 1 >= CheckListActivity.itemIndex) {
                    imagePaths.add(string);
                    item.setPhotodesc(isItemProof.equals("t") && !objGlobalMethods.IsEmptyString(CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText)
                            ? CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText : !objGlobalMethods.IsEmptyString(CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText)
                            ? CheckListActivity.al.get(CheckListActivity.itemIndex).DisplayText : "");
                    dataT.add(item);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    tempLoggerEntryDialog.imagePaths.add(string);
                    tempLoggerEntryDialog.dataT.add(item);
                } else {
                    imagePaths.add(string);
                    dataT.add(item);
                }
            }

            viewSwitcher.setDisplayedChild(0);
            if (setFailStatusActivity.isFromSetFailSatus) {
                adapter.addAll(setFailStatusActivity.dataT);
            } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                adapter.addAll(setDeliveredMassStatusActivity.dataT);
            } else if (setDeliveredStatusActivity.isFromSetOKP) {
                adapter.addAll(setDeliveredStatusActivity.dataT);
            } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                adapter.addAll(setDunningDeliveredStatusActivity.dataT);
            } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                adapter.addAll(LeaveApplicationActivity.dataT);
            } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                adapter.addAll(tempLoggerEntryDialog.dataT);
            } else {
                adapter.addAll(dataT);
            }

            if (isCallLog.equals("t")) {
                btnOKClick(null);
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (setFailStatusActivity.isFromSetFailSatus) {
                setFailStatusActivity.dataT = null;
                setFailStatusActivity.imagePaths = null;
            } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                setDeliveredMassStatusActivity.dataT = null;
                setDeliveredMassStatusActivity.imagePaths = null;
            } else if (setDeliveredStatusActivity.isFromSetOKP) {
                setDeliveredStatusActivity.dataT = null;
                setDeliveredStatusActivity.imagePaths = null;
            } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                setDunningDeliveredStatusActivity.dataT = null;
                setDunningDeliveredStatusActivity.imagePaths = null;
            } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                LeaveApplicationActivity.dataT = null;
                LeaveApplicationActivity.imagePaths = null;
            } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                tempLoggerEntryDialog.dataT = null;
                tempLoggerEntryDialog.imagePaths = null;
            } else {
                dataT = null;
                imagePaths = null;
            }
        }
    }

    @Override
    protected void onStop() {
        //imageLoader.stop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        imageLoader.stop();
        imageLoader = null;
        imagePaths = null;
        dataT = null;
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        try {
            GcmPreferences = getGcmPreferences();
            Intent parentIntent = null;
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            if (catType == 0) {
                if (CheckListActivity.openCamera != null) {
                    CheckListActivity.openCamera = null;
                    CheckListActivity.itemIndex = null;
                    parentIntent = new Intent(this, CheckListActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    parentIntent = new Intent(this, JobInfoActivity.class);
                }  else {
                    parentIntent = new Intent(this, setFailStatusActivity.class);
                }
                //parentIntent = new Intent(this, setFailStatusActivity.class);
            } else if (catType == 2) {
                if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1) {
                    CheckListActivity.openCamera = null;
                    CheckListActivity.itemIndex = null;
                    parentIntent = new Intent(this, CheckListActivity.class);
                } else if (setDeliveredStatusActivity.isFromSetOKP) {
                    setDeliveredStatusActivity.isFromSetOKP = false;
                    parentIntent = new Intent(this, setDeliveredStatusActivity.class);
                } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                    setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                    parentIntent = new Intent(this, setDeliveredMassStatusActivity.class);
                } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                    setDunningDeliveredStatusActivity.isFromSetDunningOKP = false;
                    parentIntent = new Intent(this, setDunningDeliveredStatusActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    parentIntent = new Intent(this, JobInfoActivity.class);
                }  else {
                    parentIntent = new Intent(this, setFailStatusActivity.class);
                }
            } else if (catType == -1) {
                LeaveApplicationActivity.isFromLeaveApplication = false;
                parentIntent = new Intent(this, LeaveApplicationActivity.class);
            }
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
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at CustomGalleryActivity onBackPressed. Exception: " + s, "", "", "", "");
        }
    }

    public void btnCancelClick(View view) {
        try {
            Camera5Activity.mCurrentPhotoPath = null;
            if (setFailStatusActivity.isFromSetFailSatus) {
                setFailStatusActivity.imagePaths = null;
                setFailStatusActivity.dataT = null;
            } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                setDeliveredMassStatusActivity.imagePaths = null;
                setDeliveredMassStatusActivity.dataT = null;
            } else if (setDeliveredStatusActivity.isFromSetOKP) {
                setDeliveredStatusActivity.imagePaths = null;
                setDeliveredStatusActivity.dataT = null;
            } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                setDunningDeliveredStatusActivity.imagePaths = null;
                setDunningDeliveredStatusActivity.dataT = null;
            } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                LeaveApplicationActivity.imagePaths = null;
                LeaveApplicationActivity.dataT = null;
            } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry)  {
                tempLoggerEntryDialog.imagePaths = null;
                tempLoggerEntryDialog.dataT = null;
            } else {
                imagePaths = null;
                dataT = null;
            }

            GcmPreferences = getGcmPreferences();
            Intent parentIntent = null;
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            if (catType == 0) {
                if (CheckListActivity.openCamera != null) {
                    CheckListActivity.openCamera = null;
                    CheckListActivity.itemIndex = null;
                    parentIntent = new Intent(this, CheckListActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    /*JobInfoActivity.accessFromDirection = 1;
                    tempLoggerEntryDialog.setLaunchCamFromTempLoggerEntry(false);*/
                    parentIntent = new Intent(this, JobInfoActivity.class);
                }   else {
                    parentIntent = new Intent(this, setFailStatusActivity.class);
                }
                //parentIntent = new Intent(this, setFailStatusActivity.class);
            } else if (catType == 2) {
                if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1) {
                    CheckListActivity.openCamera = null;
                    CheckListActivity.itemIndex = null;
                    parentIntent = new Intent(this, CheckListActivity.class);
                } else if (setDeliveredStatusActivity.isFromSetOKP) {
                    setDeliveredStatusActivity.isFromSetOKP = false;
                    parentIntent = new Intent(this, setDeliveredStatusActivity.class);
                } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                    setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                    parentIntent = new Intent(this, setDeliveredMassStatusActivity.class);
                } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                    setDunningDeliveredStatusActivity.isFromSetDunningOKP = false;
                    parentIntent = new Intent(this, setDunningDeliveredStatusActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    /*JobInfoActivity.accessFromDirection = 1;
                    tempLoggerEntryDialog.setLaunchCamFromTempLoggerEntry(false);*/
                    parentIntent = new Intent(this, JobInfoActivity.class);
                }   else {
                    parentIntent = new Intent(this, setFailStatusActivity.class);
                }
            } else if (catType == -1) {
                LeaveApplicationActivity.isFromLeaveApplication = false;
                parentIntent = new Intent(this, LeaveApplicationActivity.class);
            }
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
            imagePaths = null;
            dataT = null;
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at CustomGalleryActivity btnCancelClick. Exception: " + s, "", "", "", "");
        }
    }

    public void btnOKClick(View view) {
        try {
            GcmPreferences = getGcmPreferences();
            Intent parentIntent = null;
            catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
            if (catType == 0) {
                if (CheckListActivity.openCamera != null) {
                    CheckListActivity.openCamera = null;
                    CheckListActivity.itemIndex = null;
                    parentIntent = new Intent(this, CheckListActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    /*JobInfoActivity.accessFromDirection = 1;
                    tempLoggerEntryDialog.setLaunchCamFromTempLoggerEntry(false);*/
                    parentIntent = new Intent(this, JobInfoActivity.class);
                }   else {
                    parentIntent = new Intent(this, setFailStatusActivity.class);
                }
                //parentIntent = new Intent(this, setFailStatusActivity.class);
            } else if (catType == 2) {
                if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1) {
                    CheckListActivity.itemIndex = null;
                    CheckListActivity.openCamera = null;
                    parentIntent = new Intent(this, CheckListActivity.class);
                } else if (setDeliveredStatusActivity.isFromSetOKP) {
                    setDeliveredStatusActivity.isFromSetOKP = false;
                    parentIntent = new Intent(this, setDeliveredStatusActivity.class);
                } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                    setDeliveredMassStatusActivity.isFromMassSetOKP = false;
                    parentIntent = new Intent(this, setDeliveredMassStatusActivity.class);
                } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                    setDunningDeliveredStatusActivity.isFromSetDunningOKP = false;
                    parentIntent = new Intent(this, setDunningDeliveredStatusActivity.class);
                } else if (tempLoggerEntryDialog.launchCamFromTempLoggerEntry != null && tempLoggerEntryDialog.launchCamFromTempLoggerEntry) {
                    /*JobInfoActivity.accessFromDirection = 1;
                    tempLoggerEntryDialog.setLaunchCamFromTempLoggerEntry(false);*/
                    parentIntent = new Intent(this, JobInfoActivity.class);
                }   else {
                    parentIntent = new Intent(this, setFailStatusActivity.class);
                }
            } else if (catType == -1) {
                LeaveApplicationActivity.isFromLeaveApplication = false;
                parentIntent = new Intent(this, LeaveApplicationActivity.class);
            }
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
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at CustomGalleryActivity btnOKClick. Exception: " + s, "", "", "", "");
        }
    }
}

