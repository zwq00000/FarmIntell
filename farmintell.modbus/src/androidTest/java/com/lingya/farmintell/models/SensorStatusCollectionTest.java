package com.lingya.farmintell.models;

import android.test.AndroidTestCase;

import java.util.List;

/**
 * Created by zwq00000 on 2015/6/30.
 */
public class SensorStatusCollectionTest extends AndroidTestCase {

    private SensorsConfig config;

    public void setUp() throws Exception {
        super.setUp();
        config = SensorsConfigFactory.getDefaultInstance(getContext());

    }

    public void tearDown() throws Exception {

    }

    public void addSensorStatuses(SensorStatusCollection collection) {
        assertEquals(collection.size(), 0);
        List<SensorStatus> statuses = collection.getStatuses();
        SensorStatus status = new SensorStatus();
        status.setId("1-0");
        status.setName("temp");
        status.setDisplayName("温度");
        status.setValue(31.1f);
        statuses.add(status);
        assertEquals(collection.size(), 1);
    }

    public void testGetStatuses() throws Exception {
        SensorsConfig config = SensorsConfigFactory.getDefaultInstance(getContext());
        SensorStatusCollection collection = new SensorStatusCollection(config);
        assertNotNull(collection);
        assertNotNull(collection.getStatuses());
        assertEquals(collection.getStatuses().size(), 0);
    }

    public void testGetUpdateTime() throws Exception {

    }

    public void testSetUpdateTime() throws Exception {

    }

    public void testToJson() throws Exception {
        SensorStatusCollection collection = new SensorStatusCollection(config);
        System.out.println(collection.toJson());
        this.addSensorStatuses(collection);
        System.out.println(collection.toJson());
    }

    public void testDescribeContents() throws Exception {

    }

    public void testSize() throws Exception {
        SensorStatusCollection collection = new SensorStatusCollection(config);
        assertEquals(collection.size(), 0);
        collection.getStatuses().add(new SensorStatus());
        assertEquals(collection.size(), 1);
    }
}