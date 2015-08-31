package com.lingya.farmintell.models;

import android.test.AndroidTestCase;

/**
 * Created by zwq00000 on 2015/6/30.
 */
public class SensorConfigStatusCollectionTest extends AndroidTestCase {

    private SensorsConfig config;

    public void setUp() throws Exception {
        super.setUp();
        config = SensorsConfigFactory.getDefaultInstance(getContext());

    }

    public void tearDown() throws Exception {

    }


    public void testGetStatuses() throws Exception {
        SensorsConfig config = SensorsConfigFactory.getDefaultInstance(getContext());
        SensorStatusCollection collection = new SensorStatusCollection(config);
        assertNotNull(collection);
        assertNotNull(collection.getStatuses());
        assertEquals(collection.getStatuses().length, 0);
    }

    public void testGetUpdateTime() throws Exception {

    }

    public void testSetUpdateTime() throws Exception {

    }

    public void testToJson() throws Exception {
        SensorStatusCollection collection = new SensorStatusCollection(config);
        System.out.println(collection.toJson());

        System.out.println(collection.toJson());
    }

    public void testDescribeContents() throws Exception {

    }

    public void testSize() throws Exception {
        SensorStatusCollection collection = new SensorStatusCollection(config);
        assertEquals(collection.size(), 0);
    }
}