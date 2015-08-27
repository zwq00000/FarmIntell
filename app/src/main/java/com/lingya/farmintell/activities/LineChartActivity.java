package com.lingya.farmintell.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.lingya.farmintell.R;
import com.lingya.farmintell.adapters.LineChartAdapter;
import com.lingya.farmintell.adapters.SensorAdapterFactory;

public class LineChartActivity extends AppCompatActivity {

    private LineChartAdapter chartAdapter;
    private SensorAdapterFactory adapterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        showChart();
    }

    protected void showChart() {
        chartAdapter = new LineChartAdapter(this);
        chartAdapter.bindView((ViewGroup) this.findViewById(R.id.chartView));

        adapterFactory.registViewAdapter(chartAdapter);


    }

}
