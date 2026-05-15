package com.xdel.luke.BottomSheet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.View;
import android.widget.TextView;

import com.xdel.luke.R;

/**
 * Created by Obaro on 01/08/2016.
 */
public class BottomSheet3DialogFragment extends BottomSheetDialogFragment {

    TextView lblchatmessage;

    public BottomSheet3DialogFragment(){

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
            View contentView = View.inflate(getContext(), R.layout.fragment_bottomsheet3, null);
            dialog.setContentView(contentView);

            Bundle args = getArguments();
            String txt = args.getString("jobchangestxt");
            TextView lblchatmessage = contentView.findViewById(R.id.lblchatmessage);
            lblchatmessage.setText(txt);

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {

                }
            });

        } catch (Exception e) {
            String s = e.getMessage();
        }
    }

}
