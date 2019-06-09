package ck.ckeller.wgutermtracker;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AssessmentListActivity extends AppCompatActivity {

    private int courseId;
    private int ASSESSMENT_EDITOR_ACTIVITY_CODE = 1;
    private int ASSESSMENT_VIEWER_ACTIVITY_CODE = 2;

    //todo ability to add new assessment
    //todo fix font and list spacing
    //todo implement menulist to edit, delete assessments


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        parseCourse();
        Log.d("test", "value: " + courseId);
        populateAssessmentList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssessmentListActivity.this, AssessmentEditorActivity.class);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
                startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
            }
        });
    }

    public void populateAssessmentList() {
        Cursor cursor = getContentResolver().query(DataProvider.ASSESSMENTS_URI, DBOpenHelper.ASSESSMENTS_COLUMNS,
               DBOpenHelper.ASSESSMENT_COURSE_ID + " = " + courseId, null, null, null);
        String[] from = {DBOpenHelper.ASSESSMENT_NAME, DBOpenHelper.ASSESSMENT_DATETIME};
        int[] to = {R.id.tv_assess_name, R.id.tv_assess_time};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.assessment_list_item, cursor, from, to, 0);
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
    }

    public void parseCourse() {
        Intent intent = getIntent();
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
    }

}
