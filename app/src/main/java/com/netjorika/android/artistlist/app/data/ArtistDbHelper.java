package com.netjorika.android.artistlist.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.netjorika.android.artistlist.app.data.ArtistContract.ArtistEntry;
import com.netjorika.android.artistlist.app.data.ArtistContract.ArtistGenreEntry;
import com.netjorika.android.artistlist.app.data.ArtistContract.GenreEntry;

/**
 * Manages a local database for weather data.
 */
public class ArtistDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "artistlist.db";

    public ArtistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Artist table
        final String SQL_CREATE_ARTIST_TABLE = "CREATE TABLE " + ArtistEntry.TABLE_NAME + " (" +
                ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArtistEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                /*may be need add UNIQUE*/
                ArtistEntry.COLUMN_YANDEX_ID + " INTEGER UNIQUE NOT NULL , " +
                ArtistEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ArtistEntry.COLUMN_LINK + " TEXT," +

                ArtistEntry.COLUMN_ALBUMS + " INTEGER NOT NULL, " +
                ArtistEntry.COLUMN_TRACKS + " INTEGER NOT NULL, " +

                ArtistEntry.COLUMN_COVER_SMALL + " REAL NOT NULL, " +
                ArtistEntry.COLUMN_COVER_BIG + " REAL NOT NULL " +
                ");";

        //genres Table
        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + GenreEntry.TABLE_NAME + " (" +
                GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GenreEntry.COLUMN_GENRE + " TEXT UNIQUE NOT NULL " +
                " );";

        //intersection table
        final String SQL_CREATE_ARTIST_GENRE_TABLE = "CREATE TABLE " + ArtistGenreEntry.TABLE_NAME + " (" +
                ArtistGenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArtistGenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, " +
                ArtistGenreEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + ArtistGenreEntry.COLUMN_ARTIST_ID + ") REFERENCES " +
                ArtistEntry.TABLE_NAME + " (" + ArtistEntry._ID + "), " +
                " FOREIGN KEY (" + ArtistGenreEntry.COLUMN_GENRE_ID + ") REFERENCES " +
                GenreEntry.TABLE_NAME + " (" + GenreEntry._ID + "), " +
                //with unique
                " UNIQUE (" + ArtistGenreEntry.COLUMN_ARTIST_ID + ", " +
                ArtistGenreEntry.COLUMN_GENRE_ID + ") ON CONFLICT IGNORE" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_GENRE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ARTIST_GENRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArtistEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GenreEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ArtistGenreEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
