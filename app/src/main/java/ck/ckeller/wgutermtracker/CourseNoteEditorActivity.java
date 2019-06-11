package ck.ckeller.wgutermtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CourseNoteEditorActivity extends AppCompatActivity {

    private int courseId;
    private int courseNoteId;
    private CourseNote currentCourseNote;

    private EditText courseNoteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCourseNote();
            }
        });

        findViews();
        Intent intent = getIntent();
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);

        if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            setTitle("New Course Note");
        } else if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            setTitle("Edit Course Note");
            parseCourseNote();
            populateFields();
        }
    }

    public void parseCourseNote() {
        Intent intent = getIntent();
        courseNoteId = intent.getIntExtra(DataProvider.COURSE_NOTE_CONTENT_TYPE, 0);
        currentCourseNote = DataManager.getCourseNote(this, courseNoteId);
    }

    public void findViews() {
        courseNoteEditText = findViewById(R.id.edit_text_course_note_text);
    }

    public void populateFields() {
        courseNoteEditText.setText(currentCourseNote.getCourseNoteText());
    }

    public void getCourseNoteFromFields() {
        currentCourseNote.setCourseNoteText(courseNoteEditText.getText().toString().trim());
    }

    public void saveCourseNote() {
        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_INSERT)) {
            currentCourseNote = new CourseNote();
            getCourseNoteFromFields();
            DataManager.insertCourseNote(this, currentCourseNote.getCourseNoteText(), courseId);
            Toast.makeText(CourseNoteEditorActivity.this, "New course note added.", Toast.LENGTH_SHORT).show();
            finish();
        } else if (intent.getAction().equals(Intent.ACTION_EDIT)) {
            getCourseNoteFromFields();
            DataManager.updateCourseNote(this, currentCourseNote.getCourseNoteText(), courseId, courseNoteId);
            Toast.makeText(CourseNoteEditorActivity.this, "Course note updated.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


}
