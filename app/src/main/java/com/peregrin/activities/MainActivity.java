package com.peregrin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.peregrin.R;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private ArrayList<String> names = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(userRegister() == false){
            Intent intent = new Intent(MainActivity.this, SwitchActivity.class);
            MainActivity.this.startActivity(intent);
        }

        ListView contactsList = (ListView) findViewById(R.id.contactsList);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);

        contactsList.setAdapter(adapter);
    }


    public void addContact(View view) {
        names.add(((EditText)findViewById(R.id.etContactName)).getText().toString());
        adapter.notifyDataSetChanged();
    }

    boolean userRegister(){
        return false;
    }
}
