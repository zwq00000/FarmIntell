package com.lingya.farmintell.modbus;

import android.content.Context;
import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.lingya.farmintell.models.RealmFactory;
import com.lingya.farmintell.models.SensorStatus;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.models.SensorsConfig;
import com.lingya.farmintell.models.SensorsConfigFactory;

import org.json.JSONException;

import java.io.File;

/**
 * Created by zwq00000 on 2015/6/21.
 */
public class RegisterFactoryTest extends AndroidTestCase {

    public static Register createInstance() {
        return new Register((byte) 1, "test1", createSensors());
    }

    public static Register.Sensor[] createSensors() {
        Register.Sensor[] sensors = new Register.Sensor[4];
        Register.Sensor sensor = new Register.Sensor();
        sensors[0] = sensor;
        sensor.setName("temp");
        sensor.setFactor((float) 0.1);
        sensor = new Register.Sensor();
        sensors[1] = sensor;
        sensor.setName("hum");
        sensor.setFactor((float) 0.1);
        sensor = new Register.Sensor();
        sensors[2] = sensor;
        sensor.setName("co2");
        sensor = new Register.Sensor();
        sensors[3] = sensor;
        sensor.setName("light");
        sensor.setFactor(10);
        return sensors;
    }

    private static Register[] createRegisters(int count) {
        Register[] registers = new Register[count];
        for (int i = 0; i < count; i++) {
            registers[i] = new Register((byte) i, "test1", createSensors());
        }
        return registers;
    }

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testLoadFromJson() throws Exception {
        Register[] registers = RegisterFactory.loadFromJson(getContext());
        assertNotNull(registers);
        assertEquals(registers.length, 2);
        Register register = registers[0];
        assertEquals(register.getSlaveId(), 1);
        assertEquals(register.getCount(), 4);
        assertEquals(register.getStartNum(), 0);
        File settingsDir = getContext().getDir("settings", Context.MODE_WORLD_READABLE);
        File jsonFile = new File(settingsDir, "registers.json");
        assertTrue(jsonFile.exists());
    }

    public void testParseJson() throws Exception {

    }

    private String getJson(Register register) throws JSONException {
        Gson gson = new Gson();
        RegisterFactory.toValueJson(register);
        return gson.toJson(register);
    }

    public void testGson() throws Exception {
        Register instance = createInstance();
        String json = getJson(instance);
        System.out.println("register: " + json);
        Gson gson = new Gson();
        TypeAdapter<Register> adapter = gson.getAdapter(Register.class);
        System.out.println("gson: " + adapter.toJson(instance));
        Register value = adapter.fromJson(json);
        assertNotNull(value);
        assertEquals(value.getSlaveId(), 1);
        for (Register.Sensor sensor : value.getSensors()) {
            assertNotNull(sensor.getId());
            System.out.println("sensor id:" + sensor.getId());
        }
    }

