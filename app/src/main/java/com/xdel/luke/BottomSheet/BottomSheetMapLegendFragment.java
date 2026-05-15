package com.xdel.luke.BottomSheet;

import android.app.Dialog;

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.View;

import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

public class BottomSheetMapLegendFragment extends BottomSheetDialogFragment {

    public BottomSheetMapLegendFragment() {

    }


    private BottomSheetBehavior.BottomSheetCallback
            mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            try {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }
            } catch (Exception e) {
                String s = e.getMessage();
                objGlobalMethods.writeToFile("Exception at BottomSheetMapLegendFragment BottomSheetCallback. Exception: " + s, "", "", "", "");
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        try {
            View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_map_legend_dialog, null);
            dialog.setContentView(contentView);
        } catch (Exception e) {
            String s = e.getMessage();
        }
    }
}
