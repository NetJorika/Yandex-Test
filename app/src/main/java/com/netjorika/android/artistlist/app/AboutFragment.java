package com.netjorika.android.artistlist.app;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.netjorika.android.artistlist.app.data.ArtistContract;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by GEORGY on 17.07.2016.
 */

public class AboutFragment extends Fragment{


    private static final String LOG_TAG = AboutFragment.class.getSimpleName();


    public AboutFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        /*
        final Button button = (Button) getActivity().findViewById(R.id.Send_email);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",getString(R.string.my_email), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Ebail_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });*/
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.aboutfragment, menu);

    }

}
