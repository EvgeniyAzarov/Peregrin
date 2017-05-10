package com.peregrin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.peregrin.R;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class MainActivity extends Activity {

    private ArrayList<String> names = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onStart() {
        super.onStart();

        if (getPreferences(MODE_PRIVATE).getString("phone", "").equals("")) {
            this.startActivity(new Intent(MainActivity.this, SwitchActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView contactsList = (ListView) findViewById(R.id.contactsList);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);

        contactsList.setAdapter(adapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("null", "itemClick: position = " + position + ", id = " + id);
            }
        });

    }
}
