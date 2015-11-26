package com.lingya.farmintell.modbus;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.lingya.farmintell.models.SensorsConfig;
import com.redriver.modbus.Holder;

import java.io.Closeable;
import java.io.IOException;
import java.util.EventListener;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * modbus 寄存器读取器 Created by zwq00000 on 2015/6/28.
 */
public abstract class ModbusRegisterReader<H extends Holder> implements Closeable, Runnable {

    /**
     * 最大读取周期
     */
    static final long MAX_PERIOD = 1000 * 60;
    /**
     * 最小读取周期
     */
    static final int MIN_PERIOD = 1000;
    private static final String TAG = ModbusRegisterReader.class.getSimpleName();

    /**
     * 寄存器
     */
    final H[] holders;
    /**
     * 任务调度
     */
    private final ScheduledExecutorService
            scheduleService =
            Executors.newScheduledThreadPool(1);
    /**
     * 数据改变侦听器
     */
    HolderValueChangedListener<H> listener;
    /**
     * 读取周期 默认 3秒钟
     */
    long period = 3000;
    /**
     * 工作线程
     */
    private ScheduledFuture<?> readFuture;
    /**
     * 是否已经关闭
     */
    private boolean isClosed = true;

    protected ModbusRegisterReader(H[] holders) {
        if (holders == null) {
            throw new IllegalArgumentException("holders is not been null.");
        }
        this.holders = holders;
    }

    /**
     * 创建 默认的 MODBUS 寄存器读取器 实例
     *
     * @param holders
     * @return
     */
    public static ModbusRegisterReader<Holder<Short>> createInstance(Context context, Holder<Short>[] holders) {
        Log.d(TAG, "Debug Mode " + BuildConfig.DEBUG);
        if ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            //Debug 模式下使用 模拟输入
            return new ModbusRegisterReaderMock(holders);
        } else {
            return new ModbusRegisterReaderImpl(holders, SerialPortFactory.getInstance());
        }
    }

    /**
     * 设置 侦听器
     */
    public void setOnValueChangedListener(HolderValueChangedListener<H> listener) {
        this.listener = listener;
    }

    /**
     * 响应 数据读取完成事件
     */
    void onReceivedData() {
        if (listener != null) {
            //noinspection unchecked
            listener.onValueChanged(this.holders);
        }
    }

    /**
     * 打开连接
     *
     * @return 打开状态
     */
    public void open() throws IOException {
        if (!isClosed) {
            Log.d(TAG, "reader is opened.");
            return;
        }
        if (readFuture == null || readFuture.isDone()) {
            readFuture =
                    scheduleService
                            .scheduleWithFixedDelay(this, 0, period, TimeUnit.MILLISECONDS);
        }
        this.isClosed = false;
        onOpen();
    }

    void onOpen() throws IOException {

    }

    @Override
    public void close() throws IOException {
        if (isClosed) {
            Log.d(TAG, "reader is closed.");
            return;
        }
        if (this.readFuture != null && !this.readFuture.isDone()) {
            this.isClosed = readFuture.cancel(true);
        } else {
            isClosed = true;
        }
        onClose();
    }

    /**
     * 当关闭读取器时触发该事件
     */
    void onClose() throws IOException {
    }

    /**
     * 接收器是否已经关闭
     */
    public boolean isClosed() {
        return this.isClosed;
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
                this.close();
                this.open();
            }
        }
    }

    /**
     * 读取寄存器数据
     */
    abstract void readRegisters();

    @Override
    public void run() {
        if (!isClosed()) {
            try {
                readRegisters();
                onReceivedData();
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }
    }

    /**
     * 轮询 modebus 从站 数据接口
     */
    public interface HolderValueChangedListener<H extends Holder> extends EventListener {

        /**
         * 接收到 modbus 数据数据 帧类型为 @see
         */
        @SuppressWarnings("unchecked")
        void onValueChanged(H... holders);
    }

    /**
     * Modbus 寄存器读取 模拟器
     * 用于 调试程序,根据 @see SensorConfig 生成模拟输入
     */
    static class ModbusRegisterReaderMock extends ModbusRegisterReader<Holder<Short>> {

        private final SensorsConfig config;
        private final Random random = new Random();

        public ModbusRegisterReaderMock(Holder<Short>[] holders) {
            super(holders);
            this.config = SensorsConfig.getDefaultInstance();
            this.period = 1000;
        }

        /**
         * 读取寄存器数据
         */
        @Override
        synchronized void readRegisters() {

            SensorsConfig.Station[] stations = config.getStations();

            for (Holder<Short> holder : holders
                    ) {
                byte slaverId = holder.getSlaveId();
                SensorsConfig.Station station = null;
                for (SensorsConfig.Station station1 : stations) {
                    if (station1.getSlaveId() == slaverId) {
                        station = station1;
                        break;
                    }
                }
                if (station != null) {
                    SensorsConfig.SensorConfig[] sensorConfigs = station.getSensors();
                    for (int i = 0; i < holder.getCount(); i++) {
                        SensorsConfig.SensorConfig sensorConfig = sensorConfigs[i];
                        int range = Math.abs(sensorConfig.getMax() - sensorConfig.getMin());
                        short
                                value =
                                (short) (((random.nextFloat() * range) + sensorConfig.getMin()) / sensorConfig.getFactor());
                        holder.setValue(i, value);
                    }
                }
            }
        }
    }
}

