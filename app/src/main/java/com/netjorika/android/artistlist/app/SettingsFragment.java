package com.netjorika.android.artistlist.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
/**
 * Created by GEORGY on 17.07.2016.
 */
public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener{


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            //bind jsonUrl
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        }

        /**
         * always update Preference if i need more preference
         * @param preference
         */

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener( this);

            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }


        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {

                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else {

                preference.setSummary(stringValue);
            }
            return true;
        }

}
