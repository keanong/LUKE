package com.xdel.luke.templogger;

import com.tzone.devices.BaseDevice;

public class BaseDeviceScanObj {
    public Scan scan;
    public BaseDevice _D;
    public long MPDIDX;
    public String MAC;

    public void set_D(BaseDevice _D) {
        this._D = _D;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public void setMPDIDX(long MPDIDX) {
        this.MPDIDX = MPDIDX;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }
}
