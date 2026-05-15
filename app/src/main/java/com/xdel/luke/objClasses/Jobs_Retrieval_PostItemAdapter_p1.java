package com.xdel.luke.objClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.service.voice.VoiceInteractionService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.xdel.luke.BackendRun_BroadcastReceiver;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.Jobs_Retrieval_Activity;
import com.xdel.luke.R;
import com.xdel.luke.sqlite.DAO_Jobs;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kean.ong on 15/11/2017.
 */

public class Jobs_Retrieval_PostItemAdapter_p1 extends ArrayAdapter<Jobs_Retrieval_Tags_PostData_p1> {
    private ArrayList<Jobs_Retrieval_Tags_PostData_p1> list;

    public Jobs_Retrieval_PostItemAdapter_p1(Context context, ArrayList<Jobs_Retrieval_Tags_PostData_p1> postdata){
        super(context, 0, postdata);
        list = postdata;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChildViewHolder cv;
        final int pos = position;
        final Context ctx = myApp.getAppContext();
        final SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
        final String jrpudip = objGlobalMethods.getGCMPrefs(GcmPreferences, "jrpudip");

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.jobs_retrieval_item_p1, parent, false);
            cv = new ChildViewHolder();
            cv.lblAddress = (TextView) convertView.findViewById(R.id.lblAddress);
            cv.lblDatetime = (TextView) convertView.findViewById(R.id.lblDatetime);
            cv.lblJobsIDX = (TextView) convertView.findViewById(R.id.lblJobsIDX);
            cv.lblJobNo = (TextView) convertView.findViewById(R.id.lblJobNo);
            cv.hfJobsInfoJsonStr = (TextView) convertView.findViewById(R.id.hfJobsInfoJsonStr);
            cv.btnFail = (Button) convertView.findViewById(R.id.btnFail);
            cv.btnsubmit = (Button) convertView.findViewById(R.id.btnsubmit);
            convertView.setTag(cv);
        } else {
            cv = (ChildViewHolder) convertView.getTag();
        }

        final Jobs_Retrieval_Tags_PostData_p1 data = list.get(position);

        cv.lblAddress.setText(data.Address);
        cv.lblDatetime.setText(data.DateStr);
        cv.lblJobsIDX.setText(String.valueOf(data.JobsIDX));
        cv.lblJobNo.setText(data.JobNo);

        cv.btnFail.setVisibility(jrpudip != null && !jrpudip.isEmpty() && jrpudip.equals("1") ? View.GONE : View.VISIBLE);
        cv.btnsubmit.setText(jrpudip.equals("0") || jrpudip == null || jrpudip.equals("") ? ctx.getString(R.string.btnpu) : ctx.getString(R.string.btnokp));

        cv.btnsubmit.setOnClickListener(null);
        cv.btnsubmitOnClickListener = btnsubmitOnClickListener(position, jrpudip, data);
        cv.btnsubmit.setOnClickListener(cv.btnsubmitOnClickListener);

        cv.btnFail.setOnClickListener(null);
        cv.btnFailOnClickListener = btnFailOnClickListener(position, jrpudip, data);
        cv.btnFail.setOnClickListener(cv.btnFailOnClickListener);
        return convertView;
    }


    private View.OnClickListener btnsubmitOnClickListener(final int position, final String jrpudip, final Jobs_Retrieval_Tags_PostData_p1 data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updatejobPUOKP(jrpudip == null ? "0" : jrpudip, data, position);
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_PostItemAdapter_p1 btnsubmitOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_PostItemAdapter_p1 btnsubmitOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }
        };
    }

    private void updatejobPUOKP(final String jrpudip, final Jobs_Retrieval_Tags_PostData_p1 data, final int position){
        try {
            final Context ctx = Jobs_Retrieval_Activity.activity;
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setMessage(jrpudip.equals("0") || jrpudip == null || jrpudip.equals("") ? ctx.getString(R.string.alert_cfmupdatestatusPU) : ctx.getString(R.string.alert_cfmupdatestatusOKP));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), GcmPreferences);

                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date(" + l + ")/";
                    android.location.Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, regid, data.JobsIDX,
                            jrpudip.equals("0") ? 49 : 135, timestampstr, "Recipient", "",
                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                    objGlobalMethods.writeToFile(jrpudip.equals("0") ? "Job PU at Jobs_Retrieval_Activity." : "Job OKP at Jobs_Retrieval_Activity.",
                            "JobsIDX: " + String.valueOf(data.JobsIDX), "", "", "");

                    DAO_Jobs.updateJobsCatTypeID(jrpudip.equals("0") ? 1 : 3, data.JobsIDX);
                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, jrpudip.equals("0") ? 0 : 2);
                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, jrpudip.equals("0") ? 1 : 3);
                    ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                    ArrayAdapter<Jobs_Retrieval_Tags_PostData_p1> adapter = (ArrayAdapter<Jobs_Retrieval_Tags_PostData_p1>) Jobs_Retrieval_Activity.listView.getAdapter();
                    adapter.remove(adapter.getItem(position));
                    adapter.notifyDataSetChanged();
                    Jobs_Retrieval_Activity.lbljrTotaljobs.setText(String.valueOf(adapter.getCount()));
                }
            });
            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);

        }
    }

    private View.OnClickListener btnFailOnClickListener(final int position, final String jrpudip, final Jobs_Retrieval_Tags_PostData_p1 data) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updatejobFailure(data, position);
                } catch (Exception e) {
                    //String stack = JobsHandler.getStackTraceString(e);
                    //objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_PostItemAdapter_p1 btnFailOnClickListener. Exception: " + e.getMessage().toString() + "\nStack: " + stack, "", "", "", "");
                    objGlobalMethods.writeToFile("Exception at Jobs_Retrieval_PostItemAdapter_p1 btnFailOnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }
        };
    }

    private void updatejobFailure(final Jobs_Retrieval_Tags_PostData_p1 data, final int position){
        try {
            final Context ctx = Jobs_Retrieval_Activity.activity;
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setCancelable(false);
            builder.setMessage(ctx.getString(R.string.alert_cfmupdatestatusFailPU));
            builder.setPositiveButton(myApp.getAppContext().getString(R.string.buttontext_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    SharedPreferences UpdateJobInfoPreferences = objGlobalMethods.getUpdateJobInfoPreferences();
                    String regid = objGlobalMethods.getRegistrationId(myApp.getAppContext(), GcmPreferences);

                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date(" + l + ")/";
                    android.location.Location lastknownlocation = objGlobalMethods.getLastKnownLocationForJobUpdate();
                    int Accuracy = Math.round(lastknownlocation.getAccuracy() * 100);

                    JobsHandler.addUpdateJobInfoStatusWithGPSQueALPrefs(UpdateJobInfoPreferences, regid, data.JobsIDX,
                            128, timestampstr, "Nothing to collect.", "",
                            lastknownlocation.getLatitude(), lastknownlocation.getLongitude(), Accuracy, "");
                    objGlobalMethods.writeToFile("Fail Job at Jobs_Retrieval_Activity.",
                            "JobsIDX: " + String.valueOf(data.JobsIDX), "", "", "");

                    DAO_Jobs.updateJobsCatTypeID(4, data.JobsIDX);
                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 0);
                    JobsHandler.setSystemCategoryJobsIDXhashSQLite(GcmPreferences, 4);
                    ctx.sendBroadcast(new Intent(ctx, BackendRun_BroadcastReceiver.class));

                    ArrayAdapter<Jobs_Retrieval_Tags_PostData_p1> adapter = (ArrayAdapter<Jobs_Retrieval_Tags_PostData_p1>) Jobs_Retrieval_Activity.listView.getAdapter();
                    adapter.remove(adapter.getItem(position));
                    adapter.notifyDataSetChanged();
                    Jobs_Retrieval_Activity.lbljrTotaljobs.setText(String.valueOf(adapter.getCount()));
                }
            });
            builder.setNegativeButton(myApp.getAppContext().getString(R.string.buttontext_NO), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user select "No", just cancel this dialog and continue with app
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);

        }
    }

    public final class ChildViewHolder {
        TextView lblAddress;
        TextView lblDatetime;
        TextView lblJobsIDX;
        TextView lblJobNo;
        TextView hfJobsInfoJsonStr;

        Button btnFail;
        Button btnsubmit;

        View.OnClickListener  btnFailOnClickListener;
        View.OnClickListener  btnsubmitOnClickListener;

    }

}
