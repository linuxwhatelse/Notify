package de.linuxwhatelse.android.notify;

import android.net.Uri;

/**
 * Created by tadly on 12/23/14.
 */
public class Notify {
    public final static int NOTIFICATION_ID_TEST = 1337;
    public final static int NOTIFICATION_ID_SNOOZE = 1338;

    public final static String SNOOZE_NOTIFICATIONS = "snooze_notifications";
    public final static String SNOOZE_ALL = "snooze_all";

    public final static String GOOGLE_PLAY_LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArw2u3esMHiPOHsVWn7U1KAF/Fllep99WPsO5nhHVzZn2PKLq4BCHb46dOPU5t4QO3H7IDU0K+6C20D/onJw0JcDghz/8B6zmRVLNLfY2WkcGhUvfOambkMFX7Q7GPhuN2SgRkRY1uAtx6xzQZ+DCYIvd2WXwArEi0/4YzOV14yYW4rDDrfzLMSo9PgOM1M/hTevbnge2YyGY6CUBcbGw38hpoPsX2xdDDyOICbuRcKjmtPSbrwHVALGcGBRTQ1RYsKkzdDVsjbOcAlzF/43THPpOHhhrhd9JgrU9uSOi4xg11LHmE9TucJHcAtxa31UjNHgLyxKF7VqcrZ/xjKxNRQIDAQAB";
    public final static String GOOGLE_PLAY_IN_APP_DONATION_KEY = "de.linuxwhatelse.android.notify";

    public final static int DEFAULT_PORT = 8022;
    public final static int DEFAULT_DISPLAY_TIME = 5000;

    public final static String PATH_NOTIFICATION_POSTED = "/notification/posted";
    public final static String PATH_NOTIFICATION_REMOVED = "/notification/removed";

    public final static String PATH_CALL_STARTED = "/call/started";
    public final static String PATH_CALL_ENDED = "/call/ended";
    public final static String PATH_CALL_MISSED = "/call/missed";


    // SharedPreferences-Keys
    public final static String PREFERENCE_KEY_NOTIFICATIONS_SNOOZED = "notifications_snoozed";
    public final static String PREFERENCE_KEY_EVENTS_SNOOZED = "events_snoozed";
    public final static String PREFERENCE_KEY_SNOOZED_UNTIL = "snoozed_until";


    public final static Uri URI_LINUXWHATELSE_NOTIFY_LICENSES = Uri.parse("http://linuxwhatelse.de/?page_id=2120");
    public final static Uri URI_LINUXWHATELSE_GOOGLE_PLUS = Uri.parse("https://plus.google.com/communities/116226315734023023610");
    public final static Uri URI_LINUXWHATELSE_GITHUB = Uri.parse("https://github.com/linuxwhatelse");
    public final static Uri URI_LINUXWHATELSE_HOMEPAGE = Uri.parse("http://linuxwhatelse.de");

}
