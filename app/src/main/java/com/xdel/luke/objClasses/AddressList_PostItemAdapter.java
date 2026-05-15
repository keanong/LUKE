package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdel.luke.Dashboard_PostData;
import com.xdel.luke.R;

import java.util.ArrayList;


/**
 * Created by kean.ong on 15/06/2015.
 */
public class AddressList_PostItemAdapter  extends ArrayAdapter<AddressList_PostData> {

    private Activity myContext;
    private AddressList_PostData[] datas;

    public AddressList_PostItemAdapter(Context context, ArrayList<AddressList_PostData>addresslistPostData){
        super(context, 0, addresslistPostData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AddressList_PostData addresslistPostDatas = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.address_list_row, parent, false);
        }
        // Lookup view for data population
        TextView lblTitle = (TextView) convertView.findViewById(R.id.lblTitle);
        TextView lblCount = (TextView) convertView.findViewById(R.id.lblCount);
        TextView hfJobsInfoArrJsonStr = (TextView) convertView.findViewById(R.id.hfJobsInfoArrJsonStr);
        TextView hfPostal = (TextView) convertView.findViewById(R.id.hfPostal);
        TextView lblTitle2 = (TextView) convertView.findViewById(R.id.lblTitle2);
        TextView lblCompany = (TextView) convertView.findViewById(R.id.lblCompany);
        TextView lblBuilding = (TextView) convertView.findViewById(R.id.lblBuilding);
        ImageView imgL = (ImageView) convertView.findViewById(R.id.AddressListLockimg);
        ImageView imgV = (ImageView) convertView.findViewById(R.id.AddressListDNFimg);

        TextView lblLAT = (TextView) convertView.findViewById(R.id.lblLAT);
        TextView lblLNG = (TextView) convertView.findViewById(R.id.lblLNG);

        // Populate the data into the template view using the data object
        lblTitle.setText(addresslistPostDatas.postTitle);
        lblCount.setText(addresslistPostDatas.postCount);
        if (addresslistPostDatas.changeCountColorBool){
            lblCount.setTextColor(Color.RED);
        }
        else{
            lblCount.setTextColor(Color.parseColor("#0026ff"));
        }

        if (addresslistPostDatas.DNF != null && addresslistPostDatas.DNF) {
            imgV.setImageResource(R.mipmap.ic_toggle_star);
        } else {
            imgV.setImageResource(R.mipmap.ic_blank);
        }

        if (addresslistPostDatas.OTP != null && addresslistPostDatas.OTP) {
            imgL.setImageResource(R.mipmap.ic_lock);
        } else {
            imgL.setImageResource(R.mipmap.ic_blank);
        }

        if (addresslistPostDatas.DNF){
            imgV.setVisibility(View.VISIBLE);
        }
        else {
            imgV.setVisibility(View.GONE);
        }
        hfJobsInfoArrJsonStr.setText(addresslistPostDatas.JobsInfoArrJsonStr);
        hfPostal.setText(addresslistPostDatas.Postal);
        lblTitle2.setText(addresslistPostDatas.addressWithoutPostal);
        lblCompany.setText(addresslistPostDatas.companyName);
        lblLAT.setText(String.valueOf(addresslistPostDatas.LAT));
        lblLNG.setText(String.valueOf(addresslistPostDatas.LNG));
        lblBuilding.setText(addresslistPostDatas.Building);
        //lblCompany.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        //// Return the completed view to render on screen
        return convertView;
    }


}
