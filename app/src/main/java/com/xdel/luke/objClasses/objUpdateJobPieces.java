package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 14/11/2017.
 */

public class objUpdateJobPieces {
    public String ARegID;
    public long AJobsIDX;
    public int Pieces;

    public objUpdateJobPieces (String aARegID, long aAJobsIDX, int aPieces){
        this.ARegID = aARegID;
        this.AJobsIDX = aAJobsIDX;
        this.Pieces = aPieces;
    }
}
