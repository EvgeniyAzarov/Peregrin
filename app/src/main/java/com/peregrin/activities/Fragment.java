package com.peregrin.activities;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.peregrin.R;

public class Fragment extends PreferenceFragment {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }

}