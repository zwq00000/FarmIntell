package com.lingya.farmintell.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;

public class SensorStatusUpdatedReceiver extends BroadcastReceiver {

    private static final String TAG = "SensorStatusUpdatedReceiver";
    private static long lastSendTime = System.currentTimeMillis();

    public SensorStatusUpdatedReceiver() {
    }

    /**
     * 响应 接送到 传感器数据更新 广播消息事件 {@see SensorService.UPDATE_SENSOR_STATUS}
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (System.currentTimeMillis() < lastSendTime) {
            return;
        }
        int sendInterval = ClientAdapterPreferences.getInstance(context).getSendInterval();
        lastSendTime = System.currentTimeMillis() + sendInterval * 1000;

        final ClientAdapter adapter = ClientAdapter.getInstance(context);
        if (adapter == null) {
            return;
        }

        final String jsonStr = intent.getStringExtra("JSON");
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    adapter.publish(jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });
    }
}
