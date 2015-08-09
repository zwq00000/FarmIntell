package com.lingya.farmintell.models;

import com.lingya.farmintell.modbus.Register;

/**
 * Created by zwq00000 on 2015/7/29.
 */
public class SensorsConfig {

  /**
   * 主机 Id
   */
  private String hostId;

  /**
   * 主机 名称
   */
  private String hostName;

  /**
   * modbus 从站配置
   */
  private Station[] stations;

  private int sensorCount;

  /**
   * 寄存器配置
   */
  private Register[] registers;
  private String[] sensorIds;
  private Sensor[] sensorCache;

  public String getHostId() {
    return hostId;
  }

  public String getHostName() {
    return hostName;
  }

  public Station[] getStations() {
    return stations;
  }

  /**
   * 获取 传感器Id 集合
   */
  public String[] getSensorIds() {
    if (this.sensorIds == null) {
      int count = getSensorsCount();
      sensorIds = new String[count];
      int index = 0;
      for (int t = 0; t < stations.length; t++) {
        Station station = stations[t];
        for (int s = 0; s < station.size(); s++) {
          sensorIds[index] = station.getSensors()[s].getId();
          index++;
        }
      }
    }
    return sensorIds;
  }

  public Sensor[] getSensors() {
    if (this.sensorCache == null) {
      sensorCache = new Sensor[getSensorsCount()];
      int index = 0;
      for (int i = 0; i < this.stations.length; i++) {
        Sensor[] sensors = stations[i].getSensors();
        for (int s = 0; s < sensors.length; s++) {
          sensorCache[index] = sensors[s];
          index++;
        }
      }
    }
    return sensorCache;
  }

  int getSensorsCount() {
    if (sensorCount > 0) {
      return sensorCount;
    }
    int count = 0;
    for (int i = 0; i < stations.length; i++) {
      count += stations[i].size();
    }
    sensorCount = count;
    return sensorCount;
  }

  /**
   * 根据 传感器Id 查找 传感器设置
   */
  public Sensor findSensorConfig(String sensorId) {
    for (Station station : this.stations) {
      for (Sensor sensor : station.getSensors()
          ) {
        if (sensor.getId().equalsIgnoreCase(sensorId)) {
          return sensor;
        }
      }
    }
    return null;
  }

  /**
   * 获取 寄存器配置
   */
  public Register[] getRegisters() {
    if (this.stations == null) {
      throw new NullPointerException("stations is not been null");
    }
    if (registers == null) {
      registers = new Register[this.stations.length];
      for (int i = 0; i < stations.length; i++) {
        registers[i] = stations[i].toRegister();
      }
    }
    return registers;
  }

  public class Station {

    /**
     * 从站Id
     */
    private byte slaveId;

    /**
     * 传感器型号
     */
    private String model;

    /**
     * 传感器
     */
    private Sensor[] sensors;

    public int size() {
      return sensors.length;
    }

    public byte getSlaveId() {
      return slaveId;
    }

    public String getModel() {
      return model;
    }


    public Sensor[] getSensors() {
      return sensors;
    }

    /**
     * 转换为 寄存器配置
     */
    Register toRegister() {
      Register.Sensor mSensors[] = new Register.Sensor[this.sensors.length];
      for (int i = 0; i < mSensors.length; i++) {
        mSensors[i] = this.sensors[i].toSensor();
      }
      return new Register(this.slaveId, this.model, mSensors);
    }
  }

  /**
   * 传感器配置
   */
  public class Sensor {

    /**
     * 传感器 Id
     */
    private String id;


    /**
     * 传感器类型 名称
     */
    private String name;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 数值比例因子
     */
    private float factor;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 量程最小值
     */
    private int min;
    /**
     * 量程 最大值
     */
    private int max;

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getDisplayName() {
      return displayName;
    }

    public String getUnit() {
      return unit;
    }

    public int getMin() {
      return min;
    }

    public int getMax() {
      return max;
    }

    Register.Sensor toSensor() {
      return new Register.Sensor(this.getId(), this.getName(), this.getFactor());
    }

    public float getFactor() {
      return factor;
    }
  }
}
