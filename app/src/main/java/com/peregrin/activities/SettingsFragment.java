package com.peregrin.activities;


import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.peregrin.R;
import com.peregrin.ServerInfo;

public class SettingsFragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        final EditTextPreference ipPref = (EditTextPreference) findPreference("ip_server");

        ipPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                ServerInfo.ADDRESS = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getString("ip_server", ServerInfo.ADDRESS);

                return true;
            }
        });
    }

}