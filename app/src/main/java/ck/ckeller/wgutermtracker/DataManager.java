package ck.ckeller.wgutermtracker;

import android.content.Context;
import android.database.Cursor;

public class DataManager {

    // Terms
    public static Term getTerm(Context context, long termId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.TERMS_URI, DBOpenHelper.TERMS_COLUMNS,
                DBOpenHelper.TERM_ID + " = " + termId , null, null, null);
        cursor.moveToFirst();

        String termName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME));
        String termStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
        String termEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));

        Term t = new Term();
        t.setTermName(termName);
        t.setTermStart(termStart);
        t.setTermEnd(termEnd);

        return t;

    }
    // Courses

    // Course notes

    // Assessments
}
