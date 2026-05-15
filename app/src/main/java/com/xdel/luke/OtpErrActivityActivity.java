package com.xdel.luke;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objJobInfo;

import java.util.Date;

public class OtpErrActivityActivity extends AppCompatActivity {

    Button btnVerified, btnRetry;
    EditText edittext0;

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //disable taking screen shot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //disable taking screen shot

        setContentView(R.layout.activity_otp_err);

        btnVerified = (Button) findViewById(R.id.btnVerified);
        btnRetry = (Button) findViewById(R.id.btnRetry);
        edittext0 = (EditText) findViewById(R.id.edittext0);
    }

    @Override
    protected void onResume(){
        super.onResume();
        try{

        }
        catch (Exception e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_otp_err, menu);
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
        Intent intent  = new Intent(this,OtpActivity.class);
        startActivity(intent);
        this.finish();
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

    protected void btnVerifiedClick(View v) {
        try {
            if (!edittext0.getText().toString().isEmpty()) {
                Gson gson = new Gson();
                SharedPreferences GcmPreferences = getGcmPreferences();
                objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                currJobInfo.setTypeOfPinVerification(2);
                currJobInfo.setManualPinVerificaionText(edittext0.getText().toString().toUpperCase());
                String hfJobInfoJsonStr = gson.toJson(currJobInfo);
                //objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", hfJobInfoJsonStr);
                String[] prefName = null;
                String[] prefvalue = null;
                prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr"};
                prefvalue = new String[]{hfJobInfoJsonStr, String.valueOf(currJobInfo.getTypeOfPinVerification())};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                Date dt = new Date();
                objGlobalMethods.writeToFile("Courier confirmed shipment verified for delivery manually due OTP verification error. " +
                        "Manual verification code: " + edittext0.getText().toString().toUpperCase(),"","","","");
                Intent intent = new Intent (this, setDeliveredStatusActivity.class);
                startActivity(intent);
                this.finish();
            } else {
                showDialog(myApp.getAppContext().getString(R.string.otplblotperrlabel4));
            }
        }
        catch (Exception e) {
            Gson gson = new Gson();
            SharedPreferences GcmPreferences = getGcmPreferences();
            objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            currJobInfo.setTypeOfPinVerification(2);
            currJobInfo.setManualPinVerificaionText(edittext0.getText().toString().toUpperCase());
            String hfJobInfoJsonStr = gson.toJson(currJobInfo);
            objGlobalMethods.setGCMPrefs(GcmPreferences, "hfJobInfoJsonStr", hfJobInfoJsonStr);
            String[] prefName = null;
            String[] prefvalue = null;
            prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr"};
            prefvalue = new String[]{hfJobInfoJsonStr, String.valueOf(currJobInfo.getTypeOfPinVerification())};
            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at OtpErrActivityActivity btnVerifiedClick. Manual verification code not recorded. Exception: " + e.getMessage().toString(),"","","","");
            Intent intent  = new Intent(this,setDeliveredStatusActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    protected void btnRetryClick(View v) {
        try {
            Gson gson = new Gson();
            SharedPreferences GcmPreferences = getGcmPreferences();
            objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
            Date dt = new Date();
            objGlobalMethods.writeToFile("Courier choose retry OTP verification for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX) + " . Redirecting str8 to OTP page.","","","","");
            Intent intent = new Intent (this, setDeliveredStatusActivity.class);
            startActivity(intent);
            this.finish();
        }
        catch (Exception e) {
            Date dt = new Date();
            objGlobalMethods.writeToFile("Exception caught at OtpErrActivityActivity btnRetryClick. Redirecting str8 to OTP page. Exception: " + e.getMessage().toString(),"","","","");
            Intent intent  = new Intent(this,OtpActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}
