package com.peregrin.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peregrin.R;
import com.peregrin.ServerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private String interlocutorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        final EditText etMessage = (EditText) findViewById(R.id.etMessage);

        interlocutorLogin = getIntent().getStringExtra("Interlocutor_login");

        findViewById(R.id.btEnter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog progressDialog;

                    private String phone;
                    private String password;
                    private String nickname;
                    private String content;

                    private boolean enterSuccessful;
                    private boolean networkError = false;

                    @Override
                    protected void onPreExecute() {
                        content = etMessage.toString();
                    }


                    @Override
                    protected Void doInBackground(Void... params) {
                        try (
                                Socket socket = new Socket(ServerInfo.ADDRESS, ServerInfo.PORT);
                                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        ) {
                            String[] request = new String[4];
                            request[0] = "POST_MESSAGE";
                            request[1] = getSharedPreferences("user", MODE_PRIVATE).getString("phone", "");
                            request[2] = interlocutorLogin;
                            request[3] = content;

                            outputStream.writeObject(request);

                        } catch (IOException e) {
                            networkError = true;
                        }

                        return null;


                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (networkError) {
                            Toasty.error(ChatActivity.this, getString(R.string.network_error)).show();

                        }
                    }
                }.execute();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}
