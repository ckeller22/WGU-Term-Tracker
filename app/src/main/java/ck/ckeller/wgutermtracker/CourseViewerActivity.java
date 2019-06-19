package ck.ckeller.wgutermtracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CourseViewerActivity extends AppCompatActivity {

    private Course currentCourse;
    private Uri currentCourseUri;
    private int courseId;
    private int termId;

    private MenuItem enableStartNotification;
    private MenuItem disableStartNotification;
    private MenuItem enableEndNotification;
    private MenuItem disableEndNotification;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final int COURSE_EDITOR_ACTIVITY_CODE = 1;
    private static final int ASSESSMENT_LIST_ACTIVITY_CODE = 2;
    private static final int COURSE_NOTE_LIST_ACTIVITY_CODE = 3;

    //todo implement load manager to allow all lists to load data on refresh.
    //todo finish refactoring code to implement intent actions/intent extras instead of uris

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("Alarms", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        parseCourse();
        findTextViews();
    }

    public void findTextViews() {
        TextView tvName = findViewById(R.id.lbl_name);
        TextView tvStatus = findViewById(R.id.tv_status);
        TextView tvStart = findViewById(R.id.tv_start);
        TextView tvEnd = findViewById(R.id.tv_end);
        TextView tvMentor = findViewById(R.id.tv_mentor);
        TextView tvMentorPhone = findViewById(R.id.tv_mentor_phone);
        TextView tvMentorEmail = findViewById(R.id.tv_mentor_email);
        TextView tvDesc = findViewById(R.id.tv_desc);

        tvName.setText(currentCourse.getCourseName());
        tvStatus.setText(currentCourse.getCourseStatus());
        tvStart.setText(currentCourse.getCourseStart());
        tvEnd.setText(currentCourse.getCourseEnd());
        tvMentor.setText(currentCourse.getCourseMentor());
        tvMentorPhone.setText(currentCourse.getCourseMentorPhone());
        tvMentorEmail.setText(currentCourse.getCourseMentorEmail());
        tvDesc.setText(currentCourse.getCourseDesc());
    }

    public void parseCourse() {
        Intent intent = getIntent();
        long longCourseId = intent.getLongExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
        courseId = (int)longCourseId;
        termId = intent.getIntExtra(DBOpenHelper.TERM_ID, 0);
        currentCourse = DataManager.getCourse(this, courseId);

    }

    public void openAssessmentList(View view) {
        Intent intent = new Intent(this, AssessmentListActivity.class);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
        startActivityForResult(intent, ASSESSMENT_LIST_ACTIVITY_CODE);
    }

    public void openCourseNoteList(View view) {
        Intent intent = new Intent(this, CourseNoteListActivity.class);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
        Log.d("value", "value: " + courseId);
        startActivityForResult(intent, COURSE_NOTE_LIST_ACTIVITY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_viewer, menu);
        enableStartNotification = menu.findItem(R.id.enable_start_notification);
        disableStartNotification = menu.findItem(R.id.disable_start_notification);
        enableEndNotification = menu.findItem(R.id.enable_end_notification);
        disableEndNotification = menu.findItem(R.id.disable_end_notification);
        getAppropriateOptions();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getAppropriateOptions();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_course:
                Intent intent = new Intent(CourseViewerActivity.this, CourseEditorActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
                intent.putExtra(DataProvider.TERM_CONTENT_TYPE, termId);
                startActivityForResult(intent, COURSE_EDITOR_ACTIVITY_CODE);
                break;
            case R.id.delete_course:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure you wish to delete this course? All information will be lost!");
                alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.deleteCourse(CourseViewerActivity.this, courseId);
                        Toast.makeText(CourseViewerActivity.this, "Course deleted!", Toast.LENGTH_SHORT).show();
                        CourseViewerActivity.this.finish();
                    }
                });
                alertDialogBuilder.setNegativeButton(android.R.string.no, null);
                alertDialogBuilder.show();
                break;
            case R.id.enable_start_notification:
                sendCourse(CourseViewerActivity.this, AlarmReceiver.ADD_START_COURSE_ALARM_ACTION);
                invalidateOptionsMenu();
                break;
            case R.id.disable_start_notification:
                sendCourse(CourseViewerActivity.this, AlarmReceiver.CANCEL_START_COURSE_ALARM_ACTION);
                invalidateOptionsMenu();
                break;
            case R.id.enable_end_notification:
                sendCourse(CourseViewerActivity.this, AlarmReceiver.ADD_END_COURSE_ALARM_ACTION);
                invalidateOptionsMenu();
                break;
            case R.id.disable_end_notification:
                sendCourse(CourseViewerActivity.this, AlarmReceiver.CANCEL_END_COURSE_ALARM_ACTION);
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    public void sendCourse(Context context, String intentAction){
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(intentAction);
        intent.putExtra(DBOpenHelper.COURSE_ID, courseId);
        intent.putExtra(DBOpenHelper.COURSE_START, currentCourse.getCourseStart());
        intent.putExtra(DBOpenHelper.COURSE_END, currentCourse.getCourseEnd());
        intent.putExtra(DBOpenHelper.COURSE_DESC, currentCourse.getCourseDesc());
        intent.putExtra(DBOpenHelper.COURSE_TERM_ID, currentCourse.getTermId());
        intent.putExtra(DBOpenHelper.COURSE_NAME, currentCourse.getCourseName());
        sendBroadcast(intent);
    }

    public void getAppropriateOptions() {
        int courseStartExists = sharedPreferences.getInt(DataProvider.COURSE_CONTENT_TYPE + AlarmReceiver.ADD_START_COURSE_ALARM_ACTION + courseId, -1);
        int courseEndExists = sharedPreferences.getInt(DataProvider.COURSE_CONTENT_TYPE + AlarmReceiver.ADD_END_COURSE_ALARM_ACTION + courseId, -1);
        if (courseStartExists == 1) {
            enableStartNotification.setVisible(false);
            disableStartNotification.setVisible(true);
        } else {
            enableStartNotification.setVisible(true);
            disableStartNotification.setVisible(false);
        }
        if (courseEndExists == 1) {
            enableEndNotification.setVisible(false);
            disableEndNotification.setVisible(true);
        } else {
            enableEndNotification.setVisible(true);
            disableEndNotification.setVisible(false);
        }


    }

    @Override
    protected void onResume() {
        parseCourse();
        findTextViews();
        super.onResume();
    }
}
