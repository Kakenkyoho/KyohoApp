package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.io.IOException;

public class Connection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connection);



        Button startbutton = findViewById(R.id.start);
        startbutton.setOnClickListener(v -> {
            DeviceList.sendData("start");
        });

        Button button99 = findViewById(R.id.button3);
        button99.setOnClickListener(v -> {
            DeviceList.savefiles(getApplicationContext(),"-999");

        });
        Button stopbutton = findViewById(R.id.stop);
        stopbutton.setOnClickListener(v -> {
            DeviceList.sendData("stop");
        });

        Button initbutton = findViewById(R.id.initialize);
        initbutton.setOnClickListener(v -> {
            //データ初期化
            DeviceList.clearFile(getApplicationContext(),"data.txt");

        });

        Button backbutton = findViewById(R.id.button4);
        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(Connection.this, MainActivity.class);
            startActivity(intent);

        });

    }
}