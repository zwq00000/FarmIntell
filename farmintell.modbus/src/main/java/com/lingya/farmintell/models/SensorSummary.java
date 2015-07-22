package com.lingya.farmintell.models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zwq00000 on 2015/7/4.
 */
public class SensorSummary {

  private static final SimpleDateFormat shortTimeFormat = new SimpleDateFormat("MM/dd HH");
  /**
   * 采样数量
   */
  private final int samplesCount;
  /**
   * 传感器Id
   */
  private String sensorId;
  /**
   * 测量时间
   */
  private String shortTime;
  /**
   * 算数平均值
   */
  private float average;
  /**
   * 最大值
   */
  private float maximum;
  /**
   * 最小值
   */
  private float minimum;

  public SensorSummary(String sensorId, Date beginTime, int samplesCount, float average,
                       float max, float min) {
    this(sensorId, shortTimeFormat.format(beginTime), samplesCount, average, max, min);
  }

  public SensorSummary(String sensorId, String shortTime, int samplesCount, float average,
                       float max, float min) {
    this.sensorId = sensorId;
    this.shortTime = shortTime;
    this.samplesCount = samplesCount;
    this.average = average;
    this.maximum = max;
    this.minimum = min;
  }

  public String getSensorId() {
    return sensorId;
  }


  public float getAverage() {
    return average;
  }

  public float getMaximum() {
    return maximum;
  }


  public float getMinimum() {
    return minimum;
  }

  public String getShortTime() {
    return shortTime;
  }

  public int getSamplesCount() {
    return samplesCount;
  }
}
