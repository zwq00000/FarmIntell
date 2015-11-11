package com.lingya.farmintell.httpserver.adapters;

import com.koushikdutta.async.http.server.AsyncHttpServer;

import java.util.HashMap;

/**
 * Created by zwq00000 on 2015/7/27.
 */
public class WebSocketFactory {

    private static final HashMap<String, WebSocketAdapter>
            websocketMap =
            new HashMap<String, WebSocketAdapter>();

    public static void connect(AsyncHttpServer server, String regex) {
        if (!websocketMap.containsKey(regex)) {
            server.websocket(regex, createInstance(regex));
        }
    }

    public static WebSocketAdapter createInstance(String regex) {
        if (!websocketMap.containsKey(regex)) {
            WebSocketAdapter instance = new WebSocketAdapter();
            websocketMap.put(regex, instance);
        }
        return websocketMap.get(regex);
    }

    public static WebSocketAdapter getInstance(String regex) {
        if (websocketMap.containsKey(regex)) {
            return websocketMap.get(regex);
        }
        return null;
    }
}
