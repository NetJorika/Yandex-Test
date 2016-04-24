package com.netjorika.android.artistlist.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the database.
 */
public class ArtistContract {

    public static final String CONTENT_AUTHORITY = "com.netjorika.android.artistlist.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ARTIST = "artist";
    public static final String PATH_GENRE = "genre";
    public static final String PATH_ARTIST_GENRE = "artist_genre";

    public static final class ArtistEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static final String TABLE_NAME = "artist";
        public static final String COLUMN_YANDEX_ID = "yandex_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_COVER_SMALL = "coverSmall";
        public static final String COLUMN_COVER_BIG = "coverBig";
        public static final String COLUMN_TRACKS = "tracks";
        public static final String COLUMN_ALBUMS = "albums";

        public static final String COLUMN_GENRES = "genres";

        public static Uri buildArtistById(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id)).build();
        }

        public static Uri buildArtistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class GenreEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;

        public static final String TABLE_NAME = "genre";
        public static final String COLUMN_GENRE = "genre";

        public static Uri buildGenreUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ArtistGenreEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST_GENRE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST_GENRE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST_GENRE;

        public static final String TABLE_NAME = "artist_genre";
        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_GENRE_ID = "genre_id";

        public static Uri buildGenreUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
