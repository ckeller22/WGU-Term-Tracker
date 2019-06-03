package ck.ckeller.wgutermtracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CourseEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri currentCourseUri;
    private Course currentCourse;
    private int courseId;
    private Long courseTermId;
    private String action;

    private EditText editName;
    private EditText editStart;
    private EditText editEnd;
    private EditText editMentor;
    private EditText editMentorPhone;
    private EditText editMentorEmail;
    private EditText editDesc;
    private Spinner spinnerStatus;

    private DatePickerDialog startDialog;
    private DatePickerDialog endDialog;

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
        initDatePickers();

        Intent intent = getIntent();
        courseTermId = intent.getLongExtra(DataProvider.TERM_CONTENT_TYPE, 0);
        Log.d("test", "value:" + courseTermId);
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
        switch (currentCourse.getCourseStatus()) {
            case "Planned":
                spinnerStatus.setSelection(0);
                break;
            case "In Progress":
                spinnerStatus.setSelection(1);
                break;
            case "Completed":
                spinnerStatus.setSelection(2);
                break;
            case "Dropped":
                spinnerStatus.setSelection(3);
                break;
        }
    }

    private void findViews() {
        editName = findViewById(R.id.edit_text_course_name);
        editStart = findViewById(R.id.edit_text_course_start);
        editStart.setInputType(InputType.TYPE_NULL);
        editEnd = findViewById(R.id.edit_text_course_end);
        editEnd.setInputType(InputType.TYPE_NULL);
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

    public void initDatePickers() {
        editStart.setOnClickListener(this);
        editEnd.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        startDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                startDate = newDate;
                editStart.setText(sdf.format(newDate.getTime()));

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        endDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                endDate = newDate;
                editEnd.setText(sdf.format(newDate.getTime()));

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    }

    public void saveCourseChanges(View view) {
        Log.d("buttonTest", "buttonClicked");
        if (action.equals(Intent.ACTION_INSERT)) {
            currentCourse = new Course();
            getCourseFromFields();
            DataManager.insertCourse(this, currentCourse.getCourseName(), currentCourse.getCourseStart(), currentCourse.getCourseEnd(), currentCourse.getCourseMentor(),
                    currentCourse.getCourseMentorPhone(), currentCourse.getCourseMentorEmail(), currentCourse.getCourseStatus(), currentCourse.getCourseDesc(), courseTermId);
        } else if (action.equals(Intent.ACTION_EDIT)) {
            getCourseFromFields();
        }
    }

    private void getCourseFromFields() {
        currentCourse.setCourseName(editName.getText().toString().trim());
        currentCourse.setCourseStart(editStart.getText().toString().trim());
        currentCourse.setCourseEnd(editEnd.getText().toString().trim());
        currentCourse.setCourseMentor(editMentor.getText().toString().trim());
        currentCourse.setCourseMentorPhone(editMentorPhone.getText().toString().trim());
        currentCourse.setCourseMentorEmail(editMentorEmail.getText().toString().trim());
        currentCourse.setCourseDesc(editDesc.getText().toString().trim());
        currentCourse.setCourseStatus(spinnerStatus.getSelectedItem().toString());
    }

    @Override
    public void onClick(View v) {
        if (v == editStart) {
            startDialog.show();
        }
        if (v == editEnd) {
            endDialog.show();
        }
    }
}
