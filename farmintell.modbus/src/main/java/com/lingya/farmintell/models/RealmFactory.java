package com.lingya.farmintell.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.lingya.farmintell.modbus.Register;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.RealmModule;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by zwq00000 on 2015/6/24.
 */
public class RealmFactory {

  private static final String TAG = "RealmFactory";
  static long nextClearTime = -1;
  private static RealmConfiguration.Builder builder;

  private static RealmConfiguration getConfiguration(Context context) {
    if (builder == null) {
      builder = new RealmConfiguration.Builder(context)
          .setModules(new SensorModule());
    }
    return builder.build();
  }

  /**
   * 获取 Realm 默认实例
   */
  public static Realm getInstance(Context context) {
    try {
      return Realm.getInstance(getConfiguration(context));
    } catch (RealmMigrationNeededException migrationException) {
      migrationException.printStackTrace();
      Realm.deleteRealm(getConfiguration(context));
      return Realm.getInstance(getConfiguration(context));
    }
  }

  /**
   * 更新 传感器状态
   */
  public static void updateSensorStatus(Register[] registers,
                                        SensorStatusCollection statusCollection) {
    if (registers == null) {
      throw new IllegalArgumentException("registers is not been null");
    }
    if (statusCollection == null) {
      throw new IllegalArgumentException("statusCollection is not been null");
    }
    statusCollection.setUpdateTime(new Date());
    updateSensorStatus(registers, statusCollection.getStatuses());
  }

  /**
   * 更新传感器状态值
   */
  private static void updateSensorStatus(Register[] registers, List<SensorStatus> statuses) {
    if (registers == null) {
      throw new IllegalArgumentException("registers is not been null");
    }
    int index = 0;
    for (Register register : registers) {
      for (int s = 0; s < register.getSensors().length; s++) {
        Register.Sensor sensor = register.getSensors()[s];
        SensorStatus status;
        if (index < statuses.size()) {
          status = statuses.get(index);
        } else {
          status = new SensorStatus(sensor.getId());
          statuses.add(status);
        }
        status.setName(sensor.getName());
        status.setValue(sensor.getValue());
        index++;
      }
    }
  }

  /**
   * 寄存器数组 转换为 传感器记录
   */
  public static void appendSensorLog(Realm realm, final SensorStatusCollection statusCollection) {
    if (statusCollection == null) {
      throw new IllegalArgumentException("registers is not been null");
    }
    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        List<SensorStatus> statuses = statusCollection.getStatuses();
        for (SensorStatus status : statuses) {
          SensorLog log = realm.createObject(SensorLog.class);
          log.setSensorId(status.getId());
          log.setTime(statusCollection.getUpdateTime());
          log.setValue(status.getValue());
        }
        clearExpiredData(realm);
      }
    });
  }

  /**
   * 清理 过期数据
   */
  private static void clearExpiredData(Realm realm) {
    if ((nextClearTime <= 0) || (System.currentTimeMillis() > nextClearTime)) {
      Date expireDate = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7));
      RealmResults<SensorLog>
          expiredLogs =
          realm.where(SensorLog.class).lessThan("time", expireDate).findAll();
      int size = expiredLogs.size();
      if (size > 0) {
        expiredLogs.clear();
        Log.d(TAG, "清理过期日志 删除数据 " + size + " 条");
      }
      //更新下次清理时间
      nextClearTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
    }
  }

  /**
   * 获取 小时 汇总统计
   */
  public static SensorSummary queryHourlySummary(Realm realm, String sensorId,
                                                 Calendar startCalendar,
                                                 Calendar endCalendar) {
    if (TextUtils.isEmpty(sensorId)) {
      throw new IllegalArgumentException("sensorId is not been null Or Empty");
    }

    SensorSummary summary = new SensorSummary(sensorId, startCalendar.getTime());

    //计算间隔的小时数
    int
        hourCount =
        (int) TimeUnit.MILLISECONDS
            .toHours(endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis());

    if (hourCount <= 0) {
      return summary;
    }
    RealmQuery<SensorAverage>
        query =
        SensorAverageHelper.query(realm, sensorId, startCalendar, endCalendar);
    float max = query.maximumFloat("maximum");
    float min = query.minimumFloat("minimum");
    RealmResults<SensorAverage> queryResults = query.findAll();
    int size = queryResults.size();
    float[] values = new float[size];
    Date[] stamps = new Date[size];
    for (int i = 0; i < size; i++) {
      values[i] = queryResults.get(i).getAverage();
      stamps[i] = queryResults.get(i).getStartTime();
    }
    summary.setTimeStamps(stamps);
    summary.setAverages(values);
    summary.setMaximum(max);
    summary.setMinimum(min);
    return summary;
  }


  /**
   * 根据日期范围 统计 传感器数值
   */
  static float queryAverage(Realm realm, String sensorId, Date startTime,
                            Date endTime) {
    RealmQuery<SensorLog>
        query =
        realm.where(SensorLog.class).equalTo("sensorId", sensorId)
            .between("time", startTime, endTime);
    if (query.count() == 0) {
      return 0;
    }
    return (float) query.averageFloat("value");
  }

  @RealmModule(library = true, allClasses = true)
  public static class SensorModule {

  }
}
