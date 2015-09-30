package com.lingya.farmintell.modbus;

import android.test.AndroidTestCase;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.lingya.farmintell.models.SensorStatus;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.models.SensorsConfig;

import org.json.JSONException;

import java.util.Iterator;

/**
 * Created by zwq00000 on 2015/6/21.
 */
public class RegisterFactoryTest extends AndroidTestCase {

    static final String registerJson = "[\n" +
            "  {\n" +
            "    \"slaveId\": 1,\n" +
            "    \"model\": \"温湿度CO2光照变送器\",\n" +
            "    \"sensors\": [\n" +
            "      {\n" +
            "        \"id\": \"1-0\",\n" +
            "        \"name\": \"temp\",\n" +
            "        \"displayName\": \"温度\",\n" +
            "        \"factor\": 0.1,\n" +
            "        \"unit\": \"℃\",\n" +
            "        \"min\": -20,\n" +
            "        \"max\": 80\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"1-1\",\n" +
            "        \"name\": \"hum\",\n" +
            "        \"displayName\": \"湿度\",\n" +
            "        \"factor\": 0.1,\n" +
            "        \"unit\": \"%rh\",\n" +
            "        \"min\": 0,\n" +
            "        \"max\": 100\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"1-2\",\n" +
            "        \"name\": \"co2\",\n" +
            "        \"displayName\": \"CO2\",\n" +
            "        \"factor\": 1,\n" +
            "        \"unit\": \"ppm\",\n" +
            "        \"min\": 0,\n" +
            "        \"max\": 5000\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"1-3\",\n" +
            "        \"name\": \"light\",\n" +
            "        \"displayName\": \"光照\",\n" +
            "        \"factor\": 10,\n" +
            "        \"unit\": \"%rh\",\n" +
            "        \"min\": -0,\n" +
            "        \"max\": 200000\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"slaveId\": 2,\n" +
            "    \"model\": \"土壤温湿度传感器\",\n" +
            "    \"sensors\": [\n" +
            "      {\n" +
            "        \"id\": \"2-0\",\n" +
            "        \"name\": \"water\",\n" +
            "        \"displayName\": \"土壤含水率\",\n" +
            "        \"factor\": 0.1,\n" +
            "        \"unit\": \"%(m3/m3)\",\n" +
            "        \"min\": 0,\n" +
            "        \"max\": 100\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": \"2-1\",\n" +
            "        \"name\": \"temp\",\n" +
            "        \"displayName\": \"土壤温度\",\n" +
            "        \"factor\": 0.1,\n" +
            "        \"unit\": \"℃\",\n" +
            "        \"min\": -30,\n" +
            "        \"max\": 70\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "]";

    public static Register createInstance() {
        return new Register((byte) 1, "test1", createTestSensors());
    }

    public static Register.Sensor[] createTestSensors() {
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
            registers[i] = new Register((byte) i, "test1", createTestSensors());
        }
        return registers;
    }

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testLoadFromJson() throws Exception {
        Register[] registers = RegisterFactory.loadFromJson(registerJson);
        assertNotNull(registers);
        assertEquals(registers.length, 2);
        Register register = registers[0];
        assertEquals(register.getSlaveId(), 1);
        assertEquals(register.getCount(), 4);
        assertEquals(register.getStartNum(), 0);
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
        assertEquals(instance.length, 4);
        Gson gson = new Gson();
        TypeAdapter<Register> adapter = gson.getAdapter(Register.class);
        System.out.println("registers gson: " + gson.toJson(instance));
        String json = gson.toJson(instance);
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


    public void testToIterator() throws Exception {
        Register[] registers = RegisterFactory.loadFromJson(registerJson);
        Iterator<Float> iterator = RegisterFactory.toValueIterator(registers);
        int count = 0;
        for (Register reg :
                registers) {
            count += reg.getCount();
        }
        int i = 0;
        while (iterator.hasNext()) {
            Float val = iterator.next();
            assertNotNull(val);
            i++;
        }
        assertEquals(count, i);
    }

    public void testUpdateSensorStatus() throws Exception {
        Register[] registers = RegisterFactory.loadFromJson(registerJson);

        SensorsConfig config = SensorsConfig.getDefaultInstance(getContext());
        SensorStatusCollection statusList = new SensorStatusCollection(config);

        statusList.updateSensorStatus(RegisterFactory.toValueIterator(registers));

        assertEquals(statusList.size(), 6);
        for (SensorStatus status : statusList.getStatuses()) {
            assertEquals(status.getValue(), 0.0f);
        }
        registers[0].setValue(0, (short) 1);
        registers[0].setValue(1, (short) 1);
        registers[0].setValue(2, (short) 1);
        registers[0].setValue(3, (short) 1);
        registers[1].setValue(0, (short) 1);
        registers[1].setValue(1, (short) 1);
        statusList.updateSensorStatus(RegisterFactory.toValueIterator(registers));
        assertEquals(statusList.getStatuses().length, 6);
        for (SensorStatus status : statusList.getStatuses()) {
            assertNotSame(status.getValue(), 0.0f);
        }
    }

}