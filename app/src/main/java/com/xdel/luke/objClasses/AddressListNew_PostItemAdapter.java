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

import java.util.ArrayList;

/**
 * Created by kean.ong on 24/02/2017.
 */
public class AddressListNew_PostItemAdapter  extends ArrayAdapter<AddressListNew_PostData> {

    public AddressListNew_PostItemAdapter(Context context, ArrayList<AddressListNew_PostData> addresslistnewPostData){
        super(context, 0, addresslistnewPostData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AddressListNew_PostData addresslistnewPostDatas = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.address_list_row_new, parent, false);
        }
        // Lookup view for data population
        TextView lblAddressListRowNewAddress = (TextView) convertView.findViewById(R.id.lblAddressListRowNewAddress);
        TextView lblCount = (TextView) convertView.findViewById(R.id.lblCount);
        TextView hfPostal = (TextView) convertView.findViewById(R.id.hfPostal);
        TextView hfJobsInfoArrJsonStr = (TextView) convertView.findViewById(R.id.hfJobsInfoArrJsonStr);
        TextView lblAddressListRowNewTiming = (TextView) convertView.findViewById(R.id.lblAddressListRowNewTiming);
        //ImageView imgL = (ImageView) convertView.findViewById(R.id.AddressListLockimg);
        //ImageView imgV = (ImageView) convertView.findViewById(R.id.AddressListDNFimg);
        //ImageView imgS = (ImageView) convertView.findViewById(R.id.AddressListReqSignimg);
        //ImageView img2w = (ImageView) convertView.findViewById(R.id.AddressList2wayimg);

        TextView lblLAT = (TextView) convertView.findViewById(R.id.lblLAT);
        TextView lblLNG = (TextView) convertView.findViewById(R.id.lblLNG);

        // Populate the data into the template view using the data object
        lblAddressListRowNewAddress.setText(addresslistnewPostDatas.Address);
        lblCount.setText(addresslistnewPostDatas.Count);

        //img2w.setImageResource(addresslistnewPostDatas.HasP2 != null && addresslistnewPostDatas.HasP2 > 0 ? R.mipmap.ic_action_2way : R.mipmap.ic_blank);
        //imgV.setImageResource(addresslistnewPostDatas.altDNF != null && addresslistnewPostDatas.altDNF ? R.mipmap.ic_toggle_star : R.mipmap.ic_blank);
        //imgL.setImageResource(addresslistnewPostDatas.altOTP != null && addresslistnewPostDatas.altOTP ? R.mipmap.ic_lock : R.mipmap.ic_blank);
        //imgS.setImageResource(addresslistnewPostDatas.altSign ? R.mipmap.ic_reqsignature : R.mipmap.ic_blank);
        lblAddressListRowNewTiming.setText(!addresslistnewPostDatas.timingStr.equals("") ? addresslistnewPostDatas.timingStr : "");
        lblCount.setTextColor(addresslistnewPostDatas.changeCountColorBool ? Color.RED : Color.parseColor("#0026ff"));

        if (addresslistnewPostDatas.setlateColor) {
            lblAddressListRowNewTiming.setTextColor(Color.RED);
        } else if (addresslistnewPostDatas.setBoutlateColor) {
            lblAddressListRowNewTiming.setTextColor(Color.parseColor("#FFFF5C2B"));
        } else {
            lblAddressListRowNewAddress.setTextColor(Color.parseColor("#000000"));
            lblAddressListRowNewTiming.setTextColor(Color.parseColor("#0026ff"));
        }

        hfJobsInfoArrJsonStr.setText(addresslistnewPostDatas.JobsInfoArrJsonStr);
        hfPostal.setText(addresslistnewPostDatas.postal);
        lblLAT.setText(String.valueOf(addresslistnewPostDatas.LAT));
        lblLNG.setText(String.valueOf(addresslistnewPostDatas.LNG));

        //// Return the completed view to render on screen
        return convertView;
    }
}
