package com.example.android.sunshine.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by ulrichca on 28/12/2016.
 */

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets the settings UI (xml settings xml file) to this activity
        addPreferencesFromResource(R.xml.preferences);

        //Bind the preference summary field to the value of the location key. this makes that the
        //summary value underneath the pref changed updates to the value selected.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.edit_text_location_key)));
    }

    /**
     * bindPreferenceSummaryToValue Method:
     * Attaches a listener so the summary text is always updated with the preference value the user
     * selected.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
        // Trigger the listener immediately with the preference's current value.
        onPreferenceChange(preference,
            PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
        //Toast log
        Toast toast = Toast.makeText(getApplicationContext(),
                "Method: bindPreferenceSummaryToValue",
                Toast.LENGTH_LONG);
        toast.show();
        }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //This method is called everytime 'SettingsActivity' is invoked
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in the preference's 'entries'
            // list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        //Toast log
        Toast toast = Toast.makeText(getApplicationContext(),
                "Method: onPreferenceChange",
                Toast.LENGTH_LONG);
        toast.show();

        return true;
        //return false;
    }
}
