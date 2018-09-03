package com.example.marik.maporganizer.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.marik.maporganizer.R;
import com.example.marik.maporganizer.activity.MainActivity;
import com.example.marik.maporganizer.db.TaskItem;
import com.example.marik.maporganizer.fragments.FragmentTaskCreation;

import java.util.Date;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static com.example.marik.maporganizer.fragments.FragmentTaskCreation.TASK_DATE;
import static com.example.marik.maporganizer.fragments.FragmentTaskCreation.TIME_NOTIFIER;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        TaskItem mTaskItem = intent.getParcelableExtra(FragmentTaskCreation.ITEM_EXTRA);
        long taskDate = intent.getLongExtra(TASK_DATE,0);
        int notificationId = (int) Math.round(((mTaskItem.getLatitude() + mTaskItem.getLongitude()) * 100000) % 100);
        String GROUP_KEY_TIMED_NOTIF = "com.example.marik.maporganizer.TIMED_NOTIFICATIONS";

        String mDismissNotificationId = mTaskItem.getChoosedAddress();

        // Create wearableExtender
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
        wearableExtender.setDismissalId(mDismissNotificationId);

        Intent contentIntent = new Intent(context, MainActivity.class);
        double[] latlng = new double[]{mTaskItem.getLatitude(), mTaskItem.getLongitude()};
        contentIntent.putExtra(TIME_NOTIFIER, latlng);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, contentIntent, 0);

        String CHANNEL_ID = context.getString(R.string.timed_notification);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.timed_notification_channel);
            String description = context.getString(R.string.timed_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLightColor(Color.GREEN);
            channel.shouldShowLights();
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(VISIBILITY_PUBLIC);


            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Date date =  new Date(taskDate);
       /* if(date == null){
            date = new Date(System.currentTimeMillis() + 60*60*1000);
        }*/
        // using NotificationCompat
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif_nearby)
                .setContentTitle(String.format("To do at %s", date.toString()))
                .setContentText(context.getString(R.string.scheduled_job))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You have a job at " + date.toString() + "at "
                                + mTaskItem.getChoosedAddress()))
                .setVisibility(VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .extend(wearableExtender)
                .setContentIntent(pendingIntent)
                .setGroup(GROUP_KEY_TIMED_NOTIF)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

      NotificationCompat.Builder  mSummaryBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.timed_notifications))
                //set content text to support devices running API level < 24
                .setContentText("New timed messages")
                .setSmallIcon(R.drawable.ic_notif_nearby)
                //build summary info into InboxStyle template
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(context.getString(R.string.timted_group_title)))
                //specify which group this notification belongs to
                .setGroup(GROUP_KEY_TIMED_NOTIF)
                .extend(wearableExtender)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                //set this notification as the summary for the group
                .setGroupSummary(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, mBuilder.build());
    }
}
