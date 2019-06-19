package ck.ckeller.wgutermtracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class AssessmentViewerActivity extends AppCompatActivity {

    private int assessmentId;
    private int courseId;
    private Assessment currentAssessment;

    private MenuItem enableNotification;
    private MenuItem disableNotification;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private int ASSESSMENT_EDITOR_ACTIVITY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_viewer);
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
        sharedPreferences = getSharedPreferences("Alarms", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        parseAssessment();
        findTextViews();
    }

    public void parseAssessment(){
        Intent intent = getIntent();
        long longAssessmentId = intent.getLongExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, 0);
        assessmentId = (int)longAssessmentId;
        courseId = intent.getIntExtra(DataProvider.COURSE_CONTENT_TYPE, 0);
        currentAssessment = DataManager.getAssessment(this, longAssessmentId);
    }

    public void findTextViews() {
        TextView tvName = findViewById(R.id.tv_assess_name);
        TextView tvDesc = findViewById(R.id.tv_assess_desc);
        TextView tvTime = findViewById(R.id.tv_assess_time);

        tvName.setText(currentAssessment.getAssessmentName());
        tvDesc.setText(currentAssessment.getAssessmentDesc());
        tvTime.setText(currentAssessment.getAssessmentTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_assessment_viewer, menu);
        enableNotification = menu.findItem(R.id.enable_assess_notification);
        disableNotification = menu.findItem(R.id.disable_assess_notification);
        getAppropriateOptions();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getAppropriateOptions();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_assessment:
                Intent intent = new Intent(AssessmentViewerActivity.this, AssessmentEditorActivity.class);
                intent.putExtra(DataProvider.COURSE_CONTENT_TYPE, courseId);
                intent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, assessmentId);
                intent.setAction(Intent.ACTION_EDIT);
                startActivityForResult(intent, ASSESSMENT_EDITOR_ACTIVITY_CODE);
                break;
            case R.id.delete_assessment:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure you wish to delete this assessment? All information will be lost!");
                alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataManager.deleteAssessment(AssessmentViewerActivity.this, assessmentId);
                        Toast.makeText(AssessmentViewerActivity.this, "Assessment deleted!", Toast.LENGTH_SHORT).show();
                        AssessmentViewerActivity.this.finish();
                    }
                });
                alertDialogBuilder.setNegativeButton(android.R.string.no, null);
                alertDialogBuilder.show();
                break;
            case R.id.enable_assess_notification:
                sendAssessment(AssessmentViewerActivity.this, AlarmReceiver.ADD_ASSESSMENT_ALARM_ACTION);
                invalidateOptionsMenu();
                break;
            case R.id.disable_assess_notification:
                sendAssessment(AssessmentViewerActivity.this, AlarmReceiver.CANCEL_ASSESSMENT_ALARM_ACTION);
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    public void sendAssessment(Context context, String intentAction) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction(intentAction);
        intent.putExtra(DBOpenHelper.ASSESSMENT_ID, assessmentId);
        intent.putExtra(DBOpenHelper.ASSESSMENT_COURSE_ID, courseId);
        intent.putExtra(DBOpenHelper.ASSESSMENT_NAME, currentAssessment.getAssessmentName());
        intent.putExtra(DBOpenHelper.ASSESSMENT_DATETIME, currentAssessment.getAssessmentTime());
        sendBroadcast(intent);
    }

    public void getAppropriateOptions() {
        int assessmentExists = sharedPreferences.getInt(DataProvider.ASSESSMENT_CONTENT_TYPE + assessmentId, -1);
        if (assessmentExists == 1) {
            enableNotification.setVisible(false);
            disableNotification.setVisible(true);
        } else if (assessmentExists == 0 || assessmentExists == -1) {
            enableNotification.setVisible(true);
            disableNotification.setVisible(false);
        }

    }

    @Override
    protected void onResume() {
        parseAssessment();
        findTextViews();
        super.onResume();
    }
}
