package com.lingya.farmintell.httpserver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.lingya.farmintell.httpserver.adapters.WebSocketAdapter;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;

public class HttpService extends Service {

  private static final String TAG = HttpService.class.getSimpleName();
  /**
   * 传感器状态 更新 标识
   */
  private static final String UPDATE_SENSOR_STATUS = "SensorService.UPDATE_SENSOR_STATUS";
  private static String sensorStatusJson = null;
  AsyncHttpServer asyncHttpServer = new AsyncHttpServer();
  WebSocketAdapter webSocketAdapter = new WebSocketAdapter();
  private AbstractList<WebSocket> webSocketArrayList = new ArrayList<WebSocket>(10);
  private BroadcastReceiver sensorStatusReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final String jsonStr = intent.getStringExtra("JSON");
      if (TextUtils.isEmpty(jsonStr)) {
        return;
      }
      sensorStatusJson = jsonStr;
      webSocketAdapter.send(jsonStr);
    }
  };

  public HttpService() {

  }

  /**
   * Called by the system when the service is first created.  Do not call this method directly.
   */
  public void onCreate() {
    super.onCreate();
    asyncHttpServer = new AsyncHttpServer();
    initHttpServer(asyncHttpServer);
    initSensorReceiver();
  }

  /**
   * @deprecated Implement {@link #onStartCommand(Intent, int, int)} instead.
   */
  @Deprecated
  public void onStart(Intent intent, int startId) {
    startHttpServer();
  }

  @Override
  public IBinder onBind(Intent intent) {
    if (asyncHttpServer != null) {
      startHttpServer();
    }
    return null;
  }

  @Override
  public void onDestroy() {
    if (asyncHttpServer != null) {
      asyncHttpServer.stop();
    }
  }

  private void initHttpServer(AsyncHttpServer server) {
    try {
      WebSiteFactory.copyAssetToWebSite(this, "index.html");
    } catch (IOException e) {
      e.printStackTrace();
    }
    server.directory(this, "/assets/.*?", "");
    server.directory("/index.html", WebSiteFactory.getWebSiteFolder(this), true);

    SharedPreferencesRequestCallback
        prefCallback =
        new SharedPreferencesRequestCallback(this);
    server.get("/pref/.*?", prefCallback);
    server.post("/pref/.*?", prefCallback);
    server.websocket("/websocket", webSocketAdapter);
  }

  private void startHttpServer() {
    if (asyncHttpServer == null) {
      asyncHttpServer = new AsyncHttpServer();
      initHttpServer(asyncHttpServer);
    }
    asyncHttpServer.stop();
    asyncHttpServer.listen(AsyncServer.getDefault(), 8080);
  }

  private void sendSensorStatusJson(WebSocket webSocket) {
    if (!TextUtils.isEmpty(sensorStatusJson)) {
      String jsonStr = sensorStatusJson;
      webSocket.send(jsonStr);
    }
  }

  private void initSensorReceiver() {
    IntentFilter intentFilter = new IntentFilter(UPDATE_SENSOR_STATUS);
    this.registerReceiver(sensorStatusReceiver, intentFilter);
  }
}
