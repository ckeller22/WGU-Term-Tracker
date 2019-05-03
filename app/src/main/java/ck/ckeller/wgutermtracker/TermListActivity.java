package ck.ckeller.wgutermtracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TermListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
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

        Cursor cursor = getContentResolver().query(DataProvider.TERMS_URI, DBOpenHelper.TERMS_COLUMNS,
                null, null, null, null);
        String[] from = {DBOpenHelper.TERM_NAME, DBOpenHelper.TERM_START};
        int[] to = {R.id.tvTerm, R.id.tvStart};
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.term_list_item, cursor, from, to, 0);

        ListView list = findViewById(R.id.list_view);
        list.setAdapter(cursorAdapter);

    }

}
