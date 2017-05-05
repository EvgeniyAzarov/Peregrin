package com.peregrin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.peregrin.R;

public class ChatActivity extends Activity {

    private String InterlocutorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        InterlocutorLogin = getIntent().getStringExtra("Interlocutor login");
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
