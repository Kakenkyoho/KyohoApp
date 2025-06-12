package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Connection extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connection);



        Button startbutton = findViewById(R.id.start);
        startbutton.setOnClickListener(v -> {
            DeviceList.sendData("start;");
            showCountdownDialog();
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
            vibrateDevice(getApplicationContext());
        });


    }
    private void showCountdownDialog() {
        // レイアウトを読み込み
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_countdown, null);

        TextView countdownText = dialogView.findViewById(R.id.dialogCountdown);
        Button okButton = dialogView.findViewById(R.id.buttonDialogOk);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialog.show();
        okButton.setOnClickListener(v -> dialog.dismiss());
        // 3秒のカウントダウンタイマー
        new CountDownTimer(3000, 1000) {
            int count = 3;

            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText(String.valueOf(count));
                count--;
            }

            @Override
            public void onFinish() {
                countdownText.setText("完了");
                okButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }
    public static void vibrateDevice(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 1秒間（1000ミリ秒）、振動の強さデフォルト
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000);
            }
        }
    }
}