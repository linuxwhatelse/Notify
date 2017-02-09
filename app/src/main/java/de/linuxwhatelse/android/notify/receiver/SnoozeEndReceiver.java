package de.linuxwhatelse.android.notify.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;

import de.linuxwhatelse.android.notify.Notify;

/**
 * Created by tadly on 1/2/15.
 */
public class SnoozeEndReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, false);
        editor.putBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false);
        editor.putLong(Notify.PREFERENCE_KEY_SNOOZED_UNTIL, 0);

        editor.apply();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(Notify.NOTIFICATION_ID_SNOOZE);

        //PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE).cancel();
        //context.sendBroadcast(new Intent(Notify.SNOOZE_FINISHED_RECEIVER_KEY));
    }
}