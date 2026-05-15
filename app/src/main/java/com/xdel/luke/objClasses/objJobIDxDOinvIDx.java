package com.xdel.luke.objClasses;

import java.util.List;

/**
 * Created by kean.ong on 12/04/2017.
 */
public class objJobIDxDOinvIDx {
    public Long JobsIDX;
    public List<Long> InvoiceIDX;
    public int totalcount;
    public int selectedcount;

    public objJobIDxDOinvIDx(Long aJobsIDX, List<Long> aInvoiceIDX, int atotalcount, int aselectedcount) {
        this.JobsIDX = aJobsIDX;
        this.InvoiceIDX = aInvoiceIDX;
        this.totalcount = atotalcount;
        this.selectedcount = aselectedcount;
    }

    public void setInvoiceIDX(List<Long> aInvoiceIDX) {
        this.InvoiceIDX = aInvoiceIDX;
    }

    public void setSelectedcount(int aselectedcount){
        this.selectedcount = aselectedcount;
    }
}
