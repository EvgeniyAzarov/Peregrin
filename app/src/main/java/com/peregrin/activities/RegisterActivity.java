package com.peregrin.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.peregrin.R;
import com.peregrin.ServerInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String phone =
                        ((EditText) findViewById(R.id.phone_number))
                                .getText()
                                .toString()
                                .replaceAll(" ", "");
                final String nickname = ((EditText) findViewById(R.id.nickname)).getText().toString();
                final String password = ((EditText) findViewById(R.id.password)).getText().toString();
                final String password_accept = ((EditText) findViewById(R.id.password_accept)).getText().toString();

                if (nickname.equals("") || phone.equals("")) {
                    Toasty.warning(RegisterActivity.this, getString(R.string.fill_in_all_fields)).show();
                } else if (phone.length() != 10) {
                    Toasty.warning(RegisterActivity.this, getString(R.string.incorrect_phone_number)).show();
                } else if (password.length() < 6) {
                    Toasty.warning(RegisterActivity.this, getString(R.string.too_short_password)).show();
                } else if (!password.equals(password_accept)) {
                    Toasty.warning(RegisterActivity.this, getString(R.string.passwords_are_not_equals)).show();
                } else {
                    new AsyncTask<Void, Void, Void>() {
                        private ProgressDialog progressDialog;

                        private boolean registrationSuccessful;
                        private boolean networkError = false;

                        @Override
                        protected void onPreExecute() {
                            progressDialog = new ProgressDialog(RegisterActivity.this);
                            progressDialog.setMessage("Подождите...");
                            progressDialog.show();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try (
                                    Socket socket = new Socket(ServerInfo.ADDRESS, ServerInfo.PORT);
                                    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())
                            ) {
                                String[] request = new String[4];
                                request[0] = "USER_REGISTRATION";
                                request[1] = phone;
                                request[2] = password;
                                request[3] = nickname;

                                outputStream.writeObject(request);

                                registrationSuccessful = inputStream.readBoolean();
                            } catch (IOException | IllegalArgumentException e) {
                                networkError = true;
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            progressDialog.dismiss();

                            if (networkError) {
                                Toasty.error(RegisterActivity.this, getString(R.string.network_error)).show();
                            } else if (!registrationSuccessful) {
                                Toasty.warning(
                                        RegisterActivity.this,
                                        getString(R.string.user_with_this_phone_was_registered),
                                        Toast.LENGTH_LONG)
                                        .show();

                            } else {
                                SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                editor.putString("phone", phone);
                                editor.putString("nickname", nickname);
                                editor.apply();

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                );
                            }
                        }
                    }.execute();
                }
            }
        });
    }
}
