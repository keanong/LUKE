package com.xdel.luke.DOCOD;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.Spinner_TradeInModelPostItemAdapter;
import com.xdel.luke.objClasses.codupdate_PostData;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.sqlite.dbObjTradeInList;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DOCOD_ListAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private Context _context;
    public List<String> _listDataHeader; // header titles
    public HashMap<String, ArrayList<codupdate_PostData>> _listDataChild; //child title

    public HashMap<Integer, boolean[]> mChildCheckStates;
    public HashMap<Integer, boolean[]> mChildColorStates;
    private ChildViewHolder childViewHolder;
    private final DecimalFormat dec = new DecimalFormat("0.00");

    public DOCOD_ListAdapter(Context context, List<String> listDataHeader, HashMap<String, ArrayList<codupdate_PostData>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    public DOCOD_ListAdapter(Context context) {
        this._context = context;
    }

    public HashMap<String, ArrayList<codupdate_PostData>> get_listDataChild() {
        return this._listDataChild;
        /*return CODupdate2.listDataChildSortedSQLite2;*/
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
        /*return CODupdate2.listDataChildSortedSQLite2.get(CODupdate2.listDataHeader.get(groupPosition))
                .get(childPosititon);*/
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
        /*return CODupdate2.listDataChildSortedSQLite2.get(CODupdate2.listDataHeader.get(groupPosition))
                .size();*/
    }

    public float getChildrenTotalAmt(int groupPosition) {
        float z = 0;
        List<codupdate_PostData> cl = null;
        codupdate_PostData c = null;
        try {
            cl = this._listDataChild.get(this._listDataHeader.get(groupPosition));
            //cl = CODupdate2.listDataChildSortedSQLite2.get(CODupdate2.listDataHeader.get(groupPosition));
            if (cl != null && cl.size() > 0) {
                for (int i = 0; i <= cl.size() - 1; i++) {
                    c = null;
                    c = cl.get(i);
                    z += c.Amount;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter getChildrenTotalAmt. Exception: "
                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return z;
        }
    }

    public float getChildrenTotalPaidAmt(int groupPosition, int cp) {
        float z = 0;
        List<codupdate_PostData> cl = null;
        codupdate_PostData c = null;
        try {
            cl = this._listDataChild.get(this._listDataHeader.get(groupPosition));
            //cl = CODupdate2.listDataChildSortedSQLite2.get(CODupdate2.listDataHeader.get(groupPosition));
            if (cl != null && cl.size() > 0) {
                for (int i = 0; i <= cl.size() - 1; i++) {
                    c = null;
                    if ((cp == -1) || (i <= cp)) {
                        c = cl.get(i);
                        z += c.PaidAmount;
                    }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter getChildrenTotalPaidAmt. Exception: "
                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return z;
        }
    }

    public ArrayList<codupdate_PostData> getList(int groupPosition) {
        ArrayList<codupdate_PostData> l = null;
        try {
            l = this._listDataChild.get(this._listDataHeader.get(groupPosition));
            //l = CODupdate2.listDataChildSortedSQLite2.get(CODupdate2.listDataHeader.get(groupPosition));
        } catch (Exception e) {

        } finally {
            return l;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
        //return CODupdate2.listDataHeader.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
        //return CODupdate2.listDataHeader.size();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        try {
            GroupViewHolder gvh;
            String headerTitle = (String) getGroup(groupPosition);
            int chldrenCount = getChildrenCount(groupPosition);
            float DoAmt = getChildrenTotalAmt(groupPosition);
            float PaidDoAmt = getChildrenTotalPaidAmt(groupPosition, -1);
            final int grpPos = groupPosition;

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.docod_group_header, null);
                gvh = new GroupViewHolder();
                gvh.LLbg = (LinearLayout) convertView.findViewById(R.id.LLbg);
                gvh.lblDoNum = (TextView) convertView.findViewById(R.id.lblDoNum);
                gvh.lblDoAmt = (TextView) convertView.findViewById(R.id.lblDoAmt);

                convertView.setTag(gvh);
            } else {
                gvh = (GroupViewHolder) convertView.getTag();
            }

            gvh.lblDoNum.setText(headerTitle);
            gvh.lblDoNum.setTextColor(_context.getColor(PaidDoAmt > DoAmt ? R.color.White : R.color.black));
            gvh.lblDoAmt.setText(dec.format(DoAmt));
            gvh.lblDoAmt.setTextColor(_context.getColor(PaidDoAmt > DoAmt ? R.color.White : R.color.black));
            gvh.LLbg.setBackground(
                    (PaidDoAmt == DoAmt) ? _context.getDrawable(R.drawable.gradient_bg_header_green) :
                            (PaidDoAmt > DoAmt) ? _context.getDrawable(R.drawable.gradient_bg_header_red) :
                                    _context.getDrawable(R.drawable.gradient_bg_white));

            /*toggleHeaderBG_n_TextColor(
                    _context,
                    (PaidDoAmt > DoAmt) ? 2 : (PaidDoAmt == DoAmt) ? 1 : 0,
                    gvh
            );*/
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter getGroupView. Exception: "
                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertView;
        }
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        try {
            final codupdate_PostData c = (codupdate_PostData) getChild(groupPosition, childPosition);
            final ArrayList<codupdate_PostData> list = getList(groupPosition);
            final int mGroupPosition = groupPosition;
            final int mChildPosition = childPosition;
            final long AmountTypeIDX = c.AmountTypeIDX;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.codhorizontallistview_column, null);
                childViewHolder = new ChildViewHolder();
                childViewHolder.lblAmountTypeIDX = (TextView) convertView.findViewById(R.id.lblAmountTypeIDX);
                childViewHolder.lblAmount = (TextView) convertView.findViewById(R.id.lblAmount);
                childViewHolder.lblJobsIDX = (TextView) convertView.findViewById(R.id.lblJobsIDX);
                childViewHolder.lblshowFull = (TextView) convertView.findViewById(R.id.lblshowFull);
                childViewHolder.LLTypeRow1 = (LinearLayout) convertView.findViewById(R.id.LLTypeRow1);
                childViewHolder.LLlblType = (LinearLayout) convertView.findViewById(R.id.LLlblType);
                childViewHolder.LLlblTypeEstValue = (LinearLayout) convertView.findViewById(R.id.LLlblTypeEstValue);
                childViewHolder.LLbtnTypeAdd = (LinearLayout) convertView.findViewById(R.id.LLbtnTypeAdd);
                childViewHolder.lblType = (TextView) convertView.findViewById(R.id.lblType);
                childViewHolder.lblTypeEstValue = (TextView) convertView.findViewById(R.id.lblTypeEstValue);
                childViewHolder.LLTypeRow2 = (LinearLayout) convertView.findViewById(R.id.LLTypeRow2);
                childViewHolder.LLlblTypePaid = (LinearLayout) convertView.findViewById(R.id.LLlblTypePaid);
                childViewHolder.LLtxtTypePaid = (LinearLayout) convertView.findViewById(R.id.LLtxtTypePaid);
                childViewHolder.lblTypePaid = (TextView) convertView.findViewById(R.id.lblTypePaid);
                childViewHolder.txtPaidValue = (EditText) convertView.findViewById(R.id.txtPaidValue);
                childViewHolder.LLTypeRow3 = (LinearLayout) convertView.findViewById(R.id.LLTypeRow3);
                childViewHolder.LLlblTransactionRefLbl = (LinearLayout) convertView.findViewById(R.id.LLlblTransactionRefLbl);
                childViewHolder.LLlblTransactionRefVal = (LinearLayout) convertView.findViewById(R.id.LLlblTransactionRefVal);
                childViewHolder.txtTransaction = (EditText) convertView.findViewById(R.id.txtTransaction);
                childViewHolder.LLTypeRow4 = (LinearLayout) convertView.findViewById(R.id.LLTypeRow4);
                childViewHolder.LLTypeRow5 = (LinearLayout) convertView.findViewById(R.id.LLTypeRow5);
                childViewHolder.LLlblTradeinModelSpecified1 = (LinearLayout) convertView.findViewById(R.id.LLlblTradeinModelSpecified1);
                childViewHolder.LLlblTradeinModelSpecified2 = (LinearLayout) convertView.findViewById(R.id.LLlblTradeinModelSpecified2);
                childViewHolder.LLlblTradeinModel = (LinearLayout) convertView.findViewById(R.id.LLlblTradeinModel);
                childViewHolder.lblTradeInModelSpecifiedValue = (TextView) convertView.findViewById(R.id.lblTradeInModelSpecifiedValue);
                childViewHolder.ddlTradeInModel = (Spinner) convertView.findViewById(R.id.ddlTradeInModel);
                childViewHolder.LLlblTradeinVCode = (LinearLayout) convertView.findViewById(R.id.LLlblTradeinVCode);
                childViewHolder.ibtnAdd = (ImageButton) convertView.findViewById(R.id.ibtnAdd);
                childViewHolder.ibtnDel = (ImageButton) convertView.findViewById(R.id.ibtnDel);
                convertView.setTag(childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView.getTag();
            }

            if (parent != null) {
                if (childViewHolder.txtPaidValueTextWatcher != null) {
                    childViewHolder.txtPaidValue.removeTextChangedListener(childViewHolder.txtPaidValueTextWatcher);
                }

                if (childViewHolder.txtTransactionTextWatcher != null) {
                    childViewHolder.txtTransaction.removeTextChangedListener(childViewHolder.txtTransactionTextWatcher);
                }

                if (childViewHolder.ddlTradeInModelOnItemSelectedListener != null) {
                    childViewHolder.ddlTradeInModel.setOnItemSelectedListener(null);
                }

                childViewHolder.ibtnAdd.setOnClickListener(null);

                childViewHolder.txtPaidValue.setText(c.PaidAmount == 0.0 ? "" : String.valueOf(dec.format(c.PaidAmount)));
                childViewHolder.txtPaidValue.setTag(mGroupPosition);
                childViewHolder.ref = mChildPosition;
                childViewHolder.lblJobsIDX.setText(String.valueOf(c.JobsIDX));
                childViewHolder.lblAmountTypeIDX.setText(String.valueOf(c.AmountTypeIDX));
                childViewHolder.lblAmount.setText(c.Amount == 0.0 ? "0.00" : String.valueOf(dec.format(c.Amount)));
                if (c.AmountTypeIDX == 6 || c.AmountTypeIDX == 5) {
                    childViewHolder.lblTypeEstValue.setText(c.Amount == 0.0 ? "0.00" : String.valueOf(dec.format(c.Amount)));
                } else {
                    childViewHolder.lblTypeEstValue.setText("");
                }

                childViewHolder.txtTransaction.setText(c.Reference);
                childViewHolder.lblshowFull.setText(String.valueOf(c.showfull));
                childViewHolder.lblTradeInModelSpecifiedValue.setText(c.TradeInModelSpecifiedValue);

                showCtrls(childViewHolder, c.AmountTypeIDX, c.showfull == null ? true : c.showfull, c.TradInList);
                float ctotal = getChildrenTotalAmt(mGroupPosition);
                float cptotal = getChildrenTotalPaidAmt(mGroupPosition, mChildPosition);
                boolean overpaid = cptotal > ctotal;
                toggleTextBoxColor(overpaid && c.PaidAmount > 0.0,  childViewHolder.txtPaidValue, _context);
                final EditText t = childViewHolder.txtPaidValue;
                final ViewGroup par = parent;

                childViewHolder.txtPaidValueTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String PaidAmountStr = s.toString().trim();
                        Double PaidAmount = 0.0;
                        try {
                            PaidAmount = PaidAmountStr.equals("") ? 0.0 : PaidAmountStr.equals("0.00") ? 0.0 : Double.parseDouble(PaidAmountStr);
                        } catch (Exception e) {
                            PaidAmount = 0.0;
                        }

                        double total = c.Total;
                        double totalpa = 0.0;

                        if (list != null && list.size() > 0) {
                            for (int k = 0; k <= list.size() - 1; k++) {
                                if (list.get(k).AmountTypeIDX != AmountTypeIDX) {
                                    totalpa += list.get(k).PaidAmount;
                                } else {
                                    if (k != mChildPosition) {
                                        totalpa += list.get(k).PaidAmount;
                                    }
                                }
                            }
                        }

                        if ((totalpa + PaidAmount) <= total) {
                            toggleTextBoxColor(false, t, _context);
                            toggleLblDoAmtTextColor((totalpa + PaidAmount) < total ? 0 : 1, _context, par, mGroupPosition);
                            //toggleHeaderBG_n_TextColor(_context, (totalpa + PaidAmount) < total ? 0 : 1, gvh);
                        } else {
                            toggleTextBoxColor(true, t, _context);
                            toggleLblDoAmtTextColor(2, _context, par, mGroupPosition);
                            //toggleHeaderBG_n_TextColor(_context,2, gvh);
                        }

                        c.setPaidAmount(PaidAmount);
                        final CODupdate2 act = (CODupdate2) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                act.recalculatebal();
                            }
                        });
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                childViewHolder.txtPaidValue.addTextChangedListener(childViewHolder.txtPaidValueTextWatcher);
                //=========================================================================================================================================================

                childViewHolder.txtTransactionTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String Refstr = s.toString().trim();
                        c.setReference(Refstr);
                        final CODupdate2 act = (CODupdate2) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                act.recalculatebal();
                            }
                        });
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                childViewHolder.txtTransaction.addTextChangedListener(childViewHolder.txtTransactionTextWatcher);
                //=========================================================================================================================================================

                if (childViewHolder.ddlTradeInModel.isEnabled() && childViewHolder.ddlTradeInModel.getCount() > 1
                        && c.AmountTypeIDX == 6 && (c.Reference != null && c.Reference != "")) {
                    childViewHolder.ddlTradeInModel.setSelection(c.selectedTradeInModelIndex, false);
                    ddlModelselectedChangeManaul(mChildPosition, getList(mGroupPosition), c.selectedTradeInModelIndex, c.TradInList, childViewHolder.ddlTradeInModel);
                }

                //=========================================================================================================================================================
                childViewHolder.ddlTradeInModelOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView lblModelDesc = (TextView) view.findViewById(R.id.lblModelDesc);
                        TextView lblModelValue = (TextView) view.findViewById(R.id.lblModelValue);
                        String selectedModel = lblModelDesc.getText().toString();
                        Double selectedValue = Double.parseDouble(lblModelValue.getText().toString());
                        Double diff = 0.0;

                        Double cashInitialValue = list.get(0).InitialValue;
                        Double paidamt = 0.0;

                        if (selectedValue < c.InitialValue) {
                            paidamt = selectedValue;
                            //trade in with lower grade
                            diff = c.InitialValue - selectedValue;

                            for (int x = 0; x <= list.size() - 1; x++) {
                                if (x == 0) {
                                    list.get(x).setAmount(cashInitialValue + diff);
                                    //list.get(x).setTotal(list.get(x).InitialTotalCash + diff);
                                } else {
                                    //list.get(x).setTotal(list.get(x).InitialTotalCash + diff);
                                }
                            }

                        } else if (selectedValue > c.InitialValue) {
                            //trade in with higher grade
                            paidamt = c.InitialValue;
                            for (int x = 0; x <= list.size() - 1; x++) {
                                if (x == 0) {
                                    list.get(x).setAmount(cashInitialValue);
                                    //list.get(x).setTotal(list.get(x).InitialTotalCash);
                                } else {
                                    //list.get(x).setTotal(list.get(x).InitialTotalCash);
                                }
                            }
                        } else if (selectedValue.equals(c.InitialValue)) {
                            //trade in with same grade
                            paidamt = selectedValue;
                            for (int x = 0; x <= list.size() - 1; x++) {
                                if (x == 0) {
                                    list.get(x).setAmount(cashInitialValue);
                                    //list.get(x).setTotal(list.get(x).InitialTotalCash - 0);
                                } else {
                                    //list.get(x).setTotal(list.get(x).InitialTotalCash - 0);
                                }
                            }
                        }

                        RelativeLayout LL = (RelativeLayout) childViewHolder.ddlTradeInModel.getChildAt(0);
                        LinearLayout LL2 = (LinearLayout) LL.getChildAt(0);
                        c.setPaidAmount(paidamt);
                        if (selectedValue > c.InitialValue) {
                            c.setAmount(c.InitialValue);
                            LL2.setBackgroundColor(Color.RED);
                            childViewHolder.lblTypeEstValue.setText(String.valueOf(dec.format(c.InitialValue)));
                        } else {
                            c.setAmount(selectedValue);
                            LL2.setBackgroundColor(Color.WHITE);
                            childViewHolder.lblTypeEstValue.setText(selectedValue == 0.0 ? "0.00" : String.valueOf(dec.format(selectedValue)));
                        }
                        c.setReference(selectedModel);
                        c.setselectedTradeInModelIndex(position);

                        final CODupdate2 act = (CODupdate2) myApp.currentActivity;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                act.recalculatebal();
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                };

                childViewHolder.ddlTradeInModel.setOnItemSelectedListener(childViewHolder.ddlTradeInModelOnItemSelectedListener);
                //=========================================================================================================================================================

                childViewHolder.ibtnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                    /*if (v != null) {
                        InputMethodManager imm = (InputMethodManager) myApp.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }*/
                            int newindex = 0;

                            codupdate_PostData currItemData = c;
                            codupdate_PostData data;
                            //Long DOObjectIDX, Long JobsIDX, Long DOIDX, Long AmountTypeIDX, String AmountType, String Reference, Double Amount, int colorIdx, int group
                            data = new codupdate_PostData(currItemData.IDX, currItemData.InvoiceIDXList, currItemData.DOObjectIDX, currItemData.JobsIDX, currItemData.AmountTypeIDX,
                                    currItemData.AmountTypeIDX == 1 ? "CASH($)" : currItemData.AmountTypeIDX == 2 ? "CHEQUE($)" : currItemData.AmountTypeIDX == 3 ?
                                            "NETS($)" : currItemData.AmountTypeIDX == 4 ? "VISA/MASTER($)" : currItemData.AmountTypeIDX == 5 ? "VOUCHER($)" : "TRADE-IN",
                                    "", Double.parseDouble("0.00"), Double.parseDouble("0.00"), 0, false, null, currItemData.Total, currItemData.selectedTradeInModelIndex,
                                    currItemData.InitialValue, currItemData.InitialTotalCash, currItemData.TradeInModelSpecifiedValue);

                            if (mChildPosition == list.size() - 1) {
                                list.add(data);
                                newindex = mChildPosition + 1;
                            } else {
                                Boolean inserted = false;
                                for (int x = mChildPosition; x <= list.size() - 1; x++) {
                                    if (list.get(x).AmountTypeIDX != currItemData.AmountTypeIDX) {
                                        list.add(x, data);
                                        newindex = x;
                                        inserted = true;
                                        break;
                                    }
                                }

                                if (!inserted) {
                                    list.add(data);
                                    newindex = mChildPosition + 1;
                                }
                            }

                            notifyDataSetChanged();
                            final int selectionindex = newindex;
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    try {
                                        //CODupdate2.listView.setSelection(selectionindex - 1);
                                        final CODupdate2 act = (CODupdate2) myApp.currentActivity;
                                        act.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                act.setselection(selectionindex);
                                            }
                                        });
                                    } catch (Exception e) {
                                        String s = JobsHandler.getStackTraceString(e);
                                        objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter getChildView childViewHolder.ibtnAdd.setOnClickListener CODupdate2.listView.setSelection(selectionindex - 1). Exception: "
                                                + e.getMessage() + "\nStack: " + s, "", "", "", "");
                                    }
                                }
                            }, 100);
                        } catch (Exception e) {
                            String s = JobsHandler.getStackTraceString(e);
                            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter getChildView childViewHolder.ibtnAdd.setOnClickListener. Exception: "
                                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
                        }

                    }
                });

                childViewHolder.ibtnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                    /*if (v != null) {
                        InputMethodManager imm = (InputMethodManager) myApp.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }*/

                            /*ArrayAdapter<codupdate_PostData> adapter = (ArrayAdapter<codupdate_PostData>) CODupdate2.listView.getAdapter();
                            adapter.remove(adapter.getItem(mChildPosition));
                            adapter.notifyDataSetChanged();*/
                            list.remove(mChildPosition);
                            notifyDataSetChanged();
                            final CODupdate2 act = (CODupdate2) myApp.currentActivity;
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    act.recalculatebal();
                                }
                            });
                        } catch (Exception e) {
                            String s = JobsHandler.getStackTraceString(e);
                            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter getChildView childViewHolder.ibtnDel.setOnClickListener. Exception: "
                                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
                        }
                    }
                });

                convertView.clearFocus();

            }


        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter getChildView. Exception: "
                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
        } finally {
            return convertView;
        }

    }

    private void ddlModelselectedChangeManaul(int viewposition, ArrayList<codupdate_PostData> list, int posi, List<dbObjTradeInList> tList, Spinner ddlTradeInModel) {
        codupdate_PostData data;
        dbObjTradeInList tItem;
        Double paidamt = 0.0;

        try {
            data = list.get(viewposition);
            tItem = tList.get(posi);

            Double selectedValue = tItem.Value;
            Double diff = 0.0;
            Double cashInitialValue = list.get(0).InitialValue;

            if (selectedValue < data.InitialValue) {
                paidamt = selectedValue;
                //trade in with lower grade
                diff = data.InitialValue - selectedValue;

                for (int x = 0; x <= list.size() - 1; x++) {
                    if (x == 0) {
                        list.get(x).setAmount(cashInitialValue + diff);
                        //list.get(x).setTotal(list.get(x).InitialTotalCash + diff);
                    } else {
                        //list.get(x).setTotal(list.get(x).InitialTotalCash + diff);
                    }
                }

            } else if (selectedValue > data.InitialValue) {
                paidamt = data.InitialValue;
                //trade in with higher grade
                for (int x = 0; x <= list.size() - 1; x++) {
                    if (x == 0) {
                        list.get(x).setAmount(cashInitialValue);
                        //list.get(x).setTotal(list.get(x).InitialTotalCash);
                    } else {
                        //list.get(x).setTotal(list.get(x).InitialTotalCash);
                    }
                }
            } else if (selectedValue.equals(data.InitialValue)) {
                paidamt = selectedValue;
                //trade in with same grade
                for (int x = 0; x <= list.size() - 1; x++) {
                    if (x == 0) {
                        list.get(x).setAmount(cashInitialValue);
                        //list.get(x).setTotal(list.get(x).InitialTotalCash - 0);
                    } else {
                        //list.get(x).setTotal(list.get(x).InitialTotalCash - 0);
                    }
                }
            }

            RelativeLayout LL = (RelativeLayout) ddlTradeInModel.getChildAt(0);
            LinearLayout LL2 = (LinearLayout) LL.getChildAt(0);
            if (selectedValue > data.InitialValue) {
                LL2.setBackgroundColor(Color.RED);
            } else {
                LL2.setBackgroundColor(Color.WHITE);
            }

            data.setPaidAmount(paidamt);
            final CODupdate2 act = (CODupdate2) myApp.currentActivity;
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    act.recalculatebal();
                }
            });
        } catch (Exception e) {
            //String stack = JobsHandler.getStackTraceString(e);
        }
    }

    private void showCtrls(ChildViewHolder cvh, Long amtTypeIDX, Boolean showfull, List<dbObjTradeInList> tradeinlist) {

        cvh.lblType.setText(amtTypeIDX == 1 ? R.string.paymentmode_cash : amtTypeIDX == 2 ? R.string.paymentmode_cheque : amtTypeIDX == 3 ? R.string.paymentmode_nets :
                amtTypeIDX == 4 ? R.string.paymentmode_visamaster : amtTypeIDX == 5 ? R.string.paymentmode_voucher : R.string.paymentmode_tradein);
        if (showfull) {
            cvh.LLlblType.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstylegray : amtTypeIDX == 2 ? R.drawable.edittextstylegray : amtTypeIDX == 3 ?
                    R.drawable.edittextstylegray : amtTypeIDX == 4 ? R.drawable.edittextstylegray : amtTypeIDX == 5 ? R.drawable.edittextstylegray :
                    R.drawable.edittextstylegray);
            cvh.LLlblTypeEstValue.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstylegray : amtTypeIDX == 2 ? R.drawable.edittextstylegray : amtTypeIDX == 3 ?
                    R.drawable.edittextstylegray : amtTypeIDX == 4 ? R.drawable.edittextstylegray : amtTypeIDX == 5 ? R.drawable.edittextstylegray :
                    R.drawable.edittextstylegray);
            cvh.LLbtnTypeAdd.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstylegray : amtTypeIDX == 2 ? R.drawable.edittextstylegray : amtTypeIDX == 3 ?
                    R.drawable.edittextstylegray : amtTypeIDX == 4 ? R.drawable.edittextstylegray : amtTypeIDX == 5 ? R.drawable.edittextstylegray :
                    R.drawable.edittextstylegray);
        } else {
            cvh.LLlblType.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                    R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                    R.drawable.edittextstyle_tradein);
            cvh.LLlblTypeEstValue.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                    R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                    R.drawable.edittextstyle_tradein);
            cvh.LLbtnTypeAdd.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                    R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                    R.drawable.edittextstyle_tradein);
        }

        cvh.LLlblTypePaid.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);
        cvh.LLtxtTypePaid.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);
        cvh.LLlblTransactionRefLbl.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);
        cvh.LLlblTransactionRefVal.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);
        cvh.LLlblTradeinModelSpecified1.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);
        cvh.LLlblTradeinModelSpecified2.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);
        cvh.LLlblTradeinModel.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);
        cvh.LLlblTradeinVCode.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);
        /*cvh.LLtxtTradeinVCode.setBackgroundResource(amtTypeIDX == 1 ? R.drawable.edittextstyle_cash : amtTypeIDX == 2 ? R.drawable.edittextstyle_cheque : amtTypeIDX == 3 ?
                R.drawable.edittextstyle_nets : amtTypeIDX == 4 ? R.drawable.edittextstyle_visamaster : amtTypeIDX == 5 ? R.drawable.edittextstyle_voucher :
                R.drawable.edittextstyle_tradein);*/

        cvh.LLTypeRow1.setVisibility(amtTypeIDX == 1 ? View.VISIBLE :
                (amtTypeIDX == 2 || amtTypeIDX == 3 || amtTypeIDX == 4 || amtTypeIDX == 5 || amtTypeIDX == 6) && showfull ? View.VISIBLE : View.GONE);
        cvh.LLTypeRow2.setVisibility(amtTypeIDX == 6 ? View.GONE : View.VISIBLE);
        cvh.LLTypeRow3.setVisibility(amtTypeIDX == 1 || amtTypeIDX == 3 || amtTypeIDX == 4 || amtTypeIDX == 6 ? View.GONE : View.VISIBLE);
        cvh.LLTypeRow4.setVisibility(amtTypeIDX == 6 ? View.VISIBLE : View.GONE);
        cvh.LLTypeRow5.setVisibility(amtTypeIDX == 6 ? View.VISIBLE : View.GONE);

        cvh.ibtnAdd.setVisibility(amtTypeIDX == 1 || amtTypeIDX == 6 ? View.GONE :
                (amtTypeIDX == 2 || amtTypeIDX == 3 || amtTypeIDX == 4 || amtTypeIDX == 5) && showfull ? View.VISIBLE : View.GONE);

        cvh.ibtnDel.setVisibility(amtTypeIDX == 1 || amtTypeIDX == 6 ? View.GONE :
                (amtTypeIDX == 2 || amtTypeIDX == 3 || amtTypeIDX == 4 || amtTypeIDX == 5) && showfull ? View.GONE : View.VISIBLE);

        if (amtTypeIDX == 6 && (tradeinlist != null && tradeinlist.size() > 0)) {
            Spinner_TradeInModelPostItemAdapter adapter = new Spinner_TradeInModelPostItemAdapter(myApp.getAppContext(), tradeinlist);
            cvh.ddlTradeInModel.setAdapter(adapter);
            cvh.ddlTradeInModel.setEnabled(true);
        } else if (amtTypeIDX == 6 && (tradeinlist == null || tradeinlist.size() == 0)) {
            tradeinlist = new ArrayList<>();
            dbObjTradeInList obj = new dbObjTradeInList(Long.parseLong("0"), "", myApp.getAppContext().getString(R.string.tradeinmodel_none), 0.0, myApp.getAppContext().getString(R.string.tradeinmodel_none), myApp.getAppContext().getString(R.string.tradeinmodel_none_nospaces));
            tradeinlist.add(0, obj);
            Spinner_TradeInModelPostItemAdapter adapter = new Spinner_TradeInModelPostItemAdapter(myApp.getAppContext(), tradeinlist);
            cvh.ddlTradeInModel.setAdapter(adapter);
            cvh.ddlTradeInModel.setEnabled(false);
        }

    }

    private void toggleTextBoxColor(boolean red, EditText t, Context ctx) {
        try {
            t.setBackground(ctx.getDrawable(red ? R.drawable.txtbox_red : R.drawable.txtbox_def));
            t.setTextColor(red ? Color.WHITE : Color.BLACK);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter toggleTextBoxColor. Exception: "
                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private void toggleLblDoAmtTextColor(int type, Context ctx, ViewGroup par, int gid) {
        try {
            boolean childfound = false;
            EditText t = null;
            View g = null;
            TextView d = null;
            TextView v = null;
            LinearLayout bg = null;
            if (par != null && par.getChildCount() > 0) {
                for (int i = par.getChildCount() -1; i >= 0; i--) {
                  g = par.getChildAt(i);
                  if (g.getTag() instanceof ChildViewHolder && !childfound) {
                      t = g.findViewById(R.id.txtPaidValue);
                      if ((int) t.getTag() == gid) {
                          childfound = true;
                          continue;
                      }
                  }

                  if (g.getTag() instanceof GroupViewHolder && childfound) {
                      d = g.findViewById(R.id.lblDoNum);
                      v = g.findViewById(R.id.lblDoAmt);
                      bg = g.findViewById(R.id.LLbg);
                      if (v != null && bg != null)  {
                          bg.setBackground(ctx.getDrawable(type == 2 ? R.drawable.gradient_bg_header_red : type == 1 ? R.drawable.gradient_bg_header_green : R.drawable.gradient_bg_white));
                          d.setTextColor(ctx.getColor(type == 2 ? R.color.White : R.color.black));
                          v.setTextColor(ctx.getColor(type == 2 ? R.color.White : R.color.black));
                          break;
                      }
                  }
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter toggleLblDoAmtTextColor. Exception: "
                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    //private void toggleHeaderBG_n_TextColor(Context ctx, int type, GroupViewHolder gvh, ViewGroup par) {
    private void toggleHeaderBG_n_TextColor(final Context ctx, final int type, final GroupViewHolder gvh) {
        try {
            //type 0 == white bg black txt
            //type 1 == green bg black txt
            //type 2 == red bg red txt
            //if (par != null && par.getChildCount() > 0) {
            if (gvh != null){
                gvh.lblDoNum.setTextColor(_context.getColor(type == 2 ? R.color.White : R.color.black));
                gvh.lblDoAmt.setTextColor(_context.getColor(type == 2 ? R.color.White : R.color.black));
                gvh.LLbg.setBackground(
                        type == 1 ? ctx.getDrawable(R.drawable.gradient_bg_header_green) :
                                type == 2 ? ctx.getDrawable(R.drawable.gradient_bg_header_red) :
                                        ctx.getDrawable(R.drawable.gradient_bg_white)
                );
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at DOCOD_ListAdapter toggleHeaderBG_n_TextColor. Exception: "
                    + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private class TextChangeListener implements TextWatcher {
        private final GroupViewHolder gvh;
        private final codupdate_PostData c;
        private final ArrayList<codupdate_PostData> list;
        private final long AmountTypeIDX;
        private final int mChildPosition;
        private final int mGroupPosition;
        private final EditText t;

        public TextChangeListener(GroupViewHolder agvh, codupdate_PostData ac, ArrayList<codupdate_PostData> alist, long aAmountTypeIDX, int amGroupPosition, int amChildPosition, EditText at){
            this.gvh = agvh;
            this.c = ac;
            this.list = alist;
            this.AmountTypeIDX = aAmountTypeIDX;
            this.mChildPosition = amChildPosition;
            this.mGroupPosition = amGroupPosition;
            this.t = at;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String PaidAmountStr = s.toString().trim();
            Double PaidAmount = 0.0;
            try {
                PaidAmount = PaidAmountStr.equals("") ? 0.0 : PaidAmountStr.equals("0.00") ? 0.0 : Double.parseDouble(PaidAmountStr);
            } catch (Exception e) {
                PaidAmount = 0.0;
            }

            double total = c.Total;
            double totalpa = 0.0;

            if (list != null && list.size() > 0) {
                for (int k = 0; k <= list.size() - 1; k++) {
                    if (list.get(k).AmountTypeIDX != AmountTypeIDX) {
                        totalpa += list.get(k).PaidAmount;
                    } else {
                        if (k != mChildPosition) {
                            totalpa += list.get(k).PaidAmount;
                        }
                    }
                }
            }

            if ((totalpa + PaidAmount) <= total) {
                toggleTextBoxColor(false, t, _context);
                //toggleHeaderBG_n_TextColor(_context, (totalpa + PaidAmount) < total ? 0 : 1, gvh);
                //getGroupView(mGroupPosition,true, null, null);
            } else {
                toggleTextBoxColor(true, t, _context);
                //toggleHeaderBG_n_TextColor(_context,2, gvh);
                //getGroupView(mGroupPosition,true, null, null);
            }

            c.setPaidAmount(PaidAmount);
            final CODupdate2 act = (CODupdate2) myApp.currentActivity;
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //getGroupView(mGroupPosition,true, null, null);
                    toggleHeaderBG_n_TextColor(_context, 2, gvh);
                    act.recalculatebal();
                }
            });
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public final class GroupViewHolder {
        LinearLayout LLbg;
        TextView lblDoNum;
        TextView lblDoAmt;
        TextView lbltype;
    }

    public final class ChildViewHolder {
        TextView lblAmountTypeIDX;
        TextView lblAmount;
        TextView lblJobsIDX;
        TextView lblshowFull;

        TextView lblType;
        TextView lblTypeEstValue;
        TextView lblTypePaid;
        TextView lblTransaction;
        TextView lblTradeinVCode;

        EditText txtPaidValue;
        EditText txtTransaction;
        EditText txtTradeInVCodeVal;

        TextView lblTradeInModelSpecifiedValue;
        TextView lblTradeInModel;
        Spinner ddlTradeInModel;

        ImageButton ibtnAdd;
        ImageButton ibtnDel;

        LinearLayout LLTypeRow1;
        LinearLayout LLTypeRow2;
        LinearLayout LLTypeRow3;
        LinearLayout LLTypeRow4;
        LinearLayout LLTypeRow5;

        LinearLayout LLlblType;
        LinearLayout LLlblTypeEstValue;
        LinearLayout LLbtnTypeAdd;

        LinearLayout LLlblTypePaid;
        LinearLayout LLtxtTypePaid;

        LinearLayout LLlblTransactionRefLbl;
        LinearLayout LLlblTransactionRefVal;

        LinearLayout LLlblTradeinModelSpecified1;
        LinearLayout LLlblTradeinModelSpecified2;
        LinearLayout LLlblTradeinModel;
        LinearLayout LLlblTradeinVCode;
        LinearLayout LLtxtTradeinVCode;

        int ref;
        TextWatcher txtPaidValueTextWatcher;
        TextWatcher txtTransactionTextWatcher;
        InputFilter[] txtPaidValueInputFilter;
        InputFilter txtTransactionInputFilter;
        AdapterView.OnItemSelectedListener ddlTradeInModelOnItemSelectedListener;
    }


}
