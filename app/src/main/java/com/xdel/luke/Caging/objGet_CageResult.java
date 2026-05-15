package com.xdel.luke.Caging;

public class objGet_CageResult {
    public long IDX;
    public String Description;
    public long ZoneNameIDX;
    public String Zone;
    public long DriverIDX;
    public String DriverName;
    public long TakeOverIDX;
    public long DepthIDX;
    public String Location;
    public objCageItem[] Items_In_Cage;

    public objGet_CageResult(long aIDX, String aDescription, long aZoneNameIDX, String aZone, long aDriverIDX, String aDriverName, long aTakeOverIDX, long aDepthIDX, String aLocation, objCageItem[] aItems_In_Cage){
        this.IDX = aIDX;
        this.Description = aDescription;
        this.ZoneNameIDX = aZoneNameIDX;
        this.Zone = aZone;
        this.DriverIDX = aDriverIDX;
        this.DriverName = aDriverName;
        this.TakeOverIDX = aTakeOverIDX;
        this.DepthIDX = aDepthIDX;
        this.Location = aLocation;
        this.Items_In_Cage = aItems_In_Cage;
    }
}
