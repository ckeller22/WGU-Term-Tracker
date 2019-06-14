package ck.ckeller.wgutermtracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    Intent alarmIntent;
    private int notificationId = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmIntent = intent;

        if (alarmIntent.getAction().equals("ck.ckeller.wgutermtracker.ASSESS_ALARM")) {
            showAssessmentNotification(context);
        } else if (alarmIntent.getAction().equals("ck.ckeller.wgutermtracker.COURSE_ALARM")){
            showCourseNotification();
        }

    }

    public void showCourseNotification() {

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
                .addAction(android.R.drawable.ic_menu_gallery, "Dismiss", null)
                .build();

        //Show the notification
        NotificationManager noteManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteManager.notify(notificationId++, alarmNotification);
    }
}
