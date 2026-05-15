package com.xdel.luke;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xdel.luke.objClasses.objGlobalMethods;

public class QRReaderActivity extends Activity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    static String cameraScannedCode;
    private SharedPreferences GcmPreferences;
    private static boolean requireDownload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        setContentView(R.layout.activity_qrreader);

        try {
            Intent intent = new Intent(ACTION_SCAN);

            intent.putExtra("SCAN_MODE", "SCAN_MODE");//2nd parameter scan_mode means scan anything
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(QRReaderActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrreader, menu);
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
        back();
    }

    private void back() {
        Intent parentIntent = null;
        String requestIntentName = "";
        GcmPreferences = getGcmPreferences();
        boolean IsMaintenanceHours = objGlobalMethods.currTimeIsBetweenFromDateToDateToIgnore();
        try {
            requestIntentName = objGlobalMethods.getGCMPrefs(GcmPreferences, "QRScanRequestIntent");
            if (!requestIntentName.equals("")) {
                if (requestIntentName.equals("dashboard")) {
                    parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                } else if (requestIntentName.equals("addresslist")) {
                    parentIntent = new Intent(this, AddressListNewActivity.class);
                } else if (requestIntentName.equals("jobslist")) {
                    parentIntent = new Intent(this, JobsListGroupActivity.class);
                } else if (requestIntentName.equals("jobinfo")) {
                    parentIntent = new Intent(this, JobInfoActivity.class);
                } else if (requestIntentName.equals("bulkpickup")) {
                    parentIntent = new Intent(this, BulkPickUpActivity.class);
                } else if (requestIntentName.equals("jobsheet")) {
                    parentIntent = new Intent(this, JobsheetActivity.class);
                } else if (requestIntentName.equals("jobsheetsubmit")) {
                    parentIntent = new Intent(this, JobsheetSubmitActivity.class);
                } else if (requestIntentName.equals("checkstatus")) {
                    parentIntent = new Intent(this, CheckStatusActivity.class);
                } else if (requestIntentName.equals("creditcard")) {
                    parentIntent = new Intent(this, CreditCardActivity.class);
                }
            } else {
                parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() || IsMaintenanceHours ? DrawerMainActivity.class : DrawerMain2Activity.class);
                MainActivity.uponLogin = 0;
            }
        } catch (Exception e) {
            parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        } finally {
            requireDownload = false;
            MainActivity.uponLogin = 0;
            JobInfoActivity.accessFromDirection = 0;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "");
            startActivity(parentIntent);
            this.finish();
        }
    }

    public void scanBar(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "SCAN_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            requireDownload = true;
            showDialog(QRReaderActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void scanQR(View v) {
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            requireDownload = true;
            showDialog(QRReaderActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //back();
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                GcmPreferences = getGcmPreferences();
                cameraScannedCode = contents;
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", cameraScannedCode);
                Toast toast = Toast.makeText(this, "Value:" + contents, Toast.LENGTH_LONG);
                toast.show();
                this.finish();
            } else if (resultCode == RESULT_CANCELED) {
                if (requireDownload) {
                    requireDownload = false;
                } else {
                    requireDownload = false;
                    back();
                }

            }
        }

    }

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}
