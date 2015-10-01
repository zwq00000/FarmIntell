package com.lingya.farmintell.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.lingya.farmintell.R;
import com.lingya.farmintell.models.SensorStatus;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.models.SensorSummary;
import com.lingya.farmintell.models.SensorsConfig;
import com.lingya.farmintell.services.SensorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by zwq00000 on 15-8-24.
 */
public class MPLineChartAdapter implements ViewAdapter<SensorService.ISensorBinder> {

    /**
     * 图表显示的最大点数
     */
    private static final int MAX_POINTS = 25;
    /**
     * X轴 最大值
     */
    private static final double X_MAX = MAX_POINTS + 0.5;
    /**
     * X轴最小值
     */
    private static final double X_MIN = 0.5;
    /**
     * Y轴最大值
     */
    private static final double Y_MAX = 1;
    /**
     * Y轴最小值
     */
    private static final double Y_MIN = 0;
    private static final String TAG = "MPLineChartAdapter";

    /**
     * 数轴颜色
     */
    private static final int AXES_COLOR = Color.LTGRAY;
    /**
     * 标签颜色
     */
    private static final int LABELS_COLOR = Color.LTGRAY;
    /**
     * 图表背景色
     */
    private static final int CHART_COLOR_BACKGROUND = Color.TRANSPARENT;
    private static final String[] SERIES_NAMES = new String[]{"平均值", "最大值", "最小值"};

    /**
     * 序列调色板
     */
    private static int[] PALETTES;
    private final Context context;

    /**
     * 图表对象
     */
    private LineChart lineChart;
    private long resumtTime;
    private SensorService.ISensorBinder sensorBinder;
    /**
     * 选中的传感器Id
     */
    private String sensorId;

    public MPLineChartAdapter(Context context) {
        this.context = context;
        if (PALETTES == null) {
            PALETTES = context.getResources().getIntArray(R.array.colorPalttes);
        }
    }

    public MPLineChartAdapter(Context context, LineChart chart) {
        this(context);
        if (chart == null) {
            throw new IllegalArgumentException("chart is not been null");
        }
        this.lineChart = chart;
        //initChart(context,chart);
    }


    /**
     * 创建 {@link LineChart} 对象
     */
    private static void initChart(Context context, LineChart lineChart) {
        //初始化 Renderer 样式
        // no description text
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        lineChart.setHighlightEnabled(true);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        lineChart.animateX(3000);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.LTGRAY);


