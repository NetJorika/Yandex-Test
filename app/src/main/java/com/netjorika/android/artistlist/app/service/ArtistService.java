package com.netjorika.android.artistlist.app.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.netjorika.android.artistlist.app.data.ArtistContract;
import com.netjorika.android.artistlist.app.data.ArtistContract.ArtistEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;


    public class ArtistService extends IntentService {
        private ArrayAdapter<String> mArtistAdapter;
        public static final String YANDEX_BASE_URL =  "url";//"http://cache-default05e.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
        private final String LOG_TAG = ArtistService.class.getSimpleName();
        public ArtistService() {
            super("Artist");
        }


        @Override
        protected void onHandleIntent(Intent intent) {
            String locationQuery = intent.getStringExtra(YANDEX_BASE_URL);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // raw JSON response.
            String artistListJsonStr = null;

            //try to connect and get data
            try {
                Uri builtUri = Uri.parse(locationQuery);

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }
                artistListJsonStr = buffer.toString();
                getArtistDataFromJson(artistListJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return;
        }


    /**
     * Update DB fron JSON answer
     *
     * @param artistListJsonStr
     * @throws JSONException
     */
    private void getArtistDataFromJson(String artistListJsonStr)
            throws JSONException {

        // JSON names

        final String AI_ID = "id";
        final String AI_NAME = "name";
        final String AI_GENRES = "genres";
        final String AI_TRACKS = "tracks";
        final String AI_ALBUMS = "albums";
        final String AI_LINK = "link";
        final String AI_DESCRIPTION = "description";
        final String AI_COVER = "cover";
        final String AI_COVER_SMALL = "small";
        final String AI_COVER_BIG = "big";

        try {
            JSONArray artistArray = new JSONArray(artistListJsonStr);

            //Vectors for DB update
            Vector<ContentValues> cVVector = new Vector<ContentValues>(artistArray.length());
            Vector<ContentValues> cVArtistGenreVector = new Vector<ContentValues>();

            //For unique ganres update
            HashSet<String> hsetGenre = new HashSet<String>();

            for (int i = 0; i < artistArray.length(); i++) {
                String name;
                String description;
                int id;
                int tracks;
                int albums;
                String link;
                String coverSmall;
                String coverBig;
                String[] genres;

                // Get the JSON object representing
                JSONObject artist = artistArray.getJSONObject(i);


                name = artist.getString(AI_NAME);
                description = artist.getString(AI_DESCRIPTION);
                id = artist.getInt(AI_ID);
                tracks = artist.getInt(AI_TRACKS);
                albums = artist.getInt(AI_ALBUMS);
                link = artist.optString(AI_LINK);
                coverSmall = artist.getJSONObject(AI_COVER).getString(AI_COVER_SMALL);
                coverBig = artist.getJSONObject(AI_COVER).getString(AI_COVER_BIG);


                JSONArray genresArray = artist.getJSONArray(AI_GENRES);
                genres = new String[genresArray.length()];
                for (int j = 0; j < genresArray.length(); j++) {
                    // unique genres
                    genres[j] = genresArray.getString(j);
                    hsetGenre.add(genresArray.getString(j));

                    //genres for intersection table
                    ContentValues artistGenreValues = new ContentValues();
                    artistGenreValues.put(ArtistEntry.COLUMN_YANDEX_ID, id);
                    artistGenreValues.put(ArtistContract.GenreEntry.COLUMN_GENRE, genresArray.getString(j));
                    cVArtistGenreVector.add(artistGenreValues);
                }


                ContentValues artistValues = new ContentValues();
                artistValues.put(ArtistEntry.COLUMN_YANDEX_ID, id);
                artistValues.put(ArtistEntry.COLUMN_NAME, name);
                artistValues.put(ArtistEntry.COLUMN_TRACKS, tracks);
                artistValues.put(ArtistEntry.COLUMN_ALBUMS, albums);
                artistValues.put(ArtistEntry.COLUMN_LINK, link);
                artistValues.put(ArtistEntry.COLUMN_DESCRIPTION, description);
                artistValues.put(ArtistEntry.COLUMN_COVER_SMALL, coverSmall);
                artistValues.put(ArtistEntry.COLUMN_COVER_BIG, coverBig);

                cVVector.add(artistValues);

            }

            int inserted = 0;
            // add artist to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = this.getContentResolver().bulkInsert(ArtistEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "Artists Complete. " + inserted + " Inserted");


            int inserted2 = 0;
            // add genres to database
            if (hsetGenre.size() > 0) {

                ContentValues[] cvArray2 = new ContentValues[hsetGenre.size()];
                // create an iterator
                Iterator iterator = hsetGenre.iterator();
                int hi = 0;
                // check values
                while (iterator.hasNext()) {
                    ContentValues genreValues = new ContentValues();
                    genreValues.put(ArtistContract.GenreEntry.COLUMN_GENRE, iterator.next().toString());
                    cvArray2[hi] = genreValues;
                    hi++;
                }
                inserted2 = this.getContentResolver().bulkInsert(ArtistContract.GenreEntry.CONTENT_URI, cvArray2);
                Log.d(LOG_TAG, "Genres Complete. " + inserted2 + " Inserted");
            }

            int inserted3 = 0;
            // add intersect to database
            if (cVArtistGenreVector.size() > 0) {
                ContentValues[] cvArtistGenreArray = new ContentValues[cVArtistGenreVector.size()];
                cVArtistGenreVector.toArray(cvArtistGenreArray);
                inserted3 = this.getContentResolver().bulkInsert(ArtistContract.ArtistGenreEntry.CONTENT_URI, cvArtistGenreArray);
            }
            // not true but i not found how easy get it from execSQL :(
            Log.d(LOG_TAG, "ArtistGenres Complete. " + inserted3 + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

        public static class AlarmReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context context, Intent intent) {
                Intent sendIntent = new Intent(context, ArtistService.class);
                sendIntent.putExtra(ArtistService.YANDEX_BASE_URL, intent.getStringExtra(ArtistService.YANDEX_BASE_URL));
                context.startService(sendIntent);

            }
        }
}