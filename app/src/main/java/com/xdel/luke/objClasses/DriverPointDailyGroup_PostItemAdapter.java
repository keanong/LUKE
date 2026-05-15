package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.MainActivity;
import com.xdel.luke.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kean.ong on 03/04/2017.
 */
public class DriverPointDailyGroup_PostItemAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private Context _context;
    public List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    public HashMap<String, List<DriverPointDailyGroup_PostData>> _listDataChild;

    public HashMap<Integer, boolean[]> mChildCheckStates;
    private ChildViewHolder childViewHolder;

    public DriverPointDailyGroup_PostItemAdapter(Context context, List<String> listDataHeader, HashMap<String, List<DriverPointDailyGroup_PostData>> listChildData) {
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
        final DriverPointDailyGroup_PostData tempChild = (DriverPointDailyGroup_PostData) getChild(groupPosition, childPosition);
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.driver_point_row, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.lbldriverpointjobno = (TextView) convertView.findViewById(R.id.lbldriverpointjobno);
            childViewHolder.lbldriverpointtype = (TextView) convertView.findViewById(R.id.lbldriverpointtype);
            childViewHolder.LL = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
            convertView.setTag(childViewHolder);
        }
        else{
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.LL.setBackgroundResource(objGlobalMethods.isEven(childPosition) ? R.color.colorPrimaryLight : R.color.White);
        //childViewHolder.LL.setBackgroundResource(R.color.White);
        childViewHolder.lbldriverpointjobno.setTextColor(Color.BLACK);
        childViewHolder.lbldriverpointtype.setTextColor(Color.BLACK);

        Context ctx = myApp.getAppContext();
        String JobNo = tempChild.JobNo == null ? ctx.getString(R.string.lblNoJobNoError) : tempChild.JobNo.isEmpty() ? ctx.getString(R.string.lblNoJobNoError) : tempChild.JobNo;
        String cnt = tempChild.DeliveryContent == null ? ctx.getString(R.string.lblNoJobNoError) : tempChild.DeliveryContent.isEmpty() ? ctx.getString(R.string.lblNoJobNoError) : tempChild.DeliveryContent;
        String unit = !objGlobalMethods.IsEmptyString(tempChild.Unit) ? "\n#" + tempChild.Unit : "";
        String lbldriverpointjobnoVal = JobNo + "\n" + cnt + unit;
        childViewHolder.lbldriverpointjobno.setText(lbldriverpointjobnoVal);
        childViewHolder.lbldriverpointtype.setText(tempChild.StatusCode);

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
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.driver_point_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lbldriverpointgrpPoint);
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
        TextView lbldriverpointjobno;
        TextView lbldriverpointtype;
        LinearLayout LL;
    }


}