        // get the legend (only possible after setting data)
        Legend l = lineChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);

        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);

        XAxis xAxis = lineChart.getXAxis();
        //xAxis.setTypeface(tf);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = lineChart.getAxisLeft();
        //leftAxis.setTypeface(tf);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(200f);
        leftAxis.setDrawGridLines(true);
    }

    public void setViewData(SensorSummary sensorSummary) {
        if (sensorSummary == null) {
            throw new NullPointerException("summary is not been null");
        }

        try {
            fillSeriesDataset(sensorSummary);
            lineChart.invalidate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置视图数据
     *
     * @param viewData
     */
    @Override
    public void setViewData(SensorService.ISensorBinder viewData) {
        this.sensorBinder = viewData;
    }

    /**
     * 绑定视图
     *
     * @param container 容器视图
     */
    @Override
    public void bindView(ViewGroup container) {
        if (lineChart == null) {
            View chartView = container.findViewById(R.id.chart);
            if (chartView != null && chartView.getParent() == null) {
                if (container.getChildCount() > 0) {
                    container.removeAllViews();
                }
            }
            if (lineChart == null) {
                lineChart = new LineChart(context);
            }
            ViewGroup.LayoutParams
                    layoutParams =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(lineChart, layoutParams);
        }
    }

    /**
     * 更新视图
     */
    @Override
    public void notifyDataChanged() {
        if (resumtTime < System.currentTimeMillis()) {
            if (this.sensorBinder != null) {
                SensorStatusCollection status = sensorBinder.getStatus();
                this.fillSeriesDataset(status);
                lineChart.animate().start();
                lineChart.invalidate();
            }
        }
    }

    private synchronized void fillSeriesDataset(SensorStatusCollection status) {
        if (lineChart == null) {
            return;
        }
        if (TextUtils.isEmpty(sensorId)) {
            sensorId = status.getStatuses()[0].getId();
            fillSeriesDataset(status.getStatuses()[0]);
        } else {
            SensorStatus[] statuses = status.getStatuses();
            for (SensorStatus item : statuses) {
                if (item.getId().equals(sensorId)) {
                    fillSeriesDataset(item);
                    return;
                }
            }
        }
    }

    private synchronized void fillSeriesDataset(SensorStatus status) {
        if (lineChart == null) {
            return;
        }

        LineData lineData = initLineData(status.getConfig());

        LineDataSet lineDataSet = lineData.getDataSetByIndex(0);
        moveEntryForward(lineDataSet);
        int index = lineDataSet.getEntryCount();
        float yValue = status.getValue(); //(status.getValue() - config.getMin()) / config.getRange();
        lineDataSet.addEntryOrdered(new Entry(yValue, index));

        updateYAxis(status.getConfig());

        lineChart.setData(lineData);
    }

    /**
     * 更新 Y轴坐标系
     *
     * @param config
     */
    private void updateYAxis(SensorsConfig.SensorConfig config) {
        YAxis axisLeft = lineChart.getAxisLeft();
        axisLeft.setAxisMaxValue(config.getMax());
        axisLeft.setAxisMinValue(config.getMin());
        axisLeft.setDrawAxisLine(true);
        axisLeft.setStartAtZero(false);
    }

    private LineData initLineData(SensorsConfig.SensorConfig config) {
        LineData lineData;
        if (lineChart.getData() == null) {
            ArrayList<String> xVals = new ArrayList<String>();
            for (int i = 0; i < MAX_POINTS; i++) {
                xVals.add((i) + "");
            }
            lineData = new LineData(xVals);
        } else {
            lineData = lineChart.getLineData();
        }
        if (lineData.getDataSetCount() != 1) {
            LineDataSet dataSet = createDataSet(config);
            lineData.clearValues();
            lineData.addDataSet(dataSet);
        }

        updateDataSetStyle(lineData.getDataSets().get(0), config);

        return lineData;

    }

    private LineData createLineData(SensorsConfig.SensorConfig config) {
        return createLineData(config, MAX_POINTS);
    }


    private LineData createLineData(SensorsConfig.SensorConfig config, int xCount) {
        if (xCount < 1) {
            xCount = MAX_POINTS;
        }
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < xCount; i++) {
            xVals.add((i) + "");
        }
        LineData lineData = new LineData(xVals);
        LineDataSet dataSet = createDataSet(config);
        lineData.addDataSet(dataSet);
        return lineData;
    }

    /**
     * 节点前移一个位置
     *
     * @param dataSet
     */
    private void moveEntryForward(LineDataSet dataSet) {
        int count = dataSet.getEntryCount();
        if (count > MAX_POINTS) {
            for (int i = 1; i < count; i++) {
                Entry entry = dataSet.getEntryForXIndex(i);
                Entry lastEntry = dataSet.getEntryForXIndex(i - 1);
                lastEntry.setVal(entry.getVal());
            }
            dataSet.removeEntry(count - 1);
        }
    }

    /**
     * 更新 LineDataSet 样式
     *
     * @param lineDataSet
     * @param config
     */
    private void updateDataSetStyle(LineDataSet lineDataSet, SensorsConfig.SensorConfig config) {
        int color = getPaletteColor(config.getId());
        lineDataSet.setColor(color);
        lineDataSet.setLabel(config.getDisplayName());
        lineDataSet.setFillColor(color);
        //lineDataSet.setHighLightColor(Hsv.dark(color));
        //左侧 Y坐标轴
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Cubic 图
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.2f);
        lineDataSet.setDrawFilled(true);

        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleSize(3f);
        lineDataSet.setFillAlpha(65);

        lineDataSet.setDrawCircleHole(false);

        lineDataSet.setDrawValues(false);
    }

    private int getPaletteColor(String sensorId) {
        int index = getSensorIndex(sensorId);
        if (index < 0 || index >= PALETTES.length) {
            return PALETTES[Math.abs(index % PALETTES.length)];
        }
        return PALETTES[index];
    }

    private LineDataSet createDataSet(SensorsConfig.SensorConfig config) {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        LineDataSet lineDataSet = new LineDataSet(yVals, config.getDisplayName());
        updateDataSetStyle(lineDataSet, config);
        return lineDataSet;
    }

    /**
     * 根据 传感器 Id 获取 索引位置
     *
     * @param sensorId
     * @return
     */
    private int getSensorIndex(String sensorId) {
        try {
            SensorsConfig config = SensorsConfig.getDefaultInstance(context);
            String[] ids = config.getSensorIds();
            for (int i = 0; i < ids.length; i++) {
                if (ids[i].equals(sensorId)) {
                    return i;
                }
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void reset() {
        this.lineChart.clear();
        lineChart.clearValues();
    }

    /**
     * 显示 传感器 历史数据
     *
     * @param sensorId
     */
    public void showSensorHistory(String sensorId) {
        if (this.lineChart != null) {
            this.resumtTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
            this.sensorId = sensorId;

            SensorAdapterFactory factory = SensorAdapterFactory.getInstance(this.context);
            SensorSummary summary = factory.getBinder().get24HourlySummary(sensorId);

            if (summary.size() > 1) {
                setViewData(summary);
            }
            SensorsConfig.SensorConfig config = summary.getSensorConfog();
            Toast.makeText(context, config.getDisplayName() + " 数量:" + summary.size()
                    + " 最大:" + summary.getMaximum()
                    + " 最小:" + summary.getMinimum()
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private synchronized void fillSeriesDataset(SensorSummary sensorSummary) throws IOException {
        if (sensorSummary == null || lineChart == null) {
            return;
        }
        SensorsConfig.SensorConfig config = sensorSummary.getSensorConfog();
        updateYAxis(config);
        LineData lineData = createLineData(config);
        LineDataSet lineDataSet = lineData.getDataSets().get(0);
        float[] averages = sensorSummary.getAverages();
        for (int i = 0; i < averages.length; i++) {
            float value = Math.max(Math.min(averages[i], config.getMax()), config.getMin());
            lineDataSet.addEntry(new Entry(value, i));
        }

        lineChart.setData(lineData);
    }

}
