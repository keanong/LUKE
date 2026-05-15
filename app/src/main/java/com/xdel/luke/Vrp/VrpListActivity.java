package com.xdel.luke.Vrp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.WindowManager;

import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.List;
import java.util.Locale;

import static com.xdel.luke.objClasses.objGlobalMethods.getGcmPreferences;

public class VrpListActivity extends AppCompatActivity {
    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_vrp_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        try {
            //myApp.setAppContext(this);
            GcmPreferences = getGcmPreferences();
            langType = objGlobalMethods.getGCMPrefs(GcmPreferences, "language");

            String languageToLoad = langType; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            //disable taking screen shot
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            //disable taking screen shot
            setContentView(R.layout.activity_vrp_list);

            rv = findViewById(R.id.rv);
            rv.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at VrpListActivity onCreate. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            JobsHandler.currentActivity = this;
            myApp.currentActivity = this;
            Context ctx = myApp.getAppContext();
            List<JobQueue> NewJQlist = null;
            try {
                NewJQlist = JobsHandler.GetProcessJobQue6();

                if (NewJQlist != null && NewJQlist.size() > 0) {
                    loadlist(NewJQlist);
                } else {
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nodata));
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at VrpListActivity onResume. Excpetion: " + e.getMessage().toString(), "", "", "", "");
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_nodata));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(VrpListActivity.this, DrawerMainActivity.class);
        startActivity(i);
        finish();
    }

    private void loadlist(List<JobQueue> NewJQlist) {
        try {
            VrpList_RecycleViewAdapter adapter = new VrpList_RecycleViewAdapter();
            rv.setAdapter(adapter);
            adapter.setNewJQlist(NewJQlist);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at VrpListActivity loadlist. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
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

}
