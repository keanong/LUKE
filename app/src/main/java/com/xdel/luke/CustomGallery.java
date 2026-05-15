package com.xdel.luke;

public class CustomGallery {

	public String sdcardPath;
	public boolean isSeleted = false;
	public boolean isCallLog = false;
	public boolean isItemProof = false;
	public boolean isTempLogger = false;
	public String photodesc = "";
	public int checklistitemidx = -1;
	public String ImgBitmapStr;

	public void setCallLog(boolean callLog) {
		isCallLog = callLog;
	}

	public void setItemProof(boolean itemProof) {
		isItemProof = itemProof;
	}

	public void setTempLogger(boolean tempLogger) {
		isTempLogger = tempLogger;
	}

	public void setPhotodesc(String photodesc) {
		this.photodesc = photodesc;
	}

	public void setChecklistitemidx(int checklistitemidx) {
		this.checklistitemidx = checklistitemidx;
	}

	public void setImgBitmapStr(String imgBitmapStr) {
		ImgBitmapStr = imgBitmapStr;
	}
}
