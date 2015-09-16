package com.lingya.farmintell.client;

import android.test.AndroidTestCase;

/**
 * Created by zwq00000 on 15-9-12.
 */
public class SensorStatusUpdatedReceiverTest extends AndroidTestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testIsNetworkConnected() throws Exception {

        assertFalse(SensorStatusUpdatedReceiver.isNetworkConnected(getContext()));
    }
}