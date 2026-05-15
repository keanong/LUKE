package com.xdel.luke;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class objGlobalMethods2 {

    public void showTickCrossNoteMsgPrompt(String note, ArrayList<String> tickSpecInst, ArrayList<String> crossSpecInst) {
        Context c = myApp.getAppContext();
        int added = 0;
        try {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            final AlertDialog.Builder ToNoteBuilder = new AlertDialog.Builder(myApp.currentActivity);
            final View ToNotedv = myApp.currentActivity.getLayoutInflater().inflate(R.layout.custom_tonote_dialog_2, null);
            ToNoteBuilder.setView(ToNotedv);
            ToNoteBuilder.setCancelable(false);
            TextView txtToNoteTitle = ToNotedv.findViewById(R.id.txtToNoteTitle);
            txtToNoteTitle.setText(c.getString(R.string.tonote));
            TextView btnCustomToNoteNeutral = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteNeutral);
            TextView btnCustomToNoteOK = (TextView) ToNotedv.findViewById(R.id.btnCustomToNoteOK);
            LinearLayout LLcustomToNote_msg = (LinearLayout) ToNotedv.findViewById(R.id.LLcustomToNote_msg);

            if (tickSpecInst != null && tickSpecInst.size() > 0) {
                for (int ts = 0; ts <= tickSpecInst.size() - 1; ts++) {
                    TextView tv = new TextView(c);
                    tv.setText(tickSpecInst.get(ts));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tv.setAllCaps(true);
                    tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setGravity(Gravity.CENTER_VERTICAL);
                    tv.setPadding(50, 0, 75, 0);
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_ok_black, 0, 0, 0);
                    tv.setLayoutParams(params);
                    tv.setBackgroundColor(ContextCompat.getColor(c, R.color.brightergreen));
                    LLcustomToNote_msg.addView(tv);
                    added++;
                }
            }
            if (crossSpecInst != null && crossSpecInst.size() > 0) {
                if (crossSpecInst != null && crossSpecInst.size() > 0) {
                    for (int cs = 0; cs <= crossSpecInst.size() - 1; cs++) {
                        TextView tv = new TextView(c);
                        tv.setText(crossSpecInst.get(cs));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        tv.setTextColor(ContextCompat.getColor(c, R.color.White));
                        tv.setAllCaps(true);
                        tv.setTypeface(null, Typeface.BOLD);
                        tv.setGravity(Gravity.CENTER_VERTICAL);
                        tv.setPadding(50, 0, 75, 0);
                        tv.setLayoutParams(params);
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_action_failpu_white, 0, 0, 0);
                        tv.setBackgroundColor(ContextCompat.getColor(c, R.color.menu_red));
                        LLcustomToNote_msg.addView(tv);
                        added++;
                    }
                }
            }

            TextView tv = new TextView(c);
            tv.setText(note);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setAllCaps(true);
            tv.setTextColor(ContextCompat.getColor(c, R.color.black));
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setPadding(75, 40, 75, 40);
            tv.setLayoutParams(params);
            tv.setBackground(c.getDrawable(R.drawable.bottom_border));
            LLcustomToNote_msg.addView(tv);

            final AlertDialog ToNotedvd = ToNoteBuilder.create();

            btnCustomToNoteOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ToNotedvd.cancel();
                    } catch (Exception e) {

                    }
                }
            });

            if (added > 0) {
                ToNotedvd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ToNotedvd.show();
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objGlobalMethods2 showTickCrossNoteMsgPrompt. Exception: " + e.getMessage(), "", "", "", "");
        }
    }

}
