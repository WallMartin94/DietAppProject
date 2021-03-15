package com.example.dietappproject.settingstab;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.dietappproject.R;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;


public class SettingsFragment extends Fragment {
    private static final String TAG = "FoodItemFragment";

    //Notification Setup
    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID_BR = 0;
    private static final int NOTIFICATION_ID_LU = 1;
    private static final int NOTIFICATION_ID_DI = 2;
    private static final String PRIMARY_CHANNEL_ID = "Meal notification";
    public static final String ALARM_TYPE = "alarm";
    public static final String ALARM_BR = "breakfast";
    public static final String ALARM_LU = "lunch";
    public static final String ALARM_DI = "dinner";

    //Notification Alarm Times
    private int breakfastHour = 0;
    private int breakfastMinute = 0;
    private int lunchHour = 0;
    private int lunchMinute = 0;
    private int dinnerHour = 0;
    private int dinnerMinute = 0;

    //Shared Preferences
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor sharedPrefEditor;
    private static final String NOTIFICATION_BR_H = "notification_br_h";
    private static final String NOTIFICATION_BR_M = "notification_br_m";
    private static final String NOTIFICATION_LU_H = "notification_lu_h";
    private static final String NOTIFICATION_LU_M = "notification_lu_m";
    private static final String NOTIFICATION_DI_H = "notification_di_h";
    private static final String NOTIFICATION_DI_M = "notification_di_m";

    Context context;
    SwitchCompat switchBreakfast;
    SwitchCompat switchLunch;
    SwitchCompat switchDinner;
    TimePicker timePickerBreakfast;
    TimePicker timePickerLunch;
    TimePicker timePickerDinner;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getActivity();
        switchBreakfast = v.findViewById(R.id.switch_settings_breakfast);
        switchLunch = v.findViewById(R.id.switch_settings_lunch);
        switchDinner = v.findViewById(R.id.switch_settings_dinner);
        timePickerBreakfast = v.findViewById(R.id.timepicker_settings_breakfast);
        timePickerLunch = v.findViewById(R.id.timepicker_settings_lunch);
        timePickerDinner = v.findViewById(R.id.timepicker_settings_dinner);

        //Shared preferences
        sharedPref = getActivity().getSharedPreferences("settings", context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();

        //Setup Notifications and alarms
        setupTimePickers();
        setupNotifications();
        mNotificationManager = (NotificationManager)
                context.getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        return v;
    }

    public void setupNotifications() {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Notify Intents for alarm
        final Intent notifyIntentBreakfast = new Intent(context, AlarmReceiver.class);
        notifyIntentBreakfast.putExtra(ALARM_TYPE, ALARM_BR);
        final Intent notifyIntentLunch = new Intent(context, AlarmReceiver.class);
        notifyIntentLunch.putExtra(ALARM_TYPE, ALARM_LU);
        final Intent notifyIntentDinner = new Intent(context, AlarmReceiver.class);
        notifyIntentDinner.putExtra(ALARM_TYPE, ALARM_DI);

        //Set settings switches on/off if alarm is on/off
        boolean alarmUpBreakfast = (PendingIntent.getBroadcast(context, NOTIFICATION_ID_BR, notifyIntentBreakfast,
                PendingIntent.FLAG_NO_CREATE) != null);
        boolean alarmUpLunch = (PendingIntent.getBroadcast(context, NOTIFICATION_ID_LU, notifyIntentLunch,
                PendingIntent.FLAG_NO_CREATE) != null);
        boolean alarmUpDinner = (PendingIntent.getBroadcast(context, NOTIFICATION_ID_DI, notifyIntentDinner,
                PendingIntent.FLAG_NO_CREATE) != null);
        switchBreakfast.setChecked(alarmUpBreakfast);
        switchLunch.setChecked(alarmUpLunch);
        switchDinner.setChecked(alarmUpDinner);

        //--- Switch - Breakfast
        switchBreakfast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PendingIntent notifyPendingIntentBreakfast = PendingIntent.getBroadcast
                        (context, NOTIFICATION_ID_BR, notifyIntentBreakfast, PendingIntent.FLAG_UPDATE_CURRENT);

                String toastMessage;

                if (isChecked) {
                    //If switch checked -> Turn on alarm
                    //Get timepicker time
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        breakfastHour = timePickerBreakfast.getCurrentHour();
                        breakfastMinute = timePickerBreakfast.getCurrentMinute();
                    } else {
                        breakfastHour = timePickerBreakfast.getHour();
                        breakfastMinute = timePickerBreakfast.getMinute();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, breakfastHour);
                    calendar.set(Calendar.MINUTE, breakfastMinute);

                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating
                                (AlarmManager.RTC_WAKEUP,
                                        calendar.getTimeInMillis(),
                                        AlarmManager.INTERVAL_DAY,
                                        notifyPendingIntentBreakfast);
                    }
                    toastMessage = "Breakfast reminder on";

