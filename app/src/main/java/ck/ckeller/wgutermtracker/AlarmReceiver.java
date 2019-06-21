package ck.ckeller.wgutermtracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    private Intent alarmIntent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public final static String PREFERENCES_NAME = "Alarms";
    public final static String ADD_ASSESSMENT_ALARM_ACTION = "ck.ckeller.wgutermtracker.ASSESS_ALARM";
    public final static String CANCEL_ASSESSMENT_ALARM_ACTION = "ck.ckeller.wgutermtracker.ASSESS_ALARM_CANCEL";
    public final static String ADD_START_COURSE_ALARM_ACTION = "ck.ckeller.wgutermtracker.COURSE_START_ALARM";
    public final static String ADD_END_COURSE_ALARM_ACTION = "ck.ckeller.wgutermtracker.COURSE_END_ALARM";
    public final static String CANCEL_START_COURSE_ALARM_ACTION = "ck.ckeller.wgutermtracker.COURSE_START_ALARM_CANCEL";
    public final static String CANCEL_END_COURSE_ALARM_ACTION = "ck.ckeller.wgutermtracker.COURSE_END_ALARM_CANCEL";

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);

    //todo clean up string usage

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        alarmIntent = intent;

        switch (alarmIntent.getAction()) {
            case ADD_ASSESSMENT_ALARM_ACTION:
                scheduleAssessmentAlarm(context);
                break;
            case ADD_START_COURSE_ALARM_ACTION:
                scheduleCourseAlarm(context, ADD_START_COURSE_ALARM_ACTION);
                break;
            case ADD_END_COURSE_ALARM_ACTION:
                scheduleCourseAlarm(context, ADD_END_COURSE_ALARM_ACTION);
                break;
            case CANCEL_ASSESSMENT_ALARM_ACTION:
                cancelAssessmentAlarm(context, alarmIntent.getIntExtra(DBOpenHelper.ASSESSMENT_ID, 0));
                break;
            case CANCEL_START_COURSE_ALARM_ACTION:
                cancelCourseAlarm(context, alarmIntent.getIntExtra(DBOpenHelper.COURSE_ID, 0), CANCEL_START_COURSE_ALARM_ACTION);
                break;
            case CANCEL_END_COURSE_ALARM_ACTION:
                cancelCourseAlarm(context, alarmIntent.getIntExtra(DBOpenHelper.COURSE_ID, 0), CANCEL_END_COURSE_ALARM_ACTION);
                break;
                //
                default:
                    Log.d("action", "intent action not recognized");
        }

    }

    public void scheduleAssessmentAlarm(Context context) {
        //Get assessment data from alarmIntent
        String assessTimeString = alarmIntent.getStringExtra(DBOpenHelper.ASSESSMENT_DATETIME);
        int assessmentId = alarmIntent.getIntExtra(DBOpenHelper.ASSESSMENT_ID, 0);
        long longAssessmentId = (long)assessmentId;
        int courseId = alarmIntent.getIntExtra(DBOpenHelper.ASSESSMENT_COURSE_ID, 0);
        String assessmentName = alarmIntent.getStringExtra(DBOpenHelper.ASSESSMENT_NAME);

        //Parse assessTimeString to date
        Calendar assessTime = Calendar.getInstance();
        try {
            assessTime.setTime(sdf.parse(assessTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Create contentIntent to deliver AssessmentViewer with notification action
        Intent contentIntent = new Intent(context, NotificationReceiver.class);
        contentIntent.putExtra(DBOpenHelper.ASSESSMENT_ID, assessmentId);
        contentIntent.putExtra(DBOpenHelper.ASSESSMENT_COURSE_ID, courseId);
        contentIntent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, longAssessmentId);
        contentIntent.putExtra(DBOpenHelper.ASSESSMENT_DATETIME, assessTimeString);
        contentIntent.putExtra(DBOpenHelper.ASSESSMENT_NAME, assessmentName);
        contentIntent.setAction(ADD_ASSESSMENT_ALARM_ACTION);
        PendingIntent activityIntent = PendingIntent.getBroadcast(context, assessmentId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Sets the alarm and sets intent to show notification pending validation that the alarm time is in the future.

        if (!isAlarmInPast(context, assessTime)) {
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, assessTime.getTimeInMillis(), activityIntent);

            //Adds a key value pair in shared preferences to easily determine if an assessment alarm exists.
            // 1 for true, 0 for false
            editor.putInt(DataProvider.ASSESSMENT_CONTENT_TYPE + assessmentId, 1);
            editor.commit();
        }

    }

    public void cancelAssessmentAlarm(Context context, int assessmentId) {
        //Creates a notification intent for the AlarmManager to detect and cancel.
        Intent contentIntent = new Intent(context, NotificationReceiver.class);
        contentIntent.setAction(ADD_ASSESSMENT_ALARM_ACTION);
        PendingIntent notificationIntent = PendingIntent.getBroadcast(context, assessmentId, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Assigns intent to AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(notificationIntent);

        editor.putInt(DataProvider.ASSESSMENT_CONTENT_TYPE + assessmentId, 0);
        editor.commit();

    }

    public void scheduleCourseAlarm(Context context, String intentAction) {
        //Get course data from alarmIntent
        int courseId = alarmIntent.getIntExtra(DBOpenHelper.COURSE_ID, -1);
        long longCourseId = (long)courseId;
        String courseStartTimeString = alarmIntent.getStringExtra(DBOpenHelper.COURSE_START) + " 12:00 PM";
        String courseEndTimeString = alarmIntent.getStringExtra(DBOpenHelper.COURSE_END) + " 12:00 PM";
        String courseDesc = alarmIntent.getStringExtra(DBOpenHelper.COURSE_DESC);
        String courseName = alarmIntent.getStringExtra(DBOpenHelper.COURSE_NAME);
        int courseTermId = alarmIntent.getIntExtra(DBOpenHelper.COURSE_TERM_ID, -1);

        //Parse start and end time strings to respective dates
        Calendar courseStartTime = Calendar.getInstance();
        Calendar courseEndTime = Calendar.getInstance();
        try {
            courseStartTime.setTime(sdf.parse(courseStartTimeString));
            courseEndTime.setTime(sdf.parse(courseEndTimeString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Create contentIntent to deliver CourseViewer with notification action
        Intent contentIntent = new Intent(context, NotificationReceiver.class);
        contentIntent.putExtra(DBOpenHelper.COURSE_ID, courseId);
        contentIntent.putExtra(DataProvider.COURSE_CONTENT_TYPE, longCourseId);
        contentIntent.putExtra(DBOpenHelper.COURSE_START, courseStartTimeString);
        contentIntent.putExtra(DBOpenHelper.COURSE_END, courseEndTimeString);
        contentIntent.putExtra(DBOpenHelper.COURSE_TERM_ID, courseTermId);
        contentIntent.putExtra(DBOpenHelper.COURSE_DESC, courseDesc);
        contentIntent.putExtra(DBOpenHelper.COURSE_NAME, courseName);

        //Sets content action based on switch statement in onReceive, creates pending intent
        contentIntent.setAction(intentAction);
        PendingIntent activityIntent = PendingIntent.getBroadcast(context, courseId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Verifies that the respective alarm time is in the future, toasts to alert the user if it is in the past.
        //Sets the alarm based off intentAction and sets intent to show notification if alarm time is in the future.

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (intentAction.equals(ADD_START_COURSE_ALARM_ACTION)) {
            if (!isAlarmInPast(context, courseStartTime)) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, courseStartTime.getTimeInMillis(), activityIntent);

                editor.putInt(DataProvider.COURSE_CONTENT_TYPE + intentAction + courseId, 1);
                editor.commit();
            }
        }
        if (intentAction.equals(ADD_END_COURSE_ALARM_ACTION)) {
            if (!isAlarmInPast(context, courseEndTime)) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, courseEndTime.getTimeInMillis(), activityIntent);

                //Adds a key value pair in shared preferences to easily determine if an course alarm exists.
                // 1 for true, 0 for false
                editor.putInt(DataProvider.COURSE_CONTENT_TYPE + intentAction + courseId, 1);
                editor.commit();
            }

        }

    }

    public void cancelCourseAlarm(Context context, int courseId, String intentAction) {
        //Creates a notification intent for the AlarmManager to detect and cancel, converts intentAction to match to original action that
        //created the alarm.
        Intent contentIntent = new Intent(context, NotificationReceiver.class);
        if (intentAction.equals(AlarmReceiver.CANCEL_START_COURSE_ALARM_ACTION)) {
            contentIntent.setAction(AlarmReceiver.ADD_START_COURSE_ALARM_ACTION);
        } else if (intentAction.equals(AlarmReceiver.CANCEL_END_COURSE_ALARM_ACTION)) {
            contentIntent.setAction(AlarmReceiver.ADD_END_COURSE_ALARM_ACTION);
        }
        PendingIntent notificationIntent = PendingIntent.getBroadcast(context, courseId, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Assigns intent to AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(notificationIntent);

        editor.putInt(DataProvider.COURSE_CONTENT_TYPE + contentIntent.getAction() + courseId, 0);
        editor.commit();
    }

    public boolean isAlarmInPast(Context context, Calendar alarmTime) {
        boolean isAlarmInPast = false;
        if (alarmTime.getTimeInMillis() < System.currentTimeMillis()) {
            isAlarmInPast = true;
            Toast.makeText(context, "The alarm must be set for a future time.", Toast.LENGTH_LONG).show();
        }
        return isAlarmInPast;
    }




}
