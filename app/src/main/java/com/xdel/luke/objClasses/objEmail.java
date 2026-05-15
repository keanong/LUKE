package com.xdel.luke.objClasses;

/**
 * Created by kean.ong on 03/10/2015.
 */
public class objEmail {

    public String attachmentfilename;
    public String bodystr;
    public String subjectstr;
    public String ToEmailAddress;

    public objEmail(String aattachmentfilename, String abodystr, String asubjectstr, String aToEmailAddress){
        this.attachmentfilename = aattachmentfilename;
        this.bodystr = abodystr;
        this.subjectstr = asubjectstr;
        this.ToEmailAddress = aToEmailAddress;
    }

}
