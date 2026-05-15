package com.xdel.luke.BottomSheet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.DrawerMainActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.JobsHandler3;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.Vrp.JobQueue;
import com.xdel.luke.credits.credit;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.util.ArrayList;
import java.util.List;

public class vrpUnlockDialog extends DialogFragment {
    private holder h;
    public static Dialog dl;
    vrpUnlockListViewAdapter adapter;

    public vrpUnlockDialog() {
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
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_vrp_unlock, container, false);
        h = new holder();
        h.lblSelectText = view.findViewById(R.id.lblSelectText);
        h.btnvrpunlock_dialogCancel = view.findViewById(R.id.btnvrpunlock_dialogCancel);
        h.btnvrpunlock_dialogNext = view.findViewById(R.id.btnvrpunlock_dialogNext);
        h.listView = view.findViewById(R.id.listView);
        return view;
    }

    public final class holder {
        TextView lblSelectText;
        Button btnvrpunlock_dialogCancel;
        Button btnvrpunlock_dialogNext;
        ListView listView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            h.btnvrpunlock_dialogCancel.setOnClickListener(null);
            h.btnvrpunlock_dialogNext.setOnClickListener(null);

            h.btnvrpunlock_dialogCancel.setOnClickListener(btnvrpunlock_dialogCancelOnClickListener());
            h.btnvrpunlock_dialogNext.setOnClickListener(btnvrpunlock_dialogNextOnClickListener());

            /*h.txtValueTextChangeListener = new tempLoggerEntryDialog.txtValueTextChangeListener();
            h.txtValue.addTextChangedListener(h.txtValueTextChangeListener);
            h.btnViewImg.setOnClickListener(rtnViewPhotoOnClickListener());
            h.btnCamera.setOnClickListener(rtnTakePhotoOnClickListener());
            h.btntemplogger_dialogNext.setOnClickListener(btntemplogger_dialogNextOnClickListener());
            h.txtValue.setText(getTempLastRecorded());*/

            initNew();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at vrpUnlockDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void initNew() {
        Context ctx = myApp.getAppContext();
        SharedPreferences vsp = objGlobalMethods.getVrpRelatedSharedPreferences();
        Gson gson = new Gson();
        ArrayList<String> credOpenedPostals = null;
        List<String> listPipDip = null;
        boolean hasNoCredOpened = false;
        int cred_postalcountused = 0;
        int limitpostalcount = 0;
        try {
            /*boolean isAM = JobsHandler3.isAM();
            boolean isPM = JobsHandler3.isPM();
            boolean isAOH = JobsHandler3.isAOH();*/
            int AMONLYc = 0;
            int AMc = 0;
            int PMc = 0;
            int AOHc = 0;
            String creditAM = "";
            String creditPM = "";
            String creditAOH = "";
            creditAM = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditAM");
            creditPM = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditPM");
            creditAOH = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "creditAOH");
            credit cd = null;
            credit cdAM = null;
            credit cdPM = null;
            credit cdAOH = null;

            AMONLYc = DAO_Jobs.getAllPostalsInPipDipByAMOnlySlot().size();
            listPipDip = DAO_Jobs.getAllPostalsInPipDipByAMSlot();
            AMc = AMONLYc > 0 ? listPipDip.size() : 0;
            if (AMc == 0) {
                listPipDip = DAO_Jobs.getAllPostalsInPipDipByPMSlot();
                PMc = listPipDip.size();
                if (PMc == 0) {
                    listPipDip = DAO_Jobs.getAllPostalsInPipDipByAOHSlot();
                    AOHc = listPipDip.size();
                }
            }
            cdAM = (AMc > 0) && !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            cdPM = (AMc == 0 && PMc > 0) && !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            cdAOH = (PMc == 0 && AOHc > 0) && !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;
            cd = cdAM != null ? cdAM : cdPM != null ? cdPM : cdAOH != null ? cdAOH : null;

            /*if (isAM)
                cd = !objGlobalMethods.IsEmptyString(creditAM) ? gson.fromJson(creditAM, credit.class) : null;
            if (isPM)
                cd = !objGlobalMethods.IsEmptyString(creditPM) ? gson.fromJson(creditPM, credit.class) : null;
            if (isAOH)
                cd = !objGlobalMethods.IsEmptyString(creditAOH) ? gson.fromJson(creditAOH, credit.class) : null;*/

            ArrayList<vrpUnlockPostData> al = DAO_Jobs.getAllLocationJobsInPipDipParked(); //pip dip only, ignore parked

            if (al != null && al.size() > 0) {
                credOpenedPostals = cd != null ? cd.credOpenedPostals : null;
                cred_postalcountused = cd != null ? cd.cred_postalcountused : 0;
                limitpostalcount = cd != null ? cd.cred_postalcountlimit : 0;

                hasNoCredOpened = credOpenedPostals == null || (credOpenedPostals != null && credOpenedPostals.size() == 0);

                String ProcessJobQueJsonStr = objGlobalMethods.getVrpRelatedSP_KeyValue2(vsp, "ProcessJobQueJsonStr");
                List<JobQueue> NewJQlist = (!ProcessJobQueJsonStr.equals("") || !ProcessJobQueJsonStr.equals("[]")) ? new Gson().fromJson(ProcessJobQueJsonStr, new TypeToken<ArrayList<JobQueue>>() {
                }.getType()) : null;
                JobQueue j = null;
                if (NewJQlist != null && NewJQlist.size() > 0) {
                    boolean hasFlag4 = false;
                    int GcQueCount = 0;
                    for (int a = 0; a <= NewJQlist.size() - 1; a++) {
                        j = NewJQlist.get(a);
                        if (j.getGC_Queue() != null) GcQueCount++;
                        if ((j.Flag & 4) == 4) hasFlag4 = true;
                    }

                    for (int i = al.size() - 1; i >= 0; i--) {
                        for (int a = 0; a <= NewJQlist.size() - 1; a++) {
                            j = NewJQlist.get(a);
                            if (objGlobalMethods.IsEqualString(j.PostalCode, al.get(i).POSTAL)) {
                                if (((j.Flag & 4) == 4) && !(j.GC_Queue != null && j.GC_Queue > 0)) {
                                    //al.remove(i);
                                    al.get(i).setHidden(true);
                                    break;
                                } else if (!hasFlag4 && (j.GC_Queue != null && j.GC_Queue == 1) && GcQueCount == 1 && (hasNoCredOpened)) {
                                    al.get(i).setHidden(true);
                                    break;
                                } else if (!hasFlag4 && (j.GC_Queue != null && j.GC_Queue == 1) && GcQueCount == 1 && (!hasNoCredOpened && !credOpenedPostals.contains(j.PostalCode))) {
                                    al.get(i).setHidden(true);
                                    break;
                                }
                            }
                        }
                    }
                }

                boolean allHidden = false;
                if (al != null && al.size() > 0) {
                    int c = 0;
                    for (int i = 0; i <= al.size() -1; i++) {
                        if (al.get(i).hidden)
                            c++;
                    }
                    allHidden = c == al.size();
                }

                if (al != null && al.size() > 0 && !allHidden) {
                    h.lblSelectText.setText(ctx.getString(R.string.maximum) + " " + String.valueOf(limitpostalcount - cred_postalcountused) + " " + ctx.getString(R.string.selection));

                    int selectedcount = 0;
                    int c = 0;
                    for (int i = 0; i <= al.size() - 1; i++) {
                        ////this was supposed to allow gc to unset the opened postal cred
                        selectedcount = (credOpenedPostals != null && credOpenedPostals.size() > 0) ? credOpenedPostals.size() : 0;
                        al.get(i).setPositionCurrMax(selectedcount);
                        al.get(i).setSelectedcount(selectedcount);
                        ////this was supposed to allow gc to unset the opened postal cred

                        al.get(i).setUsedpostalcount(cred_postalcountused);
                        al.get(i).setLimitpostalcount(limitpostalcount);
                        String tw = returnTW(al.get(i).RDYDT, al.get(i).PUBYDT, al.get(i).FDT, al.get(i).TDT, al.get(i).EFDT, al.get(i).ETDT);
                        al.get(i).setTW(tw);

                        ////this was supposed to allow gc to unset the opened postal cred
                        if (credOpenedPostals != null && credOpenedPostals.size() > 0 && credOpenedPostals.contains(al.get(i).POSTAL)) {
                            c++;
                            al.get(i).setSelected(true);
                            al.get(i).setPosition(c);
                            for (int sp = 0; sp <= credOpenedPostals.size() -1; sp++) {
                                if (credOpenedPostals.get(sp).equals(al.get(i).POSTAL)) {
                                    al.get(i).setSelected(true);
                                    al.get(i).setPosition(sp+1);
                                }
                            }
                        }
                        ////this was supposed to allow gc to unset the opened postal cred
                    }
                    adapter = new vrpUnlockListViewAdapter(myApp.currentActivity, al);
                    h.listView.setAdapter(adapter);
                } else {
                    vrpUnlockDialog.this.dismiss();
                }
            } else {
                showErrorAlert2();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at vrpUnlockDialog initNew. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private String returnTW(long RDYDT, long PUBYDT, long FDT, long TDT, long EFDT, long ETDT) {
        String tw = "";
        String start = "";
        String end = "";

        //0 comes when two date are same,
        //1 comes when date1 is higher then date2
        //-1 comes when date1 is lower then date2
        try {
            if (RDYDT > 0 && PUBYDT > 0 && TDT > 0 && ETDT > 0) { //has puby, has tdt, has extby
                start = JobsHandler2.get_ddMMFromLong(RDYDT);
                end = (PUBYDT < TDT && PUBYDT < ETDT) ? JobsHandler2.get_ddMMFromLong(PUBYDT) : (ETDT < TDT) ? JobsHandler2.get_ddMMFromLong(ETDT) : JobsHandler2.get_ddMMFromLong(TDT);

                tw = start + " - " + end;
            } else if (ETDT > 0 && TDT > 0) { //
                start = JobsHandler2.get_ddMMFromLong(FDT);
                end = (ETDT < TDT) ? JobsHandler2.get_ddMMFromLong(ETDT) : JobsHandler2.get_ddMMFromLong(TDT);

                tw = start + " - " + end;
            } else {
                start = JobsHandler2.get_ddMMFromLong(FDT);
                end = JobsHandler2.get_ddMMFromLong(TDT);

                tw = start + " - " + end;
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at vrpUnlockDialog returnTW. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return tw;
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
                vrpUnlockDialog.this.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private View.OnClickListener btnvrpunlock_dialogCancelOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vrpUnlockDialog.this.dismiss();
            }
        };
    }

    private View.OnClickListener btnvrpunlock_dialogNextOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vrpUnlockDialog.this.dismiss();
                ArrayList<vrpUnlockPostData> selectedList = adapter.getSelectedList();
                if ((selectedList != null) && selectedList.size() > 0 && (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class)) {
                    final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            // Notifiy activity
                            act.processSetLimitedGCETA(selectedList);
                        }
                    });
                }

                /*if (validate()) {
                    vrpUnlockDialog.this.dismiss();
                    if (myApp.currentActivity != null && myApp.currentActivity.getClass() == DrawerMain2Activity.class) {
                        final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                // Notifiy activity
                                act.processSetLimitedGCETA(adapter.getSelectedList());
                            }
                        });
                    }
                } else {
                    showErrorAlert2();
                }*/
            }
        };
    }

    private boolean validate() {
        boolean asd = false;
        ArrayList<vrpUnlockPostData> al = null;
        try {
            if (adapter != null && adapter.getList() != null) al = adapter.getList();

            if (al != null && al.size() > 0) {
                for (int i = 0; i <= al.size() - 1; i++) {
                    asd = al.get(i).position > 0;
                    if (asd) break;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at vrpUnlockDialog validate. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }


}
