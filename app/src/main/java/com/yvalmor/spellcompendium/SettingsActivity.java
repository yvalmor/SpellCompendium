package com.yvalmor.spellcompendium;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setResult(RESULT_OK);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference listPreference = findPreference("lang");
            listPreference.setValue(getPreferenceManager().getSharedPreferences()
                    .getString("lang", "fr"));

            listPreference.setSummary(listPreference.getEntry());

            listPreference.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Locale locale = ((String) newValue).equals("fr") ? Locale.FRENCH :
                                    Locale.ENGLISH;

                            Configuration config = getResources().getConfiguration();

                            config.setLocale(locale);
                            Locale.setDefault(locale);

                            getResources().updateConfiguration(config,
                                    getResources().getDisplayMetrics());

                            getPreferenceManager().getSharedPreferences().edit()
                                    .putString("lang", (String) newValue).apply();

                            listPreference.setSummary(
                                    ((String) newValue).equals("fr") ? "Français" : "English");

                            return true;
                        }
                    }
            );
        }
    }
}