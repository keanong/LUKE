package com.xdel.luke.mpd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class MPDMainJob_ItemListAdapter extends ArrayAdapter<String> {
    private ArrayList<String> list;

    public MPDMainJob_ItemListAdapter(Context ctx, ArrayList<String> RefAtt) {
        super(ctx,0, RefAtt);
        list = RefAtt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v;

        try {
            String d = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mpdmain_ref_att_row, parent, false);
                v = new ViewHolder();
                v.lblRef = (TextView) convertView.findViewById(R.id.lblRef);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            v.lblRef.setText(d);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainJob_ItemListAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertView;
        }
    }

    public final class ViewHolder {
        TextView lblRef;
    }

}
