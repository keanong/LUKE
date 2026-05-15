package com.xdel.luke.JobInventoryVerify;

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

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class JobInventoryVAdapter extends ArrayAdapter<InventoryEntry> {
    private ArrayList<InventoryEntry> list;
    private Context ct;

    public JobInventoryVAdapter(Context ctx, ArrayList<InventoryEntry> data) {
        super(ctx, 0, data);
        list = data;
        ct = ctx;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder v;

        try {
            InventoryEntry d = getItem(position);
            /*if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.job_inventory_v_row_item, parent, false);
                v = new ViewHolder();
                v.linearLayout1 = convertView.findViewById(R.id.linearLayout1);
                v.lblRef = convertView.findViewById(R.id.lblRef);
                v.lblStockCode = convertView.findViewById(R.id.lblStockCode);
                convertView.setTag(v);
            } else {
                v = (ViewHolder) convertView.getTag();
            }

            v.lblRef.setText(String.valueOf(d.Reference));
            v.lblStockCode.setText(d.StockCode);
            if (d.verified) {
                v.linearLayout1.setBackground(ct.getDrawable(R.drawable.tabstyle_base_black_bg_green));
            } else {
                v.linearLayout1.setBackground(ct.getDrawable(R.drawable.tabstyle_base_black_bg_white));
            }

            if (d.blink) {
                manageBlinkEffect(v.linearLayout1);
            }*/

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.job_inventory_v_row_item, parent, false);
            }
            LinearLayout LLlayout = convertView.findViewById(R.id.LLlayout);
            TextView lblRef = convertView.findViewById(R.id.lblRef);
            TextView lblStockCode = convertView.findViewById(R.id.lblStockCode);

            lblRef.setText(String.valueOf(d.Reference));
            lblStockCode.setText(d.StockCode);
            LLlayout.setBackgroundColor(d.Verified || ((d.Flag & 2) == 2) ? Color.GREEN : Color.WHITE);

            /*if (d.blink && !d.verified) {
                manageBlinkEffect(LLlayout, false);
            } else if (d.blink && d.verified) {
                manageBlinkEffect(LLlayout, true);
            }*/

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at JobInventoryVAdapter getView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return convertView;
        }
    }

    public void setList(ArrayList<InventoryEntry> alist) {
        this.list = alist;
    }

    public ArrayList<InventoryEntry> getList(){
        return list;
    }

    private void manageBlinkEffect(LinearLayout ll, boolean wgwg) {
        try {
            ObjectAnimator anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.WHITE, Color.GREEN, Color.WHITE);
            if (wgwg) {
                anim = ObjectAnimator.ofInt(ll, "backgroundColor", R.color.White, Color.GREEN, R.color.White, Color.GREEN);
            } else {
                anim = ObjectAnimator.ofInt(ll, "backgroundColor", Color.GREEN, R.color.White, Color.GREEN);
            }

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
                    ll.setBackgroundResource(R.drawable.tabstyle_base_black_bg_green);
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

    private void setAllItemsBlinkFalse() {
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

    public final class ViewHolder {
        LinearLayout linearLayout1;
        TextView lblRef;
        TextView lblStockCode;
    }

}
