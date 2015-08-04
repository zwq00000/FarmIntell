package com.lingya.farmintell.httpserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.lingya.farmintell.httpserver.adapters.WebSocketAdapter;
import com.lingya.farmintell.httpserver.adapters.WebSocketFactory;

public class SensorStatusReceiver extends BroadcastReceiver {

  private static final String TAG = "SensorStatusReceiver";

  public SensorStatusReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    final String jsonStr = intent.getStringExtra("JSON");
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        final WebSocketAdapter adapter = WebSocketFactory.getInstance("/websocket");
        if (adapter != null && adapter.hasActiveConnection()) {
          adapter.send(jsonStr);
          Log.d(TAG, "send SENSOR_STATUS to websocket");
        }
      }
    });
  }
}
