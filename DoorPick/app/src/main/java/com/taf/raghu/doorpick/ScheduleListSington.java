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

//        Schedules s1 = new Schedules(1,"sample1","Small","Desc1","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//        Schedules s2 = new Schedules(2,"sample2","Small","Desc2","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//        Schedules s3 = new Schedules(3,"sample3","Small","Desc3","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//        Schedules s4 = new Schedules(4,"sample1","Small","Desc1","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//        Schedules s5 = new Schedules(5,"sample2","Small","Desc2","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//        Schedules s6 = new Schedules(6,"sample3","Small","Desc3","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//        Schedules s7 = new Schedules(7,"sample1","Small","Desc1","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//        Schedules s8 = new Schedules(8,"sample2","Small","Desc2","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//        Schedules s9 = new Schedules(9,"sample3","Small","Desc3","1325 the alameda","10/10/2017","10:10 Pm","library","completed");
//
//        scheduls.add(s1);
//        scheduls.add(s2);
//        scheduls.add(s3);
//        scheduls.add(s4);
//        scheduls.add(s5);
//        scheduls.add(s6);
//        scheduls.add(s7);
//        scheduls.add(s8);
//        scheduls.add(s9);

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

//    public Schedules getOrdersListInfo(int position){
//        for(Schedules od: scheduls){
//            if (od.getScdId() == position){
//                return od;
//            }
//        }
//        return null;
//    }

//    public void removeOrdersListInfo(String name){
//        for(int i=0; i<scheduls.size(); i++){
//            if (scheduls.get(i).getCustomerName().equals(name)){
//                ordersListInfos.remove(i);
//                break;
//            }
//        }
//    }
}
