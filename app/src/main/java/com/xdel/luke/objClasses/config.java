package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 21/07/2016.
 */
public class config {

    public Boolean ShowRouting;
    public Boolean ShowCreditCardEntry;

    public Boolean getShowRouting(){
        return this.ShowRouting;
    }

    public void setShowRouting(Boolean aShowRouting){
        this.ShowRouting = aShowRouting;
    }

    public Boolean getShowCreditCardEntry(){
        return this.ShowCreditCardEntry;
    }

    public void setShowCreditCardEntry(Boolean aShowCreditCardEntry){
        this.ShowCreditCardEntry = aShowCreditCardEntry;
    }

}
