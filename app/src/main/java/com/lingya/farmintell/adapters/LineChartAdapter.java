package com.lingya.farmintell.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lingya.farmintell.R;
import com.lingya.farmintell.models.SensorSummary;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by zwq00000 on 15-8-24.
 */
public class LineChartAdapter implements ViewAdapter<SensorSummary> {

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
    private static final String TAG = "LineChartAdapter";
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
     * 点的形状设置
     */
    private static final PointStyle[] mPointStyles = new PointStyle[]{
            PointStyle.X, PointStyle.CIRCLE, PointStyle.TRIANGLE, PointStyle.SQUARE, PointStyle.DIAMOND,
            PointStyle.POINT
    };
    /**
     * 序列调色板
     */
    private static int[] PALETTES;
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
    private SensorSummary summary;
    private long resumtTime;

    public LineChartAdapter(Context context) {
        this.mContext = context;
        if (PALETTES == null) {
            PALETTES = context.getResources().getIntArray(R.array.colorPalttes);
        }
        this.mSeriesDataSet = new XYMultipleSeriesDataset();
        this.mRenderer = new XYMultipleSeriesRenderer();
        this.mChartView = createChartView(context, mSeriesDataSet, mRenderer);
        resumtTime = System.currentTimeMillis();
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
        renderer.setZoomButtonsVisible(true);
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
        renderer.setBackgroundColor(CHART_COLOR_BACKGROUND);

        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setLegendHeight(40);
        renderer.setPointSize(5f);
        //renderer.setMargins(new int[]{20, 25, 2, 20});
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

    public void setViewData(SensorSummary sensorSummary) {
        if (sensorSummary == null) {
            throw new NullPointerException("summary is not been null");
        }

        this.summary = sensorSummary;

        for (int i = 0; i < SERIES_NAMES.length; i++) {
            mRenderer.addYTextLabel(i, SERIES_NAMES[i]);
        }

        mRenderer.setYAxisMin(sensorSummary.getMinimum());
        mRenderer.setYAxisMax(sensorSummary.getMaximum());
        initXYSeriesRenderer(mRenderer, 3);

        fillSeriesDataset(summary);

        //绑定一组数据完成时事件
        //mCells.addSubGroupReceivedListener(mSubGroupReceivedListener);
        mChartView.invalidate();
    }

    /**
     * 绑定视图
     *
     * @param container 容器视图
     */
    @Override
    public void bindView(ViewGroup container) {
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
        if (resumtTime < System.currentTimeMillis()) {

        }
        //调用图表更新
        //mChartView.invalidate();
    }

    public void reset() {
        mSeriesDataSet.clear();
        mRenderer.removeAllRenderers();
    }

    public void showSensorHistory(String sensorId) {
        if (this.mChartView != null) {
            SensorAdapterFactory factory = SensorAdapterFactory.getInstance(this.mContext);
            SensorSummary summary = factory.getBinder().get24HourlySummary(sensorId);
            Toast.makeText(mContext, sensorId + " 数量:" + summary.size(), Toast.LENGTH_SHORT).show();

            if (summary.size() > 0) {
                setViewData(summary);
            }
            this.resumtTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        }
    }

    private XYMultipleSeriesDataset fillSeriesDataset(SensorSummary sensorSummary) {
        mSeriesDataSet.clear();

        XYSeries[] xySeries = new XYSeries[3];
        for (int i = 0; i < SERIES_NAMES.length; i++) {
            xySeries[i] = new TimeSeries(SERIES_NAMES[i]);
        }
        float[] averages = sensorSummary.getAverages();
        Date[] times = sensorSummary.getTimeStamps();
        float min = sensorSummary.getMinimum();
        float max = sensorSummary.getMaximum();
        for (int i = 0; i < times.length; i++) {
            xySeries[0].add(i, averages[i]);
            xySeries[1].add(i, min);
            xySeries[2].add(i, max);
        }

        for (int i = 0; i < xySeries.length; i++) {
            mSeriesDataSet.addSeries(xySeries[i]);
        }
        return mSeriesDataSet;
    }

    private void addXYSeries(XYMultipleSeriesDataset dataset, String titles,
                             float[] yValues, int scale) {
        int length = yValues.length;
        XYSeries series = new XYSeries(titles, scale);

        for (int i = 0; i < length; i++) {
            series.add(i, yValues[i]);
        }
        dataset.addSeries(series);
    }
}
