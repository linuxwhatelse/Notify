package de.linuxwhatelse.android.notify.services;

/**
 * Created by tadly on 12/11/14.
 */

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.activities.MainActivity;
import de.linuxwhatelse.android.notify.database.ClientsDataSource;
import de.linuxwhatelse.android.notify.models.Client;
import de.linuxwhatelse.android.notify.models.NotifyNotification;

public class NLService extends NotificationListenerService {
    private static int FOREGROUND_NOTIFICATION_ID = 1;

    private SharedPreferences preferences = null;

    private static boolean isOngoing(Notification notification) {
        return (notification.flags & Notification.FLAG_ONGOING_EVENT) != 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        boolean foreground = preferences.getBoolean(Notify.PREFERENCE_KEY_FOREGROUND, false);
        showForegroundNotification(foreground);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        NotifyNotification noti = new NotifyNotification(getApplicationContext(), sbn);

        if (isOngoing(sbn.getNotification()))
            return;

        if (preferences.getBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, false))
            return;

        ArrayList<Client> clients = getClientsToNotify(sbn.getPackageName());
        if (clients.size() <= 0)
            return;

        Publisher.send(getApplicationContext(), clients, Notify.PATH_NOTIFICATION_POSTED, noti.getAsJSON());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (preferences.getBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, false))
            return;

        ArrayList<Client> clients = getClientsToNotify(sbn.getPackageName());
        if (clients.size() <= 0)
            return;

        NotifyNotification noti = new NotifyNotification(getApplicationContext(), sbn);
        Publisher.send(getApplicationContext(), clients, Notify.PATH_NOTIFICATION_REMOVED, noti.getAsJSON());
    }

    private ArrayList<Client> getClientsToNotify(String packageName) {
        ClientsDataSource dataSource = new ClientsDataSource(getApplicationContext());
        ArrayList<Client> clients = dataSource.getClientsToNotifyForPackage(packageName);
        dataSource.close();

        return clients;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case Notify.PREFERENCE_KEY_FOREGROUND:
                    boolean foreground = sharedPreferences.getBoolean(Notify.PREFERENCE_KEY_FOREGROUND, false);
                    showForegroundNotification(foreground);
                    break;
            }
        }
    };

    private void showForegroundNotification(boolean show) {
        if (!show) {
            stopForeground(true);
            return;
        }

        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_running))
                .setSmallIcon(R.drawable.ic_textsms)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(contentIntent)
                .setOngoing(true);

        startForeground(FOREGROUND_NOTIFICATION_ID, mBuilder.build());
    }

}
