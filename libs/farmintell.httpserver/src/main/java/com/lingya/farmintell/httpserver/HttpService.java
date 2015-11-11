package com.lingya.farmintell.httpserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.lingya.farmintell.httpserver.adapters.WebSocketFactory;

import java.io.IOException;

public class HttpService extends Service {

    private static final String TAG = HttpService.class.getSimpleName();
    AsyncHttpServer asyncHttpServer;
    private boolean isRunning = false;

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    public void onCreate() {
        super.onCreate();
        asyncHttpServer = new AsyncHttpServer();
        initHttpServer(asyncHttpServer);
    }

    /**
     * @deprecated Implement {@link #onStartCommand(Intent, int, int)} instead.
     */
    @Deprecated
    public void onStart(Intent intent, int startId) {
        onBind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (!isRunning) {
            startHttpServer();
        }
        return new HttpServerBinderImpl();
    }

    @Override
    public void onDestroy() {
        if (asyncHttpServer != null) {
            asyncHttpServer.stop();
            isRunning = false;
        }
    }

    /**
     * 初始化 Http Server
     */
    void initHttpServer(AsyncHttpServer server) {
        WebSiteFactory factory = new WebSiteFactory(this);
        try {
            factory.copyAssetToWebSite("index.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.directory(this, "/assets/.*?", "");

        server.get("/index.html", factory.getDefaultDocCallback());
        server.get("/", factory.getDefaultDocCallback());
        server.directory("/settings/.*?", this.getDir("settings", Context.MODE_PRIVATE), true);
        SharedPreferencesRequestCallback
                prefCallback =
                new SharedPreferencesRequestCallback(this);
        server.get("/pref/.*?", prefCallback);
        server.post("/pref/.*?", prefCallback);
        WebSocketFactory.connect(server, "/websocket");
    }

    /**
     * 启动 Http Server
     */
    private void startHttpServer() {
        if (asyncHttpServer == null) {
            asyncHttpServer = new AsyncHttpServer();
            initHttpServer(asyncHttpServer);
        }
        asyncHttpServer.listen(AsyncServer.getDefault(), 8080);
        isRunning = true;
    }

    public interface IHttpServerBinder {

        /**
         * 获取 Http Server 实例
         */
        AsyncHttpServer getHttpServer();
    }

    private class HttpServerBinderImpl extends Binder implements IHttpServerBinder {

        /**
         * 获取 Http Server 实例
         */
        @Override
        public AsyncHttpServer getHttpServer() {
            return HttpService.this.asyncHttpServer;
        }
    }
}
