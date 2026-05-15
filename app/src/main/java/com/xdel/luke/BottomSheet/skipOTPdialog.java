package com.xdel.luke.BottomSheet;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.xdel.luke.JobsHandler;
import com.xdel.luke.OtpActivity;
import com.xdel.luke.R;
import com.xdel.luke.checklist.ChecklistVariables;
import com.xdel.luke.objClasses.ctrlSettings;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objCheckedItems;
import com.xdel.luke.objClasses.objGlobalMethods;
import com.xdel.luke.objClasses.objGlobalVariables;
import com.xdel.luke.objClasses.objJobInfo;
import com.xdel.luke.sqlite.DAO_Jobs;

public class skipOTPdialog extends DialogFragment {
    private holder h;
    TextChangeListener tcl;
    private boolean IsMassOKP = false;
    private objCheckedItems[] aobjCheckedItemsAL;

    public void setMassOKP(boolean massOKP) {
        IsMassOKP = massOKP;
    }

    public void setAobjCheckedItemsAL(objCheckedItems[] aobjCheckedItemsAL) {
        this.aobjCheckedItemsAL = aobjCheckedItemsAL;
    }

    public skipOTPdialog() {

    }

    public static skipOTPdialog display(FragmentManager fragmentManager) {
        skipOTPdialog fs = new skipOTPdialog();
        try {
            fs.show(fragmentManager, "skipOTPdialog");
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at skipOTPdialog display. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return fs;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
            dialog.setCancelable(false);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_otpskipdialog, container, false);
        h = new holder();
        h.btnskipOTPdialogCancel = view.findViewById(R.id.btnskipOTPdialogCancel);
        h.btnskipOTPdialogSkip = view.findViewById(R.id.btnskipOTPdialogSkip);
        h.rgskipOTPdialog = view.findViewById(R.id.rgskipOTPdialog);
        h.txtskipOTPdialogOthers = view.findViewById(R.id.txtskipOTPdialogOthers);
        h.rgbnoaccess = view.findViewById(R.id.rgbnoaccess);
        h.rgbnocontact = view.findViewById(R.id.rgbnocontact);
        h.rgbnounds = view.findViewById(R.id.rgbnounds);
        h.rgbothers = view.findViewById(R.id.rgbothers);

        return view;
    }

    public final class holder {
        Button btnskipOTPdialogCancel;
        Button btnskipOTPdialogSkip;
        RadioGroup rgskipOTPdialog;
        EditText txtskipOTPdialogOthers;
        RadioButton rgbnoaccess;
        RadioButton rgbnocontact;
        RadioButton rgbnounds;
        RadioButton rgbothers;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            h.btnskipOTPdialogCancel.setOnClickListener(null);
            h.btnskipOTPdialogSkip.setOnClickListener(null);
            h.rgbnoaccess.setOnClickListener(null);
            h.rgbnocontact.setOnClickListener(null);
            h.rgbnounds.setOnClickListener(null);
            h.rgbothers.setOnClickListener(null);
            h.txtskipOTPdialogOthers.removeTextChangedListener(tcl);

