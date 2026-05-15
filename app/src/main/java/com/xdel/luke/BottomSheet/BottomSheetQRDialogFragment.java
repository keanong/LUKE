package com.xdel.luke.BottomSheet;

import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

public class BottomSheetQRDialogFragment extends BottomSheetDialogFragment {

    public BottomSheetQRDialogFragment(){

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
                objGlobalMethods.writeToFile("Exception at BottomSheetQRDialogFragment BottomSheetCallback. Exception: " + s,"","","","");
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
            View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_qr_dialog, null);
            dialog.setContentView(contentView);

            Bundle args = getArguments();
            String txt = args.getString("qrbase64");
            ImageView imgQR = contentView.findViewById(R.id.imgQR);
            if (!txt.isEmpty() && !txt.equals("")) {
                byte[] GcImg = Base64.decode(txt, Base64.DEFAULT);
                imgQR.setImageBitmap(BitmapFactory.decodeByteArray(GcImg, 0, GcImg.length));
            }
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at BottomSheetQRDialogFragment setupDialog. Exception: " + s,"","","","");
        }
    }


}
