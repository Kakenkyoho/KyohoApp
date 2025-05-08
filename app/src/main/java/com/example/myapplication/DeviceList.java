package com.example.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DeviceList extends AppCompatActivity {
    private final String TAGS="DEBUGSSSS";


    private ListView listView;
    private ArrayAdapter<String> adapter;
    private final List<String> deviceList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private Map<String, BluetoothDevice> bondedDeviceMap = new HashMap<>();
    private static boolean  _isConnected = false;
    private BufferedReader reader;
    private static BluetoothSocket socket;
    public static File sharefile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        listView = findViewById(R.id.deviceview);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(adapter);
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(DeviceList.this, MainActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = adapter.getItem(position);
            BluetoothDevice selectedDevice = bondedDeviceMap.get(selectedItem);
            /*new Thread(new Runnable() {
                @Override
                public void run() {
                    connectToDevice(selectedDevice);
                }
            }).start();*/
            connectToDevice(selectedDevice);
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = adapter.getItem(position); // 長押しされたアイテムを取得
                BluetoothDevice selectedDevice = bondedDeviceMap.get(selectedItem); // デバイスを取得

                if (selectedDevice != null) {
                    // 接続中であれば切断処理を行う
                    if (_isConnected) {
                        closeConnection();
                        Toast.makeText(DeviceList.this, "接続切断: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
                return true; // イベントを消化したことを示す
            }
        });

        bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            Log.d(TAGS, "Name: " + device.getName() + ", Address: " + device.getAddress());
        }

        showPairedDevices();
    }


    private void showPairedDevices() {


        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName();
                String address = device.getAddress();
                if (name == null) name = "Unknown";


                String deviceInfo = name + " (" + address + ")";

                deviceList.add(deviceInfo);
                bondedDeviceMap.put(deviceInfo, device);

            }
            adapter.notifyDataSetChanged();
        } else {
            deviceList.add("No paired devices found");
            adapter.notifyDataSetChanged();
        }
    }
    private void connectToDevice(BluetoothDevice device) {
        new Thread(() -> {
            try {
                if(_isConnected) {
                    closeConnection();
                    return;
                }

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP用UUID
                socket = device.createRfcommSocketToServiceRecord(uuid);
                bluetoothAdapter.cancelDiscovery(); // 接続前にスキャン停止
                socket.connect(); // 接続を試みる
                _isConnected=true;

                runOnUiThread(() -> Toast.makeText(this, "接続成功: " + device.getName(), Toast.LENGTH_SHORT).show());
                initStream();
                //File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                //sharefile = new File(documentsDir, "received_data2.txt");


                if(_isConnected){
                    Intent intent = new Intent(DeviceList.this, Connection.class);
                    startActivity(intent);
                }


                while (_isConnected) {

                    String data = receiveData();
                    //sendData("aaaaaaa");
                    if (data != null) {
                        Log.d(TAGS, "Received: " + data);
                        //saveToFile(data);
                        savefiles(getApplicationContext(),data);

                        //writeFile(sharefile, data);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "接続失敗", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void initStream() {
        try {
            InputStream inputStream = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
            _isConnected = false;
        }
    }
    public String receiveData() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            _isConnected = false;
            return null;
        }
    }


    private void saveToFile(String data) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "received_data.txt");
        values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        ContentResolver resolver = getContentResolver();
        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

        try (OutputStream os = resolver.openOutputStream(uri, "wa")) { // 'wa' is append mode
            if (os != null) {
                os.write((data + "\n").getBytes()); // Appending data with a newline
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // データを送信する
    public static void sendData(String data) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write((data + "\n").getBytes()); // ESP32側が改行でデータを読み取る場合
        } catch (IOException e) {
            e.printStackTrace();
            _isConnected = false;
        }
    }

    // 接続を終了する
    public void closeConnection() {
        try {
            if (reader != null) {
                reader.close(); // BufferedReaderをクローズ
            }
            socket.getInputStream().close();
            socket.getOutputStream().close();
            socket.close();
            _isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeConnection(); // アクティビティ終了時に接続をクローズ
    }


    public static void writeFile(File file, String content) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("書き込もうとしたファイルが存在しない、またはファイルでない: " + file.getAbsolutePath());
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(content.getBytes());
        }
    }
    private static Uri getExistingFileUri(Context context,String fileName) {
        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Files.getContentUri("external");

        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=? AND " +
                MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{
                fileName,
                Environment.DIRECTORY_DOCUMENTS + "/"
        };

        try (Cursor cursor = resolver.query(collection, new String[]{MediaStore.MediaColumns._ID}, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                return ContentUris.withAppendedId(collection, id);
            }
        }
        return null;

    }
        public static void savefiles(Context context,String data){
            String fileName = "recei2ved_data.txt";
            Uri uri = getExistingFileUri(context ,fileName);


            if (uri == null) {
                // ファイルが存在しないなら新規作成
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
                values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);
                uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            }

            if (uri != null) {
                try (OutputStream os = context.getContentResolver().openOutputStream(uri, "wa")) {
                    if (os != null) {
                        os.write((data + "\n").getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }





    /*private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAGS, "接続成功");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAGS, "切断されました");
            }
        }



        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                UUID serviceUUID = service.getUuid();
                Log.d(TAGS, "Service UUID: " + serviceUUID.toString());

                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    UUID charUUID = characteristic.getUuid();
                    Log.d(TAGS, "  Characteristic UUID: " + charUUID.toString());
                }
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            byte[] value = characteristic.getValue();
            Log.d("BLE", "Received: " + new String(value));
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();
            String received = new String(value);
            Log.d("BLEREAD", "受信: " + received);
        }
    };

    private void sendData(BluetoothGatt gatt, UUID serviceUUID, UUID charUUID, String data) {
        BluetoothGattService service = gatt.getService(serviceUUID);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(charUUID);

        characteristic.setValue(data.getBytes());
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT); // または WRITE_TYPE_NO_RESPONSE
        gatt.writeCharacteristic(characteristic);
    }*/




