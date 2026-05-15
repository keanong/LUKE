package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.DriverPointMain_Activity;
import com.xdel.luke.DriverPointSummaryActivity;
import com.xdel.luke.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DriverPointSummaryExpandableList_PostItemAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private Context _context;
    public List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    public HashMap<String, List<DriverPointSummary_PostData>> _listDataChild;

    public HashMap<Integer, boolean[]> mChildCheckStates;
    public HashMap<Integer, boolean[]> mChildColorStates;
    private ChildViewHolder cvh;
    private SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
    private DecimalFormat dec = new DecimalFormat("#.00");

    public DriverPointSummaryExpandableList_PostItemAdapter(Context context, List<String> listDataHeader, HashMap<String, List<DriverPointSummary_PostData>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        mChildCheckStates = new HashMap<Integer, boolean[]>();
        mChildColorStates = new HashMap<Integer, boolean[]>();
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
        try {
            final DriverPointSummary_PostData tempChild = (DriverPointSummary_PostData) getChild(groupPosition, childPosition);
            final int mGroupPosition = groupPosition;
            final int mChildPosition = childPosition;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.driver_point_summary_row, null);
                cvh = new ChildViewHolder();
                cvh.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
                cvh.lblAttendance = (TextView) convertView.findViewById(R.id.lblAttendance);
                cvh.lbldriverpointdetail = (TextView) convertView.findViewById(R.id.lbldriverpointdetail);
                cvh.lblDateOfWork = (TextView) convertView.findViewById(R.id.lblDateOfWork);
                cvh.lblEarlyUpdateCountVal = (TextView) convertView.findViewById(R.id.lblEarlyUpdateCountVal);
                cvh.lblLateUpdateCountVal = (TextView) convertView.findViewById(R.id.lblLateUpdateCountVal);
                cvh.lblTotal_Distance = convertView.findViewById(R.id.lblTotal_Distance);
                cvh.ibtnEnter = (ImageView) convertView.findViewById(R.id.ibtnEnter);
                convertView.setTag(cvh);
            } else {
                cvh = (ChildViewHolder) convertView.getTag();
            }

            if (parent != null) {
                cvh.linearLayout1.setBackgroundResource(objGlobalMethods.isEven(childPosition) ? R.color.colorPrimaryLight : R.color.White);
                cvh.lblAttendance.setText(tempChild.DateString + " - " + tempChild.Attendance);
                Double p = tempChild.Points.doubleValue();
                Double hp = tempChild.Half_Points.doubleValue();
                if (hp > 0)
                    hp = hp / 2;
                p = p - hp;
                //String detail = String.valueOf(tempChild.Points) + " " + _context.getString(R.string.point) + " - " + String.valueOf(tempChild.Actual_Jobs) + " Job(s)";
                String detail = String.valueOf(p) + " " + _context.getString(R.string.point) + " - " + String.valueOf(tempChild.Actual_Jobs) + " Job(s)";
                cvh.lbldriverpointdetail.setText(detail);
                cvh.lblDateOfWork.setText(tempChild.DateString);
                cvh.lblEarlyUpdateCountVal.setText(String.valueOf(tempChild.UpdateEarlyCount));
                cvh.lblLateUpdateCountVal.setText(String.valueOf(tempChild.UpdateLateCount));
                cvh.ibtnEnter.setVisibility(tempChild.Points > 0 ? View.VISIBLE : View.GONE);
                double dist = tempChild.Total_Distance;
                /*cvh.lblTotal_Distance.setText(
                        (!objGlobalMethods.IsEqualString(tempChild.Today_ddMMyyyy, tempChild.DateString) ?
                        _context.getString(R.string.performancetxt05) : _context.getString(R.string.performancetxt06))
                                + " - " + (dist == 0.0 ? "0.00" : dec.format(dist)) + _context.getString(R.string.performancetxt04));*/
                cvh.lblTotal_Distance.setText(
                        (!objGlobalMethods.IsEqualString(tempChild.Today_ddMMyyyy, tempChild.DateString) ?
                                _context.getString(R.string.performancetxt05) : _context.getString(R.string.performancetxt06))
                                + " - " + (dist == 0.0 ? "0.00" : dec.format(dist)) + " " + _context.getString(R.string.performancetxt04));
            }

            if (tempChild.Actual_Jobs != null && tempChild.Actual_Jobs > 0) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, DriverPointMain_Activity.class);
                        TextView lblDateOfWork = (TextView) v.findViewById(R.id.lblDateOfWork);
                        String Date_Of_Work = lblDateOfWork.getText().toString();
                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                        try {
                            Date dt = formatter2.parse(Date_Of_Work);
                            Long l = dt.getTime();
                            objGlobalMethods.setGCMPrefs(GcmPreferences,"currentpointdatelong",String.valueOf(l));
                        } catch (ParseException e) {
                            objGlobalMethods.setGCMPrefs(GcmPreferences,"currentpointdatelong","");
                        }

                        //_context.startActivity(intent);
                        myApp.currentActivity.startActivity(intent);
                        myApp.currentActivity.finish();
                    }
                });
            } else {
                convertView.setOnClickListener(null);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryExpandableList_PostItemAdapter getChildView. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            convertView.clearFocus();
            return convertView;
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
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.driverpointsummary_row_header, null);
                gvh = new GroupViewHolder();
                gvh.lblDriverPointSummaryDateFrom = (TextView) convertView.findViewById(R.id.lblDriverPointSummaryDateFrom);
                //gvh.imgExpand = (ImageView) convertView.findViewById(R.id.imgExpand);
                convertView.setTag(gvh);
            } else {
                gvh = (GroupViewHolder) convertView.getTag();
            }

            gvh.lblDriverPointSummaryDateFrom.setText(headerTitle);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at DriverPointSummaryExpandableList_PostItemAdapter getGroupView. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return convertView;
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
        TextView lblDriverPointSummaryDateFrom;
        ImageView imgExpand;
    }

    public final class ChildViewHolder {
        LinearLayout linearLayout1;
        TextView lblAttendance;
        TextView lbldriverpointdetail;
        TextView lblDateOfWork;
        TextView lblEarlyUpdateCountVal;
        TextView lblLateUpdateCountVal;
        TextView lblTotal_Distance;
        ImageView ibtnEnter;
    }


}
