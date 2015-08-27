package com.lingya.farmintell.models;

import com.lingya.farmintell.modbus.Register;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 传感器状态集合 Created by zwq00000 on 2015/6/29.
 */
public class SensorStatusCollection {

    /**
     * 空集合
     */
    public static final SensorStatusCollection Empty = new SensorStatusCollection();

    /**
     * 传感器状态集合
     */
    private List<SensorStatus> statuses;

    /**
     * 更新时间
     */
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
        this.statuses = new ArrayList<SensorStatus>(config.getSensorsCount());
    }

    private SensorStatusCollection() {
        this.updateTime = new Date();
    }

    public List<SensorStatus> getStatuses() {
        return this.statuses;
    }

    public Date getUpdateTime() {
        return updateTime;
    }


    /**
     * 更新传感器状态值
     *
     * @param registers
     */
    public void updateSensorStatus(Register[] registers) {
        if (registers == null) {
            throw new IllegalArgumentException("registers is not been null");
        }
        this.updateTime = new Date();
        int index = 0;
        for (Register register : registers) {
            for (int s = 0; s < register.getSensors().length; s++) {
                Register.Sensor sensor = register.getSensors()[s];
                SensorStatus status;
                if (index < statuses.size()) {
                    status = statuses.get(index);
                } else {
                    status = new SensorStatus(sensor.getId());
                    statuses.add(status);
                }
                status.setName(sensor.getName());
                status.setValue(sensor.getValue());
                index++;
            }
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
                .key("hostId").value(hostId)
                .key("hostName").value(hostName)
                .key("type").value("SensorStatus")
                .key("time").value("/Date(" + updateTime.getTime() + ")/")
                //.key("time").value(updateTime.getTime())
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
        return this.statuses.size();
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
