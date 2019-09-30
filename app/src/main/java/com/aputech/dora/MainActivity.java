package com.aputech.dora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //here is an comment
        Intent intent= new Intent(MainActivity.this,MapsActivity.class);
        startActivity(intent);
    }
}
