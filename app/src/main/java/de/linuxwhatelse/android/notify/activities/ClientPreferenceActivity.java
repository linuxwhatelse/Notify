package de.linuxwhatelse.android.notify.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.fragments.ClientPreferenceFragment;

/**
 * Created by tadly on 12/31/14.
 */
public class ClientPreferenceActivity extends ThemedAppCompatActivity {
    ClientPreferenceFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.empty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.client_new_title));

        fragment = new ClientPreferenceFragment();
        fragment.setArguments(getIntent().getExtras());

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        fragment.onBackPressed();
        super.onBackPressed();
    }
}