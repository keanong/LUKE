package com.xdel.luke.HandTakeOver;

import com.xdel.luke.HRM_GC_Schedule.objGCSchedule;

public class L2_Get_WorkScheduleResult {
    public int Status;
    public String Message;
    public objGCSchedule[] Schedules;

    public L2_Get_WorkScheduleResult(){

    }

    public L2_Get_WorkScheduleResult(int aStatus, String aMessage, objGCSchedule[] aSchedules) {
        this.Status = aStatus;
        this.Message = aMessage;
        this.Schedules = aSchedules;
    }
}
