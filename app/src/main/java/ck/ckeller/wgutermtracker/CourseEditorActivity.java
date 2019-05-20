package ck.ckeller.wgutermtracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private String action;

    private EditText editName;
    private EditText editStart;
    private EditText editEnd;
    private EditText editMentor;
    private EditText editMentorPhone;
    private EditText editMentorEmail;
    private EditText editDesc;
    private Spinner spinnerStatus;

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

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("New course");
        } else {
            action = Intent.ACTION_EDIT;
            setTitle("Edit Course");
            parseCourse();
            populateFields();
        }


    }

    private void populateFields() {
        editName.setText(currentCourse.getCourseName());
        editStart.setText(currentCourse.getCourseStart());
        editEnd.setText(currentCourse.getCourseEnd());
        editMentor.setText(currentCourse.getCourseMentor());
        editMentorPhone.setText(currentCourse.getCourseMentorPhone());
        editMentorEmail.setText(currentCourse.getCourseMentorEmail());
        editDesc.setText(currentCourse.getCourseDesc());
    }

    private void findViews() {
        editName = findViewById(R.id.edit_text_course_name);
        editStart = findViewById(R.id.edit_text_course_start);
        editEnd = findViewById(R.id.edit_text_course_end);
        editMentor = findViewById(R.id.edit_text_course_mentor);
        editMentorPhone = findViewById(R.id.edit_text_mentor_phone);
        editMentorEmail = findViewById(R.id.edit_text_mentor_email);
        editDesc = findViewById(R.id.edit_text_course_desc);

        String[] courseStatus = getResources().getStringArray(R.array.course_status_types);
        spinnerStatus = findViewById(R.id.spin_course_status);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseStatus);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(arrayAdapter);


    }

    public void parseCourse() {
        Intent intent = getIntent();
        currentCourseUri = intent.getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
        courseId = Integer.parseInt(currentCourseUri.getLastPathSegment());
        currentCourse = DataManager.getCourse(this, courseId);

    }

}
