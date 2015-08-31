package com.lingya.farmintell.mqttclient;

import android.content.pm.ApplicationInfo;
import android.test.AndroidTestCase;

/**
 * Created by zwq00000 on 2015/6/29.
 */
public class MqttClientAdapterTest extends AndroidTestCase {

  private String mqttServerUrl;

  public void setUp() throws Exception {
    super.setUp();
    MqttPreferences pref = MqttPreferences.getInstance(getContext());
    pref.setValue(MqttPreferences.KEY_MQTT_ADDRESS, "192.168.0.104");
    pref.setValue(MqttPreferences.KEY_MQTT_PORT, 1883);
    pref.setValue(MqttPreferences.KEY_MQTT_CLIENT_ID, "fram_0001");
    mqttServerUrl = pref.getMqttServerAddress();
    System.out.println(pref.getMqttServerAddress());
  }

  public void tearDown() throws Exception {

  }

  public void testPublish() throws Exception {
    MqttClientAdapter adapter = new MqttClientAdapter(getContext(), mqttServerUrl);
    for (int i = 0; i < 10; i++) {
      adapter.publish(Integer.toString(i));
      Thread.sleep(1000);
    }
  }

  public void testGetTopic() throws Exception {
    MqttClientAdapter adapter = new MqttClientAdapter(getContext());
    System.out.println("topic:" + adapter.getTopic());
    ApplicationInfo appInfo = getContext().getApplicationInfo();
    System.out.println(appInfo.toString());
  }
}