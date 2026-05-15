package com.xdel.luke;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.Screenshot.ScreenShotContentObserver;
import com.xdel.luke.objClasses.SecuritySeal_PostData;
import com.xdel.luke.objClasses.SecuritySeal_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.templogger.temploggerclass;

import java.util.ArrayList;
import java.util.List;

public class SecuritySealActivity extends AppCompatActivity {

    private SharedPreferences GcmPreferences, UpdateJobInfoPreferences;
    String langType = "en";
    public static String fromPage = "";
    public static Intent nextActivtyIntent;
    public static Integer itemIndex;
    public static ArrayList<SecuritySeal_PostData> al;
    private ListView listview;
    private Button btnCancel, btnNext;
    public SecuritySeal_PostItemAdapter adapter;
    ScreenShotContentObserver screenShotContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
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

            setContentView(R.layout.activity_security_seal);
            listview = (ListView) findViewById(R.id.listview);
            btnNext = (Button) findViewById(R.id.btnNext);

            screenShotContentObserver = objGlobalMethods.getScreenshotObserver();
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SecuritySealActivity onCreate. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (FCMservice.CURRENTACTIVIYLOCK) {
            try {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                if (!objGlobalMethods.UserCanScreenShot(GcmPreferences)){
                    getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                }
                //getContentResolver().registerContentObserver( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, screenShotContentObserver);
                JobsHandler.currentActivity = this;
                myApp.currentActivity = this;

                if (!temploggerclass.isBTEnabled()) {
                    objGlobalMethods.writeToFile("SecuritySealActivity !BTEnabled", "", "", "", "");
                    temploggerclass.promptToOnBT();
                    return;
                }

                Context ctx = myApp.getAppContext();

                String securityseallistALJsonStr = "";
                securityseallistALJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "securityseallistALJsonStr");
                if (!securityseallistALJsonStr.equals("") && !securityseallistALJsonStr.equals("[]")) {
                    Gson gson = new Gson();
                    ArrayList<String> securityseallistAL;
                    securityseallistAL = gson.fromJson(securityseallistALJsonStr, new TypeToken<List<String>>() {
                    }.getType());
                    if (securityseallistAL != null && securityseallistAL.size() > 0) {
                        loadlist(securityseallistAL);
                    } else {
                        showErrorAlert2();
                    }
                } else {
                    showErrorAlert2();
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at SecuritySealActivity onResume. Exception: " + e.getMessage().toString(), "", "", "", "");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SecuritySealActivity onDestroy unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    protected void onPause() {
        try {
            if (screenShotContentObserver != null)
                getContentResolver().unregisterContentObserver(screenShotContentObserver);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SecuritySealActivity onPause unregisterContentObserver. Exception: " + e.getMessage(), "", "", "", "");
        }
        myApp.currentActivity = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_security_seal, menu);
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

    private void showErrorAlert2() {
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_errorretrivedata2));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                onBackPressed();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;
        intent = new Intent(this, setDeliveredStatusActivity.class);
        fromPage = null;
        nextActivtyIntent = null;
        MainActivity.uponLogin = 0;
        startActivity(intent);
        finish();
    }

    public void BackToLoginPage2() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(SecuritySealActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void BackToLoginPage() {
        GcmPreferences = objGlobalMethods.getGcmPreferences();
        UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
        JobsHandler.BackToLoginPageStandardToDO(GcmPreferences, UpdateJobInfoPreferences);
        Intent parentIntent;
        parentIntent = new Intent(SecuritySealActivity.this, MainActivity.class);
        startActivity(parentIntent);
        finish();
    }

    public void CancelsetDeliveredStatus(View v) {
        onBackPressed();
    }

    public void btnNextClick(View v) {
        if (al != null && al.size() > 0) {
            next();
        } else {
            Context ctx = myApp.getAppContext();
            objGlobalMethods.writeToFile("Unable to next SecuritySealActivity. al is null or empty.", "", "", "", "");
            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorupdatep1p2)).show();
        }
    }

    private void loadlist(ArrayList<String> ji) {
        SecuritySeal_PostData data;
        try {
            if (al == null) {
                al = new ArrayList<>();
                for (int i = 0; i <= ji.size() - 1; i++) {
                    data = new SecuritySeal_PostData("");
                    al.add(data);
                }
            }

            if (al != null && al.size() > 0) {
                adapter = new SecuritySeal_PostItemAdapter(this, al);
                listview.setAdapter(adapter);
            } else {
                showErrorAlert2();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SecuritySealActivity loadlist. Exception: " + e.getMessage().toString(), "", "", "", "");
            showErrorAlert2();
        }
    }

    private void next() {
        try {
            Boolean canproceed = canProceed();
            if (canproceed) {
                /*String securityseallistvalue = "Verified Security Seal Number(s):\n";
                for (int i = 0; i<=al.size()-1; i++) {
                    securityseallistvalue += al.get(i).EnteredValue + "\n";
                }*/
                GcmPreferences = objGlobalMethods.getGcmPreferences();
                Gson gson = new Gson();
                objJobInfo ji = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                boolean p1ReqSign = false;
                if (ji != null) {
                    p1ReqSign = (ji.FLAG & 1024) == 1024;
                    if (p1ReqSign) {
                        SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                        Intent i = new Intent(SecuritySealActivity.this, SignatureNameActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(SecuritySealActivity.this, setDeliveredStatusActivity.class);
                        startActivity(i);
                    }
                } else {
                    SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                    Intent i = new Intent(SecuritySealActivity.this, SignatureNameActivity.class);
                    startActivity(i);
                }



                /*SignatureNameActivity.rtnPageName = "setDeliveredStatusActivity";
                Intent i = new Intent(SecuritySealActivity.this, SignatureNameActivity.class);
                startActivity(i);*/
            } else {
                Context ctx = myApp.getAppContext();
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sealincomplete)).show();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SecuritySealActivity next. Exception: " + e.getMessage().toString(), "", "", "", "");
        }
    }

    private Boolean canProceed() {
        Boolean asd = true;
        try {
            for (int i = 0; i <= al.size() - 1; i++) {
                if (!al.get(i).Verified) {
                    asd = false;
                    break;
                }
            }
        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception at SecuritySealActivity canProceed. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return asd;
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
                    objGlobalMethods.writeToFile("Bluetooth is on at SecuritySealActivity","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at SecuritySealActivity onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
