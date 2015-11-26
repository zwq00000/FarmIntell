package com.lingya.farmintell.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lingya.farmintell.R;
import com.lingya.farmintell.models.SensorStatus;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.models.SensorsConfig;
import com.lingya.farmintell.services.SensorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * MP线图适配器
 * 当  {@link  SensorStatus}  传感器状态更新时,通过 {@link #notifyDataChanged} 方法通知实时更新
 * {@See showSensorHistory} 方法
 * Created by zwq00000 on 15-8-24.
 */
public class MPLineChartAdapter implements ViewAdapter<SensorService.ISensorBinder> {

    /**
     * 图表显示的最大点数
     */
    private static final int MAX_POINTS = 25;

    private static final String TAG = "MPLineChartAdapter";


    /**
     * 序列调色板
     */
    private static int[] PALETTES;
    private final Context context;

    /**
     * 图表对象
     */
    private LineChart lineChart;
    /**
     * 恢复时间,从历史数据恢复为实时数据的时间
     */
    private long resumeTime;
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
    }

    /**
     * 判断 浮点值是否可用
     * @param value
     * @return
     */
    private static boolean isValiable(float value) {
        return !Float.isInfinite(value) && !Float.isNaN(value);
    }

    /**
     * 设置 平均值 图表数据
     *
     * @param config
     * @param averages
     */
    public void setViewData(SensorsConfig.SensorConfig config, float[] averages) {
        if (averages == null) {
            throw new NullPointerException("averages is not been null");
        }

        try {
            fillSeriesDataset(config, averages);
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
        if (resumeTime < System.currentTimeMillis()) {
            if (this.sensorBinder != null) {
                SensorStatusCollection status = sensorBinder.getStatus();
                this.fillSeriesDataset(status);
                lineChart.animate().start();
                lineChart.invalidate();
            }
        }
    }

    /**
     * 获取 传感器配置
     *
     * @return
     */
    private SensorsConfig.SensorConfig getSensorConfig(String sensorId) {
        SensorStatus[] statuses = this.sensorBinder.getStatus().getStatuses();
        if (statuses.length == 0) {
            return null;
        }
        if (TextUtils.isEmpty(sensorId)) {
            return statuses[0].getConfig();
        }
        for (SensorStatus item : statuses) {
            if (item.getId().equals(sensorId)) {
                return item.getConfig();
            }
        }
        return statuses[0].getConfig();
    }

    /**
     * 填充 传感器实时状态 数据图表
     * @param status
     */
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

    /**
     * 填充传感器实时状态 数据图表
     * @param status
     */
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

    /**
     * 创建 线图数据
     * @param config
     * @return
     */
    private LineData createLineData(SensorsConfig.SensorConfig config) {
        return createLineData(config, MAX_POINTS);
    }

    /**
     * 创建 线图数据
     * @param config
     * @param xCount
     * @return
     */
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

    /**
     * 根据 SensorId 获取 调色板颜色
     * @param sensorId
     * @return
     */
    private int getPaletteColor(String sensorId) {
        int index = getSensorIndex(sensorId);
        if (index < 0 || index >= PALETTES.length) {
            return PALETTES[Math.abs(index % PALETTES.length)];
        }
        return PALETTES[index];
    }

    /**
     * 创建 图表数据集
     * @param config
     * @return
     */
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

    /**
     * 重置图表，清理图表数据
     */
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
        if (this.lineChart != null && !TextUtils.isEmpty(sensorId)) {
            this.sensorId = sensorId;
            SensorsConfig.SensorConfig config = getSensorConfig(sensorId);
            SensorAdapterFactory factory = SensorAdapterFactory.getInstance(this.context);
            float[] average = factory.getBinder().get24HourlySummary(sensorId);
            setViewData(config, average);
            this.resumeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        }
    }

    /**
     * 填充 传感器统计值
     *
     * @param averages
     * @throws IOException
     */
    private synchronized void fillSeriesDataset(SensorsConfig.SensorConfig config, float[] averages) throws IOException {
        if (averages == null || lineChart == null) {
            return;
        }
        updateYAxis(config);
        LineData lineData = createLineData(config);
        LineDataSet lineDataSet = lineData.getDataSets().get(0);
        for (int i = 0; i < averages.length; i++) {
            float value = averages[i];
            if (isValiable(averages[i])) {
                lineDataSet.addEntry(new Entry(value, i));
            }
        }

        lineChart.setData(lineData);
    }

}
