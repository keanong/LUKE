package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.MainActivity;
import com.xdel.luke.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kean.ong on 18/05/2016.
 */
public class objUpdateCOD_PostItemAdapter  extends ArrayAdapter<objUpdateCOD_PostData> {
    final ArrayList<objUpdateCOD_PostData> objUpdateCODPostDataList;
    final Activity activity;

    public objUpdateCOD_PostItemAdapter(Context context, ArrayList<objUpdateCOD_PostData> objUpdateCODPostData, Activity act){
        super(context, 0, objUpdateCODPostData);
        this.activity = act;
        this.objUpdateCODPostDataList = objUpdateCODPostData;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        Context ctx = myApp.getAppContext();
        final int pos = position;
        try{
            objUpdateCOD_PostData aobjUpdateCODPostData = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.updatecod_list_row, null);
            }
            // Lookup view for data population
            DecimalFormat dec = new DecimalFormat("#.00");

            TextView lblCnInvNo = (TextView) convertView.findViewById(R.id.lblCnInvNo);
            TextView lblAIDX = (TextView) convertView.findViewById(R.id.lblAIDX);
            TextView lblJobsIDX = (TextView) convertView.findViewById(R.id.lblJobsIDX);
            TextView lblInvoiceIDX = (TextView) convertView.findViewById(R.id.lblInvoiceIDX);
            TextView lblStatusCodesIDX = (TextView) convertView.findViewById(R.id.lblStatusCodesIDX);
            TextView lblupdatecodTotalValueTxt = (TextView) convertView.findViewById(R.id.lblupdatecodTotalValueTxt);
            Button btnupdatecodcash = (Button) convertView.findViewById(R.id.btnupdatecodcash);
            Button btnupdatecodnc = (Button) convertView.findViewById(R.id.btnupdatecodnc);
            Button btnupdatecodcheque = (Button) convertView.findViewById(R.id.btnupdatecodcheque);
            ImageButton btnupdatecodRefresh = (ImageButton) convertView.findViewById(R.id.btnupdatecodRefresh);

            // Populate the data into the template view using the data object
            lblCnInvNo.setText(aobjUpdateCODPostData.CNnoINVno);
            lblAIDX.setText(String.valueOf(aobjUpdateCODPostData.AIDX));
            lblJobsIDX.setText(String.valueOf(aobjUpdateCODPostData.JobsIDX));
            lblInvoiceIDX.setText(String.valueOf(aobjUpdateCODPostData.InvoiceIDX));
            lblStatusCodesIDX.setText(String.valueOf(aobjUpdateCODPostData.CODType));
            lblupdatecodTotalValueTxt.setText("$" + String.valueOf(dec.format(aobjUpdateCODPostData.Amount)));


