package com.lingya.farmintell.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.lingya.farmintell.client.R;


/**
 * Created by zwq00000 on 15-9-8.
 */
public class ClientSettingsFragment extends PreferenceActivity {
    private Preference.OnPreferenceChangeListener bindPreferenceServerTypeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean isMqttServer = Boolean.parseBoolean(newValue.toString());
            prefServerUrl.setEnabled(!isMqttServer);
            return true;
        }
    };

    private Preference prefServerUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_client);

        prefServerUrl = findPreference(getString(R.string.key_server_url));
        bindPreferenceServerType(findPreference(getResources().getString(R.string.key_server_type)));
    }

    private void bindPreferenceServerType(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(bindPreferenceServerTypeListener);

        // Trigger the listener immediately with the preference's
        // current value.
        bindPreferenceServerTypeListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }
}
