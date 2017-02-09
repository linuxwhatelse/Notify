package de.linuxwhatelse.android.notify.services;

/**
 * Created by tadly on 12/11/14.
 */

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.database.ClientsDataSource;
import de.linuxwhatelse.android.notify.models.Client;
import de.linuxwhatelse.android.notify.models.NotifyNotification;

public class NLService extends NotificationListenerService {
    private SharedPreferences preferences = null;

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private static boolean isGroupSummary(Notification notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH)
            return false;

        return (notification.flags & Notification.FLAG_GROUP_SUMMARY) != 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private static boolean isLocalOnly(Notification notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH)
            return false;

        return (notification.flags & Notification.FLAG_LOCAL_ONLY) != 0;
    }

    private static boolean isOngoing(Notification notification) {
        return (notification.flags & Notification.FLAG_ONGOING_EVENT) != 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

}
