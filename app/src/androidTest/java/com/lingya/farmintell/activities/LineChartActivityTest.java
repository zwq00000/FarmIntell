package com.lingya.farmintell.activities;

import android.test.AndroidTestCase;

import com.lingya.farmintell.adapters.MPLineChartAdapter;
import com.lingya.farmintell.models.RealmFactory;
import com.lingya.farmintell.models.SensorSummary;

import java.util.Date;

import io.realm.Realm;

/**
 * Created by zwq00000 on 15-8-24.
 */
public class LineChartActivityTest extends AndroidTestCase {


    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {

    }

    public void testOnCreate() throws Exception {
        MPLineChartAdapter adpater = new MPLineChartAdapter(getContext());

        Realm realm = RealmFactory.getInstance(getContext());
        assertNotNull(realm);
        SensorSummary summary = RealmFactory.get24HourlySummary(realm, "0-1",new Date());
        assertNotNull(summary);
        assertEquals(summary.getAverages().length, 24);
        adpater.setViewData(summary);
    }
}