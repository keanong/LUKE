package com.xdel.luke.Vrp;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdel.luke.R;

public class JobQueSectionHeaderViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout cl;
    public TextView sectionTitle;
    public TextView lblCancel;
    public TextView lblEdit;
    public TextView lblUpdate;
    public TextView lblReset;

    public JobQueSectionHeaderViewHolder(View itemView) {
        super(itemView);
        cl = itemView.findViewById(R.id.lljobqueheaderlistitem);
        sectionTitle = itemView.findViewById(R.id.textview_section_header);
        lblCancel = itemView.findViewById(R.id.lblCancel);
        lblEdit = itemView.findViewById(R.id.lblEdit);
        lblUpdate = itemView.findViewById(R.id.lblUpdate);
        lblReset = itemView.findViewById(R.id.lblReset);
    }

}
