package com.lingya.farmintell.modbus;

import android.util.Log;

import com.redriver.modbus.Holder;
import com.redriver.modbus.ReadInputRegistersRequest;

import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Modbus Input 寄存器 读取器 Created by zwq00000 on 2015/6/28.
 */
public class ModbusRegisterReaderImpl<H extends Holder> implements ModbusRegisterReader<H> {

  private static final String TAG = "ModbusRegisterReader";
  /**
   * 最大读取周期
   */
  private static final long MAX_PERIOD = 1000 * 60;
  /**
   * 最小读取周期
   */
  private static final int MIN_PERIOD = 1000;
  private final Holder[] holders;
  /**
   * 任务调度
   */
  private final ScheduledExecutorService
      scheduleService =
      Executors.newScheduledThreadPool(1);
  /**
   * 串口工厂
   */
  private SerialPortFactory portFactory;
  /**
   * 数据侦听器
   */
  private HolderValueChangedListener listener;
  /**
   * modbus 寄存器 读取请求
   */
  private ReadInputRegistersRequest[] requests;
  /**
   * 工作线程
   */
  private ScheduledFuture<?> readerTask;
  /**
   * 是否已经关闭
   */
  private boolean isClosed = true;
  /**
   * 读取周期 默认 3秒钟
   */
  private long period = 3000;

  public ModbusRegisterReaderImpl(Holder<Short>[] holders, SerialPortFactory portFactory) {
    this.holders = holders;
    this.portFactory = portFactory;
    this.requests = new ReadInputRegistersRequest[holders.length];
    initRegisterRequest();
  }

  public ModbusRegisterReaderImpl(Holder holder, SerialPortFactory port) {
    this.holders = new Holder[]{holder};
    this.portFactory = port;
    this.requests = new ReadInputRegistersRequest[holders.length];
    initRegisterRequest();
  }

  /**
   * 初始化 寄存器读取请求
   */
  private void initRegisterRequest() {
    for (int i = 0; i < holders.length; i++) {
      Holder register = holders[i];
      requests[i] = new ReadInputRegistersRequest(register);
    }
  }

  /**
   * 设置 侦听器
   */
  @Override
  public void setOnValueChangedListener(HolderValueChangedListener<H> listener) {
    this.listener = listener;
  }

  /**
   * 打开连接
   */
  @Override
  public void open() throws IOException {
    openInternal();
  }

  /**
   * 接收器是否已经关闭
   */
  @Override
  public boolean isClosed() {
    return isClosed;
  }

  /**
   * Closes the object and release any system resources it holds.
   *
   * <p>Although only the first call has any effect, it is safe to call close multiple times on the
   * same object. This is more lenient than the overridden {@code AutoCloseable.close()}, which may
   * be called at most once.
   */
  @Override
  public void close() throws IOException {
    if (!isClosed) {
      if (!this.readerTask.isCancelled()) {
        readerTask.cancel(false);
      }
      this.isClosed = true;
    }
  }

  private void openInternal() throws IOException {
    if (isClosed) {
      readerTask =
          scheduleService
              .scheduleWithFixedDelay(new ReaderTask(), 0, period, TimeUnit.MILLISECONDS);
    }
    this.isClosed = false;
  }

  /**
   * 设置 循环周期
   */
  public void setPeriod(long periodMilliSeconds) throws IOException {
    if (periodMilliSeconds < MIN_PERIOD) {
      periodMilliSeconds = MIN_PERIOD;
    }
    if (periodMilliSeconds > MAX_PERIOD) {
      periodMilliSeconds = MAX_PERIOD;
    }
    if (periodMilliSeconds != period) {
      this.period = periodMilliSeconds;
      if (!this.isClosed()) {
        this.restart();
      }
    }
  }

  /**
   * 重新启动服务
   */
  private void restart() throws IOException {
    if (!this.isClosed) {
      this.close();
    }
    if (this.isClosed) {
      this.open();
    }
  }


  /**
   * 读取传感器集合
   */
  private boolean readRegisters() {
    boolean hasResponsed = true;
    int requestsLen = this.requests.length;
    for (int i = 0; i < requestsLen; i++) {
      ReadInputRegistersRequest request = this.requests[i];
      try {
        request.writeFrame(portFactory.getOutputStream());
        if (!request.readResponse(portFactory.getInutStream())) {
          Log.d(TAG, "read slaveId " + request.getSlaveId() + " not responsed");
        }
        if (i < requestsLen - 1) {
          Thread.sleep(50);
        }
      } catch (IOException ex) {
        Log.e(TAG, ex.getMessage());
        hasResponsed = false;
      } catch (InterruptedException e) {
        Log.e(TAG, e.toString());
        hasResponsed = false;
      }
    }
    return hasResponsed;
  }

  /**
   * 响应 数据读取完成事件
   */
  private void onReceivedData() {
    if (listener != null) {
      listener.onValueChanged(this.holders);
    }
  }

  private final class ReaderTask extends TimerTask {

    @Override
    public void run() {
      if (!isClosed()) {
        if (portFactory != null) {
          ModbusRegisterReaderImpl.this.readRegisters();
          onReceivedData();
        }
      }
    }
  }
}
