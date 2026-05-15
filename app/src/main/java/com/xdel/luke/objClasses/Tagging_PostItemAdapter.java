package com.xdel.luke.objClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.BulkPickUpActivity;
import com.xdel.luke.Label_Tagging_Activity;
import com.xdel.luke.R;

import java.util.ArrayList;

/**
 * Created by kean.ong on 25/04/2016.
 */
public class Tagging_PostItemAdapter extends ArrayAdapter<Tagging_PostData> {

    public Tagging_PostItemAdapter(Context context, ArrayList<Tagging_PostData> TaggingPostData){
        super(context, 0, TaggingPostData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Tagging_PostData TaggingPostData = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bulkpickup_row, parent, false);
        }
        // Lookup view for data population
        TextView lblBulkPickUpItemTitle = (TextView) convertView.findViewById(R.id.lblBulkPickUpItemTitle);

        // Populate the data into the template view using the data object
        String val = TaggingPostData.tagBarcode;
        lblBulkPickUpItemTitle.setText(val);
        Label_Tagging_Activity.focusTextbox();
        return convertView;
    }

}
