package de.linuxwhatelse.android.notify.database;

/**
 * Created by tadly on 12/9/14 at 7:03 PM.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import de.linuxwhatelse.android.notify.Notify;

public class ApplicationsDataSource {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {SQLiteHelper.COLUMN_APPLICATIONS_CLIENT_ID, SQLiteHelper.COLUMN_APPLICATIONS_PACKAGE_NAME, SQLiteHelper.COLUMN_APPLICATIONS_DISPLAY_TIME};

    public ApplicationsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<String> getAllPackageNamesForClient(int clientId) {
        String[] args = {String.valueOf(clientId)};

        ArrayList<String> packages = new ArrayList<String>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_APPLICATIONS, allColumns, SQLiteHelper.COLUMN_APPLICATIONS_CLIENT_ID + " = ? ", args, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            packages.add(cursor.getString(1));
            cursor.moveToNext();
        }

        cursor.close();
        return packages;
    }

    public void addApplication(int clientId, String packageName) {
        addApplication(clientId, packageName, Notify.DEFAULT_DISPLAY_TIME);
    }

    public void addApplication(int clientId, String packageName, int displayTime) {
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.COLUMN_APPLICATIONS_CLIENT_ID, clientId);
        values.put(SQLiteHelper.COLUMN_APPLICATIONS_PACKAGE_NAME, packageName);
        values.put(SQLiteHelper.COLUMN_APPLICATIONS_DISPLAY_TIME, displayTime);

        database.insert(SQLiteHelper.TABLE_APPLICATIONS, null, values);
    }

    public void removeApplications(int clientId) {
        String[] args = {String.valueOf(clientId)};
        database.delete(SQLiteHelper.TABLE_APPLICATIONS, SQLiteHelper.COLUMN_APPLICATIONS_CLIENT_ID + " = ?", args);
    }

    public void removeApplication(int clientId, String packageName) {
        String[] args = {String.valueOf(clientId), packageName};
        database.delete(SQLiteHelper.TABLE_APPLICATIONS, SQLiteHelper.COLUMN_APPLICATIONS_CLIENT_ID + " = ? AND " + SQLiteHelper.COLUMN_APPLICATIONS_PACKAGE_NAME + " = ? ", args);
    }

}
