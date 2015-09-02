package com.lingya.farmintell.mqttclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;


/**
 * MQTT client 设置首选 Created by zwq00000 on 2015/6/12.
 */
public class MqttPreferences {

  public static String KEY_MQTT_CLIENT_ID;
  public static String KEY_MQTT_PORT;
  public static String KEY_MQTT_ADDRESS;
  private static MqttPreferences mInstance;
  protected final SharedPreferences preferences;

  protected MqttPreferences(Context context) {
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    Resources resource = context.getResources();
    KEY_MQTT_ADDRESS = resource.getString(R.string.key_mqtt_address);
    KEY_MQTT_PORT = resource.getString(R.string.key_mqtt_port);
    KEY_MQTT_CLIENT_ID = resource.getString(R.string.key_client_id);
  }

  /**
   * 获取默认实例
   */
  public static MqttPreferences getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new MqttPreferences(context);
    }
    return mInstance;
  }

  public static String getMqttServerAddress(SharedPreferences preferences) {
    String address = preferences.getString(KEY_MQTT_ADDRESS, "");
    if (TextUtils.isEmpty(address)) {
      return "";
    }
    int port = preferences.getInt(KEY_MQTT_PORT, 1883);
    return "tcp://" + address + ":" + port;
  }

  public String getMqttServerAddress() {
    return getMqttServerAddress(this.preferences);
  }

  public String getClientId() {
    return preferences.getString(KEY_MQTT_CLIENT_ID, "");
  }

  /**
   * ���� �ַ� Ĭ��ֵ
   */
  protected void setValue(String key, String value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.commit();
  }

  protected void setValue(String key, int value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(key, value);
    editor.commit();
  }

  /**
   * Registers a callback to be invoked when a change happens to a preference.
   *
   * @param listener The callback that will run.
   * @see #unregisterOnSharedPreferenceChangeListener
   */
  public void registerOnSharedPreferenceChangeListener(
      SharedPreferences.OnSharedPreferenceChangeListener listener) {
    this.preferences.registerOnSharedPreferenceChangeListener(listener);
  }

  /**
   * Unregisters a previous callback.
   *
   * @param listener The callback that should be unregistered.
   * @see #registerOnSharedPreferenceChangeListener
   */
  void unregisterOnSharedPreferenceChangeListener(
      SharedPreferences.OnSharedPreferenceChangeListener listener) {
    this.preferences.unregisterOnSharedPreferenceChangeListener(listener);
  }
}
