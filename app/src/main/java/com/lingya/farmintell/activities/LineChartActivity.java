package com.lingya.farmintell.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.lingya.farmintell.R;
import com.lingya.farmintell.adapters.MPLineChartAdapter;
import com.lingya.farmintell.adapters.SensorAdapterFactory;

public class LineChartActivity extends AppCompatActivity {

    private MPLineChartAdapter chartAdapter;
    private SensorAdapterFactory adapterFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        showChart();
    }

    protected void showChart() {
        this.adapterFactory = SensorAdapterFactory.getInstance(this);
        adapterFactory.bindService();

        LineChart lineChart = (LineChart) this.findViewById(R.id.chart);
        chartAdapter = new MPLineChartAdapter(this, lineChart);

        //chartAdapter.bindView((ViewGroup) this.findViewById(R.id.chartView));
        chartAdapter.setViewData(adapterFactory.getBinder());

        adapterFactory.registViewAdapter(chartAdapter);

    }

}
