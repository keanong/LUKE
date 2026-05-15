package com.xdel.luke.RecycleViewsRelated.test;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xdel.luke.R;

public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView sectionTitle;
    public TextView lblCancel;
    public TextView lblEdit;
    public TextView lblUpdate;

    public SectionHeaderViewHolder(View itemView) {
        super(itemView);
        sectionTitle = itemView.findViewById(R.id.textview_section_header);
        lblCancel = itemView.findViewById(R.id.lblCancel);
        lblEdit = itemView.findViewById(R.id.lblEdit);
        lblUpdate = itemView.findViewById(R.id.lblUpdate);
    }

}
