package com.aputech.dora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handle = new Handler();
        Runnable maps= new Runnable() {
            @Override
            public void run() {
           Intent mapIntent= new Intent(MainActivity.this,MapsActivity.class);
           startActivity(mapIntent);
             }
            };
        handle.postDelayed(maps,1000);
    }
}





