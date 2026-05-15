package com.xdel.luke.HRM;

import android.graphics.Bitmap;

public class objPassQR_IDbarcode {

    public String QRimgBase64;
    public Bitmap IDbarcode;

    public objPassQR_IDbarcode(){
        this.QRimgBase64 = "";
        this.IDbarcode = null;
    }

    public void setQRimgBase64(String QRimgBase64) {
        this.QRimgBase64 = QRimgBase64;
    }

    public String getQRimgBase64() {
        return QRimgBase64;
    }

    public void setIDbarcode(Bitmap IDbarcode) {
        this.IDbarcode = IDbarcode;
    }

    public Bitmap getIDbarcode() {
        return IDbarcode;
    }
}
