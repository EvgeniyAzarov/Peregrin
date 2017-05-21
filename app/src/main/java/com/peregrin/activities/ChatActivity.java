package com.peregrin.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.peregrin.DBHelper;
import com.peregrin.R;
import com.peregrin.ServerInfo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class ChatActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private MessageEntryAdapter adapter;
    private ListView messagesList;
    private ArrayList<HashMap<String, String>> messages;

    private String interlocutorLogin;
    String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        interlocutorLogin = getIntent().getStringExtra("interlocutor_login");
        sender = getSharedPreferences("user", MODE_PRIVATE).getString("phone", "");

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

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
                            request[1] = sender;
                            request[2] = interlocutorLogin;
                            request[3] = content;

                            outputStream.writeObject(request);

                            ContentValues cv = new ContentValues();
                            cv.put("sender_login", sender);
                            cv.put("recipient_login", interlocutorLogin);
                            cv.put("content", content);
                            db.insert("messages", null, cv);

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
        Cursor messages_list = db.query("messages", null,"recipient_login=" + interlocutorLogin + "or (recipient_login=" + sender + "and sender_login = " + interlocutorLogin + ")",null, null, null, null);

        if (messages_list.moveToFirst()) {
            messages.clear();
            do {
                HashMap<String, String> message = new HashMap<>();
                message.put("sender_login", messages_list.getString(messages_list.getColumnIndex("sender_login")));
                message.put("content", messages_list.getString(messages_list.getColumnIndex("content")));

                messages.add(message);
            } while (messages_list.moveToNext());
        }

        messages_list.close();

        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        db.close();
        dbHelper.close();

        super.onDestroy();
    }
}
