package de.linuxwhatelse.android.notify.services;

/**
 * Created by tadly on 12/11/14.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.activities.MainActivity;
import de.linuxwhatelse.android.notify.database.ClientsDataSource;
import de.linuxwhatelse.android.notify.models.Client;
import de.linuxwhatelse.android.notify.models.NotifyNotification;

public class NLService extends NotificationListenerService {
    private static int FOREGROUND_NOTIFICATION_ID = 1;
    private static String WIFI_LOCK_TAG = "NOTIFY";

    private Context context;

    private WifiManager.WifiLock wifiLock = null;
    private SharedPreferences preferences = null;

    private static boolean isOngoing(Notification notification) {
        return (notification.flags & Notification.FLAG_ONGOING_EVENT) != 0;
    }

    private static boolean isGroupSummary(Notification notification) {
        return (notification.flags & Notification.FLAG_GROUP_SUMMARY) != 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        handleForegroundNotification();
        handleWifiLock();
    }

    @Override
    public void onDestroy() {
        this.preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);

        releaseWifiLock();

        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (isOngoing(sbn.getNotification()))
            return;

        if (isGroupSummary(sbn.getNotification()))
            return;

        if (preferences.getBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, false))
            return;

        ArrayList<Client> clients = getClientsToNotify(sbn.getPackageName());
        if (clients.size() <= 0)
            return;

        NotifyNotification noti = new NotifyNotification(context, sbn);
        if (! noti.hastTitleAndText()) {
            return;
        }

        Publisher.send(context, clients, Notify.PATH_NOTIFICATION_POSTED, noti.getAsJSON());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (preferences.getBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, false))
            return;

        ArrayList<Client> clients = getClientsToNotify(sbn.getPackageName());
        if (clients.size() <= 0)
            return;

        NotifyNotification noti = new NotifyNotification(context, sbn);
        Publisher.send(context, clients, Notify.PATH_NOTIFICATION_REMOVED, noti.getAsJSON());
    }

    private ArrayList<Client> getClientsToNotify(String packageName) {
        ClientsDataSource dataSource = new ClientsDataSource(context);
        ArrayList<Client> clients = dataSource.getClientsToNotifyForPackage(packageName);
        dataSource.close();

        return clients;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case Notify.PREFERENCE_KEY_FOREGROUND:
                    handleForegroundNotification();
                    break;
                case Notify.PREFERENCE_KEY_WIFI_LOCK:
                    handleWifiLock();
                    break;
            }
        }
    };

    private void handleForegroundNotification() {
        boolean show = preferences.getBoolean(Notify.PREFERENCE_KEY_FOREGROUND, false);

        if (!show) {
            stopForeground(true);
            return;
        }

        Intent showTaskIntent = new Intent(context, MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
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

    private void handleWifiLock() {
        boolean wifiLock = preferences.getBoolean(Notify.PREFERENCE_KEY_WIFI_LOCK, false);
        if (wifiLock) {
            holdWifiLock();
        } else {
            releaseWifiLock();
        }
    }

    private void holdWifiLock() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        if (wifiLock == null) {
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK_TAG);
        }

        wifiLock.setReferenceCounted(false);

        if (! wifiLock.isHeld()) {
            wifiLock.acquire();
        }
    }

    private void releaseWifiLock() {
        if (wifiLock == null)
            return;

        if (wifiLock.isHeld()) {
            wifiLock.release();
            wifiLock= null;
        }
    }

}