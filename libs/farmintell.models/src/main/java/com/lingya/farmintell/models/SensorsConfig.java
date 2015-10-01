package com.lingya.farmintell.models;

import android.content.Context;

import com.lingya.farmintell.utils.JsonUtils;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * 传感器配置信息
 * Created by zwq00000 on 2015/7/29.
 */
public class SensorsConfig {


    /**
     * 默认配置文件
     */
    private static final String configJsonFile = "sensorsConfig.json";
    /**
     * 默认实例
     */
    private static SensorsConfig defaultInstance;
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

    /*******************************************************************************************
     * 静态工厂方法
     *
     *******************************************************************************************/
    private String[] sensorIds;
    private SensorConfig[] sensorConfigCache;

    /**
     * 从 Json 文件加载配置
     */
    public static SensorsConfig loadFromJson(Context context) throws IOException {
        defaultInstance = JsonUtils.loadFromJson(context, configJsonFile, SensorsConfig.class);
        return defaultInstance;
    }

    /**
     * 获取默认实例
     */
    public static SensorsConfig getDefaultInstance(Context context) throws IOException {
        if (defaultInstance == null) {
            loadFromJson(context);
        }
        return defaultInstance;
    }

    /**
     * 获取默认 配置实例
     * 返回值可能为空
     *
     * @return @Nullable
     */
    public static SensorsConfig getDefaultInstance() {
        return defaultInstance;
    }


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

    public SensorConfig[] getSensors() {
        if (this.sensorConfigCache == null) {
            sensorConfigCache = new SensorConfig[getSensorsCount()];
            int index = 0;
            for (int i = 0; i < this.stations.length; i++) {
                SensorConfig[] sensors = stations[i].getSensors();
                for (int s = 0; s < sensors.length; s++) {
                    sensorConfigCache[index] = sensors[s];
                    index++;
                }
            }
        }
        return sensorConfigCache;
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
    public SensorConfig findSensorConfig(String sensorId) {
        for (Station station : this.stations) {
            for (SensorConfig sensorConfig : station.getSensors()) {
                if (sensorConfig.getId().equalsIgnoreCase(sensorId)) {
                    return sensorConfig;
                }
            }
        }
        return null;
    }

    /**
     * modbus Slave station
     */
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
        private SensorConfig[] sensors;

        public int size() {
            return sensors.length;
        }

        public byte getSlaveId() {
            return slaveId;
        }

        public String getModel() {
            return model;
        }


        public SensorConfig[] getSensors() {
            return sensors;
        }

    }

    /**
     * 传感器配置
     */
    public class SensorConfig {

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
        /**
         * 格式化数值
         */
        private DecimalFormat valueFormat;

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

        public float getFactor() {
            return factor;
        }

        public String getNumberFormat() {
            if (factor >= 1f) {
                return "#";
            }
            if (factor >= 0.1f) {
                return "#.0";
            }
            if (factor >= 0.01f) {
                return "#.00";
            }
            return "#.#";
        }

        /**
         * 数值范围 Max - Min
         *
         * @return
         */
        public float getRange() {
            return Math.abs(max - min);
        }

        public String formatValue(float value) {
            if (valueFormat == null) {
                valueFormat = new DecimalFormat(getNumberFormat());
            }
            return valueFormat.format(value);
        }
    }
}
