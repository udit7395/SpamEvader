package com.poseidon.spamevader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by udit on 5/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "spam.db";
    private final String TAG = DatabaseHelper.class.getName();
    private static DatabaseHelper instance = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance != null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static final class TableSpam {

        private static final String TABLE_NAME = "spam";

        private static final String _ID = "_id";
        private static final String NUMBER = "number";

        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NUMBER + " TEXT)";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableSpam.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableSpam.TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<String> getAllSpamNumbers() {
        ArrayList<String> spamNumbers = new ArrayList<>();

        Cursor cursor = getWritableDatabase().rawQuery("SELECT " + TableSpam.NUMBER + " FROM " + TableSpam.TABLE_NAME +
                " WHERE " + TableSpam.NUMBER, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                spamNumbers.add(cursor.getString(cursor.getColumnIndex(TableSpam.NUMBER)));
            }

            cursor.close();
        }

        return spamNumbers;
    }

    public void addSpamNumber(String spamNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableSpam.NUMBER, spamNumber);
        getWritableDatabase().insert(TableSpam.TABLE_NAME, null, contentValues);
    }

    public void deleteAllSpamNumbers() {
        getWritableDatabase().delete(TableSpam.TABLE_NAME, null, null);
    }

    private int getStart_Id() {
        ArrayList<Integer> _Ids = new ArrayList<>();
        Cursor cursor = getWritableDatabase().rawQuery("SELECT " + TableSpam._ID + " FROM " + TableSpam.TABLE_NAME, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                _Ids.add(cursor.getInt(cursor.getColumnIndex(TableSpam._ID)));
            }

            cursor.close();
        }

        return _Ids.get(0);
    }

    public boolean doesUserInputExistsInDB(String userInput){
        boolean doesUserInputExistsInDB = false;

        String selection = TableSpam.NUMBER + "='" + userInput + "'";

        Cursor cursor = getWritableDatabase().query(TableSpam.TABLE_NAME, null, selection,
                null, null, null, null);

        if (cursor != null) {
            doesUserInputExistsInDB = cursor.getCount() != 0;

            cursor.close();
        }

        return doesUserInputExistsInDB;
    }

    public void deleteSingle(String spamNumber) {
        String selection = TableSpam.NUMBER + " = '" + spamNumber + "'";
        int status = getWritableDatabase().delete(TableSpam.TABLE_NAME, selection, null);
        Log.d(TAG, "Status : " + status);
    }

}
