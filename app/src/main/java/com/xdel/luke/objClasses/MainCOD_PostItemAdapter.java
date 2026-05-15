package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by kean.ong on 08/03/2016.
 */
public class MainCOD_PostItemAdapter  extends ArrayAdapter<MainCOD_PostData> {
    private Activity myContext;
    private MainCOD_PostData[] datas;

    public MainCOD_PostItemAdapter(Context context, ArrayList<MainCOD_PostData> MainCODPostData){
        super (context,0, MainCODPostData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MainCOD_PostData MainCODPostDatas = getItem(position);
        final int pos = position;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.maincod_row, parent, false);
        }
        // Lookup view for data population
        LinearLayout maincod_row_linearLayout1 = (LinearLayout) convertView.findViewById(R.id.maincod_row_linearLayout1);
        LinearLayout maincod_row_Invoicerow = (LinearLayout) convertView.findViewById(R.id.maincod_row_Invoicerow);
        LinearLayout maincod_row_statusrow = (LinearLayout) convertView.findViewById(R.id.maincod_row_statusrow);
        LinearLayout maincod_row_lastBreakrow = (LinearLayout) convertView.findViewById(R.id.maincod_row_lastBreakrow);

        TextView lblmaincodCNtext = (TextView) convertView.findViewById(R.id.lblmaincodCNtext);
        TextView lblmaincodCN = (TextView) convertView.findViewById(R.id.lblmaincodCN);
        TextView lblInvoicetext = (TextView) convertView.findViewById(R.id.lblInvoicetext);
        TextView lblInvoice = (TextView) convertView.findViewById(R.id.lblInvoice);
        TextView lblmaincodCashCheq = (TextView) convertView.findViewById(R.id.lblmaincodCashCheq);
        TextView lblmaincodAmt = (TextView) convertView.findViewById(R.id.lblmaincodAmt);

        TextView hfJobsInfoJsonStr = (TextView) convertView.findViewById(R.id.hfJobsInfoJsonStr);
        TextView hfmaincod_row_clickableBooleanVal = (TextView) convertView.findViewById(R.id.hfmaincod_row_clickableBooleanVal);
        TextView lblLAT = (TextView) convertView.findViewById(R.id.lblLAT);
        TextView lblLNG = (TextView) convertView.findViewById(R.id.lblLNG);
        TextView lblPIPDIP = (TextView) convertView.findViewById(R.id.lblPIPDIP);

        DecimalFormat dec = new DecimalFormat("#.00");

        // Populate the data into the template view using the data object

        maincod_row_Invoicerow.setVisibility(MainCODPostDatas.Showmaincod_row_Invoicerow ? View.VISIBLE : View.GONE);
        maincod_row_statusrow.setVisibility(MainCODPostDatas.Showmaincod_row_statusrow ? View.VISIBLE : View.GONE);
        maincod_row_lastBreakrow.setVisibility(MainCODPostDatas.Showmaincod_row_lastBreakrow ? View.VISIBLE : View.VISIBLE);

        lblmaincodCN.setText(MainCODPostDatas.lblmaincodCNValue);
        lblInvoice.setText(MainCODPostDatas.lblInvoiceValue);
        if (MainCODPostDatas.CollectionStatus == null || MainCODPostDatas.CollectionStatus == -1){
            lblmaincodCashCheq.setText("-");
        }
        else if (MainCODPostDatas.CollectionStatus == 0){
            lblmaincodCashCheq.setText(myApp.getAppContext().getString(R.string.notcollected));
        }
        else if (MainCODPostDatas.CollectionStatus == 1){
            lblmaincodCashCheq.setText(myApp.getAppContext().getString(R.string.cash));
        }
        else if (MainCODPostDatas.CollectionStatus == 2){
            lblmaincodCashCheq.setText(myApp.getAppContext().getString(R.string.cheque));
        } else if (MainCODPostDatas.CollectionStatus == 3) {
            // show as collected for custcare handling, tink of the shortest wording
            lblmaincodCashCheq.setText(myApp.getAppContext().getString(R.string.hqhandling));
        }

        lblmaincodAmt.setText(MainCODPostDatas.Amount == null ? "$0.00" : "$" + String.valueOf(dec.format(MainCODPostDatas.Amount)));

