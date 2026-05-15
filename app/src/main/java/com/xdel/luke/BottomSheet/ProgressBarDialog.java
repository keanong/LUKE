package com.xdel.luke.BottomSheet;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xdel.luke.R;

public class ProgressBarDialog extends AlertDialog {

    private TextView lblText;

    public ProgressBarDialog(Context ctx){
        super(ctx);
        initialize();
    }

    private void initialize() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.progressbar_layout,null);
        lblText = v.findViewById(R.id.lblText);
        setView(v);
    }

    public void setText(String txt) {
        lblText.setText(txt);
    }


}
