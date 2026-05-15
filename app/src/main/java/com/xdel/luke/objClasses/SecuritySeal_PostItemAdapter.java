package com.xdel.luke.objClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xdel.luke.BottomSheet.securitySealDialog;
import com.xdel.luke.JobInfoActivity;
import com.xdel.luke.QRBarcodeReader;
import com.xdel.luke.R;
import com.xdel.luke.SecuritySealActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SecuritySeal_PostItemAdapter extends ArrayAdapter<SecuritySeal_PostData> {
    private ArrayList<SecuritySeal_PostData> list;
    private Context ctx;
    private SecuritySeal_PostData[] datas;

    public SecuritySeal_PostItemAdapter(Context context, ArrayList<SecuritySeal_PostData> SecuritySealPostData) {
        super(context, 0, SecuritySealPostData);
        list = SecuritySealPostData;
        ctx = context;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        try {
            ChildViewHolder cvh;
            SecuritySeal_PostData data = getItem(position);
            if (ctx == null) {
                ctx = myApp.getAppContext();
            }

            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.securityseal_item, parent, false);
                cvh = new ChildViewHolder();
                cvh.btnScanSeal = (Button) v.findViewById(R.id.btnScanSeal);
                cvh.txtSealVal = (EditText) v.findViewById(R.id.txtSealVal);
                cvh.btnVerify = (Button) v.findViewById(R.id.btnVerify);
                v.setTag(cvh);
            } else {
                cvh = (ChildViewHolder) v.getTag();
            }
            //cvh.btnVerify.setText(data.Verified ? ctx.getString(R.string.otpbuttontext_verified) : ctx.getString(R.string.btnVerify));
            cvh.btnVerify.setText(data.Verified ? ctx.getString(R.string.otpbuttontext_sealverified) : ctx.getString(R.string.otpbuttontext_sealverify));
            cvh.txtSealVal.setText(data.EnteredValue == null ? "" : data.EnteredValue);
            cvh.txtSealVal.setEnabled(data.Verified ? false : true);
            cvh.txtSealVal.setTextColor(data.Verified ? Color.parseColor("#000000") : Color.parseColor("#040404"));
            ctrlSettings.enableDisableSubmitButtonWithStyles(!data.Verified, cvh.btnScanSeal);
            ctrlSettings.enableDisableSubmitButtonWithStyles(!data.Verified, cvh.btnVerify);

            cvh.btnScanSealOnClickListener = null;
            cvh.btnVerifyOnClickListener = null;
            cvh.btnScanSealOnClickListener = rtnbtnScanSealClick(position);
            cvh.btnVerifyOnClickListener = rtnbtnVerifyClick(position, cvh);
            cvh.btnScanSeal.setOnClickListener(cvh.btnScanSealOnClickListener);
            cvh.btnVerify.setOnClickListener(cvh.btnVerifyOnClickListener);

        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at SecuritySeal_PostItemAdapter getView. Exception: " + e.getMessage().toString(), "", "", "", "");
        } finally {
            return v;
        }
    }

    private View.OnClickListener rtnbtnScanSealClick(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ctx == null) {
                        ctx = myApp.getAppContext();
                    }
                    SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                    objGlobalMethods.setGCMPrefs(GcmPreferences, "QRScanRequestIntent", "securitysealactivity");
                    Intent i = new Intent(ctx, QRBarcodeReader.class);
                    JobInfoActivity.accessFromDirection = 0;
                    SecuritySealActivity.itemIndex = position;
                    //ctx.startActivity(i);
                    myApp.currentActivity.startActivity(i);
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at SecuritySeal_PostItemAdapter rtnbtnScanSealClick. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }
        };
    }

    private View.OnClickListener rtnbtnVerifyClick(final int position, final ChildViewHolder chv) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String val = chv.txtSealVal.getText().toString().toLowerCase();
                    String origval = chv.txtSealVal.getText().toString();
                    if (ctx == null) {
                        ctx = myApp.getAppContext();
                    }
                    if (val.equals("")) {
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_securitysealempty)).show();
                    } else {
                        SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                        String securityseallistALJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "securityseallistALJsonStr");
                        if (!securityseallistALJsonStr.equals("") && !securityseallistALJsonStr.equals("[]")) {
                            Gson gson = new Gson();
                            ArrayList<String> securityseallistAL = gson.fromJson(securityseallistALJsonStr, new TypeToken<List<String>>() {
                            }.getType());
                            if (securityseallistAL != null && securityseallistAL.size() > 0) {
                                boolean dup = false;
                                if (securitySealDialog.al != null) {
                                    for (int i = 0; i <= securitySealDialog.al.size() - 1; i++) {
                                        if (securitySealDialog.al.get(i).EnteredValue.toLowerCase().equals(val) && securitySealDialog.al.get(i).Verified) {
                                            dup = true;
                                            break;
                                        }
                                    }
                                }

                                if (!dup) {
                                    boolean nonexist = true;
                                    boolean verified = false;
                                    for (int i = 0; i <= securityseallistAL.size() - 1; i++) {
                                        if (securityseallistAL.get(i).toLowerCase().equals(val)) {
                                            verified = true;
                                            nonexist = false;
                                            break;
                                        }
                                    }

                                    //unable to use for security dialog as currentActivity not linked
                                    /*if (myApp.currentActivity.getCurrentFocus() != null) {
                                        InputMethodManager inputMethodManager = (InputMethodManager) myApp.currentActivity.getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(myApp.currentActivity.getCurrentFocus().getWindowToken(), 0);
                                    }*/
                                    //unable to use for security dialog as currentActivity not linked

                                    if (securitySealDialog.dl != null && securitySealDialog.dl.getCurrentFocus() != null) {
                                        View windowToken = securitySealDialog.dl.getWindow().getDecorView().getRootView();
                                        InputMethodManager inputMethodManager = (InputMethodManager) securitySealDialog.dl.getContext().getSystemService(INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(windowToken.getWindowToken(), 0);
                                    }

                                    if (verified) {
                                        securitySealDialog.al.get(position).setVerified(true);
                                        securitySealDialog.al.get(position).setEnteredValue(origval);
                                        String securityseallistvalue = objGlobalMethods.getGCMPrefs(GcmPreferences, "securityseallistvalue");
                                        if (securityseallistvalue.equals("")) {
                                            securityseallistvalue = "Verified Security Seal Number(s):\n";
                                        }
                                        securityseallistvalue += origval + "\n";
                                        objGlobalMethods.setGCMPrefs(GcmPreferences, "securityseallistvalue", securityseallistvalue);
                                        notifyDataSetChanged();
                                    } else if (nonexist) {
                                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sealnotfound)).show();
                                    }
                                } else {
                                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sealdup)).show();
                                }

                            } else {
                                objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sealerr0)).show();;
                            }
                        } else {
                            objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sealerr0)).show();;
                        }
                    }

                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at SecuritySeal_PostItemAdapter rtnbtnVerifyClick. Exception: " + e.getMessage().toString(), "", "", "", "");
                    objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.alert_sealerr0)).show();;
                }
            }
        };
    }

    public final class ChildViewHolder {
        Button btnScanSeal;
        Button btnVerify;
        EditText txtSealVal;
        View.OnClickListener btnScanSealOnClickListener;
        View.OnClickListener btnVerifyOnClickListener;
    }
}
