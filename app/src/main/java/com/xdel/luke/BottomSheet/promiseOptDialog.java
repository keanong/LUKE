package com.xdel.luke.BottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.PromiseOpts.PromiseOpts_PostItem;
import com.xdel.luke.PromiseOpts.PromiseOpts_PostItemAdapter;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.objClasses.settings;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class promiseOptDialog extends DialogFragment {
    private holder h;
    private settings sets;

    public promiseOptDialog() {

    }

    public static promiseOptDialog display(FragmentManager fragmentManager) {
        promiseOptDialog fs = new promiseOptDialog();
        try {
            fs.show(fragmentManager, "promiseOptDialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at promiseOptDialog display. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_promise_options, container, false);
        h = new holder();
        h.btnPromiseOptdialogCancel = view.findViewById(R.id.btnPromiseOptdialogCancel);
        h.btnPromiseOptdialogSubmit = view.findViewById(R.id.btnPromiseOptdialogSubmit);
        h.listView = view.findViewById(R.id.listView);

        return view;
    }

    public final class holder {
        Button btnPromiseOptdialogCancel;
        Button btnPromiseOptdialogSubmit;
        ListView listView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        RadioButton r = null;
        Gson gson = new Gson();
        Context ctx = myApp.getAppContext();
        PromiseOpts_PostItemAdapter adapter;
        PromiseOpts_PostItem p;
        ArrayList<PromiseOpts_PostItem> listal = new ArrayList<>();
        try {
            h.btnPromiseOptdialogCancel.setOnClickListener(null);
            h.btnPromiseOptdialogSubmit.setOnClickListener(null);
            h.btnPromiseOptdialogCancel.setOnClickListener(btnPromiseOptdialogCancelOnClickListener());
            h.btnPromiseOptdialogSubmit.setOnClickListener(btnPromiseOptdialogSubmitOnClickListener());
            enabledisableSubmitButton();

            String finalSettingsStr = objGlobalMethods.getGCMPrefs(gcmp, "finalSettingsStr");
            sets = gson.fromJson(finalSettingsStr, settings.class);
            int PromiseInterval = sets.getPromiseInterval();
            int NumInterval = sets.getNumInterval();
            ArrayList al = null;
            Date start = new Date();
            for (int i = 0; i <= NumInterval-1; i++) {
                al = JobsHandler2.getPromiseIntervalDesc(start, PromiseInterval, i);
                p = new PromiseOpts_PostItem(i, PromiseInterval, (Date) al.get(0), (Date) al.get(1));
                listal.add(p);
                start = (Date) al.get(1);
            }
            if (listal != null && listal.size() > 0) {
                adapter = new PromiseOpts_PostItemAdapter(promiseOptDialog.this, ctx, listal);
                h.listView.setAdapter(adapter);
            } else {
                promiseOptDialog.this.dismiss();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at promiseOptDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private View.OnClickListener btnPromiseOptdialogCancelOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promiseOptDialog.this.dismiss();
            }
        };
    }

    private View.OnClickListener btnPromiseOptdialogSubmitOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");
                Gson gson = new Gson();
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                PromiseOpts_PostItemAdapter adapter;
                PromiseOpts_PostItem data;
                String from = "";
                String to = "";
                boolean processed = false;
                try {
                    objJobInfo ji = gson.fromJson(gcmp.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                    adapter = (PromiseOpts_PostItemAdapter) h.listView.getAdapter();
                    if (!adapter.isEmpty()) {
                        for (int i = 0; i <= adapter.getCount() -1; i++) {
                            data = adapter.getItem(i);
                            if (data.getIsSelected()) {
                                ji.setPromiseFrom(data.from);
                                ji.setPromiseTo(data.to);
                                from = sdf.format(data.from);
                                to = sdf.format(data.to);
                                processed = true;
                                break;
                            }
                        }
                        if (processed) {
                            String currJobInfoJsonStr = gson.toJson(ji);
                            objGlobalMethods.setGCMPrefs(gcmp, "hfJobInfoJsonStr", currJobInfoJsonStr);
                            DAO_Jobs.updateJobInfoPromiseFromTo(ji.JobsIDX, currJobInfoJsonStr, ji.getPromiseFrom().getTime(), ji.getPromiseTo().getTime());
                            final objJobInfo jii = ji;
                            if (myApp.currentActivity != null) {
                                final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        act.hideshowPromisedLLs(jii);
                                    }
                                });
                            }
                        }
                        objGlobalMethods.writeToFile("Promised timing for JobsIDX: " + ji.JobsIDX + " From: " + from + " By: " + to,"","","","");
                        promiseOptDialog.this.dismiss();
                    } else {
                        objGlobalMethods.writeToFile("Promised timing for JobsIDX: " + ji.JobsIDX + " failed due adapter.isEmpty()","","","","");
                        promiseOptDialog.this.dismiss();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at promiseOptDialog btnPromiseOptdialogSubmitOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
                    promiseOptDialog.this.dismiss();
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }

            }
        };
    }

    public void enabledisableSubmitButton() {
        try {
            ctrlSettings.enableDisableSubmitButtonWithStyles(canProceed(), h.btnPromiseOptdialogSubmit);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);;
            objGlobalMethods.writeToFile("Exception at promiseOptDialog enabledisableSubmitButton. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private boolean canProceed(){
        boolean asd = false;
        PromiseOpts_PostItemAdapter adapter;
        PromiseOpts_PostItem data;
        try {
            adapter = (PromiseOpts_PostItemAdapter) h.listView.getAdapter();
            if (!adapter.isEmpty()) {
                for (int i = 0; i <= adapter.getCount() -1; i++) {
                    data = adapter.getItem(i);
                    if (data.getIsSelected()) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);;
            objGlobalMethods.writeToFile("Exception at promiseOptDialog canProceed. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }

}
