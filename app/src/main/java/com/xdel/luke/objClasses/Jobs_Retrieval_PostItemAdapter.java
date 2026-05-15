package com.xdel.luke.objClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.R;
import com.xdel.luke.setDunningDeliveredStatusActivity;

import java.util.ArrayList;

/**
 * Created by kean.ong on 31/08/2017.
 */
//public class Jobs_Retrieval_PostItemAdapter   extends ArrayAdapter<Jobs_Retrieval_Tags_PostData> {
public class Jobs_Retrieval_PostItemAdapter   extends ArrayAdapter<objCabinetItems> {

    //public Jobs_Retrieval_PostItemAdapter(Context context, ArrayList<Jobs_Retrieval_Tags_PostData> postdata){
    public Jobs_Retrieval_PostItemAdapter(Context context, ArrayList<objCabinetItems> postdata){
        super(context, 0, postdata);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context ctx = myApp.getAppContext();
        //Jobs_Retrieval_Tags_PostData tempChild = getItem(position);
        objCabinetItems tempChild = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.jobs_retrieval_item, parent, false);
        }

        TextView lblCnno = (TextView) convertView.findViewById(R.id.lblCnno);
        //lblCnno.setText(tempChild.tagID);
        lblCnno.setText(tempChild.BagID);

        return convertView;
    }
}
