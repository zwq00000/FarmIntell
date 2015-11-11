package com.lingya.farmintell.adapters;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.lingya.farmintell.models.SensorLog;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.models.SensorSummary;
import com.lingya.farmintell.services.SensorService;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.realm.RealmQuery;

/**
 * Created by zwq00000 on 2015/7/11.
 */
public class SensorAdapterFactory
        implements ServiceBinderProvider<SensorService.ISensorBinder>, Closeable {


    private static final String TAG = SensorAdapterFactory.class.getSimpleName();
    private static SensorAdapterFactory defaultInstance;
    private static SensorAdapterFactory ret;
    private final Context context;
    private final Handler handler;
    private SensorService.ISensorBinder sensorBinder;
    /**
     * 服务连接
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sensorBinder = (SensorService.ISensorBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sensorBinder = null;
        }
    };
    private Set<ViewAdapter> viewAdapters = new LinkedHashSet<ViewAdapter>();
    private BroadcastReceiver sensorBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataChanged();
                }
            });
        }
    };
    /**
     * 绑定代理,解决服务延迟绑定
     */
    private SensorService.ISensorBinder proxySensorBinder = new SensorService.ISensorBinder() {
        private final SensorSummary EMPTY_SENSOR_SUMMARIES = new SensorSummary("", new Date());

        /**
         * 获取 传感器状态
         */
        @Override
        public SensorStatusCollection getStatus() {
            SensorService.ISensorBinder original = SensorAdapterFactory.this.sensorBinder;
            if (original != null) {
                return original.getStatus();
            }
            return SensorStatusCollection.Empty;
        }

        /**
         * 获取 测量记录 查询
         *
         * @return RealmQuery<SensorLog>
         */
        @Override
        public RealmQuery<SensorLog> querySensorLog() {
            SensorService.ISensorBinder original = SensorAdapterFactory.this.sensorBinder;
            if (original != null) {
                return original.querySensorLog();
            }
            return null;
        }

        /**
         * @return 测量记录日志
         */
        @Override
        public List<SensorLog> getLastOneDaySensorLogs(String sensorId) {
            SensorService.ISensorBinder original = SensorAdapterFactory.this.sensorBinder;
            if (original != null) {
                return original.getLastOneDaySensorLogs(sensorId);
            }
            return new ArrayList<SensorLog>();
        }

        /**
         * 获取 24小时 每小时的状态统计
         */
        @Override
        public SensorSummary get24HourlySummary(String sensorId) {
            SensorService.ISensorBinder original = SensorAdapterFactory.this.sensorBinder;
            if (original != null) {
                return original.get24HourlySummary(sensorId);
            }
            return EMPTY_SENSOR_SUMMARIES;
        }

        /**
         * 获取 小时 汇总统计
         *
         * @param endTime @return
         */
        @Override
        public SensorSummary getHourlySummary(String sensorId, Date startTime, Date endTime) {
            SensorService.ISensorBinder original = SensorAdapterFactory.this.sensorBinder;
            if (original != null) {
                return original.getHourlySummary(sensorId, startTime, endTime);
            }
            return EMPTY_SENSOR_SUMMARIES;
        }
    };

    private SensorAdapterFactory(Context context) {
        this.context = context;
        handler = new Handler();
        registReceiver();
    }

    /**
     * 获取 默认实例
     */
    public static SensorAdapterFactory getInstance(Context context) {
        if (defaultInstance == null) {
            defaultInstance = new SensorAdapterFactory(context);
        }
        return defaultInstance;
    }

    /**
     * 更新视图
     */
    public void notifyDataChanged() {
        if (this.sensorBinder != null) {
            for (ViewAdapter adapter : this.viewAdapters) {
                adapter.notifyDataChanged();
            }
        }
    }

    public void registViewAdapter(ViewAdapter viewAdapter) {
        if (viewAdapter != null) {
            this.viewAdapters.add(viewAdapter);
        }
    }

    public void unregistViewAdapter(ViewAdapter viewAdapter) {
        if (viewAdapter != null) {
            this.viewAdapters.remove(viewAdapter);
        }
    }

    /**
     * 注册 事件接收器
     */
    private void registReceiver() {
        context.registerReceiver(sensorBroadcastReceiver,
                new IntentFilter(SensorService.UPDATE_SENSOR_STATUS));
        Log.e(TAG, "register Receiver");
    }

    /**
     * 注销 事件接收器
     */
    private void unregisterReceiver() {
        context.unregisterReceiver(sensorBroadcastReceiver);
    }

    /**
     * 绑定服务
     */
    public boolean bindService() {
        Intent
                intent =
                new Intent(SensorService.START_SERVICE, Uri.EMPTY, this.context, SensorService.class);
        return this.context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 获取 SensorService.ISensorBinder
     *
     * @return {@see SensorService.ISensorBinder}
     */
    public SensorService.ISensorBinder getBinder() {
        return proxySensorBinder;
    }

    /**
     * Closes the object and release any system resources it holds.
     * <p/>
     * <p>Although only the first call has any effect, it is safe to call close multiple times on the
     * same object. This is more lenient than the overridden {@code AutoCloseable.close()}, which may
     * be called at most once.
     */
    @Override
    public void close() {
        if (this == defaultInstance) {
            defaultInstance = null;
        }
        unregisterReceiver();
        if (this.sensorBinder != null) {
            this.context.unbindService(this.serviceConnection);
        }
    }
}
