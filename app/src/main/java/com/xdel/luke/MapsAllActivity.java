package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.ui.IconGenerator;
import com.xdel.luke.BCnSVC.SendQueuedEmail_Receiver;
import com.xdel.luke.BottomSheet.BottomSheetMapLegendFragment;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.Vrp.JobQueue;
import com.xdel.luke.mpd.mpdclass;
import com.xdel.luke.objClasses.CustomInfoWindowGoogleMap;
import com.xdel.luke.objClasses.HttpConnection;
import com.xdel.luke.objClasses.LocationProvider;
import com.xdel.luke.objClasses.PathJSONParser;
import com.xdel.luke.objClasses.RouteLocation;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.objMapJob;
import com.xdel.luke.sqlite.objRouteJobEarliestTiming;
import com.xdel.luke.templogger.temploggerclass;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.ITALIC;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class MapsAllActivity extends FragmentActivity implements LocationProvider.LocationCallback, OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    double lat, lng, currentLatitude, currentLongitude;
    private LocationProvider mLocationProvider;
    LatLng deslatlng, currLatLng;
    String directionURL = "";
    Context context;
    ConnectionDetector cd;
    TextView lblIsDefaultVRPpage;

    private SharedPreferences GcmPreferences;
    private RouteLocation[] RouteLocationAL;
    private ProgressDialog pgLoading, pgProcessing;
    private HashMap<Marker, JobQueue> MarkerMap;
    private HashMap<Marker, RouteLocation> MarkerMapRL;
    private ArrayList<String> ProcessedPostals;
    ScreenShotContentObserver screenShotContentObserver;
    FloatingActionButton fabWaze;
    //LinearLayout llhelp, llwaze;

    private void initPG() {
        try {
            if (pgLoading == null) {
                pgLoading = new ProgressDialog(this);
                pgLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pgLoading.setCanceledOnTouchOutside(false);
                pgLoading.setMessage(myApp.getAppContext().getString(R.string.pd_wait_loading));
            }

            if (pgProcessing == null) {
                pgProcessing = new ProgressDialog(this);
                pgProcessing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pgProcessing.setCanceledOnTouchOutside(false);
                pgProcessing.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity initPG. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_all);

        try {
            mLocationProvider = new LocationProvider(this, this);
            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
            //llhelp = findViewById(R.id.llhelp);
            //llwaze = findViewById(R.id.llwaze);
            fabWaze = (FloatingActionButton) findViewById(R.id.fabWaze);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    BottomSheetMapLegendFragment bs = new BottomSheetMapLegendFragment();
                    bs.show(getSupportFragmentManager(), bs.getTag());
                    //mMap.clear();
                }
            });

            lblIsDefaultVRPpage = findViewById(R.id.lblIsDefaultVRPpage);

        /*FloatingActionButton fab_recal = (FloatingActionButton) findViewById(R.id.fab_recal);
        fab_recal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = myApp.getAppContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                builder.setCancelable(false);
                builder.setTitle(ctx.getString(R.string.map_recal_title));
                builder.setMessage(ctx.getString(R.string.map_recal_cfm));

                builder.setNegativeButton(ctx.getString(R.string.buttontext_NO2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton(ctx.getString(R.string.buttontext_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recalroute r = new recalroute();
                        r.execute();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity onCreate. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)) {
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            context = getApplicationContext();

            String[] prefName = new String[]{"FailAsND", "FailAsND_Reason"};
            String[] prefvalue = new String[]{"", ""};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

            lblIsDefaultVRPpage.setText(objGlobalMethods.IsDefaultVRPpage() ? "t" : "");

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("MapsAllActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }


            initPG();

            cd = new ConnectionDetector(null);
            JobsHandler.clearlAllDetailPagesStatic();
            JobsHandler.AddRemoveClearObjCallback(GcmPreferences, "", "", true);

            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);

            int qe = JobsHandler.numOfQueuedEmails();
            if (qe > 0) {
                objGlobalMethods.writeToFile("SendQueuedEmailReceiver called at MapsAllActivity.", "", "", "", "");
                Intent SendQueuedEmailReceiver = new Intent(context, SendQueuedEmail_Receiver.class);
                sendBroadcast(SendQueuedEmailReceiver);
            }

                /*mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                mMap.getUiSettings().setScrollGesturesEnabled(true);
                mMap.getUiSettings().setTiltGesturesEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mLocationProvider.connect();
                setUpMapIfNeeded();
                promptReqMPD();*/
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at MapsAllActivity onResume. Exception: " + s, "", "", "", "");
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (ContextCompat.checkSelfPermission(myApp.getAppContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (mMap != null) {
                mMap.setOnInfoWindowCloseListener(null);
            }
            if (mLocationProvider == null) {
                mLocationProvider = new LocationProvider(this, this);
            }
            mLocationProvider.connect();
            setUpMap2();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MapsAllActivity onMapReady. Exception: " + s, "", "", "", "");
        }
    }

    public void promptReqMPD() {
        /*objGlobalMethods.promptReqMPD();
        mpdclass.promptRegBTTL();*/
        objGlobalMethods.promptReqMPD_New();
    }

    @Override
    public void onBackPressed() {
        DashboardSubActivity.frompage = null;
        //Intent intent = new Intent(MapsAllActivity.this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent intent = new Intent(MapsAllActivity.this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        startActivity(intent);
        finish();
    }

    private boolean isVrpView() {
        return !objGlobalMethods.IsEmptyString(lblIsDefaultVRPpage == null ? "" : lblIsDefaultVRPpage.getText().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            ProcessedPostals = null;
            if (pgLoading != null) {
                if (pgLoading.isShowing()) {
                    pgLoading.dismiss();
                }
            }
            pgLoading = null;

            if (pgProcessing != null) {
                if (pgProcessing.isShowing()) {
                    pgProcessing.dismiss();
                }
            }
            pgProcessing = null;

            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at MapsAllActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
            }
            DAO_Jobs.nullifystaticitems();
            cd = null;
            GcmPreferences = null;
            myApp.currentActivity = null;
            if (mLocationProvider != null) {
                mLocationProvider.disconnect();
                mLocationProvider = null;
            }
            if (mMap != null) {
                mMap.clear();
                mMap = null;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity onPause. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            GcmPreferences = null;
            if (pgLoading != null) {
                if (pgLoading.isShowing()) {
                    pgLoading.dismiss();
                }
            }
            pgLoading = null;

            if (pgProcessing != null) {
                if (pgProcessing.isShowing()) {
                    pgProcessing.dismiss();
                }
            }
            pgProcessing = null;

            try {
                if (screenShotContentObserver != null)
                    getContentResolver().unregisterContentObserver(screenShotContentObserver);
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at MapsAllActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
            }
            DAO_Jobs.nullifystaticitems();
            cd = null;
            if (mLocationProvider != null) {
                mLocationProvider.disconnect();
                mLocationProvider = null;
            }
            if (mMap != null) {
                mMap.clear();
                mMap = null;
            }
            if (MarkerMap != null) {
                MarkerMap.clear();
                MarkerMap = null;
            }
            if (MarkerMapRL != null) {
                MarkerMapRL.clear();
                MarkerMapRL = null;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity onDestroy. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private String getRouteListJsonStr() {
        String JsonStr = "";
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            JsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "RouteList");
        } catch (Exception e) {
            JsonStr = "";
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity getRouteListJsonStr. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return JsonStr;
        }
    }

    private void showErrorAlert(String errMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        if (!errMsg.equals("")) {
            builder.setMessage(errMsg);
        } else {
            builder.setMessage("Invalid Address.");
        }

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                Intent i;
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                i = new Intent(MapsAllActivity.this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                JobInfoActivity.accessFromDirection = 0;
                MainActivity.uponLogin = 0;
                DrawerMainActivity.DashboardOpenFromPush = false;
                startActivity(i);
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setUpMap2() {
        try {
            //if (!objGlobalMethods.IsDefaultVRPpage()) {
            List<JobQueue> NewJQlist = JobsHandler.GetProcessJobQue6();
            boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
            boolean isVrpMode = isVrpView() && !IsMaintenanceHours;
            boolean hasOpsQueBool = JobsHandler.hasOpsQ() && !IsMaintenanceHours;
            boolean isWalker = JobsHandler2.isWalker(GcmPreferences);
            boolean isStdRunner = JobsHandler2.isStdRunner(GcmPreferences);
            boolean isSatSun = false;
            boolean isAOHtime = false;
            if (!isWalker && isStdRunner) {
                isSatSun = JobsHandler2.isSatSun();
                isAOHtime = JobsHandler2.isAOHtime();
            }
            if (!isVrpMode) {
                if (hasOpsQueBool) {
                    JobsHandler.redirectToVrpPage();
                } else {
                    // Do a null check to confirm that we have not already instantiated the map.

                    if (mMap != null) {
                        mMap.clear();
                        RouteLocationAL = null;
                        String RouteListJsonStr = getRouteListJsonStr();

                        if (!RouteListJsonStr.equals("") && !RouteListJsonStr.equals("[]")) {
                            Gson gson = new Gson();
                            RouteLocationAL = gson.fromJson(RouteListJsonStr, RouteLocation[].class);
                            //setUpMap2SQLiteCustomIcons(RouteLocationAL);
                            loadMapFirstTime();
                            promptReqMPD();
                            objGlobalMethods.showNewPIPdialog(objGlobalMethods.getGcmPreferences());
                            if (mMap != null) {
                                mMap.setOnInfoWindowCloseListener(mapInfoWinCloseListener());
                            }

                            if (!IsMaintenanceHours &&
                                    (JobsHandler.hasOpsQ() || objGlobalMethods.IsDefaultVRPpage() || objGlobalMethods.isVRPbySettings() || (!isWalker && isStdRunner && isSatSun) || (!isWalker && isStdRunner && !isSatSun && isAOHtime))) {
                                if (isStdRunner && isSatSun || (isStdRunner && !isSatSun && isAOHtime)) {
                                    JobsHandler.redirectToVrpPage2();
                                } else {
                                    JobsHandler.redirectToVrpPage();
                                }
                            }

                        } else {
                            objGlobalMethods.writeToFile("RouteListJsonStr is empty at MapsAllActivity.", "", "", "", "");
                            showErrorAlert("");
                        }
                    }
                }
            } else {
                setUpMap2SQLiteByJobQue(NewJQlist);
                setUpMarkerClick();
                promptReqMPD();
                objGlobalMethods.showNewPIPdialog(objGlobalMethods.getGcmPreferences());
                if (mMap != null) {
                    mMap.setOnInfoWindowCloseListener(mapInfoWinCloseListener());
                }
                if ((isWalker && !hasOpsQueBool) || (isStdRunner && !isSatSun && !isAOHtime && !hasOpsQueBool)) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "pagemode", "dashboard");
                    if (isWalker) {
                        objGlobalMethods.writeToFile("Switched to Summary Mode from MapsAllActivity due isWalker.", "", "", "", "");
                    } else {
                        objGlobalMethods.writeToFile("Switched to Summary Mode from MapsAllActivity due isStdRunner && !isSatSun.", "", "", "", "");
                    }
                    Intent i = new Intent(myApp.currentActivity, DrawerMainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpmap2. Exception: " + s, "", "", "", "");
        }
    }

    public Bitmap bitmapSizeByScall(Bitmap bitmapIn, float scall_zero_to_one_f) {

        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);

        return bitmapOut;
    }

    private boolean hasOpsQue(List<JobQueue> NewJQlist) {
        boolean asd = false;
        JobQueue jq = null;
        try {
            if (NewJQlist != null && NewJQlist.size() > 0) {
                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    if (jq.OPS_Queue != null && jq.OPS_Queue > 0) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {

        } finally {
            return asd;
        }
    }

    public void setUpMap2SQLiteByJobQueNew(List<JobQueue> preppedNewJQlist) {
        Context ctx = myApp.getAppContext();
        Context ctx2 = (Context) myApp.currentActivity;
        List<JobQueue> NewJQlist = null;
        List<JobQueue> First_3_JobQueue = null;
        JobQueue jq = null;
        double LATval = 0;
        double LNGval = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        RouteLocation rl = null;
        int count = 0;
        int index = 0;
        int TotalPIPDIPJobsCountPerPostal = 0;
        String manualDefineIndexStr = "";
        Boolean hasPUJobsIDXList = false;
        Boolean hasDLJobsIDXList = false;
        boolean hasOpsQue = false;
        String MapViewedPostal = objGlobalMethods.getGCMPrefs(GcmPreferences, "MapViewedPostal");
        boolean windowOpened = false;
        Marker mk0 = null;
        String jobsIDXs = "";
        objRouteJobEarliestTiming rjet = null;
        String timing = ctx.getString(R.string.clickviewdetails);
        int jobscount = 0;
        String[] JobsIDXsPerPostalStrArr;
        Long[] JobsIDXsPerPostal = null;
        boolean hasOpsQueBool = false;
        boolean QueisUpdated = false;
        boolean VRP_StartEarly = false;
        boolean isBefore935am = false;

        try {
            pgLoading.show();
            if (mMap != null)
                mMap.clear();

            NewJQlist = preppedNewJQlist == null ? JobsHandler.GetProcessJobQue6() : preppedNewJQlist;
            //NewJQlist = JobsHandler.GetProcessJobQue6();
            if (NewJQlist != null && NewJQlist.size() > 0) {
                QueisUpdated = JobsHandler.QueisUpdated();
                hasOpsQueBool = hasOpsQue(NewJQlist);
                VRP_StartEarly = JobsHandler2.VRP_StartEarly(GcmPreferences);
                isBefore935am = JobsHandler2.isBefore1005am();
                if (VRP_StartEarly && isBefore935am && !hasOpsQueBool) {
                    JobsHandler.releaseAllQue(NewJQlist);
                    //hasOpsQueBool = true;
                }

                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    if (!jq.getIsHeader()) {
                        TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostalWithJobsIDXs(jq.getPostalCode(), jq.getJobsIDXs());
                        if (TotalPIPDIPJobsCountPerPostal > 0) {
                            jobscount = 0;
                            jobsIDXs = "";
                            jobsIDXs = jq.getJobsIDXs();
                            JobsIDXsPerPostalStrArr = jobsIDXs.split(",");
                            if (JobsIDXsPerPostalStrArr != null && JobsIDXsPerPostalStrArr.length > 0) {
                                JobsIDXsPerPostal = new Long[JobsIDXsPerPostalStrArr.length];
                                for (int p = 0; p <= JobsIDXsPerPostalStrArr.length - 1; p++) {
                                    JobsIDXsPerPostal[p] = Long.parseLong(JobsIDXsPerPostalStrArr[p]);
                                }
                            }
                            jobscount = jq.JobsCount != null ? jq.JobsCount : 0;
                            rjet = null;
                            timing = ctx.getString(R.string.clickviewdetails);
                            if (!objGlobalMethods.IsEmptyString(jobsIDXs) && jobscount == 1) {
                                rjet = DAO_Jobs.getobjRouteJobEarliestTiming(jobsIDXs);
                            }

                            if (rjet != null && rjet.ExpectedTime != null && !rjet.ExpectedTime.isEmpty() && !rjet.ExpectedTime.equals("")) {
                                try {
                                    if (rjet.StatusCodeIDX == 103 && rjet.ReadyDateTime.equals(rjet.PickByDateTime)) {
                                        timing = "\n" + ctx.getString(R.string.rdycol) + "\n" + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ReadyDateTime), "dd/MM h:mma");
                                    } else if (rjet.StatusCodeIDX == 103 && !rjet.ReadyDateTime.equals(rjet.PickByDateTime)) {
                                        timing = "\n" + ctx.getString(R.string.colbetween) + "\n"
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ReadyDateTime), "dd/MM h:mma")
                                                + " - "
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.PickByDateTime), "dd/MM h:mma");
                                    } else if (rjet.StatusCodeIDX == 104) {
                                        /*timing = ctx.getString(R.string.deliverytime) + "\n"
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.FromDateTime), "dd/MM h:mma")
                                                + " - "
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ToDateTime), "dd/MM h:mma")
                                                + (!objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !rjet.ExtDateTime.equals("0") ? "\n" + ctx.getString(R.string.extended)
                                                + " " + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma") : "");*/

                                        timing += "\n" + ctx.getString(R.string.deliverytime) + "\n"
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.FromDateTime), "dd/MM h:mma")
                                                + " - "
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ToDateTime), "dd/MM h:mma")
                                                + (
                                                jobscount > 1 ?
                                                        "\n" + ctx.getString(R.string.clickforext)
                                                        :
                                                        !objGlobalMethods.IsEmptyString(rjet.ExtFromDateTime) && !rjet.ExtFromDateTime.equals("0") && !rjet.ExtFromDateTime.equals(rjet.ToDateTime) ?
                                                                "\n" + ctx.getString(R.string.lblor)
                                                                        + "\n"
                                                                        + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtFromDateTime), "dd/MM h:mma")
                                                                        + " - "
                                                                        + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma") :
                                                                !objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !rjet.ExtDateTime.equals("0") ?
                                                                        "\n" + ctx.getString(R.string.extended)
                                                                                + "\n" + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma")
                                                                        : "");

                                    }
                                    //timing = objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExpectedTime), "dd/MM h:mma");
                                } catch (Exception e) {
                                    String ss = JobsHandler.getStackTraceString(e);
                                    objGlobalMethods.writeToFile("Exception at MapsAllActivity get earliest timing. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                                }
                            }

                            if (!hasOpsQue && jq.getType().equals("ops")) {
                                hasOpsQue = true;
                            }
                            LATval = jq.getLatitude();
                            LNGval = jq.getLongitude();
                            if (LATval != 0 && LNGval != 0) {
                                deslatlng = new LatLng(LATval, LNGval);
                                IconGenerator iconFactory = new IconGenerator(this);
                                iconFactory.setStyle(jq.getType().equals("ops") ? IconGenerator.STYLE_ORANGE :
                                        (jq.getType().equals("vrp") && jq.isUpdated) ? IconGenerator.STYLE_BLUE : IconGenerator.STYLE_DEFAULT);

                                /*index += 1;
                                Bitmap icon = iconFactory.makeIcon(!manualDefineIndexStr.equals("") ? manualDefineIndexStr : String.valueOf(index));*/
                                manualDefineIndexStr = "";
                                /*if (hasOpsQue) {
                                    index = jq.OPS_Queue != null ? jq.OPS_Queue : (index + 1);
                                } else if (jq.VRP_Queue < 1000) {
                                    index = jq.VRP_Queue;
                                } else {
                                    index = index + 1;
                                }*/

                                if (index < 0)
                                    index++;
                                index++;

                                Bitmap icon = iconFactory.makeIcon(!manualDefineIndexStr.equals("") ? manualDefineIndexStr : String.valueOf(index));

                                MarkerOptions options = new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                                        .position(deslatlng)
                                        .title(jq.getAddressWithPostalOnly())
                                        .snippet(timing);

                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(ctx2);
                                mMap.setInfoWindowAdapter(customInfoWindow);

                                Marker mk = mMap.addMarker(options);
                                if (MarkerMapRL == null) {
                                    MarkerMapRL = new HashMap<Marker, RouteLocation>();
                                }
                                rl = new RouteLocation();
                                com.xdel.luke.objClasses.Location loc = new com.xdel.luke.objClasses.Location(jq.PostalCode, String.valueOf(LATval), String.valueOf(LNGval));
                                rl.setLocation(loc);
                                rl.setJobsIDXList(JobsIDXsPerPostal);
                                rl.setEnabledForJobUpdate(jq.IsEnabledForJobUpdate);
                                MarkerMapRL.put(mk, rl);
                                if (MarkerMap == null) {
                                    MarkerMap = new HashMap<Marker, JobQueue>();
                                }
                                MarkerMap.put(mk, jq);
                                if (count == 0) {
                                    mk0 = mk;
                                }

                                if (!windowOpened && !objGlobalMethods.IsEmptyString(MapViewedPostal) && MapViewedPostal.equals(jq.PostalCode)) {
                                    mk.showInfoWindow();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deslatlng, 12));
                                    windowOpened = true;
                                    LatLng adeslatlng = mk.getPosition();
                                    double lat = adeslatlng.latitude;
                                    double lng = adeslatlng.longitude;
                                    showfabWaze(String.valueOf(lat) + "," + String.valueOf(lng));
                                } else if (!windowOpened && i == NewJQlist.size() - 1 && mk0 != null) {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "MapViewedPostal", "");
                                    mk0.showInfoWindow();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mk0.getPosition(), 12));
                                    windowOpened = true;
                                    LatLng adeslatlng = mk0.getPosition();
                                    double lat = adeslatlng.latitude;
                                    double lng = adeslatlng.longitude;
                                    showfabWaze(String.valueOf(lat) + "," + String.valueOf(lng));
                                }
                            } else {
                                //do not show anything of lat lng 0,0. skip it for now
                            }
                            count += 1;
                        }
                    } else {
                        index--;
                    }
                }
            }

            pgLoading.dismiss();

            if (count == 0) {
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent parent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(parent);
                this.finish();
            } else {
                String nonSysCalRoute = objGlobalMethods.getGCMPrefs(GcmPreferences, "nonSysCalRoute");
                if (!nonSysCalRoute.equals("")) {
                    Toast.makeText(myApp.getAppContext(), myApp.getAppContext().getString(R.string.toastnoncalculatedroute), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            pgLoading.dismiss();
            objGlobalMethods.writeToFile("Exception caught at MapsAllActivity setUpMap2SQLiteByJobQueNew. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_invalidaddress));
        }
    }

    public void setUpMap2SQLiteByJobQue(List<JobQueue> preppedNewJQlist) {
        Context ctx = myApp.getAppContext();
        Context ctx2 = (Context) myApp.currentActivity;
        List<JobQueue> NewJQlist = null;
        List<JobQueue> First_3_JobQueue = null;
        JobQueue jq = null;
        double LATval = 0;
        double LNGval = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        RouteLocation rl = null;
        int count = 0;
        int index = 0;
        int TotalPIPDIPJobsCountPerPostal = 0;
        String manualDefineIndexStr = "";
        Boolean hasPUJobsIDXList = false;
        Boolean hasDLJobsIDXList = false;
        boolean hasOpsQue = false;
        String MapViewedPostal = objGlobalMethods.getGCMPrefs(GcmPreferences, "MapViewedPostal");
        boolean windowOpened = false;
        Marker mk0 = null;
        String jobsIDXs = "";
        objRouteJobEarliestTiming rjet = null;
        String timing = ctx.getString(R.string.clickviewdetails);
        int jobscount = 0;
        String[] JobsIDXsPerPostalStrArr;
        Long[] JobsIDXsPerPostal = null;
        boolean hasOpsQueBool = false;
        boolean QueisUpdated = false;
        boolean VRP_StartEarly = false;
        boolean isBefore935am = false;

        try {
            pgLoading.show();
            if (mMap != null)
                mMap.clear();

            NewJQlist = preppedNewJQlist == null ? JobsHandler.GetProcessJobQue6() : preppedNewJQlist;
            //NewJQlist = JobsHandler.GetProcessJobQue6();
            if (NewJQlist != null && NewJQlist.size() > 0) {
                QueisUpdated = JobsHandler.QueisUpdated();
                hasOpsQueBool = hasOpsQue(NewJQlist);
                VRP_StartEarly = JobsHandler2.VRP_StartEarly(GcmPreferences);
                isBefore935am = JobsHandler2.isBefore1005am();
                if (VRP_StartEarly && isBefore935am && !hasOpsQueBool) {
                    JobsHandler.releaseAllQue(NewJQlist);
                    //hasOpsQueBool = true;
                }

                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    if (!jq.getIsHeader()) {
                        TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostalWithJobsIDXs(jq.getPostalCode(), jq.getJobsIDXs());
                        if (TotalPIPDIPJobsCountPerPostal > 0) {
                            jobscount = 0;
                            jobsIDXs = "";
                            jobsIDXs = jq.getJobsIDXs();
                            JobsIDXsPerPostalStrArr = jobsIDXs.split(",");
                            if (JobsIDXsPerPostalStrArr != null && JobsIDXsPerPostalStrArr.length > 0) {
                                JobsIDXsPerPostal = new Long[JobsIDXsPerPostalStrArr.length];
                                for (int p = 0; p <= JobsIDXsPerPostalStrArr.length - 1; p++) {
                                    JobsIDXsPerPostal[p] = Long.parseLong(JobsIDXsPerPostalStrArr[p]);
                                }
                            }
                            jobscount = jq.JobsCount != null ? jq.JobsCount : 0;
                            rjet = null;
                            timing = ctx.getString(R.string.clickviewdetails);
                            if (!objGlobalMethods.IsEmptyString(jobsIDXs) && jobscount == 1) {
                                rjet = DAO_Jobs.getobjRouteJobEarliestTiming(jobsIDXs);
                            }

                            if (rjet != null && rjet.ExpectedTime != null && !rjet.ExpectedTime.isEmpty() && !rjet.ExpectedTime.equals("")) {
                                try {
                                    if (rjet.StatusCodeIDX == 103 && rjet.ReadyDateTime.equals(rjet.PickByDateTime)) {
                                        timing = "\n" + ctx.getString(R.string.rdycol) + "\n" + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ReadyDateTime), "dd/MM h:mma");
                                    } else if (rjet.StatusCodeIDX == 103 && !rjet.ReadyDateTime.equals(rjet.PickByDateTime)) {
                                        timing = "\n" + ctx.getString(R.string.colbetween) + "\n"
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ReadyDateTime), "dd/MM h:mma")
                                                + " - "
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.PickByDateTime), "dd/MM h:mma");
                                    } else if (rjet.StatusCodeIDX == 104) {
                                        /*timing = ctx.getString(R.string.deliverytime) + "\n"
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.FromDateTime), "dd/MM h:mma")
                                                + " - "
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ToDateTime), "dd/MM h:mma")
                                                + (!objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !rjet.ExtDateTime.equals("0") ? "\n" + ctx.getString(R.string.extended)
                                                + " " + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma") : "");*/

                                        timing += "\n" + ctx.getString(R.string.deliverytime) + "\n"
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.FromDateTime), "dd/MM h:mma")
                                                + " - "
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ToDateTime), "dd/MM h:mma")
                                                + (
                                                jobscount > 1 ?
                                                        "\n" + ctx.getString(R.string.clickforext)
                                                        :
                                                        !objGlobalMethods.IsEmptyString(rjet.ExtFromDateTime) && !rjet.ExtFromDateTime.equals("0") && !rjet.ExtFromDateTime.equals(rjet.ToDateTime) ?
                                                                "\n" + ctx.getString(R.string.lblor)
                                                                        + "\n"
                                                                        + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtFromDateTime), "dd/MM h:mma")
                                                                        + " - "
                                                                        + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma") :
                                                                !objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !rjet.ExtDateTime.equals("0") ?
                                                                        "\n" + ctx.getString(R.string.extended)
                                                                                + "\n" + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma")
                                                                        : "");

                                    }
                                    //timing = objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExpectedTime), "dd/MM h:mma");
                                } catch (Exception e) {
                                    String ss = JobsHandler.getStackTraceString(e);
                                    objGlobalMethods.writeToFile("Exception at MapsAllActivity get earliest timing. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                                }
                            }

                            if (!hasOpsQue && jq.getType().equals("ops")) {
                                hasOpsQue = true;
                            }
                            LATval = jq.getLatitude();
                            LNGval = jq.getLongitude();
                            if (LATval != 0 && LNGval != 0) {
                                deslatlng = new LatLng(LATval, LNGval);
                                IconGenerator iconFactory = new IconGenerator(this);
                                iconFactory.setStyle(jq.getType().equals("ops") ? IconGenerator.STYLE_ORANGE :
                                        (jq.getType().equals("vrp") && jq.isUpdated) ? IconGenerator.STYLE_BLUE : IconGenerator.STYLE_DEFAULT);

                                /*index += 1;
                                Bitmap icon = iconFactory.makeIcon(!manualDefineIndexStr.equals("") ? manualDefineIndexStr : String.valueOf(index));*/
                                manualDefineIndexStr = "";
                                /*if (hasOpsQue) {
                                    index = jq.OPS_Queue != null ? jq.OPS_Queue : (index + 1);
                                } else if (jq.VRP_Queue < 1000) {
                                    index = jq.VRP_Queue;
                                } else {
                                    index = index + 1;
                                }*/

                                if (index < 0)
                                    index++;
                                index++;

                                Bitmap icon = iconFactory.makeIcon(!manualDefineIndexStr.equals("") ? manualDefineIndexStr : String.valueOf(index));

                                MarkerOptions options = new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                                        .position(deslatlng)
                                        .title(jq.getAddressWithPostalOnly())
                                        .snippet(timing);

                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(ctx2);
                                mMap.setInfoWindowAdapter(customInfoWindow);

                                Marker mk = mMap.addMarker(options);
                                if (MarkerMapRL == null) {
                                    MarkerMapRL = new HashMap<Marker, RouteLocation>();
                                }
                                rl = new RouteLocation();
                                com.xdel.luke.objClasses.Location loc = new com.xdel.luke.objClasses.Location(jq.PostalCode, String.valueOf(LATval), String.valueOf(LNGval));
                                rl.setLocation(loc);
                                rl.setJobsIDXList(JobsIDXsPerPostal);
                                rl.setEnabledForJobUpdate(jq.IsEnabledForJobUpdate);
                                MarkerMapRL.put(mk, rl);
                                if (MarkerMap == null) {
                                    MarkerMap = new HashMap<Marker, JobQueue>();
                                }
                                MarkerMap.put(mk, jq);
                                if (count == 0) {
                                    mk0 = mk;
                                }

                                if (!windowOpened && !objGlobalMethods.IsEmptyString(MapViewedPostal) && MapViewedPostal.equals(jq.PostalCode)) {
                                    mk.showInfoWindow();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deslatlng, 12));
                                    windowOpened = true;
                                    LatLng adeslatlng = mk.getPosition();
                                    double lat = adeslatlng.latitude;
                                    double lng = adeslatlng.longitude;
                                    showfabWaze(String.valueOf(lat) + "," + String.valueOf(lng));
                                } else if (!windowOpened && i == NewJQlist.size() - 1 && mk0 != null) {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "MapViewedPostal", "");
                                    mk0.showInfoWindow();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mk0.getPosition(), 12));
                                    windowOpened = true;
                                    LatLng adeslatlng = mk0.getPosition();
                                    double lat = adeslatlng.latitude;
                                    double lng = adeslatlng.longitude;
                                    showfabWaze(String.valueOf(lat) + "," + String.valueOf(lng));
                                }
                            } else {
                                //do not show anything of lat lng 0,0. skip it for now
                            }
                            count += 1;
                        }
                    } else {
                        index--;
                    }
                }
            }

            pgLoading.dismiss();

            if (count == 0) {
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent parent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(parent);
                this.finish();
            } else {
                String nonSysCalRoute = objGlobalMethods.getGCMPrefs(GcmPreferences, "nonSysCalRoute");
                if (!nonSysCalRoute.equals("")) {
                    Toast.makeText(myApp.getAppContext(), myApp.getAppContext().getString(R.string.toastnoncalculatedroute), Toast.LENGTH_LONG).show();
                }

                /*mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent i = new Intent(MapsAllActivity.this, DashboardSubActivity.class);
                        JobQueue jq = null;
                        try {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            if (MarkerMap != null) {
                                jq = MarkerMap.get(marker);
                                if (jq != null) {
                                    String snippet = marker.getSnippet();
                                    String postal = jq.PostalCode;
                                    String fulladdress = marker.getTitle();
                                    LatLng adeslatlng = marker.getPosition();
                                    double LATval = 0;
                                    double LNGval = 0;
                                    LATval = adeslatlng.latitude;
                                    LNGval = adeslatlng.longitude;

                                    String[] pName = new String[]{
                                            "LAT",
                                            "LNG",
                                            "desPostal",
                                            "desAddress",
                                            "SelectedVRPJobsIDXs",
                                            "enableJobUpdates",
                                            "MapViewedPostal"};
                                    String[] pvalue = new String[]{
                                            String.valueOf(LATval),
                                            String.valueOf(LNGval),
                                            postal,
                                            fulladdress,
                                            jq.getJobsIDXs() != null && !jq.getJobsIDXs().equals("") ? jq.getJobsIDXs() : "",
                                            String.valueOf(jq.IsEnabledForJobUpdate),
                                            postal};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                                    DashboardSubActivity.frompage = "MapsAllActivity";
                                    startActivity(i);
                                    finish();
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                        } catch (Exception e) {
                            String s = e.getMessage().toString();
                            objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMap2SQLiteByJobQue mMap.setOnInfoWindowClickListener. Exception: " + s, "", "", "", "");
                        }
                    }
                });*/
            }
        } catch (Exception e) {
            pgLoading.dismiss();
            objGlobalMethods.writeToFile("Exception caught at MapsAllActivity setUpMap2SQLiteByJobQue. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_invalidaddress));
        }
    }

    private void reProcessRouteLocationAL() {
        ArrayList<com.xdel.luke.objClasses.Location> AllPipDipPostals;
        ArrayList<String> rlPostals = new ArrayList<>();
        ArrayList<String> existingPostals = null;
        RouteLocation rl = null;
        RouteLocation nrl = null;
        List<RouteLocation> L = null;
        boolean PostalStillExists = true;
        com.xdel.luke.objClasses.Location loc = null;
        String MapViewedPostal = objGlobalMethods.getGCMPrefs(objGlobalMethods.getGcmPreferences(), "MapViewedPostal");
        try {
            AllPipDipPostals = DAO_Jobs.getAllPipDipLocations();
            if (RouteLocationAL != null && RouteLocationAL.length > 0 && AllPipDipPostals != null && AllPipDipPostals.size() > 0) {

                for (int i = 0; i <= AllPipDipPostals.size() - 1; i++) {
                    if (existingPostals == null)
                        existingPostals = new ArrayList<>();
                    loc = AllPipDipPostals.get(i);
                    if (!existingPostals.contains(loc.postal)) {
                        existingPostals.add(loc.postal);
                    }
                }

                L = new ArrayList<RouteLocation>(Arrays.asList(RouteLocationAL));
                if (!objGlobalMethods.IsEmptyString(MapViewedPostal) && !existingPostals.contains(MapViewedPostal))
                    objGlobalMethods.setGCMPrefs(objGlobalMethods.getGcmPreferences(), "MapViewedPostal", "");

                //remove locations from RouteLocationAL that are supposed to be updated already
                for (int i = L.size() - 1; i >= 0; i--) {
                    rl = L.get(i);
                    if (existingPostals != null && existingPostals.size() > 0 && (!existingPostals.contains(rl.Location.postal))) {
                        L.remove(i);
                    }
                }

                //reorganise RouteLocationAL
                for (int i = 0; i <= L.size() - 1; i++) {
                    rl = L.get(i);
                    if (!objGlobalMethods.IsEmptyString(rl.Location.postal))
                        rlPostals.add(rl.Location.postal);
                }

                //add new locations to RouteLocationAL from getAllPipDipLocations
                if (rlPostals.size() > 0) {
                    for (int i = 0; i <= AllPipDipPostals.size() - 1; i++) {
                        rl = null;
                        if (!rlPostals.contains(AllPipDipPostals.get(i).postal)) {
                            rl = new RouteLocation();
                            rl.Location = AllPipDipPostals.get(i);
                            L.add(rl);
                        }
                    }

                    RouteLocation[] AL = new RouteLocation[L.size()];
                    RouteLocationAL = L.toArray(AL);
                }
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity reProcessRouteLocationAL. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private ArrayList<String> reProcessPostalcodes(String p) {
        try {
            if (ProcessedPostals == null)
                ProcessedPostals = new ArrayList<>();

            if (!ProcessedPostals.contains(p))
                ProcessedPostals.add(p);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity reProcessPostalcodes. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return ProcessedPostals;
        }
    }

    public void setUpMap2SQLiteCustomIcons(RouteLocation[] aRouteLocationAL) {
        double LATval = 0;
        double LNGval = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        RouteLocation rl = null;
        int count = 0;
        int index = 0;
        int TotalPIPDIPJobsCountPerPostal = 0;
        String manualDefineIndexStr = "";
        Boolean hasPUJobsIDXList = false;
        Boolean hasDLJobsIDXList = false;
        Context ctx = myApp.getAppContext();
        Context ctx2 = (Context) myApp.currentActivity;
        Resources resources = ctx.getResources();
        String full_address = "";
        String MapViewedPostal = objGlobalMethods.getGCMPrefs(GcmPreferences, "MapViewedPostal");
        boolean windowOpened = false;
        Marker mk0 = null;
        String jobsIDXs = "";
        objRouteJobEarliestTiming rjet = null;
        String timing0 = ctx.getString(R.string.clickviewdetails) + "\n\n";
        String timing = "";
        int jobscount = 0;
        int pipdip = 0;
        boolean pip = false;
        boolean dip = false;

        Date now = new Date();
        boolean isEarly = false;
        boolean isWithin = false;
        boolean isGoingLate = false;
        boolean isLate = false;
        boolean isCanOKPAfterToday = false;
        boolean isUnknown = false;
        boolean hasExt = false;
        Calendar ReadyDateTime = null;
        Calendar PickByDateTime = null;
        Calendar FromDateTime = null;
        Calendar ToDateTime = null;
        Calendar ExtDateTime = null;
        String ReadyDateTimeStr = "";
        String PickByDateTimeStr = "";
        String FromDateTimeStr = "";
        String ToDateTimeStr = "";
        String ExtDateTimeStr = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        ArrayList<Long> ProcessedJobsIDXs = new ArrayList<>();
        Long[] JobsIDXsPerPostal;
        objMapJob m = null;
        try {
            pgLoading.show();

            reProcessRouteLocationAL();

            //for (int i = 0; i <= aRouteLocationAL.length - 1; i++) {
            for (int i = 0; i <= RouteLocationAL.length - 1; i++) {
                pipdip = 0;
                pip = false;
                dip = false;
                isEarly = false;
                isWithin = false;
                isGoingLate = false;
                isLate = false;
                isCanOKPAfterToday = false;
                isUnknown = false;
                hasExt = false;
                jobscount = 0;
                jobsIDXs = "";
                full_address = "";
                rl = RouteLocationAL[i];
                full_address = rl.Location.full_address;
                /*hasPUJobsIDXList = rl.PUJobsIDXList != null && rl.PUJobsIDXList.length > 0 ? true : false;
                hasDLJobsIDXList = rl.DLJobsIDXList != null && rl.DLJobsIDXList.length > 0 ? true : false;*/
                timing0 = ctx.getString(R.string.clickviewdetails) + "\n\n";
                timing = "";
                JobsIDXsPerPostal = null;

                /*jobsIDXs = rl.JobsIDXList != null && rl.JobsIDXList.length > 0 ? Arrays.toString(rl.JobsIDXList) : "";
                jobsIDXs = (!objGlobalMethods.IsEmptyString(jobsIDXs) && jobsIDXs.length() > 0) ? jobsIDXs.substring(1, jobsIDXs.length() - 1) : "";
                jobscount = rl.JobsIDXList != null ? rl.JobsIDXList.length : 0;*/

                if (ProcessedPostals != null && ProcessedPostals.contains(rl.Location.postal))
                    continue;

                ProcessedPostals = reProcessPostalcodes(rl.Location.postal);

                m = DAO_Jobs.getAllJobsIDXPerPostal(rl.Location.postal);
                if (m == null) {
                    objGlobalMethods.writeToFile("MapsAllActivity objMapJob is null for postal " + rl.Location.postal, "", "", "", "");
                    continue;
                }

                JobsIDXsPerPostal = m.JobsIDX;
                jobsIDXs = JobsHandler2.convertLongArrayToString(JobsIDXsPerPostal);
                jobscount = JobsIDXsPerPostal == null ? 0 : JobsIDXsPerPostal.length;
                hasPUJobsIDXList = m.PIPcount > 0 ? true : false;
                hasDLJobsIDXList = m.DIPcount > 0 ? true : false;

                rjet = null;

                if (!objGlobalMethods.IsEmptyString(jobsIDXs) && jobscount > 0) {
                    rjet = DAO_Jobs.getobjRouteJobEarliestTiming(jobsIDXs);
                }
                if (!objGlobalMethods.IsEmptyString(jobsIDXs) && jobscount == 1) {
                    full_address = DAO_Jobs.getAddressWithoutPostalByJobsIDX(jobsIDXs);
                }

                if (rjet != null && rjet.ExpectedTime != null && !rjet.ExpectedTime.isEmpty() && !rjet.ExpectedTime.equals("")) {
                    try {
                        if (rjet.StatusCodeIDX == 103 && rjet.ReadyDateTime.equals(rjet.PickByDateTime)) {
                            timing += ctx.getString(R.string.rdycol) + "\n" + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ReadyDateTime), "dd/MM h:mma");
                        } else if (rjet.StatusCodeIDX == 103 && !rjet.ReadyDateTime.equals(rjet.PickByDateTime)) {
                            timing += ctx.getString(R.string.colbetween) + "\n"
                                    + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ReadyDateTime), "dd/MM h:mma")
                                    + " - "
                                    + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.PickByDateTime), "dd/MM h:mma");
                        } else if (rjet.StatusCodeIDX == 104) {
                            timing += ctx.getString(R.string.deliverytime) + "\n"
                                    + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.FromDateTime), "dd/MM h:mma")
                                    + " - "
                                    + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ToDateTime), "dd/MM h:mma")
                                    + (!objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !rjet.ExtDateTime.equals("0") ? "\n" + ctx.getString(R.string.extended)
                                    + " " + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma") : "");
                        }
                        //timing = objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExpectedTime), "dd/MM h:mma");

                        ReadyDateTime = Calendar.getInstance();
                        ReadyDateTime.setTimeInMillis(Long.parseLong(rjet.ReadyDateTime) * 1000);
                        ReadyDateTimeStr = formatter.format(ReadyDateTime.getTime());
                        PickByDateTime = Calendar.getInstance();
                        PickByDateTime.setTimeInMillis(Long.parseLong(rjet.PickByDateTime) * 1000);
                        PickByDateTimeStr = formatter.format(PickByDateTime.getTime());
                        FromDateTime = Calendar.getInstance();
                        FromDateTime.setTimeInMillis(Long.parseLong(rjet.FromDateTime) * 1000);
                        FromDateTimeStr = formatter.format(FromDateTime.getTime());
                        ToDateTime = Calendar.getInstance();
                        ToDateTime.setTimeInMillis(Long.parseLong(rjet.ToDateTime) * 1000);
                        ToDateTimeStr = formatter.format(ToDateTime.getTime());
                        ExtDateTime = Calendar.getInstance();
                        ExtDateTime.setTimeInMillis(Long.parseLong(rjet.ExtDateTime) * 1000);
                        ExtDateTimeStr = formatter.format(ExtDateTime.getTime());
                        hasExt = !objGlobalMethods.IsEqualString(rjet.ExtDateTime, "0") && !objGlobalMethods.IsEqualString(rjet.ToDateTime, rjet.ExtDateTime);

                        //0 comes when two date are same,
                        //1 comes when date1 is higher then date2 - later
                        //-1 comes when date1 is lower then date2 - earlier
                        if (rjet.StatusCodeIDX == 103 && (!objGlobalMethods.IsEqualString(rjet.ReadyDateTime, rjet.PickByDateTime))) {
                            isEarly = now.compareTo(ReadyDateTime.getTime()) == -1;
                            isWithin = (now.compareTo(ReadyDateTime.getTime()) >= 0) && (now.compareTo(PickByDateTime.getTime()) <= 0);
                            isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(PickByDateTime.getTime().getTime(), 45);
                            isLate = JobsHandler.isLate(PickByDateTime.getTime().getTime());
                            isCanOKPAfterToday = false;
                        } else if (rjet.StatusCodeIDX == 103) {
                            isEarly = now.compareTo(ReadyDateTime.getTime()) == -1;
                            isWithin = (now.compareTo(ReadyDateTime.getTime()) >= 0) && (now.compareTo(hasExt ? ExtDateTime.getTime() : ToDateTime.getTime()) <= 0);
                            isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime(), 45);
                            isLate = JobsHandler.isLate(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime());
                            isCanOKPAfterToday = false;
                        } else {
                            isEarly = now.compareTo(FromDateTime.getTime()) == -1;
                            isWithin = (now.compareTo(FromDateTime.getTime()) >= 0) && (now.compareTo(hasExt ? ExtDateTime.getTime() : ToDateTime.getTime()) <= 0);
                            isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime(), 45);
                            isLate = JobsHandler.isLate(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime());
                            //isCanOKPAfterToday = JobsHandler.isLaterDate(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime() * 1000L, 1);
                            isCanOKPAfterToday = JobsHandler.isOneOrMoreDaysLater(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime());
                        }
                    } catch (Exception e) {
                        String ss = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at MapsAllActivity get earliest timing. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                    }
                } else {
                    isUnknown = true;
                }

                //timing += "\n\n" + ctx.getString(R.string.lblpNum) + " " + (rl.PUJobsIDXList != null ? rl.PUJobsIDXList.length : 0) + " " + ctx.getString(R.string.lbldNum) + " " + (rl.DLJobsIDXList != null ? rl.DLJobsIDXList.length : 0);
                timing += "\n\n" + ctx.getString(R.string.lblpNum) + " " + (m.PIPcount) + " " + ctx.getString(R.string.lbldNum) + " " + (m.DIPcount);
                timing0 += timing;

                pipdip += (hasPUJobsIDXList & ((pipdip & 1) != 1)) ? 1 : 0;
                pipdip += (hasDLJobsIDXList & ((pipdip & 2) != 2)) ? 2 : 0;

                manualDefineIndexStr = rl.Arrive == null && rl.Depart == null && hasPUJobsIDXList && hasDLJobsIDXList ? "PD" :
                        rl.Arrive == null && rl.Depart == null && hasPUJobsIDXList && !hasDLJobsIDXList ? "P" :
                                rl.Arrive == null && rl.Depart == null && !hasPUJobsIDXList && hasDLJobsIDXList ? "D" : "";

                TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(rl.Location.postal);

                if (TotalPIPDIPJobsCountPerPostal > 0) {
                    LATval = Double.parseDouble(rl.Location.lat);
                    LNGval = Double.parseDouble(rl.Location.lon);

                    if (LATval != 0 && LNGval != 0) {
                        deslatlng = new LatLng(LATval, LNGval);
                        index += 1;

                        int iconRID = isEarly && (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_gray : // isEarly pipdip
                                isEarly && (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_gray : // isEarly pip
                                        isEarly && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_gray_new : // isEarly dip
                                                (isCanOKPAfterToday) && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_blue_new : // within timing with least priority
                                                        isLate && (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_red : // late pipdip
                                                                isLate && (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_red : // late pip
                                                                        isLate && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_red_new : // late dip
                                                                                isGoingLate && (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_orange : // isGoingLate pipdip
                                                                                        isGoingLate && (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_orange : // isGoingLate pip
                                                                                                isGoingLate && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_orange_new : // isGoingLate dip
                                                                                                        isWithin && (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_green : // isWithin pipdip
                                                                                                                isWithin && (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_green : // isWithin pip
                                                                                                                        isWithin && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_green_new : // isWithin dip
                                                                                                                                (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_mag : // unknown pipdip
                                                                                                                                        (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_mag : // unknown pip
                                                                                                                                                R.mipmap.map_marker_dip_mag_new; // unknown dip

                        int textColor = (isCanOKPAfterToday) ? Color.WHITE : isLate ? Color.WHITE : isGoingLate ? Color.BLACK :
                                isWithin ? Color.BLACK : isEarly ? Color.BLACK : Color.BLACK;
                        Bitmap icon = makeBitmap(resources, iconRID, textColor, !manualDefineIndexStr.equals("") ? manualDefineIndexStr : String.valueOf(index));
                        MarkerOptions options = new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                                .position(deslatlng)
                                .title(full_address + "\n" + rl.Location.postal)
                                .snippet(timing0);

                        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(ctx2);
                        mMap.setInfoWindowAdapter(customInfoWindow);

                        Marker mk = mMap.addMarker(options);
                        if (MarkerMapRL == null) {
                            MarkerMapRL = new HashMap<Marker, RouteLocation>();
                        }
                        rl.setJobsIDXList(JobsIDXsPerPostal);
                        MarkerMapRL.put(mk, rl);
                        if (count == 0) {
                            mk0 = mk;
                        }

                        if (!windowOpened && !objGlobalMethods.IsEmptyString(MapViewedPostal) && MapViewedPostal.equals(rl.Location.postal)) {
                            mk.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deslatlng, 12));
                            windowOpened = true;
                        } else if (!windowOpened && i == RouteLocationAL.length - 1 && mk0 != null) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "MapViewedPostal", "");
                            mk0.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mk0.getPosition(), 12));
                            windowOpened = true;
                        }
                    } else {
                        //do not show anything of lat lng 0,0. skip it for now
                        try {
                            if (!windowOpened && i == RouteLocationAL.length - 1 && mk0 != null) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "MapViewedPostal", "");
                                mk0.showInfoWindow();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mk0.getPosition(), 12));
                                windowOpened = true;
                            }
                        } catch (Exception e) {
                            String ss = JobsHandler.getStackTraceString(e);
                            objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMap2SQLiteCustomIcons 001. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                        }
                    }
                    count += 1;
                }
            }

            try {
                if (!windowOpened && mk0 != null) {
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "MapViewedPostal", "");
                    mk0.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mk0.getPosition(), 12));
                }
            } catch (Exception e) {
                String sss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMap2SQLiteCustomIcons 002. Exception: " + e.getMessage() + "\nStack: " + sss, "", "", "", "");
            }

            pgLoading.dismiss();

            if (count == 0) {
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent parent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(parent);
                this.finish();
            } else {
                String nonSysCalRoute = objGlobalMethods.getGCMPrefs(GcmPreferences, "nonSysCalRoute");
                if (!nonSysCalRoute.equals("")) {
                    Toast.makeText(myApp.getAppContext(), myApp.getAppContext().getString(R.string.toastnoncalculatedroute), Toast.LENGTH_LONG).show();
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent i = new Intent(MapsAllActivity.this, DashboardSubActivity.class);
                        RouteLocation rl = null;
                        int brkStartIDX = -1;
                        try {
                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            if (MarkerMapRL != null) {
                                rl = MarkerMapRL.get(marker);
                                if (rl != null) {
                                    String snippet = marker.getSnippet();
                                    String postal = rl.Location.postal;
                                    String fulladdress = marker.getTitle();
                                    brkStartIDX = fulladdress.indexOf("\n");
                                    if (brkStartIDX > 0) {
                                        fulladdress = fulladdress.substring(0, brkStartIDX);
                                    }
                                    LatLng adeslatlng = marker.getPosition();
                                    double LATval = 0;
                                    double LNGval = 0;
                                    LATval = adeslatlng.latitude;
                                    LNGval = adeslatlng.longitude;
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(LATval));
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(LNGval));
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", postal);
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", fulladdress);

                                    String[] pName = new String[]{
                                            "LAT",
                                            "LNG",
                                            "desPostal",
                                            "desAddress",
                                            "MapViewedPostal"};
                                    String[] pvalue = new String[]{
                                            String.valueOf(LATval),
                                            String.valueOf(LNGval),
                                            postal,
                                            fulladdress,
                                            postal};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                                    DashboardSubActivity.frompage = "MapsAllActivity";
                                    startActivity(i);
                                    finish();
                                } else {
                                    return;
                                }
                            } else {
                                return;
                            }
                        } catch (Exception e) {
                            String s = e.getMessage();
                            String ex = JobsHandler.getStackTraceString(e);
                            s += "\nStack: " + ex;
                            objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMap2SQLiteCustomIcons mMap.setOnInfoWindowClickListener. Exception: " + s, "", "", "", "");
                        }
                    }
                });
            }
        } catch (Exception e) {
            pgLoading.dismiss();
            String s = e.getMessage();
            String ex = JobsHandler.getStackTraceString(e);
            s += "\nStack: " + ex;
            objGlobalMethods.writeToFile("Exception caught at MapsAllActivity setUpMap2SQLiteCustomIcons. Exception: " + s, "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_invalidaddress));
        }
    }

    public static Bitmap makeBitmap(Resources resources, int fileID, int textColor, String text) {
        //Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        //Bitmap bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_action_test);
        Bitmap bitmap = BitmapFactory.decodeResource(resources, fileID);
        bitmap = bitmap.copy(ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setColor(Color.RED); // Text color
        paint.setColor(textColor); // Text color
        paint.setTextSize(14 * scale); // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        /*int x = bitmap.getWidth() - bounds.width() - 10; // 10 for padding from right
        int y = bounds.height();*/
        int x = ((bitmap.getWidth() - bounds.width()) / 2) - 3; // 10 for padding from right
        /*int y = (fileID == R.mipmap.map_marker_dip_red_new) ||
                (fileID == R.mipmap.map_marker_dip_orange_new) ||
                (fileID == R.mipmap.map_marker_dip_green_new) ||
                (fileID == R.mipmap.map_marker_dip_gray_new) ||
                (fileID == R.mipmap.map_marker_dip_mag_new) ||
                (fileID == R.mipmap.map_marker_dip_blue_new) ? 70 : 85; //the higher the value, the lower the text*/

        int y = ((bitmap.getHeight()) / 2); //the higher the value, the lower the text
        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    /*public Bitmap makeBitmap(Context context, String text)
    {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_action_test);
        bitmap = bitmap.copy(ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED); // Text color
        paint.setTextSize(14 * scale); // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        *//*int x = bitmap.getWidth() - bounds.width() - 10; // 10 for padding from right
        int y = bounds.height();*//*
        int x = ((bitmap.getWidth() - bounds.width()) / 2) - 3; // 10 for padding from right
        int y = 85;
        //int y = bounds.height(); //31
        canvas.drawText(text, x, y, paint);

        return  bitmap;
    }*/

    private CharSequence makeCharSequence() {
        String prefix = "0";
        String suffix = "";
        String sequence = prefix + suffix;
        SpannableStringBuilder ssb = new SpannableStringBuilder(sequence);
        ssb.setSpan(new StyleSpan(ITALIC), 0, prefix.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new StyleSpan(BOLD), prefix.length(), sequence.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    //marker related
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;
        private String name;

        public MyInfoWindowAdapter(String name) {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.map_marker_custom_info_contents, null);
            this.name = name;
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView) myContentsView
                    .findViewById(R.id.title));
            tvTitle.setText(name);
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }
    //marker related

    public void handleNewLocation(Location location) {


        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        //setMarker2();

        float acc = location.getAccuracy();
        currLatLng = new LatLng(currentLatitude, currentLongitude);

        /*directionURL = getMapsApiDirectionsUrl(currentLatitude, currentLongitude, lat, lng);
        getDirection downloadTask = new getDirection();
        downloadTask.execute(directionURL);*/

        /*//mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng,18));*/
    }

    private class GeocoderTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //android.os.Debug.waitForDebugger();
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            deslatlng = getLocationFromString(objGlobalMethods.getGCMPrefs(GcmPreferences, "desAddress") + ", Singapore " + objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal"));
            //deslatlng = getLocationFromString("Singapore " + objGlobalMethods.getGCMPrefs(GcmPreferences,"desPostal"));
            //deslatlng = getLocationFromString(objGlobalMethods.getGCMPrefs(GcmPreferences,"desPostal"));

            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                if (!deslatlng.toString().equals("lat/lng: (0.0,0.0)")) {
                    MarkerOptions options = new MarkerOptions()
                            .position(deslatlng)
                            .title("Your next destination");
                    mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deslatlng, 18));
                    directionURL = getMapsApiDirectionsUrl(currentLatitude, currentLongitude, lat, lng);

                    getDirection downloadTask = new getDirection();
                    downloadTask.execute(directionURL);
                } else {
                    showErrorAlert(myApp.getAppContext().getString(R.string.alert_invalidaddress));
                }

                /*MarkerOptions options = new MarkerOptions()
                        .position(deslatlng)
                        .title("Your next destination");
                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deslatlng, 18));
                directionURL = getMapsApiDirectionsUrl(currentLatitude,currentLongitude,lat,lng);

                getDirection downloadTask = new getDirection();
                downloadTask.execute(directionURL);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public LatLng getLocationFromString(String postal) {
        try {
            String pos;
            int hexidx = 0;
            postal = postal.replaceAll("\\r\\n|\\r|\\n", "");
            if (postal.contains("#")) {
                if (postal.contains("#")) {
                    hexidx = postal.indexOf("#");
                }
                postal = postal.substring(0, hexidx);
            }

            pos = postal.replace(" ", "+");
            //pos = pos.replace(",","");
            //String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + pos + "&key=" + getResources().getString(R.string.serverAPIkey);
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + pos + "&" + getResources().getString(R.string.serverAPIkeyWithtext);
            HttpGet httpGet = new HttpGet(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (IOException e) {
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());

            String Status = jsonObject.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObject.getJSONArray("results");
                JSONObject zero = null;
                JSONObject zero2;
                String long_name = "";
                JSONArray mtypes = null;
                String Type = "";
                JSONArray address_components = null;
                String formatted_address = "";
                for (int i = 0; i <= Results.length() - 1; i++) {
                    zero = Results.getJSONObject(i);
                    address_components = zero.getJSONArray("address_components");

                    for (int v = 0; v <= address_components.length() - 1; v++) {
                        zero2 = null;
                        zero2 = address_components.getJSONObject(v);
                        long_name = zero2.getString("long_name");
                        mtypes = zero2.getJSONArray("types");
                        Type = mtypes.getString(0);

                        if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                            if (Type.equalsIgnoreCase("country")) {
                                if (long_name.equalsIgnoreCase("Singapore")) {
                                        /*lng = ((JSONArray) jsonObject.get("results")).getJSONObject(i)
                                                .getJSONObject("geometry").getJSONObject("location")
                                                .getDouble("lng");

                                        lat = ((JSONArray) jsonObject.get("results")).getJSONObject(i)
                                                .getJSONObject("geometry").getJSONObject("location")
                                                .getDouble("lat");*/

                                    lng = zero.getJSONObject("geometry").getJSONObject("location")
                                            .getDouble("lng");

                                    lat = zero.getJSONObject("geometry").getJSONObject("location")
                                            .getDouble("lat");
                                    return new LatLng(lat, lng);
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
        return new LatLng(lat, lng);
    }

    private String getMapsApiDirectionsUrl(double sourceLat, double sourceLng, double DesLat, double DesLng) {
        String waypoints = "waypoints=optimize:true|"
                + sourceLat + "," + sourceLng
                + "|" + DesLat + ","
                + DesLng;

        String sensor = "sensor=false";
        String origin = "origin=" + currentLatitude + "," + currentLongitude;
        String destination = "destination=" + lat + "," + lng;
        String mode = "mode=driving";
        //String params = origin + "&" + destination + "&" + waypoints + "&" + mode + "&key=" + getResources().getString(R.string.serverAPIkey);
        String params = origin + "&" + destination + "&" + waypoints + "&" + mode + "&" + getResources().getString(R.string.serverAPIkeyWithtext);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    private class getDirection extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            if (routes != null && routes.size() > 0) {
                /*for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(6);
                    polyLineOptions.color(Color.BLUE);
                }*/

                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(0);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(6);
                polyLineOptions.color(Color.BLUE);

                if (polyLineOptions != null) {
                    mMap.addPolyline(polyLineOptions);
                }

                //mMap.addPolyline(polyLineOptions);
            } else {
                Date dt = new Date();
                objGlobalMethods.writeToFile("MapsActivity ParserTask onPostExecute has a null routes array. Route not plotted.", "", "", "", "");
            }
        }
    }

    public Dialog showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    public Dialog showChargeBattDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage(msg);

        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "ok", then he is allowed to exit from application
                Boolean asd = false;
                SharedPreferences gcmp = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                objGlobalMethods.setGCMPrefs(gcmp, "showBattChargeNotiPrefStr", asd.toString());
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    private class recalroute extends AsyncTask<Void, Void, Boolean> {

        public recalroute() {

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pgProcessing.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean L2_DeleteProblem = false;
            boolean L2_CreateProblem = false;
            boolean asd = false;
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            try {
                String regid = objGlobalMethods.getRegistrationIdOnly();
                if (!objGlobalMethods.IsEmptyString(regid)) {
                    L2_DeleteProblem = WebService.L2_DeleteProblem(regid);
                }
                //ST_DeleteProblem_b = true;
                if (L2_DeleteProblem) {
                    WebService.L2_DeleteJobQueue(regid);
                    L2_CreateProblem = WebService.L2_CreateProblem(regid);
                }

                if (!L2_CreateProblem) {
                    asd = false;
                } else {
                    JobsHandler.HandleRouteList(regid, GcmPreferences);
                    String RouteListJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "RouteList");
                    if (objGlobalMethods.IsEmptyString(RouteListJsonStr) || !RouteListJsonStr.equals("[]")) {
                        RouteLocation[] AL = null;
                        AL = DAO_Jobs.getRouteLocationAL();
                        if (AL != null && AL.length > 0) {
                            Gson gson = new Gson();
                            RouteListJsonStr = gson.toJson(AL);
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "RouteList", RouteListJsonStr);
                            asd = true;
                        }
                    } else {
                        asd = true;
                    }
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at MapsAllActivity recalroute doInBackground. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                setUpMap2();
                if (!success) {
                    objGlobalMethods.writeToFile("ST_CreateProblem unsuccessful.", "", "", "", "");
                    //showDialog(myApp.getAppContext().getString(R.string.recalroute_notok)).show();
                }
            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at MapsAllActivity recalroute onPostExecute. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
            } finally {
                if (pgProcessing != null) {
                    pgProcessing.dismiss();
                }
            }
        }
    }

    public void setUpMap2SQLiteCustomIconsAddNewPipLocations() {
        ArrayList<com.xdel.luke.objClasses.Location> AllPipDipPostals;
        ArrayList<String> rlPostals = new ArrayList<>();
        RouteLocation rl = null;
        RouteLocation nrl = null;
        List<RouteLocation> L = null;
        try {
            AllPipDipPostals = DAO_Jobs.getAllPipDipLocations();

            if (RouteLocationAL != null && RouteLocationAL.length > 0 && AllPipDipPostals != null && AllPipDipPostals.size() > 0) {

                for (int i = 0; i <= RouteLocationAL.length - 1; i++) {
                    rl = RouteLocationAL[i];
                    if (!objGlobalMethods.IsEmptyString(rl.Location.postal))
                        rlPostals.add(rl.Location.postal);
                }

                if (rlPostals.size() > 0) {
                    for (int i = 0; i <= AllPipDipPostals.size() - 1; i++) {
                        rl = null;
                        if (!rlPostals.contains(AllPipDipPostals.get(i).postal)) {
                            rl = new RouteLocation();
                            rl.Location = AllPipDipPostals.get(i);
                            if (L == null)
                                L = new ArrayList<>();
                            L.add(rl);
                        }
                    }

                    if (L != null && L.size() > 0) {
                        for (int i = 0; i <= L.size() - 1; i++) {
                            rl = L.get(i);
                            setUpMap2SQLiteCustomIconsLoadMarkers(rl, false, MarkerMapRL.size(), true);
                        }
                    }
                }
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMap2SQLiteCustomIconsAddNewPipLocations. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void loadMapFirstTime() {
        RouteLocation rl = null;
        ArrayList<String> NewPIPpostals = null;
        String NewPIPpostalsJsonStr = "";
        boolean newPIP = false;

        try {
            reProcessRouteLocationAL();
            if (RouteLocationAL.length > 0) {

                GcmPreferences = objGlobalMethods.getGcmPreferences();
                NewPIPpostalsJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "NewPIPpostalsJsonStr");
                if (!objGlobalMethods.IsEmptyString(NewPIPpostalsJsonStr)) {
                    Gson gson;
                    gson = new Gson();
                    NewPIPpostals = gson.fromJson(NewPIPpostalsJsonStr, ArrayList.class);
                }

                for (int i = 0; i <= RouteLocationAL.length - 1; i++) {
                    rl = RouteLocationAL[i];
                    newPIP = NewPIPpostals != null && NewPIPpostals.size() > 0 ? NewPIPpostals.contains(rl.Location.postal) : false;

                    //setUpMap2SQLiteCustomIconsLoadMarkers(rl, i==0, i, false);
                    setUpMap2SQLiteCustomIconsLoadMarkers(rl, i == 0, i, newPIP);
                }
                setUpMarkerClick();
                objGlobalMethods.setGCMPrefs(GcmPreferences, "NewPIPpostalsJsonStr", "");

            } else {
                //Intent parent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
                Intent parent = new Intent(this, !isVrpView() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                startActivity(parent);
                this.finish();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity loadMapFirstTime. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void setUpMap2SQLiteCustomIconsLoadMarkers(RouteLocation rl, boolean focusLocation, int index, boolean newPIP) {
        double LATval = 0;
        double LNGval = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        int count = 0;
        //int index = 0;
        int TotalPIPDIPJobsCountPerPostal = 0;
        String manualDefineIndexStr = "";
        Boolean hasPUJobsIDXList = false;
        Boolean hasDLJobsIDXList = false;
        Context ctx = myApp.getAppContext();
        Context ctx2 = (Context) myApp.currentActivity;
        Resources resources = ctx.getResources();
        String full_address = "";
        String MapViewedPostal = objGlobalMethods.getGCMPrefs(GcmPreferences, "MapViewedPostal");
        //boolean windowOpened = false;
        Marker mk0 = null;
        String jobsIDXs = "";
        objRouteJobEarliestTiming rjet = null;
        String timing0 = ctx.getString(R.string.clickviewdetails) + "\n\n";
        String timing = "";
        int jobscount = 0;
        int pipdip = 0;
        boolean pip = false;
        boolean dip = false;

        Date now = new Date();
        boolean isEarly = false;
        boolean isWithin = false;
        boolean isGoingLate = false;
        boolean isLate = false;
        boolean isLate2 = false;
        boolean isLate3 = false;
        boolean hasOrExt = false;
        boolean isCanOKPAfterToday = false;
        boolean isUnknown = false;
        boolean hasExt = false;
        Calendar ReadyDateTime = null;
        Calendar PickByDateTime = null;
        Calendar FromDateTime = null;
        Calendar ToDateTime = null;
        Calendar ExtDateTime = null;
        Calendar ExtFromDateTime = null;
        String ReadyDateTimeStr = "";
        String PickByDateTimeStr = "";
        String FromDateTimeStr = "";
        String ToDateTimeStr = "";
        String ExtDateTimeStr = "";
        String ExtFromDateTimeStr = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        ArrayList<Long> ProcessedJobsIDXs = new ArrayList<>();
        Long[] JobsIDXsPerPostal;
        objMapJob m = null;

        try {
            if (ProcessedPostals != null && ProcessedPostals.contains(rl.Location.postal))
                return;
            ProcessedPostals = reProcessPostalcodes(rl.Location.postal);

            full_address = rl.Location.full_address;
            timing0 = ctx.getString(R.string.clickviewdetails) + "\n\n";

            m = DAO_Jobs.getAllJobsIDXPerPostal(rl.Location.postal);
            if (m == null) {
                objGlobalMethods.writeToFile("MapsAllActivity objMapJob is null for postal " + rl.Location.postal, "", "", "", "");
                return;
            }

            JobsIDXsPerPostal = m.JobsIDX;
            jobsIDXs = JobsHandler2.convertLongArrayToString(JobsIDXsPerPostal);
            jobscount = JobsIDXsPerPostal == null ? 0 : JobsIDXsPerPostal.length;
            hasPUJobsIDXList = m.PIPcount > 0 ? true : false;
            hasDLJobsIDXList = m.DIPcount > 0 ? true : false;

            rjet = null;

            if (!objGlobalMethods.IsEmptyString(jobsIDXs) && jobscount > 0) {
                rjet = DAO_Jobs.getobjRouteJobEarliestTiming(jobsIDXs);
            }
            if (!objGlobalMethods.IsEmptyString(jobsIDXs) && jobscount == 1) {
                full_address = DAO_Jobs.getAddressWithoutPostalByJobsIDX(jobsIDXs);
            }

            if (rjet != null && rjet.ExpectedTime != null && !rjet.ExpectedTime.isEmpty() && !rjet.ExpectedTime.equals("")) {
                try {
                    if (rjet.StatusCodeIDX == 103 && rjet.ReadyDateTime.equals(rjet.PickByDateTime)) {
                        timing += ctx.getString(R.string.rdycol) + "\n" + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ReadyDateTime), "dd/MM h:mma");
                    } else if (rjet.StatusCodeIDX == 103 && !rjet.ReadyDateTime.equals(rjet.PickByDateTime)) {
                        timing += ctx.getString(R.string.colbetween) + "\n"
                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ReadyDateTime), "dd/MM h:mma")
                                + " - "
                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.PickByDateTime), "dd/MM h:mma");
                    } else if (rjet.StatusCodeIDX == 104) {
                        /*timing += ctx.getString(R.string.deliverytime) + "\n"
                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.FromDateTime), "dd/MM h:mma")
                                + " - "
                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ToDateTime), "dd/MM h:mma")
                                + (!objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !rjet.ExtDateTime.equals("0") ? "\n" + ctx.getString(R.string.extended)
                                + " " + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma") : "");*/

                        /*timing += ctx.getString(R.string.deliverytime) + "\n"
                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.FromDateTime), "dd/MM h:mma")
                                + " - "
                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ToDateTime), "dd/MM h:mma")
                                + (
                                !objGlobalMethods.IsEmptyString(rjet.ExtFromDateTime) && !rjet.ExtFromDateTime.equals("0") && !rjet.ExtFromDateTime.equals(rjet.ToDateTime) ?
                                        "\n" + ctx.getString(R.string.lblor)
                                                + "\n"
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtFromDateTime), "dd/MM h:mma")
                                                + " - "
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma") :
                                        !objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !rjet.ExtDateTime.equals("0") ?
                                                "\n" + ctx.getString(R.string.extended)
                                                        + "\n" + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma")
                                                : "");*/

                        timing += ctx.getString(R.string.deliverytime) + "\n"
                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.FromDateTime), "dd/MM h:mma")
                                + " - "
                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ToDateTime), "dd/MM h:mma")
                                + (
                                jobscount > 1 ?
                                        "\n" + ctx.getString(R.string.clickforext)
                                        :
                                !objGlobalMethods.IsEmptyString(rjet.ExtFromDateTime) && !rjet.ExtFromDateTime.equals("0") && !rjet.ExtFromDateTime.equals(rjet.ToDateTime) ?
                                        "\n" + ctx.getString(R.string.lblor)
                                                + "\n"
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtFromDateTime), "dd/MM h:mma")
                                                + " - "
                                                + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma") :
                                        !objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !rjet.ExtDateTime.equals("0") ?
                                                "\n" + ctx.getString(R.string.extended)
                                                        + "\n" + objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExtDateTime), "dd/MM h:mma")
                                                : "");
                    }

                    ReadyDateTime = Calendar.getInstance();
                    ReadyDateTime.setTimeInMillis(Long.parseLong(rjet.ReadyDateTime) * 1000);
                    ReadyDateTimeStr = formatter.format(ReadyDateTime.getTime());
                    PickByDateTime = Calendar.getInstance();
                    PickByDateTime.setTimeInMillis(Long.parseLong(rjet.PickByDateTime) * 1000);
                    PickByDateTimeStr = formatter.format(PickByDateTime.getTime());
                    FromDateTime = Calendar.getInstance();
                    FromDateTime.setTimeInMillis(Long.parseLong(rjet.FromDateTime) * 1000);
                    FromDateTimeStr = formatter.format(FromDateTime.getTime());
                    ToDateTime = Calendar.getInstance();
                    ToDateTime.setTimeInMillis(Long.parseLong(rjet.ToDateTime) * 1000);
                    ToDateTimeStr = formatter.format(ToDateTime.getTime());

                    if (!objGlobalMethods.IsEmptyString(rjet.ExtDateTime) && !objGlobalMethods.IsEqualString(rjet.ExtDateTime, "0")) {
                        ExtDateTime = Calendar.getInstance();
                        ExtDateTime.setTimeInMillis(Long.parseLong(rjet.ExtDateTime) * 1000);
                    }
                    /*ExtDateTime = Calendar.getInstance();
                    ExtDateTime.setTimeInMillis(Long.parseLong(rjet.ExtDateTime) * 1000);*/
                    if (!objGlobalMethods.IsEmptyString(rjet.ExtFromDateTime) && !objGlobalMethods.IsEqualString(rjet.ExtFromDateTime, "0")) {
                        ExtFromDateTime = Calendar.getInstance();
                        ExtFromDateTime.setTimeInMillis(Long.parseLong(rjet.ExtFromDateTime) * 1000);
                    }
                    /*ExtFromDateTime = Calendar.getInstance();
                    ExtFromDateTime.setTimeInMillis(Long.parseLong(rjet.ExtFromDateTime) * 1000);*/

                    hasExt = !objGlobalMethods.IsEqualString(rjet.ExtDateTime, "0") && !objGlobalMethods.IsEqualString(rjet.ToDateTime, rjet.ExtDateTime);
                    //hasExt = !objGlobalMethods.IsEqualString(rjet.ExtDateTime, "0") && !objGlobalMethods.IsEqualString(rjet.ToDateTime, rjet.ExtDateTime);

                    //0 comes when two date are same,
                    //1 comes when date1 is higher then date2 - later
                    //-1 comes when date1 is lower then date2 - earlier
                    if (rjet.StatusCodeIDX == 103 && (!objGlobalMethods.IsEqualString(rjet.ReadyDateTime, rjet.PickByDateTime))) {
                        isEarly = now.compareTo(ReadyDateTime.getTime()) == -1;
                        isWithin = (now.compareTo(ReadyDateTime.getTime()) >= 0) && (now.compareTo(PickByDateTime.getTime()) <= 0);
                        isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(PickByDateTime.getTime().getTime(), 45);
                        isLate = JobsHandler.isLate(PickByDateTime.getTime().getTime());
                        isCanOKPAfterToday = false;
                    } else if (rjet.StatusCodeIDX == 103) {
                        isEarly = now.compareTo(ReadyDateTime.getTime()) == -1;
                        isWithin = (now.compareTo(ReadyDateTime.getTime()) >= 0) && (now.compareTo(hasExt ? ExtDateTime.getTime() : ToDateTime.getTime()) <= 0);
                        isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime(), 45);
                        isLate = JobsHandler.isLate(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime());
                        isCanOKPAfterToday = false;
                    } else {
                        /*isEarly = now.compareTo(FromDateTime.getTime()) == -1;
                        isWithin = (now.compareTo(FromDateTime.getTime()) >= 0) && (now.compareTo(hasExt ? ExtDateTime.getTime() : ToDateTime.getTime()) <= 0);
                        isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime(), 45);
                        isLate = JobsHandler.isLate(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime());
                        isCanOKPAfterToday = JobsHandler.isOneOrMoreDaysLater(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime());*/

                        if (ExtFromDateTime != null)
                            hasOrExt = hasExt && ExtFromDateTime.getTime().getTime() > ToDateTime.getTime().getTime();

                        isEarly = now.compareTo(FromDateTime.getTime()) == -1;

                        isLate = JobsHandler.isLate(ToDateTime.getTime().getTime());
                        if (!isLate && (!hasExt || (hasExt && hasOrExt))) {
                            isWithin = (now.compareTo(FromDateTime.getTime()) >= 0) &&
                                    (now.compareTo(ToDateTime.getTime()) <= 0);
                            isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(ToDateTime.getTime().getTime(), 45);
                        } else if (!isLate && hasExt && !hasOrExt) {
                            isWithin = (now.compareTo(FromDateTime.getTime()) >= 0) &&
                                    (now.compareTo(ExtDateTime.getTime()) <= 0);
                            isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(ExtDateTime.getTime().getTime(), 45);
                        } else if (isLate && !hasExt) {
                            isWithin = false;
                            isGoingLate = true;
                        } else if (isLate && hasExt && !hasOrExt) {
                            isLate = JobsHandler.isLate(ExtDateTime.getTime().getTime());
                            isWithin = (now.compareTo(FromDateTime.getTime()) >= 0) &&
                                    (now.compareTo(ExtDateTime.getTime()) <= 0);
                            isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(ExtDateTime.getTime().getTime(), 45);
                        } else if (isLate && hasExt && hasOrExt) {
                            isLate =  JobsHandler.isLate(ExtDateTime.getTime().getTime());
                            isWithin = (now.compareTo(ExtFromDateTime.getTime()) >= 0) &&
                                    (now.compareTo(ExtDateTime.getTime()) <= 0);
                            isGoingLate = JobsHandler.isXminuteBeforeGivenDateTime(ExtDateTime.getTime().getTime(), 45);

                            if (!isLate && !isGoingLate && !isWithin) {
                                isEarly = true;
                            }
                        }

                        isCanOKPAfterToday = JobsHandler.isOneOrMoreDaysLater(hasExt ? ExtDateTime.getTime().getTime() : ToDateTime.getTime().getTime());

                    }
                } catch (Exception e) {
                    String ss = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at MapsAllActivity get earliest timing. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
                }
            } else {
                isUnknown = true;
            }

            timing += "\n\n" + ctx.getString(R.string.lblpNum) + " " + (m.PIPcount) + " " + ctx.getString(R.string.lbldNum) + " " + (m.DIPcount);
            timing0 += timing;

            pipdip += (hasPUJobsIDXList & ((pipdip & 1) != 1)) ? 1 : 0;
            pipdip += (hasDLJobsIDXList & ((pipdip & 2) != 2)) ? 2 : 0;

            manualDefineIndexStr = newPIP ? " " :
                    rl.Arrive == null && rl.Depart == null && hasPUJobsIDXList && hasDLJobsIDXList ? "PD" :
                            rl.Arrive == null && rl.Depart == null && hasPUJobsIDXList && !hasDLJobsIDXList ? "P" :
                                    rl.Arrive == null && rl.Depart == null && !hasPUJobsIDXList && hasDLJobsIDXList ? "D" : "";

            TotalPIPDIPJobsCountPerPostal = DAO_Jobs.getTotalPIPDIPJobsCountPerPostal(rl.Location.postal);

            if (TotalPIPDIPJobsCountPerPostal > 0) {
                LATval = Double.parseDouble(rl.Location.lat);
                LNGval = Double.parseDouble(rl.Location.lon);

                if (LATval != 0 && LNGval != 0) {
                    deslatlng = new LatLng(LATval, LNGval);
                    index += 1;

                    int iconRID = newPIP ? R.mipmap.ic_action_noti :
                            isEarly && (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_gray : // isEarly pipdip
                                    isEarly && (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_gray : // isEarly pip
                                            isEarly && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_gray_new : // isEarly dip
                                                    (isCanOKPAfterToday) && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_blue_new : // within timing with least priority
                                                            isLate && (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_red : // late pipdip
                                                                    isLate && (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_red : // late pip
                                                                            isLate && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_red_new : // late dip
                                                                                    isGoingLate && (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_orange : // isGoingLate pipdip
                                                                                            isGoingLate && (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_orange : // isGoingLate pip
                                                                                                    isGoingLate && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_orange_new : // isGoingLate dip
                                                                                                            isWithin && (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_green : // isWithin pipdip
                                                                                                                    isWithin && (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_green : // isWithin pip
                                                                                                                            isWithin && (((pipdip & 1)) != 1 && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_dip_green_new : // isWithin dip
                                                                                                                                    (((pipdip & 1) == 1) && ((pipdip & 2) == 2)) ? R.mipmap.map_marker_pipdip_mag : // unknown pipdip
                                                                                                                                            (((pipdip & 1)) == 1 && ((pipdip & 2) != 2)) ? R.mipmap.map_marker_pip_mag : // unknown pip
                                                                                                                                                    R.mipmap.map_marker_dip_mag_new; // unknown dip

                    int textColor = (isCanOKPAfterToday) ? Color.WHITE : isLate ? Color.WHITE : isGoingLate ? Color.BLACK :
                            isWithin ? Color.BLACK : isEarly ? Color.BLACK : Color.BLACK;
                    Bitmap icon = makeBitmap(resources, iconRID, textColor, !manualDefineIndexStr.equals("") ? manualDefineIndexStr : String.valueOf(index));
                    MarkerOptions options = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(icon))
                            .position(deslatlng)
                            .title(full_address + "\n" + rl.Location.postal)
                            .snippet(timing0);

                    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(ctx2);
                    mMap.setInfoWindowAdapter(customInfoWindow);
                    mMap.setOnInfoWindowCloseListener(null);

                    Marker mk = mMap.addMarker(options);
                    if (MarkerMapRL == null) {
                        MarkerMapRL = new HashMap<Marker, RouteLocation>();
                    }
                    rl.setJobsIDXList(JobsIDXsPerPostal);
                    MarkerMapRL.put(mk, rl);
                    if (focusLocation) {
                        mk0 = mk;
                    }

                    /*if (!focusLocation && !objGlobalMethods.IsEmptyString(MapViewedPostal) && rl.Location.postal.equals(MapViewedPostal)) {
                        if (mk0 != null && mk0.isInfoWindowShown()) {
                            mk0.hideInfoWindow();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "MapViewedPostal", "");
                        }

                        mk0 = mk;
                        //windowOpened = false;
                        focusLocation = true;
                    }*/

                    if ((!focusLocation && !objGlobalMethods.IsEmptyString(MapViewedPostal) && rl.Location.postal.equals(MapViewedPostal)) ||
                            (focusLocation && objGlobalMethods.IsEmptyString(MapViewedPostal))) {
                        mk0 = mk;
                        focusLocation = true;
                    } else if (focusLocation && !objGlobalMethods.IsEmptyString(MapViewedPostal) && !rl.Location.postal.equals(MapViewedPostal)) {
                        focusLocation = false;
                    }
                }
                count += 1;
            }

            if (focusLocation) {
                try {
                    if (mk0 != null && !mk0.isInfoWindowShown()) {
                        //objGlobalMethods.setGCMPrefs(GcmPreferences, "MapViewedPostal", "");
                        mk0.showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mk0.getPosition(), 12));
                        LatLng adeslatlng = mk0.getPosition();
                        double lat = adeslatlng.latitude;
                        double lng = adeslatlng.longitude;
                        showfabWaze(String.valueOf(lat) + "," + String.valueOf(lng));
                    }
                } catch (Exception e) {
                    String sss = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMap2SQLiteCustomIcons 002. Exception: " + e.getMessage() + "\nStack: " + sss, "", "", "", "");
                }
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMap2SQLiteCustomIconsLoadMarkers. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void setUpMarkerClick() {
        try {
            if (pgLoading != null && pgLoading.isShowing())
                pgLoading.dismiss();

            String nonSysCalRoute = objGlobalMethods.getGCMPrefs(GcmPreferences, "nonSysCalRoute");
            if (!nonSysCalRoute.equals("")) {
                Toast.makeText(myApp.getAppContext(), myApp.getAppContext().getString(R.string.toastnoncalculatedroute), Toast.LENGTH_LONG).show();
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    RouteLocation rl = null;
                    try {
                        if (MarkerMapRL != null && MarkerMapRL.get(marker) != null) {
                            rl = MarkerMapRL.get(marker);
                            String postal = rl.Location.postal;
                            String Latstr = rl.Location.lat;
                            String Lngstr = rl.Location.lon;

                            GcmPreferences = objGlobalMethods.getGcmPreferences();
                            //objGlobalMethods.setGCMPrefs(GcmPreferences, "MapViewedPostal", postal);

                            String[] pName = new String[]{
                                    "LAT",
                                    "LNG",
                                    "enableJobUpdates",
                                    "MapViewedPostal"};
                            String[] pvalue = new String[]{
                                    String.valueOf(Latstr),
                                    String.valueOf(Lngstr),
                                    String.valueOf(rl.IsEnabledForJobUpdate),
                                    postal};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);
                            marker.showInfoWindow();

                            showfabWaze(Latstr + "," + Lngstr);
                        }
                    } catch (Exception e) {
                        String s = JobsHandler.getStackTraceString(e);

                        objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMarkerClick mMap.setOnMarkerClickListener. Exception: " + s, "", "", "", "");
                    }
                    return false;
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent i = new Intent(MapsAllActivity.this, DashboardSubActivity.class);
                    RouteLocation rl = null;
                    int brkStartIDX = -1;
                    try {
                        GcmPreferences = objGlobalMethods.getGcmPreferences();
                        if (MarkerMapRL != null) {
                            rl = MarkerMapRL.get(marker);
                            if (rl != null) {
                                String snippet = marker.getSnippet();
                                String postal = rl.Location.postal;
                                String fulladdress = marker.getTitle();
                                brkStartIDX = fulladdress.indexOf("\n");
                                if (brkStartIDX > 0) {
                                    fulladdress = fulladdress.substring(0, brkStartIDX);
                                }
                                LatLng adeslatlng = marker.getPosition();
                                double LATval = 0;
                                double LNGval = 0;
                                LATval = adeslatlng.latitude;
                                LNGval = adeslatlng.longitude;
                                /*objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(LATval));
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(LNGval));
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "desPostal", postal);
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "desAddress", fulladdress);*/

                                String[] pName = new String[]{
                                        "LAT",
                                        "LNG",
                                        "desPostal",
                                        "desAddress",
                                        "MapViewedPostal"};
                                String[] pvalue = new String[]{
                                        String.valueOf(LATval),
                                        String.valueOf(LNGval),
                                        postal,
                                        fulladdress,
                                        postal};
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, pName, pvalue);

                                DashboardSubActivity.frompage = "MapsAllActivity";
                                startActivity(i);
                                finish();
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    } catch (Exception e) {
                        String s = e.getMessage();
                        String ex = JobsHandler.getStackTraceString(e);
                        s += "\nStack: " + ex;
                        objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMarkerClick mMap.setOnInfoWindowClickListener. Exception: " + s, "", "", "", "");
                    }
                }
            });

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (fabWaze != null && fabWaze.isShown()) {
                        fabWaze.hide();
                        //llwaze.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity setUpMarkerClick. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private GoogleMap.OnInfoWindowCloseListener mapInfoWinCloseListener() {
        return new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                try {
                    if (fabWaze != null && fabWaze.isShown()) {
                        //fabWaze.hide();
                        ////llwaze.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                }
            }
        };
    }

    private void showfabWaze(final String latlng) {
        try {
            if (fabWaze != null) {
                fabWaze.setOnClickListener(null);
            } else {
                fabWaze = findViewById(R.id.fabWaze);
            }
            fabWaze.setOnClickListener(fabWaze_OnClickListener(latlng));
            fabWaze.show();
            //llwaze.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity showfabWaze. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private View.OnClickListener fabWaze_OnClickListener(final String latlng) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!objGlobalMethods.IsEmptyString(latlng)) {
                        if (JobsHandler2.wazeInstalled()) {
                            //String url = "https://waze.com/ul?ll=1.32435477286284,103.802951580752&navigate=yes";
                            String url = "https://waze.com/ul?ll=" + latlng + "&navigate=yes";
                            //String url = "https://waze.com/ul?q=Singapore " + postalstr + "&navigate=yes";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at MapsAllActivity MapsAllMainlinearLayout1_OnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at MapsAllActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsAllActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }



}
