package com.lingya.farmintell.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * 测量数据记录
 * Created by zwq00000 on 2015/6/24.
 */
public class SensorLog extends RealmObject {

    /**
     * 传感器Id
     */
    @Index
    private String sensorId;

    /**
     * 传感器名称 @See SensorType
     */
    @Index
    private String name;

    /**
     * 测量时间
     */
    @Index
    private Date time;

    /**
     * 测量值
     */
    private float value;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
