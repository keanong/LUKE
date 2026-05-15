package com.xdel.luke.objClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xdel.luke.CreditCardActivity;
import com.xdel.luke.JobsheetSubmitActivity;
import com.xdel.luke.R;

import java.util.ArrayList;

/**
 * Created by kean.ong on 21/08/2015.
 */
public class CreditCard_PostItemAdapter extends ArrayAdapter<CreditCard_PostData> {

    public CreditCard_PostItemAdapter(Context context, ArrayList<CreditCard_PostData> CreditCardPostData){
        super(context, 0, CreditCardPostData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CreditCard_PostData CreditCardPostData = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.creditcard_row, parent, false);
        }
        // Lookup view for data population
        TextView lblCreditCardItemTitle = (TextView) convertView.findViewById(R.id.lblCreditCardItemTitle);
        TextView lblCreditCardItemPostalTitle = (TextView) convertView.findViewById(R.id.lblCreditCardItemPostalTitle);

        //// Populate the data into the template view using the data object
        //String val = CreditCardPostData.ConsignmentNote + " - " + CreditCardPostData.PostalCode;
        String val = CreditCardPostData.ConsignmentNote;
        lblCreditCardItemTitle.setText(val);
        lblCreditCardItemPostalTitle.setText(CreditCardPostData.PostalCode);
        return convertView;
    }
}
