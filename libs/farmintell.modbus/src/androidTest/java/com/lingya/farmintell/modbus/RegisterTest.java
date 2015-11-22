package com.lingya.farmintell.modbus;

import android.test.AndroidTestCase;
import android.util.Log;

import com.redriver.modbus.ReadInputRegistersRequest;
import com.ychmi.sdk.YcApi;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zwq00000 on 2015/6/21.
 */
public class RegisterTest extends AndroidTestCase {

    private static final String TAG = "RegistersTest";
    private Register register;
    private YcApi ycApi;
    private FileDescriptor port;
    private FileOutputStream output;
    private FileInputStream input;

    public void setUp() throws Exception {
        super.setUp();
        ycApi = new YcApi();
        //ycApi.SetBeep(true);
        port = ycApi.openCom(YcApi.ttySAC3, 9600, 8, 0, 1);
        //sport= new SerialPort(new File("/dev/ttySAC3"),9600,0);
        output = new FileOutputStream(port);
        input = new FileInputStream(port);
    }

    public void tearDown() throws Exception {
        if (input != null) {
            input.close();
        }
        if (output != null) {
            output.close();
        }
        if (port != null) {
            ycApi.closeCom();
        }
        port = null;
    }

    public void testParseRegisters() throws Exception {
        String jsonStr = "{\"slaveId\":1,\"count\":4,"
                + "\"coils\":[{\"Factor\":1,\"coilName\":\"TEMP\",\"index\":0},"
                + "{\"Factor\":1,\"coilName\":\"HUM\",\"index\":1},"
                + "{\"Factor\":1,\"coilName\":\"CO2\",\"index\":2},"
                + "{\"Factor\":1,\"coilName\":\"LUX\",\"index\":3}],"
                + "\"slaveId\":1}";
        JSONObject jsonObj = new JSONObject(jsonStr);
        int slaveId = jsonObj.getInt("slaveId");
        Assert.assertEquals(slaveId, 1);
        assertEquals(jsonObj.getInt("count"), 4);
        JSONArray status = jsonObj.getJSONArray("coils");
        assertNotNull(status);
        assertEquals(status.length(), 4);
        for (int i = 0; i < status.length(); i++) {
            JSONObject item = (JSONObject) status.get(i);
            assertEquals(i, item.getInt("index"));
            System.out.println(i + "\t" + item.getString("coilName"));
        }
    }

    public void testReadStatus() throws Exception {
        Register holder = this.register;
        ReadInputRegistersRequest request = new ReadInputRegistersRequest(holder.getSlaveId(), holder);
        for (int i = 0; i < 1000; i++) {
            request.writeFrame(output);
            request.readResponse(input);
            if (holder.hasChanged()) {
                Log.d(TAG, i + "\t" + RegisterFactory.toValueJson(holder));
            } else {
                Log.d(TAG, i + "\tread error");
            }
            //Thread.sleep(50);
        }
    }

    public void testRegisters() throws Exception {
        String json = "[{\"count\":4,"
                + "\"sensors\":[{\"factor\":0.1,\"index\":0,\"name\":\"temp\"},"
                + "{\"factor\":0.1,\"index\":1,\"name\":\"hum\"},"
                + "{\"factor\":1,\"index\":2,\"name\":\"co2\"},"
                + "{\"factor\":10,\"index\":3,\"name\":\"lux\"}],"
                + "\"slaveId\":1}]";
        Register[] registers = RegisterFactory.loadFromJson(json);
        assertNotNull(registers);
        assertTrue(registers.length > 0);
        assertTrue(registers[0].getCount() > 1);
        ReadInputRegistersRequest[] requests = new ReadInputRegistersRequest[registers.length];
        for (int i = 0; i < registers.length; i++) {
            Register register = registers[i];
            requests[i] = new ReadInputRegistersRequest(register.getSlaveId(), register);
        }

        for (int i = 0; i < 100; i++) {
            long before = System.currentTimeMillis();
            for (int r = 0; r < registers.length; r++) {
                ReadInputRegistersRequest request = requests[r];
                request.writeFrame(output);
                request.readResponse(input);

                System.out.println(registers[r].toJson());
                Thread.sleep(50);
            }
            long after = System.currentTimeMillis();
            System.out.println("Take " + (after - before) + " milliseconds");
            Thread.sleep(1000 - (after - before));
        }

    }

    public void testConstruct() throws Exception {
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

        Register register = new Register((byte) 1, "model", sensors);
        assertNotNull(register);
        assertNotNull(register.getSensors());
        assertEquals(register.getCount(), 4);
        for (int i = 0; i < register.getCount(); i++) {
            sensor = register.getSensors()[i];
            assertNotNull(sensor.getId());
        }
    }

    public void testSetSlaveId() throws Exception {

    }

    public void testGetCoilsCount() throws Exception {

    }

    public void testSetCoilsCount() throws Exception {

    }
}