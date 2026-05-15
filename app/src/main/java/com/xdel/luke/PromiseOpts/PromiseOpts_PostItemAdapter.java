package com.xdel.luke.PromiseOpts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.xdel.luke.BottomSheet.promiseOptDialog;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PromiseOpts_PostItemAdapter  extends ArrayAdapter<PromiseOpts_PostItem> {

    private ChildViewHolder h;
    private ArrayList<PromiseOpts_PostItem> list;
    private promiseOptDialog pod;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

    public PromiseOpts_PostItemAdapter(promiseOptDialog apod, Context context, ArrayList<PromiseOpts_PostItem> data) {
        super(context, 0, data);
        this.list = data;
        this.pod = apod;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            PromiseOpts_PostItem data = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.promiseopt_dialog_row, null);
                h = new ChildViewHolder();
                h.lblFrom = (TextView) convertView.findViewById(R.id.lblFrom);
                h.lblTo = (TextView) convertView.findViewById(R.id.lblTo);
                h.lblFromdate = (TextView) convertView.findViewById(R.id.lblFromdate); // treat as from date lbl
                h.lblTodate = (TextView) convertView.findViewById(R.id.lblTodate);  //treat tas to date lbl
                h.LLselector = (LinearLayout) convertView.findViewById(R.id.LLselector);
                convertView.setTag(h);
            } else {
                h = (ChildViewHolder) convertView.getTag();
            }

            h.LLselector.setBackgroundResource(data.IsSelected ? R.drawable.selector_brightgreen_bg_bottom_line : R.drawable.selector_white_bg_bottom_line);
            h.lblFrom.setText(data.from != null ? sdf.format(data.from) : "");
            h.lblTo.setText(data.to != null ? sdf.format(data.to) : "");

            h.LLselector.setOnClickListener(null);
            h.LLselector.setOnClickListener(rtnLLonClickListener(data.idx));

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at PromiseOpts_PostItemAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertView;
        }
    }

    private View.OnClickListener rtnLLonClickListener(final int idx) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int aidx = -1;
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i <= list.size()-1; i++) {
                            aidx = list.get(i).getIdx();
                            list.get(i).setIsSelected(aidx == idx ? true : false);
                        }
                    }

                    notifyDataSetChanged();

                    final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pod.enabledisableSubmitButton();
                        }
                    });
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at PromiseOpts_PostItemAdapter rtnLLonClickListener. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
                }
            }
        };
    }

    public final  class ChildViewHolder {
        TextView lblFrom;
        TextView lblTo;
        TextView lblFromdate;
        TextView lblTodate;
        LinearLayout LLselector;
    }

}

