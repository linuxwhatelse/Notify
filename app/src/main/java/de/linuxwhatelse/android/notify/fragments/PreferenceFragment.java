package de.linuxwhatelse.android.notify.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;

import de.linuxwhatelse.android.notify.R;

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
