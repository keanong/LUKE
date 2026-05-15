package com.xdel.luke.objClasses;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.xdel.luke.CheckListActivity;
import com.xdel.luke.CustomDateActivity;
import com.xdel.luke.R;

import java.util.ArrayList;

public class gvDatePickerMthAdapter2 extends ArrayAdapter<gvDatePickerDay_PostData> {
    private Context mContext;
    private ArrayList<gvDatePickerDay_PostData> list;
    private Integer checklistindex;

    public gvDatePickerMthAdapter2(Context c, ArrayList<gvDatePickerDay_PostData> gvDatePickerDayPostData, Integer achecklistindex) {
        super(c, 0, gvDatePickerDayPostData);
        mContext = c;
        list = gvDatePickerDayPostData;
        checklistindex = achecklistindex;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        gvDatePickerDay_PostData data = getItem(pos);
        Button btn = new Button(mContext);

        try {
            if (convertView == null) {
                btn = new Button(mContext);
                btn.setLayoutParams(Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? new GridView.LayoutParams(80, 80) : new GridView.LayoutParams(110, 110));
            } else {
                btn = (Button) convertView;
            }

            btn.setText(objGlobalMethods.returnShortMonthString(data.Value));
            btn.setTextColor(Color.BLACK);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, Build.VERSION.SDK_INT < Build.VERSION_CODES.N ? 11 : 11);
            btn.setTypeface(null,  Typeface.BOLD);

            btn.setOnClickListener(null);
            btn.setOnClickListener(btn_OnClickListener(pos, checklistindex));
            btn.setBackgroundResource(data.Selected ? R.drawable.btn_day_selected : R.drawable.btn_day_nselected);
        } catch (Exception e) {
            //String s = JobsHandler.getStackTraceString(e);
            //objGlobalMethods.writeToFile("Exception at gvDatePickerMthAdapter getView. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
            objGlobalMethods.writeToFile("Exception at gvDatePickerMthAdapter2 getView. Exception: " + e.getMessage().toString(),"","","","");
        } finally {
            return btn;
        }
    }

    private View.OnClickListener btn_OnClickListener(final int pos, final Integer checklistindex) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    for (int i = 0; i <= list.size() - 1; i++) {
                        list.get(i).setSelected(i == pos ? true : false);
                    }
                    notifyDataSetChanged();
                    if (CheckListActivity.al != null && CheckListActivity.al.size() > 0) {
                        CheckListActivity.al.get(checklistindex).setSelectedDatePendingMth(pos);
                    }
                    CustomDateActivity.loadgvDay();
                } catch (Exception e) {
                    objGlobalMethods.writeToFile("Exception at gvDatePickerMthAdapter2 btn_OnClickListener. Exception: " + e.getMessage().toString(), "", "", "", "");
                }
            }
        };
    }

}
