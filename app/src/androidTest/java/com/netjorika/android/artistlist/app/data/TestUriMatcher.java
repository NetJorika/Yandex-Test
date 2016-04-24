package com.netjorika.android.artistlist.app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;


public class TestUriMatcher extends AndroidTestCase {
    private static final long TEST_ID = 360934;


    private static final Uri TEST_ARTIST_DIR = ArtistContract.ArtistEntry.CONTENT_URI;
    private static final Uri TEST_ARTIST_BY_ID = ArtistContract.ArtistEntry.buildArtistById(TEST_ID);
    private static final Uri TEST_GENRE_DIR = ArtistContract.GenreEntry.CONTENT_URI;
    private static final Uri TEST_ARTIST_GENRE_DIR = ArtistContract.ArtistGenreEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = ArtistProvider.buildUriMatcher();

        assertEquals("Error: The ARTIST URI was matched incorrectly.",
                testMatcher.match(TEST_ARTIST_DIR), ArtistProvider.ARTIST);
        assertEquals("Error: The ARTIST_BY_ID URI was matched incorrectly.",
                testMatcher.match(TEST_ARTIST_BY_ID), ArtistProvider.ARTIST_BY_ID);
        assertEquals("Error: The GENRE URI was matched incorrectly.",
                testMatcher.match(TEST_GENRE_DIR), ArtistProvider.GENRE);
        assertEquals("Error: The ARTIST GENRE URI was matched incorrectly.",
                testMatcher.match(TEST_ARTIST_GENRE_DIR), ArtistProvider.ARTIST_GENRE);
    }
}