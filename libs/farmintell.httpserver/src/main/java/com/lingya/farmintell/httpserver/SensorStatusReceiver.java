package com.lingya.farmintell.httpserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.lingya.farmintell.httpserver.adapters.WebSocketAdapter;
import com.lingya.farmintell.httpserver.adapters.WebSocketFactory;

/**
 * 传感器状态更新 接收器,转发到 WebSocket
 */
public class SensorStatusReceiver extends BroadcastReceiver {

    private static final String TAG = "SensorStatusReceiver";
    private static final String SOCKET_NAME = "/websocket";

    public SensorStatusReceiver() {
    }

    @Override
    public void onReceive(Context context, final Intent intent) {

        final WebSocketAdapter adapter = WebSocketFactory.getInstance(SOCKET_NAME);
        if (adapter != null && adapter.hasActiveConnection()) {
            //发送 websocket 广播
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null && adapter.hasActiveConnection()) {
                        String jsonStr = intent.getStringExtra("JSON");
                        adapter.send(jsonStr);
                        Log.d(TAG, "send SENSOR_STATUS to websocket");
                    }
                }
            });
        }
    }
}
