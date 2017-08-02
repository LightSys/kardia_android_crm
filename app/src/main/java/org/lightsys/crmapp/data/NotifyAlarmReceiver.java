package org.lightsys.crmapp.data;

import android.content.BroadcastReceiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.IntegerRes;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.lightsys.crmapp.R;
import org.lightsys.crmapp.activities.ProfileActivity;
import org.lightsys.crmapp.data.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static org.lightsys.crmapp.data.CRMContract.CollaborateeTable.PARTNER_NAME;

/**
 * Created by Daniel Garcia on 02/Aug/2017,
 * based on the model created by Andrew Lockridge on 6/2/2015.
 *
 * This class receives a signal when the alarm goes off and sends a notification
 * This class also takes care of receiving the signal of a boot up completed
 * When the device boots up, all alarms need to be reset
 */
public class NotifyAlarmReceiver extends BroadcastReceiver {

    public static final String PARTNER_ID_KEY = "EXTRA_PARTNER_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        // If signal received was from bootup, go through process to reset notification alarms
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            resetAlarms(context);
        } else { // Signal came from alarm going off and notification should be sent to user
            sendNotification(context, intent);
        }
    }

    /**
     * Retrieves all notifications from database and resets them if they are valid
     * @param context, context called in
     */
    private void resetAlarms(Context context) {
        // Setting alarms requires sdk version 19 or newer
        if (Build.VERSION.SDK_INT >= 19) {

            //Get notifications from database and place them into an ArrayList
            ArrayList<Notification> notifications = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(
                    CRMContract.NotificationsTable.CONTENT_URI,
                    new String[] {CRMContract.NotificationsTable.NOTIFICATION_ID,
                            CRMContract.NotificationsTable.TIME,
                            CRMContract.NotificationsTable.PARTNER_ID,
                            CRMContract.NotificationsTable.NOTES},
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                Notification n = new Notification();
                n.setId(Integer.parseInt(cursor.getString(0)));
                n.setNotificationTime(Long.parseLong(cursor.getString(1)));
                n.setPartnerID(cursor.getString(2));
                n.setNote(cursor.getString(3));
                notifications.add(n);
            }

            cursor.close();

            Intent alarmIntent;
            PendingIntent pendingIntent;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Loop through all notifications
            for (Notification notification : notifications) {
                // If notification time has not passed, set alarm
                if (notification.getNotificationTime() > Calendar.getInstance().getTimeInMillis()) {
                    alarmIntent = new Intent(context, NotifyAlarmReceiver.class);
                    alarmIntent.putExtra("name", notification.getPartnerID());
                    alarmIntent.putExtra("note", notification.getNote());
                    alarmIntent.putExtra("id", Integer.toString(notification.getId()));
                    pendingIntent = PendingIntent.getBroadcast(context, notification.getId(), alarmIntent, 0);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    Log.w("tag", "Alarm set for: " + format.format(notification.getNotificationTime())
                            + ", ID:" + Integer.toString(notification.getId()) + ", Name:" + notification.getPartnerID());

                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, notification.getNotificationTime(), pendingIntent);
                } else { // Time has passed and notification can be deleted from database
                    context.getContentResolver().delete(CRMContract.NotificationsTable.CONTENT_URI, CRMContract.NotificationsTable.NOTIFICATION_ID + " = ?",
                            new String[] {Integer.toString(notification.getId())});
                }
            }
        }
    }

    private void sendNotification(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder nBuild;
        android.app.Notification n;
        String name, subject, partnerID;
        int notificationID;
        Intent profileIntent;
        PendingIntent pendingIntent;

        notificationID = intent.getIntExtra("notificationId", 0);
        name = intent.getStringExtra("name");
        partnerID = intent.getStringExtra("partnerID");
        subject = intent.getStringExtra("note");

        profileIntent = new Intent(context, ProfileActivity.class);
        profileIntent.putExtra(PARTNER_ID_KEY, partnerID);
        profileIntent.putExtra(PARTNER_NAME, name);
        pendingIntent = PendingIntent.getActivity(context, notificationID, profileIntent, 0);

        // Build the notification to be sent
        // BigTextStyle allows notification to be expanded if text is more than one line
        nBuild = new NotificationCompat.Builder(context)
                .setContentTitle("Followup Reminder")
                .setContentText(name + ": " + subject)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.kardiabeat_v3)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(name + ": " + subject));

        n = nBuild.build();
        notificationManager.notify(notificationID, n);

        // Delete notification from database once sent as it will not be needed again
        //context.getContentResolver().delete(CRMContract.NotificationsTable.CONTENT_URI,
        //        CRMContract.NotificationsTable.NOTIFICATION_ID + " = ?",
        //        new String[] {Integer.toString(notificationID)});

        //Delete notification, plus any notifications still stored whose time has already passed
        Cursor c = context.getContentResolver().query(
                CRMContract.NotificationsTable.CONTENT_URI,
                new String[] {CRMContract.NotificationsTable.NOTIFICATION_ID,
                        CRMContract.NotificationsTable.TIME},
                null, null, null);

        //TODO: Only delete everything before yesterday; find out how to get today in Millis
        while(c.moveToNext()){
            if(Long.parseLong(c.getString(1)) < Calendar.getInstance().getTimeInMillis()){
                context.getContentResolver().delete(CRMContract.NotificationsTable.CONTENT_URI,
                        CRMContract.NotificationsTable.NOTIFICATION_ID + " + ?",
                        new String[] {c.getString(0)});
            }
        }

        c.close();
    }
}