package com.lingya.farmintell.models;

import android.test.AndroidTestCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by zwq00000 on 2015/6/30.
 */
public class SensorLogTest extends AndroidTestCase {

  private Realm realm;

  public void setUp() throws Exception {
    super.setUp();
    // Set the module in the RealmConfiguration to allow only classes defined by the module.
    //RealmConfiguration config = new RealmConfiguration.Builder(getContext()).build();
    //Realm.deleteRealm(config);
    realm = RealmFactory.getInstance(getContext());
  }

  public void tearDown() throws Exception {
    if (realm != null) {
      realm.close();
    }
  }

  public void testGetValue() throws Exception {
    assertTrue(realm.getTable(SensorLog.class).size() > 0);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:MM");
    long oneDayMillis = 1000 * 60 * 60 * 24;
    assertEquals(oneDayMillis, TimeUnit.DAYS.toMillis(1));
    Date lastDate = new Date(System.currentTimeMillis() - oneDayMillis);
    System.out.println(format.format(lastDate));
    RealmResults<SensorLog>
        logs =
        realm.where(SensorLog.class).greaterThan("time", lastDate).findAll();
    //logs = realm.where(SensorLog.class).findAll();
    assertTrue(logs.size() > 0);
    for (SensorLog log : logs) {
      System.out
          .println(log.getSensorId() + "\t" + format.format(log.getTime()) + "\t" + log.getValue());
    }
  }

  public void testInsertSensorLogs() throws Exception {
    SensorStatusCollection coll = new SensorStatusCollection();
    SensorStatus status = new SensorStatus();
    status.setId("1-1");
    status.setName("temp");
    status.setDisplayName("温度");
    status.setValue(1);
    coll.getStatuses().add(status);
    RealmFactory.appendSensorLog(realm, coll);
    long size = realm.getTable(SensorLog.class).size();
    assertTrue(size > 0);

    realm.executeTransaction(new Realm.Transaction() {
      @Override
      public void execute(Realm realm) {
        realm.getTable(SensorLog.class).removeLast();
      }
    });
    assertEquals(realm.getTable(SensorLog.class).size(), size - 1);
  }

  public void testSetValue() throws Exception {

  }

  public void testGetSensorId() throws Exception {

  }

  public void testSetSensorId() throws Exception {

  }

  public void testGetTime() throws Exception {

  }

  public void testSetTime() throws Exception {

  }
}