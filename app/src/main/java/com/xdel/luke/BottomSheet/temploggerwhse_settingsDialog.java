package com.xdel.luke.BottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.temploggerWhse.TempLoggerWhseScanActivity;
import com.xdel.luke.temploggerWhse.temploggerWhseReadingSettings;

import java.text.DecimalFormat;

public class temploggerwhse_settingsDialog extends DialogFragment {
    private holder h;
    public static Dialog dl;
    public boolean launchCFS = false;

    public void setLaunchCFS(boolean launchCFS) {
        this.launchCFS = launchCFS;
    }

    public temploggerwhse_settingsDialog() {

    }

    public static temploggerwhse_settingsDialog display(FragmentManager fragmentManager) {
        temploggerwhse_settingsDialog fs = new temploggerwhse_settingsDialog();
        try {
            fs.show(fragmentManager, "temploggerwhsesettingsDialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerwhse_settingsDialog display. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return fs;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        h = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dl = dialog;
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
            dialog.setCancelable(false);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_temploggerwhse_settings, container, false);
        h = new holder();
        h.txtMain = view.findViewById(R.id.txtMain);
        h.txtDeviceScanning = view.findViewById(R.id.txtDeviceScanning);
        h.lblMainText1 = view.findViewById(R.id.lblMainText1);
        h.lblDeviceScanningText1 = view.findViewById(R.id.lblDeviceScanningText1);
        h.btnTemploggerWhse_dialogCancel = view.findViewById(R.id.btnTemploggerWhse_dialogCancel);
        h.btnTemploggerWhse_dialogSave = view.findViewById(R.id.btnTemploggerWhse_dialogSave);
        return view;
    }

    public static String convertSecondsToMinutesStr(String str) {
        String v = "";
        DecimalFormat df = new DecimalFormat("#.#");
        try {
            if (!objGlobalMethods.IsEmptyString(str))
                v = df.format((Double.parseDouble(str) / 60));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerwhse_settingsDialog convertSecondsToMinutesStr. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return  v;
        }
    }

    public static String convertMillisecondsToMinutesStr(String str) {
        String v = "";
        DecimalFormat df = new DecimalFormat("#.#");
        try {
            if (!objGlobalMethods.IsEmptyString(str))
                v = df.format((Double.parseDouble(str) / 60000)); //60000 == 60 seconds * 1000
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerwhse_settingsDialog convertMillisecondsToMinutesStr. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return  v;
        }
    }

    private class txtMainTextChangeListener implements TextWatcher {

        public txtMainTextChangeListener() {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = h.txtMain.getText().toString();
            if (str.isEmpty()) {
                h.lblMainText1.setText("0");
                return;
            }
            try {
                String str2 = convertSecondsToMinutesStr(str);

                if (!str2.equals(str)) {
                    h.lblMainText1.setText(str2);
                    h.txtMain.setSelection(str.length());
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at temploggerwhse_settingsDialog txtMainTextChangeListener afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss,"","","","");
            }
        }
    }

    private class txtDeviceScanningTextChangeListener implements TextWatcher {

        public txtDeviceScanningTextChangeListener() {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = h.txtDeviceScanning.getText().toString();
            if (str.isEmpty()) {
                h.lblDeviceScanningText1.setText("0");
                return;
            }
            try {
                String str2 = convertSecondsToMinutesStr(str);

                if (!str2.equals(str)) {
                    h.lblDeviceScanningText1.setText(str2);
                    h.txtDeviceScanning.setSelection(str.length());
                }
            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at temploggerwhse_settingsDialog txtDeviceScanningTextChangeListener afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss,"","","","");
            }
        }
    }

    public final class holder {
        EditText txtMain;
        EditText txtDeviceScanning;
        TextView lblMainText1;
        TextView lblDeviceScanningText1;

        txtMainTextChangeListener txtMain_TextChangeListener;
        txtDeviceScanningTextChangeListener txtDeviceScanning_TextChangeListener;

        Button btnTemploggerWhse_dialogCancel;
        Button btnTemploggerWhse_dialogSave;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            h.btnTemploggerWhse_dialogCancel.setOnClickListener(null);
            h.btnTemploggerWhse_dialogSave.setOnClickListener(null);
            if (h.txtMain_TextChangeListener != null) {
                h.txtMain.removeTextChangedListener(h.txtMain_TextChangeListener);
                h.txtMain_TextChangeListener = null;
            }
            if (h.txtDeviceScanning_TextChangeListener != null) {
                h.txtDeviceScanning.removeTextChangedListener(h.txtDeviceScanning_TextChangeListener);
                h.txtDeviceScanning_TextChangeListener = null;
            }

            SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
            String temploggerWhseReadingSettingsStr = objGlobalMethods.getGCMPrefs(btp, "temploggerWhseReadingSettingsStr");
            temploggerWhseReadingSettings settings = new temploggerWhseReadingSettings();
            if (!objGlobalMethods.IsEmptyString(temploggerWhseReadingSettingsStr))
                settings = new Gson().fromJson(temploggerWhseReadingSettingsStr, temploggerWhseReadingSettings.class);

            h.txtMain.setText(String.valueOf(settings.MainTimer_leftTimeMS / 1000));
            h.txtDeviceScanning.setText(String.valueOf(settings.DeviceScanningTimer_leftTimeMS / 1000));
            h.lblMainText1.setText(convertSecondsToMinutesStr(h.txtMain.getText().toString()));
            h.lblDeviceScanningText1.setText(convertSecondsToMinutesStr(h.txtDeviceScanning.getText().toString()));

            h.txtMain_TextChangeListener = new txtMainTextChangeListener();
            h.txtMain.addTextChangedListener(h.txtMain_TextChangeListener);
            h.txtDeviceScanning_TextChangeListener = new txtDeviceScanningTextChangeListener();
            h.txtDeviceScanning.addTextChangedListener(h.txtDeviceScanning_TextChangeListener);

            h.btnTemploggerWhse_dialogCancel.setOnClickListener(btnTemploggerWhse_dialogCancelOnClickListener());
            h.btnTemploggerWhse_dialogSave.setOnClickListener(btnTemploggerWhse_dialogSaveOnClickListener());
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at temploggerwhse_settingsDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private View.OnClickListener btnTemploggerWhse_dialogCancelOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temploggerwhse_settingsDialog.this.dismiss();
            }
        };
    }

    private View.OnClickListener btnTemploggerWhse_dialogSaveOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String MainTimerSeconds = h.txtMain.getText().toString();
                    String DeviceScanningTimer_leftTimeMS = h.txtDeviceScanning.getText().toString();

                    if (!objGlobalMethods.IsEmptyString(MainTimerSeconds) && !objGlobalMethods.IsEmptyString(DeviceScanningTimer_leftTimeMS)) {
                        SharedPreferences btp = objGlobalMethods.getBTLoggerRelatedSharedPreferences();
                        temploggerWhseReadingSettings settings = new temploggerWhseReadingSettings();
                        settings.MainTimer_leftTimeMS(Long.parseLong(MainTimerSeconds) * 1000);
                        settings.setDeviceScanningTimer_leftTimeMS(Long.parseLong(DeviceScanningTimer_leftTimeMS) * 1000);
                        String temploggerWhseReadingSettingsStr = new Gson().toJson(settings);
                        objGlobalMethods.setGCMPrefs(btp, "temploggerWhseReadingSettingsStr", temploggerWhseReadingSettingsStr);

                        /*String[] BTprefName = new String[]{"temploggerWhseReadingSettingsStr", "MainTimer_leftTimeMS", "DeviceScanningTimer_leftTimeMS"};
                        String[] BTprefvalue = new String[]{temploggerWhseReadingSettingsStr, String.valueOf((Long.parseLong(MainTimerSeconds) * 1000)), String.valueOf((Long.parseLong(DeviceScanningTimer_leftTimeMS) * 1000))};
                        objGlobalMethods.setBTLoggerRelatedMass(btp, BTprefName, BTprefvalue);*/

                        temploggerwhse_settingsDialog.this.dismiss();

                        if (myApp.currentActivity != null && myApp.currentActivity.getClass() == TempLoggerWhseScanActivity.class) {
                            final TempLoggerWhseScanActivity act = (TempLoggerWhseScanActivity) myApp.currentActivity;
                            act.runOnUiThread(new Runnable() {
                                public void run() {
                                    // Notifiy activity
                                    act.refreshSettingsDisplay();
                                }
                            });
                        }

                        Context ctx = myApp.getAppContext();
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), "Settings saved.").show();
                    } else {
                        //temploggerwhse_settingsDialog.this.dismiss();
                        Context ctx = myApp.getAppContext();
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), "Please enter values for Main Timer and Device Scan Timer.").show();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at temploggerwhse_settingsDialog btnTemploggerWhse_dialogSaveOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    temploggerwhse_settingsDialog.this.dismiss();
                }
            }
        };
    }

}
