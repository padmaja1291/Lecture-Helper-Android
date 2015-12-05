package com.iupui.smartDevice.lecturehelper.ui;

import android.app.Application;

/**
 * Class to save the data which is available throught out application
 * @author Padmaja Krishna Kumar
 * @version 1.0
 * @since 28 Nov 2015
 */
public class LectureApplication extends Application {

  private static String currentCourseName;

  @Override public void onCreate() {
    super.onCreate();
  }

  public String getCurrentCourseName() {
    return currentCourseName;
  }

  public void setCurrentCourseName(String currentCourseName) {
    this.currentCourseName = currentCourseName;
  }

}
