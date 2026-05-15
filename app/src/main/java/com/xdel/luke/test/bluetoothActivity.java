package com.xdel.luke.test;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.tzone.AppInfo;
import com.tzone.bluetooth.BleManager;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;

import java.util.Timer;
import java.util.TimerTask;

public class bluetoothActivity extends BaseActivity {

    private final String TAG = "MainActivity";

    public TextView txtSDK;
    private String[] NeedPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private boolean hasRequestPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        txtSDK = findViewById(R.id.txtSDK);
        txtSDK.setText("SDK:" + new AppInfo().Version);

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(3, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);

        isBluetooth4();
    }

    @Override
    protected void onResume(){
        try {

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }

    public void isBluetooth4() {
        try {
            if (!BleManager.getInstance().isSupportBle()) {
                Toast.makeText(this, "It is detected that the device does not support Bluetooth 4.0 BLE", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // If the local Bluetooth is not turned on, it is turned on
            if (!BleManager.getInstance().isBlueEnable()) {
                // The Intent that we initiated with the startActivityForResult () method will get the user's choice in the onActivityResult () callback method, such as when the user clicks Yes,
                // Then you will receive the result of RESULT_OK,
                // If RESULT_CANCELED on behalf of the user does not want to turn on Bluetooth
                Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(mIntent, 1);
                // Use the enable () method to open, without asking the user (affordable Bluetooth device open), then you need to use android.permission.BLUETOOTH_ADMIN authority.
                // mBluetoothAdapter.enable();
                // mBluetoothAdapter.disable();
            } else {
                isOpenGPS();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String l = s;
        }

    }

    private void isOpenGPS() {
        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                Toast.makeText(this, "Please open on GPS!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 2);
            } else {
                DangerousPermissions();
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String l = s;
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
                    Toast.makeText(this, "Bluetooth is on！", Toast.LENGTH_SHORT).show();
                    isOpenGPS();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Bluetooth is not open！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else if (requestCode == 2) {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "GPS is on！", Toast.LENGTH_SHORT).show();
                    ToStart();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "GPS is not open！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else if (requestCode == 3) {
                DangerousPermissions();
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String l = s;
        }

    }

    public void DangerousPermissions() {

        try {
            if (checkDangerousPermissions(NeedPermissions)) {
                ToStart();
            } else {
                if (!hasRequestPermission) {
                    hasRequestPermission = true;
                    requestDangerousPermissions(NeedPermissions, 1);
                }
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String l = s;
        }

    }

    @Override
    public boolean handlePermissionResult(int requestCode, boolean granted) {

        try {
            if (requestCode == 1) {
                if (!granted) {
                    Toast.makeText(this, "Permission open failed! The program exits.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    ToStart();
                }
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String l = s;
        }

        return super.handlePermissionResult(requestCode, granted);
    }

    public void ToStart() {

        try {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(bluetoothActivity.this, ScanActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            timer.schedule(task, 1000);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String l = s;
        }

    }


}