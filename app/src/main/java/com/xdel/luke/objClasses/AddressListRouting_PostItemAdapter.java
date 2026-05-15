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

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.sqlite.objRouteJobEarliestTiming;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kean.ong on 05/07/2016.
 */
public class AddressListRouting_PostItemAdapter extends ArrayAdapter<AddressListRouting_PostData> {

    private Activity myContext;
    private AddressListRouting_PostData[] datas;
    objRouteJobEarliestTiming rjet;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM h:mma");
    Date zerofiednow = objGlobalMethods.getZeroSecondsTimeDate(new Date());

    public AddressListRouting_PostItemAdapter(Context context, ArrayList<AddressListRouting_PostData> addresslistRoutingPostData){
        super(context, 0, addresslistRoutingPostData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*AddressListRouting_PostData addresslistRoutingPostData = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.addresslistrouting_row, parent, false);
        }
        TextView lblAddressListRoutingAddress = (TextView) convertView.findViewById(R.id.lblAddressListRoutingAddress);
        TextView lblCount = (TextView) convertView.findViewById(R.id.lblCount);
        TextView hfJobsIDX = (TextView) convertView.findViewById(R.id.hfJobsIDX);
        TextView lblAddressListRoutingPostal = (TextView) convertView.findViewById(R.id.lblAddressListRoutingPostal);
        TextView lblLAT = (TextView) convertView.findViewById(R.id.lblLAT);
        TextView lblLNG = (TextView) convertView.findViewById(R.id.lblLNG);
        TextView lblid = (TextView) convertView.findViewById(R.id.lblid);
        TextView lblAddressListRoutingTiming = (TextView) convertView.findViewById(R.id.lblAddressListRoutingTiming);

        lblAddressListRoutingAddress.setText(addresslistRoutingPostData.postTitle);
        lblCount.setText(addresslistRoutingPostData.postCount);
        if (addresslistRoutingPostData.changeCountColorBool){
            lblCount.setTextColor(Color.RED);
        }
        else{
            lblCount.setTextColor(Color.parseColor("#0026ff"));
        }
        hfJobsIDX.setText(addresslistRoutingPostData.JobsIDXArrJsonStr);
        lblAddressListRoutingPostal.setText(addresslistRoutingPostData.Postal);
        lblLAT.setText(String.valueOf(addresslistRoutingPostData.LAT));
        lblLNG.setText(String.valueOf(addresslistRoutingPostData.LNG));
        lblid.setText(String.valueOf(addresslistRoutingPostData.id));

