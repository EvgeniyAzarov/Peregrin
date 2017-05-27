package com.peregrin.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity{

    SharedPreferences sp_ip;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp_ip = PreferenceManager.getDefaultSharedPreferences(this);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Fragment()).commit();

        String ip = sp_ip.getString("ip_server","base_ip");

    }
}
