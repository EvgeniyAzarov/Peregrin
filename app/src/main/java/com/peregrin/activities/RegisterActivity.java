package com.peregrin.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.peregrin.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText user_phone = (EditText) findViewById(R.id.phone_number);
        EditText user_nickname = (EditText) findViewById(R.id.nickname);
        EditText user_password = (EditText) findViewById(R.id.password);
        EditText user_password_acception = (EditText) findViewById(R.id.password_accept);
        Button bt = (Button) findViewById(R.id.bt);

        if(user_password.getText().toString()!=user_password_acception.getText().toString()){
            //TODO
        }

        if(SendRegisterRequest(user_phone.getText().toString(), user_nickname.getText().toString(),user_password.getText().toString())==false){
            //TODO
        }

        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, ChatActivity.class);
                RegisterActivity.this.startActivity(intent);
            }
        });
    }

    boolean SendRegisterRequest(String login, String nickname, String password) {
        return true;
    }
}
