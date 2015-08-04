package com.lingya.farmintell.adapters;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lingya.farmintell.R;
import com.lingya.farmintell.models.SensorStatus;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.services.SensorService;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by zwq00000 on 2015/7/10.
 */
public class SensorStatusViewAdapter implements ViewAdapter<SensorService.ISensorBinder> {

  private static final int[] viewIds = new int[]{
      R.id.tv11,
      R.id.tv12,
      R.id.tv13,
      R.id.tv14,
      R.id.tv21,
      R.id.tv22,
      R.id.tv23,
      R.id.tv24,
      R.id.tv21,
      R.id.tv22,
      R.id.tv23,
      R.id.tv24
  };
  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
  private final Handler handler;
  private ViewGroup containerView;
  private SensorService.ISensorBinder viewData;

  /**
   * 构造方法
   */
  public SensorStatusViewAdapter(ViewGroup container) {
    this();
    this.containerView = container;
  }

  /**
   * 构造方法
   */
  public SensorStatusViewAdapter() {
    handler = new Handler();
  }

  /**
   * 设置视图数据
   */
  @Override
  public void setViewData(SensorService.ISensorBinder viewData) {
    this.viewData = viewData;
  }

  /**
   * 绑定视图
   *
   * @param container 容器视图
   */
  public void onBindView(ViewGroup container) {
    containerView = container;
  }

  /**
   * 更新视图
   */
  @Override
  public void notifyDataChanged() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        bind();
      }
    });
  }

  public void setOnClickListener(View.OnClickListener onClickListener) {
    for (int viewId :
        this.viewIds) {
      TextView textView = ((TextView) containerView.findViewById(viewId));
      if (textView != null) {
        textView.setOnClickListener(onClickListener);
      }
    }
  }

  /**
   * 绑定界面任务概要数据
   */
  private void bind() {
    if (this.viewData == null || containerView == null) {
      return;
    }
    final SensorStatusCollection statusCollection = viewData.getStatus();

    int count = Math.min(viewIds.length, statusCollection.size());
    List<SensorStatus>
        statuses =
        statusCollection.getStatuses();
    for (int i = 0; i < count; i++) {
      int viewId = viewIds[i];
      TextView textView = ((TextView) containerView.findViewById(viewId));
      SensorStatus status = statuses.get(i);
      textView.setText(status.getFormatedValue());
      textView.setTag(status.getId());
    }
    TextView clockView = (TextView) containerView.findViewById(R.id.updateClock);
    clockView.setText(TIME_FORMAT.format(statusCollection.getUpdateTime()));
  }
}
