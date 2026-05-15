package com.xdel.luke.objClasses;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import com.xdel.luke.R;

/**
 * Created by kean.ong on 28/04/2017.
 */
public class ctrlSettings {

    public static void enableDisableCommonButton(Boolean enabled, Button btn) {
        if (enabled) {
            btn.setBackgroundColor(Color.parseColor("#101F2D"));
            btn.setTextColor(Color.parseColor("#ffffff"));
        } else {
            btn.setBackgroundColor(Color.parseColor("#808080"));
            btn.setTextColor(Color.parseColor("#000000"));
        }
        btn.setEnabled(enabled);
    }

    public static void enableDisableSubmitButton(Boolean enabled, Button btn) {
        if (enabled) {
            btn.setBackgroundColor(Color.parseColor("#00ff00"));
            btn.setTextColor(Color.parseColor("#000000"));
        } else {
            btn.setBackgroundColor(Color.parseColor("#808080"));
            btn.setTextColor(Color.parseColor("#000000"));
        }

        btn.setEnabled(enabled);
    }

    public static void enableDisableSubmitButtonWithStyles(Boolean enabled, Button btn){
        if (enabled) {
            btn.setBackgroundResource(R.drawable.button_ok_style);
            btn.setTextColor(Color.parseColor("#000000"));
        } else {
            btn.setBackgroundResource(R.drawable.button_disabled_style);
            btn.setTextColor(Color.parseColor("#000000"));
        }

        btn.setEnabled(enabled);
    }

    public static void enableDisableEditButtonWithStyles(Boolean enabled, Button btn){
        if (enabled) {
            btn.setBackgroundResource(R.drawable.button_edit_style);
            btn.setTextColor(Color.parseColor("#000000"));
        } else {
            btn.setBackgroundResource(R.drawable.button_disabled_style);
            btn.setTextColor(Color.parseColor("#000000"));
        }

        btn.setEnabled(enabled);
    }

    public static void enableDisableCommonButtonWithStyles(Boolean enabled, Button btn) {
        if (enabled) {
            btn.setBackgroundResource(R.drawable.button_normal_style);
            btn.setTextColor(Color.parseColor("#ffffff"));
        } else {
            btn.setBackgroundResource(R.drawable.button_disabled_style);
            btn.setTextColor(Color.parseColor("#000000"));
        }

        btn.setEnabled(enabled);
    }

    public static void enableDisableButtonWithStyles(Boolean enabled, Button btn) {
        if (enabled) {
            btn.setBackgroundResource(R.drawable.button_ok_style);
            btn.setTextColor(Color.parseColor("#000000"));
        } else {
            btn.setBackgroundResource(R.drawable.button_disabled_style);
            btn.setTextColor(Color.parseColor("#000000"));
        }

        btn.setEnabled(enabled);
    }

    public static void enableDisableFailButtonWithStyles(boolean enabled, Button btn) {
        if (enabled) {
            btn.setBackgroundResource(R.drawable.button_fail_style);
            btn.setTextColor(Color.parseColor("#ffffff"));
        } else {
            btn.setBackgroundResource(R.drawable.button_disabled_style);
            btn.setTextColor(Color.parseColor("#000000"));
        }

        btn.setEnabled(enabled);
    }

    public static void toggleButtonsWithStyles(Boolean selected, Button btn) {
        if (selected) {
            btn.setBackgroundResource(R.drawable.button_ok_style);
        } else {
            btn.setBackgroundResource(R.drawable.button_disabled_style);
        }
    }

    public static void tottleButtonsWithStyles(Boolean selected, Button btn) {
        if (selected) {
            btn.setBackgroundResource(R.drawable.button_ok_style);
            btn.setTextColor(Color.parseColor("#000000"));
        } else {
            btn.setBackgroundResource(R.drawable.button_normal_style);
            btn.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    public static void toggleButtons(Boolean selected, Button btn) {
        if (selected) {
            btn.setBackgroundColor(Color.parseColor("#00ff00"));
        } else {
            btn.setBackgroundColor(Color.parseColor("#808080"));
        }
    }


}
