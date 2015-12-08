package com.iupui.smartDevice.lecturehelper.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.iupui.smartDevice.lecturehelper.R;
import com.iupui.smartDevice.lecturehelper.db.DBHelper;
import com.iupui.smartDevice.lecturehelper.utils.LectureHelperUtils;

public class DisplayCourse extends AppCompatActivity {
    private static final String TAG = "DisplayCourse";
    int from_id = 0;
    private DBHelper mydb;

    TextView course_name;
    TimePicker time_picker_start;
    TimePicker time_picker_end;
    ListView courseListView;
    int id_to_update = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_course);
        course_name = (TextView) findViewById(R.id.DisplayCourse_editText_course_name);
        time_picker_start = (TimePicker) findViewById(R.id.DisplayCourse_timePickerStart);
        time_picker_end = (TimePicker) findViewById(R.id.DisplayCourse_timePickerEnd);
        final String[] arr = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        courseListView = (ListView) findViewById(R.id.courseListView);
        courseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        courseListView.setAdapter(
            new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, arr));

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            int value = extras.getInt("id");
            if (value > 0){
                // means that this is the View part, and not the add Course part
                Cursor rs = mydb.getData(value);
                id_to_update = value;
                rs.moveToFirst();

                String name_ = rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_NAME));
                int start_hour = Integer.parseInt(rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_START_TIME)).split(":")[0]);
                int start_minute = Integer.parseInt(rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_START_TIME)).split(":")[1]);
                int end_hour = Integer.parseInt(rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_END_TIME)).split(":")[0]);
                int end_minute = Integer.parseInt(rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_END_TIME)).split(
                    ":")[1]);
                String days_of_week = rs.getString(rs.getColumnIndex(DBHelper.COURSES_COLUMN_DAY_OF_WEEKS));
                if (!rs.isClosed()){
                    rs.close();
                }

                Button add_button = (Button) findViewById(R.id.DisplayCourse_btn_add_course);
                add_button.setVisibility(View.INVISIBLE);

                course_name.setText((CharSequence) name_);
                course_name.setFocusable(false);
                course_name.setClickable(false);

                time_picker_start.setCurrentHour(start_hour);
                time_picker_start.setCurrentMinute(start_minute);
                time_picker_end.setCurrentHour(end_hour);
                time_picker_end.setCurrentMinute(end_minute);
                time_picker_start.setEnabled(false);
                time_picker_end.setEnabled(false);
                time_picker_start.setFocusable(false);
                time_picker_start.setClickable(false);
                time_picker_end.setFocusable(false);
                time_picker_end.setClickable(false);

                // missing update days of week from the database
                for (int i =0; i<days_of_week.length(); i++){
                    char c = days_of_week.charAt(i);
                    if (c == '0'){
                        courseListView.setItemChecked(i, false);
                    }
                    else{
                        courseListView.setItemChecked(i, true);
                    }
                }
                courseListView.setFocusable(false);
                courseListView.setEnabled(false);




            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.DisplayCourse_menu_edit_course:
                Button add_button = (Button)findViewById(R.id.DisplayCourse_btn_add_course);
                add_button.setVisibility(View.VISIBLE);
                course_name.setEnabled(true);
                course_name.setFocusableInTouchMode(true);
                course_name.setClickable(true);

                time_picker_start.setEnabled(true);
                time_picker_start.setFocusableInTouchMode(true);
                time_picker_start.setClickable(true);

                time_picker_end.setEnabled(true);
                time_picker_end.setFocusableInTouchMode(true);
                time_picker_end.setClickable(true);

                courseListView.setEnabled(true);

                return true;

            case R.id.DisplayCourse_menu_delete_course:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure to delete this?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mydb.DeleteCourse(id_to_update);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), ScheduleActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // user cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void run(View view){
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            int value = extras.getInt("id");
            int start_hour = time_picker_start.getCurrentHour();
            int start_minute = time_picker_start.getCurrentMinute();
            int end_hour = time_picker_end.getCurrentHour();
            int end_minute = time_picker_end.getCurrentMinute();

            String days_of_week = "";

            int len = courseListView.getCount();
            SparseBooleanArray checked = courseListView.getCheckedItemPositions();
            for (int e = 0; e < len; e++) {
                if (checked.get(e)) {
                    days_of_week += "1";
                }
                else{
                    days_of_week += "0";
                }
            }

            // validation
            String validation_status = "ok";
            if ((end_hour == start_hour && start_minute > end_minute)
                    || (end_hour < start_hour)){
                validation_status = "Start time must be earlier than End time!";
            }
            else if (course_name.getText().toString().isEmpty()){
                validation_status = "Please enter course name!";
            }
            else if (days_of_week.equals("0000000")){
                validation_status = "Please select a day of week!";
            }

            if (!validation_status.equals("ok")){
                Toast.makeText(getApplicationContext(), validation_status, Toast.LENGTH_SHORT).show();
                return;
            }

            if (value > 0){

                if (mydb.UpdateCourse(id_to_update,
                        course_name.getText().toString(),
                        days_of_week, LectureHelperUtils.getInString(start_hour)+":"+LectureHelperUtils.getInString(start_minute),
                          LectureHelperUtils.getInString(end_hour)+":"+LectureHelperUtils.getInString(end_minute))){
                    Toast.makeText(getApplicationContext(), "Course is updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),ScheduleActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                if (mydb.InsertCourse(
                        course_name.getText().toString(),
                        days_of_week, LectureHelperUtils.getInString(start_hour)+":"+LectureHelperUtils.getInString(start_minute), LectureHelperUtils.getInString(end_hour)+":"+LectureHelperUtils.getInString(end_minute))){
                    Toast.makeText(getApplicationContext(), "Course is added", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),ScheduleActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "not Inserted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
