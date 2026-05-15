package com.xdel.luke.checklist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;

public class clTB extends View {

    private View mView;

    /*public clTB(Context c) {
        super(c);
        try {
            LayoutInflater inflater = (LayoutInflater) c .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = inflater.inflate(R.layout.cltb, null);
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            String t = s;
        }
    }*/

    public clTB(Context c, AttributeSet attrs) {
        super(c, attrs);


    }

    /*public clTB(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
    }*/


}
