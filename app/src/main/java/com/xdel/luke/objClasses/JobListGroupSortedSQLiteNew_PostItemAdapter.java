package com.xdel.luke.objClasses;

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
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsHandler2;
import com.xdel.luke.JobsHandler3;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;
import com.xdel.luke.setDunningDeliveredStatusActivity;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kean.ong on 13/08/2025.
 */
public class JobListGroupSortedSQLiteNew_PostItemAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private Context _context;
    public List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    public HashMap<String, List<JobListGroupSortedSQLite_PostData>> _listDataChild;

    public HashMap<Integer, boolean[]> mChildCheckStates;
    public HashMap<Integer, boolean[]> mChildColorStates;
    public HashMap<Integer, objListItemPosChecked[]> mChildCheckStates2;
    private ChildViewHolder childViewHolder;
    private int CatTypeID;
    private SharedPreferences GcmPreferences;
    public static ProgressDialog progressDoalog;
    public static objMultiSelectInitialCriteria criteria = null;
    public static jobSelectionInfo j1 = null;

    private static Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDoalog != null) {
                progressDoalog.incrementProgressBy(1);
            }
        }
    };

    public JobListGroupSortedSQLiteNew_PostItemAdapter(Context context, List<String> listDataHeader, HashMap<String, List<JobListGroupSortedSQLite_PostData>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        mChildCheckStates = new HashMap<Integer, boolean[]>();
        mChildColorStates = new HashMap<Integer, boolean[]>();
        mChildCheckStates2 = new HashMap<Integer, objListItemPosChecked[]>();
        this.CatTypeID = objGlobalMethods.getDashboard2ActivityCategoryType(objGlobalMethods.getGcmPreferences());
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

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final JobListGroupSortedSQLite_PostData tempChild = (JobListGroupSortedSQLite_PostData) getChild(groupPosition, childPosition);
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.jobs_list_group_sorted_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
            childViewHolder.LL_ExpTypeRow = (LinearLayout) convertView.findViewById(R.id.LL_ExpTypeRow);
            childViewHolder.LLjlgcb = (LinearLayout) convertView.findViewById(R.id.LLjlgcb);
            childViewHolder.LLjlinfo = (LinearLayout) convertView.findViewById(R.id.LLjlinfo);
            childViewHolder.lblExpType = (TextView) convertView.findViewById(R.id.lblExpType);
            childViewHolder.lblCompany = (TextView) convertView.findViewById(R.id.lblCompany);
            childViewHolder.lblContactname = (TextView) convertView.findViewById(R.id.lblContactname);
            childViewHolder.lblDatetime = (TextView) convertView.findViewById(R.id.lblDatetime);
            childViewHolder.lblhfJobsInfoJsonStr = (TextView) convertView.findViewById(R.id.hfJobsInfoJsonStr);
            childViewHolder.lblCnno = (TextView) convertView.findViewById(R.id.lblCnno);
            childViewHolder.lblPcs = (TextView) convertView.findViewById(R.id.lblPcs);
            childViewHolder.lblPart = (TextView) convertView.findViewById(R.id.lblPart);
            childViewHolder.lblJobsIDX = (TextView) convertView.findViewById(R.id.lblJobsIDX);
            childViewHolder.cb = (CheckBox) convertView.findViewById(R.id.cbJobsList);
            childViewHolder.lblLunchtime = (TextView) convertView.findViewById(R.id.lblLunchtime);

            childViewHolder.lblAddressListLockimg = (TextView) convertView.findViewById(R.id.lblAddressListLockimg);
            childViewHolder.lblAddressListDNFimg = (TextView) convertView.findViewById(R.id.lblAddressListDNFimg);
            childViewHolder.lblAddressList2wayimg = (TextView) convertView.findViewById(R.id.lblAddressList2wayimg);
            childViewHolder.lblAddressListDOCODimg = (TextView) convertView.findViewById(R.id.lblAddressListDOCODimg);
            childViewHolder.lblAddressListccimg = (TextView) convertView.findViewById(R.id.lblAddressListccimg);
            childViewHolder.lblAddressListLetterboximg = (TextView) convertView.findViewById(R.id.lblAddressListLetterboximg);
            childViewHolder.lblAddressListUnderDoorimg = (TextView) convertView.findViewById(R.id.lblAddressListUnderDoorimg);
            childViewHolder.lblAddressListGuardimg = (TextView) convertView.findViewById(R.id.lblAddressListGuardimg);
            childViewHolder.lblAddressListDoorstepimg = (TextView) convertView.findViewById(R.id.lblAddressListDoorstepimg);
            childViewHolder.lblAddressListContactlessimg = convertView.findViewById(R.id.lblAddressListContactlessimg);
            childViewHolder.lblAddressListcoldimg = convertView.findViewById(R.id.lblAddressListcoldimg);

            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        if (parent != null) {
            /*if (tempChild.blink) {
                manageBlinkEffectNew(childViewHolder.linearLayout1);
            }*/
            childViewHolder.LLjlgcb.setBackgroundResource(tempChild.LLjlgcbChangeColor ? R.color.menu_red : R.color.transparent);
            childViewHolder.lblExpType.setText(tempChild.ExpType);
            childViewHolder.lblContactname.setText(tempChild.Contactname);
            childViewHolder.lblCompany.setText(tempChild.Company);
            childViewHolder.lblCompany.setVisibility(tempChild.Company.equals("") ? View.GONE : View.VISIBLE);
            childViewHolder.lblDatetime.setText(tempChild.TimingText);
            childViewHolder.lblLunchtime.setVisibility(tempChild.HASLUNCHTIME ? View.VISIBLE : View.GONE);
            childViewHolder.lblhfJobsInfoJsonStr.setText(tempChild.JobInfoJsonStr);
            childViewHolder.lblCnno.setText(tempChild.CNNO);
            childViewHolder.lblPcs.setText(" x " + tempChild.Pieces);
            childViewHolder.lblPart.setText(tempChild.Part);
            childViewHolder.lblJobsIDX.setText(tempChild.JobsIDX);
            childViewHolder.cb.setEnabled(tempChild.CbIsEnabled);
            childViewHolder.cb.setVisibility(tempChild.CbIsEnabled ? View.VISIBLE : View.GONE);

            if (tempChild.FLAG != null && tempChild.FLAG > 0) {
                //childViewHolder.lblAddressListLockimg.setText((tempChild.FLAG & 512) == 512 && (tempChild.FLAG2 & 32) != 32 && tempChild.isDelivery ? _context.getString(R.string.icontxt_otp) : "");
                childViewHolder.lblAddressListLockimg.setText(tempChild.OTP && (tempChild.FLAG2 & 32) != 32 && tempChild.isDelivery ? _context.getString(R.string.icontxt_otp) : "");
                childViewHolder.lblAddressListDNFimg.setText((tempChild.FLAG & 8) == 8 && tempChild.DNF ? _context.getString(R.string.icontxt_dnf) : "");
                childViewHolder.lblAddressListLockimg.setOnClickListener(null);
                childViewHolder.lblAddressListDNFimg.setOnClickListener(null);
                //childViewHolder.lblAddressListLockimg.setOnClickListener((tempChild.FLAG & 512) == 512 && tempChild.isDelivery ? objGlobalMethods.rtnImgV_OnClickListener(1) : null);
                childViewHolder.lblAddressListLockimg.setOnClickListener(tempChild.OTP && tempChild.isDelivery ? objGlobalMethods.rtnImgV_OnClickListener(1) : null);
                childViewHolder.lblAddressListDNFimg.setOnClickListener((tempChild.FLAG & 8) == 8 && tempChild.DNF ? objGlobalMethods.rtnImgV_OnClickListener(2) : null);
            } else if (tempChild.FLAG == null || tempChild.FLAG == 0) {
                childViewHolder.lblAddressListLockimg.setText("");
                childViewHolder.lblAddressListDNFimg.setText("");
                childViewHolder.lblAddressListLockimg.setOnClickListener(null);
                childViewHolder.lblAddressListDNFimg.setOnClickListener(null);
            }

            if (tempChild.isDelivery) {
                childViewHolder.lblAddressListccimg.setText((tempChild.NetsVisaTi & 4) == 4 ? _context.getString(R.string.icontxt_nets) : (tempChild.NetsVisaTi & 8) == 8 ? _context.getString(R.string.icontxt_visa) : "");
            } else {
                childViewHolder.lblAddressListccimg.setText("");
            }

            childViewHolder.lblAddressListcoldimg.setText(JobsHandler2.isColdItem(tempChild.FLAG2) ? _context.getString(R.string.icontxt_cold) : "");
            childViewHolder.lblAddressListcoldimg.setOnClickListener(null);
            childViewHolder.lblAddressListcoldimg.setOnClickListener(JobsHandler2.isColdItem(tempChild.FLAG2) ? objGlobalMethods.rtnImgV_OnClickListener(18) : null);

            if (tempChild.HasTradeIn && tempChild.isDelivery) {
                childViewHolder.lblAddressListDOCODimg.setText(_context.getString(R.string.icontxt_tradein));
            } else {
                childViewHolder.lblAddressListDOCODimg.setText("");
            }
            childViewHolder.lblAddressListDOCODimg.setOnClickListener(null);
            childViewHolder.lblAddressListDOCODimg.setOnClickListener((tempChild.HasTradeIn && tempChild.isDelivery) ?
                    objGlobalMethods.rtnImgV_OnClickListener(4) : null);

            childViewHolder.lblAddressListccimg.setOnClickListener(null);
            if (tempChild.isDelivery && ((tempChild.NetsVisaTi & 4) == 4)) {
                childViewHolder.lblAddressListccimg.setOnClickListener(objGlobalMethods.rtnImgV_OnClickListener(12));
            } else if (tempChild.isDelivery && ((tempChild.NetsVisaTi & 8) == 8)) {
                childViewHolder.lblAddressListccimg.setOnClickListener(objGlobalMethods.rtnImgV_OnClickListener(13));
            }

            childViewHolder.lblAddressList2wayimg.setText(tempChild.IsPartOne ? _context.getString(R.string.icontxt_2way) : "");
            childViewHolder.lblAddressList2wayimg.setOnClickListener(null);
            childViewHolder.lblAddressList2wayimg.setOnClickListener(tempChild.IsPartOne ? objGlobalMethods.rtnImgV_OnClickListener(5) : null);

            Boolean isIntendedRecOnly = objGlobalMethods.isIntendedRecOnly(tempChild.ji.FLAG != null && tempChild.ji.FLAG > 0 ? tempChild.ji.FLAG : 0);
            Boolean isTotallyCannotSlot = objGlobalMethods.isTotallyCannotSlot(tempChild.ji.FLAG != null && tempChild.ji.FLAG > 0 ? tempChild.ji.FLAG : 0);
            Boolean canSlotLB = objGlobalMethods.isCanSlotLB(tempChild.ji.FLAG != null && tempChild.ji.FLAG > 0 ? tempChild.ji.FLAG : 0);
            Boolean canSlotDoor = objGlobalMethods.isCanSlotUnderDoor(tempChild.ji.FLAG != null && tempChild.ji.FLAG > 0 ? tempChild.ji.FLAG : 0);
            Boolean canGuard = objGlobalMethods.isCanGuard(tempChild.ji.FLAG != null && tempChild.ji.FLAG > 0 ? tempChild.ji.FLAG : 0);
            Boolean contactless = JobsHandler.hasContactlessDeliveryFlag(tempChild.ji.FLAG);


            childViewHolder.lblAddressListLetterboximg.setText(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotLB ? _context.getString(R.string.icontxt_mailbox) : "");
            childViewHolder.lblAddressListLetterboximg.setOnClickListener(null);
            childViewHolder.lblAddressListLetterboximg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotLB ? objGlobalMethods.rtnImgV_OnClickListener(7) : null);

            childViewHolder.lblAddressListUnderDoorimg.setText(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotDoor ? _context.getString(R.string.icontxt_door) : "");
            childViewHolder.lblAddressListUnderDoorimg.setOnClickListener(null);
            childViewHolder.lblAddressListUnderDoorimg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canSlotDoor ? objGlobalMethods.rtnImgV_OnClickListener(8) : null);

            childViewHolder.lblAddressListGuardimg.setText(!isIntendedRecOnly && !isTotallyCannotSlot && canGuard ? _context.getString(R.string.icontxt_guard) : "");
            childViewHolder.lblAddressListGuardimg.setOnClickListener(null);
            childViewHolder.lblAddressListGuardimg.setOnClickListener(!isIntendedRecOnly && !isTotallyCannotSlot && canGuard ? objGlobalMethods.rtnImgV_OnClickListener(9) : null);

            childViewHolder.lblAddressListDoorstepimg.setText(JobsHandler2.CanLeaveDoorStep(tempChild.FLAG2) ? _context.getString(R.string.icontxt_doorstep) : "");
            childViewHolder.lblAddressListDoorstepimg.setOnClickListener(null);

            childViewHolder.lblAddressListContactlessimg.setText(contactless ? _context.getString(R.string.icontxt_contactless) : "");

            boolean isPickAndSend = (tempChild.isPickAndSendCount != null && tempChild.isPickAndSendCount > 0) ? true : false;
            boolean hasJobChangesCount = tempChild.JobChangesCount != null && tempChild.JobChangesCount > 0 ? true : false;

            childViewHolder.cb.setOnCheckedChangeListener(null);

            try {
                if (mChildCheckStates.containsKey(mGroupPosition)) {
                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    childViewHolder.cb.setChecked(getChecked[mChildPosition]);
                } else {
                    int ItemsCount = getChildrenCount(mGroupPosition);
                    objListItemPosChecked getChecked2[] = new objListItemPosChecked[ItemsCount];
                    for (int v = 0; v <= getChecked2.length - 1; v++) {
                        getChecked2[v] = new objListItemPosChecked(v, false);
                    }
                    mChildCheckStates2.put(mGroupPosition, getChecked2);
                    childViewHolder.cb.setChecked(tempChild.IsSelected);
                }

            } catch (Exception e) {
                String s = e.getMessage();
            }

        }

        if (tempChild.allowedSelection || (tempChild.CatTypeID == 0 && childViewHolder.cb.isChecked())) {
            childViewHolder.LLjlgcb.setBackgroundResource(R.color.brightgreen);
        }

        childViewHolder.cb.setOnCheckedChangeListener(null);
        //childViewHolder.cb.setOnCheckedChangeListener(cbOnCheckedChangeListenerForOKP(mGroupPosition, mChildPosition, tempChild.isDelivery));
        childViewHolder.cb.setOnCheckedChangeListener(cbOnCheckedChangeListenerForOKPNew(mGroupPosition, mChildPosition, tempChild.isDelivery));

        childViewHolder.LLjlinfo.setOnClickListener(null);
        childViewHolder.LLjlinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                Gson gson = new Gson();
                objJobInfo JobInfo;
                TextView lblhfJobsInfoJsonStr = (TextView) v.findViewById(R.id.hfJobsInfoJsonStr);
                String JobInfoJsonStr = lblhfJobsInfoJsonStr.getText().toString();
                JobInfo = gson.fromJson(JobInfoJsonStr, objJobInfo.class);
                Boolean isDunning = JobsHandler.isDunning(JobInfo);
                SharedPreferences GcmPreferences = getGcmPreferences(_context);

                long packedPosition = mGroupPosition;
                final long flatpostion = mChildPosition;
                int finalIDX = 0;
                if (mGroupPosition == 0) {
                    finalIDX = mChildPosition;
                } else {
                    final int skippedCount = JobsListGroupActivity.getSkippedCount(_listDataChild.entrySet().iterator(), mGroupPosition, _listDataHeader);
                    finalIDX = skippedCount + mChildPosition;
                }
                String[] prefName = new String[]{"JobsListGroupScrollSavedIndex", "JobsListGroupScrollSavedPosition"};
                String[] prefvalue = new String[]{String.valueOf(mGroupPosition), String.valueOf(finalIDX)};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                SharedPreferences UpdateJobInfoPreferences = getUpdateJobInfoPreferences(_context);
                String[] prefName2 = new String[]{objGlobalVariables.checkedItemsName, objGlobalVariables.checkedItemsIDX, "ScannedCodeStrsPrefStr", "massupdateokpwp2PostDataArr"};
                String[] prefvalue2 = new String[]{"", "", "", ""};
                objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName2, prefvalue2);

                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobStatusUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objPendingJobPhotoUpdatesobjALPrefStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objUpdateJobInfoPhotoObjListJsonStr", "");
                objGlobalMethods.setGCMPrefs(UpdateJobInfoPreferences, "objUpdateJobInfoSignatureObjListJsonStr", "");


                objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JobInfoJsonStr);

                int categoryType = -1;
                categoryType = objGlobalMethods.getDashboard2ActivityCategoryType(GcmPreferences);

                if (categoryType == 3 || categoryType == 4 || categoryType == 5 || categoryType == 6 || categoryType == 7) {
                    i = new Intent(activity, JobInfoActivity.class);
                    JobInfoActivity.accessFromDirection = 1;
                } else {
                    if (isDunning) {
                        if (JobInfo != null) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", JobsHandler.CannotTakePhoto(JobInfo, 2) ? "1" : "0");
                        } else {
                            objGlobalMethods.setGCMPrefs(GcmPreferences, "CannotTakePhoto", "");
                        }

                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.currJobInfoIDX, String.valueOf(JobInfo.JobsIDX));
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "currJobInfoCN", JobInfo.JobNo);
                        String addressWithUnit = "";
                        if (JobInfo.DLRedirectedLocation != null) {
                            //only for dunning
                            addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLRedirectedLocation);
                        } else if (JobInfo.DLLocation != null) {
                            //only for dunning
                            addressWithUnit = objGlobalMethods.returnAddressWithoutPostal(JobInfo.DLLocation);
                        }
                        objGlobalMethods.setGCMPrefs(GcmPreferences, "addressWithUnit", addressWithUnit);
                        i = new Intent(activity, setDunningDeliveredStatusActivity.class);
                        JobInfoActivity.accessFromDirection = 0;
                    } else {
                        i = new Intent(activity, JobInfoActivity.class);
                        JobInfoActivity.accessFromDirection = 1;
                    }
                }

                //_context.startActivity(i);
                myApp.currentActivity.startActivity(i);
            }
        });

        convertView.clearFocus();

        return convertView;
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
            objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNew_PostItemAdapter getmChildCheckStates2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return mChildCheckStates2;
        }
    }

    public HashMap<Integer, boolean[]> getmChildCheckStates(int mGroupPosition) {
        try {
            if (mChildCheckStates != null) {
                if (mChildCheckStates.containsKey(mGroupPosition)) {
                    //objListItemPosChecked getChecked2[] = mChildCheckStates2.get(mGroupPosition);
                } else {
                    int ItemsCount = getChildrenCount(mGroupPosition);
                    boolean getChecked[] = new boolean[ItemsCount];
                    for (int v = 0; v <= getChecked.length - 1; v++) {
                        getChecked[v] = false;
                    }
                    mChildCheckStates.put(mGroupPosition, getChecked);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNewNew_PostItemAdapter getmCheckCheckStates. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return mChildCheckStates;
        }
    }

    public void setmChildCheckStates(HashMap<Integer, boolean[]> mc) {
        this.mChildCheckStates = mc;
    }

    public boolean hasChecked() {
        boolean asd = false;
        try {
            if (mChildCheckStates == null) {
                Gson gson = new Gson();
                SharedPreferences glsp = objGlobalMethods.getGrouplistSP();
                String mChildCheckStatesPrefStr = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "mChildCheckStatesPrefStr");
                mChildCheckStates = objGlobalMethods.IsEmptyString(mChildCheckStatesPrefStr) ? null :
                        gson.fromJson(mChildCheckStatesPrefStr, new TypeToken<HashMap<Integer, boolean[]>>() {
                        }.getType());
            }

            if (mChildCheckStates != null && mChildCheckStates.size() > 0) {
                outer:
                for (boolean[] arr : mChildCheckStates.values()) {
                    for (boolean b : arr) {
                        if (b) {
                            asd = true;
                            break outer;
                        }
                    }
                }

            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNew_PostItemAdapter getmChildCheckStates. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    private CompoundButton.OnCheckedChangeListener cbOnCheckedChangeListenerForOKPNew(final int mGroupPosition, final int mChildPosition, final boolean isDelivery) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    SharedPreferences GcmPreferences = getGcmPreferences(_context);
                    SharedPreferences glsp = objGlobalMethods.getGrouplistSP();
                    String jobSelectionInfoJsonStr = "";
                    String jobSelectionInfoJsonStr_ = "";
                    Gson gson = new Gson();

                    boolean firstSelected = true;
                    JobListGroupSortedSQLite_PostData jpd = (JobListGroupSortedSQLite_PostData) getChild(mGroupPosition, mChildPosition);
                    objJobInfo ji = jpd.ji;
                    List<JobListGroupSortedSQLite_PostData> clist = null;
                    JobListGroupSortedSQLite_PostData cjpd = null;
                    objJobInfo cji = null;
                    jobSelectionInfo j2 = null;
                    boolean j2canMass = false;

                    if (isChecked) {
                        if (isDelivery) {
                            firstSelected = !hasChecked();
                            if (firstSelected) {
                                int itemoriPos;
                                boolean j1canMass = false;

                                j1 = jobSelectionInfo.getjobSelectionInfo(ji, true);
                                j1canMass = j1.canMassWithOthers();

                                if (_listDataChild != null && _listDataChild.size() > 0 && _listDataHeader != null && _listDataHeader.size() > 0) {
                                    for (int i = 0; i <= _listDataHeader.size() - 1; i++) {
                                        clist = _listDataChild.get(_listDataHeader.get(i));
                                        mChildCheckStates = getmChildCheckStates(i);
                                        boolean getChecked[] = mChildCheckStates.get(i);
                                        if (i != mGroupPosition) {
                                            for (int x = 0; x <= clist.size() - 1; x++) {
                                                itemoriPos = clist.get(x).oriPos;
                                                if (!j1canMass) {
                                                    clist.get(x).setCbEnabledAllowedSelection(true, false, itemoriPos);
                                                    j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                    //getChecked[x] = false;
                                                } else {
                                                    cjpd = clist.get(x);
                                                    cji = cjpd.ji;
                                                    j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                    j2canMass = j1.canMass(j2);

                                                    clist.get(x).setCbEnabledAllowedSelection(true, j2canMass, itemoriPos);
                                                    if (!j2canMass)
                                                        j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                    //getChecked[x] = false;

                                                }
                                                getChecked[x] = false;
                                            }
                                            mChildCheckStates.put(i, getChecked);

                                        } else {
                                            for (int x = 0; x <= clist.size() - 1; x++) {
                                                cjpd = clist.get(x);
                                                cji = cjpd.ji;
                                                if (cji.JobsIDX != ji.JobsIDX && !j1canMass) {
                                                    clist.get(x).setCbEnabledAllowedSelection(true, false, 0);
                                                    j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                } else if (cji.JobsIDX != ji.JobsIDX) {
                                                    j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                    j2canMass = j1.canMass(j2);

                                                    clist.get(x).setCbEnabledAllowedSelection(true, !(!j1canMass || !j2canMass), 0);
                                                    if (!j2canMass)
                                                        j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                    getChecked[x] = false;
                                                } else {
                                                    clist.get(x).setCbEnabledAllowedSelection(true, true, 0);
                                                    j1.addSelectedValidJobIDXList(cji.JobsIDX);
                                                    getChecked[x] = true;
                                                }

                                            }
                                            mChildCheckStates.put(i, getChecked);
                                        }
                                    }
                                }
                                jpd.setAllowedSelection(true);
                                jobSelectionInfoJsonStr = gson.toJson(j1);

                            } else {
                                boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                                getChecked[mChildPosition] = isChecked;
                                mChildCheckStates.put(mGroupPosition, getChecked);
                                if (j1 == null) {
                                    jobSelectionInfoJsonStr_ = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "jobSelectionInfoJsonStr");
                                    if (!objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr_)) {
                                        j1 = gson.fromJson(jobSelectionInfoJsonStr_, jobSelectionInfo.class);
                                    }
                                }
                                j2 = jobSelectionInfo.getjobSelectionInfo(ji, false);
                                if (!j1.canMass(j2)) {
                                    j1.setNotAllowedSelectionCount(1);
                                    j1.addSelectedInvalidJobIDXList(ji.JobsIDX);
                                } else {
                                    j1.addSelectedValidJobIDXList(ji.JobsIDX);
                                    j1.setFieldsFromj2(j2);

                                    ////
                                    ////
                                    boolean j1canMass = j1.canMassWithOthers();
                                    if (_listDataChild != null && _listDataChild.size() > 0 && _listDataHeader != null && _listDataHeader.size() > 0) {
                                        for (int i = 0; i <= _listDataHeader.size() - 1; i++) {
                                            clist = _listDataChild.get(_listDataHeader.get(i));
                                            if (i != mGroupPosition) {
                                                boolean getChecked2[] = mChildCheckStates.get(i);
                                                for (int x = 0; x <= clist.size() - 1; x++) {
                                                    if (clist.get(x).ji.JobsIDX != j1.JobsIDX) {
                                                        if (!j1canMass) {
                                                            clist.get(x).setCbEnabledAllowedSelection(true, false, 0);
                                                            j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                            //getChecked[x] = false;
                                                        } else {
                                                            cjpd = clist.get(x);
                                                            cji = cjpd.ji;
                                                            j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                            j2canMass = j1.canMass(j2);

                                                            clist.get(x).setCbEnabledAllowedSelection(true, j2canMass, 0);
                                                            if (!j2canMass)
                                                                j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);

                                                        }
                                                        //getChecked2[x] = false;
                                                    }
                                                }
                                                mChildCheckStates.put(i, getChecked2);

                                            } else {
                                                for (int x = 0; x <= clist.size() - 1; x++) {
                                                    if (clist.get(x).ji.JobsIDX != j1.JobsIDX) {
                                                        cjpd = clist.get(x);
                                                        cji = cjpd.ji;
                                                        if (cji.JobsIDX != ji.JobsIDX && !j1canMass) {
                                                            clist.get(x).setCbEnabledAllowedSelection(true, false, 0);
                                                            j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                        } else if (cji.JobsIDX != ji.JobsIDX) {
                                                            j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                            j2canMass = j1.canMass(j2);

                                                            clist.get(x).setCbEnabledAllowedSelection(true, !(!j1canMass || !j2canMass), 0);
                                                            if (!j2canMass)
                                                                j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                            //getChecked[x] = false;
                                                        } else {
                                                            clist.get(x).setCbEnabledAllowedSelection(true, true, 0);
                                                            j1.addSelectedValidJobIDXList(cji.JobsIDX);
                                                            //getChecked[x] = true;
                                                        }
                                                    }

                                                }
                                                mChildCheckStates.put(i, getChecked);
                                            }
                                        }
                                    }
                                    ////
                                    ////
                                }

                                jobSelectionInfoJsonStr = gson.toJson(j1);
                            }
                        } else {
                            mChildCheckStates = getmChildCheckStates(mGroupPosition);
                            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                            getChecked[mChildPosition] = isChecked;
                            mChildCheckStates.put(mGroupPosition, getChecked);
                            jpd.setAllowedSelection(true);
                        }

                        jpd.setIsSelected(true);
                        JobsHandler3.processCheckedItemsBeforeSubmitSQLiteNew(jpd, true, GcmPreferences);
                    } else {
                        //uncheck
                        mChildCheckStates = getmChildCheckStates(mGroupPosition);
                        boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                        getChecked[mChildPosition] = isChecked;
                        mChildCheckStates.put(mGroupPosition, getChecked);
                        boolean hascheck = hasChecked();

                        LinearLayout LL = (LinearLayout) buttonView.getParent();
                        LL.setBackgroundResource(R.color.transparent);
                        jpd.setLLjlgcbChangeColor(false);

                        jpd.setIsSelected(false);
                        JobsHandler3.processCheckedItemsBeforeSubmitSQLiteNew(jpd, false, GcmPreferences);
                        if (isDelivery) {
                            int position = -1;

                            if (j1 == null) {
                                jobSelectionInfoJsonStr_ = objGlobalMethods.getGrouplistSP_KeyValue(glsp, "jobSelectionInfoJsonStr");
                                if (!objGlobalMethods.IsEmptyString(jobSelectionInfoJsonStr_))
                                    j1 = gson.fromJson(jobSelectionInfoJsonStr_, jobSelectionInfo.class);
                            }

                            if (j1 == null)
                                hascheck = false;

                            if (!hascheck && j1 != null)
                                j1 = null;

                            if (j1 != null && j1.JobsIDX == jpd.ji.JobsIDX) {
                                j1 = null;
                                hascheck =  false;
                            }


                            j2 = jobSelectionInfo.getjobSelectionInfo(jpd.ji, false);

                            if (j1 != null && j1.isValidJobsIDX(jpd.ji.JobsIDX)) {
                                j1.removeSelectedValidJobIDXList(jpd.ji.JobsIDX);
                                j1.removeFieldsFromj2(j2);
                            }

                            if (j1 != null && j1.isInvalidJobsIDX(jpd.ji.JobsIDX))
                                j1.removeSelectedInvalidJobIDXList(jpd.ji.JobsIDX);

                            if (j1 != null && !j1.canMass(j2))
                                j1.setNotAllowedSelectionCount(-1);


                            if (j1 == null) {
                                if (_listDataChild != null && _listDataChild.size() > 0 && _listDataHeader != null && _listDataHeader.size() > 0) {
                                    for (int i = 0; i <= _listDataHeader.size() - 1; i++) {
                                        clist = _listDataChild.get(_listDataHeader.get(i));
                                        boolean getChecked3[] = mChildCheckStates.get(i);
                                        for (int x = 0; x <= clist.size() - 1; x++) {
                                            position = 0;
                                            clist.get(x).setCbEnabledAllowedSelection(true, false, position);
                                            getChecked3[x] = false;
                                        }
                                    }
                                }
                            } else {
                                ////
                                ////
                                boolean j1canMass = j1.canMassWithOthers();
                                if (_listDataChild != null && _listDataChild.size() > 0 && _listDataHeader != null && _listDataHeader.size() > 0) {
                                    for (int i = 0; i <= _listDataHeader.size() - 1; i++) {
                                        clist = _listDataChild.get(_listDataHeader.get(i));
                                        mChildCheckStates = getmChildCheckStates(i);
                                        if (i != mGroupPosition) {
                                            boolean getChecked2[] = mChildCheckStates.get(i);
                                            for (int x = 0; x <= clist.size() - 1; x++) {
                                                if (clist.get(x).ji.JobsIDX != j1.JobsIDX) {
                                                    if (!j1canMass) {
                                                        clist.get(x).setCbEnabledAllowedSelection(true, false, 0);
                                                        j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);

                                                    } else {
                                                        cjpd = clist.get(x);
                                                        cji = cjpd.ji;
                                                        j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                        j2canMass = j1.canMass(j2);

                                                        clist.get(x).setCbEnabledAllowedSelection(true, j2canMass, 0);
                                                        if (!j2canMass)
                                                            j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);


                                                    }
                                                    //getChecked2[x] = false;
                                                }
                                            }

                                        } else {
                                            for (int x = 0; x <= clist.size() - 1; x++) {
                                                if (clist.get(x).ji.JobsIDX != j1.JobsIDX) {
                                                    cjpd = clist.get(x);
                                                    cji = cjpd.ji;
                                                    if (cji.JobsIDX != ji.JobsIDX && !j1canMass) {
                                                        clist.get(x).setCbEnabledAllowedSelection(true, false, 0);
                                                        j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                    } else if (cji.JobsIDX != ji.JobsIDX) {
                                                        j2 = jobSelectionInfo.getjobSelectionInfo(cji, false);
                                                        j2canMass = j1.canMass(j2);

                                                        clist.get(x).setCbEnabledAllowedSelection(true, !(!j1canMass || !j2canMass), 0);
                                                        if (!j2canMass)
                                                            j1.addInvalidJobIDXList(clist.get(x).ji.JobsIDX);
                                                        //getChecked[x] = false;
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                                ////
                                ////
                            }


                            jobSelectionInfoJsonStr = !hascheck ? "" : gson.toJson(j1);

                        } else {
                            jpd.setAllowedSelection(false);
                        }
                    }

                    String mChildCheckStatesPrefStr = (mChildCheckStates != null && mChildCheckStates.size() > 0) ? gson.toJson(mChildCheckStates) : "";
                    String[] glspPName = new String[]{"mChildCheckStatesPrefStr", "jobSelectionInfoJsonStr"};
                    String[] glspPalue = new String[]{mChildCheckStatesPrefStr, jobSelectionInfoJsonStr};
                    objGlobalMethods.setGrouplistSPMass(glsp, glspPName, glspPalue);

                    notifyDataSetChanged();
                    final JobsListGroupActivity act = (JobsListGroupActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            // Notifiy activity
                            act.EnableDisableFailOKPbuttonsNew(j1);
                        }
                    });
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter cbOnCheckedChangeListenerForOKPNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    public static boolean JobCannotMassOKPwOthers(objJobInfo ji) {
        boolean asd = false;
        boolean hasSpecialCOD;
        boolean hasTradeIn;
        boolean hasSecuritySeal;
        boolean hasIsReqStaffID;
        boolean hasReqShipmentVerification;
        boolean hasReqItemProof;
        boolean hasReqNFCscan;
        boolean hasDunning;
        boolean hasOTPwPin;
        boolean IDVerificationReq;
        boolean hasContactless;
        boolean ReceiverOnly;
        try {
            if (ji != null) {
                hasSpecialCOD = JobsHandler.checkforDOCOD(ji);
                //hasSpecialCOD = JobsHandler2.checkforSpecialDOCO(ji);
                hasTradeIn = JobsHandler.hasTradeIn(ji);
                hasSecuritySeal = JobsHandler.JobHasSecuritySeal(ji, false);
                hasIsReqStaffID = objGlobalMethods.isReqStaffID(ji.FLAG);
                hasReqShipmentVerification = JobsHandler.isReqShipmentVerification(ji);
                hasReqItemProof = JobsHandler.hasReqItemProofImg(ji, null, 2);
                hasReqNFCscan = JobsHandler.reqNFCscan(ji);
                hasDunning = JobsHandler.isDunning(ji);
                hasOTPwPin = JobsHandler.hasOTPwPin(ji);
                hasContactless = JobsHandler.isContactlessDelivery(ji);
                IDVerificationReq = JobsHandler.CustIDVerificationRequired(ji) && !JobsHandler.CustIDVerificationNotRequired(ji) && !hasContactless;
                ReceiverOnly = JobsHandler.ReceiverOnly(ji);
                /*asd = (hasSpecialCOD
                        || hasTradeIn
                        || hasSecuritySeal
                        || hasIsReqStaffID
                        || hasReqShipmentVerification
                        || hasReqItemProof
                        || hasReqNFCscan
                        || hasDunning
                        || hasOTPwPin && IDVerificationReq
                        || ReceiverOnly
                );*/

                asd = (
                        //hasSpecialCOD ||
                        hasTradeIn ||
                                hasSecuritySeal ||
                                hasIsReqStaffID ||
                                hasReqShipmentVerification ||
                                hasReqItemProof ||
                                hasReqNFCscan ||
                                hasDunning ||
                                hasOTPwPin && IDVerificationReq ||
                                ReceiverOnly
                );
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter JobCannotMassOKPwOthers. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public static boolean BaseCannotMassOKPwOthers() {
        boolean asd = false;
        try {
            if (criteria == null) {
                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                String objMultiSelectInitialCriteriaJsonStr = objGlobalMethods.getGCMPrefs(gcmp, "objMultiSelectInitialCriteriaJsonStr");
                Gson gson = new Gson();
                criteria = objGlobalMethods.IsEmptyString(objMultiSelectInitialCriteriaJsonStr) ? null :
                        gson.fromJson(objMultiSelectInitialCriteriaJsonStr, objMultiSelectInitialCriteria.class);
            }
            if (criteria != null) {
                /*asd = (criteria.hasSpecialCOD
                        || criteria.hasTradeIn
                        || criteria.hasSecuritySeal
                        || criteria.hasIsReqStaffID
                        || criteria.hasReqShipmentVerification
                        || criteria.hasReqItemProof
                        || criteria.hasReqNFCscan
                        || criteria.hasDunning
                        || criteria.hasOTP && criteria.getIDVerificationRequired()
                        || criteria.getReceiverOnly()
                );*/

                asd = (
                        //criteria.hasSpecialCOD ||
                        criteria.hasTradeIn ||
                                criteria.hasSecuritySeal ||
                                criteria.hasIsReqStaffID ||
                                criteria.hasReqShipmentVerification ||
                                criteria.hasReqItemProof ||
                                criteria.hasReqNFCscan ||
                                criteria.hasDunning ||
                                criteria.hasOTP && criteria.getIDVerificationRequired() ||
                                criteria.getReceiverOnly()
                );

                if (asd) {
                    String jobsidx = String.valueOf(criteria.JobsIDX);
                    objGlobalMethods.writeToFile("JX" + jobsidx + " BaseCannotMassOKPwOthers.", "", "", "", "");
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter BaseCannotMassOKPwOthers. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }

    public boolean hasRejectedCriteria2(objJobInfo cji, int mode) {
        ////mode 0 == screen thru other items, mode 1 == item selected, mode 2 == unselected
        boolean asd = false;
        //this portion is pull parameters for subsequent jobs to c if they can b part of MASS OKP with the 1st selected job
        boolean hasContactless = JobsHandler.isContactlessDelivery(cji);
        String JobContactNumbers = JobsHandler.getValidTelNumsFromJobNew(cji, 2);
        String[] tellist = objGlobalMethods.convertStringToArray(JobContactNumbers, ",");
        boolean hasOTP = JobsHandler.hasOTPwPin(cji);
        boolean CustIDVerificationRequired = JobsHandler.CustIDVerificationRequired(cji);
        int IDVerificationReqFlag = CustIDVerificationRequired ? 128 : 0;
        int IDVerificationNotReqFlag = JobsHandler.CustIDVerificationNotRequired(cji) ? 32 : 0;
        boolean IDVerificationRequired = CustIDVerificationRequired && !JobsHandler.CustIDVerificationNotRequired(cji) && !hasContactless;
        boolean hasCustID = JobsHandler.hasCustID(cji) && CustIDVerificationRequired;
        boolean hasCheckList = JobsHandler.HasCheckList(cji.CheckList, "DS");
        String ContactName = JobsHandler.getContactName(cji, 2);
        boolean ReceiverOnly = JobsHandler.ReceiverOnly(cji);
        boolean HasReviewLinkSMS = JobsHandler.hasReviewLinkSMS(cji);
        boolean ReqSign = (cji.FLAG & 1024) == 1024;
        boolean cjiIsNormalJob = (!hasOTP && !hasCustID && !IDVerificationRequired && !hasCheckList && !hasContactless) ||
                (!hasOTP && hasCustID && !IDVerificationRequired && !hasCheckList && !hasContactless);
        boolean CanSlotLB = objGlobalMethods.isCanSlotLB(cji.FLAG);
        boolean CanUnderDoor = objGlobalMethods.isCanSlotUnderDoor(cji.FLAG);
        boolean CanGuard = objGlobalMethods.isCanGuard(cji.FLAG);
        boolean CanLeaveDoorStep = JobsHandler2.CanLeaveDoorStep(cji.FLAG2);

        //this portion is pull parameters for subsequent jobs to c if they can b part of MASS OKP with the 1st selected job

        boolean sameAccount = (criteria.ClientIDX != null && criteria.ClientIDX == cji.ClientIDX); //NO CROSS ACCOUNTS MASS OKP FOR JOBS THAT REQUIRE ID VERIFICATION
        boolean BaseIDVerificationReq = criteria.getIDVerificationRequired();
        boolean BaseHasContactless = criteria.getHasContactless() && !BaseIDVerificationReq;
        boolean BaseHasChecklist = criteria.HasCheckList();
        boolean BaseHasCustID = criteria.hasCustID;
        boolean BaseHasOTPwPIN = criteria.hasOTP;
        boolean BaseHasCustIDNoOTP = (!BaseHasOTPwPIN && BaseHasCustID && !BaseHasContactless);
        boolean hasSameChecklist = JobsHandler.hasSameChecklist(criteria.CheckList, criteria.ChecklistContactless, criteria.ChecklistIDVerification, cji.CheckList);
        boolean hasSameCustID = objGlobalMethods.IsEqualString(criteria.CustID, cji.IDVerification);
        boolean sameContactName = objGlobalMethods.IsEqualString(criteria.ContactName, ContactName);
        boolean ContactNumberExistInCriteria = criteria.ContactNumberExistInCriteria(tellist);
        boolean BaseIsNormalJob = (!BaseHasOTPwPIN && !BaseHasCustID && !BaseIDVerificationReq && !BaseHasChecklist && !BaseHasContactless);

        try {
            //criteria here is a static variable that will be nullified at certain pages
            //criteria here is based on the 1st DIP job that user selected
            if (criteria == null) {
                if (GcmPreferences == null) {
                    GcmPreferences = objGlobalMethods.getGcmPreferences();
                }
                String objMultiSelectInitialCriteriaJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "objMultiSelectInitialCriteriaJsonStr");
                Gson gson = new Gson();
                criteria = objGlobalMethods.IsEmptyString(objMultiSelectInitialCriteriaJsonStr) ? null :
                        gson.fromJson(objMultiSelectInitialCriteriaJsonStr, objMultiSelectInitialCriteria.class);
            }

            if (criteria == null) {
                asd = true;
            } else if (BaseIsNormalJob && cjiIsNormalJob) {
                //do nothing
            } else if (!BaseIsNormalJob && !cjiIsNormalJob && sameAccount && !hasSameChecklist) {
                asd = true;
            } else if (!BaseIsNormalJob && !cjiIsNormalJob && sameAccount && hasSameChecklist) {
                if (BaseHasContactless) {
                    if (!sameContactName && !ContactNumberExistInCriteria) {
                        asd = true;
                    }
                    if (mode == 1 && !asd) {
                        if (hasContactless) {
                            criteria.setContactlessCount(1);
                            criteria.addJobIDXsForContactlessChecklist(cji.JobsIDX);
                            criteria.addJobNosForContactlessChecklist(cji.JobNo);
                        }
                        if (!BaseHasCustID && hasCustID) {
                            criteria.setCustID(cji.IDVerification);
                        }
                        if (IDVerificationRequired && !BaseIDVerificationReq) {
                            criteria.setIDVerificationReqCountToDetermineChecklist(1);
                            criteria.addJobIDXsForVerificationChecklist(cji.JobsIDX);
                            criteria.addJobNosForVerificationChecklist(cji.JobNo);
                            if (criteria.ChecklistIDVerification == null) {
                                criteria.setChecklistIDVerification(cji.CheckList);
                                criteria.setContactName(cji.DLLocation != null && cji.DLLocation.Contacts != null && cji.DLLocation.Contacts.length > 0 ? cji.DLLocation.Contacts[0].NAME : "");
                            }
                        }
                    } else if (mode == 2 && !asd) {
                        if (hasContactless) {
                            criteria.setContactlessCount(-1);
                            criteria.removeJobIDXsForContactlessChecklist(cji.JobsIDX);
                            criteria.removeJobNosForContactlessChecklist(cji.JobNo);
                        }
                        if (IDVerificationRequired && hasCheckList) {
                            criteria.setIDVerificationReqCountToDetermineChecklist(-1);
                            criteria.removeJobIDXsForVerificationChecklist(cji.JobsIDX);
                            criteria.removeJobNosForVerificationChecklist(cji.JobNo);
                        }
                    }
                } else if (BaseHasCustIDNoOTP) {
                    //if ((hasContactless) || (!hasSameCustID) || (hasSameCustID && !sameContactName) || (hasSameCustID && sameContactName && !ContactNumberExistInCriteria)) {
                    if ((hasContactless) || (!hasSameCustID) || (hasSameCustID && !sameContactName)) {
                        asd = true;
                    }

                    if (mode == 1 && !asd) {
                        if ((IDVerificationRequired && !BaseIDVerificationReq) || (IDVerificationRequired && BaseIDVerificationReq)) {
                            criteria.setIDVerificationReqCountToDetermineChecklist(1);
                            criteria.addJobIDXsForVerificationChecklist(cji.JobsIDX);
                            criteria.addJobNosForVerificationChecklist(cji.JobNo);
                            if (criteria.ChecklistIDVerification == null) {
                                criteria.setChecklistIDVerification(cji.CheckList);
                                criteria.setContactName(cji.DLLocation != null && cji.DLLocation.Contacts != null && cji.DLLocation.Contacts.length > 0 ? cji.DLLocation.Contacts[0].NAME : "");
                            }
                        }
                    } else if (mode == 2 && !asd) {
                        if (IDVerificationRequired && hasCheckList) {
                            criteria.setIDVerificationReqCountToDetermineChecklist(-1);
                            criteria.removeJobIDXsForVerificationChecklist(cji.JobsIDX);
                            criteria.removeJobNosForVerificationChecklist(cji.JobNo);
                        }
                    }
                } else if (!hasCustID && !hasOTP && !ContactNumberExistInCriteria) {
                    asd = true;
                } else if (!BaseHasCustID && hasCustID && IDVerificationRequired) {
                    asd = true;

                }
            } else if (!BaseIsNormalJob && !cjiIsNormalJob && !sameAccount) {
                //non-nml Base & non-nml job of diff ClientIDX cannot mass OKP tgt
                asd = true;
            } else if ((BaseIsNormalJob && !cjiIsNormalJob) || (!BaseIsNormalJob && cjiIsNormalJob)) {
                //nml n non-nml jobs cannot mass OKP tgt
                asd = true;
            }


            if (!asd && mode > 0) {
                if (CanSlotLB) {
                    criteria.setCanSlotLBCount(mode == 1 ? 1 : -1);
                }
                if (CanUnderDoor) {
                    criteria.setCanSlotUnderDoorCount(mode == 1 ? 1 : -1);
                }
                if (CanGuard) {
                    criteria.setCanGuardCount(mode == 1 ? 1 : -1);
                }
                if (CanLeaveDoorStep) {
                    criteria.setCanLeaveDoorStepCount(mode == 1 ? 1 : -1);
                }
                if (ReceiverOnly) {
                    criteria.setReceiverOnlyCount(mode == 1 ? 1 : -1);
                }
                if (ReqSign) {
                    criteria.setReqSignCount(mode == 1 ? 1 : -1);
                }
                if (HasReviewLinkSMS) {
                    criteria.setReviewLinkSMSCount(mode == 1 ? 1 : -1);
                }

                criteria.setIDVerificationReqFlag(criteria.IDVerificationReqFlag > 0 ? 128 : IDVerificationReqFlag);
                criteria.setIDVerificationNotReqFlag(criteria.IDVerificationNotReqFlag > 0 ? 32 : IDVerificationNotReqFlag);

                if (mode == 1) {
                    criteria.addJobContactNumbersToList(tellist);
                } else {
                    criteria.removeJobContactNumbersFromList(tellist);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at checklist_PostItemAdapter hasRejectedCriteria2. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return asd;
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

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        try {
            GroupViewHolder gvh;
            String headerTitle = (String) getGroup(groupPosition);
            int chldrenCount = getChildrenCount(groupPosition);
            final int grpPos = groupPosition;

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.jobs_list_group, null);
                gvh = new GroupViewHolder();
                gvh.lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
                gvh.lblListHeaderCount = (TextView) convertView.findViewById(R.id.lblListHeaderCount);
                gvh.LLjobs_list_group_ihavearrive = (LinearLayout) convertView.findViewById(R.id.LLjobs_list_group_ihavearrive);
                gvh.LLarrival = (LinearLayout) convertView.findViewById(R.id.LLarrival);
                gvh.img_i_have_arrive = (ImageView) convertView.findViewById(R.id.img_i_have_arrive);
                gvh.lblarrival = (TextView) convertView.findViewById(R.id.lblarrival);
                convertView.setTag(gvh);
            } else {
                gvh = (GroupViewHolder) convertView.getTag();
            }

            gvh.lblListHeader.setText(headerTitle);
            gvh.lblListHeaderCount.setText(String.valueOf(chldrenCount));
            if (GcmPreferences == null) {
                GcmPreferences = objGlobalMethods.getGcmPreferences();
            }
            String enableJobUpdatesPref = objGlobalMethods.getGCMPrefs(GcmPreferences, "enableJobUpdates");
            boolean enableJobUpdates = enableJobUpdatesPref.equals("") || enableJobUpdatesPref.equals("true") ? true : false;

            gvh.img_i_have_arrive.setVisibility((CatTypeID != 0 && CatTypeID != 2) || (!enableJobUpdates) ? View.GONE : View.VISIBLE);

            if (CatTypeID == 0 || CatTypeID == 2) {
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
                //gvh.LLjobs_list_group_ihavearrive.setOnClickListener((CatTypeID != 0 && CatTypeID != 2) || (!enableJobUpdates) ? null : rtnIhaveArriveClickListener2(gvhh, CatTypeID, grpPos, _listDataHeader, _listDataChild));
                gvh.LLjobs_list_group_ihavearrive.setOnClickListener((CatTypeID != 0 && CatTypeID != 2) || (!enableJobUpdates) ? null : rtnIhaveArriveClickListenerMass(gvhh, CatTypeID, grpPos, _listDataHeader, _listDataChild));
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNew_PostItemAdapter getGroupView. Exception: "
                    + e.getMessage().toString(), "", "", "", "");
        } finally {
            return convertView;
        }
    }

    private View.OnClickListener rtnIhaveArriveClickListenerMass(final GroupViewHolder gvh, final int cattypeid, final int grpPos, final List<String> _listDataHeader,
                                                                 final HashMap<String, List<JobListGroupSortedSQLite_PostData>> _listDataChild) {
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
                                        JobsHandler.PromptDistanceViolationUponUpdate(_listDataChild.get(_listDataHeader.get(grpPos)).get(0).ji, cattypeid, cattypeid == 0 ? 1 : 4) : false;

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
                                long AStatusCodeIDX = 0;

                                if (cattypeid == 0) {
                                    AStatusCodeIDX = 368;
                                } else if (cattypeid == 2) {
                                    AStatusCodeIDX = 369;
                                }

                                if (AStatusCodeIDX > 0) {
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
                                                SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                                                objCheckedItems[] objCheckedItemsAL = null;
                                                String objCheckedItemsJsonStr = objGlobalMethods.getGCMPrefs(gcmp, objGlobalVariables.checkedItemsName);
                                                if (!objCheckedItemsJsonStr.equals("")) {
                                                    objCheckedItemsAL = gson.fromJson(objCheckedItemsJsonStr, objCheckedItems[].class);
                                                }
                                                String jobsidxs = "";
                                                for (int i = 0; i <= _listDataChild.get(_listDataHeader.get(grpPos)).size() - 1; i++) {
                                                    View cv = getChildView(grpPos, i, false, null, null);
                                                    ChildViewHolder cvh = (ChildViewHolder) cv.getTag();
                                                    Thread.sleep(200);
                                                    handle.sendMessage(handle.obtainMessage());

                                                    JobsIDXstr = _listDataChild.get(_listDataHeader.get(grpPos)).get(i).JobsIDX == null ? "" : _listDataChild.get(_listDataHeader.get(grpPos)).get(i).JobsIDX;
                                                    JobsIDX = Long.parseLong(JobsIDXstr == "" ? "0" : JobsIDXstr);
                                                    if (JobsIDX > 0) {
                                                        if (!JobsIDXList.contains(JobsIDX))
                                                            JobsIDXList.add(JobsIDX);

                                                        /*JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, regid, JobsIDX, finalAStatusCodeIDX,
                                                                timestampstr, "", "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(),
                                                                Accuracy, "");*/

                                                        _listDataChild.get(_listDataHeader.get(grpPos)).get(i).ji.setI_have_arrived_timestamp(l);
                                                        String hfJobInfoJsonStr = gson.toJson(_listDataChild.get(_listDataHeader.get(grpPos)).get(i).ji);
                                                        hfJobInfoJsonStr = hfJobInfoJsonStr == "[]" || hfJobInfoJsonStr == "" ? "" : hfJobInfoJsonStr;
                                                        _listDataChild.get(_listDataHeader.get(grpPos)).get(i).setJobInfoJsonStr(hfJobInfoJsonStr);
                                                        cvh.lblhfJobsInfoJsonStr.setText(hfJobInfoJsonStr);
                                                        DAO_Jobs.updateJobInfoPrefStr(JobsIDX, hfJobInfoJsonStr);
                                                        /*objGlobalMethods.writeToFile("Mass update job status.", "JobsIDX: " +
                                                                String.valueOf(JobsIDX), "Submitted StatusCodeIDX: " + String.valueOf(finalAStatusCodeIDX), "", "");*/

                                                        /*if (objGlobalMethods.IsEmptyString(jobsidxs)) {
                                                            jobsidxs += String.valueOf(JobsIDX);
                                                        } else {
                                                            jobsidxs += "," + String.valueOf(JobsIDX);
                                                        }*/

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

                                                /*if (!objGlobalMethods.IsEmptyString(jobsidxs)) {
                                                    objGlobalMethods.writeToFile("Mass update job status.", "JobsIDX: " +
                                                            jobsidxs, "Submitted StatusCodeIDX: " + String.valueOf(finalAStatusCodeIDX), "", "");
                                                }*/

                                                if (JobsIDXList != null && JobsIDXList.size() > 0) {
                                                    jobsidxs = Arrays.toString(JobsIDXList.toArray());
                                                    objGlobalMethods.writeToFile("Mass update job status.", "JobsIDX: " +
                                                            jobsidxs, "Submitted StatusCodeIDX: " + String.valueOf(finalAStatusCodeIDX), "", "");

                                                    JobsIDXsArr = JobsIDXList.toArray(new Long[JobsIDXList.size()]);
                                                    JobsHandler.addUpdateJobInfoStatusWithGPSMassQueALPrefs(UpdateJobInfoPreferences, regid, JobsIDXsArr, finalAStatusCodeIDX,
                                                            timestampstr, "", "", lastknownlocation.getLatitude(), lastknownlocation.getLongitude(),
                                                            Accuracy, "");

                                                }

                                                if (objCheckedItemsAL != null && objCheckedItemsAL.length > 0) {
                                                    objCheckedItemsJsonStr = gson.toJson(objCheckedItemsAL);
                                                    objGlobalMethods.setGCMPrefs(gcmp, objGlobalVariables.checkedItemsName, objCheckedItemsJsonStr);
                                                }

                                                progressDoalog.dismiss();
                                                progressDoalog = null;

                                                if (c > 0) {
                                                    /*if (SettingsDistVioEnabled && PromptDistanceViolationUponUpdate && _listDataChild.get(_listDataHeader.get(grpPos)).get(0).ji != null) {
                                                        objGlobalMethods.sendLukeDVmsgEmail(_listDataChild.get(_listDataHeader.get(grpPos)).get(0).ji, cattypeid);
                                                    }*/
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
                                                objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNew_PostItemAdapter rtnIhaveArriveClickListener thread. Exception: "
                                                        + e.getMessage().toString(), "", "", "", "");
                                                objGlobalMethods.showDialog(myApp.currentActivity, "OK", ctx.getString(R.string.alert_ihavearrive_manual)).show();
                                            }
                                        }
                                    };
                                    t.start();

                                } else {
                                    objGlobalMethods.showDialog(myApp.currentActivity, "OK", ctx.getString(R.string.alert_ihavearrive_manual)).show();
                                    dialog.cancel();
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
                    } else {
                        objGlobalMethods.showDialog(myApp.currentActivity, "OK", ctx.getString(R.string.alert_ihavearrive_manual)).show();
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNew_PostItemAdapter rtnIhaveArriveClickListenerMass. Exception: "
                            + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }


    public void manageBlinkEffect(View v, boolean start) {
        try {
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500); //You can manage the time of the blink with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            if (start) {
                v.startAnimation(anim);
            } else {
                v.clearAnimation(); // cancel blink animation
                v.setAlpha(1.0f); // restore original alpha
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNew_PostItemAdapter manageBlinkEffect. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public final class GroupViewHolder {
        TextView lblListHeader;
        TextView lblListHeaderCount;
        LinearLayout LLjobs_list_group_ihavearrive;
        LinearLayout LLarrival;
        ImageView img_i_have_arrive;
        TextView lblarrival;
    }

    public final class ChildViewHolder {
        TextView lblExpType;
        TextView lblCompany;
        TextView lblContactname;
        TextView lblDatetime;
        TextView lblhfJobsInfoJsonStr;
        TextView lblCnno;
        TextView lblPcs;
        TextView lblPart;
        TextView lblJobsIDX;
        TextView lblLunchtime;
        CheckBox cb;
        //ImageView imgL;
        //ImageView imgV;
        //ImageView imgS;
        //ImageView imgDOCOD;
        //ImageView JobsListGroupNetsVisaimg;
        //ImageView JobsListGroupCannotTakePhotoimg;
        //ImageView AddressList2wayimg;
        //ImageView JobsListGroupLetterboximg;
        //ImageView JobsListGroupUnderDoorimg;
        //ImageView JobsListGroupGuardimg;
        //ImageView JobsListGroupRecOnlyimg;
        //ImageView imgAdhoc;
        //ImageView imgExclam;

        View.OnClickListener imgL_OnClickListener;
        View.OnClickListener imgV_OnClickListener;
        View.OnClickListener imgS_OnClickListener;
        View.OnClickListener imgDOCOD_OnClickListener;
        //View.OnClickListener JobsListGroupCannotTakePhotoimg_OnClickListener;
        //View.OnClickListener AddressList2wayimg_OnClickListener;
        CompoundButton.OnCheckedChangeListener cbOnCheckedChangeListener;
        LinearLayout LLjlgcb;
        LinearLayout LLjlinfo;
        LinearLayout linearLayout1;
        LinearLayout LL_ExpTypeRow;


        TextView lblAddressListLockimg;
        TextView lblAddressListDNFimg;
        TextView lblAddressList2wayimg;
        TextView lblAddressListDOCODimg;
        TextView lblAddressListccimg;
        TextView lblAddressListLetterboximg;
        TextView lblAddressListUnderDoorimg;
        TextView lblAddressListGuardimg;
        TextView lblAddressListDoorstepimg;
        TextView lblAddressListContactlessimg;
        TextView lblAddressListcoldimg;

    }

    private SharedPreferences getUpdateJobInfoPreferences(Context context) {
        return context.getSharedPreferences("UpdateJobInfoPrefs",
                Context.MODE_PRIVATE);
    }

    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void manageBlinkEffectNew(final LinearLayout ll) {
        try {
            ObjectAnimator anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.WHITE, Color.GREEN, Color.WHITE);

            anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.GREEN, R.color.White, Color.GREEN);
            anim.setDuration(300);
            anim.setEvaluator(new ArgbEvaluator());
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.setRepeatCount(1);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ll.setBackgroundResource(R.drawable.list_selector);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNew_PostItemAdapter manageBlinkEffectNew. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            setAllItemsBlinkFalse();
        }
    }

    private void setAllItemsBlinkFalse() {
        try {
            List<String> DataHeader;
            HashMap<String, List<JobListGroupSortedSQLite_PostData>> DataChild2;
            DataHeader = _listDataHeader;
            DataChild2 = _listDataChild;
            Iterator<HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>>> itr = null;
            itr = DataChild2.entrySet().iterator();
            List<JobListGroupSortedSQLite_PostData> jpdlist = null;
            JobListGroupSortedSQLite_PostData jpd = null;
            String key = "";
            String groupName = "";
            int groupIDX = 0;
            int index = -1;

            outer:
            while (itr.hasNext()) {
                jpd = null;
                HashMap.Entry<String, List<JobListGroupSortedSQLite_PostData>> entry = itr.next();
                jpdlist = entry.getValue();
                for (int x = 0; x <= jpdlist.size() - 1; x++) {
                    jpd = jpdlist.get(x);
                    jpd.setBlink(false);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobListGroupSortedSQLiteNew_PostItemAdapter setAllItemsBlinkFalse. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            notifyDataSetChanged();
        }
    }

}
