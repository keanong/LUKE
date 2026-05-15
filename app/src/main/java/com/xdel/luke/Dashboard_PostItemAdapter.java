package com.xdel.luke;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

/**
 * Created by kean.ong on 09/06/2015.
 */
public class Dashboard_PostItemAdapter extends ArrayAdapter<Dashboard_PostData> {

    private Activity myContext;
    private Dashboard_PostData[] datas;
    private Context _context;

    public Dashboard_PostItemAdapter(Context context, ArrayList<Dashboard_PostData> dashboardPostDatas) {
        super(context, 0, dashboardPostDatas);
        this._context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v;

        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_list_row, parent, false);
                v = new ViewHolder();
                v.lblTitle = convertView.findViewById(R.id.lblTitle);
                v.lblCount = convertView.findViewById(R.id.lblCount);
                v.imgnets = convertView.findViewById(R.id.lblAddressListnetsimg);
                v.imgcc = convertView.findViewById(R.id.lblAddressListccimg);
                v.imgtradein = convertView.findViewById(R.id.lblAddressListDOCODimg);
                v.lblAddressListcoldimg = convertView.findViewById(R.id.lblAddressListcoldimg);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            final Dashboard_PostData dashboardPostDatas = getItem(position);
            v.lblTitle.setText(dashboardPostDatas.postTitle);
            v.lblCount.setText(dashboardPostDatas.postCount);
            v.lblCount.setTextColor(dashboardPostDatas.changeCountColorBool ? Color.RED : Color.BLUE);
            v.lblCount.setVisibility(!dashboardPostDatas.showCountLbl ? View.GONE : View.VISIBLE);


            if (dashboardPostDatas.ColdCount > 0 && !dashboardPostDatas.isOKP) {
                v.lblAddressListcoldimg.setVisibility(View.VISIBLE);
                v.lblAddressListcoldimg.setText(_context.getString(R.string.icontxt_cold));
            } else {
                v.lblAddressListcoldimg.setVisibility(View.GONE);
                v.lblAddressListcoldimg.setText("");
            }


            if (dashboardPostDatas.ReqNetsCount > 0 && dashboardPostDatas.isDIP) {
                v.imgnets.setVisibility(View.GONE);
                v.imgnets.setText(_context.getString(R.string.icontxt_nets));
            } else {
                v.imgnets.setVisibility(View.GONE);
                v.imgnets.setText("");
            }

            if (dashboardPostDatas.ReqVisaCount > 0 && dashboardPostDatas.isDIP) {
                v.imgcc.setVisibility(View.GONE);
                v.imgcc.setText(_context.getString(R.string.icontxt_visa));
            } else {
                v.imgcc.setVisibility(View.GONE);
                v.imgcc.setText("");
            }

            if (dashboardPostDatas.ReqTiCount > 0 && dashboardPostDatas.isDIP) {
                v.imgtradein.setVisibility(View.GONE);
                v.imgtradein.setText(_context.getString(R.string.icontxt_tradein));
            } else {
                v.imgtradein.setVisibility(View.GONE);
                v.imgtradein.setText("");
            }

            if (position == 0 && !dashboardPostDatas.IsHomesUser && !dashboardPostDatas.lite) {
                v.lblTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                v.lblTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Dashboard_PostItemAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertView;
        }
    }

    public final class ViewHolder {
        TextView lblTitle;
        TextView lblCount;
        /*ImageView imgnets;
        ImageView imgcc;
        ImageView imgtradein;*/
        TextView imgnets;
        TextView imgcc;
        TextView imgtradein;
        TextView lblAddressListcoldimg;
    }

}