            h.btnskipOTPdialogCancel.setOnClickListener(btnskipOTPdialogCancelOnClickListener());
            h.btnskipOTPdialogSkip.setOnClickListener(btnskipOTPdialogSkipOnClickListener());
            ctrlSettings.enableDisableFailButtonWithStyles(false, h.btnskipOTPdialogSkip);
            h.rgbnoaccess.setOnClickListener(rgbOnClickListener(h.rgbnoaccess.getId()));
            h.rgbnocontact.setOnClickListener(rgbOnClickListener(h.rgbnocontact.getId()));
            h.rgbnounds.setOnClickListener(rgbOnClickListener(h.rgbnounds.getId()));
            h.rgbothers.setOnClickListener(rgbOnClickListener(h.rgbothers.getId()));
            tcl = new TextChangeListener();
            h.txtskipOTPdialogOthers.addTextChangedListener(tcl);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at skipOTPdialog onViewCreated. Exception: " + e.getMessage() + "\nStack: " + s, "", "", "", "");
        }
    }

    private View.OnClickListener btnskipOTPdialogCancelOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipOTPdialog.this.dismiss();
            }
        };
    }

    private View.OnClickListener btnskipOTPdialogSkipOnClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipOTPdialog.this.dismiss();

                SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
                Gson gson = new Gson();

                if (IsMassOKP) {
                    if (aobjCheckedItemsAL != null && aobjCheckedItemsAL.length > 0) {
                        objCheckedItems ci = null;
                        objJobInfo currJobInfo = null;
                        String ManualPinVerificaionText = "";
                        for (int i = 0; i <= aobjCheckedItemsAL.length - 1; i++) {
                            ci = aobjCheckedItemsAL[i];
                            currJobInfo = ci.jobinfo;
                            ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText() + "\n\n" + getSkippedReason();
                            currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText);
                            currJobInfo.setTypeOfPinVerification(3);
                            currJobInfo.setOTPtries(3);
                            aobjCheckedItemsAL[i].setJobinfo(currJobInfo);

                            String objCheckedItemsJsonStr = gson.toJson(aobjCheckedItemsAL);

                            String[] prefName = null;
                            String[] prefvalue = null;
                            prefName = new String[]{objGlobalVariables.checkedItemsName, "TypeOfPinVerificationStr", "hideshow10to19NewForced"};
                            prefvalue = new String[]{objCheckedItemsJsonStr, "3", "s"};
                            objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                            startActivity(OtpActivity.nextActivtyIntent);
                            myApp.currentActivity.finish();
                        }
                    }
                } else {
                    objJobInfo currJobInfo = gson.fromJson(GcmPreferences.getString("hfJobInfoJsonStr", ""), objJobInfo.class);
                    String ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText();
                    objGlobalMethods.writeToFile("Courier chose to skip PIN verification for JobsIDX: " + String.valueOf(currJobInfo.JobsIDX), "", "", "", "");
                    if (ManualPinVerificaionText != null && !ManualPinVerificaionText.equals("")) {
                        currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText + "\nCourier chose to skip PIN verification.");
                    } else {
                        currJobInfo.setManualPinVerificaionText("Courier chose to skip PIN verification.");
                    }
                    ManualPinVerificaionText = currJobInfo.getManualPinVerificaionText() + "\n\n" + getSkippedReason();
                    currJobInfo.setManualPinVerificaionText(ManualPinVerificaionText);

                    currJobInfo.setTypeOfPinVerification(3);
                    currJobInfo.setOTPtries(3);
                    String currJobInfoPrefStr = gson.toJson(currJobInfo);
                    DAO_Jobs.updateJobInfoPrefStr(currJobInfo.JobsIDX, currJobInfoPrefStr);
                    String[] prefName = null;
                    String[] prefvalue = null;
                    prefName = new String[]{"hfJobInfoJsonStr", "TypeOfPinVerificationStr", "hideshow10to19NewForced"};
                    prefvalue = new String[]{currJobInfoPrefStr, String.valueOf(currJobInfo.getTypeOfPinVerification()), "s"};
                    objGlobalMethods.setGCMPrefsMass(GcmPreferences, prefName, prefvalue);

                    startActivity(OtpActivity.nextActivtyIntent);
                    myApp.currentActivity.finish();
                }

            }
        };
    }

    private View.OnClickListener rgbOnClickListener(final int selectedId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideshowTxt(selectedId);
            }
        };
    }

    private class TextChangeListener implements TextWatcher {

        public TextChangeListener() {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            try {

            } catch (Exception e) {
                String ex = JobsHandler.getStackTraceString(e);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {

            } catch (Exception e) {
                String ss = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at skipOTPdialog TextChangeListener onTextChanged. Exception: " + e.getMessage() + "\nStack: " + ss, "", "", "", "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                enabledisableSkipBtn();
            } catch (Exception e) {
                String st = JobsHandler.getStackTraceString(e);
                objGlobalMethods.writeToFile("Exception at skipOTPdialog TextChangeListener afterTextChanged. Exception: " + e.getMessage() + "\nStack: " + st, "", "", "", "");
            }
        }
    }

    private void hideshowTxt(int selectedId) {
        try {
            h.txtskipOTPdialogOthers.setVisibility(selectedId == R.id.rgbothers ? View.VISIBLE : View.GONE);

            /*if (selectedId != R.id.rgbothers) {
                h.txtskipOTPdialogOthers.setText("");
            }*/
            enabledisableSkipBtn();
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at skipOTPdialog hideshowTxt. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
    }

    private void enabledisableSkipBtn() {
        try {
            ctrlSettings.enableDisableFailButtonWithStyles(canProceed(), h.btnskipOTPdialogSkip);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);;
            objGlobalMethods.writeToFile("Exception at skipOTPdialog enabledisableSkipBtn. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }

    }

    private boolean canProceed() {
        boolean asd = false;
        try {
            if (h.rgskipOTPdialog.getCheckedRadioButtonId() == -1) {
                // no radio buttons are checked
                asd = false;
            } else {
                int selectedId = h.rgskipOTPdialog.getCheckedRadioButtonId();
                if (selectedId == R.id.rgbothers) {
                    if (objGlobalMethods.IsEmptyString(h.txtskipOTPdialogOthers.getText().toString().trim())) {
                        Context ctx = myApp.getAppContext();
                        objGlobalMethods.showDialog(myApp.currentActivity, ctx.getString(R.string.buttontext_ok), ctx.getString(R.string.otpskip_otherstexthint)).show();
                    } else {
                        asd = true;
                    }
                } else {
                    asd = true;
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);;
            objGlobalMethods.writeToFile("Exception at skipOTPdialog canProceed. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        }
        finally {
            return asd;
        }
    }

    private String getSkippedReason() {
        String a = "";
        Context ctx = myApp.getAppContext();
        try {
            if (h.rgskipOTPdialog.getCheckedRadioButtonId() == -1) {
                // no radio buttons are checked
                a = "";
            } else {
                int selectedId = h.rgskipOTPdialog.getCheckedRadioButtonId();
                if (selectedId == R.id.rgbothers) {
                    /*a = h.rgbothers.getText().toString();
                    a += "\n" + h.txtskipOTPdialogOthers.getText().toString();*/
                    a += h.txtskipOTPdialogOthers.getText().toString();
                } else if (selectedId == R.id.rgbnoaccess) {
                    //a = h.rgbnoaccess.getText().toString();
                    a = ctx.getString(R.string.otpskip_noaccess_v);
                } else if (selectedId == R.id.rgbnocontact) {
                    //a = h.rgbnocontact.getText().toString();
                    a = ctx.getString(R.string.otpskip_nocontact_v);
                } else if (selectedId == R.id.rgbnounds) {
                    //a = h.rgbnounds.getText().toString();
                    a = ctx.getString(R.string.otpskip_nounds_v);
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);;
            objGlobalMethods.writeToFile("Exception at skipOTPdialog getSkippedReason. Exception: " + e.getMessage() + "\nStack: " + s,"","","","");
        } finally {
            return a;
        }
    }

}
