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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CourseEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri currentCourseUri;
    private Course currentCourse;
    private int courseId;
    private int courseTermId;

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
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();

    private int COURSE_LIST_ACTIVITY_CODE = 1;

    //todo Clean up log messages, clean up UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();
        initDatePickers();

        Intent intent = getIntent();
        courseTermId = intent.getIntExtra(DBOpenHelper.TERM_ID, 0);

        if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            setTitle("New course");
        } else if (intent.getAction().equals(Intent.ACTION_EDIT)){
            setTitle("Edit Course");
            parseCourse();
            populateFields();
        }
        Log.d("string", "value: " + courseId);
        Log.d("string", "value: " + courseTermId);

    }

    public void populateFields() {
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

    public void findViews() {
        editName = findViewById(R.id.edit_text_course_name);
        editStart = findViewById(R.id.edit_text_course_start_date);
        editStart.setInputType(InputType.TYPE_NULL);
        editEnd = findViewById(R.id.edit_text_course_end_date);
        editEnd.setInputType(InputType.TYPE_NULL);
        editMentor = findViewById(R.id.edit_text_course_mentor);
        editMentorPhone = findViewById(R.id.edit_text_course_mentor_phone);
        editMentorEmail = findViewById(R.id.edit_text_course_mentor_email);
        editDesc = findViewById(R.id.edit_text_course_desc);

        String[] courseStatus = getResources().getStringArray(R.array.course_status_types);
        spinnerStatus = findViewById(R.id.spin_course_status);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, courseStatus);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(arrayAdapter);


    }

    public void parseCourse() {
        Intent intent = getIntent();
        //long longCourseId = intent.getLongExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
        courseTermId = intent.getIntExtra(DataProvider.TERM_CONTENT_TYPE, 0);
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

    public void saveCourseChanges(View view) throws ParseException {
        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            if (validateFields() == true) {
                currentCourse = new Course();
                getCourseFromFields();
                DataManager.insertCourse(this, currentCourse.getCourseName(), currentCourse.getCourseStart(), currentCourse.getCourseEnd(), currentCourse.getCourseMentor(),
                        currentCourse.getCourseMentorPhone(), currentCourse.getCourseMentorEmail(), currentCourse.getCourseStatus(), currentCourse.getCourseDesc(), courseTermId);
                Toast.makeText(this, "New course added.", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        } else if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            if (validateFields() == true) {
                getCourseFromFields();
                DataManager.updateCourse(this, currentCourse.getCourseName(), currentCourse.getCourseStart(), currentCourse.getCourseEnd(), currentCourse.getCourseMentor(),
                        currentCourse.getCourseMentorPhone(), currentCourse.getCourseMentorEmail(), currentCourse.getCourseStatus(), currentCourse.getCourseDesc(), courseTermId, courseId);
                Toast.makeText(this, "Course updated.", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getCourseFromFields() {
        currentCourse.setCourseName(editName.getText().toString().trim());
        currentCourse.setCourseStart(editStart.getText().toString().trim());
        currentCourse.setCourseEnd(editEnd.getText().toString().trim());
        currentCourse.setCourseMentor(editMentor.getText().toString().trim());
        currentCourse.setCourseMentorPhone(editMentorPhone.getText().toString().trim());
        currentCourse.setCourseMentorEmail(editMentorEmail.getText().toString().trim());
        currentCourse.setCourseDesc(editDesc.getText().toString().trim());
        currentCourse.setCourseStatus(spinnerStatus.getSelectedItem().toString());
    }

    public boolean validateFields() throws ParseException {
        message = "";
        boolean isValid;
        if (editName.getText().length() == 0) {
            message += "Please enter a course name. \n";
        }
        if (editStart.getText().length() == 0) {
            message += "Please choose a valid start date. \n";
        }
        if (editEnd.getText().length() == 0) {
            message += "Please choose a valid end date. \n";
        }
        if (editStart.getText().length() > 0 && editEnd.getText().length() > 0) {
            Date sDate = sdf.parse(editStart.getText().toString());
            startDate.setTime(sDate);
            Date eDate = sdf.parse(editEnd.getText().toString());
            endDate.setTime(eDate);
            if (endDate.before(startDate)) {
                message += "Planned end date must not be before the start date.\n";
            }
        }
        if (editMentor.getText().length() == 0) {
            message += "Please enter a mentor name. \n";
        }
        if (editMentorPhone.getText().length() == 0) {
            message += "Please enter a mentor phone number. \n";
        }
        if (editMentorEmail.getText().length() == 0) {
            message += "Please enter a mentor email address. \n";
        }
        if (editDesc.getText().length() == 0) {
            message += "Please enter a course description\n";
        }
        if (message.length() > 0) {
            isValid = false;
        } else {
            isValid = true;
        }
        return isValid;
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
