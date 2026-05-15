package com.xdel.luke.PieceByPiecePU;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.JobListGroupSortedSQLite_PostData;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objListItemPosChecked;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StartQueryAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private Context _context;
    public List<String> _listDataHeader; // header titles
    public List<SQHeaderJobsPcsCount> _listDataHeaderJcPc; //jobs pieces count
    public HashMap<String, List<SQItem>> _listDataChild;

    public HashMap<Integer, boolean[]> mChildCheckStates;
    public HashMap<Integer, boolean[]> mChildColorStates;
    public HashMap<Integer, objListItemPosChecked[]> mChildCheckStates2;
    public static ProgressDialog progressDoalog;


    private static Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDoalog != null) {
                progressDoalog.incrementProgressBy(1);
            }
        }
    };

    public StartQueryAdapter(Context context, List<String> listDataHeader, List<SQHeaderJobsPcsCount> listDataHeaderJcPc, HashMap<String, List<SQItem>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataHeaderJcPc = listDataHeaderJcPc;
        this._listDataChild = listChildData;

        mChildCheckStates = new HashMap<Integer, boolean[]>();
        mChildColorStates = new HashMap<Integer, boolean[]>();
        mChildCheckStates2 = new HashMap<Integer, objListItemPosChecked[]>();
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public HashMap<Integer, objListItemPosChecked[]> getmChildCheckStates2(int mGroupPosition) {
        try {
            if (mChildCheckStates2.containsKey(mGroupPosition)) {
                //objListItemPosChecked getChecked2[] = mChildCheckStates2.get(mGroupPosition);
            } else {
                int ItemsCount = getChildrenCount(mGroupPosition);
                objListItemPosChecked getChecked2[] = new objListItemPosChecked[ItemsCount];
                for (int v = 0; v <= getChecked2.length - 1; v++) {
                    getChecked2[v] = new objListItemPosChecked(v, false);
                }
                mChildCheckStates2.put(mGroupPosition, getChecked2);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLite_PostItemAdapter getmChildCheckStates2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return mChildCheckStates2;
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    private Object getGroupJCPC(int groupPosition) {
        return this._listDataHeaderJcPc.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        try {
            GroupViewHolder gvh;
            String headerTitle = (String) getGroup(groupPosition);
            SQHeaderJobsPcsCount jcpc = (SQHeaderJobsPcsCount) getGroupJCPC(groupPosition);
            final int grpPos = groupPosition;

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.meds_col_grp, null);
                gvh = new GroupViewHolder();
                gvh.lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
                gvh.lblListHeaderCount = (TextView) convertView.findViewById(R.id.lblListHeaderCount);
                gvh.lblJobsCount = (TextView) convertView.findViewById(R.id.lblJobsCount);
                gvh.lblPcsCount = (TextView) convertView.findViewById(R.id.lblPcsCount);
                gvh.lblPUCount = convertView.findViewById(R.id.lblPUCount);
                gvh.LLjobs_list_group_ihavearrive = (LinearLayout) convertView.findViewById(R.id.LLjobs_list_group_ihavearrive);
                gvh.LLarrival = (LinearLayout) convertView.findViewById(R.id.LLarrival);
                gvh.img_i_have_arrive = (ImageView) convertView.findViewById(R.id.img_i_have_arrive);
                gvh.lblarrival = (TextView) convertView.findViewById(R.id.lblarrival);
                convertView.setTag(gvh);
            } else {
                gvh = (GroupViewHolder) convertView.getTag();
            }

            gvh.lblListHeader.setText(headerTitle);

            if (jcpc != null) {
                gvh.lblJobsCount.setText(String.valueOf(jcpc.JobsCount));
                gvh.lblPcsCount.setText(String.valueOf(jcpc.PcsCount));
                gvh.lblPUCount.setText(String.valueOf(jcpc.PUCount));
            }

            Boolean hasIarrive = false;
            long ms = 0;
            for (int i = 0; i <= _listDataChild.get(_listDataHeader.get(grpPos)).size() - 1; i++) {
                if (_listDataChild.get(_listDataHeader.get(grpPos)).get(i).ji.i_have_arrived_timestamp > 0) {
                    hasIarrive = true;
                    ms = _listDataChild.get(_listDataHeader.get(grpPos)).get(i).ji.i_have_arrived_timestamp;
                    break;
                }
            }

            if (hasIarrive && ms > 0) {
                gvh.LLarrival.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
                final String arrivaltime = sdf.format(new Date(ms));
                gvh.lblarrival.setText(myApp.getAppContext().getString(R.string.buttontext_arrivaltime) + " " + arrivaltime);
            } else {
                gvh.LLarrival.setVisibility(View.GONE);
                gvh.lblarrival.setText("");
            }

            final GroupViewHolder gvhh = gvh;
            gvh.LLjobs_list_group_ihavearrive.setOnClickListener(null);
            gvh.LLjobs_list_group_ihavearrive.setOnClickListener(rtnIhaveArriveClickListenerMass(gvhh, grpPos, _listDataHeader, _listDataChild));

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at StartQueryAdapter getGroupView. Exception: "
                    + e.getMessage().toString(), "", "", "", "");
        } finally {
            return convertView;
        }
    }

    private View.OnClickListener rtnIhaveArriveClickListenerMass(final GroupViewHolder gvh, final int grpPos, final List<String> _listDataHeader,
                                                                 final HashMap<String, List<SQItem>> _listDataChild) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final int count = _listDataChild.get(_listDataHeader.get(grpPos)).size();
                    final Context ctx = myApp.getAppContext();

                    if (count > 0) {
                        //Boolean SettingsDistVioEnabled = !objGlobalMethods.getSettingsDistVioEnabled().equals("") ? true : false;
                        final boolean SettingsDistVioEnabled = true;
                        final boolean PromptDistanceViolationUponUpdate =
                                _listDataChild.get(_listDataHeader.get(grpPos)).get(0).ji != null ?
                                        JobsHandler.PromptDistanceViolationUponUpdate(_listDataChild.get(_listDataHeader.get(grpPos)).get(0).ji, 0, 1) : false;

                        AlertDialog.Builder builder = new AlertDialog.Builder(myApp.currentActivity);
                        builder.setCancelable(false);
                        builder.setMessage((SettingsDistVioEnabled && PromptDistanceViolationUponUpdate) ? ctx.getString(R.string.alert_outofrangecfm2) :
                                ctx.getString(R.string.alert_cfmupdatestatusARR));


                        builder.setPositiveButton(ctx.getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", update job status
                                SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                                Date dt = new Date();
                                final Long l = dt.getTime();
                                final String timestampstr = "/Date(" + l + ")/";
                                final android.location.Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                                final int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);
                                final String regid = objGlobalMethods.getRegistrationId(ctx, GcmPreferences);
                                long AStatusCodeIDX = 368;

                                final long finalAStatusCodeIDX = AStatusCodeIDX;
                                progressDoalog = new ProgressDialog(myApp.currentActivity);
                                progressDoalog.setMessage(ctx.getString(R.string.pd_waitprocessingupdates));
                                progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                progressDoalog.setMax(count);
                                progressDoalog.setCanceledOnTouchOutside(false);
                                progressDoalog.show();

                                final Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        int c = 0;
                                        String JobsIDXstr;
                                        long JobsIDX;
                                        List<Long> JobsIDXList = new ArrayList<>();
                                        Long[] JobsIDXsArr = null;
                                        SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                                        Gson gson = new Gson();
                                        try {
                                            SharedPreferences mcsp = objGlobalMethods.getMedsColSP();
                                            objCheckedItems[] objCheckedItemsAL = null;
                                            String objCheckedItemsJsonStr = objGlobalMethods.getMedsColPrefs(mcsp, objGlobalVariables.checkedItemsName);
                                            if (!objCheckedItemsJsonStr.equals("")) {
                                                objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                                            }
                                            String jobsidxs = "";
                                            for (int i = 0; i <= _listDataChild.get(_listDataHeader.get(grpPos)).size() - 1; i++) {
                                                View cv = getChildView(grpPos, i, false, null, null);
                                                ViewHolder cvh = (ViewHolder) cv.getTag();
                                                Thread.sleep(200);
                                                handle.sendMessage(handle.obtainMessage());

                                                JobsIDXstr = _listDataChild.get(_listDataHeader.get(grpPos)).get(i).JobsIDX == null ? "" : _listDataChild.get(_listDataHeader.get(grpPos)).get(i).JobsIDX;
                                                JobsIDX = Long.parseLong(JobsIDXstr == "" ? "0" : JobsIDXstr);
                                                if (JobsIDX > 0) {
                                                    if (!JobsIDXList.contains(JobsIDX))
                                                        JobsIDXList.add(JobsIDX);

                                                    _listDataChild.get(_listDataHeader.get(grpPos)).get(i).ji.setI_have_arrived_timestamp(l);
                                                    String hfJobInfoJsonStr = gson.toJson(_listDataChild.get(_listDataHeader.get(grpPos)).get(i).ji);
                                                    hfJobInfoJsonStr = hfJobInfoJsonStr == "[]" || hfJobInfoJsonStr == "" ? "" : hfJobInfoJsonStr;
                                                    _listDataChild.get(_listDataHeader.get(grpPos)).get(i).setJobInfoJsonStr(hfJobInfoJsonStr);
                                                    cvh.lblhfJobsInfoJsonStr.setText(hfJobInfoJsonStr);
                                                    DAO_Jobs.updateJobInfoPrefStr(JobsIDX, hfJobInfoJsonStr);

                                                    c++;

                                                    if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                                                        for (int v = objCheckedItemsAL.length - 1; v >= 0; v--) {
                                                            if (objCheckedItemsAL[v].jobinfo.JobsIDX == JobsIDX) {
                                                                objCheckedItemsAL[v].jobinfo.setI_have_arrived_timestamp(l);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }


                                            if (JobsIDXList != null && JobsIDXList.size() > 0) {
                                                jobsidxs = Arrays.toString(JobsIDXList.toArray());
                                                objGlobalMethods.writeToFile("Mass update Med job status.", "JobsIDX: " +
                                                        jobsidxs, "Submitted StatusCodeIDX: " + String.valueOf(finalAStatusCodeIDX), "", "");

                                                JobsIDXsArr = JobsIDXList.toArray(new Long[JobsIDXList.size()]);
                                                JobsHandler.addUpdateJobInfoStatusWithGPSMassQueALPrefs(UpdateJobInfoPreferences, regid, JobsIDXsArr, finalAStatusCodeIDX,
                                                        timestampstr, "", "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(),
                                                        Accuracy, "");

                                            }

                                            if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                                                objCheckedItemsJsonStr = gson.toJson(objCheckedItemsAL);
                                                objGlobalMethods.setMedsColPrefs(mcsp, objGlobalVariables.checkedItemsName, objCheckedItemsJsonStr);
                                            }

                                            progressDoalog.dismiss();
                                            progressDoalog = null;

                                            if (c > 0) {
                                                ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));
                                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM h:mma");
                                                final String arrivaltime = sdf.format(new Date(l));
                                                myApp.currentActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        gvh.LLarrival.setVisibility(View.VISIBLE);
                                                        gvh.lblarrival.setText(ctx.getString(R.string.buttontext_arrivaltime) + " " + arrivaltime);
                                                    }
                                                });
                                            } else {
                                                objGlobalMethods.showDialog(myApp.currentActivity, "OK", ctx.getString(R.string.alert_ihavearrive_manual)).show();
                                            }
                                        } catch (Exception e) {
                                            objGlobalMethods.writeToFile("Exception at StartQueryAdapter rtnIhaveArriveClickListener thread. Exception: "
                                                    + e.getMessage().toString(), "", "", "", "");
                                            objGlobalMethods.showDialog(myApp.currentActivity, "OK", ctx.getString(R.string.alert_ihavearrive_manual)).show();
                                        }
                                    }
                                };
                                t.start();


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
                        objGlobalMethods.showDialog(myApp.currentActivity, "OK", ctx.getString(R.string.alert_ihavearrive_manual)).show();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at StartQueryAdapter rtnIhaveArriveClickListenerMass. Exception: "
                            + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final SQItem d = (SQItem) getChild(groupPosition, childPosition);
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;
        final boolean manual = d.manual;

        ViewHolder v;

        try {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.meds_col_item, null);
                v = new ViewHolder();
                v.linearLayout1 = convertView.findViewById(R.id.linearLayout1);
                v.LL_ExpTypeRow = convertView.findViewById(R.id.LL_ExpTypeRow);
                v.LLjlgcb = convertView.findViewById(R.id.LLjlgcb);
                v.LLjlinfo = convertView.findViewById(R.id.LLjlinfo);
                v.lblContactname = convertView.findViewById(R.id.lblContactname);
                v.lblhfJobsInfoJsonStr = convertView.findViewById(R.id.hfJobsInfoJsonStr);
                v.lblCnno = convertView.findViewById(R.id.lblCnno);
                v.lblPcs = convertView.findViewById(R.id.lblPcs);
                v.lblJobsIDX = convertView.findViewById(R.id.lblJobsIDX);
                v.cb = convertView.findViewById(R.id.cbJobsList);

                v.lblAddressListcoldimg = convertView.findViewById(R.id.lblAddressListcoldimg);

                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            v.LLjlgcb.setBackgroundResource(d.LLjlgcbChangeColor ? R.color.menu_red : R.color.transparent);
            v.lblContactname.setText(d.Address);
            v.lblhfJobsInfoJsonStr.setText(d.JobInfoJsonStr);
            v.lblCnno.setText(d.CNNO);
            v.lblJobsIDX.setText(d.JobsIDX);
            v.lblAddressListcoldimg.setText(JobsHandler2.isColdItem(d.FLAG2) ? _context.getString(R.string.icontxt_cold) : "");
            v.lblAddressListcoldimg.setOnClickListener(null);
            v.cb.setOnCheckedChangeListener(null);

            try {
                v.cb.setChecked(d.IsSelected);
            } catch (Exception e) {
                String s = e.getMessage();
            }

            /*if (v.cb.isChecked() && d.PCS_SCANNED == d.PCS) {
                v.LLjlgcb.setBackgroundResource(R.color.brightgreen);
            } else if (v.cb.isChecked() && d.PCS_SCANNED != d.PCS) {
                v.LLjlgcb.setBackgroundResource(R.color.colorAccent);
            }*/

            v.cb.setOnCheckedChangeListener(null);
            try {
                if (mChildCheckStates2.containsKey(mGroupPosition)) {
                    objListItemPosChecked getChecked2[] = mChildCheckStates2.get(mGroupPosition);
                    v.cb.setChecked(getChecked2[mChildPosition].isChecked());
                } else {
                    int ItemsCount = getChildrenCount(mGroupPosition);
                    objListItemPosChecked getChecked2[] = new objListItemPosChecked[ItemsCount];
                    for (int b = 0; b <= getChecked2.length - 1; b++) {
                        getChecked2[b] = new objListItemPosChecked(b, false);
                    }
                    mChildCheckStates2.put(mGroupPosition, getChecked2);
                    v.cb.setChecked(d.IsSelected);
                }

            } catch (Exception e) {
                String s = e.getMessage();
            }

            /*if ((v.cb.isChecked())) {
                v.LLjlgcb.setBackgroundResource(R.color.brightgreen);
            }*/


            if (manual) {
                v.cb.setVisibility(View.VISIBLE);
                v.cb.setEnabled(true);
                v.cb.setOnCheckedChangeListener(cbOnCheckedChangeListener(mGroupPosition, mChildPosition));
                v.lblPcs.setText(" x 1");
                if (v.cb.isChecked() && d.PCS_SCANNED == d.PCS) {
                    v.linearLayout1.setBackgroundColor(Color.GREEN);
                    //v.lblAddressListcoldimg.setTextColor(Color.BLACK);
                } else if (v.cb.isChecked() && d.PCS_SCANNED > 0 && d.PCS_SCANNED < d.PCS) {
                    v.linearLayout1.setBackgroundColor(Color.WHITE);
                    //v.lblAddressListcoldimg.setTextColor(_context.getColor(R.color.cold));
                } else {
                    v.linearLayout1.setBackgroundColor(Color.WHITE);
                    //v.lblAddressListcoldimg.setTextColor(Color.BLACK);
                }
            } else {
                v.cb.setVisibility(View.GONE);
                v.cb.setEnabled(false);
                if (d.blink) {
                    SpannableString content = new SpannableString(" x " + d.PCS_SCANNED + "/" + d.PCS);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    v.lblPcs.setText(content);
                    v.lblPcs.setTypeface(null, Typeface.BOLD);
                } else {
                    v.lblPcs.setText(" x " + d.PCS_SCANNED + "/" + d.PCS);
                    v.lblPcs.setTypeface(null, Typeface.NORMAL);
                }

                if (d.PCS_SCANNED == d.PCS) {
                    v.linearLayout1.setBackgroundColor(Color.GREEN);
                    //v.lblAddressListcoldimg.setTextColor(Color.BLACK);
                } else if (d.PCS_SCANNED > 0 && d.PCS_SCANNED < d.PCS) {
                    v.linearLayout1.setBackgroundColor(Color.WHITE);
                    //v.lblAddressListcoldimg.setTextColor(_context.getColor(R.color.cold));
                } else {
                    v.linearLayout1.setBackgroundColor(Color.WHITE);
                    //v.lblAddressListcoldimg.setTextColor(Color.BLACK);
                }
            }

            v.LLjlinfo.setOnClickListener(null);
            //v.LLjlinfo.setOnClickListener(LLOnClickListener(d.Address + "\n\n" + d.TimingText));

            /*if (d.blink) {
                manageBlinkEffectNew(v.linearLayout1, d.blinkmode);
            }*/

            convertView.clearFocus();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StartQueryAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertView;
        }
    }

    private void manageBlinkEffectNew(final LinearLayout ll, int mode) {
        try {
            ObjectAnimator anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.WHITE, Color.GREEN, Color.WHITE);

            anim.setDuration(300);
            anim.setEvaluator(new ArgbEvaluator());
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.setRepeatCount(1);
            /*anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ll.setBackgroundColor(mode == 0 ? Color.GREEN : Color.WHITE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });*/
            anim.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StartQueryAdapter manageBlinkEffectNew. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            setAllItemsBlinkFalse();
        }
    }

    private void setAllItemsBlinkFalse(){
        try {
            List<String> DataHeader;
            HashMap<String, List<SQItem>> DataChild2;
            DataHeader = _listDataHeader;
            DataChild2 = _listDataChild;
            Iterator<HashMap.Entry<String, List<SQItem>>> itr = null;
            itr = DataChild2.entrySet().iterator();
            List<SQItem> jpdlist = null;
            SQItem jpd = null;
            String key = "";
            String groupName = "";
            int groupIDX = 0;
            int index = -1;

            outer:
            while (itr.hasNext()) {
                jpd = null;
                HashMap.Entry<String, List<SQItem>> entry = itr.next();
                jpdlist = entry.getValue();
                for (int x = 0; x <= jpdlist.size() - 1; x++) {
                    jpd = jpdlist.get(x);
                    jpd.setBlink(false);
                    jpd.setBlinkmode(0);
                }
            }
        } catch (Exception e){
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StartQueryAdapter setAllItemsBlinkFalse. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally{
            notifyDataSetChanged();
        }
    }

    private CompoundButton.OnCheckedChangeListener cbOnCheckedChangeListener(final int mGroupPosition, final int mChildPosition) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    SharedPreferences m = objGlobalMethods.getMedsColSP();
                    final Context ctx = myApp.getAppContext();
                    progressDoalog = new ProgressDialog(myApp.currentActivity);
                    progressDoalog.setMessage(ctx.getString(R.string.pd_waitprocessingupdates));
                    progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    //progressDoalog.setMax(count);
                    progressDoalog.setCanceledOnTouchOutside(false);
                    progressDoalog.show();

                    SQItem jpd = (SQItem) getChild(mGroupPosition, mChildPosition);
                    final long JobsIDX = Long.parseLong(jpd.JobsIDX);

                    if (isChecked) {
                        objListItemPosChecked getChecked2[] = mChildCheckStates2.get(mGroupPosition);
                        getChecked2[mChildPosition].setChecked(isChecked);
                        mChildCheckStates2.put(mGroupPosition, getChecked2);
                        jpd.setPCS_SCANNED(1);
                    } else {
                        mChildCheckStates2 = getmChildCheckStates2(mGroupPosition);
                        objListItemPosChecked getChecked2[] = mChildCheckStates2.get(mGroupPosition);
                        getChecked2[mChildPosition].setChecked(isChecked);
                        mChildCheckStates2.put(mGroupPosition, getChecked2);
                        jpd.setPCS_SCANNED(-1);
                    }

                    if (mChildCheckStates2 != null && mChildCheckStates2.size() > 0) {
                        Gson gson = new Gson();
                        String mChildCheckStates2PrefStr = gson.toJson(mChildCheckStates2);
                        objGlobalMethods.setGCMPrefs(m, "mChildCheckStates2PrefStr", mChildCheckStates2PrefStr);
                    }

                    final Thread t = new Thread() {
                        @Override
                        public void run() {
                            int val = isChecked ? 1 : -1;
                            DAO_Jobs.updatePIPPCS_1by1(JobsIDX, val);
                        }
                    };
                    t.start();

                    if (progressDoalog != null && progressDoalog.isShowing()) {
                        progressDoalog.dismiss();
                        progressDoalog = null;
                    }

                    objGlobalMethods.writeToFile(jpd.JobsIDX + " " + (isChecked ? "selected" : "unselected") + " at StartQueryAdapter. Result " + jpd.PCS_SCANNED + "/" + jpd.PCS,"","","","");

                    notifyDataSetChanged();
                } catch (Exception e) {
                    if (progressDoalog != null && progressDoalog.isShowing()) {
                        progressDoalog.dismiss();
                        progressDoalog = null;
                    }

                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at StartQueryAdapter cbOnCheckedChangeListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener LLOnClickListener(final String txt) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = myApp.getAppContext();
                objGlobalMethods.showCancellableDialog(myApp.currentActivity, ctx.getString(R.string.lblDELTO2), ctx.getString(R.string.buttontext_ok), txt).show();
            }
        };
    }

    public final class GroupViewHolder {
        TextView lblListHeader;
        TextView lblListHeaderCount;
        TextView lblJobsCount;
        TextView lblPcsCount;
        TextView lblPUCount;
        LinearLayout LLjobs_list_group_ihavearrive;
        LinearLayout LLarrival;
        ImageView img_i_have_arrive;
        TextView lblarrival;
    }

    public final class ViewHolder {
        TextView lblContactname;
        TextView lblhfJobsInfoJsonStr;
        TextView lblCnno;
        TextView lblPcs;
        TextView lblJobsIDX;
        CheckBox cb;
        LinearLayout LLjlgcb;
        LinearLayout LLjlinfo;
        LinearLayout linearLayout1;
        LinearLayout LL_ExpTypeRow;
        TextView lblAddressListcoldimg;

    }


}
