package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 13/06/2017.
 */
public class COD_List_PostData {

    String ID, ExpectedCashValue, DeclaredCashValue, ExpectedChequeValue, DeclaredChequeValue, DeclaredChequeRef,
            ExpectedNetsValue, DeclaredNetsValue, DeclaredNetsRef,
            ExpectedVisaMasterValue, DeclaredVisaMasterValue, DeclaredVisaMasterRef,
            ExpectedVoucherValue, DeclaredVoucherValue, DeclaredVoucherRef,
            ExpectedTotalValue, ExpectedTotalBalanceValue;

    public COD_List_PostData(){

    }

    public String getID(){
        return this.ID;
    }

    public void setID(String id) {
        this.ID = id;
    }
}
