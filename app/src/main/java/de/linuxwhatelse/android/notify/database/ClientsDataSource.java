package de.linuxwhatelse.android.notify.database;

/**
 * Created by tadly on 12/9/14 at 7:03 PM.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import de.linuxwhatelse.android.notify.models.Client;

public class ClientsDataSource {

    private Context context;

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {SQLiteHelper.COLUMN_CLIENTS_ID, SQLiteHelper.COLUMN_CLIENTS_NAME, SQLiteHelper.COLUMN_CLIENTS_HOST, SQLiteHelper.COLUMN_CLIENTS_PORT, SQLiteHelper.COLUMN_CLIENTS_USER, SQLiteHelper.COLUMN_CLIENTS_PWD, SQLiteHelper.COLUMN_CLIENTS_ALLOWED_SSID, SQLiteHelper.COLUMN_CLIENTS_IS_ACTIVE, SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS, SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS};

    public ClientsDataSource(Context context) {
        this.context = context;

        dbHelper = new SQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Client getClient(int clientId) {
        String[] args = {String.valueOf(clientId)};

        Cursor cursor = database.query(SQLiteHelper.TABLE_CLIENTS, allColumns, SQLiteHelper.COLUMN_CLIENTS_ID + " = ?", args, null, null, null);
        cursor.moveToFirst();
        Client client = cursorToClient(cursor);

        cursor.close();
        return client;
    }

    public ArrayList<Client> getAllClients() {
        ArrayList<Client> clients = new ArrayList<Client>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_CLIENTS, allColumns, null, null, null, null, SQLiteHelper.COLUMN_CLIENTS_NAME);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            clients.add(cursorToClient(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return clients;
    }

    public ArrayList<Client> getClientsToNotifyForPackage(String packageName) {
        String[] args = {packageName};

        String select = "SELECT " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_ID + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_NAME + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_HOST + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_PORT + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_USER + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_PWD + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_ALLOWED_SSID + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_IS_ACTIVE + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS +
                " FROM " +
                SQLiteHelper.TABLE_CLIENTS + " LEFT JOIN " + SQLiteHelper.TABLE_APPLICATIONS +
                " ON (" +
                "(" + SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS + " == 1 AND " + SQLiteHelper.TABLE_APPLICATIONS + "." + SQLiteHelper.COLUMN_APPLICATIONS_CLIENT_ID + " == " + SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_ID +
                ") OR (" +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS + " == 0 AND " + SQLiteHelper.TABLE_APPLICATIONS + "." + SQLiteHelper.COLUMN_APPLICATIONS_CLIENT_ID + " == -1))" +
                " WHERE " + SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_IS_ACTIVE + " == 1 AND " + SQLiteHelper.TABLE_APPLICATIONS + "." + SQLiteHelper.COLUMN_APPLICATIONS_PACKAGE_NAME + " = ?";

        Cursor cursor = database.rawQuery(select, args);
        cursor.moveToFirst();

        ArrayList<Client> clients = new ArrayList<Client>();
        while (!cursor.isAfterLast()) {
            clients.add(cursorToClient(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return clients;
    }

    public ArrayList<Client> getClientsToNotifyForEvent(EventsDataSource.EventTypes eventType) {
        String[] args = {eventType.toString()};

        String select = "SELECT " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_ID + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_NAME + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_HOST + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_PORT + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_USER + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_PWD + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_ALLOWED_SSID + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_IS_ACTIVE + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS + ", " +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS +
                " FROM " +
                SQLiteHelper.TABLE_CLIENTS + " LEFT JOIN " + SQLiteHelper.TABLE_EVENTS +
                " ON (" +
                "(" + SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS + " == 1  AND " + SQLiteHelper.TABLE_EVENTS + "." + SQLiteHelper.COLUMN_EVENTS_CLIENT_ID + " == " + SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_ID +
                ") OR (" +
                SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS + " == 0  AND " + SQLiteHelper.TABLE_EVENTS + "." + SQLiteHelper.COLUMN_EVENTS_CLIENT_ID + " == -1))" +
                " WHERE " + SQLiteHelper.TABLE_CLIENTS + "." + SQLiteHelper.COLUMN_CLIENTS_IS_ACTIVE + " == 1 AND " + SQLiteHelper.TABLE_EVENTS + "." + SQLiteHelper.COLUMN_EVENTS_EVENT_TYPE + " = ?";

        Cursor cursor = database.rawQuery(select, args);
        cursor.moveToFirst();

        ArrayList<Client> clients = new ArrayList<Client>();
        while (!cursor.isAfterLast()) {
            clients.add(cursorToClient(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return clients;
    }

    public int addClient(Client client) {
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.COLUMN_CLIENTS_NAME, client.getName());
        values.put(SQLiteHelper.COLUMN_CLIENTS_HOST, client.getHost());
        values.put(SQLiteHelper.COLUMN_CLIENTS_PORT, client.getPort());
        values.put(SQLiteHelper.COLUMN_CLIENTS_USER, client.getUser());
        values.put(SQLiteHelper.COLUMN_CLIENTS_PWD, client.getPwd());
        values.put(SQLiteHelper.COLUMN_CLIENTS_ALLOWED_SSID, client.getAllowedSSID());
        values.put(SQLiteHelper.COLUMN_CLIENTS_IS_ACTIVE, client.isActive());
        values.put(SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS, client.isOverwriteGlobalNotifications());
        values.put(SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS, client.isOverwriteGlobalEvents());

        return (int) database.insert(SQLiteHelper.TABLE_CLIENTS, null, values);
    }

    public void updateClient(Client client) {
        String[] args = {String.valueOf(client.getId())};
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.COLUMN_CLIENTS_NAME, client.getName());
        values.put(SQLiteHelper.COLUMN_CLIENTS_HOST, client.getHost());
        values.put(SQLiteHelper.COLUMN_CLIENTS_PORT, client.getPort());
        values.put(SQLiteHelper.COLUMN_CLIENTS_USER, client.getUser());
        values.put(SQLiteHelper.COLUMN_CLIENTS_PWD, client.getPwd());
        values.put(SQLiteHelper.COLUMN_CLIENTS_ALLOWED_SSID, client.getAllowedSSID());
        values.put(SQLiteHelper.COLUMN_CLIENTS_IS_ACTIVE, client.isActive());
        values.put(SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_NOTIFICATIONS, client.isOverwriteGlobalNotifications());
        values.put(SQLiteHelper.COLUMN_CLIENTS_OVERWRITE_GLOBAL_EVENTS, client.isOverwriteGlobalEvents());

        database.update(SQLiteHelper.TABLE_CLIENTS, values, SQLiteHelper.COLUMN_CLIENTS_ID + " = ?", args);
    }

    public void removeClient(int id) {
        String[] args = {String.valueOf(id)};

        ApplicationsDataSource dataSource1 = new ApplicationsDataSource(context);
        dataSource1.removeApplications(id);
        dataSource1.close();

        EventsDataSource dataSource2 = new EventsDataSource(context);
        dataSource2.removeEvents(id);
        dataSource2.close();

        database.delete(SQLiteHelper.TABLE_CLIENTS, SQLiteHelper.COLUMN_CLIENTS_ID + " = ?", args);
    }

    private Client cursorToClient(Cursor cursor) {
        Client client = new Client();

        client.setId(cursor.getInt(0));
        client.setName(cursor.getString(1));
        client.setHost(cursor.getString(2));
        client.setPort(cursor.getInt(3));
        client.setUser(cursor.getString(4));
        client.setPwd(cursor.getString(5));
        client.setAllowedSSID(cursor.getString(6));
        client.setActive(cursor.getInt(7) == 1);
        client.setOverwriteGlobalNotifications(cursor.getInt(8) == 1);
        client.setOverwriteGlobalEvents(cursor.getInt(9) == 1);

        return client;
    }
}
