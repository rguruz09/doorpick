package com.taf.raghu.doorpick;

import java.lang.ref.SoftReference;

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
    private String driverEmail;
    private String slat;
    private String slong;
    private String dlat;
    private String dlong;
    private String custEmail;

    public Schedules(String scdId, String pName, String pDesc, String pPickUpLoc, String pPickDate, String pPickTime, String pDropLoc, String sStatus) {
        this.scdId = scdId;
        this.pName = pName;
        this.pDesc = pDesc;
        this.pPickUpLoc = pPickUpLoc;
        this.pPickDate = pPickDate;
        this.pPickTime = pPickTime;
        this.pDropLoc = pDropLoc;
        this.sStatus = sStatus;
        driverEmail = "";
        slat = "";
        slong = "";
        dlat = "";
        dlong = "";
        custEmail = "";

    }

    public String getCustEmail() {
        return custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }

    public String getDlong() {
        return dlong;
    }

    public void setDlong(String dlong) {
        this.dlong = dlong;
    }

    public String getDlat() {
        return dlat;
    }

    public void setDlat(String dlat) {
        this.dlat = dlat;
    }

    public String getSlong() {
        return slong;
    }

    public void setSlong(String slong) {
        this.slong = slong;
    }

    public String getSlat() {
        return slat;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
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
