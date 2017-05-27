package com.peregrin.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.peregrin.DBHelper;
import com.peregrin.R;
import com.peregrin.ServerInfo;
import com.peregrin.background.Receiver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> chats = new ArrayList<>();
    SimpleAdapter adapter;

    private ImageButton btNewChat;
    private EditText etNewChat;
    private View.OnClickListener buttonChecked;
    private View.OnClickListener buttonNormal;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSharedPreferences("user", MODE_PRIVATE).getString("phone", "").equals("")) {
            this.startActivity(new Intent(MainActivity.this, SwitchActivity.class));
            this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        MainActivity.this.startService(new Intent(MainActivity.this, Receiver.class));

        db = new DBHelper(MainActivity.this).getWritableDatabase();

        adapter = new SimpleAdapter(MainActivity.this, chats,
                R.layout.chat_entry,
                new String[]{"nickname", "phone"},
                new int[]{R.id.twNickname, R.id.twPhone});
        updateChatsList();
        adapter.notifyDataSetChanged();

        ListView contactsList = (ListView) findViewById(R.id.contactsList);
        contactsList.setAdapter(adapter);
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String login = ((TextView) view.findViewById(R.id.twPhone)).getText().toString();

                MainActivity.this.startActivity(
                        new Intent(
                                MainActivity.this, ChatActivity.class
                        ).putExtra("interlocutor_login", login)
                );
            }
        });

        btNewChat = (ImageButton) findViewById(R.id.btNewChat);
        etNewChat = (EditText) findViewById(R.id.etNewChat);

        buttonChecked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNewChat.getText().toString().length() != 10) {
                    etNewChat.setVisibility(View.GONE);
                    btNewChat.setImageResource(R.drawable.ic_button_new_chat_add);
                    btNewChat.setContentDescription("ic_add");
                    btNewChat.setOnClickListener(buttonNormal);
                } else {
                    new AsyncTask<Void, Void, Void>() {
                        private ProgressDialog progressDialog;

                        private String interlocutor_login;
                        private boolean networkError;
                        private boolean loginCorrect;
                        private boolean alreadyAdded;

                        @Override
                        protected void onPreExecute() {
                            interlocutor_login = etNewChat.getText().toString();

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                            etNewChat.setText("");
                            etNewChat.setVisibility(View.GONE);
                            btNewChat.setImageResource(R.drawable.ic_button_new_chat_add);
                            btNewChat.setContentDescription("ic_add");
                            btNewChat.setOnClickListener(buttonNormal);

                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setMessage(getString(R.string.wait));
                            progressDialog.show();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try (
                                    Socket socket = new Socket(ServerInfo.ADDRESS, ServerInfo.PORT);
                                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())
                            ) {
                                String[] request = new String[3];
                                request[0] = "FIND_USER";
                                request[1] = interlocutor_login;

                                outputStream.writeObject(request);

                                loginCorrect = inputStream.readBoolean();

                                if (loginCorrect) {
                                    String interlocutor_nickname = (String) inputStream.readObject();

                                    DBHelper dbHelper = new DBHelper(MainActivity.this);
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                                    Cursor cursor = db.query("chats_list", null,
                                            "interlocutor_login = ?",
                                            new String[]{interlocutor_login}, null, null, null);

                                    if (!cursor.moveToFirst()) {

                                        ContentValues cv = new ContentValues();
                                        cv.put("interlocutor_login", interlocutor_login);
                                        cv.put("interlocutor_nickname", interlocutor_nickname);

                                        db.insert("chats_list", null, cv);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateChatsList();
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    } else {
                                        alreadyAdded = true;
                                    }

                                    cursor.close();
                                    dbHelper.close();
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                networkError = true;
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            progressDialog.dismiss();

                            if (networkError) {
                                Toasty.error(MainActivity.this, getString(R.string.network_error)).show();
                            } else if (!loginCorrect) {
                                Toasty.warning(MainActivity.this, getString(R.string.not_found_user_with_this_phone)).show();
                            } else if (alreadyAdded) {
                                Toasty.warning(MainActivity.this, getString(R.string.already_added)).show();
                            }
                        }
                    }.execute();
                }
            }
        };

        buttonNormal = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNewChat.setVisibility(View.VISIBLE);
                btNewChat.setImageResource(R.drawable.ic_button_new_chat_remove);
                btNewChat.setContentDescription("ic_remove");
                btNewChat.setOnClickListener(buttonChecked);
            }
        };

        btNewChat.setOnClickListener(buttonNormal);

        etNewChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Example: 095_777_77_77 (10 symbols)
                if (s.toString().length() == 10) {
                    btNewChat.setImageResource(R.drawable.ic_button_new_chat_done);
                    btNewChat.setContentDescription("ic_done");
                } else if (btNewChat.getContentDescription().toString().equals("ic_done")) {
                    btNewChat.setImageResource(R.drawable.ic_button_new_chat_remove);
                    btNewChat.setContentDescription("ic_remove");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log_out:
                getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
                db.delete("chats_list", null, null);
                db.delete("messages", null, null);
                startActivity(new Intent(MainActivity.this, SwitchActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                );
                break;

            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        db.close();
    }

    private void updateChatsList() {
        Cursor chats_list = db.query("chats_list", null, null, null, null, null, null);

        if (chats_list.moveToFirst()) {
            chats.clear();
            do {
                HashMap<String, String> chat = new HashMap<>();
                chat.put("nickname", chats_list.getString(chats_list.getColumnIndex("interlocutor_nickname")));
                chat.put("phone", chats_list.getString(chats_list.getColumnIndex("interlocutor_login")));

                chats.add(chat);
            } while (chats_list.moveToNext());
        }

        chats_list.close();
    }
}
