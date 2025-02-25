package com.example.demo01;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Arrays;
import java.util.List;

public class RecordGraphActivity extends AppCompatActivity {

    private LineChart lineChart;
    public LineData data;
    private Thread chartThread;
    private List<Integer> intList;

    public boolean flag = true;
    public int i = 0;
    public int j = 0;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_graph);

        Button backBtn = (Button) findViewById(R.id.back_btn);
        TextView bpm_text = (TextView) findViewById(R.id.bpm_last);
        TextView stress_text = (TextView) findViewById(R.id.stress_last);


        Intent getData = getIntent();
        String chart = getData.getStringExtra("graph");
            lineChart = (LineChart) findViewById(R.id.ecgChart_past);

            lineChart.setNoDataText("No chart data");
            lineChart.setHighlightPerDragEnabled(true);
            lineChart.setTouchEnabled(true);
            lineChart.setDrawGridBackground(true);
            lineChart.setScaleEnabled(true);
            lineChart.setDrawGridBackground(true);
            lineChart.setPinchZoom(true);
            lineChart.setBackgroundColor(Color.WHITE);


            data = new LineData();
            lineChart.setData(data);


            Legend l =lineChart.getLegend();
            l.setForm(Legend.LegendForm.LINE);
            l.setTextColor(Color.WHITE);

            XAxis x = lineChart.getXAxis();
            x.setTextColor(Color.WHITE);
            x.setDrawGridLines(false);
            x.setAvoidFirstLastClipping(true);

            YAxis yl = lineChart.getAxisLeft();
            yl.setTextColor(Color.WHITE);
            yl.setAxisMaximum(500f);
            yl.setDrawGridLines(true);
            YAxis yr = lineChart.getAxisRight();
            yr.setEnabled(false);
            String bpm = getData.getStringExtra("bpm");
            String str = getData.getStringExtra("str");
            bpm_text.setText(bpm);
            stress_text.setText(str);
            dd(chart);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void dd(String b){
        flag = true;
        String[] split = b.split(String.valueOf(','));
        Integer[] intInput = new Integer[split.length];
        for(int i = 0; i < split.length - 1; i++){

            try {

                intInput[i] = Integer.parseInt(split[i]);
                Log.d("ddddddddd:2", Integer.toString(i) + "  "+Integer.toString(intInput[i]));

            }catch(NumberFormatException e){
                Log.d("dddddddd:", "eororor");
            }
        }
        intList = Arrays.asList(intInput);

        i = 0;
        feedMultile();

    }

    ///Graph
    private void feedMultile() {
        if (chartThread != null) chartThread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry();
            }
        };

        chartThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    runOnUiThread(runnable);

                    try {
                        chartThread.sleep(50);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        chartThread.start();
    }

    private void addEntry() {
        i++;
        j++; //추가
        data = lineChart.getData();
        if (data != null) {
            ILineDataSet lineDataSet = data.getDataSetByIndex(0);

            if (lineDataSet == null) {
                lineDataSet = createSet();
                data.addDataSet(lineDataSet);
            }

            try {
                data.addEntry(new Entry((float) j/10, intList.get(i)), 0); //추가
            } catch (NullPointerException e) {
            } catch (ArrayIndexOutOfBoundsException e1) {
            }
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(14);
            if (i < intList.size()) {
                lineChart.moveViewToX(data.getEntryCount());
            } else {
                flag = false;
            }
        }
    }


    private ILineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");

        lineChart.setDoubleTapToZoomEnabled(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setDescription(description);
        lineChart.invalidate();

        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 177));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        return set;
    }


}