package com.xdel.luke.inventory;

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

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class StockRef_Adapter extends ArrayAdapter<StockRef> {
    private ArrayList<StockRef> list;
    private Context ct;

    public StockRef_Adapter(Context ctx, ArrayList<StockRef> data) {
        super(ctx, 0, data);
        list = data;
        ct = ctx;
    }

    @Override
    public View getView(int position, View cv, ViewGroup parent) {
        ViewHolder v;
        try {
            StockRef d = getItem(position);
            if (cv == null) {
                cv = LayoutInflater.from(getContext()).inflate(R.layout.stockref_row, parent, false);
                v = new ViewHolder();
                v.linearLayout1 = cv.findViewById(R.id.linearLayout1);
                v.lblStockRef = cv.findViewById(R.id.lblStockRef);
                cv.setTag(v);
            } else {
                v = (ViewHolder) cv.getTag();
            }

            v.lblStockRef.setText(d.StockReference);
            boolean vf = d.verified;
            //v.linearLayout1.setBackgroundColor(vf ? Color.GREEN : Color.WHITE);
            v.linearLayout1.setBackground(ct.getDrawable(vf ? R.drawable.tabstyle_base_black_bg_green : R.drawable.tabstyle_base_black_bg_white));

            /*if (d.blink) {
                manageBlinkEffect(v.linearLayout1);
            }*/

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at StockRef_Adapter getView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return cv;
        }
    }

    private void manageBlinkEffect(LinearLayout ll) {
        try {
            ObjectAnimator anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.WHITE, Color.GREEN, Color.WHITE);
            /*if (InitalColorCode == 3) {
                anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.RED, Color.WHITE, Color.RED);
            }
            if (InitalColorCode == 1) {
                anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.GREEN, R.color.White, Color.GREEN);
            }*/
            anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.GREEN, R.color.White, Color.GREEN);

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

    public final class ViewHolder {
        LinearLayout linearLayout1;
        TextView lblStockRef;
    }



}
