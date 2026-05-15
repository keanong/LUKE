package com.xdel.luke.BottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.PieceByPiecePU.ScannedList_Adapter;
import com.xdel.luke.R;
import com.xdel.luke.inventory.InventoryLog_Adapter;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class ScannListDialog extends DialogFragment implements DialogInterface.OnShowListener  {
    private Toolbar toolbar;
    private holder h;
    private ScannedList_Adapter adapter;

    public ScannListDialog(){

    }

    public final class holder {
        ListView lvLogs;
    }

    public static ScannListDialog display(FragmentManager fragmentManager) {
        ScannListDialog ild =  new ScannListDialog();
        try {
            ild.show(fragmentManager, "ScannListDialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ScannListDialog display. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
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
                            ScannListDialog.this.dismiss();
                        }
                    });
                    getDialog().setCancelable(true);
                }
            }.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ScannListDialog onShow. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_scanned_list_dialog, container, false);
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
                    ScannListDialog.this.dismiss();
                    return true;
                }
            });

            Dialog dialog = getDialog();
            dialog.setOnShowListener(this);

            init();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ScannListDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void init(){
        Context c = myApp.getAppContext();
        try {
            SharedPreferences m = objGlobalMethods.getMedsColSP();
            String scanned = "";
            ArrayList<String> slist = null;
            scanned = objGlobalMethods.getGCMPrefs(m, "scanned");

            if (!objGlobalMethods.IsEmptyString(scanned)) {
                slist = new Gson().fromJson(scanned, new TypeToken<ArrayList<String>>() {}.getType());
                if (slist != null && slist.size() > 0) {
                    loadlist(slist);
                } else {
                    ScannListDialog.this.dismiss();
                    //objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_errorretrivedata2)).show();
                }
            } else {
                ScannListDialog.this.dismiss();
                //objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_errorretrivedata2)).show();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ScannListDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void loadlist(ArrayList<String> logs) {
        try {
            adapter = new ScannedList_Adapter(myApp.currentActivity, logs);
            h.lvLogs.setAdapter(adapter);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at ScannListDialog loadlist. Exception: " + e.getMessage().toString(), "", "", "", "");
            Context c = myApp.getAppContext();
            objGlobalMethods.showDialog(myApp.currentActivity, c.getString(R.string.buttontext_ok), c.getString(R.string.alert_errorretrivedata2)).show();
        }
    }


}
