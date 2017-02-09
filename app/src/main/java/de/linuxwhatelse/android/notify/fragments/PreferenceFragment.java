package de.linuxwhatelse.android.notify.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;
import android.widget.Toast;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.database.EventsDataSource;

/**
 * Created by tadly on 12/31/14.
 */
public class PreferenceFragment extends android.preference.PreferenceFragment implements Preference.OnPreferenceChangeListener {
    AppCompatActivity activity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (AppCompatActivity) getActivity();

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }
}
