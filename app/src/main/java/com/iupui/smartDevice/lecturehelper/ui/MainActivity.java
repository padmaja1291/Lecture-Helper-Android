package com.iupui.smartDevice.lecturehelper.ui;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iupui.smartDevice.lecturehelper.R;
import com.iupui.smartDevice.lecturehelper.db.DBHelper;
import com.iupui.smartDevice.lecturehelper.model.CourseList;
import com.iupui.smartDevice.lecturehelper.model.CourseTime;
import com.iupui.smartDevice.lecturehelper.utils.LectureHelperUtils;
import com.iupui.smartDevice.lecturehelper.wrapper.MyCalendar;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class to save the data which is available throught out application
 * @author nhdo
 * modified by Padmaja Krishnakumar - To include silent mode functionality
 * @version 1.0
 * @since 16 Nov 2015
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CourseTime currentTime;
    private TextView textView;
    private DBHelper mDBHelper;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_TAKE_VIDEO = 2;
    private LectureApplication mLectureApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeObjects();

        textView = (TextView) findViewById(R.id.main_tv_current_course);

        makePhoneInSilent(mDBHelper);
    }

    private void initializeObjects() {
        currentTime = new CourseTime(0, 0);
        mDBHelper = new DBHelper(this);
        mLectureApplication = (LectureApplication) getApplication();
    }

    private void makePhoneInSilent(DBHelper dbHelper) {
        ArrayList<CourseList> courseList = dbHelper.getAllCourses();

        if(courseList == null){
            return;
        }

        for(CourseList cl : courseList) {
            Log.i(TAG, cl.getId() +","+cl.getName() +","+ cl.getStartTime() +"," + cl.getEndTime() + "," + cl.getDayOfWeek());
        }

        MyCalendar calendar = MyCalendar.getInstance();
        String currentHour = calendar.getInString(Calendar.HOUR_OF_DAY);
        String currentMin = calendar.getInString(Calendar.MINUTE);
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        Log.i(TAG, currentHour +":"+ currentMin +","+ currentDay);

        String courseName;
        if(!"None".equalsIgnoreCase(courseName = dbHelper.isCurrentCourseExitsNow(currentHour +":"+ currentMin, currentDay))) {

            mLectureApplication.setCurrentCourseName(courseName);
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

            if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                Log.i(TAG, "RINGER_MODE_SILENT");
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else {
                Log.i(TAG, "Already RINGER_MODE_SILENT");
            }
        }

        Log.i(TAG, "courseName : " + courseName);

        textView.setText(courseName);

    }

    @Override
    protected void onResume(){
        // get current time and update the current course name
        super.onResume();
        MyCalendar calendar = MyCalendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        currentTime.setCourseTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_TAKE_PHOTO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "The photo is saved!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_TAKE_VIDEO) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "The video is saved!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addPhoto(View view) {
        final String PICTURE_FORMAT = ".jpg";
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = LectureHelperUtils.getEmptyFileWithStructuredPath(
                mLectureApplication.getCurrentCourseName(), PICTURE_FORMAT);

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void addVideo(View view) {
        final String VIDEO_FORMAT = ".mp4";
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            File videoFile = LectureHelperUtils.getEmptyFileWithStructuredPath
                    (mLectureApplication.getCurrentCourseName(), VIDEO_FORMAT);
            if (videoFile != null) {
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(videoFile));
                startActivityForResult(takeVideoIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void addNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    public void addAudio(View view) {
        Intent intent = new Intent(this, AudioRecorder.class);
        startActivity(intent);
    }

    public void goToScheduleActivity(View view){
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

}
