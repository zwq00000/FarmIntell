package com.lingya.farmintell.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.lingya.farmintell.R;
import com.lingya.farmintell.adapters.MPLineChartAdapter;
import com.lingya.farmintell.adapters.MainClockAdapter;
import com.lingya.farmintell.adapters.SensorAdapterFactory;
import com.lingya.farmintell.adapters.SensorStatusViewAdapter;

/**
 * 主界面
 * Created by zwq00000 on 15-9-8.
 */
public class MainFragment extends android.support.v4.app.Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private SensorAdapterFactory adapterFactory;
    private SensorStatusViewAdapter sensorAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(int sectionNum, SensorAdapterFactory adapterFactory) {
        MainFragment fragment = new MainFragment();
        fragment.adapterFactory = adapterFactory;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSensorStatusViewAdapter();
    }

    /**
     * 初始化 传感器状态 适配器
     */
    void initSensorStatusViewAdapter() {

        this.sensorAdapter = new SensorStatusViewAdapter();
        sensorAdapter.bindView((ViewGroup) this.getView().findViewById(R.id.statusView));
        sensorAdapter.setViewData(adapterFactory.getBinder());
        adapterFactory.registViewAdapter(sensorAdapter);

        MainClockAdapter mainBlock = new MainClockAdapter();
        mainBlock.bindView((ViewGroup) this.getView().findViewById(R.id.mainView));
        mainBlock.setViewData(adapterFactory.getBinder());
        adapterFactory.registViewAdapter(mainBlock);


        final MPLineChartAdapter lineChart = new MPLineChartAdapter(this.getActivity(),
                (LineChart) this.getView().findViewById(R.id.chart));
        lineChart.setViewData(adapterFactory.getBinder());
        sensorAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && tag instanceof String) {
                    lineChart.showSensorHistory(tag.toString());
                }
            }
        });
        adapterFactory.registViewAdapter(lineChart);
    }

}
