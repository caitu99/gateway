package com.caitu99.gateway.utils;

import org.springframework.util.Assert;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    public static final String PATTERN_STANDARD = "yyyy-MM-dd HH:mm:ss";

    public static final String PATTERN_NUMBER_DAY = "yyyyMMdd";
    public static final String PATTERN_NUMBER_SECONDS = "HHmmss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    public static String timestamp2String(Timestamp timestamp, String pattern) {
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp null illegal");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = PATTERN_STANDARD;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(timestamp.getTime()));
    }

    public static String date2String(Date date, String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("timestamp null illegal");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = PATTERN_STANDARD;
            ;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static Timestamp currentTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    public static String currentTimestamp2String(String pattern) {
        return timestamp2String(currentTimestamp(), pattern);
    }
    
    public static String getTimeNumber() {
    	
        return timestamp2String(currentTimestamp(), PATTERN_NUMBER_DAY)
        		+timestamp2String(currentTimestamp(), PATTERN_NUMBER_SECONDS);
    }

    public static Timestamp string2Timestamp(String strDateTime, String pattern) {
        if (strDateTime == null || strDateTime.equals("")) {
            throw new IllegalArgumentException("Date Time Null Illegal");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = PATTERN_STANDARD;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(strDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new Timestamp(date.getTime());
    }

    public static Date string2Date(String strDate, String pattern) {
        if (strDate == null || strDate.equals("")) {
            throw new RuntimeException("str date null");
        }
        if (pattern == null || pattern.equals("")) {
            pattern = DateUtil.PATTERN_DATE;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;

        try {
            date = sdf.parse(strDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    public static String stringToYear(String strDest) {
        if (strDest == null || strDest.equals("")) {
            throw new IllegalArgumentException("str dest null");
        }

        Date date = string2Date(strDest, DateUtil.PATTERN_DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return String.valueOf(c.get(Calendar.YEAR));
    }

    public static String stringToMonth(String strDest) {
        if (strDest == null || strDest.equals("")) {
            throw new IllegalArgumentException("str dest null");
        }

        Date date = string2Date(strDest, DateUtil.PATTERN_DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // return String.valueOf(c.get(Calendar.MONTH));
        int month = c.get(Calendar.MONTH);
        month = month + 1;
        if (month < 10) {
            return "0" + month;
        }
        return String.valueOf(month);
    }

    public static String stringToDay(String strDest) {
        if (strDest == null || strDest.equals("")) {
            throw new IllegalArgumentException("str dest null");
        }

        Date date = string2Date(strDest, DateUtil.PATTERN_DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // return String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        int day = c.get(Calendar.DAY_OF_MONTH);
        if (day < 10) {
            return "0" + day;
        }
        return "" + day;
    }

    public static Date getFirstDayOfMonth(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = 1;
        c.set(year, month, day, 0, 0, 0);
        return c.getTime();
    }

    public static Date getLastDayOfMonth(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = 1;
        if (month > 11) {
            month = 0;
            year = year + 1;
        }
        c.set(year, month, day - 1, 0, 0, 0);
        return c.getTime();
    }

    public static String date2GregorianCalendarString(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date is null");
        }
        long tmp = date.getTime();
        GregorianCalendar ca = new GregorianCalendar();
        ca.setTimeInMillis(tmp);
        try {
            XMLGregorianCalendar t_XMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(ca);
            return t_XMLGregorianCalendar.normalize().toString();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException("Date is null");
        }

    }

    public static boolean compareDate(Date firstDate, Date secondDate) {
        if (firstDate == null || secondDate == null) {
            throw new RuntimeException();
        }

        String strFirstDate = date2String(firstDate, "yyyy-MM-dd");
        String strSecondDate = date2String(secondDate, "yyyy-MM-dd");
        if (strFirstDate.equals(strSecondDate)) {
            return true;
        }
        return false;
    }

    public static Date getStartTimeOfDate(Date currentDate) {
        Assert.notNull(currentDate);
        String strDateTime = date2String(currentDate, "yyyy-MM-dd") + " 00:00:00";
        return string2Date(strDateTime, "yyyy-MM-dd hh:mm:ss");
    }

    public static Date getEndTimeOfDate(Date currentDate) {
        Assert.notNull(currentDate);
        String strDateTime = date2String(currentDate, "yyyy-MM-dd") + " 59:59:59";
        return string2Date(strDateTime, "yyyy-MM-dd hh:mm:ss");
    }

    public static void main(String[] args) {
        String str1 = "2011-01-01";
        String str2 = "1988-09-09";
        Date date1 = DateUtil.string2Date(str1, "yyyy-MM-dd");
        Date date2 = DateUtil.string2Date(str2, "yyyy-MM-dd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(date1);
        c2.setTime(date2);
        c2.add(Calendar.YEAR, 4);
        if (c2.before(c1)) {
            System.out.println("illegal");
        } else {
            System.out.println("ok");
        }

    }
}