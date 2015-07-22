package com.lingya.farmintell.modbus;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * 传感器 配置工厂 Created by zwq00000 on 2015/6/12.
 */
public class RegisterFactory {

  private static final String SLAVE_ID = "slaveId";
  private static final String COUNT = "count";
  private static final String SENSOR_NAME = "name";
  private static final String FACTOR = "factor";
  private static final String SENSORS = "sensors";
  private static final String SENSOR_DISPLAY_NAME = "displayName";
  private static final String MODEL = "model";
  private static final String registerJsonFile = "registers.json";
  /**
   * 配置文件 存放目录
   */
  private static final String SETTINGS_FILE_DIR = "settings";
  private static TypeAdapter<Register[]> registersAdapter;

  public static Register[] loadFromJson(String jsonStr) throws IOException {
    if (registersAdapter != null) {
      Gson gson = new Gson();
      registersAdapter = gson.getAdapter(Register[].class);
    }
    Register[] registers = registersAdapter.fromJson(jsonStr);
    for (Register register : registers) {
      register.updateSensorId();
    }
    return registers;
  }

  /**
   * 传感器值 转换为 Json 格式
   */
  public static String toValueJson(Register register) throws JSONException {
    if (register == null) {
      throw new IllegalArgumentException("register is not been null");
    }
    JSONStringer stringer = new JSONStringer();
    stringer.object();
    stringer.key(SLAVE_ID).value(register.getSlaveId())
        .key(MODEL).value(register.getModel());
    for (int i = 0; i < register.getSensors().length; i++) {
      Register.Sensor sensor = register.getSensors()[i];
      stringer.key(sensor.getName()).value(sensor.getValue());
    }
    stringer.endObject();
    return stringer.toString();
  }

  private static JSONStringer stringerRegister(JSONStringer stringer, Register register)
      throws JSONException {
    stringer.object()
        .key(SLAVE_ID).value(register.getSlaveId())
        .key(COUNT).value(register.getCount())
        .key(SENSORS).array();
    for (Register.Sensor sensor : register.getSensors()) {
      stringer.object()
          .key(SENSOR_NAME).value(sensor.getName())
          .key(SENSOR_DISPLAY_NAME).value(sensor.getDisplayName())
          .key(FACTOR).value(sensor.getFactor())
          .key("unit").value(sensor.getUnit());
      stringer.endObject();
    }
    stringer.endArray()
        .endObject();
    return stringer;
  }

  public static JSONStringer stringerRegister(JSONStringer stringer, Register[] registers)
      throws JSONException {
    stringer.array();
    for (int i = 0; i < registers.length; i++) {
      stringerRegister(stringer, registers[i]);
    }
    stringer.endArray();
    return stringer;
  }

  /**
   * 复制 流 从 source 到 target,并关闭两个数据流
   */
  private static void copyStream(InputStream source, OutputStream target) throws IOException {
    byte[] buff = new byte[1024 * 8];
    try {
      int readLen = source.read(buff);
      while (readLen > 0) {
        target.write(buff, 0, readLen);
        readLen = source.read(buff);
      }
    } finally {
      source.close();
      target.close();
    }
  }

  /**
   * 从 Json 文件加载 传感器配置
   */
  public static Register[] loadFromJson(Context context) throws IOException {
    File settingsDir = context.getDir(SETTINGS_FILE_DIR, Context.MODE_WORLD_WRITEABLE);
    if (!settingsDir.exists()) {
      settingsDir.mkdir();
    }
    File jsonFile = new File(settingsDir, registerJsonFile);
    if (!jsonFile.exists()) {
      //复制默认配置到 Settings 目录
      InputStream stream = context.getAssets().open(registerJsonFile);
      FileOutputStream output = new FileOutputStream(jsonFile);
      copyStream(stream, output);
    }
    InputStream stream = new FileInputStream(jsonFile);
    try {
      return loadFromJson(stream);
    } finally {
      stream.close();
    }
  }

  /**
   * 从 Json 格式数据 生成 Register 数组
   */
  private static Register[] loadFromJson(InputStream stream) throws IOException {
    if (registersAdapter == null) {
      Gson gson = new Gson();
      registersAdapter = gson.getAdapter(Register[].class);
    }
    Register[] registers = registersAdapter.fromJson(new InputStreamReader(stream));
    for (Register register : registers) {
      register.updateSensorId();
    }
    return registers;
  }
}
