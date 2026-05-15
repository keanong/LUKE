package com.xdel.luke;

import android.content.Context;
import android.content.Intent;

import androidx.core.app.NavUtils;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.xdel.luke.objClasses.objGlobalMethods;


//public class ViewNotificationActivity extends ActionBarActivity {
public class ViewNotificationActivity extends AppCompatActivity {

    TextView lblNoti;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "Luke";
    public  int myNOTIFICATION_ID;
    public  String cnNo = "";
    public  String rdyDatetime = "";
    public  String tos = "";
    public  String puContact = "";
    public  String puContactTel = "";
    public  String puAddress = "";
    public  String delAddress = "";
    public  String delContact = "";
    public  String delContactTel = "";
    public  String colBytime = "";
    public  String SI = "";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        setContentView(R.layout.activity_view_notification);

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        context = getApplicationContext();
        lblNoti = (TextView)findViewById(R.id.lblNoti);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        displayIntentData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        //displayIntentData();
    }

    protected void displayIntentData(){
        Intent intent = getIntent();
        String newMessage = intent.getStringExtra("Message");

        lblNoti.setText(newMessage);
        //NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.cancel(myNOTIFICATION_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_notification, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Intent parentIntent = new Intent(this, Dashboard2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
        MainActivity.uponLogin = 0;
        //Dashboard2Activity.DashboardOpenFromPush = false;
        DrawerMainActivity.DashboardOpenFromPush = false;
        startActivity(parentIntent);
        this.finish();
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent parentIntent= getIntent();

        Intent newIntent=null;

            //you need to define the class with package name
            //newIntent = new Intent(ViewNotificationActivity.this,Dashboard2Activity.class);
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        newIntent = new Intent(ViewNotificationActivity.this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);

        return newIntent;
    }
}
