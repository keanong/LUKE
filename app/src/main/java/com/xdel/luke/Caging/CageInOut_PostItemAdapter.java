package com.xdel.luke.Caging;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class CageInOut_PostItemAdapter extends ArrayAdapter<objCageInOutItem> {
    private ArrayList<objCageInOutItem> list;

    public CageInOut_PostItemAdapter(Context context, ArrayList<objCageInOutItem> CageInOutPostData){
        super(context, 0, CageInOutPostData);
        list = CageInOutPostData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            objCageInOutItem data = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cage_in_out_row, parent, false);
            }
            // Lookup view for data population
            LinearLayout LLlayout = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
            TextView lblJobsheetItemTitle = (TextView) convertView.findViewById(R.id.lblcn);

            // Populate the data into the template view using the data object
            String val = !data.job_ref.isEmpty() && !data.job_ref.equals("") && data.job_ref != "" ? data.job_ref : data.ref;
            lblJobsheetItemTitle.setText(val);

            //data.color == 0: for Cage In or Not scanned by GC yet
            //data.color == 1: scanned by GC as accepted and that was caged in by GL
            //data.color == 2: scanned by GC as rejected and that was caged in by GL
            //data.color == 3: scanned by GC as rejected that was not caged in by GL
            //lblJobsheetItemTitle.setTextColor(data.color == 3 ? Color.WHITE : Color.BLACK);
            //LLlayout.setBackgroundResource(data.color == 1 ? R.color.brightgreen : data.color == 2 ? R.color.mainorange : data.color == 3 ? R.color.colorAccent : R.drawable.list_selector_white);
            lblJobsheetItemTitle.setTextColor(data.color == 3 ? Color.WHITE : Color.BLACK);
            LLlayout.setBackgroundColor(data.color == 1 ? Color.GREEN : data.color == 2 ? Color.RED : data.color == 3 ? Color.RED : Color.WHITE);

            /*if (data.IsNotPartOfCage) {
                lblJobsheetItemTitle.setTextColor(Color.RED);
            } else {
                lblJobsheetItemTitle.setTextColor(Color.BLACK);
            }*/
            CageInOutActivity.focusTextbox();

            if (data.blink) {
                manageBlinkEffect(LLlayout, data.color, data.ref);
            }

        } catch (Exception e){
            objGlobalMethods.writeToFile("Exception at CageInOut_PostItemAdapter getView. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return convertView;
        }
    }

    private void manageBlinkEffect(LinearLayout ll, int InitalColorCode, String ref) {
        try {
            ObjectAnimator anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.WHITE, Color.GREEN, Color.WHITE);
            if (InitalColorCode == 3) {
                anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.RED, Color.WHITE, Color.RED);
            }
            if (InitalColorCode == 1) {
                anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.GREEN, R.color.White, Color.GREEN);
            }

            anim.setDuration(300);
            anim.setEvaluator(new ArgbEvaluator());
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.setRepeatCount(1);
            anim.start();
        } catch (Exception e) {
            String s = e.getMessage();
        } finally {
            setAllItemsBlinkFalse();
        }
    }

    private void setAllItemsBlinkFalse() {
        try {
            /*if (CageInOutActivity.ScannedAItems != null && CageInOutActivity.ScannedAItems.size() > 0) {
                for (int i = 0; i <= CageInOutActivity.ScannedAItems.size()-1; i++) {
                    CageInOutActivity.ScannedAItems.get(i).setBlink(false);
                }
            }*/
            if (list != null && list.size() > 0) {
                for (int i = 0; i <= list.size()-1; i++) {
                    list.get(i).setBlink(false);
                }
            }
        } catch (Exception e){
            String s = e.getMessage();
        } finally{
            notifyDataSetChanged();
        }
    }


}
