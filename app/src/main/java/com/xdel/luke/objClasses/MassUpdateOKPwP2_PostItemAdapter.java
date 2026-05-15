package com.xdel.luke.objClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.MainActivity;
import com.xdel.luke.MassUpdateOKPwP2;
import com.xdel.luke.R;
import com.xdel.luke.setFailStatusActivity;

import java.util.ArrayList;

/**
 * Created by kean.ong on 18/01/2016.
 */

public class MassUpdateOKPwP2_PostItemAdapter extends ArrayAdapter<MassUpdateOKPwP2_PostData> {

    public MassUpdateOKPwP2_PostItemAdapter(Context context, ArrayList<MassUpdateOKPwP2_PostData> MassUpdateOKPwP2PostData) {
        super(context, 0, MassUpdateOKPwP2PostData);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        Context ctx = myApp.getAppContext();
        final int pos = position;
        try {
            MassUpdateOKPwP2_PostData aMassUpdateOKPwP2PostData = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                //convertView = LayoutInflater.from(getContext()).inflate(R.layout.massokpwp2_list_row, parent, false);
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.massokpwp2_list_row, null);
            }
            // Lookup view for data population
            TextView lblMassOKPwP2JobNo = (TextView) convertView.findViewById(R.id.lblMassOKPwP2JobNo);
            TextView lblPart1JobsIDX = (TextView) convertView.findViewById(R.id.lblPart1JobsIDX);
            TextView lblPart2JobsIDX = (TextView) convertView.findViewById(R.id.lblPart2JobsIDX);
            TextView lblPart2StatusCodesIDX = (TextView) convertView.findViewById(R.id.lblPart2StatusCodesIDX);
            TextView lblPart2Part2ImageString = (TextView) convertView.findViewById(R.id.lblPart2Part2ImageString);
            TextView lblPart2Part2FailureReason = (TextView) convertView.findViewById(R.id.lblPart2Part2FailureReason);
            ImageView imgVMassOKPwP2Status = (ImageView) convertView.findViewById(R.id.imgVMassOKPwP2Status);
            Button btnMassOKPwP2PU = (Button) convertView.findViewById(R.id.btnMassOKPwP2PU);
            Button btnMassOKPwP2FailPU = (Button) convertView.findViewById(R.id.btnMassOKPwP2FailPU);
            ImageButton btnMassOKPwP2Refresh = (ImageButton) convertView.findViewById(R.id.btnMassOKPwP2Refresh);

            // Populate the data into the template view using the data object
            lblMassOKPwP2JobNo.setText(aMassUpdateOKPwP2PostData.Part1JobNo);
            lblPart1JobsIDX.setText(aMassUpdateOKPwP2PostData.Part1JobsIDX);
            lblPart2JobsIDX.setText(aMassUpdateOKPwP2PostData.Part2JobsIDX);
            if (aMassUpdateOKPwP2PostData.FailureReason == null || aMassUpdateOKPwP2PostData.FailureReason.equals("")) {
                lblPart2Part2FailureReason.setText("");
            } else {
                lblPart2Part2FailureReason.setText(aMassUpdateOKPwP2PostData.FailureReason);
            }

            if (aMassUpdateOKPwP2PostData.Part2ImageString == null || aMassUpdateOKPwP2PostData.Part2ImageString.equals("")) {
                lblPart2Part2ImageString.setText("");
            } else {
                lblPart2Part2ImageString.setText(aMassUpdateOKPwP2PostData.Part2ImageString);
            }

