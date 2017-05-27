package com.peregrin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.peregrin.R;

public class SwitchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        findViewById(R.id.btSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchActivity.this.startActivity(new Intent(
                        SwitchActivity.this, EnterActivity.class));
            }
        });

        findViewById(R.id.btRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchActivity.this.startActivity(new Intent(
                        SwitchActivity.this, RegisterActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, getResources().getString(R.string.settings));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent();
                intent.setClass(SwitchActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
