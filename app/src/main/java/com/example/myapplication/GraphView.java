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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

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
    public float data1;
    public float data2;
    public float data3;
    public float data4;
    private LineDataSet[] dataSets = new LineDataSet[4];

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



        setupChart();


        }



    private void setupChart() {
            // no description text

            mChart.setTouchEnabled(true);

            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);
            mChart.setDrawGridBackground(false);

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(true);

            // set an alternative background color
            mChart.setBackgroundColor(Color.LTGRAY);

            LineData data = new LineData();
            data.setValueTextColor(Color.BLACK);

            // add empty data
            mChart.setData(data);

            //  ラインの凡例の設定
            Legend l = mChart.getLegend();
            l.setForm(Legend.LegendForm.LINE);
            l.setTextColor(Color.BLACK);

            XAxis xl = mChart.getXAxis();
            xl.setTextColor(Color.BLACK);
            //xl.setLabelsToSkip(9);

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.setTextColor(Color.BLACK);
            leftAxis.setAxisMaxValue(3.0f);
            leftAxis.setAxisMinValue(-3.0f);
            leftAxis.setStartAtZero(false);
            leftAxis.setDrawGridLines(true);

            YAxis rightAxis = mChart.getAxisRight();
            rightAxis.setEnabled(false);



        mChart.setData(data);
    }

    private void addEntryToChart(float d1, float d2, float d3, float d4) {
        LineData data = mChart.getData();
        ILineDataSet set1 = data.getDataSetByIndex(0);
        //2本目のグラフ（インデックスを1に）
        ILineDataSet set2 = data.getDataSetByIndex(1);

        ILineDataSet set3 = data.getDataSetByIndex(0);
        //2本目のグラフ（インデックスを1に）
        ILineDataSet set4 = data.getDataSetByIndex(1);

        data.addEntry(new Entry(set1.getEntryCount(), d1), 0);
        data.addEntry(new Entry(set2.getEntryCount(), d2), 1);
        data.addEntry(new Entry(set3.getEntryCount(), d3), 2);
        data.addEntry(new Entry(set4.getEntryCount(), d4), 3);

        data.notifyDataChanged();
        mChart.notifyDataSetChanged();
        mChart.setVisibleXRangeMaximum(120);
        mChart.moveViewToX(data.getEntryCount());
        mChart.invalidate();
    }
    public void setData(String rawData){
            String[] parts = rawData.split(",");
            if (parts.length == 4) {
                try {
                    data1 = Float.parseFloat(parts[0]);
                    data2 = Float.parseFloat(parts[1]);
                    data3 = Float.parseFloat(parts[2]);
                    data4 = Float.parseFloat(parts[3]);

                    addEntryToChart(data1, data2, data3, data4);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }





