package com.xdel.luke.Vrp;

import com.xdel.luke.objClasses.objJobInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobQueue {
    public int ItemID;
    public int ID;
    public Long DriverIDX;
    public Long JobsIDX;
    public Integer OPS_Queue;
    public Integer GC_Queue;
    public Integer VRP_Queue;
    public String VRP_ETA;
    public Integer CUSTOM_Queue;
    public String CUSTOM_ETA;
    public String PostalCode;
    public Double Latitude;
    public Double Longitude;
    public String AddressWithPostalOnly;
    public boolean IsHeader;
    public Integer TakeWhichQue; //ops 0 gc 1 vrp 2 custom 3
    public Integer JobsCount;
    public String JobsIDXs;
    public List<Long> JobsIDXsList;
    public int pNum;
    public int dNum;
    public boolean IsEnabledForJobUpdate;
    public boolean AllJobsCompleted;
    public int FlagSum;
    public int Flag2Sum;
    public boolean IsEditing;
    public String type;
    public boolean CanClick;
    public boolean isMoved;
    public int StartID;
    public int OldID;
    public int SUpCount;
    public int SDownCount;
    public int arrayIdx;
    public boolean isUpdated;
    public TimeWindow[] TimeWindows;
    public List<TimeWindow> TimeWindowsList;
    public boolean optional; //only luke has this field
    public boolean Valid;
    public boolean HASLUNCHTIME;
    public boolean isLaterJob;
    public boolean prevHasStdRunJob;
    public boolean HasStdRunJob;
    public int Flag;

    public JobQueue(Long aDriverIDX, Long aJobsIDX, List<Long> aJobsIDXsList, Integer aOPS_Queue, Integer aGC_Queue, Integer aVRP_Queue, String aVRP_ETA, Integer aCUSTOM_Queue,
                    String aCUSTOM_ETA, String aPostalCode, Double aLatitude, Double aLongitude, TimeWindow[] aTimeWindows, boolean aValid) {
        this.DriverIDX = aDriverIDX;
        this.JobsIDX = aJobsIDX;
        this.JobsIDXsList = aJobsIDXsList;
        this.OPS_Queue = aOPS_Queue;
        this.GC_Queue = aGC_Queue;
        this.VRP_Queue = aVRP_Queue;
        this.VRP_ETA = aVRP_ETA;
        this.CUSTOM_Queue = aCUSTOM_Queue;
        this.CUSTOM_ETA = aCUSTOM_ETA;
        this.PostalCode = aPostalCode;
        this.Latitude = aLatitude;
        this.Longitude = aLongitude;
        this.IsHeader = false;
        this.AllJobsCompleted = false;
        this.IsEditing = false;
        this.CanClick = true;
        this.isMoved = false;
        this.SUpCount = 0;
        this.SDownCount = 0;
        this.arrayIdx = 0;
        this.isUpdated = false;
        this.TimeWindows = aTimeWindows;
        this.Valid = aValid;
    }

    public JobQueue(JobQueue jq) {
        this.ItemID = jq.ItemID;
        this.ID = jq.ID;
        this.DriverIDX = jq.DriverIDX;
        this.JobsIDX = jq.JobsIDX;
        this.JobsIDXsList = jq.JobsIDXsList;
        this.OPS_Queue = jq.OPS_Queue;
        this.GC_Queue = jq.GC_Queue;
        this.VRP_Queue = jq.VRP_Queue;
        this.VRP_ETA = jq.VRP_ETA;
        this.CUSTOM_Queue = jq.CUSTOM_Queue;
        this.CUSTOM_ETA = jq.CUSTOM_ETA;
        this.PostalCode = jq.PostalCode;
        this.Latitude = jq.Latitude;
        this.Longitude = jq.Longitude;
        this.AddressWithPostalOnly = jq.AddressWithPostalOnly;
        this.IsHeader = jq.IsHeader;
        this.TakeWhichQue = jq.TakeWhichQue;
        this.JobsCount = jq.JobsCount;
        this.JobsIDXs = jq.JobsIDXs;
        this.pNum = jq.pNum;
        this.dNum = jq.dNum;
        this.IsEnabledForJobUpdate = jq.IsEnabledForJobUpdate;
        this.AllJobsCompleted = jq.AllJobsCompleted;
        this.FlagSum = jq.FlagSum;
        this.Flag2Sum = jq.Flag2Sum;
        this.IsEditing = jq.IsEditing;
        this.type = jq.type;
        this.CanClick = jq.CanClick;
        this.isMoved = jq.isMoved;
        this.StartID = jq.StartID;
        this.OldID = jq.OldID;
        this.SUpCount = jq.SUpCount;
        this.SDownCount = jq.SDownCount;
        this.arrayIdx = jq.arrayIdx;
        this.isUpdated = jq.isUpdated;
        this.TimeWindows = jq.TimeWindows;
        this.Valid = jq.Valid;
    }

    public JobQueue(){}

    public void setPrevHasStdRunJob(boolean prevHasStdRunJob) {
        this.prevHasStdRunJob = prevHasStdRunJob;
    }

    public void setHasStdRunJob(boolean hasStdRunJob) {
        HasStdRunJob = hasStdRunJob;
    }

    public void setisLaterJob(boolean laterJob) {
        isLaterJob = laterJob;
    }

    public void setHASLUNCHTIME(boolean HASLUNCHTIME) {
        if (!this.HASLUNCHTIME) {
            this.HASLUNCHTIME = HASLUNCHTIME;
        }
    }

    public void setValid(boolean valid) {
        Valid = valid;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public TimeWindow[] getTimeWindows() {
        return this.TimeWindows;
    }

    public void setTimeWindows(TimeWindow[] timeWindows) {
        TimeWindows = timeWindows;
    }

    public boolean getisUpdated(){
        return this.isUpdated;
    }
    public void setisUpdated(boolean aisupdated){
        this.isUpdated = aisupdated;
    }

    public int getArrayIdx(){
        return this.arrayIdx;
    }
    public void setArrayIdx(int arrayIdx1) {
        this.arrayIdx = arrayIdx1;
    }

    public int getItemID(){
        return this.ItemID;
    }
    public void setItemID(int aItemID) {
        this.ItemID = aItemID;
    }

    public int getID(){
        return this.ID;
    }
    public void setID(int aID) {
        this.ID = aID;
    }

    public Long getDriverIDX(){
        return this.DriverIDX;
    }
    public void setDriverIDX(Long aDriverIDX) {
        this.DriverIDX = aDriverIDX;
    }

    public Long getJobsIDX(){
        return this.JobsIDX;
    }
    public void setJobsIDX(Long aJobsIDX) {
        this.JobsIDX = aJobsIDX;
    }

    public Integer getOPS_Queue(){
        return this.OPS_Queue;
    }
    public void setOPS_Queue(Integer aOPS_Queue){
        this.OPS_Queue = aOPS_Queue;
    }

    public Integer getGC_Queue(){
        return this.GC_Queue;
    }
    public void setGC_Queue(Integer aGC_Queue) {
        this.GC_Queue = aGC_Queue;
    }

    public Integer getVRP_Queue(){
        return this.VRP_Queue;
    }
    public void setVRP_Queue(Integer aVRP_Queue) {
        this.VRP_Queue = aVRP_Queue;
    }

    public String getVRP_ETA(){
        return this.VRP_ETA;
    }
    public void setVRP_ETA(String aVRP_ETA) {
        this.VRP_ETA = aVRP_ETA;
    }

    public Integer getCUSTOM_Queue(){
        return this.CUSTOM_Queue;
    }
    public void setCUSTOM_Queue(Integer aCUSTOM_Queue) {
        this.CUSTOM_Queue = aCUSTOM_Queue;
    }

    public String getCUSTOM_ETA(){
        return this.CUSTOM_ETA;
    }
    public void setCUSTOM_ETA(String aCUSTOM_ETA) {
        this.CUSTOM_ETA = aCUSTOM_ETA;
    }

    public String getPostalCode(){
        return this.PostalCode;
    }
    public void setPostalCode(String aPostalCode) {
        this.PostalCode = aPostalCode;
    }

    public Double getLatitude(){
        return this.Latitude;
    }
    public void setLatitude(Double aLatitude) {
        this.Latitude = aLatitude;
    }

    public Double getLongitude(){
        return this.Longitude;
    }
    public void setLongitude(Double aLongitude) {
        this.Longitude = aLongitude;
    }

    public String getAddressWithPostalOnly(){
        return this.AddressWithPostalOnly;
    }
    public void setAddressWithPostalOnly(String aAddressWithPostalOnly){
        this.AddressWithPostalOnly = aAddressWithPostalOnly;
    }

    public Boolean getIsHeader() {
        return this.IsHeader;
    }
    public void setIsHeader(Boolean aIsHeader) {
        this.IsHeader = aIsHeader;
    }

    public Integer getTakeWhichQue(){
        return this.TakeWhichQue;
    }
    public void setTakeWhichQue(Integer aTakeWhichQue) {
        this.TakeWhichQue = aTakeWhichQue;
    }

    public Integer getJobsCount(){
        return this.JobsCount;
    }
    public void setJobsCount(Integer aJobsCount){
        this.JobsCount = aJobsCount;
    }

    public String getJobsIDXs(){
        return this.JobsIDXs;
    }
    public void setJobsIDXs(String aJobsIDXs) {
        if (this.JobsIDXs == null) {
            this.JobsIDXs = "";
        }
        if (this.JobsIDXs.equals("")) {
            this.JobsIDXs = aJobsIDXs;
        } else {
            this.JobsIDXs += "," + aJobsIDXs;
        }
    }

    public void setTimeWindowsList(TimeWindow[] tw) {
        this.TimeWindowsList = new ArrayList<TimeWindow>(Arrays.asList(tw));
    }

    public void addTimeWindowsList(TimeWindow[] tw) {
        if (this.TimeWindowsList == null) {
            this.TimeWindowsList = new ArrayList<>();
        }
        for (int i = 0; i <= tw.length -1; i++) {
            this.TimeWindowsList.add(tw[i]);
        }
    }

    public void addSingleTimeWindowToTimeWindowsList(TimeWindow tw) {
        if (this.TimeWindowsList == null) {
            this.TimeWindowsList = new ArrayList<>();
        }
        this.TimeWindowsList.add(tw);
    }

    public List<Long> getJobsIDXsList(){return this.JobsIDXsList;}
    public void setJobsIDXsList(Long aJobsIDXs) {
        if (this.JobsIDXsList == null) {
            this.JobsIDXsList = new ArrayList<>();
        }
        this.JobsIDXsList.add(aJobsIDXs);
    }
    public void setJobsIDXsListList(List<Long> aJobsIDXs) {
        this.JobsIDXsList = aJobsIDXs;
    }


    public int getpNum(){
        return this.pNum;
    }
    public void setpNum(int apNum) {
        this.pNum = apNum;
    }

    public int getdNum(){
        return this.dNum;
    }
    public void setdNum(int adNum){
        this.dNum = adNum;
    }

    public boolean getIsEnabled(){
        return this.IsEnabledForJobUpdate;
    }
    public void setIsEnabled(boolean aIsEnabled){
        this.IsEnabledForJobUpdate = aIsEnabled;
    }

    public boolean getAllJobsCompleted(){
        return this.AllJobsCompleted;
    }
    public void setAllJobsCompleted(boolean aAllJobsCompleted) {
        this.AllJobsCompleted = aAllJobsCompleted;
    }

    public void setFlagSum(int aFlagSum) {
        this.FlagSum = aFlagSum;
    }
    public void setFlag2Sum(int aFlag2Sum) { this.Flag2Sum = aFlag2Sum; }

    public boolean getIsEditing(){
        return this.IsEditing;
    }
    public void setIsEditing(boolean a) {
        this.IsEditing = a;
    }

    public String getType() {
        return this.type;
    }
    public void setType(String atype) {
        this.type = atype;
    }

    public boolean getCanClick(){
        return this.CanClick;
    }
    public void setCanClick(boolean aCanClick) {
        this.CanClick = aCanClick;
    }

    public boolean getisMoved(){
        return this.isMoved;
    }
    public void setisMoved(boolean aisMoved) {
        this.isMoved = aisMoved;
    }

    public int getStartID() {
        return this.StartID;
    }
    public void setStartID(int aStartID) {
        this.StartID = aStartID;
    }

    public int getOldID(){
        return this.OldID;
    }
    public void setOldID(int aOldID) {
        this.OldID = aOldID;
    }

    public int getSUpCount(){
        return this.SUpCount;
    }
    public void setSUpCount(int aSUpCount) {
        this.SUpCount = aSUpCount;
    }

    public int getSDownCount() {
        return this.SDownCount;
    }
    public void setSDownCount(int aSDownCount) {
        this.SDownCount = aSDownCount;
    }
}
