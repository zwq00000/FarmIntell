package com.lingya.farmintell.adapters;

import android.view.ViewGroup;
import android.widget.TextView;

import com.lingya.farmintell.R;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.services.SensorService;

import java.text.SimpleDateFormat;

/**
 * Created by zwq00000 on 2015/8/10.
 */
public class MainClockAdapter implements ViewAdapter<SensorService.ISensorBinder> {

  private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
  private TextView clockView;
  private SensorService.ISensorBinder viewData;

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
  @Override
  public void bindView(ViewGroup container) {
    clockView = (TextView) container.findViewById(R.id.updateClock);

  }

  /**
   * 更新视图
   */
  @Override
  public void notifyDataChanged() {
    if(this.clockView== null || this.viewData == null ){
      return;
    }
    SensorStatusCollection statuses = viewData.getStatus();
    clockView.setText(TIME_FORMAT.format(statuses.getUpdateTime()));
  }
}
