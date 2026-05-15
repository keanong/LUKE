package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 03/10/2017.
 */

public class gvDatePickerDay_PostData {
    Integer Value;
    Boolean Selected;
    Boolean Enabled;

    public gvDatePickerDay_PostData(Integer aValue, Boolean aSelected, Boolean aEnabled){
        this.Value = aValue;
        this.Selected = aSelected;
        this.Enabled = aEnabled;
    }

    public void setSelected(Boolean aSelected) {
        this.Selected = aSelected;
    }

    public void setEnabled(Boolean aEnabled) {
        this.Enabled = aEnabled;
    }
}