        rjet = addresslistRoutingPostData.getRjet();
        Date expectedTiming;
        String timing = "";
        try {
            if (rjet != null && rjet.ExpectedTime != null && !rjet.ExpectedTime.isEmpty() && !rjet.ExpectedTime.equals("")) {
                expectedTiming = objGlobalMethods.MillisecToDate(Long.parseLong(rjet.ExpectedTime));
                boolean isfutureor530 = isFutureDateOr530(expectedTiming);
                timing = objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExpectedTime), "dd/MM h:mma");
                lblAddressListRoutingTiming.setText(isfutureor530 ? "" : timing);
                lblAddressListRoutingTiming.setTextColor(rjet.isLate ? Color.RED : Color.parseColor("#0026ff"));
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListRouting_PostItemAdapter getView rjet. Exception: " + e.getMessage(),"","","","");
        }
        return convertView;*/

        ViewHolder v;

        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.addresslistrouting_row, parent, false);
                v = new ViewHolder();
                v.lblAddressListRoutingAddress = (TextView) convertView.findViewById(R.id.lblAddressListRoutingAddress);
                v.lblCount = (TextView) convertView.findViewById(R.id.lblCount);
                v.hfJobsIDX = (TextView) convertView.findViewById(R.id.hfJobsIDX);
                v.lblAddressListRoutingPostal = (TextView) convertView.findViewById(R.id.lblAddressListRoutingPostal);
                v.lblLAT = (TextView) convertView.findViewById(R.id.lblLAT);
                v.lblLNG = (TextView) convertView.findViewById(R.id.lblLNG);
                v.lblid = (TextView) convertView.findViewById(R.id.lblid);
                v.lblAddressListRoutingTiming = (TextView) convertView.findViewById(R.id.lblAddressListRoutingTiming);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            final AddressListRouting_PostData addresslistRoutingPostData = getItem(position);

            v.lblAddressListRoutingAddress.setText(addresslistRoutingPostData.postTitle);
            v.lblCount.setText(addresslistRoutingPostData.postCount);
            v.lblCount.setTextColor(addresslistRoutingPostData.changeCountColorBool ? Color.RED : Color.parseColor("#0026ff"));
            v.hfJobsIDX.setText(addresslistRoutingPostData.JobsIDXArrJsonStr);
            v.lblAddressListRoutingPostal.setText(addresslistRoutingPostData.Postal);
            v.lblLAT.setText(String.valueOf(addresslistRoutingPostData.LAT));
            v.lblLNG.setText(String.valueOf(addresslistRoutingPostData.LNG));
            v.lblid.setText(String.valueOf(addresslistRoutingPostData.id));

            rjet = addresslistRoutingPostData.getRjet();
            Date expectedTiming;
            String timing = "";
            try {
                if (rjet != null && rjet.ExpectedTime != null && !rjet.ExpectedTime.isEmpty() && !rjet.ExpectedTime.equals("")) {
                    expectedTiming = objGlobalMethods.MillisecToDate(Long.parseLong(rjet.ExpectedTime));
                    boolean isfutureor530 = isFutureDateOr530(expectedTiming);
                    timing = objGlobalMethods.MillisecToDateStr(Long.parseLong(rjet.ExpectedTime), "dd/MM h:mma");
                    v.lblAddressListRoutingTiming.setText(isfutureor530 ? "" : timing);
                    v.lblAddressListRoutingTiming.setTextColor(rjet.isLate ? Color.RED : Color.parseColor("#0026ff"));
                }
            } catch (Exception e) {
                objGlobalMethods.writeToFile("Exception at AddressListRouting_PostItemAdapter getView rjet. Exception: " + e.getMessage(),"","","","");
            }

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListRouting_PostItemAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertView;
        }
    }

    private boolean isFutureDateOr530(Date input){
        boolean asd = false;
        boolean asd2 = false;
        boolean istoday = false;
        int comparedval = 0;
        Date zerofiedInputDate;
        Calendar c = Calendar.getInstance();
        Date today530 = objGlobalMethods.getTodayDateAtCertainTime(5,30);
        Timestamp ts;
        Timestamp ts530 = new Timestamp(today530.getTime());

        //0 comes when two date are same,
        //1 comes when date1 is higher then date2
        //-1 comes when date1 is lower then date2
        try {
            zerofiedInputDate = objGlobalMethods.getZeroSecondsTimeDate(input);
            //comparedval = zerofiednow.compareTo(zerofiedInputDate);
            comparedval = zerofiedInputDate.compareTo(zerofiednow);
            asd2 = comparedval == 1 || comparedval == 0;
            if (!asd2) {
                return asd;
            } else if (asd2 && comparedval == 0) {
                asd = zerofiedInputDate.after(today530) || zerofiedInputDate.equals(today530);
            } else {
                asd = true;
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at AddressListRouting_PostItemAdapter isFutureDateOr530. Exception: " + e.getMessage(),"","","","");
        } finally {
            return asd;
        }
    }

    public final class ViewHolder {
        TextView lblAddressListRoutingAddress;
        TextView lblCount;
        TextView hfJobsIDX;
        TextView lblAddressListRoutingPostal;
        TextView lblLAT;
        TextView lblLNG;
        TextView lblid;
        TextView lblAddressListRoutingTiming;
    }
}
