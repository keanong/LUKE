package com.xdel.luke.Caging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.R;

import java.util.ArrayList;

public class Min_PostItemAdapter extends ArrayAdapter<Min_PostItem> {
    public Min_PostItemAdapter(Context context, ArrayList<Min_PostItem> data) {
        super(context, 0, data);
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

        Min_PostItem data = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_spinner_common, parent, false);
        }

        TextView lblDesc = (TextView)convertView.findViewById(R.id.lblStatusCodesDesc);
        lblDesc.setText(data.Description);

        return convertView;
    }
}
