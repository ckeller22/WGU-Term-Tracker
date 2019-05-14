package ck.ckeller.wgutermtracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TermEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private Term currentTerm;
    private Uri currentTermUri;

    private EditText editName;
    private EditText editStart;
    private EditText editEnd;

    private DatePickerDialog startDialog;
    private DatePickerDialog endDialog;

    String myFormat = "MM/dd/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);
        if (uri == null) {

        } else {
            
        }
        findViews();
        initDatePickers();

    }

    private void findViews() {
        editName = findViewById(R.id.edit_text_term_name);
        editStart = findViewById(R.id.edit_text_term_start);
        editStart.setInputType(InputType.TYPE_NULL);
        editEnd = findViewById(R.id.edit_text_term_end);
        editEnd.setInputType(InputType.TYPE_NULL);
    }

    public void initDatePickers() {
        editStart.setOnClickListener(this);
        editEnd.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        startDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                editStart.setText(sdf.format(newDate.getTime()));

        }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        endDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                editEnd.setText(sdf.format(newDate.getTime()));

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    }

    public void parseTerm() {
        Intent intent = getIntent();
        currentTermUri = intent.getParcelableExtra(DataProvider.TERM_CONTENT_TYPE);

        long termId = Long.parseLong(currentTermUri.getLastPathSegment());
        currentTerm = DataManager.getTerm(this, termId);

    }

    @Override
    public void onClick(View v) {
        if (v == editStart) {
            startDialog.show();
        }
        if (v == editEnd) {
            endDialog.show();
        }
    }
}
