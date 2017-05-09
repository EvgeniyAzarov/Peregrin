package com.peregrin.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.peregrin.R;

public class SwitchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        Button enterButton = ((Button) findViewById(R.id.btEnter));
        Button registerButton = ((Button) findViewById(R.id.btRegister));

        enterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SwitchActivity.this.startActivity(new Intent(
                        SwitchActivity.this, EnterActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SwitchActivity.this.startActivity(new Intent(
                        SwitchActivity.this, RegisterActivity.class));
            }
        });
    }
}
