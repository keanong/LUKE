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
import com.xdel.luke.Ext.extOpts_PostItemAdapter;
import com.xdel.luke.Ext.extOpts_PostItem;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.settings;

import java.util.ArrayList;

public class extDialog extends DialogFragment {
    private holder h;
    private settings sets;

    public extDialog() {

    }

    public static extDialog display(FragmentManager fragmentManager) {
        extDialog fs = new extDialog();
        try {
            fs.show(fragmentManager, "extDialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at extDialog display. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return fs;
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                extDialog.this.dismiss();
            }
        };
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
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_ext_options, container, false);
        h = new holder();
        h.btnextdialogCancel = view.findViewById(R.id.btnextdialogCancel);
        h.btnextdialogSubmit = view.findViewById(R.id.btnextdialogSubmit);
        h.listView = view.findViewById(R.id.listView);

        return view;
    }

    public final class holder {
        Button btnextdialogCancel;
        Button btnextdialogSubmit;
        ListView listView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
        RadioButton r = null;
        Gson gson = new Gson();
        Context ctx = myApp.getAppContext();
        extOpts_PostItemAdapter adapter;
        extOpts_PostItem p;
        ArrayList<extOpts_PostItem> listal = new ArrayList<>();
        boolean isOneHrOpsExtension = false;
        try {
            h.btnextdialogCancel.setOnClickListener(null);
            h.btnextdialogSubmit.setOnClickListener(null);
            h.btnextdialogCancel.setOnClickListener(btnextdialogCancelOnClickListener());
            h.btnextdialogSubmit.setOnClickListener(btnextdialogSubmitOnClickListener());
            enabledisableSubmitButton();

            p = new extOpts_PostItem(0, 15 + " " + ctx.getString(R.string.minute), 15);
            listal.add(p);
            p = new extOpts_PostItem(1, 30 + " " + ctx.getString(R.string.minute), 30);
            listal.add(p);
            p = new extOpts_PostItem(2, 45 + " " + ctx.getString(R.string.minute), 45);
            listal.add(p);
            p = new extOpts_PostItem(3, 60 + " " + ctx.getString(R.string.minute), 60);
            listal.add(p);
            //isOneHrOpsExtension = JobsHandler2.isOneHrOpsExtension(ji.FLAG); // used only when options contains more than 60min ext

            if (listal != null && listal.size() > 0) {
                adapter = new extOpts_PostItemAdapter(extDialog.this, ctx, listal);
                h.listView.setAdapter(adapter);
            } else {
                extDialog.this.dismiss();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at extDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }


    private View.OnClickListener btnextdialogCancelOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extDialog.this.dismiss();
            }
        };
    }

    private View.OnClickListener btnextdialogSubmitOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                extOpts_PostItemAdapter adapter;
                extOpts_PostItem data;
                int AMinutes = 0;
                String JobsIDX = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.currJobInfoIDX);
                boolean processed = false;
                try {
                    adapter = (extOpts_PostItemAdapter) h.listView.getAdapter();
                    if (!adapter.isEmpty()) {
                        for (int i = 0; i <= adapter.getCount() -1; i++) {
                            data = adapter.getItem(i);
                            if (data.IsSelected) {
                                AMinutes = data.value;
                                processed = AMinutes > 0 && !objGlobalMethods.IsEmptyString(JobsIDX);
                                break;
                            }
                        }

                        if (processed) {
                            objGlobalMethods.writeToFile("Ext Requseted for JobsIDX: " + JobsIDX + ", " + AMinutes + " minutes","","","","");
                            extDialog.this.dismiss();
                            if (myApp.currentActivity != null) {
                                final int min = AMinutes;
                                final long idx = (objGlobalMethods.IsEmptyString(JobsIDX) ? 0 : Long.parseLong(JobsIDX));
                                final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        act.requestExt(idx, min);
                                    }
                                });
                            }
                        }
                    } else {
                        objGlobalMethods.writeToFile("Ext Requsete for JobsIDX:" + JobsIDX + " failed due adapter.isEmpty()","","","","");
                        extDialog.this.dismiss();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at extDialog btnextdialogSubmitOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
                    extDialog.this.dismiss();
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorperformoperation)).show();
                }
            }
        };
    }

    public void enabledisableSubmitButton() {
        try {
            ctrlSettings.enableDisableSubmitButtonWithStyles(canProceed(), h.btnextdialogSubmit);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);;
            objGlobalMethods.writeToFile("Exception at extDialog enabledisableSubmitButton. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private boolean canProceed(){
        boolean asd = false;
        extOpts_PostItemAdapter adapter;
        extOpts_PostItem data;
        try {
            adapter = (extOpts_PostItemAdapter) h.listView.getAdapter();
            if (!adapter.isEmpty()) {
                for (int i = 0; i <= adapter.getCount() -1; i++) {
                    data = adapter.getItem(i);
                    if (data.IsSelected) {
                        asd = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);;
            objGlobalMethods.writeToFile("Exception at extDialog canProceed. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return asd;
        }
    }


}
