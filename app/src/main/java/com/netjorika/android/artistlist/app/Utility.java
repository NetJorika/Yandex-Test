package com.netjorika.android.artistlist.app;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Utility {
    /**
     * Получение строки с адресом json
     *
     * @param context
     * @return
     */
    public static String getPreferredJSONURL(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    /**
     * Получение множественного числа для альбомов
     *
     * @param albums
     * @param context
     * @return
     */
    public static String getAlbums(int albums, Context context) {

        if (albums % 10 == 1 && (context.getResources().getConfiguration().locale.getLanguage() != "ru" ||
                albums % 100 != 11 && context.getResources().getConfiguration().locale.getLanguage() == "ru")) {
            return albums + " " + context.getResources().getString(R.string.albums1);
        }
        if (context.getResources().getConfiguration().locale.getLanguage() == "ru" &&
                (albums % 10 == 2 || albums % 10 == 3 || albums % 10 == 4)
                && albums % 100 != 12 && albums % 100 != 13 && albums % 100 != 14) {
            return albums + " " + context.getResources().getString(R.string.albums2);
        }
        return albums + " " + context.getResources().getString(R.string.albums);
    }

    /**
     * Получение множественного числа для песен
     *
     * @param tracks
     * @param context
     * @return
     */

    public static String getTracks(int tracks, Context context) {

        if (tracks % 10 == 1 && (context.getResources().getConfiguration().locale.getLanguage() != "ru" ||
                tracks % 100 != 11 && context.getResources().getConfiguration().locale.getLanguage() == "ru")) {
            return tracks + " " + context.getResources().getString(R.string.tracks1);
        }
        if (context.getResources().getConfiguration().locale.getLanguage() == "ru" &&
                (tracks % 10 == 2 || tracks % 10 == 3 || tracks % 10 == 4)
                && tracks % 100 != 12 && tracks % 100 != 13 && tracks % 100 != 14) {
            return tracks + " " + context.getResources().getString(R.string.tracks2);
        }
        return tracks + " " + context.getResources().getString(R.string.tracks);
    }

}