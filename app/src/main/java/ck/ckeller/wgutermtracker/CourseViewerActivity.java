package ck.ckeller.wgutermtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    //ToDo Add ability to transition and view assessment list

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
        currentCourseUri = intent.getParcelableExtra(DataProvider.COURSE_CONTENT_TYPE);
        courseId = Integer.parseInt(currentCourseUri.getLastPathSegment());
        termId = intent.getIntExtra(DataProvider.TERM_CONTENT_TYPE, 0);
        currentCourse = DataManager.getCourse(this, courseId);

    }

    public void openAssessmentList(View view) {
        Intent intent = new Intent(this, AssessmentListActivity.class);
        intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
        startActivityForResult(intent, ASSESSMENT_LIST_ACTIVITY_CODE);
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
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, currentCourseUri);
                intent.putExtra(DataProvider.TERM_CONTENT_TYPE, termId);
                startActivityForResult(intent, COURSE_EDITOR_ACTIVITY_CODE);
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
        }


        return true;
    }

}
