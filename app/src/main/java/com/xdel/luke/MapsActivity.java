package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.ui.IconGenerator;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.CustomInfoWindowGoogleMap;
import com.xdel.luke.objClasses.HttpConnection;
import com.xdel.luke.objClasses.LocationProvider;
import com.xdel.luke.objClasses.PathJSONParser;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements LocationProvider.LocationCallback, OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    double lat, lng, currentLatitude, currentLongitude;
    private LocationProvider mLocationProvider;
    LatLng deslatlng, currLatLng;
    String directionURL = "";
    Context context;
    ConnectionDetector cd;
    ScreenShotContentObserver screenShotContentObserver;
    FloatingActionButton fabWaze;
    
    private SharedPreferences GcmPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_maps);

        //setUpMapIfNeeded();

        mLocationProvider = new LocationProvider(this, this);
        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            GcmPreferences = objGlobalMethods.getGcmPreferences();
            if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            }
            //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
            myApp.currentActivity = this;
            context = myApp.getAppContext();

            if (!temploggerclass.isBTEnabled()) {
                objGlobalMethods.writeToFile("MapsActivity !BTEnabled", "", "", "", "");
                temploggerclass.promptToOnBT();
                return;
            }


            //cd = new ConnectionDetector(context);
            cd = new ConnectionDetector(null);

            if (JobsHandler.hasOpsQ() && !JobsHandler.isReadingFirstQue_Location_Or_Job(myApp.currentActivity.getClass())) {
                JobsHandler.redirectToVrpPage();
            } else {
                SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
                mapFragment.getMapAsync(this);

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
                objGlobalMethods.checkToPromptNotiForBattCharging();*/
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at MapsActivity onResume. Exception: " + s,"","","","");
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

            mLocationProvider.connect();
            //setUpMapIfNeeded();
            setUpMapNew();
            objGlobalMethods.checkToPromptNotiForBattCharging();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at MapsActivity onMapReady. Exception: " + s,"","","","");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
        myApp.currentActivity = null;
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MapsActivity onPause unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GcmPreferences = null;
        //objGlobalMethods.clearStaticActivities();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at MapsActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(),"","","","");
        }
        DAO_Jobs.nullifystaticitems();
        mMap = null;
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
                i = new Intent(MapsActivity.this, JobInfoActivity.class);
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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            /*mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();*/
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMapNew();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void setUpMapNew() {
        double LATval = 0;
        double LNGval = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Context ctx = myApp.getAppContext();
        Context ctx2 = (Context) myApp.currentActivity;

        String LATstr = "";
        String LNGstr = "";
        String lrLATstr = "";
        String lrLNGstr = "";

        String desAddressStr = "";
        String lrdesAddressStr = "";
        String address = "";

        Resources resources = ctx.getResources();
        int catType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);
        try {
            LATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LAT");
            LNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LNG");
            lrLATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "lrLAT");
            lrLNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "lrLNG");

            desAddressStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "desAddress");
            lrdesAddressStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "lrdesAddress");

            LATval = !objGlobalMethods.IsEmptyString(lrLATstr) ? Double.parseDouble(lrLATstr) : !objGlobalMethods.IsEmptyString(LATstr) ? Double.parseDouble(LATstr) : 0;
            LNGval = !objGlobalMethods.IsEmptyString(lrLNGstr) ? Double.parseDouble(lrLNGstr) : !objGlobalMethods.IsEmptyString(LNGstr) ? Double.parseDouble(LNGstr) : 0;

            address = !objGlobalMethods.IsEmptyString(lrdesAddressStr) ? lrdesAddressStr : !objGlobalMethods.IsEmptyString(desAddressStr) ? desAddressStr : "";

            if (LATval != 0 && LNGval != 0) {
                deslatlng = new LatLng(LATval, LNGval);
                int iconRID = catType == 0 ? R.mipmap.map_marker_pip_black : R.mipmap.map_marker_dip_black_new;
                Bitmap icon = MapsAllActivity.makeBitmap(resources, iconRID, Color.BLACK, "");

                MarkerOptions options = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .position(deslatlng)
                        .title(address);

                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(ctx2);
                mMap.setInfoWindowAdapter(customInfoWindow);

                Marker mk = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deslatlng, 12));
                mk.showInfoWindow();

                showfabWaze(String.valueOf(LATval) + "," + String.valueOf(LNGval));

                final double lt = LATval;
                final double lg = LNGval;
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        showfabWaze(String.valueOf(lt) + "," + String.valueOf(lg));
                        return false;
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

            } else {
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_invalidaddress));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile( "Exception caught at MapsActivity setUpMapNew. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_invalidaddress));
        }
    }

    private void setUpMap() {
        double LATval = 0;
        double LNGval = 0;
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        Context ctx = myApp.getAppContext();
        String LATstr = "";
        String LNGstr = "";

        try {
            LATstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LAT");
            LNGstr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LNG");
            if (LATstr.equals("")) {
                LATval = 0;
            } else {
                LATval = Double.parseDouble(LATstr);
            }

            if (LNGstr.equals("")) {
                LNGval = 0;
            } else {
                LNGval = Double.parseDouble(LNGstr);
            }

            if (LATval != 0 && LNGval != 0) {
                deslatlng = new LatLng(LATval, LNGval);;
                IconGenerator iconFactory = new IconGenerator(this);
                //iconFactory.setStyle( IconGenerator.STYLE_GREEN);
                iconFactory.setColor(Color.GREEN);
                iconFactory.setTextAppearance(R.style.mapGreenMarkerText);
                Bitmap icon = iconFactory.makeIcon("  ");

                MarkerOptions options = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(icon))
                        .position(deslatlng)
                        .title(objGlobalMethods.getGCMPrefs(GcmPreferences, "desAddress"));

                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(ctx);
                mMap.setInfoWindowAdapter(customInfoWindow);

                Marker mk = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deslatlng, 12));
                mk.showInfoWindow();


                /*deslatlng = new LatLng(LATval, LNGval);
                MarkerOptions options = new MarkerOptions()
                        .position(deslatlng)
                        .title(objGlobalMethods.getGCMPrefs(GcmPreferences, "desAddress"))
                        .snippet("Singapore " + objGlobalMethods.getGCMPrefs(GcmPreferences, "desPostal"));

                Marker mk = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deslatlng, 18));
                mk.showInfoWindow();*/
            } else {
                showErrorAlert(myApp.getAppContext().getString(R.string.alert_invalidaddress));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile( "Exception caught at MapsActivity setUpMap. Exception: " + e.toString(), "", "", "", "");
            showErrorAlert(myApp.getAppContext().getString(R.string.alert_invalidaddress));
        }
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

        directionURL = getMapsApiDirectionsUrl(currentLatitude, currentLongitude, lat, lng);
        getDirection downloadTask = new getDirection();
        downloadTask.execute(directionURL);

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
                for (int i = 0; i < routes.size(); i++) {
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
                }

                if (polyLineOptions != null) {
                    mMap.addPolyline(polyLineOptions);
                }

                //mMap.addPolyline(polyLineOptions);
            } else {
                /*Date dt = new Date();
                objGlobalMethods.writeToFile( "MapsActivity ParserTask onPostExecute has a null routes array. Route not plotted.", "", "", "", "");*/
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
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "showBattChargeNotiPrefStr", asd.toString());
                dialog.dismiss();
            }
        });

        return builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at MapsActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MapsActivity onActivityResult. Excpetion: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
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
            objGlobalMethods.writeToFile("Exception at MapsActivity showfabWaze. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                    objGlobalMethods.writeToFile("Exception at MapsActivity fabWaze_OnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, myApp.getAppContext().getString(R.string.buttontext_ok), myApp.getAppContext().getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }


}
