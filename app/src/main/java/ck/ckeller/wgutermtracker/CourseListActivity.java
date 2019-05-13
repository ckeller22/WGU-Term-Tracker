package ck.ckeller.wgutermtracker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CourseListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Long termId;
    private Term currentTerm;
    private Uri currentTermUri;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        currentTermUri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);

        termId = Long.parseLong(currentTermUri.getLastPathSegment());
        currentTerm = DataManager.getTerm(this, termId);
        Log.d("myMessage", Integer.toString(currentTerm.getTermId()));
        populateCourseList();
    }

    private void populateCourseList() {
        Cursor cursor = getContentResolver().query(DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSE_TERM_ID + " = " + currentTerm.getTermId(), null, null, null);
        String[] from = {DBOpenHelper.COURSE_NAME, DBOpenHelper.COURSE_START, DBOpenHelper.COURSE_END};
        int[] to = {R.id.tv_course_name, R.id.tv_course_start, R.id.tv_course_end};
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.course_list_item, cursor, from, to, 0);
        ListView list = findViewById(R.id.course_view);
        list.setAdapter(cursorAdapter);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}