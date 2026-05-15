package com.xdel.luke.Vrp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.xdel.luke.DashboardSubActivity;
import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.FCM.FCMservice;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.setDunningDeliveredStatusActivity;
import com.xdel.luke.sqlite.DAO_Jobs;
import com.xdel.luke.sqlite.dbObjJobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VrpList_RecycleViewAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        VRPSwipeAndDragHelper2.ActionCompletionContract {

    private static final int USER_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    static List<JobQueue> NewJQlist;
    static List<JobQueue> originalJQlist;
    static String saveAmended4QuesJsonStr;
    private Integer categoryType;
    private boolean isDunning = false;
    private ItemTouchHelper touchHelper;
    public static SparseBooleanArray itemEnableMoveArray;
    public static Integer finaloldPosition;
    public static Integer finalnewPosition;
    static boolean hasMovements = false;
    boolean isLongTouch = false;
    public static boolean isAllWhite = true;


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case USER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_jobque_item2, parent, false);
                return new JobQueViewHolder2(view);
            case HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_jobque_section_header, parent, false);
                return new JobQueSectionHeaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_jobque_item2, parent, false);
                return new JobQueViewHolder2(view);
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
        final Context ctx = myApp.getAppContext();
        Resources resources = ctx.getResources();
        boolean EnableMoveArray;
        boolean prevQueCannotShiftDown = false;
        boolean prevQueIsHeader = false;
        boolean prevQueIsMoved = false;
        boolean nextQueIsHeader = false;
        boolean nextQueIsMoved = false;
        boolean currQueIsMoved = false;
        int NewJQlistSize = 0;
        //int ID = 0;
        int sdowncount = 0;
        JobQueue nq = null;
        JobQueue pq = null;
        try {
            if (itemViewType == USER_TYPE) {
                NewJQlistSize = NewJQlist.size();
                JobQueViewHolder2 h = (JobQueViewHolder2) holder;
                final JobQueViewHolder2 hh = (JobQueViewHolder2) holder;
                EnableMoveArray = itemEnableMoveArray.get(itempos, false);
                h.cl.setOnClickListener(null);
                h.cl.setBackgroundResource(data.getOPS_Queue() != null ? R.color.mainorange
                        : data.IsEnabledForJobUpdate && !EnableMoveArray && data.isUpdated ? R.drawable.gradient_bg_edited_firsttwo_jobque
                        : (data.IsEnabledForJobUpdate || !data.IsEnabledForJobUpdate) && EnableMoveArray && data.getisMoved() ? R.color.brightgreen
                        : (data.IsEnabledForJobUpdate || !data.IsEnabledForJobUpdate) && EnableMoveArray && !data.getisMoved() ? R.color.dialog_bg : R.color.White);

                ////shifted to setNewJQlist
                /*if (isAllWhite) {
                    isAllWhite = (data.getOPS_Queue() != null) ||
                            (data.IsEnabledForJobUpdate && !EnableMoveArray && data.isUpdated) ||
                            ((data.IsEnabledForJobUpdate || !data.IsEnabledForJobUpdate) && EnableMoveArray && data.getisMoved()) ||
                            ((data.IsEnabledForJobUpdate || !data.IsEnabledForJobUpdate) && EnableMoveArray && !data.getisMoved())
                            ? false : true;
                }*/
                ////shifted to setNewJQlist

                if (data.getArrayIdx() - 1 >= 0) {
                    try {
                        pq = NewJQlist.get(data.getArrayIdx() - 1);
                        prevQueIsHeader = pq.getIsHeader();
                        prevQueIsMoved = pq.getisMoved();
                        prevQueCannotShiftDown = pq.getSDownCount() == 2 && pq.getType().equals("vrp") && !pq.getIsHeader();
                        if (data.getArrayIdx() + 1 > 0 && data.getArrayIdx() + 1 < NewJQlist.size() - 1) {
                            nq = NewJQlist.get(data.getArrayIdx() + 1);
                            if (nq != null) {
                                nextQueIsHeader = nq.getIsHeader();
                                nextQueIsMoved = nq.getisMoved();
                            }
                        }
                    } catch (Exception e) {
                        String s = e.getMessage().toString();
                        objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 prevQueCannotShiftDown checks. Exception: " + s, "", "", "", "");
                    }
                }

                int ID = data.getArrayIdx();

                h.lblVrpAddress.setTextColor(data.getOPS_Queue() != null ? Color.BLACK
                        : ((data.IsEnabledForJobUpdate && !EnableMoveArray && !data.getisMoved())) || (data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved())
                        ? Color.WHITE
                        : Color.BLACK);

                /*h.lblVrpPostal.setTextColor(data.getOPS_Queue() != null ? Color.BLACK
                        : ((data.IsEnabledForJobUpdate && !EnableMoveArray && !data.getisMoved())) || (data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved())
                        ? Color.WHITE
                        : Color.BLACK);*/
                h.lblVrpPostal.setTextColor(data.getOPS_Queue() != null && !data.isLaterJob ? Color.BLACK
                        : data.getOPS_Queue() != null && data.isLaterJob ? Color.YELLOW
                        : ((data.IsEnabledForJobUpdate && !EnableMoveArray && !data.getisMoved()) && data.isLaterJob) || (data.IsEnabledForJobUpdate && !EnableMoveArray && data.getisMoved() && data.isLaterJob)
                        ? Color.YELLOW
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

                h.lblVrpTW.setTextColor(data.getOPS_Queue() != null ? Color.BLACK
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
                h.setID(data.getID());
                h.setStartID(data.getStartID());

                /*data.TimeWindowsList.clear();
                TimeWindow t1 = new TimeWindow();
                t1.setStart("/Date(1755570600000+0800)/");
                t1.setEnd("/Date(1755576000000+0800)/");
                TimeWindow t2 = new TimeWindow();
                t2.setStart("/Date(1755583200000+0800)/");
                t2.setEnd("/Date(1755595800000+0800)/");
                TimeWindow t3 = new TimeWindow();
                t3.setStart("/Date(1755651600000+0800)/");
                t3.setEnd("/Date(1755662400000+0800)/");
                data.TimeWindowsList.add(t1);
                data.TimeWindowsList.add(t2);
                data.TimeWindowsList.add(t3);*/
                if (data.TimeWindowsList != null && data.TimeWindowsList.size() > 0) {
                    SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM HH:mm");
                    String tw = "";
                    Date dst = new Date();
                    Date dend = new Date();
                    Date ppst = new Date();
                    Date ppend = new Date();
                    String st = "";
                    String end = "";
                    String pst = "";
                    String pend = "";
                    int comp = 0;
                    for (int i = 0; i <= data.TimeWindowsList.size() -1; i++) {
                        st = data.TimeWindowsList.get(i).start;
                        end = data.TimeWindowsList.get(i).end;

                        if (!objGlobalMethods.IsEmptyString(end) && objGlobalMethods.IsEmptyString(pend)) {
                            dst = objGlobalMethods.JsonDateToDate(st);
                            dend = objGlobalMethods.JsonDateToDate(end);
                            tw = (formatter2.format(dst) + " - " + formatter2.format(dend));
                            pst = st;
                            pend = end;
                        } else if (!end.equals(pend)) {
                            ppst = objGlobalMethods.JsonDateToDate(pst);
                            ppend = objGlobalMethods.JsonDateToDate(pend);
                            dst = objGlobalMethods.JsonDateToDate(st);
                            dend = objGlobalMethods.JsonDateToDate(end);
                            //date1.compareTo(date2) > 0 : date1 after date2, == 0: date1 equal to date2, <0 date1 before date2
                            if (ppend.compareTo(dend) <= 0) {
                                tw = (formatter2.format(ppst) + " - " + formatter2.format(ppend));
                                pst = pst;
                                pend = pend;
                            } else if (ppend.compareTo(dend) > 0) {
                                tw = (formatter2.format(dst) + " - " + formatter2.format(dend));
                                pst = st;
                                pend = end;
                            }
                        }
                    }
                    if (!objGlobalMethods.IsEmptyString(tw)) {
                        h.lblVrpTW.setText(tw);
                        h.lblpVrpLunch.setVisibility(data.HASLUNCHTIME ? View.VISIBLE : View.GONE);
                    }
                } else {
                    h.lblVrpTW.setText("");
                    h.lblpVrpLunch.setVisibility(View.GONE);
                }

                if (data.getCUSTOM_ETA() != null && !data.getCUSTOM_ETA().equals("")) {
                    etadt = objGlobalMethods.JsonDateToDate(data.getCUSTOM_ETA());
                    eta = formatter.format(etadt);
                } else {
                    eta = ctx.getString(R.string.lbletatba);
                }

                h.cl.setOnTouchListener(null);
                h.cl.setOnLongClickListener(null);
                h.reorderView.setOnTouchListener(null);

                if (wasUpdated()) {
                    /*h.reorderView.setImageResource(
                            data.getType().equals("ops") || !EnableMoveArray ? R.mipmap.ic_blank :
                                    EnableMoveArray && data.isUpdated ? R.mipmap.ic_av_not_interested :
                                            EnableMoveArray && ID == 1 && ID == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                    EnableMoveArray && ID == 1 && ID < NewJQlistSize - 1 && !nextQueIsHeader && !nextQueIsMoved && data.StartID > ID ? R.mipmap.ic_action_expand_more :
                                                            EnableMoveArray && ID == 1 && ID < NewJQlistSize - 1 && !nextQueIsHeader && !nextQueIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                    EnableMoveArray && ID == 1 && ID < NewJQlistSize - 1 && !nextQueIsHeader && nextQueIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                            EnableMoveArray && ID > 1 && ID == NewJQlistSize - 1 && prevQueIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                    EnableMoveArray && ID > 1 && ID == NewJQlistSize - 1 && !prevQueIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                            EnableMoveArray && ID > 1 && ID < NewJQlistSize - 1 && prevQueIsHeader && !nextQueIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                    EnableMoveArray && ID > 1 && ID < NewJQlistSize - 1 && prevQueIsHeader && nextQueIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                            EnableMoveArray && ID > 1 && ID < NewJQlistSize - 1 && !prevQueIsHeader && !nextQueIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                    EnableMoveArray && ID > 1 && ID < NewJQlistSize - 1 && !prevQueIsHeader && nextQueIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                            EnableMoveArray && ID > 1 && ID < NewJQlistSize - 1 && !prevQueIsHeader && !nextQueIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                    R.mipmap.ic_av_not_interested
                    );*/

                    h.reorderView.setImageResource(R.mipmap.ic_blank);
                } else {
                    /*h.reorderView.setImageResource(
                            data.getType().equals("ops") || !EnableMoveArray ? R.mipmap.ic_blank :
                                    EnableMoveArray && data.ID == 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                            EnableMoveArray && data.ID > 1 ? R.mipmap.ic_action_expand_less :
                                                    R.mipmap.ic_av_not_interested
                    );*/
                    h.reorderView.setImageResource(R.mipmap.ic_blank);
                }

                //set which ctrl can drag. here we set reorderView ctrl to b e one that can b press, hold n drag.
                if (EnableMoveArray) {
                    ////this is for touch hold drag
                    /*h.reorderView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                                hh.cl.setBackgroundResource(R.drawable.gradient_bg_hover);
                                touchHelper.startDrag(holder);
                            }
                            return false;
                        }
                    });*/
                    ////this is for touch hold drag

                    //if (data != null && data.ID == 1 && !data.isUpdated && !data.IsHeader && data.getType().equals("vrp") && !data.isMoved) {
                    if (data != null && !data.isUpdated && !data.IsHeader && data.getType().equals("vrp") && !data.isMoved) {
                        h.cl.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                try {
                                    int currpos = getItemPosition(data);
                                    JobQueue currdata = NewJQlist.get(currpos);
                                    if (currdata != null && !currdata.isMoved) {
                                        amendJQListAfterMovedForLongClick(holder, true, currpos, hh);
                                    } else if (currdata != null && currdata.isMoved) {
                                        amendJQListAfterMovedForLongClick(holder, false, currpos, hh);
                                    }
                                } catch (Exception e) {
                                    String s = e.getMessage();
                                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 h.cl.setOnLongClickListener. Exception: " + s,"","","","");
                                } finally {
                                    return false;
                                }
                            }
                        });
                        try {

                        } catch (Exception e) {
                            String s = e.getMessage();
                        }
                    }
                }
                //set which ctrl can drag. here we set reorderView ctrl to b e one that can b press, hold n drag.

                h.lblVrpPostal.setText(data.getAllJobsCompleted() ? "" : ctx.getString(R.string.lbleta) + " " + eta);
                h.lblpNum_dNum.setText(data.getAllJobsCompleted() ? "" : ctx.getString(R.string.lblpNum) + " " + String.valueOf(data.getpNum()) + " " + ctx.getString(R.string.lbldNum) + " " + String.valueOf(data.getdNum()));
                h.lblCount.setText(data.getJobsCount() == null || data.getJobsCount() == 0 ? "0" : String.valueOf(data.getJobsCount()));

                h.lblVrpPostal.setVisibility(data.getAllJobsCompleted() ? View.GONE : View.VISIBLE);
                h.lblpNum_dNum.setVisibility(data.getAllJobsCompleted() ? View.GONE : View.VISIBLE);

                h.lblVrpCold.setVisibility((data.Flag2Sum & 64) != 64 ? View.GONE : View.VISIBLE);
                h.lblVrpNets.setVisibility((data.FlagSum & 4) != 4 ? View.GONE : View.VISIBLE);
                h.lblVrpVisa.setVisibility((data.FlagSum & 8) != 8 ? View.GONE : View.VISIBLE);
                h.lblVrpTradein.setVisibility((data.FlagSum & 16) != 16 ? View.GONE : View.VISIBLE);
                /*h.imgnets.setVisibility((data.FlagSum & 4) != 4 ? View.GONE : View.VISIBLE);
                h.imgcc.setVisibility((data.FlagSum & 8) != 8 ? View.GONE : View.VISIBLE);
                h.imgtradein.setVisibility((data.FlagSum & 16) != 16 ? View.GONE : View.VISIBLE);*/

                if (data.getCanClick() && !EnableMoveArray) {
                    h.cl.setOnClickListener(null);
                    h.cl.setOnLongClickListener(null);
                    h.cl.setOnTouchListener(null);
                    //((JobQueViewHolder2) holder).cl.setOnClickListener(new View.OnClickListener() {
                    h.cl.setOnClickListener(new View.OnClickListener() {
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
                                    //} else if (JobsCount > 1) {
                                    } else if (JobsCount > 0) {
                                        if (NewJQlist != null && NewJQlist.size() > 0 && currPos - 1 > 0 && !NewJQlist.get(currPos - 1).getIsHeader() && data.IsEnabledForJobUpdate) {

                                            String[] prefName2 = new String[]{"currPostal", "earlierPostal"};
                                            String[] prefvalue2 = new String[]{NewJQlist.get(currPos).getPostalCode(), NewJQlist.get(currPos - 1).getPostalCode()};
                                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);
                                        }

                                        String JobQueueDataJsonStr = new Gson().toJson(data);
                                        String[] prefName3 = new String[]{"SelectedVRPJobsIDXs", "enableJobUpdates", "pagemode", "JobQueueDataJsonStr"};
                                        String[] prefvalue3 = new String[]{
                                                data.getJobsIDXs() != null && !data.getJobsIDXs().equals("") ? data.getJobsIDXs() : "",
                                                String.valueOf(data.IsEnabledForJobUpdate),
                                                "vrp", JobQueueDataJsonStr
                                        };
                                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName3, prefvalue3);

                                        myApp.currentActivity.startActivity(i);
                                        final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                                        act.finish();
                                    } else {
                                        startRedirectionSQLite(ctx, GcmPreferences, String.valueOf(JobsCount), desPostal, data.IsEnabledForJobUpdate, data.JobsIDXs, currPos, data);
                                    }
                                }
                            } catch (Exception e) {
                                String s = JobsHandler.getStackTraceString(e);
                                objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 onBindViewHolder JobQueViewHolder2 onClick. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                            }
                        }
                    });
                }
            } else {
                final JobQueSectionHeaderViewHolder headerViewHolder = (JobQueSectionHeaderViewHolder) holder;
                headerViewHolder.lblCancel.setOnClickListener(null);
                headerViewHolder.lblEdit.setOnClickListener(null);
                headerViewHolder.lblUpdate.setOnClickListener(null);
                headerViewHolder.lblReset.setOnClickListener(null);
                headerViewHolder.lblCancel.setVisibility(View.GONE);
                headerViewHolder.lblEdit.setVisibility(View.GONE);
                headerViewHolder.lblUpdate.setVisibility(View.GONE);
                headerViewHolder.lblReset.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 onBindViewHolder. Exception: " + s, "", "", "", "");
        }
    }

    private void hideHeaderRow(Context ctx, JobQueSectionHeaderViewHolder headerViewHolder) {
        try {
            final float scale = ctx.getResources().getDisplayMetrics().density;
            int pixels = (int) (10 * scale + 0.5f);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, pixels);
            headerViewHolder.cl.setLayoutParams(rlp);
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 hideHeaderRow. Exception: " + s, "", "", "", "");
        }
    }

    private void startRedirectionSQLite(Context ctx, SharedPreferences GcmPreferences, String lblCount, String Postal, boolean enableJobUpdates, String JobsIDXs, int currPos, final JobQueue data) {
        Intent i;
        int count;
        final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
        try {
            if (!lblCount.isEmpty()) {
                if (!lblCount.toLowerCase().equals("error")) {
                    count = Integer.parseInt(lblCount.toString());
                    if (count > 0) {
                        /*if (act != null && act.pdl != null) {
                            act.pdl.show();
                        }*/
                        if (count > 1) {
                            //more than 1 jobs, go to addresslist
                            i = new Intent(ctx, DashboardSubActivity.class);
                            JobInfoActivity.accessFromDirection = 0;

                            if (NewJQlist != null && NewJQlist.size() > 0 && currPos - 1 > 0 && !NewJQlist.get(currPos - 1).getIsHeader() && enableJobUpdates) {

                                String[] prefName = new String[]{"currPostal", "earlierPostal"};
                                String[] prefvalue = new String[]{
                                        NewJQlist.get(currPos).getPostalCode(),
                                        NewJQlist.get(currPos - 1).getPostalCode()
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                            }

                            String JobQueueDataJsonStr = new Gson().toJson(data);
                            String[] prefName2 = new String[]{"enableJobUpdates", "SelectedVRPJobsIDXs", "JobQueueDataJsonStr"};
                            String[] prefvalue2 = new String[]{
                                    String.valueOf(enableJobUpdates),
                                    JobsIDXs, JobQueueDataJsonStr
                            };
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                            myApp.currentActivity.startActivity(i);
                            //final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                            /*if (act != null && act.pdl != null && act.pdl.isShowing()) {
                                act.pdl.dismiss();
                            }*/
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

                                if (isDunning) {
                                    i = new Intent(ctx, setDunningDeliveredStatusActivity.class);
                                    JobInfoActivity.accessFromDirection = 0;
                                } else {
                                    i = new Intent(ctx, JobInfoActivity.class);
                                    JobInfoActivity.accessFromDirection = 1;
                                }

                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                if (NewJQlist != null && NewJQlist.size() > 0 && currPos - 1 > 0 && !NewJQlist.get(currPos - 1).getIsHeader() && enableJobUpdates) {

                                    String[] prefName = new String[]{"currPostal", "earlierPostal"};
                                    String[] prefvalue = new String[]{
                                            NewJQlist.get(currPos).getPostalCode(),
                                            NewJQlist.get(currPos - 1).getPostalCode()
                                    };
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                }

                                String JobQueueDataJsonStr = new Gson().toJson(data);
                                String[] prefName2 = new String[]{"isFromDashboardSub", objGlobalVariables.categoryTypePrefName, "SelectedVRPJobsIDXs", "enableJobUpdates", "JobQueueDataJsonStr"};
                                String[] prefvalue2 = new String[]{
                                        "true",
                                        String.valueOf(categoryType),
                                        JobsIDXs,
                                        String.valueOf(enableJobUpdates),
                                        JobQueueDataJsonStr
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                                //ctx.startActivity(i);
                                myApp.currentActivity.startActivity(i);
                                //final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                                /*if (act != null && act.pdl != null && act.pdl.isShowing()) {
                                    act.pdl.dismiss();
                                }*/
                                act.finish();
                            } else if (proceed > 1) {
                                //more than 1 jobs, go to addresslist
                                i = new Intent(ctx, DashboardSubActivity.class);
                                JobInfoActivity.accessFromDirection = 0;

                                if (NewJQlist != null && NewJQlist.size() > 0 && currPos - 1 > 0 && !NewJQlist.get(currPos - 1).getIsHeader() && enableJobUpdates) {

                                    String[] prefName = new String[]{"currPostal", "earlierPostal"};
                                    String[] prefvalue = new String[]{
                                            NewJQlist.get(currPos).getPostalCode(),
                                            NewJQlist.get(currPos - 1).getPostalCode()
                                    };
                                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                }

                                String JobQueueDataJsonStr = new Gson().toJson(data);
                                String[] prefName2 = new String[]{"enableJobUpdates", "SelectedVRPJobsIDXs", "JobQueueDataJsonStr"};
                                String[] prefvalue2 = new String[]{
                                        String.valueOf(enableJobUpdates),
                                        JobsIDXs, JobQueueDataJsonStr
                                };
                                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                                //ctx.startActivity(i);
                                myApp.currentActivity.startActivity(i);
                                //final DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                                /*if (act != null && act.pdl != null && act.pdl.isShowing()) {
                                    act.pdl.dismiss();
                                }*/
                                act.finish();
                            } else if (proceed == 0) {
                                /*if (act != null && act.pdl != null && act.pdl.isShowing()) {
                                    act.pdl.dismiss();
                                }*/
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
            /*if (act != null && act.pdl != null && act.pdl.isShowing()) {
                act.pdl.dismiss();
            }*/
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception caught at VrpList_RecycleViewAdapter2 startRedirectionSQLite. Exception: " + s, "", "", "", "");
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


                            addressWithUnit = objAL.get(0).getAddressWithoutPostal();

                            String[] prefName = new String[]{"addressWithUnit", objGlobalVariables.currJobInfoIDX, "currJobInfoCN"};
                            String[] prefvalue = new String[]{
                                    addressWithUnit,
                                    String.valueOf(JobInfo.JobsIDX),
                                    JobInfo.JobNo
                            };
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
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
            objGlobalMethods.writeToFile("Exception caught at VrpList_RecycleViewAdapter2 initRedirectionToJobInfoSQLite. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
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
            objGlobalMethods.writeToFile("Exception caught at VrpList_RecycleViewAdapter2 setitemEnableMoveArray. Exception: " + s, "", "", "", "");
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
            //if (currentjq.getID() == jq.getID()) break;
            if (currentjq.getItemID() == jq.getItemID()) break;
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

    public void setsaveAmended4QuesJsonStr(String asaveAmended4QuesJsonStr) {
        this.saveAmended4QuesJsonStr = asaveAmended4QuesJsonStr;
    }

    public void setNewJQlist(List<JobQueue> aNewJQlist) {
        boolean EnableMoveArray;
        isAllWhite = true;
        try {
            this.NewJQlist = new ArrayList<JobQueue>(aNewJQlist);
            //isAllWhite check is shifted here
            if (this.NewJQlist != null && this.NewJQlist.size() > 1) {
                JobQueue j = null;
                for (int i = 0; i <= this.NewJQlist.size() -1; i++) {
                    j = this.NewJQlist.get(i);
                    /*if (!j.getIsHeader()) {
                        if (isAllWhite) {
                            EnableMoveArray = itemEnableMoveArray.get(i, false);
                            isAllWhite = (j.getOPS_Queue() != null) ||
                                    (j.IsEnabledForJobUpdate && !EnableMoveArray && j.isUpdated) ||
                                    ((j.IsEnabledForJobUpdate || !j.IsEnabledForJobUpdate) && EnableMoveArray && j.getisMoved()) ||
                                    ((j.IsEnabledForJobUpdate || !j.IsEnabledForJobUpdate) && EnableMoveArray && !j.getisMoved())
                                    ? false : true;
                        }
                    }*/
                    if (!j.getIsHeader() && isAllWhite) {
                        EnableMoveArray = itemEnableMoveArray.get(i, false);
                        isAllWhite = (j.getOPS_Queue() != null) ||
                                (j.IsEnabledForJobUpdate && !EnableMoveArray && j.isUpdated) ||
                                ((j.IsEnabledForJobUpdate || !j.IsEnabledForJobUpdate) && EnableMoveArray && j.getisMoved()) ||
                                ((j.IsEnabledForJobUpdate || !j.IsEnabledForJobUpdate) && EnableMoveArray && !j.getisMoved());
                        if (!isAllWhite)
                            break;
                    }
                }
            }
            //isAllWhite check is shifted here
            notifyDataSetChanged();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 setNewJQlist. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    public void setOriginalJQlist(List<JobQueue> aNewJQlist) {
        List<JobQueue> al = new ArrayList<JobQueue>();
        try {
            if (aNewJQlist != null && aNewJQlist.size() > 0) {
                for (JobQueue v : aNewJQlist) {
                    JobQueue q = new JobQueue(v);
                    al.add(q);
                }
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 setOriginalJQlist. Exception: " + s, "", "", "", "");
            al = null;
        } finally {
            this.originalJQlist = al;
        }
    }

    public void resetNewJQlist(List<JobQueue> aNewJQlist) {
        List<JobQueue> al = new ArrayList<JobQueue>();
        try {
            for (JobQueue v : aNewJQlist) {
                JobQueue q = new JobQueue(v);
                al.add(q);
            }
            this.NewJQlist = al;
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 resetNewJQlist. Exception: " + s, "", "", "", "");
        }
    }

    public List<JobQueue> getJQlist() {
        List<JobQueue> al = null;
        try {
            al = this.NewJQlist;
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 getJQlist. Exception: " + s, "", "", "", "");
            al = null;
        } finally {
            return al;
        }
    }

    public List<JobQueue> getOriginalJQlist() {
        List<JobQueue> al = null;
        try {
            al = this.originalJQlist;
            if (al != null && al.size() > 0) {
                for (int i = 0; i <= al.size() -1; i++) {
                    al.get(i).setID(i);
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 getOriginalJQlist. Exception: " + s, "", "", "", "");
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
            JobQueue eq = null;
            JobQueue eeq = null;
            JobQueue nq = null;
            boolean eeqIsMoved = false;
            boolean eqIsMoved = false;
            boolean nqIsMoved = false;
            int eqOldID = 0;
            hasMovements = true;
            for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                JobQueue j = NewJQlist.get(i);

                if (!j.getIsHeader() && (
                        (finaloldPosition > finalnewPosition && (i <= finaloldPosition && i >= finalnewPosition)) ||
                                (finaloldPosition < finalnewPosition && (i <= finalnewPosition && i >= finaloldPosition)))) {
                    if (!wasUpdated()) {
                        if (i != j.getArrayIdx() && i < j.getArrayIdx()) {
                            //shift up
                            upcount = j.getSUpCount() + (j.getArrayIdx() - i);
                            downcount = j.getSDownCount() - (j.getArrayIdx() - i);
                            j.setSUpCount(upcount);

                            j.setSDownCount(downcount < 0 ? 0 : downcount);
                            j.setOldID(j.getArrayIdx());
                            j.setID(i);
                            if (i == j.StartID) {
                                j.setOldID(i);
                            }
                        } else if (i != j.getArrayIdx() && i > j.getArrayIdx()) {
                            //shift down
                            supcount = j.getSUpCount();
                            upcount = j.getSUpCount() - (i - j.getArrayIdx());
                            downcount = supcount > 0 ? 0 : j.getSDownCount() + (i - j.getArrayIdx());
                            j.setSUpCount(upcount);

                            j.setSDownCount(downcount < 0 ? 0 : downcount);
                            j.setOldID(j.getArrayIdx());
                            j.setID(i);
                            if (i == j.StartID) {
                                j.setOldID(i);
                            }
                            eq = i - 1 <= 0 ? null : NewJQlist.get(i - 1);
                            nq = i + 1 > 0 && i + 1 <= NewJQlist.size() - 1 ? NewJQlist.get(i + 1) : null;
                            if (eq != null) {
                                eqOldID = eq.getOldID();
                                eqIsMoved = eq.getisMoved();
                                eeq = i - 2 <= 0 ? null : NewJQlist.get(i - 2);
                                if (eeq != null) {
                                    eeqIsMoved = eeq.getisMoved();
                                }
                            }
                            if (nq != null) {
                                nqIsMoved = nq.getisMoved();
                            }

                            if (!eqIsMoved && !nqIsMoved && jq.StartID == i) {
                                jq.setisMoved(false);
                            } else if (eqIsMoved && nqIsMoved && jq.StartID == i) {
                                jq.setisMoved(true);
                            } else if (eqIsMoved && !nqIsMoved && jq.StartID != i) {
                                jq.setisMoved(true);
                            }
                        }
                    } else {
                        //j.setisMoved(true);
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

                }
                NewJQlist.set(i, j);
            }

            //int o = NewJQlist.size();
        } catch (Exception e) {
            String x = JobsHandler.getStackTraceString(e);
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved. Exception: " + s + " Stack: " + x, "", "", "", "");
            finaloldPosition = null;
            finalnewPosition = null;
        }
    }

    public void amendJQListAfterMovedForLongClick(RecyclerView.ViewHolder viewHolder, boolean isMoved, int idx, JobQueViewHolder2 hh){
        try {
            JobQueue jq = null;
            JobQueue jjq = null;
            boolean isupdated = false;
            if (NewJQlist != null && NewJQlist.size() > 0) {
                for (int i = 0; i <= NewJQlist.size() -1; i++) {
                    NewJQlist.get(i).setisMoved(false);
                }
                NewJQlist.get(idx).setisMoved(isMoved);
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter3 amendJQListAfterMovedForLongClick. Exception: " + s, "", "", "", "");
        } finally {
            notifyDataSetChanged();
        }
    }

    public void amendJQListAfterMoved5(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        try {
            int viewHolderIDX = viewHolder.getLayoutPosition();
            JobQueue jq = null;
            JobQueue jjq = null;
            boolean isupdated = false;
            if (hasMovements) {
                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    NewJQlist.get(i).setID(i);
                }

                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    if (jq != null && !jq.IsHeader && jq.getType().equals("vrp") && !isupdated) {
                        jq.setisMoved(true);
                        isupdated = true;
                        break;
                    }
                }
                if (jq != null && isupdated) {
                    List<JobQueue> ol = getOriginalJQlist();
                    setNewJQlist(ol);
                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jjq = NewJQlist.get(i);
                        if (jjq.ItemID == jq.ItemID) {
                            NewJQlist.remove(i);
                            break;
                        }
                    }

                    NewJQlist.add(jq.ID, jq);

                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        NewJQlist.get(i).setID(i);
                        if (i == jq.ID && jq.ID == jq.ItemID) {
                            NewJQlist.get(i).setisMoved(false);
                        } else if (i > jq.ID) {
                            NewJQlist.get(i).setisMoved(false);
                        }
                    }
                }
                hasMovements = false;
            } else {
                JobQueViewHolder2 vh = null;
                if (viewHolderIDX > 0) {
                    List<JobQueue> First_4_JobQueue = null;
                    VrpList_RecycleViewAdapter2 adapter2 = (VrpList_RecycleViewAdapter2) recyclerView.getAdapter();
                    if (adapter2 != null) {
                        First_4_JobQueue = adapter2.getJQlist();
                        if (First_4_JobQueue != null && First_4_JobQueue.size() > 0 && viewHolderIDX <= First_4_JobQueue.size() - 1) {
                            jq = First_4_JobQueue.get(viewHolderIDX);
                            vh = (JobQueViewHolder2) recyclerView.findViewHolderForAdapterPosition(viewHolderIDX);
                            if (vh != null) {
                                if (!wasUpdated()) {
                                    vh.cl.setBackgroundResource(
                                            jq.getType().equals("ops") ? R.color.mainorange :
                                                    jq.getisMoved() ? R.color.brightgreen
                                                            : R.color.dialog_bg);
                                } else {
                                    vh.cl.setBackgroundResource(
                                            jq.getType().equals("ops") ? R.color.mainorange :
                                                    jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isMoved ? R.color.brightgreen :
                                                            jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                                    R.color.dialog_bg);
                                }
                                vh.lblVrpAddress.setTextColor(Color.BLACK);
                                vh.lblVrpPostal.setTextColor(Color.BLACK);
                                vh.lblpNum_dNum.setTextColor(Color.BLACK);
                                vh.lblCount.setTextColor(Color.BLACK);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved5. Exception: " + s, "", "", "", "");
        } finally {
            notifyDataSetChanged();
        }
    }

    public void amendJQListAfterMoved4(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int finaldragflags) {
        try {
            int viewHolderIDX = viewHolder.getLayoutPosition();
            int stopID = 0;
            JobQueue eq = null;
            JobQueue jq = null;
            JobQueue nq = null;
            JobQueue xq = null;
            boolean donothing = false;
            int updatedcount = 0;
            if (hasMovements) {
                if (!wasUpdated()) {
                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jq = NewJQlist.get(i);
                        nq = getEQNQ(i + 1);
                        if (!jq.IsHeader) {
                            if (jq.StartID > i && jq.SUpCount > 0) {
                                stopID = i;
                            } else if (jq.StartID == i) {
                                eq = getEQNQ(i - 1);
                                nq = getEQNQ(i + 1);
                                if (eq == null) {
                                    //do nothing
                                    donothing = true;
                                } else if (eq != null && !eq.IsHeader && eq.StartID < i - 1 && nq != null && !nq.IsHeader && nq.StartID == i + 1) {
                                    //do nothing
                                    donothing = true;
                                } else if (nq != null && !nq.IsHeader && nq.StartID < jq.StartID) {
                                    stopID = i;
                                }
                            } else if (jq.StartID < i) {
                                eq = getEQNQ(i - 1);
                                nq = getEQNQ(i + 1);
                                if (eq == null) {
                                    //do nothing
                                    donothing = true;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID == i + 1 && nq.isMoved && nq.StartID > jq.StartID) {
                                    //stopID = i;
                                    boolean hasLowerStartID = false;
                                    for (int x = i + 1; x <= NewJQlist.size() - 1; x++) {
                                        xq = NewJQlist.get(x);
                                        if (xq != null && xq != null && !xq.IsHeader && xq.StartID < jq.StartID) {
                                            hasLowerStartID = true;
                                            break;
                                        }
                                    }
                                    if (hasLowerStartID) {
                                        stopID = i;
                                    }
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID == i + 1 && nq.isMoved) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID > i + 1 && nq.isMoved) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID < i + 1 && !nq.isMoved && nq.StartID < jq.StartID) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID < i + 1 && nq.isMoved && !jq.isMoved && jq.ID == viewHolderIDX) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID < i + 1 && nq.isMoved) {
                                    //do nothing
                                    donothing = true;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID == i - 1 && !nq.IsHeader && nq.StartID < i + 1 && !nq.isMoved && jq.isMoved) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID == i - 1 && !nq.IsHeader && nq.StartID < i + 1 && !nq.isMoved && !jq.isMoved && jq.ID == viewHolderIDX) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID < i - 1 && !nq.IsHeader && nq.StartID < i + 1 && !nq.isMoved && jq.isMoved) {
                                    stopID = i;
                                }
                            }
                        }
                    }

                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jq = NewJQlist.get(i);
                        if (!jq.IsHeader && i <= stopID) {
                            NewJQlist.get(i).setisMoved(true);
                        } else if (!jq.IsHeader && i > stopID) {
                            NewJQlist.get(i).setisMoved(false);
                        }
                    }
                } else {
                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jq = NewJQlist.get(i);
                        nq = getEQNQ(i + 1);
                        if (!jq.IsHeader) {
                            if (jq.StartID > i && jq.SUpCount > 0 && !jq.isUpdated) {
                                if (nq != null && !nq.IsHeader && !nq.isUpdated && !nq.isMoved) {
                                    stopID = i;
                                } else if (nq != null && !nq.IsHeader && !nq.isUpdated && nq.isMoved) {
                                    //do nothing
                                    donothing = true;
                                }
                            } else if (jq.StartID == i && !jq.isUpdated && jq.isMoved) {
                                int c = 2;
                                for (int x = i + 1; x <= NewJQlist.size() - 1; x++) {
                                    xq = NewJQlist.get(x);
                                    if (xq != null && !xq.IsHeader) {
                                        if ((!xq.isUpdated && xq.isMoved && xq.SUpCount > 0) || (xq.isUpdated && xq.isMoved)) {
                                            stopID = x;
                                            break;
                                        } else if (!xq.isUpdated && !xq.isMoved) {
                                            if (jq.StartID > xq.StartID) {
                                                stopID = i;
                                                break;
                                            } else if (i - c > stopID) {
                                                stopID = i - c - 1;
                                                break;
                                            }
                                            /*stopID = x - c;
                                            break;*/
                                        }
                                        c++;
                                        /*if ((!xq.isUpdated && xq.isMoved && xq.SUpCount > 0) || (xq.isUpdated && xq.isMoved)) {
                                            stopID = x;
                                            break;
                                        }*/
                                    }
                                }
                            } else if (jq.StartID == i && jq.isUpdated) {
                                if (nq != null && !nq.IsHeader && nq.isMoved && nq.StartID > jq.StartID) {
                                    stopID = i;
                                } else if (nq != null && !nq.IsHeader && !nq.isMoved && nq.StartID > jq.StartID) {
                                    stopID = i;
                                }
                            } else if (jq.StartID == i && !jq.isUpdated && jq.isMoved) {
                                if (nq != null && !nq.IsHeader && nq.isUpdated && nq.isMoved && nq.StartID > jq.StartID) {
                                    //do nothing
                                    donothing = true;
                                } else if (nq != null && !nq.IsHeader && nq.isMoved && nq.StartID > jq.StartID) {
                                    stopID = i;
                                }
                            } else if (jq.StartID == i && !jq.isUpdated && !jq.isMoved) {
                                //i==5
                                int c = 1;
                                for (int x = i + 1; x <= NewJQlist.size() - 1; x++) {
                                    xq = NewJQlist.get(x);
                                    if (xq != null && !xq.IsHeader && !xq.isUpdated && xq.isMoved && nq != null && !nq.IsHeader && !nq.isMoved) {
                                        //do nothing
                                        break;
                                    } else if (xq != null && !xq.IsHeader && !xq.isUpdated && xq.isMoved) {
                                        if (jq.StartID + c < xq.StartID && jq.ID != viewHolderIDX) {
                                            stopID = x;
                                            break;
                                        }
                                        /*if (jq.StartID + c < xq.StartID) {
                                            stopID = x;
                                            break;
                                        }*/
                                    } else if (xq != null && !xq.IsHeader && !xq.isUpdated && !xq.isMoved && xq.StartID != x) {
                                        eq = getEQNQ(i - 1);
                                        if (eq != null && !eq.IsHeader && eq.isUpdated) {
                                            stopID = x;
                                        } else if (eq != null && !eq.IsHeader && !eq.isUpdated && eq.isMoved && !jq.isMoved && jq.ID == viewHolderIDX) {
                                            stopID = i;
                                        } else if (eq != null && !eq.IsHeader && !eq.isUpdated && eq.isMoved) {
                                            stopID = x;
                                        }
                                    }
                                    c++;
                                }
                            } else if (jq.StartID != i && jq.isUpdated) {
                                if (nq != null && !nq.IsHeader && !nq.isUpdated && !nq.isMoved) {
                                    stopID = i;
                                } else if (nq != null && !nq.IsHeader && nq.isUpdated) {
                                    stopID = i;
                                } else if (nq != null && !nq.IsHeader && !nq.isUpdated && nq.isMoved) {
                                    eq = getEQNQ(i - 1);
                                    if (eq != null && !eq.IsHeader && eq.isUpdated && eq.StartID > jq.StartID) {
                                        stopID = i;
                                    } else if (eq != null && !eq.IsHeader && !eq.isUpdated && eq.StartID > jq.StartID) {
                                        stopID = i;
                                    } else if (eq != null && !eq.IsHeader && eq.isUpdated && eq.StartID < jq.StartID) {
                                        stopID = i;
                                    } else {
                                        //do nothing
                                        donothing = true;
                                    }
                                    /*//do nothing
                                    donothing = true;*/
                                }
                            } else if (jq.StartID != i && !jq.isUpdated && !jq.isMoved && jq.ID == viewHolderIDX) {
                                //do nothing
                                stopID = i;
                            } else if (jq.StartID != i && !jq.isUpdated && !jq.isMoved) {
                                //do nothing
                                donothing = true;
                            } else if (jq.StartID != i && !jq.isUpdated) {
                                if (nq != null && !nq.IsHeader && jq.StartID > nq.StartID) {
                                    stopID = i;
                                }
                            }
                        }
                    }

                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jq = NewJQlist.get(i);
                        if (stopID > 0) {
                            if (!jq.IsHeader && i <= stopID) {
                                NewJQlist.get(i).setisMoved(true);
                            } else if (!jq.IsHeader && i > stopID) {
                                NewJQlist.get(i).setisMoved(false);
                            }
                        }
                    }
                }

                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    if (jq != null && !jq.IsHeader) {
                        if (jq.getType().equals("ops")) {
                            donothing = true;
                        } else if (jq.getType().equals("vrp") && jq.isUpdated && updatedcount < 4) {
                            jq.setisMoved(true);
                        } else if (jq.getType().equals("vrp") && !jq.isUpdated && jq.isMoved && updatedcount < 4) {
                            jq.setisMoved(true);
                        } else {
                            jq.setisMoved(false);
                        }
                        updatedcount++;
                    }
                }

                JobQueViewHolder2 vh = null;
                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    jq.setArrayIdx(i);
                    if (!jq.getIsHeader()) {
                        vh = (JobQueViewHolder2) recyclerView.findViewHolderForAdapterPosition(i);
                        if (vh != null) {
                            if (!wasUpdated()) {
                                vh.cl.setBackgroundResource(
                                        jq.getType().equals("ops") ? R.color.mainorange :
                                                jq.getisMoved() ? R.color.brightgreen
                                                        : R.color.dialog_bg);
                            } else {
                                vh.cl.setBackgroundResource(
                                        jq.getType().equals("ops") ? R.color.mainorange :
                                                jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isMoved ? R.color.brightgreen :
                                                        jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                                R.color.dialog_bg);
                            }


                            boolean eqIsHeader = false;
                            boolean eqIsMoved = false;
                            boolean nqIsHeader = false;
                            boolean nqIsMoved = false;
                            boolean prevQueCannotShiftDown = false;
                            int NewJQlistSize = NewJQlist.size();

                            if (i > 0) {
                                try {
                                    JobQueue prevq = NewJQlist.get(i - 1);
                                    eqIsHeader = prevq.getIsHeader();
                                    eqIsMoved = !eqIsHeader && prevq.getisMoved();
                                    prevQueCannotShiftDown = prevq.getSDownCount() == 2 && prevq.getType().equals("vrp") && !prevq.getIsHeader();
                                    JobQueue nextq = i == NewJQlist.size() - 1 ? null : NewJQlist.get(i + 1);
                                    if (nextq != null) {
                                        nqIsHeader = nextq.getIsHeader();
                                        nqIsMoved = !nqIsHeader && nextq.getisMoved();
                                    }
                                } catch (Exception e) {
                                    String s = e.getMessage().toString();
                                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved4 prevQueCannotShiftDown checks. Exception: " + s, "", "", "", "");
                                }
                            }

                            if (wasUpdated()) {
                                vh.reorderView.setImageResource(
                                        jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                                jq.isUpdated ? R.mipmap.ic_av_not_interested :
                                                        i == 1 && i == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved && jq.StartID > i ? R.mipmap.ic_action_expand_more :
                                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                                        i > 1 && i == NewJQlistSize - 1 && eqIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                                i > 1 && i == NewJQlistSize - 1 && !eqIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                                        i > 1 && i < NewJQlistSize - 1 && eqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                                i > 1 && i < NewJQlistSize - 1 && eqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && nqIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                                R.mipmap.ic_av_not_interested
                                );
                            } else {
                                vh.reorderView.setImageResource(
                                        jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                                i == 1 && i == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved && jq.StartID > i ? R.mipmap.ic_action_expand_more :
                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                                i > 1 && i == NewJQlistSize - 1 && eqIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                        i > 1 && i == NewJQlistSize - 1 && !eqIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                                i > 1 && i < NewJQlistSize - 1 && eqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                        i > 1 && i < NewJQlistSize - 1 && eqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && nqIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                        R.mipmap.ic_av_not_interested
                                );
                            }

                            vh.lblVrpAddress.setTextColor(Color.BLACK);
                            vh.lblVrpPostal.setTextColor(Color.BLACK);
                            vh.lblpNum_dNum.setTextColor(Color.BLACK);
                            vh.lblCount.setTextColor(Color.BLACK);
                        }
                    }
                }
                hasMovements = false;
            } else {
                JobQueViewHolder2 vh = null;
                if (viewHolderIDX > 0) {
                    List<JobQueue> First_4_JobQueue = null;
                    VrpList_RecycleViewAdapter2 adapter2 = (VrpList_RecycleViewAdapter2) recyclerView.getAdapter();
                    if (adapter2 != null) {
                        First_4_JobQueue = adapter2.getJQlist();
                        if (First_4_JobQueue != null && First_4_JobQueue.size() > 0 && viewHolderIDX <= First_4_JobQueue.size() - 1) {
                            jq = First_4_JobQueue.get(viewHolderIDX);
                            vh = (JobQueViewHolder2) recyclerView.findViewHolderForAdapterPosition(viewHolderIDX);
                            if (vh != null) {
                                if (!wasUpdated()) {
                                    vh.cl.setBackgroundResource(
                                            jq.getType().equals("ops") ? R.color.mainorange :
                                                    jq.getisMoved() ? R.color.brightgreen
                                                            : R.color.dialog_bg);
                                } else {
                                    vh.cl.setBackgroundResource(
                                            jq.getType().equals("ops") ? R.color.mainorange :
                                                    jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isMoved ? R.color.brightgreen :
                                                            jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                                    R.color.dialog_bg);
                                }
                                vh.lblVrpAddress.setTextColor(Color.BLACK);
                                vh.lblVrpPostal.setTextColor(Color.BLACK);
                                vh.lblpNum_dNum.setTextColor(Color.BLACK);
                                vh.lblCount.setTextColor(Color.BLACK);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved4. Exception: " + s, "", "", "", "");
        }
    }

    public void amendJQListAfterMoved3(RecyclerView.ViewHolder viewHolder, boolean isMoved, int idx, JobQueViewHolder2 hh) {
        try {
            int stopID = 0;
            JobQueue eq = null;
            JobQueue jq = null;
            JobQueue nq = null;
            JobQueue xq = null;
            boolean donothing = false;

            if (NewJQlist != null && NewJQlist.size() > 0) {
                NewJQlist.get(idx).setisMoved(isMoved);

                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    eq = getEQNQ(i - 1);
                    nq = getEQNQ(i + 1);
                    if (!jq.IsHeader) {
                        if (jq.StartID > i && jq.SUpCount > 0 && !jq.isUpdated) {
                            if (nq != null && !nq.IsHeader && !nq.isUpdated && !nq.isMoved) {
                                stopID = i;
                            } else if (nq != null && !nq.IsHeader && !nq.isUpdated && nq.isMoved) {
                                //do nothing
                                donothing = true;
                            }
                        } else if (jq.StartID == i && !jq.isUpdated && jq.isMoved) {
                            int c = 2;
                            for (int x = i + 1; x <= NewJQlist.size() - 1; x++) {
                                xq = NewJQlist.get(x);
                                if (xq != null && !xq.IsHeader) {
                                    if ((!xq.isUpdated && xq.isMoved && xq.SUpCount > 0) || (xq.isUpdated && xq.isMoved)) {
                                        stopID = x;
                                        break;
                                    } else if (!xq.isUpdated && !xq.isMoved) {
                                        if (jq.StartID > xq.StartID) {
                                            stopID = i;
                                            break;
                                        } else if (i - c > stopID) {
                                            stopID = i - c - 1;
                                            break;
                                        } else if (nq != null && !nq.IsHeader && nq.isMoved && jq.StartID + 1 == nq.StartID) {
                                            stopID = i + 1;
                                            break;
                                        } else if (eq != null && eq.isMoved) {
                                            stopID = i;
                                            break;
                                        }
                                            /*stopID = x - c;
                                            break;*/
                                    }
                                    c++;
                                        /*if ((!xq.isUpdated && xq.isMoved && xq.SUpCount > 0) || (xq.isUpdated && xq.isMoved)) {
                                            stopID = x;
                                            break;
                                        }*/
                                }
                            }
                        } else if (jq.StartID == i && jq.isUpdated) {
                            if (nq != null && !nq.IsHeader && nq.isMoved && nq.StartID > jq.StartID) {
                                stopID = i;
                            } else if (nq != null && !nq.IsHeader && !nq.isMoved && nq.StartID > jq.StartID) {
                                stopID = i;
                            }
                        } else if (jq.StartID == i && !jq.isUpdated && jq.isMoved) {
                            if (nq != null && !nq.IsHeader && nq.isUpdated && nq.isMoved && nq.StartID > jq.StartID) {
                                //do nothing
                                donothing = true;
                            } else if (nq != null && !nq.IsHeader && nq.isMoved && nq.StartID > jq.StartID) {
                                stopID = i;
                            }
                        } else if (jq.StartID == i && !jq.isUpdated && !jq.isMoved) {
                            //i==5
                            int c = 1;
                            for (int x = i + 1; x <= NewJQlist.size() - 1; x++) {
                                xq = NewJQlist.get(x);
                                if (xq != null && !xq.IsHeader && !xq.isUpdated && xq.isMoved) {
                                    if (jq.StartID + c < xq.StartID) {
                                        stopID = x;
                                        break;
                                    }
                                    c++;
                                }
                            }
                        } else if (jq.StartID != i && jq.isUpdated) {
                            if (nq != null && !nq.IsHeader && !nq.isUpdated && !nq.isMoved) {
                                stopID = i;
                            } else if (nq != null && !nq.IsHeader && nq.isUpdated) {
                                stopID = i;
                            } else if (nq != null && !nq.IsHeader && !nq.isUpdated && nq.isMoved) {
                                if (eq != null && !eq.IsHeader && eq.isUpdated && eq.StartID > jq.StartID) {
                                    stopID = i;
                                } else if (eq != null && !eq.IsHeader && !eq.isUpdated && eq.StartID > jq.StartID) {
                                    stopID = i;
                                } else if (eq != null && !eq.IsHeader && eq.isUpdated && eq.StartID < jq.StartID) {
                                    stopID = i;
                                } else {
                                    //do nothing
                                    donothing = true;
                                }
                                    /*//do nothing
                                    donothing = true;*/
                            }
                        } else if (jq.StartID != i && !jq.isUpdated && jq.isMoved) {
                            if (eq != null && eq.isMoved) {
                                stopID = i;
                            }
                        } else if (jq.StartID != i && !jq.isUpdated) {
                            if (nq != null && !nq.IsHeader && jq.StartID > nq.StartID) {
                                stopID = i;
                            }
                        }
                    }
                }


                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    if (stopID > 0) {
                        if (!jq.IsHeader && !jq.getType().equals("ops") && i <= stopID) {
                            NewJQlist.get(i).setisMoved(true);
                        } else if (!jq.IsHeader && !jq.getType().equals("ops") && i > stopID) {
                            NewJQlist.get(i).setisMoved(false);
                        }
                    }
                }

                hh.cl.setBackgroundResource(isMoved ? R.color.brightgreen : R.color.dialog_bg);

                JobQueViewHolder2 vh = null;
                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    jq.setArrayIdx(i);
                    if (!jq.getIsHeader()) {
                        vh = (JobQueViewHolder2) viewHolder;
                        if (vh != null) {
                            boolean eqIsHeader = false;
                            boolean eqIsMoved = false;
                            boolean nqIsHeader = false;
                            boolean nqIsMoved = false;
                            boolean prevQueCannotShiftDown = false;
                            int NewJQlistSize = NewJQlist.size();

                            if (i > 0) {
                                try {
                                    JobQueue prevq = NewJQlist.get(i - 1);
                                    eqIsHeader = prevq.getIsHeader();
                                    eqIsMoved = !eqIsHeader && prevq.getisMoved();
                                    prevQueCannotShiftDown = prevq.getSDownCount() == 2 && prevq.getType().equals("vrp") && !prevq.getIsHeader();
                                    JobQueue nextq = i == NewJQlist.size() - 1 ? null : NewJQlist.get(i + 1);
                                    if (nextq != null) {
                                        nqIsHeader = nextq.getIsHeader();
                                        nqIsMoved = !nqIsHeader && nextq.getisMoved();
                                    }
                                } catch (Exception e) {
                                    String s = e.getMessage().toString();
                                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved prevQueCannotShiftDown checks. Exception: " + s, "", "", "", "");
                                }
                            }

                            if (wasUpdated()) {
                                vh.reorderView.setImageResource(
                                        jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                                i == 1 && i == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved && jq.StartID > i ? R.mipmap.ic_action_expand_more :
                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                                i > 1 && i == NewJQlistSize - 1 && eqIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                        i > 1 && i == NewJQlistSize - 1 && !eqIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                                i > 1 && i < NewJQlistSize - 1 && eqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                        i > 1 && i < NewJQlistSize - 1 && eqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && nqIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                        R.mipmap.ic_av_not_interested
                                );
                            } else {
                                vh.reorderView.setImageResource(
                                        jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                                i == 1 && i == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved && jq.StartID > i ? R.mipmap.ic_action_expand_more :
                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                                i > 1 && i == NewJQlistSize - 1 && eqIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                        i > 1 && i == NewJQlistSize - 1 && !eqIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                                i > 1 && i < NewJQlistSize - 1 && eqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                        i > 1 && i < NewJQlistSize - 1 && eqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && nqIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                        R.mipmap.ic_av_not_interested
                                );
                            }

                            vh.lblVrpAddress.setTextColor(Color.BLACK);
                            vh.lblVrpPostal.setTextColor(Color.BLACK);
                            vh.lblpNum_dNum.setTextColor(Color.BLACK);
                            vh.lblCount.setTextColor(Color.BLACK);
                        }
                    }
                }


            }

            notifyDataSetChanged();
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter3 amendJQListAfterMoved2. Exception: " + s, "", "", "", "");
        }
    }

    public void amendJQListAfterMoved2(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int finaldragflags) {
        try {
            int viewHolderIDX = viewHolder.getLayoutPosition();
            int stopID = 0;
            JobQueue eq = null;
            JobQueue jq = null;
            JobQueue nq = null;
            JobQueue xq = null;
            boolean donothing = false;
            if (hasMovements) {
                if (!wasUpdated()) {
                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jq = NewJQlist.get(i);
                        nq = getEQNQ(i + 1);
                        if (!jq.IsHeader) {
                            if (jq.StartID > i && jq.SUpCount > 0) {
                                stopID = i;
                            } else if (jq.StartID == i) {
                                eq = getEQNQ(i - 1);
                                nq = getEQNQ(i + 1);
                                if (eq == null) {
                                    //do nothing
                                    donothing = true;
                                } else if (eq != null && !eq.IsHeader && eq.StartID < i - 1 && nq != null && !nq.IsHeader && nq.StartID == i + 1) {
                                    //do nothing
                                    donothing = true;
                                } else if (nq != null && !nq.IsHeader && nq.StartID < jq.StartID) {
                                    stopID = i;
                                }
                            } else if (jq.StartID < i) {
                                eq = getEQNQ(i - 1);
                                nq = getEQNQ(i + 1);
                                if (eq == null) {
                                    //do nothing
                                    donothing = true;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID == i + 1 && nq.isMoved && nq.StartID > jq.StartID) {
                                    //stopID = i;
                                    boolean hasLowerStartID = false;
                                    for (int x = i + 1; x <= NewJQlist.size() - 1; x++) {
                                        xq = NewJQlist.get(x);
                                        if (xq != null && xq != null && !xq.IsHeader && xq.StartID < jq.StartID) {
                                            hasLowerStartID = true;
                                            break;
                                        }
                                    }
                                    if (hasLowerStartID) {
                                        stopID = i;
                                    }
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID == i + 1 && nq.isMoved) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID > i + 1 && nq.isMoved) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID < i + 1 && !nq.isMoved && nq.StartID < jq.StartID) {
                                    stopID = i;
                                } else if (eq != null && nq != null && !eq.IsHeader && eq.StartID > i - 1 && !nq.IsHeader && nq.StartID < i + 1 && nq.isMoved) {
                                    //do nothing
                                    donothing = true;
                                }
                            }
                        }
                    }

                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jq = NewJQlist.get(i);
                        if (!jq.IsHeader && i <= stopID) {
                            NewJQlist.get(i).setisMoved(true);
                        } else if (!jq.IsHeader && i > stopID) {
                            NewJQlist.get(i).setisMoved(false);
                        }
                    }
                } else {
                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jq = NewJQlist.get(i);
                        nq = getEQNQ(i + 1);
                        if (!jq.IsHeader) {
                            if (jq.StartID > i && jq.SUpCount > 0 && !jq.isUpdated) {
                                if (nq != null && !nq.IsHeader && !nq.isUpdated && !nq.isMoved) {
                                    stopID = i;
                                } else if (nq != null && !nq.IsHeader && !nq.isUpdated && nq.isMoved) {
                                    //do nothing
                                    donothing = true;
                                }
                            } else if (jq.StartID == i && !jq.isUpdated && jq.isMoved) {
                                int c = 2;
                                for (int x = i + 1; x <= NewJQlist.size() - 1; x++) {
                                    xq = NewJQlist.get(x);
                                    if (xq != null && !xq.IsHeader) {
                                        if ((!xq.isUpdated && xq.isMoved && xq.SUpCount > 0) || (xq.isUpdated && xq.isMoved)) {
                                            stopID = x;
                                            break;
                                        } else if (!xq.isUpdated && !xq.isMoved) {
                                            if (jq.StartID > xq.StartID) {
                                                stopID = i;
                                                break;
                                            } else if (i - c > stopID) {
                                                stopID = i - c - 1;
                                                break;
                                            }
                                            /*stopID = x - c;
                                            break;*/
                                        }
                                        c++;
                                        /*if ((!xq.isUpdated && xq.isMoved && xq.SUpCount > 0) || (xq.isUpdated && xq.isMoved)) {
                                            stopID = x;
                                            break;
                                        }*/
                                    }
                                }
                            } else if (jq.StartID == i && jq.isUpdated) {
                                if (nq != null && !nq.IsHeader && nq.isMoved && nq.StartID > jq.StartID) {
                                    stopID = i;
                                } else if (nq != null && !nq.IsHeader && !nq.isMoved && nq.StartID > jq.StartID) {
                                    stopID = i;
                                }
                            } else if (jq.StartID == i && !jq.isUpdated && jq.isMoved) {
                                if (nq != null && !nq.IsHeader && nq.isUpdated && nq.isMoved && nq.StartID > jq.StartID) {
                                    //do nothing
                                    donothing = true;
                                } else if (nq != null && !nq.IsHeader && nq.isMoved && nq.StartID > jq.StartID) {
                                    stopID = i;
                                }
                            } else if (jq.StartID == i && !jq.isUpdated && !jq.isMoved) {
                                //i==5
                                int c = 1;
                                for (int x = i + 1; x <= NewJQlist.size() - 1; x++) {
                                    xq = NewJQlist.get(x);
                                    if (xq != null && !xq.IsHeader && !xq.isUpdated && xq.isMoved && nq != null && !nq.IsHeader && !nq.isMoved) {
                                        //do nothing
                                        break;
                                    } else if (xq != null && !xq.IsHeader && !xq.isUpdated && xq.isMoved) {
                                        if (jq.StartID + c < xq.StartID) {
                                            stopID = x;
                                            break;
                                        }
                                        //c++;
                                    }
                                    c++;
                                }
                            } else if (jq.StartID != i && jq.isUpdated) {
                                if (nq != null && !nq.IsHeader && !nq.isUpdated && !nq.isMoved) {
                                    stopID = i;
                                } else if (nq != null && !nq.IsHeader && nq.isUpdated) {
                                    stopID = i;
                                } else if (nq != null && !nq.IsHeader && !nq.isUpdated && nq.isMoved) {
                                    eq = getEQNQ(i - 1);
                                    if (eq != null && !eq.IsHeader && eq.isUpdated && eq.StartID > jq.StartID) {
                                        stopID = i;
                                    } else if (eq != null && !eq.IsHeader && !eq.isUpdated && eq.StartID > jq.StartID) {
                                        stopID = i;
                                    } else if (eq != null && !eq.IsHeader && eq.isUpdated && eq.StartID < jq.StartID) {
                                        stopID = i;
                                    } else {
                                        //do nothing
                                        donothing = true;
                                    }
                                    /*//do nothing
                                    donothing = true;*/
                                }
                            } else if (jq.StartID != i && !jq.isUpdated) {
                                if (nq != null && !nq.IsHeader && jq.StartID > nq.StartID) {
                                    stopID = i;
                                }
                            }
                        }
                    }

                    for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                        jq = NewJQlist.get(i);
                        if (stopID > 0) {
                            if (!jq.IsHeader && i <= stopID) {
                                NewJQlist.get(i).setisMoved(true);
                            } else if (!jq.IsHeader && i > stopID) {
                                NewJQlist.get(i).setisMoved(false);
                            }
                        }
                    }
                }

                JobQueViewHolder2 vh = null;
                for (int i = 0; i <= NewJQlist.size() - 1; i++) {
                    jq = NewJQlist.get(i);
                    jq.setArrayIdx(i);
                    if (!jq.getIsHeader()) {
                        vh = (JobQueViewHolder2) recyclerView.findViewHolderForAdapterPosition(i);
                        if (vh != null) {
                            if (!wasUpdated()) {
                                vh.cl.setBackgroundResource(
                                        jq.getType().equals("ops") ? R.color.mainorange :
                                                jq.getisMoved() ? R.color.brightgreen
                                                        : R.color.dialog_bg);
                            } else {
                                vh.cl.setBackgroundResource(
                                        jq.getType().equals("ops") ? R.color.mainorange :
                                                jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isMoved ? R.color.brightgreen :
                                                        jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                                R.color.dialog_bg);
                            }


                            boolean eqIsHeader = false;
                            boolean eqIsMoved = false;
                            boolean nqIsHeader = false;
                            boolean nqIsMoved = false;
                            boolean prevQueCannotShiftDown = false;
                            int NewJQlistSize = NewJQlist.size();

                            if (i > 0) {
                                try {
                                    JobQueue prevq = NewJQlist.get(i - 1);
                                    eqIsHeader = prevq.getIsHeader();
                                    eqIsMoved = !eqIsHeader && prevq.getisMoved();
                                    prevQueCannotShiftDown = prevq.getSDownCount() == 2 && prevq.getType().equals("vrp") && !prevq.getIsHeader();
                                    JobQueue nextq = i == NewJQlist.size() - 1 ? null : NewJQlist.get(i + 1);
                                    if (nextq != null) {
                                        nqIsHeader = nextq.getIsHeader();
                                        nqIsMoved = !nqIsHeader && nextq.getisMoved();
                                    }
                                } catch (Exception e) {
                                    String s = e.getMessage().toString();
                                    objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved prevQueCannotShiftDown checks. Exception: " + s, "", "", "", "");
                                }
                            }

                            if (wasUpdated()) {
                                vh.reorderView.setImageResource(
                                        jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                                i == 1 && i == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved && jq.StartID > i ? R.mipmap.ic_action_expand_more :
                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                                i > 1 && i == NewJQlistSize - 1 && eqIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                        i > 1 && i == NewJQlistSize - 1 && !eqIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                                i > 1 && i < NewJQlistSize - 1 && eqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                        i > 1 && i < NewJQlistSize - 1 && eqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && nqIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                        R.mipmap.ic_av_not_interested
                                );
                            } else {
                                vh.reorderView.setImageResource(
                                        jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                                i == 1 && i == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved && jq.StartID > i ? R.mipmap.ic_action_expand_more :
                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                                i > 1 && i == NewJQlistSize - 1 && eqIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                        i > 1 && i == NewJQlistSize - 1 && !eqIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                                i > 1 && i < NewJQlistSize - 1 && eqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                        i > 1 && i < NewJQlistSize - 1 && eqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && nqIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                        R.mipmap.ic_av_not_interested
                                );
                            }

                            vh.lblVrpAddress.setTextColor(Color.BLACK);
                            vh.lblVrpPostal.setTextColor(Color.BLACK);
                            vh.lblpNum_dNum.setTextColor(Color.BLACK);
                            vh.lblCount.setTextColor(Color.BLACK);
                        }
                    }
                }
                hasMovements = false;
            } else {
                JobQueViewHolder2 vh = null;
                if (viewHolderIDX > 0) {
                    List<JobQueue> First_4_JobQueue = null;
                    VrpList_RecycleViewAdapter2 adapter2 = (VrpList_RecycleViewAdapter2) recyclerView.getAdapter();
                    if (adapter2 != null) {
                        First_4_JobQueue = adapter2.getJQlist();
                        if (First_4_JobQueue != null && First_4_JobQueue.size() > 0 && viewHolderIDX <= First_4_JobQueue.size() - 1) {
                            jq = First_4_JobQueue.get(viewHolderIDX);
                            vh = (JobQueViewHolder2) recyclerView.findViewHolderForAdapterPosition(viewHolderIDX);
                            if (vh != null) {
                                if (!wasUpdated()) {
                                    vh.cl.setBackgroundResource(
                                            jq.getType().equals("ops") ? R.color.mainorange :
                                                    jq.getisMoved() ? R.color.brightgreen
                                                            : R.color.dialog_bg);
                                } else {
                                    vh.cl.setBackgroundResource(
                                            jq.getType().equals("ops") ? R.color.mainorange :
                                                    jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isMoved ? R.color.brightgreen :
                                                            jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                                    R.color.dialog_bg);
                                }
                                vh.lblVrpAddress.setTextColor(Color.BLACK);
                                vh.lblVrpPostal.setTextColor(Color.BLACK);
                                vh.lblpNum_dNum.setTextColor(Color.BLACK);
                                vh.lblCount.setTextColor(Color.BLACK);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved2. Exception: " + s, "", "", "", "");
        }
    }

    public void amendJQListAfterMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int finaldragflags) {
        try {
            int viewHolderIDX = viewHolder.getLayoutPosition();
            int NewJQlistSize = NewJQlist.size();
            boolean prevQueCannotShiftDown = false;
            boolean eqIsHeader = false;
            boolean eqIsMoved = false;
            boolean nqIsHeader = false;
            boolean nqIsMoved = false;
            boolean isMoved = false;
            JobQueue cq = null;
            JobQueue eq = null;
            JobQueue nq = null;
            JobQueue rq = null;
            boolean backToSqOne = false;
            boolean backToSqOne2 = false;
            boolean untouch = false;
            int backToSqOne2BreakIDX = -1;
            if (hasMovements) {
                if (viewHolderIDX > 0) {
                    List<JobQueue> First_4_JobQueue = null;
                    VrpList_RecycleViewAdapter2 adapter2 = (VrpList_RecycleViewAdapter2) recyclerView.getAdapter();
                    if (adapter2 != null) {
                        First_4_JobQueue = adapter2.getJQlist();

                        if (First_4_JobQueue != null && First_4_JobQueue.size() > 0 && viewHolderIDX <= First_4_JobQueue.size() - 1) {
                            JobQueue jq = First_4_JobQueue.get(viewHolderIDX);
                            eq = getEQNQ(viewHolderIDX - 1);
                            nq = getEQNQ(viewHolderIDX + 1);
                            eqIsHeader = eq == null ? true : eq.getIsHeader();
                            eqIsMoved = eq == null ? true : eq.getisMoved();
                            nqIsHeader = nq == null ? true : nq.getIsHeader();
                            nqIsMoved = nq == null ? false : nq.getisMoved();

                            if (finaldragflags == ItemTouchHelper.UP) {
                                if (!wasUpdated()) {
                                    if (eqIsHeader && jq.StartID == viewHolderIDX && !jq.isMoved && nqIsMoved) {
                                        jq.setisMoved(true);
                                    } else if (eqIsHeader && jq.StartID == viewHolderIDX && !jq.isMoved) {
                                        jq.setisMoved(false);
                                    } else if (eqIsHeader && jq.StartID != viewHolderIDX) {
                                        jq.setisMoved(true);
                                    } else if (!eqIsHeader && jq.StartID == viewHolderIDX && !jq.isMoved && eqIsMoved) {
                                        jq.setisMoved(true);
                                    } else if (!eqIsHeader && jq.StartID != viewHolderIDX && !jq.isMoved) {
                                        //jq.setisMoved(true);
                                        JobQueue nnq = getEQNQ(viewHolderIDX + 2);
                                        if (!nqIsHeader && jq.StartID > nq.StartID) {
                                            jq.setisMoved(true);
                                        } else if (!nqIsHeader && jq.StartID < nq.StartID) {
                                            if (nnq != null && !nnq.IsHeader && !nnq.isMoved && nq.StartID > nnq.StartID) {
                                                jq.setisMoved(true);
                                                nq.setisMoved(true);
                                            } else if (nnq != null && !nnq.IsHeader && !nnq.isMoved) {
                                                jq.setisMoved(false);
                                                nq.setisMoved(false);
                                            } else {
                                                jq.setisMoved(true);
                                            }
                                        } else {
                                            jq.setisMoved(true);
                                        }
                                    }
                                } else {
                                    if (!jq.IsHeader && !jq.isUpdated && !jq.isMoved) {
                                        jq.setisMoved(true);
                                    }
                                }
                            } else {
                                if (!wasUpdated()) {
                                    if (!eqIsHeader && !eqIsMoved && !nqIsMoved && jq.StartID == viewHolderIDX) {
                                        jq.setisMoved(false);
                                    } else if (!eqIsHeader && eqIsMoved && nqIsMoved && jq.StartID == viewHolderIDX) {
                                        jq.setisMoved(true);
                                    } else if (!eqIsHeader && eqIsMoved && nqIsMoved && jq.StartID != viewHolderIDX) {
                                        jq.setisMoved(true);
                                    } else if (!eqIsHeader && eqIsMoved && !nqIsMoved && jq.StartID != viewHolderIDX) {
                                        if (nq.StartID > jq.StartID) {
                                            jq.setisMoved(false);
                                        } else {
                                            jq.setisMoved(true);
                                        }
                                        //jq.setisMoved(true);
                                    }

                                } else {
                                    jq.setisMoved(!jq.IsHeader && !jq.isUpdated && jq.StartID == viewHolderIDX ? false : true);
                                }
                            }

                            if (finaldragflags == ItemTouchHelper.UP) {
                                if (!wasUpdated()) {
                                    for (int i = 0; i <= First_4_JobQueue.size() - 1; i++) {
                                        JobQueue j = First_4_JobQueue.get(i);
                                        if (!j.IsHeader) {
                                            if (j.isMoved && j.StartID == j.ID) {
                                                backToSqOne = true;
                                            } else if (j.isMoved && j.StartID != j.ID) {
                                                backToSqOne = false;
                                                break;
                                            }
                                        }
                                    }
                                } else {

                                }

                            } else {
                                if (!wasUpdated()) {
                                    for (int i = 0; i <= First_4_JobQueue.size() - 1; i++) {
                                        JobQueue j = First_4_JobQueue.get(i);
                                        if (!j.IsHeader) {
                                            if (j.isMoved && j.StartID == j.ID) {
                                                backToSqOne = true;
                                            } else if (j.isMoved && j.StartID != j.ID) {
                                                backToSqOne = false;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    for (int i = 0; i <= First_4_JobQueue.size() - 1; i++) {
                                        JobQueue j = First_4_JobQueue.get(i);
                                        if (!j.IsHeader && !j.isUpdated && j.isMoved && j.StartID == i) {
                                            backToSqOne = true;
                                        }
                                    }
                                }
                            }

                            int z = 0;
                            if (!backToSqOne) {
                                if (finaldragflags == ItemTouchHelper.UP) {
                                    for (int i = viewHolderIDX; i <= First_4_JobQueue.size() - 1; i++) {
                                        JobQueue jqq = First_4_JobQueue.get(i);
                                        if (!jqq.getIsHeader()) {
                                            z = i - 1;
                                            if (i - z == jqq.StartID && jqq.StartID != i) {
                                                backToSqOne2 = false;
                                            } else if (i - z == jqq.StartID) {
                                                backToSqOne2 = true;
                                            } else if (i == jqq.StartID) {
                                                backToSqOne2 = true;
                                            } else {
                                                if (i - 1 > 0) {
                                                    if (i - 1 != viewHolderIDX) {
                                                        JobQueue j = First_4_JobQueue.get(i - 1);
                                                        z = i - 1 - 1;
                                                        if (i - 1 - z == j.StartID) {
                                                            backToSqOne2 = true;
                                                            backToSqOne2BreakIDX = i - 1;
                                                            break;
                                                        } else if (i - 1 - z == j.ID) { //testing
                                                            backToSqOne2 = true;
                                                            backToSqOne2BreakIDX = i + 1;
                                                            break;
                                                        } else {

                                                            backToSqOne2 = false;
                                                            break;
                                                        }
                                                    } else {
                                                        JobQueue j = First_4_JobQueue.get(i - 1);
                                                        if (j.StartID > jqq.StartID) {
                                                            backToSqOne2 = false;
                                                            break;
                                                        } else {
                                                            backToSqOne2BreakIDX = i - 1; //newly added
                                                            backToSqOne2 = true;
                                                        }
                                                    }
                                                } else {
                                                    if (i + 1 <= First_4_JobQueue.size() - 1) {
                                                        JobQueue j = First_4_JobQueue.get(i + 1);
                                                        int x = 1;
                                                        if (jqq.ID + 1 == j.ID) {
                                                            /*JobQueue xx = null;
                                                            do {
                                                                x++;
                                                                if (i + x <= First_4_JobQueue.size() -1) {
                                                                    xx = First_4_JobQueue.get(i+x);
                                                                    if (!xx.IsHeader && !xx.isMoved) {
                                                                        backToSqOne2BreakIDX = i + 1;
                                                                        backToSqOne2 = true;
                                                                        break;
                                                                    } else if (!xx.IsHeader && xx.isMoved) {
                                                                        backToSqOne2 = false;
                                                                        break;
                                                                    }
                                                                } else {
                                                                    backToSqOne2 = false;
                                                                    break;
                                                                }
                                                            } while (xx != null && xx.ID == jqq.ID + x);*/

                                                            backToSqOne2BreakIDX = i + 1;
                                                            backToSqOne2 = true;
                                                            break;
                                                        } else {
                                                            backToSqOne2 = false;
                                                            break;
                                                        }
                                                    } else {
                                                        backToSqOne2 = false;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    for (int i = viewHolderIDX; i >= 0; i--) {
                                        JobQueue j = First_4_JobQueue.get(i);
                                        if (!j.getIsHeader()) {
                                            if (i == j.StartID) {
                                                backToSqOne2 = true;
                                            } else if (i - 1 == j.StartID) {
                                                backToSqOne2 = true;
                                            } else if (i - 1 == j.OldID) { //testing
                                                backToSqOne2 = true;
                                            } else {
                                                if (i == 1) {
                                                    backToSqOne2BreakIDX = i + 1;
                                                } else {
                                                    //backToSqOne2BreakIDX = i;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (!wasUpdated()) {
                                if (backToSqOne) {
                                    for (int i = 0; i <= First_4_JobQueue.size() - 1; i++) {
                                        if (!First_4_JobQueue.get(i).getIsHeader() && First_4_JobQueue.get(i).getisMoved()) {
                                            First_4_JobQueue.get(i).setisMoved(false);
                                            First_4_JobQueue.get(i).setID(i);
                                        }
                                    }
                                } else if (backToSqOne2) {
                                    if (finaldragflags == ItemTouchHelper.UP) {
                                        if (backToSqOne2BreakIDX > -1) {
                                            for (int i = backToSqOne2BreakIDX; i <= First_4_JobQueue.size() - 1; i++) {
                                                cq = First_4_JobQueue.get(i);
                                                if (!cq.getIsHeader()) {
                                                    eq = i - 1 <= 0 ? null : First_4_JobQueue.get(i - 1);
                                                    nq = i + 1 > First_4_JobQueue.size() - 1 ? null : First_4_JobQueue.get(i + 1);
                                                    JobQueue nnq = i + 2 > First_4_JobQueue.size() - 1 ? null : First_4_JobQueue.get(i + 2);
                                                    if (i == backToSqOne2BreakIDX) {
                                                        if (eq != null) {
                                                            if (eq.StartID > cq.StartID) {
                                                                if (nq != null && nq.StartID < cq.StartID) {
                                                                    First_4_JobQueue.get(i).setisMoved(true);
                                                                } else if (nq != null && nq.StartID > cq.StartID && nq.isMoved) { //testing
                                                                    First_4_JobQueue.get(i).setisMoved(true);
                                                                } else {
                                                                    First_4_JobQueue.get(i).setisMoved(false);
                                                                }
                                                            }
                                                        } else {
                                                            First_4_JobQueue.get(i).setisMoved(false);
                                                        }
                                                    } else {
                                                        if (nnq != null && !nnq.IsHeader) {
                                                            if (nq != null && !nq.IsHeader && nq.isMoved && nnq.StartID < nq.StartID) {
                                                                First_4_JobQueue.get(i).setisMoved(true);
                                                            } else if (nq != null && !nq.IsHeader && nq.StartID < cq.StartID) {
                                                                First_4_JobQueue.get(i).setisMoved(true);
                                                            } else if (eq != null && !eq.IsHeader && nq != null && !nq.IsHeader && nq.StartID > cq.StartID && eq.isMoved && nq.isMoved) {
                                                                First_4_JobQueue.get(i).setisMoved(true);
                                                            } else {
                                                                First_4_JobQueue.get(i).setisMoved(false);
                                                            }
                                                        } else if (nq != null && !nq.IsHeader && nq.isMoved) {
                                                            First_4_JobQueue.get(i).setisMoved(false);
                                                        } else {

                                                        }
                                                        //First_4_JobQueue.get(i).setisMoved(false);
                                                    }
                                                }
                                            }
                                        } else {
                                            for (int i = viewHolderIDX; i <= First_4_JobQueue.size() - 1; i++) {
                                                if (i == viewHolderIDX && i > 1) {
                                                    if (i - 1 > 0 && i - 1 <= First_4_JobQueue.size() - 1) { //testing first
                                                        eq = i - 1 <= 0 ? null : First_4_JobQueue.get(i - 1);
                                                        nq = i + 1 > First_4_JobQueue.size() - 1 ? null : First_4_JobQueue.get(i + 1);
                                                        cq = First_4_JobQueue.get(i);
                                                        if (eq != null && !eq.IsHeader && eq.ID + 1 == cq.ID && eq.StartID < cq.StartID) {
                                                            eq.setisMoved(false);
                                                            cq.setisMoved(false);
                                                        } else if (nq != null && !nq.IsHeader && !nq.isMoved && nq.StartID > cq.StartID) {
                                                            cq.setisMoved(false);
                                                        } else if (eq != null && !eq.IsHeader && eq.isMoved && eq.ID + 1 == cq.ID && eq.StartID > cq.StartID) {
                                                            eq.setisMoved(true);
                                                            if (nq != null && !nq.IsHeader && !nq.isMoved && nq.StartID > cq.StartID) {
                                                                cq.setisMoved(false);
                                                            } else if (nq != null && !nq.IsHeader && nq.isMoved && nq.StartID > cq.StartID && eq.StartID > nq.StartID) {
                                                                cq.setisMoved(true);
                                                            } else if (nq != null && !nq.IsHeader && nq.isMoved && nq.StartID > cq.StartID) {
                                                                cq.setisMoved(false);
                                                            } else if (nq != null && !nq.IsHeader && nq.StartID < cq.StartID) {
                                                                cq.setisMoved(true);
                                                            }
                                                            //cq.setisMoved(true);
                                                        } else {
                                                            cq.setisMoved(false);
                                                        }
                                                    /*if (First_4_JobQueue.get(i - 1).ID + 1 == First_4_JobQueue.get(i).ID && First_4_JobQueue.get(i - 1).StartID < First_4_JobQueue.get(i).StartID) {
                                                        First_4_JobQueue.get(i - 1).setisMoved(false);
                                                        First_4_JobQueue.get(i).setisMoved(false);
                                                    }*/
                                                        //First_4_JobQueue.get(i).setisMoved(false); //additional here
                                                    } else if (i + 1 <= First_4_JobQueue.size() - 1) {
                                                        if (First_4_JobQueue.get(i + 1).StartID > First_4_JobQueue.get(i).StartID) {
                                                            First_4_JobQueue.get(i).setisMoved(false);
                                                        }
                                                    } else {
                                                        First_4_JobQueue.get(i).setisMoved(false);
                                                    }
                                                } else {
                                                    cq = First_4_JobQueue.get(i);
                                                    if (!First_4_JobQueue.get(i).getIsHeader()) {
                                                        eq = i - 1 <= 0 ? null : First_4_JobQueue.get(i - 1);
                                                        if (eq != null) {
                                                            if (eq.StartID < cq.StartID && !eq.isMoved) {
                                                                First_4_JobQueue.get(i).setisMoved(false);
                                                            }
                                                            /*if (eq.StartID < cq.StartID) {
                                                                First_4_JobQueue.get(i).setisMoved(false);
                                                            }*/
                                                        } else {
                                                            First_4_JobQueue.get(i).setisMoved(false);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (backToSqOne2BreakIDX > -1) { //monitor here
                                            if (backToSqOne2BreakIDX + 1 >= viewHolderIDX) {
                                                for (int i = backToSqOne2BreakIDX; i <= viewHolderIDX; i++) {
                                                    cq = First_4_JobQueue.get(i);
                                                    if (!cq.getIsHeader()) {
                                                        eq = getEQNQ(i - 1);
                                                        nq = getEQNQ(i + 1);
                                                        if (eq != null && eq.StartID > cq.StartID && eq.isMoved) {
                                                            if (nq != null && nq.isMoved && nq.StartID < cq.StartID) {//monitor here
                                                                cq.setisMoved(true);
                                                            } else if (nq != null && nq.isMoved && nq.StartID > cq.StartID) {//monitor here
                                                                cq.setisMoved(true);
                                                            } else if (nq != null && nq.StartID < cq.StartID) {
                                                                cq.setisMoved(true);
                                                            } else {
                                                                cq.setisMoved(false);
                                                            }
                                                        } else if (eq != null && eq.StartID < cq.StartID && eq.isMoved) {
                                                            if (nq != null && nq.StartID < cq.StartID) {
                                                                cq.setisMoved(true);
                                                            } else {
                                                                if (nq != null && nq.StartID > cq.StartID && nq.isMoved) {
                                                                    cq.setisMoved(true);
                                                                } else {
                                                                    cq.setisMoved(false);
                                                                }
                                                                //cq.setisMoved(false);
                                                            }
                                                        } else {
                                                            cq.setisMoved(false);
                                                        }
                                                        //First_4_JobQueue.get(i).setisMoved(false);
                                                    }
                                                }
                                            } else {
                                                for (int i = backToSqOne2BreakIDX + 1; i <= viewHolderIDX; i++) {
                                                    cq = First_4_JobQueue.get(i);
                                                    if (!cq.getIsHeader()) {
                                                        eq = getEQNQ(i - 1);
                                                        nq = getEQNQ(i + 1);
                                                        if (eq != null && eq.StartID > cq.StartID && eq.isMoved) {
                                                            if (nq != null && nq.isMoved && nq.StartID < cq.StartID) {
                                                                cq.setisMoved(true);
                                                            } else if (nq != null && nq.StartID < cq.StartID) {
                                                                cq.setisMoved(true);
                                                            } else {
                                                                cq.setisMoved(false);
                                                            }
                                                        } else {
                                                            if (eq.ID == backToSqOne2BreakIDX) {
                                                                JobQueue eqq = getEQNQ(i - 2);
                                                                if (eqq != null && !eqq.IsHeader && eqq.isMoved && eq.isMoved) {
                                                                    if (eq.StartID < cq.StartID && nq.StartID > cq.StartID) {
                                                                        eq.setisMoved(false);
                                                                    } else if (eq.StartID < cq.StartID && nq.StartID < cq.StartID) {
                                                                        eq.setisMoved(true);
                                                                    } else {
                                                                        eq.setisMoved(true);
                                                                    }
                                                                /*if (eq.StartID < cq.StartID) {
                                                                    eq.setisMoved(false);
                                                                } else {
                                                                    eq.setisMoved(true);
                                                                }*/
                                                                } else {
                                                                    eq.setisMoved(false);
                                                                }
                                                                //eq.setisMoved(false);
                                                            }
                                                            if (cq.StartID < nq.StartID) {
                                                                cq.setisMoved(false);
                                                            }
                                                            //cq.setisMoved(false);
                                                            if (nq.ID == backToSqOne2BreakIDX) {
                                                                nq.setisMoved(false);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    for (int i = 0; i <= First_4_JobQueue.size() - 1; i++) {
                                        jq = First_4_JobQueue.get(i);
                                        if (!jq.getIsHeader()) {
                                            eq = getEQNQ(i - 1);
                                            nq = getEQNQ(i + 1);
                                            eqIsHeader = eq == null ? true : eq.getIsHeader();
                                            eqIsMoved = eq == null ? true : eq.getisMoved();
                                            nqIsHeader = nq == null ? true : nq.getIsHeader();
                                            nqIsMoved = nq == null ? false : nq.getisMoved();
                                            if (!eqIsHeader && !eqIsMoved && !nqIsHeader && !nqIsMoved && jq.getStartID() == i) {
                                                jq.setisMoved(false);
                                            } else if (!eqIsHeader && !eqIsMoved && !nqIsHeader && jq.getStartID() == i) {
                                                jq.setisMoved(false);
                                            }
                                        }
                                    }
                                }
                            } else {
                                //if (backToSqOne) {
                                for (int i = 0; i <= First_4_JobQueue.size() - 1; i++) {
                                    jq = First_4_JobQueue.get(i);
                                    eq = getEQNQ(i - 1);
                                    nq = getEQNQ(i + 1);
                                    if (!jq.IsHeader && !jq.isUpdated && jq.isMoved && jq.StartID == i) {
                                        if (eq != null && nq != null && !eq.IsHeader && !nq.IsHeader && eq.StartID > jq.StartID) {
                                            if (nq.StartID < jq.StartID && nq.isUpdated) {
                                                First_4_JobQueue.get(i).setisMoved(true);
                                                First_4_JobQueue.get(i).setID(i);
                                            } else if (nq.StartID > jq.StartID) {
                                                First_4_JobQueue.get(i).setisMoved(true);
                                                First_4_JobQueue.get(i).setID(i);
                                            } else {
                                                if (wasUpdated() && jq.isMoved) {
                                                    First_4_JobQueue.get(i).setisMoved(true);
                                                    First_4_JobQueue.get(i).setID(i);
                                                } else {
                                                    First_4_JobQueue.get(i).setisMoved(false);
                                                    First_4_JobQueue.get(i).setID(i);
                                                }
                                                    /*First_4_JobQueue.get(i).setisMoved(false);
                                                    First_4_JobQueue.get(i).setID(i);*/
                                            }
                                        } else if (eq != null && nq != null && !eq.IsHeader && !nq.IsHeader && eq.StartID < jq.StartID && eq.isUpdated) {
                                            if (nq.StartID < jq.StartID && nq.isUpdated) {
                                                First_4_JobQueue.get(i).setisMoved(true);
                                                First_4_JobQueue.get(i).setID(i);
                                            } else if (nq.StartID > jq.StartID && !nq.isMoved) {
                                                First_4_JobQueue.get(i).setisMoved(false);
                                                First_4_JobQueue.get(i).setID(i);
                                            } else if (nq.StartID > jq.StartID && !nq.isMoved && !jq.isUpdated) {
                                                First_4_JobQueue.get(i).setisMoved(false);
                                                First_4_JobQueue.get(i).setID(i);
                                            } else if (nq.StartID > jq.StartID) {
                                                if (wasUpdated() && jq.isMoved && eq.isMoved) {
                                                    First_4_JobQueue.get(i).setisMoved(true); // check
                                                    First_4_JobQueue.get(i).setID(i);
                                                } else {
                                                    First_4_JobQueue.get(i).setisMoved(false); // check
                                                    First_4_JobQueue.get(i).setID(i);
                                                }
                                                    /*First_4_JobQueue.get(i).setisMoved(false); // check
                                                    First_4_JobQueue.get(i).setID(i);*/
                                            } else {
                                                First_4_JobQueue.get(i).setisMoved(false);
                                                First_4_JobQueue.get(i).setID(i);
                                            }
                                        } else {
                                            if (wasUpdated()) {
                                                First_4_JobQueue.get(i).setisMoved(true);
                                                First_4_JobQueue.get(i).setID(i);
                                            } else {
                                                First_4_JobQueue.get(i).setisMoved(false);
                                                First_4_JobQueue.get(i).setID(i);
                                            }
                                                /*First_4_JobQueue.get(i).setisMoved(false);
                                                First_4_JobQueue.get(i).setID(i);*/
                                        }
                                            /*First_4_JobQueue.get(i).setisMoved(false);
                                            First_4_JobQueue.get(i).setID(i);*/
                                    } else if (!jq.IsHeader && !jq.isUpdated && jq.isMoved && jq.StartID != i) {
                                        if (eq != null && nq != null && !eq.IsHeader && !nq.IsHeader && eq.StartID < jq.StartID && eq.isUpdated && jq.OldID == i) {
                                            if (nq.StartID > jq.StartID && !nq.isMoved) {
                                                First_4_JobQueue.get(i).setisMoved(false);
                                                First_4_JobQueue.get(i).setID(i);
                                            }
                                        }
                                    }
                                }
                                //}
                            }

                            if (finaloldPosition != null) {
                                if (!wasUpdated()) {
                                    ((JobQueViewHolder2) viewHolder).cl.setBackgroundResource(
                                            jq.getisMoved() && viewHolderIDX != jq.getStartID() ? R.color.brightgreen
                                                    : R.color.dialog_bg);
                                } else {
                                    ((JobQueViewHolder2) viewHolder).cl.setBackgroundResource(
                                            jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isUpdated ? R.color.brightgreen :
                                                    jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                            R.color.dialog_bg);
                                }
                            } else {
                                if (!wasUpdated()) {
                                    ((JobQueViewHolder2) viewHolder).cl.setBackgroundResource(
                                            jq.getisMoved() && viewHolderIDX != jq.getStartID() ? R.color.brightgreen
                                                    : R.color.dialog_bg);
                                } else {
                                    ((JobQueViewHolder2) viewHolder).cl.setBackgroundResource(
                                            jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isUpdated ? R.color.brightgreen :
                                                    jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                            R.color.dialog_bg);
                                }
                            }
                        }

                        JobQueViewHolder2 vh = null;
                        for (int i = 0; i <= First_4_JobQueue.size() - 1; i++) {
                            JobQueue jq = First_4_JobQueue.get(i);
                            jq.setArrayIdx(i);
                            if (!jq.getIsHeader()) {
                                vh = (JobQueViewHolder2) recyclerView.findViewHolderForAdapterPosition(i);
                                if (vh != null) {
                                    if (!wasUpdated()) {
                                        vh.cl.setBackgroundResource(
                                                jq.getType().equals("ops") ? R.color.mainorange :
                                                        jq.getisMoved() ? R.color.brightgreen
                                                                : R.color.dialog_bg);
                                    } else {
                                        vh.cl.setBackgroundResource(
                                                jq.getType().equals("ops") ? R.color.mainorange :
                                                        jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isMoved ? R.color.brightgreen :
                                                                jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                                        R.color.dialog_bg);
                                    }

                                    if (i > 0) {
                                        try {
                                            JobQueue prevq = First_4_JobQueue.get(i - 1);
                                            eqIsHeader = prevq.getIsHeader();
                                            eqIsMoved = !eqIsHeader && prevq.getisMoved();
                                            prevQueCannotShiftDown = prevq.getSDownCount() == 2 && prevq.getType().equals("vrp") && !prevq.getIsHeader();
                                            JobQueue nextq = i == NewJQlistSize - 1 ? null : First_4_JobQueue.get(i + 1);
                                            if (nextq != null) {
                                                nqIsHeader = nextq.getIsHeader();
                                                nqIsMoved = !nqIsHeader && nextq.getisMoved();
                                            }
                                        } catch (Exception e) {
                                            String s = e.getMessage().toString();
                                            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved prevQueCannotShiftDown checks. Exception: " + s, "", "", "", "");
                                        }
                                    }

                                    if (wasUpdated()) {
                                        vh.reorderView.setImageResource(
                                                jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                                        i == 1 && i == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved && jq.StartID > i ? R.mipmap.ic_action_expand_more :
                                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                                        i > 1 && i == NewJQlistSize - 1 && eqIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                                i > 1 && i == NewJQlistSize - 1 && !eqIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                                        i > 1 && i < NewJQlistSize - 1 && eqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                                i > 1 && i < NewJQlistSize - 1 && eqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && nqIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                                R.mipmap.ic_av_not_interested
                                        );
                                    } else {
                                        vh.reorderView.setImageResource(
                                                jq.getType().equals("ops") ? R.mipmap.ic_blank :
                                                        i == 1 && i == NewJQlistSize - 1 ? R.mipmap.ic_av_not_interested ://jq is 1st item n is last item
                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved && jq.StartID > i ? R.mipmap.ic_action_expand_more :
                                                                        i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested : //jq is 1st item n is not last item n nxt item is not header n next item is not moved
                                                                                i == 1 && i < NewJQlistSize - 1 && !nqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more : //jq is 1st item n is not last item n nxt item is not header n next item is moved
                                                                                        i > 1 && i == NewJQlistSize - 1 && eqIsHeader ? R.mipmap.ic_av_not_interested :                     //jq is not 1st item n is last item n prev item is header
                                                                                                i > 1 && i == NewJQlistSize - 1 && !eqIsHeader ? R.mipmap.ic_action_expand_less :                   //jq is not 1st item n is last item n prev item is not header && prev item is not moved
                                                                                                        i > 1 && i < NewJQlistSize - 1 && eqIsHeader && !nqIsMoved ? R.mipmap.ic_av_not_interested :   //jq is not 1st item n not last item n prev item is header && next item is not moved
                                                                                                                i > 1 && i < NewJQlistSize - 1 && eqIsHeader && nqIsMoved ? R.mipmap.ic_action_expand_more :   //jq is not 1st item n not last item n prev item is header && next item is moved
                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less : //jq is not 1st item n not last item n prev item is not header && prev item is not moved && next item is not moved
                                                                                                                                i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && nqIsMoved ? R.mipmap.ic_action_unfold_more :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is moved
                                                                                                                                        i > 1 && i < NewJQlistSize - 1 && !eqIsHeader && !nqIsMoved ? R.mipmap.ic_action_expand_less :  //jq is not 1st item n not last item n prev item is not header && prev item is moved && next item is not moved
                                                                                                                                                R.mipmap.ic_av_not_interested
                                        );
                                    }

                                    vh.lblVrpAddress.setTextColor(Color.BLACK);
                                    vh.lblVrpPostal.setTextColor(Color.BLACK);
                                    vh.lblpNum_dNum.setTextColor(Color.BLACK);
                                    vh.lblCount.setTextColor(Color.BLACK);
                                }
                            }
                        }
                    }
                }

                hasMovements = false;
            } else {
                JobQueViewHolder2 vh = null;
                if (viewHolderIDX > 0) {
                    List<JobQueue> First_4_JobQueue = null;
                    VrpList_RecycleViewAdapter2 adapter2 = (VrpList_RecycleViewAdapter2) recyclerView.getAdapter();
                    if (adapter2 != null) {
                        First_4_JobQueue = adapter2.getJQlist();
                        if (First_4_JobQueue != null && First_4_JobQueue.size() > 0 && viewHolderIDX <= First_4_JobQueue.size() - 1) {
                            JobQueue jq = First_4_JobQueue.get(viewHolderIDX);
                            vh = (JobQueViewHolder2) recyclerView.findViewHolderForAdapterPosition(viewHolderIDX);
                            if (vh != null) {
                                if (!wasUpdated()) {
                                    vh.cl.setBackgroundResource(
                                            jq.getType().equals("ops") ? R.color.mainorange :
                                                    jq.getisMoved() ? R.color.brightgreen
                                                            : R.color.dialog_bg);
                                } else {
                                    vh.cl.setBackgroundResource(
                                            jq.getType().equals("ops") ? R.color.mainorange :
                                                    jq.getisMoved() && viewHolderIDX == jq.getID() && jq.isMoved ? R.color.brightgreen :
                                                            jq.getisMoved() && viewHolderIDX != jq.getArrayIdx() ? R.color.brightgreen :
                                                                    R.color.dialog_bg);
                                }
                                vh.lblVrpAddress.setTextColor(Color.BLACK);
                                vh.lblVrpPostal.setTextColor(Color.BLACK);
                                vh.lblpNum_dNum.setTextColor(Color.BLACK);
                                vh.lblCount.setTextColor(Color.BLACK);
                            }
                        }
                    }
                }
            }

            NewJQlistSize = NewJQlist.size();
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 amendJQListAfterMoved. Exception: " + s, "", "", "", "");
        } finally {
            finaloldPosition = null;
            finalnewPosition = null;
            //notifyDataSetChanged();
        }
    }

    public JobQueue getEQNQ(int index) {
        JobQueue jq = null;
        try {
            if (NewJQlist != null && NewJQlist.size() > 0) {
                jq = NewJQlist.get(index);
                if (jq.getIsHeader()) {
                    jq = null;
                }
            }
        } catch (Exception e) {
            jq = null;
        } finally {
            return jq;
        }
    }

    public Boolean wasUpdated() {
        Boolean asd = false;
        try {
            asd = saveAmended4QuesJsonStr != null && !saveAmended4QuesJsonStr.isEmpty() && !saveAmended4QuesJsonStr.equals("");
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 wasUpdated. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public boolean getisAllWhite() {
        return isAllWhite;
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
