package ck.ckeller.wgutermtracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssessmentEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editName;
    private EditText editTime;
    private EditText editDesc;

    private int assessmentId;
    private int courseId;
    private Assessment currentAssessment;

    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;
    private String myFormat = "MM/dd/yyyy";
    private SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    private Calendar startTime = Calendar.getInstance();
    private final Calendar calendar = Calendar.getInstance();


    //todo implement intent detection edit/insert
    //todo implement ui for editor
    //todo assessment validation
    //todo data manager for update/insert assessment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findTextViews();
        initDateTimePicker();

        Intent intent = getIntent();
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);

        if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            parseAssessment();
            populateFields();

        } else if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            setTitle("New Assessment");
        }

    }

    public void parseAssessment(){
        Intent intent = getIntent();
        assessmentId = intent.getIntExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, 0);
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
        Log.d("value", "value: " + courseId);
        currentAssessment = DataManager.getAssessment(this, assessmentId);
    }


    public void findTextViews() {
        editName = findViewById(R.id.edit_text_assess_name);
        editTime = findViewById(R.id.edit_text_assess_time);
        editTime.setInputType(InputType.TYPE_NULL);
        editDesc = findViewById(R.id.edit_text_assess_desc);
    }


    public void populateFields() {
        editName.setText(currentAssessment.getAssessmentName());
        editTime.setText(currentAssessment.getAssessmentTime());
        editDesc.setText(currentAssessment.getAssessmentDesc());
    }

    public void initDateTimePicker() {
        editTime.setOnClickListener(this);

        dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                startTime = newDate;
                editTime.setText(sdf.format(newDate.getTime()));

                timeDialog = new TimePickerDialog(AssessmentEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime.set(Calendar.HOUR, hourOfDay);
                        startTime.set(Calendar.MINUTE, minute);

                        Log.d("mess", "valuye" + startTime.getTime());
                        String amOrPm;
                        if (hourOfDay > 12){
                            amOrPm = "PM";
                        } else {
                            amOrPm = "AM";
                        }
                        if (hourOfDay > 12) {
                            hourOfDay = hourOfDay - 12;
                        }
                        if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        String minuteString = Integer.toString(minute);
                        if (minute < 10) {
                            minuteString = minuteString + 0;
                        }
                        String time = hourOfDay + ":" + minuteString + " " + amOrPm;
                        editTime.setText(sdf.format(startTime.getTime()) + " " + time);

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), false);
                timeDialog.show();
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


    }

    public void getAssessmentFromFields() {
        currentAssessment.setAssessmentName(editName.getText().toString().trim());
        currentAssessment.setAssessmentTime(editTime.getText().toString().trim());
        currentAssessment.setAssessmentDesc(editDesc.getText().toString().trim());
    }

    public void saveAssessment(View view) {
        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            getAssessmentFromFields();
            DataManager.updateAssessment(this, currentAssessment.getAssessmentName(), currentAssessment.getAssessmentTime(), currentAssessment.getAssessmentDesc(), courseId, assessmentId);
            finish();
        } else if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            currentAssessment = new Assessment();
            getAssessmentFromFields();
            DataManager.insertAssessment(this, currentAssessment.getAssessmentName(), currentAssessment.getAssessmentTime(), currentAssessment.getAssessmentDesc(), courseId);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == editTime) {
            dateDialog.show();
        }
    }
}
