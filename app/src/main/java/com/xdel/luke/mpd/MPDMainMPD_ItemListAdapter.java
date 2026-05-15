package com.xdel.luke.mpd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.AddressListRouting_PostItemAdapter;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class MPDMainMPD_ItemListAdapter extends ArrayAdapter<MPD_Item> {
    private ArrayList<MPD_Item> list;

    public MPDMainMPD_ItemListAdapter(Context ctx, ArrayList<MPD_Item> MPD_ItemData) {
        super(ctx, 0, MPD_ItemData);
        list = MPD_ItemData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v;

        try {
            MPD_Item d = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.mpdmain_mpd_item_row, parent, false);
                v = new ViewHolder();
                v.lblIDX = (TextView) convertView.findViewById(R.id.lblIDX);
                v.lblDevice_Type = (TextView) convertView.findViewById(R.id.lblDevice_Type);
                v.lblSerial_Number = (TextView) convertView.findViewById(R.id.lblSerial_Number);
                v.lblDevice_Name = (TextView) convertView.findViewById(R.id.lblDevice_Name);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            v.lblIDX.setText(String.valueOf(d.IDX));
            v.lblDevice_Type.setText(String.valueOf(d.Device_Type));
            v.lblSerial_Number.setText(d.Serial_Number);
            v.lblDevice_Name.setText(d.Device_Name);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainMPD_ItemListAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertView;
        }
    }


    public final class ViewHolder {
        TextView lblIDX;
        TextView lblDevice_Type;
        TextView lblSerial_Number;
        TextView lblDevice_Name;
    }

}
