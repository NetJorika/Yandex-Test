package com.netjorika.android.artistlist.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();


    void deleteTheDatabase() {
        mContext.deleteDatabase(ArtistDbHelper.DATABASE_NAME);
    }


    public void setUp() {
        deleteTheDatabase();
    }


    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(ArtistContract.GenreEntry.TABLE_NAME);
        tableNameHashSet.add(ArtistContract.ArtistEntry.TABLE_NAME);
        tableNameHashSet.add(ArtistContract.ArtistGenreEntry.TABLE_NAME);

        mContext.deleteDatabase(ArtistDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new ArtistDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());


        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());


        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );


        assertTrue("Error: Your database was created without entry tables",
                tableNameHashSet.isEmpty());


        c = db.rawQuery("PRAGMA table_info(" + ArtistContract.GenreEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());


        final HashSet<String> genreColumnHashSet = new HashSet<String>();
        genreColumnHashSet.add(ArtistContract.GenreEntry._ID);
        genreColumnHashSet.add(ArtistContract.GenreEntry.COLUMN_GENRE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            genreColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                genreColumnHashSet.isEmpty());
        db.close();
    }


    public long insertArtist() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        ArtistDbHelper dbHelper = new ArtistDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createArtistValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long artistRowId;
        artistRowId = db.insert(ArtistContract.ArtistEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(artistRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                ArtistContract.ArtistEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return artistRowId;
    }
}