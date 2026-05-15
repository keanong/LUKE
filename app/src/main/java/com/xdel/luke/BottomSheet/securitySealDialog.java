package com.xdel.luke.BottomSheet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.SecuritySeal_PostData;
import com.xdel.luke.objClasses.SecuritySeal_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class securitySealDialog extends DialogFragment {
    private holder h;
    public static ArrayList<SecuritySeal_PostData> al;
    public static Dialog dl;
    public SecuritySeal_PostItemAdapter adapter;

    public void hideKeyboard(){
        try {
            View windowToken = getDialog().getWindow().getDecorView().getRootView();
            InputMethodManager inputMethodManager = (InputMethodManager) getDialog().getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(windowToken.getWindowToken(), 0);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at securitySealDialog hideKeyboard. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    public securitySealDialog(){

    }

    public static securitySealDialog display(FragmentManager fragmentManager) {
        securitySealDialog fs = new securitySealDialog();
        try {
            fs.show(fragmentManager, "securitySealDialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at securitySealDialog display. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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
            dialog.setCancelable(false);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_securitysealdialog, container, false);
        h = new holder();
        h.btnsecuritysealdialogCancel = view.findViewById(R.id.btnsecuritysealdialogCancel);
        h.btnsecuritysealdialogNext = view.findViewById(R.id.btnsecuritysealdialogNext);
        h.listview = view.findViewById(R.id.listview);
        return view;
    }

    public final class holder {
        Button btnsecuritysealdialogCancel;
        Button btnsecuritysealdialogNext;
        ListView listview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            h.btnsecuritysealdialogCancel.setOnClickListener(null);
            h.btnsecuritysealdialogNext.setOnClickListener(null);

            h.btnsecuritysealdialogCancel.setOnClickListener(btnsecuritysealdialogCancelOnClickListener());
            h.btnsecuritysealdialogNext.setOnClickListener(btnsecuritysealdialogNextOnClickListener());
            //ctrlSettings.enableDisableFailButtonWithStyles(false, h.btnsecuritysealdialogNext);
            init();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at securitySealDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void init(){
        try {
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String securityseallistALJsonStr = "";
            securityseallistALJsonStr = objGlobalMethods.getGCMPrefs(g, "securityseallistALJsonStr");
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
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at securitySealDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                adapter = new SecuritySeal_PostItemAdapter(myApp.currentActivity, al);
                h.listview.setAdapter(adapter);
            } else {
                showErrorAlert2();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SecuritySealActivity loadlist. Exception: " + e.getMessage().toString(), "", "", "", "");
            showErrorAlert2();
        }
    }

    private void showErrorAlert2() {
        MainActivity.uponLogin = 0;
        DrawerMainActivity.DashboardOpenFromPush = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
        builder.setCancelable(false);
        builder.setMessage(myApp.getAppContext().getString(R.string.alert_errorretrivedata2));
        builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                securitySealDialog.this.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private View.OnClickListener btnsecuritysealdialogCancelOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al = null;
                securitySealDialog.this.dismiss();
            }
        };
    }

    private View.OnClickListener btnsecuritysealdialogNextOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (al != null && al.size() > 0) {
                    next();
                } else {
                    Context ctx = myApp.getAppContext();
                    objGlobalMethods.writeToFile("Unable to next securitySealDialog. al is null or empty.", "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorupdatep1p2)).show();
                    al = null;
                    securitySealDialog.this.dismiss();
                }
            }
        };
    }

    private void next() {
        try {
            Boolean canproceed = canProceed();
            if (canproceed) {
                securitySealDialog.this.dismiss();
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
            objGlobalMethods.writeToFile("Exception at securitySealDialog canProceed. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return asd;
        }
    }



}
