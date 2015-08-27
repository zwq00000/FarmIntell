package com.lingya.farmintell.adapters;

import android.test.AndroidTestCase;

import com.lingya.farmintell.models.RealmFactory;
import com.lingya.farmintell.models.SensorAverage;
import com.lingya.farmintell.models.SensorAverageHelper;
import com.lingya.farmintell.models.SensorLog;
import com.lingya.farmintell.models.SensorSummary;
import com.lingya.farmintell.utils.CalendarUtils;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.internal.Table;
import io.realm.internal.TableView;

/**
 * Created by zwq00000 on 15-8-24.
 */
public class LineChartAdapterTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testSetViewData() throws Exception {

    }

    public void testBindView() throws Exception {

    }

    public void testNotifyDataChanged() throws Exception {

    }

    public void testReset() throws Exception {

    }

    public void testShowSensorHistory() throws Exception {
        LineChartAdapter adpater = new LineChartAdapter(getContext());
        Realm realm = RealmFactory.getInstance(getContext());
        assertNotNull(realm);
        SensorSummary summary = RealmFactory.get24HourlySummary(realm, "1-1");
        assertNotNull(summary);
        assertEquals(summary.getAverages().length, 23);

        adpater.setViewData(summary);
    }

    public void testGet24HourlySummary() throws Exception {

        SensorAverageHelper.batchUpdateAverages(getContext(), System.currentTimeMillis());

        Realm realm = RealmFactory.getInstance(getContext());
        assertNotNull(realm);
        SensorSummary summary = RealmFactory.get24HourlySummary(realm, "1-1");
        assertNotNull(summary);
        assertEquals(summary.getAverages().length, 23);
    }

    public void testGetSensorAverage() throws Exception {
        SensorAverageHelper.batchUpdateAverages(getContext(), System.currentTimeMillis());

        Realm realm = RealmFactory.getInstance(getContext());
        assertNotNull(realm);
        Table table = realm.getTable(SensorAverage.class);
        assertNotNull(table);
        assertTrue(table.size() > 0);
        System.out.println("sensor average size:" + table.size());
        System.out.println("max date" + table.maximumDate(table.getColumnIndex("startTime")).toLocaleString());
        System.out.println("min date" + table.minimumDate(table.getColumnIndex("startTime")).toLocaleString());

        Table logTable = realm.getTable(SensorLog.class).getTable();
        System.out.println("max date" + logTable.maximumDate(logTable.getColumnIndex("time")).toLocaleString());
        TableView sensorIds = logTable.where().equalTo(new long[]{logTable.getColumnIndex("sensorId")}, "1-0").findAll();
        assertTrue(sensorIds.size() > 0);
        System.out.println(sensorIds.toJson());

        Date endTime = new Date();
        Calendar startCalendar = CalendarUtils.getStartCalendar(endTime);
        startCalendar.add(Calendar.HOUR, -24);
        Calendar endCalendar = CalendarUtils.getStartCalendar(endTime);

        assertTrue(endCalendar.getTimeInMillis() > startCalendar.getTimeInMillis());

        assertTrue(startCalendar.before(endCalendar));
        RealmQuery<SensorAverage> query = SensorAverageHelper.query(realm, "1-1", startCalendar, endCalendar);
        assertNotNull(query);

        assertTrue(query.count() > 0);

    }
}