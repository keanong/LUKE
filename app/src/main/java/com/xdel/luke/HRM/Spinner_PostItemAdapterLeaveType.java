package com.xdel.luke.HRM;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.HRM_Leave.objLeaveType;
import com.xdel.luke.R;

import java.util.ArrayList;

public class Spinner_PostItemAdapterLeaveType extends ArrayAdapter<objLeaveType> {
    private Context ctx;

    public Spinner_PostItemAdapterLeaveType(Context context, ArrayList<objLeaveType> pd) {
        super(context, 0, pd);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        objLeaveType data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_spinner_leavetype, parent, false);
        }

        TextView lblIDX = (TextView) convertView.findViewById(R.id.lblIDX);
        TextView lblDescription = (TextView) convertView.findViewById(R.id.lblDescription);
        TextView lblIsPaid = (TextView) convertView.findViewById(R.id.lblIsPaid);
        TextView lblCanExceed = (TextView) convertView.findViewById(R.id.lblCanExceed);
        TextView lblAttendanceAffected = (TextView) convertView.findViewById(R.id.lblAttendanceAffected);
        TextView lblAllowedDays = (TextView) convertView.findViewById(R.id.lblAllowedDays);
        TextView lblFlag = (TextView) convertView.findViewById(R.id.lblFlag);
        TextView lblBalance = (TextView) convertView.findViewById(R.id.lblBalance);
        TextView lblBalanceToDate = convertView.findViewById(R.id.lblBalanceToDate);
        TextView lblEntitled = (TextView) convertView.findViewById(R.id.lblEntitled);
        TextView lblBalanceBroughtOver = convertView.findViewById(R.id.lblBalanceBroughtOver);


        lblIDX.setText(String.valueOf(data.IDX));
        lblDescription.setText(data.Description);
        lblIsPaid.setText(!data.IsPaid ? "f" : "t");
        lblCanExceed.setText(!data.CanExceed ? "f" : "t");
        lblAttendanceAffected.setText(!data.AttendanceAffected ? "f" : "t");
        lblAllowedDays.setText(String.valueOf(data.AllowedDays));
        lblFlag.setText(String.valueOf(data.Flag));
        lblBalance.setText(String.valueOf(data.Balance));
        lblBalanceToDate.setText(String.valueOf(data.BalanceToDate));
        lblEntitled.setText(String.valueOf(data.Entitled));
        lblBalanceBroughtOver.setText(String.valueOf(data.BalanceBroughtOver));

        return convertView;
    }

}
