package ck.ckeller.wgutermtracker;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CourseNoteListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int courseId;

    private int COURSE_NOTE_EDITOR_ACTIVITY_CODE = 1;
    private int COURSE_NOTE_VIEWER_ACTIVITY_CODE = 2;

    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseNoteListActivity.this, CourseNoteEditorActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
                startActivityForResult(intent, COURSE_NOTE_EDITOR_ACTIVITY_CODE);
            }
        });

        parseCourse();
        populateCourseNoteList();

    }

    public void parseCourse() {
        Intent intent = getIntent();
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
        Log.d("value", "value: " + courseId);
    }

    public void populateCourseNoteList() {
        //Cursor cursor = getContentResolver().query(DataProvider.COURSE_NOTES_URI, DBOpenHelper.COURSE_NOTES_COLUMNS, DBOpenHelper.COURSE_NOTE_COURSE_ID + " = " + courseId,
        //       null, null, null);
        String[] from = {DBOpenHelper.COURSE_NOTE_TEXT};
        int[] to = {R.id.tv_course_note_text};
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.course_note_list_item, null, from, to, 0);
        ListView list = findViewById(R.id.list_course_notes);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseNoteListActivity.this, CourseNoteViewerActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.putExtra(DataProvider.COURSE_NOTE_CONTENT_TYPE, id);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
                startActivityForResult(intent, COURSE_NOTE_VIEWER_ACTIVITY_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        getLoaderManager().restartLoader(0, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.COURSE_NOTES_URI, DBOpenHelper.COURSE_NOTES_COLUMNS, DBOpenHelper.COURSE_NOTE_COURSE_ID + " = " + courseId,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
