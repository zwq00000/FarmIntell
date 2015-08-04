package com.lingya.farmintell.modbus;

import com.redriver.modbus.CRC16;
import com.redriver.modbus.FunctionCode;
import com.redriver.modbus.ReadInputRegistersRequest;
import com.redriver.modbus.RegisterHolder;
import com.ychmi.sdk.YcApi;

import junit.framework.TestCase;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zwq00000 on 2015/7/25.
 */
public class ModbusRTUTest extends TestCase {

  private YcApi ycApi;
  private FileDescriptor port;
  private FileOutputStream output;
  private FileInputStream input;

  static synchronized int readStream(InputStream inputStream, byte[] buffer, int off, int length)
      throws
      IOException, InterruptedException {
    int retryCount = 0;
    int pos = off;
    int readCount = 0;
    while (pos < length) {
      int byteCount = inputStream.read(buffer, pos, length - pos);
      System.out.print("\tread:" + byteCount);
      readCount++;
      if (byteCount < 0) {
        ++retryCount;
        if (retryCount > 3) {
          break;
        }
      } else {
        pos += byteCount;
      }
    }
    if (retryCount > 0) {
      System.out.println("\n\tread count " + readCount + "\t retryCount:" + retryCount);
    }
    return pos;
  }

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

  public void testRead04() throws Exception {
    byte slavedId = 1;
    RegisterHolder holder = new RegisterHolder(slavedId, 4);
    ReadInputRegistersRequest request = new ReadInputRegistersRequest(holder);

    byte[] buff = new byte[255];
    for (int i = 0; i < 10000; i++) {
      System.out.println("start request " + i);
      request.writeFrame(output);
      //while (input.available()<=0) {
      //Thread.sleep(50);
      //}
      int id = input.read();
      assertEquals(slavedId, (byte) id);
      byte fun = (byte) input.read();
      assertEquals(fun, FunctionCode.READ_INPUT_REGISTERS);
      int byteCount = input.read();
      assertEquals(byteCount, 8);

      int len = readStream(input, buff, 0, 10);
      assertEquals(len, 10);
      Thread.sleep(1000);
    }
  }

  public void testReadStream() throws Exception {
    byte slavedId = 1;
    RegisterHolder holder = new RegisterHolder(slavedId, 4);
    ReadInputRegistersRequest request = new ReadInputRegistersRequest(holder);

    byte[] buff = new byte[255];
    for (int i = 0; i < 10000; i++) {
      System.out.println("start request " + i);
      request.writeFrame(output);
      //slaveId + funcCode + byteCount + RegValues + CRC16
      // 1 byte    1 byte    1 byte      2*4         2   = 13 byte
      int len = readStream(input, buff, 0, 13);
      assertEquals(len, 13);
      assertEquals(buff[0], 1);
      assertEquals(buff[1], FunctionCode.READ_INPUT_REGISTERS);
      assertEquals(buff[2], 8);
      byte[] crc = CRC16.calculate(buff, 11);
      assertEquals(crc.length, 2);
      assertEquals(crc[0], buff[11]);
      assertEquals(crc[1], buff[12]);
      Thread.sleep(10);
    }
  }
}
