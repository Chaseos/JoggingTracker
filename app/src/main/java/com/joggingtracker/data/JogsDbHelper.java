package com.joggingtracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class JogsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "jogs.db";
    private static final int DATABASE_VERSION = 1;

    public JogsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_INSECT_TABLE =
                "CREATE TABLE " + JogContract.JogEntry.TABLE_NAME + " (" +
                        JogContract.JogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        JogContract.JogEntry.COLUMN_JOG_DATE_TIME + " LONG NOT NULL," +
                        JogContract.JogEntry.COLUMN_JOG_MILES_LENGTH + " TEXT NOT NULL," +
                        JogContract.JogEntry.COLUMN_JOG_PACE + " TEXT NOT NULL," +
                        JogContract.JogEntry.COLUMN_JOG_TIME_LENGTH + " TEXT NOT NULL," +
                        JogContract.JogEntry.COLUMN_JOG_PATH_JSON + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_INSECT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
