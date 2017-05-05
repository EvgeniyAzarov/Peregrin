package com.peregrin.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.peregrin.R;

import es.dmoral.toasty.Toasty;

public class EnterActivity extends AppCompatActivity {
    EditText user_phone;
    EditText user_password;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        user_phone = (EditText) findViewById(R.id.enter_phone);
        user_password = (EditText) findViewById(R.id.enter_password);
        bt = (Button) findViewById(R.id.enter_bt);

        bt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkEnterParameters(user_phone.getText().toString(), user_password.getText().toString())){
                    Intent intent = new Intent(EnterActivity.this, ChatActivity.class);
                    EnterActivity.this.startActivity(intent);
                }
                else{
                    Toasty.warning(EnterActivity.this,getString(R.string.wrong_password)).show();
                }
            }
        });
    }
    boolean checkEnterParameters(String phone, String password){
        return true;
    }
}
