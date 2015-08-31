package com.lingya.farmintell.mqttclient;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttException;

public class SensorStatusUpdatedReceiver extends BroadcastReceiver {

  private static final String TAG = "SensorStatusUpdatedReceiver";
  private static MqttClientAdapter mqttClient;
  /**
   * Mqtt 服务器地址
   */
  private static String mqttServerUrl;

  /**
   * 首选项变更 侦听器,当首选项 Mqtt Server 地址变更时重新连接 Mqtt服务器
   */
  private static SharedPreferences.OnSharedPreferenceChangeListener
      preferenceChangeListener =
      new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
          if (TextUtils.equals(key, MqttPreferences.KEY_MQTT_ADDRESS)) {
            mqttServerUrl = MqttPreferences.getMqttServerAddress(sharedPreferences);
            if (mqttClient != null) {
              try {
                mqttClient.connect(mqttServerUrl);
              } catch (MqttException e) {
                e.printStackTrace();
              }
            }
          }
        }
      };
  private static MqttPreferences preference;

  public SensorStatusUpdatedReceiver() {
  }

  /**
   * 发送 传感器状态
   */
  private static void publishSensorStatus(Context context, String jsonStr)
      throws MqttException {
    initMqttClient(context);
    if (mqttClient != null) {
      try {
        if (mqttClient.isConnected()) {
          mqttClient.publish(jsonStr);
        }
      } catch (MqttException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 初始化 MQTT 客户端
   */
  @SuppressLint("LongLogTag")
  private static void initMqttClient(Context context) throws MqttException {
    if (preference == null) {
      preference = MqttPreferences.getInstance(context);
      preference.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
      mqttServerUrl = preference.getMqttServerAddress();
      Log.d(TAG, mqttServerUrl);
    }
    if (mqttClient == null) {
      mqttClient = new MqttClientAdapter(context);
      mqttClient.connect(mqttServerUrl);
    } else if (!mqttClient.isConnected()) {
      mqttClient.connect(mqttServerUrl);
    }
  }

  /**
   * 响应 接送到 传感器数据更新 广播消息事件 {@see SensorService.UPDATE_SENSOR_STATUS}
   */
  @Override
  public void onReceive(final Context context, final Intent intent) {
    final String jsonStr = intent.getStringExtra("JSON");
    if (TextUtils.isEmpty(jsonStr)) {
      return;
    }
    AsyncTask.execute(new Runnable() {
      @Override
      public void run() {
        try {
          publishSensorStatus(context, jsonStr);
        } catch (MqttException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
