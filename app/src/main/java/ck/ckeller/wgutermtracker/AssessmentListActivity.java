package ck.ckeller.wgutermtracker;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AssessmentListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int courseId;
    private int ASSESSMENT_EDITOR_ACTIVITY_CODE = 1;
    private int ASSESSMENT_VIEWER_ACTIVITY_CODE = 2;

    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        parseCourse();
        populateAssessmentList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentEditorActivity.class);
                intent.setAction(Intent.ACTION_INSERT);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
                startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
            }
        });
    }

    public void populateAssessmentList() {
        //Cursor cursor = getContentResolver().query(DataProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_COLUMNS,
        //DBOpenHelper.ASSESSMENT_COURSE_ID + " = " + courseId, null, null, null);
        String[] from = {DBOpenHelper.ASSESSMENT_NAME, DBOpenHelper.ASSESSMENT_DATETIME};
        int[] to = {R.id.tv_assess_name, R.id.tv_assess_time};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.assessment_list_item, null, from, to, 0);
        ListView list = findViewById(R.id.list_assessment);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentViewerActivity.class);
                intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, id);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
                startActivityForResult(intent, ASSESSMENT_VIEWER_ACTIVITY_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);
    }

    public void parseCourse() {
        Intent intent = getIntent();
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
    }

    @Override
    protected void onResume() {
        getLoaderManager().restartLoader(0,null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DataProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_COLUMNS,
                DBOpenHelper.ASSESSMENT_COURSE_ID + " = " + courseId, null, null);
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
