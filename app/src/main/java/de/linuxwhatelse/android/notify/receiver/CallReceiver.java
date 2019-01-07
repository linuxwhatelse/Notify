package de.linuxwhatelse.android.notify.receiver;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.database.ClientsDataSource;
import de.linuxwhatelse.android.notify.database.EventsDataSource;
import de.linuxwhatelse.android.notify.models.Client;
import de.linuxwhatelse.android.notify.services.Publisher;

/**
 * Created by tadly on 12/31/14.
 */
public class CallReceiver extends PhoneCallReceiver {

    protected void onIncomingCallStarted(Context context, String number, Date start) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false)) {
            ArrayList<Client> clients = getClientsToNotify(context);

            if (clients.size() > 0) {
                JSONObject data = new JSONObject();
                try {
                    data.put("direction", "incoming");
                    data.put("number", number);
                    data.put("start", start.getTime());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Publisher.send(context, clients, Notify.PATH_CALL_STARTED, data);
            }
        }
    }

    protected void onOutgoingCallStarted(Context context, String number, Date start) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false)) {
            ArrayList<Client> clients = getClientsToNotify(context);

            if (clients.size() > 0) {
                JSONObject data = new JSONObject();
                try {
                    data.put("direction", "outgoing");
                    data.put("number", number);
                    data.put("start", start.getTime());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Publisher.send(context, clients, Notify.PATH_CALL_STARTED, data);
            }
        }
    }

    protected void onIncomingCallEnded(Context context, String number, Date start, Date end) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false)) {
            ArrayList<Client> clients = getClientsToNotify(context);

            if (clients.size() > 0) {
                JSONObject data = new JSONObject();
                try {

                    data.put("direction", "incoming");
                    data.put("number", number);
                    data.put("start", start.getTime());
                    data.put("end", end.getTime());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Publisher.send(context, clients, Notify.PATH_CALL_ENDED, data);
            }
        }
    }

    protected void onOutgoingCallEnded(Context context, String number, Date start, Date end) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false)) {
            ArrayList<Client> clients = getClientsToNotify(context);

            if (clients.size() > 0) {
                JSONObject data = new JSONObject();
                try {
                    data.put("direction", "outgoing");
                    data.put("number", number);
                    data.put("start", start.getTime());
                    data.put("end", end.getTime());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Publisher.send(context, clients, Notify.PATH_CALL_ENDED, data);
            }
        }
    }

    protected void onMissedCall(Context context, String number, Date start) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false)) {
            ArrayList<Client> clients = getClientsToNotify(context);

            if (clients.size() > 0) {
                JSONObject data = new JSONObject();
                try {
                    data.put("number", number);
                    data.put("start", start.getTime());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Publisher.send(context, clients, Notify.PATH_CALL_MISSED, data);
            }
        }
    }

    private ArrayList<Client> getClientsToNotify(Context context) {
        ClientsDataSource dataSource = new ClientsDataSource(context);
        ArrayList<Client> clients = dataSource.getClientsToNotifyForEvent(EventsDataSource.EventTypes.CALL);
        dataSource.close();

        return clients;
    }

}
