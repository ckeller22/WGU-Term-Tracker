package ck.ckeller.wgutermtracker;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DataProvider extends ContentProvider {

    // Authority String
    private static final String AUTHORITY = "ck.ckeller.wgutermtracker.dataprovider";

    // Path Strings
    private static final String TERMS_PATH = "terms";
    private static final String COURSES_PATH = "courses";
    private static final String COURSE_NOTES_PATH = "courseNotes";
    private static final String ASSESSMENTS_PATH = "assessments";

    // URI Strings
    public static final Uri TERMS_URI = Uri.parse("content://" + AUTHORITY + "/" + TERMS_PATH);
    public static final Uri COURSES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSES_PATH);
    public static final Uri COURSE_NOTES_URI = Uri.parse("content://" + AUTHORITY + "/" + COURSE_NOTES_PATH);
    public static final Uri ASSESSMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ASSESSMENTS_PATH);

    // Constant to identify the requested operation
    private static final int TERMS = 1;
    private static final int TERMS_ID = 2;
    private static final int COURSES = 3;
    private static final int COURSES_ID = 4;
    private static final int COURSE_NOTES = 5;
    private static final int COURSE_NOTES_ID = 6;
    private static final int ASSESSMENTS = 7;
    private static final int ASSESSMENTS_ID = 8;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, TERMS_PATH, TERMS);
        uriMatcher.addURI(AUTHORITY, TERMS_PATH + "/#", TERMS_ID);
        uriMatcher.addURI(AUTHORITY, COURSES_PATH, COURSES);
        uriMatcher.addURI(AUTHORITY, COURSES_PATH + "/#", COURSES_ID);
        uriMatcher.addURI(AUTHORITY, COURSE_NOTES_PATH, COURSE_NOTES);
        uriMatcher.addURI(AUTHORITY, COURSE_NOTES_PATH, COURSE_NOTES_ID);
        uriMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH, ASSESSMENTS);
        uriMatcher.addURI(AUTHORITY, ASSESSMENTS_PATH, ASSESSMENTS_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch(uriMatcher.match(uri)) {
            case TERMS:
                return database.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERMS_COLUMNS, selection,
                        null, null, null, DBOpenHelper.TERM_ID +
                        " ASC");
            case COURSES:
                return database.query(DBOpenHelper.TABLE_COURSES, DBOpenHelper.COURSES_COLUMNS, selection,
                        null, null, null, DBOpenHelper.COURSE_ID +
                        " ASC");
            case COURSE_NOTES:
                return database.query(DBOpenHelper.TABLE_COURSE_NOTES, DBOpenHelper.COURSE_NOTES_COLUMNS, selection,
                        null, null, null, DBOpenHelper.COURSE_NOTE_ID +
                        " ASC");
            case ASSESSMENTS:
                return database.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ASSESSMENTS_COLUMNS, selection,
                        null, null, null, DBOpenHelper.ASSESSMENT_ID +
                                " ASC");
            default:
                throw new IllegalArgumentException("URI not recognized: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        switch(uriMatcher.match(uri)) {
            case TERMS:
                id = database.insert(DBOpenHelper.TABLE_TERMS, null, values);
                return Uri.parse(TERMS_PATH + "/" + id);
            case COURSES:
                id = database.insert(DBOpenHelper.TABLE_COURSES, null, values);
                return Uri.parse(COURSES_PATH + "/" + id);
            case COURSE_NOTES:
                id = database.insert(DBOpenHelper.TABLE_COURSE_NOTES, null, values);
                return Uri.parse(COURSE_NOTES_PATH + "/" + id);
            case ASSESSMENTS:
                id = database.insert(DBOpenHelper.TABLE_ASSESSMENTS, null, values);
                return Uri.parse(ASSESSMENTS_PATH + "/" + id);
            default:
                throw new IllegalArgumentException("URI not recognized: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(uriMatcher.match(uri)) {
            case TERMS:
                return database.delete(DBOpenHelper.TABLE_TERMS, selection, selectionArgs);
            case COURSES:
                return database.delete(DBOpenHelper.TABLE_COURSES, selection, selectionArgs);
            case COURSE_NOTES:
                return database.delete(DBOpenHelper.TABLE_COURSE_NOTES, selection, selectionArgs);
            case ASSESSMENTS:
                return database.delete(DBOpenHelper.TABLE_ASSESSMENTS, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("URI not recognized: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch(uriMatcher.match(uri)) {
            case TERMS:
                return database.update(DBOpenHelper.TABLE_TERMS, values, selection, selectionArgs);
            case COURSES:
                return database.update(DBOpenHelper.TABLE_COURSES, values, selection, selectionArgs);
            case COURSE_NOTES:
                return database.update(DBOpenHelper.TABLE_COURSE_NOTES, values, selection, selectionArgs);
            case ASSESSMENTS:
                return database.update(DBOpenHelper.TABLE_ASSESSMENTS, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("URI not recognized: " + uri);
        }
    }
}
