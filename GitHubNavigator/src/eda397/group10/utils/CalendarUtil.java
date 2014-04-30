package eda397.group10.utils;

import java.util.GregorianCalendar;

public class CalendarUtil {
	public static GregorianCalendar convertToCalendar(String date) {
		GregorianCalendar calendar = new GregorianCalendar();
		
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));;
		int day = Integer.parseInt(date.substring(8, 10));;
		int hourOfDay = Integer.parseInt(date.substring(11, 13));;
		int minute = Integer.parseInt(date.substring(14, 16));;
		int second = Integer.parseInt(date.substring(17, 19));;
		
		calendar.set(year, month, day, hourOfDay, minute, second);
		
		return calendar;
	}
}
