package de.linuxwhatelse.android.notify.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tadly on 1/1/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    // TABLE "clients" AND IT'S COLUMNS
    public static final String TABLE_CLIENTS = "clients";
    public static final String COLUMN_CLIENTS_ID = "id";
    public static final String COLUMN_CLIENTS_NAME = "name";
    public static final String COLUMN_CLIENTS_HOST = "host";
    public static final String COLUMN_CLIENTS_PORT = "port";
    public static final String COLUMN_CLIENTS_USER = "user";
    public static final String COLUMN_CLIENTS_PWD = "pwd";
    public static final String COLUMN_CLIENTS_ALLOWED_SSID = "allowed_ssid";
    public static final String COLUMN_CLIENTS_IS_ACTIVE = "is_active";
    public static final String COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS = "overwrite_global_notifications";
    public static final String COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS = "overwrite_global_events";
    // TABLE "applications" AND IT'S COLUMNS
    public static final String TABLE_APPLICATIONS = "applications";
    public static final String COLUMN_APPLICATIONS_CLIENT_ID = "client_id";
    public static final String COLUMN_APPLICATIONS_PACKAGE_NAME = "package_name";
    public static final String COLUMN_APPLICATIONS_DISPLAY_TIME = "display_time";
    // TABLE "events" AND IT'S COLUMNS
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_EVENTS_CLIENT_ID = "client_id";
    public static final String COLUMN_EVENTS_EVENT_TYPE = "event_type";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notify";
    private static final String CREATE_TABLE_CLIENTS = "CREATE TABLE " + TABLE_CLIENTS
            + "("
            + COLUMN_CLIENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CLIENTS_NAME + " TEXT,"
            + COLUMN_CLIENTS_HOST + " TEXT,"
            + COLUMN_CLIENTS_PORT + " INTEGER,"
            + COLUMN_CLIENTS_USER + " TEXT,"
            + COLUMN_CLIENTS_PWD + " TEXT,"
            + COLUMN_CLIENTS_ALLOWED_SSID + " TEXT,"
            + COLUMN_CLIENTS_IS_ACTIVE + " BOOLEAN,"
            + COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS + " BOOLEAN,"
            + COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS + " BOOLEAN"
            + ");";
    private static final String CREATE_TABLE_APPLICATIONS = "CREATE TABLE " + TABLE_APPLICATIONS
            + "("
            + COLUMN_APPLICATIONS_CLIENT_ID + " INTEGER,"
            + COLUMN_APPLICATIONS_PACKAGE_NAME + " TEXT,"
            + COLUMN_APPLICATIONS_DISPLAY_TIME + " INTEGER"
            + ");";
    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS
            + "("
            + COLUMN_EVENTS_CLIENT_ID + " INTEGER,"
            + COLUMN_EVENTS_EVENT_TYPE + " TEXT"
            + ");";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_CLIENTS);
        database.execSQL(CREATE_TABLE_APPLICATIONS);
        database.execSQL(CREATE_TABLE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {

        }
    }
}
