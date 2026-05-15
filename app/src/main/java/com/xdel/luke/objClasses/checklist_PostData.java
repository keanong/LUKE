package com.xdel.luke.objClasses;

import com.xdel.luke.CustomGallery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kean.ong on 06/07/2017.
 */
public class checklist_PostData {
    public String FullCode;
    public int HierarchyCode;
    public String ActionCode;
    public String ControlCode;
    public String ManOptCode;
    public String DisplayText;
    public String DisplayTextCN;
    public String[] Option;
    public String[] OptionCN;
    public Boolean AltColor;
    public ArrayList<String> OptionValue;
    public ArrayList<String> OptionValue2;
    public Date SelectedDate;
    public String FinalManOptCode;
    public Boolean IsChecked;
    public Boolean IsParent = false;
    public Boolean IsChild = false;
    public int ParentHierarchyCode;
    public int ChildHierarchyCode;
    public int ParentIDX;
    public String PhotoImagePath;
    public ArrayList<CustomGallery> dataT;
    public Integer index;
    public Date SelectedDatePending;
    public Integer SelectedDatePendingDay;
    public Integer SelectedDatePendingMth;
    public Integer SelectedDatePendingYear;
    public Integer SelectedDatePendingYearIndex;
    public boolean hidden;
    public String DelBlock = "";
    public String DelUnit_original = "";
    public String DelUnit_combine = "";
    public List<String> AllDelUnits;
    public List<String> AllDelUnitsCombined;
    public Integer FLAG;


    public checklist_PostData(String aFullCode, int aHierarchyCode, String aActionCode, String aControlCode, String aManOptCode, String aDisplayText, String aDisplayTextCN,
                              String[] aOption, Boolean aAltColor, Date aSelectedDate, String aFinalManOptCode, Integer aindex, Integer aFLAG) {
        this.FullCode = aFullCode;
        this.HierarchyCode = aHierarchyCode;
        this.ActionCode = aActionCode;
        this.ControlCode = aControlCode;
        this.ManOptCode = aManOptCode;
        this.DisplayText = aDisplayText;
        this.DisplayTextCN = aDisplayTextCN;
        this.Option = aOption;
        this.AltColor = aAltColor;
        this.OptionValue = null;
        this.SelectedDate = aSelectedDate;
        this.FinalManOptCode = aFinalManOptCode;
        this.IsChecked = false;
        this.ParentIDX = -1;
        this.PhotoImagePath = "";
        this.dataT = null;
        this.index = aindex;
        this.SelectedDatePending = null;
        this.SelectedDatePendingDay = null;
        this.SelectedDatePendingMth = null;
        this.SelectedDatePendingYear = null;
        this.SelectedDatePendingYearIndex = 0;
        this.FLAG = aFLAG;
    }

    public void setFLAG(Integer FLAG) {
        this.FLAG = FLAG;
    }

    public String getDelBlock() {
        return DelBlock;
    }

    public void setDelBlock(String delBlock) {
        DelBlock = delBlock;
    }

    public String getDelUnit_original() {
        return DelUnit_original;
    }

    public void setDelUnit_original(String delUnit_original) {
        DelUnit_original = delUnit_original;
    }

    public String getDelUnit_combine() {
        return DelUnit_combine;
    }

    public void setDelUnit_combine(String delUnit_combine) {
        DelUnit_combine = delUnit_combine;
    }

    public List<String> getAllDelUnits() {
        return AllDelUnits;
    }

    public void setAllDelUnits(List<String> allDelUnits) {
        AllDelUnits = allDelUnits;
    }

    public List<String> getAllDelUnitsCombined() {
        return AllDelUnitsCombined;
    }

    public void setAllDelUnitsCombined(List<String> allDelUnitsCombined) {
        AllDelUnitsCombined = allDelUnitsCombined;
    }

    public boolean getHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Integer getSelectedDatePendingYearIndex() {
        return this.SelectedDatePendingYearIndex;
    }
    public void setSelectedDatePendingYearIndex(Integer aSelectedDatePendingYearIndex){
        this.SelectedDatePendingYearIndex = aSelectedDatePendingYearIndex;
    }

    public Integer getSelectedDatePendingDay(){
        return this.SelectedDatePendingDay;
    }
    public void setSelectedDatePendingDay(Integer aSelectedDatePendingDay) {
        this.SelectedDatePendingDay = aSelectedDatePendingDay;
    }

    public Integer getSelectedDatePendingMth(){
        return this.SelectedDatePendingMth;
    }
    public void setSelectedDatePendingMth(Integer aSelectedDatePendingMth) {
        this.SelectedDatePendingMth = aSelectedDatePendingMth;
    }

