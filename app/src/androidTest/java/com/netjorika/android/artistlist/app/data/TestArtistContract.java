
package com.netjorika.android.artistlist.app.data;

import android.net.Uri;
import android.test.AndroidTestCase;


public class TestArtistContract extends AndroidTestCase {

    private static final long TEST_ARTIST_ID = 360934;


    public void testBuildArtistById() {
        Uri locationUri = ArtistContract.ArtistEntry.buildArtistById(TEST_ARTIST_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " +
                        "ArtistContract.",
                locationUri);
        assertEquals("Error: Artist not properly appended to the end of the Uri",
                TEST_ARTIST_ID, locationUri.getLastPathSegment());
        assertEquals("Error: Artist  Uri doesn't match our expected result",
                locationUri.toString(),
                "content://com.netjorika.android.artistlist.app/artist/360934");
    }
}