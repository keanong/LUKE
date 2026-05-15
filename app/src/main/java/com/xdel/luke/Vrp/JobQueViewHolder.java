package com.xdel.luke.Vrp;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdel.luke.R;

public class JobQueViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout cl;
    public ImageView userAvatar;
    public TextView lblVrpAddress;
    public TextView lblVrpPostal;
    public TextView lblCount;
    public TextView lblpNum_dNum;
    public ImageView reorderView;
    public String Type;
    public ImageView imgnets;
    public ImageView imgcc;
    public ImageView imgtradein;
    public boolean IsEnabled;
    public boolean EnableMove;
    public int SUpCount;
    public int SDownCount;
    public int JobQueID;

    public JobQueViewHolder(View itemView) {
        super(itemView);
        cl = itemView.findViewById(R.id.lljobquelistitem);
        lblVrpAddress = itemView.findViewById(R.id.lblVrpAddress);
        lblVrpPostal = itemView.findViewById(R.id.lblVrpPostal);
        lblCount = itemView.findViewById(R.id.lblCount);
        lblpNum_dNum = itemView.findViewById(R.id.lblpNum_dNum);
        imgnets = itemView.findViewById(R.id.imgnets);
        imgcc = itemView.findViewById(R.id.imgcc);
        imgtradein = itemView.findViewById(R.id.imgtradein);
        reorderView = itemView.findViewById(R.id.imageview_reorder);
        this.IsEnabled = true;
        this.EnableMove = false;
        this.SUpCount = 0;
        this.SDownCount = 0;
        this.JobQueID = -1;
    }

    public void setType(String aType) {
        this.Type = aType;
    }

    public String getType() {
        return Type;
    }

    public boolean getIsEnabled(){
        return this.IsEnabled;
    }
    public void setIsEnabled(boolean aIsEnabled) {
        this.IsEnabled = aIsEnabled;
    }

    public boolean getEnableMove(){
        return this.EnableMove;
    }
    public void setEnableMove(boolean aEnableMove) {
        this.EnableMove = aEnableMove;
    }

    public int getSUpCount(){
        return this.SUpCount;
    }
    public void setSUpCount(int aSUpCount) {
        this.SUpCount = aSUpCount;
    }

    public int getSDownCount() {
        return this.SDownCount;
    }
    public void setSDownCount(int aSDownCount) {
        this.SDownCount = aSDownCount;
    }

    public int getJobQueID(){
        return this.JobQueID;
    }
    public void setJobQueID(int aJobQueID) {
        this.JobQueID = aJobQueID;
    }
}
