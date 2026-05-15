package com.xdel.luke.Ext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.BottomSheet.extDialog;
import com.xdel.luke.BottomSheet.promiseOptDialog;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.PromiseOpts.PromiseOpts_PostItem;
import com.xdel.luke.PromiseOpts.PromiseOpts_PostItemAdapter;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class extOpts_PostItemAdapter extends ArrayAdapter<extOpts_PostItem> {

    private ChildViewHolder h;
    private ArrayList<extOpts_PostItem> list;
    private extDialog ext;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm");

    public extOpts_PostItemAdapter(extDialog aext, Context context, ArrayList<extOpts_PostItem> data) {
        super(context, 0, data);
        this.list = data;
        this.ext = aext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            extOpts_PostItem d = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ext_dialog_row, null);
                h = new ChildViewHolder();
                h.lblDesc = (TextView) convertView.findViewById(R.id.lblDesc);
                h.LLselector = (LinearLayout) convertView.findViewById(R.id.LLselector);
                convertView.setTag(h);
            } else {
                h = (ChildViewHolder) convertView.getTag();
            }

            h.LLselector.setBackgroundResource(d.IsSelected ? R.drawable.selector_brightgreen_bg_bottom_line : R.drawable.selector_white_bg_bottom_line);
            h.lblDesc.setText(d.desc != null ? d.desc : "");

            h.LLselector.setOnClickListener(null);
            h.LLselector.setOnClickListener(rtnLLonClickListener(d.idx));

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at extOpts_PostItemAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
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
                            aidx = list.get(i).idx;
                            list.get(i).setIsSelected(aidx == idx ? true : false);
                        }
                    }

                    notifyDataSetChanged();

                    final JobInfoActivity act = (JobInfoActivity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ext.enabledisableSubmitButton();
                        }
                    });
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at extOpts_PostItemAdapter rtnLLonClickListener. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
                }
            }
        };
    }

    public final  class ChildViewHolder {
        TextView lblDesc;
        LinearLayout LLselector;
    }

}
