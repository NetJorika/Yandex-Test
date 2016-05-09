package com.netjorika.android.artistlist.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.netjorika.android.artistlist.app.data.ArtistContract;
import com.netjorika.android.artistlist.app.service.ArtistService;


public class ArtistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ARTIST_LOADER = 0;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    // Specify the columns
    private static final String[] ARTIST_COLUMNS = {
            ArtistContract.ArtistEntry.TABLE_NAME + "." + ArtistContract.ArtistEntry._ID,
            ArtistContract.ArtistEntry.COLUMN_YANDEX_ID,
            ArtistContract.ArtistEntry.COLUMN_NAME,
            ArtistContract.ArtistEntry.COLUMN_LINK,
            ArtistContract.ArtistEntry.COLUMN_DESCRIPTION,
            ArtistContract.ArtistEntry.COLUMN_ALBUMS,
            ArtistContract.ArtistEntry.COLUMN_TRACKS,
            ArtistContract.ArtistEntry.COLUMN_COVER_SMALL,
            ArtistContract.ArtistEntry.COLUMN_COVER_BIG,
            "group_concat(" + ArtistContract.GenreEntry.COLUMN_GENRE + ",', ')"
    };

    // all colums indexes
    static final int COL_ARTIST_ID = 0;
    static final int COL_ARTIST_YANDEX_ID = 1;
    static final int COL_ARTIST_NAME = 2;
    static final int COL_ARTIST_LINK = 3;
    static final int COL_ARTIST_DESCRIPTION = 4;
    static final int COL_ARTIST_ALBUMS = 5;
    static final int COL_ARTIST_TRACKS = 6;
    static final int COL_ARTIST_COVER_SMALL = 7;
    static final int COL_ARTIST_COVER_BIG = 8;
    static final int COL_GENRE_GENRE = 9;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri idUri);
    }

    private ArtistListAdapter mArtistListAdapter;

    public ArtistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.artistfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Refresh button pressed
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateArtists();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mArtistListAdapter = new ArtistListAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get listView
        mListView = (ListView) rootView.findViewById(R.id.listview_artist);
        mListView.setAdapter(mArtistListAdapter);

        // Add drilldown activity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity())
                            .onItemSelected(ArtistContract.ArtistEntry.buildArtistById(cursor.getLong(COL_ARTIST_YANDEX_ID)
                            ));
                }
                mPosition = position;
            }


        });
        //try to save position
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ARTIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateArtists()  {
        /*
        FetchArtistListTask artistListTask = new FetchArtistListTask(getActivity());
        String jSON = Utility.getPreferredJSONURL(getActivity());
        artistListTask.execute(jSON);*/
                Intent alarmIntent = new Intent(getActivity(), ArtistService.AlarmReceiver.class);
                alarmIntent.putExtra(ArtistService.YANDEX_BASE_URL, Utility.getPreferredJSONURL(getActivity()));

                         //Wrap in a pending intent which only fires once.
                               PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0,alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);

                        AlarmManager am=(AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

                        //Set the AlarmManager to wake up the system.
                                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
    }

    // reload data if json updated
    void onLocationChanged() {
        updateArtists();
        //work not so good :( but i have not time to learn more about AsyncTasks
        getLoaderManager().restartLoader(ARTIST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = ArtistContract.ArtistEntry.COLUMN_YANDEX_ID + " ASC";
        Uri ArtistUri = ArtistContract.ArtistEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                ArtistUri,
                ARTIST_COLUMNS,
                ArtistContract.ArtistEntry.TABLE_NAME +
                        "." + ArtistContract.ArtistEntry._ID,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mArtistListAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //return state
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mArtistListAdapter.swapCursor(null);
    }
}
