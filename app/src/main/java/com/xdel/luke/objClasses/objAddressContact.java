package com.xdel.luke.objClasses;

public class objAddressContact {
    public objAddressStructure AddressStructure;
    public objContactStructure ContactStructure;

    public objAddressContact(){};

    public objAddressContact(objAddressStructure aAddressStructure, objContactStructure aContactStructure){
        this.AddressStructure = aAddressStructure;
        this.ContactStructure = aContactStructure;
    }
}
