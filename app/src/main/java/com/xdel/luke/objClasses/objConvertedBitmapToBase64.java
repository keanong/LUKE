package com.xdel.luke.objClasses;

public class objConvertedBitmapToBase64 {
    public boolean isCallLogImg;
    public String convertedBitmapToBase64;
    public String FilePath;
    public String photodesc;

    public objConvertedBitmapToBase64(boolean aisCallLogImg, String aconvertedBitmapToBase64){
        this.isCallLogImg = aisCallLogImg;
        this.convertedBitmapToBase64 = aconvertedBitmapToBase64;
    }

    public boolean isCallLogImg() {
        return isCallLogImg;
    }

    public void setCallLogImg(boolean callLogImg) {
        isCallLogImg = callLogImg;
    }

    public String getConvertedBitmapToBase64() {
        return convertedBitmapToBase64;
    }

    public void setConvertedBitmapToBase64(String convertedBitmapToBase64) {
        this.convertedBitmapToBase64 = convertedBitmapToBase64;
    }

    public void setFilePath(String filePath) {
        this.FilePath = filePath;
    }

    public void setPhotodesc(String photodesc) {
        this.photodesc = photodesc;
    }
}
