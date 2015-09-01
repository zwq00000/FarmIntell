package com.lingya.farmintell;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import com.lingya.farmintell.mqttclient.MqttPreferences;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import java.io.Closeable;
import java.io.IOException;

import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.POST;

/**
 * MQTT 客户端 适配器
 * Created by zwq00000 on 2015/6/22.
 */
public abstract class ClientAdapter implements Closeable {

    private static final String TAG = "ClientAdapter";
    private String serverUrl;

    private Context context;

    public ClientAdapter(Context context, String serverUrl) throws MqttException {
        this(context);
        this.serverUrl = serverUrl;
    }

    public ClientAdapter(Context context) throws MqttException {
        this.context = context;
    }


    /**
     * 设置 服务端 地址
     *
     * @param serverUrl
     */
    public void setServerUrl(String serverUrl) {
        if (!URLUtil.isValidUrl(serverUrl)) {
            throw new IllegalArgumentException("serverUrl is Invalid");
        }
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }


    /**
     * 建立 连接
     */
    public abstract void connect() throws IOException;

    /**
     * 断开连接
     */
    public abstract void disconnect() throws IOException;

    /**
     * 发布数据
     */
    public abstract void publish(String json) throws IOException;

    @Override
    public abstract void close() throws IOException;

    /**
     * WebApi 客户端
     */
    class WebApiClientAdapter extends ClientAdapter {

        private FarmIntellWebApi webApi;

        public WebApiClientAdapter(Context context, String serverUrl) throws MqttException {
            super(context, serverUrl);
        }

        /**
         * 验证 Url 是否合法
         *
         * @param serverUrl
         * @return Url 合法 is true,other is false
         */
        private boolean isValidUrl(String serverUrl) {
            return !TextUtils.isEmpty(serverUrl) && URLUtil.isValidUrl(serverUrl)
                    && (URLUtil.isHttpUrl(serverUrl) || URLUtil.isHttpsUrl(serverUrl));
        }

        /**
         * 建立 连接
         */
        @Override
        public void connect() throws IOException {
            if (!isValidUrl(getServerUrl())) {
                return;
            }
            RestAdapter adapter = new RestAdapter.Builder().setEndpoint(getServerUrl())
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            webApi = adapter.create(FarmIntellWebApi.class);
        }

        /**
         * 断开连接
         */
        @Override
        public void disconnect() throws IOException {
            webApi = null;
        }

        @Override
        public void close() throws IOException {

        }

        /**
         * 发布数据
         */
        public void publish(String json) throws IOException {
            if (webApi != null) {
                webApi.postSensorStatus(json);
            }
        }


    }

    interface FarmIntellWebApi {
        @POST("/Sensors")
        void postSensorStatus(@Field("value") String json);
    }

    class MqttClientAdapter extends ClientAdapter {

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
        private MqttClient client;

        public MqttClientAdapter(Context context, String serverUrl) throws MqttException {
            super(context, serverUrl);
            this.topic = getTopic(context);
        }

        /**
         * 获取 发布主题
         */
        private String getTopic(Context context) {
            return context.getPackageName().replace('.', '/') + "/" + MqttPreferences.getInstance(context)
                    .getClientId() + "/Sensors";
        }

        private boolean isValidUrl(String serverUrl) {
            if (serverUrl.startsWith("tcp://")) {
                return true;
            } else if (serverUrl.startsWith("ssl://")) {
                return true;
            } else {
                return serverUrl.startsWith("local://");
            }
        }

        /**
         * 建立 连接
         */
        @Override
        public void connect() throws IOException {
            try {
                if (this.client != null && client.isConnected()) {
                    this.client.disconnect();
                }
                if (!isValidUrl(serverUrl)) {
                    //url 不合法
                    return;
                }
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "mqtt client");
                client = new MqttClient(serverUrl, "userId", new MemoryPersistence(), wl);
                client.setCallback(this.callback);
                client.connect();
            } catch (MqttException mqtte) {
                mqtte.printStackTrace();
                throw new IOException(mqtte.getMessage());
            }
        }

        /**
         * 断开连接
         */
        @Override
        public void disconnect() throws IOException {
            if (client != null && client.isConnected()) {
                try {
                    client.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                    throw new IOException(e.getMessage());
                }
            }
        }

        /**
         * 发布数据
         *
         * @param json
         */
        @Override
        public void publish(String json) throws IOException {
            if (client == null || !client.isConnected()) {
                Log.e(TAG, "mqtt client is null or not connected");
                return;
            }
            if (json == null) {
                Log.e(TAG, "publish content is not been null");
                return;
            }
            message.setPayload(json.getBytes());
            try {
                client.getTopic(this.topic).publish(message);
            } catch (MqttException e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            }
        }

        @Override
        public void close() throws IOException {
            try {
                disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getMessage());
            }
        }
    }
}
