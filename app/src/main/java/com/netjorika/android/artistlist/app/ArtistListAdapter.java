
package com.netjorika.android.artistlist.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ArtistListAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView smallImageView;
        public final TextView nameView;
        public final TextView genreView;
        public final TextView albumsView;
        public final TextView tracksView;

        public ViewHolder(View view) {
            smallImageView = (ImageView) view.findViewById(R.id.list_item_image);
            nameView = (TextView) view.findViewById(R.id.list_item_name);
            genreView = (TextView) view.findViewById(R.id.list_item_genres);
            albumsView = (TextView) view.findViewById(R.id.list_item_albums);
            tracksView = (TextView) view.findViewById(R.id.list_item_tracks);
        }
    }

    private DisplayImageOptions options;

    public ArtistListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_artist, parent, false);
        //for reuse
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String name = cursor.getString(ArtistFragment.COL_ARTIST_NAME);
        String tracks = Utility.getTracks(cursor.getInt(ArtistFragment.COL_ARTIST_TRACKS), context);
        String albums = Utility.getAlbums(cursor.getInt(ArtistFragment.COL_ARTIST_ALBUMS), context);
        String genres = cursor.getString(ArtistFragment.COL_GENRE_GENRE);

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        ImageLoader.getInstance().displayImage(cursor.getString(ArtistFragment.COL_ARTIST_COVER_SMALL),
                viewHolder.smallImageView, options);
        viewHolder.nameView.setText(name);
        viewHolder.albumsView.setText(albums);
        viewHolder.tracksView.setText(tracks);
        viewHolder.genreView.setText(genres);
    }
}