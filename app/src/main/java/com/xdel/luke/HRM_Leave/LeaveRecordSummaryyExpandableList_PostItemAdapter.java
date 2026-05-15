package com.xdel.luke.HRM_Leave;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.HashMap;
import java.util.List;

public class LeaveRecordSummaryyExpandableList_PostItemAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private Context ctx;
    public List<String> _listDataHeader; // header titles
    public HashMap<String, List<objLeaveRecord>> _listDataChild;
    public HashMap<Integer, boolean[]> mChildCheckStates;
    public HashMap<Integer, boolean[]> mChildColorStates;
    private ChildViewHolder cvh;

    public LeaveRecordSummaryyExpandableList_PostItemAdapter(Context context, List<String> listDataHeader, HashMap<String, List<objLeaveRecord>> listChildData){
        this.ctx = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        mChildCheckStates = new HashMap<Integer, boolean[]>();
        mChildColorStates = new HashMap<Integer, boolean[]>();
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }

    public final class GroupViewHolder {
        TextView lblLeaveTypeDesc;
        TextView lblunits;
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
                LayoutInflater infalInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.leavesummary_row_header, null);
                gvh = new GroupViewHolder();
                gvh.lblLeaveTypeDesc  = (TextView) convertView.findViewById(R.id.lblLeaveTypeDesc);
                convertView.setTag(gvh);
            } else {
                gvh = (GroupViewHolder) convertView.getTag();
            }

            gvh.lblLeaveTypeDesc.setText(headerTitle);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveRecordSummaryyExpandableList_PostItemAdapter getGroupView. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return convertView;
        }
    }

    public final class ChildViewHolder {
        TextView lbldate;
        TextView lblstatus;
        TextView lblremarks;

    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        try {
            final objLeaveRecord data = (objLeaveRecord) getChild(groupPosition, childPosition);
            final int mGroupPosition = groupPosition;
            final int mChildPosition = childPosition;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.leavesummary_row, null);
                cvh = new ChildViewHolder();
                cvh.lbldate = (TextView) convertView.findViewById(R.id.lbldate);
                cvh.lblstatus = (TextView) convertView.findViewById(R.id.lblstatus);
                cvh.lblremarks = (TextView) convertView.findViewById(R.id.lblremarks);
                convertView.setTag(cvh);
            } else {
                cvh = (ChildViewHolder) convertView.getTag();
            }

            if (parent != null) {
                cvh.lbldate.setText(data.LeaveFromDateFormatStr + " " + ctx.getString(R.string.pointsummarydateto) + " " + data.getLeaveToDateFormatStr());
                String status = data.getStatus() == objStatus.Status.stSubmitted.ordinal() ? ctx.getString(R.string.submitted) :
                        data.getStatus() == objStatus.Status.stPending.ordinal() ? ctx.getString(R.string.pending) :
                                data.getStatus() == objStatus.Status.stApproved.ordinal() ? ctx.getString(R.string.approved) :
                                        data.getStatus() == objStatus.Status.stRejected.ordinal() ? ctx.getString(R.string.rejected) :
                                                data.getStatus() == objStatus.Status.stExceeded.ordinal() ? ctx.getString(R.string.rejected_exceeded) :
                                                "";
                String uom = " " + String.valueOf(data.getUnits()) + " " + (data.getUOM() == objUOM.UOM.umDays.ordinal() ? ctx.getString(R.string.days) : ctx.getString(R.string.hours));
                String contact_sup = data.getUnits() == 0 ? "\n" + ctx.getString(R.string.contact_sup) : "";
                String fullstatus = status + uom + contact_sup;
                cvh.lblstatus.setText(fullstatus);
                cvh.lblremarks.setText(data.getRemarks());
            }

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at LeaveRecordSummaryyExpandableList_PostItemAdapter getChildView. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            convertView.clearFocus();
            return convertView;
        }
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}

