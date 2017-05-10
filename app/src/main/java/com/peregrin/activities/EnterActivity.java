package com.peregrin.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import static android.content.SharedPreferences.*;

public class EnterActivity extends AppCompatActivity {
    EditText user_phone;
    EditText user_password;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setLogo(R.mipmap.ic_launcher);

        user_phone = (EditText) findViewById(R.id.enter_phone);
        user_password = (EditText) findViewById(R.id.enter_password);
        bt = (Button) findViewById(R.id.enter_bt);

        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog progressDialog;

                    private String phone;
                    private String password;
                    private String nickname;

                    private boolean enterSuccessful;
                    private boolean networkError = false;

                    @Override
                    protected void onPreExecute() {
                        progressDialog = new ProgressDialog(EnterActivity.this);
                        progressDialog.setMessage("Подождите...");
                        progressDialog.show();

                        phone = user_phone.getText().toString();
                        password = user_password.getText().toString();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        try (
                                Socket socket = new Socket(ServerInfo.ADDRESS, ServerInfo.PORT);
                                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())
                        ) {
                            String[] request = new String[3];
                            request[0] = "USER_ENTER";
                            request[1] = phone;
                            request[2] = password;

                            outputStream.writeObject(request);

                            if (enterSuccessful = inputStream.readBoolean()) {
                                nickname = (String) inputStream.readObject();
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            Log.e("Peregrin", e.getMessage());
                            networkError = true;
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        progressDialog.dismiss();

                        if (networkError) {
                            Toasty.error(EnterActivity.this, getString(R.string.network_error)).show();
                        } else if (!enterSuccessful) {
                            Toasty.warning(
                                    EnterActivity.this,
                                    getString(R.string.wrong_password),
                                    Toast.LENGTH_LONG)
                                    .show();

                        } else {
                            Editor editor = getPreferences(MODE_PRIVATE).edit();
                            editor.putString("phone", phone);
                            editor.putString("nickname", nickname);
                            editor.apply();

                            startActivity(new Intent(EnterActivity.this, MainActivity.class));
                        }
                    }
                }.execute();
            }
        });
    }
}
