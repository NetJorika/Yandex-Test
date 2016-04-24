package com.netjorika.android.artistlist.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.netjorika.android.artistlist.app.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {
    static final long  TEST_ARTIST = 360934;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createArtistValues() {
        ContentValues artistValues = new ContentValues();
        artistValues.put(ArtistContract.ArtistEntry.COLUMN_NAME, "a1");
        artistValues.put(ArtistContract.ArtistEntry.COLUMN_DESCRIPTION, "a2");
        artistValues.put(ArtistContract.ArtistEntry.COLUMN_TRACKS, 2);
        artistValues.put(ArtistContract.ArtistEntry.COLUMN_ALBUMS, 1);
        return artistValues;
    }


    static ContentValues createGenreValues() {
        ContentValues testValues = new ContentValues();
         testValues.put(ArtistContract.GenreEntry.COLUMN_GENRE, TEST_ARTIST);
        return testValues;
    }

    static long insertArtistValues(Context context) {
        // insert our test records into the database
        ArtistDbHelper dbHelper = new ArtistDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createArtistValues();

        long artistRowId;
        artistRowId = db.insert(ArtistContract.ArtistEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Genre Values", artistRowId != -1);

        return artistRowId;
    }    static long insertGenreValues(Context context) {
        // insert our test records into the database
        ArtistDbHelper dbHelper = new ArtistDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createGenreValues();

        long GenreRowId;
        GenreRowId = db.insert(ArtistContract.GenreEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Genre Values", GenreRowId != -1);

        return GenreRowId;
    }



}