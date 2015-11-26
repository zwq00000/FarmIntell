package com.lingya.farmintell.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.lingya.farmintell.modbus.ModbusRegisterReader;
import com.lingya.farmintell.modbus.Register;
import com.lingya.farmintell.modbus.RegisterFactory;
import com.lingya.farmintell.models.RealmFactory;
import com.lingya.farmintell.models.SensorAverageHelper;
import com.lingya.farmintell.models.SensorLog;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.models.SensorsConfig;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * 传感器管理 服务 Created by zwq00000 on 2015/6/22.
 */
public class SensorService extends Service {

    public static final String START_SERVICE = "SensorService.START_SERVICE";
    /**
     * 传感器状态 更新 标识
     */
    public static final String UPDATE_SENSOR_STATUS = "SensorService.UPDATE_SENSOR_STATUS";
    private static final String TAG = "SensorService";
    //private static final String STOP_SERVICE = "SensorService.STOP_SERVICE";
    // 追加日志 间隔时间（毫秒）
    private static final int APPEND_LOG_INTERVAL = 1000 * 60;
    private Realm defaultRealm;
    private ModbusRegisterReader registerReader;
    private SensorStatusCollection statusCollection;
    private Handler mHandler;
    /**
     * 最近一次 日志更新时间
     */
    private long lastAppendLogTime = System.currentTimeMillis();
    private ModbusRegisterReader.HolderValueChangedListener<Register> registerListener =
            new ModbusRegisterReader.HolderValueChangedListener<Register>() {

                /**
                 * 接收到 modbus 数据数据 帧类型为 @see
                 *
                 * @param holders
                 */
                @Override
                public void onValueChanged(final Register... holders) {
                    boolean isChanged = false;
                    for (Register register : holders) {
                        if (register.hasChanged()) {
                            isChanged = true;
                            break;
                        }
                    }
                    if (!isChanged) {
                        return;
                    }
                    if (SensorService.this.mHandler != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateSensorStatus(holders);
                            }
                        });
                    }
                }
            };

    /**
     * 更新 传感器状态
     */
    private static void updateSensorStatus(Register[] registers,
                                           SensorStatusCollection statusCollection) {
        if (registers == null) {
            throw new IllegalArgumentException("registers is not been null");
        }
        if (statusCollection == null) {
            throw new IllegalArgumentException("statusCollection is not been null");
        }
        if (registers == null) {
            throw new IllegalArgumentException("registers is not been null");
        }
        statusCollection.updateSensorStatus(RegisterFactory.toValueIterator(registers));
    }

    @Override
    public void onCreate() {
        mHandler = new Handler(Looper.myLooper());
        defaultRealm = RealmFactory.getInstance(this);
        //SensorAverageHelper.registerAverageUpdate(this);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * sensorService should clean up an resources it holds (threads, registered receivers, etc) at
     * this point.  Upon return, there will be no more calls in to this Service object and it is
     * effectively dead.  Do not call this method directly.
     */
    public void onDestroy() {
        try {
            closeInternal();
            //SensorAverageHelper.unregisterAverageUpdate(this);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            schedulingRestartService();
            if (defaultRealm != null) {
                defaultRealm.close();
            }
        }
    }

    /**
     * 10 秒后重新启动服务
     */
    private void schedulingRestartService() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(SensorService.START_SERVICE, Uri.EMPTY, this, SensorService.class);
        PendingIntent
                pendingIntent =
                PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager
                .set(AlarmManager.ELAPSED_REALTIME_WAKEUP, TimeUnit.SECONDS.toMillis(5), pendingIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        try {
            startRead();
            Log.d(TAG, "onBind Start Task");
            return new SensorServiceBinderImpl(this);
        } catch (IOException ex) {
            Log.d(TAG, ex.toString());
        }
        return null;
    }

    /**
     * @deprecated Implement {@link super#onStartCommand(Intent, int, int)} instead.
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        try {
            if (this.registerReader == null || this.registerReader.isClosed()) {
                startRead();
            }
        } catch (IOException ex) {
            Log.d(TAG, ex.toString());
        }
        return result;
    }

    /**
     * 启动 读取数据
     */
    private void startRead() throws IOException {
        if (this.registerReader != null && !this.registerReader.isClosed()) {
            this.registerReader.close();
            this.registerReader.setOnValueChangedListener(null);
        }
        SensorsConfig
                config =
                SensorsConfig.getDefaultInstance(this);
        // modbus 传感器集合
        Register[] registers = RegisterFactory.createRegisters(config);
        if (registers == null) {
            throw new IOException("初始化失败,没有设置 传感器");
        }
        this.statusCollection = new SensorStatusCollection(config);
        this.registerReader = ModbusRegisterReader.createInstance(this, registers);
        //注册 数值变更通知
        registerReader.setOnValueChangedListener(registerListener);
        this.registerReader.open();
        Log.d(TAG, "open ModbusRegisterReaderImpl");
    }

    /**
     * 关闭接收器 内部实现
     */
    private void closeInternal() throws IOException {
        if (this.registerReader != null && !registerReader.isClosed()) {
            registerReader.close();
        }
    }

    /**
     * 发送 传感器状态更新 广播消息
     */
    private void sendBroadcast(SensorStatusCollection statusCollection) {
        if (this.statusCollection == null) {
            Log.e(TAG, "statusCollection is null");
        }
        Intent intent = new Intent(UPDATE_SENSOR_STATUS);
        try {
            intent.putExtra("JSON", statusCollection.toJson());
            SensorService.this.sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新传感器状态
     */
    private void updateSensorStatus(Register... holders) {
        Log.v(TAG, "update sensor status");
        updateSensorStatus(holders, this.statusCollection);
        if (System.currentTimeMillis() >= lastAppendLogTime) {
            RealmFactory.appendSensorLog(defaultRealm, statusCollection);
            lastAppendLogTime = System.currentTimeMillis() + APPEND_LOG_INTERVAL;
        }
        sendBroadcast(statusCollection);
    }

    /**
     * Created by zwq00000 on 2015/7/4.
     */
    public interface ISensorBinder {

        /**
         * 获取 传感器状态
         */
        SensorStatusCollection getStatus();

        /**
         * 获取 测量记录 查询
         *
         * @return RealmQuery<SensorLog>
         */
        RealmQuery<SensorLog> querySensorLog();

        /**
         * @return 测量记录日志
         */
        List<SensorLog> getLastOneDaySensorLogs(String sensorId);

        /**
         * 获取 24小时 每小时的状态统计
         */
        float[] get24HourlySummary(String sensorId);

        /**
         * 获取 小时 汇总统计
         *
         * @param endTime @return
         */
        float[] getHourlySummary(String sensorId, Date startTime, Date endTime);
    }

    private static class SensorServiceBinderImpl extends Binder implements ISensorBinder {

        private final SensorService sensorService;

        private SensorServiceBinderImpl(SensorService sensorService) {
            this.sensorService = sensorService;
        }

        /**
         * 获取 传感器 状态
         */
        @Override
        public SensorStatusCollection getStatus() {
            return sensorService.statusCollection;
        }


        /**
         * 查询 传感器数据日志
         */
        @Override
        public RealmQuery<SensorLog> querySensorLog() {
            Realm realm = RealmFactory.getInstance(sensorService);
            return realm.where(SensorLog.class);
        }

        /**
         * 获取最近一天的 数据日志
         */
        @Override
        public List<SensorLog> getLastOneDaySensorLogs(String sensorId) {
            if (sensorService == null) {
                return null;
            }
            Date lastDate = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));
            Realm realm = RealmFactory.getInstance(sensorService);
            try {
                return realm.where(SensorLog.class)
                        .equalTo("sensorId", sensorId)
                        .greaterThanOrEqualTo("time", lastDate)
                        .findAll();
            } finally {
                realm.close();
            }
        }

        /**
         * 获取 24小时 每小时的状态统计
         */
        @Override
        public float[] get24HourlySummary(String sensorId) {
            Date startTime = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));
            return SensorAverageHelper.queryAverage(sensorService.defaultRealm, sensorId, startTime, 24);
        }

        /**
         * 获取 小时 汇总统计
         */
        @Override
        public float[] getHourlySummary(String sensorId, Date startTime, Date endTime) {
            if (TextUtils.isEmpty(sensorId)) {
                throw new IllegalArgumentException("sensorId is not been null Or Empty");
            }
            Calendar startCalendar = GregorianCalendar.getInstance();
            startCalendar.setTime(startTime);
            Calendar endCalendar = GregorianCalendar.getInstance();
            endCalendar.setTime(endTime);
            Realm realm = RealmFactory.getInstance(sensorService);
            try {
                return RealmFactory
                        .queryHourlySummary(realm, sensorId, startCalendar, endCalendar);
            } finally {
                realm.close();
            }
        }
    }

}
