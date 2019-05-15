package ck.ckeller.wgutermtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DataManager {

    // Terms
    public static Term getTerm(Context context, long termId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.TERMS_URI, DBOpenHelper.TERMS_COLUMNS,
                DBOpenHelper.TERM_ID + " = " + termId , null, null, null);
        cursor.moveToFirst();

        Integer tId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_ID));
        String termName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME));
        String termStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
        String termEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
        Integer termActive = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_ACTIVE));

        Term t = new Term();
        t.setTermId(tId);
        t.setTermName(termName);
        t.setTermStart(termStart);
        t.setTermEnd(termEnd);
        t.setActive(termActive);

        return t;

    }

    public static Uri insertTerm(Context context, String termName, String termStart, String termEnd, int termActive) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termName);
        values.put(DBOpenHelper.TERM_START, termStart);
        values.put(DBOpenHelper.TERM_END, termEnd);
        values.put(DBOpenHelper.TERM_ACTIVE, termActive);

        Uri termUri = context.getContentResolver().insert(DataProvider.TERMS_URI, values);
        return termUri;
    }

    public static int updateTerm(Context context, int termId, String termName, String termStart, String termEnd, int termActive) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termName);
        values.put(DBOpenHelper.TERM_START, termStart);
        values.put(DBOpenHelper.TERM_END, termEnd);
        values.put(DBOpenHelper.TERM_ACTIVE, termActive);

        termId = context.getContentResolver().update(DataProvider.TERMS_URI, values, DBOpenHelper.TERM_ID + " = " + termId, null);
        return termId;
    }

    // Courses

    // Course notes

    // Assessments
}
