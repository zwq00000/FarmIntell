package com.lingya.farmintell.httpserver.adapters;

import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.AbstractList;
import java.util.LinkedList;

/**
 * Created by zwq00000 on 2015/7/10.
 */
public class WebSocketAdapter implements AsyncHttpServer.WebSocketRequestCallback {

  private static final String TAG = WebSocketAdapter.class.getSimpleName();
  private Object syncLock = new Object();
  private AbstractList<WebSocket> webSocketArrayList = new LinkedList<WebSocket>();

  @Override
  public void onConnected(WebSocket webSocket, AsyncHttpServerRequest request) {
    initWebSocket(webSocket);
    synchronized (syncLock) {
      webSocketArrayList.add(webSocket);
    }
  }

  private void initWebSocket(WebSocket webSocket) {
    webSocket.setStringCallback(new WebSocket.StringCallback() {
      @Override
      public void onStringAvailable(String s) {
        Log.d(TAG, "onStringAvailable:" + s);
      }
    });
    webSocket.setPongCallback(new WebSocket.PongCallback() {
      @Override
      public void onPongReceived(String s) {
        Log.d(TAG, "onPongReceived:" + s);
        if (!TextUtils.equals(s, "ping")) {
          //HttpServerTest.this.webSocketes.remove(webSocket);
        }
      }
    });
  }

  public void send(String string) {
    synchronized (syncLock) {
      for (int i = 0; i < webSocketArrayList.size(); i++) {
        WebSocket item = webSocketArrayList.get(i);
        if (item.isOpen()) {
          item.send(string);
        }
      }
    }
  }

  public void send(byte[] bytes, int offset, int len) {
    synchronized (syncLock) {
      for (int i = 0; i < webSocketArrayList.size(); i++) {
        WebSocket item = webSocketArrayList.get(i);
        if (item.isOpen()) {
          item.send(bytes, offset, len);
        }
      }
    }
    clearClosedSocket();
  }

  private void clearClosedSocket() {
    synchronized (syncLock) {
      for (int i = 0; i < webSocketArrayList.size(); i++) {
        WebSocket item = webSocketArrayList.get(i);
        if (!item.isOpen()) {
          webSocketArrayList.remove(item);
        }
      }
    }
  }
}
