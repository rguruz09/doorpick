package com.taf.raghu.doorpick;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Raghu on 12/10/2016.
 */

public class ScheduleListSington {

    private static ScheduleListSington scheduleListSington;
    private Context mAppContext;
    private ArrayList<Schedules> scheduls;

    private ScheduleListSington(Context appContext) {
        mAppContext = appContext;
        scheduls = new ArrayList<Schedules>();

    }

    /*private OrdersSingleton(Context appContext) {
        mAppContext = appContext;
        ordersListInfos = new ArrayList<OrdersListInfo>();
    }*/

    public void clearList(){
        scheduls.clear();
    }

    public void addScheduleList(ArrayList<Schedules> scls){
        scheduls = scls;
    }

    public void addOneSchedule(Schedules sc){
        scheduls.add(sc);
    }

    public void serScheduleList(ArrayList<Schedules> sc){
        scheduls = sc;
    }
    public static ScheduleListSington get(Context c) {
        if (scheduleListSington == null) {
            scheduleListSington = new ScheduleListSington(c.getApplicationContext());
        }
        return scheduleListSington;
    }

    public ArrayList<Schedules> getOrdersListInfos(){
        return scheduls;
    }

}
