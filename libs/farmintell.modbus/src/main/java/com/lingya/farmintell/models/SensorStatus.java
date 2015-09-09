package com.lingya.farmintell.models;

/**
 * 传感器状态 Created by zwq00000 on 2015/6/22.
 */
public class SensorStatus {

    private static final String NaNValue = "-";
    private final SensorsConfig.SensorConfig config;
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


    public SensorStatus(SensorsConfig.SensorConfig sensorConfig) {
        this.id = sensorConfig.getId();
        this.name = sensorConfig.getName();
        this.displayName = sensorConfig.getDisplayName();
        this.config = sensorConfig;
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
        return getConfig().formatValue(value);
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
     * 传感器配置
     *
     * @return
     */
    public SensorsConfig.SensorConfig getConfig() {
        return config;
    }
}
