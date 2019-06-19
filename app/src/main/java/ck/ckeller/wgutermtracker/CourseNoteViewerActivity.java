package ck.ckeller.wgutermtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CourseNoteViewerActivity extends AppCompatActivity {

    private int courseId;
    private int courseNoteId;
    private CourseNote currentCourseNote;

    private int COURSE_NOTE_EDITOR_ACTIVITY_CODE = 1;

    //todo implement sms/email sharing feature for course notes.
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_note_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_course_note:
                Intent intent = new Intent(CourseNoteViewerActivity.this, CourseNoteEditorActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.putExtra(DataProvider.COURSE_NOTE_CONTENT_TYPE, courseNoteId);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
                startActivityForResult(intent, COURSE_NOTE_EDITOR_ACTIVITY_CODE);
                break;
            case R.id.delete_course_note:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure you wish to delete this course note? All information will be lost!");
                alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.deleteCourseNote(CourseNoteViewerActivity.this, courseNoteId);
                        Toast.makeText(CourseNoteViewerActivity.this, "Course note deleted!", Toast.LENGTH_SHORT).show();
                        CourseNoteViewerActivity.this.finish();
                    }
                });
                alertDialogBuilder.setNegativeButton(android.R.string.no, null);
                alertDialogBuilder.show();
                break;
            case R.id.share_course_note:
                Intent messageIntent = new Intent(Intent.ACTION_SEND);
                messageIntent.setType("text/plain");
                messageIntent.putExtra(Intent.EXTRA_TEXT, currentCourseNote.getCourseNoteText());
                startActivity(messageIntent);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        parseCourse();
        findViews();
        super.onResume();
    }
}
