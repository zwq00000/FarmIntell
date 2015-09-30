package com.lingya.farmintell.models;

import android.test.AndroidTestCase;

/**
 * Created by zwq00000 on 2015/7/29.
 *
 */
public class SensorsConfigFactoryTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void testLoadFromJson() throws Exception {
        SensorsConfig config = SensorsConfig.loadFromJson(getContext());
        assertNotNull(config);
        assertEquals(config.getHostId(), "0001");
        assertEquals(config.getHostName(), "1# 大棚");

        assertEquals(config.getStations().length, 2);
        SensorsConfig.Station station = config.getStations()[0];
        assertEquals(station.getSlaveId(), 1);
        assertEquals(station.getModel(), "温湿度CO2光照变送器");
    }

    public void testGetDefaultInstance() throws Exception {
        SensorsConfig config = SensorsConfig.loadFromJson(getContext());
        assertNotNull(config);
        assertNotNull(SensorsConfig.getDefaultInstance(getContext()));
        assertEquals(config, SensorsConfig.getDefaultInstance(getContext()));
        assertEquals(config.getHostId(), "0001");
        assertEquals(config.getSensorsCount(), 6);

    }
}