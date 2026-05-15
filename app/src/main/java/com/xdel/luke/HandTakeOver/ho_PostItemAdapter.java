package com.xdel.luke.HandTakeOver;

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

import com.xdel.luke.R;

import java.util.ArrayList;

public class ho_PostItemAdapter extends ArrayAdapter<ho_PostData> {
    private ArrayList<ho_PostData> list;

    public ho_PostItemAdapter(Context context, ArrayList<ho_PostData> hoPostData){
        super(context, 0, hoPostData);
        list = hoPostData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ho_PostData d = getItem(position);
        //String d = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bulkpickup_row, parent, false);
        }
        LinearLayout LLlayout = (LinearLayout) convertView.findViewById(R.id.linearLayout1);
        TextView lblBulkPickUpItemTitle = (TextView) convertView.findViewById(R.id.lblBulkPickUpItemTitle);

        lblBulkPickUpItemTitle.setText(d.ref);
        HandoverActivity.focusTextbox();

        if (d.blink) {
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
                    ll.setBackgroundResource(R.drawable.list_selector);
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
