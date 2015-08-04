package com.lingya.farmintell.models;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 传感器状态 Created by zwq00000 on 2015/6/22.
 */
public class SensorStatus {

  private static final NumberFormat VALUE_FORMAT = new DecimalFormat("#.00");
  private static final String NaNValue = "-";
  /**
   * 传感器Id
   */
  private String id;
  /**
   * 传感器名称
   */
  private String name;
  /**
   * 值
   */
  private float value;
  /**
   * 显示名称
   */
  private String displayName;


  public SensorStatus() {

  }

  public SensorStatus(String sensorId) {
    this();
    this.id = sensorId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public float getValue() {
    return value;
  }

  public void setValue(float value) {
    this.value = value;
  }

  public String getFormatedValue() {
    if (Float.isNaN(value)) {
      return NaNValue;
    }
    return VALUE_FORMAT.format(value);
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
