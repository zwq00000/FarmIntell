package com.lingya.farmintell.modbus;

import android.test.AndroidTestCase;

import com.redriver.modbus.Holder;
import com.redriver.modbus.ReadHoldingRegistersRequest;
import com.redriver.modbus.ReadInputRegistersRequest;
import com.redriver.modbus.RegisterHolder;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zwq00000 on 2015/6/28.
 */
public class ModbusRegisterReaderImplTest extends AndroidTestCase {

    public void testSetListener() throws Exception {
        Register[] registers = new Register[]{RegisterFactoryTest.createInstance()};
        assertNotNull(registers);
        SerialPortFactory portFactory = SerialPortFactory.getInstance();
        assertNotNull(portFactory);
        ModbusRegisterReaderImpl
                reader =
                new ModbusRegisterReaderImpl(registers, portFactory);
        assertNotNull(reader);

        reader.setOnValueChangedListener(
                new ModbusRegisterReader.HolderValueChangedListener<Holder<Short>>() {
                    /**
                     * 接收到 modbus 数据数据 帧类型为 @see
                     *
                     * @param holders
                     */
                    @Override
                    public void onValueChanged(Holder... holders) {
                        for (Holder<Short> holder : holders) {
                            Register register = (Register) holder;
                            try {
                                System.out.println(register.toJson());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        reader.open();

        Thread.sleep(1000 * 10);
        reader.close();
        reader.open();
        Thread.sleep(1000 * 10);
    }

    public void testOpen() throws Exception {
        Register[] registers = RegisterFactory.loadFromJson(getContext());
        assertNotNull(registers);
        assertEquals(registers.length, 2);
        SerialPortFactory portFactory = SerialPortFactory.getInstance();
        assertNotNull(portFactory);
        ModbusRegisterReaderImpl
                reader =
                new ModbusRegisterReaderImpl(registers, portFactory);
        assertNotNull(reader);

        //reader.setPeriod(1000);

        reader.setOnValueChangedListener(
                new ModbusRegisterReader.HolderValueChangedListener<Holder<Short>>() {
                    /**
                     * 接收到 modbus 数据数据 帧类型为 @see
                     *
                     * @param holders
                     */
                    @Override
                    public void onValueChanged(Holder... holders) {
                        assertEquals(holders.length, 2);
                        for (Holder holder : holders) {
                            Register register = (Register) holder;
                            try {
                                System.out.println(register.toJson());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        reader.open();
        ArrayList l = new ArrayList();
        for (int i = 0; i < 1024 * 100; i++) {
            //System.gc();
            Thread.sleep(100);
            l.add(new byte[1024]);
        }
        reader.close();
        reader.open();
        Thread.sleep(1000 * 1);
    }

    public void testReadSalved2() throws Exception {
        SerialPortFactory portFactory = SerialPortFactory.getInstance();
        assertNotNull(portFactory);
        for (byte i = 1; i < 10; i++) {
            ReadHoldingRegisters(portFactory, i);
            Thread.sleep(500);
            readInputRegisters(portFactory, i);
            Thread.sleep(500);
        }
    }

    private void ReadHoldingRegisters(SerialPortFactory portFactory, byte slavedId)
            throws IOException, InterruptedException {
        RegisterHolder holder = new RegisterHolder(slavedId, 2);
        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(holder);
        request.writeFrame(portFactory.getOutputStream());
        //Thread.sleep(10);
        boolean hasResponsed = request.readResponse(portFactory.getInutStream());
        if (!hasResponsed) {
            System.out.println("slaveId:" + slavedId + "\t not response");
            return;
        }
        StringBuilder builder = new StringBuilder("slavedId:");
        builder.append(slavedId).append("\t");
        for (int i = 0; i < holder.getCount(); i++) {
            builder.append(i).append(":").append(holder.getValue(i)).append("\t");
        }
        System.out.println(builder.toString());
    }

    private void readInputRegisters(SerialPortFactory portFactory, byte slavedId)
            throws IOException, InterruptedException {
        RegisterHolder holder = new RegisterHolder(slavedId, 2);
        ReadInputRegistersRequest request = new ReadInputRegistersRequest(holder);
        request.writeFrame(portFactory.getOutputStream());
        Thread.sleep(10);
        boolean hasResponsed = request.readResponse(portFactory.getInutStream());
        if (!hasResponsed) {
            System.out.println("slaveId:" + slavedId + "\t not response");
            return;
        }
        StringBuilder builder = new StringBuilder("slavedId:");
        builder.append(slavedId).append("\t");
        for (int i = 0; i < holder.getCount(); i++) {
            builder.append(i).append(":").append(holder.getValue(i)).append("\t");
        }
        System.out.println(builder.toString());
    }

    public void testIsClosed() throws Exception {

    }

    public void testClose() throws Exception {

    }

    public void testReadRegisters() throws Exception {
        SerialPortFactory port = SerialPortFactory.getInstance();
        RegisterHolder holder = new RegisterHolder((byte) 1, (short) 0, 4);
        ReadInputRegistersRequest request = new ReadInputRegistersRequest(holder.getSlaveId(), holder);
        request.writeFrame(port.getOutputStream());
        request.readResponse(port.getInutStream());
        for (int i = 0; i < holder.getCount(); i++) {
            System.out.println(i + " " + holder.getValue(i));
        }
    }
}