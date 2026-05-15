package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.xdel.luke.MainActivity;
import com.xdel.luke.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kean.ong on 10/02/2017.
 */
public class JobInfo_AddressesGroup_PostItemAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private Context _context;
    public List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    public HashMap<String, List<JobInfo_Addresses_PostData>> _listDataChild;

    public HashMap<Integer, boolean[]> mChildCheckStates;
    private ChildViewHolder childViewHolder;

    public JobInfo_AddressesGroup_PostItemAdapter(Context context, List<String> listDataHeader, HashMap<String, List<JobInfo_Addresses_PostData>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        mChildCheckStates = new HashMap<Integer, boolean[]>();
    }

    public void setInflater(LayoutInflater inflater, Activity activity){
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

        //final String childText = (String) getChild(groupPosition, childPosition);
        final JobInfo_Addresses_PostData tempChild = (JobInfo_Addresses_PostData) getChild(groupPosition, childPosition);
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        if (convertView == null) {
            /*LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.jobs_list_item, null);*/
            convertView = inflater.inflate(R.layout.jobinfo_address_info_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.lbljobinfoaddressinfoitemPUDIPcoy = (TextView) convertView.findViewById(R.id.lbljobinfoaddressinfoitemPUDIPcoy);
            childViewHolder.lbljobinfoaddressinfoitemPUDIPaddress = (TextView) convertView.findViewById(R.id.lbljobinfoaddressinfoitemPUDIPaddress);
            childViewHolder.lbljobinfoaddressinfoitemPUDIPcontact = (TextView) convertView.findViewById(R.id.lbljobinfoaddressinfoitemPUDIPcontact);
            childViewHolder.lbljobinfoaddressinfoitemPUDIPdept = (TextView) convertView.findViewById(R.id.lbljobinfoaddressinfoitemPUDIPdept);
            childViewHolder.lbljobinfoaddressinfoitemPUDIPtel = (TextView) convertView.findViewById(R.id.lbljobinfoaddressinfoitemPUDIPtel);
            childViewHolder.lbljobinfoaddressinfoitemPUDIPothers = (TextView) convertView.findViewById(R.id.lbljobinfoaddressinfoitemPUDIPothers);
            childViewHolder.lbljobinfoaddressinfoitemPUDIPmobile = (TextView) convertView.findViewById(R.id.lbljobinfoaddressinfoitemPUDIPmobile);
            childViewHolder.lbljobinfoaddressinfoitemPUDIPonbehalf = (TextView) convertView.findViewById(R.id.lbljobinfoaddressinfoitemPUDIPonbehalf);

            convertView.setTag(childViewHolder);
        }
        else{
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        childViewHolder.lbljobinfoaddressinfoitemPUDIPcoy.setText(tempChild.CompanyName);
        childViewHolder.lbljobinfoaddressinfoitemPUDIPaddress.setText(tempChild.Address);
        childViewHolder.lbljobinfoaddressinfoitemPUDIPcontact.setText(tempChild.ContactName);
        childViewHolder.lbljobinfoaddressinfoitemPUDIPdept.setText(tempChild.DeptName);
        childViewHolder.lbljobinfoaddressinfoitemPUDIPtel.setText(tempChild.Tel);
        childViewHolder.lbljobinfoaddressinfoitemPUDIPothers.setText(tempChild.Other);
        childViewHolder.lbljobinfoaddressinfoitemPUDIPmobile.setText(tempChild.Mobile);
        childViewHolder.lbljobinfoaddressinfoitemPUDIPonbehalf.setText(tempChild.OnHalf);

        convertView.clearFocus();

        return convertView;
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
        String headerTitle = (String) getGroup(groupPosition);
        int chldrenCount = getChildrenCount(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.jobinfo_address_info_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public final class ChildViewHolder{
        TextView lbljobinfoaddressinfoitemPUDIPcoy;
        TextView lbljobinfoaddressinfoitemPUDIPaddress;
        TextView lbljobinfoaddressinfoitemPUDIPcontact;
        TextView lbljobinfoaddressinfoitemPUDIPdept;
        TextView lbljobinfoaddressinfoitemPUDIPtel;
        TextView lbljobinfoaddressinfoitemPUDIPothers;
        TextView lbljobinfoaddressinfoitemPUDIPmobile;
        TextView lbljobinfoaddressinfoitemPUDIPonbehalf;
    }
}
