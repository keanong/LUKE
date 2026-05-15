package com.xdel.luke.objClasses;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.xdel.luke.CODupdate;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.sqlite.dbObjTradeInList;

import org.w3c.dom.Text;

import java.sql.Ref;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kean.ong on 14/06/2017.
 */
public class codupdate_PostItemAdapter extends ArrayAdapter<codupdate_PostData> {
    private ArrayList<codupdate_PostData> list;
    private final DecimalFormat dec = new DecimalFormat("0.00");

    public codupdate_PostItemAdapter(Context context, ArrayList<codupdate_PostData> codupdatePostData) {
        super(context, 0, codupdatePostData);
        list = codupdatePostData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ChildViewHolder childViewHolder;
        final int pos = position;

        //int color = pos % 2 == 0 ? 1 : 0;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.codhorizontallistview_column, parent, false);
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

        final codupdate_PostData data = list.get(position);
        //=========================================================================================================================================================
        if (childViewHolder.txtPaidValueTextWatcher != null) {
            childViewHolder.txtPaidValue.removeTextChangedListener(childViewHolder.txtPaidValueTextWatcher);
        }

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

                data.setPaidAmount(PaidAmount);
                CODupdate.recalculatebal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        childViewHolder.txtPaidValue.addTextChangedListener(childViewHolder.txtPaidValueTextWatcher);
        //=========================================================================================================================================================
        if (childViewHolder.txtTransactionTextWatcher != null) {
            childViewHolder.txtTransaction.removeTextChangedListener(childViewHolder.txtTransactionTextWatcher);
        }

        childViewHolder.txtTransactionTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Refstr = s.toString().trim();
                data.setReference(Refstr);
                CODupdate.recalculatebal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        childViewHolder.txtTransaction.addTextChangedListener(childViewHolder.txtTransactionTextWatcher);
        //=========================================================================================================================================================
        if (childViewHolder.ddlTradeInModelOnItemSelectedListener != null) {
            childViewHolder.ddlTradeInModel.setOnItemSelectedListener(null);
        }
        //=========================================================================================================================================================

        childViewHolder.ref = position;
        childViewHolder.lblJobsIDX.setText(String.valueOf(data.JobsIDX));
        childViewHolder.lblAmountTypeIDX.setText(String.valueOf(data.AmountTypeIDX));
        childViewHolder.lblAmount.setText(data.Amount == 0.0 ? "0.00" : String.valueOf(dec.format(data.Amount)));
        if (data.AmountTypeIDX == 6 || data.AmountTypeIDX == 5) {
            childViewHolder.lblTypeEstValue.setText(data.Amount == 0.0 ? "0.00" : String.valueOf(dec.format(data.Amount)));
        } else {
            childViewHolder.lblTypeEstValue.setText("");
        }
        childViewHolder.txtPaidValue.setText(data.PaidAmount == 0.0 ? "" : String.valueOf(dec.format(data.PaidAmount)));
        childViewHolder.txtPaidValue.setTag(data);
        childViewHolder.txtTransaction.setText(data.Reference);
        childViewHolder.lblshowFull.setText(String.valueOf(data.showfull));
        childViewHolder.lblTradeInModelSpecifiedValue.setText(data.TradeInModelSpecifiedValue);

        showCtrls(childViewHolder, data.AmountTypeIDX, data.showfull == null ? true : data.showfull, data.TradInList);

        /*if (childViewHolder.ddlTradeInModel.isEnabled() && childViewHolder.ddlTradeInModel.getCount() > 1
                && data.AmountTypeIDX == 6 && data.Amount > 0 && (data.Reference != null && data.Reference != "")) {*/
        if (childViewHolder.ddlTradeInModel.isEnabled() && childViewHolder.ddlTradeInModel.getCount() > 1
                && data.AmountTypeIDX == 6 && (data.Reference != null && data.Reference != "")) {
            childViewHolder.ddlTradeInModel.setSelection(data.selectedTradeInModelIndex, false);
            ddlModelselectedChangeManaul(pos, data.selectedTradeInModelIndex, data.TradInList, childViewHolder.ddlTradeInModel);
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
                    //trade in with higher grade
                    paidamt = data.InitialValue;
                    for (int x = 0; x <= list.size() - 1; x++) {
                        if (x == 0) {
                            list.get(x).setAmount(cashInitialValue);
                            //list.get(x).setTotal(list.get(x).InitialTotalCash);
                        } else {
                            //list.get(x).setTotal(list.get(x).InitialTotalCash);
                        }
                    }
                } else if (selectedValue.equals(data.InitialValue)) {
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
                data.setPaidAmount(paidamt);
                if (selectedValue > data.InitialValue) {
                    data.setAmount(data.InitialValue);
                    LL2.setBackgroundColor(Color.RED);
                    childViewHolder.lblTypeEstValue.setText(String.valueOf(dec.format(data.InitialValue)));
                } else {
                    data.setAmount(selectedValue);
                    LL2.setBackgroundColor(Color.WHITE);
                    childViewHolder.lblTypeEstValue.setText(selectedValue == 0.0 ? "0.00" : String.valueOf(dec.format(selectedValue)));
                }
                data.setReference(selectedModel);
                data.setselectedTradeInModelIndex(position);

                //notifyDataSetChanged();
                CODupdate.recalculatebal();
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

                    codupdate_PostData currItemData = (codupdate_PostData) getItem(pos);
                    codupdate_PostData data;
                    //Long DOObjectIDX, Long JobsIDX, Long DOIDX, Long AmountTypeIDX, String AmountType, String Reference, Double Amount, int colorIdx, int group
                    data = new codupdate_PostData(currItemData.IDX, currItemData.InvoiceIDXList, currItemData.DOObjectIDX, currItemData.JobsIDX, currItemData.AmountTypeIDX,
                            currItemData.AmountTypeIDX == 1 ? "CASH($)" : currItemData.AmountTypeIDX == 2 ? "CHEQUE($)" : currItemData.AmountTypeIDX == 3 ?
                                    "NETS($)" : currItemData.AmountTypeIDX == 4 ? "VISA/MASTER($)" : currItemData.AmountTypeIDX == 5 ? "VOUCHER($)" : "TRADE-IN",
                            "", Double.parseDouble("0.00"), Double.parseDouble("0.00"), 0, false, null, currItemData.Total, currItemData.selectedTradeInModelIndex,
                            currItemData.InitialValue, currItemData.InitialTotalCash, currItemData.TradeInModelSpecifiedValue);

                    if (pos == list.size() - 1) {
                        list.add(data);
                        newindex = pos + 1;
                    } else {
                        Boolean inserted = false;
                        for (int x = pos; x <= list.size() - 1; x++) {
                            if (list.get(x).AmountTypeIDX != currItemData.AmountTypeIDX) {
                                list.add(x, data);
                                newindex = x;
                                inserted = true;
                                break;
                            }
                        }

                        if (!inserted) {
                            list.add(data);
                            newindex = pos + 1;
                        }
                    }

                    notifyDataSetChanged();
                    final int selectionindex = newindex;
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            CODupdate.listView.setSelection(selectionindex - 1);
                        }
                    }, 100);
                } catch (Exception e) {

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

                    ArrayAdapter<codupdate_PostData> adapter = (ArrayAdapter<codupdate_PostData>) CODupdate.listView.getAdapter();
                    adapter.remove(adapter.getItem(pos));
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
        });

        convertView.clearFocus();

        //// Return the completed view to render on screen
        return convertView;
    }

    private void ddlModelselectedChangeManaul(int viewposition, int posi, List<dbObjTradeInList> tList, Spinner ddlTradeInModel) {
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
            CODupdate.recalculatebal();
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
