package com.lingya.farmintell.httpserver.adapters;

import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.AbstractList;
import java.util.LinkedList;

/**
 * WebSocket 适配器 Created by zwq00000 on 2015/7/10.
 */
public class WebSocketAdapter implements AsyncHttpServer.WebSocketRequestCallback {

    private static final String TAG = WebSocketAdapter.class.getSimpleName();
    /**
     * 同步对象
     */
    private final Object syncLock = new Object();
    private String lastSendValue;
    private AbstractList<WebSocket> webSocketArrayList = new LinkedList<WebSocket>();

    @Override
    public void onConnected(WebSocket webSocket, AsyncHttpServerRequest request) {
        initWebSocket(webSocket);
        synchronized (syncLock) {
            Log.d(TAG, "onConnected Path:" + request.getPath());
            webSocketArrayList.add(webSocket);
            if (!TextUtils.isEmpty(lastSendValue)) {
                webSocket.send(lastSendValue);
            }
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
        if (TextUtils.isEmpty(string)) {
            return;
        }
        int clientCount = 0;
        synchronized (syncLock) {
            for (int i = 0; i < webSocketArrayList.size(); i++) {
                WebSocket item = webSocketArrayList.get(i);
                if (item.isOpen()) {
                    item.send(string);
                    clientCount++;
                }
            }
            lastSendValue = string;
        }
        if (clientCount > 0) {
            Log.d(TAG, "send websocket clients " + clientCount);
        }
        clearClosedSocket();
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
        int clientCount = 0;
        synchronized (syncLock) {
            for (int i = 0; i < webSocketArrayList.size(); i++) {
                WebSocket item = webSocketArrayList.get(i);
                if (!item.isOpen()) {
                    webSocketArrayList.remove(item);
                    clientCount++;
                }
            }
        }
        if (clientCount > 0) {
            Log.d(TAG, "remove websocket clients " + clientCount);
        }
    }

    /**
     * 是否有活动连接
     */
    public boolean hasActiveConnection() {
        synchronized (syncLock) {
            for (int i = 0; i < webSocketArrayList.size(); i++) {
                WebSocket item = webSocketArrayList.get(i);
                if (item.isOpen()) {
                    return true;
                }
            }
        }
        return false;
    }
}
