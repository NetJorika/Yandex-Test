package com.netjorika.android.artistlist.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class ArtistProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ArtistDbHelper mOpenHelper;

    static final int ARTIST = 100;
    static final int ARTIST_BY_ID = 200;
    static final int GENRE = 300;
    static final int ARTIST_GENRE = 400;


    private static final SQLiteQueryBuilder sArtistGenreQueryBuilder;

    static {
        sArtistGenreQueryBuilder = new SQLiteQueryBuilder();


        sArtistGenreQueryBuilder.setTables(
                ArtistContract.ArtistEntry.TABLE_NAME
                        + "  LEFT OUTER JOIN " +
                        ArtistContract.ArtistGenreEntry.TABLE_NAME +
                        " ON " + ArtistContract.ArtistEntry.TABLE_NAME +
                        "." + ArtistContract.ArtistEntry._ID +
                        " = " + ArtistContract.ArtistGenreEntry.TABLE_NAME +
                        "." + ArtistContract.ArtistGenreEntry.COLUMN_ARTIST_ID + " " +
                        "  LEFT OUTER JOIN " + ArtistContract.GenreEntry.TABLE_NAME +
                        " ON " + ArtistContract.ArtistGenreEntry.TABLE_NAME +
                        "." + ArtistContract.ArtistGenreEntry.COLUMN_GENRE_ID +
                        " = " + ArtistContract.GenreEntry.TABLE_NAME +
                        "." + ArtistContract.GenreEntry._ID);
    }

    private static final String sGenreSelection =
            ArtistContract.GenreEntry.TABLE_NAME +
                    "." + ArtistContract.GenreEntry.COLUMN_GENRE + " = ? ";

    private static final String sArtistYandeIdSelection =
            ArtistContract.ArtistEntry.TABLE_NAME +
                    "." + ArtistContract.ArtistEntry.COLUMN_YANDEX_ID + " = ? ";


    //buildArtistById
    private Cursor getArtistById(
            Uri uri, String[] projection, String sortOrder) {
        long id = ArtistContract.ArtistEntry.getIdFromUri(uri);

        return sArtistGenreQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sArtistYandeIdSelection,
                new String[]{Long.toString(id)},
                ArtistContract.ArtistEntry.TABLE_NAME +
                        "." + ArtistContract.ArtistEntry.COLUMN_YANDEX_ID,
                null,
                sortOrder
        );
    }


    /**
     * Bild new Uri
     *
     * @return
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArtistContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ArtistContract.PATH_ARTIST, ARTIST);
        matcher.addURI(authority, ArtistContract.PATH_ARTIST + "/#", ARTIST_BY_ID);

        matcher.addURI(authority, ArtistContract.PATH_GENRE, GENRE);

        matcher.addURI(authority, ArtistContract.PATH_ARTIST_GENRE, ARTIST_GENRE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ArtistDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case ARTIST_BY_ID:
                return ArtistContract.ArtistEntry.CONTENT_ITEM_TYPE;
            case ARTIST:
                return ArtistContract.ArtistEntry.CONTENT_TYPE;
            case GENRE:
                return ArtistContract.GenreEntry.CONTENT_TYPE;
            case ARTIST_GENRE:
                return ArtistContract.GenreEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //to understand what query is
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ARTIST_BY_ID: {
                retCursor = getArtistById(uri, projection, sortOrder);
                break;
            }
            case ARTIST: {
                retCursor = sArtistGenreQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        ArtistContract.ArtistEntry.TABLE_NAME +
                                "." + ArtistContract.ArtistEntry.COLUMN_YANDEX_ID,
                        null,
                        sortOrder
                );
                break;
            }
            case GENRE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArtistContract.GenreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ARTIST_GENRE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArtistContract.ArtistGenreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ARTIST: {
                //Here need something else for upsert
                long _id = db.insertWithOnConflict(ArtistContract.ArtistEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (_id > 0)
                    returnUri = ArtistContract.ArtistEntry.buildArtistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GENRE: {
                long _id = db.insertWithOnConflict(ArtistContract.GenreEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (_id > 0)
                    returnUri = ArtistContract.GenreEntry.buildGenreUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ARTIST_GENRE: {
                returnUri = null;
                db.execSQL("INSERT INTO " + ArtistContract.ArtistGenreEntry.TABLE_NAME +
                        " (" + ArtistContract.ArtistGenreEntry.COLUMN_ARTIST_ID + ", " + ArtistContract.ArtistGenreEntry.COLUMN_GENRE_ID + ") " +
                        "SELECT " + ArtistContract.ArtistEntry.TABLE_NAME + "." + ArtistContract.ArtistEntry._ID + "," +
                        ArtistContract.GenreEntry.TABLE_NAME + "." + ArtistContract.GenreEntry._ID + " " +
                        "FROM " + ArtistContract.ArtistEntry.TABLE_NAME + ", " + ArtistContract.GenreEntry.TABLE_NAME + " " +
                        "WHERE " + ArtistContract.ArtistEntry.TABLE_NAME + "." + ArtistContract.ArtistEntry.COLUMN_YANDEX_ID + "='" +
                        values.get(ArtistContract.ArtistEntry.COLUMN_YANDEX_ID) + "' and " +
                        ArtistContract.GenreEntry.TABLE_NAME + "." + ArtistContract.GenreEntry.COLUMN_GENRE + "='" +
                        values.get(ArtistContract.GenreEntry.COLUMN_GENRE) + "';");
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";
        switch (match) {
            case ARTIST:
                rowsDeleted = db.delete(
                        ArtistContract.ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GENRE:
                rowsDeleted = db.delete(
                        ArtistContract.GenreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ARTIST_GENRE: {
                rowsDeleted = db.delete(
                        ArtistContract.ArtistGenreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ARTIST:
                rowsUpdated = db.update(ArtistContract.ArtistEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case GENRE:
                rowsUpdated = db.update(ArtistContract.GenreEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case ARTIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        //Here need something else for upsert
                        long _id = db.insertWithOnConflict(ArtistContract.ArtistEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case GENRE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(ArtistContract.GenreEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            case ARTIST_GENRE: {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        db.execSQL("INSERT INTO " + ArtistContract.ArtistGenreEntry.TABLE_NAME +
                                " (" + ArtistContract.ArtistGenreEntry.COLUMN_ARTIST_ID + ", " + ArtistContract.ArtistGenreEntry.COLUMN_GENRE_ID + ") " +
                                "SELECT " + ArtistContract.ArtistEntry.TABLE_NAME + "." + ArtistContract.ArtistEntry._ID + "," +
                                ArtistContract.GenreEntry.TABLE_NAME + "." + ArtistContract.GenreEntry._ID + " " +
                                "FROM " + ArtistContract.ArtistEntry.TABLE_NAME + ", " + ArtistContract.GenreEntry.TABLE_NAME + " " +
                                "WHERE " + ArtistContract.ArtistEntry.TABLE_NAME + "." + ArtistContract.ArtistEntry.COLUMN_YANDEX_ID + "='" +
                                value.get(ArtistContract.ArtistEntry.COLUMN_YANDEX_ID) + "' and " +
                                ArtistContract.GenreEntry.TABLE_NAME + "." + ArtistContract.GenreEntry.COLUMN_GENRE + "='" +
                                value.get(ArtistContract.GenreEntry.COLUMN_GENRE) + "';");
                        returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                return super.bulkInsert(uri, values);
        }
        return returnCount;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}