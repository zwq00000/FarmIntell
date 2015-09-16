package com.lingya.farmintell.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.webkit.URLUtil;


/**
 * MQTT client 设置首选 Created by zwq00000 on 2015/6/12.
 */
public class ClientAdapterPreferences {

    private static final String EMPTY_STRING = "";
    public static String KEY_CLIENT_ID;
    public static String KEY_MQTT_PORT;
    private static String KEY_SEND_INTERVAL;
    private static ClientAdapterPreferences mInstance;
    private static String KEY_SERVER_ADDRESS;
    private static String KEY_SERVER_TYPE;
    protected final SharedPreferences preferences;

    protected ClientAdapterPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * 获取默认实例
     */
    public static ClientAdapterPreferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ClientAdapterPreferences(context);
            Resources resource = context.getResources();
            KEY_SERVER_ADDRESS = resource.getString(R.string.key_server_address);
            KEY_SERVER_TYPE = resource.getString(R.string.key_server_type);
            KEY_MQTT_PORT = resource.getString(R.string.key_mqtt_port);
            KEY_CLIENT_ID = resource.getString(R.string.key_client_id);
            KEY_SEND_INTERVAL = resource.getString(R.string.key_send_interval);
        }
        return mInstance;
    }

    public boolean isMqttServer() {
        return preferences.getBoolean(KEY_SERVER_TYPE, false);
    }

    public String getServerUrl() {
        String url;
        String address = preferences.getString(KEY_SERVER_ADDRESS, EMPTY_STRING);
        if (TextUtils.isEmpty(address)) {
            return EMPTY_STRING;
        }
        if (isMqttServer()) {
            if (TextUtils.isEmpty(address)) {
                return EMPTY_STRING;
            }
            int port = preferences.getInt(KEY_MQTT_PORT, 1883);
            url = "tcp://" + address + ":" + port;
        } else {
            url = "http://" + address;
        }
        if (URLUtil.isValidUrl(url)) {
            return url;
        }
        return EMPTY_STRING;
    }

    public String getClientId() {
        return preferences.getString(KEY_CLIENT_ID, "");
    }

    /**
     * 设置 首选项值
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

    /**
     * 发送间隔时间 默认 30秒
     *
     * @return
     */
    public int getSendInterval() {
        String intervalStr = preferences.getString(KEY_SEND_INTERVAL, "30");
        return Integer.parseInt(intervalStr);
    }
}
