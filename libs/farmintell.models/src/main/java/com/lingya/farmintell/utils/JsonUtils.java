package com.lingya.farmintell.utils;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zwq00000 on 2015/7/29.
 */
public class JsonUtils {

    static final Gson gson = new Gson();
    //通讯秘钥 日期转换格式
    static final DateFormat SECRET_KEY_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    /**
     * 配置文件 存放目录
     */
    private static final String SETTINGS_FILE_DIR = "settings";
    // 通讯秘钥 字符串长度
    private static final int SECRET_KEY_LENGTH = 10;
    //加密算法
    private static final String DIGEST_ALGORITHM = "SHA-1";

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

    /**
     * 生成 通讯秘钥
     *
     * @param date
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String genSecretkey(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("data is not been null");
        }
        try {
            return toBase64(digest(getTimeString(date))).substring(0, SECRET_KEY_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "No Key";
    }

    private static String toBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT); //ByteUtils.toHexString(bytes);
    }

    private static byte[] digest(String string) throws NoSuchAlgorithmException {
        java.security.MessageDigest sha1 = java.security.MessageDigest.getInstance(DIGEST_ALGORITHM);
        return sha1.digest(string.getBytes());
    }

    public static String getTimeString(Date time) {
        return SECRET_KEY_DATE_FORMAT.format(time);
    }
}
