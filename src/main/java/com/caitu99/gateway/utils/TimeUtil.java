/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.gateway.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TimeUtil 
 * @author ws
 * @date 2015年11月30日 下午8:14:56 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TimeUtil {
	  /**
     * 屏蔽实例化
     */
    private TimeUtil() {

    }

    /**
     * 时分秒格式--HHmmss
     */
    private static final String DEFAULT_TIME_FORMAT = "HHmmss";
    /**
     * 年月日格式--yyyyMMdd
     */
    private static final String DEFAUT_DATE_FORMAT = "yyyyMMdd";



    /**
     * 将时间转换成"yyyyMMdd"格式的数字
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getIntegerDate 
     * @param date
     * @return
     * @date 2015年11月30日 下午8:20:54  
     * @author ws
     */
    public static long getIntegerDate(Date date) {
        SimpleDateFormat dateSdf = new SimpleDateFormat(DEFAUT_DATE_FORMAT);
        return new Long(dateSdf.format(date));
    }

    /**
     * 将系统当前时间转换成"yyyyMMdd"格式的整型数字
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getIntegerDate 
     * @return
     * @date 2015年11月30日 下午8:20:48  
     * @author ws
     */
    public static long getIntegerDate() {
        Date date = new Date();
        SimpleDateFormat dateSdf = new SimpleDateFormat(DEFAUT_DATE_FORMAT);
        return new Long(dateSdf.format(date));
    }

    /**
     * 将时间转换成"HHmmss"格式的数字
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getIntegerTime 
     * @param date
     * @return
     * @date 2015年11月30日 下午8:20:27  
     * @author ws
     */
    public static Long getIntegerTime(Date date) {
        SimpleDateFormat timeSdf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        return new Long(timeSdf.format(date));
    }

    /**
     * 将系统当前时间转换成"HHmmss"格式的整型数字
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getIntegerTime 
     * @return
     * @date 2015年11月30日 下午8:20:10  
     * @author ws
     */
    public static Long getIntegerTime() {
        Date date = new Date();
        SimpleDateFormat timeSdf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        return new Long(timeSdf.format(date));
    }

    /**
     * 将整型日期相减 计算相差的自然日天数
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: longDateSubtract 
     * @param dateLong1
     * @param dateLong2
     * @return
     * @throws ParseException
     * @date 2015年11月30日 下午8:19:55  
     * @author ws
     */
    public static long longDateSubtract(long dateLong1, long dateLong2) throws ParseException{
        SimpleDateFormat dateSdf = new SimpleDateFormat(DEFAUT_DATE_FORMAT);
        long days = 0;

        Date date1 = dateSdf.parse("" + dateLong1);
        Date date2 = dateSdf.parse("" + dateLong2);
        days = date1.getTime() - date2.getTime();
        days = days / 1000 / 60 / 60 / 24;
 
        return days;
    }

    /**
     * 获取两个日期间的所有日期
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getDate 
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     * @date 2015年11月30日 下午8:18:03  
     * @author ws
     */
    public static List<Long> getDates(long startDate, long endDate) throws ParseException {
        List<Long> dateList = new ArrayList<Long>();
        SimpleDateFormat dateSdf = new SimpleDateFormat(DEFAUT_DATE_FORMAT);
        Long date1 = null;
        Long date2 = null;
        long increment = 3600 * 24 * 1000;

        date1 = dateSdf.parse("" + startDate).getTime();
        date2 = dateSdf.parse("" + endDate).getTime();
        for (long date = date1; date <= date2; date += increment) {
            dateList.add(Long.parseLong(dateSdf.format(date)));
        }

        return dateList;
    }

    /**
     * 查询下N个自然日，n=-1表示前一个自然日，n=1表示后一个自然日，以此类推
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getNaturalDay 
     * @param date
     * @param n
     * @return
     * @throws ParseException
     * @date 2015年11月30日 下午8:18:20  
     * @author ws
     */
    public static Long getNaturalDay(Long date, long n) throws ParseException {
        SimpleDateFormat dateSdf = new SimpleDateFormat(DEFAUT_DATE_FORMAT);
        long increment = 3600 * 24 * 1000;
        Long date1 = null;
        try {
            date1 = dateSdf.parse("" + date).getTime(); // ms
        } catch (ParseException e) {
            throw e;
        }
        return Long.parseLong(dateSdf.format(date1 + increment * n));
    }

    /**
     * 获取参数时间的日历对象
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getCalendarFromTimeStr 
     * @param timeParam
     * @return
     * @throws ParseException
     * @date 2015年11月30日 下午8:18:29  
     * @author ws
     */
    public static Calendar getCalendarFromTimeStr(String timeParam)
            throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeSdf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        Date date = timeSdf.parse(timeParam);
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 根据参数的时间获取当天包含此时间的日历对象
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: getDateFromTimeStr 
     * @param timeParam
     * @return
     * @throws ParseException
     * @date 2015年11月30日 下午8:18:42  
     * @author ws
     */
    public static Calendar getDateFromTimeStr(String timeParam)
            throws ParseException {
        SimpleDateFormat timeSdf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        Date date = timeSdf.parse(timeParam);
        Calendar temp = Calendar.getInstance();
        temp.setTime(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, temp.get(Calendar.SECOND));
        return calendar;
    }

    /**
     * parse yyyyMMdd format string to Date object
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: parseYYYYMMDD2Date 
     * @param yyyyMMddFmt
     * @return
     * @date 2015年11月30日 下午8:18:51  
     * @author ws
     */
    public static Date parseYYYYMMDD2Date(Object yyyyMMddFmt) {
        if (yyyyMMddFmt == null || yyyyMMddFmt.toString().length() != 8) {
            return null;
        }
        try {
            return new SimpleDateFormat(DEFAUT_DATE_FORMAT).parse(yyyyMMddFmt
                    .toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * yyyyMMdd ： must to be correct date format， and this method will add
     * <code>days</code> days to the <code>srcDate</code> and return
     * <code>Long</code> date as the pattern of 'yyyyMMdd'
     * 	
     * @Description: (方法职责详细描述,可空)  
     * @Title: addDays 
     * @param srcDate
     * @param days
     * @return
     * @date 2015年11月30日 下午8:19:07  
     * @author ws
     */
    public static Long addDays(Long srcDate, Object days) {
        int IntDay = Integer.parseInt(days.toString());
        return Long
                .valueOf(DateFormatUtils.format(DateUtils.addDays(
                        TimeUtil.parseYYYYMMDD2Date(srcDate), IntDay),
                        DEFAUT_DATE_FORMAT));
    }

    
    public static Date getYearLast(){
    	Calendar currCal=Calendar.getInstance();  
        int currentYear = currCal.get(Calendar.YEAR);
        
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, currentYear);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
         
        return currYearLast;
    }
    

}
