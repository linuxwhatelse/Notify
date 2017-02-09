package de.linuxwhatelse.android.notify.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.R;

/**
 * Created by tadly on 12/31/14.
 */
public class AboutPreferenceFragment extends PreferenceFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preference);

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            findPreference("about_version").setSummary(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getKey()) {
            case "about_licenses":
                startActivity(new Intent(Intent.ACTION_VIEW, Notify.URI_LINUXWHATELSE_NOTIFY_LICENSES));
                break;
            case "about_google_plus":
                startActivity(new Intent(Intent.ACTION_VIEW, Notify.URI_LINUXWHATELSE_GOOGLE_PLUS));
                break;
            case "about_github":
                startActivity(new Intent(Intent.ACTION_VIEW, Notify.URI_LINUXWHATELSE_GITHUB));
                break;
            case "about_homepage":
                startActivity(new Intent(Intent.ACTION_VIEW, Notify.URI_LINUXWHATELSE_HOMEPAGE));
                break;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
