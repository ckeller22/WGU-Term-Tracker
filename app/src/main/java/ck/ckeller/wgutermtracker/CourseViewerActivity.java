package ck.ckeller.wgutermtracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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

    private static final int COURSE_EDITOR_ACTIVITY_CODE = 1;
    private static final int ASSESSMENT_LIST_ACTIVITY_CODE = 2;
    private static final int COURSE_NOTE_LIST_ACTIVITY_CODE = 3;

    //todo implement menu items to enable notifications for start and end dates
    //todo enable ability to enable/disable alarms/notifications
    //todo implement alarm for course start/end dates
    //todo implement load manager to allow all lists to load data on refresh.
    //todo finish refactoring code to implement intent actions/intent extras instead of uris

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        startActivityForResult(intent, COURSE_NOTE_LIST_ACTIVITY_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_viewer, menu);
        return true;
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
        }


        return true;
    }

    public void sendCourse(Context context){
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("ck.ckeller.wgutermtracker.COURSE_ALARM");
        intent.putExtra(DBOpenHelper.COURSE_ID, courseId);
        intent.putExtra(DBOpenHelper.COURSE_START, currentCourse.getCourseStart());
        intent.putExtra(DBOpenHelper.COURSE_END, currentCourse.getCourseEnd());
        intent.putExtra(DBOpenHelper.COURSE_DESC, currentCourse.getCourseDesc());
        intent.putExtra(DBOpenHelper.COURSE_TERM_ID, currentCourse.getTermId());
        sendBroadcast(intent);
    }

}
