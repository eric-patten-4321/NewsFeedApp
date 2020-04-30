package com.example.android.paperboy3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class ArticlesPreferenceFragment extends PreferenceFragment implements Preference
            .OnPreferenceChangeListener{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preference_settings_main);
            Preference searchTerm = findPreference(getString(R.string.settings_search_term_key));
            bindPreferenceSummaryToValue(searchTerm);
            Preference numArticles = findPreference(getString(R.string.settings_page_size_key));
            bindPreferenceSummaryToValue(numArticles);
            Preference apiKey = findPreference(getString(R.string.settings_api_key_key_));
            bindPreferenceSummaryToValue(apiKey);
            Preference sectionName = findPreference(getString(R.string.settings_section_name_key));
            bindPreferenceSummaryToValue(sectionName);
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            // get summary if it's a ListPreference by getting the index of values and grabbing
            // the corresponding name from the array of labels
            String stringValue = o.toString();
            if(preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if(prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                // not an array, just get the label and set it
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                    preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            if (preferenceString != null) {
                onPreferenceChange(preference, preferenceString);
            }
        }
    }
}
