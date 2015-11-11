package com.lingya.farmintell.httpserver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.Util;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    private static final String TAG = "WebSiteFactory";
    private static String[] defaultDocuments = new String[]{"index.html"};
    private final Context context;
    private HttpServerRequestCallback defaultDocumentCallback = new HttpServerRequestCallback() {
        @Override
        public void onRequest(AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
            FileInputStream is = null;
            try {
                is = new FileInputStream(WebSiteFactory.this.getDefaultDoc(request.getPath()));
                response.code(200);
                Util.pump(is, response, new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        response.end();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    public WebSiteFactory(Context context) {
        this.context = context;
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

    public static void setDefaultDocuments(String[] fileNames) {
        if (fileNames != null && fileNames.length > 0) {
            defaultDocuments = fileNames;
        }
    }

    /**
     * 获取 站点目录文件
     */
    public File getWebSiteFolder() {
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
    public File copyAssetToWebSite(String assetFileName)
            throws IOException {
        File siteFolder = getWebSiteFolder();
        File targetFile = new File(siteFolder, assetFileName);
        if (!targetFile.exists()) {
            //复制默认配置到 website 目录
            InputStream stream = context.getAssets().open(assetFileName);
            FileOutputStream output = new FileOutputStream(targetFile);
            copyStream(stream, output);
        }
        return targetFile;
    }

    private File getDefaultDoc(String path) {
        if (TextUtils.isEmpty(path) || path.equalsIgnoreCase("/")) {
            File dir = getWebSiteFolder();
            for (String fileName : defaultDocuments) {
                File file = new File(dir, fileName);
                if (file.exists()) {
                    return file;
                }
            }
        }
        Log.d(TAG, "getDefaultDoc:" + path);
        return new File(getWebSiteFolder(), "." + path);
    }

    public HttpServerRequestCallback getDefaultDocCallback() {
        return defaultDocumentCallback;
    }
}
