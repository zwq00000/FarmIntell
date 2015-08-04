package com.lingya.farmintell.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by zwq00000 on 2015/8/2.
 */
public class CalendarUtils {

  public static Calendar getStartCalendar(Date time) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(time);
    alignmentHour(calendar);
    return calendar;
  }

  public static Calendar getEndCalendar(Date time) {
    Calendar calendar = GregorianCalendar.getInstance();
    calendar.setTime(time);
    calendar.add(Calendar.HOUR, 1);
    alignmentHour(calendar);
    return calendar;
  }

  /**
   * 整小时对齐
   */
  static void alignmentHour(Calendar calendar) {
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);
  }
}
