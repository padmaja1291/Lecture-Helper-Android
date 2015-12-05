package com.iupui.smartDevice.lecturehelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;
import com.iupui.smartDevice.lecturehelper.model.CourseList;
import com.iupui.smartDevice.lecturehelper.utils.LectureHelperUtils;
import java.util.ArrayList;

/**
 * Created by nhdo on 11/12/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
  private static final String TAG = "DBHelper";
  public static final String DATABASE_NAME = "CourseDatabase.db";
  public static final String COURSES_TABLE_NAME = "Courses";
  public static final String COURSES_COLUMN_ID = "id";
  public static final String COURSES_COLUMN_NAME = "name";
  public static final String COURSES_COLUMN_DAY_OF_WEEKS = "days_of_week";
  public static final String COURSES_COLUMN_START_TIME = "start_time";
  public static final String COURSES_COLUMN_END_TIME = "end_time";
  //private HashMap hp;

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, 1);
  }

  @Override public void onCreate(SQLiteDatabase db) {

    Log.e(TAG, "onCreate");
    db.execSQL("create table " + COURSES_TABLE_NAME + "( " +
        COURSES_COLUMN_ID + " integer primary key, " +
        COURSES_COLUMN_NAME + " text, " +
        COURSES_COLUMN_DAY_OF_WEEKS + " text," +
        COURSES_COLUMN_START_TIME + " text," +
        COURSES_COLUMN_END_TIME + " text" +
        ")");
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE_NAME);
    onCreate(db);
  }

  public void dropAllCourse() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DROP TABLE IF EXISTS " + COURSES_TABLE_NAME);
  }

  public boolean InsertCourse(String name, String days_of_week, String startTime, String endTime) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(COURSES_COLUMN_NAME, name);
    contentValues.put(COURSES_COLUMN_DAY_OF_WEEKS, days_of_week);
    contentValues.put(COURSES_COLUMN_START_TIME, startTime);
    contentValues.put(COURSES_COLUMN_END_TIME, endTime);
    try {
      db.insert(COURSES_TABLE_NAME, null, contentValues);
      return true;
    } catch (SQLiteException e) {
      Log.e(TAG, "InsertCourse()" + e.toString());
      return false;
    }
  }

  public Cursor getData(int id) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor res = db.rawQuery(
        "select * from " + COURSES_TABLE_NAME + " where " + COURSES_COLUMN_ID + "=" + id + "",
        null);
    return res;
  }

  public int numberOfRows() {
    SQLiteDatabase db = this.getReadableDatabase();
    int numRows = (int) DatabaseUtils.queryNumEntries(db, COURSES_TABLE_NAME);
    return numRows;
  }

  public boolean UpdateCourse(Integer id, String name, String days_of_week,String startTime, String endTime) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(COURSES_COLUMN_NAME, name);
    contentValues.put(COURSES_COLUMN_DAY_OF_WEEKS, days_of_week);
    contentValues.put(COURSES_COLUMN_START_TIME, startTime);
    contentValues.put(COURSES_COLUMN_END_TIME, endTime);
    db.update(COURSES_TABLE_NAME, contentValues, COURSES_COLUMN_ID + "= ? ",
        new String[] { Integer.toString(id) });

    return true;
  }

  public Integer DeleteCourse(Integer id) {
    SQLiteDatabase db = this.getWritableDatabase();
    return db.delete(COURSES_TABLE_NAME, COURSES_COLUMN_ID + "= ? ",
        new String[] { Integer.toString(id) });
  }

  public ArrayList<CourseList> getAllCourses() {
    ArrayList<CourseList> courseList = null;
    CourseList courseListObj;
    SQLiteDatabase db = this.getReadableDatabase();

    try {
      Cursor res = db.rawQuery("select * from " + COURSES_TABLE_NAME, null);
      res.moveToFirst();

      courseList = new ArrayList<>();

      while (!res.isAfterLast()) {
        String courseName = res.getString(res.getColumnIndex(COURSES_COLUMN_NAME));
        Integer courseId = res.getInt(res.getColumnIndex(COURSES_COLUMN_ID));
        Integer dyaOfWeek = res.getInt(res.getColumnIndex(COURSES_COLUMN_DAY_OF_WEEKS));
        String startTime = res.getString(res.getColumnIndex(COURSES_COLUMN_START_TIME));
        String endTime = res.getString(res.getColumnIndex(COURSES_COLUMN_END_TIME));

        courseListObj = new CourseList();

        courseListObj.setName(courseName);
        courseListObj.setId(courseId);
        courseListObj.setDayOfWeek(dyaOfWeek);
        courseListObj.setStartTime(startTime);
        courseListObj.setEndTime(endTime);

        courseList.add(courseListObj);

        res.moveToNext();
      }
    } catch (SQLiteException e) {
      Log.d(TAG, "getAllCourses()" + e.toString());
    }
    return courseList;
  }

  public String isCurrentCourseExitsNow(String currentTime, int currentDayOfWeek) {

    int indexOfDay = LectureHelperUtils.getDayOfWeek(currentDayOfWeek);

    SQLiteDatabase db = this.getReadableDatabase();
    StringBuffer sb = new StringBuffer();
    sb.append("select * from ");
    sb.append(COURSES_TABLE_NAME);
    sb.append(" where ");
    sb.append("'" + currentTime + "' >= " + COURSES_COLUMN_START_TIME);
    sb.append(" AND ");
    sb.append("'" + currentTime + "' <= " + COURSES_COLUMN_END_TIME);

    Log.d(TAG, sb.toString());

    try {
      Cursor rs = db.rawQuery(sb.toString(), null);
      rs.moveToFirst();
      while (!rs.isAfterLast()) {

        String dayOfWeeks = rs.getString(rs.getColumnIndex(COURSES_COLUMN_DAY_OF_WEEKS));
        if(dayOfWeeks.charAt(indexOfDay) == '1') {
          return rs.getString(rs.getColumnIndex(COURSES_COLUMN_NAME));
        }

        rs.moveToNext();
      }
    } catch (SQLiteException e) {
      Log.d("DBHelper", "isCurrentCourseExitsNow()" + e.toString());
    }
    return "None";
  }

  //public String FindCourseNameAtCurrentTime(int day_of_week, CourseTime current_time) {
  //  SQLiteDatabase db = this.getReadableDatabase();
  //  Cursor rs = db.rawQuery("select * from " + COURSES_TABLE_NAME, null);
  //  rs.moveToFirst();
  //  day_of_week -= 1; // to align SUNDAY to 0 (instead of 1)
  //  while (!rs.isAfterLast()) {
  //    String days_of_week = rs.getString(rs.getColumnIndex(COURSES_COLUMN_DAY_OF_WEEKS));
  //    if (days_of_week.charAt(day_of_week) == '1') {
  //
  //      String startTime[] =
  //          rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_START_TIME)).split(":");
  //      String endTime[] =
  //          rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_END_TIME)).split(":");
  //
  //      int start_hour = Integer.parseInt(startTime[0]);
  //      int start_minute = Integer.parseInt(startTime[1]);
  //      int end_hour = Integer.parseInt(endTime[0]);
  //      int end_minute = Integer.parseInt(endTime[1]);
  //
  //      CourseTime start_time = new CourseTime(start_hour, start_minute);
  //      CourseTime end_time = new CourseTime(end_hour, end_minute);
  //      if (start_time.isEarlierThan(current_time) && current_time.isEarlierThan(end_time)) {
  //        String current_course_name =
  //            rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_NAME));
  //        return current_course_name;
  //      }
  //    }
  //    rs.moveToNext();
  //  }
  //  return "none";
  //}

}
