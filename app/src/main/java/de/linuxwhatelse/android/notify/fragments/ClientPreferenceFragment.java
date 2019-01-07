package de.linuxwhatelse.android.notify.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.activities.ApplicationsActivity;
import de.linuxwhatelse.android.notify.activities.EventsPreferenceActivity;
import de.linuxwhatelse.android.notify.database.ClientsDataSource;
import de.linuxwhatelse.android.notify.models.Client;

/**
 * Created by tadly on 12/31/14.
 */
public class ClientPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private int clientId = -1;
    AppCompatActivity activity;

    SwitchPreference enabled;
    EditTextPreference name;
    EditTextPreference host;
    EditTextPreference port;
    EditTextPreference user;
    EditTextPreference pwd;
    EditTextPreference ssid;
    CheckBoxPreference overwriteGlobalNotifications;
    CheckBoxPreference overwriteGlobalEvents;

    private Client client = new Client();
    private boolean is_new_client = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.activity = (AppCompatActivity) getActivity();
        this.activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getArguments() != null) {
            this.clientId = getArguments().getInt("client_id", -1);
            this.is_new_client = getArguments().getBoolean("is_new_client", false);
        }

        addPreferencesFromResource(R.xml.client_preference);

        this.enabled = (SwitchPreference) findPreference("client_preference_enabled");
        this.name = (EditTextPreference) findPreference("client_preference_name");
        this.host = (EditTextPreference) findPreference("client_preference_host");
        this.port = (EditTextPreference) findPreference("client_preference_port");
        this.user = (EditTextPreference) findPreference("client_preference_user");
        this.pwd = (EditTextPreference) findPreference("client_preference_pwd");
        this.ssid = (EditTextPreference) findPreference("client_preference_ssid");
        this.overwriteGlobalNotifications = (CheckBoxPreference) findPreference("client_preference_notification_overwrite_global");
        this.overwriteGlobalEvents = (CheckBoxPreference) findPreference("client_preference_event_overwrite_global");

        this.name.setOnPreferenceChangeListener(this);
        this.host.setOnPreferenceChangeListener(this);
        this.port.setOnPreferenceChangeListener(this);
        this.user.setOnPreferenceChangeListener(this);
        this.pwd.setOnPreferenceChangeListener(this);
        this.ssid.setOnPreferenceChangeListener(this);

        this.port.setDefaultValue(Notify.DEFAULT_PORT);

        if (clientId != -1) {
            ClientsDataSource dataSource = new ClientsDataSource(getActivity());
            this.client = dataSource.getClient(clientId);
            dataSource.close();

            if (!client.getName().equals("")) {
                this.activity.getSupportActionBar().setTitle(client.getName());
            } else if (!client.getHost().equals("")) {
                this.activity.getSupportActionBar().setTitle(client.getHost());
            } else {
                this.activity.getSupportActionBar().setTitle(getString(R.string.client_new_title));
            }

            this.enabled.setChecked(client.isActive());

            this.name.setText(client.getName());
            if (!client.getName().equals(""))
                this.name.setSummary(client.getName());

            this.host.setText(client.getHost());
            if (!client.getHost().equals(""))
                this.host.setSummary(client.getHost());

            this.port.setText(String.valueOf(client.getPort()));
            this.port.setSummary(String.valueOf(client.getPort()));

            this.user.setText(client.getUser());
            if (!client.getUser().equals(""))
                this.user.setSummary(client.getUser());

            this.pwd.setText(client.getPwd());
            if (!client.getPwd().equals(""))
                this.pwd.setSummary(pwd.getEditText().getTransformationMethod().getTransformation(client.getPwd(), pwd.getEditText()));

            this.ssid.setText(client.getAllowedSSID());
            if (!client.getAllowedSSID().equals(""))
                this.ssid.setSummary(client.getAllowedSSID());

            this.overwriteGlobalNotifications.setChecked(client.isOverwriteGlobalNotifications());
            this.overwriteGlobalEvents.setChecked(client.isOverwriteGlobalEvents());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.client_preference_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.client_preference_toolbar_menu_delete:
                if (clientId != -1) {
                    ClientsDataSource dataSource = new ClientsDataSource(getActivity());
                    dataSource.removeClient(clientId);
                    dataSource.close();
                }
                this.activity.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "client_preference_notification_notifications":
                Intent appIntent = new Intent(getActivity(), ApplicationsActivity.class);
                appIntent.putExtra("client_id", clientId);
                startActivity(appIntent);
                break;

            case "client_preference_event_events":
                Intent eventsIntent = new Intent(getActivity(), EventsPreferenceActivity.class);
                eventsIntent.putExtra("client_id", clientId);
                startActivity(eventsIntent);
                break;
            case "client_preference_ssid":
                // Request location access
                int permissionState = ContextCompat.checkSelfPermission(this.activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

                if (permissionState != PackageManager.PERMISSION_GRANTED) {
                    new AlertDialog.Builder(this.activity)
                            .setTitle(getString(R.string.dialog_permission_title))
                            .setMessage(getString(R.string.dialog_permission_location_msg))
                            .setPositiveButton(getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                                }
                            })
                            .show();
                }
                break;
        }

        return true;
    }

    private void saveClient() {
        client.setName(name.getText());
        client.setHost(host.getText());
        if (port.getText() != null) {
            client.setPort(Integer.valueOf(port.getText()));
        }
        client.setUser(user.getText());
        client.setPwd(pwd.getText());
        client.setAllowedSSID(ssid.getText());
        client.setActive(enabled.isChecked());
        client.setOverwriteGlobalNotifications(overwriteGlobalNotifications.isChecked());
        client.setOverwriteGlobalEvents(overwriteGlobalEvents.isChecked());

        ClientsDataSource dataSource = new ClientsDataSource(this.activity);
        if (this.clientId == -1) {
            clientId = dataSource.addClient(client);
        } else {
            client.setId(clientId);
            dataSource.updateClient(client);
        }
        dataSource.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onBackPressed();
    }

    public void onBackPressed() {
        if (is_new_client || host.getText().equals("")) {
            ClientsDataSource dataSource = new ClientsDataSource(getActivity());
            dataSource.removeClient(clientId);
            dataSource.close();
        } else {
            saveClient();
            this.activity.setResult(ClientsFragment.REFRESH_CLIENTS_LIST);
        }

        this.activity.finish();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        is_new_client = false;

        if (preference == pwd) {
            pwd.setSummary(pwd.getEditText().getTransformationMethod().getTransformation(newValue.toString(), pwd.getEditText()));
        } else {
            if (preference == name)
                this.activity.getSupportActionBar().setTitle(newValue.toString());

            preference.setSummary(newValue.toString());
        }

        return true;
    }

}
