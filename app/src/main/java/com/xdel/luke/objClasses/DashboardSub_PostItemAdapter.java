package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdel.luke.R;

import java.util.ArrayList;

/**
 * Created by kean.ong on 07/07/2016.
 */
public class DashboardSub_PostItemAdapter extends ArrayAdapter<DashboardSub_PostData> {

    private Activity myContext;
    private DashboardSub_PostData[] datas;
    private Context _context;

    public DashboardSub_PostItemAdapter(Context context, ArrayList<DashboardSub_PostData> dashboardSubPostDatas){
        super(context, 0, dashboardSubPostDatas);
        this._context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DashboardSub_PostData dashboardSubPostDatas = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_list_row, parent, false);
        }
        // Lookup view for data population
        TextView lblTitle = (TextView) convertView.findViewById(R.id.lblTitle);
        TextView lblCount = (TextView) convertView.findViewById(R.id.lblCount);
        /*ImageView imgnets = (ImageView) convertView.findViewById(R.id.imgnets);
        ImageView imgcc = (ImageView) convertView.findViewById(R.id.imgcc);
        ImageView imgtradein = (ImageView) convertView.findViewById(R.id.imgtradein);*/

        TextView imgnets = convertView.findViewById(R.id.lblAddressListnetsimg);
        TextView imgcc = convertView.findViewById(R.id.lblAddressListccimg);
        TextView imgtradein = convertView.findViewById(R.id.lblAddressListDOCODimg);
        TextView lblAddressListcoldimg = convertView.findViewById(R.id.lblAddressListcoldimg);

        TextView lblhfJobsInfoArrJsonStr = (TextView) convertView.findViewById(R.id.lblhfJobsInfoArrJsonStr);
        // Populate the data into the template view using the data object
        lblTitle.setText(dashboardSubPostDatas.postTitle);
        lblCount.setText(dashboardSubPostDatas.postCount);
        lblCount.setVisibility(View.VISIBLE);
        if (dashboardSubPostDatas.changeCountColorBool){
            lblCount.setTextColor(Color.RED);
        }
        else{
            lblCount.setTextColor(Color.parseColor("#0026ff"));
        }
        lblhfJobsInfoArrJsonStr.setText(dashboardSubPostDatas.JobsInfoArrJsonStr);
        /*imgnets.setVisibility(dashboardSubPostDatas.ReqNetsCount > 0 && dashboardSubPostDatas.isDIP ? View.VISIBLE : View.GONE);
        imgcc.setVisibility(dashboardSubPostDatas.ReqVisaCount > 0 && dashboardSubPostDatas.isDIP ? View.VISIBLE : View.GONE);
        imgtradein.setVisibility(dashboardSubPostDatas.ReqTiCount > 0 && dashboardSubPostDatas.isDIP ? View.VISIBLE : View.GONE);*/

        if (dashboardSubPostDatas.ColdCount > 0) {
            lblAddressListcoldimg.setVisibility(View.VISIBLE);
            lblAddressListcoldimg.setText(_context.getString(R.string.icontxt_cold));
        } else {
            lblAddressListcoldimg.setVisibility(View.GONE);
            lblAddressListcoldimg.setText("");
        }

        if (dashboardSubPostDatas.ReqNetsCount > 0 && dashboardSubPostDatas.isDIP) {
            imgnets.setVisibility(View.GONE);
            imgnets.setText(_context.getString(R.string.icontxt_nets));
        } else {
            imgnets.setVisibility(View.GONE);
            imgnets.setText("");
        }

        if (dashboardSubPostDatas.ReqVisaCount > 0 && dashboardSubPostDatas.isDIP) {
            imgcc.setVisibility(View.GONE);
            imgcc.setText(_context.getString(R.string.icontxt_visa));
        } else {
            imgcc.setVisibility(View.GONE);
            imgcc.setText("");
        }

        if (dashboardSubPostDatas.ReqTiCount > 0 && dashboardSubPostDatas.isDIP) {
            imgtradein.setVisibility(View.GONE);
            imgtradein.setText(_context.getString(R.string.icontxt_tradein));
        } else {
            imgtradein.setVisibility(View.GONE);
            imgtradein.setText("");
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
