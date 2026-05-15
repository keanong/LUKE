package com.xdel.luke.objClasses;

public class objAuthRequest {
    String LoginName;
    String Password;
    String RegID;
    String AUTC;
    double Lat;
    double Lng;
    int Accuracy;
    String QRCode;
    boolean CommunalDevice;
    int Flag; //<Flag>Regular or AdministrativeMode or Unused1 or Unused2</Flag>

    public objAuthRequest (String aLoginName, String aPassword, String aRegID, String aAUTC, double aLat, double aLng, int aAccuracy, String aQRCode) {
        this.LoginName = aLoginName;
        this.Password = aPassword;
        this.RegID = aRegID;
        this.AUTC = aAUTC;
        this.Lat = aLat;
        this.Lng = aLng;
        this.Accuracy = aAccuracy;
        this.QRCode = aQRCode;
    }

    public void setCommunalDevice(boolean communalDevice) {
        CommunalDevice = communalDevice;
    }

    public void setFlag(int flag) {
        Flag = flag;
    }
}
