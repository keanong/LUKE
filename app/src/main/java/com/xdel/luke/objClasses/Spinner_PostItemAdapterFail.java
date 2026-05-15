package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;

import java.util.ArrayList;

public class Spinner_PostItemAdapterFail extends ArrayAdapter<Spinner_PostDataFail> {

    private Activity myContext;
    private Spinner_PostDataFail[] datas;

    public Spinner_PostItemAdapterFail(Context context, ArrayList<Spinner_PostDataFail> SpinnerPostDataFail) {
        super(context, 0, SpinnerPostDataFail);
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

    public View getCustomView(int position, View convertView, ViewGroup parent){
       /* // TODO Auto-generated method stub
//return super.getView(position, convertView, parent);

        Spinner_PostDataFail SpinnerPostDataFail = getItem(position);
// Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_spinnerfail, parent, false);
        }

        TextView lblDesc = (TextView)convertView.findViewById(R.id.lblStatusCodesDesc);
        TextView lblIDX = (TextView)convertView.findViewById(R.id.lblStatusCodesIDX);
        TextView lblStatusCodesDescEN = (TextView)convertView.findViewById(R.id.lblStatusCodesDescEN);
        TextView lblFlag = (TextView)convertView.findViewById(R.id.lblFlag);

        lblDesc.setText(SpinnerPostDataFail.Desc);
        lblIDX.setText(SpinnerPostDataFail.IDX);
        lblStatusCodesDescEN.setText(SpinnerPostDataFail.DescEN);
        lblFlag.setText(String.valueOf(SpinnerPostDataFail.Flag));

        return convertView;*/

        try {
            holder h;
            Spinner_PostDataFail SpinnerPostDataFail = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_spinnerfail, parent, false);
                h = new holder();
                h.lblStatusCodesDesc = convertView.findViewById(R.id.lblStatusCodesDesc);
                h.lblStatusCodesIDX = convertView.findViewById(R.id.lblStatusCodesIDX);
                h.lblStatusCodesDescEN = convertView.findViewById(R.id.lblStatusCodesDescEN);
                h.lblFlag = convertView.findViewById(R.id.lblFlag);
                convertView.setTag(h);
            } else {
                h =( holder) convertView.getTag();
            }

            h.lblStatusCodesDesc.setText(SpinnerPostDataFail.Desc);
            h.lblStatusCodesIDX.setText(SpinnerPostDataFail.IDX);
            h.lblStatusCodesDescEN.setText(SpinnerPostDataFail.DescEN);
            h.lblFlag.setText(String.valueOf(SpinnerPostDataFail.Flag));

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Spinner_PostItemAdapterFail getCustomView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertView;
        }
    }


    public final class holder {
        TextView lblStatusCodesDesc;
        TextView lblStatusCodesIDX;
        TextView lblStatusCodesDescEN;
        TextView lblFlag;
    }
}
