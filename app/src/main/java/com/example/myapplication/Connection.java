package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Connection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connection);



        Button startbutton = findViewById(R.id.start);
        startbutton.setOnClickListener(v -> {
            DeviceList.sendData("start;");
        });

        Button button99 = findViewById(R.id.button3);
        button99.setOnClickListener(v -> {
            DeviceList.savefiles(getApplicationContext(),"-999");

        });
        Button stopbutton = findViewById(R.id.stop);
        stopbutton.setOnClickListener(v -> {
            DeviceList.sendData("stop;");
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

        Button bibe = findViewById(R.id.bibes);
        bibe.setOnClickListener(v -> {
            DeviceList.sendData("stop;");
            vibrateDevice(getApplicationContext());
        });

    }
    public static void vibrateDevice(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 1秒間（1000ミリ秒）、振動の強さデフォルト
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000); // 非推奨（API < 26向け）
            }
        }
    }
}