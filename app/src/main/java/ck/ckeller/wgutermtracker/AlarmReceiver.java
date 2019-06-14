package ck.ckeller.wgutermtracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
    private int notificationId = 1;



    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.US);

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmIntent = intent;

        switch (alarmIntent.getAction()) {
            case "ck.ckeller.wgutermtracker.ASSESS_ALARM":
                scheduleAssessmentAlarm(context);
            case "ck.ckeller.wgutermtracker.COURSE_ALARM":
                scheduleCourseAlarm(context);
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

    }

    public static void cancelAssessmentAlarm(Context context, int assessmentId) {
        //Creates a notification intent for the AlarmManager to detect and cancel.
        Intent alertIntent = new Intent("ck.ckeller.wgutermtracker.ASSESS_ALARM");
        PendingIntent notificationIntent = PendingIntent.getBroadcast(context, assessmentId, alertIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //Assigns intent to AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(notificationIntent);

    }

    public void scheduleCourseAlarm(Context context) {

    }




}
