package com.xdel.luke.BottomSheet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xdel.luke.Camera5Activity;
import com.xdel.luke.CustomGallery;
import com.xdel.luke.CustomGalleryActivity;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.JobTemperaturePayload;
import com.xdel.luke.templogger.Temperature_Log;

import java.util.ArrayList;
import java.util.Date;

public class tempLoggerEntryDialog extends DialogFragment {
    private holder h;
    public static Dialog dl;
    public static Boolean launchCamFromTempLoggerEntry;
    public static ArrayList<String> imagePaths;
    public static ArrayList<CustomGallery> dataT;
    public static Boolean isCompleted = false;
    public boolean launchCFS = false;
    public long JobsIDX;

    public void setLaunchCFS(boolean launchCFS) {
        this.launchCFS = launchCFS;
    }

    public void setJobsIDX(long jobsIDX) {
        JobsIDX = jobsIDX;
    }

    public static void setLaunchCamFromTempLoggerEntry(Boolean launchCamFromTempLoggerEntry) {
        tempLoggerEntryDialog.launchCamFromTempLoggerEntry = launchCamFromTempLoggerEntry;
    }

    public static String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL) {
        if (str.charAt(0) == '.') {
            str = "0" + str;
        } else if (str.length() == 2 && (str.charAt(0) == '0' && str.charAt(1) == '0')) {
            str = "0";
        } else if (str.length() == 2 && (str.charAt(0) == '0' && str.charAt(1) != '0' && str.charAt(1) != '.')) {
            str = str.substring(1);
        }

        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0, up = 0, decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);
            if (t != '.' && after == false) {
                up++;
                if (up > MAX_BEFORE_POINT) return rFinal;
            } else if (t == '.') {
                after = true;
            } else {
                decimal++;
                if (decimal > MAX_DECIMAL)
                    return rFinal;
            }
            rFinal = rFinal + t;
            i++;
        }
        return rFinal;
    }

    private class txtValueTextChangeListener implements TextWatcher {

        public txtValueTextChangeListener() {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = h.txtValue.getText().toString();
            if (str.isEmpty()) return;
            String str2 = PerfectDecimal(str, 2, 2);

            if (!str2.equals(str)) {
                h.txtValue.setText(str2);
                h.txtValue.setSelection(str2.length());
            }
        }
    }

    private InputFilter getTwoDecPlcInputFilter() {
        InputFilter f = null;
        try {
            f = new InputFilter() {
                final int maxDigitsBeforeDecimalPoint = 2;
                final int maxDigitsAfterDecimalPoint = 1;

                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    StringBuilder builder = new StringBuilder(dest);
                    builder.replace(dstart, dend, source
                            .subSequence(start, end).toString());
                    if (!builder.toString().matches(
                            "(([1-9]{1})([0-9]{0," + (maxDigitsBeforeDecimalPoint - 1) + "})?)?(\\.[0-9]{0," + maxDigitsAfterDecimalPoint + "})?"

                    )) {
                        if (source.length() == 0)
                            return dest.subSequence(dstart, dend);
                        return "";
                    }

                    return null;

                }
            };
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception caught at checklist_PostItemAdapter getTwoDecPlcInputFilter. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return f;
        }
    }

    public tempLoggerEntryDialog() {

    }

    public static tempLoggerEntryDialog display(FragmentManager fragmentManager) {
        tempLoggerEntryDialog fs = new tempLoggerEntryDialog();
        try {
            fs.show(fragmentManager, "tempLoggerEntryDialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at tempLoggerEntryDialog display. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dl = dialog;
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
            dialog.setCancelable(true);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
            {
                @Override
                public boolean onKey(android.content.DialogInterface dialog, int keyCode,android.view.KeyEvent event) {

                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    {
                        prompt2();
                        return true; // pretend we've processed it
                    }
                    else
                        return false; // pass on to be processed as normal
                }
            });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_temploggerentrydialog, container, false);
        h = new holder();
        h.btnViewImg = view.findViewById(R.id.btnViewImg);
        h.btnCamera = view.findViewById(R.id.btnCamera);
        h.btntemplogger_dialogCancel = view.findViewById(R.id.btntemplogger_dialogCancel);
        h.btntemplogger_dialogNext = view.findViewById(R.id.btntemplogger_dialogNext);
        h.txtValue = view.findViewById(R.id.txtValue);
        return view;
    }

    public final class holder {
        Button btnViewImg;
        Button btnCamera;
        Button btntemplogger_dialogCancel;
        Button btntemplogger_dialogNext;
        EditText txtValue;
        InputFilter inputFilter;
        txtValueTextChangeListener txtValueTextChangeListener;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            h.btnViewImg.setOnClickListener(null);
            h.btnCamera.setOnClickListener(null);
            h.btntemplogger_dialogCancel.setOnClickListener(null);
            h.btntemplogger_dialogNext.setOnClickListener(null);
            if (h.txtValueTextChangeListener != null) {
                h.txtValue.removeTextChangedListener(h.txtValueTextChangeListener);
                h.txtValueTextChangeListener = null;
            }

            /*if (h.inputFilter != null) {
                h.txtValue.setFilters(null);
            }
            h.txtValue.setFilters(new InputFilter[] {getTwoDecPlcInputFilter()});*/
            h.txtValueTextChangeListener = new txtValueTextChangeListener();
            h.txtValue.addTextChangedListener(h.txtValueTextChangeListener);
            h.btnViewImg.setOnClickListener(rtnViewPhotoOnClickListener());
            h.btnCamera.setOnClickListener(rtnTakePhotoOnClickListener());
            h.btntemplogger_dialogCancel.setOnClickListener(btntemplogger_dialogCancelOnClickListener());
            h.btntemplogger_dialogNext.setOnClickListener(btntemplogger_dialogNextOnClickListener());
            h.txtValue.setText(getTempLastRecorded());
            ctrlSettings.enableDisableCommonButtonWithStyles((dataT != null && dataT.size() > 0), h.btnViewImg);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at tempLoggerEntryDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private View.OnClickListener rtnTakePhotoOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //saveTemp2(null);
                    saveTemp(null);
                    Context ctx = myApp.getAppContext();
                    launchCamFromTempLoggerEntry = true;
                    JobInfoActivity.tempLoggerEntryDialogisCompleted = false;
                    Intent i = new Intent(ctx, Camera5Activity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myApp.currentActivity.startActivity(i);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at tempLoggerEntryDialog rtnTakePhotoOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnViewPhotoOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ctx = myApp.getAppContext();
                    launchCamFromTempLoggerEntry = true;
                    JobInfoActivity.tempLoggerEntryDialogisCompleted = false;
                    Intent i = new Intent(ctx, CustomGalleryActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    myApp.currentActivity.startActivity(i);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at tempLoggerEntryDialog rtnViewPhotoOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener btntemplogger_dialogCancelOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prompt2();
            }
        };
    }

    private View.OnClickListener btntemplogger_dialogNextOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String val = h.txtValue.getText().toString();
                    if (objGlobalMethods.IsEmptyString(val) || (dataT == null || (dataT != null && dataT.size() == 0))) {
                        prompt2();
                    } else {
                        launchCamFromTempLoggerEntry = false;
                        saveTemp2(null);
                        tempLoggerEntryDialog.this.dismiss();
                        JobInfoActivity.tempLoggerEntryDialogisCompleted = true;
                        if (launchCFS) {
                            JobInfoActivity.showCFS();
                        }
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at tempLoggerEntryDialog btntemplogger_dialogNextOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                    tempLoggerEntryDialog.this.dismiss();
                    JobInfoActivity.tempLoggerEntryDialogisCompleted = true;
                    if (launchCFS) {
                        JobInfoActivity.showCFS();
                    }
                }
            }
        };
    }

    private void saveTemp2(SharedPreferences g) {
        JobTemperaturePayload plByJobsIDX = new JobTemperaturePayload();
        Temperature_Log tl = null;
        String JobTemperaturePayloadByJobsIDXJsonStr = "";

        try {
            if (g == null) {
                g = objGlobalMethods.getGcmPreferences();
            }
            String val = h.txtValue.getText().toString();
            Date dt = new Date();
            Long currTimeLong = dt.getTime();
            CustomGallery CustomGalleryItem = getCustomGalleryItem();

            tl = new Temperature_Log();
            tl.setTemperature(!objGlobalMethods.IsEmptyString(val) ? Double.parseDouble(val) : 0);
            //tl.setTimestamp("/Date(" + currTimeLong + ")/");
            tl.setTimestamp(String.valueOf(currTimeLong));
            tl.setItem(CustomGalleryItem);
            Temperature_Log[] tla = new Temperature_Log[]{tl};
            plByJobsIDX.setTemperatureLogs(tla);
            plByJobsIDX.setAJobsIDX(JobsIDX);

            Gson gson = new Gson();
            JobTemperaturePayloadByJobsIDXJsonStr = gson.toJson(plByJobsIDX);

            String[] prefName = new String[]{"JobTemperaturePayloadByJobsIDXJsonStr", "mTempLastRecorded"};
            String[] prefvalue = new String[]{JobTemperaturePayloadByJobsIDXJsonStr, ""};
            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at tempLoggerEntryDialog saveTemp2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void saveTemp(SharedPreferences g) {
        try {
            if (g == null) {
                g = objGlobalMethods.getGcmPreferences();
            }
            String val = h.txtValue.getText().toString();
            String[] prefName = new String[]{"mTempLastRecorded"};
            String[] prefvalue = new String[]{val};
            objGlobalMethods.setGCMPrefsMass(g, prefName, prefvalue);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at tempLoggerEntryDialog saveTemp. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void prompt2() {
        String temp = h.txtValue.getText().toString();
        boolean tempRecorded = false;
        boolean photoTaken = false;
        Context ctx = myApp.getAppContext();
        try {
            tempRecorded = !objGlobalMethods.IsEmptyString(temp);
            photoTaken = dataT != null && dataT.size() > 0;
            ArrayList<String> tickSpecInst = (tempRecorded || photoTaken) ? new ArrayList<String>() : null;
            ArrayList<String> crossSpecInst = (!tempRecorded || !photoTaken) ? new ArrayList<String>() : null;

            if (tickSpecInst != null && tempRecorded) {
                tickSpecInst.add(ctx.getString(R.string.temp_recorded));
            }
            if (tickSpecInst != null && photoTaken) {
                tickSpecInst.add(ctx.getString(R.string.lblphotoacquired));
            }
            if (crossSpecInst != null && !tempRecorded) {
                crossSpecInst.add(ctx.getString(R.string.temp_recorded));
            }
            if (crossSpecInst != null && !photoTaken) {
                crossSpecInst.add(ctx.getString(R.string.lblphotoacquired));
            }

            String note = ctx.getString(R.string.notetotakelogger);
            showTickCrossNoteMsgPrompt(note, tickSpecInst, crossSpecInst);
        } catch (Exception e) {
            launchCamFromTempLoggerEntry = false;
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at tempLoggerEntryDialog prompt2. " + e.getMessage() + "\nStack: " + s, "", "", "", "");

            tempLoggerEntryDialog.this.dismiss();
            JobInfoActivity.tempLoggerEntryDialogisCompleted = true;
            /*if (launchCFS) {
                JobInfoActivity.showCFS();
            }*/
        }
    }

    private String getTempLastRecordedNew() {
        String t = "";
        Gson gson = new Gson();
        JobTemperaturePayload plByJobsIDX = new JobTemperaturePayload();
        Temperature_Log tl = null;
        String JobTemperaturePayloadByJobsIDXJsonStr = "";
        try {
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            JobTemperaturePayloadByJobsIDXJsonStr = objGlobalMethods.getGCMPrefs(g, "JobTemperaturePayloadByJobsIDXJsonStr");
            if (!objGlobalMethods.IsEmptyString(JobTemperaturePayloadByJobsIDXJsonStr)) {
                plByJobsIDX = gson.fromJson(JobTemperaturePayloadByJobsIDXJsonStr, JobTemperaturePayload.class);
                if (plByJobsIDX != null && plByJobsIDX.TemperatureLogs != null && plByJobsIDX.TemperatureLogs.length > 0) {
                    tl = plByJobsIDX.TemperatureLogs[0];
                    t = String.valueOf(tl.Temperature);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at tempLoggerEntryDialog getTempLastRecordedNew. " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return t;
        }
    }

    private String getTempLastRecorded() {
        String t = "";
        try {
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            t = objGlobalMethods.getGCMPrefs(g, "mTempLastRecorded");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at tempLoggerEntryDialog getTempLastRecorded. " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return t;
        }
    }

    public CustomGallery getCustomGalleryItem() {
        CustomGallery item = null;
        try {
            if (dataT != null && dataT.size() > 0) {
                item = dataT.get(0);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception caught at tempLoggerEntryDialog getCustomGalleryItem. " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return item;
        }
    }

    public void showTickCrossNoteMsgPrompt(String note, ArrayList<String> tickSpecInst, ArrayList<String> crossSpecInst) {
        Context c = myApp.getAppContext();
        int added = 0;
        try {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            final AlertDialog.Builder ToNoteBuilder = new AlertDialog.Builder(myApp.currentActivity);
            final View ToNotedv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_tonote_dialog_2, null);
            ToNoteBuilder.setView(ToNotedv);
            ToNoteBuilder.setCancelable(false);
            TextView txtToNoteTitle = ToNotedv.findViewById(R.id.txtToNoteTitle);
            txtToNoteTitle.setText(c.getString(R.string.tonote));
            TextView btnCustomToNoteNeutral = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteNeutral);
            btnCustomToNoteNeutral.setVisibility(View.VISIBLE);
            TextView btnCustomToNoteOK = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteOK);
            LinearLayout LLcustomToNote_msg = (LinearLayout) ToNotedv.findViewById(R.id.LLcustomToNote_msg);

            if (tickSpecInst != null && tickSpecInst.size() > 0) {
                for (int ts = 0; ts <= tickSpecInst.size() - 1; ts++) {
                    TextView tv = new TextView(c);
                    tv.setText(tickSpecInst.get(ts));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tv.setAllCaps(true);
                    tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setPadding(50, 0, 75, 0);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_ok_black, 0, 0, 0);
                    tv.setLayoutParams(params);
                    tv.setBackgroundColor(ContextCompat.getColor(c, R.color.brightergreen));
                    LLcustomToNote_msg.addView(tv);
                    added++;
                }
            }
            if (crossSpecInst != null && crossSpecInst.size() > 0) {
                if (crossSpecInst != null && crossSpecInst.size() > 0) {
                    for (int cs = 0; cs <= crossSpecInst.size() - 1; cs++) {
                        TextView tv = new TextView(c);
                        tv.setText(crossSpecInst.get(cs));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        tv.setTextColor(ContextCompat.getColor(c, R.color.White));
                        tv.setAllCaps(true);
                        tv.setTypeface(null, Typeface.BOLD);
                        tv.setGravity(Gravity.CENTER_VERTICAL);
                        tv.setPadding(50, 0, 75, 0);
                        tv.setLayoutParams(params);
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_failpu_white, 0, 0, 0);
                        tv.setBackgroundColor(ContextCompat.getColor(c, R.color.menu_red));
                        LLcustomToNote_msg.addView(tv);
                        added++;
                    }
                }
            }

            TextView tv = new TextView(c);
            tv.setText(note);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setAllCaps(true);
            tv.setTextColor(ContextCompat.getColor(c, R.color.black));
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setPadding(75, 40, 75, 40);
            tv.setLayoutParams(params);
            tv.setBackground(c.getDrawable(R.drawable.bottom_border));
            LLcustomToNote_msg.addView(tv);

            final AlertDialog ToNotedvd = ToNoteBuilder.create();

            btnCustomToNoteNeutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ToNotedvd.dismiss();
                    } catch (Exception e) {

                    }
                }
            });

            btnCustomToNoteOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ToNotedvd.dismiss();
                        launchCamFromTempLoggerEntry = false;
                        saveTemp(null);
                        tempLoggerEntryDialog.this.dismiss();
                        JobInfoActivity.tempLoggerEntryDialogisCompleted = false;
                        if (launchCFS) {
                            JobInfoActivity.showCFS();
                        }
                    } catch (Exception e) {

                    }
                }
            });

            if (added > 0) {
                ToNotedvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ToNotedvd.show();
            }
        } catch (Exception e) {
            JobInfoActivity.tempLoggerEntryDialogisCompleted = false;
            objGlobalMethods.writeToFile("Exception at objGlobalMethods2 showTickCrossNoteMsgPrompt. Exception: " + e.getMessage(), "", "", "", "");
        }
    }



}
