package com.xdel.luke.Vrp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.ConnectionDetector;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.WebService;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCabinet;
import com.xdel.luke.objClasses.objGC_CustomQueue;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.setDunningDeliveredStatusActivity;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjJobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class VrpList_RecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        VRPSwipeAndDragHelper.ActionCompletionContract {

    private static final int USER_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    static List<JobQueue> NewJQlist;
    static List<JobQueue> originalJQlist;
    private Integer categoryType;
    private boolean isDunning = false;
    private ItemTouchHelper touchHelper;
    public static SparseBooleanArray itemEnableMoveArray;
    public static Integer finaloldPosition;
    public static Integer finalnewPosition;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case USER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_jobque_item, parent, false);
                return new JobQueViewHolder(view);
            case HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_jobque_section_header, parent, false);
                return new JobQueSectionHeaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_jobque_item, parent, false);
                return new JobQueViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        final int pos = position;
        final JobQueue data = NewJQlist.get(pos);
        final int itempos = getItemPosition(data);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
        Date etadt = new Date();
        String eta = "";
        Context ctx = myApp.getAppContext();
        Resources resources = ctx.getResources();
        boolean EnableMoveArray;
        boolean prevQueCannotShiftDown = false;
        boolean prevQueIsHeader = false;
        int NewJQlistSize = 0;
        int ID = 0;
        int sdowncount = 0;
        try {
            if (itemViewType == USER_TYPE) {
                JobQueViewHolder h = (JobQueViewHolder) holder;
                final JobQueViewHolder hh = (JobQueViewHolder) holder;
                EnableMoveArray = itemEnableMoveArray.get(itempos, false);
                h.cl.setOnClickListener(null);
                h.cl.setBackgroundResource(data.getOPS_Queue() != null ? R.color.mainorange
                        : data.IsEnabledForJobUpdate && !EnableMoveArray && !data.getisMoved() ? R.color.colorPrimary
                        : data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved() ? R.drawable.gradient_bg_edited_firsttwo_jobque
                        : !data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved() ? R.color.holo_blue_light
                        : (data.IsEnabledForJobUpdate || !data.IsEnabledForJobUpdate) && EnableMoveArray && data.getisMoved() && itempos != data.getStartID() ? R.color.brightgreen
                        : (data.IsEnabledForJobUpdate || !data.IsEnabledForJobUpdate) && EnableMoveArray ? R.color.dialog_bg : R.color.White);
                if (data.getID() - 1 > 0) {
                    try {
                        JobQueue jq = NewJQlist.get(data.getID() - 1);
                        prevQueIsHeader = jq.getIsHeader();
                        prevQueCannotShiftDown = jq.getSDownCount() == 2 && jq.getType().equals("vrp") && !jq.getIsHeader();
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                        objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter prevQueCannotShiftDown checks. Exception: " + s, "", "", "", "");
                    }
                }
                NewJQlistSize = NewJQlist.size();
                ID = data.getID();

                h.lblVrpAddress.setTextColor(data.getOPS_Queue() != null ? Color.BLACK
                        : ((data.IsEnabledForJobUpdate && !EnableMoveArray && !data.getisMoved())) || (data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved())
                        ? Color.WHITE
                        : Color.BLACK);

                h.lblVrpPostal.setTextColor(data.getOPS_Queue() != null ? Color.BLACK
                        : ((data.IsEnabledForJobUpdate && !EnableMoveArray && !data.getisMoved())) || (data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved())
                        ? Color.WHITE
                        : Color.BLACK);

                h.lblpNum_dNum.setTextColor(data.getOPS_Queue() != null ? Color.BLACK
                        : ((data.IsEnabledForJobUpdate && !EnableMoveArray && !data.getisMoved())) || (data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved())
                        ? Color.WHITE
                        : Color.BLACK);

                h.lblCount.setTextColor(data.getOPS_Queue() != null ? Color.BLACK
                        : ((data.IsEnabledForJobUpdate && !EnableMoveArray && !data.getisMoved())) || (data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved())
                        ? Color.WHITE
                        : Color.BLACK);

                h.lblVrpAddress.setText(data.getAddressWithPostalOnly());
                h.setType(data.getType());
                h.setIsEnabled(data.IsEnabledForJobUpdate);
                h.setEnableMove(EnableMoveArray);
                h.setSDownCount(data.getSDownCount());
                h.setSUpCount(data.getSUpCount());
                h.setJobQueID(data.getID());

                if (data.getCUSTOM_ETA() != null && !data.getCUSTOM_ETA().equals("")) {
                    etadt = objGlobalMethods.JsonDateToDate(data.getCUSTOM_ETA());
                    eta = formatter.format(etadt);
                } else {
                    eta = ctx.getString(R.string.lbletatba);
                }

                h.cl.setOnTouchListener(null);
                h.reorderView.setOnTouchListener(null);

                h.reorderView.setImageResource(
                        data.getType().equals("ops") ? R.mipmap.ic_blank :
                        EnableMoveArray && ID == 1 && ID == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested :
                        EnableMoveArray && ID > 1 && ID == NewJQlistSize - 1 && (prevQueCannotShiftDown || prevQueIsHeader) ? R.mipmap.ic_av_not_interested :
                                EnableMoveArray && ID > 1 && ID == NewJQlistSize - 1 && !prevQueCannotShiftDown && !prevQueIsHeader ? R.mipmap.ic_action_expand_less :
                                        EnableMoveArray && ID == 1 && data.SDownCount < 2 ? R.mipmap.ic_action_expand_more :
                                                EnableMoveArray && ID > 1 && data.SDownCount < 2 && ID != NewJQlistSize - 1 && (prevQueCannotShiftDown || prevQueIsHeader) ? R.mipmap.ic_action_expand_more :
                                                EnableMoveArray && ID > 1 && data.SDownCount < 2 && ID != NewJQlistSize - 1 ? R.mipmap.ic_action_unfold_more :
                                                        EnableMoveArray && ID > 1 && data.SDownCount == 2 && ID < NewJQlistSize - 1 ? R.mipmap.ic_action_expand_less : R.mipmap.ic_blank);

                //set which ctrl can drag. here we set reorderView ctrl to b e one that can b press, hold n drag.
                if (EnableMoveArray) {
                    h.reorderView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                                hh.cl.setBackgroundResource(R.drawable.gradient_bg_hover);
                                touchHelper.startDrag(holder);
                            }
                            return false;
                        }
                    });
                }
                //set which ctrl can drag. here we set reorderView ctrl to b e one that can b press, hold n drag.

                h.lblVrpPostal.setText(data.getAllJobsCompleted() ? "" : ctx.getString(R.string.lbleta) + " " + eta);
                h.lblpNum_dNum.setText(data.getAllJobsCompleted() ? "" : ctx.getString(R.string.lblpNum) + " " + String.valueOf(data.getpNum()) + " " + ctx.getString(R.string.lbldNum) + " " + String.valueOf(data.getdNum()));
                h.lblCount.setText(data.getJobsCount() == null || data.getJobsCount() == 0 ? "0" : String.valueOf(data.getJobsCount()));

                h.lblVrpPostal.setVisibility(data.getAllJobsCompleted() ? View.GONE : View.VISIBLE);
                h.lblpNum_dNum.setVisibility(data.getAllJobsCompleted() ? View.GONE : View.VISIBLE);

                h.imgnets.setVisibility((data.FlagSum & 4) != 4 ? View.GONE : View.VISIBLE);
                h.imgcc.setVisibility((data.FlagSum & 8) != 8 ? View.GONE : View.VISIBLE);
                h.imgtradein.setVisibility((data.FlagSum & 16) != 16 ? View.GONE : View.VISIBLE);

                if (data.getCanClick()) {
                    ((JobQueViewHolder) holder).cl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Integer JobsCount = data.getJobsCount();
                                if (JobsCount == null || JobsCount == 0) {
                                    //do nothing
                                } else {
                                    Context ctx = myApp.getAppContext();
                                    Intent i = new Intent(ctx, DashboardSubActivity.class);
                                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                                    Double LAT = data.getLatitude() == null ? 0 : data.getLatitude();
                                    Double LNG = data.getLongitude() == null ? 0 : data.getLongitude();
                                    String desPostal = data.getPostalCode() == null ? "" : data.getPostalCode();
                                    String desAddress = data.getAddressWithPostalOnly() == null ? "" : data.getAddressWithPostalOnly();
                                    String[] prefName = new String[]{"LAT", "LNG", "desPostal", "desAddress", "DrawerMain2ScrollSavedPosition"};
                                    String[] prefvalue = new String[]{String.valueOf(LAT), String.valueOf(LNG), desPostal, desAddress, String.valueOf(getItemPosition(data))};
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                                    int currPos = getItemPosition(data);
                                    if (JobsCount == null || JobsCount == 0) {
                                        //do nothing
                                    } else if (JobsCount > 1) {
                                        if (NewJQlist != null && NewJQlist.size() > 0 && currPos - 1 > 0 && !NewJQlist.get(currPos - 1).getIsHeader() && data.IsEnabledForJobUpdate) {
                                            objGlobalMethods.setGCMPrefs(GcmPreferences, "currPostal", NewJQlist.get(currPos).getPostalCode());
                                            objGlobalMethods.setGCMPrefs(GcmPreferences, "earlierPostal", NewJQlist.get(currPos - 1).getPostalCode());
                                        }
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs", data.getJobsIDXs() != null && !data.getJobsIDXs().equals("") ? data.getJobsIDXs() : "");
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "enableJobUpdates", String.valueOf(data.IsEnabledForJobUpdate));
                                        //ctx.startActivity(i);
                                        myApp.currentActivity.startActivity(i);
                                        final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                                        act.finish();
                                    } else {
                                        startRedirectionSQLite(ctx, GcmPreferences, String.valueOf(JobsCount), desPostal, data.IsEnabledForJobUpdate, data.JobsIDXs, currPos);
                                    }
                                }
                            } catch (Exception e) {
                                String s = e.getMessage().toString();
                                objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter onBindViewHolder JobQueViewHolder onClick. Exception: " + s, "", "", "", "");
                            }
                        }
                    });
                }
            } else {
                final JobQueSectionHeaderViewHolder headerViewHolder = (JobQueSectionHeaderViewHolder) holder;
                headerViewHolder.cl.setBackgroundResource(data.getType().equals("ops") ? R.color.mainorange : R.color.dialog_bg);

                headerViewHolder.lblCancel.setOnClickListener(null);
                headerViewHolder.lblEdit.setOnClickListener(null);
                headerViewHolder.lblUpdate.setOnClickListener(null);
                headerViewHolder.lblReset.setOnClickListener(null);

                if (itempos >= 4) {
                    hideHeaderRow(ctx, headerViewHolder);
                    hideShowUpdateCtrls(headerViewHolder, false, true);
                } else {
                    if (data.getType().equals("ops") || data.getType() == "ops") {
                        hideHeaderRow(ctx, headerViewHolder);
                        hideShowUpdateCtrls(headerViewHolder, false, true);
                    }

                    try {
                        if (data.getOPS_Queue() != null) {
                            hideShowUpdateCtrls(headerViewHolder, false, true);
                        } else {
                            hideShowUpdateCtrls(headerViewHolder, data.IsEditing, false);

                            headerViewHolder.lblEdit.setOnClickListener(rtnEditClickListener(itempos, headerViewHolder));

                            headerViewHolder.lblCancel.setOnClickListener(rtnCancelUpdateClickListener(itempos, headerViewHolder));

                            headerViewHolder.lblUpdate.setOnClickListener(rtnUpdateClickListener(itempos, headerViewHolder));

                            headerViewHolder.lblReset.setOnClickListener(rtnResetClickListener(itempos, headerViewHolder));
                        }
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                        objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter onBindViewHolder JobQueSectionHeaderViewHolder onClick event. Exception: " + s, "", "", "", "");
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter onBindViewHolder. Exception: " + s, "", "", "", "");
        }
    }

    public class SetGCETA extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pd;
        private final JobQueSectionHeaderViewHolder headerViewHolder;
        private final int itempos;
        private final List<JobQueue> tempJQlist;

        public SetGCETA(JobQueSectionHeaderViewHolder aheaderViewHolder, int aitempos, List<JobQueue> atempJQlist) {
            this.headerViewHolder = aheaderViewHolder;
            this.itempos = aitempos;
            this.tempJQlist = atempJQlist;
            this.pd = new ProgressDialog(myApp.currentActivity);
            this.pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.pd.setCanceledOnTouchOutside(false);
            this.pd.setMessage(myApp.getAppContext().getString(R.string.pd_waitprocessing));
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean asd = false;
            objGC_CustomQueue GC_CustomQueue = null;
            ArrayList<objGC_CustomQueue> AQueue = null;
            JobQueue jq = null;
            int queue = 0;
            String jqJobsIDXs = "";
            List<String> jqJobsIDXsArr = null;
            try {
                if (tempJQlist != null && tempJQlist.size() > 0) {
                    for (int i = 0; i <= tempJQlist.size() - 1; i++) {
                        jq = tempJQlist.get(i);
                        if (!jq.getIsHeader() && !jq.getType().equals("ops")) {
                            jqJobsIDXs = jq.getJobsIDXs();
                            jqJobsIDXsArr = !jqJobsIDXs.isEmpty() && !jqJobsIDXs.equals("") && jqJobsIDXs != "" ? Arrays.asList(jqJobsIDXs.split("\\s*,\\s*")) : null;
                            if (jqJobsIDXsArr != null && jqJobsIDXsArr.size() > 0) {
                                queue++;
                                for (int x = 0; x <= jqJobsIDXsArr.size() - 1; x++) {
                                    GC_CustomQueue = new objGC_CustomQueue(Long.parseLong(jqJobsIDXsArr.get(x)), queue);
                                    if (AQueue == null) {
                                        AQueue = new ArrayList<objGC_CustomQueue>();
                                    }
                                    AQueue.add(GC_CustomQueue);
                                }
                            }
                        }
                    }

                    if (AQueue != null && AQueue.size() > 0) {
                        SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                        SharedPreferences updateJobInfoPref = objGlobalMethods.getUpdateJobInfoPreferences();
                        String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), gcmp);
                        boolean L2_SetGCETAResult = WebService.L2_SetGCETA(regid, AQueue);
                        if (L2_SetGCETAResult) {
                            asd = true;
                            Gson gson = new Gson();
                            String saveAmended4QuesJsonStr = "";
                            List<JobQueue> al = new ArrayList<JobQueue>();
                            for (JobQueue v : tempJQlist) {
                                JobQueue q = new JobQueue(v);
                                al.add(q);
                            }
                            for (int i = al.size() - 1; i >= 0; i--) {
                                if (al.get(i).getIsHeader() || al.get(i).getType().equals("ops")) {
                                    al.remove(i);
                                }
                            }
                            if (al != null && al.size() > 0) {
                                saveAmended4QuesJsonStr = gson.toJson(al);
                            } else {
                                saveAmended4QuesJsonStr = "";
                            }

                            if (!saveAmended4QuesJsonStr.isEmpty() && !saveAmended4QuesJsonStr.equals("") && saveAmended4QuesJsonStr != "") {
                                String[] UsernamePwd = new String[4];
                                String useridx = "";
                                UsernamePwd = objGlobalMethods.getLoginUsernamePwd(gcmp);
                                useridx = UsernamePwd[2];
                                Date dt = new Date();
                                Long l = dt.getTime();
                                String timestampstr = "/Date(" + l + ")/";
                                objCabinet cabinet = new objCabinet(0, Long.parseLong(!useridx.isEmpty() && !useridx.equals("") && useridx != "" ? useridx : "0"), "2", saveAmended4QuesJsonStr, timestampstr);
                                JobsHandler.addUpdateCabinetQueALPrefs(updateJobInfoPref, regid, cabinet);
                                myApp.getAppContext().sendBroadcast(new Intent(myApp.getAppContext(), BackendRun_BroadcastReceiver.class));
                            }
                        } else {
                            asd = false;
                        }
                    }
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter SetGCETA doInBackground. Exception: " + s, "", "", "", "");
                asd = false;
            } finally {
                return asd;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Context ctx = myApp.getAppContext();
            try {
                if (pd != null) {
                    pd.dismiss();
                }
                if (success) {
                    NewJQlist = tempJQlist;
                    JobQueue u = NewJQlist.get(itempos);
                    u.setIsEditing(false);
                    NewJQlist.set(itempos, u);
                    setitemEnableMoveArray(false, itempos);
                    //NewJQlist = JobsHandler.rtnEnableDisableJobQueForUpdates(NewJQlist, null);
                    NewJQlist = JobsHandler.rtnEnableDisableJobQueForUpdatesNew(NewJQlist, null);
                    hideShowUpdateCtrls(headerViewHolder, false, false);
                    notifyDataSetChanged();
                    originalJQlist = null;
                    finaloldPosition = null;
                    finalnewPosition = null;
                    JobsHandler.SaveAmended4Ques(NewJQlist);
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_routeupdated)).show();
                } else {
                    JobQueue u = NewJQlist.get(itempos);
                    u.setIsEditing(false);
                    //NewJQlist.set(itempos, u);
                    List<JobQueue> al = getOriginalJQlist();
                    al.set(itempos, u);
                    setNewJQlist(al);
                    setitemEnableMoveArray(false, itempos);
                    hideShowUpdateCtrls(headerViewHolder, false, false);
                    notifyDataSetChanged();
                    nullOriginalJQlist();
                    finaloldPosition = null;
                    finalnewPosition = null;
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorupdateroute)).show();
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
                objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter SetGCETA doInBackground. Exception: " + s, "", "", "", "");
                JobQueue u = NewJQlist.get(itempos);
                u.setIsEditing(false);
                List<JobQueue> al = getOriginalJQlist();
                al.set(itempos, u);
                setNewJQlist(al);
                setitemEnableMoveArray(false, itempos);
                hideShowUpdateCtrls(headerViewHolder, false, false);
                notifyDataSetChanged();
                nullOriginalJQlist();
                originalJQlist = null;
                finaloldPosition = null;
                finalnewPosition = null;
                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_errorupdateroute)).show();
            }
        }
    }

    private View.OnClickListener rtnUpdateClickListener(final int itempos, final JobQueSectionHeaderViewHolder headerViewHolder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectionDetector cd;
                try {
                    Context ctx = myApp.getAppContext();
                    //cd = new ConnectionDetector(ctx);
                    cd = new ConnectionDetector(null);
                    if (cd.isConnectingToInternet()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                        builder.setCancelable(false);
                        //builder.setMessage(ctx.getString(R.string.alert_cfmupdateroute));
                        builder.setTitle(ctx.getString(R.string.alert_cfmupdateroute));
                        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", update job status
                                //List<JobQueue> tempJQlist = JobsHandler.rtnEnableDisableJobQueForUpdates(NewJQlist, null);
                                List<JobQueue> tempJQlist = JobsHandler.rtnEnableDisableJobQueForUpdatesNew(NewJQlist, null);
                                SetGCETA t = new SetGCETA(headerViewHolder, itempos, tempJQlist);
                                t.execute();
                            }
                        });
                        builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user select "No", just cancel this dialog and continue with app
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        objGlobalMethods.showDialog(myApp.currentActivity, "OK", myApp.getAppContext().getString(R.string.toast_workingconnection)).show();
                    }
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter rtnUpdateClickListener. Exception: " + s, "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnEditClickListener(final int itempos, final JobQueSectionHeaderViewHolder headerViewHolder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ctx = myApp.getAppContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setCancelable(false);
                    //builder.setMessage(ctx.getString(R.string.alert_cfmeditupdateroute));
                    builder.setTitle(ctx.getString(R.string.alert_cfmeditupdateroute));
                    builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user pressed "yes", update job status
                            setOriginalJQlist(NewJQlist);
                            JobQueue u = NewJQlist.get(itempos);
                            u.setIsEditing(true);
                            NewJQlist.set(itempos, u);
                            setitemEnableMoveArray(true, itempos);
                            hideShowUpdateCtrls(headerViewHolder, true, false);
                            notifyDataSetChanged();
                            finaloldPosition = null;
                            finalnewPosition = null;
                        }
                    });
                    builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user select "No", just cancel this dialog and continue with app
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter rtnEditClickListener. Exception: " + s, "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnCancelUpdateClickListener(final int itempos, final JobQueSectionHeaderViewHolder headerViewHolder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ctx = myApp.getAppContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setCancelable(false);
                    //builder.setMessage(ctx.getString(R.string.alert_cfmcancelupdateroute));
                    builder.setTitle(ctx.getString(R.string.alert_cfmcancelupdateroute));
                    builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user pressed "yes", update job status
                            /*JobQueue u = NewJQlist.get(itempos);
                            u.setIsEditing(false);
                            List<JobQueue> al = getOriginalJQlist();*/
                            List<JobQueue> al = getOriginalJQlist();
                            JobQueue u = al.get(itempos);
                            u.setIsEditing(false);
                            al.set(itempos, u);
                            setNewJQlist(al);
                            setitemEnableMoveArray(false, itempos);
                            hideShowUpdateCtrls(headerViewHolder, false, false);
                            nullOriginalJQlist();
                            finaloldPosition = null;
                            finalnewPosition = null;
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user select "No", just cancel this dialog and continue with app
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter rtnCancelUpdateClickListener. Exception: " + s, "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnResetClickListener(final int itempos, final JobQueSectionHeaderViewHolder headerViewHolder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Context ctx = myApp.getAppContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                    builder.setCancelable(false);
                    //builder.setMessage(ctx.getString(R.string.alert_cfmcancelupdateroute));
                    builder.setTitle(ctx.getString(R.string.alert_cfmresetupdateroute));
                    builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user pressed "yes", update job status
                            try {
                                List<JobQueue> al = getOriginalJQlist();
                                NewJQlist = al;
                                setOriginalJQlist(NewJQlist);
                                JobQueue u = NewJQlist.get(itempos);
                                u.setIsEditing(true);
                                NewJQlist.set(itempos, u);
                                setitemEnableMoveArray(true, itempos);
                                hideShowUpdateCtrls(headerViewHolder, true, false);
                                finaloldPosition = null;
                                finalnewPosition = null;
                                notifyDataSetChanged();
                            } catch (Exception e) {
                                String s = e.getMessage().toString();
                            }
                        }
                    });
                    builder.setNegativeButton(ctx.getString(R.string.buttontext_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //if user select "No", just cancel this dialog and continue with app
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } catch (Exception e) {
                    String s = e.getMessage().toString();
                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter rtnResetClickListener. Exception: " + s, "", "", "", "");
                }
            }
        };
    }

    private void hideHeaderRow(Context ctx, JobQueSectionHeaderViewHolder headerViewHolder) {
        try {
            final float scale = ctx.getResources().getDisplayMetrics().density;
            int pixels = (int) (10 * scale + 0.5f);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, pixels);
            headerViewHolder.cl.setLayoutParams(rlp);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter hideHeaderRow. Exception: " + s, "", "", "", "");
        }
    }

    private void startRedirectionSQLite(Context ctx, SharedPreferences GcmPreferences, String lblCount, String Postal, boolean enableJobUpdates, String JobsIDXs, int currPos) {
        Intent i;
        int count;
        try {
            if (!lblCount.isEmpty()) {
                if (!lblCount.toLowerCase().equals("error")) {
                    count = Integer.parseInt(lblCount.toString());
                    if (count > 0) {
                        if (count > 1) {
                            //more than 1 jobs, go to addresslist
                            i = new Intent(ctx, DashboardSubActivity.class);
                            JobInfoActivity.accessFromDirection = 0;
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "enableJobUpdates", String.valueOf(enableJobUpdates));
                            if (NewJQlist != null && NewJQlist.size() > 0 && currPos - 1 > 0 && !NewJQlist.get(currPos - 1).getIsHeader() && enableJobUpdates) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "currPostal", NewJQlist.get(currPos).getPostalCode());
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "earlierPostal", NewJQlist.get(currPos - 1).getPostalCode());
                            }
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs", JobsIDXs);
                            //ctx.startActivity(i);
                            myApp.currentActivity.startActivity(i);
                            final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                            act.finish();
                        } else if (count == 1) {
                            //only 1 job, go str8 to jobInfo
                            int proceed = 0;
                            proceed = initRedirectionToJobInfoSQLite(GcmPreferences, Postal, JobsIDXs);
                            if (proceed == 1) {
                                if (categoryType == 0) {
                                    //objGlobalMethods.stopAlarmForCommon();
                                    if (FCMservice.mNotificationManager != null) {
                                        FCMservice.mNotificationManager.cancel(2);
                                    }
                                    FCMservice.notiNotRead = false;
                                }
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "isFromDashboardSub", "true");
                                if (isDunning) {
                                    i = new Intent(ctx, setDunningDeliveredStatusActivity.class);
                                    JobInfoActivity.accessFromDirection = 0;
                                } else {
                                    i = new Intent(ctx, JobInfoActivity.class);
                                    JobInfoActivity.accessFromDirection = 1;
                                }
                                objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, String.valueOf(categoryType));
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "enableJobUpdates", String.valueOf(enableJobUpdates));
                                if (NewJQlist != null && NewJQlist.size() > 0 && currPos - 1 > 0 && !NewJQlist.get(currPos - 1).getIsHeader() && enableJobUpdates) {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "currPostal", NewJQlist.get(currPos).getPostalCode());
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "earlierPostal", NewJQlist.get(currPos - 1).getPostalCode());
                                }
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs", JobsIDXs);
                                //ctx.startActivity(i);
                                myApp.currentActivity.startActivity(i);
                                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                                act.finish();
                            } else if (proceed > 1) {
                                //more than 1 jobs, go to addresslist
                                i = new Intent(ctx, DashboardSubActivity.class);
                                JobInfoActivity.accessFromDirection = 0;
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "enableJobUpdates", String.valueOf(enableJobUpdates));
                                if (NewJQlist != null && NewJQlist.size() > 0 && currPos - 1 > 0 && !NewJQlist.get(currPos - 1).getIsHeader() && enableJobUpdates) {
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "currPostal", NewJQlist.get(currPos).getPostalCode());
                                    objGlobalMethods.setGCMPrefs(GcmPreferences, "earlierPostal", NewJQlist.get(currPos - 1).getPostalCode());
                                }
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "SelectedVRPJobsIDXs", JobsIDXs);
                                //ctx.startActivity(i);
                                myApp.currentActivity.startActivity(i);
                                final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                                act.finish();
                            } else if (proceed == 0) {
                                notifyDataSetChanged();
                            }
                        }
                    } else if (count == 0) {
                        //0 jobs, do nothing
                    }
                } else {
                    //show error instead of count, do nothing
                }
            } else {
                //assume to be 0 jobs, do nothing
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception caught at VrpList_RecycleViewAdapter startRedirectionSQLite. Exception: " + s, "", "", "", "");
        }
    }

    private int initRedirectionToJobInfoSQLite(SharedPreferences GcmPreferences, String Postal, String JobsIDXs) {
        int asd = 0;
        Gson gson = new Gson();
        String JsonStr;
        objJobInfo JobInfo;
        String addressWithUnit = "";
        Boolean hasCOD = false;
        try {
            List<dbObjJobs> objAL = DAO_Jobs.getAlldbObjJobsForDashboardSubByJobsIDXs(Postal, JobsIDXs);
            if (objAL != null && objAL.size() == 0) {
                asd = 0;
            } else if (objAL != null && objAL.size() > 1) {
                asd = 2;
            } else if (objAL != null && objAL.size() == 1) {
                JsonStr = objAL.get(0).getJobInfoJsonStr();
                categoryType = objAL.get(0).getCatTypeID();
                objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JsonStr);
                JobsHandler.sethfJobsInfoArrJsonStrPref(GcmPreferences, "");
                if (JsonStr != "") {
                    JobInfo = gson.fromJson(JsonStr, objJobInfo.class);
                    if (JobInfo != null) {
                        hasCOD = JobsHandler.checkForCODwithCatType(JobInfo, categoryType);
                        if (hasCOD) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "true");
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "JobhasCOD", "false");
                        }

                        isDunning = JobsHandler.isDunning(JobInfo);
                        if (isDunning) {
                            objGlobalMethods.writeToFile(objAL.get(0).getJobNo() + " is Dunning.", "", "", "", "");
                            if (JobInfo != null) {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0");
                            } else {
                                objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                            }

                            objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobInfo.JobsIDX));
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", JobInfo.JobNo);
                            addressWithUnit = objAL.get(0).getAddressWithoutPostal();
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", addressWithUnit);
                        }
                        asd = 1;
                    } else {
                        asd = 0;
                    }
                } else {
                    asd = 0;
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception caught at VrpList_RecycleViewAdapter initRedirectionToJobInfoSQLite. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private void hideShowUpdateCtrls(JobQueSectionHeaderViewHolder headerViewHolder, boolean IsEditing, boolean hideall) {
        try {
            if (hideall) {
                headerViewHolder.lblCancel.setVisibility(View.GONE);
                headerViewHolder.lblEdit.setVisibility(View.GONE);
                headerViewHolder.lblUpdate.setVisibility(View.GONE);
                headerViewHolder.lblReset.setVisibility(View.GONE);
            } else if (IsEditing) {
                headerViewHolder.lblCancel.setVisibility(View.VISIBLE);
                headerViewHolder.lblEdit.setVisibility(View.GONE);
                headerViewHolder.lblUpdate.setVisibility(View.VISIBLE);
                headerViewHolder.lblReset.setVisibility(View.VISIBLE);
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "editingvrp", "true");
            } else {
                headerViewHolder.lblCancel.setVisibility(View.GONE);
                headerViewHolder.lblEdit.setVisibility(View.VISIBLE);
                headerViewHolder.lblUpdate.setVisibility(View.GONE);
                headerViewHolder.lblReset.setVisibility(View.GONE);
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                objGlobalMethods.setGCMPrefs(gcmp, "editingvrp", "");
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception caught at VrpList_RecycleViewAdapter hideShowUpdateCtrls. Exception: " + s, "", "", "", "");
        }
    }

    public void setitemEnableMoveArray(boolean enabled, int Ppos) {
        try {
            int counter = 0;
            if (NewJQlist != null && NewJQlist.size() > 0) {
                if (Ppos > 0) {
                    for (int i = Ppos; i <= NewJQlist.size() - 1; i++) {
                        JobQueue jq = NewJQlist.get(i);
                        if (jq.getType().equals("vrp")) {
                            itemEnableMoveArray.put(i, enabled);
                            NewJQlist.get(i).setCanClick(!enabled);
                        }
                    }
                    if (Ppos > 1 && NewJQlist.size() >= Ppos + 1) {
                        for (int i = 0; i <= Ppos - 1; i++) {
                            NewJQlist.get(i).setCanClick(!enabled);
                        }
                    }
                } else {
                    for (int i = Ppos; i <= NewJQlist.size() - 1; i++) {
                        JobQueue jq = NewJQlist.get(i);
                        if (jq.getType().equals("vrp")) {
                            itemEnableMoveArray.put(i, enabled);
                            NewJQlist.get(i).setCanClick(!enabled);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception caught at VrpList_RecycleViewAdapter setitemEnableMoveArray. Exception: " + s, "", "", "", "");
        }
    }

    @Override //to prevent messed up animation when use notifyDatasetChanged()
    public long getItemId(int position) {
        return NewJQlist.get(position).hashCode();
    }

    private int getItemPosition(JobQueue jq) { // (<-- replace with your item)
        int i = 0;
        // (replace with your items and methods here)
        for (JobQueue currentjq : NewJQlist) {
            if (currentjq.getID() == jq.getID()) break;
            i++;
        }
        return i;
    }

    @Override
    public int getItemCount() {
        return NewJQlist == null ? 0 : NewJQlist.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if (TextUtils.isEmpty(usersList.get(position).getName())) {
            return HEADER_TYPE;
        } else {
            return USER_TYPE;
        }*/
        return NewJQlist.get(position).getIsHeader() ? HEADER_TYPE : USER_TYPE;
    }

    public void setNewJQlist(List<JobQueue> aNewJQlist) {
        this.NewJQlist = new ArrayList<JobQueue>(aNewJQlist);
        notifyDataSetChanged();
    }

    public void setOriginalJQlist(List<JobQueue> aNewJQlist) {
        //this.originalJQlist = new ArrayList<JobQueue>(aNewJQlist);
        List<JobQueue> al = new ArrayList<JobQueue>();
        for (JobQueue v : aNewJQlist) {
            JobQueue q = new JobQueue(v);
            al.add(q);
        }
        this.originalJQlist = al;
    }

    public void resetNewJQlist(List<JobQueue> aNewJQlist){
        List<JobQueue> al = new ArrayList<JobQueue>();
        for (JobQueue v : aNewJQlist) {
            JobQueue q = new JobQueue(v);
            al.add(q);
        }
        this.NewJQlist = al;
    }

    public List<JobQueue> getJQlist() {
        List<JobQueue> al = null;
        try {
            al = this.NewJQlist;
        } catch (Exception e) {
            String s = e.getMessage().toString();
            al = null;
        } finally {
            return al;
        }
    }

    public List<JobQueue> getOriginalJQlist() {
        List<JobQueue> al = null;
        try {
            al = this.originalJQlist;
        } catch (Exception e) {
            String s = e.getMessage().toString();
            al = null;
        } finally {
            return al;
        }
    }

    public void nullOriginalJQlist() {
        this.originalJQlist = null;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        try {
            JobQueue targetJobQue = NewJQlist.get(oldPosition);
            JobQueue jq = new JobQueue(targetJobQue);
            finaloldPosition = oldPosition;
            finalnewPosition = newPosition;
            NewJQlist.remove(oldPosition);
            NewJQlist.add(newPosition, jq);
            notifyItemMoved(oldPosition, newPosition);

            int vrpHeaderIndex = -1;
            int supcount = 0;
            int ssupcount = 0;
            int upcount = 0;
            int sdowncount = 0;
            int downcount = -1;
            for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                JobQueue j = NewJQlist.get(i);
                if (j.getIsHeader() && j.getType().equals("vrp")) {
                    vrpHeaderIndex = i;
                }
                if (!j.getIsHeader() && (
                        (finaloldPosition > finalnewPosition && (i <= finaloldPosition && i >= finalnewPosition)) ||
                                (finaloldPosition < finalnewPosition && (i <= finalnewPosition && i >= finaloldPosition)))) {
                    j.setisMoved(true);
                    if (i != j.getID() && i < j.getID()) {
                        //shift up
                        upcount = j.getSUpCount() + (j.getID() - i);
                        downcount = j.getSDownCount() - (j.getID() - i);
                        j.setSUpCount(upcount);
                        //j.setSDownCount(downcount);
                        j.setSDownCount(downcount < 0 ? 0 : downcount);
                        j.setOldID(j.getID());
                        j.setID(i);
                        if (i != j.getOldID()) {
                            j.setOldID(j.getID());
                            j.setID(i);
                        }
                    } else if (i != j.getID() && i > j.getID()) {
                        //shift down
                        supcount = j.getSUpCount();
                        upcount = j.getSUpCount() - (i - j.getID());
                        downcount = supcount > 0 ? 0 : j.getSDownCount() + (i - j.getID());
                        j.setSUpCount(upcount);
                        //j.setSDownCount(downcount);
                        j.setSDownCount(downcount < 0 ? 0 : downcount);
                        j.setOldID(j.getID());
                        j.setID(i);
                        if (i != j.getOldID()) {
                            j.setOldID(j.getID());
                            j.setID(i);
                        }
                    }
                }
                NewJQlist.set(i, j);
            }

            int o = NewJQlist.size();
        } catch (Exception e) {
            String x = JobsHandler.getStackTraceString(e);
            finaloldPosition = null;
            finalnewPosition = null;
        }
    }

    public static void amendJQListAfterMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        try {
            int viewHolderIDX = viewHolder.getLayoutPosition();
            int NewJQlistSize = NewJQlist.size();
            boolean prevQueCannotShiftDown = false;
            boolean prevQueIsHeader = false;
            if (viewHolderIDX > 0) {
                List<JobQueue> First_4_JobQueue = null;
                VrpList_RecycleViewAdapter adapter = (VrpList_RecycleViewAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    First_4_JobQueue = adapter.getJQlist();
                    if (First_4_JobQueue != null && First_4_JobQueue.size() > 0 && viewHolderIDX <= First_4_JobQueue.size() - 1) {
                        JobQueue jq = First_4_JobQueue.get(viewHolderIDX);
                        if (finaloldPosition != null) {
                            ((JobQueViewHolder) viewHolder).cl.setBackgroundResource(
                                    jq.getisMoved() && viewHolderIDX != jq.getStartID() ? R.color.brightgreen
                                            : R.color.dialog_bg);
                        } else {
                            ((JobQueViewHolder) viewHolder).cl.setBackgroundResource(
                                    jq.getisMoved() && viewHolderIDX != jq.getStartID() ? R.color.brightgreen
                                            : R.color.dialog_bg);
                        }
                    }

                    JobQueViewHolder vh = null;
                    for (int i = 0; i <= First_4_JobQueue.size() - 1; i++) {
                        JobQueue jq = First_4_JobQueue.get(i);
                        if (!jq.getIsHeader()) {
                            vh = (JobQueViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                            vh.cl.setBackgroundResource(
                                    jq.getType().equals("ops") ? R.color.mainorange :
                                            jq.getisMoved() && i != jq.getStartID() ? R.color.brightgreen
                                                    : R.color.dialog_bg);

                            if (jq.getID() - 1 > 0) {
                                try {
                                    JobQueue jqq = First_4_JobQueue.get(jq.getID() - 1);
                                    prevQueIsHeader = jqq.getIsHeader();
                                    prevQueCannotShiftDown = jqq.getSDownCount() == 2 && jqq.getType().equals("vrp") && !jqq.getIsHeader();
                                } catch (Exception e) {
                                    String s = e.getMessage().toString();
                                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter amendJQListAfterMoved prevQueCannotShiftDown checks. Exception: " + s, "", "", "", "");
                                }
                            }

                            /*vh.reorderView.setImageResource(jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                    jq.ID == 1 && jq.ID == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested :
                                    jq.ID > 1 && jq.ID == NewJQlistSize - 1 && (prevQueCannotShiftDown || prevQueIsHeader) ? R.mipmap.ic_av_not_interested :
                                            jq.ID > 1 && jq.ID == NewJQlistSize - 1 && !prevQueCannotShiftDown && !prevQueIsHeader ? R.mipmap.ic_action_expand_less :
                                                    jq.ID == 1 && jq.SDownCount < 2 ? R.mipmap.ic_action_expand_more :
                                                            jq.ID > 1 && jq.SDownCount < 2 && jq.ID != NewJQlistSize - 1 ? R.mipmap.ic_action_unfold_more :
                                                                    jq.ID > 1 && jq.SDownCount <= 2 && jq.ID < NewJQlistSize - 1 ? R.mipmap.ic_action_expand_less : R.mipmap.ic_blank);*/

                            vh.reorderView.setImageResource(jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                    jq.ID == 1 && jq.ID == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested :
                                            jq.ID > 1 && jq.ID == NewJQlistSize - 1 && (prevQueCannotShiftDown || prevQueIsHeader) ? R.mipmap.ic_av_not_interested :
                                                    jq.ID > 1 && jq.ID == NewJQlistSize - 1 && !prevQueCannotShiftDown && !prevQueIsHeader ? R.mipmap.ic_action_expand_less :
                                                            jq.ID == 1 && jq.SDownCount < 2 ? R.mipmap.ic_action_expand_more :
                                                                    jq.ID > 1 && jq.SDownCount < 2 && jq.ID != NewJQlistSize - 1 && (prevQueCannotShiftDown || prevQueIsHeader) ? R.mipmap.ic_action_expand_more :
                                                                            jq.ID > 1 && jq.SDownCount < 2 && jq.ID != NewJQlistSize - 1 ? R.mipmap.ic_action_unfold_more :
                                                                                    jq.ID > 1 && jq.SDownCount == 2 && jq.ID < NewJQlistSize - 1 ? R.mipmap.ic_action_expand_less : R.mipmap.ic_blank);

                            vh.lblVrpAddress.setTextColor(Color.BLACK);
                            vh.lblVrpPostal.setTextColor(Color.BLACK);
                            vh.lblpNum_dNum.setTextColor(Color.BLACK);
                            vh.lblCount.setTextColor(Color.BLACK);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        } finally {
            finaloldPosition = null;
            finalnewPosition = null;
        }
    }

    @Override
    public void onViewSwiped(int position) {
        NewJQlist.remove(position);
        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

    public static List<JobQueue> rtnCalculatedList(int currentPos, int targetPos, int movementFlags) {
        List<JobQueue> al = new ArrayList<JobQueue>();
        JobQueue jq = null;
        JobQueue cjq = null;
        JobQueue tjq = null;
        boolean isUp = false;
        boolean Cismax = false;
        boolean Tismax = false;
        boolean inbetweenMax = false;
        int inbetweenMaxIDX = -1;
        int CTismax = 0;
        try {
            isUp = movementFlags == ItemTouchHelper.UP;
            for (JobQueue v : NewJQlist) {
                JobQueue q = new JobQueue(v);
                al.add(q);
            }

            if (al != null && al.size() > 0) {
                cjq = al.get(currentPos);
                tjq = al.get(targetPos);
                Cismax = cjq.getSDownCount() == 2;
                Tismax = tjq.getSDownCount() == 2;
                CTismax = Cismax && Tismax ? 3 : Cismax && !Tismax ? 1 : !Cismax && Tismax ? 2 : 0;
                if (targetPos == 2) {
                    boolean asdasd = false;
                }
                if (isUp) {
                    if (CTismax > 0 && CTismax == 1) {
                        for (int i = currentPos - 1; i >= 0; i--) {
                            jq = al.get(i);
                            if (!jq.getIsHeader() && jq.getSDownCount() == 2) {
                                inbetweenMaxIDX = i;
                                break;
                            }
                        }
                        if (inbetweenMaxIDX > -1) {
                            for (int i = inbetweenMaxIDX; i >= 0; i--) {
                                jq = al.get(i);
                                if (!jq.getIsHeader()) {
                                    jq.setSDownCount(2);
                                    al.set(i, jq);
                                }
                            }
                        }
                        /*for (int i = al.size() - 1; i >= currentPos; i--) {
                            jq = al.get(i);
                            if (i != currentPos && !jq.getIsHeader()) {
                                jq.setSDownCount(2);
                                al.set(i, jq);
                            }
                        }*/
                    } else if (CTismax > 0 && CTismax == 2) {
                        for (int i = targetPos - 1; i >= 0; i--) {
                            jq = al.get(i);
                            if (!jq.getIsHeader()) {
                                jq.setSDownCount(2);
                                al.set(i, jq);
                            }
                        }
                    } else if (CTismax > 0 && CTismax == 3) {
                        for (int i = 0; i <= al.size() - 1; i++) {
                            if (i != currentPos && i != targetPos) {
                                jq = al.get(i);
                                if (!jq.getIsHeader()) {
                                    jq.setSDownCount(2);
                                    al.set(i, jq);
                                }
                            }
                        }
                    } else {
                        for (int i = currentPos - 1; i >= 0; i--) {
                            jq = al.get(i);
                            if (!jq.getIsHeader() && jq.getSDownCount() == 2) {
                                inbetweenMaxIDX = i;
                                break;
                            }
                        }
                        if (inbetweenMaxIDX > -1) {
                            for (int i = inbetweenMaxIDX; i >= 0; i--) {
                                jq = al.get(i);
                                if (!jq.getIsHeader()) {
                                    jq.setSDownCount(2);
                                    al.set(i, jq);
                                }
                            }
                        }
                    }
                } else {
                    if (CTismax > 0 && CTismax == 1) {
                        for (int i = al.size() - 1; i > currentPos; i--) {
                            jq = al.get(i);
                            if (i != currentPos && !jq.getIsHeader()) {
                                jq.setSDownCount(2);
                                al.set(i, jq);
                            }
                        }
                    } else if (CTismax > 0 && CTismax == 2) {
                        for (int i = al.size() - 1; i >= targetPos; i--) {
                            jq = al.get(i);
                            if (i != currentPos && !jq.getIsHeader()) {
                                jq.setSDownCount(jq.getSDownCount() - 1 < 0 ? 0 : jq.getSDownCount() - 1);
                                al.set(i, jq);
                            }
                        }
                    } else if (CTismax > 0 && CTismax == 3) {
                        for (int i = al.size() - 1; i > currentPos; i--) {
                            jq = al.get(i);
                            if (i != currentPos && !jq.getIsHeader()) {
                                jq.setSDownCount(2);
                                al.set(i, jq);
                            }
                        }
                    } else {
                        for (int i = currentPos; i <= al.size() - 1; i++) {
                            jq = al.get(i);
                            if (!jq.getIsHeader() && jq.getSDownCount() == 2) {
                                inbetweenMaxIDX = i;
                                break;
                            }
                        }
                        if (inbetweenMaxIDX > -1) {
                            for (int i = inbetweenMaxIDX; i <= al.size() - 1; i++) {
                                jq = al.get(i);
                                if (!jq.getIsHeader()) {
                                    jq.setSDownCount(2);
                                    al.set(i, jq);
                                }
                            }
                        }
                        /*if (inbetweenMaxIDX == -1 && currentPos - 1 > 0) {
                            for (int i = currentPos - 1; i <= al.size() - 1; i++) {
                                jq = al.get(i);
                                if (!jq.getIsHeader() && jq.getSDownCount() == 2) {
                                    inbetweenMaxIDX = i;
                                    break;
                                }
                            }
                        }

                        if (inbetweenMaxIDX > -1) {
                            for (int i = inbetweenMaxIDX; i <= al.size() - 1; i++) {
                                jq = al.get(i);
                                if (!jq.getIsHeader()) {
                                    jq.setSDownCount(2);
                                    al.set(i, jq);
                                }
                            }
                        }*/
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            al = null;
        } finally {
            return al;
        }
    }

}
