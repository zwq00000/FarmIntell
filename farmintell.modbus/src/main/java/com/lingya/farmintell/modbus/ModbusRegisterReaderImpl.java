package com.lingya.farmintell.modbus;

import android.util.Log;

import com.redriver.modbus.Holder;
import com.redriver.modbus.ReadInputRegistersRequest;

import java.io.IOException;

/**
 * Modbus Input 寄存器 读取器 Created by zwq00000 on 2015/6/28.
 */
public class ModbusRegisterReaderImpl extends ModbusRegisterReader<Holder<Short>> {

  private static final String TAG = "ModbusRegisterReaderImpl";

  /**
   * 串口工厂
   */
  private SerialPortFactory portFactory;

  /**
   * modbus 寄存器 读取请求
   */
  private ReadInputRegistersRequest[] requests;

  ModbusRegisterReaderImpl(Holder<Short>[] holders, SerialPortFactory portFactory) {
    super(holders);
    this.portFactory = portFactory;
    this.requests = new ReadInputRegistersRequest[holders.length];
    initRegisterRequest();
  }

  ModbusRegisterReaderImpl(Holder<Short>[] holders) {
    this(holders, SerialPortFactory.getInstance());
  }

  /**
   * 初始化 寄存器读取请求
   */
  private void initRegisterRequest() {
    for (int i = 0; i < holders.length; i++) {
      Holder register = holders[i];
      //noinspection unchecked
      requests[i] = new ReadInputRegistersRequest(register);
    }
  }

  /**
   * Closes the object and release any system resources it holds.
   *
   * <p>Although only the first call has any effect, it is safe to call close multiple times on the
   * same object. This is more lenient than the overridden {@code AutoCloseable.close()}, which may
   * be called at most once.
   */
  @Override
  public void onClose() throws IOException {
    portFactory.stopWatchdog();
  }

  @Override
  void onOpen() throws IOException {
    portFactory.startWatchdog();
  }

  /**
   * 读取传感器集合
   */
  synchronized void readRegisters() {
    int requestsLen = this.requests.length;
    for (int i = 0; i < requestsLen; i++) {
      ReadInputRegistersRequest request = this.requests[i];
      try {
        readRegister(request);
        if (i < requestsLen - 1) {
          Thread.sleep(50);
        }
      } catch (IOException ex) {
        Log.e(TAG, ex.getMessage());
      } catch (InterruptedException e) {
        Log.e(TAG, e.toString());
      }
    }
  }

  private boolean readRegister(ReadInputRegistersRequest request) throws IOException {
    int retryCount = 0;
    while (retryCount < 3) {
      request.writeFrame(portFactory.getOutputStream());
      if (request.readResponse(portFactory.getInutStream())) {
        return true;
      } else {
        retryCount++;
        Log.d(TAG, "read slaveId " + request.getSlaveId() + " not responsed retry:" + retryCount);
      }
    }
    return false;
  }

  /**
   * Starts executing the active part of the class' code. This method is called when a thread is
   * started that has been created with a class which implements {@code Runnable}.
   */
  @Override
  public void run() {
    portFactory.feedWatchdog();
    super.run();
  }
}
