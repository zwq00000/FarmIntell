package com.lingya.farmintell.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 传感器状态 Created by zwq00000 on 2015/6/22.
 */
public class SensorStatus implements Parcelable {

  public static final Creator<SensorStatus> CREATOR = new Creator<SensorStatus>() {
    @Override
    public SensorStatus createFromParcel(Parcel in) {
      return new SensorStatus(in);
    }

    @Override
    public SensorStatus[] newArray(int size) {
      return new SensorStatus[size];
    }
  };
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

  /**
   * 计量单位
   */
  private String unit;

  public SensorStatus() {

  }

  public SensorStatus(String sensorId) {
    this();
    this.id = sensorId;
  }

  protected SensorStatus(Parcel in) {
    id = in.readString();
    name = in.readString();
    value = in.readFloat();
    displayName = in.readString();
    unit = in.readString();
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

  /**
   * Describe the kinds of special objects contained in this Parcelable's marshalled
   * representation.
   *
   * @return a bitmask indicating the set of special object types marshalled by the Parcelable.
   */
  @Override
  public int describeContents() {
    return 0;
  }

  /**
   * Flatten this object in to a Parcel.
   *
   * @param dest  The Parcel in which the object should be written.
   * @param flags Additional flags about how the object should be written. May be 0 or {@link
   *              #PARCELABLE_WRITE_RETURN_VALUE}.
   */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.id);
    dest.writeString(this.name);
    dest.writeFloat(this.value);
    dest.writeString(this.displayName);
    dest.writeString(this.unit);
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }
}
