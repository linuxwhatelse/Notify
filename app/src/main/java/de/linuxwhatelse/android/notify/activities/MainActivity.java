package de.linuxwhatelse.android.notify.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.linuxwhatelse.android.notify.Notify;
import de.linuxwhatelse.android.notify.R;
import de.linuxwhatelse.android.notify.dialogs.SnoozeDialogFragment;
import de.linuxwhatelse.android.notify.fragments.ApplicationsFragment;
import de.linuxwhatelse.android.notify.fragments.ClientsFragment;
import de.linuxwhatelse.android.notify.fragments.EventsPreferenceFragment;
import de.linuxwhatelse.android.notify.receiver.SnoozeEndReceiver;

/**
 * Created by tadly on 12/11/14.
 */

public class MainActivity extends ThemedAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BillingProcessor.IBillingHandler,
        SnoozeDialogFragment.OnSnoozeTimeSelected {

    private static final int READ_PHONE_STATE_PERMISSION_REQUEST = 1;

    MainActivity context;
    Toolbar toolbar;

    BillingProcessor billingProcessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout navdrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (navdrawer == null)
            return;

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, navdrawer, toolbar, R.string.navdrawer_open, R.string.navdrawer_close);
        navdrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navdrawer_view);

        if (navigationView == null)
            return;

        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        if (!isNotificationAccessActivated()) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.message_activate_notification_access), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.activate), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                        }
                    });
            snackbar.show();
        }

        handlePermissions();
        handleSnoozedNotification();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer == null)
            return;

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_toolbar_menu_testnotification:
                createTestNotification();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction;

        switch (item.getItemId()) {
            case R.id.navdrawer_apps:
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, new ApplicationsFragment());
                transaction.commit();

                break;

            case R.id.navdrawer_events:
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, new EventsPreferenceFragment());
                transaction.commit();

                break;

            case R.id.navdrawer_devices:
                transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, new ClientsFragment());
                transaction.commit();

                break;

            case R.id.navdrawer_snooze_notifications_only:
            case R.id.navdrawer_snooze_all_events:
                SnoozeDialogFragment dialog = new SnoozeDialogFragment();

                if (item.getItemId() == R.id.navdrawer_snooze_notifications_only) {
                    dialog.setTitle(getString(R.string.dialog_snooze_notifications_title));
                    dialog.show(getFragmentManager(), Notify.SNOOZE_NOTIFICATIONS);
                } else {
                    dialog.setTitle(getString(R.string.dialog_snooze_events_title));
                    dialog.show(getFragmentManager(), Notify.SNOOZE_ALL);
                }

                break;

            case R.id.navdrawer_donate:
                billingProcessor = new BillingProcessor(this, Notify.GOOGLE_PLAY_LICENSE_KEY, this);
                break;

            case R.id.navdrawer_about:
                startActivity(new Intent(this, AboutPreferenceActivity.class));
                break;

            case R.id.navdrawer_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer == null)
            return false;

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handlePermissions() {
        int phonePermissionState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);

        if (phonePermissionState != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_PERMISSION_REQUEST);
        }
    }

    private boolean isNotificationAccessActivated() {
        ContentResolver contentResolver = context.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = context.getPackageName();

        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
    }

    private void createTestNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(Notify.NOTIFICATION_ID_TEST);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notification_title))
                .setTicker(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_text)))
                .setSmallIcon(R.drawable.ic_textsms)
                .setPriority(NotificationCompat.PRIORITY_MIN);

        notificationManager.notify(Notify.NOTIFICATION_ID_TEST, mBuilder.build());
    }

    private void handleSnoozedNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean notificationsSnoozed = preferences.getBoolean(Notify.PREFERENCE_KEY_NOTIFICATIONS_SNOOZED, false);
        boolean eventsSnoozed = preferences.getBoolean(Notify.PREFERENCE_KEY_EVENTS_SNOOZED, false);
        long snoozeUntil = preferences.getLong(Notify.PREFERENCE_KEY_SNOOZED_UNTIL, -1);

        if (!notificationsSnoozed && !eventsSnoozed) {
            notificationManager.cancel(Notify.NOTIFICATION_ID_SNOOZE);
            return;
        }

        String title;
        if (notificationsSnoozed && eventsSnoozed) {
            title = getString(R.string.snoozed_all_title);
        } else {
            title = getString(R.string.snoozed_notifications_title);
        }

        String body;
        if (snoozeUntil != -1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(snoozeUntil);
            String day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            String time = (new SimpleDateFormat("HH:mm", getResources().getConfiguration().locale)).format(cal.getTime());

            body = getString(R.string.snoozed_text_resume_at) + " " + day + " " + time;
        } else {
            body = getString(R.string.snoozed_text_resume_indefinitely);
        }

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_notifications_paused)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(activityPendingIntent)
                .setOngoing(true);

        Intent intent = new Intent(getApplicationContext(), SnoozeEndReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_notifications_active,
                getString(R.string.snoozed_action_resume),
                pendingIntent).build();
        mBuilder.addAction(action);

        notificationManager.notify(Notify.NOTIFICATION_ID_SNOOZE, mBuilder.build());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (billingProcessor != null && !billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSnoozeTimeSelected(long snoozeUntil, String snooze) {
        handleSnoozedNotification();
    }

    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {
        billingProcessor.consumePurchase(Notify.GOOGLE_PLAY_IN_APP_DONATION_KEY);
        if (billingProcessor != null)
            billingProcessor.release();
        Toast.makeText(this, getString(R.string.message_donate_success), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
        if (billingProcessor != null)
            billingProcessor.release();
        Toast.makeText(this, getString(R.string.message_donate_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBillingInitialized() {
        billingProcessor.purchase(this, Notify.GOOGLE_PLAY_IN_APP_DONATION_KEY);
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