    /**
     * 测试 Registers 集合 的 toValueJson
     */
    public void testRegistersGson() throws Exception {
        Register[] instance = createRegisters(4);
        Gson gson = new Gson();
        TypeAdapter<Register> adapter = gson.getAdapter(Register.class);
        System.out.println("registers gson: " + gson.toJson(instance));

        String json = "[\n"
                + "  {\n"
                + "    \"sensors\": [\n"
                + "      {\n"
                + "        \"name\": \"temp\",\n"
                + "        \"displayName\": \"温度\",\n"
                + "        \"factor\": 0.1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"hum\",\n"
                + "        \"displayName\": \"湿度\",\n"
                + "        \"factor\": 0.1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"co2\",\n"
                + "        \"displayName\": \"CO2\",\n"
                + "        \"factor\": 1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"light\",\n"
                + "        \"displayName\": \"光照\",\n"
                + "        \"factor\": 10\n"
                + "      }\n"
                + "    ],\n"
                + "    \"slaveId\": 0\n"
                + "  },\n"
                + "  {\n"
                + "    \"sensors\": [\n"
                + "      {\n"
                + "        \"name\": \"temp\",\n"
                + "        \"displayName\": \"温度\",\n"
                + "        \"factor\": 0.1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"hum\",\n"
                + "        \"displayName\": \"湿度\",\n"
                + "        \"factor\": 0.1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"co2\",\n"
                + "        \"displayName\": \"CO2\",\n"
                + "        \"factor\": 1,\n"
                + "        \"coilValue\": 0\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"light\",\n"
                + "        \"displayName\": \"光照\",\n"
                + "        \"factor\": 10\n"
                + "      }\n"
                + "    ],\n"
                + "    \"slaveId\": 1\n"
                + "  },\n"
                + "  {\n"
                + "    \"sensors\": [\n"
                + "      {\n"
                + "        \"name\": \"temp\",\n"
                + "        \"displayName\": \"温度\",\n"
                + "        \"factor\": 0.1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"hum\",\n"
                + "        \"displayName\": \"湿度\",\n"
                + "        \"factor\": 0.10000000149011612,\n"
                + "        \"coilValue\": 0\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"co2\",\n"
                + "        \"displayName\": \"CO2\",\n"
                + "        \"factor\": 1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"light\",\n"
                + "        \"displayName\": \"光照\",\n"
                + "        \"factor\": 10\n"
                + "      }\n"
                + "    ],\n"
                + "    \"slaveId\": 2\n"
                + "  },\n"
                + "  {\n"
                + "    \"sensors\": [\n"
                + "      {\n"
                + "        \"name\": \"temp\",\n"
                + "        \"displayName\": \"温度\",\n"
                + "        \"factor\": 0.1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"hum\",\n"
                + "        \"displayName\": \"湿度\",\n"
                + "        \"factor\": 0\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"co2\",\n"
                + "        \"displayName\": \"CO2\",\n"
                + "        \"factor\": 1\n"
                + "      },\n"
                + "      {\n"
                + "        \"name\": \"light\",\n"
                + "        \"displayName\": \"光照\",\n"
                + "        \"factor\": 10\n"
                + "      }\n"
                + "    ],\n"
                + "    \"slaveId\": 3\n"
                + "  }\n"
                + "]";
        Register[] regs = gson.fromJson(json, Register[].class);
        assertNotNull(regs);
        assertEquals(regs.length, 4);
        assertEquals(regs[0].getSlaveId(), 0);
        assertEquals(regs[0].getSensors().length, 4);
        assertEquals(regs[0].getSensors()[0].getName(), "temp");
        for (Register.Sensor sensor : regs[0].getSensors()) {
            assertNotNull(sensor.getId());
            System.out.println("senser id:" + sensor.getId());
        }
    }

    public void testToJson() throws Exception {
        String
                json =
                "{\"sensors\":[{\"name\":\"temp\",\"displayName\":\"温度\",\"factor\":0.1,\"coilValue\":0},{\"name\":\"hum\",\"displayName\":\"湿度\",\"factor\":0.1,\"coilValue\":0},{\"name\":\"co2\",\"displayName\":\"CO2\",\"factor\":1.0,\"coilValue\":0},{\"name\":\"light\",\"displayName\":\"光照\",\"factor\":10,\"coilValue\":0}],\"slaveId\":1}";
        Gson gson = new Gson();
        TypeAdapter<Register> adapter = gson.getAdapter(Register.class);
        Register register = adapter.fromJson(json);
        assertNotNull(register);
        assertEquals(register.getCount(), 4);
        assertEquals(register.getSensors()[0].getName(), "temp");
        assertEquals(register.getSensors()[0].getFactor(), 0.1f);
    }

    public void testUpdateSensorStatus() throws Exception {
        Register[] registers = new Register[]{createInstance()};
        SensorsConfig config = SensorsConfigFactory.getDefaultInstance(getContext());
        SensorStatusCollection statusList = new SensorStatusCollection(config);
        RealmFactory.updateSensorStatus(registers, statusList);
        assertEquals(statusList.size(), 4);
        for (SensorStatus status : statusList.getStatuses()) {
            assertEquals(status.getValue(), 0.0f);
        }
        registers[0].setValue(0, (short) 1);
        registers[0].setValue(1, (short) 1);
        registers[0].setValue(2, (short) 1);
        registers[0].setValue(3, (short) 1);
        RealmFactory.updateSensorStatus(registers, statusList);
        assertEquals(statusList.getStatuses().length, 4);
        for (SensorStatus status : statusList.getStatuses()) {
            assertNotSame(status.getValue(), 0.0f);
        }
    }

}