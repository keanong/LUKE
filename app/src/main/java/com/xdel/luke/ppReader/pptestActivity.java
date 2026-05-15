package com.xdel.luke.ppReader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

public class pptestActivity extends AppCompatActivity {
    private SharedPreferences g;
    ConnectionDetector cd;
    private String langType = "en";
    ScreenShotContentObserver screenShotContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        langType = objGlobalMethods.getGCMPrefs(g, "language");
        setContentView(R.layout.activity_pptest);


        screenShotContentObserver = objGlobalMethods.getScreenshotObserver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {

            } catch (Exception e) {
                String s = JobsHandler.getStackTraceString(e);
            }
        }
    }

    public void btnpptestVCANCEL_Click(View v) {
        Intent i = new Intent(myApp.currentActivity, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void btnpptestVScan_Click(View v) {

    }

}