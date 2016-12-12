package com.taf.raghu.doorpick;

/**
 * Created by raghu on 22/11/16.
 */
public class Schedules {

    private String scdId;
    private String pName;
    private String pDesc;
    private String pPickUpLoc;
    private String pPickDate;
    private String pPickTime;
    private String pDropLoc;
    private String sStatus;

    public Schedules(String scdId, String pName, String pDesc, String pPickUpLoc, String pPickDate, String pPickTime, String pDropLoc, String sStatus) {
        this.scdId = scdId;
        this.pName = pName;
        this.pDesc = pDesc;
        this.pPickUpLoc = pPickUpLoc;
        this.pPickDate = pPickDate;
        this.pPickTime = pPickTime;
        this.pDropLoc = pDropLoc;
        this.sStatus = sStatus;
    }

    public String getScdId() {
        return scdId;
    }

    public String getpName() {
        return pName;
    }

    public String getpDesc() {
        return pDesc;
    }

    public String getpPickUpLoc() {
        return pPickUpLoc;
    }

    public String getpPickDate() {
        return pPickDate;
    }

    public String getpPickTime() {
        return pPickTime;
    }

    public String getpDropLoc() {
        return pDropLoc;
    }

    public String getsStatus() {
        return sStatus;
    }

    public void setsStatus(String sStatus) {
        this.sStatus = sStatus;
    }
}
