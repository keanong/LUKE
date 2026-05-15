package com.xdel.luke.messagingClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xdel.luke.MainActivity;
import com.xdel.luke.Messaging;
import com.xdel.luke.R;
import com.xdel.luke.messagingClasses.MessagingList_PostData;
import com.xdel.luke.objClasses.AddressList_PostData;
import com.xdel.luke.objClasses.myApp;

import java.util.ArrayList;

/**
 * Created by kean.ong on 14/12/2015.
 */
public class MessagingList_PostItemAdapter extends BaseAdapter {
//public class MessagingList_PostItemAdapter extends ArrayAdapter<MessagingList_PostData> {

    private Activity activity;
    private LayoutInflater inflater;
    private Context context;
    public ArrayList<MessagingList_PostData> datas;

    public MessagingList_PostItemAdapter(Context context, ArrayList<MessagingList_PostData> MessagingListPostData){
        this.context = context;
        this.datas = MessagingListPostData;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = null;
        MessagingList_PostData MessagingListPostData = datas.get(position);
        ViewHolder1 h;

        if (convertView == null){
            v = LayoutInflater.from(context).inflate(R.layout.messaginglist_row, null, false);
            h = new ViewHolder1();
            h.lblMsgUserIDX = (TextView) v.findViewById(R.id.lblMsgUserIDX);
            h.lblMsgUserName = (TextView) v.findViewById(R.id.lblMsgUserName);
            h.lblMsgJobsIDX = (TextView) v.findViewById(R.id.lblMsgJobsIDX);
            v.setTag(h);
        }
        else {
            v = convertView;
            h = (ViewHolder1) v.getTag();
        }

        h.lblMsgUserIDX.setText(MessagingListPostData.UserIDX);
        h.lblMsgUserName.setText(MessagingListPostData.UserName);
        h.lblMsgJobsIDX.setText(MessagingListPostData.JobsIDX);
        h.lblMsgUserName.setTextColor(MessagingListPostData.changeCountColorBool ? Color.parseColor("#0026ff") : Color.RED);
        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder1 {
        public TextView lblMsgUserName;
        public TextView lblMsgUserIDX;
        public TextView lblMsgJobsIDX;
    }


}
