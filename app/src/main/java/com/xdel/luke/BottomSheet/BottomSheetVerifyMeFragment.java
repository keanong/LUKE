package com.xdel.luke.BottomSheet;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

public class BottomSheetVerifyMeFragment extends BottomSheetDialogFragment {

    public BottomSheetVerifyMeFragment(){

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
                objGlobalMethods.writeToFile("Exception at BottomSheetVerifyMeFragment BottomSheetCallback. Exception: " + s,"","","","");
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
            View contentView = View.inflate(getContext(), R.layout.fragment_verify_me, null);
            dialog.setContentView(contentView);

            Bundle args = getArguments();
            String verifymeqrbase64 = args.getString("verifymeqrbase64");
            ImageView imgQR = contentView.findViewById(R.id.imgQR);
            if (!verifymeqrbase64.isEmpty() && !verifymeqrbase64.equals("")) {
                byte[] GcImg = Base64.decode(verifymeqrbase64, Base64.DEFAULT);
                imgQR.setImageBitmap(BitmapFactory.decodeByteArray(GcImg, 0, GcImg.length));
            }

            String GcImgb64 = args.getString("GcImgb64");
            ImageView imgProfilePix = contentView.findViewById(R.id.imgProfilePix);
            if (!GcImgb64.isEmpty() && !GcImgb64.equals("")) {
                byte[] GcImg = Base64.decode(GcImgb64, Base64.DEFAULT);
                imgProfilePix.setImageBitmap(BitmapFactory.decodeByteArray(GcImg, 0, GcImg.length));
            }

            String GcName = args.getString("GcName");
            String GcID = args.getString("GcID");
            String MaskedGcID = args.getString("MaskedGcID");
            TextView lblName = contentView.findViewById(R.id.lblName);
            final TextView lblID = contentView.findViewById(R.id.lblID);

            lblName.setText(GcName);
            lblID.setText(MaskedGcID);

            final ImageView imgVisible = contentView.findViewById(R.id.imgVisible);
            imgVisible.setOnClickListener(null);
            imgVisible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences gcmp = objGlobalMethods.getGcmPreferences();
                    try {
                        Bundle args = getArguments();
                        String showID = objGlobalMethods.getGCMPrefs(gcmp, "showID");
                        boolean show;
                        show = !objGlobalMethods.IsEmptyString(showID) ? false : true;
                        String GcID = args.getString("GcID");
                        String MaskedGcID = args.getString("MaskedGcID");
                        lblID.setText(show ? GcID : MaskedGcID);
                        objGlobalMethods.setGCMPrefs(gcmp, "showID", show ? "t" : "");
                        imgVisible.setImageResource(show ? R.mipmap.ic_action_eye : R.mipmap.ic_action_eye_off);
                    } catch (Exception e) {
                        String s = JobsHandler.getStackTraceString(e);
                        objGlobalMethods.writeToFile("Exception at BottomSheetVerifyMeFragment setupDialog imgVisible.setOnClickListener. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
                    }
                }
            });

        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at BottomSheetVerifyMeFragment setupDialog. Exception: " + s,"","","","");
        }
    }
}