            if (aMassUpdateOKPwP2PostData.StatusCodesIDX == null || aMassUpdateOKPwP2PostData.StatusCodesIDX.equals("0")) {
                lblPart2StatusCodesIDX.setText(String.valueOf(0));
                Uri uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.mipmap.ic_action_unknownblue);
                imgVMassOKPwP2Status.setImageURI(uri);
                btnMassOKPwP2PU.setEnabled(true);
                btnMassOKPwP2FailPU.setEnabled(true);
                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnMassOKPwP2PU);
                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnMassOKPwP2FailPU);
            } else if (aMassUpdateOKPwP2PostData.StatusCodesIDX != null && Long.parseLong(aMassUpdateOKPwP2PostData.StatusCodesIDX) > 0 && Long.parseLong(aMassUpdateOKPwP2PostData.StatusCodesIDX) == 49) {
                Uri uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.mipmap.ic_navigation_check);
                imgVMassOKPwP2Status.setImageURI(uri);
                btnMassOKPwP2PU.setEnabled(false);
                btnMassOKPwP2FailPU.setEnabled(false);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnMassOKPwP2PU);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnMassOKPwP2FailPU);
            } else if (aMassUpdateOKPwP2PostData.StatusCodesIDX != null && Long.parseLong(aMassUpdateOKPwP2PostData.StatusCodesIDX) > 0 && Long.parseLong(aMassUpdateOKPwP2PostData.StatusCodesIDX) != 49) {
                lblPart2StatusCodesIDX.setText(String.valueOf(aMassUpdateOKPwP2PostData.StatusCodesIDX));
                Uri uri = Uri.parse("android.resource://" + ctx.getPackageName() + "/" + R.mipmap.ic_av_not_interested);
                imgVMassOKPwP2Status.setImageURI(uri);
                btnMassOKPwP2PU.setEnabled(false);
                btnMassOKPwP2FailPU.setEnabled(false);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnMassOKPwP2PU);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnMassOKPwP2FailPU);
            }

            btnMassOKPwP2PU.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MassUpdateOKPwP2_PostData muOKPwp2 = (MassUpdateOKPwP2_PostData) getItem(pos);
                        muOKPwp2.setStatusCodesIDX("49");
                        muOKPwp2.currJobInfo.setpendingStatusUpdate(49);
                        muOKPwp2.currJobInfo.setReceiver("");
                        muOKPwp2.currJobInfo.setFailureReason("");
                        muOKPwp2.currJobInfo.setconvertedBitmapToBase64(null);
                        muOKPwp2.currJobInfo.setSignatureStr("");

                        //get the row the clicked button is in
                        LinearLayout vwParentRow = (LinearLayout) v.getParent();
                        ImageView imgV = (ImageView) vwParentRow.findViewById(R.id.imgVMassOKPwP2Status);
                        Context mmCtx = myApp.getAppContext();
                        Uri uri = Uri.parse("android.resource://" + mmCtx.getPackageName() + "/" + R.mipmap.ic_navigation_check);
                        imgV.setImageURI(uri);
                        Button btnMassOKPwP2PU = (Button) vwParentRow.findViewById(R.id.btnMassOKPwP2PU);
                        Button btnMassOKPwP2FailPU = (Button) vwParentRow.findViewById(R.id.btnMassOKPwP2FailPU);
                        btnMassOKPwP2PU.setEnabled(false);
                        btnMassOKPwP2FailPU.setEnabled(false);
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnMassOKPwP2PU);
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnMassOKPwP2FailPU);
                    } catch (Exception e) {
                        String s = e.getMessage();
                        objGlobalMethods.writeToFile("Exception at MassUpdateOKPwP2_PostItemAdapter getView btnMassOKPwP2PU.setOnClickListener. Exception: " + s, "", "", "", "");
                    }
                }
            });

            btnMassOKPwP2FailPU.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MassUpdateOKPwP2.isFailFromMassUpdateOKPwP2 = true;
                        Context mmCtx = myApp.getAppContext();
                        MassUpdateOKPwP2_PostData muOKPwp2 = (MassUpdateOKPwP2_PostData) getItem(pos);
                        setFailStatusActivity.MassUpdateOKPwP2PostData = muOKPwp2;
                        SharedPreferences GcmPreferences = mmCtx.getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");
                        Intent parentIntent = new Intent(mmCtx, setFailStatusActivity.class);
                        parentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        setFailStatusActivity.selectedStatusCodesIDX = "";
                        //mmCtx.startActivity(parentIntent);
                        myApp.currentActivity.startActivity(parentIntent);

                    } catch (Exception e) {
                        String s = e.getMessage();
                        objGlobalMethods.writeToFile("Exception at MassUpdateOKPwP2_PostItemAdapter getView btnMassOKPwP2FailPU.setOnClickListener. Exception: " + s, "", "", "", "");
                    }

                }
            });

            btnMassOKPwP2Refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MassUpdateOKPwP2_PostData muOKPwp2 = (MassUpdateOKPwP2_PostData) getItem(pos);
                    muOKPwp2.setStatusCodesIDX("0");
                    muOKPwp2.currJobInfo.setpendingStatusUpdate(0);
                    muOKPwp2.currJobInfo.setReceiver("");
                    muOKPwp2.currJobInfo.setFailureReason("");
                    muOKPwp2.currJobInfo.setconvertedBitmapToBase64(null);
                    muOKPwp2.currJobInfo.setSignatureStr("");
                    muOKPwp2.Part2ImageString = "";
                    muOKPwp2.FailureReason = "";
                    LinearLayout vwParentRow = (LinearLayout) v.getParent();
                    ImageView imgV = (ImageView) vwParentRow.findViewById(R.id.imgVMassOKPwP2Status);
                    Context mmCtx = myApp.getAppContext();
                    Uri uri = Uri.parse("android.resource://" + mmCtx.getPackageName() + "/" + R.mipmap.ic_action_unknownblue);
                    imgV.setImageURI(uri);
                    Button btnMassOKPwP2PU = (Button) vwParentRow.findViewById(R.id.btnMassOKPwP2PU);
                    Button btnMassOKPwP2FailPU = (Button) vwParentRow.findViewById(R.id.btnMassOKPwP2FailPU);
                    ImageButton btnMassOKPwP2Refresh = (ImageButton) vwParentRow.findViewById(R.id.btnMassOKPwP2Refresh);
                    btnMassOKPwP2PU.setEnabled(true);
                    btnMassOKPwP2FailPU.setEnabled(true);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnMassOKPwP2PU);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnMassOKPwP2FailPU);
                }
            });
        } catch (Exception e) {
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at MassUpdateOKPwP2_PostItemAdapter getView btnMassOKPwP2Refresh.setOnClickListener. Exception: " + s, "", "", "", "");
        }

        return convertView;
    }

}
