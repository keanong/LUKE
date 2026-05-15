package com.xdel.luke.objClasses;

public class objEmailItem {
    public String[] toArr;
    public String subjectstr;
    public String bodystr;
    public String attachmentfilename;
    public boolean DeleteAttachementFile;
    public String EmailObjectiveTitle;
    public int sentAttempt;

    public objEmailItem(String[] atoArr, String asubjectstr, String abodystr, String aattachmentfilename, String aEmailObjectiveTitle) {
        toArr = atoArr;
        this.subjectstr = asubjectstr;
        this.bodystr = abodystr;
        this.attachmentfilename = aattachmentfilename;
        this.EmailObjectiveTitle = aEmailObjectiveTitle;
        this.sentAttempt = 1;
    }

    public void setDeleteAttachementFile(boolean deleteAttachementFile) {
        DeleteAttachementFile = deleteAttachementFile;
    }

    public void setSentAttempt() {
        this.sentAttempt++;
    }
}
