package de.linuxwhatelse.android.notify.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.fragments.PreferenceFragment;

/**
 * Created by tadly on 12/31/14.
 */
public class PreferenceActivity extends ThemedAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.empty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.navdrawer_settings));


        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new PreferenceFragment());
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}