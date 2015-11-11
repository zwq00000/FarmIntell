package com.lingya.farmintell.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

public class SensorStatusUpdatedReceiver extends BroadcastReceiver {

    private static final String TAG = "SensorStatusUpdatedReceiver";
    private static long lastSendTime = System.currentTimeMillis();

    public SensorStatusUpdatedReceiver() {
    }

    /**
     * 网络是否连接
     *
     * @param context
     * @return
     */
    static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            } else {
                Log.d(TAG, "getActiveNetworkInfo is null");
            }
        }
        return false;
    }

    /**
     * 响应 接送到 传感器数据更新 广播消息事件 {@see SensorService.UPDATE_SENSOR_STATUS}
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        //检查发送周期
        if (System.currentTimeMillis() < lastSendTime) {
            return;
        } else {
            int sendInterval = ClientAdapterPreferences.getInstance(context).getSendInterval();
            lastSendTime = System.currentTimeMillis() + sendInterval * 1000;
        }
        //检查网络状态
        //if(!isNetworkConnected(context)){
        //    return;
        //}

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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
