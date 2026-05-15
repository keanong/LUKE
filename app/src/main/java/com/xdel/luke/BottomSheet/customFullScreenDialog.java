package com.xdel.luke.BottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.templogger.DeviceInfo;

import java.util.ArrayList;

public class customFullScreenDialog extends DialogFragment implements DialogInterface.OnShowListener {
    private Toolbar toolbar;
    private LinearLayout LLcustomToNote_msg;
    private LinearLayout LLcustomCanCannot_msg;
    private ArrayList<String> pudlsi;
    private ArrayList<String> tickSpecInst;
    private ArrayList<String> crossSpecInst;
    private ArrayList<String> notice;
    private boolean hasColdItem;
    private boolean showTL;
    public int Device_Type;

    public customFullScreenDialog (){

    }

    public void setDevice_Type(int device_Type) {
        Device_Type = device_Type;
    }

    public boolean isShowTL() {
        return showTL;
    }

    public void setShowTL(boolean showTL) {
        this.showTL = showTL;
    }

    public void setHasColdItem(boolean ahasColdItem) { this.hasColdItem = ahasColdItem; }

    public void setPudlsi(ArrayList<String> a) {
        this.pudlsi = a;
    }

    public ArrayList<String> getPudlsi() {
        return pudlsi;
    }

    public void setNotice(ArrayList<String> notice) {
        this.notice = notice;
    }

    public ArrayList<String> getNotice() {
        return notice;
    }

    public void setTickSpecInst(ArrayList<String> tickSpecInst) {
        this.tickSpecInst = tickSpecInst;
    }

    public ArrayList<String> getTickSpecInst() {
        return tickSpecInst;
    }

    public void setCrossSpecInst(ArrayList<String> crossSpecInst) {
        this.crossSpecInst = crossSpecInst;
    }

    public ArrayList<String> getCrossSpecInst() {
        return crossSpecInst;
    }

    public static customFullScreenDialog display(FragmentManager fragmentManager) {
        customFullScreenDialog fs = new customFullScreenDialog();
        try {
            fs.show(fragmentManager, "customFullScreenDialog_dialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialog display. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return fs;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
            dialog.setCancelable(false);

        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        try {
            toolbar = (Toolbar) getDialog().findViewById(R.id.toolbar);
            final Toolbar tb = toolbar;
            new CountDownTimer(1100, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tb.setNavigationIcon(
                            (millisUntilFinished > 0 && millisUntilFinished <= 1100) ? R.mipmap.num_1 :
                                    (millisUntilFinished > 1100 && millisUntilFinished <= 2100) ? R.mipmap.num_2 :
                                            (millisUntilFinished > 2100 && millisUntilFinished <= 3500) ? R.mipmap.num_3 :
                                                    R.mipmap.ic_action_failpu_white

                    );
                }
                @Override
                public void onFinish() {
                    tb.setNavigationIcon(R.mipmap.ic_action_failpu_white);
                    tb.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customFullScreenDialog.this.dismiss();
                        }
                    });
                    getDialog().setCancelable(true);
                }
            }.start();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialog onShow. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_pudlsi_dialog, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        LLcustomToNote_msg = view.findViewById(R.id.LLcustomToNote_msg);
        LLcustomCanCannot_msg = view.findViewById(R.id.LLcustomCanCannot_msg);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customFullScreenDialog.this.dismiss();
            }
        });*/
        toolbar.setNavigationOnClickListener(null);
        toolbar.setTitle(myApp.getAppContext().getString(R.string.tonote));
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.inflateMenu(R.menu.menu_customfullscreen_dialog);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                customFullScreenDialog.this.dismiss();
                return true;
            }
        });

        Dialog dialog = getDialog();
        dialog.setOnShowListener(this);
        boolean toshow = false;
        try {
            Context c = myApp.getAppContext();
            if (hasColdItem && LLcustomToNote_msg != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                TextView tv = new TextView(c);
                tv.setText(c.getString(R.string.jobtype_cold) + (showTL ? "\n\n" + (Device_Type == 22 ? c.getString(R.string.temp_prompt) : c.getString(R.string.temp_prompt_manual)) : ""));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                tv.setAllCaps(true);
                tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                tv.setTypeface(null, Typeface.BOLD);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setPadding(75, 40, 75, 40);
                tv.setLayoutParams(params);
                tv.setBackgroundColor(ContextCompat.getColor(c, R.color.cold));
                LLcustomToNote_msg.addView(tv);
                toshow = true;
            }

            if (notice != null && notice.size() > 0) {
                if (LLcustomToNote_msg != null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);
                    for (int i = 0; i <= notice.size() -1; i++) {
                        TextView tv = new TextView(c);
                        tv.setText(notice.get(i));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        tv.setAllCaps(true);
                        tv.setTextColor(i == 0 ? ContextCompat.getColor(c, R.color.White) : ContextCompat.getColor(c, R.color.black));
                        tv.setTypeface(null, Typeface.BOLD);
                        tv.setGravity(i == 0 ? Gravity.CENTER : Gravity.CENTER_VERTICAL);
                        tv.setPadding(75, 40, 75, 40);
                        tv.setLayoutParams(params);
                        tv.setBackgroundColor(
                                i == 0 ? ContextCompat.getColor(c, R.color.menu_red) :
                                        i % 2 == 0 ? ContextCompat.getColor(c, R.color.cold)
                                                : ContextCompat.getColor(c, R.color.instructions_orange));
                        LLcustomToNote_msg.addView(tv);
                        toshow = true;
                    }
                }
            }

            if (pudlsi != null && pudlsi.size() > 0) {
                if (LLcustomToNote_msg != null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);
                    for (int i = 0; i <= pudlsi.size() -1; i++) {
                        TextView tv = new TextView(c);
                        tv.setText(pudlsi.get(i));
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                        tv.setAllCaps(true);
                        tv.setTextColor(ContextCompat.getColor(c, R.color.black));
                        tv.setTypeface(null, Typeface.BOLD);
                        tv.setGravity(Gravity.CENTER_VERTICAL);
                        tv.setPadding(75, 40, 75, 40);
                        tv.setLayoutParams(params);
                        tv.setBackgroundColor(ContextCompat.getColor(c, R.color.instructions_orange));
                        LLcustomToNote_msg.addView(tv);
                        toshow = true;
                    }
                }
            }

            if ((tickSpecInst != null && tickSpecInst.size() > 0) || (crossSpecInst != null && crossSpecInst.size() > 0)) {
                if (LLcustomToNote_msg != null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);

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
                            toshow = true;
                        }
                    }
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
                            toshow = true;
                        }
                    }
                }
            }

            if (!toshow) {
                customFullScreenDialog.this.dismiss();
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at customFullScreenDialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }

    }

}
