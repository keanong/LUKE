package com.xdel.luke.HRM_GC_Schedule;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class GCscheduleExpandable_Adapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private Context _context;
    public List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    public HashMap<String, List<GCschedule_PostData>> _listDataChild;

    public HashMap<Integer, boolean[]> mChildCheckStates;
    public HashMap<Integer, boolean[]> mChildColorStates;
    private ChildViewHolder cvh;
    private SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
    private DecimalFormat dec = new DecimalFormat("#.00");

    public GCscheduleExpandable_Adapter(Context context, List<String> listDataHeader, HashMap<String, List<GCschedule_PostData>> listChildData) {
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
            final GCschedule_PostData d = (GCschedule_PostData) getChild(groupPosition, childPosition);
            final int mGroupPosition = groupPosition;
            final int mChildPosition = childPosition;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.gc_schedule_row, null);
                cvh = new ChildViewHolder();
                cvh.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
                cvh.lblDate = (TextView) convertView.findViewById(R.id.lblDate);
                cvh.lblFromTo = (TextView) convertView.findViewById(R.id.lblFromTo);
                convertView.setTag(cvh);
            } else {
                cvh = (ChildViewHolder) convertView.getTag();
            }

            if (parent != null) {
                cvh.linearLayout1.setBackgroundResource(objGlobalMethods.isEven(childPosition) ? R.color.colorPrimaryLight : R.color.White);
                cvh.lblDate.setText(d.DateString + " - " + d.Remarks);
                cvh.lblFromTo.setText(d.FromTo);
                if (d.isToday) {
                    cvh.linearLayout1.setBackgroundResource(R.color.brightergreen);
                }
            }
            convertView.setOnClickListener(null);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at GCscheduleExpandable_Adapter getChildView. Exception: " + e.getMessage(), "", "", "", "");
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
                convertView = infalInflater.inflate(R.layout.gc_schedule_row_header, null);
                gvh = new GroupViewHolder();
                gvh.lblHeaderTitle = (TextView) convertView.findViewById(R.id.lblHeaderTitle);
                convertView.setTag(gvh);
            } else {
                gvh = (GroupViewHolder) convertView.getTag();
            }

            gvh.lblHeaderTitle.setText(headerTitle);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at GCscheduleExpandable_Adapter getGroupView. Exception: " + e.getMessage().toString(), "", "", "", "");
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
        TextView lblHeaderTitle;
    }

    public final class ChildViewHolder {
        LinearLayout linearLayout1;
        TextView lblDate;
        TextView lblFromTo;
    }



}
