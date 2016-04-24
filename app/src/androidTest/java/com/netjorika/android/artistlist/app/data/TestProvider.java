
package com.netjorika.android.artistlist.app.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.test.AndroidTestCase;

import com.netjorika.android.artistlist.app.data.ArtistContract.ArtistEntry;
import com.netjorika.android.artistlist.app.data.ArtistContract.GenreEntry;


public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();


    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ArtistEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                GenreEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }


    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }


    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();


        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                ArtistProvider.class.getName());
        try {

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);


            assertEquals("Error: ArtistProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + ArtistContract.CONTENT_AUTHORITY,
                    providerInfo.authority, ArtistContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }


    public void testGetType() {

        String type = mContext.getContentResolver().getType(ArtistEntry.CONTENT_URI);

        assertEquals("Error: the ArtistEntry CONTENT_URI should return ArtistEntry.CONTENT_TYPE",
                ArtistEntry.CONTENT_TYPE, type);

        long testId = 360934;
        type = mContext.getContentResolver().getType(
                ArtistEntry.buildArtistById( testId));
        assertEquals("Error: the ArtistEntry CONTENT_URI with location and date should return ArtistEntry.CONTENT_ITEM_TYPE",
                ArtistEntry.CONTENT_ITEM_TYPE, type);


        type = mContext.getContentResolver().getType(GenreEntry.CONTENT_URI);
        assertEquals("Error: the GenreEntry CONTENT_URI should return GenreEntry.CONTENT_TYPE",
                GenreEntry.CONTENT_TYPE, type);
    }



    public void testBasicArtistQuery() {
        ArtistDbHelper dbHelper = new ArtistDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createArtistValues();
        long artistRowId1 = TestUtilities.insertArtistValues(mContext);


        ContentValues artistValues = TestUtilities.createGenreValues();

        long artistRowId = db.insert(ArtistEntry.TABLE_NAME, null, artistValues);
        assertTrue("Unable to Insert ArtistEntry into the Database", artistRowId != -1);

        db.close();


        Cursor artistCursor = mContext.getContentResolver().query(
                ArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicWeatherQuery", artistCursor, artistValues);
    }


    public void testBasicGenreQueries() {

        ArtistDbHelper dbHelper = new ArtistDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createGenreValues();
        long GenreRowId = TestUtilities.insertGenreValues(mContext);


        Cursor genreCursor = mContext.getContentResolver().query(
                GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicLocationQueries, location query", genreCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    genreCursor.getNotificationUri(), GenreEntry.CONTENT_URI);
        }
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertArtistValues() {
        long currentId = TestUtilities.TEST_ARTIST;

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentId++ ) {

            ContentValues artistValues = new ContentValues();
            artistValues.put(ArtistEntry.COLUMN_NAME, "Name "+i);
            artistValues.put(ArtistEntry.COLUMN_YANDEX_ID, i);
            returnContentValues[i] = artistValues;
        }
        return returnContentValues;
    }


}