package com.xdel.luke;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xdel.luke.HRM_Leave.LeaveApplicationActivity;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.temploggerclass;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;

public class CustomGallery2Activity extends Activity {

    GridView gridGallery;
    Handler handler;
    CustomGalleryAdapter adapter;
    ImageView imgNoMedia;
    Button btnGalleryOk;
    Button btnGalleryCX;
    String action;
    private ImageLoader imageLoader;
    SharedPreferences GcmPreferences;
    int catType;
    public static Looper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        myApp.currentActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //disable taking screen shot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.gallery_grid);

        action = getIntent().getAction();
        if (action == null) {
            finish();
        }
        initImageLoader();
        init();
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
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();

            File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
                    CACHE_DIR);

            /*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                    getBaseContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .discCache(new UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());

            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);*/


            //better alternatives from claude ai
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .discCacheSize(50 * 1024 * 1024)        // 50MB disk cache
                    .discCacheFileCount(100)                // Max 100 files
                    .memoryCacheSize(2 * 1024 * 1024)       // 2MB memory cache
                    .build();

            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CustomGallery2Activity.\nStack: " + s,"","","","");
        }
    }

    private void init() {

        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new CustomGalleryAdapter(myApp.getAppContext(), imageLoader);
        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
                true, true);
        gridGallery.setOnScrollListener(listener);

        if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {

            findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
            gridGallery.setOnItemClickListener(mItemMulClickListener);
            adapter.setMultiplePick(true);

        } else if (action.equalsIgnoreCase(Action.ACTION_PICK)) {

            findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
            gridGallery.setOnItemClickListener(mItemSingleClickListener);
            adapter.setMultiplePick(false);

        }

        gridGallery.setAdapter(adapter);
        imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

        btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
        btnGalleryOk.setOnClickListener(mOkClickListener);

        btnGalleryCX = (Button) findViewById(R.id.btnGalleryCX);
        btnGalleryCX.setOnClickListener(mCXClickListener);

        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
        //adapter.addAll(getGalleryPhotos());
        adapter.addAll(isCallLog.equals("t") ? getGalleryPhotos() : getAppSpecificFilesGalleryPhotos());
        checkImageStatus();
        adapter.notifyDataSetChanged();

        /*new Thread() {

            @Override
            public void run() {
                myLooper = Looper.myLooper();
                //Looper.prepare();
                myLooper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.addAll(getGalleryPhotos());
                        checkImageStatus();
                        adapter.notifyDataSetChanged();
                    }
                });
                //Looper.loop();
                myLooper.loop();
            }
        }.start();*/

    }

    private void checkImageStatus() {
        if (adapter.isEmpty()) {
            imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            imgNoMedia.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        gridGallery = null;
        handler = null;
        adapter = null;
        imgNoMedia = null;
        btnGalleryOk = null;
        btnGalleryCX = null;
        action = null;
        imageLoader = null;
        GcmPreferences = null;
        if (myLooper != null) {
            myLooper.quit();
            myLooper = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        /*Intent parentIntent = null;
        parentIntent = new Intent(CustomGallery2Activity.this, CustomGalleryActivity.class);
        MainActivity.uponLogin = 0;
        startActivity(parentIntent);
        finish();*/
        back();
    }

    private void back(){
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
        if (isCallLog.equals("t")) {
            Camera5Activity.mCurrentPhotoPath = null;
            Intent parentIntent = new Intent(CustomGallery2Activity.this, setDeliveredStatusActivity.isFromSetOKP ? setDeliveredStatusActivity.class : setFailStatusActivity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CustomGallery2Activity.class) {
                myApp.currentActivity.finish();
            } else {
                finish();
            }
        } else if (LeaveApplicationActivity.isFromLeaveApplication) {
            Camera5Activity.mCurrentPhotoPath = null;
            Intent parentIntent = new Intent(CustomGallery2Activity.this, LeaveApplicationActivity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CustomGallery2Activity.class) {
                myApp.currentActivity.finish();
            } else {
                finish();
            }
        } else {
            Intent parentIntent = null;
            parentIntent = new Intent(CustomGallery2Activity.this, CustomGalleryActivity.class);
            MainActivity.uponLogin = 0;
            startActivity(parentIntent);
            if (myApp.currentActivity != null && myApp.currentActivity.getClass() == CustomGallery2Activity.class) {
                myApp.currentActivity.finish();
            } else {
                finish();
            }
        }
    }

    View.OnClickListener mCXClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            back();
        }
    };

    View.OnClickListener mOkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ArrayList<CustomGallery> selected = adapter.getSelected();

            String[] allPath = new String[selected.size()];
            for (int i = 0; i < allPath.length; i++) {
                allPath[i] = selected.get(i).sdcardPath;
            }

            Intent data = new Intent().putExtra("all_path", allPath);
            setResult(RESULT_OK, data);
            finish();

        }
    };
    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            int count = 0;
            int imagePathscount = 0;
            int currCount = 0;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
            boolean isSameDay = false;
            Context ctx = myApp.getAppContext();
            int Xminutes = 10;
            try {
                CustomGallery o = (CustomGallery) l.getItemAtPosition(position);
                File img = new File(o.sdcardPath);
                if (img.exists()) {
                    long imgCreatedTimeLong = img.lastModified();
                    if (imgCreatedTimeLong > 0) {
                        isSameDay = JobsHandler.isSameDay(imgCreatedTimeLong, null);
                    }
                }
                if (!isSameDay && !o.isSeleted) {
                    /*showDialog(ctx.getString(R.string.unabletousephoto0) + "\n" + ctx.getString(R.string.unabletousephoto1) + " " + Xminutes + ctx.getString(R.string.unabletousephoto2));*/
                    showDialog(ctx.getString(R.string.unabletousephoto0) + "\n\n" + ctx.getString(R.string.unabletousephoto3));
                } else {
                    if (CheckListActivity.itemIndex != null && CheckListActivity.itemIndex > -1) {
                        adapter.changeSelection(v, position);
                        count = adapter.getSelectedCount();
                        if (count > 1) {
                            adapter.changeSelection(v, position);
                            showDialog(ctx.getString(R.string.alert_errorrselectmax1pic));
                        }
                    } else if (setFailStatusActivity.isFromSetFailSatus) {
                        if (isCallLog.equals("t")) {
                            adapter.changeSelection(v, position);
                        } else if (setFailStatusActivity.imagePaths != null && setFailStatusActivity.imagePaths.size() > 0) {
                            adapter.changeSelection(v, position);

                            imagePathscount = setFailStatusActivity.imagePaths.size();
                            int callLogImageCount = 0;
                            if (setFailStatusActivity.dataT != null && setFailStatusActivity.dataT.size() > 0) {
                                for (int i = 0; i <= setFailStatusActivity.dataT.size() - 1; i++) {
                                    callLogImageCount += setFailStatusActivity.dataT.get(i).isCallLog ? 1 : 0;
                                }
                            }
                            count = adapter.getSelectedCount();
                            currCount = imagePathscount + count;

                            if (currCount - callLogImageCount > 6) {
                                adapter.changeSelection(v, position);
                                showDialog(ctx.getString(R.string.alert_errorrselectmax6pic));
                            }

                        /*if (currCount > 6) {
                            adapter.changeSelection(v, position);
                            showDialog(myApp.getAppContext().getString(R.string.alert_errorrselectmax6pic));
                        }*/
                        }
                    } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                        if (setDeliveredMassStatusActivity.imagePaths != null && setDeliveredMassStatusActivity.imagePaths.size() > 0) {
                            adapter.changeSelection(v, position);

                            imagePathscount = setDeliveredMassStatusActivity.imagePaths.size();
                            count = adapter.getSelectedCount();
                            currCount = imagePathscount + count;

                            if (currCount > 6) {
                                adapter.changeSelection(v, position);
                                showDialog(ctx.getString(R.string.alert_errorrselectmax6pic));
                            }
                        }
                    } else if (setDeliveredStatusActivity.isFromSetOKP) {
                        if (isCallLog.equals("t")) {
                            adapter.changeSelection(v, position);
                        } else if (setDeliveredStatusActivity.imagePaths != null && setDeliveredStatusActivity.imagePaths.size() > 0) {
                            adapter.changeSelection(v, position);

                            imagePathscount = setDeliveredStatusActivity.imagePaths.size();
                            int callLogImageCount = 0;
                            if (setDeliveredStatusActivity.dataT != null && setDeliveredStatusActivity.dataT.size() > 0) {
                                for (int i = 0; i <= setDeliveredStatusActivity.dataT.size() - 1; i++) {
                                    callLogImageCount += setDeliveredStatusActivity.dataT.get(i).isCallLog ? 1 : 0;
                                }
                            }
                            count = adapter.getSelectedCount();
                            currCount = imagePathscount + count;

                            if (currCount - callLogImageCount > 6) {
                                adapter.changeSelection(v, position);
                                showDialog(ctx.getString(R.string.alert_errorrselectmax6pic));
                            }

                        /*if (currCount > 6) {
                            adapter.changeSelection(v, position);
                            showDialog(myApp.getAppContext().getString(R.string.alert_errorrselectmax6pic));
                        }*/
                        }
                    } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                        if (setDunningDeliveredStatusActivity.imagePaths != null && setDunningDeliveredStatusActivity.imagePaths.size() > 0) {
                            adapter.changeSelection(v, position);

                            imagePathscount = setDunningDeliveredStatusActivity.imagePaths.size();
                            count = adapter.getSelectedCount();
                            currCount = imagePathscount + count;

                            if (currCount > 6) {
                                adapter.changeSelection(v, position);
                                showDialog(ctx.getString(R.string.alert_errorrselectmax6pic));
                            }
                        }
                    } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                        if (LeaveApplicationActivity.imagePaths != null && LeaveApplicationActivity.imagePaths.size() > 0) {
                            adapter.changeSelection(v, position);

                            imagePathscount = LeaveApplicationActivity.imagePaths.size();
                            count = adapter.getSelectedCount();
                            currCount = imagePathscount + count;

                            if (currCount > 6) {
                                adapter.changeSelection(v, position);
                                showDialog(ctx.getString(R.string.alert_errorrselectmax6pic));
                            }
                        } else {
                            adapter.changeSelection(v, position);
                        }
                    } else {
                        if (CustomGalleryActivity.imagePaths != null && CustomGalleryActivity.imagePaths.size() > 0) {
                            adapter.changeSelection(v, position);

                            imagePathscount = CustomGalleryActivity.imagePaths.size();
                            count = adapter.getSelectedCount();
                            currCount = imagePathscount + count;

                            if (currCount > 6) {
                                adapter.changeSelection(v, position);
                                showDialog(ctx.getString(R.string.alert_errorrselectmax6pic));
                            }
                        }
                    }
                    ///////////////////////
                }

            } catch (Exception e) {
                //String stack = JobsHandler.getStackTraceString(e);
                //objGlobalMethods.writeToFile("Exception caught at CustomGallery2Activity mItemMulClickListner. " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
                objGlobalMethods.writeToFile("Exception caught at CustomGallery2Activity mItemMulClickListner. " + e.getMessage().toString(), "", "", "", "");
            }

            //adapter.changeSelection(v, position);

        }
    };

    AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            CustomGallery item = adapter.getItem(position);
            Intent data = new Intent().putExtra("single_path", item.sdcardPath);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    private ArrayList<CustomGallery> getAppSpecificFilesGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
        AlbumStorageDirFactory mAlbumStorageDirFactory = null;
        try {
            ////below 2 lines of codes for app specific folder, but have issues on some devices suspected unable to read/write from app specific folder
            //String dirPath = getFilesDir().getAbsolutePath() + File.separator + getString(R.string.appspecificfilefolder_image); //app specific - same location per preferenes & sqlite, cannot find via normal file system
            //File folder = new File(dirPath);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }
            File folder = mAlbumStorageDirFactory.getAlbumStorageDir(myApp.getAppContext().getString(R.string.appspecificfilefolder_image));
            File[] allFiles = null;
            if (folder.exists()) {
                allFiles = folder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
                    }
                });

                if (allFiles != null && allFiles.length > 0){
                    for (int i = 0; i <= allFiles.length-1;i++) {
                        CustomGallery item = new CustomGallery();
                        item.sdcardPath = allFiles[i].getAbsolutePath();
                        if (setFailStatusActivity.isFromSetFailSatus) {
                            if (setFailStatusActivity.imagePaths != null && setFailStatusActivity.imagePaths.size() > 0) {
                                if (!setFailStatusActivity.imagePaths.contains(item.sdcardPath)) {
                                    galleryList.add(item);
                                }
                            } else if (isCallLog.equals("t")) {
                                galleryList.add(item);
                            }
                        } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                            if (setDeliveredMassStatusActivity.imagePaths != null && setDeliveredMassStatusActivity.imagePaths.size() > 0) {
                                if (!setDeliveredMassStatusActivity.imagePaths.contains(item.sdcardPath)) {
                                    galleryList.add(item);
                                }
                            }
                        } else if (setDeliveredStatusActivity.isFromSetOKP) {
                            if (setDeliveredStatusActivity.imagePaths != null && setDeliveredStatusActivity.imagePaths.size() > 0) {
                                if (!setDeliveredStatusActivity.imagePaths.contains(item.sdcardPath)) {
                                    galleryList.add(item);
                                }
                            } else if (isCallLog.equals("t")) {
                                galleryList.add(item);
                            }
                        } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                            if (setDunningDeliveredStatusActivity.imagePaths != null && setDunningDeliveredStatusActivity.imagePaths.size() > 0) {
                                if (!setDunningDeliveredStatusActivity.imagePaths.contains(item.sdcardPath)) {
                                    galleryList.add(item);
                                }
                            }
                        } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                            if (LeaveApplicationActivity.imagePaths != null && LeaveApplicationActivity.imagePaths.size() > 0) {
                                if (!LeaveApplicationActivity.imagePaths.contains(item.sdcardPath)) {
                                    galleryList.add(item);
                                }
                            } else {
                                galleryList.add(item);
                            }
                        } else {
                            if (CustomGalleryActivity.imagePaths != null && CustomGalleryActivity.imagePaths.size() > 0) {
                                if (!CustomGalleryActivity.imagePaths.contains(item.sdcardPath)) {
                                    galleryList.add(item);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at CustomGallery2Activity getAppSpecificFilesGalleryPhotos. " + e.getMessage().toString(), "", "", "", "");
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

    private ArrayList<CustomGallery> getGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        String isCallLog = objGlobalMethods.getGCMPrefs(GcmPreferences, "isCallLog");
        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    CustomGallery item = new CustomGallery();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.sdcardPath = imagecursor.getString(dataColumnIndex);

                    if (setFailStatusActivity.isFromSetFailSatus) {
                        if (setFailStatusActivity.imagePaths != null && setFailStatusActivity.imagePaths.size() > 0) {
                            if (!setFailStatusActivity.imagePaths.contains(item.sdcardPath)) {
                                galleryList.add(item);
                            }
                        } else if (isCallLog.equals("t")) {
                            galleryList.add(item);
                        }
                    } else if (setDeliveredMassStatusActivity.isFromMassSetOKP) {
                        if (setDeliveredMassStatusActivity.imagePaths != null && setDeliveredMassStatusActivity.imagePaths.size() > 0) {
                            if (!setDeliveredMassStatusActivity.imagePaths.contains(item.sdcardPath)) {
                                galleryList.add(item);
                            }
                        }
                    } else if (setDeliveredStatusActivity.isFromSetOKP) {
                        if (setDeliveredStatusActivity.imagePaths != null && setDeliveredStatusActivity.imagePaths.size() > 0) {
                            if (!setDeliveredStatusActivity.imagePaths.contains(item.sdcardPath)) {
                                galleryList.add(item);
                            }
                        } else if (isCallLog.equals("t")) {
                            galleryList.add(item);
                        }
                    } else if (setDunningDeliveredStatusActivity.isFromSetDunningOKP) {
                        if (setDunningDeliveredStatusActivity.imagePaths != null && setDunningDeliveredStatusActivity.imagePaths.size() > 0) {
                            if (!setDunningDeliveredStatusActivity.imagePaths.contains(item.sdcardPath)) {
                                galleryList.add(item);
                            }
                        }
                    } else if (LeaveApplicationActivity.isFromLeaveApplication) {
                        if (LeaveApplicationActivity.imagePaths != null && LeaveApplicationActivity.imagePaths.size() > 0) {
                            if (!LeaveApplicationActivity.imagePaths.contains(item.sdcardPath)) {
                                galleryList.add(item);
                            }
                        } else {
                            galleryList.add(item);
                        }
                    } else {
                        if (CustomGalleryActivity.imagePaths != null && CustomGalleryActivity.imagePaths.size() > 0) {
                            if (!CustomGalleryActivity.imagePaths.contains(item.sdcardPath)) {
                                galleryList.add(item);
                            }
                        }
                    }

                    /*if (CustomGalleryActivity.imagePaths != null && CustomGalleryActivity.imagePaths.size() > 0){
                        if (!CustomGalleryActivity.imagePaths.contains(item.sdcardPath)){
                            galleryList.add(item);
                        }
                    }*/
                    //galleryList.add(item);
                }
            }
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception caught at CustomGallery2Activity getGalleryPhotos. " + e.getMessage().toString() + "\nStack: " + stack,"","","","");
            objGlobalMethods.writeToFile("Exception caught at CustomGallery2Activity getGalleryPhotos. " + e.getMessage().toString(), "", "", "", "");
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_custom_gallery2, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at CustomGallery2Activity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at CustomerGallery2Activity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
