package com.xdel.luke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;
import com.xdel.luke.DOCOD.CODupdate2;
import com.xdel.luke.HandTakeOver.HandoverActivity;
import com.xdel.luke.HandTakeOver.TakeoverActivity;
import com.xdel.luke.PieceByPiecePU.StartQueryActivity;
import com.xdel.luke.ShpmtVerification.ShpmtVActivity;
import com.xdel.luke.inventory.InventoryMainActivity;
import com.xdel.luke.mpd.MPDMainActivity;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.temploggerclass;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRBarcodeReader extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    static String cameraScannedCode;
    private SharedPreferences GcmPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myApp.setAppContext(this);
        setContentView(R.layout.activity_qrbarcode_reader);

        try {
            scannerView = (ZXingScannerView) findViewById(R.id.scanner_view);
            /*scannerView.startCamera();
            scannerView.setResultHandler(this);*/
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            scannerView.setResultHandler(this);
            scannerView.setAutoFocus(true);
            scannerView.startCamera();
        } catch (Exception e) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        //Intent parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        Intent parentIntent = new Intent(this, MainActivity.class);
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
                } else if (requestIntentName.equals("jobinfop2")) {
                    parentIntent = new Intent(this, JobInfoP2Activity.class);
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
                } else if (requestIntentName.equals("tagging")) {
                    parentIntent = new Intent(this, Label_Tagging_Activity.class);
                } else if (requestIntentName.equals("itemgroupcheck")) {
                    parentIntent = new Intent(this, ItemGroupCheck.class);
                } else if (requestIntentName.equals("jobsretrieval")) {
                    parentIntent = new Intent(this, Jobs_Retrieval_Activity.class);
                } else if (requestIntentName.equals("securityseal")) {
                    parentIntent = new Intent(this, SecuritySealActivity.class);
                } else if (requestIntentName.equals("mpd")) {
                    parentIntent = new Intent(this, MPD_CheckOutActivity.class);
                } else if (requestIntentName.equals("CODupdate2")) {
                    parentIntent = new Intent(this, CODupdate2.class);
                }/* else if (requestIntentName.equals("cagescan")) {
                    parentIntent = new Intent(this, CageScanActivity.class);
                }  else if (requestIntentName.equals("cageinout")) {
                    parentIntent = new Intent(this, CageInOutActivity.class);
                }*/ else if (requestIntentName.equals("mainactivity")) {
                    parentIntent = new Intent(this, MainActivity.class);
                } else if (requestIntentName.equals("takeover")) {
                    parentIntent = new Intent(this, TakeoverActivity.class);
                } else if (requestIntentName.equals("handover")) {
                    parentIntent = new Intent(this, HandoverActivity.class);
                } else if (requestIntentName.equals("MPDMainActivity")) {
                    parentIntent = new Intent(this, MPDMainActivity.class);
                } else if (requestIntentName.equals("InventoryMainActivity")) {
                    parentIntent = new Intent(this, InventoryMainActivity.class);
                } else if (requestIntentName.equals("ShpmtV")) {
                    parentIntent = new Intent(this, ShpmtVActivity.class);
                } else if (requestIntentName.equals("bpu2")) {
                    parentIntent = new Intent(this, StartQueryActivity.class);
                }
            } else {
                //parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
                parentIntent = new Intent(this, MainActivity.class);
                MainActivity.uponLogin = 0;
            }
        } catch (Exception e) {
            parentIntent = new Intent(this, !objGlobalMethods.IsDefaultVRPpage() ? DrawerMainActivity.class : DrawerMain2Activity.class);
        } finally {
            MainActivity.uponLogin = 0;
            JobInfoActivity.accessFromDirection = 0;
            objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
            objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "");
            //startActivity(parentIntent);
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrbarcode_reader, menu);
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
    public void handleResult(Result result) {
        GcmPreferences = getGcmPreferences();
        try {
            String requestIntentName = objGlobalMethods.getGCMPrefs(GcmPreferences, "QRScanRequestIntent");
            if (result != null && !result.getText().equals("")) {
                if (requestIntentName.equals("securitysealactivity")) {
                    if (SecuritySealActivity.al != null && SecuritySealActivity.al.size() > 0 && SecuritySealActivity.itemIndex != null
                            && SecuritySealActivity.al.size()-1 >= SecuritySealActivity.itemIndex && SecuritySealActivity.itemIndex > -1) {
                     SecuritySealActivity.al.get(SecuritySealActivity.itemIndex).setEnteredValue(result.getText());
                     SecuritySealActivity.itemIndex = null;
                    }
                } else {
                    cameraScannedCode = result.getText();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", cameraScannedCode);
                }
            } else {
                objGlobalMethods.setGCMPrefs(GcmPreferences, "cameraScannedCode", "");
                Toast toast = Toast.makeText(this, "Unable to detect.", Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (Exception e){
            objGlobalMethods.writeToFile("Exception at QRBarcodeReader handleResult. Exception: " + e.getMessage().toString(),"","","","");
        } finally {
            this.finish();
        }
    }

    private SharedPreferences getGcmPreferences() {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        try {
            //Log.i("Main", "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    objGlobalMethods.writeToFile("Bluetooth is on at QRBarcodeReader","","","","");
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    temploggerclass.disposeTimer();
                    temploggerclass.startTimer(5000);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at QRBarcodeReader onActivityResult. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

}
