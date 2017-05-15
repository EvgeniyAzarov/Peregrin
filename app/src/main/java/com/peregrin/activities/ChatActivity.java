package com.peregrin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.peregrin.R;

public class ChatActivity extends AppCompatActivity {

    private String InterlocutorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        InterlocutorLogin = getIntent().getStringExtra("Interlocutor login");
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
