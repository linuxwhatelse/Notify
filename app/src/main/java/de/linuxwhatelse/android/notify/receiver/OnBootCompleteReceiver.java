package de.linuxwhatelse.android.notify.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.linuxwhatelse.android.notify.Notify;

/**
 * Created by tadly on 1/2/15.
 */
public class OnBootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, false);
        editor.putBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false);

        editor.apply();
    }

}
