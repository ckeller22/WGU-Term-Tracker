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
    private final String PREFERENCES_NAME = "Alarms";
    private final String ADD_ASSESSMENT_ALARM_ACTION = "ck.ckeller.wgutermtracker.ASSESS_ALARM";
    private final String CANCEL_ASSESSMENT_ALARM_ACTION = "ck.ckeller.wgutermtracker.ASSESS_ALARM_CANCEL";
    private final String ADD_COURSE_ALARM_ACTION = "ck.ckeller.wgutermtracker.COURSE_ALARM";
    private final String CANCEL_COURSE_ALARM_ACTION = "ck.ckeller.wgutermtracker.COURSE_ALARM_CANCEL";

    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);

    //todo implement course alarms add/delete, clean up string usage
    
    
    @Override
    public void onReceive(Context context, Intent intent) {
    sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        alarmIntent = intent;

        switch (alarmIntent.getAction()) {
            case ADD_ASSESSMENT_ALARM_ACTION:
                scheduleAssessmentAlarm(context);
                break;
            case ADD_COURSE_ALARM_ACTION:
                scheduleCourseAlarm(context);
                break;
            case CANCEL_ASSESSMENT_ALARM_ACTION:
                cancelAssessmentAlarm(context, alarmIntent.getIntExtra(DBOpenHelper.ASSESSMENT_ID, 0));
                break;
            case CANCEL_COURSE_ALARM_ACTION:
                cancelCourseAlarm(context, alarmIntent.getIntExtra(DBOpenHelper.COURSE_ID, 0));
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
        contentIntent.setAction("ck.ckeller.wgutermtracker.ASSESS_ALARM");
        PendingIntent activityIntent = PendingIntent.getBroadcast(context, assessmentId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Sets the alarm and sets intent to show notification.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 8 * 1000, activityIntent);

        //Adds a key value pair in shared preferences to easily determine if an assessment alarm exists.
        // 1 for true, 0 for false
        editor.putInt(DataProvider.ASSESSMENT_CONTENT_TYPE + assessmentId, 1);
        editor.commit();
    }

    public void cancelAssessmentAlarm(Context context, int assessmentId) {
        //Creates a notification intent for the AlarmManager to detect and cancel.
        Intent contentIntent = new Intent(context, NotificationReceiver.class);
        contentIntent.setAction("ck.ckeller.wgutermtracker.ASSESS_ALARM_CANCEL");
        PendingIntent notificationIntent = PendingIntent.getBroadcast(context, assessmentId, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Assigns intent to AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(notificationIntent);

        editor.putInt(DataProvider.ASSESSMENT_CONTENT_TYPE + assessmentId, 0);
        editor.commit();

    }

    public void scheduleCourseAlarm(Context context) {

    }

    public void cancelCourseAlarm(Context context, int courseId) {

    }




}
