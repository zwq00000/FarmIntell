package com.lingya.farmintell.modbus;

import android.util.Log;

import com.ychmi.sdk.YcApi;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zwq00000 on 2015/6/22.
 */
public class SerialPortFactory implements java.io.Closeable {

    private static final String TAG = "SerialPortFactory";
    private static SerialPortFactory instance;
    private final YcApi ycApi;
    private final String portName;
    private FileOutputStream output;
    private FileInputStream input;

    private SerialPortFactory(String portName) {
        ycApi = new YcApi();
        this.portName = portName;
        openStream();
    }

    public static SerialPortFactory getInstance() {
        if (instance == null) {
            instance = new SerialPortFactory(YcApi.ttySAC3);
        }
        return instance;
    }

    /**
     * 喂狗
     */
    public void feedWatchdog() {
        ycApi.FeedWDog();
    }

    /**
     * 启动看门狗
     */
    public void startWatchdog() {
        ycApi.SetWDog((byte) 30);
        ycApi.StartWDog();
    }

    /**
     * 停止看门狗
     */
    public void stopWatchdog() {
        ycApi.StopWDog();
    }

    /**
     * 初始化 数据流对象
     */
    private void openStream() {
        if (input != null) {
            try {
                this.closeStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileDescriptor port = ycApi.openCom(portName, 9600, 8, 0, 1);
        if (!port.valid()) {
            Log.e(TAG, "invalided port file " + portName);
        }
        output = new FileOutputStream(port);
        input = new FileInputStream(port);
    }

    /**
     * 关闭 IO流 和 com 端口
     */
    private void closeStream() throws IOException {
        try {
            this.input.close();
            this.output.close();
            this.output = null;
            this.input = null;
        } finally {
            ycApi.closeCom();
        }
    }

    /**
     * 获取输入流，不要在调用方关闭输出流，使用  {@see close()} 方法
     */
    public InputStream getInutStream() {
        if (input == null) {
            openStream();
        }
        return input;
    }

    /**
     * 获取输出流，不要在调用方关闭输出流，使用  {@see close()} 方法
     */
    public OutputStream getOutputStream() {
        if (output == null) {
            openStream();
        }
        return output;
    }

    /**
     * 获取 端口名称
     */
    public String getPortName() {
        return portName;
    }

    /**
     * Closes the object and release any system resources it holds.
     * <p/>
     * <p>Although only the first call has any effect, it is safe to call close multiple times on the
     * same object. This is more lenient than the overridden {@code AutoCloseable.close()}, which may
     * be called at most once.
     */
    @Override
    public void close() throws IOException {
        if (this == instance) {
            instance = null;
        }
        closeStream();
    }
}