        hfJobsInfoJsonStr.setText(MainCODPostDatas.JobInfoJsonStr);
        hfmaincod_row_clickableBooleanVal.setText(String.valueOf(MainCODPostDatas.Clickable));

        lblLAT.setText(String.valueOf(MainCODPostDatas.LAT));
        lblLNG.setText(String.valueOf(MainCODPostDatas.LNG));

        if (MainCODPostDatas.CODcollectionAtCatType == 0 || MainCODPostDatas.CODcollectionAtCatType == 1){
            lblPIPDIP.setText("PIP");
        }
        else if (MainCODPostDatas.CODcollectionAtCatType == 2 || MainCODPostDatas.CODcollectionAtCatType == 3){
            lblPIPDIP.setText("DIP");
        }
        else if (MainCODPostDatas.CODcollectionAtCatType == -1){
            lblPIPDIP.setText("");
        }

        if (!MainCODPostDatas.Clickable){
            maincod_row_linearLayout1.setBackgroundColor(Color.parseColor("#ffff9e1b"));
            lblmaincodCNtext.setVisibility(View.GONE);
            lblmaincodCN.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            lblmaincodCN.setTextColor(Color.BLACK);
            lblInvoicetext.setVisibility(View.GONE);
            lblInvoice.setTextColor(Color.BLACK);
            lblInvoice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
        else {
            maincod_row_linearLayout1.setBackgroundResource(R.drawable.list_selector_white);
            lblmaincodCNtext.setVisibility(View.VISIBLE);
            lblmaincodCN.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            lblInvoicetext.setVisibility(View.VISIBLE);
            lblInvoice.setTextColor(Color.BLUE);
            lblInvoice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                MainCOD_PostData MainCODPostDatas2 = (MainCOD_PostData)getItem(pos);
                if (MainCODPostDatas2.Clickable){
                    Context mmCtx = myApp.getAppContext();
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();

                    if (MainCODPostDatas2.catType == 0){
                        objGlobalMethods.setGCMPrefs(GcmPreferences,objGlobalVariables.categoryTitlePrefName,myApp.getAppContext().getString(R.string.item_dashboard2PIP));
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "0");
                    }
                    else if (MainCODPostDatas2.catType == 1){
                        objGlobalMethods.setGCMPrefs(GcmPreferences,objGlobalVariables.categoryTitlePrefName,myApp.getAppContext().getString(R.string.item_dashboard2PU));
                        objGlobalMethods.setGCMPrefs(GcmPreferences,objGlobalVariables.categoryTypePrefName,"1");
                    }
                    else if (MainCODPostDatas2.catType == 2){
                        objGlobalMethods.setGCMPrefs(GcmPreferences,objGlobalVariables.categoryTitlePrefName,myApp.getAppContext().getString(R.string.item_dashboard2DIP));
                        objGlobalMethods.setGCMPrefs(GcmPreferences,objGlobalVariables.categoryTypePrefName,"2");
                    }
                    else if (MainCODPostDatas2.catType == 3){
                        objGlobalMethods.setGCMPrefs(GcmPreferences,objGlobalVariables.categoryTitlePrefName,myApp.getAppContext().getString(R.string.item_dashboard2OKP));
                        objGlobalMethods.setGCMPrefs(GcmPreferences, objGlobalVariables.categoryTypePrefName, "3");
                    }

                    String JobInfoJsonStr = "";
                    JobInfoJsonStr = MainCODPostDatas2.JobInfoJsonStr;
                    objGlobalMethods.setJobInfoJsonStrPref(GcmPreferences, JobInfoJsonStr);
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "LAT", String.valueOf(MainCODPostDatas2.LAT));
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "LNG", String.valueOf(MainCODPostDatas2.LNG));

                    objGlobalMethods.setGCMPrefs(GcmPreferences,"isFromMainCOD","true");

                    Intent i;
                    i = new Intent(mmCtx, JobInfoActivity.class);
                    JobInfoActivity.accessFromDirection = 1;
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //mmCtx.startActivity(i);
                    myApp.currentActivity.startActivity(i);
                }
            }
        });

        //// Return the completed view to render on screen
        return convertView;
    }
}