    public Integer getSelectedDatePendingYear(){
        return this.SelectedDatePendingYear;
    }
    public void setSelectedDatePendingYear(Integer aSelectedDatePendingYear) {
        this.SelectedDatePendingYear = aSelectedDatePendingYear;
    }

    public Integer getIndex(){
        return this.index;
    }
    public void setIndex(Integer aindex) {
        this.index = aindex;
    }

    public String getPhotoImagePath() {
        return this.PhotoImagePath;
    }
    public void setPhotoImagePath(String aPhotoImagePath) {
        this.PhotoImagePath = aPhotoImagePath;
    }

    public ArrayList<CustomGallery> getDataT() {
        return dataT;
    }
    public void addDataT(CustomGallery datat) {
        if (dataT == null) {
            dataT = new ArrayList<CustomGallery>();
        }
        dataT.add(datat);
    }

    public void setDataT(ArrayList<CustomGallery> datat){
        dataT = datat;
    }

    public int getParentIDX(){
        return this.ParentIDX;
    }
    public void setParentIDX(int aParentIDX) {
        this.ParentIDX = aParentIDX;
    }

    public Boolean getIsParent(){
        return this.IsParent;
    }
    public void setIsParent(Boolean aIsParent) {
        this.IsParent = aIsParent;
    }

    public Boolean getIsChild(){
        return this.IsChild;
    }
    public void setIsChild(Boolean aIsChild) {
        this.IsChild = aIsChild;
    }

    public int getParentHierarchyCode(){
        return this.ParentHierarchyCode;
    }
    public void setParentHierarchyCode(int aParentHierarchyCode) {
        this.ParentHierarchyCode = aParentHierarchyCode;
    }

    public int getChildHierarchyCode(){
        return this.ChildHierarchyCode;
    }
    public void setChildHierarchyCode(int aChildHierarchyCode){
        this.ChildHierarchyCode = aChildHierarchyCode;
    }

    public String getFullCode(){
        return this.FullCode;
    }
    public void setFullCode(String aFullCode) {
        this.FullCode = aFullCode;
    }

    public int getHierarchyCode(){
        return this.HierarchyCode;
    }
    public void setHierarchyCode(int aHierarchyCode) {
        this.HierarchyCode = aHierarchyCode;
    }

    public String getActionCode(){
        return this.ActionCode;
    }
    public void setActionCode(String aActionCode) {
        this.ActionCode = aActionCode;
    }

    public String getControlCode() {
        return this.ControlCode;
    }
    public void setControlCode(String aControlCode) {
        this.ControlCode = aControlCode;
    }

    public String getManOptCode(){
        return this.ManOptCode;
    }
    public void setManOptCode(String aManOptCode) {
        this.ManOptCode = aManOptCode;
    }

    public String getDisplayTextCN(){
        return this.DisplayTextCN;
    }
    public void setDisplayTextCN(String aDisplayTextCN) {
        this.DisplayTextCN = aDisplayTextCN;
    }

    public String getDisplayText(){
        return this.DisplayText;
    }
    public void setDisplayText(String aDisplayText) {
        this.DisplayText = aDisplayText;
    }

    public String[] getOption(){
        return this.Option;
    }
    public void setOption(String[] aOption){
        this.Option = aOption;
    }

    public String[] getOptionCN() {
        return OptionCN;
    }
    public void setOptionCN(String[] optionCN) {
        OptionCN = optionCN;
    }

    public Boolean getAltColor(){
        return this.AltColor;
    }
    public void setAltColor(Boolean aAltColor) {
        this.AltColor = aAltColor;
    }

    public ArrayList<String> getOptionValue() {
        return this.OptionValue;
    }
    public void setOptionValue(ArrayList<String> aOptionValue) {
        this.OptionValue = aOptionValue;
    }

    public ArrayList<String> getOptionValue2() {
        return this.OptionValue2;
    }
    public void setOptionValue2(ArrayList<String> aOptionValue2) {
        this.OptionValue2 = aOptionValue2;
    }

    public Date getSelectedDate() {
        return this.SelectedDate;
    }
    public void setSelectedDate(Date aSelectedDate) {
        this.SelectedDate = aSelectedDate;
    }

    public Date getSelectedDatePending(){
        return this.SelectedDatePending;
    }

    public void setSelectedDatePending(Date aSelectedDatePending) {
        this.SelectedDatePending = aSelectedDatePending;
    }

    public String getFinalManOptCode() {
        return this.FinalManOptCode;
    }
    public void setFinalManOptCode(String aFinalManOptCode) {
        this.FinalManOptCode = aFinalManOptCode;
    }

    public Boolean getIsChecked(){ return this.IsChecked; }
    public void setIsChecked(Boolean aIsChecked) {
        this.IsChecked = aIsChecked;
    }

}
