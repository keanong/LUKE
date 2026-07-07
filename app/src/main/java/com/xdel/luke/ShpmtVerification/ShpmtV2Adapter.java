package com.xdel.luke.ShpmtVerification;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.JobsListGroupActivity;
import com.xdel.luke.R;
import com.xdel.luke.jobinfo.JobInfoLite;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.List;

public class ShpmtV2Adapter extends ArrayAdapter<ShpmtV2data> {
    private ArrayList<ShpmtV2data> list;
    private Context ctx;

    public ShpmtV2Adapter(Context context, ArrayList<ShpmtV2data> alist) {
        super(context, 0, alist);
        list = alist;
        this.ctx = context;
    }

    @Override
    public View getView(int position, View vw, ViewGroup parent) {
        ViewHolder v;
        ShpmtV2data d = getItem(position);
        JobInfoLite ji = null;
        try {
            if (vw == null) {
                vw = LayoutInflater.from(getContext()).inflate(R.layout.shpmtv2_row, parent, false);
                v = new ViewHolder();
                v.lblRef = vw.findViewById(R.id.lblRef);
                v.lblexpected = vw.findViewById(R.id.lblexpected);
                v.lblactual = vw.findViewById(R.id.lblactual);
                v.txtactual = vw.findViewById(R.id.txtactual);
                v.linearLayout1 = vw.findViewById(R.id.linearLayout1);
                v.LLtxtactual = vw.findViewById(R.id.LLtxtactual);
                vw.setTag(v);
            } else {
                v = (ViewHolder) vw.getTag();
            }

            ji = d.ji;
            String ref = ji != null && !objGlobalMethods.IsEmptyString(ji.ConsignmentNote) ? ji.ConsignmentNote : !objGlobalMethods.IsEmptyString(ji.JobNo) ? ji.JobNo : "";
            v.lblRef.setText(ref);
            v.lblexpected.setText(String.valueOf(d.expectedc));
            v.lblactual.setText(String.valueOf(d.actualc));
            v.txtactual.setText(String.valueOf(d.actualc));
            v.lblactual.setVisibility(d.ji.isAlps ? View.VISIBLE : View.GONE);
            v.LLtxtactual.setVisibility(!d.ji.isAlps ? View.VISIBLE : View.GONE);

            if (v.OnClickListenerlblRef != null) {
                v.lblRef.setOnClickListener(null);
                v.OnClickListenerlblRef = null;
            }
            if (v.OnClickListenerlblexpected != null) {
                v.lblexpected.setOnClickListener(null);
                v.OnClickListenerlblexpected = null;
            }
            v.OnClickListenerlblRef = hidekeyboard_clickListener();
            v.lblRef.setOnClickListener(v.OnClickListenerlblRef);
            v.OnClickListenerlblexpected = hidekeyboard_clickListener();
            v.lblexpected.setOnClickListener(v.OnClickListenerlblexpected);

            // Remove previous listener to avoid duplication
            if (v.txtactual.getTag() instanceof TextWatcher)
                v.txtactual.removeTextChangedListener((TextWatcher) v.txtactual.getTag());

            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() == 0) {
                            /*d.setActualc2(0);
                            d.setVerified(false);*/
                        } else {
                            try {
                                int val = Integer.parseInt(s.toString());
                                if (d.verified) {
                                    d.setActualc2(val);
                                    d.setVerified(!d.ji.isAlps ? (val > 0) : (val >= d.expectedc));
                                } else {
                                    d.setActualc2(val);
                                    d.setVerified(false);
                                }
                                /*d.setActualc2(val);
                                d.setVerified(!d.ji.isAlps ? (val > 0) : (val >= d.expectedc));*/
                            } catch (NumberFormatException e) {
                                d.setActualc2(0);
                                d.setVerified(false);
                            }
                        }
                        applyBackground(v, d);
                        final ShpmtV2Activity act = (ShpmtV2Activity) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            public void run() {
                                // Notifiy activity
                                act.canProceedCheck();
                            }
                        });
                    } catch (Exception e) {
                        String sss = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at ShpmtV2Adapter getView TextWatcher. Exception: " + e.getMessage()
                                + "\nStack: " + sss,"","","","");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            };

            v.txtactual.addTextChangedListener(watcher);
            v.txtactual.setTag(watcher);

            // Remove previous listener if exists
            if (v.txtactual.getTag(R.id.focus_listener_tag) instanceof View.OnFocusChangeListener)
                v.txtactual.setOnFocusChangeListener(null);

            View.OnFocusChangeListener focusListener = (view, hasFocus) -> {
                try {
                    if (!hasFocus) {
                        String input = v.txtactual.getText().toString().trim();
                        if (input.isEmpty()) {
                            d.setActualc2(0);
                            d.setVerified(false);
                            v.txtactual.setText("0");
                        } else {
                            try {
                                int val = Integer.parseInt(input); // strips leading zeros
                                /*d.setActualc2(val);
                                d.setVerified(!d.ji.isAlps ? (val > 0) : (val >= d.expectedc));
                                v.txtactual.setText(String.valueOf(val));*/
                                if (d.verified) {
                                    d.setActualc2(val);
                                    d.setVerified(!d.ji.isAlps ? (val > 0) : (val >= d.expectedc));
                                    v.txtactual.setText(String.valueOf(val));
                                } else {
                                    d.setActualc2(val);
                                    d.setVerified(false);
                                    v.txtactual.setText(String.valueOf(val));
                                }
                            } catch (NumberFormatException e) {
                                d.setActualc2(0);
                                d.setVerified(false);
                                v.txtactual.setText("0");
                            }
                        }
                        applyBackground(v, d);
                        v.lblactual.setText(String.valueOf(d.getActualc())); // keep lblactual synced
                    } else {
                        String input = v.txtactual.getText().toString().trim();
                        if (input.equals("0")) {
                            d.setActualc2(0);
                            d.setVerified(false);
                            v.txtactual.setText("");
                        }
                    }
                } catch (Exception e) {
                    String sss = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at ShpmtV2Adapter getView focusListener. Exception: " + e.getMessage()
                            + "\nStack: " + sss,"","","","");
                }
            };

            v.txtactual.setOnFocusChangeListener(focusListener);
            v.txtactual.setTag(R.id.focus_listener_tag, focusListener);

            // --- Safe InputFilter for leading zero handling ---
            InputFilter[] oldFilters = v.txtactual.getFilters();
            List<InputFilter> newFilters = new ArrayList<>();
            for (InputFilter f : oldFilters) {
                // filter out our custom one if already applied
                if (!(f instanceof InputFilterLeadingZero)) {
                    newFilters.add(f);
                }
            }

            // Add our custom filter
            newFilters.add(new InputFilterLeadingZero());
            v.txtactual.setFilters(newFilters.toArray(new InputFilter[0]));

            // Apply background depending on blink/verified state
            applyBackground(v, d);

        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at ShpmtV2Adapter getView. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return vw;
        }
    }

    private void applyBackground(ViewHolder v, ShpmtV2data d) {
        if (ctx == null)
            ctx = myApp.getAppContext();
        if (d.verified) {
            v.lblRef.setBackground(ctx.getDrawable(R.drawable.rect_border_lrb_brightergreen));
            v.lblexpected.setBackground(ctx.getDrawable((d.ji != null && d.ji.hasColdItem) ?  R.drawable.rect_border_rb_cold : R.drawable.rect_border_rb_brightergreen));
            v.lblactual.setBackground(ctx.getDrawable(R.drawable.rect_border_rb_brightergreen));
            v.LLtxtactual.setBackground(ctx.getDrawable(R.drawable.rect_border_rb_brightergreen));
        } else {
            v.lblRef.setBackground(ctx.getDrawable(!d.verified ? R.drawable.rect_border_lrb : R.drawable.rect_border_lrb_brightergreen));
            if ((d.ji != null && d.ji.hasColdItem)) {
                v.lblexpected.setBackground(ctx.getDrawable(R.drawable.rect_border_rb_cold ));
            } else {
                v.lblexpected.setBackground(ctx.getDrawable(!d.verified ? R.drawable.rect_border_rb : R.drawable.rect_border_rb_brightergreen));
            }

            v.lblactual.setBackground(ctx.getDrawable(!d.verified ? R.drawable.rect_border_rb : R.drawable.rect_border_rb_brightergreen));
            v.LLtxtactual.setBackground(ctx.getDrawable(!d.verified ? R.drawable.rect_border_rb : R.drawable.rect_border_rb_brightergreen));
        }
    }

    public View.OnClickListener hidekeyboard_clickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final ShpmtV2Activity act = (ShpmtV2Activity) myApp.currentActivity;
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            // Notifiy activity
                            act.hidekeyboard();
                        }
                    });
                } catch (Exception e) {
                    String s = JobsHandler.getStackTraceString(e);
                    objGlobalMethods.writeToFile("Exception at ShpmtV2Adapter hidekeyboard_clickListener. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
                }
            }
        };

    }

    // Prevents leading zeros except for "0"
    private static class InputFilterLeadingZero implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            String newText = dest.subSequence(0, dstart)
                    + source.toString()
                    + dest.subSequence(dend, dest.length());

            // Allow just "0"
            if (newText.equals("0")) {
                return null;
            }

            // Block if it starts with leading zero
            if (newText.startsWith("0")) {
                return "";
            }

            return null; // accept normally
        }
    }

    public final class ViewHolder {
        TextView lblRef;
        TextView lblexpected;
        TextView lblactual;
        EditText txtactual;
        LinearLayout linearLayout1;
        LinearLayout LLtxtactual;
        View.OnClickListener OnClickListenerlblRef;
        View.OnClickListener OnClickListenerlblexpected;
    }

}
