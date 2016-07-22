package com.netjorika.android.artistlist.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MainActivity extends ActionBarActivity
        implements ArtistFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String ARTISTFRAGMENT_TAG = "AFTAG";
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String TAG = "Main";

    private HeadSetReceiver myReceiver;
    private String mJSONURL;

    private class HeadSetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (findViewById(R.id.listview_headset) != null) {
                if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            //Headset unplugged
                            (findViewById(R.id.listview_headset)).setVisibility(View.GONE);
                            break;
                        case 1:
                            //Headset plugged
                            (findViewById(R.id.listview_headset)).setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        myReceiver = new HeadSetReceiver();
        //mJSONURL =  Utility.getPreferredJSONURL(this);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ArtistFragment(), ARTISTFRAGMENT_TAG)
                    .commit();
        }
        initImageLoader(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new AboutFragment(),DETAILFRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
       /* if (id == R.id.action_settings) {
            //startActivity(new Intent(this, SettingsActivity.class));
            Bundle arguments = new Bundle();
            /*
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new SettingsFragment(), ARTISTFRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
        String JSONURL = Utility.getPreferredJSONURL(this);

        if (JSONURL != null && !JSONURL.equals(mJSONURL)) {
            ArtistFragment af = (ArtistFragment) getSupportFragmentManager().findFragmentByTag(ARTISTFRAGMENT_TAG);
            if (null != af) {
                af.onLocationChanged();
            }
            mJSONURL = JSONURL;
        }
    }

    @Override
    protected void onPause() {
        try {
            if (myReceiver != null)
                unregisterReceiver(myReceiver);
        } catch (Exception e) {
        }
        super.onPause();
    }

    /**
     * ImageLoader Init
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onItemSelected(Uri contentUri) {

        Bundle args = new Bundle();
        args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, DETAILFRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
        /*
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(contentUri);
        startActivity(intent);
        */
    }

    public void buttonSendEmail(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.my_email)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));
        startActivity(Intent.createChooser(emailIntent, "Send email"));
    }
}

