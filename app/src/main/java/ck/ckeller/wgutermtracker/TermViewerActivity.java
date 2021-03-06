package ck.ckeller.wgutermtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TermViewerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int termId;
    private Uri currentTermUri;
    private Term currentTerm;

    private int COURSE_LIST_ACTIVITY_CODE = 1;
    private int TERM_EDITOR_ACTIVITY_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_viewer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        parseTerm();
        if (currentTerm.getActive() == 1) {
            setTitle(getString(R.string.current_term));
        } else {
            setTitle(getString(R.string.term_info));
        }
        findTextViews();

    }


    public void openCourseList(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
        intent.putExtra(DBOpenHelper.TERM_ID, termId);
        startActivityForResult(intent, COURSE_LIST_ACTIVITY_CODE);

    }

    public void findTextViews() {
        TextView tvName = findViewById(R.id.text);
        TextView tvStart = findViewById(R.id.tv_term_start);
        TextView tvEnd = findViewById(R.id.tv_term_end);

        tvName.setText(currentTerm.getTermName());
        tvStart.setText(currentTerm.getTermStart());
        tvEnd.setText(currentTerm.getTermEnd());
    }

    public void parseTerm() {
        Intent intent = getIntent();
        long longTermId = intent.getLongExtra(DBOpenHelper.TERM_ID, 0);
        termId = (int)longTermId;
        currentTerm = DataManager.getTerm(this, termId);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_term_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_term:
                Intent intent = new Intent(this, TermEditorActivity.class);
                intent.setAction(Intent.ACTION_EDIT);
                intent.putExtra(DataProvider.TERM_CONTENT_TYPE, termId);
                startActivityForResult(intent, TERM_EDITOR_ACTIVITY_CODE);
                break;
            case R.id.mark_active:
                markTermAsActive();
                Toast.makeText(this, currentTerm.getTermName() + getString(R.string.set_active), Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete_term:
                if (termDeleteValidation()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Are you sure you wish to delete this term? All information will be lost!");
                    alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DataManager.deleteTerm(TermViewerActivity.this, termId);
                            Toast.makeText(TermViewerActivity.this, "Term deleted!", Toast.LENGTH_SHORT).show();
                            TermViewerActivity.this.finish();
                        }
                    });
                    alertDialogBuilder.setNegativeButton(android.R.string.no, null);
                    alertDialogBuilder.show();
                } else {
                    Toast.makeText(TermViewerActivity.this, "The term has at least one course assigned to it and cannot be deleted.", Toast.LENGTH_SHORT).show();
                }
        }
        return true;
    }

    public boolean termDeleteValidation() {
        Cursor cursor = getContentResolver().query(DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS, DBOpenHelper.COURSE_TERM_ID + " = " + termId,
                null, null, null);
        return !cursor.moveToFirst();

    }

    @Override
    protected void onResume() {
        parseTerm();
        findTextViews();
        super.onResume();
    }

    public void markTermAsActive() {

        Cursor cursor = getContentResolver().query(DataProvider.TERMS_URI, DBOpenHelper.TERMS_COLUMNS,
                null, null, null, null);
        ArrayList<Term> termList = new ArrayList<>();
        while (cursor.moveToNext()) {
            termList.add(DataManager.getTerm(this, cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_ID))));
        }
        for (Term term : termList) {
            DataManager.updateTerm(this, term.getTermId(), term.getTermName(), term.getTermStart(), term.getTermEnd(), 0);
        }

        DataManager.updateTerm(this, currentTerm.getTermId(), currentTerm.getTermName(), currentTerm.getTermStart(), currentTerm.getTermEnd(), 1);
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
