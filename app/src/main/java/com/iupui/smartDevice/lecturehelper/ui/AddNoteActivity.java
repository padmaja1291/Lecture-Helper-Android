package com.iupui.smartDevice.lecturehelper.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iupui.smartDevice.lecturehelper.R;
import com.iupui.smartDevice.lecturehelper.utils.LectureHelperUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TEXT_FILE_FORMAT = ".txt";
    EditText note;
    private LectureApplication mLectureApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        initializeObjects();

        note = (EditText) findViewById(R.id.addNote_text);
    }

    private void initializeObjects() {
        mLectureApplication = (LectureApplication) getApplication();
    }





    public void saveNote(View view){
        File output_file = LectureHelperUtils.getEmptyFileWithStructuredPath(
            mLectureApplication.getCurrentCourseName(), TEXT_FILE_FORMAT);

        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(output_file, true));
            buf.append(note.getText().toString());
            buf.newLine();
            buf.close();
            note.setText("");
            Toast.makeText(this, "Notes is saved!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
        }





    }
}
