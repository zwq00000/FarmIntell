package com.lingya.farmintell.modbus;

import com.redriver.modbus.Holder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Modbus 寄存器
 *
 * Created by zwq00000 on 2015/6/21.
 */
public class Register implements Holder<Short> {

  /**
   * 数据是否已经改变
   */
  private boolean isChanged = false;

  /**
   * 从站Id    {@value 1~255}
   */
  private byte slaveId = 0;

  /**
   * 型号
   */
  private String model = "";

  /**
   * 从站 传感器集合
   */
  private Sensor[] sensors;

  public Register(byte slaveId, String model, Sensor[] sensors) {
    if (sensors == null) {
      throw new IllegalArgumentException("sensors isnot been null");
    }
    this.setSlaveId(slaveId);
    this.setModel(model);
    this.setSensors(sensors);
  }

  /**
   * 更新传感器Id
   */
  public void updateSensorId() {
    if (sensors != null && sensors.length > 0 && sensors[0].id == null) {
      for (int i = 0; i < sensors.length; i++) {
        Sensor sensor = sensors[i];
        sensor.id = this.getSlaveId() + "-" + i;
      }
    }
  }

  /**
   * 从站Id    {@value 1~255}
   */
  public byte getSlaveId() {
    return slaveId;
  }

  /**
   * 设置 从站Id    {@value 1~255}
   */
  public void setSlaveId(byte slaveId) {
    this.slaveId = slaveId;
  }

  /**
   * 寄存器数量
   */
  public short getCount() {
    return (short) getSensors().length;
  }

  /**
   * 设置 寄存器 数值
   */
  @Override
  public void setValue(int offset, Short value) {
    Sensor sensor = this.getSensors()[offset];
    if (sensor.getRawValue() != value) {
      sensor.setRawValue(value);
      isChanged = true;
    }
  }

  /**
   * 读取寄存器 原始值
   */
  @Override
  public Short getValue(int offset) {
    return this.getSensors()[offset].getRawValue();
  }

  /**
   * 获取 传感器值
   */
  public Number getSensorValue(int offset) {
    return this.getSensors()[offset].getValue();
  }

  /**
   * 寄存器 起始位置
   */
  @Override
  public short getStartNum() {
    return 0;
  }


  /**
   * 读取之前 重置寄存器数值
   */
  @Override
  public void reset() {
    for (Sensor sensor :
        getSensors()) {
      sensor.reset();
    }
    isChanged = false;
  }

  /**
   * 获取 JSON 格式的 传感器 数据
   */
  public String toJson() throws JSONException {
    JSONObject root = new JSONObject();
    for (Sensor register : getSensors()) {
      float regValue = register.getValue();
      if (Float.isNaN(regValue)) {
        root.put(register.getName(), "NaN");
      } else {
        root.put(register.getName(), regValue);
      }
    }
    root.put("slaveId", this.getSlaveId());
    return root.toString();
  }

  public String getModel() {
    return model;
  }

  /**
   * 设置 传感器型号
   */
  public void setModel(String model) {
    this.model = model;
  }

  /**
   * 寄存器状态
   */
  public Sensor[] getSensors() {
    updateSensorId();
    return sensors;
  }

  /**
   * 设置 传感器集合
   */
  public void setSensors(Sensor[] sensors) {
    if (sensors != this.sensors) {
      this.sensors = sensors;
      updateSensorId();
    }
  }

  /**
   * 数据是否已经改变
   */
  public boolean isChanged() {
    return isChanged;
  }

  /**
   * 传感器
   */
  public static class Sensor {
    /**
     * 索引
     */
    private String id;

    /**
     * 实际数值系数因子
     */
    private float factor;
    /**
     * 寄存器原始值
     */
    private short rawValue;
    /**
     * 寄存器名称
     */
    private String name;
    /**
     * 获取传感器值 rawValue * Factor
     */
    private float value;

    public Sensor() {
      this.name = "";
      this.rawValue = 0;
      this.factor = 1;
    }

    public Sensor(String id, String name, float factor) {
      this.id = id;
      this.name = name;
      this.rawValue = 0;
      this.factor = factor;
    }

    /**
     * 获取寄存器名称
     */
    public String getName() {
      return name;
    }

    /**
     * 设置寄存器名称
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     * 获取传感器值 rawValue * Factor
     */
    public float getValue() {
      return value;
    }

    /**
     * 实际数值系数因子
     */
    public float getFactor() {
      return factor;
    }

    /**
     * 设置 实际数值 转换系数
     */
    public void setFactor(float factor) {
      this.factor = factor;
    }

    /**
     * 获取寄存器 原始值
     */
    public short getRawValue() {
      return rawValue;
    }

    /**
     * 设置 寄存器值
     */
    protected void setRawValue(short rawValue) {
      this.rawValue = rawValue;
      this.value = this.rawValue * this.getFactor();
    }

    /**
     * 获取 传感器 Id
     */
    public String getId() {
      return id;
    }

    /**
     * 传感器索引
     */
    public void setId(String id) {
      this.id = id;
    }

    /**
     * 重置传感器值
     */
    public void reset() {
      this.setRawValue(Short.MIN_VALUE);
      this.value = Float.NaN;
    }
  }
}
