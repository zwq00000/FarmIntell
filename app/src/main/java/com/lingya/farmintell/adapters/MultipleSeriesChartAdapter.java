package com.lingya.farmintell.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.lingya.farmintell.models.SensorLog;
import com.lingya.farmintell.models.SensorSummary;
import com.lingya.farmintell.services.SensorService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


/**
 * 多线图 Created by Administrator on 2014/7/10.
 */
public class MultipleSeriesChartAdapter implements ViewAdapter<SensorService.ISensorBinder> {

  /**
   * 图表显示的最大点数
   */
  private static final int MAX_POINTS = 25;

  /**
   * 序列调色板
   */
  private static final int[] PALETTES = new int[]{
      Color.rgb(102, 153, 255),
      Color.rgb(153, 204, 51),
      Color.rgb(255, 204, 0),
      Color.rgb(0, 51, 153),
      Color.rgb(255, 255, 240),
      Color.rgb(255, 255, 224),
      Color.rgb(255, 240, 205),
      Color.rgb(255, 248, 220),
      Color.rgb(255, 245, 238),
      Color.rgb(255, 255, 240),
      Color.YELLOW};
  /**
   * 数轴颜色
   */
  private static final int AXES_COLOR = Color.LTGRAY;
  /**
   * 标签颜色
   */
  private static final int LABELS_COLOR = Color.LTGRAY;
  private static final SimpleDateFormat SHORT_TIME_FORMAT = new SimpleDateFormat("HH:mm");
  /**
   * 图表背景色
   */
  private static final int CHART_COLOR_BACKGROUND = Color.BLACK;
  private static final String[] SERIES_NAMES = new String[]{"平均值", "最大值", "最小值"};
  private static final String TAG = MultipleSeriesChartAdapter.class.getSimpleName();

  private final Context mContext;

  /**
   * 图表对象
   */
  private LineChartView chartView;
  private SensorService.ISensorBinder sensorBinder;
  private HashMap<String, Integer> colorMap = new HashMap<>();

  public MultipleSeriesChartAdapter(Context context) {
    this.mContext = context;
    this.chartView = createChartView(context);
  }

  /**
   * 创建 {@link LineChartView} 对象
   */
  private static LineChartView createChartView(Context context) {
    LineChartView chartView = new LineChartView(context);
    return chartView;
  }


  public void setViewData(SensorService.ISensorBinder sensorBinder) {
    if (sensorBinder == null) {
      throw new NullPointerException("sensorBinder is not been null");
    }

    this.sensorBinder = sensorBinder;

    //绑定一组数据完成时事件
    //mCells.addSubGroupReceivedListener(mSubGroupReceivedListener);
    if (chartView != null) {
      chartView.invalidate();
    }
  }

  private int getLineColor(String id) {
    if (colorMap.containsKey(id)) {
      return colorMap.get(id);
    }
    int color = new Random().nextInt(PALETTES.length);
    colorMap.put(id, color);
    return color;
  }

  /**
   * 绑定视图
   *
   * @param container 容器视图
   */
  @Override
  public void onBindView(ViewGroup container) {
    if (chartView.getParent() == null) {
      if (container.getChildCount() > 0) {
        container.removeAllViews();
      }
      ViewGroup.LayoutParams
          layoutParams =
          new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                     ViewGroup.LayoutParams.MATCH_PARENT);
      container.addView(chartView, layoutParams);
    }
  }

  /**
   * 更新视图
   */
  @Override
  public void notifyDataChanged() {
    //调用图表更新
    chartView.invalidate();
  }

  /**
   * 填充图表数据集已测量数据
   */
  private ArrayList<ChartSet> convertToLineSet(String sensorId, String[] labels, float[] values,
                                               float max,
                                               float min) {
    ArrayList<ChartSet> lineSets = new ArrayList<>(3);
    LineSet lineSet = new LineSet(labels, values);
    lineSet.setColor(getLineColor(sensorId));
    lineSet.setSmooth(true);
    lineSet.setDotsStrokeThickness(0.1f);
    lineSets.add(lineSet);

    float[] maxValues = new float[labels.length];
    Arrays.fill(maxValues, max);
    LineSet maxLineSet = new LineSet(labels, maxValues);
    maxLineSet.setColor(Color.RED);
    maxLineSet.setThickness(0.1f);
    lineSets.add(maxLineSet);

    float[] minValues = new float[labels.length];
    Arrays.fill(minValues, min);
    LineSet minLineSet = new LineSet(labels, minValues);
    minLineSet.setColor(Color.RED);
    minLineSet.setThickness(0.1f);
    lineSets.add(minLineSet);
    return lineSets;
  }

  public void reset() {
    chartView.reset();
  }

  public void showSensorHistory(String sensorId) {
    if (this.chartView != null && sensorBinder != null) {
      SensorSummary summary = this.sensorBinder.get24HourlySummary(sensorId);
      if (summary.size() < 10) {
        showSensorLogs(sensorId);
      } else {
        this.chartView.addData(
            convertToLineSet(sensorId, summary.getTimeLables(), summary.getAverages(),
                             summary.getMaximum(), summary.getMinimum()));
        showChartView((int) summary.getMaximum(), (int) summary.getMinimum());
      }
    }
  }

  private void showSensorLogs(String sensorId) {
    List<SensorLog>
        logs =
        this.sensorBinder.getLastOneDaySensorLogs(sensorId);
    int size = logs.size();
    if (logs.size() <= 0) {
      return;
    }
    float max = Float.MIN_VALUE;
    float min = Float.MAX_VALUE;
    float[] values = new float[size];
    String[] labels = new String[size];
    for (int i = 0; i < logs.size(); i++) {
      SensorLog log = logs.get(i);
      float value = log.getValue();
      values[i] = value;
      labels[i] = SHORT_TIME_FORMAT.format(log.getTime());
      max = Math.max(max, value);
      min = Math.min(min, value);
    }
    this.chartView.addData(convertToLineSet(sensorId, labels, values, max, min));
    showChartView((int) max, (int) min);
  }


  private void showChartView(int max, int min) {
    int range = Math.abs(max - min) / 4;
    int step = range / 10;
    if (range == 0) {
      range = 1;
    }
    if (step == 0) {
      step = 1;
    }
    chartView
        //.setTopSpacing(Tools.fromDpToPx(15))
        //.setBorderSpacing(Tools.fromDpToPx(0))
        .setAxisBorderValues(min - range, max + range, 1)
        .setXLabels(AxisController.LabelPosition.NONE)
        .setYLabels(AxisController.LabelPosition.OUTSIDE)
        //.setLabelsColor(Color.parseColor("#e08b36"))
        .setXAxis(true)
        .setYAxis(false);

    Animation anim = new Animation().setStartPoint(-1, 1);
    this.chartView.show(anim);
  }
}
