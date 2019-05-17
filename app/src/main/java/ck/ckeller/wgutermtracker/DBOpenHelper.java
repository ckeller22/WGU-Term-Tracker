package ck.ckeller.wgutermtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    // Constants for database name and version
    private static final String DATABASE_NAME = "wguTermTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Constants for database tables and columns
    // Terms table
    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "_id";
    public static final String TERM_NAME = "termName";
    public static final String TERM_START = "termStart";
    public static final String TERM_END = "termEnd";
    public static final String TERM_ACTIVE = "termActive";
    public static final String TERM_CREATED = "termCreated";
    public static final String[] TERMS_COLUMNS =
            {TERM_ID, TERM_NAME, TERM_START, TERM_END, TERM_ACTIVE, TERM_CREATED};

    // Courses table
    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_TERM_ID = "courseTermId";
    public static final String COURSE_NAME = "courseTitle";
    public static final String COURSE_DESC = "courseDesc";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String COURSE_START = "courseStart";
    public static final String COURSE_END = "courseEnd";
    public static final String COURSE_MENTOR = "courseMentor";
    public static final String COURSE_MENTOR_PHONE = "courseMentorPhone";
    public static final String COURSE_MENTOR_EMAIL = "courseMentorEmail";
    public static final String COURSE_CREATED = "courseCreated";
    public static final String[] COURSES_COLUMNS =
            {COURSE_ID, COURSE_TERM_ID, COURSE_NAME, COURSE_DESC, COURSE_STATUS, COURSE_START, COURSE_END,
            COURSE_MENTOR, COURSE_MENTOR_PHONE, COURSE_MENTOR_EMAIL, COURSE_CREATED};

    // Course Notes table
    public static final String TABLE_COURSE_NOTES = "courseNotes";
    public static final String COURSE_NOTE_ID = "_id";
    public static final String COURSE_NOTE_COURSE_ID = "courseNoteId";
    public static final String COURSE_NOTE_TEXT = "courseNoteText";
    public static final String COURSE_NOTE_CREATED = "courseNoteCreated";
    public static final String[] COURSE_NOTES_COLUMNS =
            {COURSE_NOTE_ID, COURSE_NOTE_COURSE_ID, COURSE_NOTE_TEXT, COURSE_NOTE_CREATED};

    // Assessments table
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_COURSE_ID = "assessmentCourseId";
    public static final String ASSESSMENT_NAME = "assessmentName";
    public static final String ASSESSMENT_DESC = "assessmentDesc";
    public static final String ASSESSMENT_DATETIME = "assessmentDateTime";
    public static final String ASSESSMENT_CREATED = "assessmentCreated";
    public static final String[] ASSESSMENTS_COLUMNS =
            {ASSESSMENT_ID, ASSESSMENT_COURSE_ID, ASSESSMENT_NAME, ASSESSMENT_DESC, ASSESSMENT_DATETIME,
            ASSESSMENT_CREATED};

    // Create table SQL Statements
    // Terms
    public static final String TERMS_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + TABLE_TERMS + " (" +
            TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TERM_NAME + " TEXT, " +
            TERM_START + " DATE, " +
            TERM_END + " DATE, " +
            TERM_ACTIVE + " INTEGER, " +
            TERM_CREATED + " TEXT DEFAULT CURRENT_TIMESTAMP);"
            ;

    // Courses
    public static final String COURSES_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + " (" +
            COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COURSE_TERM_ID + " INTEGER, " +
            COURSE_NAME + " TEXT, " +
            COURSE_DESC + " TEXT, " +
            COURSE_STATUS + " TEXT, " +
            COURSE_START + " TEXT, " +
            COURSE_END + " TEXT, " +
            COURSE_MENTOR + " TEXT, " +
            COURSE_MENTOR_PHONE + " TEXT, " +
            COURSE_MENTOR_EMAIL + " TEXT, " +
            COURSE_CREATED + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(" + COURSE_TERM_ID + ") REFERENCES " + TABLE_TERMS + "(" +
                TERM_ID + "));"
            ;

    // Course Notes
    public static final String COURSE_NOTES_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + TABLE_COURSE_NOTES + " (" +
            COURSE_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COURSE_NOTE_COURSE_ID + " INTEGER, " +
            COURSE_NOTE_TEXT + " TEXT, " +
            COURSE_NOTE_CREATED + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(" + COURSE_NOTE_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" +
                COURSE_ID + "));"
            ;
    // Assessments
    public static final String ASSESSMENTS_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS " + TABLE_ASSESSMENTS + " (" +
            ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ASSESSMENT_COURSE_ID + " INTEGER, " +
            ASSESSMENT_NAME + " TEXT, " +
            ASSESSMENT_DESC + " TEXT, " +
            ASSESSMENT_DATETIME + " TEXT, " +
            ASSESSMENT_CREATED + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(" + ASSESSMENT_COURSE_ID + ") REFERENCES " + TABLE_COURSES + "(" +
                COURSE_ID + "));"
            ;


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TERMS_TABLE_CREATE);
        db.execSQL(COURSES_TABLE_CREATE);
        db.execSQL(COURSE_NOTES_TABLE_CREATE);
        db.execSQL(ASSESSMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        onCreate(db);
    }
}
;