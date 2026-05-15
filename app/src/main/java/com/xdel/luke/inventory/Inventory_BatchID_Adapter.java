package com.xdel.luke.inventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class Inventory_BatchID_Adapter extends ArrayAdapter<String> {
    private ArrayList<String> list;
    private Context ct;

    public Inventory_BatchID_Adapter(Context ctx, ArrayList<String> data) {
        super(ctx, 0, data);
        list = data;
        ct = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v;

        try {
            String d = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_batchid_item_row, parent, false);
                v = new ViewHolder();
                v.linearLayout1 = convertView.findViewById(R.id.linearLayout1);
                v.lblBatchID = convertView.findViewById(R.id.lblBatchID);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            v.lblBatchID.setText(String.valueOf(d));

            v.linearLayout1.setOnClickListener(null);
            if (v.itemClickListener != null) {
                v.itemClickListener = null;
            }
            v.itemClickListener = rtnitemClickListener(d);
            v.linearLayout1.setOnClickListener(v.itemClickListener);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Inventory_BatchID_Adapter getView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertView;
        }
    }

    private View.OnClickListener rtnitemClickListener(final String BatchID) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myApp.currentActivity != null && myApp.currentActivity.getClass() == InventoryMainActivity.class) {
                        final InventoryMainActivity act = (InventoryMainActivity) myApp.currentActivity;
                        act.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        act.GetSavedInventoryBatchByBatchID(BatchID);
                                    }
                                }
                        );
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at Inventory_BatchID_Adapter rtnitemClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }


    public final class ViewHolder {
        LinearLayout linearLayout1;
        TextView lblBatchID;
        View.OnClickListener itemClickListener;
    }

}
