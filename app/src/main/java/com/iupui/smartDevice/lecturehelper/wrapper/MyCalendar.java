package com.iupui.smartDevice.lecturehelper.wrapper;

import java.util.Calendar;
/**
 * Wrapper class for Calendar
 * @author Padmaja Krishna Kumar
 * @version 1.0
 * @since 28 Nov 2015
 */
public class MyCalendar {

  private Calendar mCalendar;

  public static MyCalendar getInstance() {
    Calendar calendar = Calendar.getInstance();
    return new MyCalendar(calendar);
  }

  private MyCalendar(Calendar calendar) {
    mCalendar = calendar;
  }

  public String getInString(int field) {
    int fieldValue = mCalendar.get(field);
    if (fieldValue < 10) {
      return "0" + fieldValue;
    } else {
      return String.valueOf(fieldValue);
    }
  }

  public int get(int field) {
    return mCalendar.get(field);
  }
}
