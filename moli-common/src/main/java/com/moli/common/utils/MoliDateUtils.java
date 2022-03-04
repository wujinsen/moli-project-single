package com.moli.common.utils;


public class MoliDateUtils {


    public static String startTimeToDateStart(String startTime) {
        return startTime + " 00:00:00";
    }

    public static String endTimeToDateEnd(String endTime) {
        return endTime + " 23:59:59";
    }

}
