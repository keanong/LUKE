package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.R;
import com.xdel.luke.sqlite.dbObjTradeInList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kean.ong on 28/06/2017.
 */
public class Spinner_TradeInModelPostItemAdapter extends ArrayAdapter<dbObjTradeInList> {
    private Activity myContext;

    public Spinner_TradeInModelPostItemAdapter(Context context, List<dbObjTradeInList> tradeinlist){
        super(context,0, tradeinlist);
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

        dbObjTradeInList data = getItem(position);
// Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trade_in_model_spinner, parent, false);
        }

        TextView lblModelDesc = (TextView)convertView.findViewById(R.id.lblModelDesc);
        TextView lblModelValue = (TextView)convertView.findViewById(R.id.lblModelValue);

        lblModelDesc.setText(data.BrandModel);
        lblModelValue.setText(String.valueOf(data.Value));
        return convertView;
    }
}