                    sharedPrefEditor.putInt(NOTIFICATION_BR_H, breakfastHour);
                    sharedPrefEditor.putInt(NOTIFICATION_BR_M, breakfastMinute);
                    sharedPrefEditor.apply();
                } else {
                    //If switch not checked -> Disable alarm
                    mNotificationManager.cancel(NOTIFICATION_ID_BR);
                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntentBreakfast);
                        notifyPendingIntentBreakfast.cancel();
                    }
                    toastMessage = "Breakfast reminder off";
                }

                //Toast to confirm setting
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        //--- Switch - Lunch
        switchLunch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PendingIntent notifyPendingIntentLunch = PendingIntent.getBroadcast
                        (context, NOTIFICATION_ID_LU, notifyIntentLunch, PendingIntent.FLAG_UPDATE_CURRENT);

                String toastMessage;

                if (isChecked) {
                    //If switch checked -> Turn on alarm
                    //Get timepicker time
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        lunchHour = timePickerLunch.getCurrentHour();
                        lunchMinute = timePickerLunch.getCurrentMinute();
                    } else {
                        lunchHour = timePickerLunch.getHour();
                        lunchMinute = timePickerLunch.getMinute();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, lunchHour);
                    calendar.set(Calendar.MINUTE, lunchMinute);

                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating
                                (AlarmManager.RTC_WAKEUP,
                                        calendar.getTimeInMillis(),
                                        AlarmManager.INTERVAL_DAY,
                                        notifyPendingIntentLunch);
                    }
                    toastMessage = "Lunch reminder on";

                    sharedPrefEditor.putInt(NOTIFICATION_LU_H, lunchHour);
                    sharedPrefEditor.putInt(NOTIFICATION_LU_M, lunchMinute);
                    sharedPrefEditor.apply();
                } else {
                    //If switch not checked -> Disable alarm
                    mNotificationManager.cancel(NOTIFICATION_ID_LU);
                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntentLunch);
                        notifyPendingIntentLunch.cancel();
                    }
                    toastMessage = "Lunch reminder off";
                }

                //Toast to confirm setting
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        //--- Switch - Dinner
        switchDinner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PendingIntent notifyPendingIntentDinner = PendingIntent.getBroadcast
                        (context, NOTIFICATION_ID_DI, notifyIntentDinner, PendingIntent.FLAG_UPDATE_CURRENT);

                String toastMessage;

                if (isChecked) {
                    //If switch checked -> Turn on alarm
                    //Get timepicker time
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        dinnerHour = timePickerDinner.getCurrentHour();
                        dinnerMinute = timePickerDinner.getCurrentMinute();
                    } else {
                        dinnerHour = timePickerDinner.getHour();
                        dinnerMinute = timePickerDinner.getMinute();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, dinnerHour);
                    calendar.set(Calendar.MINUTE, dinnerMinute);

                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating
                                (AlarmManager.RTC_WAKEUP,
                                        calendar.getTimeInMillis(),
                                        AlarmManager.INTERVAL_DAY,
                                        notifyPendingIntentDinner);
                    }
                    toastMessage = "Dinner reminder on";

                    sharedPrefEditor.putInt(NOTIFICATION_DI_H, dinnerHour);
                    sharedPrefEditor.putInt(NOTIFICATION_DI_M, dinnerMinute);
                    sharedPrefEditor.apply();
                } else {
                    //If switch not checked -> Disable alarm
                    mNotificationManager.cancel(NOTIFICATION_ID_DI);
                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntentDinner);
                        notifyPendingIntentDinner.cancel();
                    }
                    toastMessage = "Dinner reminder off";
                }

                //Toast to confirm setting
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void createNotificationChannel() {
        mNotificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            //Create the NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Meal reminders",
                            NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Daily reminders of meal time");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }


    private void setupTimePickers() {
        //Timepickers shows earlier used timestamps, if existing
        breakfastHour = sharedPref.getInt(NOTIFICATION_BR_H, 8);
        breakfastMinute = sharedPref.getInt(NOTIFICATION_BR_M, 0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePickerBreakfast.setCurrentHour(breakfastHour);
            timePickerBreakfast.setCurrentMinute(breakfastMinute);
        } else {
            timePickerBreakfast.setHour(breakfastHour);
            timePickerBreakfast.setMinute(breakfastMinute);
        }

        lunchHour = sharedPref.getInt(NOTIFICATION_LU_H, 12);
        lunchMinute = sharedPref.getInt(NOTIFICATION_LU_M, 0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePickerLunch.setCurrentHour(lunchHour);
            timePickerLunch.setCurrentMinute(lunchMinute);
        } else {
            timePickerLunch.setHour(lunchHour);
            timePickerLunch.setMinute(lunchMinute);
        }

        dinnerHour = sharedPref.getInt(NOTIFICATION_DI_H, 18);
        dinnerMinute = sharedPref.getInt(NOTIFICATION_DI_M, 0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePickerDinner.setCurrentHour(dinnerHour);
            timePickerDinner.setCurrentMinute(dinnerMinute);
        } else {
            timePickerDinner.setHour(dinnerHour);
            timePickerDinner.setMinute(dinnerMinute);
        }
    }
}