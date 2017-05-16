package com.peregrin.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.peregrin.DBHelper;
import com.peregrin.R;
import com.peregrin.ServerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> chats = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private Button btNewChat;
    private EditText etNewChat;
    private View.OnClickListener buttonChecked;
    private View.OnClickListener buttonNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSharedPreferences("user", MODE_PRIVATE).getString("phone", "").equals("")) {
            this.startActivity(new Intent(MainActivity.this, SwitchActivity.class));
            this.finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        btNewChat = (Button) findViewById(R.id.btNewChat);

        etNewChat = (EditText) findViewById(R.id.etNewChat);

        buttonChecked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNewChat.getText().toString().length() != 10) {
                    etNewChat.setVisibility(View.GONE);
                    btNewChat.setText("+");
                    btNewChat.setOnClickListener(buttonNormal);
                } else {
                    new AsyncTask<Void, Void, Void>() {
                        private String interlocutor_login;
                        private boolean networkError;
                        private boolean loginCorrect;

                        @Override
                        protected void onPreExecute() {
                            interlocutor_login = etNewChat.getText().toString();
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
                                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                                    ContentValues cv = new ContentValues();
                                    cv.put("interlocutor_login", interlocutor_login);
                                    cv.put("interlocutor_nickname", interlocutor_nickname);

                                    database.insert("chats_list", null, cv);

                                    dbHelper.close();
                                }
                            } catch (IOException | ClassNotFoundException e) {
                                networkError = true;
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if (networkError) {
                                Toasty.error(MainActivity.this, getString(R.string.network_error)).show();
                            } else if (!loginCorrect) {
                                Toasty.warning(MainActivity.this, getString(R.string.not_found_user_with_this_phone)).show();
                            } else {
                                etNewChat.setText("");
                                etNewChat.setVisibility(View.GONE);
                                btNewChat.setText("+");
                                btNewChat.setOnClickListener(buttonNormal);
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
                btNewChat.setText("-");
                btNewChat.setOnClickListener(buttonChecked);
            }
        };

        btNewChat.setOnClickListener(buttonNormal);

        etNewChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Example: 095_777_77_77 (10 symbols)
                if (s.toString().length() == 10) {
                    btNewChat.setText("->");
                } else if (btNewChat.getText().toString().equals("->")){
                    btNewChat.setText("-");
                }
            }
        });

        ListView contactsList = (ListView) findViewById(R.id.contactsList);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, chats);
        contactsList.setAdapter(adapter);
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("null", "itemClick: position = " + position + ", id = " + id);
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
                startActivity(new Intent(MainActivity.this, SwitchActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                );
        }

        return super.onOptionsItemSelected(item);
    }
}
