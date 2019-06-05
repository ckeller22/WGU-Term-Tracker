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
    public static Course getCourse(Context context, long courseId) {
        Cursor cursor = context.getContentResolver().query(DataProvider.COURSES_URI, DBOpenHelper.COURSES_COLUMNS,
                DBOpenHelper.COURSE_ID + " = " + courseId , null, null, null);
        cursor.moveToFirst();

        Integer cId = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_ID));
        String courseName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME));
        String courseStart = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
        String courseEnd = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
        String courseMentor = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR));
        String courseMentorPhone = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_PHONE));
        String courseMentorEmail = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_MENTOR_EMAIL));
        String courseStatus = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
        String courseDesc = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_DESC));

        Course c = new Course();
        c.setCourseId(cId);
        c.setCourseName(courseName);
        c.setCourseStart(courseStart);
        c.setCourseEnd(courseEnd);
        c.setCourseMentor(courseMentor);
        c.setCourseMentorPhone(courseMentorPhone);
        c.setCourseMentorEmail(courseMentorEmail);
        c.setCourseStatus(courseStatus);
        c.setCourseDesc(courseDesc);

        return c;
    }

    public static Uri insertCourse(Context context, String courseName, String courseStart, String courseEnd, String courseMentor, String courseMentorPhone,
                                   String courseMentorEmail, String courseStatus, String courseDesc, int courseTermId) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NAME, courseName);
        values.put(DBOpenHelper.COURSE_START, courseStart);
        values.put(DBOpenHelper.COURSE_END, courseEnd);
        values.put(DBOpenHelper.COURSE_MENTOR, courseMentor);
        values.put(DBOpenHelper.COURSE_MENTOR_PHONE, courseMentorPhone);
        values.put(DBOpenHelper.COURSE_MENTOR_EMAIL, courseMentorEmail);
        values.put(DBOpenHelper.COURSE_STATUS, courseStatus);
        values.put(DBOpenHelper.COURSE_DESC, courseDesc);
        values.put(DBOpenHelper.COURSE_TERM_ID, courseTermId);

        Uri courseUri = context.getContentResolver().insert(DataProvider.COURSES_URI, values);
        return courseUri;


    }

    public static int updateCourse(Context context, String courseName, String courseStart, String courseEnd, String courseMentor, String courseMentorPhone,
                                   String courseMentorEmail, String courseStatus, String courseDesc, int courseTermId) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NAME, courseName);
        values.put(DBOpenHelper.COURSE_START, courseStart);
        values.put(DBOpenHelper.COURSE_END, courseEnd);
        values.put(DBOpenHelper.COURSE_MENTOR, courseMentor);
        values.put(DBOpenHelper.COURSE_MENTOR_PHONE, courseMentorPhone);
        values.put(DBOpenHelper.COURSE_MENTOR_EMAIL, courseMentorEmail);
        values.put(DBOpenHelper.COURSE_STATUS, courseStatus);
        values.put(DBOpenHelper.COURSE_DESC, courseDesc);
        values.put(DBOpenHelper.COURSE_TERM_ID, courseTermId);

        courseTermId = context.getContentResolver().update(DataProvider.COURSES_URI, values, DBOpenHelper.COURSE_ID + " = " + courseTermId, null);
        return courseTermId;
    }

    // Course notes

    // Assessments
}
