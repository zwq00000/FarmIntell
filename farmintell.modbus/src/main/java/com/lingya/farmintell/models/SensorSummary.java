package com.lingya.farmintell.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 传感器值 小时合计
 * Created by zwq00000 on 2015/7/4.
 */
public class SensorSummary {

    private static final SimpleDateFormat SHORT_TIME_FORMAT = new SimpleDateFormat("HH");
    /**
     * 传感器Id
     */
    private String sensorId;

    /**
     * 测量时间
     */
    private Calendar startCalendar;
    /**
     * 算数平均值
     */
    private float[] averages;

    /**
     * 时间标记序列
     */
    private Date[] timeStamps;

    /**
     * 最大值
     */
    private float maximum;
    /**
     * 最小值
     */
    private float minimum;

    private SensorSummary() {
        this.maximum = 0;
        this.minimum = 0;
        this.averages = new float[0];
        startCalendar = Calendar.getInstance();
    }

    public SensorSummary(String sensorId, Date beginTime) {
        this();
        this.sensorId = sensorId;
        startCalendar.setTime(beginTime);
    }

    public String getSensorId() {
        return sensorId;
    }

    public float[] getAverages() {
        return averages;
    }

    public void setAverages(float[] averages) {
        this.averages = averages;
    }

    public float getMaximum() {
        return maximum;
    }

    public void setMaximum(float maxValue) {
        this.maximum = maxValue;
    }

    public float getMinimum() {
        return minimum;
    }

    public void setMinimum(float minValue) {
        this.minimum = minValue;
    }

    public String getShortTime() {
        return SHORT_TIME_FORMAT.format(startCalendar);
    }

    public String[] getTimeLables() {
        int count = timeStamps.length;
        String[] labels = new String[count];
        for (int i = 0; i < count; i++) {
            labels[i] = SHORT_TIME_FORMAT.format(timeStamps[i]);
        }
        return labels;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.sensorId);
        builder.append(" ").append(getShortTime())
                .append(" Max:").append(getMaximum())
                .append(" min:").append(getMinimum()).append(" avg:[");

        for (int i = 0; i < this.averages.length; i++) {
            builder.append(averages[i]).append(",");
        }
        builder.append("]");
        return builder.toString();
    }

    public Date[] getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(Date[] timeStamps) {
        this.timeStamps = timeStamps;
    }

    public int size() {
        if (averages == null) {
            return 0;
        }
        return averages.length;
    }
}
