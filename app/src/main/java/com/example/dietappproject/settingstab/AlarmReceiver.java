package com.example.dietappproject.settingstab;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.dietappproject.MainActivity;
import com.example.dietappproject.R;

public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager mNotificationManager;

    private static final int NOTIFICATION_ID_BR = 0;
    private static final int NOTIFICATION_ID_LU = 1;
    private static final int NOTIFICATION_ID_DI = 2;
    private static final String PRIMARY_CHANNEL_ID = "Meal notification";

    public static final String ALARM_TYPE = "alarm";
    public static final String ALARM_BR = "breakfast";
    public static final String ALARM_LU = "lunch";
    public static final String ALARM_DI = "dinner";

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Bundle bundle = intent.getExtras();
        String receivedAlarm = bundle.getString(ALARM_TYPE);

        Log.i("AlarmReceiver", bundle.getString(ALARM_TYPE));

        switch (receivedAlarm) {
            case ALARM_BR:
                breakfastNotification(context);
                break;
            case ALARM_LU:
                lunchNotification(context);
                break;
            case ALARM_DI:
                dinnerNotification(context);
                break;
        }
    }

    private void breakfastNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.putExtra("notificationFragment", "AddMealFragment");
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID_BR, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_breakfast)
                .setContentTitle("Breakfast alert")
                .setContentText("Remember to eat your daily breakfast")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(NOTIFICATION_ID_BR, builder.build());
    }

    private void lunchNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.putExtra("notificationFragment", "AddMealFragment");
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID_LU, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_lunch)
                .setContentTitle("Lunch alert")
                .setContentText("Have a lunch break!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(NOTIFICATION_ID_LU, builder.build());
    }

    private void dinnerNotification(Context context) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.putExtra("notificationFragment", "AddMealFragment");
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID_DI, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_meal)
                .setContentTitle("Dinner alert")
                .setContentText("Time for dinner!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(NOTIFICATION_ID_DI, builder.build());
    }
}