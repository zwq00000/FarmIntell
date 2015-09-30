package com.lingya.farmintell.models;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * 传感器平均值 Created by zwq00000 on 2015/8/2.
 */
public class SensorAverage extends RealmObject {

    /**
     * 传感器Id
     */
    @Index
    private String sensorId;

    /**
     * 小时平均值
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


    /**
     * 采样数量
     */
    private int samplesCount;

    /**
     * 开始时间
     */
    @Index
    private Date startTime;

    /**
     * 结束时间
     */
    @Index
    private Date endTime;

    public SensorAverage(String sensorId, Calendar startCalendar, Calendar endCalendar,
                         int samplesCount,
                         float average) {
        this.sensorId = sensorId;
        this.startTime = startCalendar.getTime();
        this.endTime = endCalendar.getTime();
        this.samplesCount = samplesCount;
        this.average = average;
    }

    public SensorAverage() {
    }


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public int getSamplesCount() {
        return samplesCount;
    }

    public void setSamplesCount(int samplesCount) {
        this.samplesCount = samplesCount;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public float getMaximum() {
        return maximum;
    }

    public void setMaximum(float maximum) {
        this.maximum = maximum;
    }

    public float getMinimum() {
        return minimum;
    }

    public void setMinimum(float minimum) {
        this.minimum = minimum;
    }
}
