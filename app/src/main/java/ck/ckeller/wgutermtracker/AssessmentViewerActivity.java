package ck.ckeller.wgutermtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AssessmentViewerActivity extends AppCompatActivity {

    private int assessmentId;
    private int courseId;
    private Assessment currentAssessment;

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

}
