package com.xdel.luke.HRM_Leave;

public class objLeaveType {
    public long IDX;
    public String Description;
    public String Description_C;
    public boolean IsPaid;
    public boolean CanExceed;
    public boolean AttendanceAffected;
    public int AllowedDays;
    public int Flag;
    public double Balance;
    public double BalanceToDate;
    public double Entitled;
    public double BalanceBroughtOver;

    public objLeaveType(long aIDX, String aDescription, String aDescription_C, boolean aIsPaid, boolean aCanExceed, boolean aAttendanceAffected, int aAllowedDays, int aFlag,
                        double aBalance, double aBalanceToDate, double aEntitled, double aBalanceBroughtOver) {
        this.IDX = aIDX;
        this.Description = aDescription;
        this.Description_C = aDescription_C;
        this.IsPaid = aIsPaid;
        this.CanExceed = aCanExceed;
        this.AttendanceAffected = aAttendanceAffected;
        this.AllowedDays = aAllowedDays;
        this.Flag = aFlag;
        this.Balance = aBalance;
        this.BalanceToDate = aBalanceToDate;
        this.Entitled = aEntitled;
        this.BalanceBroughtOver = aBalanceBroughtOver;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
