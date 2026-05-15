package com.xdel.luke.objClasses;

import com.xdel.luke.sqlite.objRouteJobEarliestTiming;

/**
 * Created by kean.ong on 05/07/2016.
 */
public class AddressListRouting_PostData {

    public String postTitle;
    public String postCount;
    public String JobsIDXArrJsonStr;
    public String Postal;
    public Double LAT;
    public Double LNG;
    public Boolean changeCountColorBool;
    public String id;
    public objRouteJobEarliestTiming rjet;

    public AddressListRouting_PostData(String postTitle, String postCount, String aJobsIDXArrJsonStr, String Postal,
                                       Double aLAT, Double aLNG, Boolean achangeCountColorBool, String aid) {
        this.postTitle = postTitle;
        this.postCount = postCount;
        this.JobsIDXArrJsonStr = aJobsIDXArrJsonStr;
        this.Postal = Postal;
        this.LAT = aLAT;
        this.LNG = aLNG;
        this.changeCountColorBool = achangeCountColorBool;
        this.id = aid;
    }

    public void setRjet(objRouteJobEarliestTiming rjet) {
        this.rjet = rjet;
    }

    public objRouteJobEarliestTiming getRjet(){
        return this.rjet;
    }
}

