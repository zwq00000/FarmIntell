package com.lingya.farmintell.client;

import android.content.Context;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

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
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * 客户端 适配器
 * Created by zwq00000 on 2015/6/22.
 */
public abstract class ClientAdapter implements Closeable {

    private static final String TAG = "ClientAdapter";
    static WebApiClientAdapter webApiClientAdapter;
    static MqttClientAdapter mqttClientAdapter;
    protected String serverUrl;
    private Context context;

    public ClientAdapter(Context context, String serverUrl) throws IOException {
        this(context);
        this.serverUrl = serverUrl;
    }

    public ClientAdapter(Context context) throws IOException {
        this.context = context;
    }

    /**
     * 获取默认适配器
     *
     * @param context
     * @return
     */
    public static ClientAdapter getInstance(Context context) {
        ClientAdapterPreferences preferences = ClientAdapterPreferences.getInstance(context);
        String serverUrl = preferences.getServerUrl();
        if (TextUtils.isEmpty(serverUrl)) {
            return null;
        }

        if (preferences.isMqttServer()) {

            if (mqttClientAdapter == null) {
                try {
                    mqttClientAdapter = new MqttClientAdapter(context, serverUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mqttClientAdapter.setServerUrl(serverUrl);
            }
            return mqttClientAdapter;
        } else {
            if (webApiClientAdapter == null) {
                try {
                    webApiClientAdapter = new WebApiClientAdapter(context, serverUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                webApiClientAdapter.setServerUrl(serverUrl);
            }
            return webApiClientAdapter;
        }
    }

    public String getServerUrl() {
        return serverUrl;
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

    public Context getContext() {
        return context;
    }

    /**
     * WebApi 访问接口
     */
    interface FarmIntellWebApi {
        @POST("/api/Sensors")
        String postSensorStatus(@Body String json);
    }

    /**
     * WebApi 客户端
     */
    static class WebApiClientAdapter extends ClientAdapter {

        private FarmIntellWebApi webApi;
        private RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.BASIC;
        ;

        public WebApiClientAdapter(Context context, String serverUrl) throws IOException {
            super(context, serverUrl);

            //if ("debug".equals(BuildConfig.BUILD_TYPE)) {
            logLevel = RestAdapter.LogLevel.FULL;
            //}
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


            RestAdapter adapter = new RestAdapter.Builder()
                    .setConverter(new JsonStringConvert())
                    .setEndpoint(getServerUrl())
                    .setLogLevel(logLevel)
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
            if (webApi == null) {
                connect();
            }
            if (webApi == null) {
                return;
            }
            webApi.postSensorStatus(json);
        }
    }

    /**
     * MQTT 客户端适配器
     */
    static class MqttClientAdapter extends ClientAdapter {

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

        public MqttClientAdapter(Context context, String serverUrl) throws IOException {
            super(context, serverUrl);
            this.topic = getTopic(context);
        }

        /**
         * 获取 发布主题
         */
        private String getTopic(Context context) {
            return context.getPackageName().replace('.', '/') + "/" + ClientAdapterPreferences.getInstance(context)
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
                PowerManager pm = (PowerManager) super.getContext().getSystemService(Context.POWER_SERVICE);
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

            if (json == null) {
                Log.e(TAG, "publish content is not been null");
                return;
            }
            if (client == null || !client.isConnected()) {
                this.connect();
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
