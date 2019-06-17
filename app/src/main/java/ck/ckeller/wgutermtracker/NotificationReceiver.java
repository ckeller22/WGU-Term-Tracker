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
import android.util.Log;
import android.widget.EditText;

public class NotificationReceiver extends BroadcastReceiver {

    Intent alarmIntent;
    private int notificationId = 1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //todo implement a way to switch between setting a start notification or an end notification

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(AlarmReceiver.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        alarmIntent = intent;

        if (alarmIntent.getAction().equals(AlarmReceiver.ADD_ASSESSMENT_ALARM_ACTION)) {
            showAssessmentNotification(context);
        } else if (alarmIntent.getAction().equals(AlarmReceiver.ADD_COURSE_ALARM_ACTION)){
            showCourseNotification(context);
        }

    }

    public void showCourseNotification(Context context) {
        int courseId = alarmIntent.getIntExtra(DBOpenHelper.COURSE_ID, -1);
        String courseStartTime = alarmIntent.getStringExtra(DBOpenHelper.COURSE_START);
        String courseEndTime = alarmIntent.getStringExtra(DBOpenHelper.COURSE_END);
        String courseName = alarmIntent.getStringExtra(DBOpenHelper.COURSE_NAME);

        //Create notification intent
        alarmIntent.setClass(context, CourseViewerActivity.class);
        PendingIntent notificationIntent = PendingIntent.getActivity(context, courseId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create and assign notification channel
        String CHANNEL_ID = "Courses";
        String CHANNEL_NAME = "Course Alarm";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_NAME);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        //Build the notification
        Notification alarmNotification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Course Alarm")
                .setContentText("Course " + courseName + " at " + courseStartTime)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentIntent(notificationIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_gallery, "Open", notificationIntent)
                .build();

        //Show the notification
        NotificationManager noteManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteManager.notify(notificationId++, alarmNotification);

        //Set value of assessmentId in shared preferences to 0 once notification is disabled.
        editor.putInt(DataProvider.COURSE_CONTENT_TYPE + courseId, 0);
        editor.commit();
        Log.d("ALARM", "VALUE: RECEIVED " + courseId);
    }

    public void showAssessmentNotification(Context context) {
        int assessmentId = alarmIntent.getIntExtra(DBOpenHelper.ASSESSMENT_ID, 0);
        String assessmentName = alarmIntent.getStringExtra(DBOpenHelper.ASSESSMENT_NAME);
        String assessmentTime = alarmIntent.getStringExtra(DBOpenHelper.ASSESSMENT_DATETIME);

        //Create notification intent
        alarmIntent.setClass(context, AssessmentViewerActivity.class);
        PendingIntent notificationIntent = PendingIntent.getActivity(context, assessmentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
                .setContentText("Assessment " + assessmentName + " at " + assessmentTime)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentIntent(notificationIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_gallery, "Open", notificationIntent)
                .build();

        //Show the notification
        NotificationManager noteManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteManager.notify(notificationId++, alarmNotification);

        //Set value of assessmentId in shared preferences to 0 once notification is disabled.
        editor.putInt(DataProvider.ASSESSMENT_CONTENT_TYPE + assessmentId, 0);
        editor.commit();
    }
}
