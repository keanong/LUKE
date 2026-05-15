package com.xdel.luke.HRM;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.DriverPointSummaryActivity;
import com.xdel.luke.HRM_GC_Schedule.GCScheduleActivity;
import com.xdel.luke.HRM_Leave.LeaveActivity;
import com.xdel.luke.Locker.LockerProfileActivity;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.ppReader.ui.ppMainActivity;

import java.util.ArrayList;

public class ProfileItemAdapter extends ArrayAdapter<objHRM_Info> {
    private Context ctx;

    public ProfileItemAdapter(Context context, ArrayList<objHRM_Info> data) {
        super(context, 0, data);
        this.ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hrm_row, parent, false);
        }
        // Get the data item for this position
        try {
            objHRM_Info data = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view

            // Lookup view for data population
            TextView lbltitle = (TextView) convertView.findViewById(R.id.lbltitle);
            TextView lblpageid = (TextView) convertView.findViewById(R.id.lblpageid);

            // Populate the data into the template view using the data object
            lbltitle.setText(data.title);
            lblpageid.setText(String.valueOf(data.pageid));
            // Return the completed view to render on screen

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = null;
                        int pageid = -1;
                        TextView lblpageid = (TextView) v.findViewById(R.id.lblpageid);
                        if (!lblpageid.getText().toString().equals("")) {

                            try {
                                pageid = Integer.parseInt(lblpageid.getText().toString());
                            } catch (Exception e) {
                                pageid = -1;
                            }

                            if (pageid >= 0) {
                                intent = pageid == 0 ? new Intent(myApp.currentActivity, DriverPointSummaryActivity.class) :
                                        pageid == 1 ? new Intent(myApp.currentActivity, LeaveActivity.class) :
                                                pageid == 2 ? new Intent(myApp.currentActivity, ChangePwdActivity.class) :
                                                        pageid == 3 ? new Intent(myApp.currentActivity, StaffPassActivity.class) :
                                                                pageid == 4 ? new Intent(myApp.currentActivity, LockerProfileActivity.class) :
                                                                        pageid == 5 ? new Intent(myApp.currentActivity, GCScheduleActivity.class) :
                                                                                pageid == 6 ? new Intent(myApp.currentActivity, ppMainActivity.class) : null;
                                if (intent != null) {
                                    if (pageid == 0) {
                                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                                        String[] prefName = new String[]{"dailypoints", "summarypoints", "summaryfromdate", "summarytodate",
                                                "summaryfromdate2", "summarytodate2", "currentpointdatelong", "visiblelv", "gcsch"};
                                        String[] prefvalue = new String[]{"", "", "", "", "", "", "", "", ""};
                                        objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);
                                    } else if (pageid == 1) {
                                        LeaveActivity.InsideLeaveModule = null;
                                    }
                                    //ctx.startActivity(intent);
                                    myApp.currentActivity.startActivity(intent);
                                    myApp.currentActivity.finish();
                                }
                            }
                        }
                    } catch (Exception e) {
                        objGlobalMethods.writeToFile("Exception at ProfileItemAdapter getView convertView.setOnClickListener. Exception: " + e.getMessage(), "", "", "", "");
                    }
                }
            });
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at ProfileItemAdapter getView. Exception: " + s, "", "", "", "");
        } finally {
            return convertView;
        }
    }
}
