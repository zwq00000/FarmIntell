package com.lingya.farmintell.models;

import com.lingya.farmintell.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.Date;
import java.util.Iterator;

/**
 * 传感器状态集合 Created by zwq00000 on 2015/6/29.
 */
public class SensorStatusCollection {

    /**
     * 空集合
     */
    public static final SensorStatusCollection Empty = new SensorStatusCollection();

    /**
     * 通讯秘钥
     */
    private String token;

    /**
     * 传感器状态集合
     */
    private SensorStatus[] statuses;

    private Date updateTime;

    /**
     * 终端Id
     */
    private String hostId;

    /**
     * 终端名称
     */
    private String hostName;

    public SensorStatusCollection(SensorsConfig config) {
        this();
        if (config == null) {
            throw new IllegalArgumentException("config is not been null");
        }
        this.hostId = config.getHostId();
        this.hostName = config.getHostName();
        this.statuses = new SensorStatus[config.getSensorsCount()];
        SensorsConfig.SensorConfig[] sensorConfigs = config.getSensors();
        for (int i = 0; i < sensorConfigs.length; i++) {
            statuses[i] = new SensorStatus(sensorConfigs[i]);
        }
    }

    private SensorStatusCollection() {
        this.setUpdateTime(new Date());
        statuses = new SensorStatus[0];
    }

    public SensorStatus[] getStatuses() {
        return this.statuses;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 更新传感器状态值
     *
     * @param values
     */
    public void updateSensorStatus(Iterator<Float> values) {
        if (values == null) {
            throw new IllegalArgumentException("registers is not been null");
        }

        int i = 0;
        while (values.hasNext()) {
            statuses[i++].setValue(values.next());
        }
    }

    /**
     * 转换为 JSON
     *
     * @return
     * @throws JSONException
     */
    public String toJson() throws JSONException {
        JSONStringer stringer = new JSONStringer();
        stringer.object()
                .key("key").value(JsonUtils.genSecretkey(this.getUpdateTime()))
                .key("hostId").value(hostId)
                .key("hostName").value(hostName)
                .key("type").value("SensorStatus")
                .key("time").value("/Date(" + getUpdateTime().getTime() + ")/")
                .key("statuses")
                .array();
        for (SensorStatus status : this.statuses) {
            if (!Float.isNaN(status.getValue())) {
                stringer.object()
                        .key("id").value(status.getId())
                        .key("name").value(status.getName())
                        .key("value").value(status.getFormatedValue())
                        .endObject();
            }
        }
        stringer.endArray().endObject();
        return stringer.toString();
    }

    /**
     * 传感器数量
     *
     * @return
     */
    public int size() {
        return this.statuses.length;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}
