package com.peregrin.activities;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
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

    public final static String ACTION_UPDATE_CHAT = "UpdateChat";

    private String interlocutorLogin;
    String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        interlocutorLogin = getIntent().getStringExtra("interlocutor_login");
        sender = getSharedPreferences("user", MODE_PRIVATE).getString("phone", "");

        final ImageButton btCycle = (ImageButton) findViewById(R.id.btCycling);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        messages = new ArrayList<>();

        adapter = new MessageEntryAdapter(this, messages);

        messagesList = (ListView) findViewById(R.id.messages_list);
        messagesList.setAdapter(adapter);

        findViewById(R.id.btCycling).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesList.post(new Runnable() {
                    @Override
                    public void run() {
                        int position = messagesList.getCount()-1;
                        messagesList.setSelection(position);
                        View v = messagesList.getChildAt(position);
                        if (v != null) {
                            v.requestFocus();
                        }
                    }
                });
            }
        });

        messagesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(totalItemCount-firstVisibleItem-visibleItemCount>=3){
                    btCycle.setVisibility(View.VISIBLE);
                }
                else{
                    btCycle.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.btSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    private String content;

                    private boolean messageCorrect = true;
                    private boolean networkError = false;

                    @Override
                    protected void onPreExecute() {
                        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(100);
                        
                        EditText etMessage = (EditText) findViewById(R.id.etMessage);
                        content = etMessage.getText().toString();
                        etMessage.setText("");
                        if(content.equals("")){
                            messageCorrect = false;
                        }
                    }

                    @Override
                    protected Void doInBackground(Void... params) {

                        if(messageCorrect) {
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

        BroadcastReceiver br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                boolean received = intent.getBooleanExtra("received", false);
                if(received){
                    updateChat();
                }
            }
        };

        IntentFilter intFilt = new IntentFilter(ACTION_UPDATE_CHAT);
        registerReceiver(br, intFilt);
    }

    private void updateChat() {
        new AsyncTask<Void, Void, Void>() {
            private final ArrayList<HashMap<String, String>> newMessages = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                Cursor messages_list;

                do {
                    messages_list = db.query("messages", null,
                            "recipient_login=? or (recipient_login=? and sender_login = ?)",
                            new String[]{interlocutorLogin, sender, interlocutorLogin}, null, null, null);

                    if (!messages_list.moveToFirst())
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignored) {

                        }
                } while (!messages_list.moveToFirst());


                messages.clear();
                do {
                    HashMap<String, String> message = new HashMap<>();
                    message.put("sender_login", messages_list.getString(messages_list.getColumnIndex("sender_login")));
                    message.put("content", messages_list.getString(messages_list.getColumnIndex("content")));

                    newMessages.add(message);
                } while (messages_list.moveToNext());

                messages_list.close();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                messages.addAll(newMessages);

                adapter.notifyDataSetChanged();

                messagesList.post(new Runnable() {
                    @Override
                    public void run() {
                        int position = messagesList.getCount()-1;
                        messagesList.setSelection(position);
                        View v = messagesList.getChildAt(position);
                        if (v != null) {
                            v.requestFocus();
                        }
                    }
                });
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateChat();
    }


    // It's a big crutch!!!!! need to do normal
    //    @Override
//    protected void onDestroy() {
//        db.close();
//        dbHelper.close();
//
//        super.onDestroy();
//    }
}

