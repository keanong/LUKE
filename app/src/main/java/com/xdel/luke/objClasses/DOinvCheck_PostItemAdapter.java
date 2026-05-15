package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by kean.ong on 11/04/2017.
 */
public class DOinvCheck_PostItemAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private Context _context;
    public List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    public HashMap<String, List<DOinvCheck_PostData>> _listDataChild;

    public HashMap<Integer, boolean[]> mChildCheckStates;
    public HashMap<Integer, boolean[]> mChildColorStates;
    private ChildViewHolder childViewHolder;

    public DOinvCheck_PostItemAdapter(Context context, List<String> listDataHeader, HashMap<String, List<DOinvCheck_PostData>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        mChildCheckStates = new HashMap<Integer, boolean[]>();
        mChildColorStates = new HashMap<Integer, boolean[]>();
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
        final DOinvCheck_PostData tempChild = (DOinvCheck_PostData) getChild(groupPosition, childPosition);
        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        if (convertView == null) {
            /*LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.jobs_list_item, null);*/
            convertView = inflater.inflate(R.layout.doinvchecklist_item, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.lbldoinvTxt = (TextView) convertView.findViewById(R.id.lbldoinvTxt);
            childViewHolder.lbldoinvIDX = (TextView) convertView.findViewById(R.id.lbldoinvIDX);
            childViewHolder.lbldoinvJobsIDX = (TextView) convertView.findViewById(R.id.lbldoinvJobsIDX);
            childViewHolder.cbdoinvCheckbox = (CheckBox) convertView.findViewById(R.id.cbdoinvCheckbox);
            convertView.setTag(childViewHolder);
        }
        else{
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }


        childViewHolder.lbldoinvTxt.setText(tempChild.tag);
        //childViewHolder.lbldoinvIDX.setText(tempChild.InvoicesIDX.toString());
        childViewHolder.lbldoinvJobsIDX.setText(tempChild.JobsIDX.toString());

        childViewHolder.cbdoinvCheckbox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
            childViewHolder.cbdoinvCheckbox.setChecked(getChecked[mChildPosition]);
        } else {
            boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];

            Iterator<HashMap.Entry<String, List<DOinvCheck_PostData>>> itr;
            List<String> DataHeader = _listDataHeader;
            HashMap<String, List<DOinvCheck_PostData>> DataChild2 = _listDataChild;
            itr = DataChild2.entrySet().iterator();
            List<DOinvCheck_PostData> jpdlist = null;
            DOinvCheck_PostData pd = null;
            String key = "";

            while (itr.hasNext()) {
                pd = null;
                HashMap.Entry<String, List<DOinvCheck_PostData>> entry = itr.next();
                key = entry.getKey();
                if (key.equals(tempChild.JobNoOrCNNO)) {
                    jpdlist = entry.getValue();
                    for (int i = 0; i <= jpdlist.size() - 1; i++) {
                        pd = jpdlist.get(i);
                        getChecked[i] = pd.selected;
                    }
                }
            }

            mChildCheckStates.put(mGroupPosition, getChecked);
            childViewHolder.cbdoinvCheckbox.setChecked(tempChild.selected);
        }


        childViewHolder.cbdoinvCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DOinvCheck_PostData dic = (DOinvCheck_PostData) getChild(mGroupPosition, mChildPosition);
                SharedPreferences GcmPreferences = getGcmPreferences(_context);
                if (isChecked) {
                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                    dic.setSelected(true);
                    JobsHandler.processDOinvCheckedItemsBeforeSubmit(dic.JobsIDX, dic.tag, dic.total, true, GcmPreferences);
                } else {
                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                    dic.setSelected(false);
                    JobsHandler.processDOinvCheckedItemsBeforeSubmit(dic.JobsIDX, dic.tag, dic.total, false, GcmPreferences);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DOinvCheck_PostData dic = (DOinvCheck_PostData) getChild(mGroupPosition, mChildPosition);
                SharedPreferences GcmPreferences = getGcmPreferences(_context);
                CheckBox cbdoinvCheckbox = (CheckBox) v.findViewById(R.id.cbdoinvCheckbox);
                Boolean isChecked = cbdoinvCheckbox.isChecked();
                if (isChecked) {
                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                    dic.setSelected(false);
                    cbdoinvCheckbox.setChecked(false);
                    //JobsHandler.processDOinvCheckedItemsBeforeSubmit(dic.JobsIDX, dic.tag, dic.total, true, GcmPreferences);
                } else {
                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                    dic.setSelected(true);
                    cbdoinvCheckbox.setChecked(true);
                    //JobsHandler.processDOinvCheckedItemsBeforeSubmit(dic.JobsIDX, dic.tag, dic.total, false, GcmPreferences);
                }
            }
        });

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
            gvh.LLarrival.setVisibility(View.GONE);
            gvh.img_i_have_arrive.setVisibility(View.GONE);
            gvh.lblarrival.setVisibility(View.GONE);
        } catch (Exception e){
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at DOinvCheck_PostItemAdapter getGroupView. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception at DOinvCheck_PostItemAdapter getGroupView. Exception: " + e.getMessage().toString(),"","","","");
        } finally {
            return convertView;
        }
        /*String headerTitle = (String) getGroup(groupPosition);
        int chldrenCount = getChildrenCount(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.jobs_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        TextView lblListHeaderCount = (TextView) convertView.findViewById(R.id.lblListHeaderCount);
        lblListHeader.setText(headerTitle);
        lblListHeaderCount.setText(String.valueOf(chldrenCount));

        return convertView;*/
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

    public final class ChildViewHolder{
        TextView lbldoinvTxt;
        TextView lbldoinvIDX;
        TextView lbldoinvJobsIDX;
        CheckBox cbdoinvCheckbox;
    }
}
