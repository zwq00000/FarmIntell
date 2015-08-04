package com.lingya.farmintell.utils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by zwq00000 on 2015/7/29.
 */
public class JsonUtils {

  static final Gson gson = new Gson();
  /**
   * 配置文件 存放目录
   */
  private static final String SETTINGS_FILE_DIR = "settings";

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
  public static <T> T loadFromJson(Context context, String jsonFilePath, Class<T> entityType)
      throws IOException {
    File settingsDir = context.getDir(SETTINGS_FILE_DIR, Context.MODE_WORLD_WRITEABLE);
    if (!settingsDir.exists()) {
      settingsDir.mkdir();
    }
    File jsonFile = new File(settingsDir, jsonFilePath);
    if (!jsonFile.exists()) {
      //复制默认配置到 Settings 目录
      InputStream stream = context.getAssets().open(jsonFilePath);
      FileOutputStream output = new FileOutputStream(jsonFile);
      copyStream(stream, output);
    }
    InputStream stream = new FileInputStream(jsonFile);
    try {
      TypeAdapter<T> jsonAdapter = gson.getAdapter(entityType);
      return jsonAdapter.fromJson(new InputStreamReader(stream));
    } finally {
      stream.close();
    }
  }
}
