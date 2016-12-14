package com.taf.raghu.doorpick;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Raghu on 12/12/2016.
 */

public class ReqScheSington {

    private static ReqScheSington reqScheSington;
    private ArrayList<Schedules> scheduls;

    private ReqScheSington() {
        scheduls = new ArrayList<Schedules>();
    }

    public void clearList(){
        scheduls.clear();
    }

    public void addScheduleList(ArrayList<Schedules> scls){
        scheduls = scls;
    }

    public void addOneSchedule(Schedules sc){
        scheduls.add(sc);
    }

    public void setScheduleList(ArrayList<Schedules> sc){
        scheduls = sc;
    }

    public static ReqScheSington get() {
        if (reqScheSington == null) {
            reqScheSington = new ReqScheSington();
        }
        return reqScheSington;
    }

    public ArrayList<Schedules> getOrdersListInfos(){
        return scheduls;
    }

}
