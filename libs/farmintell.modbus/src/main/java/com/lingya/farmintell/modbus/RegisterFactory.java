package com.lingya.farmintell.modbus;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.lingya.farmintell.models.SensorsConfig;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.Iterator;

/**
 * 传感器 配置工厂 Created by zwq00000 on 2015/6/12.
 */
public class RegisterFactory {

    private static final String SLAVE_ID = "slaveId";
    private static final String MODEL = "model";
    private static TypeAdapter<Register[]> registersAdapter;

    public static Register[] loadFromJson(String jsonStr) throws IOException {
        if (registersAdapter == null) {
            Gson gson = new Gson();
            registersAdapter = gson.getAdapter(Register[].class);
        }
        Register[] registers = registersAdapter.fromJson(jsonStr);
        for (Register register : registers) {
            register.updateSensorId();
        }
        return registers;
    }

    /**
     * 传感器值 转换为 Json 格式
     */
    public static String toValueJson(Register register) throws JSONException {
        if (register == null) {
            throw new IllegalArgumentException("register is not been null");
        }
        JSONStringer stringer = new JSONStringer();
        stringer.object();
        stringer.key(SLAVE_ID).value(register.getSlaveId())
                .key(MODEL).value(register.getModel());
        for (int i = 0; i < register.getSensors().length; i++) {
            Register.Sensor sensor = register.getSensors()[i];
            stringer.key(sensor.getName()).value(sensor.getValue());
        }
        stringer.endObject();
        return stringer.toString();
    }


    /**
     * 转换为 寄存器配置
     */
    static Register toRegister(SensorsConfig.Station station) {
        if (station == null) {
            throw new IllegalArgumentException("station is not been Null");
        }
        SensorsConfig.SensorConfig[] configSensors = station.getSensors();
        Register.Sensor mSensors[] = new Register.Sensor[configSensors.length];

        for (int i = 0; i < mSensors.length; i++) {
            SensorsConfig.SensorConfig s = configSensors[i];
            mSensors[i] = new Register.Sensor(s.getId(), s.getName(), s.getFactor());
        }
        return new Register(station.getSlaveId(), station.getModel(), mSensors);
    }

    /**
     * 根据 传感器配置文件 创建 modbus 寄存器配置
     */
    public static Register[] createRegisters(SensorsConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config is not been null");
        }

        SensorsConfig.Station[] stations = config.getStations();
        Register[] registers = new Register[stations.length];
        for (int i = 0; i < stations.length; i++) {
            registers[i] = toRegister(stations[i]);
        }
        return registers;
    }

    /**
     * 获取 传感器值 的枚举器
     *
     * @param registers
     * @return
     */
    public static Iterator<Float> toValueIterator(final Register[] registers) {
        if (registers == null) {
            throw new IllegalArgumentException("register is not been null");
        }

        return new Iterator<Float>() {
            //寄存器索引
            int registerIndex = 0;
            //传感器索引
            int sensorIndex = -1;

            @Override
            public boolean hasNext() {
                if (registerIndex < registers.length - 1) {
                    return true;
                }
                return sensorIndex < registers[registerIndex].getCount() - 1;
            }

            @Override
            public Float next() {
                sensorIndex++;
                if (registerIndex < registers.length) {
                    Register register = registers[registerIndex];
                    if (sensorIndex < register.getCount() && register.hasChanged()) {
                        return (Float) registers[registerIndex].getSensorValue(sensorIndex);
                    } else {
                        registerIndex++;
                        sensorIndex = -1;
                        return next();
                    }
                } else {
                    throw new IndexOutOfBoundsException("registerIndex=" + registerIndex + " sensorIndex=" + sensorIndex);
                }
            }

            @Override
            public void remove() {

            }
        };
    }
}
