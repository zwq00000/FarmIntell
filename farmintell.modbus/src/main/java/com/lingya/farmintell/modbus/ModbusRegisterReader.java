package com.lingya.farmintell.modbus;

import com.redriver.modbus.Holder;

import java.io.Closeable;
import java.io.IOException;
import java.util.EventListener;

/**
 * modbus 寄存器读取器 Created by zwq00000 on 2015/6/28.
 */
public interface ModbusRegisterReader<H extends Holder> extends Closeable {

  /**
   * 设置 侦听器
   */
  void setOnValueChangedListener(HolderValueChangedListener<H> listener);

  /**
   * 打开连接
   *
   * @return 打开状态
   */
  void open() throws IOException;

  /**
   * 接收器是否已经关闭
   */
  boolean isClosed();

  /**
   * 轮询 modebus 从站 数据接口
   */
  interface HolderValueChangedListener<H extends Holder> extends EventListener {

    /**
     * 接收到 modbus 数据数据 帧类型为 @see
     */
    void onValueChanged(H[] holders);
  }

}
