package com.xdel.luke.mpd;

public class MPD_Group {
    public long IDX;
    public long UserIDX;
    public String Group_Creator;
    public long DriverIDX;
    public String Current_GC;
    public String Group_Created;
    public int Flag;
    public String Group_Contents;
    public MPD_Group_Item[] Items;
    public long[] Jobs_Attached;
    public int Status;
    public String Message;
    public String[] References_Attached;

    public MPD_Group() {

    }

    public void setIDX(long IDX) {
        this.IDX = IDX;
    }

    public void setUserIDX(long userIDX) {
        UserIDX = userIDX;
    }

    public void setGroup_Creator(String group_Creator) {
        Group_Creator = group_Creator;
    }

    public void setDriverIDX(long driverIDX) {
        DriverIDX = driverIDX;
    }

    public void setCurrent_GC(String current_GC) {
        Current_GC = current_GC;
    }

    public void setGroup_Created(String group_Created) {
        Group_Created = group_Created;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }

    public void setGroup_Contents(String group_Contents) {
        Group_Contents = group_Contents;
    }

    public void setItems(MPD_Group_Item[] items) {
        Items = items;
    }

    public void setJobs_Attached(long[] jobs_Attached) {
        Jobs_Attached = jobs_Attached;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
