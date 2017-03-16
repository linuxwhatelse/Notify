package de.linuxwhatelse.android.notify.activities;

/**
 * Created by tadly on 1/3/15.
 */

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.fragments.QRCodeScannerFragment;

public class QRCodeScannerActivity extends ThemedAppCompatActivity {
    private final int CAMERA_PERMISSION_REQUEST = 1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.empty);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.app_name));

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new QRCodeScannerFragment());
        transaction.commit();

        // Request camera access
        int permissionState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
                break;
            }
        }
    }
}