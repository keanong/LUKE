package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 09/09/2015.
 */
public class objRecipientType {
    public String Recipient;
    public String Recipient_C;
    public boolean Photo_Required;
    public boolean Name_Required;
    public int Photos_Required;
    public boolean Is_Human;
    public String ClientIDX_List;
    public String ContactIDX_List;
    public String Inclusive_ClientIDX_List;
    public String Inclusive_ContactIDX_List;
    public String Instructions;
    public String Instructions_C;
    public int Flag;

    public objRecipientType(String aRecipient,String aRecipient_C, int aPhotos_Required, boolean aName_Required, boolean aishuman, String aClientIDX_List, String aContactIDX_List,
                            String aINCLUSIVE_CLIENTIDX_LIST, String aINCLUSIVE_CONTACTIDX_LIST, String aINSTRUCTIONS, String aINSTRUCTIONS_C, int aFlag){
        this.Recipient = aRecipient;
        this.Recipient_C = aRecipient_C;
        this.Photos_Required = aPhotos_Required;
        this.Name_Required = aName_Required;
        this.Is_Human = aishuman;
        this.ClientIDX_List = aClientIDX_List;
        this.ContactIDX_List = aContactIDX_List;
        this.Inclusive_ClientIDX_List = aINCLUSIVE_CLIENTIDX_LIST;
        this.Inclusive_ContactIDX_List = aINCLUSIVE_CONTACTIDX_LIST;
        this.Instructions = aINSTRUCTIONS;
        this.Instructions_C = aINSTRUCTIONS_C;
        this.Flag = aFlag;
    }
}
