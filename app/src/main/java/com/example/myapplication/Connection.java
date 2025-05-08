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











        Button button = findViewById(R.id.button3);
        button.setOnClickListener(v -> {
            //Intent intent = new Intent(MainActivity.this, DeviceList.class);
            //startActivity(intent);
            // 安全に追記
            /*try (FileOutputStream fos = new FileOutputStream(DeviceList.sharefile, true)) {
                fos.write("-999".getBytes());
                fos.write(",".getBytes()); // 区切り文字が必要な場合
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            DeviceList.sendData("start");
            DeviceList.savefiles(getApplicationContext(),"-999");

        });

    }
}