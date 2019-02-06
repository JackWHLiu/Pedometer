package com.lwh.pedometer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具 
 * @author lwh
 */
public class DateConverter {
	
	/**
	 * yyyy
	 */
	public final static String FORMAT_YEAR = "yyyy";
	/**
	 * MM
	 */
	public final static String FORMAT_MONTH = "MM";
	/**
	 * dd
	 */
	public final static String FORMAT_DAY = "dd";
	/**
	 * MM.dd
	 */
	public final static String FORMAT_MONTH_DAY = "MM.dd";
	/**
	 * MM月dd日
	 */
	public final static String FORMAT_MONTH_DAY_CN = "MM月dd日";
	/**
	 * yyyy-MM-dd
	 */
	public final static String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";
	/**
	 * yyyy年MM月dd日
	 */
	public final static String FORMAT_YEAR_MONTH_DAY_CN = "yyyy年MM月dd日"; 
	/**
	 * HH:mm
	 */
	public final static String FORMAT_HOUR_MINUTE = "HH:mm";
	/**
	 * HH时mm分
	 */
	public final static String FORMAT_HOUR_MINUTE_CN = "HH时mm分";
	/**
	 * HH:mm:ss
	 */
	public final static String FORMAT_HOUR_MINUTE_SECOND = "HH:mm:ss";
	/**
	 * HH:mm:ss.SSS
	 */
	public final static String FORMAT_HOUR_MINUTE_SECOND_MILLIS = "HH:mm:ss.SSS";
	/**
	 * MM.dd hh:mm
	 */
	public final static String FORMAT_MONTH_DAY_HOUR_MINUTE = "MM.dd hh:mm";
	/**
	 * MM月dd日hh时mm分
	 */
	public final static String FORMAT_MONTH_DAY_HOUR_MINUTE_CN = "MM月dd日hh时mm分";
	/**
	 * yyyy-MM-dd HH:mm
	 */
	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
	/**
	 * 闰年所包含的秒数
	 */
	public static final int LEAP_YEAR = 366 * 24 * 60 * 60;
	/**
	 * 平年所包含的秒数
	 */
	public static final int NONLEAP_YEAR = 365 * 24 * 60 * 60;
	/**
	 * 月所包含的秒数
	 */
	public static final int MONTH = 30 * 24 * 60 * 60;
	/**
	 * 日所包含的秒数
	 */
	public static final int DAY = 24 * 60 * 60;
	/**
	 * 时所包含的秒数
	 */
	public static final int HOUR = 60 * 60;
	/**
	 * 分所包含的秒数
	 */
	public static final int MINUTE = 60;
	
	/**
	 * 得到描述性时间
	 * @param timestamp 时间戳
	 * @return
	 */
	public static String getDescTime(long timestamp) {
		long currentTime = System.currentTimeMillis();
		//时间间隔，与现在时间相差秒数
		long timeGap = (currentTime - timestamp) / 1000; 
		String timeStr = null;
		//我们作为一般的平年处理
		if (timeGap > LEAP_YEAR) {
			timeStr = timeGap / LEAP_YEAR + "年前";
		// 1个月以上
		} else if (timeGap > MONTH) {
			timeStr = timeGap / MONTH + "个月前";
		// 1天以上
		} else if (timeGap > DAY) {
			timeStr = timeGap / DAY + "天前";
		// 1小时-24小时
		} else if (timeGap > HOUR) {
			timeStr = timeGap / HOUR + "小时前";
		// 1分钟-59分钟	
		} else if (timeGap > MINUTE) {
			timeStr = timeGap / MINUTE + "分钟前";
		// 1秒钟-59秒钟
		} else {
			timeStr = "刚刚";
		}
		return timeStr;
	}

	public static String date2str(Date data, String formatType) {
 		return new SimpleDateFormat(formatType,Locale.CHINA).format(data);
 	}
 
	public static String long2str(long currentTime,String formatType){
 		String strTime="";
		Date date = long2date(currentTime, formatType);
		strTime = date2str(date, formatType); 
 		return strTime;
 	}
	
 	public static Date str2date(String strTime, String formatType){
 		Date date = null;
 		try {
			date = new SimpleDateFormat(formatType, Locale.CHINA).parse(strTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
 		return date;
 	}
 
 	public static Date long2date(long currentTime, String formatType){
 		Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
 		String sDateTime = date2str(dateOld, formatType); // 把date类型的时间转换为string
 		Date date = str2date(sDateTime, formatType); // 把String类型转换为Date类型
 		return date;
 	}
 
 	public static long str2long(String strTime, String formatType){
 		Date date = str2date(strTime, formatType); 
 		if (date == null) {
 			return 0;
 		} else {
 			long currentTime = date2long(date); 
 			return currentTime;
 		}
 	}
 
 	public static long date2long(Date date) {
 		return date.getTime();
 	}
 	
	/**、
	 * 判断闰年
	 * @param year 年份
	 * @return
	 */
	public static boolean isLeapYear(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 本月的天数
	 * @param year 年份
	 * @param month 月份
	 * @return
	 */
	public static int getDaysOfMonth(int year,int month) {
		boolean isLeapYear = isLeapYear(year);
		int result = 0;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			result = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			result = 30;
			break;
		default:
			if (isLeapYear) {
				result = 29;
			}else{
				result = 28;
			}
			break;
		}
		return result;
	}
}
