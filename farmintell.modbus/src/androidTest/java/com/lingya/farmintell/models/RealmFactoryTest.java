package com.lingya.farmintell.models;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

/**
 * Created by zwq00000 on 2015/7/9.
 */
public class RealmFactoryTest extends AndroidTestCase {

    private static final String TAG = RealmFactoryTest.class.getSimpleName();

    private Realm realm;
    private SensorsConfig config;

    public void setUp() throws Exception {
        super.setUp();
        realm = RealmFactory.getInstance(getContext());
        config = SensorsConfigFactory.getDefaultInstance(getContext());
    }

    public void tearDown() throws Exception {
        if (realm != null) {
            realm.close();
        }
    }

    public void testGetInstance() throws Exception {

    }

    public void testUpdateSensorStatus() throws Exception {

    }

    public void createSensorStatuses(SensorStatusCollection collection) {
        assertEquals(collection.size(), 0);
        List<SensorStatus> statuses = collection.getStatuses();
        SensorStatus status = new SensorStatus();
        status.setId("1-0");
        status.setName("temp");
        status.setDisplayName("温度");
        statuses.add(status);
        assertEquals(collection.size(), 1);

        status = new SensorStatus();
        status.setId("1-1");
        status.setName("him");
        status.setDisplayName("湿度");
        statuses.add(status);

        status = new SensorStatus();
        status.setId("1-2");
        status.setName("co2");
        status.setDisplayName("二氧化碳");
        statuses.add(status);

        status = new SensorStatus();
        status.setId("1-3");
        status.setName("light");
        status.setDisplayName("光照");
        statuses.add(status);
        assertEquals(statuses.size(), 4);
    }

    private void setRandomValue(SensorStatusCollection collection) {
        List<SensorStatus> statuses = collection.getStatuses();
        Random random = new Random();
        for (SensorStatus status : statuses) {
            status.setValue(random.nextFloat() * 100);
        }
    }

    public void testAppendSensorLog() throws Exception {

        SensorStatusCollection collection = new SensorStatusCollection(config);
        createSensorStatuses(collection);
        Calendar startCalendar = GregorianCalendar.getInstance();
        startCalendar.setTimeInMillis(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(5));

        long orgSize = realm.getTable(SensorLog.class).size();
        for (int i = 0; i < 24 * 60 * 5; i++) {
            setRandomValue(collection);
            //collection.setUpdateTime(startCalendar.getTime());
            RealmFactory.appendSensorLog(realm, collection);
            startCalendar.add(Calendar.MINUTE, 1);
            Log.d(TAG, "insert SensorLog " + i);
        }

        assertEquals(realm.getTable(SensorLog.class).size(), orgSize + 24 * 60 * 5 * 4);
    }

    public void testQueryHourlySummary() throws Exception {
        Calendar startCalendar = GregorianCalendar.getInstance();

        String[] ids = new String[]{
                "1-0",
                "1-1",
                "1-2",
                "1-3",
                "2-0",
                "2-1",
        };

        for (String id : ids) {
            startCalendar.add(Calendar.HOUR, -24);
            SensorSummary
                    summary =
                    RealmFactory
                            .queryHourlySummary(realm, id, startCalendar, GregorianCalendar.getInstance());

            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
            TypeAdapter<SensorSummary> adapter = gson.getAdapter(SensorSummary.class);
            System.out.println(adapter.toJson(summary));
        }
    }
}