package com.lingya.farmintell.modbus;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import android.content.Context;

import com.lingya.farmintell.utils.JsonUtils;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;

/**
 * 传感器 配置工厂 Created by zwq00000 on 2015/6/12.
 */
public class RegisterFactory {

  private static final String SLAVE_ID = "slaveId";
  private static final String MODEL = "model";
  private static final String registerJsonFile = "registers.json";
  private static TypeAdapter<Register[]> registersAdapter;
  private static Register[] defaultInstance;

  public static Register[] loadFromJson(String jsonStr) throws IOException {
    if (registersAdapter == null) {
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

  /**
   * 从 Json 文件加载 传感器配置
   */
  public static Register[] loadFromJson(Context context) throws IOException {
    defaultInstance = JsonUtils.loadFromJson(context, registerJsonFile, Register[].class);
    return defaultInstance;
  }
}
