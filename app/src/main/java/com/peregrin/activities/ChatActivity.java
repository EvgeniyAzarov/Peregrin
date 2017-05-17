package com.peregrin.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.peregrin.R;

public class ChatActivity extends AppCompatActivity {

    private String interlocutorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        interlocutorLogin = getIntent().getStringExtra("interlocutor_login");
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
