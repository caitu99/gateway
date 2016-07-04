package com.caitu99.gateway.frequency;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenyun on 15/7/30.
 */
public class TimeUtil {

    public static long getTimeDistance(String baseTime, FrequencyType type) {
        long nowTime = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long result = 0;
        try {
            Date d1 = df.parse(baseTime);
            long diff = nowTime - d1.getTime();
            switch (type) {
                case second:
                    result = diff / 1000;
                    break;
                case min:
                    result = diff / (60 * 1000);
                    break;
                case hour:
                    result = diff / (60 * 60 * 1000);
                    break;
                case day:
                    result = diff / (60 * 60 * 24 * 1000);
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static long getTimeDistance(FrequencyType type) {
        long nowTime = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long result = 0;
        try {
            Date d1 = df.parse("1970-01-01 00:00:00");
            long diff = nowTime - d1.getTime();
            switch (type) {
                case second:
                    result = diff / 1000;
                    break;
                case min:
                    result = diff / (60 * 1000);
                    break;
                case hour:
                    result = diff / (60 * 60 * 1000);
                    break;
                case day:
                    result = diff / (60 * 60 * 24 * 1000);
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static long getTimeDistanceExpired(FrequencyType type) {
        long nowTime = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long result = 0;
        try {
            Date d1 = df.parse("1970-01-01 00:00:00");
            long diff = nowTime - d1.getTime();
            switch (type) {
                case second:
                    result = diff / 1000 - 1;
                    break;
                case min:
                    result = diff / (60 * 1000) - 1;
                    break;
                case hour:
                    result = diff / (60 * 60 * 1000) - 1;
                    break;
                case day:
                    result = diff / (60 * 60 * 24 * 1000) - 1;
                    break;
                default:
                    break;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(getTimeDistance(FrequencyType.min));
    }

}
