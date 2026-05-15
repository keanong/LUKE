package com.xdel.luke.RecycleViewsRelated.test;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdel.luke.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout cl;
    public ImageView userAvatar;
    public TextView username;
    public ImageView reorderView;
    public String Type;

    public UserViewHolder(View itemView) {
        super(itemView);
        cl = itemView.findViewById(R.id.lluserlistitem);
        //userAvatar = itemView.findViewById(R.id.imageview_profile);
        username = itemView.findViewById(R.id.textview_name);
        reorderView = itemView.findViewById(R.id.imageview_reorder);
    }

    public void setType(String aType) {
        this.Type = aType;
    }

    public String getType(){
        return Type;
    }
}
