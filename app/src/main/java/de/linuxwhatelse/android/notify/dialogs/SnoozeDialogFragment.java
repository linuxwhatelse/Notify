package de.linuxwhatelse.android.notify.dialogs;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Calendar;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.receiver.SnoozeEndReceiver;

/**
 * Created by tadly on 1/2/15.
 */
public class SnoozeDialogFragment extends DialogFragment implements ListView.OnItemClickListener {
    Activity activity;

    String title;
    OnSnoozeTimeSelected mListener;

    public void setTitle(String title) {
        this.title = title;

        Dialog diag = getDialog();
        if (diag != null) {
            diag.setTitle(title);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSnoozeTimeSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTimeDialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = getActivity();
        getDialog().setTitle(title);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this.activity,
                R.array.snooze_dialog_data_text,
                android.R.layout.simple_list_item_1);

        View view = inflater.inflate(R.layout.snooze_dialog, container, false);
        ListView listView = view.findViewById(R.id.snooze_dialog_listview);

        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String snoozeWhat = getTag();

        int snoozeFor = getResources().getIntArray(R.array.snooze_dialog_data_values)[position];

        long snoozeUntilRealTime = -1;
        long snoozeUntil = -1;
        if (snoozeFor != -1) {
            snoozeUntilRealTime = SystemClock.elapsedRealtime() + snoozeFor * 3600000;
            snoozeUntil = Calendar.getInstance().getTimeInMillis() + snoozeFor * 3600000;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.activity);
        SharedPreferences.Editor editor = preferences.edit();
        if (snoozeWhat.equals(Notify.SNOOZE_ALL)) {
            editor.putBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, true);
            editor.putBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, true);
        } else if (snoozeWhat.equals(Notify.SNOOZE_NOTIFICATIONS)) {
            editor.putBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, true);
            editor.putBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false);
        }
        editor.putLong(Notify.PREFERENCE_KEY_SNOOZED_UNTIL, snoozeUntil);
        editor.apply();

        PendingIntent pendingIntent;
        if (snoozeFor != -1) {
            AlarmManager alarmManager = (AlarmManager) this.activity.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this.activity, SnoozeEndReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this.activity, 0, intent, 0);

            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, snoozeUntilRealTime, pendingIntent);
        }

        mListener.onSnoozeTimeSelected(snoozeUntil, snoozeWhat);

        this.dismiss();
    }

    public interface OnSnoozeTimeSelected {
        void onSnoozeTimeSelected(long snoozeUntil, String snooze);
    }

}
