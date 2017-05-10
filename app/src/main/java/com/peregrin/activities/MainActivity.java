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

import static com.peregrin.R.id.contactsList;


public class MainActivity extends Activity {

    private ArrayList<String> names = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void addContact(View view) {
        names.add(((EditText)findViewById(R.id.etContactName)).getText().toString());
        adapter.notifyDataSetChanged();
    }

    boolean userRegister(){
        return false;
    }
}
