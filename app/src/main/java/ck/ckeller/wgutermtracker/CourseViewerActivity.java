package ck.ckeller.wgutermtracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class CourseViewerActivity extends AppCompatActivity {

    private Course currentCourse;
    private Uri currentCourseUri;
    private int courseId;

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
        currentCourse = DataManager.getCourse(this, courseId);

        //currentCourse = DataManager.getCourse(this, courseId);
    }
}
