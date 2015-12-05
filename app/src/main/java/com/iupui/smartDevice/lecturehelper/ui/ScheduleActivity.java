package com.iupui.smartDevice.lecturehelper.ui;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.iupui.smartDevice.lecturehelper.R;
import com.iupui.smartDevice.lecturehelper.db.DBHelper;
import com.iupui.smartDevice.lecturehelper.model.CourseList;
import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    // for database
    private ArrayList<CourseList> mCourseList;
    private DBHelper mydb;
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        initializeObjects();

        ListView lv = ((ListView) findViewById(R.id.listView1));
        lv.setAdapter(new ScheduleAdapter(mCourseList));
        lv.setOnItemClickListener(this);
    }

    private void initializeObjects() {
        mInflater = LayoutInflater.from(this);
        mydb = new DBHelper(this);
        mCourseList = mydb.getAllCourses();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle dataBundle = new Bundle();
        dataBundle.putInt("id", mCourseList.get(position).getId());

        Intent intent = new Intent(getApplicationContext(), DisplayCourse.class);
        intent.putExtras(dataBundle);
        startActivity(intent);
    }

    private class ScheduleAdapter extends BaseAdapter {

        private ArrayList<CourseList> courseList;
        public ScheduleAdapter(ArrayList<CourseList> courseList){
            this.courseList = courseList;
        }

        @Override public int getCount() {
            return courseList != null ? courseList.size() : 0;
        }

        @Override public Object getItem(int position) {
            return null;
        }

        @Override public long getItemId(int position) {
            return 0;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent) {

            convertView  = mInflater.inflate(R.layout.cell_schedule, null);
            ((TextView)convertView.findViewById(R.id.schedule_tv_label)).setText(courseList.get(position).getName());

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.action_add_course:
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(), DisplayCourse.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }


}