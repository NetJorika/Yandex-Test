package com.netjorika.android.artistlist.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.netjorika.android.artistlist.app.data.ArtistContract;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "URI";

    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    //columns for artist
    private static final String[] DETAIL_COLUMNS = {
            ArtistContract.ArtistEntry.TABLE_NAME + "." + ArtistContract.ArtistEntry._ID,
            ArtistContract.ArtistEntry.COLUMN_NAME,
            ArtistContract.ArtistEntry.COLUMN_LINK,
            ArtistContract.ArtistEntry.COLUMN_ALBUMS,
            ArtistContract.ArtistEntry.COLUMN_TRACKS,
            ArtistContract.ArtistEntry.COLUMN_DESCRIPTION,
            ArtistContract.ArtistEntry.COLUMN_COVER_BIG,
            //aggregate function for genres
            "group_concat(" + ArtistContract.GenreEntry.COLUMN_GENRE + ",', ')"
    };

    // projection
    private static final int COL_ARTIST_ID = 0;
    private static final int COL_ARTIST_NAME = 1;
    private static final int COL_ARTIST_LINK = 2;
    private static final int COL_ARTIST_ALBUMS = 3;
    private static final int COL_ARTIST_TRACKS = 4;
    private static final int COL_ARTIST_DESCRIPTION = 5;
    private static final int COL_ARTIST_COVER_BIG = 6;
    private static final int COL_GENRE_GENRE = 7;

    private ImageView mCoverBig;
    private TextView mGenres;
    private TextView mTracks;
    private TextView mAlbums;
    private TextView mDescription;

    private DisplayImageOptions options;

    public DetailFragment() {
        setHasOptionsMenu(true);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mCoverBig = (ImageView) rootView.findViewById(R.id.detail_cover_BIG);
        mGenres = (TextView) rootView.findViewById(R.id.detail_ganres);
        mTracks = (TextView) rootView.findViewById(R.id.detail_tracks);
        mAlbums = (TextView) rootView.findViewById(R.id.detail_albums);
        mDescription = (TextView) rootView.findViewById(R.id.detail_description);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // creating cursor for data
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }
        getActivity().setTitle(data.getString(COL_ARTIST_NAME));
        ImageLoader.getInstance().displayImage(data.getString(COL_ARTIST_COVER_BIG),
                mCoverBig, options);
        mAlbums.setText(Utility.getAlbums(data.getInt(COL_ARTIST_ALBUMS), this.getActivity()));
        mTracks.setText(Utility.getTracks(data.getInt(COL_ARTIST_TRACKS), this.getActivity()));
        mGenres.setText(data.getString(COL_GENRE_GENRE));
        mDescription.setText(data.getString(COL_ARTIST_DESCRIPTION));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
