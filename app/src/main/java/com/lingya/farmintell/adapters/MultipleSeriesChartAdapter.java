package com.lingya.farmintell.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;

import com.lingya.farmintell.models.SensorSummary;
import com.lingya.farmintell.services.SensorService;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * 多线图 Created by Administrator on 2014/7/10.
 */
public class MultipleSeriesChartAdapter implements ViewAdapter<SensorService.ISensorBinder> {

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
  private static final double Y_MAX = 1.5;
  /**
   * Y轴最小值
   */
  private static final double Y_MIN = -1.5;

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
  /**
   * 图表背景色
   */
  private static final int CHART_COLOR_BACKGROUND = Color.BLACK;
  private static final String[] SERIES_NAMES = new String[]{"平均值", "最大值", "最小值"};
  /**
   * 点的形状设置
   */
  private static final PointStyle[] mPointStyles = new PointStyle[]{
      PointStyle.X, PointStyle.CIRCLE, PointStyle.TRIANGLE, PointStyle.SQUARE, PointStyle.DIAMOND,
      PointStyle.POINT
  };
  private final Context mContext;
  /**
   * 图表渲染器
   */
  private final XYMultipleSeriesRenderer mRenderer;
  /**
   * 数据集
   */
  private final XYMultipleSeriesDataset mSeriesDataSet;
  /**
   * 图表对象
   */
  private GraphicalView mChartView;
  private SensorService.ISensorBinder sensorBinder;

  public MultipleSeriesChartAdapter(Context context) {
    this.mContext = context;
    this.mSeriesDataSet = new XYMultipleSeriesDataset();
    this.mRenderer = new XYMultipleSeriesRenderer();
    this.mChartView = createChartView(context, mSeriesDataSet, mRenderer);
  }

  /**
   * 创建 {@link GraphicalView} 对象
   */
  private static GraphicalView createChartView(Context context,
                                               XYMultipleSeriesDataset dataset,
                                               XYMultipleSeriesRenderer renderer) {
    //初始化 Renderer 样式
    initChartRendererStyle(renderer);
    if (dataset.getSeriesCount() == 0) {
      initXYSeriesRenderer(renderer, 1);
      dataset.addSeries(0, new XYSeries("-"));
    }
    GraphicalView chartView = ChartFactory.getLineChartView(context, dataset, renderer);
    //todo chartView 背景色由容器确定，在此不设置背景
    //chartView.setBackgroundColor(CHART_COLOR_BACKGROUND);
    return chartView;
  }

  /**
   * 初始化图表渲染器属性
   */
  private static XYMultipleSeriesRenderer initChartRendererStyle(
      XYMultipleSeriesRenderer renderer) {
    //设置x轴显示25个点,根据setChartSettings的最大值和最小值自动计算点的间隔
    renderer.setXLabels(MAX_POINTS);
    //设置y轴显示4个点,根据setChartSettings的最大值和最小值自动计算点的间隔
    renderer.setYLabels(4);
    //是否显示网格
    renderer.setShowGrid(true);
    //刻度线与刻度标注之间的相对位置关系
    renderer.setXLabelsAlign(Paint.Align.RIGHT);
    //刻度线与刻度标注之间的相对位置关系
    renderer.setYLabelsAlign(Paint.Align.RIGHT);
    //是否显示放大缩小按钮
    renderer.setZoomButtonsVisible(false);
    // 屏蔽移动
    renderer.setPanEnabled(true, false);
    renderer.setZoomEnabled(false, false);

    //mRenderer.setYTitle("百分比");
    renderer.setXAxisMin(X_MIN);
    renderer.setXAxisMax(X_MAX);
    renderer.setYAxisMin(Y_MIN);
    renderer.setYAxisMax(Y_MAX);
    renderer.setAxesColor(AXES_COLOR);
    renderer.setLabelsColor(LABELS_COLOR);

    renderer.setAxisTitleTextSize(16);
    renderer.setChartTitleTextSize(20);
    renderer.setLabelsTextSize(15);
    renderer.setLegendTextSize(15);
    renderer.setLegendHeight(40);
    renderer.setPointSize(5f);
    renderer.setMargins(new int[]{20, 25, 2, 20});
    return renderer;
  }

  /**
   * 初始化 XY 序列渲染器
   */
  private static void initXYSeriesRenderer(XYMultipleSeriesRenderer renderer, int lines) {
    renderer.removeAllRenderers();
    for (int i = 0; i < lines; i++) {
      XYSeriesRenderer r = new XYSeriesRenderer();
      r.setColor(PALETTES[i]);
      renderer.addSeriesRenderer(r);
    }
  }

  public void setViewData(SensorService.ISensorBinder sensorBinder) {
    if (sensorBinder == null) {
      throw new NullPointerException("sensorBinder is not been null");
    }

    this.sensorBinder = sensorBinder;

    for (int i = 0; i < SERIES_NAMES.length; i++) {
      mRenderer.addYTextLabel(i, SERIES_NAMES[i]);
    }
    initXYSeriesRenderer(mRenderer, 3);

    //绑定一组数据完成时事件
    //mCells.addSubGroupReceivedListener(mSubGroupReceivedListener);
    if (mChartView != null) {
      mChartView.invalidate();
    }
  }

  /**
   * 绑定视图
   *
   * @param container 容器视图
   */
  @Override
  public void onBindView(ViewGroup container) {
    if (mChartView.getParent() == null) {
      if (container.getChildCount() > 0) {
        container.removeAllViews();
      }
      ViewGroup.LayoutParams
          layoutParams =
          new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                     ViewGroup.LayoutParams.MATCH_PARENT);
      container.addView(mChartView, layoutParams);
    }
  }

  /**
   * 更新视图
   */
  @Override
  public void notifyDataChanged() {
    //调用图表更新
    mChartView.invalidate();
  }

  /**
   * 填充图表数据集已测量数据
   */
  private XYMultipleSeriesDataset fillSeriesDataset(SensorSummary[] sensorSummaries) {
    mSeriesDataSet.clear();

    XYSeries[] xySeries = new XYSeries[3];
    for (int i = 0; i < SERIES_NAMES.length; i++) {
      xySeries[i] = new XYSeries(SERIES_NAMES[i]);
    }

    for (int k = 0; k < sensorSummaries.length; k++) {
      xySeries[0].add(k + 1, k, sensorSummaries[k].getAverage());
      xySeries[1].add(k + 1, k, sensorSummaries[k].getMaximum());
      xySeries[2].add(k + 1, k, sensorSummaries[k].getMinimum());
    }
    for (int i = 0; i < xySeries.length; i++) {
      mSeriesDataSet.addSeries(xySeries[i]);
    }
    return mSeriesDataSet;
  }

  public void reset() {
    mSeriesDataSet.clear();
    mRenderer.removeAllRenderers();
  }

  public void showSensorHistory(String sensorId) {
    if (this.mChartView != null || sensorBinder != null) {
      SensorSummary[] sensorSummaries = this.sensorBinder.get24HourlySummary(sensorId);
      fillSeriesDataset(sensorSummaries);

    }
  }
}
