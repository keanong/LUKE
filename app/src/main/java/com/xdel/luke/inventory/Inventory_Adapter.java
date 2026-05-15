package com.xdel.luke.inventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.CheckListActivity;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.OtpActivity;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class Inventory_Adapter extends ArrayAdapter<InventoryLite> {
    private ArrayList<InventoryLite> list;
    private Context ct;

    public Inventory_Adapter(Context ctx, ArrayList<InventoryLite> data) {
        super(ctx, 0, data);
        list = data;
        ct = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v;

        try {
            InventoryLite d = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_item_row, parent, false);
                v = new ViewHolder();
                v.linearLayout1 = convertView.findViewById(R.id.linearLayout1);
                v.lblInventoryIDX = convertView.findViewById(R.id.lblInventoryIDX);
                v.lblInventoryDetailsIDX = convertView.findViewById(R.id.lblInventoryDetailsIDX);
                v.lblSN = convertView.findViewById(R.id.lblSN);
                v.lblStockCode = convertView.findViewById(R.id.lblStockCode);
                v.lblDesc = convertView.findViewById(R.id.lblDesc);
                v.lblBal = convertView.findViewById(R.id.lblBal);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            v.lblInventoryIDX.setText(String.valueOf(d.IDX));
            //v.lblInventoryDetailsIDX.setText(String.valueOf(d.Device_Type));
            int sn = position +1;
            v.lblSN.setText(String.valueOf(sn));
            v.lblStockCode.setText(d.StockCode);
            v.lblDesc.setText(d.Description);
            v.lblBal.setText(String.valueOf(d.vBalance) + "/" + String.valueOf(d.Balance));
            /*v.linearLayout1.setBackground(ct.getDrawable(
                    ((d.vBalance > 0) && (d.vBalance < d.Balance)) ? R.drawable.tabstyle_base_black_bg_pink
                            : d.VFlag > 0 ? R.drawable.tabstyle_base_black_bg_green
                            : R.drawable.tabstyle_base_black_bg_white));*/
            v.linearLayout1.setBackground(ct.getDrawable(
                    ((d.vBalance > 0) && (d.vBalance < d.Balance)) ? R.drawable.tabstyle_base_black_bg_pink
                            : d.vBalance >= d.Balance ? R.drawable.tabstyle_base_black_bg_green
                            : R.drawable.tabstyle_base_black_bg_white));

            v.linearLayout1.setOnClickListener(null);
            v.linearLayout1.setOnLongClickListener(null);
            if (v.itemOnLongClickListener != null) {
                v.itemOnLongClickListener = null;
            }
            if (v.itemClickListener != null) {
                v.itemClickListener = null;
            }
            v.itemClickListener = rtnitemClickListener(d);
            v.itemOnLongClickListener = rtnItemOnLongClickListener(d);
            //v.linearLayout1.setOnClickListener(v.itemClickListener);

            /*if (d.vBalance > 0) {
                v.linearLayout1.setOnLongClickListener(v.itemOnLongClickListener);
            }*/
            v.linearLayout1.setOnLongClickListener(v.itemOnLongClickListener);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at MPDMainMPD_ItemListAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertView;
        }
    }

    //private View.OnClickListener rtnitemClickListener(final InventoryLite d, final int invlidx) {
    private View.OnClickListener rtnitemClickListener(final InventoryLite d) {
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
                                        //act.toggleToTallyNew(d, invlidx);
                                        act.toggleToTallyNew(d);
                                    }
                                }
                        );
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at Inventory_Adapter rtnitemClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };
    }

    //private View.OnLongClickListener rtnItemOnLongClickListener(final InventoryLite d, final int invlidx) {
    private View.OnLongClickListener rtnItemOnLongClickListener(final InventoryLite d) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    if (myApp.currentActivity != null && myApp.currentActivity.getClass() == InventoryMainActivity.class) {
                        final InventoryMainActivity act = (InventoryMainActivity) myApp.currentActivity;
                        act.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        InventoryLite invl = d;
                                        if (!((invl.Flag & 2) == 2) && !((invl.Flag & 4) == 4) && !((invl.Flag & 8) == 8) && !((invl.Flag & 16) == 16) && !((invl.Flag & 32) == 32) && !((invl.Flag & 64) == 64)) {
                                            //do nothing
                                        } else {
                                            if (invl.Stockrefs != null && invl.Stockrefs.size() > 0 && (invl.vBalance != invl.Balance) && (invl.vBalance == 0)) {
                                                invl.setInvTallyMode(3);
                                            }
                                        }
                                        //act.toggleToTallyNew(invl, invlidx);
                                        act.toggleToTallyNew(invl);
                                    }
                                }
                        );
                    }
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at Inventory_Adapter rtnItemOnLongClickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
                return false;
            }
        };
    }


    public final class ViewHolder {
        LinearLayout linearLayout1;
        TextView lblInventoryIDX;
        TextView lblInventoryDetailsIDX;
        TextView lblSN;
        TextView lblStockCode;
        TextView lblDesc;
        TextView lblBal;
        View.OnClickListener itemClickListener;
        View.OnLongClickListener itemOnLongClickListener;
    }


}
