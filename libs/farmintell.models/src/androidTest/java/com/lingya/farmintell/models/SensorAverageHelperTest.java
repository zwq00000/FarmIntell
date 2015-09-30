package com.lingya.farmintell.models;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by zwq00000 on 15-9-28.
 */
public class SensorAverageHelperTest extends AndroidTestCase {

    private static final String TAG = "SensorAverageHelperTest";
    private static final String SENSOR_ID = "1-1";
    private Realm realm;

    private static void showSensorAverage(RealmResults<SensorAverage> averages) {
        for (int i = 0; i < averages.size(); i++) {
            SensorAverage item = averages.get(i);
            Log.d(TAG, item.getSensorId() + " AVG:" + item.getAverage() + "  MAX:" + item.getMaximum() + " MIN:" + item.getMinimum() + " Samples:" + item.getSamplesCount());
        }
    }

    private static void showSensorAverage(RealmQuery<SensorAverage> averages) {
        showSensorAverage(averages.findAll());
    }

    private static void showSensorLog(RealmResults<SensorLog> logs) {
        for (int i = 0; i < logs.size(); i++) {
            SensorLog item = logs.get(i);
            Log.d(TAG, item.getSensorId() + " Time:" + item.getTime().toLocaleString() + "  Value:" + item.getValue());
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        realm = RealmFactory.getInstance(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        realm.close();
    }

    public void testBatchUpdateAverages() throws Exception {

        clearTable(SensorAverage.class);

        long initSize = realm.getTable(SensorAverage.class).size();
        Log.d(TAG, "Init Size:" + initSize);

        //testAppendAverage();

        SensorAverageHelper.batchUpdateAverages(getContext(), System.currentTimeMillis());

        long lastSize = realm.getTable(SensorAverage.class).size();
        Log.d(TAG, "last Size:" + lastSize);
        assertTrue(lastSize > initSize);
        assertTrue(SensorAverageHelper.getLatestUpdateTime(getContext()).getTimeInMillis() < System.currentTimeMillis());
    }

    //清理 SensorLog 表
    private void clearTable(final Class<? extends RealmObject> clazz) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.getTable(clazz).clear();
                assertEquals(realm.getTable(clazz).size(), 0);
            }
        });
    }

    public void testAppendSensorLogs() throws Exception {

        clearTable(SensorLog.class);

        Date startTime = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                long time = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1);

                for (int i = 0; i < 12 * 60 * 24; i++) {
                    SensorLog log = realm.createObject(SensorLog.class);
                    log.setSensorId(SENSOR_ID);
                    log.setTime(new Date(time));
                    log.setValue(i);
                    time += 20 * 1000;
                    //System.out.print('.');
                }
            }
        });

        RealmResults<SensorLog> query = realm.where(SensorLog.class).greaterThanOrEqualTo("time", startTime).findAll();
        for (int i = 0; i < query.size(); i++) {
            SensorLog item = query.get(i);
            //Log.d(TAG,item.getSensorId() + " " + item.getTime().toLocaleString() + " " + item.getValue());
        }
        assertEquals(query.size(), 12 * 60 * 24);


    }

    public void testGetLatestUpdateTime() throws Exception {
        Calendar nextTime = SensorAverageHelper.getLatestUpdateTime(getContext());
        Log.d(TAG, "Next Update Time :" + nextTime.getTime().toLocaleString());

        clearTable(SensorAverage.class);

        nextTime = SensorAverageHelper.getLatestUpdateTime(getContext());
        Log.d(TAG, "Next Update Time :" + nextTime.getTime().toLocaleString());
    }

    public void testShowSensorAverages() throws Exception {
        RealmResults<SensorAverage> table = realm.where(SensorAverage.class).findAll();

        showSensorAverage(table);
    }

    public void testQuerySensorLog() throws Exception {
        //重新填充 测试数据
        testAppendSensorLogs();

        //计算 时间范围
        Date updateTime = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(5));
        final Calendar startCalendar = CalendarUtils.getStartCalendar(updateTime);
        final Calendar endCalendar = CalendarUtils.getEndCalendar(updateTime);

        Log.d(TAG, "startCalendar:" + startCalendar.getTime().toLocaleString() + " endCalendar:" + endCalendar.getTime().toLocaleString());

        RealmQuery<SensorLog> query = realm.where(SensorLog.class);
        Log.d(TAG, " 开始时间:" + query.minimumDate("time").toLocaleString() + " 结束时间:" + query.maximumDate("time").toLocaleString());

        assertTrue(realm.where(SensorLog.class)
                .equalTo("sensorId", SENSOR_ID)
                .greaterThanOrEqualTo("time", startCalendar.getTime())
                .count() > 0);

        assertTrue(realm.where(SensorLog.class)
                .equalTo("sensorId", SENSOR_ID)
                .greaterThanOrEqualTo("time", startCalendar.getTime())
                .maximumDate("time").getTime() > startCalendar.getTimeInMillis());

        assertTrue(realm.where(SensorLog.class)
                .equalTo("sensorId", SENSOR_ID)
                .lessThan("time", endCalendar.getTime())
                .count() > 0);

        assertTrue(realm.where(SensorLog.class)
                .equalTo("sensorId", SENSOR_ID)
                .lessThan("time", endCalendar.getTime())
                .minimumDate("time").getTime() <= endCalendar.getTimeInMillis());

        Log.d(TAG, " " + startCalendar.getTime().toLocaleString() + " " + endCalendar.getTime().toLocaleString());

        query = realm.where(SensorLog.class)
                .equalTo("sensorId", SENSOR_ID)
                .greaterThanOrEqualTo("time", startCalendar.getTime());

        assertTrue(query.count() > 0);
        Log.d(TAG, " average: " + query.averageFloat("value") + " Time:" + query.minimumDate("time") + " ~ " + query.maximumDate("time") + " Samples:" + query.count());

        //showSensorLog(query.findAll());
        assertNotSame(query.averageFloat("value"), 0f);

    }

    public void testRegisterAverageUpdate() throws Exception {

    }

    public void testUnregisterAverageUpdate() throws Exception {

    }

    public void testHasInstance() throws Exception {

    }

    public void testAppendAverage() throws Exception {
        clearTable(SensorAverage.class);

        testBatchUpdateAverages();

        long startTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(22);
        Date result = SensorAverageHelper.appendAverage(realm, new String[]{SENSOR_ID}, new Date(startTime));
        Log.d(TAG, result.toLocaleString());
        assertTrue(result.getTime() > startTime);

        RealmQuery<SensorAverage> query = realm.where(SensorAverage.class).greaterThanOrEqualTo("startTime", new Date(startTime));
        assertTrue(query.count() > 0);
        showSensorAverage(query);
    }

    public void testAppendAverage1() throws Exception {

    }

    public void testQuery() throws Exception {

    }

    public void testQuery1() throws Exception {

    }
}