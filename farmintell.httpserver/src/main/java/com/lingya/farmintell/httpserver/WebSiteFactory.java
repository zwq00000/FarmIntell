package com.lingya.farmintell.httpserver;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * web站点构造工厂 Created by zwq00000 on 2015/7/2.
 */
public class WebSiteFactory {

  /**
   * 默认站点目录名称
   */
  public static final String WEBSITE_FOLDER_NAME = "website";

  /**
   * 获取 站点目录文件
   */
  public static File getWebSiteFolder(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("context is not been null");
    }
    File siteFolder = context.getDir(WEBSITE_FOLDER_NAME, Context.MODE_WORLD_WRITEABLE);
    if (!siteFolder.exists()) {
      siteFolder.mkdir();
    }
    return siteFolder;
  }

  /**
   * 复制 资产文件 到 默认站点目录
   */
  public static File copyAssetToWebSite(Context context, String assetFileName)
      throws IOException {
    File siteFolder = getWebSiteFolder(context);
    File targetFile = new File(siteFolder, assetFileName);
    if (!targetFile.exists()) {
      //复制默认配置到 website 目录
      InputStream stream = context.getAssets().open(assetFileName);
      FileOutputStream output = new FileOutputStream(targetFile);
      copyStream(stream, output);
    }
    return targetFile;
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
}
