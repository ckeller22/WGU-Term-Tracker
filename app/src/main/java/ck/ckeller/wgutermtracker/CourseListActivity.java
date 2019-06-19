package ck.ckeller.wgutermtracker;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CourseListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int termId;
    private Term currentTerm;
    private Uri currentTermUri;

    private CursorAdapter cursorAdapter;

    private static final int COURSE_VIEWER_ACTIVITY_CODE = 1;
    private static final int COURSE_EDITOR_ACTIVITY_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseListActivity.this, CourseEditorActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.putExtra(DBOpenHelper.TERM_ID, termId);
                startActivityForResult(intent, COURSE_EDITOR_ACTIVITY_CODE);
            }
        });

        parseTerm();
        populateCourseList();
    }

    private void populateCourseList() {
        //Cursor cursor = getContentResolver().query(DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS,
        //        DBOpenHelper.COURSE_TERM_ID + " = " + currentTerm.getTermId(), null, null, null);
        String[] from = {DBOpenHelper.COURSE_NAME, DBOpenHelper.COURSE_START, DBOpenHelper.COURSE_END};
        int[] to = {R.id.tv_course_name, R.id.tv_course_start, R.id.tv_course_end};
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.course_list_item, null, from, to, 0);
        ListView list = findViewById(R.id.course_view);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseListActivity.this, CourseViewerActivity.class);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, id);
                intent.putExtra(DBOpenHelper.TERM_ID, termId);
                startActivityForResult(intent, COURSE_VIEWER_ACTIVITY_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    private void parseTerm() {
        Intent intent = getIntent();
        termId = intent.getIntExtra(DBOpenHelper.TERM_ID, 0);
        currentTerm = DataManager.getTerm(this, termId);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS, DBOpenHelper.COURSE_TERM_ID + " = " + currentTerm.getTermId(), null, null);
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
