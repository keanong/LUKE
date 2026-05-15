package com.xdel.luke.templogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class lukeTLregistry_Adapter extends ArrayAdapter<lukeTLregistry> {
    private ArrayList<lukeTLregistry> list;
    private Context ct;

    public lukeTLregistry_Adapter(Context ctx, ArrayList<lukeTLregistry> data) {
        super(ctx, 0, data);
        list = data;
        ct = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v;

        try {
            lukeTLregistry d = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.luke_tl_reg_row, parent, false);
                v = new ViewHolder();
                v.lblRef = (TextView) convertView.findViewById(R.id.lblRef);
                v.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            v.lblRef.setText(d.desc);
            v.linearLayout1.setBackground(ct.getDrawable(
                    (d.registered > 0) ? R.drawable.tabstyle_base_black_bg_green
                            : R.drawable.tabstyle_base_black_bg_white));
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at lukeTLregistry_Adapter getView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertView;
        }
    }

    public final class ViewHolder {
        TextView lblRef;
        LinearLayout linearLayout1;
    }

}
