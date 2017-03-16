package de.linuxwhatelse.android.notify.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.R;

/**
 * Created by tadly on 12/23/16.
 */

public class ThemedAppCompatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = preferences.getBoolean(Notify.PREFERENCE_KEY_DARK_THEME, true);

        if (darkTheme)
            setTheme(R.style.AppTheme_Dark);
        else
            setTheme(R.style.AppTheme);


        super.onCreate(savedInstanceState);
    }
}
