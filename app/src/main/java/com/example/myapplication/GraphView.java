package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GraphView extends AppCompatActivity {
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_graph_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




            mChart = findViewById(R.id.lineChart);

            // Grid背景色
            mChart.setDrawGridBackground(true);

            // no description text
            mChart.getDescription().setEnabled(true);

            // Grid縦軸を破線
            XAxis xAxis = mChart.getXAxis();
            xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            YAxis leftAxis = mChart.getAxisLeft();
            // Y軸最大最小設定
            leftAxis.setAxisMaximum(150f);
            leftAxis.setAxisMinimum(0f);
            // Grid横軸を破線
            leftAxis.enableGridDashedLine(10f, 10f, 0f);
            leftAxis.setDrawZeroLine(true);

            // 右側の目盛り
            mChart.getAxisRight().setEnabled(false);

            // add data
        try {
            setData(getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mChart.animateX(2500);
            //mChart.invalidate();

            // don't forget to refresh the drawing
            // mChart.invalidate();
        }

        private void setData(Context context) throws IOException {
            // Entry()を使ってLineDataSetに設定できる形に変更してarrayを新しく作成
            Uri uri = DeviceList.getExistingFileUri(context, "data.txt");
            if (uri == null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "data.txt");
                values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
                values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);
                uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            }

            if (uri == null) return; // 取得できなかった場合は終了


            String line;
            int index = 0;

            List<Double> x1List = new ArrayList<>();
            List<Double> y1List = new ArrayList<>();
            List<Double> x2List = new ArrayList<>();
            List<Double> y2List = new ArrayList<>();

            //String[] lines = rawData.strip().split("\n");

            ArrayList<Entry> values = new ArrayList<>();
            ArrayList<Entry> values2 = new ArrayList<>();
            try (InputStream is = context.getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                Log.d("aaaaa","sss");
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        float y1 = Float.parseFloat(parts[1]);
                        float y2 = Float.parseFloat(parts[3]);
                        values.add(new Entry(index, y1));
                        values2.add(new Entry(index, y2));
                        index++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }




            /*for (int i = 0; i < data.length; i++) {
                values.add(new Entry(i, data[i], null, null));
            }*/

            LineDataSet dataSet1 = new LineDataSet(values, "Y1");
            dataSet1.setColor(Color.RED);
            dataSet1.setCircleRadius(3f);
            dataSet1.setLineWidth(2f);

            LineDataSet dataSet2 = new LineDataSet(values2, "Y2");
            dataSet2.setColor(Color.BLUE);
            dataSet2.setCircleRadius(3f);
            dataSet2.setLineWidth(2f);

            LineData lineData = new LineData(dataSet1, dataSet2);
            mChart.setData(lineData);

            Description desc = new Description();
            desc.setText("Y1とY2の折れ線グラフ");
            mChart.setDescription(desc);

            mChart.invalidate(); // グラフ再描画



            /*if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {

                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set1.setValues(values);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();

            } else {
                // create a dataset and give it a type
                set1 = new LineDataSet(values, "DataSet");

                set1.setDrawIcons(false);
                set1.setColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(0f);
                set1.setDrawFilled(true);
                set1.setFormLineWidth(1f);
                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                set1.setFormSize(15.f);

                set1.setFillColor(Color.BLUE);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1); // add the datasets

                // create a data object with the datasets
                LineData lineData = new LineData(dataSets);

                // set data
                mChart.setData(lineData);
            }*/

        }
    }





