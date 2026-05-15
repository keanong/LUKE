package com.xdel.luke.BottomSheet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.inventory.InventoryBatchID;
import com.xdel.luke.inventory.InventoryLite;
import com.xdel.luke.inventory.InventoryLog_Adapter;
import com.xdel.luke.objClasses.SecuritySeal_PostData;
import com.xdel.luke.objClasses.SecuritySeal_PostItemAdapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InventoryLogginDialog extends DialogFragment implements DialogInterface.OnShowListener  {
    private Toolbar toolbar;
    private holder h;
    private InventoryLog_Adapter adapter;

    public InventoryLogginDialog(){

    }

    public final class holder {
        ListView lvLogs;
    }

   public static InventoryLogginDialog display(FragmentManager fragmentManager) {
       InventoryLogginDialog ild =  new InventoryLogginDialog();
       try {
           ild.show(fragmentManager, "InventoryLogginDialog");
       } catch (Exception e) {
           String s = JobsHandler.getStackTraceString(e);
           objGlobalMethods.writeToFile("Exception at InventoryLogginDialog display. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
       } finally {
           return ild;
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
    public void onShow(DialogInterface dialog) {
        try {
            toolbar = (Toolbar) getDialog().findViewById(R.id.toolbar);
            final Toolbar tb = toolbar;
            new CountDownTimer(100, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tb.setNavigationIcon(
                            (millisUntilFinished > 0 && millisUntilFinished <= 100) ? R.mipmap.num_1 :
                                    (millisUntilFinished > 1100 && millisUntilFinished <= 200) ? R.mipmap.num_2 :
                                            (millisUntilFinished > 2100 && millisUntilFinished <= 300) ? R.mipmap.num_3 :
                                                    R.mipmap.ic_action_failpu_white

                    );
                }
                @Override
                public void onFinish() {
                    tb.setNavigationIcon(R.mipmap.ic_action_failpu_white);
                    tb.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InventoryLogginDialog.this.dismiss();
                        }
                    });
                    getDialog().setCancelable(true);
                }
            }.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryLogginDialog onShow. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_inventory_main_log_dialog, container, false);
        h = new holder();

        toolbar = view.findViewById(R.id.toolbar);
        h.lvLogs = view.findViewById(R.id.lvLogs);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            toolbar.setNavigationOnClickListener(null);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    InventoryLogginDialog.this.dismiss();
                    return true;
                }
            });

            Dialog dialog = getDialog();
            dialog.setOnShowListener(this);

            init();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryLogginDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void init(){
        Context c = myApp.getAppContext();
        try {
            SharedPreferences g = objGlobalMethods.getGcmPreferences();
            String InventoryBatchIDStr = "";
            InventoryBatchID ib = null;
            InventoryBatchIDStr = objGlobalMethods.getGCMPrefs(g, "InventoryBatchIDStr");
            ArrayList<String> logs = null;

            if (!objGlobalMethods.IsEmptyString(InventoryBatchIDStr)) {
                ib = new Gson().fromJson(InventoryBatchIDStr, InventoryBatchID.class);
                logs = ib != null && ib.logs != null ? ib.logs : null;

                if (logs != null && logs.size() > 0) {
                    Collections.sort(logs, Collections.reverseOrder());
                    loadlist(logs);
                } else {
                    InventoryLogginDialog.this.dismiss();
                    objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_errorretrivedata2)).show();
                }
            } else {
                InventoryLogginDialog.this.dismiss();
                objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_errorretrivedata2)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at InventoryLogginDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loadlist(ArrayList<String> logs) {
        try {
            adapter = new InventoryLog_Adapter(myApp.currentActivity, logs);
            h.lvLogs.setAdapter(adapter);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at InventoryLogginDialog loadlist. Exception: " + e.getMessage().toString(), "", "", "", "");
            Context c = myApp.getAppContext();
            objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_errorretrivedata2)).show();
        }
    }

}
