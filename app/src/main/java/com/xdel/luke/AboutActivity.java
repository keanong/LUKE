package com.xdel.luke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;


//public class AboutActivity extends ActionBarActivity {
public class AboutActivity extends AppCompatActivity {

    Context context;
    private TextView lblVer;
    private TextView lblAboutLoginVal;

    private static SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String username;
    String password;
    String useridx;
    String ishomesuser;
    String[] UsernamePwd = new String[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        //disable taking screen shot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();

        lblVer = (TextView)findViewById(R.id.lblVer);
        lblAboutLoginVal = (TextView)findViewById(R.id.lblAboutLoginVal);
    }

    @Override
    protected void onResume(){
        //context = getApplicationContext();
        context = myApp.getAppContext();
        GcmPreferences = getGcmPreferences(context);
        UpdateJobInfoPreferences = getUpdateJobInfoPreferences(context);

        lblVer.setText("Version " + getAppVersionName(context));

        UsernamePwd = objGlobalMethods.getLoginUsernamePwd(GcmPreferences);
        username = UsernamePwd[0];
        useridx = UsernamePwd[2];
        ishomesuser = UsernamePwd[3];

        lblAboutLoginVal.setText(username.toUpperCase());

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
    public void onBackPressed() {
        //Dashboard2Activity.handler.removeCallbacks(runnable);
        //Intent parentIntent = new Intent(this, Dashboard2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        Intent parentIntent = new Intent(this, !IsMaintenanceHours && objGlobalMethods.IsDefaultVRPpage() ? DrawerMain2Activity.class : DrawerMainActivity.class);
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    private static String getAppVersionName(Context context) {
        try {
            String versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);

        }
    }

    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private SharedPreferences getUpdateJobInfoPreferences(Context context){
        return getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);
    }
}
