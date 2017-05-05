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

        Button ENbt = ((Button) findViewById(R.id.btEnter));
        Button RGbt = ((Button) findViewById(R.id.btEnter));

        ENbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SwitchActivity.this, EnterActivity.class);
                SwitchActivity.this.startActivity(intent);
            }
        });

        RGbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SwitchActivity.this, RegisterActivity.class);
                SwitchActivity.this.startActivity(intent);
            }
        });
    }

}
