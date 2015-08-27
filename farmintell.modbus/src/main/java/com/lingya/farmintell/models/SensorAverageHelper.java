package com.lingya.farmintell.models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.lingya.farmintell.utils.CalendarUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * 侧脸值 小时平均值 助手
 * Created by zwq00000 on 2015/7/28.
 */
public class SensorAverageHelper {

    private static final String TAG = "SensorAverageHelper";
    /**
     * 下一次更新时间
     */
    private static Date nextUpdateTime;
    private static BroadcastReceiver averageUpdateOnTimeTick = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d(TAG, "averageUpdateOnTimeTick");
            final long currentTime = System.currentTimeMillis();
            if (nextUpdateTime == null) {
                nextUpdateTime = getNextUpdateTime(context).getTime();
                Log.d(TAG, "update average " + nextUpdateTime.toLocaleString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        batchUpdateAverages(context, currentTime);
                    }
                });
                return;
            }

            if (currentTime < nextUpdateTime.getTime()) {
                //没有到达下一次 更新
                return;
            }

            Realm realm = null;
            try {
                realm = RealmFactory.getInstance(context);
                SensorsConfig sensors = SensorsConfigFactory.getDefaultInstance(context);
                String[] ids = sensors.getSensorIds();
                Log.d(TAG, "append sensors AVG " + nextUpdateTime.toLocaleString());
                SensorAverageHelper.appendAverage(realm, ids, nextUpdateTime);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }
    };

    /**
     * 批量更新 传感器平均值
     *
     * @param context
     * @param endTime
     */
    public static void batchUpdateAverages(Context context, long endTime) {
        Realm realm = null;
        try {
            realm = RealmFactory.getInstance(context);
            SensorsConfig sensors = SensorsConfigFactory.getDefaultInstance(context);
            String[] ids = sensors.getSensorIds();
            Date nextTime = getNextUpdateTime(context).getTime();
            while (endTime >= nextTime.getTime()) {
                Log.d(TAG, "append sensors AVG " + nextTime.toLocaleString());
                nextTime = SensorAverageHelper.appendAverage(realm, ids, nextTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    /**
     * 获取下一次更新时间
     */
    private static Calendar getNextUpdateTime(Context context) {
        Realm realm = null;
        try {
            Calendar endCalendar = CalendarUtils.getStartCalendar(new Date());
            realm = RealmFactory.getInstance(context);
            RealmQuery<SensorAverage> averages = realm.where(SensorAverage.class);
            if (averages.count() == 0) {
                RealmQuery<SensorLog> sensorLogs = realm.where(SensorLog.class);
                if (sensorLogs.count() > 0) {
                    endCalendar.setTime(sensorLogs.minimumDate("time"));
                }
            } else {
                Date endTime = averages.maximumDate("endTime");
                endCalendar.setTime(endTime);
            }
            return endCalendar;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static void registerAverageUpdate(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        context.registerReceiver(averageUpdateOnTimeTick, filter);
        //clearAverages(context);
    }

    public static void unregisterAverageUpdate(Context context) {
        context.unregisterReceiver(averageUpdateOnTimeTick);
    }

    private static void clearAverages(Context context) {
        Realm realm = null;
        try {
            realm = RealmFactory.getInstance(context);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.getTable(SensorAverage.class).clear();
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    /**
     * 是否包含 平均值实例
     */
    public static boolean hasInstance(Realm realm, String sensorId, Date updateTime) {
        RealmQuery<SensorAverage> query = realm.where(SensorAverage.class)
                .equalTo("sensorId", sensorId)
                .greaterThanOrEqualTo("startTime", updateTime)
                .lessThan("endTime", updateTime);
        return query.count() > 0;
    }

    /**
     * 追加 平均值
     */
    public static void appendAverage(Realm realm, final SensorStatusCollection statusCollection) {
        List<SensorStatus> statuses = statusCollection.getStatuses();
        String[] ids = new String[statuses.size()];
        for (int i = 0; i < statuses.size(); i++) {
            ids[i] = statuses.get(i).getId();
        }
        appendAverage(realm, ids, statusCollection.getUpdateTime());
    }

    /**
     * 追加平均值记录
     *
     * @param realm
     * @param sensorIds
     * @param updateTime
     * @return 下一次更新时间
     */
    public static Date appendAverage(final Realm realm, final String[] sensorIds,
                                     final Date updateTime) {
        final Calendar startCalendar = CalendarUtils.getStartCalendar(updateTime);
        final Calendar endCalendar = CalendarUtils.getEndCalendar(updateTime);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (String sensorId : sensorIds) {
                    RealmQuery<SensorLog> query = realm.where(SensorLog.class)
                            .equalTo("sensorId", sensorId)
                            .between("time", startCalendar.getTime(), endCalendar.getTime());
                    float value = (float) query.averageFloat("value");
                    SensorAverage instance = realm.createObject(SensorAverage.class);
                    instance.setSensorId(sensorId);
                    instance.setAverage(value);
                    instance.setStartTime(startCalendar.getTime());
                    instance.setEndTime(endCalendar.getTime());
                    instance.setSamplesCount((int) query.count());
                    instance.setMaximum(query.maximumFloat("value"));
                    instance.setMinimum(query.minimumFloat("value"));
                }
            }
        });
        nextUpdateTime = endCalendar.getTime();
        return nextUpdateTime;
    }

    public static RealmQuery<SensorAverage> query(Realm realm, String sensorId, Date startTime,
                                                  int count) {
        final Calendar startCalendar = CalendarUtils.getStartCalendar(startTime);
        Calendar endCalendar = CalendarUtils.getStartCalendar(startTime);
        return query(realm, sensorId, startCalendar, endCalendar);
    }

    public static RealmQuery<SensorAverage> query(Realm realm, String sensorId,
                                                  Calendar startCalendar,
                                                  Calendar endCalendar) {
        if (endCalendar.before(startCalendar)) {
            //前后交换
            long temp = startCalendar.getTimeInMillis();
            startCalendar.setTimeInMillis(endCalendar.getTimeInMillis());
            endCalendar.setTimeInMillis(temp);
        }

        RealmQuery<SensorAverage> query = realm.where(SensorAverage.class).equalTo("sensorId", sensorId)
                .greaterThanOrEqualTo("startTime", startCalendar.getTime())
                .lessThan("endTime", endCalendar.getTime());
        long
                hours =
                TimeUnit.MILLISECONDS
                        .toHours(Math.abs(endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()));
        if (hours < query.count()) {
            Calendar calendar = (Calendar) startCalendar.clone();
            for (int i = 0; i < hours; i++) {
                if (!hasInstance(realm, sensorId, calendar.getTime())) {
                    appendAverage(realm, new String[]{sensorId}, calendar.getTime());
                }
                calendar.add(Calendar.HOUR, 1);
            }

            query = realm.where(SensorAverage.class).equalTo("sensorId", sensorId)
                    .greaterThanOrEqualTo("startTime", startCalendar.getTime())
                    .lessThanOrEqualTo("endTime", endCalendar.getTime());
        }
        return query;
    }
}
