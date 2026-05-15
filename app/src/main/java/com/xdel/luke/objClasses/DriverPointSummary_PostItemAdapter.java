package com.xdel.luke.objClasses;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdel.luke.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by kean.ong on 01/06/2017.
 */
public class DriverPointSummary_PostItemAdapter  extends ArrayAdapter<DriverPointSummary_PostData> {
    private Context ctx;
    private DecimalFormat dec = new DecimalFormat("#.00");
    public DriverPointSummary_PostItemAdapter(Context context, ArrayList<DriverPointSummary_PostData> DriverPointSummaryPostData){
        super(context, 0, DriverPointSummaryPostData);
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DriverPointSummary_PostData DriverPointSummaryPostData = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_point_summary_row, parent, false);
        }
        // Lookup view for data population
        TextView lbldriverpointdetail = (TextView) convertView.findViewById(R.id.lbldriverpointdetail);
        TextView lblDateOfWork = (TextView) convertView.findViewById(R.id.lblDateOfWork);
        TextView lblTotal_Distance = convertView.findViewById(R.id.lblTotal_Distance);
        String detail = DriverPointSummaryPostData.DateString + " - " + String.valueOf(DriverPointSummaryPostData.Points) + " " + ctx.getString(R.string.point) + " - " + String.valueOf(DriverPointSummaryPostData.Actual_Jobs) + " Job(s)";
        lbldriverpointdetail.setText(detail);
        double dist = DriverPointSummaryPostData.Total_Distance;
        lblTotal_Distance.setText(ctx.getString(R.string.performancetxt03) + " " + (dist == 0.0 ? "0.0" : dec.format(dist)) + ctx.getString(R.string.performancetxt04));
        lblDateOfWork.setText(DriverPointSummaryPostData.DateString);
        /*if (DriverPointSummaryPostData.AlternateColor == 0) {
            //convertView.setBackgroundColor(Color.parseColor("#FF9E1B"));
            convertView.setBackgroundResource(R.color.colorPrimary);
            lbldriverpointdetail.setTextColor(Color.WHITE);
            lblDateOfWork.setTextColor(Color.WHITE);
        } else {
            //convertView.setBackgroundColor(Color.parseColor("#FFE7C9"));
            convertView.setBackgroundResource(R.color.White);
            lbldriverpointdetail.setTextColor(Color.BLACK);
            lblDateOfWork.setTextColor(Color.BLACK);
        }*/
        //// Return the completed view to render on screen
        return convertView;
    }
}
