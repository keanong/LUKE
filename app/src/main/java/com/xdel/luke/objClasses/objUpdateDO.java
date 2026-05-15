package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 12/04/2017.
 */
public class objUpdateDO {
    public String ARegID;
    public long AJobsIDX;
    public String[] tags;
    public Boolean ADelivered;
    public String AUTC;

    public objUpdateDO (String aARegID, long aAJobsIDX, String[] atags, Boolean aADelivered, String aAUTC){
        this.ARegID = aARegID;
        this.AJobsIDX = aAJobsIDX;
        this.tags = atags;
        this.ADelivered = aADelivered;
        this.AUTC = aAUTC;

    }

}
