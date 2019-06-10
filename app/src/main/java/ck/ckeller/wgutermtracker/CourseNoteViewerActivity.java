package ck.ckeller.wgutermtracker;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class CourseNoteViewerActivity extends AppCompatActivity {

    private int courseId;
    private int courseNoteId;
    private CourseNote currentCourseNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_viewer);
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

        parseCourse();
        findViews();
    }

    public void parseCourse() {
        Intent intent = getIntent();
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
        long longCourseNoteId = intent.getLongExtra(DataProvider.COURSE_NOTE_CONTENT_TYPE, 0);
        courseNoteId = (int)longCourseNoteId;
        currentCourseNote = DataManager.getCourseNote(this, courseNoteId);
    }

    public void findViews() {
        TextView courseNoteText = findViewById(R.id.tv_course_note_text);

        courseNoteText.setText(currentCourseNote.getCourseNoteText());
    }

}
