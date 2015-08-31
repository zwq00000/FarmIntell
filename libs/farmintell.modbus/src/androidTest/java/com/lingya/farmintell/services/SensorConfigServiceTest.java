package com.lingya.farmintell.services;

import android.content.Intent;
import android.net.Uri;
import android.test.ServiceTestCase;
import android.util.Log;

import com.lingya.farmintell.models.SensorStatus;
import com.lingya.farmintell.models.SensorStatusCollection;
import com.lingya.farmintell.models.SensorSummary;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by zwq00000 on 2015/6/24.
 */
public class SensorConfigServiceTest extends ServiceTestCase {

    private static final String TAG = "SensorConfigServiceTest";

    /**
     * Constructor
     */
    public SensorConfigServiceTest() {
        super(SensorService.class);
    }

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testStartService() throws Exception {
        Intent
                intent =
                new Intent(SensorService.START_SERVICE, Uri.EMPTY, getContext(), SensorService.class);
        SensorService.ISensorBinder binding = (SensorService.ISensorBinder) this.bindService(intent);
        assertNotNull(binding);
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000 * 5);
            SensorStatusCollection statuses = binding.getStatus();
            for (SensorStatus status : statuses.getStatuses()) {
                System.out
                        .println(status.getId() + " " + status.getDisplayName() + " " + status.getValue());
            }
        }
    }

    public void testOnCreate() throws Exception {

    }

    public void testOnDestroy() throws Exception {

    }

    public void testOnBind() throws Exception {
        Intent
                intent =
                new Intent(SensorService.START_SERVICE, Uri.EMPTY, getContext(), SensorService.class);
        SensorService.ISensorBinder binding = (SensorService.ISensorBinder) this.bindService(intent);
        assertNotNull(binding);
        SensorSummary summary = binding.get24HourlySummary("0-1");
        Log.d(TAG, summary.toString());
    }

    public void testGetHours() throws Exception {
        Calendar startCalendar = GregorianCalendar.getInstance();
        startCalendar.add(Calendar.HOUR, -24);
        Calendar nextCalendar = GregorianCalendar.getInstance();
        long millis = nextCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        System.out.println("diff millis :" + millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        assertEquals(hours, 24);
    }

    public void testIsClosed() throws Exception {

    }
}