package com.xdel.luke.objClasses;

import java.util.HashMap;

public class objCallStatusHashmapList {
    public HashMap<String, objCallStatus> calllist;

    public objCallStatusHashmapList(){
        if (this.calllist == null) {
            this.calllist = new HashMap<String, objCallStatus>();
        }
    }

    public HashMap<String, objCallStatus> getCalllist() {
        return calllist;
    }

    public void setCalllist(HashMap<String, objCallStatus> calllist) {
        this.calllist = calllist;
    }

    public void addDialedNumber(String dialedNum, objCallStatus cs) {
        try {
            this.calllist.put(dialedNum, cs);
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objCallStatusHashmapList addDialedNumber. Exception: " + e.getMessage(),"","","","");
        }
    }

    public void removeDialedNumber(String dialedNum) {
        try {
            if (this.calllist != null && this.calllist.size() > 0 && this.calllist.containsKey(dialedNum)) {
                this.calllist.remove(dialedNum);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objCallStatusHashmapList removeDialedNumber. Exception: " + e.getMessage(),"","","","");
        }
    }


}
