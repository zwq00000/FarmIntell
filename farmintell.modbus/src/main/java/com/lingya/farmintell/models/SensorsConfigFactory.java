package com.lingya.farmintell.models;

import android.content.Context;

import com.lingya.farmintell.utils.JsonUtils;

import java.io.IOException;

/**
 * Created by zwq00000 on 2015/7/29.
 */
public class SensorsConfigFactory {

  private static final String configJsonFile = "sensorsConfig.json";
  private static SensorsConfig defaultInstance;

  /**
   * 从 Json 文件加载配置
   */
  public static SensorsConfig loadFromJson(Context context) throws IOException {
    defaultInstance = JsonUtils.loadFromJson(context, configJsonFile, SensorsConfig.class);
    return defaultInstance;
  }

  /**
   * 获取默认实例
   */
  public static SensorsConfig getDefaultInstance(Context context) throws IOException {
    if (defaultInstance == null) {
      loadFromJson(context);
    }
    return defaultInstance;
  }
}
