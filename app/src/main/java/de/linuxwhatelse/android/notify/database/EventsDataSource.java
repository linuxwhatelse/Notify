package de.linuxwhatelse.android.notify.database;

/**
 * Created by tadly on 12/9/14 at 7:03 PM.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EventsDataSource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {SQLiteHelper.COLUMN_EVENTS_CLIENT_ID, SQLiteHelper.COLUMN_EVENTS_EVENT_TYPE};
    public EventsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean getEventStateForClient(int clientId, EventTypes event) {
        boolean active = false;
        String[] args = {String.valueOf(clientId), event.toString()};

        Cursor cursor = database.query(SQLiteHelper.TABLE_EVENTS, allColumns, SQLiteHelper.COLUMN_EVENTS_CLIENT_ID + " = ? and " + SQLiteHelper.COLUMN_EVENTS_EVENT_TYPE + " = ?", args, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            active = true;
        }

        cursor.close();

        return active;
    }

    public void addEvent(int clientId, EventTypes eventType) {
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.COLUMN_EVENTS_CLIENT_ID, clientId);
        values.put(SQLiteHelper.COLUMN_EVENTS_EVENT_TYPE, eventType.toString());

        database.insert(SQLiteHelper.TABLE_EVENTS, null, values);
    }

    public void removeEvents(int clientId) {
        String[] args = {String.valueOf(clientId)};
        database.delete(SQLiteHelper.TABLE_EVENTS, SQLiteHelper.COLUMN_EVENTS_CLIENT_ID + " = ?", args);
    }

    public void removeEvent(int clientId, EventTypes eventType) {
        String[] args = {String.valueOf(clientId), eventType.toString()};
        database.delete(SQLiteHelper.TABLE_EVENTS, SQLiteHelper.COLUMN_EVENTS_CLIENT_ID + " = ? and " + SQLiteHelper.COLUMN_EVENTS_EVENT_TYPE + " = ? ", args);
    }

    public enum EventTypes {
        CALL
    }

}
