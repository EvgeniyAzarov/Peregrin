package com.peregrin.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.peregrin.R;
import com.peregrin.ServerInfo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private MessageEntryAdapter adapter;
    private ListView messagesList;
    private ArrayList<HashMap<String, String>> messages;

    private String interlocutorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        interlocutorLogin = getIntent().getStringExtra("interlocutor_login");

        messages = new ArrayList<>();

        adapter = new MessageEntryAdapter(this, messages);

        messagesList = (ListView) findViewById(R.id.messages_list);
        messagesList.setAdapter(adapter);

        findViewById(R.id.btSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    private String content;

                    private boolean networkError = false;

                    @Override
                    protected void onPreExecute() {
                        EditText etMessage = (EditText) findViewById(R.id.etMessage);
                        content = etMessage.getText().toString();
                        etMessage.setText("");
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

                            //TODO add insert to DB

                        } catch (IOException e) {
                            networkError = true;
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if (networkError) {
                            Toasty.error(ChatActivity.this, getString(R.string.network_error)).show();
                        } else {
                            updateChat();
                        }
                    }
                }.execute();
            }
        });
    }

    private void updateChat() {

    }
}
