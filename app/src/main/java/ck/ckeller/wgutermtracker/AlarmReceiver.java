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
        Intent contentIntent = new Intent(context, AssessmentViewerActivity.class);
        contentIntent.putExtra(DBOpenHelper.ASSESSMENT_ID, assessmentId);
        contentIntent.putExtra(DBOpenHelper.ASSESSMENT_COURSE_ID, courseId);
        contentIntent.putExtra(DataProvider.ASSESSMENT_CONTENT_TYPE, longAssessmentId);
        PendingIntent activityIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create notification intent
        Intent alertIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent notificationIntent = PendingIntent.getActivity(context, 0, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create and assign notification channel
        String CHANNEL_ID = "Assessments";
        String CHANNEL_NAME = "Assessment Alarm";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_NAME);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        //Build the notification
        Notification alarmNotification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Assessment Alarm")
                .setContentText("Assessment scheduled for " + assessTimeString)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentIntent(notificationIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_gallery, "Open", activityIntent)
                .addAction(android.R.drawable.ic_menu_gallery, "Dismiss", null)
                .build();

        NotificationManager noteManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteManager.notify(notificationId++, alarmNotification);

        //Sets the alarm and sets intent to show notification.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, assessTime.getTimeInMillis(), notificationIntent);

    }

    public void scheduleCourseAlarm(Context context) {

    }


}
