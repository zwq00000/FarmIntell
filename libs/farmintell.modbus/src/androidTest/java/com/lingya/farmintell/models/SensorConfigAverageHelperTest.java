package com.lingya.farmintell.models;

import android.test.AndroidTestCase;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by zwq00000 on 2015/8/2.
 */
public class SensorConfigAverageHelperTest extends AndroidTestCase {

    private Realm realm;

    private static Calendar getStartCalendar(Date time) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);
        alignmentHour(calendar);
        return calendar;
    }

    private static Calendar getEndCalendar(Date time) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.HOUR, 1);
        alignmentHour(calendar);
        return calendar;
    }

    /**
     * 整小时对齐
     */
    private static void alignmentHour(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
    }

    public void setUp() throws Exception {
        super.setUp();
        realm = RealmFactory.getInstance(getContext());

    }

    public void tearDown() throws Exception {

    }

    public void testGetHours() throws Exception {

    }

    public void testHasInstance() throws Exception {
        boolean result = SensorAverageHelper.hasInstance(realm, "1-0", new Date());
        assertFalse(result);
    }

    public void testAppendAverage() throws Exception {
        String[] ids = new String[]{"1-0", "1-1", "1-2", "1-3", "2-0", "2-1"};
        int lastSize = realm.where(SensorAverage.class).findAll().size();
        SensorAverageHelper.appendAverage(realm, ids, new Date());
        assertEquals(realm.where(SensorAverage.class).findAll().size(), lastSize + 6);
        RealmResults<SensorAverage> all = realm.where(SensorAverage.class).findAll();
        for (SensorAverage average :
                all) {
            System.out.println(
                    average.getSensorId() + "\t" + average.getAverage() + "\t" + average.getStartTime() + "\t"
                            + average.getEndTime());
        }
        assertTrue(SensorAverageHelper.hasInstance(realm, "1-0", new Date()));
    }

    public void testAppendAverage1() throws Exception {

    }

    public void testGetStartCalendar() throws Exception {
        Date date = new Date();
        Calendar start = getStartCalendar(date);
        Calendar end = getEndCalendar(date);
        assertTrue(start.getTime().getTime() < date.getTime());
        assertTrue(end.getTime().getTime() > date.getTime());
        System.out.println(
                "date:" + date.toString() + "\tstart:" + start.getTime() + "\tend:" + end.getTime());
    }
}