package com.xdel.luke.objClasses;

import com.xdel.luke.JobsHandler;

import java.util.ArrayList;

public class objCallCheck {
    public ArrayList<String> JobNos;
    public ArrayList<String> ContactNo;

    public objCallCheck() {

    }

    public void addJobNos(String n) {
        if (JobNos == null) {
            JobNos = new ArrayList<>();
        }
        if (!JobNos.contains(n)) {
            JobNos.add(n);
        }
    }

    public void removeJobNos(String n) {
        if (JobNos != null && JobNos.size() > 0) {
            for (int i = 0; i <= JobNos.size()-1 ; i++) {
                if (objGlobalMethods.IsEqualString(JobNos.get(i), n)) {
                    JobNos.remove(i);
                }
            }
        }
    }

    public void clearJobNos(){
        JobNos = null;
    }

    public void addContactNo(String n) {
        if (ContactNo == null) {
            ContactNo = new ArrayList<>();
        }
        if (n.contains(",")) {
            String[] ns = n.split(",");
            if (ns != null && ns.length > 0) {
                for (int i = 0; i <= ns.length-1; i++) {
                    ContactNo.add(ns[i]);
                }
            }
        } else if (!ContactNo.contains(n)) {
            ContactNo.add(n);
        }
    }

    public void removeContactNo(String n) {
        if (ContactNo != null && ContactNo.size() > 0) {
            for (int i = 0; i <= ContactNo.size()-1 ; i++) {
                if (objGlobalMethods.IsEqualString(ContactNo.get(i), n)) {
                    ContactNo.remove(i);
                    break;
                }
            }
        }
    }

    public void clearContactNo(){
        ContactNo = null;
    }

    public boolean ContactNosExists(String n) {
        int f = 0;
        boolean asd = false;
        try {
            if (n.contains(",")) {
                String[] ns = n.split(",");
                if (ns != null && ns.length > 0) {
                    for (int i = 0; i <= ns.length-1; i++) {
                        if (ContactNo.contains(ns[i]) && (f & 1) != 1) {
                            f += 1;
                        } else if (ContactNo.contains(ns[i]) && (f & 1) == 1) {
                            //do nothing
                        } else if (!ContactNo.contains(ns[i]) && (f & 2) != 2) {
                            f += 2;
                        } else if (!ContactNo.contains(ns[i]) && (f & 2) == 2) {
                            //do nothing
                        }
                    }
                }
                asd = (f == 0) || ((f & 2) == 2);
            } else {
                asd = ContactNo.contains(n);
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objCallCheck ContactNosExists. Exception: " + e.getMessage(),"","","","");
        } finally {
            return asd;
        }
    }

    public String getAdditionalCallLogsInfo(){
        String s = "";

        try {
            //return empty string if update only 1 job
            if (JobNos != null && JobNos.size() > 1 && ContactNo != null && ContactNo.size() > 0) {
                //s = "Call Log info from the following Job No(s).\n";
                s = "\n\nCall evidence can be found in the following Job No(s).\n";
                for (int i = 0; i <= JobNos.size()-1; i++) {
                    if (i == JobNos.size() -1) {
                        s += JobNos.get(i);
                    } else {
                        s += JobNos.get(i) + ",";
                    }
                }
            }
        } catch (Exception e) {
            objGlobalMethods.writeToFile("Exception at objCallCheck getAdditionalCallLogsInfo. Exception: " + e.getMessage(),"","","","");
        } finally {
            return s;
        }
    }


}
