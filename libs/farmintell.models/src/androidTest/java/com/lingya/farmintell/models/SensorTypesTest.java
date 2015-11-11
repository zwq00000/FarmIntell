package com.lingya.farmintell.models;

import android.test.AndroidTestCase;

/**
 * Created by zwq00000 on 15-11-11.
 */
public class SensorTypesTest extends AndroidTestCase {

    public void testName() throws Exception {
        assertEquals(SensorType.values().length, 6);
        assertEquals(SensorType.co2.name(), "co2");
        assertEquals(SensorType.values()[2].name(), "co2");
    }

    public void testOrdinal() throws Exception {

    }

    public void testToString() throws Exception {

    }
}