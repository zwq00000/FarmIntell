package com.lingya.farmintell.httpserver;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zwq00000 on 2015/6/30.
 */
public class HttpServerTest extends AndroidTestCase {

  private static final String TAG = "HttpServerTest";

  private Context context;
  private AbstractList<WebSocket> webSocketes = new ArrayList<WebSocket>(10);

  public void setUp() throws Exception {
    context = getContext();
  }

  public void testStartHttpServer() throws Exception {
    assertNotNull(getContext());
    assertNotNull(getContext().getApplicationContext());
    getContext().startService(new Intent("startServer", null, getContext(), HttpService.class));
    Log.d(TAG, "start httpserver");
    Thread.sleep(1000 * 1000);
  }

  public void testAssetsManager() throws Exception {
    Context context = getContext();
    AssetManager assets = context.getAssets();
    String[] list = assets.list("/assets");
    for (String file :
        list) {
      System.out.println("Assets List '/' :" + file);
    }
  }

  public void testStartServer() throws Exception {
    startHttpServer();
    Thread.sleep(1000 * 1000);
  }

  public void testMatcher() throws Exception {
    matcher("/.*?", "/index.html");
    matcher("/.*", "/index.html");
    matcher("/(.*)", "/index.html");
    matcher("/", "/assets/index.html");
    matcher("/.*", "/assets/index.html");
    matcher("/(.*)", "/assets/index.html");
  }

  private void matcher(String regex, String input) {
    Pattern pattern = Pattern.compile("^" + regex);
    Matcher m = pattern.matcher(input);
    System.out.println(
        "regiex:" + pattern.pattern() + "\tinput:" + input + "\tmatches:" + m.matches()
        + "\treplaceAll:" + m.replaceAll(""));
  }

  private void startHttpServer() {
    AsyncHttpServer server = new AsyncHttpServer();
    server.directory(getContext(), "/assets/.*?", "");
    //asyncHttpServer.directory(getContext(), "/.*?", "");
    //asyncHttpServer.get("/", new AssetsDirectoryCallback(getContext()));
    server.directory("/index.html", WebSiteFactory.getWebSiteFolder(getContext()), true);
    SharedPreferencesRequestCallback
        prefCallback =
        new SharedPreferencesRequestCallback(getContext());
    server.get("/pref/.*?", prefCallback);
    server.post("/pref/.*?", prefCallback);
    server.websocket("/live", new AsyncHttpServer.WebSocketRequestCallback() {
      @Override
      public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
        HttpServerTest.this.webSocketes.add(webSocket);
        webSocket.setStringCallback(new WebSocket.StringCallback() {
          @Override
          public void onStringAvailable(String s) {
            Log.d(TAG, "onStringAvailable:" + s);
            webSocket.send("Welcome Client!");
          }
        });
        webSocket.setPongCallback(new WebSocket.PongCallback() {
          @Override
          public void onPongReceived(String s) {
            Log.d(TAG, "onPongReceived:" + s);
            if (!TextUtils.equals(s, "pong")) {
              //HttpServerTest.this.webSocketes.remove(webSocket);
            }
          }
        });
      }
    });
    server.listen(AsyncServer.getDefault(), 8080);
    Log.d(TAG, "start httpserver 8080");
  }

  public void testWebSocket() throws Exception {
    startHttpServer();
    int step = 0;
    while (true) {
      Thread.sleep(1000);

      Log.d(TAG, "step:" + step + "\twebSocketes.size:" + this.webSocketes.size());
      for (int i = 0; i < this.webSocketes.size(); i++) {
        WebSocket item = this.webSocketes.get(i);
        if (item.isOpen()) {
          item.send("{\"item\":" + i + ",\"step\":" + step++ + "}");
        } else {
          Log.d(TAG, "remove socket " + i);
          webSocketes.remove(i);
        }
      }
    }
  }

  public void testRequestHttpServer() throws Exception {
    assertNotNull(AsyncHttpServer.getAssetStream(getContext(), "index.html"));
    assertNull(AsyncHttpServer.getAssetStream(getContext(), "index.htm"));
    assertNotNull(AsyncHttpServer.getAssetStream(getContext(), "css/bootstrap.min.css"));
    assertNotNull(AsyncHttpServer.getAssetStream(getContext(), "js/bootstrap.js"));
    assertNotNull(
        AsyncHttpServer.getAssetStream(getContext(), "fonts/glyphicons-halflings-regular.eot"));
    startHttpServer();
    String url = "http://192.168.0.196:8080/index.html";
    AsyncHttpClient client = new AsyncHttpClient(new AsyncServer());
    client.executeString(
        new AsyncHttpGet(url),
        new AsyncHttpClient.StringCallback() {
          @Override
          public void onCompleted(Exception e,
                                  AsyncHttpResponse response,
                                  String s) {
            if (e != null) {
              e.printStackTrace();
            }
            if (response != null) {
              assertNotSame(response.code(), 404);
            }
            System.out.println(s);
          }
        });
    Thread.sleep(1000 * 100);
  }
}

