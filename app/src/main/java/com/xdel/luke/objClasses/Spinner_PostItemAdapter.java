package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.R;

import java.util.ArrayList;

/**
 * Created by kean.ong on 26/06/2015.
 */
public class Spinner_PostItemAdapter   extends ArrayAdapter<Spinner_PostData> {

    private Activity myContext;
    private Spinner_PostData[] datas;
    private holder h;

    public Spinner_PostItemAdapter(Context context, ArrayList<Spinner_PostData> SpinnerPostData){
        super(context,0, SpinnerPostData);
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
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        /*Spinner_PostData SpinnerPostDatas = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_spinner, parent, false);
        }

        TextView lblDesc = (TextView)convertView.findViewById(R.id.lblStatusCodesDesc);
        TextView lblIDX = (TextView)convertView.findViewById(R.id.lblStatusCodesIDX);
        TextView lblStatusCodesDescEN = (TextView)convertView.findViewById(R.id.lblStatusCodesDescEN);

        lblDesc.setText(SpinnerPostDatas.Desc);
        lblIDX.setText(SpinnerPostDatas.IDX);
        lblStatusCodesDescEN.setText(SpinnerPostDatas.DescEN);*/

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_spinner, parent, false);
            h = new holder();
            h.lblDesc = convertView.findViewById(R.id.lblStatusCodesDesc);
            h.lblIDX = convertView.findViewById(R.id.lblStatusCodesIDX);
            h.lblStatusCodesDescEN = convertView.findViewById(R.id.lblStatusCodesDescEN);
            convertView.setTag(h);
        } else {
            h = (holder) convertView.getTag();
        }

        Spinner_PostData SpinnerPostDatas = getItem(position);

        h.lblDesc.setText(SpinnerPostDatas.Desc);
        h.lblIDX.setText(SpinnerPostDatas.IDX);
        h.lblStatusCodesDescEN.setText(SpinnerPostDatas.DescEN);

        return convertView;
    }

    public final class holder {
        TextView lblDesc;
        TextView lblIDX;
        TextView lblStatusCodesDescEN;
    }


}
