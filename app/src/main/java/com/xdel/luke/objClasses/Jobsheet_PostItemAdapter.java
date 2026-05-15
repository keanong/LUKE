package com.xdel.luke.objClasses;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.JobsheetSubmitActivity;
import com.xdel.luke.R;

import java.util.ArrayList;

/**
 * Created by kean.ong on 17/08/2015.
 */
public class Jobsheet_PostItemAdapter extends ArrayAdapter<Jobsheet_PostData> {
    private ArrayList<Jobsheet_PostData> list;

    public Jobsheet_PostItemAdapter(Context context, ArrayList<Jobsheet_PostData> jobsheetPostData){
        super(context, 0, jobsheetPostData);
        list = jobsheetPostData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Jobsheet_PostData jobsheetPostData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.job_sheet_row, parent, false);
        }
        LinearLayout LLlayout = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
        TextView lblJobsheetItemTitle = (TextView) convertView.findViewById(R.id.lblJobsheetItemTitle);

        // Populate the data into the template view using the data object
        String val = jobsheetPostData.postTitle;
        lblJobsheetItemTitle.setText(val);
        JobsheetSubmitActivity.focusTextbox();

        if (jobsheetPostData.blink) {
            manageBlinkEffect(LLlayout);
        }
        return convertView;
    }

    private void manageBlinkEffect(final LinearLayout ll) {
        try {
            ObjectAnimator anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.WHITE, Color.GREEN, Color.WHITE);

            anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.GREEN, R.color.White, Color.GREEN);
            anim.setDuration(300);
            anim.setEvaluator(new ArgbEvaluator());
            anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.setRepeatCount(1);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ll.setBackgroundResource(R.drawable.list_selector_white);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();
        } catch (Exception e) {
            String s = e.getMessage();
        } finally {
            setAllItemsBlinkFalse();
        }
    }

    private void setAllItemsBlinkFalse(){
        try {
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