            if (aobjUpdateCODPostData.CODType == null || aobjUpdateCODPostData.CODType == -1){
                lblStatusCodesIDX.setText(String.valueOf(-1));
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodnc);
                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnupdatecodcash);
                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnupdatecodcheque);
                btnupdatecodRefresh.setEnabled(true);

                if (aobjUpdateCODPostData.EnableNCbutton){
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnupdatecodnc);
                }
            }
            else if (aobjUpdateCODPostData.CODType != null && aobjUpdateCODPostData.CODType== 0){
                //NOT COLLECTED
                btnupdatecodnc.setTextColor(Color.GREEN);
                btnupdatecodnc.setEnabled(false);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodcash);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodcheque);
                btnupdatecodRefresh.setEnabled(true);
            }
            else if (aobjUpdateCODPostData.CODType != null && aobjUpdateCODPostData.CODType== 1){
                //CASH
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodnc);
                btnupdatecodcash.setTextColor(Color.GREEN);
                btnupdatecodcash.setEnabled(false);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodcheque);
                btnupdatecodRefresh.setEnabled(true);

                if (aobjUpdateCODPostData.EnableNCbutton){
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnupdatecodnc);
                }
            }
            else if (aobjUpdateCODPostData.CODType != null && aobjUpdateCODPostData.CODType== 2){
                //CHEQUE
                btnupdatecodcheque.setTextColor(Color.GREEN);
                btnupdatecodcheque.setEnabled(false);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodcash);
                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodnc);
                btnupdatecodRefresh.setEnabled(true);

                if (aobjUpdateCODPostData.EnableNCbutton){
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnupdatecodnc);
                }
            }

            btnupdatecodnc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objUpdateCOD_PostData ucoddata = (objUpdateCOD_PostData)getItem(pos);
                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date("+l+")/";
                    ucoddata.setAUTC(timestampstr);
                    ucoddata.setStatus(0);
                    //get the row the clicked button is in
                    LinearLayout vwParentRow = (LinearLayout)v.getParent();
                    Context mmCtx = myApp.getAppContext();

                    Button btnupdatecodcash = (Button) vwParentRow.findViewById(R.id.btnupdatecodcash);
                    Button btnupdatecodnc = (Button) vwParentRow.findViewById(R.id.btnupdatecodnc);
                    Button btnupdatecodcheque = (Button) vwParentRow.findViewById(R.id.btnupdatecodcheque);
                    ImageButton btnupdatecodRefresh = (ImageButton) vwParentRow.findViewById(R.id.btnupdatecodRefresh);

                    btnupdatecodnc.setTextColor(Color.GREEN);
                    btnupdatecodnc.setEnabled(false);

                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodcash);
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodcheque);
                    btnupdatecodRefresh.setEnabled(true);

                    DecimalFormat dec = new DecimalFormat("#.00");
                    Double DtotalAmtToCollect = 0.0;
                    String StotalAmtToCollect = "0.00";
                    DtotalAmtToCollect = JobsHandler.returnTotalCODafterUpdateAmt(objUpdateCODPostDataList);
                    if (DtotalAmtToCollect.equals(0.0)){
                        StotalAmtToCollect = "0.00";
                    }
                    else if (DtotalAmtToCollect > 0.0){
                        StotalAmtToCollect = String.valueOf(dec.format(DtotalAmtToCollect));
                    }
                    activity.setTitle(myApp.getAppContext().getString(R.string.title_activity_update_cod__list) + " - " + "$" + StotalAmtToCollect);
                }
            });

            btnupdatecodcash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objUpdateCOD_PostData ucoddata = (objUpdateCOD_PostData)getItem(pos);
                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date("+l+")/";
                    ucoddata.setAUTC(timestampstr);
                    ucoddata.setStatus(1);
                    //get the row the clicked button is in
                    LinearLayout vwParentRow = (LinearLayout)v.getParent();
                    Context mmCtx = myApp.getAppContext();
                    Button btnupdatecodcash = (Button) vwParentRow.findViewById(R.id.btnupdatecodcash);
                    Button btnupdatecodnc = (Button) vwParentRow.findViewById(R.id.btnupdatecodnc);
                    Button btnupdatecodcheque = (Button) vwParentRow.findViewById(R.id.btnupdatecodcheque);
                    ImageButton btnupdatecodRefresh = (ImageButton) vwParentRow.findViewById(R.id.btnupdatecodRefresh);

                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodnc);
                    btnupdatecodcash.setTextColor(Color.GREEN);
                    btnupdatecodcash.setEnabled(false);
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodcheque);

                    btnupdatecodRefresh.setEnabled(true);
                }
            });

            btnupdatecodcheque.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objUpdateCOD_PostData ucoddata = (objUpdateCOD_PostData)getItem(pos);
                    Date dt = new Date();
                    Long l = dt.getTime();
                    String timestampstr = "/Date("+l+")/";
                    ucoddata.setAUTC(timestampstr);
                    ucoddata.setStatus(2);
                    //get the row the clicked button is in
                    LinearLayout vwParentRow = (LinearLayout)v.getParent();
                    Context mmCtx = myApp.getAppContext();
                    Button btnupdatecodcash = (Button) vwParentRow.findViewById(R.id.btnupdatecodcash);
                    Button btnupdatecodnc = (Button) vwParentRow.findViewById(R.id.btnupdatecodnc);
                    Button btnupdatecodcheque = (Button) vwParentRow.findViewById(R.id.btnupdatecodcheque);
                    ImageButton btnupdatecodRefresh = (ImageButton) vwParentRow.findViewById(R.id.btnupdatecodRefresh);

                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodnc);
                    ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodcash);
                    btnupdatecodcheque.setTextColor(Color.GREEN);
                    btnupdatecodcheque.setEnabled(false);
                    btnupdatecodRefresh.setEnabled(true);
                }
            });

            btnupdatecodRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    objUpdateCOD_PostData ucoddata = (objUpdateCOD_PostData)getItem(pos);
                    ucoddata.setAUTC("");
                    ucoddata.setStatus(-1);
                    ucoddata.setEnableNCbutton(false);
                    LinearLayout vwParentRow = (LinearLayout)v.getParent();
                    Context mmCtx = myApp.getAppContext();
                    Button btnupdatecodcash = (Button) vwParentRow.findViewById(R.id.btnupdatecodcash);
                    Button btnupdatecodnc = (Button) vwParentRow.findViewById(R.id.btnupdatecodnc);
                    Button btnupdatecodcheque = (Button) vwParentRow.findViewById(R.id.btnupdatecodcheque);
                    ImageButton btnupdatecodRefresh = (ImageButton) vwParentRow.findViewById(R.id.btnupdatecodRefresh);
                    TextView lblJobsIDX = (TextView) vwParentRow.findViewById(R.id.lblJobsIDX);
                    TextView lblInvoiceIDX = (TextView) vwParentRow.findViewById(R.id.lblInvoiceIDX);

                    String CODncActivationJobsIDXInvoiceIDXstrALJsonStr = "";
                    ArrayList<long[]> CODncActivationJobsIDXInvoiceIDXstrAL = null;
                    long[] JobsIDXInvoiceIDXpairArr = null;
                    objUpdateCOD_PostData aobjUpdateCODPostData = getItem(pos);
                    SharedPreferences GcmPreferences = myApp.getAppContext().getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                    CODncActivationJobsIDXInvoiceIDXstrALJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "CODncActivationJobsIDXInvoiceIDXstrALJsonStr");
                    if (!CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("") && !CODncActivationJobsIDXInvoiceIDXstrALJsonStr.equals("[]")) {
                        Gson gson = new Gson();
                        CODncActivationJobsIDXInvoiceIDXstrAL = gson.fromJson(CODncActivationJobsIDXInvoiceIDXstrALJsonStr, new TypeToken<List<long[]>>() {
                        }.getType());

                        for (int i = 0; i <= CODncActivationJobsIDXInvoiceIDXstrAL.size() - 1; i++) {
                            JobsIDXInvoiceIDXpairArr = CODncActivationJobsIDXInvoiceIDXstrAL.get(i);
                            if (JobsIDXInvoiceIDXpairArr[0] == aobjUpdateCODPostData.JobsIDX && JobsIDXInvoiceIDXpairArr[1] == aobjUpdateCODPostData.InvoiceIDX) {
                                break;
                            }
                        }

                        if (JobsIDXInvoiceIDXpairArr != null) {
                            if (JobsIDXInvoiceIDXpairArr[0] == aobjUpdateCODPostData.JobsIDX && JobsIDXInvoiceIDXpairArr[1] == aobjUpdateCODPostData.InvoiceIDX) {
                                ctrlSettings.enableDisableCommonButtonWithStyles(true, btnupdatecodnc);
                            } else {
                                ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodnc);
                            }
                        } else {
                            ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodnc);
                        }
                    } else {
                        ctrlSettings.enableDisableCommonButtonWithStyles(false, btnupdatecodnc);
                    }

                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnupdatecodcash);
                    ctrlSettings.enableDisableCommonButtonWithStyles(true, btnupdatecodcheque);
                    btnupdatecodRefresh.setEnabled(true);

                    DecimalFormat dec = new DecimalFormat("#.00");
                    Double DtotalAmtToCollect = 0.0;
                    String StotalAmtToCollect = "0.00";
                    DtotalAmtToCollect = JobsHandler.returnTotalCODafterUpdateAmt(objUpdateCODPostDataList);
                    if (DtotalAmtToCollect.equals(0.0)){
                        StotalAmtToCollect = "0.00";
                    }
                    else if (DtotalAmtToCollect > 0.0){
                        StotalAmtToCollect = String.valueOf(dec.format(DtotalAmtToCollect));
                    }
                    activity.setTitle(myApp.getAppContext().getString(R.string.title_activity_update_cod__list) + " - " + "$" + StotalAmtToCollect);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }


        return convertView;
    }
}
