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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kean.ong on 23/02/2016.
 */
public class Spinner_PostItemAdapterOKP   extends ArrayAdapter<Spinner_PostDataOKP> {

    private Activity myContext;
    private Spinner_PostDataOKP[] datas;
    private holder h;

    public Spinner_PostItemAdapterOKP(Context context, ArrayList<Spinner_PostDataOKP> SpinnerPostDataOKP){
        super(context,0, SpinnerPostDataOKP);
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
        try {
            /*Spinner_PostDataOKP SpinnerPostDatas = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_spinnerokp, parent, false);
            }

            TextView lblDesc = (TextView)convertView.findViewById(R.id.lblStatusCodesDesc);
            TextView lblDescEN = (TextView)convertView.findViewById(R.id.lblStatusCodesDescEN);
            TextView lblIDX = (TextView)convertView.findViewById(R.id.lblStatusCodesIDX);
            TextView lblName_Required = (TextView) convertView.findViewById(R.id.lblName_Required);
            TextView lblIsHuman = (TextView) convertView.findViewById(R.id.lblIsHuman);
            TextView lblInstructions = (TextView) convertView.findViewById(R.id.lblInstructions);
            TextView lblsubcatEN = (TextView) convertView.findViewById(R.id.lblsubcatEN);
            TextView lblsubcatZH = (TextView) convertView.findViewById(R.id.lblsubcatZH);

            lblDesc.setText(SpinnerPostDatas.Desc);
            lblDescEN.setText(SpinnerPostDatas.DescEN);
            lblIDX.setText(SpinnerPostDatas.IDX);
            lblName_Required.setText(SpinnerPostDatas.Name_Required);
            lblIsHuman.setText(SpinnerPostDatas.ishuman);
            lblInstructions.setText(SpinnerPostDatas.instructions);
            lblsubcatEN.setText(SpinnerPostDatas.subcatEN);
            lblsubcatZH.setText(SpinnerPostDatas.subcatZH);*/

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.default_spinnerokp, parent, false);
                h = new holder();
                h.lblStatusCodesDesc = convertView.findViewById(R.id.lblStatusCodesDesc);
                h.lblStatusCodesDescEN = convertView.findViewById(R.id.lblStatusCodesDescEN);
                h.lblStatusCodesIDX = convertView.findViewById(R.id.lblStatusCodesIDX);
                h.lblName_Required = convertView.findViewById(R.id.lblName_Required);
                h.lblIsHuman = convertView.findViewById(R.id.lblIsHuman);
                h.lblInstructions = convertView.findViewById(R.id.lblInstructions);
                h.lblsubcatEN = convertView.findViewById(R.id.lblsubcatEN);
                h.lblsubcatZH = convertView.findViewById(R.id.lblsubcatZH);
                h.lblFlag = convertView.findViewById(R.id.lblFlag);
                convertView.setTag(h);
            } else {
                h = (holder) convertView.getTag();
            }

            Spinner_PostDataOKP SpinnerPostDatas = getItem(position);

            h.lblStatusCodesDesc.setText(SpinnerPostDatas.Desc);
            h.lblStatusCodesDescEN.setText(SpinnerPostDatas.DescEN);
            h.lblStatusCodesIDX.setText(SpinnerPostDatas.IDX);
            h.lblName_Required.setText(SpinnerPostDatas.Name_Required);
            h.lblIsHuman.setText(SpinnerPostDatas.ishuman);
            h.lblInstructions.setText(SpinnerPostDatas.instructions);
            h.lblsubcatEN.setText(SpinnerPostDatas.subcatEN);
            h.lblsubcatZH.setText(SpinnerPostDatas.subcatZH);
            h.lblFlag.setText(String.valueOf(SpinnerPostDatas.Flag));
        } catch ( Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at Spinner_PostItemAdapterOKP getCustomView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertView;
        }
    }

    public final class holder {
        TextView lblStatusCodesDesc;
        TextView lblStatusCodesDescEN;
        TextView lblStatusCodesIDX;
        TextView lblName_Required;
        TextView lblIsHuman;
        TextView lblInstructions;
        TextView lblsubcatEN;
        TextView lblsubcatZH;
        TextView lblFlag;
    }


}
