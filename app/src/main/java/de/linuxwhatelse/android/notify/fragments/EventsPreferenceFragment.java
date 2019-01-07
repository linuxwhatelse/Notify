package de.linuxwhatelse.android.notify.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.database.EventsDataSource;

/**
 * Created by tadly on 12/31/14.
 */
public class EventsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final int READ_PHONE_STATE_PERMISSION_REQUEST = 1;

    AppCompatActivity activity;
    CheckBoxPreference callPreference;
    private int clientId = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (AppCompatActivity) getActivity();

        if (getArguments() != null) {
            clientId = getArguments().getInt("client_id", -1);
        }

        addPreferencesFromResource(R.xml.event_preference);

        this.callPreference = (CheckBoxPreference) findPreference("events_preference_call");
        this.callPreference.setOnPreferenceChangeListener(this);

        EventsDataSource dataSource = new EventsDataSource(getActivity());
        boolean callActive = dataSource.getEventStateForClient(clientId, EventsDataSource.EventTypes.CALL);
        dataSource.close();

        this.callPreference.setChecked(callActive);
    }

    private void handleCallPermissions() {
        int readPhoneState = ContextCompat.checkSelfPermission(this.activity,
                Manifest.permission.READ_PHONE_STATE);

        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_PERMISSION_REQUEST);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        EventsDataSource dataSource = new EventsDataSource(getActivity());

        switch (preference.getKey()) {
            case "events_preference_call":
                boolean callActive = (Boolean) newValue;
                if (callActive) {
                    handleCallPermissions();
                    dataSource.addEvent(clientId, EventsDataSource.EventTypes.CALL);
                } else {
                    dataSource.removeEvent(clientId, EventsDataSource.EventTypes.CALL);
                }
                break;
        }

        dataSource.close();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case READ_PHONE_STATE_PERMISSION_REQUEST: {
                break;
            }
        }
    }
}
