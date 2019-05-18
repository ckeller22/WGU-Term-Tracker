package ck.ckeller.wgutermtracker;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CourseEditorActivity extends AppCompatActivity {

    private Uri currentCourseUri;
    private Course currentCourse;
    private int courseId;

    private String message;
    private String myFormat = "MM/dd/yyyy";
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    private Calendar startDate;
    private Calendar endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();

    }

    private void findViews() {
        EditText tvName = findViewById(R.id.edit_text_course_name);
        EditText tvStart = findViewById(R.id.edit_text_course_start);
        EditText tvEnd = findViewById(R.id.edit_text_course_end);
        EditText tvMentor = findViewById(R.id.edit_text_course_mentor);
        EditText tvMentorPhone = findViewById(R.id.edit_text_mentor_phone);
        EditText tvMentorEmail = findViewById(R.id.edit_text_mentor_email);
        EditText tvDesc = findViewById(R.id.edit_text_course_desc);

        String[] courseStatus = getResources().getStringArray(R.array.course_status_types);
        Spinner spinnerStatus = findViewById(R.id.spin_course_status);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseStatus);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(arrayAdapter);


    }

}
