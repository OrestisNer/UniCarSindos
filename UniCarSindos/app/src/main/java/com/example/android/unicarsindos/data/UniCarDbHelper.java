package com.example.android.unicarsindos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.unicarsindos.data.UniCarContract.UniCarEntry;

/**
 * Created by User on 19-Dec-17.
 */

public class UniCarDbHelper extends SQLiteOpenHelper  {

    private static final String DATABASE_NAME = "usersDb.db";
    private static final int VERSION = 15;


    public UniCarDbHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create tasks table (careful to follow SQL formatting rules)
        final String CREATE_TABLE = "CREATE TABLE "  + UniCarEntry.TABLE_NAME + " (" +
                UniCarEntry._ID                  +" INTEGER PRIMARY KEY, "    +
                UniCarEntry.COLUMN_FIRST_NAME    +" TEXT NOT NULL, "          +
                UniCarEntry.COLUMN_SECOND_NAME   +" TEXT NOT NULL, "          +
                UniCarEntry.COLUMN_PHONE         +" TEXT NOT NULL, "          +
                UniCarEntry.COLUMN_EMAIL         +" TEXT NOT NULL, "          +
                UniCarEntry.COLUMN_PASSWORD      +" TEXT NOT NULL, "          +
                UniCarEntry.COLUMN_HAS_PROFILE   +" INTEGER DEFAULT 0, "      +
                UniCarEntry.COLUMN_OFFER_RIDE    +" INTEGER DEFAULT 0, "      +
                UniCarEntry.COLUMN_ADDRESS       +" TEXT, "                   +
                UniCarEntry.COLUMN_DAYS_HOURS    +" TEXT, "                   +
                UniCarEntry.COLUMN_INFORMATION   +" TEXT, "                   +
                UniCarEntry.COLUMN_AREA          +" TEXT, "                   +
                UniCarEntry.COLUMN_LATITUDE      +" REAL, "                   +
                UniCarEntry.COLUMN_LONGITUDE     +" REAL"                     +");";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UniCarEntry.TABLE_NAME);
        onCreate(db);
    }
}
