package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 11/06/2015.
 */
public class objLukeDefinitions {

    public static enum RetrieveType {
        rtNew, rtPicked, rtDelivery, rtDelivered, rtFailedPickUp, rtFailedDelivery
    }

    public static enum eExpressType {
        etNormal ("Five Thirty Plus"),
        etHalfHour ("1 Hour"),
        etOneHour ("1.5 Hour"),
        etTwoHour ("2.5 Hour"),
        etThreeHour ("3.5 Hour"),
        etPriority ("Four Thirty"),
        etGuaranteed ("Five Thirty"),
        etAOH ("AOH"),
        etWeekend ("Weekend"),
        etTwoDays ("Two Days"),
        etThreeDays ("Three Days");

        private final String ExpressType;

        private eExpressType(String s) {
            ExpressType = s;
        }

        public boolean equalsName(String otherExpressType){
            return (otherExpressType == null)? false:ExpressType.equals(otherExpressType);
        }

        public String toString(){
            return ExpressType;
        }

    }

    public static enum eCODCollectAt {
        ctPickup ("Pick Up"),
        ctDelivery ("Delivery"),
        ctOriginator ("Originator");

        private final String CODCollectAt;

        private eCODCollectAt(String s) {
            CODCollectAt = s;
        }

        public boolean equalsName(String otherCODCollectAt){
            return (otherCODCollectAt == null)? false:CODCollectAt.equals(otherCODCollectAt);
        }

        public String toString(){
            return CODCollectAt;
        }

    }

    public static enum eTOSType {
        ttUnknown (""),
        ttEL ("One Way"),
        ttTC ("Collection"),
        ttRT ("Two Way");

        private final String TOSType;

        private eTOSType(String s) {
            TOSType = s;
        }

        public boolean equalsName(String otherTOSType){
            return (otherTOSType == null)? false:TOSType.equals(otherTOSType);
        }

        public String toString(){
            return TOSType;
        }

    }

}

