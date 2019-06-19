package ck.ckeller.wgutermtracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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

    private TaskStackBuilder stackBuilder;

    //todo implement a way to switch between setting a start notification or an end notification

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences(AlarmReceiver.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(new Intent(context, MainActivity.class));

        alarmIntent = intent;

        switch (alarmIntent.getAction()) {
            case AlarmReceiver.ADD_ASSESSMENT_ALARM_ACTION:
                showAssessmentNotification(context);
                break;
            case AlarmReceiver.ADD_START_COURSE_ALARM_ACTION:
                showCourseNotification(context, AlarmReceiver.ADD_START_COURSE_ALARM_ACTION);
                break;
            case AlarmReceiver.ADD_END_COURSE_ALARM_ACTION:
                showCourseNotification(context, AlarmReceiver.ADD_END_COURSE_ALARM_ACTION);
                break;
                default:
                    Log.d("Notification", "notification not recognized.");
        }

    }

    public void showCourseNotification(Context context, String intentAction) {
        int courseId = alarmIntent.getIntExtra(DBOpenHelper.COURSE_ID, -1);
        String courseStartTime = alarmIntent.getStringExtra(DBOpenHelper.COURSE_START);
        String courseEndTime = alarmIntent.getStringExtra(DBOpenHelper.COURSE_END);
        String courseName = alarmIntent.getStringExtra(DBOpenHelper.COURSE_NAME);

        //Create notification intent
        alarmIntent.setClass(context, CourseViewerActivity.class);
        //PendingIntent notificationIntent = PendingIntent.getActivity(context, courseId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        stackBuilder.addNextIntent(alarmIntent);
        PendingIntent notificationIntent = stackBuilder.getPendingIntent(courseId, PendingIntent.FLAG_UPDATE_CURRENT);
        
        //Create and assign notification channel
        String CHANNEL_ID = "Courses";
        String CHANNEL_NAME = "Course Alarm";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_NAME);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        //Creates notification strings based on alarmIntent action
        String contentText = "";
        if (intentAction.equals(AlarmReceiver.ADD_START_COURSE_ALARM_ACTION)) {
            contentText = "Course " + courseName + " starts at " + courseStartTime;
        } else if (intentAction.equals(AlarmReceiver.ADD_END_COURSE_ALARM_ACTION)) {
            contentText = "Course " + courseName + " ends at " + courseEndTime;
        }

        //Build the notification
        Notification alarmNotification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Course Alarm")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentIntent(notificationIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_gallery, "Open", notificationIntent)
                .build();

        //Show the notification
        NotificationManager noteManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteManager.notify(notificationId++, alarmNotification);

        //Set value of assessmentId in shared preferences to 0 once notification is disabled.
        editor.putInt(DataProvider.COURSE_CONTENT_TYPE + intentAction + courseId, 0);
        editor.commit();
        Log.d("ALARM", "VALUE: RECEIVED " + courseId);
    }



    public void showAssessmentNotification(Context context) {
        int assessmentId = alarmIntent.getIntExtra(DBOpenHelper.ASSESSMENT_ID, 0);
        String assessmentName = alarmIntent.getStringExtra(DBOpenHelper.ASSESSMENT_NAME);
        String assessmentTime = alarmIntent.getStringExtra(DBOpenHelper.ASSESSMENT_DATETIME);

        //Create notification intent

        alarmIntent.setClass(context, AssessmentViewerActivity.class);
        stackBuilder.addNextIntent(alarmIntent);

        //PendingIntent notificationIntent = PendingIntent.getActivity(context, assessmentId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent notificationIntent = stackBuilder.getPendingIntent(assessmentId, PendingIntent.FLAG_CANCEL_CURRENT);

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
