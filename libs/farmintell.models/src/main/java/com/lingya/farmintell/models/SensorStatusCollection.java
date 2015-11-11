package com.lingya.farmintell.models;

import android.support.v4.util.ArrayMap;

import com.lingya.farmintell.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

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

    private Map<String, SensorStatus[]> statusMap;

    public SensorStatusCollection(SensorsConfig config) {
        this();
        if (config == null) {
            throw new IllegalArgumentException("config is not been null");
        }
        this.hostId = config.getHostId();
        this.hostName = config.getHostName();
        this.statusMap = new ArrayMap<String, SensorStatus[]>(6);
        this.statuses = new SensorStatus[config.getSensorsCount()];
        SensorsConfig.SensorConfig[] sensorConfigs = config.getSensors();
        for (int i = 0; i < sensorConfigs.length; i++) {
            SensorsConfig.SensorConfig item = sensorConfigs[i];
            statuses[i] = new SensorStatus(item);
        }
        buildStatusMap();
    }

    private SensorStatusCollection() {
        this.setUpdateTime(new Date());
        statuses = new SensorStatus[0];
        this.statusMap = new ArrayMap<>(0);
    }

    /**
     * 构建 状态Map
     */
    private void buildStatusMap() {
        statusMap.clear();
        for (SensorType type : SensorType.values()) {
            String sensorName = type.name();
            ArrayList<SensorStatus> list = new ArrayList<SensorStatus>(2);
            for (SensorStatus status : this.statuses) {
                if (status.getName().equalsIgnoreCase(sensorName)) {
                    list.add(status);
                }
            }
            statusMap.put(type.name(), list.toArray(new SensorStatus[list.size()]));
        }
    }

    /**
     * 获取 传感器状态集合
     *
     * @return
     */
    public SensorStatus[] getStatuses() {
        return this.statuses;
    }

    /**
     * 根据 传感器名称 返回 传感器集合
     *
     * @param sensorName
     * @return
     */
    public SensorStatus[] findByName(String sensorName) {
        if (statusMap.containsKey(sensorName)) {
            return statusMap.get(sensorName);
        }
        return new SensorStatus[0];
    }

    /**
     * 根据 传感器名称 返回 传感器集合
     *
     * @param sensorType
     * @return
     */
    public SensorStatus[] findByName(SensorType sensorType) {
        return findByName(sensorType.name());
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
        while (values.hasNext() && i < statuses.length) {
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
