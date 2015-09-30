package com.lingya.farmintell.utils;

import android.content.Context;
import android.test.AndroidTestCase;

import com.lingya.farmintell.models.SensorsConfig;

/**
 * Created by zwq00000 on 2015/7/29.
 *
 */
public class JsonUtilsTest extends AndroidTestCase {

    private static final String registerJsonFile = "registers.json";


    public void testLoadFromJson1() throws Exception {
        Context context = getContext();
        String configJsonFile = "sensorsConfig.json";
        SensorsConfig
                config =
                JsonUtils.loadFromJson(context, configJsonFile, SensorsConfig.class);

        assertNotNull(config);
        assertEquals(config.getHostId(), "0001");
        assertNotNull(config.getStations());
        assertEquals(config.getStations().length, 2);
    }
}