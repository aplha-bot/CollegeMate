package com.example.collegemate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Collections;
import java.util.Comparator;

public class BasicFunctions {

    static Integer getDayIndex(String day){
        if(day.equals("MON")){
            return 0;
        }else if(day.equals("TUE")){
            return 1;
        }else if(day.equals("WED")){
            return 2;
        }else if(day.equals("THU")){
            return  3;
        }else if(day.equals("FRI")){
            return 4;
        }else if(day.equals("SAT")){
            return 5;
        }
        return 0 ;
    }

    static void SortTimeTableData(int day){
        class SortByTime implements Comparator<Global.ModalClasses.TimeTableModal> {

            @Override
            public int compare(Global.ModalClasses.TimeTableModal o1, Global.ModalClasses.TimeTableModal o2) {
                return o1.time - o2.time;
            }
        }


        Collections.sort(Global.documentData.timetable.get(String.valueOf(day)),new SortByTime());
    }

    static void SortAssignmentData(){
        class SortByTime implements Comparator<Global.ModalClasses.AssignmentModal>{

            @Override
            public int compare(Global.ModalClasses.AssignmentModal o1, Global.ModalClasses.AssignmentModal o2) {
                return (int) (o1.timestamp - o2.timestamp);
            }
        }

        Collections.sort(Global.documentData.assignment,new SortByTime());
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
