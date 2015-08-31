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
public class SensorConfigLogTest extends AndroidTestCase {

    private Realm realm;
    private SensorsConfig config;

    public void setUp() throws Exception {
        super.setUp();
        // Set the module in the RealmConfiguration to allow only classes defined by the module.
        //RealmConfiguration config = new RealmConfiguration.Builder(getContext()).build();
        //Realm.deleteRealm(config);
        realm = RealmFactory.getInstance(getContext());
        config = SensorsConfigFactory.getDefaultInstance(getContext());
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