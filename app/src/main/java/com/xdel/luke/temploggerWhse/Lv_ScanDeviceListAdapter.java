package com.xdel.luke.temploggerWhse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzone.devices.DeviceRecordType;
import com.tzone.devices.DeviceType;
import com.tzone.utils.StringUtil;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.Scan;
import com.xdel.luke.test.ScanActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Lv_ScanDeviceListAdapter extends BaseAdapter {
    private LayoutInflater _Inflater;
    private Activity _Activity;
    private List<Scan> DeviceList;
    public String Key = "";

    public Lv_ScanDeviceListAdapter(Activity activity) {
        this._Activity = activity;
        this._Inflater = activity.getLayoutInflater();
        DeviceList = new ArrayList<Scan>();
    }

    public Lv_ScanDeviceListAdapter(){
        DeviceList = new ArrayList<Scan>();
    }

    public void setDeviceList(List<Scan> aDeviceList) {
        this.DeviceList = aDeviceList;
    }

    public List<Scan> getDeviceList(){
        return DeviceList;
    }

    public void AddOrUpdate(final Scan device) {
        try {
            if (device == null || device.getDeviceType() == DeviceType.Unknown)
                return;
            if (Key != null && !Key.isEmpty()) {
                if (!device.getName().contains(Key)
                        && !device.getMac().contains(Key)
                        && !device.getID().contains(Key))
                    return;
            }

            for (int i = 0; i < DeviceList.size(); i++) {
                Scan d = DeviceList.get(i);
                if (d.getMac().equals(device.getMac())) {
                    d.BroadcastUpdate(device);
                    return;
                }
            }
            DeviceList.add(device);
        } catch (Exception ex) {
        }
    }

    @Override
    public int getCount() {
        return DeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return DeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void Clear() {
        DeviceList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        try {
            final Scan d = DeviceList.get(position);
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = _Inflater.inflate(R.layout.layout_list, null);
                holder.txtName = (TextView) view.findViewById(R.id.txtName);
                holder.txtRecordStatus = (TextView) view.findViewById(R.id.txtRecordStatus);
                holder.txtTemperature = (TextView) view.findViewById(R.id.txtTemperature);
                holder.txtHumidity = (TextView) view.findViewById(R.id.txtHumidity);
                holder.txtDeviceID = (TextView) view.findViewById(R.id.txtDeviceID);
                holder.txtMacAddress = (TextView) view.findViewById(R.id.txtMacAddress);
                holder.lblLastActive = (TextView) view.findViewById(R.id.lblLastActive);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            String v = "v" + d.getVersion();
            if (!StringUtil.IsNullOrEmpty(d.getName()))
                holder.txtName.setText(d.getName() + " " + v);

            boolean isOnline = true;
            holder.txtRecordStatus.setText("");
            holder.txtRecordStatus.setTextColor(Color.BLACK);
            Date now = new Date();
            long totalTime = (now.getTime() - d.getLastActiveTime())
                    / (1000);
            if (totalTime > 60) {
                isOnline = false;
                holder.txtRecordStatus.setText("Offline");
                holder.txtRecordStatus.setTextColor(Color.GRAY);
            } else {
                holder.txtRecordStatus.setText("Online");
                holder.txtRecordStatus.setTextColor(Color.GREEN);

                if (d.getRecordStatus() == DeviceRecordType.Initialize) {
                    holder.txtRecordStatus.setText("Initialize");
                    holder.txtRecordStatus.setTextColor(Color.BLACK);
                } else if (d.getRecordStatus() == DeviceRecordType.Delay) {
                    holder.txtRecordStatus.setText("Delay");
                    holder.txtRecordStatus.setTextColor(Color.BLACK);
                } else if (d.getRecordStatus() == DeviceRecordType.Recording) {
                    holder.txtRecordStatus.setText("Recording");
                    holder.txtRecordStatus.setTextColor(Color.GREEN);
                } else if (d.getRecordStatus() == DeviceRecordType.Stop
                        || d.getRecordStatus() == DeviceRecordType.Stop_USB
                        || d.getRecordStatus() == DeviceRecordType.Stop_StorageFull
                        || d.getRecordStatus() == DeviceRecordType.Stop_App) {
                    holder.txtRecordStatus.setText("Stop");
                    holder.txtRecordStatus.setTextColor(Color.BLUE);
                }
            }

            holder.lblLastActive.setText(objGlobalMethods.MillisecToDateStr2(d.getLastActiveTime(), "dd/MM/yyyy h:mma"));

            holder.txtTemperature.setText("-- ");
            if (d.getTemperature() != -1000)
                holder.txtTemperature.setText(d.getTemperature() + "");

            holder.txtHumidity.setText("--");
            if (d.getHumidity() != -1000)
                holder.txtHumidity.setText(d.getHumidity() + "");
            holder.txtDeviceID.setText(d.getID());
            holder.txtMacAddress.setText(d.getMac());
        } catch (Exception ex) {
            Log.e("getView", "Exception: " + ex.toString());
        }
        return view;
    }

    public final class ViewHolder {
        public TextView txtName;
        public TextView txtRecordStatus;
        public TextView txtTemperature;
        public TextView txtHumidity;
        public TextView txtDeviceID;
        public TextView txtMacAddress;
        public TextView lblLastActive;
    }

}
