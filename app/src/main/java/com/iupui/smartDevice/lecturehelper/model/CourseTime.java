package com.iupui.smartDevice.lecturehelper.model;

/**
 * Created by nhdo on 10/24/2015.
 */
public class CourseTime {
    private int hour;
    private int minute;

    public CourseTime(int _hour, int _minute){
        hour = _hour;
        minute = _minute;
    }

    public boolean isEarlierThan(CourseTime other){
        if (hour == other.hour){
            return minute <= other.minute;
        }
        return hour < other.hour;
    }

    public void setCourseTime(int _hour, int _minute){
        hour = _hour;
        minute = _minute;
    }

}
