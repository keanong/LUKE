package com.xdel.luke.HRM_Leave;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.xdel.luke.HRM.Spinner_PostItemAdapterLeaveType;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;

public class LeaveApplication_BottomSheet_Dialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.leave_apply_bottomsheet, container, false);
        Button btnLeaveApplyCancel = v.findViewById(R.id.btnLeaveApplyCancel);
        btnLeaveApplyCancel.setOnClickListener(null);
        btnLeaveApplyCancel.setOnClickListener(btnLeaveApplyCancelClick());
        Spinner ddlLeaveType = v.findViewById(R.id.ddlLeaveType);
        loadLeaveTypeDDL(ddlLeaveType);

        return v;
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
        void onbtnLeaveApplyClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    private boolean loadLeaveTypeDDL(Spinner ddlLeaveType) {
        boolean asd = false;
        objLeaveType type = null;
        ArrayList<objLeaveType> SpinnerPostDataArr = new ArrayList<>();
        Gson gson = new Gson();
        objLeaveType[] objLeaveTypelist = null;
        try {
            SharedPreferences GcmPreferences = objGlobalMethods.getGcmPreferences();
            String LeaveTypesJsonStr = objGlobalMethods.getGCMPrefs(GcmPreferences, "LeaveTypesJsonStr");
            if (!LeaveTypesJsonStr.equals("") && !LeaveTypesJsonStr.equals("[]")) {
                objLeaveTypelist = gson.fromJson(LeaveTypesJsonStr, objLeaveType[].class);
                if (objLeaveTypelist != null && objLeaveTypelist.length > 0) {
                    //load leavetype ddl
                    for (int i = 0; i <= objLeaveTypelist.length - 1; i++) {
                        type = objLeaveTypelist[i];
                        if (((objLeaveTypelist[i].Flag & 1) != 1) && ((objLeaveTypelist[i].Flag & 4) != 4)) {
                            SpinnerPostDataArr.add(type);
                        }
                    }

                    if (SpinnerPostDataArr != null && SpinnerPostDataArr.size() > 0) {
                        Spinner_PostItemAdapterLeaveType adapter = new Spinner_PostItemAdapterLeaveType(myApp.getAppContext(), SpinnerPostDataArr);
                        ddlLeaveType.setAdapter(adapter);
                        asd = true;
                    }
                }
            }
        } catch (Exception e) {
            asd = false;
            objGlobalMethods.writeToFile("Exception at LeaveActivity loadLeaveTypeDDL. Exception: " + e.getMessage(), "", "", "", "");
        } finally {
            return asd;
        }
    }

    private View.OnClickListener btnLeaveApplyCancelClick() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    dismiss();
                } catch (Exception e) {
                    String s = e.getMessage();
                    objGlobalMethods.writeToFile("Exception at LeaveApplication_BottomSheet_Dialog btnLeaveApplyCancelClick. Exception: " + s,"","","","");
                }
            }
        };
    }

}
