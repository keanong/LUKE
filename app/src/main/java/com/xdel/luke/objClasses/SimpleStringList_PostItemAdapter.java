package com.xdel.luke.objClasses;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.JobsheetSubmitActivity;
import com.xdel.luke.R;

import java.util.ArrayList;

/**
 * Created by kean.ong on 25/08/2015.
 */
public class SimpleStringList_PostItemAdapter extends ArrayAdapter<SimpleStringList_PostData>{

    public SimpleStringList_PostItemAdapter(Context context, ArrayList<SimpleStringList_PostData> SimpleStringListPostData){
        super(context, 0, SimpleStringListPostData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SimpleStringList_PostData SimpleStringListPostData = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simpleliststring_row, parent, false);
        }
        // Lookup view for data population
        TextView lblSimpleStringListItemTitle = (TextView) convertView.findViewById(R.id.lblSimpleStringListItemTitle);

        // Populate the data into the template view using the data object
        String val = SimpleStringListPostData.postTitle;
        lblSimpleStringListItemTitle.setText(val);
        if (SimpleStringListPostData.changeColor == 0) {
            lblSimpleStringListItemTitle.setTextColor(Color.parseColor("#000000"));
        } else if (SimpleStringListPostData.changeColor == 1) {
            lblSimpleStringListItemTitle.setTextColor(Color.parseColor("#00ff00"));
        } else if (SimpleStringListPostData.changeColor == 2) {
            lblSimpleStringListItemTitle.setTextColor(Color.parseColor("#ffff0000"));
        }
        return convertView;
    }
}
