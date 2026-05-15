package com.xdel.luke.Vrp;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdel.luke.R;

public class JobQueViewHolder2 extends RecyclerView.ViewHolder  {


    public RelativeLayout cl;
    public ImageView userAvatar;
    public TextView lblVrpAddress;
    public TextView lblVrpPostal;
    public TextView lblCount;
    public TextView lblpNum_dNum;
    public TextView lblVrpTW;
    public TextView lblpVrpLunch;
    public TextView lblVrpCold;
    public TextView lblVrpNets;
    public TextView lblVrpVisa;
    public TextView lblVrpTradein;
    public ImageView reorderView;
    public ImageView imageview_recall;
    public String Type;
    public ImageView imgnets;
    public ImageView imgcc;
    public ImageView imgtradein;
    public boolean IsEnabled;
    public boolean EnableMove;
    public int SUpCount;
    public int SDownCount;
    public int JobQueID;
    public int StartID;
    public int ID;

    public JobQueViewHolder2(View itemView) {
        super(itemView);
        cl = itemView.findViewById(R.id.lljobquelistitem);
        lblVrpAddress = itemView.findViewById(R.id.lblVrpAddress);
        lblVrpPostal = itemView.findViewById(R.id.lblVrpPostal);
        lblCount = itemView.findViewById(R.id.lblCount);
        lblpNum_dNum = itemView.findViewById(R.id.lblpNum_dNum);
        lblVrpTW = itemView.findViewById(R.id.lblVrpTW);
        lblpVrpLunch = itemView.findViewById(R.id.lblpVrpLunch);
        lblVrpCold = itemView.findViewById(R.id.lblVrpCold);
        lblVrpNets = itemView.findViewById(R.id.lblVrpNets);
        lblVrpVisa = itemView.findViewById(R.id.lblVrpVisa);
        lblVrpTradein = itemView.findViewById(R.id.lblVrpTradein);
        /*imgnets = itemView.findViewById(R.id.imgnets);
        imgcc = itemView.findViewById(R.id.imgcc);
        imgtradein = itemView.findViewById(R.id.imgtradein);*/
        reorderView = itemView.findViewById(R.id.imageview_reorder);
        //imageview_recall = itemView.findViewById(R.id.imageview_recall);
        this.IsEnabled = true;
        this.EnableMove = false;
        this.SUpCount = 0;
        this.SDownCount = 0;
        this.JobQueID = -1;
    }

    public void setID(int aID){
        this.ID = aID;
    }
    public int getID() {
        return this.ID;
    }

    public void setStartID(int aStartID) {
        this.StartID = aStartID;
    }
    public int getStartID(){
        return this.StartID;
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
