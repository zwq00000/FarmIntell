package com.lingya.farmintell.mqttclient;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

/**
 * MQTT 客户端 适配器 Created by zwq00000 on 2015/6/22.
 */
public class MqttClientAdapter {

  private static final String TAG = "MqttClientAdapter";
  private MqttClient client;
  private Context context;
  private MqttCallback callback = new MqttCallback() {
    @Override
    public void connectionLost(Throwable cause) {
      //失去连接,等待1分钟后重试
      Log.e(TAG, "connectionLost " + cause.getMessage());
    }

    @Override
    public void messageArrived(MqttTopic topic, MqttMessage message) throws Exception {
      Log.e(TAG, "messageArrived " + topic.toString());
    }

    @Override
    public void deliveryComplete(MqttDeliveryToken token) {
      //消息分发成功
      Log.d(TAG, "deliveryComplete " + token.toString());
    }
  };
  private MqttMessage message = new MqttMessage();
  private String topic;

  public MqttClientAdapter(Context context, String serverUrl) throws MqttException {
    this(context);
    connect(serverUrl);
  }

  public MqttClientAdapter(Context context) throws MqttException {
    this.context = context;
    this.topic = getTopic();
  }

  private static boolean isValidateURI(String serverURI) {
    if (serverURI.startsWith("tcp://")) {
      return true;
    } else if (serverURI.startsWith("ssl://")) {
      return true;
    } else {
      return serverURI.startsWith("local://");
    }
  }

  /**
   * 建立 MQTT 连接
   */
  public void connect(String serverUrl) throws MqttException {
    if (this.client != null && client.isConnected()) {
      this.client.disconnect();
    }
    if (!isValidateURI(serverUrl)) {
      //url 不合法
      return;
    }
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "mqtt client");
    client = new MqttClient(serverUrl, "userId", new MemoryPersistence(), wl);
    client.setCallback(this.callback);
    client.connect();
  }

  /**
   * 断开连接
   */
  public void disconnect() throws MqttException {
    if (client != null && client.isConnected()) {
      client.disconnect();
    }
  }

  public boolean isConnected() {
    return client != null && client.isConnected();
  }

  /**
   * 获取 发布主题
   */
  String getTopic() {
    return context.getPackageName().replace('.', '/') + "/" + MqttPreferences.getInstance(context)
        .getClientId() + "/Sensors";
  }

  /**
   * 发布数据
   */
  public void publish(String json) throws MqttException {
    if (client == null || !client.isConnected()) {
      Log.e(TAG, "mqtt client is null or not connected");
      return;
    }
    if (json == null) {
      Log.e(TAG, "publish content is not been null");
      return;
    }
    message.setPayload(json.getBytes());
    client.getTopic(this.topic).publish(message);
  }
}
